package src.xaurora.dropboxV2;
import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.json.JsonReader;
import com.dropbox.core.DbxAccountInfo;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry.WithChildren;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;

import static com.dropbox.core.util.StringUtil.jq;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.session.SessionHandler;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

public class DropboxMain extends AbstractHandler {


	private long expireDuration = 259200000;
	private final Common common;
	private final DropboxAuth dropboxAuth;
	//private final DropboxBrowse dropboxBrowse;
	private static DbxClient client;
	private static ArrayList<DbxEntry.File> onlineFiles = new ArrayList<DbxEntry.File>();
	private static User currentUser;
	private static ArrayList<File> files = new ArrayList<File>();
	private ArrayList<String> expiredFiles = new ArrayList<String>();
	private boolean isSynced = false;
	
	
	private DropboxMain(PrintWriter log, DbxAppInfo dbxAppInfo, File userDbFile)
			throws IOException, Common.DatabaseException{

		this.common = new Common(log, dbxAppInfo, userDbFile);
		this.dropboxAuth = new DropboxAuth(common);
		//this.dropboxBrowse = new DropboxBrowse(common);
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
		//		// Dropbox file browsing routes.
		//		else if (target.equals("/browse")) {
		//			dropboxBrowse.doBrowse(request, response);
		//		}
		//		else if (target.equals("/upload")) {
		//			dropboxBrowse.doUpload(request, response);
		//		}
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
				user.setUserName(username);
				user.setPassword(password);
				user.setEmail(null);
				user.setCode(code);
				user.setAccessToken(null);
				user.setDirectory("C:/Users/Owner/workspace/dropbox V3/localFile");

				common.userDb.put(user.getUserName(), user);
				common.saveUserDb();
			} else {
				if (user.getCode() != code){
					response.sendError(400, "Incorrect password or username " + username + " is taken");
					return;
				} 
			}
		}

		request.getSession().setAttribute("logged-in-username", user.getUserName());
		request.getSession().setAttribute("logged-in-password", user.getPassword());
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
		currentUser = common.getLoggedInUser(request);
		if (currentUser == null) {
			response.sendRedirect("/");
			return;
		}
		//getUserEmail();
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
		out.println("<h3>Username: " + escapeHtml4(currentUser.getUserName()) + "</h3>");

		if (currentUser.getAccessToken() != null) {
			// Show information about linked Dropbox account.  Display the "Unlink" form.
			client = new DbxClient(common.getRequestConfig(request), currentUser.getAccessToken());

			DropboxAuth.readyToSync = true;
			getUserEmail(client);

			out.println("<h3>Linked email: " + escapeHtml4(currentUser.getEmail()) + "</h3>");
			System.out.println("current user is "+currentUser.toString());
			out.println("<p>Successfully linked to your Dropbox account!");
			out.println("<form action='/dropbox-unlink' method='POST'>");
			fp.insertAntiCsrfFormField(out);
			out.println("<input type='submit' value='Unlink Dropbox account' />");
			out.println("</form>");
			out.println("</p>");
			//out.println("<p><a href='/browse'>Browse your Dropbox files</a></p>");

			// Log out form.
			out.println("<p><form action='/logout' method='POST'>");
			fp.insertAntiCsrfFormField(out);
			out.println("<input type='submit' value='Logout' />");
			out.println("</form></p>");

			out.println("</body>");
			out.println("</html>");
			out.flush();

			try {
				while (DropboxAuth.readyToSync == true){
					startSync();
					Thread.sleep(20*1000);
					if (!isSynced){
						Thread.sleep(20*1000);
					}
				}
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			// They haven't linked their Dropbox account.  Display the "Link" form.
			out.println("<p><form action='/dropbox-auth-start' method='POST'>");
			fp.insertAntiCsrfFormField(out);
			out.println("<input type='submit' value='Link to your Dropbox account' />");
			out.println("</form></p>");

			// Log out form.
			out.println("<p><form action='/logout' method='POST'>");
			fp.insertAntiCsrfFormField(out);
			out.println("<input type='submit' value='Logout' />");
			out.println("</form></p>");

			out.println("</body>");
			out.println("</html>");
			out.flush();
		}

	}

	public void doLogout(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException{
		if (!common.checkPost(request, response)) return;
		request.getSession().removeAttribute("logged-in-username");
		request.getSession().removeAttribute("logged-in-password");
		DropboxAuth.readyToSync = false;
		response.sendRedirect("/");
	}

	public void getUserEmail(DbxClient currentClient) throws IOException{
		DbxAccountInfo account = null;
		try {
			account = currentClient.getAccountInfo();
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String email = account.email;
		currentUser.setEmail(email);
		common.saveUserDb();
	}
	
	public User getCurrentUser(){
		return currentUser;
	}

	public void startSync(){
		isSynced = false;
		System.out.println("Synchronization has started");
		onlineFiles.clear();
		files.clear();
		onlineFiles = getAllOnlineFiles();
		getAllLocalFile(currentUser.getDirectory(), files);
		
		System.out.println("local file number "+files.size());
		System.out.println("online file number " +onlineFiles.size());
		
		if (!onlineFiles.isEmpty() || !files.isEmpty()){

			for (int i = 0; i < onlineFiles.size(); i++){
				long ts = System.currentTimeMillis();
				long time = onlineFiles.get(i).lastModified.getTime();;			
				if (ts - time > expireDuration){
					expiredFiles.add(onlineFiles.get(i).name);
				}
				ifExpired();
				checkOnlineFiles(onlineFiles.get(i));
			}
			for (int j = 0; j < files.size(); j++){
				checkLocalFiles(files.get(j));
			} 	
		}
		isSynced = true;
		System.out.println("Synchronization has finished");
	}

	public void setExpireDuration(long milisecond){
		this.expireDuration = milisecond;
	}
	
	// Get all files on the dropbox cloud
	private ArrayList<DbxEntry.File> getAllOnlineFiles(){
		DbxEntry.WithChildren listing = null;
		try {
			listing = client.getMetadataWithChildren("/");
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<DbxEntry.File> onlineFileList = new ArrayList<DbxEntry.File>();
		for (DbxEntry child : listing.children) {
			onlineFileList.add(child.asFile());
		}
		return onlineFileList;
	}

	// List all files and store them in a file array
	private void getAllLocalFile(String directoryName, ArrayList<File> files){
		// .............list file
		File directory = new File(directoryName);

		// get all the files from local directory
		File[] fList = directory.listFiles();
		if (fList != null) {
			for (File file : fList) {
				if (file.isFile()) {
					files.add(file);
				} else if (file.isDirectory()) {
					listf(file.getAbsolutePath(), files);
				}
			}
		}
	}

	public void listf(String directoryName, ArrayList<File> files) {
		File directory = new File(directoryName);

		// get all the files from a directory
		File[] fList = directory.listFiles();
		for (File file : fList) {
			if (file.isFile()) {
				files.add(file);
			} else if (file.isDirectory()) {
				listf(file.getAbsolutePath(), files);
			}
		}
	}

	private void checkLocalFiles(File f){

		boolean isUploaded = false;
		String fileName = f.getName();
		List<DbxEntry> list = null;

		// Search for files in cloud containing the filename
		try {
			list = client.searchFileAndFolderNames("/", fileName);
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		outerloop:
		for (int i = 0; i < list.size(); i++){
			if (compareFile(f, list.get(i).asFile())){
				long compareTime = compareTime(f, list.get(i).asFile());
				if (compareTime == 0){
				} else if (compareTime > 60*1000){
					System.out.println("check local - local file newer than online");
					DeleteOperation.DeleteSingleFile(fileName, client);
					UploadOperation.UploadSingleFile(currentUser.getDirectory(), fileName, client);
				} else if (compareTime < (-60)*1000){
					System.out.println("check local - online file newer than local");
					if(f.delete()){
						System.out.println(fileName + " is deleted!");
					}else{
						System.out.println("Delete operation is failed.");
					}
					DownloadOperation.DownloadSingleFile(currentUser.getDirectory(), fileName,client);					
				}
				isUploaded = true;
				break outerloop;
			}
		}
		if (!isUploaded){
			UploadOperation.UploadSingleFile(currentUser.getDirectory(), fileName, client);
		}
	}

	private String convertToUTC(long mili){
		Date d = new Date(mili);
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(d);
	}

	private String convertDate(Date d){
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(d);
	}

	private long compareTime(File f, DbxEntry.File f2){
		String localFile = convertToUTC(f.lastModified());
		String onlineFile = convertDate(f2.lastModified);
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date local = null, online = null;
		try {
			local = format.parse(localFile);
			System.out.println("local is " + local.toString());
			online = format.parse(onlineFile);
			System.out.println("online is " + online.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return local.getTime() - online.getTime();
	}

	private static boolean compareFile (File f, DbxEntry.File file){

		if (f.getName().equals(file.name)){
			return true;
		} 
		return false;
	}

	private void checkOnlineFiles(DbxEntry.File f){
		boolean isDownloaded = false;
		outerloop:
		for (int i = 0; i < files.size(); i++){
			if (compareFile(files.get(i), f)){
				long compareTime = compareTime(files.get(i), f);
				if (compareTime == 0){
				} else if (compareTime > 60*1000){
					System.out.println("check online - local file newer than online");
					DeleteOperation.DeleteSingleFile(files.get(i).getName(), client);
					UploadOperation.UploadSingleFile(currentUser.getDirectory(), files.get(i).getName(), client);
				} else if (compareTime < (-60)*1000){
					System.out.println("check online - online file newer than local");
					if(files.get(i).delete()){
						System.out.println(files.get(i).getName() + " is deleted!");
					}else{
						System.out.println("Delete operation is failed.");
					}
					DownloadOperation.DownloadSingleFile(currentUser.getDirectory(), files.get(i).getName(),client);
				}

				isDownloaded = true;
				break outerloop;
			}
		}

		if (!isDownloaded){
			DownloadOperation.DownloadSingleFile(currentUser.getDirectory(), f.name, client);
		}

	}

	private void ifExpired(){

		while (!expiredFiles.isEmpty()){
			for (int j = 0; j<expiredFiles.size(); j++){
				String filename = expiredFiles.get(j);
				DeleteOperation.DeleteSingleFile(filename, client);
			}		
		}
		expiredFiles.clear();
	}
	public static void startServer(){

		String argPort = "5051";
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
			String redirectUri = "http://localhost:" + port;
			Desktop.getDesktop().browse(java.net.URI.create(redirectUri));
			server.join();


		}
		catch (Exception ex) {
			System.err.println("Error running server: " + ex.getMessage());
			System.exit(1);
		}
	}

}
