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
//	  This script contains JavaScript variables and methods used to generate on-map connected agencies 
//	  report in the Transit Network Analysis Software Tool.
// =========================================================================================================

var gap=0.1; // default search radius
var connectionMarkers = new L.FeatureGroup();
var connectionPolylines = new L.FeatureGroup();
var selectedAgency;
var selectedAgencies=Array();
var polylines = Array();
var stopsCluster;
var text='';
map.addLayer(connectionMarkers);
map.addLayer(connectionPolylines);

// initialize the dialog box for connected agencies on-map report
var connectedAgenciesDialog=$("#connectedAgencies-form").dialog({
    autoOpen: false,
    height: 700,
    width: 350,
    modal: false,
    draggable: true,
    resizable: false,
    closeOnEscape: false,
    position: {my: "right top", at: "right-55 top+5", of: window },    
    buttons: {
        },
    close: function() { // removes markers and lines from the map on close 
    	connectionMarkers.eachLayer(function (layer) {
		    connectionMarkers.removeLayer(layer);
		});
    	connectionPolylines.eachLayer(function (layer) {
    		connectionPolylines.removeLayer(layer);
		});
    	$('#Sradius').val(0.1);
    	gap=0.1;
    	selectedAgency = [];
    	selectedAgencies = [];
    	polylines = [];
      },
    open: function(  ) { 	
    	dialog.dialog( "close" );
    	$('.jstree-checked').each(function() {
    		$( this ).children('a').children('.jstree-checkbox').click();
    	});
    	$('#ui-id-1').css('font-size','80%');
    },
  }).dialogExtend({
	  "closable" : true,
      "minimizable" : true,
      "minimizeLocation": "right",
      "minimize" : function() {
    	  //miniMap._restore();
      },
      "restore" : function() {
    	  //miniMap._minimize();
      }
  });

/**
 * generates the on-map connected agencies report for the 
 * selected agency (node), populates the dialog box, creates
 * stops and connections.
 * @param node
 */
function loadConnectedAgenciesDialog(node){
	$mylist.dialogExtend("collapse");
    connectedAgenciesDialog.dialog( "open" );
    $('#dialogPreLoader2').show();
	$('#displayConAgenciesTable').empty();
	var html='';
	var connectionsClusters=new Array();
	var key = Math.random();
	
	$.ajax({
		type: 'GET',
		datatype: 'json',
		url: '/TNAtoolAPI-Webapp/queries/transit/ConAgenXR?&agency='+node.attr("id")+'&gap='+gap+'&key='+ key+'&dbindex='+dbindex+'&username='+getSession(),
		async: true,
		success: function(data){
			var cacolorArray=['cagcluster', 'capicluster', 'caccluster', 'carcluster', 'capucluster', 'cabrcluster'];
			var colorArray=['gcluster', 'picluster', 'ccluster', 'rcluster', 'pucluster', 'brcluster'];
			var colors = ['rgba(110, 204, 57, 0.8)',
			              'rgba(255, 51, 255, 0.8)',
			              'rgba(5, 250, 252, 0.7)',
			              'rgba(254, 10, 10, 0.6)',
			              'rgba(122, 0, 245, 0.6)',
			              'rgba(204, 102, 0, 0.8)'];
			// filling in the dialog box
			text = data.agency;
			$('#dialogSelectedAgency').html(data.agency);
    		$('#dialogNoOfConnectedAgencies').html(data.ClusterR.length);
			html = '<table id="connectedAgenciesTable" class="display" align="center">';
			var tmp = 	'<th>Agency ID </th>'+
            			'<th>Agency Name</th>'+
            			'<th>Number of Connections</th></tr>';	
			html += '<thead>'+tmp+'</thead><tbody>';
			
			for (var i = 0; i < data.ClusterR.length; i++) {
				html += '<td>'+ data.ClusterR[i].id +'</td>'+
				'<td>'+ data.ClusterR[i].name +'</td>'+
				'<td>'+ data.ClusterR[i].size +'</td></tr>';
			}
			
			html += '</tbody></table>';
			$('#displayConAgenciesTable').append(html);
			
			// initializing data table
			var connectedAgenciesTable = $('#connectedAgenciesTable').DataTable( {
				"paging": false,
				"bSort": false,
				"dom": 'T<"clear">lfrtip',
		        "tableTools": {
		        	"sSwfPath": "js/lib/DataTables/swf/copy_csv_xls_pdf.swf",
		        	"sRowSelect": "multi",
		        	"aButtons": []}
			});
			$("#connectedAgenciesTable_length").remove();
		    $("#connectedAgenciesTable_filter").remove();
		    $("#connectedAgenciesTable_info").remove();
		    
		    /**
		     * if the row is already selected, removes its stops and connections from map,
		     * else adds stops of connected agencies to the map
		     */
		    connectedAgenciesTable.$('tr').click( function () {
		    	if($(this).hasClass('selected')){
		    		connectionMarkers.removeLayer(connectionsClusters[$(this).index()]);
		    		connectionPolylines.removeLayer(polylines[$(this).index()]);
		    		// remove selected agency from agency list.
		    		selectedAgencies.splice(selectedAgencies.indexOf($(this).children().eq(0).html()),1);
		    	}else{
		    		var index = $(this).index();
		    		selectedAgencies.push($(this).children().eq(0).html());
		    		var agencyId = $(this).children().eq(0).html();
		    		var c = ($(this).index()+1)%6;
		    		var tmpConnectionsClusters = new L.MarkerClusterGroup({
    					iconCreateFunction: function (cluster) {
    						return new L.DivIcon({ html: cluster.getChildCount(), className: cacolorArray[c], iconSize: new L.Point(25, 25) });						
    					},
    					spiderfyOnMaxZoom: true, showCoverageOnHover: false, zoomToBoundsOnClick: true, singleMarkerMode: false, maxClusterRadius: 30
    				});
    				var agencyName=data.ClusterR[$(this).index()].name;
    				$.each(data.ClusterR[$(this).index()].connections, function (i, item){
    					var str = item.dcoords.replace("{","");
    					str = str.replace("}","");
            			str=str.split(",");
    					var lat = str[0];
    					var lon = str[1];
    					
    					var marker = new L.marker([lat,lon], {icon: L.divIcon({html:'1',iconSize: new L.point( 25, 25 ),className: colorArray[c]})}).on('click',onMarkerClick);
    					marker.id = item.id;	// This ID is used for pushing the connections of each stop to polylines array.
    					marker.agencyId = agencyId;
    					marker.lat = lat;
    					marker.lon = lon;
    					marker.color = colors[c];
    					marker.bindPopup('<b>Agency: </b>'+ agencyName +'<br>'+
    									'<b>Stop Name: </b>'+item.names);
    					tmpConnectionsClusters.addLayer(marker);
    				});

    				tmpConnectionsClusters.on('clusterclick', function (a) {
    				    // a.layer is actually a cluster
    					for(var i=0; i<a.layer.getAllChildMarkers().length;i++){
    						if(polylines[a.layer.getAllChildMarkers()[i]._leaflet_id]!=null){
    							a.layer.getAllChildMarkers()[i].closePopup();
    							connectionPolylines.removeLayer(polylines[a.layer.getAllChildMarkers()[i]._leaflet_id]);
    							delete polylines[a.layer.getAllChildMarkers()[i]._leaflet_id];
    						}
    					}
    				});
    				connectionsClusters[$(this).index()] = (tmpConnectionsClusters);
		    		connectionMarkers.addLayer(connectionsClusters[$(this).index()]);
		    	}                   		    	
		    });
		    $('#dialogPreLoader2').hide();
		    
		    /**
		     * gets stops belonging to the original agency and adds them to 
		     * the map with yellow markers
		     */
			$.ajax({
				type: 'GET',
				datatype: 'jason',
				url: '/TNAtoolAPI-Webapp/queries/transit/agenStops?agency='+ node.attr("id")+'&dbindex='+dbindex,
				async: true,
				success: function(data){
					var stopsCluster = new L.MarkerClusterGroup({
						iconCreateFunction: function (cluster) {
							return new L.DivIcon({ html: cluster.getChildCount(), className: 'caycluster', iconSize: new L.Point(25, 25) });						
						},
						spiderfyOnMaxZoom: true, showCoverageOnHover: false, zoomToBoundsOnClick: true, singleMarkerMode: true, maxClusterRadius: 30
					});
					
					$.each(data.stopsList,function(i, item){
						var marker = new L.marker([item.lat,item.lon]).on('click',onMarkerClick);
						marker.bindPopup('<b>Agency:</b> '+item.agencyName+
										'<br><b>Stop Name: </b> '+item.name);
						marker.id = item.id;	// This ID is used for pushing the connections of each stop to polylines array.
						marker.lat = item.lat;
						marker.lon = item.lon;
						marker.agencyId = node.attr("id");
						marker.color = 'rgba(255, 255, 0, 0.8)';
						stopsCluster.addLayer(marker);
					}); 
					connectionMarkers.addLayer(stopsCluster);
					map.fitBounds(stopsCluster);
				}
			});
		}
	});
}

/**
 * draws a direct line from a stop to all the connected stops 
 * that are shown on the map. 
 */
function onMarkerClick(){
	var id = this._leaflet_id;
	
	/*
	 * A shallow copy of selectedAgencies. This array is trimmed based on whether a stop
	 * of the original agency is clicked or a stop for one of the agencies in the list.
	 * This is done to make sure only the connections between the original and the other
	 * agencies are displayed.
	 */ 	
	var agencies;									 	
	if (this.agencyId == selectedAgency.attr("id")){
		agencies = [];
		agencies = selectedAgencies.slice(0);		
		agencies.splice(agencies.indexOf(this.agencyId),1);
	}else{
		agencies = selectedAgency.attr("id");
	} 		
	
	if (polylines[id]==null){
		var selectedStopLat= this.lat;
		var selectedStopLon=this.lon;
		var color = this.color;
			
		this.closePopup();
		$.ajax({
			type: 'GET',
			datatype: 'json',
			url: 	'/TNAtoolAPI-Webapp/queries/transit/castops?&lat=' + selectedStopLat +
					'&lon=' + selectedStopLon +'&agencies='+ agencies +'&radius=' + gap + '&dbindex=' + dbindex,
			async: true,
			success: function(data){
				var sourceMarker = new L.marker([selectedStopLat,selectedStopLon]);
				var bounds = Array();
				bounds.push(sourceMarker.getLatLng());
				var tmpConnectionsPolylines = new L.FeatureGroup();
				$.each(data.stopsList, function(i,item){
					if (item.lat!=selectedStopLat && item.lon != selectedStopLon){
						var latlngs= Array();
						var destMarker = new L.marker([item.lat,item.lon]);
						bounds.push(destMarker.getLatLng());
						latlngs.push(sourceMarker.getLatLng());
						latlngs.push(destMarker.getLatLng());
						var polyline = L.polyline(latlngs, {color: color});
						tmpConnectionsPolylines.addLayer(polyline);
					}		
				});
				polylines[id] = tmpConnectionsPolylines;
				connectionPolylines.addLayer(polylines[id]);
				connectedAgenciesDialog.dialogExtend("minimize");
				map.fitBounds(bounds,{maxZoom:18});
				
				// zoom out if the connection is too small and
				// map tiles are not small enough to bound the connection.
				if (map.getZoom >18)
					map.setZoom(18);
			}
		});
	}else{
		this.closePopup();
		connectionPolylines.removeLayer(polylines[id]);
		delete polylines[id];
	}
}

/**
 * removes all markers and connection already shown on 
 * the map and reload the report with the new search 
 * radius.
 * @param input - search radius
 */
function reloadConnectedAgenciesDialog(input){
	if (input > 5){	// Checks if the entered search radius exceeds the maximum.
		alert('Enter a radius less than 5 miles.');
		return;
	}
	gap=input;
	connectionMarkers.eachLayer(function (layer) {
	    connectionMarkers.removeLayer(layer);
	});
	connectionPolylines.eachLayer(function (layer) {
	    connectionPolylines.removeLayer(layer);
	});
	selectedAgencies = [];
	selectedAgencies.push(selectedAgency.attr("id"));
	loadConnectedAgenciesDialog(selectedAgency);
}