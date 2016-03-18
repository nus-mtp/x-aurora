package src.xaurora.dropboxV2;

public class User {
	private String username;
	private String password;
	private String email;
	private int code;
	private String dropboxAccessToken;
	private String directory;

	public String getUserName(){
		return this.username;
	}
	public void setUserName(String userName){
		this.username = userName;
	}
	
	public String getPassword(){
		return this.password;
	}
	public void setPassword(String pwd){
		this.password = pwd;
	}
	
	public String getEmail(){
		return this.email;
	}
	public void setEmail(String Email){
		this.email = Email;
	}
	
	public int getCode(){
		return this.code;
	}
	public void setCode(int Code){
		this.code = Code;
	}
	
	public String getAccessToken(){
		return this.dropboxAccessToken;
	}
	public void setAccessToken(String accessT){
		this.dropboxAccessToken = accessT;
	}
	
	public String getDirectory(){
		return this.directory;
	}
	public void setDirectory(String Directory){
		this.directory = Directory;
	}
	
	public String toString() { 
		if (this.dropboxAccessToken != null)
			return "username: " + this.username + ", email: " + this.email + ", access token: " 
				+ this.dropboxAccessToken + ", directory: " + this.directory;
		return "username: " + this.username + ", email: " + this.email + ", directory: " + this.directory;
	} 
}

