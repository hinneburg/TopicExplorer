define(["knockout",
        "modules/topicexplorer-model", 
        "modules/topicexplorer-model-load", 
        "modules/topicexplorer-view-config"
       ],
function(ko,topicexplorerModel) {
    return function() {
		this.topicexplorer=topicexplorerModel;
		this.version="TopicExplorer 1.0";
    };
});
