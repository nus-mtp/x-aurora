package xaurora.dropbox;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import xaurora.util.UserProfile;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;

public class DropboxMain {
	public static final ArrayList<UserProfile> user = null;
	
	private static int currentUserIndex;
	private static DbxClient client;
	public static void startCall() throws DbxException{
		client  = DropboxAuth.Auth();
	}

	public static void setCurrentIndex(int index){
		currentUserIndex = index;
	}
	
	public static UserProfile getCurrentUser(){
		return user.get(currentUserIndex);
	}
	
	
	public void getAllMetaData() throws DbxException{
		DbxEntry.WithChildren listing = client.getMetadataWithChildren("/");
		System.out.println("Files in the root path:");
		for (DbxEntry child : listing.children) {
			System.out.println("	" + child.name + ": " + child.toString());
		}
	}
	
	private String getLastModified (String meta){
		String[] parts = meta.split("=");
		int index = parts[5].lastIndexOf('"');
		String time = parts[5].substring(1, index-4);
		return time;
	}
	
	private static String convertToUTC(long mili){
		Date d = new Date(mili);
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(d);
	}
	
	private int compareTime(File f, String meta) throws ParseException{
		String localFile = convertToUTC(f.lastModified());
		String onlineFile = getLastModified(meta);
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date local = format.parse(localFile);
		Date online = format.parse(onlineFile);

		return local.compareTo(online);
	}
	
	private static String getFileName(String meta){
		String[] parts = meta.split("=");
		int first = parts[0].lastIndexOf('/');
		int last = parts[0].lastIndexOf('"');
		String name = parts[0].substring(first+1, last);
		return name;
	}
	
	private static boolean compareFileName (File f, String meta){
		return f.getName().equals(getFileName(meta));
	}
}
