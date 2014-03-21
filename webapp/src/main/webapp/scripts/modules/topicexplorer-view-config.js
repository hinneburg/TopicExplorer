define(["modules/topicexplorer-model"], function(topicexplorerModel) {
    topicexplorerModel.config = {	
			documentView:	{
				activePlugin: 0,
				pluginTemplates: [ "document-view-id", 
					               "document-view-plugin-text"					
								 ]   
			},
			singleView:	{
				activePlugin: 0,
				pluginTemplates: [ "single-view-id", 
					               "single-view-plugin-text"					
								 ]   
			},
			topicView:	{
				activePlugin: 0,
				pluginTemplates: [ "topic-view-id", 
					               "topic-view-plugin-text", 
								   "topic-view-plugin-frame"
								 ]   
			}			
    };
});
