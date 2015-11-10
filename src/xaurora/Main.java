package xaurora;
import xaurora.communication.*;

import java.util.*;
public class Main {
	public static void main(String[] args){
		BrowserServer server = new BrowserServer(6789);
		Thread serverThread = new Thread(server);
		serverThread.start();
	}
}
