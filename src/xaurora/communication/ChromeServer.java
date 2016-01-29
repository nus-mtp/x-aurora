package xaurora.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import xaurora.dropbox.*;
import xaurora.io.DataFileIO;
import xaurora.io.IDGenerator;

public class ChromeServer implements Runnable{
	ServerSocket server = null;
	Socket client = null;
	int port = 0;
	boolean isTextContent = false;
	private static final int TYPE_FULL_TEXT = 0;
	
	public ChromeServer(int inputedPort) {
		port = inputedPort;
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
	
			String text = receiveMessage();
			outputToFile(text);
			
		}
	}

	private static String getURL(String text){
		String[] data = text.split("\n");
		return data[0];
	}
	private void outputToFile(String text) {
		String id = IDGenerator.instanceOf().GenerateID(getURL(text), TYPE_FULL_TEXT);
		DataFileIO.instanceOf().createDataFile(id, String.valueOf(text).getBytes());
	}

	private String receiveMessage() {
		String contentData = "";
		try {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				client.getInputStream()));
		PrintWriter out = new PrintWriter(client.getOutputStream(), true);

		String line = "";
		int length = 0;
		
		for (int i=0; i<4; i++){
			line = in.readLine();
			if (i==3){
				String[] data = line.split(":");
				length = Integer.parseInt(data[1].trim());
			}
		}
		for (int i=0; i<7; i++){
			line = in.readLine();
		}
		int comCode = Integer.parseInt(in.readLine());
		
		char[] content = new char[length];
		in.read(content);
		for (int i = 0; i < content.length; i++) {
			contentData += String.valueOf(content[i]);
		}
		out.print(genOutput(contentData,comCode));
		
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
	
	private String genOutput(String input, int commCode){
		String res = new String();
		
		// Block list shall be merged to one string like:
		String dummyBlocklist = "chrome://\ngoogle.com\nwikipedia.org\n";
		
		switch (commCode) {
		case CommunicationCode.CONNECTION_REQUEST: {
			res = Integer.toString(CommunicationCode.ALL_OK);
			break;
		}
		case CommunicationCode.CONNECTION_REQUEST_WITH_BLOCKLIST :{
			res = Integer.toString(CommunicationCode.BLOCK_LIST);
			res = res + "\n" + dummyBlocklist;
			break;
		}
		case CommunicationCode.SEND_TEXT :{
			isTextContent = true;
			res = Integer.toString(CommunicationCode.RECEIVED);
			
			WordServer.pushContent(input);
			break;
		}
		default:{
			break;
		}
		}
		
		return res;
	}
}
