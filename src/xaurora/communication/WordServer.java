package xaurora.communication;

import java.io.BufferedReader;
import java.util.*;
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
	
	// Dummy Database For Sprint 1
	static List<String> recData;
	
	public WordServer(int myPort) {
		port = myPort;
		recData = new ArrayList<String>();
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

	/*
		Method receiveMessage()
		Output: String
		
		This method accepts incoming Word Plugin connection request.
		And get the content data of the incoming message.
	*/
	private String receiveMessage() {
		String contentData = "";
		try {
		// Get input and output Stream
		BufferedReader in = new BufferedReader(new InputStreamReader(
				client.getInputStream()));
		PrintWriter out = new PrintWriter(client.getOutputStream(), true);

		// Get data length
		int length = Integer.parseInt(in.readLine());
		
		// Get data content
		char[] content = new char[length];
		in.read(content);
		for (int i = 0; i < content.length; i++) {
			contentData += String.valueOf(content[i]);
		}
		
		// Debug use
		if (contentData!="") 
			System.out.println("Word Plugin Message : "+contentData);
		
		// Split message to get communication code
		String[] parts = contentData.split("\n");
		
		// Generate response to connection request
		out.print(genOutput(parts, Integer.parseInt(parts[0])));
		out.flush();
		
		// Reset Socket
		in.close();
		out.close();
		client.close();
		client = server.accept();
		} catch (IOException e){
			e.printStackTrace();
		}
		return contentData;
	}
	
	/*
		Method genOutput(String, int)
		Output: String
		
		This method reads a String which is communication content data,
		and an integer which is communication code.
		According to communication code and content data, generate
		response contents, and return it.
	*/
	private String genOutput(String[] input, int commCode){
		String res = new String();
		String[] dummyHotKeys;
		
		switch(commCode){
		case CommunicationCode.CONNECTION_REQUEST:
		{
			if (recData.isEmpty()) res = Integer.toString(CommunicationCode.ALL_OK);
			else {
				res = Integer.toString(CommunicationCode.PREFERENCE_LIST);
				res = res + "\n" + recData.get(0);
				recData = recData.subList(1, recData.size());
			}
			break;
		}
		case CommunicationCode.CONNECTION_REQUEST_WITH_HOT_KEY:
		{
			res = Integer.toString(CommunicationCode.HOT_KEY);
			res = res + "\n" ;
			break;
		}
		default:
		{
			break;
		}
		}
		/*
		if (input.equalsIgnoreCase("Request to Connect")) 
			res = "200";
		else {
			res = "Received\n";
			ArrayList<String> result = PrefixMatcher.getResult(input);
			for(int i = 0;i<result.size();i++){
				res+=result.get(i);
				System.out.println(result.get(i));
			}
		}
		*/
		return res;
	}
	
	// push things into dummy database
	public static void pushContent(String input){
		recData.add(input);
	}
}
