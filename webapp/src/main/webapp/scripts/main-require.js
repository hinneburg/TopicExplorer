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
	var timeoutId = null;
	var self=this;
	ko.bindingHandlers.module.baseDir = "modules";
	
	// global delegates
	$(document).delegate(".menuActivator", 'click', function(e){
		if($(this).next().height() + 26 > $(this).parent().height()) {
	    	$(this).next().css('max-height', $(this).parent().height() - 26);
	    }
		
		$(this).toggleClass("rotate1 rotate2");
	    $(this).next().toggle('blind');
	    	    
	}).delegate(".documentList li", 'mouseover', function(){
		$(this).addClass('myHover').children(".docButtons").show();
	}).delegate(".documentList li", 'mouseout', function(){
		$(this).removeClass('myHover').children(".docButtons").hide();
	}).delegate(".documentList circle", "mouseover", function(){
		$(this).attr("r", "7");
	}).delegate(".documentList circle", "mouseout", function(){
		$(this).attr("r", "5");
	}).delegate(".ui-menu-item circle", "mouseover", function(el){
		
		if (!timeoutId) {
	        timeoutId = window.setTimeout(function() {
	            timeoutId = null; // EDIT: added this line
	            moveToTopic(el);
		    }, 1500);
		}
		$(this).attr("r", "7");
	}).delegate(".ui-menu-item circle", "mouseout", function(){
		if (timeoutId) {
		    window.clearTimeout(timeoutId);
		    timeoutId = null;
		}
		$(this).attr("r", "5");
	}).delegate("#groupG rect", "mouseover", function(){
		$(this).attr("height", "17");
		$(this).attr("y", "0");
	}).delegate("#groupG rect", "mouseout", function(){
		$(this).attr("height", "13");
		$(this).attr("y", "2");
	}).delegate("#desktop, .topicList", "mouseenter", function(){
		$(this).children(":first").show();
	}).delegate("#desktop, .topicList", "mouseleave", function(){
		$(this).children(":first").hide();
		$(this).children(":first").next().hide();
		$(this).children(":first").removeClass("rotate2");
		$(this).children(":first").addClass("rotate1");
	}).bind("#desktop", "scroll",function(){
		alert('hallo');
	}).delegate(".documentList circle, #groupG rect", "click", moveToTopic);

	self.minHeight = 400;
	self.minWidth = 800;

	$(window).resize(function() {	
		ko.postbox.publish("windowWidth",Math.max(self.minWidth, $(window).width(), /* For opera: */ document.documentElement.clientWidth));
		ko.postbox.publish("windowHeight",Math.max(self.minHeight, $(window).height(), /* For opera: */ document.documentElement.clientHeight));
		setTopicSlider();
	});
	
	setTimeout(function() {
		ko.applyBindings(new App());
	}, 0);

	setTimeout(function() {
		ko.postbox.publish("windowWidth",Math.max(self.minWidth, $(window).width(), /* For opera: */ document.documentElement.clientWidth));
		ko.postbox.publish("windowHeight",Math.max(self.minHeight, $(window).height(), /* For opera: */ document.documentElement.clientHeight));
		setTopicSlider();
	}, 650);
	
});

function setTopicSlider() {	
	var slider = $('.topicPrevSlider');		
	var maxListWidth = $('.topicList > ul').width();
	var topicDivWidth = $('.topicList').width();
	var bottomDivWidth = $('.topicBottomSliderDiv').width();
	
	slider.width((topicDivWidth*bottomDivWidth)/ maxListWidth);
	
	slider.children('rect').attr('width', (topicDivWidth*bottomDivWidth)/ maxListWidth);
	slider.children('polygon').attr('points', function(){
		var p1={},p2={},p3={};
		p1.x = ~~slider.children('rect').attr('width')/2+~~slider.children('rect').attr('x');
		p1.y = ~~slider.children('rect').attr('height')-10;
		p2.x = p1.x-5;
		p2.y = p1.y+10;
		p3.x = p1.x+5;
		p3.y = p2.y;
		return p1.x+","+p1.y+" "+p2.x+","+p2.y+" "+p3.x+","+p3.y+" ";
	});		
	var maxScrollPos = $('.topicBottomSliderDiv').width() - $('.topicPrevSlider').width();	
	$( ".topicPrevSlider" ).draggable({ axis: "x", containment: [ 0, 0, maxScrollPos, 0 ]});
	$( ".topicPrevSlider" ).on( "drag", function( event, ui ) {		
		var maxScroll = $('.topicList > ul').width() - $('.topicList').width();			
		var scroll = (ui.position.left/maxScrollPos)*maxScroll;
		$('.topicList').scrollLeft(scroll);	
		$('#topicMenu, .topicList > img').css('left', scroll);		
	});
};

function makeMenu(el) {
	$(el).menu({
		select : function(event, ui) {
			$(this).parent().parent().prev()
				.toggleClass("rotate1 rotate2");
			$(this).parent().parent().toggle('blind');		
//			resizeDocumentDivs();
		}
	});
};

function getScrollPositionByValue(val) {	
	var offset = 3;	
	var scroll = val;
	var maxScrollPos = $('.topicBottomSliderDiv').width() - $('.topicPrevSlider').width();
	var maxScroll = $('.topicList > ul').width() - $('.topicList').width();
	var position = Math.round(scroll*maxScrollPos/maxScroll)+offset;	
	if(position < 0)
		position = 0;
	if(position > maxScrollPos)
		position = maxScrollPos;
	return position;
};

function moveToTopic(self) {
	
	var topic_id = 0;	
	if(!self.currentTarget) {
		topic_id = self;
	} else {
		topic_id = $(self.currentTarget).attr('id').split("_")[1];
	}
	var offset = $(".topicList").width() / 2 - $("#topic" + topic_id).width() / 2;
	
	$(".topicList").animate({
		scrollLeft : ($("#topic" + topic_id).position().left - offset)
	}, {
		duration : 2000,
		easing : "swing"
	});
	$(".topicPrevSlider").animate({
		left : getScrollPositionByValue(($("#topic" + topic_id).position().left - offset))
	}, {
		duration : 2000,
		easing : "swing"
	});	
	setTimeout(function() { 
		$(".topicList > img, #topicMenu").css('left', $(".topicList").scrollLeft());	
	}, 2000);
};


