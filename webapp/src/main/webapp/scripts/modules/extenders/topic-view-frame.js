define(["knockout", "jquery"],
function(ko, $) {
	return function (instance) {
		instance.loadDocumentsForFrames = function (frameIndex, context) { 
			var topicId = $(context.target).parents('.topic').attr('id').split('_')[1];
			var frame = globalData.Topic[topicId].ITEMS[instance.textSelection().field][frameIndex].ITEM_NAME; 
			ko.postbox.publish('openNewTab', {moduleName:"document-browse-tab", tabHeading:"Topic " + topicId + " (" + frame + ")", data: {topicId: topicId, frame: frame, frameType: instance.textSelection().field, getParam: "bestDocs&TopicId=" + topicId + "&frame=" + frame + "&frameType=" + instance.textSelection().field}});	
		};
		
		
		instance.frameScrollCallback = function(el) {
			instance.checkScrollHeightForJumpStart(el);
			if(!instance.loading() && !globalData.Topic[el].FULL[instance.textSelection().field]() && $('#topic_' + el).children('div').children('.topicElementContent').height() +  $('#topic_' + el).children('div').children('.topicElementContent').scrollTop() >=  $('#topic_' + el).children('div').children('.topicElementContent')[0].scrollHeight - 35) {
				instance.loading(true);
				$.getJSON("JsonServlet?Command=getFrames&topicId=" + el + "&offset=" + globalData.Topic[el].COUNT[instance.textSelection().field] + "&frameType=" + instance.textSelection().field).success(function(receivedParsedJson) {
					$.extend(globalData.Topic[el].ITEMS[instance.textSelection().field], receivedParsedJson[el].FRAMES);
					globalData.Topic[el].SORTING[instance.textSelection().field](globalData.Topic[el].SORTING[instance.textSelection().field]().concat(receivedParsedJson[el].SORTING));
					globalData.Topic[el].COUNT[instance.textSelection().field] += receivedParsedJson[el].SORTING.length;
					if(receivedParsedJson[el].SORTING.length < 20) {
						globalData.Topic[el].FULL[instance.textSelection().field](true);
					}	
					instance.loading(false);
					
				});
			}
		};	
		
		
		$.getJSON("JsonServlet?Command=getBestFrames").success(function(receivedParsedJson) {
			var firstTopic = true;
			for (topicId in receivedParsedJson) {
				$.extend(globalData.Topic[topicId].ITEMS, receivedParsedJson[topicId].ITEMS);
			
				for(frameType in receivedParsedJson[topicId].ITEMS) {
					var frames = receivedParsedJson[topicId].ITEMS[frameType].SORTING;
				
					delete globalData.Topic[topicId].ITEMS[frameType].SORTING;
					delete receivedParsedJson[topicId].ITEMS[frameType].SORTING;
					globalData.Topic[topicId].COUNT[frameType] = 0;
					for(var i in receivedParsedJson[topicId].ITEMS[frameType]) {
						if(receivedParsedJson[topicId].ITEMS[frameType].hasOwnProperty(i) && i != 'frameCount') {
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
						instance.textSelectArray.push(new instance.TextRepresentation('Frames (' + frameType + ')' , frameType));	
						instance.scrollCallback[frameType] = instance.frameScrollCallback;
						instance.loadDocumentsForItem[frameType] = instance.loadDocumentsForFrames;
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
	};	
});