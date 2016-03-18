package xaurora.util;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import xaurora.system.TimeManager;

/**
 * This class is a helper class that mainly stores the needed meta data of a data file
 * @author GAO RISHENG A0101891L
 *
 */
public final class DataFileMetaData {
    
	private static final String FILE_LENGTH_UNIT = " KB";
	private static final String DEFAULT_FILE_SIZE_FORMAT = "#.##";
	private static final double BYTES_PER_KB = 1024.0;
	private String filename;
    private String url;
    private String source;
    private long length;
    private long lastModified;
    private String lastModifeidDateTime;
    private static final String SOURCE_UNKNOWN = "unknown";
    private static final String SECURITY_MSG_DISABLE_SERIALIZE = "Object cannot be serialized";
    private static final String CLASS_CANNOT_BE_DESERIALIZED = "Class cannot be deserialized";
    public DataFileMetaData(String filename, String url) {
        this.filename = filename;
        this.url = url;
        this.getHostFromURL();
    }

    private void getHostFromURL() {
        this.source = SOURCE_UNKNOWN;
        try {
            URL sourceURL = new URL(this.url);
            this.source = sourceURL.getHost();
        } catch (MalformedURLException e) {

            //e.printStackTrace(); log here
        }
    }

    /**
     * @param length, is the size of the file
     * @param lastModified, is the time that file is created
     * @author GAO RISHENG A0101891L
     */
    public void addFileMetaData(long length, long lastModified) {
        this.length = length;
        this.lastModified = lastModified;
    }

    /**
     * @return the File name of the data file
     * @author GAO RISHENG
     */
    public final String getFilename() {
        return this.filename;
    }
    
    /**
     * @return the URL (which is default the first line inside the file) of a data file
     * @author GAO RISHENG
     */
    public final String getUrl() {
        return this.url;
    }

    /**
     * @return the host name of the source of a data file
     * @author GAO RISHENG
     */
    public final String getSource() {
        return this.source;
    }
    
    /**
     * @return the file size in KBs of a data file
     * @author GAO RISHENG A0101891L
     */
    public final String getLength() {
        DecimalFormat df = new DecimalFormat(DEFAULT_FILE_SIZE_FORMAT);
        return df.format(this.length / BYTES_PER_KB) + FILE_LENGTH_UNIT;
    }

    /**
     * @return the last modified field in milliseconds of a data file
     * @author GAO RISHENG A0101891L
     */
    public final long getLastModified() {
        return this.lastModified;
    }
    
    /**
     * @return the String format of the last Modified Field of the data file
     * @author GAO RISHENG A0101891L
     */
    public final String getLastModifiedDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat();
        this.lastModifeidDateTime = sdf.format(lastModified);
        return this.lastModifeidDateTime;
    }

    /**
     * @return the Last Modified Field of the data file, which is the time it is created or down loaded
     * @author GAO RISHENG A0101891L
     */
    public final String getCreateTime() {
        return TimeManager.formatDateInMilliseconds(this.lastModified);
    }
    
    /**
	 * Secure Programming. Making this Object not-clonable. Object.clone() allows cloning the data of an object without initialize it
	 * which may leak the chances for attacker to access the data internally
	 * @Author GAO RISHENG A0101891L
	 */
	public final Object clone() throws java.lang.CloneNotSupportedException {
        throw new java.lang.CloneNotSupportedException();
	}
	
	/**
	 * Secure Programming. Disable the serialize option of the object which avoid attacker to print the object in serialize manner
	 * and inspect the internal status of the object
	 * @author GAO RISHENG A0101891L
	 */
	private final void writeObject(ObjectOutputStream out)
			throws java.io.IOException {
			        throw new java.io.IOException(SECURITY_MSG_DISABLE_SERIALIZE);
			}
	/**
	 * Secure Programming. Disable the de-serialize option of the object which avoid attacker to de-serialize the object stores in the file system
	 * and inspect the internal status of the object
	 * @author GAO RISHENG A0101891L
	 */
	private final void readObject(ObjectInputStream in)
			throws java.io.IOException {
			        throw new java.io.IOException(CLASS_CANNOT_BE_DESERIALIZED);
			}
}
