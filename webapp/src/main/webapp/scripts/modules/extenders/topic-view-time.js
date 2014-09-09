define(["knockout", "jquery"],
function(ko, $) {
	return function (instance) {
		if(globalData.PLUGINS.indexOf('time') > -1) {
			instance.loadTimeViewForTopic = function (topicId) { 			
				ko.postbox.publish('openNewTab', {moduleName:"time-chart-tab", tabHeading:"Time Chart " + topicId, data:{topicId: topicId}});	
			};
		}
	};	
});