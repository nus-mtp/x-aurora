package xaurora.system;

import xaurora.io.DataFileIO;

public class DBManager implements Runnable{
	private static long CHECK_INTERVAL = 600000;
	private boolean isToUpdate;
	public void run() {
		while(true){
			if(isCorrectUpdateTiming()&&this.isToUpdate){
				DataFileIO.instanceOf().updateIndexingFromFiles();
				System.out.println("DB UPDATE COMPLETE");
				this.isToUpdate = false;
			}
			if(isCorrectCheckTiming()&&!this.isToUpdate){
				//DataFileIO.instanceOf().autoDbUpdate();
				System.out.println("auto update complete");
			}
		}
		
	}
	public DBManager(){
		this.isToUpdate = true;
	}
	private static boolean isCorrectCheckTiming(){
		long currentTime = System.currentTimeMillis();
		//System.out.println(currentTime/1000%60);
		return currentTime%60000== 0;
	}
	private static boolean isCorrectUpdateTiming(){
		long currentTime = System.currentTimeMillis();
		//System.out.println(currentTime/1000%60);
		return currentTime%60000== 30000;
	}
}
