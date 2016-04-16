package xaurora.system;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.log4j.Logger;

import xaurora.ui.Message;

/**
 * Description: This class is in charge of converting the time format, recording
 * the time setting of the current user, and verifying whether a file is expired
 * 
 * @author GAO RISHENG A0101891L
 *
 */
public final class TimeManager {
    private static final String ERR_MSG_UNABLE_TO_CHANGE_SYSTEM_TIME = "Unable to change system time.";
    private static final String ERR_MSG_UNABLE_TO_READ_NTP_REPLY = "Error, unable to read NTP server reply. ";
    private static final int NTP_PORT = 37;
    private static final String CMD_CHANGE_TIME = "cmd  /c  time ";
    private static final String CMD_CHANGE_DATE = "cmd /c date ";
    private static final String TIME_DELIMITER = ":";
    private static final String DATE_DELIMITER = "-";
    private static final String ERR_MSG_UNABLE_TO_CONNECT_TIME_SERVER = "Unable to connect to time server. System will use current time.";
    private static final int THIRD_BYTE_OFFSET = 8;
    private static final int SECOND_BYTE_OFFSET = 16;
    private static final int FIRST_BYTE_OFFSET = 24;
    private static final String ERR_MSG_INVALID_UPDATE_EXPIRY_HOUR = "Error, system is trying to update a non-positive expiry time in hours";
    private static final String ERR_MSG_INVALID_UPDATE_EXPIRY_SECOND = "Error, system is trying to update a non-positive expiry time in seconds";
    private static final int MINIMUM_NON_NEGATIVE = 0;
    private static final String MSG_START = "An instance of Time Manager is created. This message should appear only once at every software running flow.";
    private static final String SECURITY_MSG_DISABLE_SERIALIZE = "Object cannot be serialized";
    private static final String CLASS_CANNOT_BE_DESERIALIZED = "Class cannot be deserialized";
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
    private static final int MILLISECONDS_PER_SECOND = 1000;
    private static final int SECONDS_PER_HOURS = 3600;
    private static final long DEFAULT_EXPIRE_INTERVAL = 259200000;// 3 days
    // private static final long EPOCH_OFFSET_MILLIS;
    // time server list
    private static final String[] hostName = { "time-c.nist.gov",
            "time-nw.nist.gov", "time.nist.gov", "0.sg.pool.ntp.org",
            "1.sg.pool.ntp.org", "2.sg.pool.ntp.org", "3.sg.pool.ntp.org" };

    // Calculate the offset time offset
    /*
     * static {
     * 
     * Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
     * calendar.set(1900, Calendar.JANUARY, 1, 0, 0, 0); EPOCH_OFFSET_MILLIS =
     * Math.abs(calendar.getTime().getTime());
     * 
     * }
     */

    private long expireInterval;
    private static TimeManager classInstance;
    private Logger logger;
    private Message message = new Message();

    private TimeManager() {
        this.logger = Logger.getLogger(this.getClass());
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

    public final void setExpiredIntervalSecond(long seconds) {
        if (seconds <= MINIMUM_NON_NEGATIVE) {
            this.logger.error(ERR_MSG_INVALID_UPDATE_EXPIRY_SECOND + seconds);
            message.showError(ERR_MSG_INVALID_UPDATE_EXPIRY_SECOND);
        } else
            this.expireInterval = seconds * MILLISECONDS_PER_SECOND;
    }

    public final void setExpiredIntervalHour(int hours) {
        if (hours < MINIMUM_NON_NEGATIVE) {
            this.logger.error(ERR_MSG_INVALID_UPDATE_EXPIRY_HOUR + hours);
            message.showError(ERR_MSG_INVALID_UPDATE_EXPIRY_HOUR);
        }
    }

    /**
     * Description: calibrating the system time with the remote time server time
     * 
     * @author GAO RISHENG A0101891L
     * @deprecated Reason: From windows 7 onwards time will be synchronized
     *             automatically
     */
    public void calibrateTime() {
        for (String s : hostName) {
            retrieveTime(s);
        }
    }

    /**
     * Description: retrieve the current time from a Time server
     * 
     * @param hostname,
     *            the host name of a time server
     * @author Gao Risheng A0101891L
     * @deprecated Reason: From windows 7 onwards time will be synchronized
     *             automatically
     */
    private void retrieveTime(String hostname) {
        long currentMillisecond = MINIMUM_NON_NEGATIVE;
        try {

            Socket socket = new Socket(hostname, NTP_PORT);

            BufferedInputStream bis = new BufferedInputStream(
                    socket.getInputStream(),

                    socket.getReceiveBufferSize());
            // read a 32 bit (4 byte) long value representing the time in
            // milliseconds
            int byte1 = bis.read();
            int byte2 = bis.read();
            int byte3 = bis.read();
            int byte4 = bis.read();

            if ((byte1 | byte2 | byte3 | byte4) > MINIMUM_NON_NEGATIVE) {
                // construct back the long value
                currentMillisecond = (((long) byte1) << FIRST_BYTE_OFFSET)
                        + (byte2 << SECOND_BYTE_OFFSET)
                        + (byte3 << THIRD_BYTE_OFFSET) + byte4;

                socket.close();
                // changeTime(currentMillisecond*MILLISECONDS_PER_SECOND-EPOCH_OFFSET_MILLIS);
            }

        } catch (UnknownHostException ex) {

            this.logger.error(
                    ERR_MSG_UNABLE_TO_CONNECT_TIME_SERVER + ex.getMessage());

        } catch (IOException ex) {

            this.logger
                    .error(ERR_MSG_UNABLE_TO_READ_NTP_REPLY + ex.getMessage());

        }

    }

    /**
     * Description: Changing the current system time with the network time
     * 
     * @param currentMillisecond,
     *            the current time in milliseconds
     * @author GAO RISHENG A0101891L
     * @deprecated Reason: From windows 7 onwards time will be synchronized
     *             automatically
     */
    private void changeTime(long currentMillisecond) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(currentMillisecond);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        c.setTimeInMillis(System.currentTimeMillis());
        int system_year = c.get(Calendar.YEAR);
        int system_month = c.get(Calendar.MONTH) + 1;
        int system_day = c.get(Calendar.DAY_OF_MONTH);
        int system_hour = c.get(Calendar.HOUR_OF_DAY);
        int system_minute = c.get(Calendar.MINUTE);
        String date = year + DATE_DELIMITER + month + DATE_DELIMITER + day;
        String time = hour + TIME_DELIMITER + minute + TIME_DELIMITER + second;
        try {
            // date calibration
            if (year != system_year || month != system_month
                    || day != system_day) {

                String cmd = CMD_CHANGE_DATE + date;
                Process process = Runtime.getRuntime().exec(cmd);
                // process.waitFor();
            }

            if (hour != system_hour || minute != system_minute) {
                String cmd = CMD_CHANGE_TIME + time;
                Process process = Runtime.getRuntime().exec(cmd);
                // process.waitFor();
            }

        } catch (IOException ex) {

            this.logger.error(
                    ERR_MSG_UNABLE_TO_CHANGE_SYSTEM_TIME + ex.getMessage());

        } /*
           * catch (InterruptedException ex) {
           * 
           * this.logger.error(ERR_MSG_UNABLE_TO_CHANGE_TIME_INTERRUPTED+ex.
           * getMessage());
           * 
           * }
           */

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
