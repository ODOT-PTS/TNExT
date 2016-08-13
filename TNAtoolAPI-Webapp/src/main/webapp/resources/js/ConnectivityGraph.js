//var isConGraphDialogOpen = false;
var day;
var gap;
var vertices = {}; // declared to keep track of agencies on the map and avoid adding duplicates.
var edges = [];
var vertexEdgesMap = {};  // maps vertices to their edges.
var edgeVerticesMap = {}; // maps edges to their vertices.
var congraphobj;
var edgeStyle = {};	// The object that contains the style setting of edges on the graph.
edgeStyle['normal'] = {
			color : 'blue',
			opacity : 0.3,
		};
edgeStyle['highlight'] = {
			color : 'red',
			opacity : 1,
		};
edgeStyle['hover'] = {
			color : 'red',
			opacity : 1,
		}

var agencyStyle = {}; // The object that contains the style setting of agency elements on the graph.
agencyStyle['normal'] = {
			color : 'black',
			opacity : 0.2,
		};
agencyStyle['highlight'] = {
			color : 'black',
			opacity : 0.7,
		};
agencyStyle['hover'] = {
			color : 'black',
			opacity : 0.7,
		}

/**
 * Loads/toggles the connectivity graph dialog box.
 */
function toggleConGraphDialog(){
	
	// Initializing the connectivity graph dialog box
	$( "#con-graph-dialog" ).empty();
	var dialogheight = Math.round((window.innerHeight)*.6); 
	if (dialogheight > 900)
		dialogheight = 900;
	
	$(function() {
		$( "#con-graph-dialog" ).dialog({
	        autoOpen: false,
	        height: dialogheight,
	        width: 350,
	        modal: false,
	        draggable: false,
	        resizable: true,
	        closeOnEscape: false,
	        position: {my: "left top", at: "right top", of: "#con-graph-button"},   
	        buttons: {
	            },
	        close: function() {
	        	// removing the graph from the map
	        	$.each(vertices, function(id, vertex){
	        		map.removeLayer(vertex);
	        	});
	        	$.each(edges, function(index, edge){
	        		map.removeLayer(edge);
	        	});
	        	
	        	// empty global variables
	        	vertices = {};
	        	edges = [];
	        	vertexEdgesMap = {};
	        	edgeVerticesMap = {};
	          },
	        open: function( event, ui ) {
	        	// To be implemented.
	        }
	      });
	  });
	
	if ($( "#con-graph-dialog" ).dialog( "isOpen" ))
		$( "#con-graph-dialog" ).dialog( "close" );
	else{
		$( "#con-graph-dialog" ).empty;
		html = '<table style="font-size: 100%;"><td>Connectivity Gap (miles) </td><td><input id="con-graph-input" value="0.1" style="width:5em"></td></table><br>';
		html += '<p><b>Date</b>: <input readonly type="text" class="POPcal" id="ConGraphDatepicker"></p>';
		html +='<button onclick="openConGraph()">Submit1</button><button onclick="openConGraph2()">Submit2</button><br>';
		$( "#con-graph-dialog" ).append(html);
		$( "#con-graph-dialog" ).dialog( "open" );
		
		$( "#ConGraphDatepicker" ).datepicker({
		    showButtonPanel: true,
			onSelect: function (date) {
				currentDate = date;				
		    }
		});
		$("#ConGraphDatepicker").datepicker( "setDate", new Date());
	}
}

function openConGraph(){
	day = $( '#ConGraphDatepicker' ).val();
	gap = $( '#con-graph-input' ).val();
	$( "#con-graph-dialog" ).dialog( "close" );
	toggleConGraphDialog();
	$( "#ConGraphDatepicker" ).datepicker( "setDate", day);
	$( "#con-graph-input" ).val(gap);
	
	var dbindex = getURIParameter("dbindex");
	var agencyCentroids= {};
	var loaderHtml = '<img id="conGraphPreLoader" src="../resources/images/287.GIF" align="middle" alt="Page Loading" style="height:80; width:80; margin:120px" >';
	$( "#con-graph-dialog" ).append(loaderHtml);
	
	// Getting agency centroids
	$.ajax({
		type: 'GET',
		datatype: 'json',
		url: '/TNAtoolAPI-Webapp/queries/transit/agencyCentriods?&dbindex='+dbindex+'&username='+getSession(),
		async: true,
		success: function(d){
			$.each(d.list,function(index, item){
				agencyCentroids[item.ID] = item;
			});
			callBack(agencyCentroids, dbindex);
		}
	});
}

function callBack(agencyCentroids, dbindex){
	var result = {};
	
	// Getting connections
	$.ajax({
		type: 'GET',
		datatype: 'json',
		url: '/TNAtoolAPI-Webapp/queries/transit/connectivityGraph?&x=' +  gap*1609.3 + '&day='+day+'&dbindex='+dbindex+'&username='+getSession(),
		async: true,
		success: function(d){			
			localStorage.setItem('myStorage', JSON.stringify(d));	
			result = d.set;
//			result = JSON.parse(localStorage.getItem('myStorage')).set;
			$.each(result,function(index, item){
				// Initializing start point of the edge
				var vertex1;
				if (vertices[item.a1ID] != undefined){
					vertex1 = vertices[item.a1ID];
				}else{
					vertex1 = makeMarker(agencyCentroids[item.a1ID]);
					vertex1.id = item.a1ID;
					vertex1.bindPopup('<table style="font-size:100%"><tr><td>Agency&nbsp;ID:&nbsp;</td><td nowrap>' + item.a1ID + '</td></tr><tr><td>Agency&nbsp;Name:&nbsp;</td><td nowrap>' + item.a1name + '</td></tr></table>', {autoPan : false});
					vertex1.on('mouseover',function(e){
//							vertex1.openPopup(); 
							vertex1.setStyle({weight:6, opacity:agencyStyle['hover'].opacity});
						})
						.on('mouseout',function(e){
//							vertex1.closePopup(); 
							vertex1.setStyle({weight:4, opacity:agencyStyle[vertex1.status].opacity});
						});
					
					vertices[item.a1ID] = vertex1;
				}
						
				// Initializing end point of the edge if exists
				if (item.connections.size > 0){
					
					var vertex2;
					if (vertices[item.a2ID] != undefined){
						vertex2 = vertices[item.a2ID];
					}else{
						vertex2 = makeMarker(agencyCentroids[item.a2ID]);
						vertex2.id = item.a2ID;
						vertex2.bindPopup('<table style="font-size:100%"><tr><td>Agency&nbsp;ID:&nbsp;</td><td nowrap>' + item.a2ID + '</td></tr><tr><td>Agency&nbsp;Name:&nbsp;</td><td nowrap>' + item.a2name + '</td></tr></table>', {autoPan : false});
						vertex2.on('mouseover',function(e){
//								vertex2.openPopup(); 
								vertex2.setStyle({weight:6, opacity:agencyStyle['hover'].opacity});
							})
							.on('mouseout',function(e){
//								vertex2.closePopup(); 
								vertex2.setStyle({weight:4, opacity:agencyStyle[vertex2.status].opacity});
							});
						
						vertices[item.a2ID] = vertex2;
					}
					
					// Initializing the edge
					var edgeCoordinates = getCoordinatesForEdge(vertex1, vertex2);
					var edge = L.polyline(edgeCoordinates,{color: edgeStyle['normal'].color, opacity:edgeStyle['normal'].opacity});
					edge.on('mouseover',function(e){
							edge.openPopup();
							toggleEdge(edgeVerticesMap, edge, 'hover', 'hover');					
						})
						.on('mouseout',function(e){
							edge.closePopup();
							toggleEdge(edgeVerticesMap, edge, edge.status, edge.status);
						});
					
					var popUpContent = '<table style="font-size:100%"><tr><td>From:&nbsp;</td><td nowrap>' + item.a1name + '</td></tr><tr><td>To:&nbsp;</td><td nowrap>' + item.a2name + '</td></tr><tr><td>Connections:&nbsp;</td><td>' + item.connections.size + '</td></tr></table>';
					edge.bindPopup(popUpContent, {autoPan : false});
					edge.id = vertex1.id + vertex2.id;
					edge.status = 'normal';
					edges.push(edge);
					map.addLayer(edge);

					// updating mapping objects (agencyEdgesMap & agencyEdgesMap).
					if (vertexEdgesMap[vertex1.id]===undefined) vertexEdgesMap[vertex1.id] = [];
					vertexEdgesMap[vertex1.id].push(vertex2);
					vertexEdgesMap[vertex1.id].push(edge);
					
					if (vertexEdgesMap[vertex2.id]===undefined) vertexEdgesMap[vertex2.id]= [];
					vertexEdgesMap[vertex2.id].push(vertex1);
					vertexEdgesMap[vertex2.id].push(edge);
					
					edgeVerticesMap[edge.id] = [];
					edgeVerticesMap[edge.id].push(vertex1);
					edgeVerticesMap[edge.id].push(vertex2);
				}				
			});
			
			// Add markers to the map
			$.each(vertices, function(index, agencyShape){
				map.addLayer(agencyShape);
				
				// Add isolated agencies to vertexEdgesMap
//				if ()
			});
			
			///////// loading agency list and check-boxes///////////////////
			// get agency list
			var agencies = {};
			var sortedAgencyList = []; // used to sort the list of agencies in connectivity graph dialog box
			var html = '';
			
			$.ajax({
				type: 'GET',
				datatype: 'json',
				url: '/TNAtoolAPI-Webapp/queries/transit/allAgencies?&dbindex='+dbindex+'&username='+getSession(),
				async: false,
				success: function(d){			
					$.each(d,function(index, item){
						agencies[item.id] = item.name;
						sortedAgencyList.push({id:item.id, name:item.name});
					});	
					sortedAgencyList.sort(function(a,b) {return (a.name > b.name) ? 1 : ((b.name > a.name) ? -1 : 0);} );
				}
			
			});
			
			
			html += '<br><table style="font-size:100%"><tr style="border-bottom: 1px solid #ddd">';
			html += '<th><img src="images/tooltip.png" style="cursor:help; width:100%;" title="The checkbox highlights the connectivity graph element that represents the agency" alt="1"></th>';
			html += '<th><img src="images/tooltip.png" style="cursor:help; width:100%;" title="The checkbox highlights the agency, as well as the agencies connected to it and the connections" alt="1"></th>';
			html += '<th><img src="images/tooltip.png" style="cursor:help; width:100%;" title="The checkbox highlights all parts of the transit graph where one can get to, starting from the associated agency" alt="1"></th>';
			html += '<th>Agency</th></tr>';
				
//			$.each(sortedAgencyList, function(id, name){
			for (var i = 0 ; i < sortedAgencyList.length ; i++){
				var id = sortedAgencyList[i].id;
				var name = agencies[sortedAgencyList[i].id];
				html += '<tr><th><input type="checkbox" class="conGraphCheckBox" id="'+id+'" data-_case="1" onchange="myFunction(this)"></th>'+
				'<th><input type="checkbox" class="conGraphCheckBox" id="'+id+'" data-_case="2" onchange="myFunction(this)"></th>'+
				'<th><input type="checkbox" class="conGraphCheckBox" id="'+id+'" data-_case="3" onchange="myFunction(this)"></th>'+
				'<th nowrap>' + name + '</th></tr>';
				
			};
			html += '</table>';
			
			$( "#con-graph-dialog" ).append(html);
			$('#conGraphPreLoader').hide();
			isConGraphDialogOpen = true;
		}			
	});	

	
}

/**
 * returns an array of coordinates that shows the shortest distance between to shapes (agencies)
 * on the map.
 * 
 */
function getCoordinatesForEdge(v1,v2){
	var output = [];
	var shortestDist = Math.pow(10,10);
	$.each(v1.coordinates, function(index1, item1){
		$.each(v2.coordinates, function(index2, item2){
			
			var dist = getDistance(item1,item2);
			if (dist < shortestDist){
				shortestDist = dist;
				output[0] = {lat:item1.lat, lng:item1.lng};
				output[2] = {lat:item2.lat, lng:item2.lng};
			}
		});
	});
	output[1] = {lat:(output[0].lat+output[2].lat)/2, lng:(output[0].lng+output[2].lng)/2};  // Setting the center point on the edge to locate the popup.
	return output;
}

/**
 * Computes the physical distance between two coordinates (Km).
 * @param p1
 * @param p2
 * @returns
 */
function getDistance(p1, p2){
	var dLon = Math.radians(p2.lng - p1.lng); 
	var dLat = Math.radians(p2.lat - p1.lat);
	var lat1 = Math.radians(p1.lat);
	var lat2 = Math.radians(p2.lat);
	var a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2); 
	var c = 2 * Math.atan2( Math.sqrt(a), Math.sqrt(1-a) ) ;
	var output =  6371 * c ;
	return output;
}

Math.radians = function(deg){
	return deg * (Math.PI/180);
}

/**
 * Toggles the opacity of the selected edge and both its agencies/vetices.
 * 
 * @param edgeVerticesMap (object)
 * @param edge (object)
 * @param opacity (number)
 */
function toggleEdge(edgeVerticesMap, e, agency_style, edge_style){
	e.setStyle({opacity:edgeStyle[edge_style].opacity, color:edgeStyle[edge_style].color});
	$.each(edgeVerticesMap[e.id],function(index, item){
		item.setStyle({
			fillOpacity:agencyStyle[agency_style].opacity, 
			opacity:agencyStyle[agency_style].opacity, 
			fillColor:agencyStyle[agency_style].color, 
			color:agencyStyle[agency_style].color
		});
	});
}

/**
 * Toggles the opacity of the selected agencies along
 * with all the edges and agencies connected to it.
 * 
 * @param agencyEdgesMap (object)
 * @param vertex (object)
 * @param opacity (number)
 * @param color (string)
 */
function toggleVertexNeighbors(agencyEdgesMap, v, agency_style, edge_style){
	v.setStyle({
		fillOpacity:agencyStyle[agency_style].opacity, 
		opacity:agencyStyle[agency_style].opacity, 
		fillColor:agencyStyle[agency_style].color, 
		color:agencyStyle[agency_style].color
		});
	if (agency_style != 'hover')
		v.status = agency_style;
	
	$.each(agencyEdgesMap[v.id],function(index, item){
		if (item instanceof L.Polyline){
			item.setStyle({
				opacity:edgeStyle[edge_style].opacity, 
				color : edgeStyle[edge_style].color
				});
			if (edge_style != 'hover')
				item.status = edge_style;
		}
		else if(item instanceof L.FeatureGroup)
			item.setStyle({
				fillOpacity:agencyStyle[agency_style].opacity, 
				opacity:agencyStyle[agency_style].opacity, 
				fillColor:agencyStyle[agency_style].color, 
				color:agencyStyle[agency_style].color
				});
		if (agency_style != 'hover')
			item.status = agency_style;
	});
}

/**
 * Toggles the opacity of all the edges and vertices
 * connected to the selected vertex.
 * 
 * @param agencyEdgesMap (HashMap)
 * @param edgeVerticesMap (HashMap)
 * @param vertex (object)
 * @param opacity (number)
 * @param color (string)
 */
function toggleVertexConnections(agencyEdgesMap, edgeVerticesMap, v, agency_style, edge_style){
	var stop = true;
	v.setStyle({
		fillOpacity : agencyStyle[agency_style].opacity, 
		opacity: agencyStyle[agency_style].opacity, 
		fillColor:agencyStyle[agency_style].color, 
		color:agencyStyle[agency_style].color
		});
	v.status = agency_style; 
	
	if (agencyEdgesMap[v.id] != undefined){ 
		$.each(agencyEdgesMap[v.id], function(index,item){
			if (item instanceof L.Polyline){
				if (item.options.opacity != edgeStyle[edge_style].opacity){
					item.setStyle({opacity:edgeStyle[edge_style].opacity, color:edgeStyle[edge_style].color});
					item.status = edge_style;
					$.each(edgeVerticesMap[item.id], function(index, vertex){
						if(vertex instanceof L.FeatureGroup){
							$.each(vertex.getLayers(), function(index, item){
								if (item.options.fillOpacity != agencyStyle[agency_style].opacity){
									item.setStyle({
										fillOpacity:agencyStyle[agency_style].opacity, 
										opacity: agencyStyle[agency_style].opacity, 
										fillColor:agencyStyle[agency_style].color, 
										color:agencyStyle[agency_style].color
										});
									vertex.status = agency_style;
									stop = false;
								}
								if (!stop){
									toggleVertexConnections(agencyEdgesMap, edgeVerticesMap, vertex, agency_style, edge_style);
								}
							})
							vertex.status = agency_style;
						}else if (vertex.options.fillOpacity != agencyStyle[agency_style].opacity){
							vertex.setStyle({
								fillOpacity : agencyStyle[agency_style].opacity, 
								opacity : agencyStyle[agency_style].opacity, 
								fillColor : agencyStyle[agency_style].color, 
								color : agencyStyle[agency_style].color
								});
							vertex.status = agency_style;
							toggleVertexConnections(agencyEdgesMap, edgeVerticesMap, vertex, agency_style, edge_style);
						}
					})
				}				
			}
		});
	}
	
}

/**
 * Makes the leaflet object of the agency "x". If the agency is 
 * centralized, a marker is returned, esle a FeatureGroupe.
 * @param ConGraphAgency (object)
 * @returns {L.Path}
 */
function makeMarker(x){
	if (x.centralized){
		var marker = new L.CircleMarker([x.vertices[0].lat, x.vertices[0].lng], {
			radius: 3,
	        color: agencyStyle['normal'].color,
	        weight: 2,
	        opacity: agencyStyle['normal'].opacity,
	        fillOpacity: agencyStyle['normal'].opacity,
	    });
		marker.coordinates = [];
		marker.coordinates.push(marker.getLatLng());
		marker.status = 'normal';
		return marker;
	}else{
		var graphObjects = [];
		for (var i = 0 ; i < x.vertices.length ; i++){
				var marker = new L.CircleMarker([x.vertices[i].lat, x.vertices[i].lng], {
					radius: 3,
			        color: agencyStyle['normal'].color,
			        weight: 2,
			        opacity: agencyStyle['normal'].opacity,
			        fillOpacity: agencyStyle['normal'].opacity,
			    });
				graphObjects.push(marker);
		};
		
		$.each(x.edges, function(index, item){
			graphObjects.push(L.polyline([item[0], item[1]],{
				stroke : true,
				color : agencyStyle['normal'].color, 
				weight : 4,
				opacity : agencyStyle['normal'].opacity,
				dashArray : '3 8 3 8',
			})
			);
		})
		var shape = L.featureGroup(graphObjects);//.bindPopup(agencyID);
		shape.coordinates = x.vertices;
		shape.status = 'normal';
		return shape;
	}
}

/**
 * Returns the constroid of two points
 * @param vertex1
 * @param vertex2
 * @returns {L.LatLng}
 */
function edgeCentroid(v1,v2){
	var output = {};
	output.lat = (v1.getLatLng().lat + v2.getLatLng().lat) / 2;
	output.lng = (v1.getLatLng().lng + v2.getLatLng().lng) / 2;
	return output;
}

function myFunction(input){
	var vertex = vertices[input.id];
	var myCase = input.dataset._case;
	var isChecked = input.checked;
	var agencyID = input.dataset.id;
	
	// Uncheck the boxes for the selected agency.
	$.each($( ":input#" + input.id + ".conGraphCheckBox" ), function(index,item){
		if (item.dataset._case != myCase ){
			item.checked = false;
			toggleVertexConnections(vertexEdgesMap, edgeVerticesMap, vertices[item.id], 'normal', 'normal');			
		}			
	});
	
	if (myCase == 1){
		if (isChecked){
			vertex.setStyle({
				fillOpacity:agencyStyle['highlight'].opacity, 
				opacity:agencyStyle['highlight'].opacity, 
				fillColor:agencyStyle['highlight'].color, 
				color:agencyStyle['highlight'].color
				});
		}else{
			vertex.setStyle({
				fillOpacity:agencyStyle['normal'].opacity, 
				opacity:agencyStyle['normal'].opacity, 
				fillColor:agencyStyle['normal'].color, 
				color:agencyStyle['normal'].color
				});
		}
	}else if (myCase == 2){
		if (isChecked){
			toggleVertexNeighbors(vertexEdgesMap, vertex, 'highlight', 'highlight');
		}else{
			toggleVertexNeighbors(vertexEdgesMap, vertex, 'normal', 'normal');
		}
	}else if (myCase == 3){
		if (isChecked){
			toggleVertexConnections(vertexEdgesMap, edgeVerticesMap, vertex, 'highlight', 'highlight');
		}else{
			toggleVertexConnections(vertexEdgesMap, edgeVerticesMap, vertex, 'normal', 'normal');
		}
	}
}

