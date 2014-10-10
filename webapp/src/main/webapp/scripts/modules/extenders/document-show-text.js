define(["knockout", "jquery"],
function(ko, $) {
	return function (instance) {
		$(document).delegate('.topicWord', 'click', function() {
			instance.moveToTopic($(this).attr('id').split('_')[1]);
		});
		instance.singleData.selectedTextTopics = ko.observableArray(globalData.TOPIC_SORTING).subscribeTo("selectedTopics");
		instance.singleData.selectedTextTopics.subscribe(function(newValue){
			for(topicIndex in newValue) {
				$('.singleTopic_' + newValue[topicIndex]).attr('title', 'Topic ' + newValue[topicIndex]);
				$('.singleTopic_' + newValue[topicIndex]).attr('id', 't_' + newValue[topicIndex]);
				$('.singleTopic_' + newValue[topicIndex]).css('border-color', globalData.Topic[newValue[topicIndex]].COLOR_TOPIC$COLOR);
			}
			
		});
		
		var words = instance.singleData[instance.active()].DOCUMENT.WORD_LIST;
		var text = instance.singleData[instance.active()].DOCUMENT.TEXT$FULLTEXT;
		var lastStart = text.length;
		for (key in words) {
			if(parseInt(words[key].POSITION_OF_TOKEN_IN_DOCUMENT) + words[key].TOKEN.length <= lastStart) { // avoid covering each other
				var singleTopicString = 'singleTopic_' + words[key].TOPIC_ID;
				if(instance.singleData.selectedTextTopics().indexOf(parseInt(words[key].TOPIC_ID)) > -1)
					topicId = words[key].TOPIC_ID;
				for(var i = 0; i < words[key].HIERARCHICAL_TOPIC$PARENT_IDS.length; i++) {
					singleTopicString += ' singleTopic_' + words[key].HIERARCHICAL_TOPIC$PARENT_IDS[i];
					if(instance.singleData.selectedTextTopics().indexOf(parseInt(words[key].HIERARCHICAL_TOPIC$PARENT_IDS[i])) > -1)
						topicId = words[key].HIERARCHICAL_TOPIC$PARENT_IDS[i];
				}
				color = globalData.Topic[topicId].COLOR_TOPIC$COLOR;
				text = text.substring(0, words[key].POSITION_OF_TOKEN_IN_DOCUMENT)
					+ '<span style="border-bottom: 2px solid '
					+ color
					+ ';" class="topicWord ' + singleTopicString
					+ '" id="t_' + topicId + '" '
					+ 'title="Topic ' + topicId + '">'
					+ words[key].TOKEN
					+ '</span>'
					+ text.substring(parseInt(words[key].POSITION_OF_TOKEN_IN_DOCUMENT)
					+ words[key].TOKEN.length);
				lastStart = words[key].POSITION_OF_TOKEN_IN_DOCUMENT;
			} else {
				console.warn("Covering keywords found: doc " + instance.active() + ", token " + words[key].TOKEN + ", pos " + words[key].POSITION_OF_TOKEN_IN_DOCUMENT);
			}
		}
		instance.singleData[instance.active()].TITLE_REPRESENTATION.TEXT_ONLY = instance.singleData[instance.active()].DOCUMENT.TEXT$TITLE;
		instance.singleData[instance.active()].TEXT_REPRESENTATION.TEXT_ONLY = instance.singleData[instance.active()].DOCUMENT.TEXT$FULLTEXT;
		instance.singleData[instance.active()].textSelectArray.push(new instance.TextRepresentation('Just the Text','TEXT_ONLY'));
		
		instance.singleData[instance.active()].TITLE_REPRESENTATION.TEXT_KEYWORDS = instance.singleData[instance.active()].DOCUMENT.TEXT$TITLE;
		instance.singleData[instance.active()].TEXT_REPRESENTATION.TEXT_KEYWORDS = text;
		instance.singleData[instance.active()].textSelectArray.push(new instance.TextRepresentation('Text with Keywords','TEXT_KEYWORDS'));
	};
});
