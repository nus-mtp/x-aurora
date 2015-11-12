package xaurora.dropbox;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import xaurora.io.DataFileIO;
import xaurora.util.UserProfile;
import xaurora.dropbox.DropboxAuth;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;

public class DropboxMain {
	public static ArrayList<UserProfile> user = new ArrayList<UserProfile>();

	protected static int currentUserIndex;
	private static DbxClient client = null;
	private static ArrayList<String> metaData = new ArrayList<String>();
	private static UserProfile currentUser = null;
	private static File[] fList;
	private static ArrayList<File> expiredFiles = new ArrayList<File>();

	public static void startCall(){
		client  = DropboxAuth.Auth();
		currentUser = user.get(currentUserIndex);
		DataFileIO.instanceOf().setDirectory(currentUser.getPath());
		sync();
	}

	public static void setCurrentIndex(int index){
		currentUserIndex = index;
	}

	public static UserProfile getCurrentUser(){
		while (user.isEmpty()){
		}
		
		return currentUser;
	}

	public static boolean isConnectionEstablised(){
		if (client != null && currentUser != null){
			return true;
		}
		return false;
	}

	private static void getAllMetaData(){
		DbxEntry.WithChildren listing = null;
		try {
			listing = client.getMetadataWithChildren("/");
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		metaData = new ArrayList<String>();
		for (DbxEntry child : listing.children) {
			metaData.add(child.toString());
		}
	}

	private static void getAllLocalFile(){
		// .............list file
		File directory = new File(currentUser.getPath());

		// get all the files from a directory
		fList = directory.listFiles();
	}

	private static void sync(){
		getAllMetaData();
		getAllLocalFile();
		if (!metaData.isEmpty() && fList != null){
			for (int i = 0; i < metaData.size(); i++){
				checkOnlineFiles(metaData.get(i));
			}
			for (int j = 0; j < fList.length; j++){
				checkLocalFiles(fList[j]);
			}
			ifExpired();
		}
	}

	private static void checkLocalFiles(File f){

		boolean isUploaded = false;
		String fileName = f.getName();
		List<DbxEntry> list = null;
		try {
			list = client.searchFileAndFolderNames("/", fileName);
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < list.size(); i++){
			if (compareFileName(f,list.get(i).toString())){
				int compareTime = compareTime(f,list.get(i).toString());
				if (compareTime == 0){
				} else if (compareTime > 0){
					DeleteOperation.DeleteSingleFile(fileName, client);
					UploadOperation.UploadSingleFile(fileName, client);
				} else if (compareTime < 0){
					if(f.delete()){
						System.out.println(fileName + " is deleted!");
					}else{
						System.out.println("Delete operation is failed.");
					}
					DownloadOperation.DownloadSingleFile(fileName,client);					
				}
				isUploaded = true;
				break;
			}
		}
		if (!isUploaded){
			UploadOperation.UploadSingleFile(fileName, client);
		}
	}

	private static void checkOnlineFiles(String metaData){
		boolean isDownloaded = false;
		for (int i = 0; i < fList.length; i++){
			if (compareFileName(fList[i], metaData)){
				int compareTime = compareTime(fList[0], metaData);
				if (compareTime == 0){
				} else if (compareTime > 0){
					DeleteOperation.DeleteSingleFile(fList[i].getName(), client);
					UploadOperation.UploadSingleFile(fList[i].getName(), client);
				} else if (compareTime < 0){
					if(fList[i].delete()){
						System.out.println(fList[i].getName() + " is deleted!");
					}else{
						System.out.println("Delete operation is failed.");
					}
					DownloadOperation.DownloadSingleFile(fList[i].getName(),client);
				}
				isDownloaded = true;
				break;
			}
		}
		
		if (!isDownloaded){
			DownloadOperation.DownloadSingleFile(getFileName(metaData), client);
		}
		
	}

	private static String getLastModified (String meta){
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

	private static int compareTime(File f, String meta){
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

	private static void ifExpired(){
		expiredFiles = null;
		for (int i = 0; i < fList.length; i++){
			long ts = System.currentTimeMillis();
			long time = fList[i].lastModified();
			if (ts - time > 259200000){
				expiredFiles.add(fList[i]);
			}

		}
		while (!expiredFiles.isEmpty()){
			for (int j = 0; j<expiredFiles.size(); j++){
				DeleteOperation.DeleteSingleFile(expiredFiles.get(j).getName(), client);
				if(expiredFiles.get(j).delete()){
					System.out.println(expiredFiles.get(j).getName() + " is deleted!");
				}else{
					System.out.println("Delete operation is failed.");
				}
			}
			expiredFiles.clear();
		}
	}
	
	private static void moveDirectory(String path){
		File dir1 = new File(currentUser.getPath());
		File newPath = new File(path);
		try {
			newPath.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(dir1.isDirectory()) {
		    File[] content = dir1.listFiles();
		    for(int i = 0; i < content.length; i++) {
		        content[i].renameTo(new File(path, content[i].getName()));
		    }
		}
	}
}
