var metricDef = [
		{
			"report" : "Transit Agencies Summary Report",
			"metric" : "Total Routes",
			"definition" : "Total number of routes operated by the transit agency."
		},
		{
			"report" : "Transit Agencies Summary Report",
			"metric" : "Total Stops",
			"definition" : "Total number of stops operated by the transit agency."
		},
		{
			"report" : "Counties Summary Report",
			"metric" : "Total Routes",
			"definition" : "Total number of routes serving stops in the given geogrates serving stops in the given geographic areates serving stops in the given geographic areates serving stops in the given geographic areates serving stops in the given geographic areates serving stops in the given geographic areaphic area."
		},
		{
			"report" : "Counties Summary Report",
			"metric" : "Total Stops",
			"definition" : "Total number of stops within the given geographic area."
		},
];

var metricsDic = {};
var reportsDic = {};
var metricsJson = "";
var reportsJson = "";

$.each(metricDef,function(index,item){
	metricsDic[item.metric] = metricsDic[item.metric] || [];
	metricsDic[item.metric].push(item);
	
	reportsDic[item.report] = reportsDic[item.report] || [];
	reportsDic[item.report].push(item);
});
console.log(metricsDic);
console.log(reportsDic);

// putting together metric json to build the collapsible tree.
metricsJson = '{ "name": "Metrics" , "children" : [\n';
$.each(Object.keys(metricsDic),function(index,key){
	metricsJson += '{ "name": "' + key + '", "children" : [\n';
	$.each(metricsDic[key],function(ind, metric){
		metricsJson += '{ "name" : "' + metric.report +  '", "children" :[{"name" : "' + metric.definition + '"}]';
		if (ind+1 == metricsDic[key].length) metricsJson += "}\n]}\n";
		else metricsJson += "},\n";
	})
	if (index+1 < Object.keys(metricsDic).length) metricsJson += ',';
	else metricsJson += '\n]}';
})
//metricsJson += ']}'
console.log(metricsJson);


