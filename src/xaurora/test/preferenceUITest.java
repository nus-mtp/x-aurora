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
        CheckBox checkbox1 = find("#checkbox1");
        CheckBox checkbox2 = find("#checkbox2");
        boolean checkbox1Value = checkbox1.isSelected();
        boolean checkbox2Value = checkbox2.isSelected();
        click(checkbox1);
        click(checkbox2);
        assertEquals(!checkbox1Value, checkbox1.isSelected());
        assertEquals(!checkbox2Value, checkbox2.isSelected());
    }
}
