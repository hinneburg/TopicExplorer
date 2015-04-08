define(["knockout", "jquery", "jquery.ui"],
function(ko, $) {
	var self = {};
	
	self.markParents = function(wordType) {
		if(globalData.FLAT_TREE[wordType].PARENT == -1) {
			return;
		}
		if(globalData.checkedWordtypes().indexOf(String(globalData.FLAT_TREE[wordType].PARENT)) == -1) {
			globalData.checkedWordtypes.push(String(globalData.FLAT_TREE[wordType].PARENT));
		}	
		self.markParents(globalData.FLAT_TREE[wordType].PARENT);
	}
	
	self.statusMessage = ko.observable(""); 
	for(wordTypeIdx in globalData.checkedWordtypes()) {
		self.markParents(globalData.checkedWordtypes()[wordTypeIdx]);
	}
	self.version = "Frame Selector Version Alpha 0.1";
	self.frameCount = ko.observable(1);
	self.wordtypes = ko.observableArray([]);
	self.Frames = function(firstWordtype, lastWordtype, signsBetween, firstWordtypeLimit, lastWordtypeLimit) {
		this.firstWordtype = firstWordtype;
		this.lastWordtype = lastWordtype;
		this.signsBetween = signsBetween;
		this.firstWordtypeLimit = firstWordtypeLimit;
		this.lastWordtypeLimit = lastWordtypeLimit;
	};
	
	self.frameDelimiters = ko.observableArray(['。', '？', '?', '！', '!', '．．', '..', '・・', '．．．', '...', '…', '・・・']);
	self.newFrameDelimiter = ko.observable('');
	for(node in globalData.FLAT_TREE) {
		globalData.FLAT_TREE[node].id = node;
		self.wordtypes.push(globalData.FLAT_TREE[node]);
	}
	self.addFrame = function() {
		if($('#firstWordtypeLimit').val().length == 0) {
			alert('insert a valid number into "best x of start wordtype" field');
			$('#firstWordtypeLimit').val("");
			$('#firstWordtypeLimit').focus();
		} else if($('#signsBetween').val().length == 0) {
			alert('insert a valid number into "signs between" field');
			$('#signsBetween').val("");
			$('#signsBetween').focus();
		} else if($('#lastWordtypeLimit').val().length == 0) {
			alert('insert a valid number into "best x of end wordtype" field');
			$('#lastWordtypeLimit').val("");
			$('#lastWordtypeLimit').focus();
		} else {
			globalData.selectedFrames.push(new self.Frames($('#selectFirstWordtype').val(), $('#selectLastWordtype').val(), $('#signsBetween').val(), $('#firstWordtypeLimit').val(), $('#lastWordtypeLimit').val()));
			var firstIndex;
			for(var wordtypeIdx in globalData.WORDTYPE_WORDCOUNTS) {
				if(globalData.checkedWordtypes.indexOf(String(globalData.WORDTYPE_WORDCOUNTS[wordtypeIdx].POS)) > -1) {
					firstIndex = globalData.WORDTYPE_WORDCOUNTS[wordtypeIdx].POS;
					break;
				}
			}
			
			$('#signsBetween').val("");
			$('#firstWordtypeLimit').val("");
			$('#lastWordtypeLimit').val("");
			$('#selectFirstWordtype').val(firstIndex);
			$('#selectLastWordtype').val(firstIndex);
		}
	};
	self.editFrame = function(index) {
		$('#signsBetween').val(globalData.selectedFrames()[index].signsBetween);
		$('#selectFirstWordtype').val(globalData.selectedFrames()[index].firstWordtype);
		$('#selectLastWordtype').val(globalData.selectedFrames()[index].lastWordtype);
		$('#signsBetween').val(globalData.selectedFrames()[index].signsBetween);
		$('#selectFirstWordtypeLimit').val(globalData.selectedFrames()[index].firstWordtypeLimit).focus();
		$('#selectLastWordtypeLimit').val(globalData.selectedFrames()[index].lastWordtypeLimit);
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
		var go = true;
		if(globalData.selectedFrames().length < 1) {
			go = confirm("No frames selected... Are you sure to continue?");
		}
		if(go) {
			$('#overlay').show();
			self.statusMessage("writing frame data to property file...");
			$.getJSON("JsonServlet?Command=specifyFrames&frames=" + JSON.stringify(globalData.selectedFrames()) + "&frameDelimiters=" + JSON.stringify(self.frameDelimiters())).success(function(receivedParsedJson) {
				globalData.module('success');
			});
		} 
	};
	
	return self;
});