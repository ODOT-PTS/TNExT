//var isConGraphDialogOpen = false;
var vertices = {}; // declared to keep track of vertices on the map and avoid adding duplicates.
var edges = [];
var vertexEdgesMap = {};  // maps vertices to their edges. A1_lat+A1_lng form the key.
var edgeVerticesMap = {}; // maps edges to their vertices. A1_lat+A1_lng+A2_lat+A2_lng form the key.
var opc0 = 0.3; // default opacity of the graph elements
var opc1 = 1; // opacity of highlighted graph elements
var color0 = 'blue'; // default color of the graph elements
var color1 = 'red';	 // color of highlighted graph elements
var congraphobj;

function openConGraph2(){
	day = $( '#ConGraphDatepicker' ).val();
	gap = $( '#con-graph-input' ).val();
	$( "#con-graph-dialog" ).dialog( "close" );
	toggleConGraphDialog();
	$( "#ConGraphDatepicker" ).datepicker( "setDate", day);
	$( "#con-graph-input" ).val(gap);
		
	var dbindex = getURIParameter("dbindex");
	var agencyCentroids= {};
	var loaderHtml = '<img id="conGraphPreLoader" src="../resources/images/287.GIF" alt="Page Loading" style="height:80; width:80; margin:120px" >';
	$( "#con-graph-dialog" ).append(loaderHtml);
	
	// Getting agency centroids
	$.ajax({
		type: 'GET',
		datatype: 'json',
		url: '/TNAtoolAPI-Webapp/queries/transit/agencyCentriods2?&dbindex='+dbindex,
		async: true,
		success: function(d){
			$.each(d.list,function(index, item){
				agencyCentroids[item.id] = {lat:item.lat, lng:item.lng};
			});
			callBack2(agencyCentroids, dbindex);
		}
	});
	
}

function callBack2(agencyCentroids, dbindex){
	var result = {};

	// Getting connections
	$.ajax({
		type: 'GET',
		datatype: 'json',
		url: '/TNAtoolAPI-Webapp/queries/transit/connectivityGraph?&x='+$('#con-graph-input').val()*1609.3+'&day='+day+'&dbindex='+dbindex,
		async: true,
		success: function(d){			
			localStorage.setItem('myStorage', JSON.stringify(d));	
			result = d.set;	
//			var result = JSON.parse(localStorage.getItem('myStorage')).set;
			
			var a1 = {}; // agency 1, i.e., start point of the object.
			var a2 = {}; // agency 2, i.e., end point of the object.
		console.log(agencyCentroids);
			$.each(result,function(index, item){

				// Initializing start point of the edge
				a1.lat = agencyCentroids[item.a1ID].lat;
				a1.lng = agencyCentroids[item.a1ID].lng;
				a1.hashCode = item.a1ID;
				
				var vertex1;
				if (vertices[a1.hashCode] != undefined){
					vertex1 = vertices[a1.hashCode];
				}else{
					vertex1 = makeMarker2(a1.lat, a1.lng);
					vertex1.bindPopup('<table style="font-size:100%"><tr><td>Agency&nbsp;ID:&nbsp;</td><td nowrap>' + item.a1ID + '</td></tr><tr><td>Agency&nbsp;Name:&nbsp;</td><td nowrap>' + item.a1name + '</td></tr></table>');
					vertex1.on('mouseover',function(e){
							vertex1.openPopup(); 
							toggleVertexNeighbors2(vertexEdgesMap, vertex1, opc1, color1);
						})
						.on('mouseout',function(e){
							vertex1.closePopup(); 
							toggleVertexNeighbors2(vertexEdgesMap, vertex1, opc0, color0);
						});
					
					vertex1.hashCode = a1.hashCode;
					vertices[a1.hashCode] = vertex1;
				}
						
				// Initializing end point of the edge if exists
				if (item.connections.size > 0){
					a2.lat = agencyCentroids[item.a2ID].lat;
					a2.lng = agencyCentroids[item.a2ID].lng;
					a2.hashCode = item.a2ID;
					
					var vertex2;
					if (vertices[a2.hashCode] != undefined){
						vertex2 = vertices[a2.hashCode];
					}else{
						vertex2 = makeMarker2(a2.lat, a2.lng);
						vertex2.bindPopup('<table style="font-size:100%"><tr><td>Agency&nbsp;ID:&nbsp;</td><td nowrap>' + item.a2ID + '</td></tr><tr><td>Agency&nbsp;Name:&nbsp;</td><td nowrap>' + item.a2name + '</td></tr></table>');
						vertex2.on('mouseover',function(e){
								vertex2.openPopup(); 
								toggleVertexNeighbors2(vertexEdgesMap, vertex2, opc1, color1);
							})
							.on('mouseout',function(e){
								vertex2.closePopup(); 
								toggleVertexNeighbors2(vertexEdgesMap, vertex2, opc0, color0);
							});
						
						vertex2.hashCode = a2.hashCode;
						vertices[a2.hashCode] = vertex2;
					}
					
					// Initializing the edge
					var edge = L.polyline([vertex1.getLatLng(),edgeCentroid2(vertex1,vertex2), vertex2.getLatLng()],{color: color0, opacity:opc0});
					edge.on('mouseover',function(e){
							edge.openPopup();
							toggleEdge2(edgeVerticesMap, edge, opc1, color1);					
						})
						.on('mouseout',function(e){
							edge.closePopup();
							toggleEdge2(edgeVerticesMap, edge, opc0, color0);
						});
					
					var popUpContent = '<table style="font-size:100%"><tr><td>From:&nbsp;</td><td nowrap>' + item.a1name + '</td></tr><tr><td>To:&nbsp;</td><td nowrap>' + item.a2name + '</td></tr><tr><td>Connections:&nbsp;</td><td>' + item.connections.size + '</td></tr></table>';
					edge.bindPopup(popUpContent, {autoPan : false});
					edge.hashCode = a1.hashCode + a2.hashCode;
					edges.push(edge);
					map.addLayer(edge);

					// updating mapping objects (agencyEdgesMap & agencyEdgesMap).
					if (vertexEdgesMap[vertex1.hashCode]===undefined) vertexEdgesMap[vertex1.hashCode] = [];
					vertexEdgesMap[vertex1.hashCode].push(vertex2);
					vertexEdgesMap[vertex1.hashCode].push(edge);
					
					if (vertexEdgesMap[vertex2.hashCode]===undefined) vertexEdgesMap[vertex2.hashCode]= [];
					vertexEdgesMap[vertex2.hashCode].push(vertex1);
					vertexEdgesMap[vertex2.hashCode].push(edge);
					
					edgeVerticesMap[edge.hashCode] = [];
					edgeVerticesMap[edge.hashCode].push(vertex1);
					edgeVerticesMap[edge.hashCode].push(vertex2);
				}		
				
			});
			
			// Add markers to the map
			$.each(vertices, function(index, agencyShape){
				map.addLayer(agencyShape);
			});
			
			// get agency list
			var agencies = {};
			var sortedAgencyList = []; // used to sort the list of agencies in connectivity graph dialog box
			var html = '';
			
			$.ajax({
				type: 'GET',
				datatype: 'json',
				url: '/TNAtoolAPI-Webapp/queries/transit/allAgencies?&dbindex='+dbindex,
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
				html += '<tr><th><input type="checkbox" class="conGraphCheckBox" id="'+id+'" data-_case="1" onchange="myFunction2(this)"></th>'+
				'<th><input type="checkbox" class="conGraphCheckBox" id="'+id+'" data-_case="2" onchange="myFunction2(this)"></th>'+
				'<th><input type="checkbox" class="conGraphCheckBox" id="'+id+'" data-_case="3" onchange="myFunction2(this)"></th>'+
				'<th nowrap>' + name + '</th></tr>';
				
			};
			html += '</table>';
			
			$( "#con-graph-dialog" ).append(html);
			$('#conGraphPreLoader').hide();
			isConGraphDialogOpen = true;
		}
	});		
}

/*function makeFeatureGroup(agencyCentroids,agencyID){
	var object = agencyCentroids[agencyID];
	var graphObjects = [];
	$.each(object.vertices, function(index, item){
		graphObjects.push(makeMarker(item.lat, item.lng));
	})
	$.each(object.edges, function(index, item){
		graphObjects.push(L.polyline([item[0], item[1]],{color: 'green', opacity:opc0}));
	})
	var shape = L.featureGroup(graphObjects).bindPopup(agencyID);
	shape.coordinates = object.vertices;
	return shape;
}*/


/**
 * Toggles the opacity of the selected edge and both its agencies/vetices.
 * 
 * @param edgeVerticesMap (object)
 * @param edge (object)
 * @param opacity (number)
 */
function toggleEdge2(edgeVerticesMap, e, o, c){
	e.setStyle({opacity:o, color:c});
	$.each(edgeVerticesMap[e.hashCode],function(index, item){
		item.setStyle({fillOpacity:o, fillColor:c});
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
function toggleVertexNeighbors2(agencyEdgesMap, v, o, c){
	v.setStyle({fillOpacity:o, fillColor:c});
	$.each(agencyEdgesMap[v.hashCode],function(index, item){
		if (item instanceof L.Polyline)
			item.setStyle({opacity:o, color:c})
		else if(item instanceof L.CircleMarker)
			item.setStyle({fillOpacity:o, fillColor:c});
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
function toggleVertexConnections2(agencyEdgesMap, edgeVerticesMap, v, o, c){
	v.setStyle({fillOpacity : o, fillColor:c});
	$.each(agencyEdgesMap[v.hashCode], function(index,item){
		if (item instanceof L.Polyline){
			if (item.options.opacity != o){
				item.setStyle({opacity:o, color:c});
				$.each(edgeVerticesMap[item.hashCode], function(index, vertex){
					if (vertex.options.fillOpacity != o){
						vertex.setStyle({fillOpacity:o, fillOpacity:c});
						toggleVertexConnections2(agencyEdgesMap, edgeVerticesMap, vertex, o, c);
					}
				})
			}				
		}
	});
}

/**
 * Makes marker for the connectivity graph report.
 * @param lat
 * @param lng
 * @returns {L.CircleMarker}
 */
function makeMarker2(lat,lng){
	return new L.CircleMarker([lat, lng], {
		radius: 3,
        fillColor: color0,
        color: "#333333",
        weight: 2,
        opacity: 0.5,
        fillOpacity: 0.3,
        /*zIndexOffset: 1000*/
    });
}

/**
 * Loads/toggles the connectivity graph dialog box.
 *//*
function toggleConGraphDialog(){
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
		html = '<table style="font-size: 100%;"><td>Connectivity Gap (miles) </td><td><input id="con-graph-input" value="0.1" style="width:5em"></td></table><br><button onclick="openConGraph()">Submit</button><br>';
		$( "#con-graph-dialog" ).append(html);
		$( "#con-graph-dialog" ).dialog( "open" );
	}
		
	
}*/

/**
 * Returns the constroid of two points
 * @param vertex1
 * @param vertex2
 * @returns {L.LatLng}
 */
function edgeCentroid2(v1,v2){
	var output = {};
	output.lat = (v1.getLatLng().lat + v2.getLatLng().lat) / 2;
	output.lng = (v1.getLatLng().lng + v2.getLatLng().lng) / 2;
	return output;
}

function myFunction2(input){
	var vertex = vertices[input.id];
	var myCase = input.dataset._case;
	var isChecked = input.checked;
	var agencyID = input.dataset.id;
	
	// Uncheck the boxes for the selected agency.
	$.each($( ":input#" + input.id + ".conGraphCheckBox" ), function(index,item){
		if (item.dataset._case != myCase ){
			item.checked = false;
			toggleVertexConnections2(vertexEdgesMap, edgeVerticesMap, vertices[item.id], opc0, color0);			
		}			
	});
	
	if (myCase == 1){
		if (isChecked){
			vertex.setStyle({fillOpacity:opc1, fillColor:color1});
		}else{
			vertex.setStyle({fillOpacity:opc0, fillColor:color0});
		}
	}else if (myCase == 2){
		if (isChecked){
			toggleVertexNeighbors2(vertexEdgesMap, vertex, opc1, color1);
		}else{
			toggleVertexNeighbors2(vertexEdgesMap, vertex, opc0, color0);
		}
	}else if (myCase == 3){
		if (isChecked){
			toggleVertexConnections2(vertexEdgesMap, edgeVerticesMap, vertex, opc1, color1);
		}else{
			toggleVertexConnections2(vertexEdgesMap, edgeVerticesMap, vertex, opc0, color0);
		}
	}
}
