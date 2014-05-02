define(
    [ "knockout", "jquery", "highstock"],
    function(ko, $) {
    	self.leftBodyHeight=ko.observable(topicexplorerModel.view.leftBodyHeight).subscribeTo("leftBodyHeight");
    	
    	
    	
    	self.timeDesktopHeight= ko.computed(function() {
    		return ((self.leftBodyHeight() - 90) * 0.7);
    	});
    	
    	self.timeScrollCallback = function(el) {
			$("#singleMenuActivator, #singleMenu").css('top', $("#desktop").scrollTop());
		};
		
		self.timeDesktopHeight.subscribe(function() {
			self.makeChart();
		});
		
		
		self.singlePluginTemplates = topicexplorerModel.config.singleView.pluginTemplates;
		self.singlePluginTemplate = ko.observable(self.singlePluginTemplates[topicexplorerModel.config.singleView.activePlugin]);
		self.singlePluginTemplate.subscribe(function(newValue) {
			topicexplorerModel.config.singleView.activePlugin = topicexplorerModel.config.singleView.pluginTemplates.indexOf(newValue);
		});
		
		self.activeTab = ko.observable(topicexplorerModel.view.activeTab)
			.subscribeTo("TabView.activeTab");
		
		self.activeTab.subscribe(function(newValue) {
			if(topicexplorerModel.view.tab[newValue].module == "time-view") {
				self.topicId(topicexplorerModel.view.tab[newValue].focus);
				if(typeof topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].renderedTopics == 'undefined')
	    			topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].renderedTopics = new Array("" + self.topicId());
	    		self.renderedTopics(topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].renderedTopics);
	    		
				self.makeChart();
			}
		});
		
		
    	self.topicId = ko.observable(topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].focus);
    	
    	self.renderedTopics = ko.observableArray();
		self.renderedTopics.subscribe(function(newValue) {
			if(topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].renderedTopics.length > newValue.length) {
				var diff = $(topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].renderedTopics).not(newValue).get();
				self.chart.get('topicChart' + diff[0]).remove();
			} else if(topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].renderedTopics.length < newValue.length) {
				var diff = $(newValue).not(topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].renderedTopics).get();
				self.addChart(diff[0]);
			}
			topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].renderedTopics = newValue.slice();
		});
    	
    	self.makeChart = function() {
    		self.topicId(topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].focus);
    		self.chart = new Highcharts.StockChart({
		   	    chart: {
		    	   renderTo: 'chart',
		    	   height: self.timeDesktopHeight()
		    	},
		    	tooltip: {
		            formatter: function() {
		            	var timeIndex = -1;
		            	
		            	for(key1 in topicexplorerModel.data.topic[0].TIME$WORDS_PER_WEEK) {
		            		if(topicexplorerModel.data.topic[0].TIME$WORDS_PER_WEEK[key1].hasOwnProperty(this.x)) {
		            			timeIndex = key1;
		            			break;
		            		}
		            	}
		            	var html ="";
		            	
		            	for(key2 in topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].renderedTopics) {
		            		var topic_id = topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].renderedTopics[key2];
		            		html += '<span style="color: ' + topicexplorerModel.data.topic[topic_id].COLOR_TOPIC$COLOR 
		            			+ '">' + topicexplorerModel.data.topic[topic_id].TIME$WORDS_PER_WEEK[timeIndex][this.x].BEST_WORDS 
		            			+ '</span>: ' + topicexplorerModel.data.topic[topic_id].TIME$WORDS_PER_WEEK[timeIndex][this.x].WORD_COUNT
		            			+ '<br/>';
		            	}
		            	return html;
		            }
		    	}
		    });
    		for(var i = 0; i < topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].renderedTopics.length; i++ ) {
    			self.addChart(topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].renderedTopics[i]);
    		}
    	};
    	
    	self.addChart = function(topicId) {
    		self.chart.addSeries({
    			id: 'topicChart' + topicId,
    			name: 'topicChart' + topicId,
    			data: (function() {
    				var data = new Array();
    				for(key in topicexplorerModel.data.topic[topicId].TIME$WORDS_PER_WEEK) {
    					for(key2 in topicexplorerModel.data.topic[topicId].TIME$WORDS_PER_WEEK[key]) {
    						data.push(new Array(parseInt(key2), topicexplorerModel.data.topic[topicId].TIME$WORDS_PER_WEEK[key][key2].WORD_COUNT));
    					}
    				}
    				return data;
    			})(),
		    	color: topicexplorerModel.data.topic[topicId].COLOR_TOPIC$COLOR
    		});
    	};
    	
    	self.init = function() {
    		if(typeof topicexplorerModel.data.timeDataLoaded == 'undefined') {
    			topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].renderedTopics = new Array("" + self.topicId());
	    		self.renderedTopics(topicexplorerModel.view.tab[topicexplorerModel.view.activeTab].renderedTopics);
				$.getJSON("JsonServlet?Command=getDates").success(function(receivedParsedJson) {
					for (key in receivedParsedJson) {
					    $.extend(self.topicexplorerModel.data.topic[key], receivedParsedJson[key]);
					}
					topicexplorerModel.data.timeDataLoaded = 1;
					self.makeChart();
				});
			} else {
				self.makeChart();
			}
    	};
    	return self;
    }); 
