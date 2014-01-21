define(["knockout"],
function(ko) { 
	return function(topicexplorer) {
    	this.topicexplorer = topicexplorer;
    	this.pluginTemplates = topicexplorer.config.topicView.pluginTemplates;
    	this.selectedPluginTemplate = ko.observable(this.pluginTemplates[0]);
    	this.loadDocumentsForSearch = function () { 
    		var searchWord = $('#searchField').val();
			topicexplorer.loadDocuments(
				{paramString:"Command=search&SearchWord="+searchWord},
				function(newDocumentIds) {
					ko.postbox.publish("DocumentView.selectedDocuments", newDocumentIds);
					resizeDocumentDivs();
				}
			);
		};  
	};
});