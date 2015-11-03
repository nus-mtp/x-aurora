package xaurora.io;

public class DataFileIO {
	private static String syncDirectory;
	private static DataFileIO instance = null;
	protected DataFileIO(){
		
	}
	public static DataFileIO instanceOf(){
		if(instance == null){
			instance = new DataFileIO();
		}
		return new DataFileIO();
	}
	
}
