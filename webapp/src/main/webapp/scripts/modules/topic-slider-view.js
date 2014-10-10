define(["knockout", "jquery"],
function(ko, $) { 
	$(document).delegate(".topicRect", "mouseover", function(){
		$(this).css("height", "17px");
		$(this).css("margin-top", "-1px");
	}).delegate(".topicRect", "mouseout", function(){
		$(this).css("height", "15px");
		$(this).css("margin-top", "0px");
	});
	
	var self = {};
	self.windowWidth = ko.observable(1024).subscribeTo("windowWidth");
	self.moveToTopic = function(topic) {
		ko.postbox.publish("moveToTopic", topic);
	};
	
	
	self.setTopicSlider = function() {	
		var slider = $('.topicPrevSlider');	
		var maxListWidth = $('.topicList > ul').width();		
		var topicDivWidth = $('.topicList').width();
		var bottomDivWidth = $('.topicBottomSliderDiv').width();
		
		slider.width((topicDivWidth*bottomDivWidth)/ maxListWidth);
		$('.topicPrevSlider > span').css('margin-left', (topicDivWidth * bottomDivWidth) / (maxListWidth * 2) - ($('.topicPrevSlider > span').width() / 2));	
		var maxScrollPos = bottomDivWidth - $('.topicPrevSlider').width();	
		$( ".topicPrevSlider" ).draggable({ 
			axis: "x", 
			containment: [ 4, 0, maxScrollPos, 0 ],
			drag: function( event, ui ) {	
				var maxScroll = (maxListWidth - topicDivWidth);	
				var scroll = ((ui.position.left) / maxScrollPos) * maxScroll ;
				$('.topicList').scrollLeft(scroll);	
				$('#topicMenuActivator, #topicMenu').css('left', scroll);
			}
		});
	};
	
	self.sliderElWidth = ko.computed (function() {
		self.setTopicSlider();			
		return (self.windowWidth() - 8) / $('.topicList > ul > li').size() - 1;
	});
	
	return self;
});