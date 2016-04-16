package xaurora.communication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import xaurora.io.IDGenerator;
import xaurora.system.DBManager;
import xaurora.system.SystemManager;
import xaurora.util.BlockedPage;
import xaurora.util.UserPreference;

/**
 * @author GAO RISHENG A0101891L 
 * Description: This class is mainly in charge of
 * 1. receiving data from web browser plug-in
 * 2. replying data towards the web browser plug-in
 * 3. sending necessary data towards web browser plug-in (like block list)
 */
public final class BrowserCoimmunicator implements Runnable {
    private static final String ERR_MSG_UNABLE_TO_CREATE_SEND_SOCKET = "Unable to create socket for sending data. ";
    private static final String SECURITY_MSG_DISABLE_SERIALIZE = "Object cannot be serialized";
    private static final String CLASS_CANNOT_BE_DESERIALIZED = "Class cannot be deserialized";
    private static final String UNKNOWN_URL = "UNKNOWN_URL";
    private static final String ERR_MSG_UNABLE_READ_BROWSER_DATA = "Unable to read browser data.";
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final int NUM_OF_SPACE = 7;
    private static final int INDEX_LENGTH = 1;
    private static final String LENGTH_DELIMITER = ":";
    private static final int END_OF_HEADER = 3;
    private static final int HEADER_LENGTH = 4;
    private static final String MSG_EMPTY_DATA_RETRIEVE = "Empty data is retrieved.";
    private static final String ERR_MSG_UNABLE_TO_GET_BROWSER_DATA = "Unable to receive browser data. ";
    private static final String MSG_START = "Start Browser Receiver.";
    private static final String LINE_DELIMITER = "\n";
    private static final String EMPTY_STRING = "";
    private static final int INDEX_ZERO = 0;
    private static final String HOST_LOCALHOST = "localhost";
    private static final int SEND_PORT = 9302;
    private ServerSocket server = null;
    private Socket client = null;
    private int port = 0;
    private boolean isTextContent = false;
    private static final int TYPE_FULL_TEXT = 0;
    private SystemManager systemManager;
    private DBManager databaseManager;
    private Logger logger;

    public BrowserCoimmunicator(int inputedPort) {
        port = inputedPort;
        this.logger = Logger.getLogger(this.getClass());
        this.logger.info(MSG_START);
    }

    public void run() {
        try {
            server = new ServerSocket(port);
            client = server.accept();
        } catch (IOException e) {
            // TODO: Think of ways to popup error box
            this.logger
                    .error(ERR_MSG_UNABLE_TO_GET_BROWSER_DATA + e.getMessage());
            e.printStackTrace();
        }
        this.systemManager = SystemManager.getInstance();
        this.databaseManager = DBManager.getClassInstance();
        while (!this.systemManager.isManagerInitialize()) {
        }
        while (true) {
            isTextContent = false;
            byte[] text = receiveMessage();
            if (isTextContent) {
                if (text != null) {
                    outputToFile(text, this.systemManager,
                            this.databaseManager);
                }
            }

        }
    }

    /**
     * @param text,
     *            the raw text byte array
     * @return the respective source url, if possible.
     * @author GAO RISHENG A0101891L
     */
    private final static String getURL(byte[] text) {
        String input = new String(text);
        if (input.equals(EMPTY_STRING) || input == null) {
            return UNKNOWN_URL;
        }
        String[] data = input.split(LINE_DELIMITER);
        return data[INDEX_ZERO];
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
    public synchronized void sentContentToBrowser(byte[] content) {
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
     * Description: store the extracted data into the database
     * 
     * @param text,
     *            the raw text data
     * @param manager,
     *            the System Manager instance that handles storing of the data
     * @param databaseManager,
     *            the DBManger instance that handles tracking of the data
     * @author GAO RISHENG
     */
    private final void outputToFile(byte[] text, SystemManager manager,
            DBManager databaseManager) {
        String url = getURL(text);
        // url and content will not be null or ""
        if (!url.equals(EMPTY_STRING)) {
            // id is not possible to be null or ""
            String id = IDGenerator.instanceOf().GenerateID(url,
                    TYPE_FULL_TEXT);
            manager.getDataFileIOInstance().createDataFile(url, id, text,
                    manager, databaseManager);
        } else {
            this.logger.debug(MSG_EMPTY_DATA_RETRIEVE);
        }
    }

    /**
     * Description: Receive browser extracted data
     * 
     * @return the received data in byte array
     * @author GAO RISHENG
     */
    private final byte[] receiveMessage() {
        String contentData = EMPTY_STRING;
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);

            String line = EMPTY_STRING;
            int length = 0;

            // Skip Header
            for (int i = INDEX_ZERO; i < HEADER_LENGTH; i++) {
                line = in.readLine();
                // Get length
                if (i == END_OF_HEADER) {
                    String[] data = line.split(LENGTH_DELIMITER);
                    length = Integer.parseInt(data[INDEX_LENGTH].trim());
                }
            }
            for (int i = INDEX_ZERO; i < NUM_OF_SPACE; i++) {
                line = in.readLine();
            }

            // read communication code
            int comCode = Integer.parseInt(in.readLine());

            // read content data
            char[] content = new char[length - String.valueOf(comCode).length()
                    - LINE_DELIMITER.length()]; // cut the length of
                                                // communication code
            in.read(content);
            contentData = new String(content);
            // Generate Output
            for (String s : genOutput(contentData, comCode)) {
                out.println(s);
            }
            out.flush();

            // Reset communication socket
            in.close();
            out.close();
            client.close();
            client = server.accept();
            return new String(content).getBytes(DEFAULT_ENCODING);
        } catch (IOException e) {
            this.logger.error(ERR_MSG_UNABLE_READ_BROWSER_DATA);
            return new String().getBytes();
        }

    }

    /**
     * @param input,
     *            the input data
     * @param commCode,
     *            the communication code that indicates the type of the input
     * @return the list of content that to be replied to the browser
     * @author GAO RISHENG A0101891L
     */
    private final ArrayList<String> genOutput(String input, int commCode) {
        ArrayList<String> output = new ArrayList<String>();
        // According to Communication code, generate response.
        switch (commCode) {
        case CommunicationCode.CONNECTION_REQUEST: {
            output.add(CommunicationCode.ALL_OK + EMPTY_STRING);
            break;
        }
        case CommunicationCode.CONNECTION_REQUEST_WITH_BLOCKLIST: {
            output.add(CommunicationCode.BLOCK_LIST + EMPTY_STRING);
            ArrayList<BlockedPage> blockList = UserPreference.getInstance()
                    .getBlockedList();
            output.add(blockList.size() + EMPTY_STRING);
            for (BlockedPage b : blockList) {
                output.add(b.getUrl());
            }
            break;
        }
        default: {
            break;
        }
        }

        return output;
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
