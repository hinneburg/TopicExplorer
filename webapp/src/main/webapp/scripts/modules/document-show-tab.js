define(["knockout", "jquery"],
function(ko, $) {
	var instance;
    function Singleton (data) {
		// this could be loaded from somewhere else, however all AMD modules need to be loaded in advance
		var extend = new Array("scripts/modules/extenders/document-show-frames", "scripts/modules/extenders/document-show-text", "scripts/modules/extenders/document-show-time");
		// here comes the show-document-tab 
		var self = {};
		
		self.TextRepresentation = function(label, field) {
    		this.label = label;
    		this.field = field;
    	};
		
    	self.moveToTopic = function(topic) {
			ko.postbox.publish('moveToTopic', topic);
		};
		
		self.singleData= new Object();
		
		self.loading = ko.observable(false);
		
		self.windowWidth = ko.observable(Math.max(800, $(window).width(), /* For opera: */ document.documentElement.clientWidth)).subscribeTo("windowWidth");
		
		self.lineHeight = ko.computed (function() {
			var width = Math.min(Math.max(800, self.windowWidth()), 3000);
			return 130 + (((width - 800) / 2200) * 100);
		});
		
		self.setData = function (data) { 
			
			self.active = ko.observable(JSON.stringify(data));
			if (!self.singleData[self.active()]) {
				self.loading(true);
				$.getJSON("JsonServlet?Command=getDoc&DocId=" + data.documentId)
				.success(function(receivedParsedJson) {
					self.singleData[self.active()] = receivedParsedJson;
					self.singleData[self.active()].TEXT_REPRESENTATION = new Object();
					self.singleData[self.active()].TITLE_REPRESENTATION = new Object();
					self.singleData[self.active()].TEXT_REPRESENTATION.KEYWORDS = receivedParsedJson.DOCUMENT.WORD_LIST[0].TOKEN;
					
					for(var i = 1; i < receivedParsedJson.DOCUMENT.WORD_LIST.length; i++) {
						self.singleData[self.active()].TEXT_REPRESENTATION.KEYWORDS += ' ' + receivedParsedJson.DOCUMENT.WORD_LIST[i].TOKEN;
					}
					self.singleData[self.active()].TITLE_REPRESENTATION.KEYWORDS = globalData.DOCUMENT[data.documentId].KEYWORD_TITLE;
					var keywordRepresentation = new self.TextRepresentation('Keywords', 'KEYWORDS')
					self.singleData[self.active()].textSelectArray = ko.observableArray([keywordRepresentation]);
					self.singleData[self.active()].textSelection = ko.observable(keywordRepresentation);
					self.singleData[self.active()].data = data;
					for (var i=0;i<extend.length;i++) {
						var extender = require(extend[i]);
						extender(self);
					}
					self.loading(false);
				});
			}				
		};
		self.getData = function () { return self.active(); };
		self.setData(data);
		
		
		
		return self;
	};

	return function getSingleton(data) {
		if (instance) { // already exists
			instance.setData(data); // pass the data from view
			return instance; // that already exists
		} else { // instance is not existing
			return (instance = new Singleton(data)); // create a new one with data from view
		};	
	};
});
