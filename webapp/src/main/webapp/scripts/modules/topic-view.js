define(["knockout", "jquery", "scripts/modules/tab-view"],
function(ko, $) { 	
	self.topicPluginTemplates = topicexplorerModel.config.topicView.pluginTemplates;
	self.topicPluginTemplate = ko.observable(self.topicPluginTemplates[topicexplorerModel.config.topicView.activePlugin]);
	self.selectedTopics = ko.observableArray(topicexplorerModel.data.topicSorting);
	
	self.changeSelectedTopics = function () { self.selectedTopics(["1"]); };
	
	self.loadDocumentsForTopic = function (topicId) { 
		topicexplorerModel.newTab("Command=bestDocs&TopicId="+topicId, "Topic " + topicId, 'document-view', [{"topic": topicId}]);	
	};   
	
	self.loadTimeViewForTopic = function (topicId) { 
		topicexplorerModel.newTab('timeView' + topicId, 'Chart Topic ' + topicId, 'time-view', topicId);	
	};
	
	self.leftBodyHeight = ko.observable(100).subscribeTo("leftBodyHeight");
	
	self.topicListHeight = ko.computed (function() {
		return(self.leftBodyHeight() - 90) * 0.3;
	});	
	
	self.initialize = function() {
		if(!topicexplorerModel.data.documentsLoading() && typeof topicexplorerModel.data.frameDataLoaded == 'undefined') {
			topicexplorerModel.data.documentsLoading(true);
			$.getJSON("JsonServlet?Command=getBestFrames").success(function(receivedParsedJson) {
				for (key in receivedParsedJson) {
					var frames = receivedParsedJson[key].SORTING;
					delete receivedParsedJson[key].SORTING;
					$.extend(self.topicexplorerModel.data.topic[key], receivedParsedJson[key]);
					self.topicexplorerModel.data.topic[key].frameCount = 0;
					for(var i in receivedParsedJson[key].FRAMES) {
						if(receivedParsedJson[key].FRAMES.hasOwnProperty(i)) {
							self.topicexplorerModel.data.topic[key].frameCount++;
						}
					}
					self.topicexplorerModel.data.topic[key].frameSorting = ko.observableArray(frames);
					if(self.topicexplorerModel.data.topic[key].frameCount < 10) {
						self.topicexplorerModel.data.topic[key].frameFull = true;
					} else {
						self.topicexplorerModel.data.topic[key].frameFull = false;
					}
				}
				topicexplorerModel.data.frameDataLoaded = 1;
				topicexplorerModel.data.documentsLoading(false);
				
			});
		}
	};
	
	self.loadDocumentsForFrames = function(frameId, event) {
		var target = 0;
        
		if (event.target) target = event.target;
		else if (event.srcElement) target = event.srcElement;
	        
		if (target.nodeType == 3) // defeat Safari bug
			target = target.parentNode;

		var topicId = target.parentNode.parentNode.parentNode.parentNode.parentNode.getAttribute('id').substr(5);
		var frame = topicexplorerModel.data.topic[topicId].FRAMES[frameId].FRAME;
		topicexplorerModel.newTab("Command=bestDocs&TopicId=" + topicId + "&frame=" + frame, "Topic " + topicId + " Frame " + frame, 'document-view', [{"frame": frame, "topic": topicId}]);	
	};
	
	self.frameScrollCallback = function(el) {
		// el = topicId
		
		if(!topicexplorerModel.data.documentsLoading() && !self.topicexplorerModel.data.topic[el].frameFull && $('#topic' + el).children('div').children('.topicElementContent').height() +  $('#topic' + el).children('div').children('.topicElementContent').scrollTop() >=  $('#topic' + el).children('div').children('.topicElementContent')[0].scrollHeight) {
			topicexplorerModel.data.documentsLoading(true);
			$.getJSON("JsonServlet?Command=getFrames&topicId=" + el + "&offset=" + self.topicexplorerModel.data.topic[el].frameCount).success(function(receivedParsedJson) {
				$.extend(self.topicexplorerModel.data.topic[el].FRAMES, receivedParsedJson[el].FRAMES);
				var frames = self.topicexplorerModel.data.topic[el].frameSorting().concat(receivedParsedJson[el].SORTING);
				var count = 0;
				for(var i in receivedParsedJson[el].FRAMES) {
					if(receivedParsedJson[el].FRAMES.hasOwnProperty(i)) {
						count++;
					}
				}
				self.topicexplorerModel.data.topic[el].frameSorting(frames);
				self.topicexplorerModel.data.topic[el].frameCount += count;
				if(count < 10) {
					self.topicexplorerModel.data.topic[el].frameFull = true;
				}
				topicexplorerModel.data.documentsLoading(false);
				
			});

		}
	};
	
	return self;
});

