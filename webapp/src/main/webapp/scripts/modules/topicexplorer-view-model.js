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
						console.log("leftBodyHeight"+temp);
						return (self.windowHeight()-self.searchbarHeight()-30);
					}).publishOn("leftBodyHeight");
		 
    };
});
