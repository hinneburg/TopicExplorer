define(["knockout", "jquery"],
function(ko, $) { 
	return function(topicexplorer) {
		var self = this;
    	this.topicexplorer = topicexplorer;
    	this.pluginTemplates = topicexplorer.config.topicView.pluginTemplates;
    	this.selectedPluginTemplate = ko.observable(this.pluginTemplates[0]);
    	this.selectedTopics = ko.observableArray(topicexplorerModel.topicSorting);
    	this.changeSelectedTopics = function () { this.selectedTopics(["1"]); };
    	this.loadDocumentsForTopic = function (topicId) { 
    		topicexplorer.documentsLoading(true);
			
    		var index = ++topicexplorer.tabsLastIndex;
    		topicexplorer.tab[topicexplorer.activeTab].scrollPosition = $("#desktop").scrollTop();
    		topicexplorer.activeTab = "t" + index;
			topicexplorer.tabs.push("t" + index);
			
			topicexplorer.tab["t" + index] = new Array();
			topicexplorer.tab["t" + index].scrollPosition = 0;
			topicexplorer.tab["t" + index].tabTitle = "Topic " + topicId;
			topicexplorer.tab["t" + index].documentGetParameter = "Command=bestDocs&TopicId="+topicId;	
			topicexplorer.loadDocuments(
				{paramString:"Command=bestDocs&TopicId="+topicId},
				function(newDocumentIds) {
					if(newDocumentIds.length < topicexplorerModel.documentLimit) { 
						topicexplorer.tab["t" + index].documentsFull = ko.observable(true);
					} else {
						topicexplorer.tab["t" + index].documentsFull = ko.observable(false);
					}
					topicexplorer.tab["t" + index].documentSorting = newDocumentIds;
					topicexplorer.tab["t" + index].documentCount = newDocumentIds.length;
					
					ko.postbox.publish("DocumentView.selectedDocuments", newDocumentIds);	
				}
			);
    		ko.postbox.publish("TabView.tabs", topicexplorer.tabs);
			$("#desktop").scrollTop(0);
			
		};   
		self.leftBodyHeight = ko.observable(100).subscribeTo("leftBodyHeight");
		
		self.topicListHeight = ko.computed (function() {
			return(self.leftBodyHeight() - 90) * 0.3;
		});		
	};
});

