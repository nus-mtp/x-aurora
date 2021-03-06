package xaurora;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

import xaurora.communication.*;
import xaurora.system.DBManager;
import xaurora.system.SystemManager;
import xaurora.ui.LoginUI;
import xaurora.text.PrefixMatcher;

public class Main {
    private static final int PORT_BROWSER = 6789;
    private static final int PORT_PLUGIN = 23333;
    private static final String LOCAL_LOG_PROPERTIES = "/conf_data/system/log4j.properties";
    private static final String EMPTY_STRING = "";

    public static void main(String[] args) {

        BasicConfigurator.configure();
        PropertyConfigurator.configure(getProperties());
        
        SystemManager sa = SystemManager.getInstance();
        sa.reset();
        assert sa.isManagerInitialize();
        DBManager dbManager = DBManager.getClassInstance();
        Thread autoUpdatingThread = new Thread(dbManager);
        autoUpdatingThread.start();
        if (sa.isNetAccessible()) {
            sa.getTimeManagerInstance().calibrateTime();
            BrowserCoimmunicator chromeSvr = new BrowserCoimmunicator(PORT_BROWSER);
            Thread chromeSvrThread = new Thread(chromeSvr);
            chromeSvrThread.start();
        }
        EditorCommunicator wordSvr = new EditorCommunicator(PORT_PLUGIN);
        Thread wordSvrThread = new Thread(wordSvr);

        // Establish connection between browser/editor and logic
        wordSvrThread.start();
        
        LoginUI.main(args);
    }

    private static String getProperties() {
        File temp = new File(EMPTY_STRING);
        return temp.getAbsolutePath() + LOCAL_LOG_PROPERTIES;
    }

}
