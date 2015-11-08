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
	
	String accessToken;
	
	public void getAccessToken () throws IOException{
		if (Dropbox.user.getAccessToken() == null){
			String redirectUri = "https://www.dropbox.com/1/oauth2/authorize?response_type=token&client_id=4tpptik431fwlqo&redirect_uri=https://www.dropbox.com/home";
			Desktop.getDesktop().browse(java.net.URI.create(redirectUri));
			accessToken = "_csCIJFXenAAAAAAAAAAEwa7tOFlw9BtBom7BcBdhO-OFsFcIj7wOUwxe8LNsQRi";;
		}
		else {
		accessToken = Dropbox.user.getAccessToken();
		}
		System.out.println("accessToken is = " + accessToken);
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
		user.setAccessToken(accessToken);
		user.setUserName(account.displayName);
		user.setEmail(account.email);
		user.setStorage(formulateStorage(bytesToGB(account.quota.normal),bytesToGB(account.quota.total)));
		user = Dropbox.user;
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
