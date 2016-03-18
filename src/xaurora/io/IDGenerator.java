package xaurora.io;

import java.math.BigInteger;
import java.security.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Description: this class is mainly in charge of generating MD5 hashed ID for
 * data file from their source URL and the current System time
 * 
 * @author GAO RISHENG A0101891L
 *
 */
public class IDGenerator {
    private static final String PADDING_ZERO = "0";
    private static final int ID_LENGTH = 32;
    private static final int STRING_LENGTH = 16;
    private static IDGenerator instance = null;
    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
    private static final String HASH_TYPE = "MD5";

    private IDGenerator() {

    }

    public static IDGenerator instanceOf() {
        if (instance == null) {
            instance = new IDGenerator();
        }
        return new IDGenerator();
    }

    /**
     * Description: generate the String format of the current time
     * 
     * @return the String format of the current time
     * @author GAO RISHENG A0101891L
     */
    private static String getNow() {
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
    public String GenerateID(String url, int type) {
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
            return id.toString();
        }

    }
}
