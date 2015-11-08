package xaurora.dropbox;

import java.awt.Desktop;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Locale;

import com.dropbox.core.DbxAccountInfo;
import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthInfo;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;

public class DropboxAuth {

	final String APP_KEY = "4tpptik431fwlqo";
	final String APP_SECRET = "xe5robnc898oy37";
	
	static String accessToken;
	static String userID;
	static int index;
	
	public static void setAccessToken (String url) throws IOException{
		
		for (int i = 0; i< Dropbox.user.size(); i++){
			userProfile demo = Dropbox.user.get(i);
			if (demo.getAccessToken()==null){
				index = i;
				userID = parseUserID(url);
				accessToken = parseAccessToken(url);
				break;
			}
		}
	}
	
	public static String parseUserID(String url){
		String[] elements = url.split("=");
		return elements[3];	
	}
	
	public static String parseAccessToken(String url){
		String[] parts = url.split("=");
		String[] token = parts[1].split("&");
		
		return token[0];	
	}
	
	public void Auth() throws DbxException{
		DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);

		DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0",
				Locale.getDefault().toString());
		
		// Save auth information to output file.
        DbxAuthInfo authInfo = new DbxAuthInfo(accessToken, appInfo.host);

		// Now use the access token to make Dropbox API calls.
        DbxClient client = new DbxClient(config, accessToken);
		DbxAccountInfo account = client.getAccountInfo();

		userProfile user = new userProfile();
		user.setUserID(userID);
		user.setAccessToken(accessToken);
		user.setUserName(account.displayName);
		user.setEmail(account.email);
		user.setStorage(formulateStorage(bytesToGB(account.quota.normal),bytesToGB(account.quota.total)));
		Dropbox.user.set(index, user);
		System.out.println(user.toString());
	}
	
	public static String formulateStorage (String used, String total){
		return used + "GB is used out of " + total + "GB";
	}
	
	public static String bytesToGB(long quota){		
		double GB = (double) quota / (1024.0*1024.0*1024.0);
		DecimalFormat df = new DecimalFormat("#.##");
		
		return df.format(GB);		
	}
}
