package xaurora.Simple_Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class SimpleServer {
	ServerSocket server = null;
	Socket client = null;

	public SimpleServer(int port) throws Exception {
		server = new ServerSocket(port);
		client = server.accept();
	}

	public void run() throws Exception {

		while (true) {
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
		}
	}
}
