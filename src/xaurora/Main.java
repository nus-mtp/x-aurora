package xaurora;

import xaurora.communication.*;
import xaurora.security.Security;
import xaurora.system.SystemManager;
import xaurora.ui.LoginUI;

public class Main {

    public static void main(String[] args) {
        /***
        ChromeServer chromeSvr = new ChromeServer(6789);
        WordServer wordSvr = new WordServer(23333);

        Thread chromeSvrThread = new Thread(chromeSvr);
        Thread wordSvrThread = new Thread(wordSvr);

        chromeSvrThread.start();
        wordSvrThread.start();
        SystemManager s = SystemManager.getInstance();
        LoginUI.main(null);
        * ***/

        Security s = Security.getInstance();
        try {
            s.encrypt("abcdefg".getBytes("UTF-8"));
            for (int i = 0; i < s.decrypt(s.encrypt("abcdefg".getBytes("UTF-8"))).length; i++) {
                System.out.print((char) s.decrypt(s.encrypt("abcdefg".getBytes("UTF-8")))[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
