package xaurora.system;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: This class is in charge of converting the time format, recording
 * the time setting of the current user, and verifying whether a file is expired
 * 
 * @author GAO RISHENG A0101891L
 *
 */
public final class TimeManager {
    private static final String ERR_MSG_INVALID_UPDATE_EXPIRY_HOUR = "Error, system is trying to update a non-positive expiry time in hours {}";
    private static final String ERR_MSG_INVALID_UPDATE_EXPIRY_SECOND = "Error, system is trying to update a non-positive expiry time in seonds {}";
    private static final int MINIMUM_NON_NEGATIVE = 0;
    private static final String MSG_START = "An instance of Time Manager is created. This message should appear only once at every software running flow.";
    private static final String SECURITY_MSG_DISABLE_SERIALIZE = "Object cannot be serialized";
    private static final String CLASS_CANNOT_BE_DESERIALIZED = "Class cannot be deserialized";
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
    private static final int MILLISECONDS_PER_SECOND = 1000;
    private static final int SECONDS_PER_HOURS = 3600;
    private static final long DEFAULT_EXPIRE_INTERVAL = 259200000;// 3 days
    private long expireInterval;
    private static TimeManager classInstance;
    private Logger logger;
    private TimeManager() {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.expireInterval = DEFAULT_EXPIRE_INTERVAL;
        this.logger.info(MSG_START);
    }

    public static TimeManager getInstance() {
        if (classInstance == null) {
            classInstance = new TimeManager();
        }
        return classInstance;
    }

    /**
     * Description: Generate a human-readable time format from the last Modified
     * Field of a data file, which is in milliseconds.
     * 
     * @param input,
     *            the last modified field of a text file stating its creation
     *            time in milliseconds
     * @return A String with format yyyy-MM-dd HH:mm
     */
    public final static String formatDateInMilliseconds(long input) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT,
                Locale.getDefault());
        GregorianCalendar calendar = new GregorianCalendar(
                TimeZone.getDefault());
        calendar.setTimeInMillis(input);
        return formatter.format(calendar.getTime());
    }

    /**
     * Description checking whether a data file is expire in the system
     * 
     * @param input,
     *            the Last Modified field of a data file in milliseconds
     * @return true if the difference between the input and the current time is
     *         greater than the expire interval, false otherwise.
     * @author GAO RISHENG A0101891L
     */
    public final boolean isExpired(long input) {
        long currentTime = System.currentTimeMillis();
        return currentTime - input > this.expireInterval;
    }

    /**
     * Description: Getting the ExpiredInterval for the current user
     * 
     * @return the expire interval of a data file in millisecond of the current
     *         user
     * @author GAO RISHENG A0101891L
     */
    public final long getExpiredInterval() {
        return this.expireInterval;
    }

    /**
     * Description: setting the intervals for the system to determine whether a
     * files is expired
     * 
     * @param seconds/hours,
     *            number of seconds in long type.
     * @author GAO RISHENG A0101891L
     */
    public final void setExpiredInterval(long seconds) {
        if(seconds<=MINIMUM_NON_NEGATIVE){
            this.logger.error(ERR_MSG_INVALID_UPDATE_EXPIRY_SECOND,seconds);
        } else 
            this.expireInterval = seconds * MILLISECONDS_PER_SECOND;
    }
    
    public final void setExpiredInterval(int hours){
        if(hours<=MINIMUM_NON_NEGATIVE){
            this.logger.error(ERR_MSG_INVALID_UPDATE_EXPIRY_HOUR,hours);
        }
        else
            this.expireInterval = hours*SECONDS_PER_HOURS*MILLISECONDS_PER_SECOND;
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
