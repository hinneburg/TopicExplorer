define(["knockout", "jquery", "jquery-ui", "scripts/modules/tab-view"],
function(ko, $) { 
	return function(topicexplorer) {
		var self = this;
    	this.topicexplorer = topicexplorer;
    	this.loadDocumentsForSearch = function () { 
    		var searchWord = $('#searchField').val();
    		topicexplorer.loadDocumentsForTab("Command=search&SearchWord="+searchWord, "Search: " + searchWord);
    		
   		};
   		self.windowWidth = ko.observable(topicexplorer.view.windowWidth).subscribeTo("windowWidth");
   		self.leftPadding = ko.computed(function() {
   			$('.searchBar').css('margin-left', ((self.windowWidth() - 500) / 2) + 'px'); // Safari fallback
   			return ((self.windowWidth() - 500) / 2) + 'px';
		});
		ko.bindingHandlers.searchbarHandler = { init: 
			function(el) { 		
				
				self.searchbarHeight=$('.searchBar').height();
				ko.postbox.publish("searchbarHeight",self.searchbarHeight);
			}
		};
		ko.bindingHandlers.autoCompleteHandler = { init: function(el) { self.autocomplete('searchField');}};
		this.autocomplete = function(boxID) {
			var itemIdx = 1;
			$('#' + boxID).bind('keydown', function() {
				$('#searchItem').val("none");
			});
			$('#' + boxID).bind('keyup', function() {
				autocompleteSearch = $('#' + boxID).val();
			});
			$("#" + boxID).autocomplete( {
				source : function(request, response) {
					$.ajax( {
						url : "JsonServlet",
						dataType : "json",
						cache : true,
						data : {
							Command: 'autocomplete',
							SearchWord : request.term
						},
						type : 'GET',
						success : function(data) {
							response($.map(data, function(item) {
								return {
									label : item.TERM_NAME,
									color : item.TOP_TOPIC,
									value : item.label,
									item : 'document'
								};
							}));
						}
					});
				},
				select : function(event, ui) {
					self.loadDocumentsForSearch();
				},
				minLength : 1,
				delay : 700
			}).data("ui-autocomplete")._renderItem = function(ul, item) {
				var circleString = "<a onmouseover=\"$('#" + boxID + "').val('"
				+ item.label + "')\"  onmouseout=\"$('#" + boxID
				+ "').val('')\" onclick=\"$('#" + boxID
				+ "').parent('form').submit()\" style=\"width: " 
				+ ($('#' + boxID).width() + 20) + "px;\">" + item.label
				+ "<svg height=\"20px\" width=\"100px\">";
				for(var i = 0; i < item.color.length; i++) {
					circleString += self.generateCircle(item.color[i], i);
				}

				circleString += "</svg></a>";
				return $("<li class=\"autocompleteEntry\"></li>").data("item.autocomplete", item).append(
						circleString).appendTo(ul);
			};
		};

		self.generateCircle = function (color, itemIdx) {
			var cx = 10 + itemIdx * 12;
			var topic = topicexplorerModel.topic[color];
			if(!topic)
				return "";
			var circleString = "<circle class=\"topicCircle\" id=\"t_"+color+"\" "
				+ " r=\"5\" cx=\""+cx+"\" cy=\"14\" fill=\""
				+ topic.COLOR_TOPIC$COLOR
				+ "\" title=\""
				+ topic.TEXT$TOPIC_LABEL
				+ "\" stroke=\"black\" stroke-width=\"0.5\" style=\"cursor:pointer\"/>";

			return circleString;
		};
	};
});