define(["knockout",
        "modules/topicexplorer-model", 
        "modules/topicexplorer-model-load", 
        "modules/topicexplorer-view-config"
       ],
function(ko,topicexplorerModel) {
    return function() {
    	var self =this;
		this.topicexplorer=topicexplorerModel;
		this.version="TopicExplorer 1.0";
		this.windowHeight = ko.observable(100).subscribeTo("windowHeight");  ;
		this.searchbarHeight = ko.observable(100).subscribeTo("searchbarHeight"); 
		this.leftBodyHeight = ko.computed(
					function() {
						var temp = self.windowHeight()-self.searchbarHeight()-30;
						console.log("leftbodyHeight"+temp);
						return self.windowHeight()-self.searchbarHeight()-30;
					});
		
		//ko.bindingHandlers.leftBodyHeightHandler = {update: function() {self.leftBodyHeight(self.windowHeight-searchbarHeight-30);} };
		//self.leftBodyHeight(self.windowHeight-searchbarHeight-30);

		
//		this.windowHeight = 100;
//		this.searchbarHeight = 100;
//		
//		ko.postbox.subscribe("windowHeight",
//				function (windowHeight) {
//					self.windowHeight=windowHeight;
//					self.leftBodyHeight(self.windowHeight-searchbarHeight-30);
//					});
//		ko.postbox.subscribe("searchbarHeight",
//				function(searchbarHeight) {
//					self.searchbarHeight=searchbarHeight;
//					self.leftBodyHeight(self.windowHeight-searchbarHeight-30);
//					});
		 
    };
});
