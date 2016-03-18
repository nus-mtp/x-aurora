package xaurora.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import xaurora.io.DataFileIO;
import xaurora.io.IDGenerator;
import xaurora.text.TextIndexer;

public class ChromeServer implements Runnable {
    ServerSocket server = null;
    Socket client = null;
    int port = 0;
    boolean isTextContent = false;
    private static final int TYPE_FULL_TEXT = 0;

    public ChromeServer(int inputedPort) {
        port = inputedPort;
    }

    public void run() {
        try {
            server = new ServerSocket(port);
            client = server.accept();
        } catch (IOException e) {
            // TODO: Think of ways to popup error box
            e.printStackTrace();
        }

        while (true) {
            isTextContent = false;
            String text = receiveMessage();
            if (isTextContent) {
                outputToFile(text);
            }

        }
    }

    private static String getURL(String text) {
        String[] data = text.split("\n");
        return data[0];
    }

    private void outputToFile(String text) {
        String id = IDGenerator.instanceOf().GenerateID(getURL(text),
                TYPE_FULL_TEXT);
        DataFileIO.instanceOf().createDataFile(getURL(text), id,
                String.valueOf(text).getBytes());
    }

    /*
     * Method receiveMessage() Output: String
     * 
     * This method accepts incoming Chrome Plugin connection request. And get
     * the content data of the incoming message.
     */
    private String receiveMessage() {
        String contentData = "";
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);

            String line = "";
            int length = 0;

            // Skip Header
            for (int i = 0; i < 4; i++) {
                line = in.readLine();
                // System.out.println(line);
                // Get length
                if (i == 3) {
                    String[] data = line.split(":");
                    length = Integer.parseInt(data[1].trim());
                }
            }
            for (int i = 0; i < 7; i++) {
                line = in.readLine();
            }

            // read communication code
            int comCode = Integer.parseInt(in.readLine());

            // read content data
            char[] content = new char[length - String.valueOf(comCode).length()
                    - ("\n").length()]; // cut the length of communication code
            in.read(content);
            for (int i = 0; i < content.length; i++) {
                contentData += String.valueOf(content[i]);
            }

            // Generate Output
            out.print(genOutput(contentData, comCode));
            out.flush();

            // Reset communication socket
            in.close();
            out.close();
            client.close();
            client = server.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // System.out.println(contentData);
        return contentData;
    }

    /*
     * Method genOutput(String, int) Output: String
     * 
     * This method reads a String which is communication content data, and an
     * integer which is communication code. According to communication code and
     * content data, generate response contents, and return it.
     */
    private String genOutput(String input, int commCode) {
        String res = new String();

        // Block list shall be merged to one string like:
        String dummyBlocklist = "";// "chrome://\ngoogle.com\nwikipedia.org\n";

        // According to Communication code, generate response.
        switch (commCode) {
        case CommunicationCode.CONNECTION_REQUEST: {
            res = Integer.toString(CommunicationCode.ALL_OK);
            break;
        }
        case CommunicationCode.CONNECTION_REQUEST_WITH_BLOCKLIST: {
            res = Integer.toString(CommunicationCode.BLOCK_LIST);
            res = res + "\n" + dummyBlocklist;
            break;
        }
        case CommunicationCode.SEND_TEXT: {
            isTextContent = true;
            res = Integer.toString(CommunicationCode.RECEIVED);

            WordServer.pushContent(input);
            break;
        }
        default: {
            break;
        }
        }

        return res;
    }
}
