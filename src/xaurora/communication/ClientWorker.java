package xaurora.communication;
import java.util.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
		System.exit(0);
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
			
			int count = 0;
			int length = 0;
			String contentData = "";
			Scanner sc = new Scanner(in);
			while(sc.hasNext()){
				line = sc.nextLine();
				System.out.println(line);
				
				count++;
				if(count == 4){
					String[] data = line.split(":");
					length = Integer.parseInt(data[1].trim());
					
				}
				if(count ==12){
					char[] content = new char[length];
					in.read(content);
					for(int i = 0;i<content.length;i++){
						contentData+=String.valueOf(content[i]);
					}
					break;
				}
			}
			System.out.println(contentData);
			outputToFile(contentData,"output.txt");
			System.out.println("Receive Completed");
			out.flush();
			
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	private static void outputToFile(String content,String outputPath){
		try{
		
			File outputFile = new File(outputPath);
			if(!outputFile.exists()){
				outputFile.createNewFile();
			}
			FileWriter fw = new FileWriter(outputFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content+"\n");
			bw.flush();
			bw.close();
		} catch(IOException e){
			e.printStackTrace();
		}
	}
}
