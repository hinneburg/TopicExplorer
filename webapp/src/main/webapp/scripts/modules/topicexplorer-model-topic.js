define(["modules/topicexplorer-model"], function(topicexplorerModel) {
    topicexplorerModel.topic = {	"1":	{
				id:"1",
				color$color: "red",
				text$title:"Topic 1", 
				topTerms: ["3","2","1"],
				frame$TopFrames:[ 
					{verb: "x", noun:"y",frequency:1001}, 
					{verb: "x", noun:"y",frequency:1001}
					]
				},
        "2":	{
				id:"2",
				color$color: "blue",
				text$title:"Topic 2", 
				topTerms: ["4","5","6"],
				frame$TopFrames:[ 
					{verb: "A", noun:"B",frequency:101}, 
					{verb: "C", noun:"D",frequency:100}
					]
			      	}
    };
});
