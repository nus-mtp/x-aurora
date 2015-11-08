package xaurora.dropbox;

import java.text.DecimalFormat;

public class userProfile {

	private static final String APP_KEY = "4tpptik431fwlqo";
	
	private static final String APP_SECRET = "xe5robnc898oy37";
	
	private static String accessToken;	
	private static String userName;	
	private static String emailAddress;	
	private static String storage;
	
	userProfile(){	
		accessToken = null;
		userName = null;
		emailAddress = null;
		storage = null;
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
	
	public void setStorage(String storage){
		this.storage = storage;
	}
	
	public String getStorage(){
		return storage;
	}
	
	public String toString(){
		String userInfo = "UserName: " + userName + "\nEmail: " + emailAddress + "\nStorage: " + storage + "\n";
		return userInfo;
	}

}
