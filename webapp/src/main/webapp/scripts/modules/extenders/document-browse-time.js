define(["knockout", "jquery", "moment"],
function(ko, $, moment) {
	return function (instance) {
		if(globalData.PLUGINS.indexOf('time') > -1) {
			instance.activateDatePicker = function() {
				$( ".datepicker" ).datepicker();
			}
			if(instance.browseData[instance.active()].sortingOptions.indexOf('Time') == -1) {
				instance.browseData[instance.active()].activeFilter.firstDate = ko.observable("");
				instance.browseData[instance.active()].activeFilter.lastDate = ko.observable("");
				
				instance.browseData[instance.active()].filter.firstDate = ko.observable("");
				instance.browseData[instance.active()].filter.lastDate = ko.observable("");
				
				instance.browseData[instance.active()].filter.firstDate.subscribe(instance.activateDatePicker);
				instance.browseData[instance.active()].filter.lastDate.subscribe(instance.activateDatePicker);
				
				
				instance.browseData[instance.active()].sortingOptions.push('Time');
				
				$.getJSON("JsonServlet?Command=GetDateRange")
					.success(function(receivedParsedJson) {
						$.datepicker.setDefaults({
							minDate: new Date(receivedParsedJson.DateRange[0] * 1000),
							maxDate: new Date(receivedParsedJson.DateRange[1] * 1000)
						});
						$( ".datepicker" ).datepicker();
				});
				
			}
			
			
			
		}
		
	};	
});