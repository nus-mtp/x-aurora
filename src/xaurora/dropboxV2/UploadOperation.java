package src.xaurora.dropboxV2;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxWriteMode;

public class UploadOperation {

	public static void UploadSingleFile(String Directory, String fileName, DbxClient client){
		String localFilePath = Directory + "/" + fileName;
		String onlineFilePath = "/" + fileName;
		File inputFile = new File(localFilePath);
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(inputFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			try {
				DbxEntry.File uploadedFile = client.uploadFile(onlineFilePath,
						DbxWriteMode.add(), inputFile.length(), inputStream);
				System.out.println(fileName + " is uploaded to dropbox");
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
