package xaurora.system;

import xaurora.dropbox.DropboxMain;
import xaurora.io.DataFileIO;

public class SystemManager {
	private static final String UNSYNC_DATA_PATH = "\\local_data\\";
	private boolean isLogin;
	private DataFileIO io;
	private static SystemManager s;
	public boolean updateDirectory(String dir){
		this.io = DataFileIO.instanceOf();
		
		return this.io.setDirectory(dir);
		
	}
	private SystemManager(){
				
	}
	public static SystemManager getInstance(){
		if(s == null){
			s = new SystemManager();
		}
		return s;
	}
	public void login(boolean isLogin){
		this.isLogin = isLogin;
		if(this.isLogin){
			this.io.setDirectory(DropboxMain.getCurrentUser().getPath());
		} else {
			this.io.setDirectory(UNSYNC_DATA_PATH);
		}
	}
}
