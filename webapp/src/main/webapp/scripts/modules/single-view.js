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

			self.activeTab = ko.observable(topicexplorerModel.view.activeTab)
					.subscribeTo("TabView.activeTab");
			
			self.activeTab.subscribe(function(newValue) {
				self.selectedDocument(topicexplorerModel.view.tab[newValue].focus);
			});
			
			self.selectedDocument = ko.observable(topicexplorerModel.view.tab[self.activeTab()].focus);

			self.markWords = function() {
				if (!topicexplorerModel.data.document[self.selectedDocument()].TEXT_WITH_MARKED_WORDS) {
					var text = topicexplorerModel.data.document[self
							.selectedDocument()].TEXT$FULLTEXT;
					var words = topicexplorerModel.data.document[self
							.selectedDocument()].WORD_LIST;
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
					topicexplorerModel.data.document[self.selectedDocument()].TEXT_WITH_MARKED_WORDS = text;

					return text;

				} else {
					return topicexplorerModel.data.document[self
							.selectedDocument()].TEXT_WITH_MARKED_WORDS;
				}
			};
			self.init = function() {
				$.getJSON("JsonServlet?" + topicexplorerModel.view.tab[self.activeTab()].getParameter)
					.success(function(receivedParsedJson) {
						$.extend(self.topicexplorerModel.data.document[receivedParsedJson.DOCUMENT.DOCUMENT_ID], receivedParsedJson.DOCUMENT);
						topicexplorerModel.view.tab[self.activeTab()].focus = receivedParsedJson.DOCUMENT.DOCUMENT_ID;
						self.selectedDocument(receivedParsedJson.DOCUMENT.DOCUMENT_ID);
					}
				);
			};

			self.init();
			
			self.singleScrollCallback = function(el) {
				$("#singleMenuActivator, #singleMenu").css('top',
						$("#desktop").scrollTop());
			};

			return self;
		});
