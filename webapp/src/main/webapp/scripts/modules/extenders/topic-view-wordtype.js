define(["knockout", "jquery"],
function(ko, $) {
	return function (instance) {
		instance.activeWordType = ko.observable();
		
		instance.wordtypeScrollCallback = function(el) {
			instance.checkScrollHeightForJumpStart(el);
			if(!instance.loading() && !globalData.Topic[el].FULL[instance.activeWordType()]() && $('#topic_' + el).children('div').children('.topicElementContent').height() +  $('#topic_' + el).children('div').children('.topicElementContent').scrollTop() >=  $('#topic_' + el).children('div').children('.topicElementContent')[0].scrollHeight - 35) {
				instance.loading(true);
				$.getJSON("JsonServlet?Command=getTerms&TopicId=" + el + "&offset=" + globalData.Topic[el].COUNT[instance.activeWordType()] + "&wordtype=" + instance.activeWordType()).success(function(receivedParsedJson) {
					$.extend(globalData.Topic[el].WORDTYPES[instance.activeWordType()], receivedParsedJson.Term); 
					wordtypeSorting = [];
					for(var j = 0; j <  receivedParsedJson.Topic[el].Top_Terms.length; j++) {
						wordtypeSorting.push(receivedParsedJson.Topic[el].Top_Terms[j].TermId);
						globalData.Topic[el].WORDTYPES[instance.activeWordType()][receivedParsedJson.Topic[el].Top_Terms[j].TermId].NUMBER_OF_DOCUMENT_TOPIC = receivedParsedJson.Topic[el].Top_Terms[j].relevance;
					}
					globalData.Topic[el].SORTING[instance.activeWordType()](globalData.Topic[el].SORTING[instance.activeWordType()]().concat(wordtypeSorting));
					globalData.Topic[el].COUNT[instance.activeWordType()] += wordtypeSorting.length;
					if(wordtypeSorting.length < 20) {
						globalData.Topic[el].FULL[instance.activeWordType()](true);
					}	
					instance.loading(false);
					
				});
			}
		};
		
		var firstTopic = true;
		$.getJSON("JsonServlet?Command=getBestTerms").success(function(receivedParsedJson) {
			for (topicId in receivedParsedJson) {
				$.extend(globalData.Topic[topicId], receivedParsedJson[topicId]);
			
				for(wordType in receivedParsedJson[topicId].WORDTYPES) {
					var wordtypes = receivedParsedJson[topicId].WORDTYPES[wordType].SORTING;
				
					delete globalData.Topic[topicId].WORDTYPES[wordType].SORTING;
					delete receivedParsedJson[topicId].WORDTYPES[wordType].SORTING;
					globalData.Topic[topicId].COUNT[wordType] = 0;
					for(var i in receivedParsedJson[topicId].WORDTYPES[wordType]) {
						if(receivedParsedJson[topicId].WORDTYPES[wordType].hasOwnProperty(i) && i != 'wordtypeCount') {
							globalData.Topic[topicId].COUNT[wordType]++;
						}
					}
					globalData.Topic[topicId].SORTING[wordType] = ko.observableArray(wordtypes);
					
					globalData.Topic[topicId].FULL[wordType] = ko.observable(false);
					
					if(globalData.Topic[topicId].COUNT[wordType] < 10) {
						globalData.Topic[topicId].FULL[wordType](true);
					} 
					
					instance.Topic[topicId].TITLE_REPRESENTATION[wordType] = instance.Topic[topicId].TITLE_REPRESENTATION.KEYWORDS;
					instance.Topic[topicId].INFO_HIGHLIGHT[wordType] = "";
					if(firstTopic) {
						instance.bodyTemplate[wordType] = 'extenders/topic-wordtype';
						instance.textSelectArray.push(new instance.TextRepresentation(wordType , wordType));	
						instance.scrollCallback[wordType] = instance.wordtypeScrollCallback;
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
		
				
	};	
});