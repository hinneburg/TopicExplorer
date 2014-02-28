define(["modules/topicexplorer-model","text!/../webapp/JsonServlet", "knockout"], function(topicexplorerModel,temp,ko) {
	var json = JSON.parse(temp);
	topicexplorerModel.document=json.JSON.DOCUMENT; 
	topicexplorerModel.documentLimit = json.LIMIT;
	topicexplorerModel.documentsLoading = ko.observable(false);
	
	topicexplorerModel.tabs = new Array("t0");
	topicexplorerModel.tabsLastIndex = 0;
	topicexplorerModel.activeTab = "t0";
	
	topicexplorerModel.tab = new Array();
	topicexplorerModel.tab["t0"] = new Array();
	topicexplorerModel.tab["t0"].tabTitle = "Topic 1";
	topicexplorerModel.tab["t0"].documentSorting=json.JSON.DOCUMENT_SORTING; 
	topicexplorerModel.tab["t0"].documentGetParameter = 'Command=bestDocs&TopicId=1';
	topicexplorerModel.tab["t0"].documentCount = topicexplorerModel.documentLimit;
	topicexplorerModel.tab["t0"].documentsFull = ko.observable(false);
	topicexplorerModel.tab["t0"].scrollPosition = 0;
	
	topicexplorerModel.term = json.JSON.Term;
	topicexplorerModel.topic = json.JSON.Topic;
	topicexplorerModel.topicSorting = json.JSON.TOPIC_SORTING;
});
