package xaurora.dropboxV2;

public class User {
	public String username;
	public String email;
	public int code;
	public String dropboxAccessToken;

	public String toString() { 
		if (this.dropboxAccessToken != null)
			return "username: " + this.username + ", email: " + this.email + ", access token: " + this.dropboxAccessToken;
		return "username: " + this.username + ", email: " + this.email;
	} 
}
