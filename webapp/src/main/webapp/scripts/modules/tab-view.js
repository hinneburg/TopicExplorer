define(
	[ "knockout", "jquery", "jquery-ui"],
	function(ko, $) {
	//		var self = this;
	//		self.topicexplorer = topicexplorer;
			self.activeTab = ko.observable(
					topicexplorerModel.view.activeTab).subscribeTo(
					"TabView.activeTab");
			self.tabs = ko.observableArray(
				topicexplorerModel.view.tabs).subscribeTo(
				"TabView.tabs");
			self.invisibleTabs = ko.observableArray(topicexplorerModel.view.invisbleTabs).subscribeTo(
				"TabView.invisibleTabs");
			self.windowWidth = ko.observable(topicexplorerModel.view.windowWidth).subscribeTo("windowWidth");
			
			self.activeModule = ko.observable(topicexplorerModel.view.tab[self.activeTab()].module).subscribeTo(
				"TabView.activeModule");;

			
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
			return self;
});