define(["knockout"],
function(ko) {
	return function (instance) {
		if(instance.browseData[instance.active()].sortingOptions.indexOf('TIME') == -1)
			instance.browseData[instance.active()].sortingOptions.push('TIME');
	};	
});