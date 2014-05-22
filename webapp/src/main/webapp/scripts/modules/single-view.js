define(
		[ "knockout", "jquery", "scripts/modules/tab-view" ],
		function(ko, $) {
			self.leftBodyHeight = ko.observable(
					topicexplorerModel.view.leftBodyHeight).subscribeTo(
					"leftBodyHeight");

			self.desktopHeight = ko.computed(function() {
				return ((self.leftBodyHeight() - 90) * 0.7);
			});

			self.selectedMark = ko.observable();
			self.selectedMark.subscribe(function(newValue){
				topicexplorerModel.view.tab[self.activeTab()].selectedMark = newValue;
				self.markedText(topicexplorerModel.view.tab[self.activeTab()][topicexplorerModel.view.tab[self.activeTab()].selectedMark]);
			});
			self.singlePluginTemplates = topicexplorerModel.config.singleView.pluginTemplates;
			self.singlePluginTemplate = ko
					.observable(self.singlePluginTemplates[topicexplorerModel.config.singleView.activePlugin]);
			self.singlePluginTemplate
					.subscribe(function(newValue) {
						topicexplorerModel.config.singleView.activePlugin = topicexplorerModel.config.singleView.pluginTemplates
								.indexOf(newValue);
					});
			
			self.markedText = ko.observable(""); 

			self.activeTab = ko.observable(topicexplorerModel.view.activeTab)
					.subscribeTo("TabView.activeTab");
			
			self.activeTab.subscribe(function(newValue) {
				if(topicexplorerModel.view.tab[newValue].module == "single-view") {
					self.selectedSingleDocument(topicexplorerModel.view.tab[newValue].focus[0]);
				} 
			});
			
			self.selectedSingleDocument = ko.observable(topicexplorerModel.view.tab[self.activeTab()].focus[0]);

			self.markWords = function() {
				var original = topicexplorerModel.data.document[self.selectedSingleDocument()].TEXT$FULLTEXT;
				if (!topicexplorerModel.view.tab[self.activeTab()].TEXT_WITH_MARKED_WORDS) {	
					var words = topicexplorerModel.data.document[self
							.selectedSingleDocument()].WORD_LIST;
					var text = original;
					var lastStart = text.length;
					for (key in words) {
						if(parseInt(words[key].POSITION_OF_TOKEN_IN_DOCUMENT) + words[key].TOKEN.length < lastStart) { // avoid covering each other
							text = text.substring(0, words[key].POSITION_OF_TOKEN_IN_DOCUMENT)
								+ '<span style="border-bottom: 2px solid '
								+ topicexplorerModel.data.topic[words[key].TOPIC_ID].COLOR_TOPIC$COLOR
								+ ';">'
								+ words[key].TOKEN
								+ '</span>'
								+ text.substring(parseInt(words[key].POSITION_OF_TOKEN_IN_DOCUMENT)
								+ words[key].TOKEN.length);
							lastStart = words[key].POSITION_OF_TOKEN_IN_DOCUMENT;
						}
					}
					topicexplorerModel.view.tab[self.activeTab()].TEXT_WITH_MARKED_WORDS = text;
					if(topicexplorerModel.data.document[self.selectedSingleDocument()].FRAME_LIST != 'undefined') {
						var frames = topicexplorerModel.data.document[self.selectedSingleDocument()].FRAME_LIST;
						text = original;
						lastStart = text.length;
						for(key in frames) {
							if(frames[key].END_POSITION < lastStart) { // avoid covering each other
								text = text.substring(0, frames[key].START_POSITION)
									+ '<span style="border-bottom: 2px solid '
									+ topicexplorerModel.data.topic[frames[key].TOPIC_ID].COLOR_TOPIC$COLOR
									+ ';">'
									+ text.substring(frames[key].START_POSITION, frames[key].END_POSITION)
									+ '</span>'
									+ text.substring(parseInt(frames[key].END_POSITION));
								lastStart = frames[key].START_POSITION;
							}
						} 
						topicexplorerModel.view.tab[self.activeTab()].TEXT_WITH_MARKED_FRAMES = text;
							
						if(typeof topicexplorerModel.view.tab[self.activeTab()].focus[1] != 'undefined') {
							text = original;
							lastStart = text.length;
							for(key in frames) {
								if(frames[key].END_POSITION < lastStart && frames[key].TOPIC_ID == topicexplorerModel.view.tab[self.activeTab()].focus[1].topic) { // avoid covering each other
									text = text.substring(0, frames[key].START_POSITION)
										+ '<span style="border-bottom: 2px solid '
										+ topicexplorerModel.data.topic[frames[key].TOPIC_ID].COLOR_TOPIC$COLOR
										+ ';">'
										+ text.substring(frames[key].START_POSITION, frames[key].END_POSITION)
										+ '</span>'
										+ text.substring(parseInt(frames[key].END_POSITION));
									lastStart = frames[key].START_POSITION;
								}
							} 
							topicexplorerModel.view.tab[self.activeTab()].TEXT_WITH_MARKED_FRAMES_FOR_TOPIC = text;
								
							self.topicFrameText = ko.observable(text); 
								
								
							if(typeof topicexplorerModel.view.tab[self.activeTab()].focus[1].frame != 'undefined') {
								text = original;
								lastStart = text.length;
								for(key in frames) {
									if(frames[key].END_POSITION < lastStart && frames[key].TOPIC_ID == topicexplorerModel.view.tab[self.activeTab()].focus[1].topic && frames[key].FRAME == topicexplorerModel.view.tab[self.activeTab()].focus[1].frame) { // avoid covering each other											text = text.substring(0, frames[key].START_POSITION)
										text = text.substring(0, frames[key].START_POSITION)
											+ '<span style="border-bottom: 2px solid '
											+ topicexplorerModel.data.topic[frames[key].TOPIC_ID].COLOR_TOPIC$COLOR
											+ ';">'
											+ text.substring(frames[key].START_POSITION, frames[key].END_POSITION)
											+ '</span>'
											+ text.substring(parseInt(frames[key].END_POSITION));
										lastStart = frames[key].START_POSITION;
									}
								} 
									topicexplorerModel.view.tab[self.activeTab()].TEXT_WITH_MARKED_FRAMES_FOR_TOPIC_AND_FRAME = text;
							}
							
						}
					}
				} 
				
				if(typeof topicexplorerModel.view.tab[self.activeTab()].selectedMark == 'undefined') {
					topicexplorerModel.view.tab[self.activeTab()].selectedMark = "TEXT_WITH_MARKED_WORDS";
				}
				self.selectedMark(topicexplorerModel.view.tab[self.activeTab()].selectedMark);
				
			};
			
			self.initialize = function() {
				if(typeof topicexplorerModel.view.tab[self.activeTab()] != 'undefined' && topicexplorerModel.view.tab[self.activeTab()].module == "single-view") {
					if(!topicexplorerModel.data.documentsLoading()) {
						if(typeof topicexplorerModel.data.document[topicexplorerModel.view.tab[self.activeTab()].focus[0]].singleDataLoaded == 'undefined') {
			    			topicexplorerModel.data.documentsLoading(true);
			    			$.getJSON("JsonServlet?" + topicexplorerModel.view.tab[self.activeTab()].getParameter)
							.success(function(receivedParsedJson) {
								$.extend(self.topicexplorerModel.data.document[receivedParsedJson.DOCUMENT.DOCUMENT_ID], receivedParsedJson.DOCUMENT);
								topicexplorerModel.view.tab[self.activeTab()].focus[0] = receivedParsedJson.DOCUMENT.DOCUMENT_ID;
								topicexplorerModel.data.document[topicexplorerModel.view.tab[self.activeTab()].focus[0]].singleDataLoaded = 1;
								self.markWords();
								topicexplorerModel.data.documentsLoading(false);
							});
						} else {
							self.markWords();
						}
					}
				}
	    	};

			self.singleScrollCallback = function(el) {
				$("#singleMenuActivator, #singleMenu").css('top',
						$("#desktop").scrollTop());
			};

			return self;
		});
