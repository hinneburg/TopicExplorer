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
	    $(this).next().toggle('slow');
	}).delegate(".documentList li", 'mouseover', function(e){
		$(this).addClass('myHover').children(".docButtons").show();
	}).delegate(".documentList li", 'mouseout', function(e){
		$(this).removeClass('myHover').children(".docButtons").hide();
	}).delegate(".documentList circle", "mouseover",function(){
		$(this).attr("r", "7");
	}).delegate(".documentList circle", "mouseout",function(){
		$(this).attr("r", "5");
	}).delegate(".showBig", "click", function() {
		$.getJSON("http://localhost:8080/webapp/JsonServlet?Command=bestDocs&TopicId=" + $(this).parent().parent().parent().attr('id').substring(5)).success(function(receivedParsedJson) {
			self.topicexplorerModel.document = receivedParsedJson;
//			callback(Object.keys(self.topicexplorerModel.document));
		});
	//	alert($(this).parent().parent().parent().attr('id'));
	});
	
	setTimeout(function() {
		ko.applyBindings(new App());
	}, 0);
});
