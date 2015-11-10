package xaurora.dropbox;

import java.util.ArrayList;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;

public class DropboxMain {
	public static final ArrayList<userProfile> user = null;
	
	private static int currentUserIndex;
	
	public static void startCall() throws DbxException{
		DbxClient client  = DropboxAuth.Auth();
	}

	public static void setCurrentIndex(int index){
		currentUserIndex = index;
	}

	
}
