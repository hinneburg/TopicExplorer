define(["modules/topicexplorer-model","text!/../webapp/JsonServlet", "knockout"], function(topicexplorerModel,temp,ko) {
	var json = JSON.parse(temp);
	topicexplorerModel.document=json.JSON.DOCUMENT; 
	topicexplorerModel.documentLimit = json.LIMIT;
	topicexplorerModel.documentsLoading = ko.observable(false);
	
	topicexplorerModel.tabs = new Array("0");
	topicexplorerModel.tabsLastIndex = 0;
	topicexplorerModel.activeTab = 0;
	
	topicexplorerModel.tab = new Array();
	topicexplorerModel.tab[0] = new Array();
	topicexplorerModel.tab[0].tabTitle = "Topic 1";
	topicexplorerModel.tab[0].documentSorting=json.JSON.DOCUMENT_SORTING; 
	topicexplorerModel.tab[0].documentGetParameter = 'Command=bestDocs&TopicId=1';
	topicexplorerModel.tab[0].documentCount = topicexplorerModel.documentLimit;
	topicexplorerModel.tab[0].documentsFull = ko.observable(false);
	topicexplorerModel.tab[0].scrollPosition = 0;
	
	topicexplorerModel.term = json.JSON.Term;
	topicexplorerModel.topic = json.JSON.Topic;
	topicexplorerModel.topicSorting = json.JSON.TOPIC_SORTING;
});
