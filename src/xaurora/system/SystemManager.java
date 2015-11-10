package xaurora.system;

import xaurora.io.DataFileIO;

public class SystemManager {

	public static boolean updateDirectory(String dir){
		DataFileIO io = DataFileIO.instanceOf();
		
		return io.setDirectory(dir);
		
	}
}
