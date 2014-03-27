define(["knockout", "jquery"],
function(ko, $) { 
	return function(topicexplorer) {
		var self = this;
    	self.windowWidth = ko.observable(100).subscribeTo("windowWidth");
    	
    	self.setTopicSlider = function() {	
			var slider = $('.topicPrevSlider');		
			var maxListWidth = $('.topicList > ul').width();
			var topicDivWidth = $('.topicList').width();
			var bottomDivWidth = $('.topicBottomSliderDiv').width();
			
			slider.width((topicDivWidth*bottomDivWidth)/ maxListWidth);
			
			$('.topicPrevSlider > span').css('margin-left', (topicDivWidth * bottomDivWidth) / (maxListWidth * 2) - ($('.topicPrevSlider > span').width() / 2));	
			var maxScrollPos = $('.topicBottomSliderDiv').width() - $('.topicPrevSlider').width();	
			$( ".topicPrevSlider" ).draggable({ 
				axis: "x", 
				containment: [ 0, 0, maxScrollPos, 0 ],
				drag: function( event, ui ) {	
					var maxScroll = (maxListWidth - topicDivWidth);	
					var scroll = ((ui.position.left + 4) / maxScrollPos) * maxScroll ;
					$('.topicList').scrollLeft(scroll);	
					$('#topicMenu, .topicList > img').css('left', scroll);
				}
			});
		};
		
		self.sliderElWidth = ko.computed (function() {
    		self.setTopicSlider();
   			return self.windowWidth() / $('.topicList > ul > li').size();
		});
	};
});