// Copyright (C) 2015 Oregon State University - School of Mechanical,Industrial and Manufacturing Engineering 
//   This file is part of Transit Network Analysis Software Tool.
//
//    Transit Network Analysis Software Tool is free software: you can redistribute it and/or modify
//    it under the terms of the GNU  General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Transit Network Analysis Software Tool is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU  General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Transit Network Analysis Software Tool.  If not, see <http://www.gnu.org/licenses/>.
// =========================================================================================================
//	  This script contains JavaScript variables and methods used to generate Timing Connections Report
//	  in the Transit Network Analysis Software Tool.
// =========================================================================================================

var selectedDB = 1;
var selectedAgency = '';
var selectedRoute = '';
var trips = {};
var map;

function TimingConStart() {
	window.open('/TNAtoolAPI-Webapp/TimingConnection.html?&dbindex=' + dbindex);
}

/**
 * populates the list of agencies based on selected database
 * 
 * @param {Integer}
 *            database index
 */
function getAgencies(input) {
	selectedDB = input;
	$("#submit").prop('disabled', true);
	// Getting the list of agencies from the selected DB.
	var sortedAgencyList = [];
	$.ajax({
		type : 'GET',
		datatype : 'json',
		url : '/TNAtoolAPI-Webapp/queries/transit/allAgencies?&dbindex='
				+ selectedDB + '&username=' + getSession(),
		async : false,
		success : function(d) {
			$.each(d, function(index, item) {
				sortedAgencyList.push({
					id : item.id,
					name : item.name
				});
			});
			sortedAgencyList.sort(function(a, b) {
				return (a.name > b.name) ? 1 : ((b.name > a.name) ? -1 : 0);
			});

			$.each(sortedAgencyList, function(i, item) {
				$('#agencySelect').append(
						$('<option></option>').attr("value", item.id).text(
								item.name));
			});
		}
	});
}

/**
 * populates the list of routes based on selected agency
 * 
 * @param {String}
 *            agency ID
 */
function getRoutes(input) {
	selectedAgency = input.value;
	$('#routeSelect,#tripSelect').find('option').remove().end();
	$('#routeSelect').append(
			$('<option value="default">&lt; select &gt;</option>'));
	$('#tripSelect').append(
			$('<option value="default">&lt; select &gt;</option>'));

	$.ajax({
		type : 'GET',
		datatype : 'json',
		url : '/TNAtoolAPI-Webapp/queries/transit/agencyRoutes?agency='
				+ selectedAgency + '&dbindex=' + selectedDB,
		async : false,
		success : function(d) {
			var html = '<form>';
			$.each(d.RouteR, function(index, item) {
				$('#routeSelect').append(
						$('<option></option>').attr("value", item.RouteId)
								.text(item.RouteLName));
			});
		}
	});
	$("#submit").prop('disabled', true);
}

/**
 * populates the list of active trips based on selected route and day
 * 
 * @param {String}
 *            Route ID
 */
function getTripsOfDay(input) {
	selectedRoute = input.value;
	$('#tripSelect').find('option').remove().end();
	$('#tripSelect').append($('<option>&lt; select &gt;</option>'));

	$
			.ajax({
				type : 'GET',
				datatype : 'json',
				url : '/TNAtoolAPI-Webapp/queries/transit/tripsOfTheDayByRoute?agency='
						+ selectedAgency
						+ '&date='
						+ $('#date').val()
						+ '&dbindex='
						+ $('#dbselect').val()
						+ '&routeId='
						+ $('#routeSelect').val(),
				async : false,
				success : function(d) {
					if (d.length == 0) {
						alert($('#routeSelect option:selected').text()
								+ ' has no runs on the given date in the selected GTFS Source.');
						$('#routeSelect').val('default');
					} else {
						$
								.each(
										d,
										function(index, item) {
											$('#tripSelect')
													.append(
															$(
																	'<option></option>')
																	.attr(
																			"value",
																			item.tripId)
																	.text(
																			doubleDigit(item.start_hr)
																					+ ':'
																					+ doubleDigit(item.start_min)
																					+ ':'
																					+ doubleDigit(item.start_sec)
																					+ ' - '
																					+ doubleDigit(item.end_hr)
																					+ ':'
																					+ doubleDigit(item.end_min)
																					+ ':'
																					+ doubleDigit(item.end_sec)));
										});
					}
				}
			});
	$("#submit").prop('disabled', true);
}

/**
 * generates the tabular report for timing connections
 */
function getTimingConReport() {
	$('#displayReport').empty();
	$('#progressbar').show();
	trips = {};
	html = '<table id="RT" class="display" align="center">';
	tmp = '<tr><th class="metric" title="">#</th>'
			+ '<th class="metric" title="Stop belonging to the selected trip." >From - Stop ID</th>'
			+ '<th class="metric" title="Stop belonging to the selected trip.">From - Stop Name</th>'
			+ '<th class="metric" title="Stop belonging to the connected trip.">To - Stop ID</th>'
			+ '<th class="metric" title="Stop belonging to the connected trip.">To - Stop Name</th>'
			+ '<th class="metric" title="The agency that is connected to the selected agency by having a stop and route accessable within the specified radius and time window.">To - Agency</th>'
			+ '<th class="metric" title="The route that is connected to the selected route by having a stop and trip accessable within the specified radius and time window.">To - Route ID</th>'
			+ '<th class="metric" title="The route that is connected to the selected route by having a stop and trip accessable within the specified radius and time window.">To - Route Name</th>'
			+ '<th class="metric" title="Time of arrival at stop 1.">Arrival at Stop 1</th>'
			+ '<th class="metric" title="Time of departure from stop 2.">Departure from Stop 2</th>'
			+ '<th class="metric" title="The difference between arriving at stop1 and departing from stop2.">Time Difference </th></tr>';
	html += '<thead>' + tmp + '</thead><tbody>';

	$.ajax({
		type : 'GET',
		datatype : 'json',
		url : '/TNAtoolAPI-Webapp/queries/transit/timingCon?&radius='
				+ $('#Sradius').val() * 1609.3 + '&agencyId=' + selectedAgency
				+ '&date=' + $('#date').val() + '&dbindex='
				+ $('#dbselect').val() + '&timeWin=' + $('#TW').val()
				+ '&tripId=' + $('#tripSelect').val(),
		async : true,
		success : function(d) {
			trips = d;
			$.each(d, function(index, item) {
				html += '<tr><td>' + (index + 1) + '</td>' 
				+ '<td>' + item.stopId1 + '</td>' 
				+ '<td>' + item.stopName1 + '</td>' 
				+ '<td>' + item.stopId2 + '</td>' 
				+ '<td>' + item.stopName2 + '</td>' 
				+ '<td>' + item.agencyName + '</td>' 
				+ '<td>' + item.routeId + '</td>' 
				+ '<td>' + item.routeName + '</td>' 
				+ '<td>' + secToHour(item.arrival1) + '</td>' 
				+ '<td>' + secToHour(item.departure2) + '</td>';
				if (item.timeDiff > 0)
					html += '<td style="color:green">'
							+ secToHour(item.timeDiff) + '</td>';
				else
					html += '<td style="color:red">-'
							+ secToHour(Math.abs(item.timeDiff)) + '</td>';
			});
			$('#displayReport').append($(html));

			table = buildDatatables();
			$('#progressbar').hide();
			document.getElementById('iframe').contentDocument.location
					.reload(true);
		},
		error : function(e) {
			console.log('Error: ' + e);
		}
	});
}

/**
 * generates the on-map timing connection report
 */
function openMap() {
	$(function() {
		$("#mapDialog")
				.dialog(
						{
							width : '80%',
							height : $(window).height() * 0.98,
							modal : true,
							resizable : true,
							close : function() {
							},
							open : function() {
								$('#iframe')
										.css(
												"width",
												$(
														'body > div.ui-dialog.ui-widget.ui-widget-content.ui-corner-all.ui-front.ui-draggable')
														.width() * .98);
								$('#iframe').css("height",
										$('#mapDialog').height() * .98);
							},
							resize : function() {
								$('#iframe')
										.css(
												"width",
												$(
														'body > div.ui-dialog.ui-widget.ui-widget-content.ui-corner-all.ui-front.ui-draggable')
														.width() * .98);
								$('#iframe').css("height",
										$('#mapDialog').height() * .98);
								$('#mapLegend').css("position", {
									my : "left+10 bottom",
									at : "left bottom",
									of : "#map"
								});
							}
						});
	});
}
