var metricsDic = {}; // Dictionary built based on the metrics array of object
						// to make collapsible tree categorized based on metrics
var reportsDic = {}; // Dictionary built based on the metrics array of object
						// to make collapsible tree categorized based on reports
var metricsJson = "";
var reportsJson = "";

$.each(metricDef, function(index, item) {
	metricsDic[item.metric.trim()] = metricsDic[item.metric.trim()] || [];
	metricsDic[item.metric.trim()].push(item);

	reportsDic[item.report.trim()] = reportsDic[item.report.trim()] || [];
	reportsDic[item.report.trim()].push(item);

});

// sorting the objects
var sortedMetricsDic = {};
var sortedReportsDic = {};
$.each(Object.keys(metricsDic).sort(), function(index, item) {
	sortedMetricsDic[item] = metricsDic[item].sort(function(a,b){return a.report.localeCompare(b.report)});
});
$.each(Object.keys(reportsDic).sort(), function(index, item) {
	sortedReportsDic[item] = reportsDic[item].sort(function(a,b){ return a.metric.localeCompare(b.metric)});
});
metricsDic = sortedMetricsDic;
reportsDic = sortedReportsDic;

// putting together metrics json to build the collapsible tree.
metricsJson = '{ "name": "Metrics" , "children" : [\n';
$.each(Object.keys(metricsDic), function(index, key) {
	metricsJson += '{ "name": "' + key + '", "children" : [\n';
	$.each(metricsDic[key], function(ind, metric) {
		metricsJson += '{ "name" : "' + metric.report.trim()
				+ '", "children" :[{"name" : "' + metric.definition.trim() + '"}]';
		if (ind + 1 == metricsDic[key].length)
			metricsJson += "}\n]}\n";
		else
			metricsJson += "},\n";
	})
	if (index + 1 < Object.keys(metricsDic).length)
		metricsJson += ',';
	else
		metricsJson += '\n]}';
});

//putting together reports json to build the collapsible tree.
reportsJson = '{ "name": "Reports" , "children" : [\n';
$.each(Object.keys(reportsDic), function(index, key) {
	reportsJson += '{ "name": "' + key + '", "children" : [\n';
	$.each(reportsDic[key], function(ind, metric) {
		reportsJson += '{ "name" : "' + metric.metric.trim()
				+ '", "children" :[{"name" : "' + metric.definition.trim() + '"}]';
		if (ind + 1 == reportsDic[key].length)
			reportsJson += "}\n]}\n";
		else
			reportsJson += "},\n";
	})
	if (index + 1 < Object.keys(reportsDic).length)
		reportsJson += ',';
	else
		reportsJson += '\n]}';

	// sort the dictionaries alphabetically

});

/**
 * Takes a string and splits it based on the length and return <tspan> tags to
 * be put into "svg:text" tag.
 * 
 * @param len -
 *            maximum number of characters in a line
 */
function trimLengthyTexts(len) {
	var content = '';
	$.each($('text'), function(ind, item) {
		content = $(item).text();
		if (content.length > len) {
			var lines = splitLines(content, len);
			var newHtml = '';
			$.each(lines, function(ind, item) {
				if (ind == 0)
					newHtml += '<tspan x="0" dy="5">' + item + '</tspan>';
				else
					newHtml += '<tspan x="7" dy="12">' + item + '</tspan>';
			})
			$(item).empty();
			$(item).html(newHtml);
		}
	})
}

/**
 * Split a string into an array of lines of size lineLength or less
 * 
 * @param string
 * @param lineLength
 * @returns {Array}
 */
function splitLines(string, lineLength) {
	var line = '';
	var tempIndex;
	var output = [];
	while (string.length > lineLength) {
		tempIndex = string.substring(0, lineLength).lastIndexOf(' ');
		line = string.substring(0, tempIndex + 1);
		output.push(line);
		string = string.substring(tempIndex + 1, string.length);
	}
	output.push(string);
	return output;
}

function sort(dic) {
	console.log(dic);
}
