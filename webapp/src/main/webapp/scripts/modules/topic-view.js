define(["knockout"],
function(ko) { 
	return function(topicexplorer) {
    	this.topicexplorer = topicexplorer;
    	this.pluginTemplates = topicexplorer.config.topicView.pluginTemplates;
    	this.selectedPluginTemplate = ko.observable(this.pluginTemplates[0]);
    	this.selectedTopics = ko.observableArray(["1","2"]);
    	this.changeSelectedTopics = function () { this.selectedTopics(["1"]); };
		this.clickedTopic = ko.observable().subscribeTo("clickedTopic");
		this.makeMenu = function(el) {
			$(el).menu({select: function(event, ui) {
				$(this).parent().parent().hide('slow');
				$(this).parent().parent().prev().toggleClass("rotate1 rotate2");
			}});
		};
	};
});

