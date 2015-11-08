package xaurora.communication;

import java.io.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;



/**
 * Listen for connections who wish to communicate with ACP Logic. Once the connection 
 * is accepted, a ClientWorker thread will be spawned for further processing.
 * 
 * @author Loke Yan Hao
 */
public class Server implements Runnable{
	
	
	
	private int port;
	private ServerSocket server;
	public static final int BACKLOG = 20;
	
	/**
	 * Basic constructor to create an instance of Server object.
	 * 
	 * @param port	the port it is listening to.
	 */
	public Server(int port){
		this.port = port; 
	}
	
	/**
	 * Terminate the server.
	 */
	public void terminate(){
		// TODO: Need to find a way to stop and clean up the server.
	}
	
	/**
	 * Listen to client connections (eg. plugins) on the port. Once a connection is accepted,
	 * a instance of ClientWorker object will be created. Further processing will be done on 
	 * the ClientWorker object.
	 */
	private void listenConnection(){
		// Listen to port
		try{
			server = new ServerSocket(6789, BACKLOG, InetAddress.getLoopbackAddress());
		} catch (IOException e) {
			// TODO: Think of ways to popup error box
			e.printStackTrace();
		}
		
		// Waiting for clients		
		while(true){
			ClientWorker w;
			Socket s;
			InputStream is;
			OutputStream os;
			BufferedReader br;
			DataOutputStream dos;
			try 
			{
				s = server.accept();
			} 
			catch (IOException e)
			{
				System.err.println("Unable to accept connection: " + e.getMessage());
				continue;
			}
			System.out.println("Connection accepted.");
			
			// Get the input stream (to read from) and output stream
			// (to write to), and wrap nice reader/writer classes around
			// the streams.
			try 
			{
				is = s.getInputStream();
				br = new BufferedReader(new InputStreamReader(is));

				os = s.getOutputStream();
				dos = new DataOutputStream(os);

				// Now, we wait for HTTP request from the connection
				String line = br.readLine();
				System.out.println(line);
				// Bail out if line is null. In case some client tries to be 
				// funny and close immediately after connection.  (I am
				// looking at you, Chrome!)
				if (line == null)
				{
					continue;
				}
				
				// We are expecting the first line to be GET <filename> ...
				// We only care about the first two tokens here.
				String tokens[] = line.split(" ");

				// If the first word is not GET, bail out.  We do not
				// support PUT, HEAD, etc.
				if (!tokens[0].equals("GET"))
				{
					String errorMessage = "This simplistic server only understand GET request\r\n";
					dos.writeBytes("HTTP/1.1 400 Bad Request\r\n");
					dos.writeBytes("Content-length: " + errorMessage.length() + "\r\n\r\n");
					dos.writeBytes(errorMessage);
					s.close();
					continue;
				}

				// We do not really care about the rest of the HTTP
				// request header either.  Read them off the input
				// and throw them away.
				while (!line.equals("")) 
				{
					line = br.readLine();
					System.out.println(line);
				}

				// Print to screen so that we have a log of client's 
				// requests.
				System.out.println("GET " + tokens[1]);

				// The second token indicates the filename.
				//String filename = WEB_ROOT + tokens[1];
				//File file = new File(filename);

				// Check for file permission or not found error.
				

				// Assume everything is OK then.  Send back a reply.
				dos.writeBytes("HTTP/1.1 200 OK\r\n");

				// We send back some HTTP response headers.
				dos.writeBytes("Content-length: 0"  + "\r\n");

				// We could have use Files.probeContentType to find 
				// the content type of the requested file, but let 
				// me do the poor man approach here.
				
				dos.writeBytes("\r\n");

				
				
				
				dos.flush();

				// Finally, close the socket and get ready for
				// another connection.
				s.close();
			}
			catch (IOException e)
			{
				System.err.println("Unable to read/write: "  + e.getMessage());
			}
		}
	}

	/**
	 * Start listening for network connections.
	 */
	public void run() {
		listenConnection();
	}
}
