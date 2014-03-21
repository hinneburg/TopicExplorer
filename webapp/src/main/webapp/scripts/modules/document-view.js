define(
		[ "knockout", "jquery"],
		function(ko, $) {
			
//				var self  = this;
				self.pluginTemplates = topicexplorerModel.config.documentView.pluginTemplates;
				self.selectedPluginTemplate = ko.observable(self.pluginTemplates[topicexplorerModel.config.documentView.activePlugin]);
				self.selectedPluginTemplate.subscribe(function(newValue) {
					topicexplorerModel.config.documentView.activePlugin = topicexplorerModel.config.documentView.pluginTemplates.indexOf(newValue);
				});
				
				self.loadDocument = function(docId) {
					topicexplorerModel.loadDocumentForTab('Command=getDoc&DocId=' + docId, 'Doc ' + docId);
				};
				
				self.activeTab = ko.observable(topicexplorerModel.activeTab).subscribeTo(
						"TabView.activeTab");
				
				self.selectedDocuments = ko.observableArray(
						topicexplorerModel.tab[self.activeTab()].documentSorting).subscribeTo(
						"DocumentView.selectedDocuments");
			    
				self.scrollFunc = function() {
					$("#desktop").scrollTop(topicexplorerModel.tab[self.activeTab()].scrollPosition);
				};
				
				self.scrollCallback = function(el) {
					$("#browserMenuActivator, #browserMenu").css('top',$("#desktop").scrollTop());
					$("#jumpToStart").css('top',($("#desktop").scrollTop() - 10) + 'px');
					self.loadMoreDocuments();
					
					if($("#desktop").scrollTop() > 1000) {
						$("#jumpToStart").show();
					} else {
						$("#jumpToStart").hide();
					}
				};
				
				self.checkSize = function() {
					if($(".browser").length > 0 ) {
						self.loadMoreDocuments();
					}
				};
				self.leftBodyHeight = ko.observable(topicexplorerModel.leftBodyHeight).subscribeTo("leftBodyHeight");
				
				self.desktopHeight = ko.computed(function() {
					return ((self.leftBodyHeight() - 90) * 0.7);
				});			
				
				self.windowWidth = ko.observable(topicexplorerModel.windowWidth).subscribeTo("windowWidth");
				self.documentElementWidth = ko.computed (function() {
					var documentWidth = 262;
					var docDeskRatio = Math.floor((self.windowWidth() - 10) / documentWidth);
					return ((self.windowWidth() - 10) / docDeskRatio) - 32;
				});		
				
				topicexplorerModel.documentsLoading.subscribe(self.checkSize);
				self.desktopHeight.subscribe(self.checkSize);
				self.documentElementWidth.subscribe(self.checkSize);						
						
				self.loadMoreDocuments = function() {
					if(!topicexplorerModel.documentsLoading() && !topicexplorerModel.tab[self.activeTab()].documentsFull() && $("#desktop").scrollTop() + self.desktopHeight() + 90 >= $("#desktop")[0].scrollHeight) {
						topicexplorerModel.documentsLoading(true);
						topicexplorerModel.loadDocuments(
							{paramString: topicexplorerModel.tab[self.activeTab()].documentGetParameter + '&offset=' + topicexplorerModel.tab[self.activeTab()].documentCount},
							function(newDocumentIds) {
								if(newDocumentIds.length < topicexplorerModel.documentLimit) { 
									topicexplorerModel.tab[self.activeTab()].documentsFull(true);
								}
								topicexplorerModel.tab[self.activeTab()].documentSorting = topicexplorerModel.tab[self.activeTab()].documentSorting.concat(newDocumentIds);
								self.selectedDocuments(topicexplorerModel.tab[self.activeTab()].documentSorting);
							}
						);
						topicexplorerModel.tab[self.activeTab()].documentCount += topicexplorerModel.documentLimit;
					}
				};
				return self;
		});

