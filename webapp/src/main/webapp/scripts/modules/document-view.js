define(
		[ "knockout", "jquery"],
		function(ko, $) {
				self.documentPluginTemplates = topicexplorerModel.config.documentView.pluginTemplates;
				self.documentPluginTemplate = ko.observable(self.documentPluginTemplates[topicexplorerModel.config.documentView.activePlugin]);
				self.documentPluginTemplate.subscribe(function(newValue) {
					topicexplorerModel.config.documentView.activePlugin = topicexplorerModel.config.documentView.pluginTemplates.indexOf(newValue);
				});
				
				self.loadDocument = function(docId) {
					var focus = [docId];
					if(typeof topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].topicId!= 'undefined') {
						if(typeof topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].frame != 'undefined') {
							focus.push({"frame" : topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].frame, "topic": topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].topicId});
						} else {
							focus.push({"topic": topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].topicId});
						}
					}
					topicexplorerModel.newTab('Command=getDoc&DocId=' + docId, 'Doc ' + docId, 'single-view', focus);
				};
				
				self.activeTab = ko.observable(topicexplorerModel.view.activeTab)
					.subscribeTo("TabView.activeTab");
			
				self.activeTab.subscribe(function(newValue) {
					if(topicexplorerModel.view.tab[newValue].module == "document-view") {
						if(topicexplorerModel.view.tab[newValue].focus.length > 0) {
							if(topicexplorerModel.view.tab[newValue].focus[0].hasOwnProperty("topic")) {
								topicexplorerModel.view.tab[newValue].topicId = topicexplorerModel.view.tab[newValue].focus[0].topic;
								if(topicexplorerModel.view.tab[newValue].focus[0].hasOwnProperty("frame")) {
									topicexplorerModel.view.tab[newValue].frame = topicexplorerModel.view.tab[newValue].focus[0].frame;	
								}
								topicexplorerModel.view.tab[newValue].focus.shift();
							}
						}

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
//						console.log($("#desktop").scrollTop() +" " +  $("#desktop")[0].scrollHeight+" "+ self.desktopHeight());
						topicexplorerModel.data.documentsLoading(true);
						topicexplorerModel.loadDocuments(
							{paramString: topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].getParameter + '&offset=' + topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].documentCount},
							function(newDocumentIds) {
								if(newDocumentIds.length < topicexplorerModel.data.documentLimit) { 
									topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].documentsFull(true);
								}
								topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].documentCount += newDocumentIds.length;
								topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].focus = topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].focus.concat(newDocumentIds);
								self.selectedDocuments(topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].focus);
							}
						);
						
					}
				};
				
				self.initialize = function() {
					if(topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].focus.length > 0) {
						if(topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].focus[0].hasOwnProperty("topic")) {
							topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].topicId = topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].focus[0].topic;
							if(topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].focus[0].hasOwnProperty("frame")) {
								topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].frame = topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].focus[0].frame;	
							}
							topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].focus.shift();
						}
					}
					self.selectedDocuments(topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].focus);
				};
				
				return self;
		});

