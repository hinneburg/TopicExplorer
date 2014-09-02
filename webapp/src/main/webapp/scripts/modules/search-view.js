define(
		[ "knockout", "jquery", "jquery-ui"],
		function(ko, $) {
			var self = this;
			self.loadDocumentsForSearch = function() {
				var searchWord = $('#searchField').val();
				ko.postbox.publish('openNewTab', {moduleName:"document-browse-tab",tabHeading:"Search " + searchWord, data: {searchWord: searchWord, getParam: "search&SearchWord=" + searchWord}});
//				
//				topicexplorerModel.newTab("Command=search&SearchWord="
//						+ searchWord, "Search: " + searchWord, 'document-view',
//						new Array());
			};

			self.windowWidth = ko.observable(1024).subscribeTo(
					"windowWidth");
			self.leftPadding = ko.computed(function() {
				$('.searchBar').css('margin-left',
						((self.windowWidth() - 500) / 2) + 'px'); // Safari
																	// fallback
				return ((self.windowWidth() - 500) / 2) + 'px';
			});
			ko.bindingHandlers.searchbarHandler = {
				init : function(el) {
					self.searchbarHeight = $('.searchBar').height();
					ko.postbox.publish("searchbarHeight", self.searchbarHeight);
				}
			};
			ko.bindingHandlers.autoCompleteHandler = {
				init : function(el) {
					self.autocomplete('searchField');
				}
			};
			self.autocomplete = function(boxID) {
				$('#' + boxID).bind('keydown', function() {
					$('#searchItem').val("none");
				});
				$('#' + boxID).bind('keyup', function() {
					autocompleteSearch = $('#' + boxID).val();
				});
				$("#" + boxID).autocomplete({
					source : function(request, response) {
						$.ajax({
							url : "JsonServlet",
							dataType : "json",
							cache : true,
							data : {
								Command : 'autocomplete',
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
					var circleString = "<a onmouseover=\"$('#" + boxID
							+ "').val('" + item.label
							+ "')\"  onmouseout=\"$('#" + boxID
							+ "').val('')\" onclick=\"$('#" + boxID
							+ "').parent('form').submit()\" style=\"width: "
							+ ($('#' + boxID).width() + 20) + "px;white-space: nowrap;\">"
							+ item.label;
					for (var i = 0; i < item.color.length; i++) {
//						circleString += self.generateCircle(item.color[i], i);
					}
					circleString += "</a>";
					return $("<li class=\"autocompleteEntry\"></li>").data(
							"item.autocomplete", item).append(circleString)
							.appendTo(ul);
				};
			};

//			self.generateCircle = function(color, itemIdx) {
//				var topic = topicexplorerModel.data.topic[color];
//				if (!topic)
//					return "";
//				var circleString = "<span class=\"topicCircle\" id=\"t_"
//						+ color
//						+ "\" "
//						+ "style=\"color:"
//						+ topic.COLOR_TOPIC$COLOR
//						+ "\" title=\"Topic "
//						+ color
//						+ "\">&#9679;</span>";
//
//				return circleString;
//			};
			return self;
		});