package xaurora.communication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.util.*;
import org.apache.log4j.Logger;

import javafx.scene.input.KeyCode;
import xaurora.system.SystemManager;
import xaurora.text.PrefixMatcher;
import xaurora.util.UserPreference;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author GAO RISHENG A0101891L
 * Description: this class is mainly in charge of 
 * 1. receiving message from editor plugin
 * 2. replying the respective queries
 * 3. Sending contents to editor if necessary (like update hot keys)
 */
public final class EditorCommunicator implements Runnable {
    private static final String ERR_MSG_UNABLE_TO_CREATE_SEND_SOCKET = "Unable to create socket for sending data. ";
    private static final String SECURITY_MSG_DISABLE_SERIALIZE = "Object cannot be serialized";
    private static final String CLASS_CANNOT_BE_DESERIALIZED = "Class cannot be deserialized";
    private static final String DEFAULT_INVALID_SUGGESSTION_LENGTH = "-1";
    private static final String DEFAULT_INVALID_OUTPUT = "INVALID";
    private static final String ERR_MSG_UNABLE_TO_SEND_DATA = "Unable to sent data to editor. ";
    private static final int INDEX_INDEX = 2;
    private static final int INDEX_QUERY_STRING = 1;
    private static final String EMPTY_STRING = "";
    private static final int INDEX_ZERO = 0;
    private static final String LINE_DELIMITER = "\n";
    private static final String MSG_START_WORD_RECEIVER = "Start Word Receiver.";
    private static final int INDEX_FILENAME = 1;
    private static final String HOST_LOCALHOST = "localhost";
    private static final int SEND_PORT = 9353;
    private ServerSocket server = null;
    private Socket client = null;
    private int port = 0;
    private Logger logger;

    public EditorCommunicator(int myPort) {
        this.port = myPort;
        this.logger = Logger.getLogger(this.getClass());
        this.logger.info(MSG_START_WORD_RECEIVER);
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
            receiveMessage();

        }
    }


    /**
     * Description: receive Message from editor plugin
     * @author GAO RISHENG A0101891L
     */
    private final void receiveMessage() {
        String contentData = EMPTY_STRING;
        try {
            // Get input and output Stream
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);

            // Get data length
            int length = Integer.parseInt(in.readLine());

            // Get data content
            char[] content = new char[length];
            in.read(content);
            contentData = new String(content);

            // Split message to get communication code
            String[] parts = contentData.split(LINE_DELIMITER);

            // Generate response to connection request
            ArrayList<String> output = genOutput(parts, Integer.parseInt(parts[INDEX_ZERO]));
            for(String s:output){
                out.println(s);
            }
            out.flush();

            // Reset Socket
            in.close();
            out.close();
            client.close();
            client = server.accept();
        } catch (IOException e) {
            this.logger.error(ERR_MSG_UNABLE_TO_SEND_DATA+e.getMessage());
        }
    }

    /**
     * Description: Generate the output from given queries
     * @param input, the raw input from word plugin
     * @param commCode, the communication code which indicates the type of query
     * @return the content to be sent to the editor plugin
     * @author GAO RISHENG A0101891L
     */
    private final ArrayList<String> genOutput(String[] input, int commCode) {
        ArrayList<String> output = new ArrayList<String>();

        switch (commCode) {
        case CommunicationCode.CONNECTION_REQUEST: {
            output.add(CommunicationCode.ALL_OK + EMPTY_STRING);
            break;
        }
            // requesting for hot keys
        case CommunicationCode.CONNECTION_REQUEST_WITH_HOT_KEY: {
            output.add(CommunicationCode.HOT_KEY + EMPTY_STRING);
            KeyCode[][] hotkeys = UserPreference.getInstance().getHotKey().getCodes();
            for (KeyCode[] temp : hotkeys) {
                output.add(Arrays.toString(temp));
            }
            break;
        }
            // currently is requesting the display number
        case CommunicationCode.REQUEST_FOR_PREFERENCE: {
            output.add(CommunicationCode.PREFERENCE_LIST + EMPTY_STRING);
            output.add(SystemManager.getInstance().getDisplayNumber()
                    + EMPTY_STRING);
        }
            // format
            // line 0 communication code
            // line 1 query string
        case CommunicationCode.QUERY_WITH_INPUT: {
            // make sure there is something to search
            while (!SystemManager.getInstance().isManagerInitialize()) {
            }
            ArrayList<String> suggestions = PrefixMatcher.getResult(
                    input[INDEX_QUERY_STRING],
                    SystemManager.getInstance().getIndexerInstance());
            output.add(suggestions.size()+EMPTY_STRING);
            output.addAll(suggestions);
            break;
        }
            // format
            // line 0 communication code
            // line 1 filename
            // line 2 index of next paragraph
        case CommunicationCode.QUERY_WITH_INDEX: {
            while (!SystemManager.getInstance().isManagerInitialize()) {
            }
            String suggestion = PrefixMatcher.getResult(input[INDEX_FILENAME],
                    Integer.parseInt(input[INDEX_INDEX]),
                    SystemManager.getInstance().getIndexerInstance());
            if(suggestion.equals(DEFAULT_INVALID_OUTPUT)){
                output.add(DEFAULT_INVALID_SUGGESSTION_LENGTH);
            }
            output.add(suggestion);
            break;
        }
        default: {
            assert false;
            break;
        }
        }
        return output;
    }
    
    /**
     * Description: Sending data to Browser in byte array format,
     * re-transmission for invalid response 
     * Expected Response Format: "" for invalid, otherwise for valid
     * 
     * @param content,
     *            the data to be sent
     * @author GAO RISHENG A0101891L
     */
    public synchronized void sentContentToEditor(byte[] content) {
        String response = EMPTY_STRING;
        while (response.equals(EMPTY_STRING)) {
            try {
                Socket clientSocket = new Socket(HOST_LOCALHOST, SEND_PORT);
                DataOutputStream outToServer = new DataOutputStream(
                        clientSocket.getOutputStream());
                BufferedReader inFromServer = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                outToServer.write(content);
                response = inFromServer.readLine();
                clientSocket.close();
            } catch (IOException e) {
                this.logger.error(
                        ERR_MSG_UNABLE_TO_CREATE_SEND_SOCKET + e.getMessage());
            }
        }
    }
    
    /**
     * Secure Programming. Making this Object not-clonable. Object.clone()
     * allows cloning the data of an object without initialize it which may leak
     * the chances for attacker to access the data internally
     * 
     * @Author GAO RISHENG A0101891L
     */
    public final Object clone() throws java.lang.CloneNotSupportedException {
        throw new java.lang.CloneNotSupportedException();
    }

    /**
     * Secure Programming. Disable the serialize option of the object which
     * avoid attacker to print the object in serialize manner and inspect the
     * internal status of the object
     * 
     * @author GAO RISHENG A0101891L
     */
    private final void writeObject(ObjectOutputStream out)
            throws java.io.IOException {
        throw new java.io.IOException(SECURITY_MSG_DISABLE_SERIALIZE);
    }

    /**
     * Secure Programming. Disable the de-serialize option of the object which
     * avoid attacker to de-serialize the object stores in the file system and
     * inspect the internal status of the object
     * 
     * @author GAO RISHENG A0101891L
     */
    private final void readObject(ObjectInputStream in)
            throws java.io.IOException {
        throw new java.io.IOException(CLASS_CANNOT_BE_DESERIALIZED);
    }

}
