package xaurora.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class SimpleServer implements Runnable{
	ServerSocket server = null;
	Socket client = null;

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
			receiveMessage();
		}
	}

	private void receiveMessage() {
		try {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				client.getInputStream()));
		PrintWriter out = new PrintWriter(client.getOutputStream(), true);

		String line = "";
		int count = 0;
		int length = 0;
		String contentData = "";
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
		out.println("received!");
		out.flush();
		in.close();
		out.close();
		client.close();
		client = server.accept();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
}
