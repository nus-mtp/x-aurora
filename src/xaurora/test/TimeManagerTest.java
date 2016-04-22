package xaurora.test;

import static org.junit.Assert.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

import xaurora.system.SystemManager;

/**
 * @author GAO RISHENG A0101891L
 * Description: This class is a Junit Test case that verifies the correctness of the
 * file expiry checking and the expiry value setting of the current system
 */
public final class TimeManagerTest {
    private static final String SECURITY_MSG_DISABLE_SERIALIZE = "Object cannot be serialized";
    private static final String CLASS_CANNOT_BE_DESERIALIZED = "Class cannot be deserialized";
    private static SystemManager testInstance;

    @Before
    public final static void setup() {
        testInstance = SystemManager.getInstance();
        testInstance.reset();
    }

    /**
     * @deprecated Test since from Windows 7 onwards the time will be
     *             synchronized automatically
     */
    @Test

    public final void calibrateTimeTest() {
        Calendar c = Calendar.getInstance();
        long currentSystemTime = System.currentTimeMillis();
        c.setTimeInMillis(currentSystemTime);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        long modifiedSystemTime = currentSystemTime + 3 * 86400000;
        c.setTimeInMillis(modifiedSystemTime);
        int modifiedYear = c.get(Calendar.YEAR);
        int modifiedMonth = c.get(Calendar.MONTH) + 1;
        int modifiedDay = c.get(Calendar.DAY_OF_MONTH);
        assertEquals(year, modifiedYear);
        assertEquals(month, modifiedMonth);
        assertEquals(day + 3, modifiedDay);
        // testInstance.getTimeManagerInstance().calibrateTime();
    }

    @Test
    public final void setExpireTimeHourTest() {
        // Simulation of file creation at current time
        long fileLastModified = System.currentTimeMillis();
        // change expiry setting
        testInstance.getTimeManagerInstance().setExpiredIntervalHour(0);
        try {
            Thread.sleep(1000);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        assertTrue(testInstance.getTimeManagerInstance()
                .isExpired(fileLastModified));
        // change the expiry setting again
        testInstance.getTimeManagerInstance().setExpiredIntervalHour(24);
        assertFalse(testInstance.getTimeManagerInstance()
                .isExpired(fileLastModified));
        // Invalid modification of expiry time
        testInstance.getTimeManagerInstance().setExpiredIntervalHour(-24);

        // previous setting will remain
        assertFalse(testInstance.getTimeManagerInstance()
                .isExpired(fileLastModified));
    }
    
    @Test
    public final void setExpireTimeSecondTest() {
        // Simulation of file creation at current time
        long fileLastModified = System.currentTimeMillis();
        // change expiry setting
        testInstance.getTimeManagerInstance().setExpiredIntervalSecond(1);
        try {
            Thread.sleep(2000);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        assertTrue(testInstance.getTimeManagerInstance()
                .isExpired(fileLastModified));
        // change the expiry setting again
        testInstance.getTimeManagerInstance().setExpiredIntervalSecond(86400);
        assertFalse(testInstance.getTimeManagerInstance()
                .isExpired(fileLastModified));
        // Invalid modification of expiry time
        testInstance.getTimeManagerInstance().setExpiredIntervalSecond(-3600);

        // previous setting will remain
        assertFalse(testInstance.getTimeManagerInstance()
                .isExpired(fileLastModified));
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
