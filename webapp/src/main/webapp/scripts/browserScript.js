$(document).ready(function() {
	$('#changeColor').click(function(event) { 
		var color = '#'+$(this).prev().val();
		$(this).prevAll('div').css('backgroundColor', color);
		console.log($(this).prevAll('div').css('backgroundColor'));
		var theme = $(this).prev().prev().val();
		modelView.searchTopic(theme).color(color);
		event.preventDefault();
	});
});

$(window).resize(function() {
	resizeDivs();
});

function resizeDivs() {
	var minHeight = 400;
	var width = Math.max($(window).width(), /* For opera: */ document.documentElement.clientWidth);
	var height = Math.max(minHeight, $(window).height(), /* For opera: */ document.documentElement.clientHeight);
	
	$('.leftBodyContent').height(height-$('.searchBar').height()-30);	
	$('#desktop').height(($('.leftBodyContent').height()-90)*0.7);	
	$('.topicList').height(($('.leftBodyContent').height()-90)*0.3);
	$('.shoppingCart').height($('.leftBodyContent').height());
	$('.topic').height($('.topicList').height()-14);
	
	var topics = $('.topicList > ul > li').size();
	$('#groupG').attr('transform', 'scale('+ ($('.topicPrevElCont').width()) / topics+',1)');
	$('#groupG2').attr('transform', 'scale('+ ($('.topicPrevElCont').width()) / topics+',1)');
	$('.topicList > ul').width(topics*213);
	$('.topicList2 > ul').width(topics*213);
	
	var desktopWidth = $('#desktop > .documentList > ul').width()-63;
	var documentWidth = 275;
	
	var docDeskRatio = Math.floor(desktopWidth/documentWidth);
	$('#desktop > .documentList > ul > li').outerWidth(desktopWidth/docDeskRatio);
	
	setTopicSlider();
}

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
	});
}

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
}

function moveToTopic(self) {
	var topic_id;	
	if(!self.currentTarget)
		topic_id = self;
	else if(self.currentTarget.nodeName == 'circle')
		topic_id = $(self.currentTarget).attr('class').split("_")[1];
	else if(self.currentTarget.nodeName == 'rect')
		topic_id = $(self.currentTarget).attr('id').split("_")[1];
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
}