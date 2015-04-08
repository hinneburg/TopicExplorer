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
	self.globalData.module = ko.observable('words');
	self.globalData.FLAT_TREE =  {};
	flatTree(self.globalData.WORDTYPE_WORDCOUNTS, ko, 0);
	
	$(document).tooltip({ track: true });
	
	ko.bindingHandlers.module.baseDir = "scripts/modules";
	
	ko.applyBindings(new function() {} ());
});

function flatTree(childArray, ko, depth) {
	for(idx in childArray) {
		node = {}
		node.PARENT = childArray[idx].PARENT;
		node.LABEL = childArray[idx].LABEL;
		node.TERM_COUNT = childArray[idx].TERM_COUNT;
		node.PARENTS_CHILDREN = [];
		
		children = [];
		for(idx2 in childArray[idx].CHILDREN) {
			children.push(childArray[idx].CHILDREN[idx2].POS);
		}
		
		node.HIERARCHICAL_LABEL = "";
		for(var i = 0; i < depth; i++) {
			node.HIERARCHICAL_LABEL += "&nbsp;&nbsp;";
		}
		node.HIERARCHICAL_LABEL += childArray[idx].LABEL;
		node.CHILDREN = children;
		node.lowerBorder = ko.observable(0);
		node.upperBorder = ko.observable(self.globalData.DOCUMENT_COUNT);
		
		node.stopwords = ko.observableArray([]);
		self.globalData.FLAT_TREE[childArray[idx].POS] = node;
		if(childArray[idx].CHILDREN.length > 0) {
			flatTree(childArray[idx].CHILDREN, ko, depth + 1);
		} 
	}
	return;
};

