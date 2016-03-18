package xaurora.system;

import xaurora.io.DataFileIO;

public class DBManager implements Runnable{
	private static final long DEFAULT_CHECK_INTERVAL = 60000;//10 minute
	private boolean isToUpdate;
	public void run() {
		while(true){
			if(isCorrectUpdateTiming()&&this.isToUpdate){
				DataFileIO.instanceOf().updateIndexingFromFiles();
				System.out.println("DB UPDATE COMPLETE");
				this.isToUpdate = false;
			}
			if(isCorrectCheckTiming()&&!this.isToUpdate){
				DataFileIO.instanceOf().autoCheckForExpiredFile();
				System.out.println("auto update complete");
			}
		}
		
	}
	public DBManager(){
		this.isToUpdate = true;
	}
	private static boolean isCorrectCheckTiming(){
		long currentTime = System.currentTimeMillis();
		//Every 10 minutes
		return currentTime%DEFAULT_CHECK_INTERVAL == 10000;
	}
	private static boolean isCorrectUpdateTiming(){
		long currentTime = System.currentTimeMillis();
		//Every 5th minutes within a 10-minute-period
		return currentTime%DEFAULT_CHECK_INTERVAL == 30000;
	}
}
