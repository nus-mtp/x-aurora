package xaurora.dropbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxWriteMode;

public class UploadOperation {

	public void UploadSingleFile(String fileName, DbxClient client) throws DbxException, IOException{
		String filePath = "/" + fileName;
		File inputFile = new File(fileName);
		FileInputStream inputStream = new FileInputStream(inputFile);
		try {
			DbxEntry.File uploadedFile = client.uploadFile(filePath,
					DbxWriteMode.add(), inputFile.length(), inputStream);
			System.out.println("Uploaded: " + uploadedFile.toString());
		} finally {
			inputStream.close();
		}
	}
	
}
