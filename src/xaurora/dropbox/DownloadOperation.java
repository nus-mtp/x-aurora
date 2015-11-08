package xaurora.dropbox;

import java.io.FileOutputStream;
import java.io.IOException;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;

public class DownloadOperation {
	
	public void DownloadSingleFile(String fileName, DbxClient client) throws DbxException, IOException{
		String filePath = "/" + fileName;
		FileOutputStream outputStream = new FileOutputStream(fileName);
		try {
			DbxEntry.File downloadedFile = client.getFile(filePath, null,
					outputStream);
			System.out.println("Metadata: " + downloadedFile.toString());
		} finally {
			outputStream.close();
		}
	}

}
