define(["knockout", "jquery", "jquery-ui"],
function(ko, $) { 
	return function(topicexplorer) {
		var self = this;
    	this.topicexplorer = topicexplorer;
    	this.loadDocumentsForSearch = function () { 
    		var searchWord = $('#searchField').val();
    		
    		var index = ++topicexplorer.tabsLastIndex;
    		topicexplorer.tab[topicexplorer.activeTab].scrollPosition = $("#desktop").scrollTop();
    		topicexplorer.activeTab = "t" + index;
			topicexplorer.tabs.push("t" + index);
			
			topicexplorer.tab["t" + index] = new Array();
			topicexplorer.tab["t" + index].scrollPosition = 0;
			topicexplorer.tab["t" + index].tabTitle = "Search: " + searchWord;
			topicexplorer.tab["t" + index].documentGetParameter = "Command=search&SearchWord="+searchWord;	
			topicexplorer.loadDocuments(
				{paramString:"Command=search&SearchWord="+searchWord},
				function(newDocumentIds) {
					if(newDocumentIds.length < topicexplorer.documentLimit) { 
						topicexplorer.tab["t" + index].documentsFull = ko.observable(true);
					} else {
						topicexplorer.tab["t" + index].documentsFull = ko.observable(false);
					}
					topicexplorer.tab["t" + index].documentCount = newDocumentIds.length;					
					topicexplorer.tab["t" + index].documentSorting = newDocumentIds;
					console.log(topicexplorer.tab["t" + index].documentsFull() + index);
					ko.postbox.publish("TabView.tabs", topicexplorer.tabs);
					ko.postbox.publish("DocumentView.selectedDocuments", newDocumentIds);
					$("#desktop").scrollTop(0);
		    		
				}
				
			);
    		
   		};
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