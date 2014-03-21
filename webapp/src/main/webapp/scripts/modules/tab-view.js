define(
	[ "knockout", "jquery", "jquery-ui"],
	function(ko, $) {
	//		var self = this;
	//		self.topicexplorer = topicexplorer;
			self.activeTab = ko.observable(
					topicexplorerModel.activeTab).subscribeTo(
					"TabView.activeTab");
			self.tabs = ko.observableArray(
				topicexplorerModel.tabs).subscribeTo(
				"TabView.tabs");
			self.invisibleTabs = ko.observableArray().subscribeTo(
				"TabView.invisibleTabs");
			self.windowWidth = ko.observable(1024).subscribeTo("windowWidth");
			
			self.activeModule = ko.observable(topicexplorerModel.tab[self.activeTab()].module).subscribeTo(
				"TabView.activeModule");;
/*			elf.activeTab.subscribe(function() {
				console.log('ha');
				if(topicexplorerModel.tab[self.activeTab()].module !=self.activeModule()) {
					console.log(topicexplorerModel.tab[self.activeTab()].module);
					self.activeModule(topicexplorerModel.tab[self.activeTab()].module);
				}
			});*/
			
			self.windowWidth.subscribe(function(newValue) {
				topicexplorerModel.windowWidth = newValue;
				var allTabs = topicexplorerModel.invisibleTabs.concat(topicexplorerModel.tabs);
				if($("#desktop").width() / allTabs.length >= 200) {
					topicexplorerModel.tabs = allTabs;
					topicexplorerModel.invisibleTabs = new Array();
				} else {
					var numVisibleTabs = Math.floor(($("#desktop").width() - 76) / 200);
					topicexplorerModel.invisibleTabs = allTabs.slice(0, allTabs.length - numVisibleTabs);
					topicexplorerModel.tabs = allTabs.slice(allTabs.length - numVisibleTabs);					
				}
				self.invisibleTabs(topicexplorerModel.invisibleTabs);
				self.tabs(topicexplorerModel.tabs);
			});
			
			self.setActive = function() {
				$(".tab").removeClass('active');
				$("#tab" + topicexplorerModel.activeTab).addClass('active');
				self.activeTab(topicexplorerModel.activeTab);	
				self.activeModule(topicexplorerModel.tab[topicexplorerModel.activeTab].module);
				ko.postbox.publish("DocumentView.selectedDocuments", topicexplorerModel.tab[topicexplorerModel.activeTab].documentSorting);
				$("#desktop").scrollTop(topicexplorerModel.tab[topicexplorerModel.activeTab].scrollPosition);
			};
			self.toggleActive = function(active) {
				$('#tabMenu').hide();
				topicexplorerModel.tab[topicexplorerModel.activeTab].scrollPosition = $("#desktop").scrollTop();
				topicexplorerModel.activeTab = active;
				var index = $.inArray(active, self.invisibleTabs());
				if(index > -1) {
					topicexplorerModel.invisibleTabs.splice(index, 1);
					var newInvisibleTab = topicexplorerModel.tabs.shift();
					topicexplorerModel.invisibleTabs.push(newInvisibleTab);
					topicexplorerModel.tabs.unshift(active);
					self.invisibleTabs(topicexplorerModel.invisibleTabs);
					self.tabs(topicexplorerModel.tabs);
				}
				
				topicexplorerModel.documentsLoading(true);					
				self.setActive();
				topicexplorerModel.documentsLoading(false);
			};
			self.deleteTab = function(tab) {
				if(topicexplorerModel.tabs.length < 2) {
					alert("Ein Tab muss bleiben!");
					return;
				}

				var tabIndex = $.inArray(tab, topicexplorerModel.tabs);
				
				topicexplorerModel.tabs.splice(tabIndex, 1);
				delete topicexplorerModel.tab[tab];
				
				if(self.invisibleTabs().length > 0) {
					var newTabIndex = topicexplorerModel.invisibleTabs.shift();
					topicexplorerModel.tabs.unshift(newTabIndex);
					self.invisibleTabs(topicexplorerModel.invisibleTabs);
				}
					 
				self.tabs(topicexplorerModel.tabs);
				
				if(topicexplorerModel.activeTab == tab) {
					if(tabIndex > topicexplorerModel.tabs.length - 1) {
						tabIndex--;						
					}
					topicexplorerModel.activeTab = topicexplorerModel.tabs[tabIndex];
					self.setActive();
				}					
			};
			
			self.deleteAllHidden = function() {
				if(confirm("Alle " + self.invisibleTabs().length + " unsichtbaren Tabs loeschen?")) {
					for(key in topicexplorerModel.invisibleTabs) {
						delete(topicexplorerModel.tab[topicexplorerModel.invisibleTabs[key]]);
					}
					
					topicexplorerModel.invisibleTabs = new Array();
					self.invisibleTabs(topicexplorerModel.invisibleTabs);
					
				}
			};
			return self;
});