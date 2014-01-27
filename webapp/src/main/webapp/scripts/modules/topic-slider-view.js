define(["knockout", "jquery"],
function(ko, $) { 
	return function(topicexplorer) {
		var self = this;
    	self.windowWidth = ko.observable(100).subscribeTo("windowWidth");
		
    	self.sliderElWidth = ko.computed (function() {
    		console.log(self.windowWidth());
    		console.log($('.topicList > ul > li').size());
			return (self.windowWidth()) / $('.topicList > ul > li').size();
		});
	};
});