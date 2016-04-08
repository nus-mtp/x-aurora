package xaurora.system;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import xaurora.system.SystemManager;
import xaurora.util.DataFileMetaData;
import org.apache.log4j.Logger;
/**
 * @author GAO RISHENG A0101891L Description: This class is mainly for
 *         periodically monitoring the files in the database, checking and
 *         deleting expired files, detecting the new synchronized file in the
 *         local folder
 */
public final class DBManager implements Runnable {
    private static final int FIVE_MINUTE = 300000;
    private static final String MSG_EXPIRY_CHECK_COMPLETE = "Auto File Expiry Check Complete. Start Deleting expired files.";
    private static final String MSG_EXPIRY_CHECK_START = "Auto File Expiry Check for current user start.";
    private static final String MSG_UPDATE_COMPLETE = "Database update complete.";
    private static final String MSG_UPDATE_START = "New Data File is found. Start Updating the local Database.";
    private static final String MSG_INIT_COMPLETE = "Finish loading data files for current user. Initialization complete. Tracking start.";
    private static final String MSG_INIT_START = "Start Retriving all data files for the current user. Start Tracking files in the local database.";
    private static final String MSG_START = "An instance of DBManager is created. This message should appear only once at every software running flow.";
    private static final String SECURITY_MSG_DISABLE_SERIALIZE = "Object cannot be serialized";
    private static final String CLASS_CANNOT_BE_DESERIALIZED = "Class cannot be deserialized";
    private boolean isToUpdate;
    private SystemManager instance;
    private HashSet<String> fileset;
    private static DBManager db;
    private Logger logger;
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     * 
     * @author: GAO RISHENG A0101891L Description: periodically check for
     * database update and expired file deletion
     */
    public void run() {
        while (true) {
            ArrayList<DataFileMetaData> updateData = this.monitorFileSet();
            if (this.isToUpdate) {
                this.logger.info(MSG_UPDATE_START);
                this.instance.getDataFileIOInstance()
                        .updateIndexingFromFiles(this.instance, updateData);
                this.logger.info(MSG_UPDATE_COMPLETE);
                this.isToUpdate = false;
            }
            try {
                Thread.sleep(FIVE_MINUTE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (this.isToUpdate) {
                this.logger.info(MSG_EXPIRY_CHECK_START);
                ArrayList<DataFileMetaData> deleteData = this.instance
                        .getDataFileIOInstance()
                        .autoCheckForExpiredFile(this.instance);
                this.logger.info(MSG_EXPIRY_CHECK_COMPLETE);
                for (DataFileMetaData m : deleteData) {
                    this.fileset.remove(m.getFilename());
                }
                this.logger.info(MSG_UPDATE_COMPLETE);
            }
            try {
                Thread.sleep(FIVE_MINUTE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private DBManager() {
        this.logger = Logger.getLogger(this.getClass());
        this.logger.info(MSG_START);
        this.instance = SystemManager.getInstance();
        this.fileset = new HashSet<String>();
        this.logger.info(MSG_INIT_START);
        init();
        this.logger.info(MSG_INIT_COMPLETE);
    }

    /**
     * Description The singleton class instance getter
     * 
     * @return the class instance of DBManager
     * @author GAO RISHENG A0101891L
     */
    public static DBManager getClassInstance() {
        if (db == null) {
            db = new DBManager();
        }
        return db;
    }

    /**
     * Description: Initializing the DBManager instance by loading all the files
     * in the current store directory
     * 
     * @author GAO RISHENG A0101891L
     */
    private synchronized void init() {
        this.monitorFileSet();
    }


    /**
     * Description Adding monitor to data files which is generated locally
     * (extracted from web-browser),should only be call by
     * DataFileIO.createDataFile method
     * 
     * @param file,
     *            the file name of the data file
     * 
     * @author GAO RISHENG A0101891L
     */
    public final void addMonitorToAFile(String file) {
        this.fileset.add(file);
    }

    /**
     * Description: monitoring the change of local data directory, return the
     * list of file meta data for files newly added into the directory
     * 
     * @return the arrayList of file meta data that for all files need to be
     *         inserted into the indexing system
     * 
     * @author Gao Risheng A0101891L
     */
    private final ArrayList<DataFileMetaData> monitorFileSet() {
        ArrayList<DataFileMetaData> updateData = new ArrayList<DataFileMetaData>();
        ArrayList<DataFileMetaData> meta = this.instance.getDataFileIOInstance()
                .getAllMetaData(this.instance);
        for (DataFileMetaData m : meta) {
            if (!this.fileset.contains(m.getFilename())) {
                this.fileset.add(m.getFilename());
                updateData.add(m);
                this.isToUpdate = true;
            }

        }
        return updateData;
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
