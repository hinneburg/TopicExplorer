$(document).ready(function() {
  $('.showBig').click(function(){

	  $(".showBigTopContentDiv").html("");
	  $(".showBigTopTitle").html("Words per week for Topic: " + $(".showBigTopTitle").html());
	  var index = $(this).parent().parent().parent().index();
	  
	  
	  drawChart (index, index, ".showBigTopContentDiv");
	  
  });
});


function drawChart(firstIndex, lastIndex, elem) {
	var margin = {top: 20, right: 60, bottom: 30, left: 20},
    width = $(elem)[0].offsetWidth - margin.right - margin.left,
    height = $(elem)[0].offsetHeight - margin.top - margin.bottom;

	var x = d3.time.scale()
	    .range([0, width]);
	
	var y = d3.scale.linear()
	    .range([height, 0]);
	
	var parse = d3.time.format("%d.%m.%Y").parse,
	    format = d3.time.format("%x");
	
	var svg = d3.select(elem).append("svg")
	    .attr("width", $(elem)[0].offsetWidth)
	    .attr("height",$(elem)[0].offsetHeight)
	    .append("g")
	    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");
	
	d3.json("Time_getWordCountPerWeek.jsp?topicId=" + firstIndex, function(temp) {
		var json = new Array();
		
		json['data']=temp[0];
		json['farbe'] =temp[1];
		json['name'] = temp[2];
		
		firstIndex=0;
		lastIndex=0;
		
		delete temp;
		var fields = new Array();
		$.each(json.data[0], function(key, value) {
			if(key != "date") {
				fields.push(key);
			}
		});
	//	alert(fields.length);
		
		var color = new Array();
		var actFields = new Array();
	    for(var i=firstIndex; i <= lastIndex; i++) {
	    	actFields[i-firstIndex] = fields[i];
	    	color[i-firstIndex] = json.farbe[json.farbe.length - (i + 1)];
	    }
	    
	  //  alert(color[0]);
	    // Transpose the data into layers by cause.
	    var causes = d3.layout.stack()//.offset("silhouette")
	  (actFields.map(function(cause) {
	      return json.data.map(function(d) {
	        return {x: parse(d.date), y: +d[cause]};
	      });
	    }));

	  var z = d3.scale.ordinal()
	      .range(color);
	    // Compute the x-domain (by date) and y-domain (by top).
	    x.domain([causes[0][0].x, causes[0][causes[0].length - 1].x]);
	    y.domain([0, d3.max(causes[causes.length - 1], function(d) { return d.y0 + d.y; })]);

	    // Add an area for each cause.
	    svg.selectAll("path.area")
	        .data(causes)
	      .enter().append("path")
	    //    .attr("class", "area")
	        .style("fill", function(d, i) { return z(i); })
	        .attr("d", d3.svg.area()
	        	.x(function(d) { return x(d.x); })
	        	.y0(function(d) { return y(d.y0); })
	        	.y1(function(d) { return y(d.y0 + d.y) })
	        	.interpolate("basis"))
	     //   .append("title")
	    //    .text(function(d,i) { return i + ":" + json.name[i]; });

	    // Add a label per date.
	    svg.selectAll("text")
	        .data(x.ticks(12))
	        .enter().append("text")
	        .attr("x", x)
	        .attr("y", height + 6)
	        .attr("text-anchor", "middle")
	        .attr("dy", ".71em")
	        .text(x.tickFormat(12));

	    // Add y-axis rules.
	    var rule = svg.selectAll("g.rule")
	        .data(y.ticks(5))
	        .enter().append("g")
	        .attr("class", "rule")
	        .attr("transform", function(d) { return "translate(0," + y(d) + ")"; });

	    rule.append("line")
	        .attr("x2", width)
	        .style("stroke", function(d) { return d ? "#fff" : "#000"; })
	        .style("stroke-opacity", function(d) { return d ? .7 : null; });

	    rule.append("text")
	        .attr("x", width + 6)
	        .attr("dy", ".35em")
	        .text(d3.format("r")); 
	  });
}

