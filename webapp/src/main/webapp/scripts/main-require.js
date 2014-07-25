require.config({
	baseUrl : "./",
	paths : {
		"knockout" : "scripts/lib/knockout-3.1.0",
		"knockout-amd-helpers" : "scripts/lib/knockout-amd-helpers",
		"knockout-postbox" : "scripts/lib/knockout-postbox",
		"text" : "scripts/lib/text",
		"jquery" : "scripts/lib/jquery.min",
		"jquery-ui" : "scripts/lib/jquery-ui.min",
		"highstock" : "scripts/lib/highstock",
		"moment": "scripts/lib/moment",
		"filesaver": "scripts/lib/FileSaver"
	},
	shim: {
		'jquery-ui': {
            deps: ['jquery']
        }
	}
});

require([ "knockout","jquery", "text!/JsonServlet?Command=getTopics", "knockout-amd-helpers", "knockout-postbox", "moment",	"text", "jquery-ui",
          "scripts/modules/extenders/document-show-frames", 
          "scripts/modules/extenders/document-show-text",
          "scripts/modules/extenders/document-show-time",
          "scripts/modules/extenders/document-browse-time",
          "scripts/modules/extenders/document-browse-text",
          "scripts/modules/extenders/topic-view-time",
          "scripts/modules/extenders/topic-view-frame",
          "scripts/modules/extenders/topic-view-wordtype"], function(ko, $, topics) {
	var self = this;
	self.globalData = JSON.parse(topics);
	
	for(var i in self.globalData.Topic) {
		var termSorting = [];
		globalData.Topic[i].FULL = {};
		globalData.Topic[i].COUNT = {};
		globalData.Topic[i].SORTING = {};
		globalData.Topic[i].FULL.KEYWORDS = ko.observable(false);
		if( self.globalData.Topic[i].Top_Terms.length < 20) {
			globalData.Topic[i].FULL.KEYWORDS(true);
		} 
		self.globalData.Topic[i].COUNT.KEYWORDS = self.globalData.Topic[i].Top_Terms.length;
		for(var j = 0; j <  self.globalData.Topic[i].Top_Terms.length; j++) {
			termSorting.push(self.globalData.Topic[i].Top_Terms[j].TermId);
		}
		globalData.Topic[i].SORTING.KEYWORDS = ko.observableArray(termSorting);
	}
	
	ko.bindingHandlers.module.baseDir = "scripts/modules";
	
	$(document).tooltip({ track: true });
	
	minHeight = 400;
	minWidth = 800;

	$(window).resize(function() {	
		ko.postbox.publish("windowWidth",Math.max(minWidth, $(window).width(), /* For opera: */ document.documentElement.clientWidth));
		ko.postbox.publish("windowHeight",Math.max(minHeight, $(window).height(), /* For opera: */ document.documentElement.clientHeight));
	});
	
	ko.applyBindings(new function() {} ());
	
	//fallback:
	setTimeout(function() {
		$(window).trigger('resize');
	}, 3000);
	setTimeout(function() {
		$(window).trigger('resize');
	}, 8000);
});



