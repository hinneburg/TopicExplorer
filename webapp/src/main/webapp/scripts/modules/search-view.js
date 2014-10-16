define(
		[ "knockout", "jquery", "jquery.ui", "knockout-autocomplete"],
		function(ko, $) {
			var self = this;
			self.loadDocumentsForSearch = function() {
				var searchWord = $('#searchField').val();
				var strict = $('#searchStrict').is(':checked');
				var suffix = "";
				if(strict) suffix = " !";
				self.searchWord("");
				self.multiWords(false);
				ko.postbox.publish('openNewTab', {moduleName:"document-browse-tab",tabHeading:"Search " + searchWord + suffix, data: {searchWord: searchWord, getParam: "search&SearchWord=" + searchWord + "&SearchStrict=" + strict}});
			};

			self.selectedTopics = ko.observableArray(globalData.TOPIC_SORTING).subscribeTo("selectedTopics");
			
			self.windowWidth = ko.observable(1024).subscribeTo(
					"windowWidth");
			self.leftPadding = ko.computed(function() {
				$('.searchBar').css('margin-left',(self.windowWidth() - 500) / 2 + 'px'); // Safari
																	// fallback
				return (self.windowWidth() - 500) / 2;
			});
			
			var timeoutId = null;
	    	$(document).delegate(".autocompleteTopic", "mouseover", function(){
	    		var self = this;
	    		if (!timeoutId) {
	    			$(self).css('cursor', 'progress');
	    	        timeoutId = window.setTimeout(function() {
	    	            timeoutId = null; 
	    	            $(self).css('cursor', 'pointer');
	    	            ko.postbox.publish('moveToTopic',$(self).attr('id').split('_')[1]);
	    		    }, 1500);
	    		}
	    	}).delegate(".autocompleteTopic", "mouseout", function(){
	    		if (timeoutId) {
	    			$(this).css('cursor', 'progress');
	    		    window.clearTimeout(timeoutId);
	    		    timeoutId = null;
	    		}
	    	});
			
			ko.bindingHandlers.searchbarHandler = {
				init : function(el) {
					self.searchbarHeight = $('.searchBar').height();
					ko.postbox.publish("searchbarHeight", self.searchbarHeight);
				}
			};

			self.searchWord = ko.observable("");
			
			self.multiWords = ko.observable(false);
			
			self.autocompleteItems = ko.observableArray([]);
			
			self.autocomplete = function(newValue, callback){
				if(newValue.length > 0) {
					$.ajax({
						url : "JsonServlet",
						dataType : "json",
						cache : true,
						data : {
							Command : 'autocomplete',
							SearchWord : newValue
						},
						type : 'GET'
					}).done(callback);
				} else {
					self.autocompleteItems([]);
				}
				
				var partCount = 0;
				parts = newValue.split(" ");
				for(id in parts) {
					if(parts[id].length > 0) {
						partCount++;
					}
				}
				self.multiWords(partCount > 1);
			};
			
			self.moveToTopic = function(topic) {
				ko.postbox.publish('moveToTopic', topic);
			};
			
			self.changeSearchWord = function(newValue) {
				self.searchWord(newValue.TERM_NAME);
			};
			
			return self;
		});