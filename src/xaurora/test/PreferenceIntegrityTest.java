package xaurora.test;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import xaurora.util.UserPreference;

public class PreferenceIntegrityTest {
	UserPreference preferences = UserPreference.getInstance();
	private static String EMPTY_FILE = "empty";
	private static String INITIAL_FILE = "initial";
	private static String INPUT_FILE_EXTENSION = ".in";
	private static final int numPreferences = 19;

	@Test
	public void testInit() throws IOException{
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
	}



}
