package xaurora.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Stack;

import org.junit.Test;

import xaurora.security.Security;

/**
 * Description: This is a Junit test programming for testing the ability of the
 * software encrypting and decrypting the text files This test supports multiple
 * input test cases which corresponds to text files that with different sizes
 * Testing procedure: 1. Auto-Generate Random Test cases ----> Run
 * SecurityTestInputGenerator.java Input the number of test cases to be
 * generated and the number of lines that 1 test case can contains ----> Run
 * SecurityInputProcessor.java to generate the expected encrypted input ---->
 * Run this program to verify the decrypted output for all the test cases P.S
 * Stress Test result: 10 * 10000 lines with 10000 characters per line, overall
 * 50 M per input files will fail this program because this program is not able
 * to stores 20 * 50 M = 1GB data in the memory 2. Manual Generate Test cases
 * ----> Put all the text files under
 * /unit_testing/Security_Test/Input/Random_Input directory ----> Run
 * SecurityInputProcessor.java to generate the expected encrypted input ---->
 * Run this program to verify the decrypted output for all the test cases
 * 
 * Performance Analysis Normal Data file size 6-24 KB 10 Test Cases size 450-550
 * KB Process Time (for Input Processor) 0.422 seconds Test Time 0.429 seconds
 * 
 * @author GAO RISHENG A0101891L
 *
 */
public final class SecurityTest {
    private static final String SECURITY_MSG_DISABLE_SERIALIZE = "Object cannot be serialized";
    private static final String CLASS_CANNOT_BE_DESERIALIZED = "Class cannot be deserialized";
    private static final int INDEX_ZERO = 0;

    @Test
    public void test() {
        produceActualOutput();
        ArrayList<SecurityTestTextUnit> expectedOutputs = getDirectoryContent(
                SecurityTestInputGenerator.EXPECTED_OUTPUT_DIRECTORY);
        ArrayList<SecurityTestTextUnit> actualOutputs = getDirectoryContent(
                SecurityTestInputGenerator.ACTUAL_OUTPUT_DIRECTORY);
        assertEquals(expectedOutputs.size(), actualOutputs.size());
        for (int index = INDEX_ZERO; index < expectedOutputs.size(); index++) {
            assertEquals(expectedOutputs.get(index).getText().length,
                    actualOutputs.get(index).getText().length);
            assertEquals(actualOutputs.get(index).getName(),
                    actualOutputs.get(index).getName());

            byte[] expectedOutput = Security.decrypt(
                    expectedOutputs.get(index).getText(),
                    expectedOutputs.get(index).getName().replace(
                            SecurityTestInputGenerator.DEFAULT_FILE_EXTENSION,
                            SecurityTestInputGenerator.NEW_EMPTY_STRING));
            byte[] actualOutput = Security.decrypt(
                    actualOutputs.get(index).getText(),
                    actualOutputs.get(index).getName().replace(
                            SecurityTestInputGenerator.DEFAULT_FILE_EXTENSION,
                            SecurityTestInputGenerator.NEW_EMPTY_STRING));
            assertEquals(expectedOutput.length, actualOutput.length);
            for (int offset = INDEX_ZERO; offset < expectedOutput.length; offset++) {
                assertEquals(actualOutput[offset], expectedOutput[offset]);
            }
        }
    }

    /**
     * Description: Generate the encrypted text file (actual output for
     * encryption)
     * 
     * @author GAO RISHENG A0101891L
     */
    private static void produceActualOutput() {

        File temp = new File(SecurityTestInputGenerator.NEW_EMPTY_STRING);
        File storeDir = new File(temp.getAbsolutePath()
                + SecurityTestInputGenerator.RANDOM_INPUT_DIRECTORY);
        Stack<File> allFiles = new Stack<File>();
        int count = INDEX_ZERO;
        allFiles.push(storeDir);
        while (!allFiles.isEmpty()) {
            File f = allFiles.pop();
            if (f.isDirectory()) {
                File[] files = f.listFiles();
                for (File t : files) {
                    allFiles.push(t);
                }
            } else {
                count++;
                try {
                    Path path = Paths.get(f.getAbsolutePath());
                    File outputFile = new File(temp.getAbsolutePath()
                            + SecurityTestInputGenerator.ACTUAL_OUTPUT_DIRECTORY
                            + SecurityTestInputGenerator.PATH_SEPARATOR
                            + SecurityTestInputGenerator.RANDOM_OUTPUT_FILENAME
                            + count
                            + SecurityTestInputGenerator.DEFAULT_FILE_EXTENSION);
                    if (!outputFile.exists()) {
                        outputFile.createNewFile();
                    }
                    byte[] content = Files.readAllBytes(path);
                    FileOutputStream fos = new FileOutputStream(
                            outputFile.getAbsoluteFile());
                    fos.write(Security.encrypt(content, f.getName().replace(
                            SecurityTestInputGenerator.DEFAULT_FILE_EXTENSION,
                            SecurityTestInputGenerator.NEW_EMPTY_STRING)));
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * Description: retrieve all the text content as well as the file names in 1
     * directory and store them in an ArrayList
     * 
     * @param path,
     *            the directories that stores all the files
     * @return an ArrayList that stores the text content of each text files
     *         within the directory and their file names
     * 
     * @author GAO RISHENG A0101891L
     */
    private static ArrayList<SecurityTestTextUnit> getDirectoryContent(
            String path) {
        ArrayList<SecurityTestTextUnit> contents = new ArrayList<SecurityTestTextUnit>();
        File temp = new File(SecurityTestInputGenerator.NEW_EMPTY_STRING);
        File storeDir = new File(temp.getAbsolutePath() + path);
        Stack<File> allFiles = new Stack<File>();
        allFiles.push(storeDir);
        while (!allFiles.isEmpty()) {
            File f = allFiles.pop();
            if (f.isDirectory()) {
                File[] files = f.listFiles();
                for (File t : files) {
                    allFiles.push(t);
                }
            } else {
                String filename = f.getName().replace(
                        SecurityTestInputGenerator.DEFAULT_FILE_EXTENSION,
                        SecurityTestInputGenerator.NEW_EMPTY_STRING);
                try {
                    byte[] content = Files
                            .readAllBytes(Paths.get(f.getAbsolutePath()));
                    SecurityTestTextUnit tempUnit = new SecurityTestTextUnit(
                            content, filename);
                    contents.add(tempUnit);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return contents;
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
