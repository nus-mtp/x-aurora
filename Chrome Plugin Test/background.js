

/*
chrome.tabs.query({active: true, currentWindow: true}, function(tabs){
    chrome.tabs.sendMessage(tabs[0].id, {action: "SendIt"}, function(response) {});  
});
*/
var serverStat = "unconnected";
var connector = {
    onLoad: function(){
        setInterval(function(){ connector.getConnection() },3000);
    },
    
    getConnection: function(){
        var request = new XMLHttpRequest();
        request.onreadystatechange = function() {
            if (request.readyState == 4) {
                alert(request.responseText);
            }
        }
        request.addEventListener("load", connector.connectionSuccess, false);  
		request.addEventListener("error", connector.connectionFail, false);
        request.open('GET', 'http://127.0.0.1:6789', true); 
		request.send('Hello Server!');
    },
    
    connectionSuccess: function(){
        serverStat = "connected";
        textExtractor.pushText();
    },
    
    connectionFail: function(){
        alert("Connection Lost!");  
        serverStat = "unconnected";
    }
};

var textExtractor = {
    pushText: function(){
        var request = new XMLHttpRequest();
        request.onreadystatechange = function() {
            if (request.readyState == 4) {
                alert(request.responseText);
            }
        }
        request.open('PUT', 'http://127.0.0.1:6789', true); 
		request.send('This is a plain text.');
    }
}

connector.onLoad();