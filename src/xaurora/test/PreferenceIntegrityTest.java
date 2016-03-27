package xaurora.test;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import xaurora.util.UserPreference;

public class PreferenceIntegrityTest {
	UserPreference preferences = UserPreference.getInstance();
	private static String EMPTY_FILE = "empty";
	private static String INITIAL_FILE = "initial";
	private static String RANDOM_FILE = "random";
	private static String INPUT_FILE_EXTENSION = ".in";
	private static final int numPreferences = 19;

	@Test
	public void testInitWithEmptyFile() throws IOException{
		//create an empty file
		File emptyFile = new File(EMPTY_FILE + INPUT_FILE_EXTENSION);
		emptyFile.createNewFile();
		//create preferences file with initial value
		File initialFile = new File(INITIAL_FILE + INPUT_FILE_EXTENSION);
		preferences.initPreferences();
		preferences.writePreferences(INITIAL_FILE);

		//read from empty file
		preferences.readPreferences(EMPTY_FILE);
		preferences.writePreferences(EMPTY_FILE);
		
		//check if initialization is done correctly
		boolean isEqual = FileUtils.contentEquals(emptyFile, initialFile);
		assertEquals(isEqual, true);	
		
		//delete after done testing
		emptyFile.delete();
		initialFile.delete();
	}
	
	@Test
	public void testInitWithRandomInputFile() throws IOException{
		String randomString;
		
		//create file with random input string
		File randomFile = new File(RANDOM_FILE + INPUT_FILE_EXTENSION);
		FileWriter fileWriter = new FileWriter(randomFile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        for (int i=0; i<numPreferences; i++){
        	randomString = UUID.randomUUID().toString().replaceAll("-", "");
        	bufferedWriter.write(randomString);
        	bufferedWriter.newLine();
        }
        bufferedWriter.close();
        
        //read from random file
        preferences.readPreferences(RANDOM_FILE);
		preferences.writePreferences(RANDOM_FILE);

        //create preferences file with initial value
        File initialFile = new File(INITIAL_FILE + INPUT_FILE_EXTENSION);
        preferences.initPreferences();
        preferences.writePreferences(INITIAL_FILE);
        
        //check if random input is corrected
        boolean isEqual = FileUtils.contentEquals(randomFile, initialFile);
		assertEquals(isEqual, true);	
		
		//delete after done testing
		randomFile.delete();
		initialFile.delete();
	}



}
