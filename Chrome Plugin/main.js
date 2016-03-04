// Add Listener for tabs to call extractor on page load completed.
chrome.tabs.onUpdated.addListener(function(tabId , changeInfo) {
    chrome.tabs.get(tabId, function(tarTab){
        if (changeInfo.status == "complete") connector.pushText(tabId, tarTab.url, 0);
    })
});

// call connector to load.
connector.getConnection();
connector.onLoad();

// Utility functions
// Check whether website is in block list
function isInBlkList(tarUrl){
    for (var j=0; j< blkList.length; j=j+1){
        if (contains(tarUrl, blkList[j])) return true;    
    }
    return false;
}

// Check whether website is in duplicate list
function isInDupList(tarUrl){
    for (var j=0; j< dupList.length; j=j+1){
        if (dupList[j] == tarUrl) return true;    
    }
    return false;
}

// Check whether a String x contains a substring y
function contains(orig, targ){
    var result = orig.indexOf(targ);
    if (result > -1) return true;
    else return false;
}

function encode_utf8(s) {
  return unescape(encodeURIComponent(s));
}

function decode_utf8(s) {
  return decodeURIComponent(escape(s));
}

chrome.runtime.onMessage.addListener(
    function(request, sender, sendResponse) {
        chrome.browserAction.setIcon({
            path: request.newIconPath
        });
    });