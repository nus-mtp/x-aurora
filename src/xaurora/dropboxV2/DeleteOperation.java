package src.xaurora.dropboxV2;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;

public class DeleteOperation {

	public static void DeleteSingleFile(String fileName, DbxClient client){
		String filePath = "/" + fileName;
		try {
			client.delete(filePath);
			System.out.println(fileName + " is deleted in dropbox");
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
