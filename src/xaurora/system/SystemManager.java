package xaurora.system;

import java.io.IOException;

import xaurora.io.DataFileIO;

public class SystemManager {
    private static final String HOST_DROPBOX = "www.dropbox.com";
    private static final String COMMAND_PING = "ping ";
    private static final String UNSYNC_DATA_PATH = "\\local_data\\";
    private static final int INTERNAL_SUCCESS = 0;
    private boolean isLogin;
    private DataFileIO io;
    private static SystemManager s;

    public boolean updateDirectory(String dir) {
        return this.io.setDirectory(dir);
    }

    private SystemManager() {
        this.io = DataFileIO.instanceOf();
    }

    public static SystemManager getInstance() {
        if (s == null) {
            s = new SystemManager();
        }
        return s;
    }

    public void login(boolean isLogin) {
        this.isLogin = isLogin;
        if (this.isLogin) {
            this.io.setDirectory(UNSYNC_DATA_PATH);
        } else {
            this.io.setDirectory(UNSYNC_DATA_PATH);
        }
    }

    // Description: check whether the Internet is available by trying to ping
    // the dropbox host
    // post-condition: return true if response is obtain by pinging the dropbox
    // host, else return false
    public boolean isNetAccessible() {
        boolean result = false;
        try {
            Process pingProcess = java.lang.Runtime.getRuntime()
                    .exec(COMMAND_PING + HOST_DROPBOX);
            try {
                result = (pingProcess.waitFor() == INTERNAL_SUCCESS);

                pingProcess.destroy();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
