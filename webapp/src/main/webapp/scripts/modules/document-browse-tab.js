define(["knockout", "jquery"],
function(ko, $) {
	var instance;
    function Singleton (data) {
		// this could be loaded from somewhere else, however all AMD modules need to be loaded in advance
		var extend = new Array("scripts/modules/extenders/document-browse-time","scripts/modules/extenders/document-browse-text");
		// here comes the show-document-tab 
		var self = {};
		
		self.TextRepresentation = function(label, field) {
    		this.label = label;
    		this.field = field;
    	};
		
		self.documentLimit = globalData.DocBrowserLimit;
		
		self.browseData= new Object();
		
		self.loading = ko.observable(false);
		
		self.firstLoading = ko.observable(false);
		
		self.selectedTopics = ko.observableArray(globalData.TOPIC_SORTING).subscribeTo("selectedTopics");

		self.selectedTopics.subscribe(function(newValue) {
			setTopTopics(self.browseData[self.active()].selectedDocuments(), false);
		});
		
		
		self.scrollCallback = function(el) {
			$("#desktop").children(".documentList").children(".jumpToStart").css('top',($("#desktop").scrollTop() - 10) + 'px');
			self.loadUntilFull();
			
			if($("#desktop").scrollTop() > 1000) {
				$("#desktop").children(".documentList").children(".jumpToStart").show();
			} else {
				$("#desktop").children(".documentList").children(".jumpToStart").hide();
			}
		};
		
		self.change = function() {
			if(!self.loading()) {
				self.browseData[self.active()].documentsFull(false);
				self.loading(true);
				
				filter = {};
				for(key in self.browseData[self.active()].filter){
					filter[key] = self.browseData[self.active()].activeFilter[key]();
				}
				
				self.browseData[self.active()].selectedDocuments([]);
				$.getJSON("JsonServlet?Command=" + self.browseData[self.active()].data.getParam + "&sorting=" + self.browseData[self.active()].selectedSorting().toUpperCase() + "&filter=" + JSON.stringify(filter))
				.success(function(receivedParsedJson) {
					self.browseData[self.active()].nextOffset = self.documentLimit;
					if(receivedParsedJson.DOCUMENT_SORTING.length < self.documentLimit) {
						self.browseData[self.active()].documentsFull(true);
					} else {
						self.browseData[self.active()].documentsFull(false);
					}
					$.extend(globalData.DOCUMENT, receivedParsedJson.DOCUMENT);
					
					setTopTopics(receivedParsedJson.DOCUMENT_SORTING, true);
					
					self.browseData[self.active()].selectedDocuments(receivedParsedJson.DOCUMENT_SORTING);
					for (var i=0;i<extend.length;i++) {
			 			var extender = require(extend[i]);
						extender(self);
					}
					self.loading(false);
					self.loadUntilFull();
				});	
			}
		};
		
		self.changeFilters = function() {
			for(key in self.browseData[self.active()].filter){
				self.browseData[self.active()].activeFilter[key](self.browseData[self.active()].filter[key]());	
			}
			self.change();
			
		}
		
		self.deleteFilter = function(filter) {
			self.browseData[self.active()].activeFilter[filter]("");
			self.browseData[self.active()].filter[filter]("");
			self.change();
		}
		
		self.checkSize = function() {
			if($(".browser").length > 0 ) {
				self.loadMoreDocuments();
			}
		};
		
		self.reloadTimer = false;
		
		self.loadMoreDocuments = function() {
			if(!self.loading()) {
				if(!self.browseData[self.active()].documentsFull() && $("#desktop").scrollTop() + $("#desktop").height() + 90 >= $("#desktop")[0].scrollHeight && $('#browserToolbar').length > 0) {
					self.loading(true);
					
					filter = {};
					for(key in self.browseData[self.active()].filter){
						filter[key] = self.browseData[self.active()].activeFilter[key]();
					}
					
					$.getJSON("JsonServlet?Command=" + self.browseData[self.active()].data.getParam + "&offset=" + self.browseData[self.active()].nextOffset + "&sorting=" + self.browseData[self.active()].selectedSorting().toUpperCase() + "&filter=" + JSON.stringify(filter))
					.success(function(receivedParsedJson) {
						self.browseData[self.active()].nextOffset += self.documentLimit;
						if(receivedParsedJson.DOCUMENT_SORTING.length < self.documentLimit) {
							self.browseData[self.active()].documentsFull(true);
						}
						$.extend(globalData.DOCUMENT, receivedParsedJson.DOCUMENT);
						
						setTopTopics(receivedParsedJson.DOCUMENT_SORTING, true);
						
						self.browseData[self.active()].selectedDocuments(self.browseData[self.active()].selectedDocuments().concat(receivedParsedJson.DOCUMENT_SORTING));
						for (var i=0;i<extend.length;i++) {
				 			var extender = require(extend[i]);
							extender(self);
						}
						self.loading(false);
					});	
				} else {
					clearInterval(self.checkFullInterval);
				}
			}
			
		};
		
		self.checkFullInterval = false;
		
		self.loadUntilFull = function() {
			self.checkFullInterval = setInterval(self.loadMoreDocuments, 50);
		}
		
		self.loadDocument = function(docId) {
			var postData = $.extend({}, self.browseData[self.active()].data);
			delete(postData.getParam);
			postData.documentId = docId;
			var meta = "";
			if(postData.hasOwnProperty('topicId')) {
				meta += " Topic " + postData.topicId;
			}
			if(postData.hasOwnProperty('frame')) {
				meta += " (" + postData.frame + ")";
			}
			ko.postbox.publish('openNewTab', {moduleName:"document-show-tab", tabHeading: docId + meta, data:postData});
		};
		
		self.moveToTopic = function(topic) {
			ko.postbox.publish('moveToTopic', topic);
		};
		
		globalData.DOCUMENT ={};
		
		self.setData = function (data) { 
			self.active = ko.observable(data.getParam);
			if (!self.browseData[self.active()]) {
				self.loading(true);
				self.firstLoading(true);
				self.browseData[self.active()] = {};
				
				self.browseData[self.active()].activeFilter = {};
				self.browseData[self.active()].activeFilter.word = ko.observable("");
				
				self.browseData[self.active()].filter = {};
				self.browseData[self.active()].filter.word = ko.observable("");
				
				self.browseData[self.active()].data = data;
				self.browseData[self.active()].sortingOptions = ko.observableArray(['Relevance']);
				self.browseData[self.active()].selectedSorting = ko.observable('Relevance');
				self.browseData[self.active()].selectedSorting.subscribe(self.change);
				self.browseData[self.active()].selectedDocuments = ko.observableArray([]);
				self.browseData[self.active()].textSelectArray = ko.observableArray([]);
				self.browseData[self.active()].textSelection = ko.observable();
				$.getJSON("JsonServlet?Command=" + self.browseData[self.active()].data.getParam + "&sorting=" + self.browseData[self.active()].selectedSorting())
				.success(function(receivedParsedJson) {
					
					self.browseData[self.active()].nextOffset = self.documentLimit;
					if(receivedParsedJson.DOCUMENT_SORTING.length < self.documentLimit) {
						self.browseData[self.active()].documentsFull = ko.observable(true);
					} else {
						self.browseData[self.active()].documentsFull = ko.observable(false);
					}
					$.extend(globalData.DOCUMENT, receivedParsedJson.DOCUMENT);
					setTopTopics(receivedParsedJson.DOCUMENT_SORTING, true);
					self.browseData[self.active()].selectedDocuments(receivedParsedJson.DOCUMENT_SORTING);
					self.browseData[self.active()].textSelectArray.push(new self.TextRepresentation('Keywords', 'KEYWORD_'));
					self.browseData[self.active()].textSelection(new self.TextRepresentation('Keywords', 'KEYWORD_'));
					
					for (var i=0;i<extend.length;i++) {
			 			var extender = require(extend[i]);
						extender(self);
					}
					self.loading(false);
					self.firstLoading(false);
					$("#desktop").scrollTop(0);
					self.loadUntilFull();
				});
			} 
		};
		self.getData = function () { return self.active(); };
		self.setData(data);
		
		self.windowWidth = ko.observable(Math.max(800, $(window).width(), /* For opera: */ document.documentElement.clientWidth)).subscribeTo("windowWidth");
		self.windowWidth.subscribe(self.loadUntilFull);
		self.documentElementWidth = ko.computed (function() {
			var documentWidth = 262;
			var docDeskRatio = Math.floor((self.windowWidth() - 10) / documentWidth);
			return ((self.windowWidth() - 10) / docDeskRatio) - 32;
		});
		
		function setTopTopics(documents, isNew) {
			for(docIndex in documents) {	
				docId = documents[docIndex];
				bestTopics = [];
				count = 0;
				for(topicIndex in globalData.DOCUMENT[docId].TOP_TOPIC) {
					topicId = globalData.DOCUMENT[docId].TOP_TOPIC[topicIndex];
					if(self.selectedTopics.indexOf(topicId) > -1) {
						bestTopics.push(topicId);
						count++;
						if(count > 3) break;
					}
				}
				if(isNew) {
					globalData.DOCUMENT[docId].topTopics = ko.observableArray(bestTopics);
				} else {
					globalData.DOCUMENT[docId].topTopics(bestTopics);
				}
			}	
		} 
		
		return self;
	};

	return function getSingleton(data) {
		if (instance) { // already exists
			instance.setData(data); // pass the data from view
			return instance; // that already exists
			
		} else { // instance is not existing
			return (instance = new Singleton(data)); // create a new one with data from view
			
		};	
	};
});