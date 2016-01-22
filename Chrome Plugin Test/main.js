// Add Listener for tabs to call extractor on page load completed.
chrome.tabs.onUpdated.addListener(function(tabId , changeInfo) {
    chrome.tabs.get(tabId, function(tarTab){
        if (changeInfo.status == "complete") connector.pushText(tabId, tarTab.url, 0);
    })
});

// call connector to load.
connector.onLoad();


function isInBlkList(tarUrl){
    for (var j=0; j< blkList.length; j=j+1){
        if (contains(tarUrl, blkList[j])) return true;    
    }
    return false;
}

function isInDupList(tarUrl){
    for (var j=0; j< dupList.length; j=j+1){
        if (dupList[j] == tarUrl) return true;    
    }
    return false;
}

function contains(orig, targ){
    var result = orig.indexOf(targ);
    if (result > -1) return true;
    else return false;
}