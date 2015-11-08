package xaurora.io;
import java.io.*;
public class DataFileIO {
	private String syncDirectory = "";
	private static DataFileIO instance = null;
	private DataFileIO(){
		
	}
	public static DataFileIO instanceOf(){
		if(instance == null){
			instance = new DataFileIO();
		}
		return new DataFileIO();
	}
	public void setDirectory(String path){
		this.syncDirectory = path;
	}
	public void createDataFile(byte[] filename,byte[] content){
		String dstpath = this.syncDirectory+String.valueOf(filename);
		File dstFile = new File(dstpath);
		if(dstFile.exists()){
			System.err.println("ERROR MESSAGE: MD5 COLLISION");
			System.exit(1);
		} else {
			try{
				dstFile.createNewFile();
				FileOutputStream fos = new FileOutputStream(dstFile.getAbsolutePath());
				fos.write(content);
				fos.close();
			} catch(IOException e){
				
			}
		}
	}
}
