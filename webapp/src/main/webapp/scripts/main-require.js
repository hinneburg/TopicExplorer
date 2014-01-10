require.config({
	paths : {
		"knockout" : "lib/knockout-3.0.0",
		"knockout-amd-helpers" : "lib/knockout-amd-helpers",
		"knockout-postbox" : "lib/knockout-postbox",
		"text" : "lib/text",
		"jquery" : "lib/jquery-1.9.1.min",
		"jquery-ui" : "lib/jquery-ui-1.10.3.custom.min"			
	}
});

require([ "knockout","jquery", "modules/topicexplorer-view-model",
		"knockout-amd-helpers", "knockout-postbox",
		"text", "jquery-ui"], function(ko, $, App) {
	ko.bindingHandlers.module.baseDir = "modules";
	
	$(document).delegate(".menuActivator", 'click', function(e){
	    $(this).toggleClass("rotate1 rotate2");
	    $(this).parent().next().toggle('slow');
	});
	
	setTimeout(function() {
		ko.applyBindings(new App());
	}, 0);
});
