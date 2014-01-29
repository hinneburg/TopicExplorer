define(
		[ "knockout", "jquery"],
		function(ko, $) {
			return function(topicexplorer) {
				var self  = this;
				this.pluginTemplates = topicexplorer.config.documentView.pluginTemplates;
				this.selectedPluginTemplate = ko.observable(this.pluginTemplates[0]);

				this.selectedDocuments = ko.observableArray(
						topicexplorerModel.documentSorting).subscribeTo(
						"DocumentView.selectedDocuments");
			    
				this.scrollCallback = function(el) {
					$("#browserMenuActivator, #browserMenu").css('top',$("#desktop").scrollTop());
					$("#jumpToStart").css('top',($("#desktop").scrollTop() - 10) + 'px');
					if($("#desktop").scrollTop() +  $("#desktop").height() + 60 >= $(".browser")[0].scrollHeight && !topicexplorer.documentsLoading && ! topicexplorer.documentsFull()) {
						topicexplorer.documentsLoading = true;
						topicexplorer.loadDocuments(
							{paramString: topicexplorer.documentGetParameter + '&offset=' + topicexplorer.documentCount},
							function(newDocumentIds) {
								if(newDocumentIds.length < topicexplorerModel.documentLimit) { 
									topicexplorer.documentsFull(true);
									console.log('full');
								}
								ko.postbox.publish("DocumentView.selectedDocuments", self.selectedDocuments().concat(newDocumentIds));
							}
						);
						topicexplorer.documentCount += topicexplorerModel.documentLimit;
					}
					if($("#desktop").scrollTop() > 1000) {
						$("#jumpToStart").show();
					} else {
						$("#jumpToStart").hide();
					}
				};
				self.leftBodyHeight = ko.observable(100).subscribeTo("leftBodyHeight");
				this.desktopHeight = ko.computed(function() {
					return ((self.leftBodyHeight()-90)*0.7);
				});
				self.windowWidth = ko.observable(100).subscribeTo("windowWidth");
				self.documentElementWidth = ko.computed (function() {
					var documentWidth = 262;
					var docDeskRatio = Math.floor((self.windowWidth() - 10) / documentWidth);
					return ((self.windowWidth() - 10) / docDeskRatio) - 32;
				});							
			};
		});

