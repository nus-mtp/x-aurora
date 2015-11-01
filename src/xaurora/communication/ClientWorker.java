package xaurora.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientWorker implements Runnable {	
	private Socket client;
	public ClientWorker(Socket client){
		this.client = client;
	}
	public void run() {
		BufferedReader in = null;
		PrintWriter out = null;
		
		try{
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new PrintWriter(client.getOutputStream(), true);
			processInput(in,out);
		} catch(IOException e){
			
		}
		
			
		close(in, out);
	}
	private void close(BufferedReader in, PrintWriter out){
		try{
			in.close();
			out.close();
			client.close();
		} catch(IOException e){
			System.out.println("Close failed");
		}
	}
	private void processInput(BufferedReader in, PrintWriter out){
		String line = "";
		try {
			
			while((line = in.readLine())!=null){
				System.out.println(line);
			}
			System.out.println("Receive Completed");
			out.flush();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
