define(["knockout",
        "scripts/modules/topicexplorer-model", 
        "scripts/modules/topicexplorer-model-load", 
        "scripts/modules/topicexplorer-view-config"
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
				topicexplorerModel.leftBodyHeight = self.windowHeight()-self.searchbarHeight()-30;
				return topicexplorerModel.leftBodyHeight;
			}).publishOn("leftBodyHeight");
		 
    };
});
