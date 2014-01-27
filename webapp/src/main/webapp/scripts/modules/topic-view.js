define(["knockout"],
function(ko) { 
	return function(topicexplorer) {
	//	var self = this;
    	this.topicexplorer = topicexplorer;
    	this.pluginTemplates = topicexplorer.config.topicView.pluginTemplates;
    	this.selectedPluginTemplate = ko.observable(this.pluginTemplates[0]);
    	this.selectedTopics = ko.observableArray(topicexplorerModel.topicSorting);
    	this.changeSelectedTopics = function () { this.selectedTopics(["1"]); };
    	this.loadDocumentsForTopic = function (topicId) { 
			topicexplorer.loadDocuments(
				{paramString:"Command=bestDocs&TopicId="+topicId},
				function(newDocumentIds) {
					ko.postbox.publish("DocumentView.selectedDocuments", newDocumentIds);
					resizeDocumentDivs();
				}
			);
		};   
		self.leftBodyHeight = ko.observable(100).subscribeTo("leftBodyHeight");
		
		self.topicListHeight = ko.computed (function() {
			return(self.leftBodyHeight() - 90) * 0.3;
		});		
	};
});

