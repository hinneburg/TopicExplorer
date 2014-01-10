define(["knockout"],
function(ko) {
   return function(topicexplorer) {
		this.pluginTemplates = topicexplorer.config.documentView.pluginTemplates;
	    this.selectedPluginTemplate = ko.observable(this.pluginTemplates[0]);
		
	    this.selectedDocuments = ko.observableArray(["1","3"]);
		this.changeSelectedDocuments = function () { this.selectedDocuments(["2","4"]); };
		this.makeMenu = function(el) {
			 $(el).menu({select: function(event, ui) {
		            $(this).parent().parent().hide('slow');}});
		};
    };
});
