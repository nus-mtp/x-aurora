package xaurora.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import xaurora.system.DBManager;
import xaurora.system.SystemManager;

/**
 * @author GAO RISHENG A0101891L
 * Description: This is the Junit Test case for testing the system manager.
 * Testing involves the login features and system stability for switching user.
 */
public final class SystemTest {
    private static final int INDEX_ZERO = 0;
    private static final String[] contentSet = { "\nb", "\na",
            "www.example.com\nsome content", "www.nus.edu.sg\nsldjf",
            "abcdefg.hij\ntestcase" };
    private static final String[] idSet = { "abc", "id", "1234567890987654",
            "a", "b" };
    private static final String[] urlSet = { "UNKNOWN", "UNKNOWN",
            "www.example.com", "www.nus.edu.sg", "abcdefg.hij" };
    private static SystemManager testInstance;
    private static DBManager testDBInstance;
    @Before
    public final void setup(){
        testInstance  = SystemManager.getInstance();
        testInstance.reset();
        testDBInstance = DBManager.getClassInstance();
        testDBInstance.reInit();
    }
    @Test
    public final void loginTest() {
        testInstance.changeUser("new user", "example@gmail.com",
                "E:\\study\\study2015sem1\\CS3283\\x-aurora\\local_data\\",
                10, 36);
        assertTrue(testInstance.isManagerInitialize());
        assertEquals("new user",testInstance.getUserName());
        assertEquals("example@gmail.com",testInstance.getEmail());
        assertEquals("E:\\study\\study2015sem1\\CS3283\\x-aurora\\local_data\\",testInstance.getDataFileIOInstance().getSyncDirectory());
        assertEquals(10,testInstance.getDisplayNumber());
        assertEquals(36,testInstance.getExpireInterval());
    }
    
    @Test
    public final void switchUserTest(){
        testInstance.changeUser("new user", "example@gmail.com",
                "E:\\study\\study2015sem1\\CS3283\\x-aurora\\local_data\\",
                10, 36);
        for (int i = INDEX_ZERO; i < 3; i++) {
            testInstance.getDataFileIOInstance().createDataFile(urlSet[i],
                    idSet[i], contentSet[i].getBytes(), testInstance,
                    testDBInstance);
        }
        assertEquals(3,testInstance.getIndexerInstance().getReader().maxDoc());
        assertEquals(3,testDBInstance.getEntries());
        testInstance.changeUser("new user2", "abcde@gmail.com",
                "E:\\study\\study2015sem1\\CS3283\\x-aurora\\local_data\\",
                7, 24);

        for (int i = 3; i < contentSet.length; i++) {
            testInstance.getDataFileIOInstance().createDataFile(urlSet[i],
                    idSet[i], contentSet[i].getBytes(), testInstance,
                    testDBInstance);
        }
        assertEquals(2,testInstance.getIndexerInstance().getReader().maxDoc());
        assertEquals(2,testDBInstance.getEntries());
    }

}
