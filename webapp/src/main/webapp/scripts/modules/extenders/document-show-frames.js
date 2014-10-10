define(["knockout", "jquery"],
function(ko, $) {
	return function (instance) {
		instance.frameSingleData = {};
		instance.frameSingleData.selectedTopics = ko.observableArray(globalData.TOPIC_SORTING).subscribeTo("selectedTopics");
		instance.frameSingleData.frameTypes = instance.singleData[instance.active()].DOCUMENT.FRAME_LIST;
		
		generateFrameTexts = function() {
			for(frameType in instance.frameSingleData.frameTypes) {
				var frames = instance.singleData[instance.active()].DOCUMENT.FRAME_LIST[frameType];
				
				var text = instance.singleData[instance.active()].DOCUMENT.TEXT$FULLTEXT; // text with all frames
				var text2 = text; // text with all inactive frames
				var text3 = text; // text with all frames of topic x
				var text4 = text; // text with all frames of topic x and type y
				var lastStart = text.length;
				var lastStart2  = text.length;
				var lastStart3  = text.length;
				var lastStart4  = text.length;
				var frameSpan;
				var textActive = false;
				var textInactive = false;
				var textTopic = false;
				var textTopicFrame =  false;
				
				for(key in frames) {
					if(instance.frameSingleData.selectedTopics().indexOf(parseInt(frames[key].TOPIC_ID)) > -1) {
						frameSpan = text.substring(0, frames[key].START_POSITION)
							+ '<span style="border-bottom: 2px solid '
							+ globalData.Topic[frames[key].TOPIC_ID].COLOR_TOPIC$COLOR
							+ ';" class="topicWord" id="t_' + frames[key].TOPIC_ID + '" '
							+ 'title="Topic ' + frames[key].TOPIC_ID + '">'
							+ text.substring(frames[key].START_POSITION, frames[key].END_POSITION)
							+ '</span>';
						if(frames[key].ACTIVE == 1) {
							if(parseInt(frames[key].END_POSITION) < lastStart) { // avoid covering each other
								text = frameSpan + text.substring(parseInt(frames[key].END_POSITION));
								lastStart = parseInt(frames[key].START_POSITION);
								textActive = true;
							} 
							if(typeof instance.singleData[instance.active()].data.topicId != 'undefined') {
								if(frames[key].TOPIC_ID == instance.singleData[instance.active()].data.topicId) {
									if(parseInt(frames[key].END_POSITION) < lastStart3) { // avoid covering each other
										text3 = frameSpan + text3.substring(parseInt(frames[key].END_POSITION));
										lastStart3 = parseInt(frames[key].START_POSITION);
										textTopic = true;
									}
									if(typeof instance.singleData[instance.active()].data.frame != 'undefined') {
										if(frames[key].FRAME == instance.singleData[instance.active()].data.frame) {
											if(parseInt(frames[key].END_POSITION) < lastStart4) { // avoid covering each other
												text4 = frameSpan + text4.substring(parseInt(frames[key].END_POSITION));
												lastStart4 = parseInt(frames[key].START_POSITION);
												textTopicFrame = true;
											}
										}
									}
								}
							}
						} else {
							if(parseInt(frames[key].END_POSITION) < lastStart2) { // avoid covering each other
								text2 = frameSpan + text2.substring(parseInt(frames[key].END_POSITION));
								lastStart2 = parseInt(frames[key].START_POSITION);
								textInactive = true;
							}
						}
					}
				} 
				if(textActive) {
					instance.singleData[instance.active()].TITLE_REPRESENTATION['TEXT_ACTIVE_FRAMES_' + frameType] = instance.singleData[instance.active()].DOCUMENT.TEXT$TITLE;
					instance.singleData[instance.active()].TEXT_REPRESENTATION['TEXT_ACTIVE_FRAMES_' + frameType] = text;
					instance.singleData[instance.active()].textSelectArray.push(new instance.TextRepresentation('Text with Frames of type ' + frameType, 'TEXT_ACTIVE_FRAMES_' + frameType));
				}
				if(textInactive) {
					instance.singleData[instance.active()].TITLE_REPRESENTATION['TEXT_INACTIVE_FRAMES_' + frameType] = instance.singleData[instance.active()].DOCUMENT.TEXT$TITLE;
					instance.singleData[instance.active()].TEXT_REPRESENTATION['TEXT_INACTIVE_FRAMES_' + frameType] = text2;
					instance.singleData[instance.active()].textSelectArray.push(new instance.TextRepresentation('Text with inactive Frames of type ' + frameType, 'TEXT_INACTIVE_FRAMES_' + frameType));
				}
				if(textTopic) {
					instance.singleData[instance.active()].TITLE_REPRESENTATION['TEXT_TOPIC_FRAMES_' + frameType] = instance.singleData[instance.active()].DOCUMENT.TEXT$TITLE;
					instance.singleData[instance.active()].TEXT_REPRESENTATION['TEXT_TOPIC_FRAMES_' + frameType] = text3;
					if(instance.frameSingleData.selectedTopics().indexOf(parseInt(instance.singleData[instance.active()].data.topicId)) > -1)
						instance.singleData[instance.active()].textSelectArray.push(new instance.TextRepresentation('Text with Frames of type ' + frameType + ' and Topic ' + instance.singleData[instance.active()].data.topicId, 'TEXT_TOPIC_FRAMES_' + frameType));
					else
						instance.singleData[instance.active()].textSelectArray.push(new instance.TextRepresentation('Text with Frames of type ' + frameType + ' and Topic ' + instance.singleData[instance.active()].data.topicId, 'TEXT_TOPIC_FRAMES_' + frameType));
				}
				if(textTopicFrame) {
					instance.singleData[instance.active()].TITLE_REPRESENTATION['TEXT_TOPIC_FRAME_FRAMES_' + frameType] = instance.singleData[instance.active()].DOCUMENT.TEXT$TITLE;
					instance.singleData[instance.active()].TEXT_REPRESENTATION['TEXT_TOPIC_FRAME_FRAMES_' + frameType] = text4;
					instance.singleData[instance.active()].textSelectArray.push(new instance.TextRepresentation('Text with Frame "'  + instance.singleData[instance.active()].data.frame + '" and Topic ' + instance.singleData[instance.active()].data.topicId, 'TEXT_TOPIC_FRAME_FRAMES_' + frameType));
				}
			};
		};	
		instance.frameSingleData.selectedTopics.subscribe(function(newValue) {
			var selectArray = instance.singleData[instance.active()].textSelectArray().slice();
			oldSelection = instance.singleData[instance.active()].textSelection().label;
			for(var i = selectArray.length - 1; i > 0 ; i--) {
				if(selectArray[i].field.indexOf('FRAME') > -1) {
					selectArray.splice(i, 1);
				}
			}
			instance.singleData[instance.active()].textSelectArray(selectArray);
			generateFrameTexts();
			var pos = instance.singleData[instance.active()].textSelectArray().map(function(e) { 
				return e.label; }).indexOf(oldSelection);
			if(pos > -1) {
				instance.singleData[instance.active()].textSelection(instance.singleData[instance.active()].textSelectArray()[pos]);
			} else {
				$('#desktop').animate({scrollTop: 0});
			}
		});
			
		generateFrameTexts();	
	};
});
