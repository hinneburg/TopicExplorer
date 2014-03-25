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
	topicexplorerModel.view.tab["t0"].documentSorting=json.JSON.DOCUMENT_SORTING; 
	topicexplorerModel.view.tab["t0"].documentGetParameter = 'Command=bestDocs&TopicId=1';
	topicexplorerModel.view.tab["t0"].documentCount = topicexplorerModel.data.documentLimit;
	topicexplorerModel.view.tab["t0"].documentsFull = ko.observable(false);
	topicexplorerModel.view.tab["t0"].scrollPosition = 0;
	topicexplorerModel.view.tab["t0"].module = 'document-view';
	
	topicexplorerModel.data.term = json.JSON.Term;
	topicexplorerModel.data.topic = json.JSON.Topic;
	topicexplorerModel.data.topicSorting = json.JSON.TOPIC_SORTING;
	
	
	topicexplorerModel.checkTabExists = function(documentGetParameter) {
		topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].scrollPosition = $("#desktop").scrollTop();
		
		for(key in topicexplorerModel.view.tab) {
			if(topicexplorerModel.view.tab[key].documentGetParameter == documentGetParameter) {
				var index = $.inArray(key, topicexplorerModel.view.invisibleTabs);
				if(index > -1) {
					topicexplorerModel.view.invisibleTabs.splice(index, 1);
					var newInvisibleTab = topicexplorerModel.view.tabs.shift();
					topicexplorerModel.view.invisibleTabs.push(newInvisibleTab);
					topicexplorerModel.view.tabs.unshift(key);
					ko.postbox.publish("TabView.invisibleTabs", topicexplorerModel.view.invisibleTabs);
					
				}
				topicexplorerModel.view.activeTab = key;
				ko.postbox.publish("TabView.activeModule", topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].module);
				ko.postbox.publish("TabView.activeTab", topicexplorerModel.view.activeTab);
				$(".tab").removeClass('active');
				$("#tab" + topicexplorerModel.view.activeTab).addClass('active');	
				ko.postbox.publish("DocumentView.selectedDocuments", topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].documentSorting);
				ko.postbox.publish("TabView.tabs", topicexplorerModel.view.tabs);
				$("#desktop").scrollTop(topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].scrollPosition);					
				
				
				return true;
			}
		}
		return false;
	};
	
	topicexplorerModel.loadDocumentsForTab = function (param, headLine) { 
		if(topicexplorerModel.checkTabExists(param)) {
			return;
		}
		
		topicexplorerModel.data.documentsLoading(true);
		
		var index = ++topicexplorerModel.view.tabsLastIndex;
		topicexplorerModel.view.activeTab = "t" + index;
		
		topicexplorerModel.view.tabs.push("t" + index);
		
		topicexplorerModel.view.tab["t" + index] = new Array();
		topicexplorerModel.view.tab["t" + index].scrollPosition = 0;
		topicexplorerModel.view.tab["t" + index].tabTitle = headLine;
		topicexplorerModel.view.tab["t" + index].module = 'document-view';
		topicexplorerModel.view.tab["t" + index].documentGetParameter = param;	
		topicexplorerModel.loadDocuments(
			{paramString:param},
			function(newDocumentIds) {
				if(newDocumentIds.length < topicexplorerModel.documentLimit) { 
					topicexplorerModel.view.tab["t" + index].documentsFull = ko.observable(true);
				} else {
					topicexplorerModel.view.tab["t" + index].documentsFull = ko.observable(false);
				}
				topicexplorerModel.view.tab["t" + index].documentSorting = newDocumentIds;
				topicexplorerModel.view.tab["t" + index].documentCount = newDocumentIds.length;
				
				if($("#desktop").width() < topicexplorerModel.view.tabs.length * 200 || topicexplorerModel.view.invisibleTabs.length) {
					var allTabs = topicexplorerModel.view.invisibleTabs.concat(topicexplorerModel.view.tabs);
					var numVisibleTabs = Math.floor(($("#desktop").width() - 76) / 200);
					topicexplorerModel.view.invisibleTabs = allTabs.slice(0, allTabs.length - numVisibleTabs);
					topicexplorerModel.view.tabs = allTabs.slice(allTabs.length - numVisibleTabs);					
					ko.postbox.publish("TabView.invisibleTabs", topicexplorerModel.view.invisibleTabs);
				}
				
				ko.postbox.publish("TabView.activeTab", topicexplorerModel.view.activeTab);
				ko.postbox.publish("DocumentView.selectedDocuments", newDocumentIds);	
				ko.postbox.publish("TabView.tabs", topicexplorerModel.view.tabs);
				$("#desktop").scrollTop(0);
			}
		);
	};
	
	topicexplorerModel.loadDocumentForTab = function(param, headLine) {
		if(topicexplorerModel.checkTabExists(param)) {
			return;
		}
		
		var index = ++topicexplorerModel.view.tabsLastIndex;
		topicexplorerModel.view.activeTab = "t" + index;
		
		topicexplorerModel.view.tabs.push("t" + index);
		
		topicexplorerModel.view.tab["t" + index] = new Array();
		topicexplorerModel.view.tab["t" + index].tabTitle = headLine;
		topicexplorerModel.view.tab["t" + index].module = 'single-view';
		topicexplorerModel.view.tab["t" + index].documentsFull = ko.observable(true);
		topicexplorerModel.view.tab["t" + index].documentGetParameter = param;
		topicexplorerModel.loadDocument(
			{paramString:param},
			function(newDocumentId) {
				topicexplorerModel.view.tab["t" + index].documentSorting = new Array(newDocumentId);
				
				if($("#desktop").width() < topicexplorerModel.view.tabs.length * 200 || topicexplorerModel.view.invisibleTabs.length) {
					var allTabs = topicexplorerModel.view.invisibleTabs.concat(topicexplorerModel.view.tabs);
					var numVisibleTabs = Math.floor(($("#desktop").width() - 76) / 200);
					topicexplorerModel.view.invisibleTabs = allTabs.slice(0, allTabs.length - numVisibleTabs);
					topicexplorerModel.view.tabs = allTabs.slice(allTabs.length - numVisibleTabs);					
					ko.postbox.publish("TabView.invisibleTabs", topicexplorerModel.view.invisibleTabs);
				}
				
				ko.postbox.publish("TabView.activeTab", topicexplorerModel.view.activeTab);
				ko.postbox.publish("DocumentView.selectedDocuments", topicexplorerModel.view.tab["t" + index].documentSorting);	
				ko.postbox.publish("TabView.tabs", topicexplorerModel.view.tabs);
				$("#desktop").scrollTop(0);
			}
		);
		
	};
});
