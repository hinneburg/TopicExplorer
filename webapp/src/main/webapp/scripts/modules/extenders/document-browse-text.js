define(["knockout", "jquery"],
function(ko, $) {
	return function (instance) {
		if($.grep(instance.browseData[instance.active()].textSelectArray(), function(obj) { return obj.field == 'TEXT$';}).length < 1) {
			var textRepresentation = new instance.TextRepresentation('Text', 'TEXT$');
			instance.browseData[instance.active()].textSelectArray.push(textRepresentation);
			instance.browseData[instance.active()].textSelection(textRepresentation);
		}
	};
});
