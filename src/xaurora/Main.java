package xaurora;
import xaurora.communication.*;

import xaurora.security.Security;

import xaurora.ui.LoginUI;


public class Main {
	public static void main(String[] args){
		ChromeServer chromeSvr = new ChromeServer(6789);
		WordServer wordSvr = new WordServer(23333);
		
		Thread chromeSvrThread = new Thread(chromeSvr);
		Thread wordSvrThread = new Thread(wordSvr);
		
		chromeSvrThread.start();
		wordSvrThread.start();

                
        LoginUI.main();

	}
}
