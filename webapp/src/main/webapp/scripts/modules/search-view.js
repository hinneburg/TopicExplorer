define(["knockout", "jquery", "jquery-ui"],
function(ko, $) { 
	return function(topicexplorer) {
		var self = this;
    	this.topicexplorer = topicexplorer;
    	this.loadDocumentsForSearch = function () { 
    		var searchWord = $('#searchField').val();
			topicexplorer.loadDocuments(
				{paramString:"Command=search&SearchWord="+searchWord},
				function(newDocumentIds) {
					ko.postbox.publish("DocumentView.selectedDocuments", newDocumentIds);
					resizeDocumentDivs();
				}
			);
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
					$('#searchField').autocomplete("close");
					self.loadDocumentsForSearch();
				},
				minLength : 1,
				delay : 700
			}).data("ui-autocomplete")._renderItem = function(ul, item) {
				var circleString = "<a onmouseover=\"$('#" + boxID + "').val('"
				+ item.label + "')\"  onmouseout=\"$('#" + boxID
				+ "').val('')\" onclick=\"$('#" + boxID
				+ "').parent('form').submit()\"><img src=\"images/" + item.item
				+ ".png\" title=\"" + item.item + "\" />" + item.label
				+ "<svg height=\"20px\">";
				for(var i = 0; i < item.color.length; i++) {
					circleString += generateCircle(item.color[i], i);
				}

				circleString += "</svg>";
				ul.find('svg').delegate("circle", "click", move);
				return $("<li></li>").data("item.autocomplete", item).append(
						circleString).appendTo(ul);
			};
		};
	};
});