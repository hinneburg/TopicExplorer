define(function() {
	self = this;
	self.topicexplorerModel = new Object();
	self.topicexplorerModel.loadDocuments = function (parameter,callback) {
		$.getJSON("http://localhost:8080/webapp/JsonServlet").success(function(receivedParsedJson) {
			self.topicexplorerModel.document = receivedParsedJson.JSON.DOCUMENT;
			callback(receivedParsedJson.JSON.DOCUMENT_SORTING);
		});
	};
    return self.topicexplorerModel;
});
