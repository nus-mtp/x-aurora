/*
chrome.tabs.query({active: true, currentWindow: true}, function(tabs){
    chrome.tabs.sendMessage(tabs[0].id, {action: "SendIt"}, function(response) {});  
});
*/


var serverStat = "unconnected";
var serverPort = '6789';
var dataPort = '6789';
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
		request.send("Hello Server!\nI want to shake hands!\n");
        request.timeout = 3000;
    },
    
    connectionSuccess: function(){
        serverStat = "connected";
    },
    
    connectionFail: function(){
        serverStat = "unconnected";
    }
};

var textExtractor = {
    pushText: function(tabid){
        if (serverStat == "connected") {
            chrome.tabs.sendMessage(tabid, {action: "getDOM"}, function(response) {
                var request = new XMLHttpRequest();
                console.log("Sending Content");
                request.open('PUSH', 'http://127.0.0.1:'+dataPort, true); 
                request.send(response.extracted);
            });
        }
    }
}

chrome.tabs.onActivated.addListener(function (info){
    textExtractor.pushText(info.tabId);
});

connector.onLoad();