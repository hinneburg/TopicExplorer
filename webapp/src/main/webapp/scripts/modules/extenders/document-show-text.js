define(["knockout", "jquery"],
function(ko, $) {
	return function (instance) {
		$('#tab').delegate('.topicWord', 'click', function() {
			instance.moveToTopic($(this).attr('id').split('_')[1]);
		});
		var words = instance.singleData[instance.active()].DOCUMENT.WORD_LIST;
		var text = instance.singleData[instance.active()].DOCUMENT.TEXT$FULLTEXT;
		var lastStart = text.length;
		for (key in words) {
			if(parseInt(words[key].POSITION_OF_TOKEN_IN_DOCUMENT) + words[key].TOKEN.length <= lastStart) { // avoid covering each other
				text = text.substring(0, words[key].POSITION_OF_TOKEN_IN_DOCUMENT)
					+ '<span style="border-bottom: 2px solid '
					+ globalData.Topic[words[key].TOPIC_ID].COLOR_TOPIC$COLOR
					+ ';" class="topicWord" id="t_' + words[key].TOPIC_ID + '" '
					+ 'title="Topic ' + words[key].TOPIC_ID + '">'
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
