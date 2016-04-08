/*
 * This component acts as the main IO feature of the software. It receives the ID generated from the
 * Logic component and the data sent from the logic component, encrypt the data,
 *  and write it into a text file. Also, it read all the content in files within the sync directory, 
 *  decrypt the data and store that into and arraylist which can be sent to the logic component for 
 *  further usage.
 *  
 *   @author GAO RISHENG
 */

package xaurora.io;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import xaurora.security.*;
import xaurora.system.SystemManager;
import xaurora.system.DBManager;
import xaurora.system.SecurityManager;
import xaurora.text.TextIndexer;
import xaurora.util.DataFileMetaData;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import java.util.ArrayList;
import java.util.Stack;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public final class DataFileIO {
    private static final String ERR_MSG_ILLEGAL_BLOCK = "Unable to decrypt due to Illegal block";
    private static final String ERR_MSG_INVALID_PADDING = "Unable to decrypt due to invalid padding";
    private static final String ERR_MSG_INVALID_ALGORITHM = "Unable to decrypt due to invalid algorithm";
    private static final String ERR_MSG_INVALID_KEY = "Unable to decrypt due to invalid key";
    private static final String MSG_NEW_LUCENE_ENTRY_IS_CREATE = "A new lucene document is created, its source data file name is %s.";
    private static final String MSG_NEW_FILE_IS_FOUND = "%d new file(s) is/are found after the last update.";
    private static final String MSG_META_DATA_RETRIEVE = "A request of reading all meta data is raised. The number of files in the current system is %d, with %d valid files and %d invalid files.";
    private static final String MSG_NEW_DATA_FILE_PATH_CREATE = "A new data file path %s is created.";
    private static final String MSG_UPDATE_INDEX_DIRECTORY_SUCCESS = "Update local index directory successfully at location %s.";
    private static final String MSG_UPDATE_SYNC_DIRECTORY_SUCCESS = "Update local sync directory successfully at location %s.";
    private static final String MSG_START = "An instance of DataFileIO is created. This message should appear only once at every software running flow.";
    private static final String ERR_MSG_UNABLE_TO_DELETE_DATA_FILE = "Error occurs at attempting to delete a local data file %s, the error message is %s.";
    private static final String ERR_MSG_UNABLE_TO_READ_DATA_FILE = "Error occurs at reading data file %s, the error messag is %s.";
    private static final String ERR_MSG_UNABLE_TO_WRITE_DATA_FILE = "Error occurs at writing data file %s, the error messag is %s.";
    private static final String ERR_MSG_UNABLE_TO_SET_SYNC_FILE = "Error,unable to set the local sync directory with directory %s because one of the following reason: 1. Invalid Path, 2. Input Path is not a directory.";
    private static final String ERR_MSG_UNABLE_TO_SET_INDEX_FILE = "Error,unable to set the local index directory with directory %s because one of the following reason: 1. Invalid Path, 2. Input Path is not a directory.";
    private static final String SECURITY_MSG_DISABLE_SERIALIZE = "Object cannot be serialized";
    private static final String CLASS_CANNOT_BE_DESERIALIZED = "Class cannot be deserialized";
    private static final String PATH_SEPARATOR = "\\";
    private static final String NEWLINE = "\n";
    private static final String DEFAULT_SYNC_DIRECTORY = "/local_data/";
    private static final String DEFAULT_INDEX_DIRECTORY = "/index_data/";
    private static final String DEFAULT_FILE_EXTENSION = ".txt";
    private static final String TEXT_FILE_TYPE = "txt";
    private static final String ERR_MSG_MD5_COLLISION = "ERROR MESSAGE: MD5 COLLISION";
    private static final String ERR_INVALID_FILE_TYPE = "INVALID FILE TYPE";
    private static final int INDEX_ZERO = 0;
    private static final int UNIQUE = 1;
    private static final String NEW_EMPTY_STRING = "";
    private static final char FILE_EXTENSION_DELIMITER = '.';
    private String syncDirectory = DEFAULT_SYNC_DIRECTORY;
    private String indexDirectory = DEFAULT_INDEX_DIRECTORY;
    private static DataFileIO instance = null;
    private final Logger logger;

    // Singleton Class constructor
    // This is to limits that only 1 instance of DataFileIO will be created
    private DataFileIO() {
        this.logger = Logger.getLogger(this.getClass());
    }

    public static final DataFileIO instanceOf() {
        if (instance == null) {
            instance = new DataFileIO();
            instance.logger.info(MSG_START);
        }

        return instance;
    }

    /**
     * Description: this method is to update the sync directory
     * 
     * @param path
     *            must be a valid directory
     * @return true if the path is successfully updated, else return false
     * 
     * @author GAO RISHENG A0101891L
     * 
     */
    public final void setDirectory(final String path) {
        if (!new File(path).exists() || !new File(path).isDirectory()) {
            this.logger.error(
                    String.format(ERR_MSG_UNABLE_TO_SET_SYNC_FILE, path));
        } else {

            this.syncDirectory = path;
            this.logger.info(String.format(MSG_UPDATE_SYNC_DIRECTORY_SUCCESS,
                    this.syncDirectory));
        }
    }

    public final void setIndexDirectory(final String path) {
        if (!new File(path).exists() || !new File(path).isDirectory()) {
            this.logger.error(
                    String.format(ERR_MSG_UNABLE_TO_SET_INDEX_FILE, path));
        } else {
            this.indexDirectory = path;
            this.logger.info(String.format(MSG_UPDATE_INDEX_DIRECTORY_SUCCESS,
                    this.indexDirectory));
        }
    }

    /**
     * reset method to reset current setting
     * 
     * @author GAO RISHENG A0101891L
     */
    public final void reset() {
        this.syncDirectory = DEFAULT_SYNC_DIRECTORY;
        this.indexDirectory = DEFAULT_INDEX_DIRECTORY;
    }

    /**
     * 
     * @return return the current sync directory
     * @author GAO RISHENG A0101891L
     */
    public final String getSyncDirectory() {
        return this.syncDirectory;
    }

    /**
     * @return the current Indexing directory
     * @author GAO RISHENG A0101891L
     */
    public final String getIndexDirectory() {
        return this.indexDirectory;
    }

    /**
     * Description: base on a MD5 hased String ID generated from the source url,
     * generate the respective datafile path
     * 
     * @param id,
     *            A correctly MD5- hashed String ID which identifies the source
     *            URL and the extraction time
     * @return An absolute path of the data file to be created with this
     *         respective ID
     * 
     * @author GAO RISHENG A0101891L
     */
    private final String generateDataFilePath(final String id) {
        String dstpath = this.syncDirectory + PATH_SEPARATOR + new String(id)
                + DEFAULT_FILE_EXTENSION;
        this.logger.info(String.format(MSG_NEW_DATA_FILE_PATH_CREATE, dstpath));
        return dstpath;
    }

    /**
     * Description: Writing extracted contents from the web page into text
     * datafile with encryption DataFile format: the first line is always the
     * source URL and from the second line onwards, there will be the extracted
     * text content from the source URL
     * 
     * @param url,
     *            the url from the source
     * @param content,
     *            the byte array storing the extracted text data
     * @param dstFile,
     *            the destination data file path
     * @throws IOException
     *             in case I/O Error occurs in the writing process
     * 
     * @author GAO RISHENG A0101891L
     */
    private final void writeDataFileWithEncryption(final String url,
            final byte[] content, File dstFile, final SecurityManager secure)
                    throws IOException {
        dstFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(dstFile.getAbsolutePath());
        String overallContent = url + NEWLINE + new String(content);
        // Use the filename of the data file as the IV of the encryption
        fos.write(Security.encrypt(overallContent.getBytes(), dstFile.getName()
                .replace(DEFAULT_FILE_EXTENSION, NEW_EMPTY_STRING), secure));

        fos.close();
        dstFile.setReadOnly();
        dstFile.setReadable(true);

    }

    /**
     * The complete data file generation process
     * 
     * @param url
     *            the String URL of the source web site
     * @param id
     *            the MD5-hashed ID of the source
     * @param content
     *            the text content received from the web browser
     * 
     * @author GAO RISHENG A0101891L
     */
    public final void createDataFile(final String url, final String id,
            final byte[] content, SystemManager manager, DBManager dbManager) {
        String dstpath = generateDataFilePath(id);
        // Store the data in the lucene indexing system.
        long currentTime = System.currentTimeMillis();
        manager.getIndexerInstance().createIndexDocumentFromWeb(
                new String(content), url, dstpath, currentTime);
        File dstFile = new File(dstpath);
        if (dstFile.exists()) {
            this.logger.error(ERR_MSG_MD5_COLLISION);

        } else {
            try {
                writeDataFileWithEncryption(url, content, dstFile,
                        manager.getSecurityManagerInstance());
                // register this file into DBSystem
                dbManager.addMonitorToAFile(dstFile.getName()
                        .replace(DEFAULT_FILE_EXTENSION, NEW_EMPTY_STRING));
            } catch (IOException e) {
                this.logger
                        .error(String.format(ERR_MSG_UNABLE_TO_WRITE_DATA_FILE,
                                dstFile, e.getMessage()));
            }
        }
    }

    /**
     * Description: read all the data from a data file and store it into an
     * ArrayList
     * 
     * @param f,
     *            a valid text file that storing the encrypted data
     * @return A String that contains the decrypted data from the encrypted text
     *         file
     * 
     * @author GAO RISHENG A0101891L
     */
    private final String readFileContent(final File f, SystemManager system) {
        assert (f.isFile() && f.exists() && !f.isDirectory()
                && !FilenameUtils.getExtension(f.getAbsolutePath())
                        .equals(TEXT_FILE_TYPE)) : ERR_INVALID_FILE_TYPE;

        try {
            Path path = Paths.get(f.getAbsolutePath());
            byte[] data = Files.readAllBytes(path);

            byte[] decrypted = Security.decrypt(data,
                    f.getName().replace(DEFAULT_FILE_EXTENSION,
                            NEW_EMPTY_STRING),
                    system.getSecurityManagerInstance());

            return new String(decrypted);

        } catch (IOException e) {
            this.logger.error(String.format(ERR_MSG_UNABLE_TO_READ_DATA_FILE,
                    f.getAbsolutePath(), e.getMessage()));
            return NEW_EMPTY_STRING;
        } catch (InvalidKeyException e) {
            this.logger.error(ERR_MSG_INVALID_KEY, e);
            return NEW_EMPTY_STRING;
        } catch (InvalidAlgorithmParameterException e) {
            this.logger.error(ERR_MSG_INVALID_ALGORITHM, e);
            return NEW_EMPTY_STRING;
        } catch (NoSuchAlgorithmException e) {
            this.logger.error(ERR_MSG_INVALID_ALGORITHM, e);
            return NEW_EMPTY_STRING;
        } catch (NoSuchPaddingException e) {
            this.logger.error(ERR_MSG_INVALID_PADDING, e);
            return NEW_EMPTY_STRING;
        } catch (IllegalBlockSizeException e) {
            this.logger.error(ERR_MSG_ILLEGAL_BLOCK, e);
            return NEW_EMPTY_STRING;
        } catch (BadPaddingException e) {
            this.logger.error(ERR_MSG_INVALID_PADDING, e);
            return NEW_EMPTY_STRING;
        }

    }

    /**
     * Description: Check for expired file and delete its local data file and
     * indexing entity
     * 
     * @param manager,
     *            the System Manager instance that needs to be updated
     * @return the arrayList of data file meta data that to be deleted
     * 
     * @author GAO RISHENG A0101891L
     */
    public final synchronized ArrayList<DataFileMetaData> autoCheckForExpiredFile(
            SystemManager manager) {
        ArrayList<DataFileMetaData> deleteMetaData = new ArrayList<DataFileMetaData>();
        ArrayList<DataFileMetaData> allMetaData = this.getAllMetaData(manager);
        for (int index = INDEX_ZERO; index < allMetaData.size(); index++) {
            if (manager.getTimeManagerInstance()
                    .isExpired(allMetaData.get(index).getLastModified())) {

                manager.getIndexerInstance().deleteByField(
                        TextIndexer.FIELD_SEARCH_FILENAME,
                        allMetaData.get(index).getFilename());
                deleteMetaData.add(allMetaData.get(index));
            }
        }
        return deleteMetaData;
    }

    /**
     * Description: Removing a local data file from from given data file
     * filename
     * 
     * @param A
     *            valid String filename of a local data file
     * 
     * @author GAO RISHENG A0101891L
     * 
     */
    public final void removeDataFile(final String filename) {
        // This should never be triggered. => use Assert
        assert filename != null && !filename.trim().equals(NEW_EMPTY_STRING);

        Path filePath = Paths.get(this.syncDirectory + PATH_SEPARATOR + filename
                + DEFAULT_FILE_EXTENSION);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            this.logger.error(String.format(ERR_MSG_UNABLE_TO_DELETE_DATA_FILE,
                    filePath.toString(), e.getMessage()));
        }

    }

    /**
     * Description: read the source from the content of a data file
     * 
     * @param f,a
     *            valid file
     * @return the source URL of the text content within the file
     * 
     * @author GAO RISHENG A0101891L.
     */
    private final String getUrlFromFile(final File f, SystemManager manager) {
        // Assertion: thie method must read a txt file with valid file path
        assert (f.isFile() && f.exists() && !f.isDirectory()
                && !FilenameUtils.getExtension(f.getAbsolutePath())
                        .equals(TEXT_FILE_TYPE)) : ERR_INVALID_FILE_TYPE;
        String textContent = readFileContent(f, manager);
        if (textContent.equals(NEW_EMPTY_STRING)) {
            return textContent;
        }
        String[] paragraphs = textContent.split(NEWLINE);

        return paragraphs.length > INDEX_ZERO ? paragraphs[INDEX_ZERO]
                : NEW_EMPTY_STRING;

    }

    /**
     * 
     * Description: read all the useful meta data from all the data files in the
     * system in the current synchronization directory
     * 
     * @return an arraylist of dataFileMetaData which stores all following meta
     *         data: Filename URL Host name of the source Length of file
     * @author GAO RISHENG A0101891L
     */
    public final ArrayList<DataFileMetaData> getAllMetaData(
            SystemManager manager) {
        ArrayList<DataFileMetaData> result = new ArrayList<DataFileMetaData>();
        File dir = new File(this.syncDirectory);
        Stack<File> s = new Stack<File>();
        s.push(dir);
        int overall_counter = INDEX_ZERO;
        int valid_counter = INDEX_ZERO;
        int invalid_counter = INDEX_ZERO;
        // iterate through the directory tree
        while (!s.isEmpty()) {
            File f = s.pop();
            if (f.exists()) {
                if (f.isDirectory()) {
                    File[] subDir = f.listFiles();
                    for (int i = INDEX_ZERO; i < subDir.length; i++) {
                        s.push(subDir[i]);
                    }

                } else {
                    if (FilenameUtils.getExtension(f.getAbsolutePath())
                            .equals(TEXT_FILE_TYPE)) {
                        String filename = f.getName().substring(INDEX_ZERO,
                                f.getName()
                                        .lastIndexOf(FILE_EXTENSION_DELIMITER));
                        String url = getUrlFromFile(f, manager);
                        this.logger.debug(url);
                        // To handle the case when more than 1 user share the
                        // same store directory
                        // that one user may accidentally add the cipher text of
                        // other user
                        // (since he/she cannot decrypt this) to the monitor set
                        if (!url.equals(NEW_EMPTY_STRING)) {
                            DataFileMetaData tempEntity = new DataFileMetaData(
                                    filename, url);
                            tempEntity.addFileMetaData(f.length(),
                                    f.lastModified());
                            result.add(tempEntity);
                            valid_counter++;
                        } else {
                            invalid_counter++;
                        }
                        overall_counter++;
                    }
                }
            }
        }
        // this log is to keep track of the number of data files in the current
        // system.
        this.logger.info(String.format(MSG_META_DATA_RETRIEVE, overall_counter,
                valid_counter, invalid_counter));
        return result;
    }

    /**
     * Description: Update the indexing system when new data enters the sync
     * directory
     * 
     * @param manager,
     *            the System Manager that needs to update
     * @param updateData,
     *            the ArrayList of file meta data that specifies the files to be
     *            added into the indexing system
     * 
     * @author GAO RISHENG A0101891L
     */
    public final synchronized void updateIndexingFromFiles(
            SystemManager manager,
            final ArrayList<DataFileMetaData> updateData) {
        this.logger
                .info(String.format(MSG_NEW_FILE_IS_FOUND, updateData.size()));
        ArrayList<String> content = new ArrayList<String>();
        File f = new File(this.syncDirectory);
        for (int index = INDEX_ZERO; index < updateData.size(); index++) {
            String filename = updateData.get(index).getFilename();
            File[] matchingFiles = f.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.startsWith(filename)
                            && name.endsWith(TEXT_FILE_TYPE);
                }
            });
            assert matchingFiles.length == UNIQUE;
            for (File temp : matchingFiles) {
                content.add(this.readFileContent(temp, manager));
            }
        }

        for (int index = INDEX_ZERO; index < updateData.size(); index++) {
            this.logger.info(String.format(MSG_NEW_LUCENE_ENTRY_IS_CREATE,
                    updateData.get(index).getFilename()));
            manager.getIndexerInstance().createIndexDocumentFromWeb(
                    content.get(index), updateData.get(index).getUrl(),
                    updateData.get(index).getFilename(),
                    updateData.get(index).getLastModified());

        }
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
