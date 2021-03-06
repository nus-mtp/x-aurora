package xaurora.test;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;
import java.util.ArrayList;
import org.loadui.testfx.GuiTest;
import xaurora.ui.PreferenceUI;
import xaurora.util.UserPreference;
import xaurora.util.BlockedPage;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;

/**
 *
 * @author Lee
 */
public class PreferenceUITest extends GuiTest{
    
    private static final String TEST_FILE = "test";
    private static final String INPUT_FILE_EXTENSION = ".in";
    private File testFile;
    PreferenceUI preferenceUI = new PreferenceUI();
    UserPreference preferences = UserPreference.getInstance();
    
    @Override
    protected Parent getRootNode() {
    	preferenceUI.setUsername("test");
    	return preferenceUI.createPreferencePane();
    }
    
    @Test
    public void testSystemPane(){
    	click("#Setting");
    	click("#System");
    	CheckBox checkboxRunOnStartUp = find("#checkboxRunOnStartUp");
        CheckBox checkboxHideInToolbar = find("#checkboxHideInToolbar");
        Button applyButton = find("#applyButton");
        
    	//test if clicking toggle the boolean value 
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
        assertEquals(false, preferences.isRunOnStartUp());
        assertEquals(false, preferences.isHideInToolbar());
        click(checkboxRunOnStartUp);
        click(checkboxHideInToolbar);
        click(applyButton);
        assertEquals(true, preferences.isRunOnStartUp());
        assertEquals(true, preferences.isHideInToolbar());
        deleteTestFile();       
    }
    
    @Test
    public void testHotkeysPane(){
    	click("#Setting");
    	click("#Hotkeys");
    }
    
    @Test
    public void testTextEditorPane(){
    	click("#Setting");
    	click("#TextEditor");
    	Spinner<Integer> spinner = find("#spinner");
    	CheckBox cbShowTextSource = find("#cbShowTextSource");
    	Slider slider = find("#transparency");
    	Button applyButton = find("#applyButton");
    	createTestFile();
    	
    	//test spinner input range between 1 to 20
    	click(spinner);
    	push(KeyCode.BACK_SPACE);
    	push(KeyCode.BACK_SPACE);
    	push(KeyCode.DIGIT1);
    	push(KeyCode.DIGIT0);
    	push(KeyCode.ENTER);
    	click(applyButton);
    	assertEquals(10, preferences.getNumMatchingTextDisplay());
    	
    	//test spinner input range < 1
    	click(spinner);
    	push(KeyCode.BACK_SPACE);
    	push(KeyCode.BACK_SPACE);
    	push(KeyCode.DIGIT0);
    	push(KeyCode.ENTER);
    	click(applyButton);
    	assertEquals(1, preferences.getNumMatchingTextDisplay());
    	
    	//test spinner input range > 20
    	click(spinner);
    	push(KeyCode.BACK_SPACE);
    	push(KeyCode.BACK_SPACE);
    	push(KeyCode.DIGIT3);
    	push(KeyCode.DIGIT0);
    	push(KeyCode.ENTER);
    	click(applyButton);
    	assertEquals(20, preferences.getNumMatchingTextDisplay());
    	
    	//test spinner decrement button
    	Node decrementArrowButton = spinner.lookup(".decrement-arrow-button");
    	for(int i=0; i<10; i++){
    		click(decrementArrowButton);
    	}
    	click(applyButton);
    	assertEquals(10, preferences.getNumMatchingTextDisplay());
    	
    	//test spinner increment button
    	Node incrementArrowButton = spinner.lookup(".increment-arrow-button");
    	for(int i=0; i<5; i++){
    		click(incrementArrowButton);
    	}
    	click(applyButton);
    	assertEquals(15, preferences.getNumMatchingTextDisplay());
    	
    	//test checkbox
    	click(cbShowTextSource);
    	click(applyButton);
    	assertEquals(false, preferences.isShowTextSource());
    	click(cbShowTextSource);
    	click(applyButton);
    	assertEquals(true, preferences.isShowTextSource());
    	
    	//test slider
    	Node sliderThumb = slider.lookup(".thumb");
    	drag(sliderThumb).by(200, 0).drop();
    	click(applyButton);
    	assertEquals(100, preferences.getBoxTransparency(), 0);
    	drag(sliderThumb).by(-200, 0).drop();
    	click(applyButton);
    	assertEquals(0, preferences.getBoxTransparency(), 0);

    	deleteTestFile();  	
    }
    
    @Test
    public void testBlockedListPane(){
    	click("#Setting");
    	click("#BlockedList");
    	TextField urlField = find("#urlField");
    	Button addButton = find("#addButton");
    	ToggleButton toggleButton = find("#toggleButton");
    	Button deleteButton = find("#deleteButton");
    	Button applyButton = find("#applyButton");
    	ArrayList<BlockedPage> blockedList;
    	createTestFile();
    	
    	//test add to blocked list
    	click(urlField);
    	type("www.youtube.com");
    	click(addButton);
    	click(applyButton);
    	blockedList = preferences.getBlockedList();
    	assertEquals(1, blockedList.size());
    	assertEquals("www.youtube.com", blockedList.get(0).getUrl());
    	assertEquals(true, blockedList.get(0).getIsEnabled());
    	
    	//test toggle enable/disable
    	click(toggleButton);
    	assertEquals(false, blockedList.get(0).getIsEnabled());
    	click(toggleButton);
    	assertEquals(true, blockedList.get(0).getIsEnabled());
    	
    	//test delete from blocked list
    	click(deleteButton);
    	assertEquals(0, blockedList.size());
    	
    	deleteTestFile();
    }
    
    @Test
    public void testPathPane(){
    	click("#Setting");
    	click("#Path");
    	TextField contentPathField = find("#contentPathField");
    	Button browseContentButton = find("#browseContentButton");
    	CheckBox checkboxShowPreviewText = find("#checkboxShowPreviewText");
    	ChoiceBox<String> cbClearCachesTime = find("#cbClearCachesTime");
    	Button applyButton = find("#applyButton");
    	createTestFile();
    	
    	//test unable to write to text field
    	click(contentPathField);
    	type("aaaaa");
    	assertEquals(preferences.getContentPath(), contentPathField.getText());
    	
    	//test choosing directory
    	click(browseContentButton);
    	type("downloads");
    	push(KeyCode.ENTER);
    	push(KeyCode.ENTER);
    	click(applyButton);
    	assertEquals(preferences.getContentPath(), contentPathField.getText());
    	
    	//test checkbox
    	click(checkboxShowPreviewText);
    	click(applyButton);
    	assertEquals(false, preferences.isShowPreviewText());
    	
    	//test choicebox
    	click(cbClearCachesTime);
    	moveBy(0, 30).click();
    	click(applyButton);
    	assertEquals(cbClearCachesTime.getItems().get(1), preferences.getClearCachesTime());
    	
    	deleteTestFile();
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
