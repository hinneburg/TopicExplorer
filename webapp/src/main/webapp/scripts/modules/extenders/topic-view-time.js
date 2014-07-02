define(["knockout"],
function(ko) {
	return function (instance) {
		instance.loadTimeViewForTopic = function (topicId) { 
			ko.postbox.publish('openNewTab', {moduleName:"time-chart-tab", tabHeading:"Time Chart " + topicId, data:{topicId: topicId}});	
		};
	};	
});