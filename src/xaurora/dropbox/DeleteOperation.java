package xaurora.dropbox;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;

public class DeleteOperation {

	public void DeleteSingleFile(String fileName, DbxClient client) throws DbxException{
		String filePath = "/" + fileName;
		client.delete(filePath);
	}
}
