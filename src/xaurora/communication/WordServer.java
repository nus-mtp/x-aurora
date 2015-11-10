package xaurora.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import xaurora.system.PrefixMatcher;

public class WordServer implements Runnable{
	ServerSocket server = null;
	Socket client = null;
	int port = 0;
	
	public WordServer(int myPort) {
		port = myPort;
	}

	public void run()  {
		try{
			server = new ServerSocket(port);
			client = server.accept();
		} catch (IOException e) {
			// TODO: Think of ways to popup error box
			e.printStackTrace();
		}
		
		while (true) {
			receiveMessage();
			
		}
	}

	private String receiveMessage() {
		String contentData = "";
		try {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				client.getInputStream()));
		PrintWriter out = new PrintWriter(client.getOutputStream(), true);

		int length = Integer.parseInt(in.readLine());
		
		char[] content = new char[length];
		in.read(content);
		for (int i = 0; i < content.length; i++) {
			contentData += String.valueOf(content[i]);
		}
		
		if (contentData!="") 
			System.out.println("Word Plugin Message : "+contentData);
		out.print(genOutput(contentData));
		out.flush();
		in.close();
		out.close();
		client.close();
		client = server.accept();
		} catch (IOException e){
			e.printStackTrace();
		}
		return contentData;
	}
	
	private String genOutput(String input){
		String res = new String();
		if (input.equalsIgnoreCase("Request to Connect")) 
			res = "200";
		else {
			res = "Received\n";
			res += PrefixMatcher.getResult(input);
		}
		return res;
	}
}
