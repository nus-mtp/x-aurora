package xaurora.system;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.log4j.Logger;
import xaurora.io.DataFileIO;
import xaurora.io.SystemIO;
import xaurora.security.Security;
import xaurora.text.TextIndexer;
import xaurora.ui.Message;

/**
 * Thie class is the main controller of the Logic component that controls all
 * the initialization of all the controller within Logic Component
 * 
 * @author GAO RISHENG A0101891L
 *
 */
public final class SystemManager {

    private static final String ERR_MSG_INVALID_USER_UPDATE = "Error, invalid update of user profile. Last update is unsuccessful.";
    private static final String ERR_MSG_INVALID_UPDATE_EMAIL = "Error, the system is trying to update a null email.";
    private static final String ERR_MSG_INVALID_UPDATE_DISPLAY_NUMBER = "Error, system is trying to update display number with a non-positive number {}.";
    private static final String ERR_MSG_INVALID_UPDATE_EXPIRY_HOURS = "Error, system is trying to update a non-positive expiry time in hours {}";
    private static final String ERR_MSG_INVALID_UPDATE_STORE_DIRECTORY = "Error, system is trying to set an invalid(null) store path.";
    private static final String ERR_MSG_INVALID_UPDATE_USERNAME = "Error, system is trying to set an invalid userName {}.";
    private static final String MSG_USER_INIT_COMPLETE = "User Setting Initialization Complete.";
    private static final String MSG_USER_INIT = "Start initializing for user settings.";
    private static final String ERR_MSG_UNABLE_TO_CHECK_FOR_NETWORK_ACCESSIBILITY = "Error occurs when trying to check for network accessibility. The error message is {}.";
    private static final String ERR_MSG_NET_ACCESS_INTERRUPT = "Error occurs when trying to get response from the remote server. The error message is {}.";
    private static final String MSG_FOUNDATION_COMPLETE = "Primary Initialization Complete.";
    private static final String MSG_FOUNDATION_INIT = "Primary Initialization starts. This is the initialization that construct the foundation before user login.";
    private static final String MSG_START = "An instance of System Manager is created. This message should appear only once at every software running flow.";
    private static final String SECURITY_MSG_DISABLE_SERIALIZE = "Object cannot be serialized";
    private static final String CLASS_CANNOT_BE_DESERIALIZED = "Class cannot be deserialized";
    private static final String HOST_DROPBOX = "www.dropbox.com";
    private static final String COMMAND_PING = "ping ";
    private static final String DEFAULT_USERNAME = "default";
    private static final String DEFAULT_SYNC_DIRECTORY = "/local_data/";
    private static final int MINIMUM_NON_NEGATIVE = 0;
    private static final int DEFAULT_DISPLAY_NUMBER = 5;
    private static final int DEFAULT_EXPIRY_HOURS = 72;
    private static final int INTERNAL_SUCCESS = 0;
    private static final String EMPTY_STRING = "";
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
    private Logger logger;
    private Message message = new Message();

    private SystemManager() {

        this.logger = Logger.getLogger(this.getClass());
        this.logger.info(MSG_START);
        this.logger.info(MSG_FOUNDATION_INIT);
        
        this.io = DataFileIO.instanceOf();
        this.systemIo = SystemIO.getClassInstance();
        this.secure = SecurityManager.getClassInstance(this.systemIo);
        if (!systemIo.isLocalKeyCreated()) {
            byte[] entry = Security.generateLocalKey(DEFAULT_USERNAME);
            this.systemIo.registerNewUser(DEFAULT_USERNAME, EMPTY_STRING, entry,
                    this.io, this.secure);
        }

        this.timeManager = TimeManager.getInstance();
        this.logger.info(MSG_FOUNDATION_COMPLETE);
    }

    /**
     * Singleton class instance getter
     * 
     * @return the un-initialized instance of System Manager (before user login)
     */
    public static SystemManager getInstance() {
        if (s == null) {
            s = new SystemManager();
        }
        return s;
    }

    /**
     * Description: check whether the Internet is available by trying to ping
     * the drop-box host
     * 
     * @return true if response is obtained by pinging the drop-box other wise
     *         return false
     * @author GAO RISHENG A0101891L
     */
    public final boolean isNetAccessible() {
        boolean result = false;
        try {
            Process pingProcess = java.lang.Runtime.getRuntime()
                    .exec(COMMAND_PING + HOST_DROPBOX);
            try {
                result = (pingProcess.waitFor() == INTERNAL_SUCCESS);

                pingProcess.destroy();
            } catch (InterruptedException e) {
                this.logger.error(ERR_MSG_NET_ACCESS_INTERRUPT, e);
                message.showError(ERR_MSG_NET_ACCESS_INTERRUPT);
            }
        } catch (IOException e) {
            this.logger.error(ERR_MSG_UNABLE_TO_CHECK_FOR_NETWORK_ACCESSIBILITY,
                    e);
            message.showError(ERR_MSG_UNABLE_TO_CHECK_FOR_NETWORK_ACCESSIBILITY);
        }
        return result;
    }

    /**
     * This should only be call after finish login and finish data
     * synchronization from drop-box Description: initializing all internal
     * manager to setting up the file system , building the indexing database,
     * reading user secret keys
     * 
     * @author GAO RISHENG A0101891L
     */
    private synchronized final void triggerInitialization() {
        // 3 assertions that must be fulfilled
        assert this.currentUserName != null
                && !this.currentUserName.trim().equals(EMPTY_STRING);
        assert this.userEmail != null;
        assert this.storeDirectory != null;
        this.logger.info(MSG_USER_INIT);
        this.secure = SecurityManager.getClassInstance(this.systemIo);
        if (this.secure.isNewUser(this.currentUserName, this.userEmail)) {
            byte[] entry = Security.generateUserKey(this.currentUserName,
                    this.userEmail);
            this.systemIo.registerNewUser(this.currentUserName, this.userEmail,
                    entry, this.io, this.secure);
            this.secure.reInit(this.systemIo);
        }
        this.secure.setCurrentHash(
                Security.hashUserName(this.currentUserName, this.userEmail));
        this.systemIo.setUpUserIndexDirectory(this.secure.getCurrentHash(),
                this.io);
        this.io.setDirectory(this.storeDirectory);
        this.indexer = TextIndexer.getInstance(this.io);
        this.indexer.setDisplayNumber(this.displayNumber);
        this.timeManager.setExpiredIntervalHour(this.expiryHours);
        this.isInitialized = true;
        this.logger.info(MSG_USER_INIT_COMPLETE);
        DBManager.getClassInstance().reInit();
    }

    /**
     * Description: This method is to switch user profile for different user
     * 
     * @param userName,
     *            the new user Name
     * @param email,
     *            the new user email
     * @param directory,
     *            the new user sync directory
     * @param displayNumber,the
     *            display number of the new user
     * @param expiry,
     *            the expiry time of new user
     * @author GAO RISHENG A0101891L
     */
    public synchronized final void changeUser(String userName, String email,
            String directory, int displayNumber, int expiry) {
        String currentUserName = this.currentUserName;
        String currentEmail = this.userEmail;
        boolean isUpdateValid = true;
        isUpdateValid &= this.setCurrentUser(userName);
        isUpdateValid &= this.setUserEmail(email);
        isUpdateValid &= this.setSyncDirectory(directory);
        isUpdateValid &= this.setDisplayNumber(displayNumber);
        isUpdateValid &= this.setExpireTime(expiry);
        if (isUpdateValid) {
            if (currentUserName != null)
                this.systemIo.cleanIndexData(this.io,
                        Security.hashUserName(currentUserName, currentEmail));
            triggerInitialization();
            DBManager.getClassInstance().reInit();
        } else {
            this.logger.error(ERR_MSG_INVALID_USER_UPDATE);
            message.showError(ERR_MSG_INVALID_USER_UPDATE);
        }
    }

    /**
     * Description: reset all user info stored in the system to default value
     * which is equivalent to change the current user to the default user which
     * is equivalent to logout which is equivalent to re-initialize the system
     */
    public synchronized final void reset() {
        this.changeUser(DEFAULT_USERNAME, EMPTY_STRING,
                getDefaultStoreDirectory(), DEFAULT_DISPLAY_NUMBER,
                DEFAULT_EXPIRY_HOURS);
    }

    /**
     * Attribute Getter for Text Indexer
     * 
     * @return a text indexer instance
     * @author GAO RISHENG A0101891L
     */
    public final TextIndexer getIndexerInstance() {
        assert this.isInitialized;
        return this.indexer;
    }

    /**
     * Attribute Getter for Security Manager
     * 
     * @return a security Manager instance
     * @author GAO RISHENG A0101891L
     */
    public final SecurityManager getSecurityManagerInstance() {
        assert this.isInitialized;
        return this.secure;
    }

    /**
     * Attribute Getter for Data FileIO
     * 
     * @return a data file IO instance
     * @author GAO RISHENG A0101891L
     */
    public final DataFileIO getDataFileIOInstance() {
        assert this.isInitialized;
        return this.io;
    }

    /**
     * Attribute Getter for Time Manager
     * 
     * @return a time manager instance
     * @author Gao Risheng A0101891L
     */
    public final TimeManager getTimeManagerInstance() {
        assert this.isInitialized;
        return this.timeManager;
    }
    /**
     * Description: test get method
     * @return the user name of the current user
     * @author GAO RISHENG A0101891L
     */
    public final String getUserName(){
        assert this.isInitialized;
        assert this.currentUserName!=null;
        return this.currentUserName;
    }
    /**
     * Description: test get method
     * @return the email of the current user
     * @author GAO RISHENG A0101891L
     */
    public final String getEmail(){
        assert this.isInitialized;
        assert this.currentUserName!=null;
        return this.userEmail;
    }
    /**
     * Description: test get method
     * @return the expiry interval of the current user
     * @author GAO RISHENG A0101891L
     */
    public final int getExpireInterval(){
        assert this.isInitialized;
        assert this.expiryHours>=MINIMUM_NON_NEGATIVE;
        return this.expiryHours;
    }
    /**
     * Description: test get method
     * @return the display number of the current user
     * @author GAO RISHENG A0101891L
     */
    public final int getDisplayNumber(){
        assert this.isInitialized;
        assert this.displayNumber>=MINIMUM_NON_NEGATIVE;
        return this.displayNumber;
    }
    /**
     * Description: Check whether this instance is initialized
     * 
     * @return true if and only if this instance is initialized
     * @author GAO RISHENG A0101891L
     */
    public final boolean isManagerInitialize() {
        return this.isInitialized;
    }

    /**
     * Description: Setting the current user name
     * 
     * @param userName,
     *            the user name of the current user
     * @author GAO RISHENG A0101891L
     */
    private final boolean setCurrentUser(final String userName) {
        if (userName != null && userName.trim() != EMPTY_STRING) {
            this.currentUserName = userName;
            return true;
        } else {
            this.logger.error(ERR_MSG_INVALID_UPDATE_USERNAME + userName);
            message.showError(ERR_MSG_INVALID_UPDATE_USERNAME);
            return false;
        }
    }

    /**
     * Description: Setting the directory that stores the data
     * 
     * @param directory,
     *            the path name of the directory
     * @author GAO RISHENG A0101891L
     */
    private final boolean setSyncDirectory(final String directory) {
        if (directory != null) {
            this.storeDirectory = directory;
            return true;
        } else {
            this.logger.error(ERR_MSG_INVALID_UPDATE_STORE_DIRECTORY);
            message.showError(ERR_MSG_INVALID_UPDATE_STORE_DIRECTORY);
            return false;
        }
    }

    /**
     * Description: Setting the expire time for extracted text
     * 
     * @param numOfHours,number
     *            of hours for extracted file to be deleted
     * @author GAO RISHENG A0101891L
     */
    private final boolean setExpireTime(final int numOfHours) {
        if (numOfHours >= MINIMUM_NON_NEGATIVE) {
            this.expiryHours = numOfHours;
            return true;
        } else {
            this.logger.error(ERR_MSG_INVALID_UPDATE_EXPIRY_HOURS + numOfHours);
            message.showError(ERR_MSG_INVALID_UPDATE_EXPIRY_HOURS);
            return false;
        }
    }

    /**
     * Description: setting the number of item to be displayed as the search
     * result
     * 
     * @param displayNumber
     * @author GAO RISHENG A0101891L
     */
    private final boolean setDisplayNumber(final int displayNumber) {
        if (displayNumber > MINIMUM_NON_NEGATIVE) {
            this.displayNumber = displayNumber;
            return true;
        } else {
            this.logger.error(
                    ERR_MSG_INVALID_UPDATE_DISPLAY_NUMBER + displayNumber);
            message.showError(ERR_MSG_INVALID_UPDATE_DISPLAY_NUMBER);
            return false;
        }
    }

    /**
     * Description: Setting the user Email for current user
     * 
     * @param email,
     *            user Email
     * @author Gao Risheng A0101891L
     */
    private final boolean setUserEmail(final String email) {
        if (email != null) {
            this.userEmail = email;
            return true;
        } else {
            this.logger.error(ERR_MSG_INVALID_UPDATE_EMAIL);
            message.showError(ERR_MSG_INVALID_UPDATE_EMAIL);
            return false;
        }
    }

    /**
     * @return the sync directory of default user
     * @author GAO RISHENG
     */
    private final String getDefaultStoreDirectory() {
        File temp = new File(EMPTY_STRING);
        return temp.getAbsolutePath() + DEFAULT_SYNC_DIRECTORY;
    }

    /**
     * Secure Programming. Making this Object not-clonable. Object.clone()
     * allows cloning the data of an object without initialize it which may leak
     * the chances for attacker to access the data internally
     * 
     * @Author GAO RISHENG A0101891L
     */
    public final Object clone() throws java.lang.CloneNotSupportedException {
        throw new java.lang.CloneNotSupportedException();
    }

    /**
     * Secure Programming. Disable the serialize option of the object which
     * avoid attacker to print the object in serialize manner and inspect the
     * internal status of the object
     * 
     * @author GAO RISHENG A0101891L
     */
    private final void writeObject(ObjectOutputStream out)
            throws java.io.IOException {
        throw new java.io.IOException(SECURITY_MSG_DISABLE_SERIALIZE);
    }

    /**
     * Secure Programming. Disable the de-serialize option of the object which
     * avoid attacker to de-serialize the object stores in the file system and
     * inspect the internal status of the object
     * 
     * @author GAO RISHENG A0101891L
     */
    private final void readObject(ObjectInputStream in)
            throws java.io.IOException {
        throw new java.io.IOException(CLASS_CANNOT_BE_DESERIALIZED);
    }
}
