package xaurora.dropbox;

public class userProfile {

	private static final String APP_KEY = "4tpptik431fwlqo";
	
	private static final String APP_SECRET = "xe5robnc898oy37";
	
	private static String accessToken;	
	private static String userName;	
	private static String emailAddress;	
	private static String storage;
	private static String path;
	private static String userID;
	private static boolean current;
	
	userProfile(){	
		accessToken = null;
		userName = null;
		emailAddress = null;
		storage = null;
		path = null;
		userID = null;
		current = false;
	}
	
	public void setAccessToken(String token){
		accessToken = token;
	}
	
	public String getAccessToken(){
		return accessToken;
	}
	
	public void setUserName(String name){
		userName = name;
	}
	
	public String getUserName(){
		return userName;
	}
	
	public void setEmail(String email){
		emailAddress = email;
	}
	
	public String getEmail(){
		return emailAddress;
	}
	
	public void setStorage(String GB){
		storage = GB;
	}
	
	public String getStorage(){
		return storage;
	}
	
	public void setPath(String filePath){
		path = filePath;
	}
	
	public String getPath(){
		return path;
	}
	
	public void setUserID(String id){
		userID = id;
	}
	
	public String getUserID(){
		return userID;
	}
	
	public void setCurrent(){
		current = true;
	}
	
	public String getCurrent(){
		if (current == true){
			return "User is current account";
		}
		return "User is not current account";
	}
	
	public String toString(){
		String userInfo = "UserName: " + userName + "\nEmail: " + emailAddress + "\nUserID: " + userID + 
				"\nStorage: " + storage + "\nLocal file is stored at: " + storage + "\n" + getCurrent() + "\n";
		return userInfo;
	}

}
