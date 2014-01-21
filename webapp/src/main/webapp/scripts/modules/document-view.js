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
				
			};
		});

