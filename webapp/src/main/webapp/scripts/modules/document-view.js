define(["knockout"],
function(ko) {
   return function(topicexplorer) {
		this.pluginTemplates = topicexplorer.config.documentView.pluginTemplates;
	    this.selectedPluginTemplate = ko.observable(this.pluginTemplates[0]);
		
	    this.selectedDocuments = ko.observableArray(topicexplorerModel.documentSorting);
//	    this.changeSelectedDocuments = function () { this.selectedDocuments(["2","4"]); };
	    this.changeSelectedDocuments = function () { 
			topicexplorer.loadDocuments({notUsedParameter:"someParameterValue"}, 
										this.selectedDocuments
										); 
		};   
		this.makeMenu = function(el) {
			$(el).menu({select: function(event, ui) {
				$(this).parent().parent().hide('slow');
				$(this).parent().parent().prev().toggleClass("rotate1 rotate2");
			}});
		};
    };
});
