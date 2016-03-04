package xaurora.dropboxV2;
import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.json.JsonReader;

import static com.dropbox.core.util.StringUtil.jq;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.session.SessionHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

public class DropboxMain extends AbstractHandler {


	private final Common common;
    private final DropboxAuth dropboxAuth;
    private final DropboxBrowse dropboxBrowse;
    
    private DropboxMain(PrintWriter log, DbxAppInfo dbxAppInfo, File userDbFile)
            throws IOException, Common.DatabaseException
    {
		
		this.common = new Common(log, dbxAppInfo, userDbFile);
        this.dropboxAuth = new DropboxAuth(common);
        this.dropboxBrowse = new DropboxBrowse(common);
	}

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
	        throws IOException, ServletException
	    {
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
	        	common.log.println("https://www.dropbox.com/1/oauth2/authorize?response_type=token&client_id=4tpptik431fwlqo&redirect_uri=https://www.dropbox.com/home");
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
	        throws IOException, ServletException
	    {
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
	        out.println("<head><title>xAurora</title></head>");
	        out.println("<body>");
	        fp.insertAntiRedressHtml(out);

	        // Login form.
	        out.println("<h2>Welcome to xAurora</h2>");
	        //out.println("<h3>Connect to Dropbox!</h3>");
	        out.println("<form action='/login' method='POST'>");
	        fp.insertAntiCsrfFormField(out);
	        out.println("<p>Input your username: <input name='username' type='text' /> (pick whatever you want)</p>");
	        out.println("<p>Dropbox email account: <input name='email' type='text' /> </p>");
	        //out.println("<p>No password needed for this tiny example.</p>");
	        out.println("<input type='submit' value='Connect to Dropbox!' />");
	        out.println("</form>");

	        out.println("</body>");
	        out.println("</html>");
	        out.flush();
	    }
	
	public void doLogin(HttpServletRequest request, HttpServletResponse response)
	        throws IOException, ServletException
	    {
	        if (!common.checkPost(request, response)) return;

	        String username = request.getParameter("username");
	        common.log.println(username);
	        if (username == null) {
	            response.sendError(400, "Missing field \"username\".");
	            return;
	        }
	        
	        String email = request.getParameter("email");
	        common.log.println(email);
	        String usernameError = checkUsername(username);
	        if (usernameError != null) {
	            response.sendError(400, "Invalid username: " + usernameError);
	            return;
	        }

	        // Lookup user.  If the user doesn't exist, create it.
	        User user;
	        synchronized (common.userDb) {
	            user = common.userDb.get(username);
	            if (user == null) {
	                user = new User();
	                user.username = username;
	                user.email = email;
	                user.dropboxAccessToken = null;

	                common.userDb.put(user.username, user);
	                common.saveUserDb();
	            }
	        }

	        request.getSession().setAttribute("logged-in-username", user.username);
	        request.getSession().setAttribute("logged-in-email", user.email);
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
	
	public void doHome(HttpServletRequest request, HttpServletResponse response)
	        throws IOException, ServletException
	    {
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
	        out.println("<head><title>xAurora</title></head>");
	        out.println("<body>Welcome</body>");
	        fp.insertAntiRedressHtml(out);

	        // Show user info.
	        out.println("<h3>Username: " + escapeHtml4(user.username) + "</h3>");
	        out.println("<h3>Dropbox email: " + escapeHtml4(user.email) + "</h3>");

	        if (user.dropboxAccessToken != null) {
	            // Show information about linked Dropbox account.  Display the "Unlink" form.
	            out.println("<p>Linked to your Dropbox account (" + escapeHtml4(user.dropboxAccessToken) + "), ");
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
	        throws IOException, ServletException
	    {
	        if (!common.checkPost(request, response)) return;
	        request.getSession().removeAttribute("logged-in-username");
	        response.sendRedirect("/");
	    }
	
	final static String APP_KEY = "4tpptik431fwlqo";
	final static String APP_SECRET = "xe5robnc898oy37";
	
	public static void main(String[] args)
	        throws IOException
	    {
//	        if (args.length == 0) {
//	            System.out.println("");
//	            System.out.println("Usage: COMMAND <http-listen-port> <app-info-file> <database-file>");
//	            System.out.println("");
//	            System.out.println(" <http-listen-port>: The port to run the HTTP server on.  For example,");
//	            System.out.println("    \"8080\").");
//	            System.out.println("");
//	            System.out.println(" <app-info-file>: A JSON file containing your Dropbox API app key, secret");
//	            System.out.println("    and access type.  For example, \"my-app.app\" with:");
//	            System.out.println("");
//	            System.out.println("    {");
//	            System.out.println("      \"key\": \"Your Dropbox app key...\",");
//	            System.out.println("      \"secret\": \"Your Dropbox app secret...\"");
//	            System.out.println("    }");
//	            System.out.println("");
//	            System.out.println(" <database-file>: Where you want this program to store its database.  For");
//	            System.out.println("    example, \"web-file-browser.db\".");
//	            System.out.println("");
//	            return;
//	        }
//
//	        if (args.length != 3) {
//	            System.err.println("Expecting exactly 3 arguments, got " + args.length + ".");
//	            System.err.println("Run with no arguments for help.");
//	            System.exit(1); return;
//	        }

	        String argPort = "5053";
	        String argDatabase = "database.db";

	        // Figure out what port to listen on.
	        int port;
//	        try {
	            port = Integer.parseInt(argPort);
//	            if (port < 1 || port > 65535) {
//	                System.err.println("Expecting <http-listen-port> to be a number from 1 to 65535.  Got: " + port + ".");
//	                System.exit(1); return;
//	            }
//	        }
//	        catch (NumberFormatException ex) {
//	            System.err.println("Expecting <http-listen-port> to be a number from 1 to 65535.  Got: " + jq(argPort) + ".");
//	            System.exit(1); return;
//	        }

	        DbxAppInfo dbxAppInfo = new DbxAppInfo(APP_KEY, APP_SECRET);

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
