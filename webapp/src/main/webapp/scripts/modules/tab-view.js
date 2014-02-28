define(
	[ "knockout", "jquery"],
	function(ko, $) {
		return function(topicexplorer) {
			var self = this;
			this.tabs = ko.observableArray(
					topicexplorer.tabs).subscribeTo(
					"TabView.tabs");
			self.topicexplorer = topicexplorer;
			self.setActive = function() {
				$(".tab").removeClass('active');
				$("#tab" + topicexplorer.activeTab).addClass('active');
			};
			self.toggleActive = function(active) {
				topicexplorer.tab[topicexplorer.activeTab].scrollPosition = $("#desktop").scrollTop();
				topicexplorer.activeTab = active;
				ko.postbox.publish("TabView.activeTab", topicexplorer.activeTab);
				ko.postbox.publish("DocumentView.selectedDocuments", topicexplorer.tab[active].documentSorting);
				$("#desktop").scrollTop(topicexplorer.tab[topicexplorer.activeTab].scrollPosition);					
				self.setActive();
			};
			this.deleteTab = function(tab) {
				if(topicexplorer.tabs.length < 2) {
					alert("Ein Tab muss bleiben!");
					return;
				}
				var tabIndex = $.inArray(tab, topicexplorer.tabs);
				
				topicexplorer.tabs.splice(tabIndex, 1);
				delete topicexplorer.tab[tab];
					 
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
		};
});