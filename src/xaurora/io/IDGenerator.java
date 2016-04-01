package xaurora.io;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.log4j.Logger;

/**
 * Description: this class is mainly in charge of generating MD5 hashed ID for
 * data file from their source URL and the current System time
 * 
 * @author GAO RISHENG A0101891L
 *
 */
public final class IDGenerator {
    private static final String ERR_MSG_INVALID_HASH_ALGORITHM = "Error, unable to generate the hashed ID due to invalid Hash Algorithm. Error Message: %s.";
    private static final String MSG_START = "An instance of IDGenerator is created. This message should appear only once at every software running flow.";
    private static final String SECURITY_MSG_DISABLE_SERIALIZE = "Object cannot be serialized";
    private static final String CLASS_CANNOT_BE_DESERIALIZED = "Class cannot be deserialized";
    private static final String PADDING_ZERO = "0";
    private static final int ID_LENGTH = 32;
    private static final int STRING_LENGTH = 16;
    private static IDGenerator instance = null;
    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
    private static final String HASH_TYPE = "MD5";
    private Logger logger;

    private IDGenerator() {
        this.logger = Logger.getLogger(this.getClass());
        this.logger.info(MSG_START);
    }

    public static final IDGenerator instanceOf() {
        if (instance == null) {
            instance = new IDGenerator();
        }
        return instance;
    }

    /**
     * Description: generate the String format of the current time
     * 
     * @return the String format of the current time
     * @author GAO RISHENG A0101891L
     */
    private static final String getNow() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());
    }

    /**
     * Description: Generate the hashed ID for a data file from its extracted
     * URL and its type
     * 
     * @param url,
     *            the URL of the extracted data source
     * @param type
     * @return the MD5 hashed ID for the input
     * @author GAO RISHENG A0101891L
     */
    public final String GenerateID(final String url, final int type) {
        String output = url + getNow() + type;
        byte[] id = output.getBytes();
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_TYPE);
            md.reset();
            md.update(output.getBytes());
            byte[] newID = md.digest(id);
            BigInteger bigInt = new BigInteger(1, newID);
            String hashID = bigInt.toString(STRING_LENGTH);
            while (hashID.length() < ID_LENGTH) {
                hashID = PADDING_ZERO + hashID;
            }
            return hashID;
            // return id;
        } catch (NoSuchAlgorithmException e) {
            // SHOW ERROR LOG MESSAGE
            this.logger.error(String.format(ERR_MSG_INVALID_HASH_ALGORITHM, e.getMessage()));
            return id.toString();
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
