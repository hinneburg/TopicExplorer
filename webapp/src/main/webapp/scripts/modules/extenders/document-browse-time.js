define(["knockout", "jquery", "moment"],
function(ko, $, moment) {
	return function (instance) {
		if(globalData.PLUGINS.indexOf('time') > -1) {
			
			if(typeof instance.browseData.timeData == 'undefined') {
				instance.browseData.timeData = {};
				$.getJSON("JsonServlet?Command=GetDateRange")
				.success(function(receivedParsedJson) {
					instance.browseData.timeData.minDate = receivedParsedJson.DateRange[0] * 1000;
					instance.browseData.timeData.maxDate = receivedParsedJson.DateRange[1] * 1000;
					$.datepicker.setDefaults({
						changeMonth: true, 
						changeYear: true,
						minDate: new Date(instance.browseData.timeData.minDate),
						maxDate: new Date(instance.browseData.timeData.maxDate)
					});
					instance.browseData.activateDatePicker()
				});
				
				instance.browseData.activateDatePicker = function() {
					if(instance.browseData[instance.active()].filter.firstDate().length == 0) {
						$.datepicker.setDefaults({
							minDate: new Date(instance.browseData.timeData.minDate)
						});
					} else {
						$.datepicker.setDefaults({
							minDate: new Date(instance.browseData[instance.active()].filter.firstDate())
						});
					}
					
					if(instance.browseData[instance.active()].filter.lastDate().length == 0) {
						$.datepicker.setDefaults({
							maxDate: new Date(instance.browseData.timeData.maxDate)
						});
					} else {
						$.datepicker.setDefaults({
							maxDate: new Date(instance.browseData[instance.active()].filter.lastDate())
						});
					}
						
					$(".datepicker").datepicker();
					$(".datepicker").prop('readOnly', true);
					
				}
				
				$(document).delegate(".datepicker", "focusin", function(){
					$(this).datepicker();
					$(this).prop('readOnly', true);
				});
				
			} 
			if(instance.browseData[instance.active()].sortingOptions.indexOf('Time') == -1) {
				instance.browseData[instance.active()].activeFilter.firstDate = ko.observable("");
				instance.browseData[instance.active()].activeFilter.lastDate = ko.observable("");
				
				instance.browseData[instance.active()].filter.firstDate = ko.observable("");
				instance.browseData[instance.active()].filter.lastDate = ko.observable("");
				
				instance.browseData[instance.active()].filter.firstDate.subscribe(instance.browseData.activateDatePicker);
				instance.browseData[instance.active()].filter.lastDate.subscribe(instance.browseData.activateDatePicker);
				
				
				instance.browseData[instance.active()].sortingOptions.push('Time');
				
				
			}
			instance.browseData.activateDatePicker();
			
			
		}
		
	};	
});