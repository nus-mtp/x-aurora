/*
chrome.tabs.query({active: true, currentWindow: true}, function(tabs){
    chrome.tabs.sendMessage(tabs[0].id, {action: "SendIt"}, function(response) {});  
});
*/

// Varibles
var serverStat = "unconnected";
var serverPort = '6789';
var dataPort = '6789';
var ngList = []; // Duplicated and Blocked List


var connector = {
    onLoad: function(){
        setInterval(function(){ connector.getConnection(); },3000);
    },
    
    getConnection: function(){
        var request = new XMLHttpRequest();
        request.addEventListener('readystatechange', function(e) {
            if( this.readyState === 4 ) {
                if  (request.responseText == "200") {
                    serverStat = "connected";
                    console.log("connected!");
                }
                else {
                    serverStat = "unconnected";
                    console.log("connection lost!");
                }
            }
        });
        request.open('PUSH', 'http://127.0.0.1:'+serverPort, true); 
		request.send("Request to Connect");
        request.timeout = 3000;
    },
};

function isInNGList(tarUrl){
    for (i=0; i< ngList.length; i=i+1){
        if (ngList[i] == tarUrl) return true;    
    }
    return false;
}

var textExtractor = {
    pushText: function(tabid, tabUrl){
        console.log("getting text!");
        if ((serverStat == "connected") && (!isInNGList(tabUrl))) {
            chrome.tabs.sendMessage(tabid, {action: "getDOM"}, function(response) {
                var contentText = response.extracted;
                var request = new XMLHttpRequest();
                console.log("Sending Texts!");
                request.open('PUSH', 'http://127.0.0.1:'+dataPort, true);
                request.send(tabUrl+"\n"+contentText);
                
                request.addEventListener('readystatechange', function(e) {
                    if( this.readyState === 4 ) {
                        console.log(request.responseText);
                        if  (request.responseText == "Received") {
                            ngList.push(tabUrl);
                            console.log("Sending Complete!");
                        }
                        else {
                            console.log("Sending Error!");
                            if (serverStat == "connected") {
                                console.log("resending");
                                textExtractor.pushText(tabid,tabUrl);
                            }
                        }
                    }
                });
            });     
        }
    }
}

/*
function encode_utf8(s) {
  return unescape(encodeURIComponent(s));
}

function decode_utf8(s) {
  return decodeURIComponent(escape(s));
}

chrome.tabs.onActivated.addListener(function (info){
    chrome.tabs.query({currentWindow:true, active:true}, function(tabs){
        if (tabs[0].status == "complete")   textExtractor.pushText(tabs[0].id, tabs[0].url);
    })
});
*/

chrome.tabs.onUpdated.addListener(function(tabId , status) {
    chrome.tabs.get(tabId, function(tarTab){
        textExtractor.pushText(tabId, tarTab.url);
    })
});

connector.onLoad();