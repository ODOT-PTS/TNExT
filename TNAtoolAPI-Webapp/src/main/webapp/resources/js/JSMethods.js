// Copyright (C) 2015 Oregon State University - School of Mechanical,Industrial and Manufacturing Engineering 
//   This file is part of Transit Network Explorer Tool.
//
//    Transit Network Explorer Tool is free software: you can redistribute it and/or modify
//    it under the terms of the GNU  General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Transit Network Explorer Tool is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU  General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Transit Network Explorer Tool.  If not, see <http://www.gnu.org/licenses/>.
// =========================================================================================================
//	  This script contains JavaScript variables and methods used to generate tabular reports
//	  in the Transit Network Explorer Tool.
// =========================================================================================================


/////////////////////////////////
//////						/////
//////		Variables		/////
//////						/////
/////////////////////////////////
var key = Math.random();
var maxRadius = 5;
var qstring = '';
var qstringd = '';
var qstringx = '0.25';
var nameString = '';
var agencyId = getURIParameter("agency");
var w_qstringx = parseFloat(getURIParameter("x"));
var w_qstringl = parseInt(getURIParameter("l"));
var keyName = getURIParameter("n");
var gap = parseFloat(getURIParameter("gap"));
var dbindex = parseInt(getURIParameter("dbindex"));
var popYear = parseInt(getURIParameter("popYear"));
var progVal = 0;
var d = new Date();
var w_qstring, w_qstringd, ajaxURL, html, temp, table,nav, metricsDefForToolips;
var docMetadata = '';
var tableProperties = {
	ordering : true,
	hiddenCols : [],
	hiddenRows : [],
	unsortableCols : [],
	colsToExport : [],
	iDisplayLength : 14,
	paging : true,
	bSort : true,
	bAutoWidth : false
};
$.getScript('../resources/data/navigation-tree.js');

// ///////////////////////////////
// //// 					/////
// //// 	Methods 		/////
// //// 					/////
// ///////////////////////////////
/**
 * populated the drop down list of databases in tabular reports
 */
function loadDBList() {
	$.ajax({
		type : 'GET',
		datatype : 'json',
		url : '/TNAtoolAPI-Webapp/queries/transit/DBList',
		async : false,
		success : function(d) {
			var select = document.getElementById("dbselect");
			select.options.length = 0;
			var menusize = 0;
			$.each(d.DBelement, function(i, item) {
				var option = document.createElement('option');
				option.text = item;
				option.value = i;
				select.add(option, i);
				menusize++;
			});
			if (dbindex < 0 || dbindex > menusize - 1) {
				dbindex = 0;
				history.pushState('data', '', document.URL.split("dbindex")[0]
						+ 'dbindex=0');
			}
			select.options.size = menusize;
			select.selectedIndex = dbindex;
		}
	});
}

/**
 * initializes the progress bar and checks for progress every 5000ms
 * Ed 2017-10-26: changed from 100ms to 5000ms 
 */
function progressBar() {
	var progressLabel = $(".progress-label");
	$("#progressbar")
			.progressbar(
					{
						value : false,
						change : function() {
							progressLabel
									.html('<table><tr><td>Report in progress... </td><td>'
											+ $(this).progressbar("value")
											+ "% "
											+ '</td><td></span><img src="../resources/images/loadingGif.gif" alt="loading" style="width:20px;height:20px"></td></tr></table>');
						}
					});
	var prog = false;
	function progress() {
		$.ajax({
			type : 'GET',
			datatype : 'json',
			url : '/TNAtoolAPI-Webapp/queries/transit/PorgVal?&key=' + key,
			async : true,
			success : function(item) {
				progVal = parseInt(item.progVal);
				if (progVal == 0) {
					if(prog){
						progVal=5000;
						clearTimeout(timeVar);
					}else{
						progVal=false;
					}
				} else {
					prog = true;
				}
				$("#progressbar").progressbar("value", progVal);
			}
		});
		if (progVal == 5000) {
			clearTimeout(timeVar);
		}
	}
	timeVar = setInterval(progress, 5000);
}

function pad(s) {
	return (s < 10) ? '0' + s : s;
}

/**
 * initializes datatables and sets the required attributes like number of records,
 * which columns to hide or sort and etc.
 * @returns
 */
function buildDatatables() {
	var table = $('#RT').DataTable(
			{
				"responsive": "true",
				"scrollX": "100%",
				"ordering": tableProperties.ordering,
				"paging" : tableProperties.paging,
				"bAutoWidth" : tableProperties.bAutoWidth,
				"bSort" : tableProperties.bSort,
				"iDisplayLength" : tableProperties.iDisplayLength,
				"aoColumnDefs" : [ {
					"bSortable" : false,
					"aTargets" : tableProperties.unsortableCols
				}, {
					"visible" : false,
					"targets" : tableProperties.hiddenCols
				} ],
				dom : 'Bfrtip',
				buttons : [
						{
							className : 'buttons-csv-meta buttons-html5',
							footer : false,
							fieldSeparator : ',',
							fieldBoundary : '"',
							escapeChar : '"',
							charset : null,
							header : true,
							text : "Export CSV & Metadata",
							// toolTip: "Sources of the data and description of
							// the metrics",
							action : function(e, dt, node, config) {
								var output = exportData(dt, config).str;
								var charset = config.charset;
								if (config.customize) {
									output = config.customize(output, config);
								}
								if (charset !== false) {
									if (!charset) {
										charset = document.characterSet
												|| document.charset;
									}
									if (charset) {
										charset = ';charset=' + charset;
									}
								} else {
									charset = '';
								}
								var zip = new JSZip();
								zip.file($(document).find("title").text()
										+ "-metaData.txt", getMetadata());
								zip.file($(document).find("title").text()
										+ ".csv", output, {
									type : 'text/csv' + charset
								});
								zip.generateAsync({
									type : "blob"
								}).then(function(content) {
								   saveAs(content, $(document).find("title")
										.text()+".zip");
								});
							}
						}, {
							extend : 'print',
							text : 'Print Report',
							footer : false,
							exportOptions : {
								stripHtml : false,
								stripNewlines : false,
								columns : ':visible'
							}
						},
				]
			});
	$(".dt-buttons").css("float", "right");
	$(".dt-buttons").css("margin-bottom", "1em");
	$("#RT_length").remove();
	$("#RT_filter").insertBefore("#RT_info");
	$(".dataTables_filter").css("float", "left");
	$(".dataTables_filter").before("<br>");
	return table;
}

/**
 * 
 * @param table - Datatable
 * @param column - Int
 * @param order - String ('asc' or 'desc')
 */
function orderTable(table, column, order){
	table
    .column( column + ':visible' )
    .order( order )
    .draw();
}

/**
 * Checks if x is larger than the maximum search radius (maxRadius)
 * @param x
 * @returns {Boolean}
 */
function exceedsMaxRadius(x){
	if (x>maxRadius){
		return true;
	}else{
		return false;
	}
}

/**
 * sets the position and transitions for tooltips in tabular reports
 */
function updateToolTips() {
	$(document).tooltip({});
	
	$(".input").each(function(index, object) {
		$(object).on('input', function() {
			$("#submit").trigger('mouseenter');
		});
	});

	$("#submit").tooltip({});
}

/**
 * updates parameters in the URL based on the values 
 * entered by the users.
 * 
 */
function reloadPage() {
	// check whether the value put in by the users exceeds a maximum 
	var exit = false;
	$(".radius").each(function(index, object) {
		var tmpX = parseFloat($(object).val()).toString();
		if (exceedsMaxRadius(tmpX)){	// Checks if the entered search radius exceeds the maximum.
			alert('Enter a number less than ' + maxRadius + ' miles for Search Radius.');
			exit = true;
		}
	});
	
	// iterating over tags that belong to [.input] class and updating the associated values in the URL. 
	if (!exit){
		var output = document.URL;
		$(".input").each(function(index, object) {
			console.log(output, object.name, object.value);
			output = setURIParameter(output, object.name, object.value, null)
		});
		
	
		// updating dates
		try {
			if($('#stime').val()!=null){
				var stime=$('#stime').val();
				var etime=$('#etime').val();
				var hs=stime.split(":");
				var st=(parseInt(hs[0])*100)+parseInt(hs[1]);
				var he=etime.split(":");
				var et=(parseInt(he[0])*100)+parseInt(he[1]);
				if(et>st){
					output = setURIParameter(output, 'stime', stime, null);
		        	output = setURIParameter(output, 'etime', etime, null);
				} else{
					alert("Choose an end time that is the after start time");
					return
				}
			}
			var dates = $('#datepicker').multiDatesPicker('getDates');
			if (dates.length == 0) {
				$("#datepicker").multiDatesPicker({
					addDates : [ new Date() ]
				});
			}
			dates = $('#datepicker').multiDatesPicker('getDates');
			w_qstringd = dates.join(",");
			output = setURIParameter(output, 'n', setDates(w_qstringd), keyName)
			keyName = setDates(w_qstringd);
		} catch (err) {
			console.log("error: " + err.message);
		}
		window.open(output,'_self');
	}
}

/**
 * returns a dictionary with array of report name and metric name
 * as key and metric definition as value.
 * @returns Object{[report,metric]}
 */
function populateMetricDefs(){
	var output = {};
	// metricDef is located in src>main>webapp>resources>data>metricDefinitions.js
	$.each(metricDef, function(index,item){
		output[[item.report.trim(), item.metric.trim()]] = item.definition;
	});
	return output;
}

/**
 * removes the substrings of type '([number])' form the string.
 * this method is developed to remove Input/Output mapping 
 * numbers from metric titles in tabular reports. Used in populateMetricDefs().
 * 
 * @returns {String}
 */
String.prototype.strip = function () {
	var str = this;
	for ( var i = 1; i < 10 ; i++)
		str = str.replace('('+i+')', '');
	return str;
}
/**
 * gathers the metadata of the tabular report in a text file to be exported.
 */
function getMetadata() {
	
	/*
	 * Appending metadata
	 */
	var output = 		   'Metadata\r\n';
	output = output.concat('--------------------\r\n');
	if ($('#reportTitle').text() == ""){
		output = output.concat('Title: ' + $('h2').text() + '\r\n');
	}
	else{
		output = output.concat('Title: ' + $('#reportTitle').text() + '\r\n');
	}
	output = output.concat('Time & Date: ' + Date() + '\r\n');
	output = output.concat('Tool Version: ' + getVersion());
	
	/*
	 * Appending data sources
	 */
	var counter = 1;
	output = output.concat('\r\n\r\n\r\nData Sources\r\n');
	output = output.concat('--------------------\r\n');
	$.each( datasources, function(dataset, datasetInfo){
	    if (datasetInfo.common.flag && $.inArray($(document).attr('title'), datasetInfo.common.exceptions)<0){
	    	output = output.concat(counter++ + ')\r\n');
	    	if (datasetInfo.survey != null) output = output.concat('Survey: ' + datasetInfo.survey + '\r\n');
	    	if (datasetInfo.dataset != null) output = output.concat('Dataset: ' + datasetInfo.dataset + '\r\n');
	    	if (datasetInfo.table != null) output = output.concat('Table: ' + datasetInfo.table + '\r\n');
	    	output = output.concat('\r\n');
	    }else if (!datasetInfo.common.flag && $.inArray($(document).attr('title'), datasetInfo.reports)>-1){
	    	output = output.concat(counter++ + ')\r\n');
	    	if (datasetInfo.survey != null) output = output.concat('Survey: ' + datasetInfo.survey + '\r\n');
	    	if (datasetInfo.dataset != null) output = output.concat('Dataset: ' + datasetInfo.dataset + '\r\n');
	    	if (datasetInfo.table != null) output = output.concat('Table: ' + datasetInfo.table + '\r\n');
	    	output = output.concat('\r\n');
	    }	    	
	});
	
	/*
	 * Appending report parameters
	 */ 
	output = output.concat('\r\n\r\n\r\nReport Parameters\r\n');
	output = output.concat('--------------------\r\n');	
	$(".input").each(function(index, object) {
		console.log($(object).attr('class'));
		if ($(object).is('td'))
			output = output.concat(object.dataset.label + ': ' + $(object).text() + '\r\n');
		else if ($(object).is('select'))
			output = output.concat(object.dataset.label + ': ' + $("#" + $(object).attr('id') + " option[value='"+ object.value +"']").text() + '\r\n');
		else
			output = output.concat(object.dataset.label + ': ' + object.value + '\r\n');
	});
	if (keyName != null) output = output.concat('Selected Service Dates : ' + getDates(keyName));
	
	/*
	 * Appeding Metric Defenitions
	 */ 
	output = output.concat('\r\n\r\n\r\nMetric Definitions\r\n');
	output = output.concat('--------------------\r\n');
	$('#RT .metric').each(function(i,item){
		output = output.concat($(item).text() + ': ' + item.title + '\r\n')
		});
	output = output.concat('\r\n');
	
	// Adding description of the input/output mapping numbers that map inputs and metrics
	$(".input").each(function(index, object) {
		if (!$(object).is('select') && object.dataset.iomap != undefined){
			output = output.concat('(' + object.dataset.iomap + ') '  + object.dataset.label + '\r\n');
		}	
	});
	
	// Adding selected dates if any
	if (keyName != null) output = output.concat('(' + dateIOnumber + ') Selected Service Dates');
	
	return output;
}

/**
 * Changes the current value of a parameter in a url with a new one
 * 
 * @param url
 * @param param - the parameter to be set in the URL
 * @param newValue
 * @param currentValue
 * @return url
 */
function setURIParameter(url, param, newValue, currentValue) {
	if (newValue != currentValue) {
		var URL = url.split("&" + param + "=");
		var last = "";
		URL.push(""); // handle new params
		if (URL[1].indexOf("&") != -1) {
			last = URL[1].substring(URL[1].indexOf("&"));
		}
		return URL[0] + "&" + param + "=" + newValue + last;
	} else
		return url;
}

/**
 * returns the latest version of the tool 
 * @return version
 */
function getVersion(){
	var version = "";
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/queries/transit/getVersion",
        dataType: "json",
        async: false,
        success: function(d) {
        	version = d.DBError;
        }
	});	
	return version;
}

/**
 * returns the value of a parameter
 * @param param
 * @param asArray
 * @returns
 */
function getURIParameter(param, asArray) {
	return document.location.search.substring(1).split('&').reduce(
			function(p, c) {
				var parts = c.split('=', 2).map(function(param) {
					return decodeURIComponent(param);
				});
				if (parts.length == 0 || parts[0] != param)
					return (p instanceof Array) && !asArray ? null : p;
				return asArray ? p.concat(parts.concat(true)[1]) : parts
						.concat(true)[1];
			}, []);
}

/**
 * returns the date associated with the 3 character input string 
 * as dd/mm/yyyy
 * 
 * @param string of length 3
 * @returns date
 */
function getDates(hex) {
	if (hex == "--") {
		return null;
	}

	var year = [ 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
			'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
			'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
			'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
			'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '!', '@',
			'#', '$', '%', '^', '*', '(', ')', '-', '+', '_', '`', '~' ];
	var month = [ 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l' ];
	var day = [ 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
			'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
			'z', 'A', 'B', 'C', 'D', 'E' ];

	var str = "";
	var tmp = "";
	var j = 0;
	for (var i = 0; i < Math.floor(hex.length / 3); i++) {
		tmp = month.indexOf(hex[j]) + 1;
		if (tmp < 10) {
			str += '0';
		}
		str += tmp;
		str += '/';
		j++;

		tmp = day.indexOf(hex[j]) + 1;
		if (tmp < 10) {
			str += '0';
		}
		str += tmp;
		str += '/';
		j++;

		str += year.indexOf(hex[j]) + 2000;
		if (i < Math.floor(hex.length / 3) - 1) {
			str += ',';
		}
		j++;
	}
	return str;

}

/**
 * gets a date and encode it in a string of length 3
 * @param str
 * @returns {String}
 */
function setDates(str) {
	var year = [ 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
			'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
			'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
			'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
			'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '!', '@',
			'#', '$', '%', '^', '*', '(', ')', '-', '+', '_', '`', '~' ];
	var month = [ 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l' ];
	var day = [ 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
			'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
			'z', 'A', 'B', 'C', 'D', 'E' ];

	var strs = str.split(',');
	var hex = "";
	var date;
	for (var i = 0; i < strs.length; i++) {
		date = strs[i].split('/');
		if (parseInt(date[2]) > 2075) {
			date[2] = '2075';
		} else if (parseInt(date[2]) < 2000) {
			date[2] = '2000';
		}
		hex += month[parseInt(date[0]) - 1] + day[parseInt(date[1] - 1)]
				+ year[parseInt(date[2]) - 2000];
	}
	return hex;
}

/**
 * initializes the date picker widget and sets the dates based on the dates
 * in "w_qstringd", a variable used to store selected date values in tabular
 * reports
 * 
 * @param key
 */
function go(key) {
	$("#datepicker").multiDatesPicker(
			{
				changeMonth : true,
				changeYear : true,
				addDates : w_qstringd.split(","),
				onSelect : function(date) {
					dateID = date.replace("/", "").replace("/", "");
					if ($("#" + dateID).length == 0) {
						addDate(date);
						$("#submit").trigger('mouseenter');
					} else {
						$("#" + dateID).remove();
						$("#submit").trigger('mouseenter');
					}
					$("#accordion > h3")
							.html(
									$('#datepicker').multiDatesPicker(
											'getDates').length
											+ " day(s) selected<span class='IOSym' style='font-size:10'>" + dateIOnumber + "</span>");
					$('.selectedDate').css("text-align", "center");
					$('.ui-accordion-header').css({'width':'100%','font-size':'80%','margin':'auto','text-align':'center'});
				}
			});

	var cdate;
	for (var i = 0; i < w_qstringd.split(",").length; i++) {
		cdate = w_qstringd.split(",")[i];
		dateID = cdate.replace("/", "").replace("/", "");
		addDate(cdate);
	}

	$("#accordion").accordion({
		collapsible : true,
		active : false,
		heightStyle : "content"
	});
	$("#accordion").accordion("refresh");
	$("#accordion > h3")
			.html(w_qstringd.split(",").length + " day(s) selected<span class='IOSym' style='font-size:10'>(" + dateIOnumber + ")</span>");
	$('.selectedDate').css("text-align", "center");
	$('.ui-accordion-header').css({'width':'100%','font-size':'80%','margin':'auto','text-align':'center'});
}

/**
 * Adds a date to the datepicker widget
 * @param date
 */
function addDate(date) {
	$(
			"<li title='Click to remove.' id=" + dateID
					+ " class='selectedDate' onclick=\"dateRemove(this, '"
					+ date + "')\">"
					+ Date.parse(date).toString('dddd, MMMM d, yyyy') + "</li>")
			.appendTo("#accordionItems");
	$("#" + dateID).css({
		"border" : "1px solid black",
		"padding-left" : "10px",
		"font-size" : "95%",
		"display" : "block",
		"width" : "80%",
		"background-color" : "grey",
		"text-decoration" : "none",
		"color" : "white",
		"margin" : "3px",
		"border-radius" : "5px"
	});
	$("#" + dateID).hover(function() {
		$(this).css({
			"cursor" : "pointer",
			"-moz-transform" : "scale(1.1,1.1)",
			"-webkit-transform" : "scale(1.1,1.1)",
			"transform" : "scale(1.1,1.1)"
		});
	}, function() {
		$(this).css({
			"cursor" : "pointer",
			"-moz-transform" : "scale(1,1)",
			"-webkit-transform" : "scale(1,1)",
			"transform" : "scale(1,1)"
		});
	});
	$('.selectedDate').css('margin', 'auto');
}

/**
 * returns user's session
 * @returns {String}
 */
function getSession() {
	var username = "admin";
	$.ajax({
		type : "GET",
		url : "/TNAtoolAPI-Webapp/FileUpload?getSessionUser=gsu",
		dataType : "json",
		async : false,
		success : function(d) {
			username = d.username;
		}
	});
	return username;
}

/**
 * removes data, d, and its associated html tag, e,  from datepicker widget
 * @param e
 * @param d
 */
function dateRemove(e, d) {
	$(e).remove();
	$("#datepicker").multiDatesPicker('removeDates', d);
	$("#accordion > h3").html(
			$('#datepicker').multiDatesPicker('getDates').length
					+ " day(s) selected<span class='IOSym' style='font-size:10'>(4)</span>");
	$("#submit").trigger('mouseenter');
}


/**
 * returns the data populated in tabular reports. This method is called while
 * setting the datatables properties.
 * @param dt
 * @param config
 * @returns {object}
 */
function exportData(dt, config) {
	var newLine = NewLine(config);
	var data = dt.buttons.exportData(config.exportOptions);
	var boundary = config.fieldBoundary;
	var separator = config.fieldSeparator;
	var reBoundary = new RegExp(boundary, 'g');
	var escapeChar = config.escapeChar !== undefined ? config.escapeChar : '\\';
	var join = function(a) {
		var s = '';

		// If there is a field boundary, then we might need to escape it in
		// the source data
		for (var i = 0, ien = a.length; i < ien; i++) {
			if (i > 0) {
				s += separator;
			}

			s += boundary ? boundary
					+ ('' + a[i]).replace(reBoundary, escapeChar + boundary)
					+ boundary : a[i];
		}

		return s;
	};

	var header = config.header ? join(data.header) + newLine : '';
	var footer = config.footer && data.footer ? newLine + join(data.footer)
			: '';
	var body = [];

	for (var i = 0, ien = data.body.length; i < ien; i++) {
		body.push(join(data.body[i]));
	}

	return {
		str : header + body.join(newLine) + footer,
		rows : body.length
	};
}

/**
 * retrurns new line character based on inpur
 * 
 * @param configS
 */
function NewLine(config) {
	return config.newline ? config.newline : navigator.userAgent
			.match(/Windows/) ? '\r\n' : '\n';
}

/**
 * takes a number and returns it in non-scientific notation with 1000 seperator (,) 
 * @param x
 * @returns
 */
function numberconv(x) {
	x = x + '';
	if (x.indexOf('E') > -1) 
		x = Number(x).toString();
	
	var parts = x.split(".");
	parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	if (parts[1] > 0) {
		return parts.join(".");
	} else {
		return parts[0];
	}
}

/**
 * returns true if the character typed in is a number
 */
function isWholeNumber(evt) {
	evt = (evt) ? evt : window.event;
	var charCode = (evt.which) ? evt.which : evt.keyCode;
	if (charCode > 31 && (charCode < 48 || charCode > 57)) {
		return false;
	}
	return true;
}

/**
 * returns true if the typed character is a number or '.'
 * @param evt
 * @returns {Boolean}
 */
function isNumber(evt) {
	evt = (evt) ? evt : window.event;
	var charCode = (evt.which) ? evt.which : evt.keyCode;
	if (charCode == 46) {
		if ($("#" + evt.target.id).val().indexOf('.') !== -1)
			return false;
	} else if (charCode > 31 && (charCode < 48 || charCode > 57)) {
		return false;
	}
	return true;
}

/**
 * populates the drop down list associated with "Population / Employment Source" on tabular reports
 */
function setPopOptions() {
	var popselect = document.getElementById("popselect");
	var years = [ 2010, 2015, 2020, 2025, 2030, 2035, 2040, 2045, 2050 ];
	var option;
	for (var i = 0; i < years.length; i++) {
		option = document.createElement('option');
		option.text = years[i];
		option.value = years[i];
		popselect.add(option, i);
	}
	;
	$('#popselect').val(popYear);
}

/**
 * returns the index of the latest database
 * @return int
 */
function getDefaultDbIndex(){
	var dbindex = -1;
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/queries/transit/getDefaultDbIndex",
        dataType: "json",
        async: false,
        success: function(d) {
        	dbindex = d.DBError;
        }
	});	
	return dbindex;
}

/**
 * returns today's date in yyyymmdd
 * @returns {String}
 */
function currentDateFormatted(){
	var today = new Date();
	var dd = today.getDate();
	var mm = today.getMonth()+1; //January is 0!
	var yyyy = today.getFullYear();

	if(dd<10) {
	    dd='0'+dd;
	} 

	if(mm<10) {
	    mm='0'+mm;
	} 

	var today = ''+yyyy+mm+dd;
	return today;
}

/**
 * takes a date and remove '/' character from it
 * @param date
 * @returns
 */
function dateToString(date){
	var dArr = date.split("/");
	return dArr[2]+dArr[0]+dArr[1];
}

/**
 * adds the year options to the Heat Map 
 */
function setPopOptions1() {
	var popselect = document.getElementById("popselect");
	var years = [ 2010, 2011, 2012, 2013, 2014, 2015, 2016, 2017, 2018 ,2019 ,2020,2021,2022,2023,2024,2025 ];
	var option;
	for (var i = 0; i < years.length; i++) {
		option = document.createElement('option');
		option.text = years[i];
		option.value = years[i];
		popselect.add(option, i);
	}
	;
	$('#popselect').val(popYear);
}

/**
 * takes a date string as 'yyyymmdd' and returns it as 'dd/mm/yyy'
 *
 * @param date - String
 * @returns String
 */
function stringToDate(str){
	var sArr = new Array();
	sArr.push(str.substring(4, 6));
	sArr.push(str.substring(6, 8));
	sArr.push(str.substring(0, 4));
	var output = sArr.join("/");
	return output;
}

/**
 * add a '$' character to the end of the input
 * @param v
 * @returns {String}
 */
function showDollarSign(v) {
	if (!isNaN(v))
		return '$' + v;
	else
		return 'N/A';
}

/**
 * adds a '%' character to the end of the input
 * @param x
 * @returns {String}
 */
function addPercent(x) {
	return x + '%';
}

/**
 * trim the length of the input to 12 characters.
 * used to trim longitudes.
 * @param x
 * @returns
 */
function trimLat(x) {
	if (x.length > 12)
		x = x.substring(0, 11);
	return x;
}

/**
 * trim the length of the input to 14 characters.
 * used to trim latitude.
 * @param x
 * @returns
 */
function trimLon(x) {
	if (x.length > 14)
		x = x.substring(0, 13);
	return x;
}

/**
 * Adds the navigation tree to the tablular reports
 */
function appendNavigation(name){
	if($( "#td2" ).length==0){
		$( "#td1" ).after( '<td id="td2"><div></div></td>');
	}
	if(name==null||name==undefined || name==""){
		name = "";
	}else{
		name = "<br><i style='font-size:80%'>("+name+")</i>";
	}
	var title = $(document).find("title").text();
	nav = findNavigationId(title);	
	var html = '<div id="navigationAccordion" style="font-size:90%;width:90%">';
	html += '<h3 id="reportTitle">'+title+name+'</h3><div><div id="navigationTree" style="font-size: 80%;font-weight: normal;"></div></div></div>';
	$('h2').css('width','100%');
	$('h2').append(html);
	$("#navigationAccordion > h3").css('text-align','center');
	
	$("#navigationAccordion").accordion({
		collapsible : true,
		active : false,
		heightStyle : "content"
	});
	$('#navigationTree').jstree(navigationMap);
	$('#navigationTree').on('loaded.jstree', function (e, data) {
		hideTreeNodes($(this),nav);
	});
	$('#navigationTree').on('open_node.jstree', function (e, data) {
		$('.jstree-icon.jstree-themeicon').css('display','none');
	});
	$('#navigationAccordion > h3').click(function(){
		$('.jstree-icon.jstree-themeicon').css('display','none');
		$($('#navigationTree' + ' li[id="'+nav+'"] a')[0]).css({'font-weight': 'bold','font-size': '110%'});
		for(var i=0; i<children.length; i++){
			$($('#navigationTree' + ' li[id="'+children[i]+'"] a')[0]).css({'font-style': 'italic'});
		}
	});
}

/**
 * returns the navigation parameter of a tabular report based on its title
 * @param title
 * @returns {String}
 */
function findNavigationId(title){
	var nav = getURIParameter("nav");
	if(nav==null || nav==undefined || nav=='undefined'){
		if(navigationIdMap[title]=="stateS"){
			nav = "stateS";
		}else{
			nav =  "stateS-"+navigationIdMap[title];
		}
	}else{
		nav += "-"+navigationIdMap[title];
	}	
	return nav;
}

/**
 * A depth-first-search recursive algorithm for building the title navigation trees in tabular reports
 * @param node
 * @param family
 * @param tree
 * @param nav
 */
function navigationTreeDFS(node, family, tree, nav){
	
	if(!family.includes(node.id)){
		if(tree.jstree().get_parent(node)==nav){
			tree.jstree().disable_node(node);
			$('#'+node.id+' i').prop('disabled', true);
			children.push(node.id);
		}else{
			tree.jstree().hide_node(node);
		}
		
	}else{
		tree.jstree().open_node(node);
		$.each(node.children, function(i,item){
			navigationTreeDFS(item, family, tree, nav);
		});
		
	}
}

/**
 * hides the nodes of the navigation tree down to the current tabular report
 * @param tree
 * @param nav
 */
function hideTreeNodes(tree,nav){
	var root = tree.jstree().get_json()[0];
	var current = tree.jstree().get_node(nav);
	var family = [];
	children = [];
	family.push(current.id);
	while(tree.jstree().get_parent(tree.jstree().get_node(family[family.length-1]))!="#"){
		family.push(tree.jstree().get_parent(tree.jstree().get_node(family[family.length-1])));
	}
	navigationTreeDFS(root, family, tree, nav);	
}

/**
 * Adds a '0' character to a one digit number
 * converts '1' to '01'. Used to display time 
 * 03:33:05.
 * 
 * @param input
 * @returns {String}
 */
function doubleDigit(input) {
	if (0 <= input && input < 10)
		return '0' + input;
	else
		return input + '';
}

/**
 * Converts the seconds to time. For example
 * 9000 is returned as 02:30:00.
 * @param input * 
 * @returns {String}
 */
function secToHour(input) {
	var hr = Math.floor(input / 3600);
	var min = Math.floor(input % 3600 / 60);
	var sec = Math.floor(input % 3600 % 60);
	return doubleDigit(hr) + ":" + doubleDigit(min) + ":" + doubleDigit(sec);
}

/**
 * drags the map to the input coordinates and draws a circle of
 * 0.25 miles diameter to generate the on-map report
 * @param latLng
 * 
 */
function drawCircleAroundCoordinate(latLng) {
	var lat = latLng[0];
	var lng = latLng[1];
	var x = 0.25;
	var marLat = (Math.round(lat * 1000000) / 1000000).toString().replace('.',
			'').replace('-', '');
	var marLng = (Math.round(lng * 1000000) / 1000000).toString().replace('.',
			'').replace('-', '');
	drawCentroid[0] = lat;
	drawCentroid[1] = lng;
	area = Math.pow(x, 2) * Math.PI;
	drawCentroid[0] = (Math.round(drawCentroid[0] * 1000000) / 1000000)
			.toString();
	drawCentroid[1] = (Math.round(drawCentroid[1] * 1000000) / 1000000)
			.toString();
	area = Math.round(area * 100) / 100;
	var that = drawControl._toolbars[L.DrawToolbar.TYPE]._modes.circle.handler;
	that.enable();
	that._startLatLng = [ lat, lng ];
	that._shape = new L.Circle([ lat, lng ], x * 1609.34,
			that.options.shapeOptions);
	that._map.addLayer(that._shape);
	that._fireCreatedEvent();
	that.disable();
}
