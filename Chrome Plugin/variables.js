// Varibles
// Local Varibles
var serverPort = '6789';
var dataPort = '6789';

var blkList = []; // Block List
var dupList = []; // Duplicate List

var serverStat = "unconnected"; //System status of whether connected to main system.
var getBlkList = false; // System status of whether get blocklist from main system
var sending = false; // System status of checking whether is sending text to main system.

var DEFAULT_TIME_OUT = 3000;
var MAX_TIME_OUT_COUNT = 3; // Max time out count for sending texts.
var CONNECTION_STATUS_COMPLETE = 4;  // For xmlHTTPRequest, 4 is the status of complete and got response.

// Communication code
var CONNECTION_REQUEST = "101";
var CONNECTION_REQUEST_WITH_BLOCKLIST = "102";
var SEND_TEXT = "103";

// Received Communication code
var ALL_OK = "200";
var BLOCK_LIST = "150";
var RECEIVED = "151";
var UNKNOWN_CODE = "444";