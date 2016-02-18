package xaurora.dropbox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DownloadOperation {
	
	public static void DownloadSingleFile(String fileName, DbxClient client){
		String filePath = "/" + fileName;
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(fileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			DbxEntry.File downloadedFile = null;
			try {
				downloadedFile = client.getFile(filePath, null, outputStream);
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
