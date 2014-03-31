define(
	[ "knockout", "jquery", "jquery-ui"],
	function(ko, $) {
		self.activeTab = ko.observable(topicexplorerModel.view.activeTab);
		self.tabs = ko.observableArray(topicexplorerModel.view.tabs);
		self.invisibleTabs = ko.observableArray(topicexplorerModel.view.invisbleTabs);
		self.activeModule = ko.observable(topicexplorerModel.view.tab[self.activeTab()].module);
		
		self.windowWidth = ko.observable(topicexplorerModel.view.windowWidth).subscribeTo("windowWidth");

		self.windowWidth.subscribe(function(newValue) {
			topicexplorerModel.view.windowWidth = newValue;
			var allTabs = topicexplorerModel.view.invisibleTabs.concat(topicexplorerModel.view.tabs);
			if($("#desktop").width() / allTabs.length >= 200) {
				topicexplorerModel.view.tabs = allTabs;
				topicexplorerModel.view.invisibleTabs = new Array();
			} else {
				var numVisibleTabs = Math.floor(($("#desktop").width() - 76) / 200);
				topicexplorerModel.view.invisibleTabs = allTabs.slice(0, allTabs.length - numVisibleTabs);
				topicexplorerModel.view.tabs = allTabs.slice(allTabs.length - numVisibleTabs);			
			}
			self.invisibleTabs(topicexplorerModel.view.invisibleTabs);
			self.tabs(topicexplorerModel.view.tabs);
		});
		
		self.setActive = function() {
			$(".tab").removeClass('active');
			$("#tab" + topicexplorerModel.view.activeTab).addClass('active');
			self.activeTab(topicexplorerModel.view.activeTab);	
			self.activeModule(topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].module);
			ko.postbox.publish("DocumentView.selectedDocuments", topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].documentSorting);
			$("#desktop").scrollTop(topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].scrollPosition);
		};
		self.toggleActive = function(active) {
			$('#tabMenu').hide();
			topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].scrollPosition = $("#desktop").scrollTop();
			topicexplorerModel.view.activeTab = active;
			var index = $.inArray(active, self.invisibleTabs());
			if(index > -1) {
				topicexplorerModel.view.invisibleTabs.splice(index, 1);
				var newInvisibleTab = topicexplorerModel.view.tabs.shift();
				topicexplorerModel.view.invisibleTabs.push(newInvisibleTab);
				topicexplorerModel.view.tabs.unshift(active);
				self.invisibleTabs(topicexplorerModel.view.invisibleTabs);
				self.tabs(topicexplorerModel.view.tabs);
			}
			
			topicexplorerModel.data.documentsLoading(true);			
			self.setActive();
			topicexplorerModel.data.documentsLoading(false);
		};
		self.deleteTab = function(tab) {
			if(topicexplorerModel.view.tabs.length < 2) {
				alert("Ein Tab muss bleiben!");
				return;
			}
	
			var tabIndex = $.inArray(tab, topicexplorerModel.view.tabs);
			
			topicexplorerModel.view.tabs.splice(tabIndex, 1);
			delete topicexplorerModel.view.tab[tab];
			
			if(self.invisibleTabs().length > 0) {
				var newTabIndex = topicexplorerModel.view.invisibleTabs.shift();
				topicexplorerModel.view.tabs.unshift(newTabIndex);
				self.invisibleTabs(topicexplorerModel.view.invisibleTabs);
			}
				 
			self.tabs(topicexplorerModel.view.tabs);
			
			if(topicexplorerModel.view.activeTab == tab) {
				if(tabIndex > topicexplorerModel.view.tabs.length - 1) {
				tabIndex--;			
				}
				topicexplorerModel.view.activeTab = topicexplorerModel.view.tabs[tabIndex];
				self.setActive();
			}			
		};
		
		self.deleteAllHidden = function() {
			if(confirm("Alle " + self.invisibleTabs().length + " unsichtbaren Tabs loeschen?")) {
				for(key in topicexplorerModel.view.invisibleTabs) {
				delete(topicexplorerModel.view.tab[topicexplorerModel.view.invisibleTabs[key]]);
				}
				
				topicexplorerModel.view.invisibleTabs = new Array();
				self.invisibleTabs(topicexplorerModel.view.invisibleTabs);
				
			}
		};
		topicexplorerModel.checkTabExists = function(documentGetParameter) {
			topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].scrollPosition = $("#desktop").scrollTop();
			
			for(key in topicexplorerModel.view.tab) {
				if(topicexplorerModel.view.tab[key].documentGetParameter == documentGetParameter) {
				var index = $.inArray(key, topicexplorerModel.view.invisibleTabs);
				if(index > -1) {
					topicexplorerModel.view.invisibleTabs.splice(index, 1);
					var newInvisibleTab = topicexplorerModel.view.tabs.shift();
					topicexplorerModel.view.invisibleTabs.push(newInvisibleTab);
					topicexplorerModel.view.tabs.unshift(key);
					self.tabs(topicexplorerModel.view.tabs);
					self.invisibleTabs(topicexplorerModel.view.invisbleTabs);
					
				}
				topicexplorerModel.view.activeTab = key;
				self.setActive();
				return true;
				}
			}
			return false;
		};
		
		topicexplorerModel.loadDocumentsForTab = function (param, headLine) { 
			if(topicexplorerModel.checkTabExists(param)) {
				return;
			}
			
			topicexplorerModel.data.documentsLoading(true);
			
			var index = ++topicexplorerModel.view.tabsLastIndex;
			topicexplorerModel.view.activeTab = "t" + index;
			
			topicexplorerModel.view.tabs.push("t" + index);
			
			topicexplorerModel.view.tab["t" + index] = new Array();
			topicexplorerModel.view.tab["t" + index].scrollPosition = 0;
			topicexplorerModel.view.tab["t" + index].tabTitle = headLine;
			topicexplorerModel.view.tab["t" + index].module = 'document-view';
			topicexplorerModel.view.tab["t" + index].documentGetParameter = param;	
			topicexplorerModel.loadDocuments({paramString:param},function(newDocumentIds) {
				if(newDocumentIds.length < topicexplorerModel.documentLimit) { 
					topicexplorerModel.view.tab["t" + index].documentsFull = ko.observable(true);
				} else {
					topicexplorerModel.view.tab["t" + index].documentsFull = ko.observable(false);
				}
				topicexplorerModel.view.tab["t" + index].documentSorting = newDocumentIds;
				topicexplorerModel.view.tab["t" + index].documentCount = newDocumentIds.length;
				
				if($("#desktop").width() < topicexplorerModel.view.tabs.length * 200 || topicexplorerModel.view.invisibleTabs.length) {
					var allTabs = topicexplorerModel.view.invisibleTabs.concat(topicexplorerModel.view.tabs);
					var numVisibleTabs = Math.floor(($("#desktop").width() - 76) / 200);
					topicexplorerModel.view.invisibleTabs = allTabs.slice(0, allTabs.length - numVisibleTabs);
					topicexplorerModel.view.tabs = allTabs.slice(allTabs.length - numVisibleTabs);	
					self.invisibleTabs(topicexplorerModel.view.invisibleTabs);
				}
				self.tabs(topicexplorerModel.view.tabs);
				self.setActive();
				}
			);
		};
		
		topicexplorerModel.loadDocumentForTab = function(param, headLine) {
			if(topicexplorerModel.checkTabExists(param)) {
				return;
			}
			
			var index = ++topicexplorerModel.view.tabsLastIndex;
			topicexplorerModel.view.activeTab = "t" + index;
			
			topicexplorerModel.view.tabs.push("t" + index);
			
			topicexplorerModel.view.tab["t" + index] = new Array();
			topicexplorerModel.view.tab["t" + index].scrollPosition = 0;
			topicexplorerModel.view.tab["t" + index].tabTitle = headLine;
			topicexplorerModel.view.tab["t" + index].module = 'single-view';
			topicexplorerModel.view.tab["t" + index].documentsFull = ko.observable(true);
			topicexplorerModel.view.tab["t" + index].documentGetParameter = param;
			topicexplorerModel.loadDocument(
				{paramString:param},
				function(newDocumentId) {
				topicexplorerModel.view.tab["t" + index].documentSorting = new Array(newDocumentId);
				
				if($("#desktop").width() < topicexplorerModel.view.tabs.length * 200 || topicexplorerModel.view.invisibleTabs.length) {
					var allTabs = topicexplorerModel.view.invisibleTabs.concat(topicexplorerModel.view.tabs);
					var numVisibleTabs = Math.floor(($("#desktop").width() - 76) / 200);
					topicexplorerModel.view.invisibleTabs = allTabs.slice(0, allTabs.length - numVisibleTabs);
					topicexplorerModel.view.tabs = allTabs.slice(allTabs.length - numVisibleTabs);			
					self.invisibleTabs(topicexplorerModel.view.invisibleTabs);		
				}
				self.tabs(topicexplorerModel.view.tabs);
				self.setActive();
				}
			);
		
		};
		return self;
});