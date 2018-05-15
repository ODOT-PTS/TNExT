// Copyright (C) 2015 Oregon State University - School of Mechanical,Industrial and Manufacturing Engineering 
//   This file is part of Transit Network Explorer Tool.
//
//    Transit Network Explorer Tool is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Transit Network Explorer Tool is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU  General Public License for more details.
//
//    You should have received a copy of the GNU  General Public License
//    along with Transit Network Explorer Tool.  If not, see <http://www.gnu.org/licenses/>.
// =========================================================================================================
//	  This script contains JavaScript variables and methods used to load Employment Report 
//	  in the Transit Network Explorer Tool and all its features.
// =========================================================================================================

function bindEvents() {
	tree = $('#jstree').jstree(true);
	$(document).on("dnd_stop.vakata", function(e, data) {
		$('.jstree-node').each(function() {
			if (!$('#jstree').jstree(true).is_disabled(this))
				$('#jstree').jstree(true).deselect_node(this);
		})
	});

	$("#jstree").bind("rename_node.jstree", function(e, data) {
		data.node.data.title = data.text;
	});
}

/**
 * returns the leaf nodes of a given node.
 */
function getLeaves(x) {
	var y = [ x ];
	var hasLeaf = true;
	var nodesToAdd = new Array();
	var nodesToRemove = new Array();
	tree = $('#jstree').jstree(true);
	while (hasLeaf) {
		hasLeaf = false;
		nodesToAdd = [];
		nodesToRemove = [];
		for (var i = 0; i < y.length; i++) {
			var node = y[i];
			if (tree.is_parent(node)) {
				hasLeaf = true;
				nodesToRemove.push(node);
				nodesToAdd.push.apply(nodesToAdd, tree.get_children_dom(node));
			}
		}
		// removing nodesToRemove from y
		for (var k = 0; k < nodesToRemove.length; k++) {
			var index = y.indexOf(nodesToRemove[k]);
			if (index > -1)
				y.splice(index, 1);
		}
		y.push.apply(y, nodesToAdd);
	}
	return y;
}

/**
 * returns employment data for both RAC and WAC datasets
 */
function racWacAjax() {
	$
			.ajax({
				type : 'GET',
				datatype : 'json',
				url : '/TNAtoolAPI-Webapp/queries/transit/emp?&dataSet=lodes_blocks_rac&projection='
						+ $('#projectionYear').val()
						+ '&report='
						+ $("#reportType").val()
						+ '&day='
						+ w_qstringd
						+ '&radius='
						+ $('#Sradius').val()
						* 1609.34
						+ '&L='
						+ $('#LOS').val()
						+ '&dbindex='
						+ dbindex,
				async : true,
				success : function(d) {
					docMetadata = d.metadata;
					var temp = d.EmpDataList;
					racData.EmpDataList2 = [];
					temp.forEach(function(i, ind, arr) {
						racData.EmpDataList2.push(i);
					});
					$
							.ajax({
								type : 'GET',
								datatype : 'json',
								url : '/TNAtoolAPI-Webapp/queries/transit/emp?&dataSet=lodes_blocks_wac&projection='
										+ $('#projectionYear').val()
										+ '&report='
										+ $("#reportType").val()
										+ '&day='
										+ w_qstringd
										+ '&radius='
										+ $('#Sradius').val()
										* 1609.34
										+ '&L='
										+ $('#LOS').val()
										+ '&dbindex='
										+ dbindex,
								async : true,
								success : function(d) {
									docMetadata = d.metadata;
									var temp = d.EmpDataList;
									wacData.EmpDataList2 = [];
									temp.forEach(function(i, ind, arr) {
										wacData.EmpDataList2.push(i);
									});
									openReport2();
								}
							});
				}
			});
}

/**
 * returns employment data for the RAC dataset
 */
function racAjax() {
	$
			.ajax({
				type : 'GET',
				datatype : 'json',
				url : '/TNAtoolAPI-Webapp/queries/transit/emp?&dataSet=lodes_blocks_rac&projection='
						+ $('#projectionYear').val()
						+ '&report='
						+ $("#reportType").val()
						+ '&day='
						+ w_qstringd
						+ '&radius='
						+ $('#Sradius').val()
						* 1609.34
						+ '&L='
						+ $('#LOS').val()
						+ '&dbindex='
						+ dbindex,
				async : true,
				success : function(d) {
					docMetadata = d.metadata;
					var temp = d.EmpDataList;
					racData.EmpDataList2 = [];
					temp.forEach(function(i, ind, arr) {
						racData.EmpDataList2.push(i);
					});
					openReport2();
				}
			});
}

/**
 * returns employment data for the RAC dataset
 */
function wacAjax() {
	$
			.ajax({
				type : 'GET',
				datatype : 'json',
				url : '/TNAtoolAPI-Webapp/queries/transit/emp?&dataSet=lodes_blocks_wac&projection='
						+ $('#projectionYear').val()
						+ '&report='
						+ $("#reportType").val()
						+ '&day='
						+ w_qstringd
						+ '&radius='
						+ $('#Sradius').val()
						* 1609.34
						+ '&L='
						+ $('#LOS').val()
						+ '&dbindex='
						+ dbindex,
				async : true,
				success : function(d) {
					docMetadata = d.metadata;				
					var temp = d.EmpDataList;
					wacData.EmpDataList2 = [];
					temp.forEach(function(i, ind, arr) {
						wacData.EmpDataList2.push(i);
					});
					openReport2();
				}
			});
}


function toggleCheckbox(checkbox) {
	if (checkbox.checked) {
		tree.select_all();
	} else {
		tree.deselect_all();
	}
}

/**
 * returns the html for table headers based on user selection.
 * @returns {String}
 */
function getTableHeaders() {
	var nodesList = [];
	var tree = $('#jstree').jstree(true);
	$('.jstree-node')
			.each(
					function() {
						if ((tree.is_selected($(this))
								&& tree.get_parent($(this)).indexOf(
										'_aggregate') < 0
								&& tree.get_parent($(this)) != '#' && tree
								.get_parent($(this)).indexOf('Category') == -1)
								|| ($(this).attr('id').indexOf('_aggregate') > -1 && tree
										.get_children_dom($(this)) != null)
								&& tree.get_parent($(this)).indexOf('Category') == -1) {
							nodesList.push($(this));
						}
					});
	var y = "";
	if (racBool && wacBool) {
		if ($('#reportType').val() == 'Agencies') {
			y += '<th class="metric" title="Total number of employed people residing in the geographic area and served by the agency on selected date(s).">Employment Served (RAC)<span class="IOSym">(1)</span></th>'
					+ '<th class="metric" title="Total number of employed people working in the geographic area and served by the agency on selected date(s).">Employees served (WAC)<span class="IOSym">(1)</span></th>';
		} else {
			y += '<th class="metric" title="Total number of employed people residing in the geographic area.">Employment (RAC)</th>'
					+ '<th class="metric" title="Total number of employed people working in the geographic area.">Employees (WAC)</th>';
		}

		nodesList.forEach(function(item, index, array) {
					var node = tree.get_node(item.attr('id'));
					if ($('#reportType').val() != 'Agencies')
						y += '<th class="metric" title="Total number of employed people, belonging to the category, residing in the geographic area.">'
								+ node.data.title + ' (RAC)</th>';
					y += '<th class="metric" title="[Category] Served: Total number of unduplicated employed people, belonging to the category, residing in '
						+ 'census blocks with their centroids within X-mile radius (i.e., stop distance) of any stop of the agency/geographic area. '
						+ 'Each block is counted once (unduplicated). This metric is date-independent, i.e., the stops may or may not be served on the selected date(s).">'
						+ node.data.title + ' - S (RAC)<span class="IOSym">(1)</span></th>'
						+ '<th class="metric" title="[Category] Served at Level of Service: Total unduplicated employed people,'
						+ 'belonging to the category, residing in census blocks with their centroids located within X-miles radius of any stop of the agency/geographic area and '
						+ 'served at least N-times on the selected date(s). X is the employment search radius and N is the minimum level of service set by the user.">'
						+ node.data.title + ' - SLOS (RAC)<span class="IOSym">(1)(2)(3)</span></th>'
						+ '<th class="metric" title="[Category] Served by Service: Summation of [Category] Served by Service over all census blocks that have their centroid within '
						+ 'X-mile radius (i.e., stop distance) of any stop of the agency/geographic area. [Category] Served by Service for a block is calculated as the number of '
						+ 'employed people, belonging to the category, residing in that block multiplied by the times that block is served on the selected date(s). '
						+ 'Reported number is cumulative over the selected dates.">'
						+ node.data.title + ' - SS (RAC)<span class="IOSym">(1)(3)</span></th>';
					if ($('#reportType').val() != 'Agencies')
						y += '<th class="metric" title="Total number of employed people, belonging to the category, working in the geographic area.">'
						+ node.data.title + ' (WAC)</th>';
					y += '<th class="metric" title="[Category] Served: Total number of unduplicated employed people, belonging to the category, working in '
						+ 'census blocks with their centroids within X-mile radius (i.e., stop distance) of any stop of the agency/geographic area. '
						+ 'Each block is counted once (unduplicated). This metric is date-independent, i.e., the stops may or may not be served on the selected date(s).">'
						+ node.data.title + ' - S (WAC)<span class="IOSym">(1)</span></th>'
						+ '<th class="metric" title="[Category] Served at Level of Service: Total unduplicated employed people,'
						+ 'belonging to the category, working in census blocks with their centroids located within X-miles radius of any stop of the agency/geographic area and '
						+ 'served at least N-times on the selected date(s). X is the employment search radius and N is the minimum level of service set by the user.">'
						+ node.data.title + ' - SLOS (WAC)<span class="IOSym">(1)(2)(3)</span></th>'
						+ '<th class="metric" title="[Category] Served by Service: Summation of [Category] Served by Service over all census blocks that have their centroid within '
						+ 'X-mile radius (i.e., stop distance) of any stop of the agency/geographic area. [Category] Served by Service for a block is calculated as the number of '
						+ 'employed people, belonging to the category, working in that block multiplied by the times that block is served on the selected date(s). '
						+ 'Reported number is cumulative over the selected dates.">'
						+ node.data.title + ' - SS (WAC)<span class="IOSym">(1)(3)</span></th>';
				});
	} else if (racBool) {
		if ($('#reportType').val() == 'Agencies') {
			y += '<th class="metric" title="Total number of employed people residing in the geographic area and served by the agency on selected date(s).">Employment Served (RAC)<span class="IOSym">(1)</span></th>'
		} else {
			y += '<th class="metric" title="Total number of employed people residing in the geographic area.">Employment (RAC)</th>'
		}

		nodesList.forEach(function(item, index, array) {
			var node = tree.get_node(item.attr('id'));
			if ($('#reportType').val() != 'Agencies')
				y += '<th class="metric" title="Total number of employed people, belonging to the category, residing in the geographic area.">'
						+ node.data.title + ' (RAC)</th>';
			y += '<th class="metric" title="[Category] Served: Total number of unduplicated employed people, belonging to the category, residing in '
				+ 'census blocks with their centroids within X-mile radius (i.e., stop distance) of any stop of the agency/geographic area. '
				+ 'Each block is counted once (unduplicated). This metric is date-independent, i.e., the stops may or may not be served on the selected date(s).">'
				+ node.data.title + ' - S (RAC)<span class="IOSym">(1)</span></th>'
				+ '<th class="metric" title="[Category] Served at Level of Service: Total unduplicated employed people,'
				+ 'belonging to the category, residing in census blocks with their centroids located within X-miles radius of any stop of the agency/geographic area and '
				+ 'served at least N-times on the selected date(s). X is the employment search radius and N is the minimum level of service set by the user.">'
				+ node.data.title + ' - SLOS (RAC)<span class="IOSym">(1)(2)(3)</span></th>'
				+ '<th class="metric" title="[Category] Served by Service: Summation of [Category] Served by Service over all census blocks that have their centroid within '
				+ 'X-mile radius (i.e., stop distance) of any stop of the agency/geographic area. [Category] Served by Service for a block is calculated as the number of '
				+ 'employed people, belonging to the category, residing in that block multiplied by the times that block is served on the selected date(s). '
				+ 'Reported number is cumulative over the selected dates.">'
				+ node.data.title + ' - SS (RAC)<span class="IOSym">(1)(3)</span></th>';
			});
	} else {
		if ($('#reportType').val() == 'Agencies') {
			y += '<th class="metric" title="Total number of employed people working in the geographic area and served by the agency on selected date(s).">Employees served (WAC)<span class="IOSym">(1)</span></th>';
		} else {
			y += '<th class="metric" title="Total number of employed people working in the geographic area.">Employees (WAC)</th>';
		}
		nodesList.forEach(function(item, index, array) {
				var node = tree.get_node(item.attr('id'));
				if ($('#reportType').val() != 'Agencies')
					y += '<th class="metric" title="Total number of employed people, belonging to the category, working in the geographic area.">'
					+ node.data.title + ' (WAC)</th>';
				y += '<th class="metric" title="[Category] Served: Total number of unduplicated employed people, belonging to the category, working in '
					+ 'census blocks with their centroids within X-mile radius (i.e., stop distance) of any stop of the agency/geographic area. '
					+ 'Each block is counted once (unduplicated). This metric is date-independent, i.e., the stops may or may not be served on the selected date(s).">'
					+ node.data.title + ' - S (WAC)<span class="IOSym">(1)</span></th>'
					+ '<th class="metric" title="[Category] Served at Level of Service: Total unduplicated employed people,'
					+ 'belonging to the category, working in census blocks with their centroids located within X-miles radius of any stop of the agency/geographic area and '
					+ 'served at least N-times on the selected date(s). X is the employment search radius and N is the minimum level of service set by the user.">'
					+ node.data.title + ' - SLOS (WAC)<span class="IOSym">(1)(2)(3)</span></th>'
					+ '<th class="metric" title="[Category] Served by Service: Summation of [Category] Served by Service over all census blocks that have their centroid within '
					+ 'X-mile radius (i.e., stop distance) of any stop of the agency/geographic area. [Category] Served by Service for a block is calculated as the number of '
					+ 'employed people, belonging to the category, working in that block multiplied by the times that block is served on the seleceted date(s). '
					+ 'Reported number is cumulative over the selected dates.">'
					+ node.data.title + ' - SS (WAC)<span class="IOSym">(1)(3)</span></th>';
				});
	}
	return y;
}

/**
 * Clears html content of the tabular section of the page
 */
function clearReport() {
	document.getElementById('displayReport').innerHTML = "";
	$('#initialText').show();
}

/**
 * Updates flags based on dataset selection 
 * @param e
 */
function datasetChange(e) {
	if (e.value == 'lodes_blocks_wac') {
		racBool = false;
		wacBool = true;
	} else if (e.value == 'lodes_blocks_rac') {
		racBool = true;
		wacBool = false;
	} else {
		racBool = true;
		wacBool = true
	}
}

/**
 * updates reportType value based on the selected report.
 */
function selectFunction() {
	$('#reportTitle').html($('#reportType').val());
}

function updateProjection() {
	$('#jstree').jstree(true).destroy();
	if ($('#projectionYear').val() == 'current') {
		$('#jstree').jstree(default_jstree);
		$('#dataSet').removeAttr('disabled');
		$("#checkbox").removeAttr("disabled");
	} else {
		$('#jstree').jstree(new_jstree);
		bindEvents();
		$('#dataSet').attr('disabled', 'disabled');
		;
		$('#dataSet').val('lodes_blocks_rac');
		$('#promptText')
				.html('<span style="line-height: 150%;">The future employment figures provided by this report were estimated using a linear projection '
					+ 'that used county-level employment for 2010 and employment estimates for the years 2015 and 2025 as a basis.</span>'
					+ '<br><br><input type="checkbox" id="showMSG">Do not show this message again');
		$("#checkbox").attr("disabled", true);
		$('#checkbox').attr('checked', false);
		racBool = true;
		wacBool = false;
	}
}

function sectorPercentage(x) {
	if (x.indexOf('.') > -1) {
		return '0' + x.slice(-3);
	} else
		return 1;
}

function getTrimmedNodeID(x) {
	var y = ($(x).attr('id')).toLowerCase()
	if (y.indexOf('_.') > -1)
		return y.substring(0, y.length - 4);
	else
		return y;
}

/**
 * method called to generate the report.
 * @returns {Number}
 */
function openReport() {
	clearReport();
	if (exceedsMaxRadius($('#Sradius').val())) { // Checks if the entered
													// search radius exceeds the
													// maximum.
		alert('Enter a number less than ' + maxRadius
				+ ' miles for the population search radius.');
		return 0;
	}

	if ($('#jstree').jstree(true).get_selected() == "") {
		alert('At least select 1 category');
		return 0;
	}

	// prompt for showing meta-data
	if (showPrompt && $('#projectionYear').val() != 'current')
		$('#dialog-confirm').dialog('open');
	$('.ui-dialog').css("opacity", "1");

	// update dates
	var dates = $('#datepicker').multiDatesPicker('getDates');
	if (dates.length == 0) {
		$("#datepicker").multiDatesPicker({
			addDates : [ new Date() ]
		});
	}
	w_qstringd = dates.join(",");

	// close slidebar
	mySlidebar.slidebars.close();
	$("#initialText").hide();
	$('#bePatient').show();
	$('#dialogPreLoader').show();

	document.getElementById('displayReport').innerHTML = "";
	html = '<table id="RT" class="display" align="center">';
	if ($('#reportType').val() == 'Agencies') {
		tmp = '<tr><th id="tableid">Agency ID</em></th>'
				+ '<th id="tablename">Agency Name</em></th>';
	} else {
		tmp = '<tr><th id="tableid">Area ID</em></th>'
				+ '<th id="tablename">Area Name</em></th>';
	}
	tmp += getTableHeaders() + '</tr>';
	html += '<thead>' + tmp + '</thead><tbody>';
	if (racBool && wacBool)
		racWacAjax();
	else if (racBool)
		racAjax();
	else
		wacAjax();
	$('#bePatient').hide();
}

function openReport2() {
	// making a 2D array based on the selected categories and aggregated
	// categories
	var tableContent = [];
	var rootList = [];
	var tree = $('#jstree').jstree(true);
	$('.jstree-node')
			.each(
					function() {
						if ((tree.is_selected($(this))
								&& tree.get_parent($(this)).indexOf(
										'_aggregate') < 0
								&& tree.get_parent($(this)) != '#' && tree
								.get_parent($(this)).indexOf('Category') == -1)
								|| ($(this).attr('id').indexOf('_aggregate') > -1
										&& tree.get_children_dom($(this)) != null && tree
										.get_parent($(this))
										.indexOf('Category') == -1)) {
							rootList.push(this);
						}
					});

	tree.open_all();
	rootList.forEach(function(item, index, array) {
		var parentID = $(item).attr('id');
		if (tree.is_parent(item)) {
			var children = getLeaves(item);
			var childrenIDs = [];
			$.each(children, function(ind, obj) {
				if (tree.is_selected(obj)) {
					childrenIDs.push($(obj).attr('id')); // You should push
															// the leaf nodes
															// not the Category
															// nodes.
				}
			});
			tableContent.push([ parentID, childrenIDs ]);
		} else {
			var childrenIDs = [ parentID ];
			tableContent.push([ parentID, childrenIDs ]);
		}
	});
	// Filling in the first 3 columns of the datatable and making a hashmap of
	// the query results
	if (racBool && wacBool) {
		for (var i = 0; i < racData.EmpDataList2.length; i++) {
			html += '<tr><td>' + racData.EmpDataList2[i].id + '</a></td>'
					+ '<td>' + racData.EmpDataList2[i].name + '</td>';
			if ($('#reportType').val() == "Agencies") {
				html += '<td>'
						+ numberconv(racData.EmpDataList2[i].c000served.toFixed(0))
						+ '</td>' + '<td>'
						+ numberconv(wacData.EmpDataList2[i].c000served.toFixed(0))
						+ '</td>';
			} else {
				html += '<td>' + numberconv(racData.EmpDataList2[i].c000.toFixed(0))
						+ '</td>' + '<td>'
						+ numberconv(wacData.EmpDataList2[i].c000.toFixed(0)) + '</td>';
			}

			var resultSetRac = {};
			var resultSetWac = {};
			$('.jstree-leaf')
					.each(
							function() {
								if ($('#reportType').val() != 'Agencies')
									resultSetRac[$(this).attr('id')] = racData.EmpDataList2[i][($(this)
											.attr('id')).toLowerCase()];
								resultSetRac[$(this).attr('id') + 'los'] = racData.EmpDataList2[i][($(this)
										.attr('id')).toLowerCase()
										+ 'los'];
								resultSetRac[$(this).attr('id') + 'within'] = racData.EmpDataList2[i][($(this)
										.attr('id')).toLowerCase()
										+ 'within'];
								resultSetRac[$(this).attr('id') + 'served'] = racData.EmpDataList2[i][($(this)
										.attr('id')).toLowerCase()
										+ 'served'];
							});
			$('.jstree-leaf')
					.each(
							function() {
								if ($('#reportType').val() != 'Agencies')
									resultSetWac[$(this).attr('id')] = wacData.EmpDataList2[i][($(this)
											.attr('id')).toLowerCase()];
								resultSetWac[$(this).attr('id') + 'los'] = wacData.EmpDataList2[i][($(this)
										.attr('id')).toLowerCase()
										+ 'los'];
								resultSetWac[$(this).attr('id') + 'within'] = wacData.EmpDataList2[i][($(this)
										.attr('id')).toLowerCase()
										+ 'within'];
								resultSetWac[$(this).attr('id') + 'served'] = wacData.EmpDataList2[i][($(this)
										.attr('id')).toLowerCase()
										+ 'served'];
							});

			// filling in the other columns of the datatable
			for ( var index in tableContent) {
				var racSummation = 0;
				var racPopServedSummation = 0;
				var racPopAtLOSSummation = 0;
				var racServedByServiceSummation = 0;
				var wacSummation = 0;
				var wacPopServedSummation = 0;
				var wacPopAtLOSSummation = 0;
				var wacServedByServiceSummation = 0;
				tableContent[index][1].forEach(function(j, ind, arr) {

					var node = $('#jstree').find("[id='" + j + "']");
					if (tree.is_selected(node)) {
						if ($('#reportType').val() != 'Agencies') {
							racSummation += resultSetRac[j];
							wacSummation += resultSetWac[j];
						}
						racPopServedSummation += resultSetRac[j + 'within'];
						racPopAtLOSSummation += resultSetRac[j + 'los'];
						racServedByServiceSummation += resultSetRac[j
								+ 'served'];

						wacPopServedSummation += resultSetWac[j + 'within'];
						wacPopAtLOSSummation += resultSetWac[j + 'los'];
						wacServedByServiceSummation += resultSetWac[j
								+ 'served'];
					}
				});
				if ($('#reportType').val() != 'Agencies')
					html += '<td>' + numberconv(racSummation.toFixed(0)) + '</td>';
				html += '<td>' + numberconv(racPopServedSummation.toFixed(0)) + '</td>'
						+ '<td>' + numberconv(racPopAtLOSSummation.toFixed(0))
						+ '</td>' + '<td>'
						+ numberconv(racServedByServiceSummation.toFixed(0)) + '</td>';
				if ($('#reportType').val() != 'Agencies')
					html += '<td>' + numberconv(wacSummation.toFixed(0)) + '</td>';
				html += '<td>' + numberconv(wacPopServedSummation.toFixed(0)) + '</td>'
						+ '<td>' + numberconv(wacPopAtLOSSummation.toFixed(0))
						+ '</td>' + '<td>'
						+ numberconv(wacServedByServiceSummation.toFixed(0)) + '</td>';
			}
		};
	} else if (racBool) {
		var resultSetRac = {};

		$
				.each(
						racData.EmpDataList2,
						function(i, item) {
							$('.jstree-leaf')
									.each(
											function() {
												var sectorPercent = sectorPercentage($(
														this).attr('id'));
												if ($('#reportType').val() != 'Agencies')
													resultSetRac[$(this).attr(
															'id')] = Math
															.round(sectorPercent
																	* racData.EmpDataList2[i][getTrimmedNodeID(this)]);
												resultSetRac[$(this).attr('id')
														+ 'los'] = Math
														.round(sectorPercent
																* racData.EmpDataList2[i][getTrimmedNodeID(this)
																		+ 'los']);
												resultSetRac[$(this).attr('id')
														+ 'within'] = Math
														.round(sectorPercent
																* racData.EmpDataList2[i][getTrimmedNodeID(this)
																		+ 'within']);
												resultSetRac[$(this).attr('id')
														+ 'served'] = Math
														.round(sectorPercent
																* racData.EmpDataList2[i][getTrimmedNodeID(this)
																		+ 'served']);
											});

							html += '<tr><td>' + item.id + '</a></td>' + '<td>'
									+ item.name + '</td>';
							if ($('#reportType').val() == 'Agencies')
								html += '<td>' + numberconv(item.c000served.toFixed(0))
										+ '</td>';
							else
								html += '<td>' + numberconv(item.c000.toFixed(0))
										+ '</td>';

							// filling in the other columns of the datatable
							for ( var index in tableContent) {
								var racSummation = 0;
								var racPopServedSummation = 0;
								var racPopAtLOSSummation = 0;
								var racServedByServiceSummation = 0;
								tableContent[index][1]
										.forEach(function(j, ind, arr) {
											var node = $('#jstree').find(
													"[id='" + j + "']");
											if (tree.is_selected(node)) {
												if ($('#reportType').val() != 'Agencies')
													racSummation += resultSetRac[j];
												racPopServedSummation += resultSetRac[j
														+ 'within'];
												racPopAtLOSSummation += resultSetRac[j
														+ 'los'];
												racServedByServiceSummation += resultSetRac[j
														+ 'served'];
											}
										});
								if ($('#reportType').val() != 'Agencies')
									html += '<td>'
											+ numberconv(racSummation.toFixed(0))
											+ '</td>';
								html += '<td>'
										+ numberconv(racPopServedSummation.toFixed(0))
										+ '</td>'
										+ '<td>'
										+ numberconv(racPopAtLOSSummation.toFixed(0))
										+ '</td>'
										+ '<td>'
										+ numberconv(racServedByServiceSummation.toFixed(0))
										+ '</td>';
							}
						});
	} else {
		var resultSetWac = {};
		$
				.each(
						wacData.EmpDataList2,
						function(i, item) {
							$('.jstree-leaf')
									.each(
											function() {
												if ($('#reportType').val() != 'Agencies')
													resultSetWac[$(this).attr(
															'id')] = wacData.EmpDataList2[i][($(this)
															.attr('id'))
															.toLowerCase()];
												resultSetWac[$(this).attr('id')
														+ 'los'] = wacData.EmpDataList2[i][($(this)
														.attr('id'))
														.toLowerCase()
														+ 'los'];
												resultSetWac[$(this).attr('id')
														+ 'within'] = wacData.EmpDataList2[i][($(this)
														.attr('id'))
														.toLowerCase()
														+ 'within'];
												resultSetWac[$(this).attr('id')
														+ 'served'] = wacData.EmpDataList2[i][($(this)
														.attr('id'))
														.toLowerCase()
														+ 'served'];
											});
							html += '<tr><td>' + item.id + '</td>' + '<td>'
									+ item.name + '</td>';
							if ($('#reportType').val() == 'Agencies')
								html += '<td>' + numberconv(item.c000served.toFixed(0))
										+ '</td>';
							else
								html += '<td>' + numberconv(item.c000.toFixed(0))
										+ '</td>';

							// filling in the other columns of the datatable
							for ( var index in tableContent) {
								var wacSummation = 0;
								var wacPopServedSummation = 0;
								var wacPopAtLOSSummation = 0;
								var wacServedByServiceSummation = 0;
								tableContent[index][1]
										.forEach(function(j, ind, arr) {
											var node = $('#jstree').find(
													"[id='" + j + "']");
											if (tree.is_selected(node)) {
												if ($('#reportType').val() != 'Agencies')
													wacSummation += resultSetWac[j];
												wacPopServedSummation += resultSetWac[j
														+ 'within'];
												wacPopAtLOSSummation += resultSetWac[j
														+ 'los'];
												wacServedByServiceSummation += resultSetWac[j
														+ 'served'];
											}
										});
								if ($('#reportType').val() != 'Agencies')
									html += '<td>'
											+ numberconv(wacSummation.toFixed(0))
											+ '</td>';
								html += '<td>'
										+ numberconv(wacPopServedSummation.toFixed(0))
										+ '</td>'
										+ '<td>'
										+ numberconv(wacPopAtLOSSummation.toFixed(0))
										+ '</td>'
										+ '<td>'
										+ numberconv(wacServedByServiceSummation.toFixed(0))
										+ '</td>';

							}
						});
	}

	$('#displayReport').append($(html));
	$('#dialogPreLoader').hide();
	table = buildDatatables();
}