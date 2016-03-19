package xaurora.system;

import xaurora.system.SystemManager;;

public final class DBManager implements Runnable {
    private static final long DEFAULT_CHECK_INTERVAL = 60000;// 10 minute
    private boolean isToUpdate;
    private SystemManager instance;
    public void run() {
        while (true) {
            if (isCorrectUpdateTiming() && this.isToUpdate) {
                System.out.println("update start");
                this.instance.getDataFileIOInstance().updateIndexingFromFiles(this.instance);
                System.out.println("udpate complete");
                this.isToUpdate = false;
            }
            if (isCorrectCheckTiming() && !this.isToUpdate) {
                this.instance.getDataFileIOInstance().autoCheckForExpiredFile(this.instance);
            }
        }

    }

    public DBManager() {
        this.isToUpdate = true;
        this.instance = SystemManager.getInstance();
    }

    private static boolean isCorrectCheckTiming() {
        long currentTime = System.currentTimeMillis();
        // Every 10 minutes
        return currentTime % DEFAULT_CHECK_INTERVAL == 10000;
    }

    private static boolean isCorrectUpdateTiming() {
        long currentTime = System.currentTimeMillis();
        // Every 5th minutes within a 10-minute-period
        return currentTime % DEFAULT_CHECK_INTERVAL == 30000;
    }
}
