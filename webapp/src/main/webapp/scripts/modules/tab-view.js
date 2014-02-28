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
				this.toggleActive = function(active) {
					topicexplorer.tab[topicexplorer.activeTab].scrollPosition = $("#desktop").scrollTop();
					topicexplorer.activeTab = parseInt(active);
					ko.postbox.publish("DocumentView.selectedDocuments", topicexplorer.tab[active].documentSorting);
					$("#desktop").scrollTop(topicexplorer.tab[topicexplorer.activeTab].scrollPosition);					
					self.setActive();
				};
				this.deleteTab = function(tab) {
					var index = $.inArray(tab, topicexplorer.tabs);
					topicexplorer.tabs.splice(index, 1);
					topicexplorer.tab.splice(index, 1);
				};
			};
});