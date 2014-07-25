define(["knockout", "jquery"],
function(ko, $) {
	return function (instance) {
		instance.loadDocumentsForFrames = function (frameIndex, context) { 
			var topicId = $(context.target).parents('.topic').attr('id').split('_')[1];
			var frame = globalData.Topic[topicId].FRAMES[instance.activeFrameType()][frameIndex].FRAME; 
			ko.postbox.publish('openNewTab', {moduleName:"document-browse-tab", tabHeading:"Topic " + topicId + " (" + frame + ")", data: {topicId: topicId, frame: frame, frameType: instance.activeFrameType(), getParam: "bestDocs&TopicId=" + topicId + "&frame=" + frame + "&frameType=" + instance.activeFrameType()}});	
		};
		
		
		instance.frameScrollCallback = function(el) {
			instance.checkScrollHeightForJumpStart(el);
			if(!instance.loading() && !globalData.Topic[el].FULL[instance.activeFrameType()]() && $('#topic_' + el).children('div').children('.topicElementContent').height() +  $('#topic_' + el).children('div').children('.topicElementContent').scrollTop() >=  $('#topic_' + el).children('div').children('.topicElementContent')[0].scrollHeight - 35) {
				instance.loading(true);
				$.getJSON("JsonServlet?Command=getFrames&topicId=" + el + "&offset=" + globalData.Topic[el].COUNT[instance.activeFrameType()] + "&frameType=" + instance.activeFrameType()).success(function(receivedParsedJson) {
					$.extend(globalData.Topic[el].FRAMES[instance.activeFrameType()], receivedParsedJson[el].FRAMES);
					globalData.Topic[el].SORTING[instance.activeFrameType()](globalData.Topic[el].SORTING[instance.activeFrameType()]().concat(receivedParsedJson[el].SORTING));
					globalData.Topic[el].COUNT[instance.activeFrameType()] += receivedParsedJson[el].SORTING.length;
					if(receivedParsedJson[el].SORTING.length < 20) {
						globalData.Topic[el].FULL[instance.activeFrameType()](true);
					}	
					instance.loading(false);
					
				});
			}
		};	
		
		instance.activeFrameType = ko.observable();
		var firstTopic = true;
		$.getJSON("JsonServlet?Command=getBestFrames").success(function(receivedParsedJson) {
			for (topicId in receivedParsedJson) {
				$.extend(globalData.Topic[topicId], receivedParsedJson[topicId]);
			
				for(frameType in receivedParsedJson[topicId].FRAMES) {
				//	instance.activeFrameType(frameType);
					var frames = receivedParsedJson[topicId].FRAMES[frameType].SORTING;
				
					delete globalData.Topic[topicId].FRAMES[frameType].SORTING;
					delete receivedParsedJson[topicId].FRAMES[frameType].SORTING;
					globalData.Topic[topicId].COUNT[frameType] = 0;
					for(var i in receivedParsedJson[topicId].FRAMES[frameType]) {
						if(receivedParsedJson[topicId].FRAMES[frameType].hasOwnProperty(i) && i != 'frameCount') {
							globalData.Topic[topicId].COUNT[frameType]++;
						}
					}
					globalData.Topic[topicId].SORTING[frameType] = ko.observableArray(frames);
					
					globalData.Topic[topicId].FULL[frameType] = ko.observable(false);
					if(globalData.Topic[topicId].COUNT[frameType] < 10) {
						globalData.Topic[topicId].FULL[frameType](true);
					} 
					instance.Topic[topicId].TITLE_REPRESENTATION[frameType] = instance.Topic[topicId].TITLE_REPRESENTATION.KEYWORDS;
					if(firstTopic) {
						instance.bodyTemplate[frameType] = 'extenders/topic-frame';
						instance.textSelectArray.push(new instance.TextRepresentation('Frames (' + frameType + ')' , frameType));	
						instance.scrollCallback[frameType] = instance.frameScrollCallback;
					}

				}
				firstTopic = false;
			}
			$.getJSON("JsonServlet?Command=getFrameInfo").success(function(receivedParsedJson2) {
				for (topicId in receivedParsedJson2) {
					for(frameType in receivedParsedJson2[topicId].FRAMES) {
						instance.Topic[topicId].INFO_HIGHLIGHT[frameType] = receivedParsedJson2[topicId].FRAMES[frameType].FRAME_COUNT + ' frames, ' + receivedParsedJson2[topicId].FRAMES[frameType].UNIQUE_FRAME_COUNT + ' unique frames';
					}
				}
			});
		});
		
		
		
		instance.textSelection.subscribe(function(newValue) {
			if(globalData.Topic[topicId].FRAMES.hasOwnProperty(newValue.field)){
				instance.activeFrameType(newValue.field);
			}
		});
		
			
	};	
});