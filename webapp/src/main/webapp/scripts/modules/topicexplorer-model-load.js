define(["modules/topicexplorer-model","text!/../webapp/JsonServlet?art=random&id=&limit=20"], function(topicexplorerModel,temp) {
	var json = JSON.parse(temp);
	console.log(json);
	topicexplorerModel.document=json.JSON.DOCUMENT; 
	topicexplorerModel.term = json.JSON.Term;
	topicexplorerModel.topic = json.JSON.Topic;
});
