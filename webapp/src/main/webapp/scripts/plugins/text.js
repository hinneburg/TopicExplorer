//Initialize your plugin
plugin.setTab = true;
plugin.setActive = true;
plugin.tabName = 'Dokumentenview';
plugin.view = '';
plugin.content = '';

plugin.init = function() {
	this.content = $('<div>').attr('class', 'documentList').html(template());
	
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
	.delegate(".documentList .docTitle", "click", showDocument);	
	
	documentModel = new DocumentViewModel();
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
	ko.applyBindings(documentModel, this.content[0]);
};

function DocumentViewModel()
{
	var self = this;
	self.documentData = ko.observableArray();
	$.each( json.get('DOCUMENT'), function( key, document ) {
		self.documentData()[key] = new DocumentModel(document.DOCUMENT_ID, document.TEXT$TITLE, document.TOP_TOPIC, document.TEXT$FULLTEXT);
	});
}
function DocumentModel(id, name, relevanzen, textSnippet) {
	var self = this;
	self.id = id;
	self.name = ko.observable(name);
	self.relevanzen = ko.observableArray(relevanzen);
	self.textSnippet = textSnippet;
	self.topTopics = self.relevanzen.slice(0,4);
}

function showDocument(e) {
	var doc = $(e.currentTarget).parents('li').attr('id').split('doc_')[1];
	console.log(doc);
	doc = jsonModel.DOCUMENT[doc];
//	console.log(doc);
	gui.drawTab(doc.TEXT$TITLE(),true,true, doc.TEXT$FULLTEXT());
	e.preventDefault();
	return false;
}

function template()
{
	var template = '<ul data-bind="foreach: documentData">'+
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
}