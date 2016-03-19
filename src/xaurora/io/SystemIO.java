package xaurora.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Stack;

import org.apache.commons.io.FilenameUtils;

import xaurora.security.Security;

public class SystemIO {
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
    private static final int INDEX_ZERO= 0;
    
    private static SystemIO classInstance;

    private SystemIO() {
        this.init();
    }

    public static SystemIO getClassInstance() {
        if (classInstance == null) {
            classInstance = new SystemIO();
        }
        return classInstance;
    }

    private void init() {
        File temp = new File(NEW_EMPTY_STRING);
        File systemDir = new File(
                temp.getAbsolutePath() + SYSTEM_CONF_DIRECTORY);
        File userDir = new File(
                systemDir.getAbsolutePath() + SYSTEM_USER_DIRECTORY);
        if (!systemDir.mkdir()) {
            // log
        }
        if (!userDir.mkdir()) {
            // log
        }

    }

    public void registerNewUser(final String userName, final String email,
            byte[] entry, DataFileIO instance) {
        final String hashName = Security.hashUserName(userName, email);
        setUpUserIndexDirectory(hashName, instance);
        createKeyFile(hashName, entry);
    }
    public void setUpUserIndexDirectory(String hashName, DataFileIO instance) {
        
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
                instance.setIndexDirectory(indexDirectory.getAbsolutePath());
            } else {
                // log error
            }
        }
    }

    public boolean isLocalKeyCreated() {
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

    public void createLocalKey(byte[] entry) {
        String filename = Security.hashUserName(DEFAULT_USERNAME,
                NEW_EMPTY_STRING);
        createKeyFile(filename, entry);
    }

    public void createKeyFile(String hashName, byte[] entry) {
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
        assert !keySetFile.exists() : "error! Overwriting local key";
        if (!keySetFile.exists()) {

            try {
                keySetFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(
                        keySetFile.getAbsolutePath());
                fos.write(entry);
                fos.flush();
                fos.close();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

    public HashMap<String, byte[]> retrieveKeys() {
        //Salt is the value, hash name is the key
        HashMap<String, byte[]> keyset = new HashMap<String, byte[]>();
        File temp = new File(NEW_EMPTY_STRING);
        File systemDir = new File(
                temp.getAbsolutePath() + SYSTEM_CONF_DIRECTORY);
        File userDir = new File(
                systemDir.getAbsolutePath() + SYSTEM_USER_DIRECTORY);
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
                        byte[] entry = Files.readAllBytes(Paths.get(f.getAbsolutePath()));
                        byte[] hashName = new byte[HASH_LENGTH];
                        byte[] salt = new byte[HASH_LENGTH];
                        for(int index = INDEX_ZERO;index<HASH_LENGTH;index++){
                            hashName[index] = entry[index];
                            salt[index] = entry[HASH_LENGTH+index];
                        }
                        keyset.put(new String(hashName), salt);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
        return keyset;
    }

}
