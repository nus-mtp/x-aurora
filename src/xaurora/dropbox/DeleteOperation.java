package xaurora.dropbox;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;

public class DeleteOperation {

	public static void DeleteSingleFile(String fileName, DbxClient client){
		String filePath = "/" + fileName;
		try {
			client.delete(filePath);
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
