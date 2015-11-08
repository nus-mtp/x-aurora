package xaurora;
import xaurora.communication.*;

import java.util.*;
public class Main {
	public static void main(String[] args){
		SimpleServer server = new SimpleServer(6789);
		Thread serverThread = new Thread(server);
		serverThread.start();
	}
}
