//Initialize your plugin
plugin.setTab = false;
plugin.view = '';
plugin.content = '';

plugin.init = function() {
	//var content = $('<div>').attr('class', 'documentList').html(template());
	$('.topicList').html(template());
	$('.topicList')
		.delegate('.showBig', 'click', topicClick);
	topicModel = new TopicViewModel();	
	termModel = ko.mapping.fromJS(json.get('Term'));
	newTopicModel = ko.mapping.fromJS(json.get('Topic'));
	ko.applyBindings(topicModel, document.getElementById('topicModel'));
	topicsLoaded();
};

function TopicViewModel() {	
	var self = this;
	self.topicData = ko.observableArray();
	self.topicList = ko.observableArray();
	$.each( json.get('Topic'), function( key, topic ) {		
		var myTopic = new TopicModel(topic.COLOR_TOPIC$COLOR, topic.TOPIC_ID, topic.TEXT$TOPIC_LABEL, topic.Top_Terms);
		//An key-Position einfügen, um später direkt per ID zugreifen zu können		
		self.topicData().push(myTopic);
		self.topicList()[key] = myTopic;
	});	
}
function TopicModel(color, id, name, words) {
	var self = this;
	self.COLOR_TOPIC$COLOR = ko.observable(color);
	self.TOPIC_ID = id;
	self.TEXT$TOPIC_LABEL = name;
	self.words = ko.observableArray(words);	
}

function template()
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
				'<li><div class="topicWordTag" data-bind="text: jsonModel.Term[TermId].TERM_NAME(), style: { fontSize: 25*relevance/$parent.words()[0].relevance > 9 ? 25*relevance/$parent.words()[0].relevance+\'px\' : \'9px\' }"></div></li>'+										
			'</ul>'+
		'</div>'+
	'</div>'+
'</li>'+
'</ul>';
	return template;
}

function topicClick(e) {
	var topic = $(e.currentTarget).parents('li').attr('id').split('topic')[1];
	topic = jsonModel.Topic[topic];	
	gui.drawTab(topic.TEXT$TOPIC_LABEL(), true, true, topic.TEXT$TOPIC_LABEL());
}