define(["scripts/modules/topicexplorer-model","text!/JsonServlet", "knockout"], function(topicexplorerModel,temp,ko) {
	var json = JSON.parse(temp);
	
	topicexplorerModel.data = new Array();
	topicexplorerModel.view = new Array();
	
	topicexplorerModel.view.leftBodyHeight = 0;
	topicexplorerModel.view.windowWidth = 0;
	
	topicexplorerModel.data.document=json.JSON.DOCUMENT; 
	topicexplorerModel.data.documentLimit = json.LIMIT;
	topicexplorerModel.data.documentsLoading = ko.observable(false);
	
	topicexplorerModel.view.tabs = new Array("t0");
	topicexplorerModel.view.invisibleTabs = new Array();
	topicexplorerModel.view.tabsLastIndex = 0;
	topicexplorerModel.view.activeTab = "t0";
	
	topicexplorerModel.view.tab = new Array();
	topicexplorerModel.view.tab["t0"] = new Array();
	topicexplorerModel.view.tab["t0"].tabTitle = "Topic 1";
	topicexplorerModel.view.tab["t0"].focus=json.JSON.DOCUMENT_SORTING; 
	topicexplorerModel.view.tab["t0"].getParameter = 'Command=bestDocs&TopicId=1';
	topicexplorerModel.view.tab["t0"].documentCount = topicexplorerModel.data.documentLimit;
	topicexplorerModel.view.tab["t0"].documentsFull = ko.observable(false);
	topicexplorerModel.view.tab["t0"].scrollPosition = 0;
	topicexplorerModel.view.tab["t0"].module = 'document-view';
	
	topicexplorerModel.data.term = json.JSON.Term;
	topicexplorerModel.data.topic = json.JSON.Topic;
	topicexplorerModel.data.topicSorting = json.JSON.TOPIC_SORTING;
	
	
	
});
