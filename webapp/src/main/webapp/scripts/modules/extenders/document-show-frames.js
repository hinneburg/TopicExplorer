define(["knockout"],
function(ko) {
	return function (instance) {
		var frames = instance.singleData[instance.active()].DOCUMENT.FRAME_LIST;
		var text = instance.singleData[instance.active()].DOCUMENT.TEXT$FULLTEXT;
		var text2 = text;
		var text3 = text;
		var text4 = text;
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
			frameSpan = text.substring(0, frames[key].START_POSITION)
				+ '<span style="border-bottom: 2px solid '
				+ globalData.Topic[frames[key].TOPIC_ID].COLOR_TOPIC$COLOR
				+ ';" class="topicWord" id="t_' + frames[key].TOPIC_ID + '" '
				+ 'title="Topic ' + frames[key].TOPIC_ID + '">'
				+ text.substring(frames[key].START_POSITION, frames[key].END_POSITION)
				+ '</span>';
			if(frames[key].ACTIVE == 1) {
				if(frames[key].END_POSITION < lastStart) { // avoid covering each other
					text = frameSpan + text.substring(parseInt(frames[key].END_POSITION));
					lastStart = frames[key].START_POSITION;
					textActive = true;
				}
				if(typeof instance.singleData[instance.active()].data.topicId != 'undefined') {
					if(frames[key].TOPIC_ID == instance.singleData[instance.active()].data.topicId) {
						if(frames[key].END_POSITION < lastStart3) { // avoid covering each other
							text3 = frameSpan + text3.substring(parseInt(frames[key].END_POSITION));
							lastStart3 = frames[key].START_POSITION;
							textTopic = true;
						}
						if(typeof instance.singleData[instance.active()].data.frame != 'undefined') {
							if(frames[key].FRAME == instance.singleData[instance.active()].data.frame) {
								if(frames[key].END_POSITION < lastStart4) { // avoid covering each other
									text4 = frameSpan + text4.substring(parseInt(frames[key].END_POSITION));
									lastStart4 = frames[key].START_POSITION;
									textTopicFrame = true;
								}
							}
						}
					}
				}
			} else {
				if(frames[key].END_POSITION < lastStart2) { // avoid covering each other
					text2 = frameSpan + text2.substring(parseInt(frames[key].END_POSITION));
					lastStart2 = frames[key].START_POSITION;
					textInactive = true;
				}
			}
			
		} 
		if(textActive) {
			instance.singleData[instance.active()].TITLE_REPRESENTATION.TEXT_ACTIVE_FRAMES = instance.singleData[instance.active()].DOCUMENT.TEXT$TITLE;
			instance.singleData[instance.active()].TEXT_REPRESENTATION.TEXT_ACTIVE_FRAMES = text;
			instance.singleData[instance.active()].textSelectArray.push(new instance.TextRepresentation('Text with Frames', 'TEXT_ACTIVE_FRAMES'));
		}
		if(textInactive) {
			instance.singleData[instance.active()].TITLE_REPRESENTATION.TEXT_INACTIVE_FRAMES = instance.singleData[instance.active()].DOCUMENT.TEXT$TITLE;
			instance.singleData[instance.active()].TEXT_REPRESENTATION.TEXT_INACTIVE_FRAMES = text2;
			instance.singleData[instance.active()].textSelectArray.push(new instance.TextRepresentation('Text with inactive Frames', 'TEXT_INACTIVE_FRAMES'));
		}
		if(textTopic) {
			instance.singleData[instance.active()].TITLE_REPRESENTATION.TEXT_TOPIC_FRAMES = instance.singleData[instance.active()].DOCUMENT.TEXT$TITLE;
			instance.singleData[instance.active()].TEXT_REPRESENTATION.TEXT_TOPIC_FRAMES = text3;
			instance.singleData[instance.active()].textSelectArray.push(new instance.TextRepresentation('Text with Frames of Topic ' + instance.singleData[instance.active()].data.topicId, 'TEXT_TOPIC_FRAMES'));
		}
		if(textTopicFrame) {
			instance.singleData[instance.active()].TITLE_REPRESENTATION.TEXT_TOPIC_FRAME_FRAMES = instance.singleData[instance.active()].DOCUMENT.TEXT$TITLE;
			instance.singleData[instance.active()].TEXT_REPRESENTATION.TEXT_TOPIC_FRAME_FRAMES = text4;
			instance.singleData[instance.active()].textSelectArray.push(new instance.TextRepresentation('Text with Frame '  + instance.singleData[instance.active()].data.frame + ' and Topic ' + instance.singleData[instance.active()].data.topicId, 'TEXT_TOPIC_FRAME_FRAMES'));
		}
	};
});
