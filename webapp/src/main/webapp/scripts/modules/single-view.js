define(
		[ "knockout", "jquery", "scripts/modules/tab-view" ],
		function(ko, $) {
			self.leftBodyHeight = ko.observable(
					topicexplorerModel.view.leftBodyHeight).subscribeTo(
					"leftBodyHeight");

			self.desktopHeight = ko.computed(function() {
				return ((self.leftBodyHeight() - 90) * 0.7);
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
					self.selectedSingleDocument(topicexplorerModel.view.tab[newValue].focus);
				} 
			});
			
			self.selectedSingleDocument = ko.observable(topicexplorerModel.view.tab[self.activeTab()].focus);

			self.markWords = function() {
				if (!topicexplorerModel.data.document[self.selectedSingleDocument()].TEXT_WITH_MARKED_WORDS) {
					var text = topicexplorerModel.data.document[self
							.selectedSingleDocument()].TEXT$FULLTEXT;
					var words = topicexplorerModel.data.document[self
							.selectedSingleDocument()].WORD_LIST;
					for (key in words) {
						text = text.substring(0,
								words[key].POSITION_OF_TOKEN_IN_DOCUMENT)
								+ '<span style="border-bottom: 2px solid '
								+ topicexplorerModel.data.topic[words[key].TOPIC_ID].COLOR_TOPIC$COLOR
								+ ';">'
								+ words[key].TOKEN
								+ '</span>'
								+ text
										.substring(parseInt(words[key].POSITION_OF_TOKEN_IN_DOCUMENT)
												+ words[key].TOKEN.length);
					}
					topicexplorerModel.data.document[self.selectedSingleDocument()].TEXT_WITH_MARKED_WORDS = text;

					self.markedText(text);

				} else {
					self.markedText(topicexplorerModel.data.document[self
							.selectedSingleDocument()].TEXT_WITH_MARKED_WORDS);
				}
			};
			
			self.initialize = function() {
				if(typeof topicexplorerModel.view.tab[self.activeTab()] != 'undefined') {
					if(typeof topicexplorerModel.data.document[topicexplorerModel.view.tab[self.activeTab()].focus] != 'undefined') {
			    		if(typeof topicexplorerModel.data.document[topicexplorerModel.view.tab[self.activeTab()].focus].singleDataLoaded == 'undefined') {
			    			$.getJSON("JsonServlet?" + topicexplorerModel.view.tab[self.activeTab()].getParameter)
							.success(function(receivedParsedJson) {
								$.extend(self.topicexplorerModel.data.document[receivedParsedJson.DOCUMENT.DOCUMENT_ID], receivedParsedJson.DOCUMENT);
								topicexplorerModel.view.tab[self.activeTab()].focus = receivedParsedJson.DOCUMENT.DOCUMENT_ID;
								topicexplorerModel.data.document[topicexplorerModel.view.tab[self.activeTab()].focus].singleDataLoaded = 1;
								self.markWords();
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
