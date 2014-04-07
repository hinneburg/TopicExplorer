define(
		[ "knockout", "jquery"],
		function(ko, $) {
				self.documentPluginTemplates = topicexplorerModel.config.documentView.pluginTemplates;
				self.documentPluginTemplate = ko.observable(self.documentPluginTemplates[topicexplorerModel.config.documentView.activePlugin]);
				self.documentPluginTemplate.subscribe(function(newValue) {
					topicexplorerModel.config.documentView.activePlugin = topicexplorerModel.config.documentView.pluginTemplates.indexOf(newValue);
				});
				
				self.loadDocument = function(docId) {
					topicexplorerModel.newTab('Command=getDoc&DocId=' + docId, 'Doc ' + docId, 'single-view', docId);
				};
				
				self.activeTab = ko.observable(topicexplorerModel.view.activeTab)
					.subscribeTo("TabView.activeTab");
			
				self.activeTab.subscribe(function(newValue) {
					topicexplorerModel.view.activeTab = newValue;
					if(typeof topicexplorerModel.view.tab[newValue].documentCount == 'undefined' && !topicexplorerModel.data.documentsLoading()) {
						topicexplorerModel.view.tab[newValue].documentsFull = ko.observable(false);
						self.selectedDocuments(new Array());
						topicexplorerModel.data.documentsLoading(true);
						topicexplorerModel.loadDocuments(
							{paramString: topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].getParameter},
							function(newDocumentIds) {
								if(newDocumentIds.length < topicexplorerModel.data.documentLimit) { 
									topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].documentsFull(true);	
								}
								topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].documentCount = newDocumentIds.length;
								topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].focus = topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].focus.concat(newDocumentIds);
								self.selectedDocuments(topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].focus);
							}
						);				
					} else {
						self.selectedDocuments(topicexplorerModel.view.tab[newValue].focus);
					}
				});
				
				self.selectedDocuments = ko.observableArray(
						topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].focus).subscribeTo(
						"DocumentView.selectedDocuments");
			    
				self.scrollFunc = function() {
					$("#desktop").scrollTop(topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].scrollPosition);
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
				self.leftBodyHeight = ko.observable(topicexplorerModel.view.leftBodyHeight).subscribeTo("leftBodyHeight");
				
				self.desktopHeight = ko.computed(function() {
					return ((self.leftBodyHeight() - 90) * 0.7);
				});			
				
				self.windowWidth = ko.observable(topicexplorerModel.view.windowWidth).subscribeTo("windowWidth");
				self.documentElementWidth = ko.computed (function() {
					var documentWidth = 262;
					var docDeskRatio = Math.floor((self.windowWidth() - 10) / documentWidth);
					return ((self.windowWidth() - 10) / docDeskRatio) - 32;
				});		
				
				topicexplorerModel.data.documentsLoading.subscribe(self.checkSize);
				self.desktopHeight.subscribe(self.checkSize);
				self.documentElementWidth.subscribe(self.checkSize);						
						
				self.loadMoreDocuments = function() {
					if(!topicexplorerModel.data.documentsLoading() && !topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].documentsFull() && $("#desktop").scrollTop() + self.desktopHeight() + 90 >= $("#desktop")[0].scrollHeight) {
						topicexplorerModel.data.documentsLoading(true);
						topicexplorerModel.loadDocuments(
							{paramString: topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].documentGetParameter + '&offset=' + topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].documentCount},
							function(newDocumentIds) {
								if(newDocumentIds.length < topicexplorerModel.data.documentLimit) { 
									topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].documentsFull(true);
								}
								topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].documentSorting = topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].documentSorting.concat(newDocumentIds);
								self.selectedDocuments(topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].documentSorting);
							}
						);
						topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].documentCount += topicexplorerModel.data.documentLimit;
					}
				};
				return self;
		});

