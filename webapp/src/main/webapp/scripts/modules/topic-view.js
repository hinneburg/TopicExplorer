define(["knockout"],
function(ko) { 
	return function(topicexplorer) {
    	this.topicexplorer = topicexplorer;
    	this.pluginTemplates = topicexplorer.config.topicView.pluginTemplates;
    	this.selectedPluginTemplate = ko.observable(this.pluginTemplates[0]);
    	this.selectedTopics = ko.observableArray(topicexplorerModel.topicSorting);
    	this.changeSelectedTopics = function () { this.selectedTopics(["1"]); };
    	this.loadDocumentsForTopic = function (topicId) { 
			topicexplorer.loadDocuments(
					{jsonName:"bestDocumentsForTopic_"+topicId+".json"},
					function(newDocumentIds) {
						ko.postbox.publish("DocumentView.selectedDocuments", newDocumentIds);
					}
			);
		};   
		
		this.clickedTopic = ko.observable().subscribeTo("clickedTopic");
	};
});

