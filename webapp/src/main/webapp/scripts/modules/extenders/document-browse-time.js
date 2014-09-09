define(["knockout", "jquery", "moment"],
function(ko, $, moment) {
	return function (instance) {
		if(instance.browseData[instance.active()].sortingOptions.indexOf('TIME') == -1 && globalData.PLUGINS.indexOf('time') > -1)
			instance.browseData[instance.active()].sortingOptions.push('TIME');
	};	
});