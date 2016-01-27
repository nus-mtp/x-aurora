var connector = {
    
    // Method onLoad()
    // Creating an interval action of generating connection request.
    onLoad: function(){
        setInterval(function(){ connector.getConnection(); },3000); 
    },
    
    //Method getConnection()
    getConnection: function(){
        var request = new XMLHttpRequest();
        
        // Response Check
        request.addEventListener('readystatechange', function(e) {
            if( this.readyState === 4 ) {
                
                // Get response code
                var resp = request.responseText.split("\n");
                var respCode = resp[0];
                
                // According to response code, take actions.
                if  (respCode == ALL_OK) {
                    serverStat = "connected";
                    console.log("Connection is ALIVE!"); // Console message.
                } else if (respCode == BLOCK_LIST) {
                    serverStat = "connected";
                    getBlkList = 1;
                    
                    for (var i=1; i<(resp.length-1); i++){
                        currURL = resp[i];
                        console.log(currURL); // Console message.
                        if ((!isInBlkList(currURL)) && (currURL!="")) blkList.push(currURL);
                    }
                    console.log("Block list updated!"); // Console message.
                    console.log(blkList); // Console message.
                }
                else {
                    serverStat = "unconnected";
                    console.log("Connection lost!"); // Console message.
                }
            }
        });
        
        // Send Connection Response
        request.open('PUSH', 'http://127.0.0.1:'+serverPort, true); 
        if (sending != 1){
            if (getBlkList == 0) {
                request.send(CONNECTION_REQUEST_WITH_BLOCKLIST);
            } else {
                request.send(CONNECTION_REQUEST);
            }
        }
        request.timeout = 3000;
    },
    
    // Method pushText(int tabid, String tabUrl, int timeOutCount)
    pushText: function(tabid, tabUrl, timeOutCount){
        
        // Dulpicate List and Block List check.
        if (isInBlkList(tabUrl)){
            console.log("URL : "+tabUrl+" is already in Blocked List."); // Console message.
        }
        else if (isInDupList(tabUrl)){
            console.log("URL : "+tabUrl+" is already browsed and content sent to main system."); // Console message.
        }
        
        else {
            
            // Connection Check
            if (serverStat == "connected") {
                
                // Extract Text
                chrome.tabs.sendMessage(tabid, {action: "getDOM"}, function(response) {
                    
                    sending = 1; // Set system status to sending texts, which will not generate regular connection request.
                    
                    console.log("getting text from : "+tabUrl); // Console message.
                    var contentText = response.extracted;
                    var request = new XMLHttpRequest();
                    console.log("Sending Texts!"); // Console message.
                    request.open('PUSH', 'http://127.0.0.1:'+dataPort, true);
                    request.send(SEND_TEXT+"\n"+tabUrl+"\n"+contentText);

                    // Upon received response, do actions according to response.
                    request.addEventListener('readystatechange', function(e) {
                        if( this.readyState === 4 ) {
                            if  (request.responseText == RECEIVED) {
                                console.log("Adding Url:" + tabUrl + " to duplicated list."); // Console message.
                                dupList.push(tabUrl);
                                sending = 0;
                                console.log("Sending Complete!"); // Console message.
                            }
                            else if (timeOutCount <= MAX_TIME_OUT_COUNT){
                                console.log("Sending Error!"); // Console message.
                                if (serverStat == "connected") {
                                    console.log("resending"); // Console message.
                                    timeOutCount += 1;
                                    console.log("Timeout:"+timeOutCount+" for URL:"+tabUrl) // Console message.
                                    textExtractor.pushText(tabid,tabUrl,timeOutCount+1);
                                }
                            } else {
                                sending = 0;
                            }
                        }
                    });
                });     
            }
        }
    },
};


/*
function encode_utf8(s) {
  return unescape(encodeURIComponent(s));
}

function decode_utf8(s) {
  return decodeURIComponent(escape(s));
}
*/