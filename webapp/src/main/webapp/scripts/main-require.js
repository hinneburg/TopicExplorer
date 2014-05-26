require.config({
	baseUrl : "./",
	waitSeconds: 15,
	paths : {
		"knockout" : "scripts/lib/knockout-3.1.0",
		"knockout-amd-helpers" : "scripts/lib/knockout-amd-helpers",
		"knockout-postbox" : "scripts/lib/knockout-postbox",
		"text" : "scripts/lib/text",
		"jquery" : "scripts/lib/jquery-1.9.1.min",
		"jquery-ui" : "scripts/lib/jquery-ui-1.10.3.custom.min",
		"highstock" : "scripts/lib/highstock"
			
	}
});

require([ "knockout","jquery", "scripts/modules/topicexplorer-view-model",
		"knockout-amd-helpers", "knockout-postbox",
		"text", "jquery-ui" ], function(ko, $, App) {
	var timeoutId = null;
	var self=this;
	ko.bindingHandlers.module.baseDir = "scripts/modules";

	// global delegates
	$(document).delegate(".menuActivator", 'click', function(e){
		if($(this).next().height() + 26 > $(this).parent().height()) {
	    	$(this).next().css('max-height', $(this).parent().height() - 26);
	    }
		
		$(this).toggleClass("rotate1 rotate2");
	    $(this).next().toggle('blind');
	    	    
	}).delegate("#invisbleTabs > span", 'click', function() {
		$('#tabMenu').toggle('blind');
	}).delegate("a.ui-corner-all", 'mouseover', function() {
		$(this).addClass('ui-state-focus');
	}).delegate("a.ui-corner-all", 'mouseout', function() {
		$(this).removeClass('ui-state-focus');
	}).delegate(".tab", 'mouseover', function() {
		$(this).children('img').show();
	}).delegate(".tab", 'mouseout', function() {
		$(this).children('img').hide();
	}).delegate(".documentList li", 'mouseenter', function(){
		$(this).addClass('myHover').find(".docButtons").show();
	}).delegate(".documentList li", 'mouseleave', function(){
		$(this).removeClass('myHover').find(".docButtons").hide();
	}).delegate(".documentList circle", "mouseover", function(){
		$(this).attr("r", "7");
	}).delegate(".documentList circle", "mouseout", function(){
		$(this).attr("r", "5");
	}).delegate(".ui-menu-item circle, .topicCheckbox", "mouseover", function(){
		var self = this;
		if (!timeoutId) {
	        timeoutId = window.setTimeout(function() {
	            timeoutId = null; 
	            moveToTopic($(self).attr('id').split('_')[1]);
		    }, 1500);
		}
		$(self).attr("r", "7");
	}).delegate(".ui-menu-item circle, .topicCheckbox", "mouseout", function(){
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
	}).delegate(".topicList", "mouseenter", function(){
		$(this).children(":first").show();
	}).delegate("#desktop", "mouseenter", function(){
		$(this).children(":first").show();
		if($("#desktop").scrollTop() > 1000) {
			$("#jumpToStart").show();
		}
	}).delegate("#desktop, .topicList", "mouseleave", function(){
		$(this).children(":first").hide();
		$(this).children(":first").next().hide();
		$(this).children(":first").removeClass("rotate2");
		$(this).children(":first").addClass("rotate1");
		$("#jumpToStart").hide();
	}).delegate("#jumpToStart", "click", function() {
		$("#desktop").animate({
			scrollTop: 0
		});
	}).delegate(".documentList circle, #groupG rect", "click", moveToTopic);
	
	$(document).tooltip();
	
	self.minHeight = 400;
	self.minWidth = 800;

	$(window).resize(function() {	
		ko.postbox.publish("windowWidth",Math.max(self.minWidth, $(window).width(), /* For opera: */ document.documentElement.clientWidth));
		ko.postbox.publish("windowHeight",Math.max(self.minHeight, $(window).height(), /* For opera: */ document.documentElement.clientHeight));
	});
	
	setTimeout(function() {
		ko.applyBindings(new App());
	}, 0);

	setTimeout(function() {
		$(window).trigger('resize');
	}, 3000);


});





function makeMenu(el) {
	$(el).menu({
		select : function(event, ui) {
			$(this).parent().parent().prev()
				.toggleClass("rotate1 rotate2");
			$(this).parent().parent().toggle('blind');		
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


