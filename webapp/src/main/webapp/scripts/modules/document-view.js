define(
		[ "knockout" ],
		function(ko) {
			return function(topicexplorer) {
				this.pluginTemplates = topicexplorer.config.documentView.pluginTemplates;
				this.selectedPluginTemplate = ko
						.observable(this.pluginTemplates[0]);

				this.selectedDocuments = ko.observableArray(
						topicexplorerModel.documentSorting).subscribeTo(
						"DocumentView.selectedDocuments");
				topicexplorer.loadDocuments({
					jsonName : "document.json"
				}, this.selectedDocuments);
				this.changeSelectedDocuments = function() {
					topicexplorer.loadDocuments({
						jsonName : "document_new.json"
					}, this.selectedDocuments);
				};
				
			};
		});
