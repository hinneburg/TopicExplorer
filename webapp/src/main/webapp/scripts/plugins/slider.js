//Initialize your plugin
plugin.setTab = false;
plugin.view = '';
plugin.content = '';

plugin.init = function() {
	$('#topicModel > .bottom').html(template());
	
	ko.bindingHandlers.drawSlider = {
		update: function(elem,valueAccessor) {
			var child = $(elem).children('svg').children('g');
			$.each(valueAccessor(), function(index, value) {				
				var rect = $(SVG('rect')).attr('height', 13).attr('width', 1).attr('x', index).attr('y', 2).attr('title', value.TEXT$TOPIC_LABEL).attr('index', index).attr('fill', value.COLOR_TOPIC$COLOR()).attr('id','topic_'+value.TOPIC_ID).attr('stroke-width', '0.02').attr('stroke', 'black').attr('class','topicRec');
				rect.click(function(){
					console.log(value.id);
				});
				child.append(rect);
			});
		}
	};
};

function template()
{
	var template = '<div class="topicBottomSliderDiv" data-bind="with: topicData">'+
		'<div class="topicPrevElCont" data-bind="drawSlider: $data">'+
	'<svg style="z-index: 2;">'+
		'<g id="groupG"></g>'+
	'</svg>'+
'</div>'+
'<svg class="topicPrevSlider" height="15" x="0" y="0">'+
	'<rect fill="white" height="15" width="203.45389671361502" style="opacity: 0.5; stroke-width: 2; stroke: black; cursor: col-resize;" x="0"/>'+
	'<polygon fill="black" points="101.5,5 96.5,15 106.5,15"/>'+
'</svg>'+
'</div>';
	return template;
}