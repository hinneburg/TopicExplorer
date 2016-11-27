define(["knockout", "jquery", "jquery.ui", "jquery.tbltree"],
function(ko, $) {
	var self = {};
	self.version = "Word Selector Version Alpha 0.1";
	self.loading = ko.observable(false);
	self.wordlistLabel = ko.observable("");
	
	globalData.checkedWordtypes = ko.observableArray([]);
	
	self.activePos= ko.observable(0);
	self.changeStopWords = function(newValue) {
		if(globalData.FLAT_TREE[self.activePos()].wordList()[newValue].SELECTED) {
			globalData.FLAT_TREE[self.activePos()].stopwords.push(globalData.FLAT_TREE[self.activePos()].wordList()[newValue].TERM);
			self.addToChildrensStopwordlist(self.activePos(), globalData.FLAT_TREE[self.activePos()].wordList()[newValue].TERM);
		} else {
			globalData.FLAT_TREE[self.activePos()].stopwords.remove(globalData.FLAT_TREE[self.activePos()].wordList()[newValue].TERM);
			self.removeFromChildrensStopwordlist(self.activePos(), globalData.FLAT_TREE[self.activePos()].wordList()[newValue].TERM);
			self.removeFromParentsStopwordlist(self.activePos(), globalData.FLAT_TREE[self.activePos()].wordList()[newValue].TERM);
		}
		return true;
	};
	
	self.addToChildrensStopwordlist = function(pos, term) {
		for(childPos in globalData.FLAT_TREE[pos].CHILDREN) {
			globalData.FLAT_TREE[globalData.FLAT_TREE[pos].CHILDREN[childPos]].stopwords.push(term);
			if(typeof globalData.FLAT_TREE[globalData.FLAT_TREE[pos].CHILDREN[childPos]].wordList == 'function') {
				for(wordIdx in globalData.FLAT_TREE[globalData.FLAT_TREE[pos].CHILDREN[childPos]].wordList()) {
					if(globalData.FLAT_TREE[globalData.FLAT_TREE[pos].CHILDREN[childPos]].wordList()[wordIdx].TERM == term) {
						globalData.FLAT_TREE[globalData.FLAT_TREE[pos].CHILDREN[childPos]].wordList()[wordIdx].SELECTED = false;
					}
				}
			}
			self.addToChildrensStopwordlist(globalData.FLAT_TREE[pos].CHILDREN[childPos], term);
		}
	};
	
	self.removeFromChildrensStopwordlist = function(pos, term) {
		for(childPos in globalData.FLAT_TREE[pos].CHILDREN) {
			globalData.FLAT_TREE[globalData.FLAT_TREE[pos].CHILDREN[childPos]].stopwords.remove(term);
			if(typeof globalData.FLAT_TREE[globalData.FLAT_TREE[pos].CHILDREN[childPos]].wordList == 'function') {
				for(wordIdx in globalData.FLAT_TREE[globalData.FLAT_TREE[pos].CHILDREN[childPos]].wordList()) {
					if(globalData.FLAT_TREE[globalData.FLAT_TREE[pos].CHILDREN[childPos]].wordList()[wordIdx].TERM == term) {
						globalData.FLAT_TREE[globalData.FLAT_TREE[pos].CHILDREN[childPos]].wordList()[wordIdx].SELECTED = true;
					}
				}
			}
			self.removeFromChildrensStopwordlist(globalData.FLAT_TREE[pos].CHILDREN[childPos], term);
		}
	};
	
	self.removeFromParentsStopwordlist = function(pos, term) {
		if(globalData.FLAT_TREE[pos].PARENT > -1) {
			globalData.FLAT_TREE[globalData.FLAT_TREE[pos].PARENT].stopwords.remove(term);
			if(typeof globalData.FLAT_TREE[globalData.FLAT_TREE[pos].PARENT].wordList == 'function') {
				for(wordIdx in globalData.FLAT_TREE[globalData.FLAT_TREE[pos].PARENT].wordList()) {
					if(globalData.FLAT_TREE[globalData.FLAT_TREE[pos].PARENT].wordList()[wordIdx].TERM == term) {
						globalData.FLAT_TREE[globalData.FLAT_TREE[pos].PARENT].wordList()[wordIdx].SELECTED = true;
					}
				}
			}
			self.removeFromParentsStopwordlist(globalData.FLAT_TREE[pos].PARENT, term);
		}
	}
	
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
	
	globalData.editSelected = function(newValue) {
		self.openOverlay(newValue);
	}
	
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
	
	self.giveBordersToChildren = function(pos) {
		for(childIdx in globalData.FLAT_TREE[pos].CHILDREN) {
			globalData.FLAT_TREE[globalData.FLAT_TREE[pos].CHILDREN[childIdx]].lowerBorder(globalData.FLAT_TREE[pos].lowerBorder());
			globalData.FLAT_TREE[globalData.FLAT_TREE[pos].CHILDREN[childIdx]].upperBorder(globalData.FLAT_TREE[pos].upperBorder());
			self.giveBordersToChildren(globalData.FLAT_TREE[pos].CHILDREN[childIdx]);
		}
		return;
	}
	
	self.giveBordersToParents = function(pos) {
		if(globalData.FLAT_TREE[pos].PARENT == -1) return;
		if(globalData.FLAT_TREE[globalData.FLAT_TREE[pos].PARENT].CHILDREN.length > 1) {
			globalData.FLAT_TREE[globalData.FLAT_TREE[pos].PARENT].lowerBorder(0);
			globalData.FLAT_TREE[globalData.FLAT_TREE[pos].PARENT].upperBorder(globalData.DOCUMENT_COUNT);
		} else {
			globalData.FLAT_TREE[globalData.FLAT_TREE[pos].PARENT].lowerBorder(globalData.FLAT_TREE[pos].lowerBorder());
			globalData.FLAT_TREE[globalData.FLAT_TREE[pos].PARENT].upperBorder(globalData.FLAT_TREE[pos].upperBorder());
		}
		self.giveBordersToParents(globalData.FLAT_TREE[pos].PARENT);
	} 
	
	self.renderedWordList = ko.observableArray([]);
	
	self.openOverlay = function(data) {
		self.loading(true);
		self.activePos(data.POS);
		$('#overlay').show();
		self.wordlistLabel(data.LABEL);
		if(globalData.FLAT_TREE[data.POS].wordList == undefined) {
			globalData.FLAT_TREE[data.POS].wordList = ko.observableArray([]);
			
			$.getJSON("JsonServlet?Command=getWordlist&pos=" + data.POS).success(function(receivedParsedJson) {
				globalData.FLAT_TREE[data.POS].wordList(receivedParsedJson.TERM);
				
				
				var tempWordList = [];
				var maxIndex = Math.min(50, receivedParsedJson.TERM.length);
				for(var i = 0; i < maxIndex; i++) {
					tempWordList.push(i);
				}
				
				for(wordIdx in globalData.FLAT_TREE[data.POS].wordList()) {
					if(globalData.FLAT_TREE[data.POS].stopwords().indexOf(globalData.FLAT_TREE[data.POS].wordList()[wordIdx].TERM) < 0) {
						globalData.FLAT_TREE[data.POS].wordList()[wordIdx].SELECTED = true;
					} else {
						globalData.FLAT_TREE[data.POS].wordList()[wordIdx].SELECTED = false;
					}
				}
				
				self.renderedWordList(tempWordList.slice());
				
				$( "#slider" ).slider({
					range: true,
					min: 0,
					max: globalData.DOCUMENT_COUNT,
					values: [globalData.FLAT_TREE[data.POS].lowerBorder(), globalData.FLAT_TREE[data.POS].upperBorder()],
					slide: function(event, ui) {
						globalData.FLAT_TREE[data.POS].lowerBorder(ui.values[0]);
						globalData.FLAT_TREE[data.POS].upperBorder(ui.values[1]);
						self.giveBordersToChildren(data.POS);
						self.giveBordersToParents(data.POS);
					}
				});
				self.loading(false);
			});	
		} else {
			self.loading(false);
			$( "#slider" ).slider("values", [globalData.FLAT_TREE[data.POS].lowerBorder(), globalData.FLAT_TREE[data.POS].upperBorder()]);
			for(wordIdx in globalData.FLAT_TREE[data.POS].wordList()) {
				if(globalData.FLAT_TREE[data.POS].wordList()[wordIdx].COUNT > globalData.FLAT_TREE[data.POS].upperBorder() || globalData.FLAT_TREE[data.POS].wordList()[wordIdx].COUNT <= globalData.FLAT_TREE[data.POS].lowerBorder()) {
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
	};
	
	self.statusMessage = ko.observable(""); 
	self.goToFrames = function() {
		globalData.selectedFrames = ko.observableArray([]);
		$('#overlay2').hide();
		$('#overlay3').show();
		var wordtypes = [];
		for(var id in globalData.checkedWordtypes()) {
			wordtype = {};
			wordtype.id = globalData.checkedWordtypes()[id];
			wordtype.upperBorder = globalData.FLAT_TREE[globalData.checkedWordtypes()[id]].upperBorder();
			wordtype.lowerBorder = globalData.FLAT_TREE[globalData.checkedWordtypes()[id]].lowerBorder();
			wordtype.stopWords = globalData.FLAT_TREE[globalData.checkedWordtypes()[id]].stopwords();
			wordtypes.push(wordtype);
		}
		self.statusMessage("writing topic count to property file...");
		$.getJSON("JsonServlet?Command=specifyTopicCount&topicCount=" + self.topicCount()).success(function(receivedParsedJson) {
			self.statusMessage("prepare json for generating csv...");
			$.post("JsonServlet?Command=generateCSV", {wordList: JSON.stringify(wordtypes)}, function(receivedParsedJson, status) {
				globalData.module('frames');
			});
		});
		
		
	};
	var lastScrollTop = 0;
	self.wordListScroll = function(bla, evt) {
		temp = self.renderedWordList().slice();
		if($("#wordlistDiv").scrollTop() + $("#wordlistDiv").height() + 90 >= $("#wordlistDiv")[0].scrollHeight && lastScrollTop <= $("#wordlistDiv").scrollTop() && temp[temp.length - 1] + 1 < globalData.FLAT_TREE[self.activePos()].TERM_COUNT) {
			var addCount = Math.min(10, globalData.FLAT_TREE[self.activePos()].TERM_COUNT - temp[temp.length - 1] - 1); 
			
			for(var i = 0; i < addCount; i++) {
				temp.splice(temp.length, 0, temp[temp.length - 1] + 1);
			}
			self.renderedWordList(temp);
			
			var firstHeight = $("#wordlistDiv")[0].scrollHeight;
			
			temp.splice(0,addCount);
			self.renderedWordList(temp);
			
			$("#wordlistDiv").scrollTop($("#wordlistDiv").scrollTop() - ((firstHeight - $("#wordlistDiv")[0].scrollHeight)));			
		} else if($("#wordlistDiv").scrollTop() < 10 && lastScrollTop > $("#wordlistDiv").scrollTop() && temp[0] >= 0) { 
			var addCount = Math.min(10, temp[0]);
			
			for(var i = 0; i < addCount; i++) {
				temp.splice(0, 0, temp[0] - 1);
			}
			self.renderedWordList(temp);
			
			var firstHeight = $("#wordlistDiv")[0].scrollHeight;
			
			temp.splice(temp.length - addCount, addCount);
			self.renderedWordList(temp);
			
			$("#wordlistDiv").scrollTop($("#wordlistDiv").scrollTop() + ((firstHeight - $("#wordlistDiv")[0].scrollHeight)));
		}
		lastScrollTop = $("#wordlistDiv").scrollTop();
	};
	
	self.isChecked = function(wordIndex) {
		return ko.dependentObservable(function () {
			if(globalData.FLAT_TREE[self.activePos()].wordList == undefined) return false;
			if(globalData.FLAT_TREE[self.activePos()].wordList()[wordIndex] == undefined) return false;
			return !(globalData.FLAT_TREE[self.activePos()].wordList()[wordIndex].COUNT > globalData.FLAT_TREE[self.activePos()].lowerBorder() && globalData.FLAT_TREE[self.activePos()].wordList()[wordIndex].COUNT <= globalData.FLAT_TREE[self.activePos()].upperBorder());
		}, this);
	}
	
	self.topicCount = ko.observable(parseInt(globalData.DOCUMENT_COUNT / 1000) * 10);
	return self;
});

