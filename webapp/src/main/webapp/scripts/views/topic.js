//Initialize your view
view.setTab = false;
view.view = '';
view.content = '';

view.init = function() {
	$('.topicList').html(view.Template());
	$('.topicList')
		.delegate('.showBig', 'click', view.topicClick);
		
	topicModel = new view.TopicViewModel();	
	ko.applyBindings(topicModel, document.getElementById('topicModel'));
	topicsLoaded();
};

view.TopicViewModel = function () {
	var self = this;
	self.topicData = ko.observableArray();
	self.topicList = ko.observableArray();
	
	$.each( topicExplorer.jsonModel.Topic, function( key, topic ) {
		
		var myTopic = new view.TopicModel(topic);
		//An key-Position einfügen, um später direkt per ID zugreifen zu können		
		self.topicData().push(myTopic);
		self.topicList()[topic.TOPIC_ID()] = myTopic;
	});
	
//	console.log(self.topicData());
	
};
view.TopicModel = function (topic) {
	var self = this;
	if(typeof topic[topicColor] == 'function') {
		self.topicColor = topic[topicColor]();
	} else {
		self.topicColor = topicColor;
	}
	if(topicTitleFields.length > 0) {
		self.topicTitle = topic[topicTitleFields[0]]();
		$.each(topicTitleFields, function(key, value){
			self[value] = topic[value]();
		});
	} else {
		self.topicTitle = "Topic " + topic.TOPIC_ID();
	}
	self.topicId = topic.TOPIC_ID();
	self.words = ko.observableArray(topic.Top_Terms());	
};

view.Template = function ()
{	
	var template = '<ul data-bind="foreach: topicData">'+
		'<li class="topic" data-bind="style: { backgroundColor: topicColor}, attr: { \'id\': \'topic\'+$data.topicId }">'+
	'<div class="topicElementDiv">'+
		'<div class="topicElementMenu">'+
			'<button class="showBig" type="button" title="details" name="showBig"></button>'+
			'<button class="addToCart" type="button" title="add to shortlist" name="addToCart"></button>'+
		'</div>'+
		'<div class="topicTitle" style="cursor: pointer;" data-bind="text: topicTitle"></div>'+
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

view.topicClick = function (e) {
	var topic = $(e.currentTarget).parents('li').attr('id').split('topic')[1];
	topic = topicModel.topicList()[topic];
	$.getJSON('JsonServlet', {Command:'bestDocs', TopicId:topic.topicId})
	.done(function(json) {
		topicExplorer.jsonModel.DOCUMENT = ko.mapping.fromJS(json.DOCUMENT);
	});
	var textModel = topicExplorer.viewModel.getView('browser').getDocumentViewModel("topicRelevance", 0);
	var template = topicExplorer.viewModel.getView('browser').getTemplate();//(topicExplorer.jsonModel);
	ko.applyBindings(textModel, template[0]);
	gui.drawTab(topic.topicTitle, true, true, template);
};