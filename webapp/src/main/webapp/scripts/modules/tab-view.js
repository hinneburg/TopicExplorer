define(
	[ "knockout", "jquery", "jquery-ui"],
	function(ko, $) {
		return function(topicexplorer) {
			var self = this;
			this.tabs = ko.observableArray(
				topicexplorer.tabs).subscribeTo(
				"TabView.tabs");
			this.invisibleTabs = ko.observableArray().subscribeTo(
				"TabView.invisibleTabs");
			self.windowWidth = ko.observable(1024).subscribeTo("windowWidth");
			self.topicexplorer = topicexplorer;
			self.windowWidth.subscribe(function() {
				var allTabs = topicexplorer.invisibleTabs.concat(topicexplorer.tabs);
				if($("#desktop").width() / allTabs.length >= 200) {
					topicexplorer.tabs = allTabs;
					topicexplorer.invisibleTabs = new Array();
				} else {
					var numVisibleTabs = Math.floor(($("#desktop").width() - 76) / 200);
					topicexplorer.invisibleTabs = allTabs.slice(0, allTabs.length - numVisibleTabs);
					topicexplorer.tabs = allTabs.slice(allTabs.length - numVisibleTabs);					
				}
				self.invisibleTabs(topicexplorer.invisibleTabs);
				self.tabs(topicexplorer.tabs);
			});
			self.setActive = function() {
				$(".tab").removeClass('active');
				$("#tab" + topicexplorer.activeTab).addClass('active');
				
			};
			self.toggleActive = function(active) {
				$('#tabMenu').hide();
				topicexplorer.tab[topicexplorer.activeTab].scrollPosition = $("#desktop").scrollTop();
				topicexplorer.activeTab = active;
				var index = $.inArray(active, self.invisibleTabs());
				if(index > -1) {
					topicexplorer.invisibleTabs.splice(index, 1);
					var newInvisibleTab = topicexplorer.tabs.shift();
					topicexplorer.invisibleTabs.push(newInvisibleTab);
					topicexplorer.tabs.unshift(active);
					self.invisibleTabs(topicexplorer.invisibleTabs);
					self.tabs(topicexplorer.tabs);
				}
				
				topicexplorer.documentsLoading(true);
				ko.postbox.publish("DocumentView.selectedDocuments", topicexplorer.tab[active].documentSorting);
				$("#desktop").scrollTop(topicexplorer.tab[topicexplorer.activeTab].scrollPosition);					
				ko.postbox.publish("TabView.activeTab", topicexplorer.activeTab);
				self.setActive();
				topicexplorer.documentsLoading(false);
			};
			this.deleteTab = function(tab) {
				if(topicexplorer.tabs.length < 2) {
					alert("Ein Tab muss bleiben!");
					return;
				}

				var tabIndex = $.inArray(tab, topicexplorer.tabs);
				
				topicexplorer.tabs.splice(tabIndex, 1);
				delete topicexplorer.tab[tab];
				
				if(self.invisibleTabs().length > 0) {
					var newTabIndex = topicexplorer.invisibleTabs.shift();
					topicexplorer.tabs.unshift(newTabIndex);
					self.invisibleTabs(topicexplorer.invisibleTabs);
				}
					 
				self.tabs(topicexplorer.tabs);
				
				if(topicexplorer.activeTab == tab) {
					if(tabIndex > topicexplorer.tabs.length - 1) {
						tabIndex--;						
					}
					topicexplorer.activeTab = topicexplorer.tabs[tabIndex];
					ko.postbox.publish("TabView.activeTab", topicexplorer.activeTab);
					ko.postbox.publish("DocumentView.selectedDocuments", topicexplorer.tab[topicexplorer.activeTab].documentSorting);
					$("#desktop").scrollTop(topicexplorer.tab[topicexplorer.activeTab].scrollPosition);					
					self.setActive();
				}					
			};
			
			self.deleteAllHidden = function() {
				if(confirm("Alle " + self.invisibleTabs().length + " unsichtbaren Tabs loeschen?")) {
					for(key in topicexplorer.invisibleTabs) {
						console.log(key);
						delete(topicexplorer.tab[topicexplorer.invisibleTabs[key]]);
					}
					
					topicexplorer.invisibleTabs = new Array();
					self.invisibleTabs(topicexplorer.invisibleTabs);
					
				}
			};
		};
});