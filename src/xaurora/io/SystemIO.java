package xaurora.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import xaurora.security.Security;

/**
 * Description: This class is mainly in charge of file IO that related to system
 * i.e creating indexing directory that for current user, read and write the
 * user key set file etc
 * 
 * @author GAO RISHENG A0101891l
 *
 */
public final class SystemIO {
    
    private static final String MSG_READ_KEYS_SUCCESS = "Successfully Loading the local key set.";
    private static final String ERR_MSG_FAIL_TO_READ_KEY_FROM_KEY_FILE = "Fail to read the key for the following key file : {}. And the Error message is {}.";
    private static final String MSG_START_RETRIEVING_KEYS = "A Local Device key set construction request is triggered. Start Reading all local keys.";
    private static final String MSG_CREATE_KEY_SUCCESS = "The key for current user {} is created successfully.";
    private static final String ERR_MSG_FAIL_TO_CREATE_KEY_FILE = "Error occurs in creating key set file for the current user {}, the error message is {}.";
    private static final String MSG_GENERATE_LOCAL_KEY = "System starts to generate local key for default user/users who does not choose to login.";
    private static final String ERR_MSG_UNABLE_TO_CREATE_INDEX_DIRECTORY = "Error occurs in creating the indexing directory,it may be caused by invalid path name.";
    private static final String ERR_MSG_UNABLE_TO_CLEAN_UP_PREVIOUS_INDEXING_DIRECTORY = "Error occurs at cleanning up the old data in the indexing directory of this user. The error message is {}.";
    private static final String ERR_MSG_FAIL_TO_CREATE_USER_DIRECTORY = "User directory does not exist and fail to create system directory.";
    private static final String ERR_MSG_FAIL_TO_CREATE_SYSTEM_DIRECTORY = "System directory does not exist and fail to create system directory.";
    private static final String MSG_UPDATE_INDEX_DIRECTORY_SUCCESS = "Successfully update the indexing directory for user {}.";
    private static final String MSG_USER_REGISTRATION = "A new user with hashed user name {} is register to this local device.";
    private static final String MSG_START = "An instance of SystemIO is created. This message should appear only once at every software running flow.";
    private static final String ERROR_OVERWRITING_LOCAL_KEY = "error! Overwriting local key";
    private static final String SECURITY_MSG_DISABLE_SERIALIZE = "Object cannot be serialized";
    private static final String CLASS_CANNOT_BE_DESERIALIZED = "Class cannot be deserialized";
    private static final String SYSTEM_CONF_DIRECTORY = "/conf_data/";
    private static final String SYSTEM_USER_DIRECTORY = "/user/";
    private static final String PATH_SEPARATOR = "\\";
    private static final String KEY_SET_FILE = "KEY_SET";
    private static final String KEY_SET_EXTENSION = ".ks";
    private static final String KEY_SET_FILE_EXTENSION = "ks";
    private static final String NEW_EMPTY_STRING = "";
    private static final String DEFAULT_INDEX_DIRECTORY = "/index_data/";
    private static final String DEFAULT_USERNAME = "default";
    private static final int HASH_LENGTH = 32;
    private static final int INDEX_ZERO = 0;
    private Logger logger;
    private static SystemIO classInstance;

    private SystemIO() {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.init();
    }

    /**
     * Description: Singleton Class instance getter
     * 
     * @return an initialized SystemIO instance
     * @author Gao Risheng A0101891L
     */
    public static final SystemIO getClassInstance() {
        if (classInstance == null) {
            classInstance = new SystemIO();
        }
        return classInstance;
    }

    /**
     * Description: initializing the System IO by creating the conf_data folder
     * and user folder
     * 
     * @author Gao Risheng A0101891L
     */
    private final void init() {
        File temp = new File(NEW_EMPTY_STRING);
        File systemDir = new File(
                temp.getAbsolutePath() + SYSTEM_CONF_DIRECTORY);
        File userDir = new File(
                systemDir.getAbsolutePath() + SYSTEM_USER_DIRECTORY);
        this.logger.info(MSG_START);
        if (!systemDir.mkdir()) {
            if (!systemDir.exists()) {
                this.logger.error(ERR_MSG_FAIL_TO_CREATE_SYSTEM_DIRECTORY);
            }
        }
        assert systemDir.exists();
        if (!userDir.mkdir()) {
            if (!userDir.exists()) {
                this.logger.error(ERR_MSG_FAIL_TO_CREATE_USER_DIRECTORY);
            }
        }
        assert userDir.exists();
    }

    /**
     * Description: register a new user to the local device after he/she logs in
     * 
     * @param userName,
     *            The String user name
     * @param email,
     *            the user email
     * @param entry,
     *            the secure salt generated to this user
     * @param instance,
     *            the DataFileIO instance to be updated
     * 
     * @author Gao Risheng A0101891L
     */
    public final void registerNewUser(final String userName, final String email,
            final byte[] entry, DataFileIO instance) {
        assert userName != null && email != null
                && !userName.trim().equals(NEW_EMPTY_STRING);
        
        final String hashName = Security.hashUserName(userName, email);
        setUpUserIndexDirectory(hashName, instance);
        createKeyFile(hashName, entry);
        this.logger.info(MSG_USER_REGISTRATION, hashName);
    }

    /**
     * Description: Setting up the local indexing directory for the current user
     * 
     * @param hashName,
     *            the hashed String user name
     * @param instance,
     *            the DataFileIO instance which request for update
     * 
     * @author Gao Risheng A0101891L
     */
    public final void setUpUserIndexDirectory(final String hashName,
            DataFileIO instance) {

        File temp = new File(NEW_EMPTY_STRING);
        File systemDir = new File(
                temp.getAbsolutePath() + SYSTEM_CONF_DIRECTORY);
        File userDir = new File(
                systemDir.getAbsolutePath() + SYSTEM_USER_DIRECTORY);
        File personalDirectory = new File(
                userDir.getAbsolutePath() + PATH_SEPARATOR + hashName);
        File indexDirectory = new File(
                personalDirectory.getAbsolutePath() + DEFAULT_INDEX_DIRECTORY);
        if (indexDirectory.mkdirs()) {
            instance.setIndexDirectory(indexDirectory.getAbsolutePath());
        } else {
            if (indexDirectory.exists() && indexDirectory.isDirectory()) {
                try {
                    FileUtils.cleanDirectory(indexDirectory);
                } catch (IOException e) {
                    this.logger.error(
                            ERR_MSG_UNABLE_TO_CLEAN_UP_PREVIOUS_INDEXING_DIRECTORY,
                            e.getMessage());
                    // ASSUMPTION: Fail in cleaning the indexing directory of
                    // this user will not affect new DataBase construction
                }
                instance.setIndexDirectory(indexDirectory.getAbsolutePath());
            } else {
                this.logger.error(ERR_MSG_UNABLE_TO_CREATE_INDEX_DIRECTORY);
                // In this case, the index directory will be set to the default
                // value in DataFileIO class.
            }
        }
        assert indexDirectory.exists();
        // problem for future development: Need to have an error manager to
        // contact the logic with UI to let the user know what is getting wrong
        this.logger.info(MSG_UPDATE_INDEX_DIRECTORY_SUCCESS,hashName);
    }

    /**
     * Description: Verify whether the local key to this device is created by
     * checking the existence of the key set file
     * 
     * @return true when the local key set file exist, false otherwise.
     * 
     * @author Gao Risheng A0101891L
     */
    public final boolean isLocalKeyCreated() {
        String filename = Security.hashUserName(KEY_SET_FILE, NEW_EMPTY_STRING);
        String directoryName = Security.hashUserName(DEFAULT_USERNAME,
                NEW_EMPTY_STRING);
        File temp = new File(NEW_EMPTY_STRING);
        File systemDir = new File(
                temp.getAbsolutePath() + SYSTEM_CONF_DIRECTORY);
        File userDir = new File(
                systemDir.getAbsolutePath() + SYSTEM_USER_DIRECTORY);
        File personalDirectory = new File(
                userDir.getAbsolutePath() + PATH_SEPARATOR + directoryName);
        File localKeyFile = new File(personalDirectory.getAbsoluteFile()
                + PATH_SEPARATOR + filename + KEY_SET_EXTENSION);
        return localKeyFile.exists();
    }

    /**
     * Description: Creating the unique local key for this device for users who
     * do not use the log in feature of this software
     * 
     * @param entry,
     *            the random salt that belongs to default user in this device
     * 
     * @author Gao Risheng A0101891L
     */
    public final void createLocalKey(final byte[] entry) {
        String filename = Security.hashUserName(DEFAULT_USERNAME,
                NEW_EMPTY_STRING);
        this.logger.info(MSG_GENERATE_LOCAL_KEY);
        createKeyFile(filename, entry);
    }

    /**
     * Description: Creating the local key file for storing users' key
     * 
     * @param hashName,
     *            the Hashed user Name
     * @param entry,
     *            The random salt belongs to this user
     * 
     * @author GAO RISHENG A0101891L
     */
    public final void createKeyFile(final String hashName, final byte[] entry) {
        
        String filename = Security.hashUserName(KEY_SET_FILE, NEW_EMPTY_STRING);
        File temp = new File(NEW_EMPTY_STRING);
        File systemDir = new File(
                temp.getAbsolutePath() + SYSTEM_CONF_DIRECTORY);
        File userDir = new File(
                systemDir.getAbsolutePath() + SYSTEM_USER_DIRECTORY);
        File personalDirectory = new File(
                userDir.getAbsolutePath() + PATH_SEPARATOR + hashName);
        File keySetFile = new File(personalDirectory.getAbsoluteFile()
                + PATH_SEPARATOR + filename + KEY_SET_EXTENSION);
        assert !keySetFile.exists() : ERROR_OVERWRITING_LOCAL_KEY;
        if (!keySetFile.exists()) {

            try {
                keySetFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(
                        keySetFile.getAbsolutePath());
                fos.write(entry);
                fos.flush();
                fos.close();
                keySetFile.setReadOnly();
                keySetFile.setReadable(true);
            } catch (IOException e) {
                this.logger.error(ERR_MSG_FAIL_TO_CREATE_KEY_FILE,hashName,e.getMessage());
            }
        }
        assert keySetFile.exists();
        this.logger.info(MSG_CREATE_KEY_SUCCESS,hashName);
    }

    /**
     * Description: this is reading all the keys store locally to construct a
     * Hash map that stores all the salt string and hashed user name
     * 
     * @return a HashMap consist of all the Hashed user name and their random
     *         salt String
     * 
     * @author GAO RISHENG
     */
    public final HashMap<String, byte[]> retrieveKeys() {
        // Salt is the value, hash name is the key
        HashMap<String, byte[]> keyset = new HashMap<String, byte[]>();
        this.logger.info(MSG_START_RETRIEVING_KEYS);
        File temp = new File(NEW_EMPTY_STRING);
        File systemDir = new File(
                temp.getAbsolutePath() + SYSTEM_CONF_DIRECTORY);
        File userDir = new File(
                systemDir.getAbsolutePath() + SYSTEM_USER_DIRECTORY);
        // reading all the keys from all the .ks files
        Stack<File> allFiles = new Stack<File>();
        allFiles.push(userDir);
        while (!allFiles.isEmpty()) {
            File f = allFiles.pop();
            if (f.isDirectory()) {
                File[] subContents = f.listFiles();
                for (File t : subContents) {
                    allFiles.push(t);
                }
            } else {
                if (FilenameUtils.getExtension(f.getAbsolutePath())
                        .equals(KEY_SET_FILE_EXTENSION)) {
                    try {
                        byte[] entry = Files
                                .readAllBytes(Paths.get(f.getAbsolutePath()));
                        byte[] hashName = new byte[HASH_LENGTH];
                        byte[] salt = new byte[HASH_LENGTH];
                        for (int index = INDEX_ZERO; index < HASH_LENGTH; index++) {
                            hashName[index] = entry[index];
                            salt[index] = entry[HASH_LENGTH + index];
                        }
                        keyset.put(new String(hashName), salt);
                    } catch (IOException e) {
                        this.logger.error(ERR_MSG_FAIL_TO_READ_KEY_FROM_KEY_FILE,f.getName(),e.getMessage());
                    }
                }
            }
        }
        this.logger.info(MSG_READ_KEYS_SUCCESS);
        return keyset;
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
