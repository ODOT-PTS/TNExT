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
		{
			"report" : "Transit Agencies Summary Report",
			"metric" : "Geographic Areas",
			"definition" : "Count of geographic areas that the transit agency operates within."
		},
		{
			"report" : "Transit Agencies Summary Report",
			"metric" : "Total Counties",
			"definition" : "Count of counties that the transit agency operates within."
		},
		{
			"report" : "Transit Agencies Summary Report",
			"metric" : "Total Census Places",
			"definition" : "Count of census places that the transit agency operates within."
		},
		{
			"report" : "Transit Agencies Summary Report",
			"metric" : "Total Urban Areas",
			"definition" : "Count of urban areas that the transit agency operates within."
		},
		{
			"report" : "Transit Agencies Summary Report",
			"metric" : "Total Congressional Districts",
			"definition" : "Count of congressional districts that the transit agency operates within."
		},
		{
			"report" : "Transit Agencies Summary Report",
			"metric" : "Total ODOT Transit Regions",
			"definition" : "Count of ODOT transit regions that the transit agency operates within."
		},
		{
			"report" : "Transit Agencies Summary Report",
			"metric" : "Fare",
			"definition" : "If available, points to the fare information published by the transit agency on its web site."
		},
		{
			"report" : "Transit Agencies Summary Report",
			"metric" : "Service Start Date",
			"definition" : "The earliest service date specified in the transit agency feed in YYYYMMDD format."
		},
		{
			"report" : "Transit Agencies Summary Report",
			"metric" : "Service End Date",
			"definition" : "The latest service date specified in the transit agency feed in YYYYMMDD format."
		},
		
		{
			"report" : "Transit Agencies Summary Report",
			"metric" : "Feed Information",
			"definition" : "If available, this field points to the feed information such as name, version, publisher name and publisher URL."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Route Miles",
			"definition" : "Summation of the lengths of the longest route variant for all routes that the transit agency serves."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Route Stops",
			"definition" : "Total number of stops served by the transit agency."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Stops Per Route Mile",
			"definition" : "Number of stops in the agency\'s routes divided by Route Miles."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Service Hours",
			"definition" : "Total hours the transit agency spends on serving all round trips of routes. Service hours may be calculated for a specific date or a set of dates specified using the calendar. Reported number are cumulative over the selected dates."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Service Miles",
			"definition" : "Total miles driven by a transit agency over all round trips of a route. Service miles may be calculated for a specific date or a set of dates specified using the calendar. Reported number are cumulative over the selected dates."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Urban Population Served",
			"definition" : "Summation of unduplicated urban population within X-mile radius (i.e., stop distance) of all stops that the transit agency serves."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Rural Population Served",
			"definition" : "Summation of unduplicated rural population within X-mile radius (i.e., stop distance) of all stops that the transit agency serves."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Employment (RAC) Served",
			"definition" : "Summation of  Employment (RAC) within X-mile radius (i.e., stop distance) of all stops that the transit agency serves."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Employees (WAC) Served",
			"definition" : "Summation of Employees(WAC) within X-mile radius (i.e., stop distance) of all stops that the transit agency serves."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Service Stops",
			"definition" : "Number of trips scheduled at a stop in a route. The service stops for a route is calculated as its stop count multiplied by the number of visits per stop. Reported number are cumulative over the selected dates."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Urban Population Served By Service",
			"definition" : "Total unduplicated urban population impacted within an X-mile radius (i.e., stop distance) of all stops that the transit agency serves. Urban population served by service is calculated as service stops multiplied by the unduplicated urban population within an X-mile radius (i.e., stop distance) of all stops that the transit agency serves. Reported number is cumulative over the selected dates."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Rural Population Served By Service",
			"definition" : "Total unduplicated rural population impacted within an X-mile radius (i.e., stop distance) of all stops that the transit agency serves. Rural population served by service is calculated as service stops multiplied by the unduplicated rural population within an X-mile radius (i.e., stop distance) of all stops that the transit agency serves. Reported number is cumulative over the selected dates."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Employment (RAC) Served By Service",
			"definition" : "Total Employment (RAC) impacted within an X-mile radius (i.e., stop distance) of all stops that the transit agency serves. Employment (RAC) served by service is calculated as service stops multiplied by the unduplicated Employment (RAC) within an X-mile radius (i.e., stop distance) of all stops that the transit agency serves. Reported number is cumulative over the selected dates."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Employees (WAC) Served By Service",
			"definition" : "Total Employees (WAC)  impacted within an X-mile radius (i.e., stop distance) of all stops that the transit agency serves. Employees (WAC) served by service is calculated as service stops multiplied by the unduplicated Employees (WAC) within an X-mile radius (i.e., stop distance) of all stops that the transit agency serves. Reported number is cumulative over the selected dates."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Service Days",
			"definition" : "Set of days (from the selected days) in which at least one trip is served by the selected transit agency."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Hours of Service",
			"definition" : "Earliest and latest arrival and departure times of all stops served by the transit agency."
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
});

reportsJson = '{ "name": "Reports" , "children" : [\n';
$.each(Object.keys(reportsDic),function(index,key){
	reportsJson += '{ "name": "' + key + '", "children" : [\n';
	$.each(reportsDic[key],function(ind, metric){
		reportsJson += '{ "name" : "' + metric.metric +  '", "children" :[{"name" : "' + metric.definition + '"}]';
		if (ind+1 == reportsDic[key].length) reportsJson += "}\n]}\n";
		else reportsJson += "},\n";
	})
	if (index+1 < Object.keys(reportsDic).length) reportsJson += ',';
	else reportsJson += '\n]}';
});
