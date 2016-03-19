package xaurora.system;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Description: This class is in charge of converting the time format, recording
 * the time setting of the current user, and verifying whether a file is expired
 * 
 * @author GAO RISHENG A0101891L
 *
 */
public final class TimeManager {
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
    private static final int MILLISECONDS_PER_SECOND = 1000;
    private static final int SECONDS_PER_HOURS = 3600;
    private static final long DEFAULT_EXPIRE_INTERVAL = 259200000;// 3 days
    private long expireInterval;
    private static TimeManager classInstance;

    private TimeManager() {

        this.expireInterval = DEFAULT_EXPIRE_INTERVAL;
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
     * @param seconds,
     *            number of seconds in long type.
     * @author GAO RISHENG A0101891L
     */
    public final void setExpiredInterval(long seconds) {
        this.expireInterval = seconds * MILLISECONDS_PER_SECOND;
    }
    
    public final void setExpiredInterval(int hours){
        this.expireInterval = hours*SECONDS_PER_HOURS*MILLISECONDS_PER_SECOND;
    }
}
