package xaurora.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import org.apache.lucene.document.Document;
import org.junit.Test;

import xaurora.system.SystemManager;
import xaurora.text.PrefixMatcher;
import xaurora.text.TextIndexer;

/**
 * @author GAO RISHENG A0101891L,
 * Description: This is a Junit Testing program
 *         in charge of testing functionalities of TextIndexer and Prefix
 *         Matcher of the system. As there are 3 components of this test program
 *         1. Insertion test. This is to test the correctness of data insertion
 *         to lucene indexing system within text indexer 2. Deletion test. This
 *         is to test the correctness of existing data deletion from the lucene
 *         indexing system within text indexer. 3. Search test. This is to test
 *         the correctness of search query generation of prefix matcher and the
 *         correctness of the search result in TextIndexer
 */
public class IndexingTest {
    private static final String SECURITY_MSG_DISABLE_SERIALIZE = "Object cannot be serialized";
    private static final String CLASS_CANNOT_BE_DESERIALIZED = "Class cannot be deserialized";
    private static final String NEWLINE = "\n";
    private static final String EMPTY_STRING = "";
    private static final String DEFAULT_DATAFILE_EXTENSION = ".txt";
    private static final String[] contentSet = { "test case.", "content1.",
            "helloword.", "some contents.",
            "0Xklsjdo-35lksjfs werjlkju0 random string input in 1 sentence." };
    private static final String[] urlSet = { "www.google.com",
            "www.facebook.com", "comp.nus.edu.sg", "asdahgw23gdgtgr",
            "Random.generated.input" };
    private static final String[] filenameSet = {
            "7342F2c8ad800c05a7995109f042aaf5.txt",
            "d15df9e7386bc56b51f3fb5f85d41991.txt",
            "b38d7b263bc6539ca7b3f4fca9e78f24.txt",
            "8c4e604c8cff5c30f08c36133b6b2f79.txt",
            "393e7cb3c73d247eb89e7768b8474b02.txt" };
    private static final int INDEX_ZERO = 0;

    @Test
    public void test() {
        SystemManager testInstance = SystemManager.getInstance();
        testInstance.reset();
        insertTest(testInstance.getIndexerInstance());
        deletionTest(testInstance.getIndexerInstance());
        searchTest(testInstance.getIndexerInstance());
    }

    /**
     * Description: The mechanism of insertion test is 1. add the document data
     * to lucene 2. check the whether the data changes during the addition
     * 
     * @param instance
     *            the text indexer instance
     * @author Gao Risheng A0101891L
     */
    private static void insertTest(TextIndexer instance) {
        for (int i = INDEX_ZERO; i < contentSet.length; i++) {
            instance.createIndexDocumentFromWeb(
                    urlSet[i] + NEWLINE + contentSet[i], urlSet[i],
                    filenameSet[i], System.currentTimeMillis());
        }
        assertEquals(instance.getReader().maxDoc(), contentSet.length);
        for (int i = INDEX_ZERO; i < contentSet.length; i++) {
            try {
                Document doc = instance.getReader().document(i);
                assertEquals(doc.get(TextIndexer.FIELD_CONTENT), contentSet[i]);
                assertEquals(doc.get(TextIndexer.FIELD_FILENAME), filenameSet[i]
                        .replace(DEFAULT_DATAFILE_EXTENSION, EMPTY_STRING));
                assertEquals(doc.get(TextIndexer.FIELD_URL), urlSet[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * Description: The mechanism of deletion test is 1. delete data from
     * existing data base after insertion test 2. Check whether the the database
     * delete only 1 entry after each deletion 3. check the whether the database
     * is empty after deletion
     * 
     * @param instance
     *            the text indexer instance
     * @author Gao Risheng A0101891L
     */
    private static void deletionTest(TextIndexer instance) {
        int counter = contentSet.length;
        for (int i = INDEX_ZERO; i < contentSet.length; i++) {
            assertFalse(instance.getReader().maxDoc() == INDEX_ZERO);
            instance.deleteByField(TextIndexer.FIELD_SEARCH_FILENAME,
                    filenameSet[i]);
            assertEquals(counter - 1, instance.getReader().maxDoc());
            counter--;
        }
        assertTrue(instance.getReader().maxDoc() == INDEX_ZERO);
    }

    /**
     * Description: The mechanism of searching is 1. insert data; 2.search with
     * different queries 3.compare the expected output and the actual output
     * 
     * @param instance
     *            the text indexer instance
     * @author Gao Risheng A0101891L
     */
    private static void searchTest(TextIndexer instance) {
        for (int i = INDEX_ZERO; i < contentSet.length; i++) {
            instance.createIndexDocumentFromWeb(
                    urlSet[i] + NEWLINE + contentSet[i], urlSet[i],
                    filenameSet[i], System.currentTimeMillis());
        }
        // Test for input that have multiple valid entries
        ArrayList<String> testResult1 = PrefixMatcher.getResult("content",
                instance);
        assertEquals(2, testResult1.size());
        // expected output should be content of contentset[1] and contentset[3]
        // since they have the keyword 'content'
        assertEquals(contentSet[1], testResult1.get(0));
        assertEquals(contentSet[3], testResult1.get(1));
        // Test for input that have multiple terms
        // expected output should be content of contentset[4] since it is the
        // only
        // entry with both keyword 'random' and 'input'
        ArrayList<String> testResult2 = PrefixMatcher.getResult("random input",
                instance);
        assertEquals(1, testResult2.size());
        assertEquals(contentSet[4], testResult2.get(0));
        // Test for numerical input
        // expected output should be content of contentset[4] since it is the
        // only
        // entry with a single keyword '1'
        ArrayList<String> testResult3 = PrefixMatcher.getResult("1", instance);
        assertEquals(1, testResult3.size());
        assertEquals(contentSet[4], testResult3.get(0));
        // Test for invalid input
        // Expected output should be empty as none of the content of contentset
        // will have
        // the keyword 'invalidInput'
        ArrayList<String> testResult4 = PrefixMatcher.getResult("invalidINPUT",
                instance);
        assertTrue(testResult4.isEmpty());
        // Test for email query
        // Expected output should be empty because none of the entries created
        // from the content set
        // would have the email field filled as there is no email wihin the
        // content
        ArrayList<String> testResult5 = PrefixMatcher.getResult("email:",
                instance);
        assertTrue(testResult5.isEmpty());

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
