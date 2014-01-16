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
	ko.bindingHandlers.module.baseDir = "modules";
	
	// global delegates
	$(document).delegate(".menuActivator", 'click', function(e){
	    $(this).toggleClass("rotate1 rotate2");
	    $(this).next().toggle('blind');
	}).delegate(".documentList li", 'mouseover', function(e){
		$(this).addClass('myHover').children(".docButtons").show();
	}).delegate(".documentList li", 'mouseout', function(e){
		$(this).removeClass('myHover').children(".docButtons").hide();
	}).delegate(".documentList circle", "mouseover",function(){
		$(this).attr("r", "7");
	}).delegate(".documentList circle", "mouseout",function(){
		$(this).attr("r", "5");
	});
	$(window).resize(function() {
		resizeDocumentDivs();
		resizeTopicDivs();
	});
	
	setTimeout(function() {
		ko.applyBindings(new App());
	}, 0);
});

function resizeDocumentDivs() {
		var minHeight = 400;
		var width = Math.max($(window).width(), /* For opera: */ document.documentElement.clientWidth);
		var height = Math.max(minHeight, $(window).height(), /* For opera: */ document.documentElement.clientHeight);
	
		$('.leftBody').height(height-$('.searchBar').height()-30);	
		$('#desktop').height(($('.leftBody').height()-90)*0.7);	
		setTimeout(function() {
			var desktopWidth = $('#desktop > .documentList > ul').width()-63;
			var documentWidth = 275;
	
			var docDeskRatio = Math.floor(desktopWidth/documentWidth);
			$('#desktop > .documentList > ul > li').outerWidth(desktopWidth/docDeskRatio);
		}, 300);
};

function resizeTopicDivs() {
	setTimeout(function() {
		$('.topicList').height(($('.leftBody').height()-90)*0.3);
	//	$('.shoppingCart').height($('.leftBody').height());
		$('.topic').height($('.topicList').height()-14);
	
		var topics = $('.topicList > ul > li').size();
//		$('#groupG').attr('transform', 'scale('+ ($('.topicPrevElCont').width()) / topics+',1)');
//		$('#groupG2').attr('transform', 'scale('+ ($('.topicPrevElCont').width()) / topics+',1)');
		$('.topicList > ul').width(topics*213);
//		$('.topicList2 > ul').width(topics*213);
	}, 0);
};

function makeMenu(el) {
	$(el).menu({
		select : function(event, ui) {
			$(this).parent().parent().prev()
				.toggleClass("rotate1 rotate2");
			$(this).parent().parent().toggle('blind');		
		}
	});
};


