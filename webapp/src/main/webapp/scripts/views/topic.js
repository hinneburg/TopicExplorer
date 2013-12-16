//Initialize your view
view.setTab = false;
view.view = '';
view.content = '';
var TopicBodyRefArray = '';
var TopicBodyArray = '';
var TopicBodyArrayField = ''; 

view.init = function() {
	if(topicBodyFields.length > 0) {
		TopicBodyRefArray = topicBodyFields[0][0];
		TopicBodyArray = topicBodyFields[0][1];
		TopicBodyArrayField = topicBodyFields[0][2];
	} else {	
		TopicBodyRefArray = 'Term';
		TopicBodyArray = "Top_Terms";
		TopicBodyArrayField = 'TERM_ID';		
	}
	
	$('.topicList').html(view.Template());
	$('.topicList')
		.delegate('.showBig', 'click', view.topicClick);
		
	topicModel = new view.TopicViewModel();	
	ko.applyBindings(topicModel, document.getElementById('topicModel'));
	topicsLoaded();	

	if(topicTitleFields.length > 1 || topicBodyFields.length > 1) {
		var template;
		$('#topicModel').before('<div><img class="menuActivator rotate1" src="images/Black_Settings.png" alt="&gt;"/></div>' 
			+ '<div id="topicMenu" style="display: none;background-color: #EEEEEE; border: 1px solid #D1D1D1; position: absolute; z-index: 6;"/>');
		if(topicTitleFields.length > 1) {
			template = 'TopicTitle: <ul id="topicTitleMenu" class="topicMenu" > ';
			$.each(topicTitleFields, function(key, value) {
				template += '<li><a href="#">' + value + '</a></li>';
			});
			template += '</ul>';
			$('#topicMenu').html(template);
		}
		if(topicBodyFields.length > 1) {
			template = 'TopicBody: <ul id="topicBodyMenu" class="topicMenu"> ';
			$.each(topicBodyFields, function(key, value) {
				template += '<li><a href="#">' + value[0] + ' -> ' + value[2] + '</a></li>';
			});
			template += '</ul>';
			$('#topicMenu').html($('#topicMenu').html() + template);
		}
	}
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
	self.parts = ko.observableArray(topic[TopicBodyArray]());	
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
			'<ul class="wordlist" data-bind="foreach: parts">'+
				'<li><div class="topicWordTag" data-bind="text: topicExplorer.jsonModel.' + TopicBodyRefArray + '[' + TopicBodyRefArray + 'Id()].' + TopicBodyArrayField + '(), style: { fontSize: 25*relevance()/$parent.parts()[0].relevance() > 9 ? 25*relevance()/$parent.parts()[0].relevance()+\'px\' : \'9px\' }"></div></li>'+										
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