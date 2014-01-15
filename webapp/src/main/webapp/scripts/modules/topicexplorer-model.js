define(function() {
	self = this;
	self.topicexplorerModel = new Object();
	self.topicexplorerModel.loadDocuments = function (parameter,callback) {
		$.getJSON("http://localhost:8080/webapp/JsonServlet").success(function(receivedParsedJson) {
			self.topicexplorerModel.document = receivedParsedJson.JSON.DOCUMENT;
		//	self.topicexplorerModel.documentSorting = receivedParsedJson.JSON.DOCUMENT_SORTING; 
			alert(typeof callback);
			callback(receivedParsedJson.JSON.DOCUMENT_SORTING);
		});
	};
    return self.topicexplorerModel;
});
