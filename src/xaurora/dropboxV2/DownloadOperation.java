package src.xaurora.dropboxV2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;

public class DownloadOperation {
	
	public static void DownloadSingleFile(String Directory, String fileName, DbxClient client){
		String localFilePath = Directory + "/" + fileName;
		String onlineFilePath = "/" + fileName;

		File toBeDownloadFile = new File(localFilePath);
		if(!toBeDownloadFile.exists()) {
			try {
				toBeDownloadFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(toBeDownloadFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			DbxEntry.File downloadedFile = null;
			try {
				downloadedFile = client.getFile(onlineFilePath, null, outputStream);
				System.out.println(fileName + " is downloaded from dropbox");
			} catch (DbxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			try {
				outputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
