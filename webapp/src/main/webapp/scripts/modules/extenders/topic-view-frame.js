define(["knockout", "jquery"],
function(ko, $) {
	return function (instance) {
		instance.loadDocumentsForFrames = function (frameIndex, context) { 
			var topicId = $(context.target).parents('.topic').attr('id').split('_')[1];
			var frame = globalData.Topic[topicId].FRAMES[frameIndex].FRAME; 
			ko.postbox.publish('openNewTab', {moduleName:"document-browse-tab",tabHeading:"Topic " + topicId + "(" + frame + ")", data: {topicId: topicId, frame: frame, getParam: "bestDocs&TopicId=" + topicId + "&frame=" + frame}});	
		};
		
		$.getJSON("JsonServlet?Command=getBestFrames").success(function(receivedParsedJson) {
			for (key in receivedParsedJson) {
				var frames = receivedParsedJson[key].SORTING;
				delete receivedParsedJson[key].SORTING;
				$.extend(globalData.Topic[key], receivedParsedJson[key]);
				globalData.Topic[key].frameCount = 0;
				for(var i in receivedParsedJson[key].FRAMES) {
					if(receivedParsedJson[key].FRAMES.hasOwnProperty(i)) {
						globalData.Topic[key].frameCount++;
					}
				}
				globalData.Topic[key].frameSorting = ko.observableArray(frames);
				if(globalData.Topic[key].frameCount < 10) {
					globalData.Topic[key].frameFull = true;
				} else {
					globalData.Topic[key].frameFull = false;
				}
				instance.Topic[key].TITLE_REPRESENTATION.FRAMES = instance.Topic[key].TITLE_REPRESENTATION.KEYWORDS;
			}
		});
		
		instance.bodyTemplate.FRAMES = 'extenders/topic-frame';
		
		instance.textSelectArray.push(new instance.TextRepresentation('Frames', 'FRAMES'));
		
		instance.framesLoading = ko.observable(false);
		
		instance.frameScrollCallback = function(el) {
			if(!instance.framesLoading() && !globalData.Topic[el].frameFull && $('#topic_' + el).children('div').children('.topicElementContent').height() +  $('#topic_' + el).children('div').children('.topicElementContent').scrollTop() >=  $('#topic_' + el).children('div').children('.topicElementContent')[0].scrollHeight) {
				instance.framesLoading(true);
				$.getJSON("JsonServlet?Command=getFrames&topicId=" + el + "&offset=" + globalData.Topic[el].frameCount).success(function(receivedParsedJson) {
					$.extend(globalData.Topic[el].FRAMES, receivedParsedJson[el].FRAMES);
					globalData.Topic[el].frameSorting(globalData.Topic[el].frameSorting().concat(receivedParsedJson[el].SORTING));
					globalData.Topic[el].frameCount += receivedParsedJson[el].SORTING.length;
					if(receivedParsedJson[el].SORTING.length < 10) {
						globalData.Topic[el].frameFull = true;
					}	
					instance.framesLoading(false);
					
				});
			}
		};		
	};	
});