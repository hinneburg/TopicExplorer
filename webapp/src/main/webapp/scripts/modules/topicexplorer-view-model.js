define(["knockout",
        "modules/topicexplorer-model", 
        "modules/topicexplorer-model-load", 
//        "modules/topicexplorer-model-document", 
//        "modules/topicexplorer-model-topic", 
//        "modules/topicexplorer-model-term",
        "modules/topicexplorer-view-config"
       ],
function(ko,topicexplorerModel) {
    return function() {
		this.topicexplorer=topicexplorerModel;
		this.version="TopicExplorer 1.0";
    };
});
