define(["knockout", "jquery"],
function(ko, $) { 	
	$(document).delegate(".topicList", "mouseenter", function(){
		$(this).children().children("#topicMenuActivator").show();
		$(this).children('#topicMenuActivatorDiv').show('fast');
	}).delegate(".topicList", "mouseleave", function(){
		$(this).children('#topicMenuActivatorDiv').hide('fast');
		$(this).children('#topicMenuActivatorDiv').children("#topicMenuActivator").hide();
		$(this).children('#topicMenuActivatorDiv').next().hide();
		$(this).children('#topicMenuActivatorDiv').children("#topicMenuActivator").removeClass("rotate2");
		$(this).children('#topicMenuActivatorDiv').children("#topicMenuActivator").addClass("rotate1");
	}).delegate("#topicMenuActivatorDiv", 'click', function(e){
		$(this).children("#topicMenuActivator").toggleClass("rotate1 rotate2");
	    $(this).next().toggle('blind');	    	    
	});
	
	var extend = new Array("scripts/modules/extenders/topic-view-time", "scripts/modules/extenders/topic-view-wordtype", "scripts/modules/extenders/topic-view-frame", "scripts/modules/extenders/topic-view-hierarchical");
	
	var self = {};
	self.scrollCallback = {};
	self.loadDocumentsForItem = {};
	self.loadDocumentsForItem.KEYWORDS = function (keywordId, context) { 
		var topicId = $(context.target).parents('.topic').attr('id').split('_')[1];
		keyword = globalData.Topic[topicId].ITEMS[self.textSelection().field][keywordId].ITEM_NAME;
		ko.postbox.publish('openNewTab', {moduleName:"document-browse-tab", tabHeading:"Topic " + topicId + " (" + keyword + ")", data: {topicId: topicId, term: keyword, getParam: "bestDocs&TopicId=" + topicId + "&term=" + keyword}});	
	};
	
	self.TextRepresentation = function(label, field) {
		this.label = label;
		this.field = field;
	};
	self.selectedTopics = ko.observableArray(globalData.TOPIC_SORTING).publishOn("selectedTopics");
	self.selectedTopics.subscribe(function(newValue){
		globalData.TOPIC_SORTING = newValue;
	});
	
	self.Topic = new Array(self.selectedTopics().length);
	for(i in globalData.Topic) {
		self.Topic[i] = new Object();
		self.Topic[i].TITLE_REPRESENTATION = new Object();
		self.Topic[i].INFO_HIGHLIGHT = new Object();
		self.Topic[i].INFO_HIGHLIGHT.KEYWORDS = "";
		self.Topic[i].TITLE_REPRESENTATION.KEYWORDS = globalData.Topic[i].ITEMS.KEYWORDS[globalData.Topic[i].SORTING.KEYWORDS()[0]].ITEM_NAME;
		for(var j = 1; j < Math.min(3, globalData.Topic[i].SORTING.KEYWORDS().length); j++) {
			self.Topic[i].TITLE_REPRESENTATION.KEYWORDS += ', ' + globalData.Topic[i].ITEMS.KEYWORDS[globalData.Topic[i].SORTING.KEYWORDS()[j]].ITEM_NAME;
		}
	}
	
	self.textSelectArray = ko.observableArray([new self.TextRepresentation('Keywords', 'KEYWORDS')]);
	self.textSelection = ko.observable(new self.TextRepresentation('Keywords', 'KEYWORDS'));
	self.textSelection.subscribe(function() {
		$('#topicMenuActivator').toggleClass("rotate1 rotate2");
		$('#topicMenu').toggle('blind');	  
		for(topicId in self.selectedTopics()) {
			$('#topic_' + topicId).children('div').children('.topicElementContent').scrollTop(0);
		}
	});
	
	self.loadDocumentsForTopic = function (topicId) { 
		ko.postbox.publish('openNewTab', {moduleName:"document-browse-tab", tabHeading:"Topic " + topicId, data: {topicId: topicId, getParam: "bestDocs&TopicId=" + topicId}});
	};   
	
	self.triggerResize = function() {
		$(window).trigger('resize');
		$(".topicList").scrollLeft(0);
		for(topicId in self.selectedTopics()) {
			$('#topic_' + topicId).children('div').children('.topicElementContent').scrollTop(0);
		}
	};
	
	self.windowHeight = ko.observable(Math.max(400, $(window).height(), /* For opera: */ document.documentElement.clientHeight)).subscribeTo(
	"windowHeight");
	
	self.topicListHeight = ko.computed (function() {
		return(self.windowHeight() - 154) * 0.3;
	});	
	
	self.topicListWidth  = ko.computed (function() {
		return self.selectedTopics().length * 213 ;
	});
	
	self.loading = ko.observable(false);
	
	self.checkScrollHeightForJumpStart = function(el) {
		$('#topic_' + el).children('.topicElementDiv').children('.topicElementContent').children(".wordList").children("li").children(".jumpToStart").css('top',($('#topic_' + el).children('.topicElementDiv').children('.topicElementContent').scrollTop() - 10) + 'px');
		if($('#topic_' + el).children('.topicElementDiv').children('.topicElementContent').scrollTop() > 1000) {
			$('#topic_' + el).children('.topicElementDiv').children('.topicElementContent').children(".wordList").children("li").children(".jumpToStart").show();
		} else {
			$('#topic_' + el).children('.topicElementDiv').children('.topicElementContent').children(".wordList").children("li").children(".jumpToStart").hide();
		}
	};
	
	self.scrollCallback['KEYWORDS'] = function(el) {
		self.checkScrollHeightForJumpStart(el);
		if(!self.loading() && !globalData.Topic[el].FULL.KEYWORDS() && $('#topic_' + el).children('.topicElementDiv').children('.topicElementContent').height() +  $('#topic_' + el).children('div').children('.topicElementContent').scrollTop() >=  $('#topic_' + el).children('div').children('.topicElementContent')[0].scrollHeight - 35) {
			self.loading(true);
			$.getJSON("JsonServlet?Command=getTerms&TopicId=" + el + "&offset=" + globalData.Topic[el].COUNT.KEYWORDS).success(function(receivedParsedJson) {
				var count = 0;
				keywordSorting = [];
				for(termId in receivedParsedJson.Topic[el].Top_Terms) {
					count++;	
					keywordSorting.push(receivedParsedJson.Topic[el].Top_Terms[termId].TermId);
					globalData.Topic[el].ITEMS[self.textSelection().field][receivedParsedJson.Topic[el].Top_Terms[termId].TermId] = {};
					globalData.Topic[el].ITEMS[self.textSelection().field][receivedParsedJson.Topic[el].Top_Terms[termId].TermId].ITEM_ID = receivedParsedJson.Topic[el].Top_Terms[termId].TermId;
					globalData.Topic[el].ITEMS[self.textSelection().field][receivedParsedJson.Topic[el].Top_Terms[termId].TermId].ITEM_NAME = receivedParsedJson.Term[receivedParsedJson.Topic[el].Top_Terms[termId].TermId].TERM_NAME;
					globalData.Topic[el].ITEMS[self.textSelection().field][receivedParsedJson.Topic[el].Top_Terms[termId].TermId].ITEM_COUNT = receivedParsedJson.Topic[el].Top_Terms[termId].relevance;			
				}
				
				if(count < 20) {
					globalData.Topic[el].FULL[self.textSelection().field](true);
				}
				globalData.Topic[el].COUNT[self.textSelection().field] += count;
				globalData.Topic[el].SORTING[self.textSelection().field](globalData.Topic[el].SORTING[self.textSelection().field]().concat(keywordSorting));

				self.loading(false);				
			});
		}
	};	
	
	self.getScrollPositionByValue = function (val) {		
		var scroll = val;
		var maxScrollPos = $('.topicPrevElCont').width() - $('.topicPrevSlider').width() - 8;
		var maxScroll = $('.topicList > ul').width() - $('.topicList').width();
		var position = Math.round(scroll*maxScrollPos/maxScroll);
		if(position < 0)
			position = 0;
		if(position > maxScrollPos)
			position = maxScrollPos;
		return position;
	};
	ko.postbox.subscribe("moveToTopic", function(topic) {
		var topic_id = 0;	
		if(!topic.currentTarget) {
			topic_id = topic;
		} else {
			topic_id = $(topic.currentTarget).attr('id').split("_")[1];
		}
		var offset = $(".topicList").width() / 2 - $("#topic_" + topic_id).width() / 2;
		$(".topicList").animate({
			scrollLeft : ($("#topic_" + topic_id).position().left - offset)
		}, {
			queue: false,
			duration : 2000,
			easing : "swing"
		});
		$(".topicPrevSlider").animate({
			left : self.getScrollPositionByValue(($("#topic_" + topic_id).position().left - offset))
		}, {
			queue: false,
			duration : 2000,
			easing : "swing"
		});	
		$("#topicMenuActivatorDiv, #topicMenu").css(
			'left', Math.min(Math.max(0, $("#topic_" + topic_id).position().left - offset), $('.topicList > ul').width() -  $('.topicList').width())
		);
	});
	
	for (var i=0;i<extend.length;i++) {
		var extender = require(extend[i]);
		extender(self);
	}
	
	return self;
});

