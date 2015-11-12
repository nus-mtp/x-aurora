/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
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
