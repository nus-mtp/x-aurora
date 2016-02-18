package xaurora.dropbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxWriteMode;

public class UploadOperation {

	public static void UploadSingleFile(String fileName, DbxClient client){
		String filePath = "/" + fileName;
		File inputFile = new File(fileName);
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(inputFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			try {
				DbxEntry.File uploadedFile = client.uploadFile(filePath,
						DbxWriteMode.add(), inputFile.length(), inputStream);
			} catch (DbxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
