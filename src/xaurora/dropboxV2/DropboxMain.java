package xaurora.dropboxV2;
import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.json.JsonReader;
import com.dropbox.core.DbxAccountInfo;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import static com.dropbox.core.util.StringUtil.jq;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.session.SessionHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

public class DropboxMain extends AbstractHandler {


	private final Common common;
	private final DropboxAuth dropboxAuth;
	private final DropboxBrowse dropboxBrowse;
	private DbxClient client;
	
	private DropboxMain(PrintWriter log, DbxAppInfo dbxAppInfo, File userDbFile)
			throws IOException, Common.DatabaseException{

		this.common = new Common(log, dbxAppInfo, userDbFile);
		this.dropboxAuth = new DropboxAuth(common);
		this.dropboxBrowse = new DropboxBrowse(common);
			}

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException{
		
		// Don't pollute the logging with the favicon.ico requests that browsers issue.
		if (target.equals("/favicon.ico")) {
			response.sendError(404);
			return;
		}

		// Log the request path.
		String requestPath = target;
		if (request.getQueryString() != null) {
			requestPath += "?" + request.getQueryString();
		}

		common.log.println("-- Request: " + request.getMethod() + " " + requestPath);

		if (target.equals("/")) {
			doIndex(request, response);
		}
		else if (target.equals("/login")) {
			doLogin(request, response);
		}
		else if (target.equals("/home")) {
			doHome(request, response);
		}
		else if (target.equals("/logout")) {
			doLogout(request, response);
		}
		// Dropbox authorization routes.
		else if (target.equals("/dropbox-auth-start")) {
			dropboxAuth.doStart(request, response);
		}
		else if (target.equals("/dropbox-auth-finish")) {
			dropboxAuth.doFinish(request, response);
		}
		else if (target.equals("/dropbox-unlink")) {
			dropboxAuth.doUnlink(request, response);
		}
		// Dropbox file browsing routes.
		else if (target.equals("/browse")) {
			dropboxBrowse.doBrowse(request, response);
		}
		else if (target.equals("/upload")) {
			dropboxBrowse.doUpload(request, response);
		}
		else {
			response.sendError(404);
		}
	}

	public void doIndex(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		if (!common.checkGet(request, response)) return;

		// If there's a user logged in, send them to "/home".
		User user = common.getLoggedInUser(request);
		if (user != null) {
			response.sendRedirect("/home");
			return;
		}

		FormProtection fp = FormProtection.start(response);

		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "UTF-8"));

		out.println("<html>");
		out.println("<head><title>X-aurora</title></head>");
		out.println("<body>");
		fp.insertAntiRedressHtml(out);

		// Login form.
		out.println("<h2>Welcome to X-aurora</h2>");

		out.println("<form action='/login' method='POST'>");
		fp.insertAntiCsrfFormField(out);
		out.println("<h3>Please register an account with us</h3>");
		out.println("<p>Input your username: <input name='username' type='text' /> (Min 3 characters)</p>");
		out.println("<p>Password: <input name='password' type='password' /> (Between 6-20 characters long) </p>");
		out.println("<input type='submit' value='Connect to Dropbox!' />");
		//out.println("<p><a href='/password'>Forgot password?</a></p>");
		out.println("</form>");

		out.println("</body>");
		out.println("</html>");
		out.flush();
	}

	public void doLogin(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		if (!common.checkPost(request, response)) return;

		String username = request.getParameter("username");
		common.log.println(username);
		if (username == null) {
			response.sendError(400, "Missing field \"username\".");
			return;
		}

		String password = request.getParameter("password");
		common.log.println(password);
		String usernameError = checkUsername(username);
		String passwordError = checkPassword(password);
		if (usernameError != null) {
			response.sendError(400, "Invalid username: " + usernameError);
			return;
		}
		else if (passwordError != null) {
			response.sendError(400, "Invalid password: " + passwordError);
			return;
		}

		// Lookup user.  If the user doesn't exist, create it. 
		// If the user does exist, wrong password is keyed in, send error.
		User user;
		synchronized (common.userDb) {		
			String userInfo = username + password;
			int code = userInfo.hashCode();
			user = common.userDb.get(username);
			
			if (user == null) {	
				user = new User();
				user.username = username;
				user.password = password;
				user.email = null;
				user.code = code;
				user.dropboxAccessToken = null;

				common.userDb.put(user.username, user);
				common.saveUserDb();
			} else {
				if (user.getCode() != code){
					response.sendError(400, "Incorrect password or username " + username + " is taken");
					return;
				} 
			}
		}

		request.getSession().setAttribute("logged-in-username", user.username);
		request.getSession().setAttribute("logged-in-password", user.password);
		response.sendRedirect("/");
	}

	private static String checkUsername(String username)
	{
		if (username.length() < 3) {
			return "too short (minimum: 3 characters)";
		}
		else if (username.length() > 64) {
			return "too long (maximum: 64 characters)";
		}
		for (int i = 0; i < username.length(); i++) {
			char c = username.charAt(i);
			if (c >= 'A' && c <= 'Z') continue;
			if (c >= 'a' && c <= 'z') continue;
			if (c >= '0' && c <= '9') continue;
			if (c == '_') continue;
			return "invalid character at position " + (i+1) + ": " + jq(""+c);
		}
		return null;
	}

	private static String checkPassword(String password)
	{
		if (password.length() < 6) {
			return "too short (minimum: 6 characters)";
		}
		else if (password.length() > 20) {
			return "too long (maximum: 20 characters)";
		}
		for (int i = 0; i < password.length(); i++) {
			char c = password.charAt(i);
			if (c >= 'A' && c <= 'Z') continue;
			if (c >= 'a' && c <= 'z') continue;
			if (c >= '0' && c <= '9') continue;
			if (c == '_') continue;
			return "invalid character at position " + (i+1) + ": " + jq(""+c);
		}
		return null;
	}

	public void doHome(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException{
		if (!common.checkGet(request, response)) return;

		// If nobody's logged in, send the browser to "/".
		User user = common.getLoggedInUser(request);
		if (user == null) {
			response.sendRedirect("/");
			return;
		}

		FormProtection fp = FormProtection.start(response);
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "UTF-8"));

		out.println("<html>");
		out.println("<head><title>X-aurora</title></head>");
		out.println("<body>");
		fp.insertAntiRedressHtml(out);

		out.println("<h2>Welcome to X-aurora</h2>");
		
		// Show user info.
		out.println("<h3>Username: " + escapeHtml4(user.username) + "</h3>");
		
		if (user.dropboxAccessToken != null) {
			// Show information about linked Dropbox account.  Display the "Unlink" form.
			client = new DbxClient(common.getRequestConfig(request), user.dropboxAccessToken);
//			try {
//				DbxAccountInfo account = client.getAccountInfo();
//				String email = account.email;
//				out.println("<h3>Linked email: " + escapeHtml4(email) + "</h3>");
//			} catch (DbxException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			out.println("<p>Successfully linked to your Dropbox account!");
			out.println("<form action='/dropbox-unlink' method='POST'>");
			fp.insertAntiCsrfFormField(out);
			out.println("<input type='submit' value='Unlink Dropbox account' />");
			out.println("</form>");
			out.println("</p>");
			out.println("<p><a href='/browse'>Browse your Dropbox files</a></p>");
		} else {
			// They haven't linked their Dropbox account.  Display the "Link" form.
			out.println("<p><form action='/dropbox-auth-start' method='POST'>");
			fp.insertAntiCsrfFormField(out);
			out.println("<input type='submit' value='Link to your Dropbox account' />");
			out.println("</form></p>");
		}

		// Log out form.
		out.println("<p><form action='/logout' method='POST'>");
		fp.insertAntiCsrfFormField(out);
		out.println("<input type='submit' value='Logout' />");
		out.println("</form></p>");

		out.println("</body>");
		out.println("</html>");
		out.flush();
	}

	public void doLogout(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException{
		if (!common.checkPost(request, response)) return;
		request.getSession().removeAttribute("logged-in-username");
		request.getSession().removeAttribute("logged-in-password");
		response.sendRedirect("/");
			}

	public static void main(String[] args) throws IOException{

		String argPort = "5056";
		String argAppInfo = "api.app";
		String argDatabase = "database.db";

		int port = Integer.parseInt(argPort);

		// Read app info file (contains app key and app secret)
		DbxAppInfo dbxAppInfo;
		try {
			dbxAppInfo = DbxAppInfo.Reader.readFromFile(argAppInfo);
		}
		catch (JsonReader.FileLoadException ex) {
			System.err.println("Error loading <app-info-file>: " + ex.getMessage());
			System.exit(1); return;
		}
		System.out.println("Loaded app info from " + jq(argAppInfo));

		File dbFile = new File(argDatabase);

		// Run server
		try {
			DropboxMain main = new DropboxMain(new PrintWriter(System.out, true), dbxAppInfo, dbFile);

			Server server = new Server(port);
			SessionHandler sessionHandler = new SessionHandler();
			sessionHandler.setServer(server);
			sessionHandler.setHandler(main);
			server.setHandler(sessionHandler);

			server.start();
			System.out.println("Server running: http://localhost:" + port + "/");

			server.join();
		}
		catch (Exception ex) {
			System.err.println("Error running server: " + ex.getMessage());
			System.exit(1);
		}
	}

}
