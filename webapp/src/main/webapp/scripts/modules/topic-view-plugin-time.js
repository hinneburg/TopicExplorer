define(["knockout", "jquery","scripts/modules/topic-view", "scripts/modules/tab-view"],
function(ko, $, self) { 	

	self.loadTimeViewForTopic = function (topicId) { 
		topicexplorerModel.newTab('timeView' + topicId, 'Chart Topic ' + topicId, 'time-view', topicId);	
	};
	return self;
});

