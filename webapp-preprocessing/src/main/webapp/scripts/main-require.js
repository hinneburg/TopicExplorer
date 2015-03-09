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
	
	self.globalData.FLAT_TREE =  {};
	flatTree(self.globalData.WORDTYPE_WORDCOUNTS);
	
	$(document).tooltip({ track: true });
	
	ko.bindingHandlers.module.baseDir = "scripts/modules";
	
	ko.applyBindings(new function() {} ());
});

function flatTree(childArray) {
	for(idx in childArray) {
		node = {}
		node.PARENT = childArray[idx].PARENT;
		children = [];
		for(idx2 in childArray[idx].CHILDREN) {
			children.push(childArray[idx].CHILDREN[idx2].POS);
		}
		node.CHILDREN = children;
		self.globalData.FLAT_TREE[childArray[idx].POS] = node;
		if(childArray[idx].CHILDREN.length > 0) {
			flatTree(childArray[idx].CHILDREN);
		} 
	}
	return;
};

