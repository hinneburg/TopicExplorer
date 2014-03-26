define(["knockout", "jquery", "scripts/modules/tab-view"],
function(ko, $) { 
	return function(topicexplorer) {
		var self = this;
    	this.topicexplorer = topicexplorer;
    	this.topicPluginTemplates = topicexplorer.config.topicView.pluginTemplates;
    	this.topicPluginTemplate = ko.observable(this.topicPluginTemplates[topicexplorer.config.topicView.activePlugin]);
    	this.selectedTopics = ko.observableArray(topicexplorerModel.data.topicSorting);
    	this.changeSelectedTopics = function () { this.selectedTopics(["1"]); };
    	this.loadDocumentsForTopic = function (topicId) { 
    		topicexplorer.loadDocumentsForTab("Command=bestDocs&TopicId="+topicId, "Topic " + topicId);
		};   
		self.leftBodyHeight = ko.observable(100).subscribeTo("leftBodyHeight");
		
		self.topicListHeight = ko.computed (function() {
			return(self.leftBodyHeight() - 90) * 0.3;
		});		
	};
});

