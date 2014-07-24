define(["knockout", "jquery"],
function(ko, $) {
	return function (instance) {
		if($.grep(instance.browseData[instance.active()].textSelectArray(), function(obj) { return obj.field == 'TEXT$';}).length < 1) {
			instance.browseData[instance.active()].textSelectArray.push(new instance.TextRepresentation('Text', 'TEXT$'));
		}
	};
});
