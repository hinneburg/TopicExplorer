define(["jquery"], function($) {
	self = this;
	self.topicexplorerModel = new Object();
	self.topicexplorerModel.loadDocuments = function (parameter, callback) {
		$.getJSON("http://localhost:8080/webapp/JsonServlet?" + parameter.paramString).success(function(receivedParsedJson) {
			$.extend(self.topicexplorerModel.document, receivedParsedJson.DOCUMENT);
			callback(receivedParsedJson.DOCUMENT_SORTING);
			self.topicexplorerModel.documentsLoading(false);
		});
	};
	self.topicexplorerModel.loadDocument = function (parameter, callback) {
		$.getJSON("http://localhost:8080/webapp/JsonServlet?" + parameter.paramString).success(function(receivedParsedJson) {
			$.extend(self.topicexplorerModel.document[receivedParsedJson.DOCUMENT.DOCUMENT_ID], receivedParsedJson.DOCUMENT);
			callback(receivedParsedJson.DOCUMENT.DOCUMENT_ID);
			self.topicexplorerModel.documentsLoading(false);
		});
	};
    return self.topicexplorerModel;
});
