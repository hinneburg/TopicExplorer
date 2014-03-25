define(["jquery"], function($) {
	self = this;
	self.topicexplorerModel = new Object();
	self.topicexplorerModel.loadDocuments = function (parameter, callback) {
		$.getJSON("JsonServlet?" + parameter.paramString).success(function(receivedParsedJson) {
			$.extend(self.topicexplorerModel.data.document, receivedParsedJson.DOCUMENT);
			callback(receivedParsedJson.DOCUMENT_SORTING);
			self.topicexplorerModel.data.documentsLoading(false);
		});
	};
	self.topicexplorerModel.loadDocument = function (parameter, callback) {
		$.getJSON("JsonServlet?" + parameter.paramString).success(function(receivedParsedJson) {
			$.extend(self.topicexplorerModel.data.document[receivedParsedJson.DOCUMENT.DOCUMENT_ID], receivedParsedJson.DOCUMENT);
			callback(receivedParsedJson.DOCUMENT.DOCUMENT_ID);
			self.topicexplorerModel.data.documentsLoading(false);
		});
	};
    return self.topicexplorerModel;
});
