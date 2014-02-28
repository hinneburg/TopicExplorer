define(
		[ "knockout", "jquery"],
		function(ko, $) {
			return function(topicexplorer) {
				var self  = this;
				this.pluginTemplates = topicexplorer.config.documentView.pluginTemplates;
				this.selectedPluginTemplate = ko.observable(this.pluginTemplates[0]);

				this.selectedDocuments = ko.observableArray(
						topicexplorer.tab[topicexplorerModel.activeTab].documentSorting).subscribeTo(
						"DocumentView.selectedDocuments");
			    
				this.scrollCallback = function(el) {
					$("#browserMenuActivator, #browserMenu").css('top',$("#desktop").scrollTop());
					$("#jumpToStart").css('top',($("#desktop").scrollTop() - 10) + 'px');
					self.loadMoreDocuments();
					
					if($("#desktop").scrollTop() > 1000) {
						$("#jumpToStart").show();
					} else {
						$("#jumpToStart").hide();
					}
				};
				
				this.checkSize = function() {
					if($(".browser").length > 0 ) {
						self.loadMoreDocuments();
					}
				};
				self.leftBodyHeight = ko.observable(100).subscribeTo("leftBodyHeight");
				
				self.desktopHeight = ko.computed(function() {
					return ((self.leftBodyHeight() - 90) * 0.7);
				});			
				
				self.windowWidth = ko.observable(100).subscribeTo("windowWidth");
				self.documentElementWidth = ko.computed (function() {
					var documentWidth = 262;
					var docDeskRatio = Math.floor((self.windowWidth() - 10) / documentWidth);
					return ((self.windowWidth() - 10) / docDeskRatio) - 32;
				});		
				
				topicexplorer.documentsLoading.subscribe(self.checkSize);
				self.desktopHeight.subscribe(self.checkSize);
				self.documentElementWidth.subscribe(self.checkSize);
				
				self.loadMoreDocuments = function() {
					if(!topicexplorer.documentsLoading() && !topicexplorer.tab[topicexplorer.activeTab].documentsFull() && $("#desktop").scrollTop() + self.desktopHeight() + 90 >= $("#desktop")[0].scrollHeight) {
						topicexplorer.documentsLoading(true);
						topicexplorer.loadDocuments(
							{paramString: topicexplorer.tab[topicexplorer.activeTab].documentGetParameter + '&offset=' + topicexplorer.tab[topicexplorer.activeTab].documentCount},
							function(newDocumentIds) {
								if(newDocumentIds.length < topicexplorerModel.documentLimit) { 
									topicexplorer.tab[topicexplorer.activeTab].documentsFull(true);
								}
								topicexplorer.tab[topicexplorer.activeTab].documentSorting = topicexplorer.tab[topicexplorer.activeTab].documentSorting.concat(newDocumentIds);
								self.selectedDocuments(self.selectedDocuments().concat(newDocumentIds));
							}
						);
						topicexplorer.tab[topicexplorer.activeTab].documentCount += topicexplorerModel.documentLimit;
					}
				};
			};
		});

