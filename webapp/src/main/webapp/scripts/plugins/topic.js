//Initialize your plugin
plugin.setTab = false;
plugin.view = '';
plugin.content = '';

plugin.init = function() {
	//var content = $('<div>').attr('class', 'documentList').html(template());
	$('.topicList').html(plugin.Template());
	$('.topicList')
		.delegate('.showBig', 'click', plugin.topicClick);
		
	topicModel = new plugin.TopicViewModel();	
	ko.applyBindings(topicModel, document.getElementById('topicModel'));
	topicsLoaded();
};

plugin.TopicViewModel = function () {
	var self = this;
	self.topicData = ko.observableArray();
	self.topicList = ko.observableArray();
	$.each( topicExplorer.jsonModel.Topic, function( key, topic ) {
		var myTopic = new plugin.TopicModel(topic.COLOR_TOPIC$COLOR(), topic.TOPIC_ID(), topic.TEXT$TOPIC_LABEL(), topic.Top_Terms());
		//An key-Position einfügen, um später direkt per ID zugreifen zu können		
		self.topicData().push(myTopic);
		self.topicList()[topic.TOPIC_ID()] = myTopic;
	});
//	console.log(self.topicData());
	
};
plugin.TopicModel = function (color, id, name, words) {
	var self = this;
	self.COLOR_TOPIC$COLOR = ko.observable(color);
	self.TOPIC_ID = id;
	self.TEXT$TOPIC_LABEL = name;
	self.words = ko.observableArray(words);	
};

plugin.Template = function ()
{
	var template = '<ul data-bind="foreach: topicData">'+
		'<li class="topic" data-bind="style: { backgroundColor: COLOR_TOPIC$COLOR()}, attr: { \'id\': \'topic\'+$data.TOPIC_ID }">'+
	'<div class="topicElementDiv">'+
		'<div class="topicElementMenu">'+
			'<button class="showBig" type="button" title="details" name="showBig"></button>'+
			'<button class="addToCart" type="button" title="add to shortlist" name="addToCart"></button>'+
		'</div>'+
		'<div class="topicTitle" style="cursor: pointer;" data-bind="text: TEXT$TOPIC_LABEL"></div>'+
		'<div class="topicElementContent">'+
			'<ul class="wordlist" data-bind="foreach: words">'+
				'<li><div class="topicWordTag" data-bind="text: topicExplorer.jsonModel.Term[TermId()].TERM_NAME(), style: { fontSize: 25*relevance()/$parent.words()[0].relevance() > 9 ? 25*relevance()/$parent.words()[0].relevance()+\'px\' : \'9px\' }"></div></li>'+										
			'</ul>'+
		'</div>'+
	'</div>'+
'</li>'+
'</ul>';
	return template;
};

plugin.topicClick = function (e) {
	var topic = $(e.currentTarget).parents('li').attr('id').split('topic')[1];
	topic = topicExplorer.jsonModel.Topic[topic];	
	$.getJSON('JsonServlet', {Command:'bestDocs', TopicId:topic.TOPIC_ID()})
	.done(function(json) {
		topicExplorer.jsonModel.DOCUMENT = ko.mapping.fromJS(json.DOCUMENT);
	});
	var textModel = topicExplorer.pluginModel.getPlugin('text').getDocumentViewModel();
	var template = topicExplorer.pluginModel.getPlugin('text').getTemplate(topicExplorer.jsonModel);
	ko.applyBindings(textModel, template[0]);
	gui.drawTab(topic.TEXT$TOPIC_LABEL(), true, true, template);
}