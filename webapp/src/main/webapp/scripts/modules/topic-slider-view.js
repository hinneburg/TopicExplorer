define(["knockout", "jquery"],
function(ko, $) { 
	return function(topicexplorer) {
		var self = this;
    	self.windowWidth = ko.observable(100).subscribeTo("windowWidth");
		
    	self.sliderElWidth = ko.computed (function() {
   			return (self.windowWidth()) / $('.topicList > ul > li').size();
		});
	};
});