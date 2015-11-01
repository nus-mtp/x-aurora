package xaurora;
import xaurora.communication.*;
import java.util.*;
public class Main {
	public static void main(String[] args){
		Server server = new Server(6789);
		Thread serverThread = new Thread(server);
		serverThread.start();
	}
}
