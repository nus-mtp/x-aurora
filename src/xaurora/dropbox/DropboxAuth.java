package xaurora.dropbox;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Locale;

import xaurora.util.UserProfile;

import com.dropbox.core.DbxAccountInfo;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;

public class DropboxAuth {

	final static String APP_KEY = "4tpptik431fwlqo";
	final static String APP_SECRET = "xe5robnc898oy37";
	
	static String accessToken;
	static String userID;
	static int index;
	static DbxClient client;
	
	public static void setAccessToken (String url){
		
		for (int i = 0; i< DropboxMain.user.size(); i++){
			UserProfile demo = DropboxMain.user.get(i);
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
	
	public static DbxClient Auth() throws DbxException{

		DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0",
				Locale.getDefault().toString());

		// Now use the access token to make Dropbox API calls.
        client = new DbxClient(config, accessToken);
		setUserProfile();
		return client;
	}
	
	public static void setUserProfile() throws DbxException{
		DbxAccountInfo account = client.getAccountInfo();

		UserProfile user = new UserProfile();
		user.setUserID(userID);
		user.setAccessToken(accessToken);
		user.setUserName(account.displayName);
		user.setEmail(account.email);
		user.setStorage(formulateStorage(bytesToGB(account.quota.normal),bytesToGB(account.quota.total)));
		
		File f = new File("/" + userID + "/");
		try {
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String path = f.getAbsolutePath();
		user.setPath(path);
		user.setCurrent();
		DropboxMain.user.set(index, user);
		DropboxMain.setCurrentIndex(index);
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
