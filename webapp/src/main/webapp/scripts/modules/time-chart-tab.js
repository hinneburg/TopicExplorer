define([ "knockout", "jquery", "moment", "highstock", "filesaver"],
function(ko, $, moment) {
	var instance;
    function Singleton (data) {    	
    	var timeoutId = null;
    	$(document).delegate(".topicCheckbox", "mouseover", function(){
    		var self = this;
    		if (!timeoutId) {
    		//	$(self).css('cursor', 'progress').children('*').css('cursor', 'progress');
    	        timeoutId = window.setTimeout(function() {
    	            timeoutId = null; 
    	            $(self).css('cursor', 'pointer').children('*').css('cursor', 'pointer');
    	            ko.postbox.publish('moveToTopic',$(self).children('input').attr('id').split('_')[1]);
    		    }, 1500);
    		}
    	}).delegate(".topicCheckbox", "mouseout", function(){
    		if (timeoutId) {
    			$(this).css('cursor', 'progress').children('*').css('cursor', 'progress');
    		    window.clearTimeout(timeoutId);
    		    timeoutId = null;
    		}
    	});
    	
    	
    	var self = {};
    	
    	self.windowHeight = ko.observable(Math.max(400, $(window).height(), /* For opera: */ document.documentElement.clientHeight)).subscribeTo("windowHeight");
    	self.windowHeight.subscribe(function(newValue) {
    		self.chart.setSize($('#desktop').width(),((newValue - 184) * 0.7) - ($('#topicCheckboxes').height() + 32.0 * $('#getcsv').size()), false);
    	});
    	
		self.timeDataLoaded = false;
		self.loadTimeData = function() {
			$.getJSON("JsonServlet?Command=getDates").success(function(receivedParsedJson) {
				var avg = new Object();
				
				for (key in receivedParsedJson) {
					$.extend(globalData.Topic[key], receivedParsedJson[key]);
				    $.each(receivedParsedJson[key].TIME$WORDS_PER_WEEK, function(index, value) {
			    		if(avg[index] == null) avg[index] = 0;
			    		avg[index] += value.WORD_COUNT;
	
				    });
				}
				var topicCount = Object.keys(globalData.Topic).length;
				
				globalData.Topic.average = new Object();
				globalData.Topic.average.COLOR_TOPIC$COLOR = "#000000";
				globalData.Topic.average.TIME$WORDS_PER_WEEK = new Object();
				$.each(avg , function(weekStamp, value) {
					globalData.Topic.average.TIME$WORDS_PER_WEEK[weekStamp] = {'WORD_COUNT':Math.round(value / topicCount), 'LABEL': 'Average'};
				});
				self.makeChart();
				self.timeDataLoaded = true;
			});
		};
		
		self.timeData = {};
		
		self.downloadCSV = function() {
    		try {
    		    var isFileSaverSupported = !!new Blob;
    		    var out = "id;color";
        		for(tstamp in topicexplorerModel.data.topic[0].TIME$WORDS_PER_WEEK)
        			out += ";" + tstamp;
        		out += "\n";
        		for(topic in self.renderedTopics()) {
        			out += self.renderedTopics()[topic] + ";" + topicexplorerModel.data.topic[self.renderedTopics()[topic]].COLOR_TOPIC$COLOR;
        			if(self.renderedTopics()[topic] != "average") {
        				for(tstamp in topicexplorerModel.data.topic[self.renderedTopics()[topic]].TIME$WORDS_PER_WEEK) {
        					out += ";" + topicexplorerModel.data.topic[self.renderedTopics()[topic]].TIME$WORDS_PER_WEEK[tstamp].WORD_COUNT;
        				}
        				out += "\n";
        			} 		
        		}
        		var blob = new Blob([out], {type: "application/csv;charset=utf-8"});
        		saveAs(blob, "data.csv");
    		} catch (e) {
    			alert("Download is not supported by your browser - please update!");
    		}
    	};
    	
    	self.loadDocumentsForTopicsAndWeek = function (week) { 
    		ko.postbox.publish('openNewTab',{moduleName:"document-browse-tab", tabHeading: moment(week).format('L').substr(0, 6) + ' - ' + moment(week).add('d', 6).format('L') + " (" + self.timeData[self.active()].topicId() + ")", data: {topicId: self.timeData[self.active()].topicId(), getParam: "bestDocs&TopicId="+ self.timeData[self.active()].topicId() + "&week=" + week.toString().substr(0,10)}});	
    	};
    	
    	self.makeChart = function() {
    		self.chart = new Highcharts.StockChart({
		   	    chart: {
		    	   renderTo: 'chart',
		    	   height: $('#desktop').height() - ($('#topicCheckboxes').height() + 32.0 * $('#getcsv').size())
		    	},
		    	tooltip: {
		            formatter: function() {
		            	var html ="";		  
		            	for(var i = 0; i < self.timeData[self.active()].renderedTopics().length; i++ ) {
				            html += '<span style="font-size: x-large; font-family: Helvetica, Arial, sans-serif;text-shadow: -0.5px 0 black, 0 0.5px black, 0.5px 0 black, 0 -0.5px black;color: ' 
				            	+ globalData.Topic[self.timeData[self.active()].renderedTopics()[i]].COLOR_TOPIC$COLOR	+ '">● </span><span>'; 
				            if(self.timeData[self.active()].renderedTopics()[i] != 'average')
				            	html += self.timeData[self.active()].renderedTopics()[i] + ": "; 
				            html +=  globalData.Topic[self.timeData[self.active()].renderedTopics()[i]].TIME$WORDS_PER_WEEK[this.x].LABEL 
				            	+ '</span>: ' + globalData.Topic[self.timeData[self.active()].renderedTopics()[i]].TIME$WORDS_PER_WEEK[this.x].WORD_COUNT
				            	+ '<br/>';
		            	}
		            	html += '<span style="color: #7f7f7f;font-size:xx-small">click to open best documents for topic ' + self.timeData[self.active()].renderedTopics()[0] + ' from ' + moment(this.x).format('LL') + ' to ' + moment(this.x).add('d', 6).format('LL');
		            	return html;
		            }
		    	}
		    });
    		for(var i = 0; i < self.timeData[self.active()].renderedTopics().length; i++ ) {
	    		self.addChart(self.timeData[self.active()].renderedTopics()[i]);
	    	} 
    	};
    	
    	self.addChart = function(topicId) {
    		self.chart.addSeries({
    			id: 'topicChart' + topicId,
    			name: 'topicChart' + topicId,
    			data: (function() {
    				var data = new Array();
    				for(key in globalData.Topic[topicId].TIME$WORDS_PER_WEEK) {
    					data.push(new Array(parseInt(key), globalData.Topic[topicId].TIME$WORDS_PER_WEEK[key].WORD_COUNT));
    				}
    				return data;
    			})(),
		    	color: globalData.Topic[topicId].COLOR_TOPIC$COLOR,
		    	cursor: 'pointer',
		    	events: {
		    		click: function(event) {
		    			self.loadDocumentsForTopicsAndWeek(event.point.x);
		    		}    		
		    	}
    		});
    	};
		
    	self.changeCharts = function(newValue) { 		
       		if(self.timeData[self.active()].lastRenderedTopics.length > newValue.length) {
       			var diff = $(self.timeData[self.active()].lastRenderedTopics).not(newValue).get();	
       			self.chart.get('topicChart' + diff[0]).remove();
			} else if(self.timeData[self.active()].lastRenderedTopics.length < newValue.length) {
				var diff = $(newValue).not(self.timeData[self.active()].lastRenderedTopics).get();
				self.addChart(diff[0]);
			}
       		self.timeData[self.active()].lastRenderedTopics = newValue.slice();;
    	};
    	
    	self.changeTopics = function(newValue) {
    		renderedTopics = self.timeData[self.active()].renderedTopics.slice();
    		for(topicIndex in renderedTopics) {
    			topicId = renderedTopics[topicIndex];
    			if(newValue.indexOf(topicId) == -1) {
    				renderedTopics.splice(topicIndex, 1);
    			}
    		}
    		if(newValue.indexOf(self.timeData[self.active()].topicId()) > -1 && renderedTopics.indexOf(self.timeData[self.active()].topicId()) == -1) {
    			renderedTopics.push(self.timeData[self.active()].topicId());
    		}
    		self.timeData[self.active()].renderedTopics(renderedTopics);
    	};
    	
    	self.timeData.allTopics = ko.observableArray(globalData.TOPIC_SORTING).subscribeTo("selectedTopics");
		self.timeData.allTopics.subscribe(self.changeTopics);
    	
		self.setData = function (data) { 
			self.active = ko.observable(data.topicId);
			if (!self.timeData[self.active()]) {
				self.timeData[self.active()] = {};
				
				self.timeData[self.active()].renderedTopics = ko.observableArray([data.topicId]);
				self.timeData[self.active()].lastRenderedTopics = [data.topicId];
				self.timeData[self.active()].renderedTopics.subscribe(self.changeCharts);
				self.timeData[self.active()].topicId = ko.observable(data.topicId);	
			}
			if(!self.timeDataLoaded) {
				self.loadTimeData();
			} else {
				setTimeout(function() {
					self.makeChart();
				},0);
			}
    	};
    	self.getData = function () { return self.active(); };
		self.setData(data);
    	return self;
    }
    
    return function getSingleton(data) {
		if (instance) { // already exists
			instance.setData(data); // pass the data from view
			return instance; // that already exists
			
		} else { // instance is not existing
			return(instance = new Singleton(data)); // create a new one with data from view			
		};	
	};
});
