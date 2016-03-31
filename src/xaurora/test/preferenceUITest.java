package xaurora.test;

import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;
import javafx.scene.control.Button;
import org.loadui.testfx.GuiTest;
import xaurora.ui.PreferenceUI;
import xaurora.util.UserPreference;

/**
 *
 * @author Lee
 */
public class preferenceUITest extends GuiTest{
    
    PreferenceUI preferenceUI = new PreferenceUI();
    UserPreference preferences = UserPreference.getInstance();
    private static final String TEST_FILE = "test";
    private static String INPUT_FILE_EXTENSION = ".in";
    private File testFile;

    @Override
    protected Parent getRootNode() {
    	return preferenceUI.createPreferencePane();
    }
    
    @Test
    public void testSystemPane(){
    	click("#Setting");
    	click("#System");
    	
    	//test if clicking toggle the boolean value 
        CheckBox checkboxRunOnStartUp = find("#checkboxRunOnStartUp");
        CheckBox checkboxHideInToolbar = find("#checkboxHideInToolbar");
        Button applyButton = find("#Apply");
        boolean isCheckboxRunOnStartUpSelected = checkboxRunOnStartUp.isSelected();
        boolean isCheckboxHideInToolbarSelected = checkboxHideInToolbar.isSelected();
        click(checkboxRunOnStartUp);
        click(checkboxHideInToolbar);
        assertEquals(!isCheckboxRunOnStartUpSelected, checkboxRunOnStartUp.isSelected());
        assertEquals(!isCheckboxHideInToolbarSelected, checkboxHideInToolbar.isSelected());
        
        //test if value correctly write to preference file
        createTestFile();
        click(checkboxRunOnStartUp);
        click(checkboxHideInToolbar);
        click(applyButton);
        assertEquals(preferences.isRunOnStartUp(), false);
        assertEquals(preferences.isHideInToolbar(), false);
        click(checkboxRunOnStartUp);
        click(checkboxHideInToolbar);
        click(applyButton);
        assertEquals(preferences.isRunOnStartUp(), true);
        assertEquals(preferences.isHideInToolbar(), true);
        deleteTestFile();
        
    }
    
    /*
    @Test
    public void testHotkeysPane(){
    	click("#Setting");
    	click("#Hotkeys");
    }
    
    @Test
    public void testTextEditorPane(){
    	click("#Setting");
    	click("#TextEditor");
    }
    
    @Test
    public void testBlockedListPane(){
    	click("#Setting");
    	click("#BlockedList");
    }
    
    @Test
    public void testPathPane(){
    	click("#Setting");
    	click("#Path");
    }
    
    @Test
    public void testStoragePane(){
    	click("#Setting");
    	click("#Storage");
    }
    
    @Test
    public void testTutorialPane(){
    	click("#Tutorial");
    }
    
    @Test
    public void testDataManagingPane(){
    	click("#DataManaging");
    }
    */
    
    private File createTestFile(){
    	testFile = new File(TEST_FILE + INPUT_FILE_EXTENSION);
    	preferences.readPreferences(TEST_FILE);
    	preferences.writePreferences(TEST_FILE);
    	
    	return testFile;
    }
    
    private void deleteTestFile(){
    	testFile.delete();
    }
    
}
