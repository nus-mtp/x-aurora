package xaurora.util;

public class UserProfile {
	
	private String accessToken;	
	private String userName;	
	private String emailAddress;	
	private String storage;
	private String path;
	private String userID;

	public UserProfile(){	
		accessToken = "";
		userName = "";
		emailAddress = "";
		storage = "";
		path = "/";
		userID = "";
	}
	
	public UserProfile(String token, String name, String email, String storage, String path, String id){
		this.accessToken = token;
		this.userName = name;
		this.emailAddress = email;
		this.storage = storage;
		this.path = path;
		this.userID = id;
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

	
	@Override
	public String toString(){
		String userInfo = "UserName: " + userName + "\nEmail: " + emailAddress + "\nUserID: " + userID + 
				"\nStorage: " + storage + "\nLocal file is stored at: " + path;// + "\n" + getCurrent() + "\n";
		return userInfo;
	}

}
