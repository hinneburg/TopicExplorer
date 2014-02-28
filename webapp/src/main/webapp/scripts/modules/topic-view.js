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
    		topicexplorer.activeTab = index;
			topicexplorer.tabs.push("" + index);
			
			topicexplorer.tab[index] = new Array();
			topicexplorer.tab[index].scrollPosition = 0;
			topicexplorer.tab[index].tabTitle = "Topic " + topicId;
			topicexplorer.tab[index].documentCount = topicexplorerModel.documentLimit;
			topicexplorer.tab[index].documentGetParameter = "Command=bestDocs&TopicId="+topicId;	
			topicexplorer.tab[index].documentsFull = ko.observable(false);
    		topicexplorer.loadDocuments(
				{paramString:"Command=bestDocs&TopicId="+topicId},
				function(newDocumentIds) {
					ko.postbox.publish("DocumentView.selectedDocuments", newDocumentIds);
					topicexplorer.tab[index].documentSorting = newDocumentIds;
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

