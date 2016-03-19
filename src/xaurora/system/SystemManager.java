package xaurora.system;

import java.io.IOException;

import xaurora.io.DataFileIO;
import xaurora.io.SystemIO;
import xaurora.security.Security;
import xaurora.text.TextIndexer;

public class SystemManager {
    private static final String HOST_DROPBOX = "www.dropbox.com";
    private static final String COMMAND_PING = "ping ";
    private static final String UNSYNC_DATA_PATH = "\\local_data\\";
    private static final String DEFAULT_USERNAME = "default";
    private static final int DEFAULT_DISPLAY_NUMBER = 5;
    private static final int DEFAULT_EXPIRY_HOURS = 72;
    private static final int INTERNAL_SUCCESS = 0;
    private static final String EMPTY_STRING = "";
    private boolean isLogin;
    private DataFileIO io;
    private static SystemManager s;
    private String currentUserName;
    private String userEmail;
    private String storeDirectory;
    private int expiryHours = DEFAULT_EXPIRY_HOURS;
    private int displayNumber = DEFAULT_DISPLAY_NUMBER;
    private SystemIO systemIo;
    private SecurityManager secure;
    private TextIndexer indexer;
    private TimeManager timeManager;
    private boolean isInitialized = false;
    private SystemManager() {
        this.io = DataFileIO.instanceOf();
        this.systemIo = SystemIO.getClassInstance();
        if(!systemIo.isLocalKeyCreated()){
            byte[] entry = Security.generateLocalKey(DEFAULT_USERNAME);
            this.systemIo.registerNewUser(DEFAULT_USERNAME, EMPTY_STRING, entry, io);
        }
        this.secure = SecurityManager.getClassInstance(this.systemIo);
        this.timeManager = TimeManager.getInstance();
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
    /**
     * This should only be call after finish login
     * and finish data synchronization from drop-box 
     */
    public synchronized void triggerInitialization(){
        assert this.currentUserName !=null;
        assert this.userEmail != null;
        assert this.storeDirectory != null;
        this.secure = SecurityManager.getClassInstance(this.systemIo);
        if(this.secure.isNewUser(this.currentUserName, this.userEmail)){
            byte[] entry = Security.generateUserKey(this.currentUserName, this.userEmail);
            this.systemIo.registerNewUser(this.currentUserName, this.userEmail, entry, io);
            this.secure.reInit(this.systemIo);
        } 
        this.secure.setCurrentHash(Security.hashUserName(this.currentUserName, this.userEmail));
        this.systemIo.setUpUserIndexDirectory(this.secure.getCurrentHash(), this.io);
        this.io.setDirectory(this.storeDirectory);
        this.indexer = TextIndexer.getInstance(this.io);
        this.indexer.setDisplayNumber(this.displayNumber);
        this.timeManager.setExpiredInterval(this.expiryHours);
        this.isInitialized = true;
    }
    public final TextIndexer getIndexerInstance(){
        assert this.isInitialized;
        return this.indexer;
    }
    public final SecurityManager getSecurityManagerInstance(){
        assert this.isInitialized;
        return this.secure;
    }
    public final DataFileIO getDataFileIOInstance(){
        assert this.isInitialized;
        return this.io;
    }
    public final TimeManager getTimeManagerInstance(){
        assert this.isInitialized;
        return this.timeManager;
    }
    public final boolean isManagerInitialize(){
        return this.isInitialized;
    }
    public final void setCurrentUser(final String userName){
        this.currentUserName = userName;
    }
    public final void setSyncDirectory(final String directory){
        this.storeDirectory = directory;
    }
    public final void setExpireTime(final int numOfHours){
        this.expiryHours = numOfHours;
    }
    public final void setDisplayNumber(final int displayNumber){
        this.displayNumber = displayNumber;
    }
    public final void setUserEmail(final String email){
        this.userEmail = email;
    }
}
