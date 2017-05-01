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
//	  This script contains JavaScript variables and methods used to generate On-Map Timing Connections 
//	  in the Transit Network Analysis Software Tool.
// =========================================================================================================

var map;
var missedCons = L.featureGroup(); // Leaflet object
var missedConsPolylines = []; // Array of Leaflet Polylines
var accessibleCons = L.featureGroup(); // Leaflet object
var accessibleConsPolylines = []; // Array of Leaflet Polylines
var originalTrip; // L.Polyline
var originalTripPattern = {patterns: [
	        // defines a pattern of 15px-wide dashes, repeated every 20px on the line
	        {offset: 5, repeat: 50, symbol: L.Symbol.arrowHead({pixelSize: 15, headAngle: 45, pathOptions: {fillOpacity: 0.6, weight: 0}})}
	    ]};

$(document)
		.ready(
				function() {
					//---------------loading map tiles----------------
					map = new L.Map('map', {
						minZoom : 0,
						maxZoom : 18,
						zoomControl:false
					});
					var OSMURL = "http://{s}.mqcdn.com/tiles/1.0.0/osm/{z}/{x}/{y}.png";
					var aerialURL = "http://{s}.mqcdn.com/tiles/1.0.0/sat/{z}/{x}/{y}.png";
					var tonerMap = new L.StamenTileLayer("toner");
					var terrainMap = new L.StamenTileLayer("terrain");
					var osmAttrib = 'Map by &copy; <a href="http://osm.org/copyright" target="_blank">OpenStreetMap</a> contributors'
							+ ' | Census & shapes by &copy; <a href="http://www.census.gov" target="_blank">US Census Bureau</a> 2010 | <a href="https://github.com/tnatool/beta" target="_blank">TNA Software Tool</a> '
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
					
					//-------------- generating route shapes and adding them to the map
					accessibleCons.addTo(map);
					missedCons.addTo(map);

					d = L.PolylineUtil.decode(parent.trips[0].tripShape1);
					
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
					originalTrip.bindPopup('<br><b>Route ID:</b> ' + parent.trips[0].routeId
							+ '<br><b>Route Name:</b> '	+ parent.trips[0].routeName 
							+ '<br><b>Agency:</b> '	+ parent.trips[0].agencyName);
					map.addLayer(originalTrip);
					
					$.each(parent.trips, function(i, item) {
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
					});
				});

// loading map legend
$(function() {
	$("#mapLegend").dialog({

		minHeight : '200px',
		resizable : false,
		draggable : false,
		position : {
			my : "left+10 top+10",
			at : "left top",
			of : "#map"
		}
	})
	$('.ui-dialog-titlebar').css('display', 'none');
});

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