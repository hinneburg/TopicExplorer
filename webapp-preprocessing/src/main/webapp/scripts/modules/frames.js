define(["knockout", "jquery", "jquery.ui", "jquery.tbltree"],
function(ko, $) {
	var self = {};
	self.version = "Frame Selector Version Alpha 0.1";
	self.frameCount = ko.observable(1);
	self.wordtypes = ko.observableArray([]);
	self.Frames = function(firstWordtype, lastWordtype, signsBetween) {
		this.firstWordtype = firstWordtype;
		this.lastWordtype = lastWordtype;
		this.signsBetween = signsBetween;
	};
	
	self.frameDelimiters = ko.observableArray(['。', '？', '?', '！', '!', '．．', '..', '・・', '．．．', '...', '…', '・・・']);
	self.newFrameDelimiter = ko.observable('');
	for(node in globalData.FLAT_TREE) {
		globalData.FLAT_TREE[node].id = node;
		self.wordtypes.push(globalData.FLAT_TREE[node]);
	}
	self.addFrame = function() {
		if($('#signsBetween').val().length == 0) {
			alert('insert a valid number into "signs between" field');
			$('#signsBetween').val("");
			$('#signsBetween').focus();
		} else {
			globalData.selectedFrames.push(new self.Frames($('#selectFirstWordtype').val(), $('#selectLastWordtype').val(), $('#signsBetween').val()));
			var firstIndex;
			for(var wordtypeIdx in globalData.WORDTYPE_WORDCOUNTS) {
				if(globalData.checkedWordtypes.indexOf(globalData.WORDTYPE_WORDCOUNTS[wordtypeIdx].POS) > -1) {
					firstIndex = globalData.WORDTYPE_WORDCOUNTS[wordtypeIdx].POS;
					break;
				}
			}
			
			$('#signsBetween').val("");
			$('#selectFirstWordtype').val(firstIndex);
			$('#selectLastWordtype').val(firstIndex);
		}
	};
	self.editFrame = function(index) {
		$('#signsBetween').val(globalData.selectedFrames()[index].signsBetween);
		$('#selectFirstWordtype').val(globalData.selectedFrames()[index].firstWordtype).focus();
		$('#selectLastWordtype').val(globalData.selectedFrames()[index].lastWordtype);
		
		globalData.selectedFrames.splice(index, 1);
	};
	
	self.removeFrame = function(index) {
		globalData.selectedFrames.splice(index, 1);
	};
	
	self.addFrameDelimiter = function() {
		if(self.newFrameDelimiter().length > 0) {
			self.frameDelimiters.push(self.newFrameDelimiter());
			self.newFrameDelimiter('');
		} else {
			alert("no new frame delimter specified");
			$('#newFrameDelimiter').focus();		
		}
	}
	
	self.removeFrameDelimiter = function(toRemove, elem) {
		self.frameDelimiters.splice(self.frameDelimiters.indexOf($(elem.currentTarget).text()), 1);		
	}
	self.back = function() {
		globalData.module('words');
	};
	
	self.done = function() {
		alert('done');
	};
	
	return self;
});