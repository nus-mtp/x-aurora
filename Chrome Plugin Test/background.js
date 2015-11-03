/*
chrome.tabs.query({active: true, currentWindow: true}, function(tabs){
    chrome.tabs.sendMessage(tabs[0].id, {action: "SendIt"}, function(response) {});  
});
*/

var serverStat = "unconnected";
var serverPort = '6789';
var dataPort = '6789';
var ngList = [];
var connector = {
    onLoad: function(){
        setInterval(function(){ connector.getConnection(); },3000);
    },
    
    getConnection: function(){        
        var request = new XMLHttpRequest();
        request.onreadystatechange = function() {
            if (request.readyState == 4) {
                var respond = request.responseText;
            }
        }
        request.addEventListener("load", connector.connectionSuccess, false);  
		request.addEventListener("error", connector.connectionFail, false);
        request.open('PUSH', 'http://127.0.0.1:'+serverPort, true); 
		request.send("");
        request.timeout = 3000;
    },
    
    connectionSuccess: function(){
        console.log("connected!")
        serverStat = "connected";
    },
    
    connectionFail: function(){
        serverStat = "unconnected";
    }
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
                var request = new XMLHttpRequest();
                console.log("Sending Texts!")
                request.open('PUSH', 'http://127.0.0.1:'+dataPort, true); 
                request.send(encode_utf8(response.extracted));
                ngList.push(tabUrl);
            });     
        }
    }
}

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

chrome.tabs.onUpdated.addListener(function(tabId , status) {
    chrome.tabs.query({currentWindow:true, active:true}, function(tabs){
        if ((tabs[0].status == "complete") && (tabId == tabs[0].id))   textExtractor.pushText(tabs[0].id, tabs[0].url);
    })
});

connector.onLoad();