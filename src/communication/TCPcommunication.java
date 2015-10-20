package communication;
import java.net.*;
import java.io.*;


public class TCPcommunication
{
   public static void main(String argv[]) throws Exception
   {
         String capitalizedSentence;
         ServerSocket welcomeSocket = new ServerSocket(6789);
        
         while(true)
         {
        	System.out.println("waiting for browser data");
            capitalizedSentence = receive(welcomeSocket);
            System.out.println("sending data to editor");
            clientSend(capitalizedSentence);
            System.out.println("END");
         }
   }
   public static String receive(ServerSocket welcomeSocket) throws Exception
   {
	   Socket connectionSocket = welcomeSocket.accept();
       BufferedReader inFromClient =
          new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
       String clientSentence = inFromClient.readLine();
       System.out.println("Received: " + clientSentence);
       String capitalizedSentence = clientSentence.toUpperCase() + '\n';
       //outToClient.writeBytes(capitalizedSentence);
       return capitalizedSentence;
   }
   
   public static void clientSend(String s) throws Exception
   {

	   Socket clientSocket = new Socket("localhost", 1234);
	   DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

	   outToServer.writeBytes(s + '\n');

	   clientSocket.close();
   }
   public static void run() {
		try {
			int serverPort = 4020;
			ServerSocket serverSocket = new ServerSocket(serverPort);
			serverSocket.setSoTimeout(10000); 
			while(true) {
				System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "..."); 

				Socket server = serverSocket.accept();
				System.out.println("Just connected to " + server.getRemoteSocketAddress()); 

				PrintWriter toClient = 
					new PrintWriter(server.getOutputStream(),true);
				BufferedReader fromClient =
					new BufferedReader(
							new InputStreamReader(server.getInputStream()));
				String line = fromClient.readLine();
				System.out.println("Server received: " + line); 
				toClient.println("Thank you for connecting to " + server.getLocalSocketAddress() + "\nGoodbye!"); 
			}
		}
		catch(UnknownHostException ex) {
			ex.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	  }
		
	  /*public static void main(String[] args) {
			
			run();
	  }*/
}