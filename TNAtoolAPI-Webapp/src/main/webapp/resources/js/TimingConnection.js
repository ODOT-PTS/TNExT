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
	$("#mapDialog").dialog({
		width : $(window).width() * 0.90,
		height : $(window).height() * 0.90,
		modal : true,
		resizable : true,
		close : function() {
		},
		open : function() {
			// weird but works
			$('#map').css("width",
				$('body > div.ui-dialog.ui-widget.ui-widget-content.ui-corner-all.ui-front.ui-draggable').width() * .98
			);
			$('#map').css("height", 
				$('#mapDialog').height() * .98
			);
			drawMap();
		}
	});
}

///////////
/// MAP ///
///////////

var map;
var missedCons = L.featureGroup(); // Leaflet object
var missedConsPolylines = []; // Array of Leaflet Polylines
var accessibleCons = L.featureGroup(); // Leaflet object
var accessibleConsPolylines = []; // Array of Leaflet Polylines
var originalTrip; // L.Polyline

function initTimingConnectionMap() {
	map = new L.Map('map', {
		minZoom : 4,
		maxZoom : 18,
		zoomControl: false
	});
	var OSMURL = "http://{s}.mqcdn.com/tiles/1.0.0/osm/{z}/{x}/{y}.png";
	var aerialURL = "http://{s}.mqcdn.com/tiles/1.0.0/sat/{z}/{x}/{y}.png";
	var tonerMap = new L.StamenTileLayer("toner");
	var terrainMap = new L.StamenTileLayer("terrain");
	var osmAttrib = 'Map by &copy; <a href="http://osm.org/copyright" target="_blank">OpenStreetMap</a> contributors'
			+ ' | Census & shapes by &copy; <a href="http://www.census.gov" target="_blank">US Census Bureau</a> 2010 | <a href="https://github.com/tnatool/beta" target="_blank">TNExT</a> '
			+ getVersion() + ' beta';
	var osmLayer = new L.TileLayer(OSMURL, {
		subdomains : [ "otile1-s", "otile2-s", "otile3-s",
				"otile4-s" ],
		maxZoom : 19,
		attribution : osmAttrib
	});
	var INIT_LOCATION = new L.LatLng(44.141894, -121.783660);
	var initLocation = INIT_LOCATION;
	map.addLayer(terrainMap);
	map.setView(initLocation, 7);
	accessibleCons.addTo(map);
	missedCons.addTo(map);
}

function clearMap() {
	// Reset the map
	missedCons.clearLayers();
	accessibleCons.clearLayers();
	if (originalTrip != null) {
		map.removeLayer(originalTrip);
	}
	while(missedConsPolylines.length > 0) { missedConsPolylines.pop(); }
	while(accessibleConsPolylines.length > 0) { accessibleConsPolylines.pop(); }
}

function drawMap() {
	// Draw the new trips
	clearMap();
	// Resize map
	map.invalidateSize();

	//-------------- generating route shapes and adding them to the map
	if (trips == null || trips[0] == null ) {
		console.log("no trips");
		return
	}

	d = L.PolylineUtil.decode(trips[0].tripShape1);
	points = [ d ];
	var polyline = L.multiPolyline(points, {
		weight : 4,
		color : "#0230ff",
		opacity : .8,
		smoothFactor : 1
	});
	originalTrip = L.featureGroup([polyline,L.polylineDecorator(points, {
		patterns: [
				// defines a pattern of 15px-wide dashes, repeated every 20px on the line
				{offset: 5, repeat: 50, symbol: L.Symbol.arrowHead({pixelSize: 15, headAngle: 45, pathOptions: {fillOpacity: 0.6, weight: 0}})}
			]
		})
		]);
	originalTrip.bindPopup('<br><b>Route ID:</b> ' + trips[0].routeId
			+ '<br><b>Route Name:</b> '	+ trips[0].routeName 
			+ '<br><b>Agency:</b> '	+ trips[0].agencyName);
	map.addLayer(originalTrip);
	var bounds = originalTrip.getBounds();

	$.each(trips, function(i, item) {
		d = L.PolylineUtil.decode(item.tripShape2);
		points = [ d ];
		var polyline = L.multiPolyline(points, {
			weight : getWeight(item),
			color : getColor(item),
			opacity : .5,
			smoothFactor : 1
		});
		var directedPolyline = L.featureGroup([polyline, L.polylineDecorator(points, {
			patterns: [
					// defines a pattern of 15px-wide dashes, repeated every 20px on the line
					{offset: 5, repeat: 50, symbol: L.Symbol.arrowHead({pixelSize: 15, headAngle: 45, 
						pathOptions: {fillOpacity: 0.6, color: getColor(item), weight: 0}})}
				]
			})
		]);
		var popupHtml = '<b><b>Connection number:</b> '
				+ (i + 1) + '<br><b>Route ID:</b> '
				+ item.routeId + '<br><b>Route Name:</b> '
				+ item.routeName + '<br><b>Agency:</b> '
				+ item.agencyName;
		if (item.timeDiff >= 0)
			popupHtml += '<br><b>Time Difference:</b> '
					+ secToHour(item.timeDiff);
		else
			popupHtml += '<br><b>Time Difference:-</b> '
					+ secToHour(Math.abs(item.timeDiff));

		directedPolyline.bindPopup(popupHtml);
		if (item.timeDiff > 0) {
			accessibleCons.addLayer(directedPolyline);
			accessibleConsPolylines.push(directedPolyline);
		} else {
			missedCons.addLayer(directedPolyline);
			missedConsPolylines.push(directedPolyline);
		}
		bounds.extend(directedPolyline.getBounds());
	});
	
	// Put on top
	originalTrip.bringToFront();
	
	// Fit map to bounds
	console.log("map fitBounds:", bounds.toBBoxString());
	map.fitBounds(bounds);
}

/**
 * hide/display the original trips as well as missed and accessible ones
 * @param input
 */
function toggleTrips(input) {
	if (input.value == 'originTrip') {
		if (!$(input).is(':checked'))
			map.removeLayer(originalTrip);
		else
			map.addLayer(originalTrip);

	} else if (input.value == 'accessible') {
		if (!$(input).is(':checked'))
			removeLayers(accessibleCons, accessibleConsPolylines);
		else
			addLayers(accessibleCons, accessibleConsPolylines);

	} else if (input.value == 'missed') {
		if (!$(input).is(':checked'))
			removeLayers(missedCons, missedConsPolylines);
		else
			addLayers(missedCons, missedConsPolylines);
	}
}

/**
 * Add Polylines to FeatureGroup.
 * 
 * @param featureGroup -
 *            L.FeatureGroup
 * @param polylines -
 *            L.Layer[]
 */
function addLayers(featureGroup, polylines) {
	$.each(polylines, function(index, item) {
		featureGroup.addLayer(item);
	});
}

/**
 * Removes Polylines from FeatureGroup.
 * 
 * @param featureGroup -
 *            L.FeatureGroup
 * @param polylines -
 *            L.Layer[]
 */
function removeLayers(featureGroup, polylines) {
	$.each(polylines, function(index, item) {
		featureGroup.removeLayer(item);
	});
}

/**
 * Returns a red or blue color code based on the positive or negative
 * timeDifference of the ConTrip.java object.
 * 
 * @param input
 * @returns {String}
 */
function getColor(input) {
	if (input.timeDiff <= 0) {
		return "#f44242";
	} else {
		return "#0da00b";
	}
}

function getWeight(input) {
	if (input.timeDiff > 0)
		return "3";
	else
		return "6";
}