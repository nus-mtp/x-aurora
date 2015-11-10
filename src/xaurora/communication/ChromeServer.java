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
			if(isLogin(text)){
				DropboxAuth.setAccessToken(getURL(text));
			} else{
				outputToFile(text);
			}
		}
	}
	private static boolean isLogin(String text){
		return getURL(text).contains("access_token=")&&getURL(text).contains("www.dropbox.com");
	}
	private static String getURL(String text){
		String[] data = text.split("\n");
		return data[0];
	}
	private void outputToFile(String text) {
		byte[] id = IDGenerator.instanceOf().GenerateID(getURL(text), TYPE_FULL_TEXT);
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
		
		char[] content = new char[length];
		in.read(content);
		for (int i = 0; i < content.length; i++) {
			contentData += String.valueOf(content[i]);
		}
		
		if (contentData!="") //System.out.println("Chrome Plugin Message : "+contentData);
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
		else 
			res = "Received";
		
		return res;
	}
}
