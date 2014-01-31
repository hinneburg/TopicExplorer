define(["modules/topicexplorer-model","text!/../webapp/JsonServlet", "knockout"], function(topicexplorerModel,temp,ko) {
	var json = JSON.parse(temp);
	topicexplorerModel.document=json.JSON.DOCUMENT; 
	topicexplorerModel.documentSorting=json.JSON.DOCUMENT_SORTING; 
	topicexplorerModel.documentGetParameter = 'Command=bestDocs&TopicId=1';
	topicexplorerModel.documentLimit = json.LIMIT;
	topicexplorerModel.documentCount = topicexplorerModel.documentLimit;
	topicexplorerModel.documentsLoading = ko.observable(false);
	topicexplorerModel.documentsFull = ko.observable(false);
	topicexplorerModel.term = json.JSON.Term;
	topicexplorerModel.topic = json.JSON.Topic;
	topicexplorerModel.topicSorting = json.JSON.TOPIC_SORTING;
});
