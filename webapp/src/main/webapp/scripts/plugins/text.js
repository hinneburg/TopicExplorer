//Initialize your plugin
plugin.setTab = true;
plugin.setActive = true;
plugin.tabName = 'Dokumentenview';
plugin.view = '';
plugin.content = '';

plugin.init = function() {
	this.content = $('<div>').attr('class', 'documentList').html(plugin.Template());
	
	//use delegate, cause content is not loaded yet	
	//bind on desktop, cause this element is not refreshed by tab view and don't lose bindings 
	$('#desktop').delegate(".documentList li", 'mouseover', function(e){
		$(this).addClass('myHover').children(".docButtons").show();
	})
	.delegate(".documentList li", 'mouseout', function(e){
		$(this).removeClass('myHover').children(".docButtons").hide();
	})	
	.delegate(".documentList circle", "mouseover",function(){
		$(this).attr("r", "7");
	})
	.delegate(".documentList circle", "mouseout",function(){
		$(this).attr("r", "5");
	})
	.delegate(".documentList circle", "click", moveToTopic)
	.delegate(".documentList .docTitle", "click", plugin.showDocument);
	
	plugin.documentModel = new plugin.DocumentViewModel();
	//draw circles of documents
	ko.bindingHandlers.drawCircles = {
	    update: function(elem, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {		
			var child = $(elem).children('svg');
			$.each(valueAccessor(), function(index, value){
				var topic = topicModel.topicList()[value];
				var circle = $(SVG('circle')).attr('r', 5).attr('cx', 10).attr('cy', 10+index*15).attr('fill', topic.COLOR_TOPIC$COLOR()).attr('title', topic.TEXT$TOPIC_LABEL).attr('stroke','black').attr('stroke-width', '0.5').attr('class','t_'+topic.TOPIC_ID);
				child.append(circle);
			});
	    }
	};
	
	//assign documentModel to GUI
	ko.applyBindings(plugin.documentModel, this.content[0]);
};

plugin.getDocumentViewModel = function ()
{
	var plugin = this;
	var self = new Object();
	self.documentData = ko.observableArray();
	$.each(topicExplorer.jsonModel.DOCUMENT, function( key, document ) {
		if(key != '__ko_mapping__')
			self.documentData()[key] = new plugin.DocumentModel(document.DOCUMENT_ID(), document.TEXT$TITLE(), document.TOP_TOPIC(), document.TEXT$FULLTEXT());
	});
	return self;
};
plugin.DocumentViewModel = function ()
{
	var self = this;
	self.documentData = ko.observableArray();
	$.each(topicExplorer.jsonModel.DOCUMENT, function( key, document ) {
		if(key != '__ko_mapping__')
			self.documentData()[key] = new plugin.DocumentModel(document.DOCUMENT_ID(), document.TEXT$TITLE(), document.TOP_TOPIC(), document.TEXT$FULLTEXT());
	});
}
plugin.DocumentModel = function (id, name, relevanzen, textSnippet) {
	var self = this;
	self.id = id;
	self.name = ko.observable(name);
	self.relevanzen = ko.observableArray(relevanzen);
	self.textSnippet = textSnippet;
	self.topTopics = self.relevanzen.slice(0,4);
}

plugin.showDocument = function (e) {
	var doc = $(e.currentTarget).parents('li').attr('id').split('doc_')[1];
	var wordList = null;
	
	$.getJSON('JsonServlet', {Command:'getDoc', DocId:doc})
	.done(function(json) {
		doc = json.DOCUMENT;	
		wordList = json.WORD_LIST;
	});
//	doc = jsonModel.DOCUMENT[doc];
//	console.log(json.WORD_LIST);
	var text = doc.TEXT$FULLTEXT;
	for(var i = 0; wordList[i]; i++) {
		text = text.substring(0, wordList[i].POSITION_OF_TOKEN_IN_DOCUMENT) + '<span style="background-color: '+topicExplorer.jsonModel.Topic[wordList[i].TOPIC_ID].COLOR_TOPIC$COLOR()+'">' + wordList[i].TOKEN + "</span>" + text.substring(Number(wordList[i].POSITION_OF_TOKEN_IN_DOCUMENT) + wordList[i].TOKEN.length);
	}
	gui.drawTab(doc.TEXT$TITLE,true,true, "<h2>" + doc.TEXT$TITLE + "</h2>" + text);
	e.preventDefault();
	return false;
}
plugin.setDocumentModel = function ()
{
	//documentModel = new plugin.DocumentViewModel();
}

plugin.Template = function ()
{
	var template = '<ul data-bind="foreach: $root.documentData">'+
	'<!-- ko if: hasOwnProperty("id") -->'+
	'<li class="documents" data-bind="attr: { \'id\': \'doc_\'+$data.id }">'+
		'<div class="docButtons">'+
			'<button class="addButton" type="button" title="add to shortlist" name="addToCart"></button>'+
			'<button class="chartButton" type="button" title="show topic mixture" name="showBig" onclick=""></button>'+
		'</div>'+
		'<a href="#" class="docTitle"><nobr data-bind="text: name"></nobr></a>'+
		'<p class="docContent"><span data-bind="text: textSnippet"></span></p>'+
		'<div class="circles" data-bind="drawCircles: topTopics">'+
			'<svg width="100%" height="100%"></svg>'+
		'</div>'+
	'</li>'+
	'<!-- /ko -->'+
	'</ul>';
	return template;
};
plugin.getTemplate = function ()
{
	var template = $('<div>').attr('class', 'documentList').html(this.Template());
	return template;
}