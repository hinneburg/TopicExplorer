var jsonObject;
//This would be the default script
function PluginModel(defaultPlugins) {
	var self = this;
	self.plugins = ko.observableArray();
	
	//method to register your plugins
	self.registerPlugin = function(pluginName) {
		//check if the plugin is already registered
		var isLoaded = ko.utils.arrayFirst(self.plugins(), function(item) {			
			if(item.name === pluginName) {
				return item;
			}
		});
		if(!isLoaded) {
			$( "#ajaxLoader" ).show();
			//Initialize a plugin object
			plugin = new Object();
			//loadScript from plugin folder
			$.ajaxSetup({
				async: false
				});
			$.getScript('scripts/plugins/'+pluginName+'.js').success(function(data) {
				start = new Date().getTime();
				plugin.init();
				//delete init function after init
				delete plugin.init;
				plugin.name = pluginName;
				self.plugins.push(plugin);
				if(plugin.setTab) {
					gui.drawTab(plugin.tabName, plugin.tabCanClose, plugin.setActive, plugin.content);
				}
				delete plugin;
				console.log("Zeit KO Plugin"+pluginName+": " + (new Date().getTime() - start));
			}).complete(function(){
				$( "#ajaxLoader" ).hide();				
			});
			//avoid overwrite
			//push plugin into KO array
			//self.plugins.push(plugin);
			//delete reference to object
			//delete plugin;
		}
	};
	
	$.each(defaultPlugins,function(key, value){		
		self.registerPlugin(value);
	});
}

var pluginModel;
$(document).ajaxStart(function() {
	$( "#ajaxLoader" ).show();
}).ajaxSuccess(function(event, request, settings) {
	//$( "#ajaxLoader" ).hide();
}).ajaxComplete(function(){
	$( "#ajaxLoader" ).hide();
});
$(document).ready(function() {
	var start = new Date().getTime();
	$.getJSON('getRandomDocs', {art:'random', id:null, limit:20})
	.done(function(json) {
		console.log("Zeit JSON holen: " + (new Date().getTime() - start));
		start = new Date().getTime();
		
		jsonModel = ko.mapping.fromJS(json.JSON);
		console.log("Zeit JSON mit KO mappen: " + (new Date().getTime() - start));
		start = new Date().getTime();
		jsonObject = json.JSON;
		console.log(json.FRONTEND_VIEWS);
		pluginModel = new PluginModel(json.FRONTEND_VIEWS);// ["search", "slider", "topic", "text"]
		console.log("Zeit KO Plugins initialisieren: " + (new Date().getTime() - start));
		
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

//functions for plugin programmer
var gui = new Object();
var tabCache = new Object();
gui.drawTab = function (tabName, canClose, active, content) {
	var cache = new Object();
	var newTab = $("<p>").attr('class', 'reiter active button gradientGray');
	//var newTab = $("<p>").attr('class', $('#tabView > p').first().attr("class"));
	if(active) {
		if($('#tabView > .active > span').html()) {
			cache.content = $('#desktop').html();
			tabCache[$('#tabView > .active > span').html()] = cache;
		}
		$('#tabView > .active').removeClass('active');
		if(!content)
			content = "";
		$('#desktop').html(content);
	}
	else {
		cache.content = content;
		tabCache[tabName] = cache;
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
		tabCache[activeReiter] = cache;
		$('#tabView > .active').removeClass('active');
		if(content) {
			$('#desktop').html(content);
		}
		else {
			content = tabCache[reiter];
			if(!content || !content.content)
				content.content = '';
			$('#desktop').html(content.content);
		}
		tabObject.addClass('active');
	}
};
var json = new Object();
json.get = function(field) {
	return jsonObject[field];
};