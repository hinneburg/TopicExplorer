define(["knockout", "jquery", "jquery.ui", "jquery.tbltree"],
function(ko, $) {
	var self = {};
	self.version = "NLP Configurator Version Alpha 0.1";
	self.wordList = ko.observableArray([]);
	self.loading = ko.observable(false);
	self.wordlistLabel = ko.observable("");
	
	globalData.checkedWordtypes = ko.observableArray([]);
	
	globalData.changeSelected = function(newValue) {
		if(globalData.checkedWordtypes().indexOf(String(newValue.POS)) == -1) {
			self.markParent(globalData.FLAT_TREE[newValue.POS].PARENT, newValue.POS);
			self.markChildren(newValue.POS);
		} else {
			self.demarkParent(newValue.POS);
			self.demarkChildren(newValue.POS);
		}
		return true;
	};
	
	self.markParent = function(parentPos, newPos) {
		if(parentPos == -1) return;
		for(childIdx in globalData.FLAT_TREE[parentPos].CHILDREN) {
			if(globalData.checkedWordtypes().indexOf(String(globalData.FLAT_TREE[parentPos].CHILDREN[childIdx])) == -1 && globalData.FLAT_TREE[parentPos].CHILDREN[childIdx] != newPos) {
				return;
			
			}
		}
		globalData.checkedWordtypes().push(String(parentPos));
		self.markParent(globalData.FLAT_TREE[parentPos].PARENT, parentPos);
	};
	
	self.markChildren = function(newPos) {
		for(childIdx in globalData.FLAT_TREE[newPos].CHILDREN) {
			globalData.checkedWordtypes().push(String(globalData.FLAT_TREE[newPos].CHILDREN[childIdx]));
			self.markChildren(globalData.FLAT_TREE[newPos].CHILDREN[childIdx]);
		}
		return;
	};
	
	self.demarkParent = function(parentPos) {
		if(parentPos == -1) return;
		globalData.checkedWordtypes.remove(String(parentPos));
		self.demarkParent(globalData.FLAT_TREE[parentPos].PARENT);
		return;
	};
	
	self.demarkChildren = function(newPos) {
		for(childIdx in globalData.FLAT_TREE[newPos].CHILDREN) {
			globalData.checkedWordtypes.remove(String(globalData.FLAT_TREE[newPos].CHILDREN[childIdx]));
			self.demarkChildren(globalData.FLAT_TREE[newPos].CHILDREN[childIdx]);
		}
		return;
	};
	
	globalData.openOverlay = function(data) {
		self.loading(true);
		$('#overlay').show();
		self.wordlistLabel(data.LABEL);
		$.getJSON("JsonServlet?Command=getWordlist&low=" + data.LOW + "&high=" + data.HIGH).success(function(receivedParsedJson) {
			self.wordList(receivedParsedJson.TOKEN);
			self.loading(false);
		});	
	}; 
	self.closeOverlay = function() {
		$('#overlay').hide();
	}; 
	return self;
});

