define(["scripts/modules/topicexplorer-model"], function(topicexplorerModel) {
    topicexplorerModel.config = {	
			documentView:	{
				activePlugin: 1,
				pluginTemplates: [ "document-view-id", 
					               "document-view-plugin-text"					
								 ]   
			},
			singleView:	{
				activePlugin: 1,
				pluginTemplates: [ "single-view-id", 
					               "single-view-plugin-text"					
								 ]   
			},
			topicView:	{
				activePlugin: 2,
				pluginTemplates: [ "topic-view-id", 
					               "topic-view-plugin-text", 
								   "topic-view-plugin-time"
								 ]   
			}			
    };
});
