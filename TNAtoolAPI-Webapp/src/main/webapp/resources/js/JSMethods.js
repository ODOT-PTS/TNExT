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
var w_qstringd;
var w_qstring;
var keyName = getURIParameter("n");
var gap = parseFloat(getURIParameter("gap"));
var dbindex = parseInt(getURIParameter("dbindex"));
var popYear = parseInt(getURIParameter("popYear"));
var ajaxURL;
var progVal = 0;
var d = new Date();
var html, temp;
var table;
var docMetadata = '';
var nav;
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

// ///////////////////////////////
// //// 					/////
// //// 	Methods 		/////
// //// 					/////
// ///////////////////////////////

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
						progVal=100;
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
		if (progVal == 100) {
			clearTimeout(timeVar);
		}
	}
	timeVar = setInterval(progress, 100);
}

function pad(s) {
	return (s < 10) ? '0' + s : s;
}

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

function updateToolTips() {
	$(document).tooltip({
		position : {
			my : "bottom",
			at : "center top+12"
		}
	});
	
	$(".input").each(function(index, object) {
		$(object).on('input', function() {
			$("#submit").trigger('mouseenter');
		});
	});

	$("#submit").tooltip({
		open : function() {
			setTimeout(function() {
				$("#submit").trigger('mouseleave');
			}, 1000);
		}
	});
	
	// Add title attribute to the I/O relationship symbols.
	addIOeffects();
}

/**
 * Add title attribute to the I/O relationship symbols
 */
function addIOeffects(){
	$('.IOSym').each(function(index,object){
		$(object).attr('title','The number(s) shows the inputs on which the metric depends');
	});
}

function reloadPage() {
	var exit = false;
	$(".radius").each(function(index, object) {
		var tmpX = parseFloat($(object).val()).toString();
		if (exceedsMaxRadius(tmpX)){	// Checks if the entered search radius exceeds the maximum.
			alert('Enter a number less than ' + maxRadius + ' miles for Search Radius.');
			exit = true;
		}
	});
	
	if (!exit){
		var output = document.URL;
		$(".input").each(function(index, object) {
			output = setURIParameter(output, object.name, object.value, null)
		});
		try {
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
		window.location.href = output;
	}
}

/*
 * This method is implemented to be used for gathering the metadata of the
 * report in a text file to be exported.
 */
function getMetadata() {
	
	/*
	 * Appending metadata
	 */
	var output = 		   'Metadata\r\n';
	output = output.concat('--------------------\r\n');
	output = output.concat('Title: ' + $('h2').text() + '\r\n');
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
	 * Appending algortihm for ran to generate the report, if any.
	 */
	if (typeof algDesc !== 'undefined') {
//		output = output.concat('\r\n\r\n\r\nAlgorithm\r\n');
//		output = output.concat('--------------------\r\n');	
		output = output.concat(algDesc);
	}
	
	/*
	 * Appending report parameters
	 */ 
	output = output.concat('\r\n\r\n\r\nReport Parameters\r\n');
	output = output.concat('--------------------\r\n');	
	$(".input").each(function(index, object) {
		if ($(object).is('select'))
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
	$('.metric').each(function(i,item){
		output = output.concat($(item).text() + ': ' + item.title + '\r\n')
		});
	output = output.concat('\r\n');
	
	// Adding description of the footnotes that map inputs and metrics
	$(".input").each(function(index, object) {
		if (!$(object).is('select')){
			output = output.concat('(' + object.dataset.iomap + ') '  + object.dataset.label + '\r\n');
		}	
	});
	
	// Adding selected dates if exists.
	if (keyName != null) output = output.concat('(' + dateIOnumber + ') Selected Service Dates');
	
	return output;
}

function setURIParameter(url, param, newValue, currentValue) {
	if (newValue != currentValue) {
		var URL = url.split("&" + param + "=");
		var last = "";
		if (URL[1].indexOf("&") != -1) {
			last = URL[1].substring(URL[1].indexOf("&"));
		}
		return URL[0] + "&" + param + "=" + newValue + last;
	} else
		return url;
}

function getVersion(){
	var version = "";
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/getVersion",
        dataType: "json",
        async: false,
        success: function(d) {
        	version = d.DBError;
        }
	});	
	return version;
}

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
					$('.ui-accordion-header').css({'width':'90%','font-size':'80%','margin':'auto','text-align':'center'});
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

function dateRemove(e, d) {
	$(e).remove();
	$("#datepicker").multiDatesPicker('removeDates', d);
	$("#accordion > h3").html(
			$('#datepicker').multiDatesPicker('getDates').length
					+ " day(s) selected<span class='IOSym' style='font-size:10'>(4)</span>");
	$("#submit").trigger('mouseenter');
}

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

function NewLine(config) {
	return config.newline ? config.newline : navigator.userAgent
			.match(/Windows/) ? '\r\n' : '\n';
}

function numberconv(x) {
	if (x.indexOf('E') > -1) {
		x = Number(x).toString();
	}
	var parts = x.split(".");
	parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	if (parts[1] > 0) {
		return parts.join(".");
	} else {
		return parts[0];
	}
}

function numWithCommas(x) {
	return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

function isWholeNumber(evt) {
	evt = (evt) ? evt : window.event;
	var charCode = (evt.which) ? evt.which : evt.keyCode;
	if (charCode > 31 && (charCode < 48 || charCode > 57)) {
		return false;
	}
	return true;
}

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

function dateToString(date){
	var dArr = date.split("/");
	return dArr[2]+dArr[0]+dArr[1];
}

function stringToDate(str){
	var sArr = new Array();
	sArr.push(str.substring(4, 6));
	sArr.push(str.substring(6, 8));
	sArr.push(str.substring(0, 4));
	return sArr.join("/");
}

function showDollarSign(v) {
	if (!isNaN(v))
		return '$' + v;
	else
		return 'N/A';
}

function addPercent(x) {
	return x + '%';
}

function trimLat(x) {
	if (x.length > 12)
		x = x.substring(0, 11);
	return x;
}

function trimLon(x) {
	if (x.length > 14)
		x = x.substring(0, 13);
	return x;
}

/**
 * Add the navigation tree to the tablular reports
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
	html += '<h3>'+title+name+'</h3><div><div id="navigationTree" style="font-size: 80%;font-weight: normal;"></div></div></div>';
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

function findNavigationId(title){
	var nav = getURIParameter("nav");
	if(nav==null || nav==undefined){
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
	//return;
}


function hideTreeNodes(tree,nav){
	var root = tree.jstree().get_json()[0];
	var current = tree.jstree().get_node(nav);
	var family = [];
	children = [];
	family.push(current.id);//console.log(nav);console.log(root.id);console.log(current.id);alert(nav);return;
	while(tree.jstree().get_parent(tree.jstree().get_node(family[family.length-1]))!="#"){
		family.push(tree.jstree().get_parent(tree.jstree().get_node(family[family.length-1])));
	}
	//console.log(root);
	navigationTreeDFS(root, family, tree, nav);
	
}


var children;
var navigationIdMap = {
	"Statewide Summary Report" : "stateS",
	"Statewide Extended Report" : "stateX",
	"Transit Agencies Summary Report" : "agencyS",
	"Transit Agency Extended Report" : "agencyX",
	"Counties Summary Report" : "countyS",
	"County Extended Report" : "countyX",
	"Tracts Summary Report" : "tractS",
	"Tract Extended Report" : "tractX",
	"Congressional Districts Summary Report" : "congS",
	"Congressional District Extended Report" : "congX",
	"ODOT Transit Regions Summary Report" : "regionS",
	"ODOT Transit Region Extended Report" : "regionX",
	"Census Places Summary Report" : "placeS",
	"Census Place Extended Report" : "placeX",
	"Urban Areas Summary Report" : "urbanS",
	"Urban Area Extended Report" : "urbanX",
	"Routes Summary Report" : "route",
	"Stops Summary Report" : "stop",
};
var navigationMap = { 
	'core' : {
		'data' : [
		    //statewide statewide    
 	       	{ "id" : "stateS", "parent" : "#", "text" : "Statewide Summary Report", "data" : {"styleClass" : "test"}},
 	       
	 	    	//nodes under statewide summary
		       	{ "id" : "stateS-stateX", "parent" : "stateS", "text" : "Statewide Extended Report"},
		       	{ "id" : "stateS-agencyS", "parent" : "stateS", "text" : "Transit Agencies Summary Report" },
		       		//nodes under agency summary
		       		{ "id" : "stateS-agencyS-agencyX", "parent" : "stateS-agencyS", "text" : "Transit Agency Extended Report" },
		       		{ "id" : "stateS-agencyS-route", "parent" : "stateS-agencyS", "text" : "Routes Summary Report" },
		       		{ "id" : "stateS-agencyS-stop", "parent" : "stateS-agencyS", "text" : "Stops Summary Report" },
		       		{ "id" : "stateS-agencyS-urbanS", "parent" : "stateS-agencyS", "text" : "Urban Areas Summary Report" },
		       			//nodes under agency->urban summary
				       	{ "id" : "stateS-agencyS-urbanS-urbanX", "parent" : "stateS-agencyS-urbanS", "text" : "Urban Area Extended Report" },
				       	{ "id" : "stateS-agencyS-urbanS-route", "parent" : "stateS-agencyS-urbanS", "text" : "Routes Summary Report" },
				       	{ "id" : "stateS-agencyS-urbanS-stop", "parent" : "stateS-agencyS-urbanS", "text" : "Stops Summary Report" },
		       		{ "id" : "stateS-agencyS-countyS", "parent" : "stateS-agencyS", "text" : "Counties Summary Report" },
			       		//nodes under agency->county summary
					   	{ "id" : "stateS-agencyS-countyS-countyX", "parent" : "stateS-agencyS-countyS", "text" : "County Extended Report" },
					   	{ "id" : "stateS-agencyS-countyS-route", "parent" : "stateS-agencyS-countyS", "text" : "Routes Summary Report" },
					   	{ "id" : "stateS-agencyS-countyS-stop", "parent" : "stateS-agencyS-countyS", "text" : "Stops Summary Report" },
					   	{ "id" : "stateS-agencyS-countyS-urbanS", "parent" : "stateS-agencyS-countyS", "text" : "Urban Areas Summary Report" },
					       //nodes under agency->county->urban summary
					       { "id" : "stateS-agencyS-countyS-urbanS-urbanX", "parent" : "stateS-agencyS-countyS-urbanS", "text" : "Urban Area Extended Report" },
					       { "id" : "stateS-agencyS-countyS-urbanS-route", "parent" : "stateS-agencyS-countyS-urbanS", "text" : "Routes Summary Report" },
					       { "id" : "stateS-agencyS-countyS-urbanS-stop", "parent" : "stateS-agencyS-countyS-urbanS", "text" : "Stops Summary Report" },
					    { "id" : "stateS-agencyS-countyS-tractS", "parent" : "stateS-agencyS-countyS", "text" : "Tracts Summary Report" },
					        //nodes under agency->county->tract summary
					        { "id" : "stateS-agencyS-countyS-tractS-tractX", "parent" : "stateS-agencyS-countyS-tractS", "text" : "Tract Extended Report" },
					        { "id" : "stateS-agencyS-countyS-tractS-route", "parent" : "stateS-agencyS-countyS-tractS", "text" : "Routes Summary Report" },
					        { "id" : "stateS-agencyS-countyS-tractS-stop", "parent" : "stateS-agencyS-countyS-tractS", "text" : "Stops Summary Report" },
					        { "id" : "stateS-agencyS-countyS-tractS-urbanS", "parent" : "stateS-agencyS-countyS-tractS", "text" : "Urban Areas Summary Report" },
						       //nodes under agency->county->tract->urban summary
						       { "id" : "stateS-agencyS-countyS-tractS-urbanS-urbanX", "parent" : "stateS-agencyS-countyS-tractS-urbanS", "text" : "Urban Area Extended Report" },
						       { "id" : "stateS-agencyS-countyS-tractS-urbanS-route", "parent" : "stateS-agencyS-countyS-tractS-urbanS", "text" : "Routes Summary Report" },
						       { "id" : "stateS-agencyS-countyS-tractS-urbanS-stop", "parent" : "stateS-agencyS-countyS-tractS-urbanS", "text" : "Stops Summary Report" },
		       		{ "id" : "stateS-agencyS-congS", "parent" : "stateS-agencyS", "text" : "Congretional Districts Summary Report" },
			       		//nodes under agency->cong summary
					   	{ "id" : "stateS-agencyS-congS-countyX", "parent" : "stateS-agencyS-congS", "text" : "County Extended Report" },
					   	{ "id" : "stateS-agencyS-congS-route", "parent" : "stateS-agencyS-congS", "text" : "Routes Summary Report" },
					   	{ "id" : "stateS-agencyS-congS-stop", "parent" : "stateS-agencyS-congS", "text" : "Stops Summary Report" },
					   	{ "id" : "stateS-agencyS-congS-urbanS", "parent" : "stateS-agencyS-congS", "text" : "Urban Areas Summary Report" },
					       //nodes under agency->cong->urban summary
					       { "id" : "stateS-agencyS-congS-urbanS-urbanX", "parent" : "stateS-agencyS-congS-urbanS", "text" : "Urban Area Extended Report" },
					       { "id" : "stateS-agencyS-congS-urbanS-route", "parent" : "stateS-agencyS-congS-urbanS", "text" : "Routes Summary Report" },
					       { "id" : "stateS-agencyS-congS-urbanS-stop", "parent" : "stateS-agencyS-congS-urbanS", "text" : "Stops Summary Report" },
		       		{ "id" : "stateS-agencyS-regionS", "parent" : "stateS-agencyS", "text" : "ODOT Transit Regions Summary Report" },
			       		//nodes under agency->region summary
					   	{ "id" : "stateS-agencyS-regionS-countyX", "parent" : "stateS-agencyS-regionS", "text" : "County Extended Report" },
					   	{ "id" : "stateS-agencyS-regionS-route", "parent" : "stateS-agencyS-regionS", "text" : "Routes Summary Report" },
					   	{ "id" : "stateS-agencyS-regionS-stop", "parent" : "stateS-agencyS-regionS", "text" : "Stops Summary Report" },
					   	{ "id" : "stateS-agencyS-regionS-urbanS", "parent" : "stateS-agencyS-regionS", "text" : "Urban Areas Summary Report" },
					       //nodes under agency->region->urban summary
					       { "id" : "stateS-agencyS-regionS-urbanS-urbanX", "parent" : "stateS-agencyS-regionS-urbanS", "text" : "Urban Area Extended Report" },
					       { "id" : "stateS-agencyS-regionS-urbanS-route", "parent" : "stateS-agencyS-regionS-urbanS", "text" : "Routes Summary Report" },
					       { "id" : "stateS-agencyS-regionS-urbanS-stop", "parent" : "stateS-agencyS-regionS-urbanS", "text" : "Stops Summary Report" },
		       		{ "id" : "stateS-agencyS-placeS", "parent" : "stateS-agencyS", "text" : "Census Places Summary Report" },
			       		//nodes under agency->place summary
					   	{ "id" : "stateS-agencyS-placeS-countyX", "parent" : "stateS-agencyS-placeS", "text" : "County Extended Report" },
					   	{ "id" : "stateS-agencyS-placeS-route", "parent" : "stateS-agencyS-placeS", "text" : "Routes Summary Report" },
					   	{ "id" : "stateS-agencyS-placeS-stop", "parent" : "stateS-agencyS-placeS", "text" : "Stops Summary Report" },
					   	{ "id" : "stateS-agencyS-placeS-urbanS", "parent" : "stateS-agencyS-placeS", "text" : "Urban Areas Summary Report" },
					       //nodes under agency->place->urban summary
					       { "id" : "stateS-agencyS-placeS-urbanS-urbanX", "parent" : "stateS-agencyS-placeS-urbanS", "text" : "Urban Area Extended Report" },
					       { "id" : "stateS-agencyS-placeS-urbanS-route", "parent" : "stateS-agencyS-placeS-urbanS", "text" : "Routes Summary Report" },
					       { "id" : "stateS-agencyS-placeS-urbanS-stop", "parent" : "stateS-agencyS-placeS-urbanS", "text" : "Stops Summary Report" },
		       
		       	{ "id" : "stateS-urbanS", "parent" : "stateS", "text" : "Urban Areas Summary Report" },
			       //nodes under urban summary
			       { "id" : "stateS-urbanS-urbanX", "parent" : "stateS-urbanS", "text" : "Urban Area Extended Report" },
			       { "id" : "stateS-urbanS-route", "parent" : "stateS-urbanS", "text" : "Routes Summary Report" },
			       { "id" : "stateS-urbanS-stop", "parent" : "stateS-urbanS", "text" : "Stops Summary Report" },
			       { "id" : "stateS-urbanS-agencyS", "parent" : "stateS-urbanS", "text" : "Transit Agencies Summary Report" },
				       //nodes under urban->agency summary
				       { "id" : "stateS-urbanS-agencyS-agencyX", "parent" : "stateS-urbanS-agencyS", "text" : "Transit Agency Extended Report" },
				       { "id" : "stateS-urbanS-agencyS-route", "parent" : "stateS-urbanS-agencyS", "text" : "Routes Summary Report" },
				       { "id" : "stateS-urbanS-agencyS-stop", "parent" : "stateS-urbanS-agencyS", "text" : "Stops Summary Report" },
			       
		       	{ "id" : "stateS-countyS", "parent" : "stateS", "text" : "Counties Summary Report" },
				 	//nodes under county summary
				   	{ "id" : "stateS-countyS-countyX", "parent" : "stateS-countyS", "text" : "County Extended Report" },
				   	{ "id" : "stateS-countyS-route", "parent" : "stateS-countyS", "text" : "Routes Summary Report" },
				   	{ "id" : "stateS-countyS-stop", "parent" : "stateS-countyS", "text" : "Stops Summary Report" },
				   	{ "id" : "stateS-countyS-urbanS", "parent" : "stateS-countyS", "text" : "Urban Areas Summary Report" },
				       //nodes under county->urban summary
				       { "id" : "stateS-countyS-urbanS-urbanX", "parent" : "stateS-countyS-urbanS", "text" : "Urban Area Extended Report" },
				       { "id" : "stateS-countyS-urbanS-route", "parent" : "stateS-countyS-urbanS", "text" : "Routes Summary Report" },
				       { "id" : "stateS-countyS-urbanS-stop", "parent" : "stateS-countyS-urbanS", "text" : "Stops Summary Report" },
				       { "id" : "stateS-countyS-urbanS-agencyS", "parent" : "stateS-countyS-urbanS", "text" : "Transit Agencies Summary Report" },
					       //nodes under county->urban->agency summary
					       { "id" : "stateS-countyS-urbanS-agencyS-agencyX", "parent" : "stateS-countyS-urbanS-agencyS", "text" : "Transit Agency Extended Report" },
					       { "id" : "stateS-countyS-urbanS-agencyS-route", "parent" : "stateS-countyS-urbanS-agencyS", "text" : "Routes Summary Report" },
					       { "id" : "stateS-countyS-urbanS-agencyS-stop", "parent" : "stateS-countyS-urbanS-agencyS", "text" : "Stops Summary Report" },
				   	{ "id" : "stateS-countyS-agencyS", "parent" : "stateS-countyS", "text" : "Transit Agencies Summary Report" },
				       //nodes under county->agency summary
				       { "id" : "stateS-countyS-agencyS-agencyX", "parent" : "stateS-countyS-agencyS", "text" : "Transit Agency Extended Report" },
				       { "id" : "stateS-countyS-agencyS-route", "parent" : "stateS-countyS-agencyS", "text" : "Routes Summary Report" },
				       { "id" : "stateS-countyS-agencyS-stop", "parent" : "stateS-countyS-agencyS", "text" : "Stops Summary Report" },
				    
				   	{ "id" : "stateS-countyS-tractS", "parent" : "stateS-countyS", "text" : "Tracts Summary Report" },
				       //nodes under county->tract summary
				       { "id" : "stateS-countyS-tractS-tractX", "parent" : "stateS-countyS-tractS", "text" : "Tract Extended Report" },
				       { "id" : "stateS-countyS-tractS-route", "parent" : "stateS-countyS-tractS", "text" : "Routes Summary Report" },
				       { "id" : "stateS-countyS-tractS-stop", "parent" : "stateS-countyS-tractS", "text" : "Stops Summary Report" },
				       { "id" : "stateS-countyS-tractS-urbanS", "parent" : "stateS-countyS-tractS", "text" : "Urban Areas Summary Report" },
					       //nodes under county->tract->urban summary
					       { "id" : "stateS-countyS-tractS-urbanS-urbanX", "parent" : "stateS-countyS-tractS-urbanS", "text" : "Urban Area Extended Report" },
					       { "id" : "stateS-countyS-tractS-urbanS-route", "parent" : "stateS-countyS-tractS-urbanS", "text" : "Routes Summary Report" },
					       { "id" : "stateS-countyS-tractS-urbanS-stop", "parent" : "stateS-countyS-tractS-urbanS", "text" : "Stops Summary Report" },
					       { "id" : "stateS-countyS-tractS-urbanS-agencyS", "parent" : "stateS-countyS-tractS-urbanS", "text" : "Transit Agencies Summary Report" },
						       //nodes under county->tract->urban->agency summary
						       { "id" : "stateS-countyS-tractS-urbanS-agencyS-agencyX", "parent" : "stateS-countyS-tractS-urbanS-agencyS", "text" : "Transit Agency Extended Report" },
						       { "id" : "stateS-countyS-tractS-urbanS-agencyS-route", "parent" : "stateS-countyS-tractS-urbanS-agencyS", "text" : "Routes Summary Report" },
						       { "id" : "stateS-countyS-tractS-urbanS-agencyS-stop", "parent" : "stateS-countyS-tractS-urbanS-agencyS", "text" : "Stops Summary Report" },
				       { "id" : "stateS-countyS-tractS-agencyS", "parent" : "stateS-countyS-tractS", "text" : "Transit Agencies Summary Report" },
					       //nodes under county->tract->agency summary
					       { "id" : "stateS-countyS-tractS-agencyS-agencyX", "parent" : "stateS-countyS-tractS-agencyS", "text" : "Transit Agency Extended Report" },
					       { "id" : "stateS-countyS-tractS-agencyS-route", "parent" : "stateS-countyS-tractS-agencyS", "text" : "Routes Summary Report" },
					       { "id" : "stateS-countyS-tractS-agencyS-stop", "parent" : "stateS-countyS-tractS-agencyS", "text" : "Stops Summary Report" },
				       
				{ "id" : "stateS-congS", "parent" : "stateS", "text" : "Congressional Districts Summary Report" },
			       //nodes under cong summary
			       { "id" : "stateS-congS-congX", "parent" : "stateS-congS", "text" : "Congressional District Extended Report" },
			       { "id" : "stateS-congS-route", "parent" : "stateS-congS", "text" : "Routes Summary Report" },
			       { "id" : "stateS-congS-stop", "parent" : "stateS-congS", "text" : "Stops Summary Report" },
			       { "id" : "stateS-congS-urbanS", "parent" : "stateS-congS", "text" : "Urban Areas Summary Report" },
				       //nodes under cong->urban summary
				       { "id" : "stateS-congS-urbanS-urbanX", "parent" : "stateS-congS-urbanS", "text" : "Urban Area Extended Report" },
				       { "id" : "stateS-congS-urbanS-route", "parent" : "stateS-congS-urbanS", "text" : "Routes Summary Report" },
				       { "id" : "stateS-congS-urbanS-stop", "parent" : "stateS-congS-urbanS", "text" : "Stops Summary Report" },
				       { "id" : "stateS-congS-urbanS-agencyS", "parent" : "stateS-congS-urbanS", "text" : "Transit Agencies Summary Report" },
					       //nodes under cong->urban->agency summary
					       { "id" : "stateS-congS-urbanS-agencyS-agencyX", "parent" : "stateS-congS-urbanS-agencyS", "text" : "Transit Agency Extended Report" },
					       { "id" : "stateS-congS-urbanS-agencyS-route", "parent" : "stateS-congS-urbanS-agencyS", "text" : "Routes Summary Report" },
					       { "id" : "stateS-congS-urbanS-agencyS-stop", "parent" : "stateS-congS-urbanS-agencyS", "text" : "Stops Summary Report" },
			       { "id" : "stateS-congS-agencyS", "parent" : "stateS-congS", "text" : "Transit Agencies Summary Report" },
				       //nodes under cong->agency summary
				       { "id" : "stateS-congS-agencyS-agencyX", "parent" : "stateS-congS-agencyS", "text" : "Transit Agency Extended Report" },
				       { "id" : "stateS-congS-agencyS-route", "parent" : "stateS-congS-agencyS", "text" : "Routes Summary Report" },
				       { "id" : "stateS-congS-agencyS-stop", "parent" : "stateS-congS-agencyS", "text" : "Stops Summary Report" },
			       
				{ "id" : "stateS-regionS", "parent" : "stateS", "text" : "ODOT Transit Regions Summary Report" },
			       //nodes under region summary
			       { "id" : "stateS-regionS-regionX", "parent" : "stateS-regionS", "text" : "ODOT Transit Region Extended Report" },
			       { "id" : "stateS-regionS-route", "parent" : "stateS-regionS", "text" : "Routes Summary Report" },
			       { "id" : "stateS-regionS-stop", "parent" : "stateS-regionS", "text" : "Stops Summary Report" },
			       { "id" : "stateS-regionS-urbanS", "parent" : "stateS-regionS", "text" : "Urban Areas Summary Report" },
				       //nodes under region->urban summary
				       { "id" : "stateS-regionS-urbanS-urbanX", "parent" : "stateS-regionS-urbanS", "text" : "Urban Area Extended Report" },
				       { "id" : "stateS-regionS-urbanS-route", "parent" : "stateS-regionS-urbanS", "text" : "Routes Summary Report" },
				       { "id" : "stateS-regionS-urbanS-stop", "parent" : "stateS-regionS-urbanS", "text" : "Stops Summary Report" },
				       { "id" : "stateS-regionS-urbanS-agencyS", "parent" : "stateS-regionS-urbanS", "text" : "Transit Agencies Summary Report" },
					       //nodes under region->urban->agency summary
					       { "id" : "stateS-regionS-urbanS-agencyS-agencyX", "parent" : "stateS-regionS-urbanS-agencyS", "text" : "Transit Agency Extended Report" },
					       { "id" : "stateS-regionS-urbanS-agencyS-route", "parent" : "stateS-regionS-urbanS-agencyS", "text" : "Routes Summary Report" },
					       { "id" : "stateS-regionS-urbanS-agencyS-stop", "parent" : "stateS-regionS-urbanS-agencyS", "text" : "Stops Summary Report" },
			       { "id" : "stateS-regionS-agencyS", "parent" : "stateS-regionS", "text" : "Transit Agencies Summary Report" },
				       //nodes under region->agency summary
				       { "id" : "stateS-regionS-agencyS-agencyX", "parent" : "stateS-regionS-agencyS", "text" : "Transit Agency Extended Report" },
				       { "id" : "stateS-regionS-agencyS-route", "parent" : "stateS-regionS-agencyS", "text" : "Routes Summary Report" },
				       { "id" : "stateS-regionS-agencyS-stop", "parent" : "stateS-regionS-agencyS", "text" : "Stops Summary Report" },
			       
				{ "id" : "stateS-placeS", "parent" : "stateS", "text" : "Census Places Summary Report" },
			       //nodes under place summary
			       { "id" : "stateS-placeS-placeX", "parent" : "stateS-placeS", "text" : "Census Place Extended Report" },
			       { "id" : "stateS-placeS-route", "parent" : "stateS-placeS", "text" : "Routes Summary Report" },
			       { "id" : "stateS-placeS-stop", "parent" : "stateS-placeS", "text" : "Stops Summary Report" },
			       { "id" : "stateS-placeS-urbanS", "parent" : "stateS-placeS", "text" : "Urban Areas Summary Report" },
				       //nodes under place->urban summary
				       { "id" : "stateS-placeS-urbanS-urbanX", "parent" : "stateS-placeS-urbanS", "text" : "Urban Area Extended Report" },
				       { "id" : "stateS-placeS-urbanS-route", "parent" : "stateS-placeS-urbanS", "text" : "Routes Summary Report" },
				       { "id" : "stateS-placeS-urbanS-stop", "parent" : "stateS-placeS-urbanS", "text" : "Stops Summary Report" },
				       { "id" : "stateS-placeS-urbanS-agencyS", "parent" : "stateS-placeS-urbanS", "text" : "Transit Agencies Summary Report" },
					       //nodes under place->urban->agency summary
					       { "id" : "stateS-placeS-urbanS-agencyS-agencyX", "parent" : "stateS-placeS-urbanS-agencyS", "text" : "Transit Agency Extended Report" },
					       { "id" : "stateS-placeS-urbanS-agencyS-route", "parent" : "stateS-placeS-urbanS-agencyS", "text" : "Routes Summary Report" },
					       { "id" : "stateS-placeS-urbanS-agencyS-stop", "parent" : "stateS-placeS-urbanS-agencyS", "text" : "Stops Summary Report" },
			       { "id" : "stateS-placeS-agencyS", "parent" : "stateS-placeS", "text" : "Transit Agencies Summary Report" },
				       //nodes under place->agency summary
				       { "id" : "stateS-placeS-agencyS-agencyX", "parent" : "stateS-placeS-agencyS", "text" : "Transit Agency Extended Report" },
				       { "id" : "stateS-placeS-agencyS-route", "parent" : "stateS-placeS-agencyS", "text" : "Routes Summary Report" },
				       { "id" : "stateS-placeS-agencyS-stop", "parent" : "stateS-placeS-agencyS", "text" : "Stops Summary Report" },
			       
			       

	    ]
	}
};