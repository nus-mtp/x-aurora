package xaurora.communication;

public class CommunicationCode {

    // Received Communication code
    public static final int CONNECTION_REQUEST = 101;
    public static final int CONNECTION_REQUEST_WITH_BLOCKLIST = 102;
    public static final int SEND_TEXT = 103;

    public static final int CONNECTION_REQUEST_WITH_HOT_KEY = 131;
    public static final int REQUEST_FOR_PREFERENCE = 132;

    // Respond code
    public static final int ALL_OK = 200;
    public static final int BLOCK_LIST = 150;
    public static final int RECEIVED = 151;

    public static final int HOT_KEY = 171;
    public static final int PREFERENCE_LIST = 172;
    //@author GAO RISHENG A0101891L
    public static final int QUERY_WITH_INPUT = 0xFF;
    public static final int QUERY_WITH_INDEX = 0xEE;
}
