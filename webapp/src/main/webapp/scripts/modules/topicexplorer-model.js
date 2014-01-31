define(function() {
	self = this;
	self.topicexplorerModel = new Object();
	self.topicexplorerModel.loadDocuments = function (parameter, callback) {
		$.getJSON("http://localhost:8080/webapp/JsonServlet?" + parameter.paramString).success(function(receivedParsedJson) {
			console.log('da: ' + parameter.paramString);
			$.extend(self.topicexplorerModel.document, receivedParsedJson.DOCUMENT);
			callback(receivedParsedJson.DOCUMENT_SORTING);
			self.topicexplorerModel.documentsLoading(false);
		});
	};
    return self.topicexplorerModel;
});
