define(["modules/topicexplorer-model","text!/../webapp/JsonServlet", "knockout"], function(topicexplorerModel,temp,ko) {
	var json = JSON.parse(temp);
	topicexplorerModel.document=json.JSON.DOCUMENT; 
	topicexplorerModel.documentLimit = json.LIMIT;
	topicexplorerModel.documentsLoading = ko.observable(false);
	
	topicexplorerModel.tabs = new Array("t0");
	topicexplorerModel.invisibleTabs = new Array();
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
	
	topicexplorerModel.loadDocumentsForTab = function (param, headLine) { 
		for(key in topicexplorerModel.tab) {
			if(topicexplorerModel.tab[key].documentGetParameter == param) {
				topicexplorerModel.tab[topicexplorerModel.activeTab].scrollPosition = $("#desktop").scrollTop();
				var index = $.inArray(key, topicexplorerModel.invisibleTabs);
				if(index > -1) {
					topicexplorerModel.invisibleTabs.splice(index, 1);
					var newInvisibleTab = topicexplorerModel.tabs.shift();
					topicexplorerModel.invisibleTabs.push(newInvisibleTab);
					topicexplorerModel.tabs.unshift(key);
					ko.postbox.publish("TabView.invisibleTabs", topicexplorerModel.invisibleTabs);
					ko.postbox.publish("TabView.tabs", topicexplorerModel.tabs);
				}
				topicexplorerModel.activeTab = key;
				ko.postbox.publish("TabView.activeTab", topicexplorerModel.activeTab);
				ko.postbox.publish("DocumentView.selectedDocuments", topicexplorerModel.tab[key].documentSorting);
				$("#desktop").scrollTop(topicexplorerModel.tab[topicexplorerModel.activeTab].scrollPosition);					
				$(".tab").removeClass('active');
				$("#tab" + topicexplorerModel.activeTab).addClass('active');	
				return;
			}
		}
		topicexplorerModel.documentsLoading(true);
		
		var index = ++topicexplorerModel.tabsLastIndex;
		topicexplorerModel.tab[topicexplorerModel.activeTab].scrollPosition = $("#desktop").scrollTop();
		topicexplorerModel.activeTab = "t" + index;
		
		topicexplorerModel.tabs.push("t" + index);
		
		topicexplorerModel.tab["t" + index] = new Array();
		topicexplorerModel.tab["t" + index].scrollPosition = 0;
		topicexplorerModel.tab["t" + index].tabTitle = headLine;
		topicexplorerModel.tab["t" + index].documentGetParameter = param;	
		topicexplorerModel.loadDocuments(
			{paramString:param},
			function(newDocumentIds) {
				if(newDocumentIds.length < topicexplorerModel.documentLimit) { 
					topicexplorerModel.tab["t" + index].documentsFull = ko.observable(true);
				} else {
					topicexplorerModel.tab["t" + index].documentsFull = ko.observable(false);
				}
				topicexplorerModel.tab["t" + index].documentSorting = newDocumentIds;
				topicexplorerModel.tab["t" + index].documentCount = newDocumentIds.length;
				
				if($("#desktop").width() < topicexplorerModel.tabs.length * 200 || topicexplorerModel.invisibleTabs.length) {
					while($("#desktop").width() < topicexplorerModel.tabs.length * 200 + 60) {
						var deletedTab = topicexplorerModel.tabs.shift();
						topicexplorerModel.invisibleTabs.push(deletedTab);
						ko.postbox.publish("TabView.invisibleTabs", topicexplorerModel.invisibleTabs);
					}
					
				}
				
				ko.postbox.publish("TabView.activeTab", topicexplorerModel.activeTab);
				ko.postbox.publish("DocumentView.selectedDocuments", newDocumentIds);	
				ko.postbox.publish("TabView.tabs", topicexplorerModel.tabs);
				$("#desktop").scrollTop(0);
			}
		);
		
		
	};
});
