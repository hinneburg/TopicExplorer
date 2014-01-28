define(["modules/topicexplorer-model","text!/../webapp/JsonServlet?art=random&id=&limit=20"], function(topicexplorerModel,temp) {
	var json = JSON.parse(temp);
	topicexplorerModel.document=json.JSON.DOCUMENT; 
	topicexplorerModel.documentSorting=json.JSON.DOCUMENT_SORTING; 
	topicexplorerModel.documentGetParameter = 'Command=bestDocs&TopicId=1';
	topicexplorerModel.documentLimit = 20;
	topicexplorerModel.documentCount = topicexplorerModel.documentLimit;
	topicexplorerModel.term = json.JSON.Term;
	topicexplorerModel.topic = json.JSON.Topic;
	topicexplorerModel.topicSorting = json.JSON.TOPIC_SORTING;
});
