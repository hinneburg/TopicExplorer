define(["knockout", "jquery", "jquery.ui", "jquery.tbltree"],
function(ko, $) {
	var self = {};
	self.version = "NLP Configurator Version Alpha 0.1";
	self.wordList = ko.observableArray([]);
	self.loading = ko.observable(false);
	self.openOverlay = function(data) {
		self.loading(true);
		$('#overlay').show();
		$.getJSON("JsonServlet?Command=getWordlist&low=" + data.LOW + "&high=" + data.HIGH).success(function(receivedParsedJson) {
			self.loading(false);
			self.wordList(receivedParsedJson.TOKEN);
		});	
	}; 
	self.closeOverlay = function() {
		$('#overlay').hide();
	}; 
	return self;
});

