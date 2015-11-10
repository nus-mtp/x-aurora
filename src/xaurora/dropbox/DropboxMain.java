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
	public static ArrayList<UserProfile> user = null;
	
	private static int currentUserIndex;
	private static DbxClient client;
	private ArrayList<String> metaData = null;
	public static void startCall(){
		client  = DropboxAuth.Auth();
	}

	public static void setCurrentIndex(int index){
		currentUserIndex = index;
	}
	
	public static UserProfile getCurrentUser(){
		while (!user.isEmpty()){
			
		}
		return user.get(currentUserIndex);
	}
	
	public boolean isConnectedEstablised(){
		if (client != null){
			return true;
		}
		return false;
	}
	
	public void getAllMetaData() throws DbxException{
		DbxEntry.WithChildren listing = client.getMetadataWithChildren("/");
		System.out.println("Files in the root path:");
		metaData = null;
		for (DbxEntry child : listing.children) {
			metaData.add(child.toString());
		}
	}
	
	private void checkLocalFiles(File f){
		try {
			getAllMetaData();
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean isUploaded = false;
		String fileName = f.getName();
		for (int i = 0; i < metaData.size(); i++){
			if (compareFileName(f,metaData.get(i))){
				int compareTime = compareTime(f,metaData.get(i));
				if (compareTime == 0){
					isUploaded = true;
				} else if (compareTime > 0){
					DeleteOperation.DeleteSingleFile(fileName, client);
					UploadOperation.UploadSingleFile(fileName, client);
					isUploaded = true;
				} else if (compareTime < 0){
					if(f.delete()){
		    			System.out.println(fileName + " is deleted!");
		    		}else{
		    			System.out.println("Delete operation is failed.");
		    		}
					DownloadOperation.DownloadSingleFile(fileName,client);
					isUploaded = true;
				}
			}
		}
		if (!isUploaded){
			UploadOperation.UploadSingleFile(fileName, client);
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
	
	private int compareTime(File f, String meta){
		String localFile = convertToUTC(f.lastModified());
		String onlineFile = getLastModified(meta);
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date local = null, online = null;
		try {
			local = format.parse(localFile);
			online = format.parse(onlineFile);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
