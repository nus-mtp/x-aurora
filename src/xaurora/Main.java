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
<<<<<<< HEAD
		System.out.println("Hello");
                if(sa.isNetAccessible()){
=======
		//System.out.println(sa.isNetAccessible());
		if(sa.isNetAccessible()){
>>>>>>> 9c429fc631969743fa609d00f0fca7523fa8e1c2
			
			ChromeServer chromeSvr = new ChromeServer(PORT_BROWSER);
			Thread chromeSvrThread = new Thread(chromeSvr);
			chromeSvrThread.start();
<<<<<<< HEAD
                }
		WordServer wordSvr = new WordServer(23333);
=======
		}
		WordServer wordSvr = new WordServer(PORT_PLUGIN);
>>>>>>> 9c429fc631969743fa609d00f0fca7523fa8e1c2
		Thread wordSvrThread = new Thread(wordSvr);
		
		//Establish connection between browser/editor and logic
		
		wordSvrThread.start();
		
		
        sa.login(false);
        
	}

}
