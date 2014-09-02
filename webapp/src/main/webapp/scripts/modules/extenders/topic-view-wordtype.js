define(["knockout", "jquery"],
function(ko, $) {
	return function (instance) {
		instance.wordtypeScrollCallback = function(el) {
			instance.checkScrollHeightForJumpStart(el);
			if(!instance.loading() && !globalData.Topic[el].FULL[instance.textSelection().field]() && $('#topic_' + el).children('div').children('.topicElementContent').height() +  $('#topic_' + el).children('div').children('.topicElementContent').scrollTop() >=  $('#topic_' + el).children('div').children('.topicElementContent')[0].scrollHeight - 35) {
				instance.loading(true);
				$.getJSON("JsonServlet?Command=getTerms&TopicId=" + el + "&offset=" + globalData.Topic[el].COUNT[instance.textSelection().field] + "&wordtype=" + instance.textSelection().field).success(function(receivedParsedJson) {
					var count = 0;
					wordtypeSorting = [];
					for(termId in receivedParsedJson.Topic[el].Top_Terms) {
						count++;	
						wordtypeSorting.push(receivedParsedJson.Topic[el].Top_Terms[termId].TermId);
						globalData.Topic[el].ITEMS[instance.textSelection().field][receivedParsedJson.Topic[el].Top_Terms[termId].TermId] = {};
						globalData.Topic[el].ITEMS[instance.textSelection().field][receivedParsedJson.Topic[el].Top_Terms[termId].TermId].ITEM_ID = receivedParsedJson.Topic[el].Top_Terms[termId].TermId;
						globalData.Topic[el].ITEMS[instance.textSelection().field][receivedParsedJson.Topic[el].Top_Terms[termId].TermId].ITEM_NAME = receivedParsedJson.Term[receivedParsedJson.Topic[el].Top_Terms[termId].TermId].TERM_NAME;
						globalData.Topic[el].ITEMS[instance.textSelection().field][receivedParsedJson.Topic[el].Top_Terms[termId].TermId].ITEM_COUNT = receivedParsedJson.Topic[el].Top_Terms[termId].relevance;			
					}
					
					if(count < 20) {
						globalData.Topic[el].FULL[instance.textSelection().field](true);
					}
					globalData.Topic[el].COUNT[instance.textSelection().field] += count;
					globalData.Topic[el].SORTING[instance.textSelection().field](globalData.Topic[el].SORTING[instance.textSelection().field]().concat(wordtypeSorting));
	
					instance.loading(false);
					
				});
			}
		};
			
		$.getJSON("JsonServlet?Command=getBestTerms").success(function(receivedParsedJson) {
			var firstTopic = true;
			for (topicId in receivedParsedJson) {
				$.extend(globalData.Topic[topicId].ITEMS, receivedParsedJson[topicId].ITEMS);
				
				for(wordType in receivedParsedJson[topicId].ITEMS) {
					var wordtypes = receivedParsedJson[topicId].ITEMS[wordType].SORTING;
				
					delete globalData.Topic[topicId].ITEMS[wordType].SORTING;
					delete receivedParsedJson[topicId].ITEMS[wordType].SORTING;
					globalData.Topic[topicId].COUNT[wordType] = 0;
					for(var i in receivedParsedJson[topicId].ITEMS[wordType]) {
						if(receivedParsedJson[topicId].ITEMS[wordType].hasOwnProperty(i) && i != 'SORTING') {
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
						instance.textSelectArray.push(new instance.TextRepresentation(wordType, wordType));	
						instance.scrollCallback[wordType] = instance.wordtypeScrollCallback;
						instance.loadDocumentsForItem[wordType] = instance.loadDocumentsForItem.KEYWORDS;
					}				
				}
				firstTopic = false;
			}
		});	
	};	
});