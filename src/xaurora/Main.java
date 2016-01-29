package xaurora;

import xaurora.communication.*;
import xaurora.io.DataFileIO;
import xaurora.security.Security;
import xaurora.system.SystemManager;
import xaurora.ui.LoginUI;

public class Main {
private static final int PORT_BROWSER = 6789;
private static final int PORT_PLUGIN = 23333;


	public static void main(String[] args){
		SystemManager sa = SystemManager.getInstance();
		//System.out.println(sa.isNetAccessible());
		if(sa.isNetAccessible()){
			
			ChromeServer chromeSvr = new ChromeServer(PORT_BROWSER);
			Thread chromeSvrThread = new Thread(chromeSvr);
			chromeSvrThread.start();
		}
		WordServer wordSvr = new WordServer(PORT_PLUGIN);
		Thread wordSvrThread = new Thread(wordSvr);
		
		//Establish connection between browser/editor and logic
		
		wordSvrThread.start();
		
		
        sa.login(false);
        
	}

}
