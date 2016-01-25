package xaurora;

import xaurora.communication.*;
import xaurora.io.DataFileIO;
import xaurora.security.Security;
import xaurora.system.SystemManager;
import xaurora.ui.LoginUI;

public class Main {


	public static void main(String[] args){
		System.out.println("a");
		SystemManager sa = SystemManager.getInstance();
		System.out.println("Hello");
		if(sa.isNetAccessible()){
			
			ChromeServer chromeSvr = new ChromeServer(6789);
			Thread chromeSvrThread = new Thread(chromeSvr);
			chromeSvrThread.start();
		}
		WordServer wordSvr = new WordServer(23333);
		Thread wordSvrThread = new Thread(wordSvr);
		
		//Establish connection between browser/editor and logic
		
		wordSvrThread.start();
		
		
        sa.login(false);
        
	}

}
