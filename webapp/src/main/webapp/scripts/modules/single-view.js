define(
    [ "knockout", "jquery"],
    function(ko, $) {
    	self.leftBodyHeight=ko.observable(topicexplorerModel.leftBodyHeight).subscribeTo("leftBodyHeight");
    	
    	self.desktopHeight= ko.computed(function() {
    		return ((this.leftBodyHeight() - 90) * 0.7);
    	});
    	self.activeTab = ko.observable(topicexplorerModel.activeTab).subscribeTo(
			"TabView.activeTab");
    	self.activeTab.subscribe(function(newValue) {
    		self.singlePluginTemplate(self.singlePluginTemplates[topicexplorerModel.config.singleView.activePlugin]);
		});
    	self.selectedDocuments = ko.observable(topicexplorerModel.tab[topicexplorerModel.activeTab].documentSorting).subscribeTo('DocumentView.selectedDocuments');
    	
    	self.markWords = function() {
    		if(!topicexplorerModel.document[self.selectedDocuments()[0]].TEXT_WITH_MARKED_WORDS) {
	    		var text = topicexplorerModel.document[self.selectedDocuments()[0]].TEXT$FULLTEXT;
	    		var words = topicexplorerModel.document[self.selectedDocuments()[0]].WORD_LIST;
	    		for(key in words) {
	    			text = text.substring(0, words[key].POSITION_OF_TOKEN_IN_DOCUMENT) 
	    				+ '<span style="border-bottom: 2px solid ' + topicexplorerModel.topic[words[key].TOPIC_ID].COLOR_TOPIC$COLOR + ';">' 
	    				+ words[key].TOKEN + '</span>' + text.substring(parseInt(words[key].POSITION_OF_TOKEN_IN_DOCUMENT) + words[key].TOKEN.length);
	    		}
	    		topicexplorerModel.document[self.selectedDocuments()[0]].TEXT_WITH_MARKED_WORDS = text;
    		}
    		return topicexplorerModel.document[self.selectedDocuments()[0]].TEXT_WITH_MARKED_WORDS;
    	};
    	
    	self.singleScrollCallback = function(el) {
			$("#singleMenuActivator, #singleMenu").css('top',$("#desktop").scrollTop());
		};
    	
		self.singlePluginTemplates = topicexplorerModel.config.singleView.pluginTemplates;
		self.singlePluginTemplate = ko.observable(self.singlePluginTemplates[topicexplorerModel.config.singleView.activePlugin]);
		self.singlePluginTemplate.subscribe(function(newValue) {
			topicexplorerModel.config.singleView.activePlugin = topicexplorerModel.config.singleView.pluginTemplates.indexOf(newValue);
		});
		
    	return self;
    }); 
