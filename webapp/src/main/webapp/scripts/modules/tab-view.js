define(
	[ "knockout", "jquery", "jquery-ui"],
	function(ko, $) {
		self.activeTab = ko.observable(topicexplorerModel.view.activeTab).publishOn("TabView.activeTab");
		self.activeModule = ko.observable(topicexplorerModel.view.tab[self.activeTab()].module);
//		self.activeTab.subscribe(function(newValue) {
//			console.log(topicexplorerModel.view.tab[newValue].module);
//			self.activeModule(topicexplorerModel.view.tab[newValue].module);
//			ko.postbox.publish("TabView.activeTab", newValue);			
//		});
		
		self.tabs = ko.observableArray(topicexplorerModel.view.tabs);
		self.invisibleTabs = ko.observableArray(topicexplorerModel.view.invisbleTabs);
		
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
		
		self.setActive = function(allowLoading) {
//			
			if(!allowLoading) topicexplorerModel.data.documentsLoading(true);
			$("#desktop").scrollTop(0);
			$(".tab").removeClass('active');
			$("#tab" + topicexplorerModel.view.activeTab).addClass('active');
			
			if(self.activeModule() == topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].module) {
				self.activeTab(topicexplorerModel.view.activeTab);	
				ko.postbox.publish("TabView.activeTab", topicexplorerModel.view.activeTab);		
			} else {
				self.activeModule(topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].module);
			}
			
			
			
//			ko.postbox.publish("TabView.activeTab", topicexplorerModel.view.activeTab);
//			console.log("newTab");
			
			$("#desktop").scrollTop(topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].scrollPosition);
			if(!allowLoading) topicexplorerModel.data.documentsLoading(false);
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
			self.setActive(false);	
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
				self.setActive(false);
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
		topicexplorerModel.checkTabExists = function(getParameter) {
			topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].scrollPosition = $("#desktop").scrollTop();
			
			for(key in topicexplorerModel.view.tab) {
				if(topicexplorerModel.view.tab[key].getParameter == getParameter) {
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
				
				self.setActive(false);
				return true;
				}
			}
			return false;
		};
		
		topicexplorerModel.newTab = function(param, headLine, module, focus) {
			if(topicexplorerModel.checkTabExists(param)) {
				return;
			}
			
			var index = ++topicexplorerModel.view.tabsLastIndex;
			topicexplorerModel.view.activeTab = "t" + index;
			
			topicexplorerModel.view.tabs.push("t" + index);
			
			topicexplorerModel.view.tab["t" + index] = new Array();
			topicexplorerModel.view.tab["t" + index].scrollPosition = 0;
			topicexplorerModel.view.tab["t" + index].tabTitle = headLine;
			topicexplorerModel.view.tab["t" + index].module = module;
			topicexplorerModel.view.tab["t" + index].getParameter = param;
			topicexplorerModel.view.tab["t" + index].focus = focus;
			topicexplorerModel.view.tab["t" + index].documentsFull = ko.observable(true);
			
			if($("#desktop").width() < topicexplorerModel.view.tabs.length * 200 || topicexplorerModel.view.invisibleTabs.length) {
				var allTabs = topicexplorerModel.view.invisibleTabs.concat(topicexplorerModel.view.tabs);
				var numVisibleTabs = Math.floor(($("#desktop").width() - 76) / 200);
				topicexplorerModel.view.invisibleTabs = allTabs.slice(0, allTabs.length - numVisibleTabs);
				topicexplorerModel.view.tabs = allTabs.slice(allTabs.length - numVisibleTabs);			
				self.invisibleTabs(topicexplorerModel.view.invisibleTabs);		
			}
			self.tabs(topicexplorerModel.view.tabs);
			self.setActive(true);
			
		};
		
		return self;
});