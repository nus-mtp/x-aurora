package xaurora;

import java.util.ArrayList;
import java.util.Scanner;

import xaurora.communication.*;
import xaurora.system.DBManager;
import xaurora.system.SystemManager;

import xaurora.ui.LoginUI;
import xaurora.text.PrefixMatcher;

public class Main {
    private static final int PORT_BROWSER = 6789;
    private static final int PORT_PLUGIN = 23333;

    public static void main(String[] args) {
        // Simulate the user Login process
        SystemManager sa = SystemManager.getInstance();
        sa.setCurrentUser("new user");
        sa.setDisplayNumber(10);
        sa.setExpireTime(36);
        sa.setUserEmail("example@gmail.com");
        sa.setSyncDirectory(
                "E:\\study\\study2015sem1\\CS3283\\x-aurora\\local_data\\");
        sa.triggerInitialization();
        assert sa.isManagerInitialize();
        DBManager dbManager = new DBManager();
        Thread autoUpdatingThread = new Thread(dbManager);
        autoUpdatingThread.start();
        if (sa.isNetAccessible()) {

            ChromeServer chromeSvr = new ChromeServer(PORT_BROWSER);
            Thread chromeSvrThread = new Thread(chromeSvr);
            chromeSvrThread.start();
        }

        WordServer wordSvr = new WordServer(PORT_PLUGIN);

        Thread wordSvrThread = new Thread(wordSvr);

        // Establish connection between browser/editor and logic

        wordSvrThread.start();

        ArrayList<String> actualResult = new ArrayList<String>();
        Scanner sc = new Scanner(System.in);
        String userInput = sc.nextLine();
        actualResult = PrefixMatcher.getResult(userInput + "*",
                sa.getIndexerInstance());
        System.out.println(actualResult.isEmpty());
        for (int i = 0; i < actualResult.size(); i++) {
            System.out.println(actualResult.get(i));
        }

        // sa.login(false);

    }

}
