define(["knockout", "jquery"],
function(ko, $) {
	return function (instance) {
		instance.activeWordType = ko.observable();
		var firstTopic = true;
		$.getJSON("JsonServlet?Command=getBestTerms").success(function(receivedParsedJson) {
			for (topicId in receivedParsedJson) {
				$.extend(globalData.Topic[topicId], receivedParsedJson[topicId]);
			
				for(wordType in receivedParsedJson[topicId].WORDTYPES) {
					var wordtypes = receivedParsedJson[topicId].WORDTYPES[wordType].SORTING;
				
					delete globalData.Topic[topicId].WORDTYPES[wordType].SORTING;
					delete receivedParsedJson[topicId].WORDTYPES[wordType].SORTING;
					globalData.Topic[topicId].WORDTYPES[wordType].wordtypeCount = 0;
					for(var i in receivedParsedJson[topicId].WORDTYPES[wordType]) {
						if(receivedParsedJson[topicId].WORDTYPES[wordType].hasOwnProperty(i) && i != 'wordtypeCount') {
							globalData.Topic[topicId].WORDTYPES[wordType].wordtypeCount++;
						}
					}
					globalData.Topic[topicId].WORDTYPES[wordType].wordtypeSorting = ko.observableArray(wordtypes);
					
					globalData.Topic[topicId].WORDTYPES[wordType].wordtypeFull = ko.observable(false);
					
					if(globalData.Topic[topicId].WORDTYPES[wordType].wordtypeCount < 10) {
						globalData.Topic[topicId].WORDTYPES[wordType].wordtypeFull(true);
					} 
					
					instance.Topic[topicId].TITLE_REPRESENTATION[wordType] = instance.Topic[topicId].TITLE_REPRESENTATION.KEYWORDS;
					if(firstTopic) {
						instance.bodyTemplate[wordType] = 'extenders/topic-wordtype';
						instance.textSelectArray.push(new instance.TextRepresentation(wordType , wordType));		
					}

				}
				firstTopic = false;
			}
		});
		
		
		
		instance.textSelection.subscribe(function(newValue) {
			if(globalData.Topic[topicId].WORDTYPES.hasOwnProperty(newValue.field)){
				instance.activeWordType(newValue.field);
			}
		});
		
		instance.wordtypesLoading = ko.observable(false);
		
		instance.wordtypeScrollCallback = function(el) {
			instance.checkScrollHeightForJumpStart(el);
			if(!instance.wordtypesLoading() && !globalData.Topic[el].WORDTYPES[instance.activeWordType()].wordtypeFull() && $('#topic_' + el).children('div').children('.topicElementContent').height() +  $('#topic_' + el).children('div').children('.topicElementContent').scrollTop() >=  $('#topic_' + el).children('div').children('.topicElementContent')[0].scrollHeight - 10) {
				instance.wordtypesLoading(true);
				$.getJSON("JsonServlet?Command=getTerms&TopicId=" + el + "&offset=" + globalData.Topic[el].WORDTYPES[instance.activeWordType()].wordtypeCount + "&wordtype=" + instance.activeWordType()).success(function(receivedParsedJson) {
					$.extend(globalData.Topic[el].WORDTYPES[instance.activeWordType()], receivedParsedJson.Term); 
					wordtypeSorting = [];
					for(var j = 0; j <  receivedParsedJson.Topic[el].Top_Terms.length; j++) {
						wordtypeSorting.push(receivedParsedJson.Topic[el].Top_Terms[j].TermId);
						globalData.Topic[el].WORDTYPES[instance.activeWordType()][receivedParsedJson.Topic[el].Top_Terms[j].TermId].NUMBER_OF_DOCUMENT_TOPIC = receivedParsedJson.Topic[el].Top_Terms[j].relevance;
					}
					globalData.Topic[el].WORDTYPES[instance.activeWordType()].wordtypeSorting(globalData.Topic[el].WORDTYPES[instance.activeWordType()].wordtypeSorting().concat(wordtypeSorting));
					globalData.Topic[el].WORDTYPES[instance.activeWordType()].wordtypeCount += wordtypeSorting.length;
					if(wordtypeSorting.length < 20) {
						globalData.Topic[el].WORDTYPES[instance.activeWordType()].wordtypeFull(true);
					}	
					instance.wordtypesLoading(false);
					
				});
			}
		};		
	};	
});