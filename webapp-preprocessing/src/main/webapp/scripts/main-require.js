require.config({
	baseUrl : "./",
	paths : {
		"knockout" : "scripts/lib/knockout-3.1.0",
		"knockout-amd-helpers" : "scripts/lib/knockout-amd-helpers",
		"knockout-postbox" : "scripts/lib/knockout-postbox",
		"text" : "scripts/lib/text",
		"jquery" : "scripts/lib/jquery.min",
		"jquery.ui" : "scripts/lib/jquery-ui.min",
		"jquery.tbltree" : "scripts/lib/jquery.tbltree"
	},
	shim: {
		'jquery.ui': {
            deps: ['jquery']
        },
        'jquery.tbltree': {
        	deps: ['jquery.ui']
        }
	}
});

require([ "knockout","jquery","text!/JsonServlet?Command=init","knockout-amd-helpers","knockout-postbox","text","jquery.ui", "jquery.tbltree"], 
		function(ko, $, initJson) {
	var self = this;
	self.globalData = JSON.parse(initJson);
	
	$(document).tooltip({ track: true });
	
	ko.bindingHandlers.module.baseDir = "scripts/modules";
	
	ko.applyBindings(new function() {} ());
});



