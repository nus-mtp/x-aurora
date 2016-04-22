package xaurora.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

import xaurora.system.DBManager;
import xaurora.system.SystemManager;

/**
 * @author GAO RISHENG A0101891L Description: This is a Junit Test program that
 *         test data file creation and deletion in the system
 *
 */
public final class DataFileTest {
    private static final String SECURITY_MSG_DISABLE_SERIALIZE = "Object cannot be serialized";
    private static final String CLASS_CANNOT_BE_DESERIALIZED = "Class cannot be deserialized";
    private static final int INDEX_ZERO = 0;
    private static final String[] contentSet = { "", "\na",
            "www.example.com\nsome content", "www.nus.edu.sg\nsldjf",
            "abcdefg.hij\ntestcase" };
    private static final String[] idSet = { "abc", "id", "1234567890987654",
            "a", "b" };
    private static final String[] urlSet = { "UNKNOWN", "UNKNOWN",
            "www.example.com", "www.nus.edu.sg", "abcdefg.hij" };

    @Test
    public final void test() {
        SystemManager testInstance = SystemManager.getInstance();
        testInstance.reset();
        DBManager tempDBInstance = DBManager.getClassInstance();

        // testInstance.getDataFileIOInstance().createDataFile(url, id, content,
        // manager, dbManager);
        createDataFileTest(testInstance, tempDBInstance);
        deleteDataFileTest(testInstance, tempDBInstance);

    }

    private final void createDataFileTest(SystemManager testInstance,
            DBManager tempDBInstance) {
        for (int i = INDEX_ZERO; i < contentSet.length; i++) {
            testInstance.getDataFileIOInstance().createDataFile(urlSet[i],
                    idSet[i], contentSet[i].getBytes(), testInstance,
                    tempDBInstance);
        }
        // The first input will not be created as there is no point storing
        // entries with
        // empty contents
        assertEquals(4, testInstance.getIndexerInstance().getReader().maxDoc());
    }

    private final void deleteDataFileTest(SystemManager testInstance,
            DBManager tempDBInstance) {
        for (int i = INDEX_ZERO; i < contentSet.length; i++) {
            testInstance.getDataFileIOInstance().removeDataFile(idSet[i]);
        }
        File dir = new File(
                testInstance.getDataFileIOInstance().getSyncDirectory());
        assertEquals(0, dir.listFiles().length);
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
