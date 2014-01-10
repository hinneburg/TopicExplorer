define(["modules/topicexplorer-model"], function(topicexplorerModel) {
    topicexplorerModel.config = {	
			documentView:	{
				pluginTemplates: [ "document-view-id", 
					               "document-view-plugin-text", 
								   "document-view-plugin-fulltext"
								 ]   
			},
			topicView:	{
				pluginTemplates: [ "topic-view-id", 
					               "topic-view-plugin-text", 
								   "topic-view-plugin-frame"
								 ]   
			}			
    };
});
