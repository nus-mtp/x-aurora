package xaurora.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import xaurora.io.DataFileIO;
import xaurora.io.IDGenerator;

public class SimpleServer implements Runnable{
	ServerSocket server = null;
	Socket client = null;
	private static final int TYPE_FULL_TEXT = 0;
	public SimpleServer(int port) {
		try{
			server = new ServerSocket(6789);
			client = server.accept();
		} catch (IOException e) {
			// TODO: Think of ways to popup error box
			e.printStackTrace();
		}
		
	}

	public void run()  {

		while (true) {
			String text = receiveMessage();
			outputToFile(text);
		}
	}

	private void outputToFile(String text) {
		String[] data = text.split("\n");
		byte[] id = IDGenerator.instanceOf().GenerateID(data[0], TYPE_FULL_TEXT);
		DataFileIO.instanceOf().createDataFile(id, String.valueOf(text).getBytes());
	}

	private String receiveMessage() {
		String contentData = "";
		try {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				client.getInputStream()));
		PrintWriter out = new PrintWriter(client.getOutputStream(), true);

		String line = "";
		int count = 0;
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
		
		if (contentData!="") System.out.println(contentData);
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
