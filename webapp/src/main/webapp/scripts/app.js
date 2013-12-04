topicExplorer = new Object();
topicExplorer.viewModel;

var topicTitleFields = new Array(); 
var topicBodyFields = new Array();
var documentTitleFields = new Array();
var documentBodyFields = new Array();
var topicColor = "#ffffff";


//This would be the default script
topicExplorer.ViewModel = function (defaultViews) {
	var self = this;
	self.views = ko.observableArray();
//	self.views = new Array();
	
	//method to search registered view
	self.getView = function(searchView) 
	{
		var isLoaded = ko.utils.arrayFirst(self.views(), function(item) {			
			if(item.name === searchView) {
				return item;
			}
		});
		return isLoaded;
	};
	
	//method to register your views
	self.registerView = function(viewName) {
		//check if the view is already registered
		if(!self.getView(viewName)) {
			$( "#ajaxLoader" ).show();
			//Initialize a view object
			view = new Object();
			//loadScript from view folder
			
			$.getScript('scripts/views/'+viewName+'.js').success(function(data) {
				start = new Date().getTime();
				view.init();
				//delete init function after init
				delete view.init;
				view.name = viewName;
				self.views.push(view);
				if(view.setTab) {
					gui.drawTab(view.tabName, view.tabCanClose, view.setActive, view.content);
				}
				delete view;
				console.log("Zeit KO View"+viewName+": " + (new Date().getTime() - start));
			}).complete(function(){
				$( "#ajaxLoader" ).hide();				
			});
			//avoid overwrite
			//push view into KO array
			//self.views.push(view);
			//delete reference to object
			//delete view;
		}
	};
	
	$.each(defaultViews,function(key, value){		
		self.registerView(value);
	});
};
$(document).ajaxStart(function() {
	$( "#ajaxLoader" ).show();
}).ajaxSuccess(function(event, request, settings) {
	//$( "#ajaxLoader" ).hide();
}).ajaxComplete(function(){
	$( "#ajaxLoader" ).hide();
});
$(document).ready(function() {
	var start = new Date().getTime();
	$.ajaxSetup({
		async: false
	});
	$.getJSON('JsonServlet', {art:'random', id:null, limit:20})
	.done(function(json) {
		console.log("Zeit JSON holen: " + (new Date().getTime() - start));
		start = new Date().getTime();
		$.each(json.PLUGINS,function(key, value){		
			$.getScript('scripts/plugins/'+value+'.js').success(function(data) {
			}).done(function(script){
				console.log('scripts/plugins/'+value+'.js loaded');		
			}).fail(function( jqxhr, settings, exception ){
				console.log('scripts/plugins/'+value+'.js not loaded');
			});
		});
	
		topicExplorer.jsonModel = ko.mapping.fromJS(json.JSON);
		console.log("Zeit JSON mit KO mappen: " + (new Date().getTime() - start));
		start = new Date().getTime();
		console.log(json.FRONTEND_VIEWS);
		topicExplorer.viewModel = new topicExplorer.ViewModel(json.FRONTEND_VIEWS);// ["search", "slider", "topic", "text"]
		console.log("Zeit KO Views initialisieren: " + (new Date().getTime() - start));
		
	}).fail(console.log("error"));
		
	ko.bindingHandlers.topicTest = {
		update: function(elem,valueAccessor) {
			if(valueAccessor().length > 0) {
				console.log(valueAccessor());
				$(elem).append($('<div class="small">').append(valueAccessor()[0].name));
			}
		}
	};
	ko.bindingHandlers.topicTest = {
		update: function(elem,valueAccessor, allBindingsAccessor, viewModel, bindingContext) {			
			$(elem).append($('<div class="">').append('<input data-bind="value: $data.name">'));
		}
	};
	$('#tabView').delegate("p", 'click', function(){		
		gui.switchTab($(this), false);
	});
	
	$(".menu").menu({
        select: function(event, ui) {
            $(this).parent().hide('slow');
            
        	console.log($(this).attr('id').split('Menu')[0] + ui.item.text());
        	
        	
            
        }
    });
	
	$('body').delegate(".menuActivator", 'click', function(e){
	    $(this).toggleClass("rotate1 rotate2");
	    $(this).parent().next().toggle('slow');
	});

});
//ko.applyBindings(modelView);

function topicsLoaded() {
	resizeDivs();
	
	$('#groupG rect.topicRec').on("click", moveToTopic)
	.on("mouseover",function(){
		$(this).attr("height", "18").attr('y', '0');
	})
	.on("mouseout",function(){
		$(this).attr("height", "13").attr('y', '2');
	});
	
	if($('.groupRect').size() == 0) {
		var groupG = $('#groupG');
		var rect = $(SVG('rect'));
		rect.attr("fill", $('.topicList > ul > li').first().css('backgroundColor')).attr("width", $('.topicList > ul > li').size())
		.attr("height", 13).attr("x", 0).attr("depth",0).attr("y", 16).attr("class", "groupRect")
		.attr("border",0).css("strokeWidth", "0.02").css("stroke", "black");
		groupG.append(rect);
	}
}

function SVG(tag) {
	return document.createElementNS('http://www.w3.org/2000/svg', tag);
}

//functions for view programmer
var gui = new Object();
gui.tabCache = new Object();
gui.drawTab = function (tabName, canClose, active, content) {
	var cache = new Object();
	var newTab = $("<p>").attr('class', 'reiter active button gradientGray');
	//var newTab = $("<p>").attr('class', $('#tabView > p').first().attr("class"));
	if(active) {
		if($('#tabView > .active > span').html()) {
			cache.content = $('#desktop').html();
			gui.tabCache[$('#tabView > .active > span').html()] = cache;
		}
		$('#tabView > .active').removeClass('active');
		if(!content)
			content = "";
		$('#desktop').html(content);
	}
	else {
		cache.content = content;
		gui.tabCache[tabName] = cache;
		newTab.removeClass('active');
	}	
	newTab.append($("<span>").append(tabName));
	if(canClose) {
		var closeIcon = $('<img>').attr('alt', 'close').attr('src', 'images/delete_24.png').attr('height', '8px').attr('width', '8px').addClass('closeIcon');
		closeIcon.on('click', function(e){
			//stop delegate to tab
			e.stopPropagation();
			newTab.remove();
			if($(this).parent().hasClass('active'))
				gui.switchTab($('#tabView').find('p').first(), false);
		});
		newTab.append(closeIcon);
	}
	$('#tabView').append(newTab);
};
gui.switchTab = function (tabObject, content) {
	var reiter = tabObject.find('span').html();
	var activeReiter = $('#tabView > .active > span').html();
	var cache = new Object();
	if(!tabObject.hasClass('active')) {
		cache.content = $('#desktop').html();
		gui.tabCache[activeReiter] = cache;
		$('#tabView > .active').removeClass('active');
		if(content) {
			$('#desktop').html(content);
		}
		else {
			content = gui.tabCache[reiter];
			if(!content || !content.content)
				content.content = '';
			$('#desktop').html(content.content);
		}
		tabObject.addClass('active');
	}
};