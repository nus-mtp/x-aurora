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
        return preferenceUI.createSettingPane();
    }
    
    @Test
    public void isCheckboxClicked(){
        CheckBox checkboxRunOnStartUp = find("#checkboxRunOnStartUp");
        CheckBox checkboxHideInToolbar = find("#checkboxHideInToolbar");
        boolean checkboxRunOnStartUpValue = checkboxRunOnStartUp.isSelected();
        boolean checkboxHideInToolbarValue = checkboxHideInToolbar.isSelected();
        click(checkboxRunOnStartUp);
        click(checkboxHideInToolbar);
        assertEquals(!checkboxRunOnStartUpValue, checkboxRunOnStartUp.isSelected());
        assertEquals(!checkboxHideInToolbarValue, checkboxHideInToolbar.isSelected());
    }
}
