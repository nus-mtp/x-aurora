chrome.runtime.onMessage.addListener(function(request, sender, sendResponse) {
    sendResponse({extracted: document.body.innerText});
});

// Get the text within an element
// Doesn't do any normalising, returns a string
// of text as found.
function getText(element) {
  var text = [];
  var self = arguments.callee;
  var el, els = element.childNodes;
  var excluded = {
    'noscript': 'noscript',
    'script'  : 'script'
  };

  for (var i=0, iLen=els.length; i<iLen; i++) {
    el = els[i];

    // May need to add other node types here
    if ( el.nodeType == 1 && 
       !(el.tagName.toLowerCase() in excluded)) {
      text.push(self(el));

    // If working with XML, add nodeType 4 to get text from CDATA nodes
    } else if (el.nodeType == 3) {

      // Deal with extra whitespace and returns in text here.
      text.push(el.data);
    }
  }
  return text.join('');
}