package xaurora.dropbox;

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
