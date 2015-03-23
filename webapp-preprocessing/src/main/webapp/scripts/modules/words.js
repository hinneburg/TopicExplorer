define(["knockout", "jquery", "jquery.ui", "jquery.tbltree"],
function(ko, $) {
	var self = {};
	self.version = "Word Selector Version Alpha 0.1";
	self.loading = ko.observable(false);
	self.wordlistLabel = ko.observable("");
	
	globalData.checkedWordtypes = ko.observableArray([]);
	
	self.stopWords = ko.observableArray([]);
	self.activePos= ko.observable(0);
	self.changeStopWords = function(newValue) {
		if(newValue.SELECTED) {
			self.stopWords.push(newValue.TERM);
		} else {
			self.stopWords.remove(newValue.TERM);
		}
		return true;
	};
	
	globalData.changeSelected = function(newValue) {
		if(globalData.checkedWordtypes().indexOf(String(newValue.POS)) == -1) {
			self.markParent(globalData.FLAT_TREE[newValue.POS].PARENT, newValue.POS);
			self.markChildren(newValue.POS);
			self.openOverlay(newValue);
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
	
	self.openOverlay = function(data) {
		self.loading(true);
		self.activePos(data.POS);
		$('#overlay').show();
		self.wordlistLabel(data.LABEL);
		if(globalData.FLAT_TREE[data.POS].wordList == undefined) {
			globalData.FLAT_TREE[data.POS].wordList = ko.observableArray([]);
			$.getJSON("JsonServlet?Command=getWordlist&pos=" + data.POS).success(function(receivedParsedJson) {
				globalData.FLAT_TREE[data.POS].wordList(receivedParsedJson.TERM);
				
				for(wordIdx in globalData.FLAT_TREE[data.POS].wordList()) {
					globalData.FLAT_TREE[data.POS].wordList()[wordIdx].SELECTED = true;
				}
				
				$( "#slider" ).slider({
					range: true,
					min: 0,
					max: globalData.DOCUMENT_COUNT,
					values: [globalData.FLAT_TREE[data.POS].lowerBorder(), globalData.FLAT_TREE[data.POS].upperBorder()],
					slide: function(event, ui) {
						globalData.FLAT_TREE[data.POS].lowerBorder(ui.values[0]);
						globalData.FLAT_TREE[data.POS].upperBorder(ui.values[1]);
						for(wordIdx in globalData.FLAT_TREE[data.POS].wordList()) {
							if(globalData.FLAT_TREE[data.POS].wordList()[wordIdx].COUNT < ui.values[1] && globalData.FLAT_TREE[data.POS].wordList()[wordIdx].COUNT > ui.values[0]) {
								$('ul#wordList li:nth-child(' + (parseInt(wordIdx) + 1) + ')').css('color', 'black');
								$('ul#wordList li:nth-child(' +  (parseInt(wordIdx) + 1) + ') :nth-child(2)').prop('disabled', false);
							} else {
								$('ul#wordList li:nth-child(' +  (parseInt(wordIdx) + 1) + ')').css('color', 'lightgrey');
								$('ul#wordList li:nth-child(' +  (parseInt(wordIdx) + 1) + ') :nth-child(2)').prop('disabled', true);
							}
						}
					}
				});
				self.loading(false);
			});	
		} else {
			self.loading(false);
			$( "#slider" ).slider("values", [globalData.FLAT_TREE[data.POS].lowerBorder(), globalData.FLAT_TREE[data.POS].upperBorder()]);
			for(wordIdx in globalData.FLAT_TREE[data.POS].wordList()) {
				if(globalData.FLAT_TREE[data.POS].wordList()[wordIdx].COUNT >= globalData.FLAT_TREE[data.POS].upperBorder() || globalData.FLAT_TREE[data.POS].wordList()[wordIdx].COUNT <= globalData.FLAT_TREE[data.POS].lowerBorder()) {
					$('ul#wordList li:nth-child(' +  (parseInt(wordIdx) + 1) + ')').css('color', 'lightgrey');
					$('ul#wordList li:nth-child(' +  (parseInt(wordIdx) + 1) + ') :nth-child(2)').prop('disabled', true);
				}
			}
		}
	}; 
	self.closeOverlay = function() {
		$('.overlay').hide();
	}; 
	
	self.goOn = function() {
		if(globalData.checkedWordtypes().length > 0) {
			$('#overlay2').show();
		} else {
			alert('you have to select at least one wordtype');
		}		
	}
	self.goToFrames = function() {
		globalData.selectedFrames = ko.observableArray([]);
		globalData.module('frames');
	}
	self.topicCount = ko.observable(parseInt(globalData.DOCUMENT_COUNT / 1000) * 10);
	return self;
});

