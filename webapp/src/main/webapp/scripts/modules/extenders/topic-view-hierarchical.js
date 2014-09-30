define(["knockout", "jquery"],
function(ko, $) {
	return function (instance) {
		if(globalData.PLUGINS.indexOf('hierarchicaltopic') > -1) {
			instance.topicListWidth  = ko.computed (function() {
				topicListWidth = 0;
				for(topicId in instance.selectedTopics()) {
					topicListWidth += (globalData.Topic[instance.selectedTopics()[topicId]].HIERARCHICAL_TOPIC$END - globalData.Topic[instance.selectedTopics()[topicId]].HIERARCHICAL_TOPIC$START) + 1;
				}
				return topicListWidth * 213 ;
			});
			
			instance.explodeTopic = function(topicId) {
				newSelectedTopics = instance.selectedTopics().slice();
				remTopicIndex = newSelectedTopics.indexOf(parseInt(topicId));
				newSelectedTopics.splice(remTopicIndex, 1);
				child = new Array(2);
				for(addTopicId in globalData.Topic) {
					if(parseInt(globalData.Topic[topicId].HIERARCHICAL_TOPIC$DEPTH) == parseInt(globalData.Topic[addTopicId].HIERARCHICAL_TOPIC$DEPTH) - 1) {
						if(parseInt(globalData.Topic[topicId].HIERARCHICAL_TOPIC$START) == parseInt(globalData.Topic[addTopicId].HIERARCHICAL_TOPIC$START)) {
							child[0] = parseInt(addTopicId);
						} else if(parseInt(globalData.Topic[topicId].HIERARCHICAL_TOPIC$END) == parseInt(globalData.Topic[addTopicId].HIERARCHICAL_TOPIC$END)) {
							child[1] = parseInt(addTopicId);
						}
					}
				}
				newSelectedTopics = newSelectedTopics.slice(0,remTopicIndex).concat(child).concat(newSelectedTopics.slice(remTopicIndex));
				instance.selectedTopics(newSelectedTopics);
			};
			instance.implodeTopics = function(topicId) {
				var index = instance.selectedTopics().indexOf(parseInt(topicId));
				node = getParentNode(index);
				if(node == -1) return;
				newSelectedTopics = instance.selectedTopics().slice();
				inputPosition = index; 
				remTopicIndex = -1;
				for(remTopicId in instance.selectedTopics()) {
					if(parseInt(globalData.Topic[node].HIERARCHICAL_TOPIC$START) <= parseInt(globalData.Topic[instance.selectedTopics()[remTopicId]].HIERARCHICAL_TOPIC$START)
						&& parseInt(globalData.Topic[node].HIERARCHICAL_TOPIC$END) >= parseInt(globalData.Topic[instance.selectedTopics()[remTopicId]].HIERARCHICAL_TOPIC$END)) {	
						remTopicIndex = newSelectedTopics.indexOf(parseInt(instance.selectedTopics()[remTopicId]));
						newSelectedTopics.splice(remTopicIndex, 1);
						if(remTopicIndex < inputPosition) {
							inputPosition = remTopicIndex;
						}
					} 
				}
				newSelectedTopics = newSelectedTopics.slice(0,inputPosition).concat([parseInt(node)]).concat(newSelectedTopics.slice(inputPosition));
				instance.selectedTopics(newSelectedTopics);
			};
			instance.markParentNode = function(topicId) {
				var index = instance.selectedTopics().indexOf(parseInt(topicId));
				node = getParentNode(index);
				if(node == -1) return;
				var first = true;
				var color= 'white';
				for(remTopicId in instance.selectedTopics()) {
					if(parseInt(globalData.Topic[node].HIERARCHICAL_TOPIC$START) <= parseInt(globalData.Topic[instance.selectedTopics()[remTopicId]].HIERARCHICAL_TOPIC$START)
						&& parseInt(globalData.Topic[node].HIERARCHICAL_TOPIC$END) >= parseInt(globalData.Topic[instance.selectedTopics()[remTopicId]].HIERARCHICAL_TOPIC$END)) {	
						if(first) {
							color = globalData.Topic[instance.selectedTopics()[remTopicId]].COLOR_TOPIC$COLOR;
							first = false;
						} else {
							$('#topic_' + instance.selectedTopics()[remTopicId]).css('border-left-color', color);
							$('.topicRect:eq(' + remTopicId + ')').css('border-left-color', color);
						}
						$('#topic_' + instance.selectedTopics()[remTopicId]).css('background-color', color);
						$('#topic_' + instance.selectedTopics()[remTopicId]).css('border-right-color', color);
						$('#sliderTopic_' + instance.selectedTopics()[remTopicId]).css('background-color', color);
						$('#sliderTopic_' + instance.selectedTopics()[remTopicId]).css('border-right-color', color);	
						lastIndex = remTopicId;
					}
				}
				$('#topic_' + instance.selectedTopics()[lastIndex]).css('border-right-color', 'black');
				$('.topicRect:eq(' + lastIndex + ')').css('border-right-color', 'black');
			};
			instance.unmarkParentNode = function(topicId) {
				for(remTopicId in instance.selectedTopics()) {
					$('#topic_' + instance.selectedTopics()[remTopicId]).css('background-color', globalData.Topic[instance.selectedTopics()[remTopicId]].COLOR_TOPIC$COLOR);
					$('#topic_' + instance.selectedTopics()[remTopicId]).css('border-color', 'black');
					$('#sliderTopic_' + instance.selectedTopics()[remTopicId]).css('background-color', globalData.Topic[instance.selectedTopics()[remTopicId]].COLOR_TOPIC$COLOR);
					$('#sliderTopic_' + instance.selectedTopics()[remTopicId]).css('border-right-color', 'black');	
				}	
			};
			
			function getParentNode(index) {
				var start = parseInt(globalData.Topic[instance.selectedTopics()[index]].HIERARCHICAL_TOPIC$START);
				var end =  parseInt(globalData.Topic[instance.selectedTopics()[index + 1]].HIERARCHICAL_TOPIC$END);
				var depth = -1;
				var newTopicId = -1;
				for(topicId in globalData.Topic) {
					if(parseInt(globalData.Topic[topicId].HIERARCHICAL_TOPIC$START) <= start 
							&& parseInt(globalData.Topic[topicId].HIERARCHICAL_TOPIC$END) >= end 
							&& depth < globalData.Topic[topicId].HIERARCHICAL_TOPIC$DEPTH) {
						depth = globalData.Topic[topicId].HIERARCHICAL_TOPIC$DEPTH;
						newTopicId = topicId;
					}
				}
				return newTopicId;
			};
			
		}
	};
});