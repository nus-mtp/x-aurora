package xaurora.test;

import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import org.junit.Test;
import static org.junit.Assert.*;
import org.loadui.testfx.GuiTest;
import xaurora.ui.PreferenceUI;

/**
 *
 * @author Lee
 */
public class preferenceUITest extends GuiTest{
    
    PreferenceUI preferenceUI = new PreferenceUI();

    @Override
    protected Parent getRootNode() {
    	return preferenceUI.createPreferencePane();
    }
    
    @Test
    public void testSystemPane(){
    	click("#Setting");
    	click("#System");
        CheckBox checkboxRunOnStartUp = find("#checkboxRunOnStartUp");
        CheckBox checkboxHideInToolbar = find("#checkboxHideInToolbar");
        boolean checkboxRunOnStartUpValue = checkboxRunOnStartUp.isSelected();
        boolean checkboxHideInToolbarValue = checkboxHideInToolbar.isSelected();
        click(checkboxRunOnStartUp);
        click(checkboxHideInToolbar);
        assertEquals(!checkboxRunOnStartUpValue, checkboxRunOnStartUp.isSelected());
        assertEquals(!checkboxHideInToolbarValue, checkboxHideInToolbar.isSelected());
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
    
    
}
