var default_dbindex = getDefaultDbIndex(); //selects the most recent database 
var dbindex = default_dbindex;
var newdates = null;
var maxRadius = 5;

//setting the URL based on selected dates
var key="--"; //used for saving or loading selected dates
var URL = document.URL;
if (URL.split("?").length >0){
	URL = URL.split("?")[0];
	if (document.URL.indexOf("n=")<1){
		URL +="?&n="+key;
	} else {
		var index = (document.URL.split("n=")[1].indexOf("&")>0) ? document.URL.split("n=")[1].indexOf("&"):document.URL.split("n=")[1].length;
		key = document.URL.split("n=")[1].substr(0,index);
		URL +="?&n="+key;
	}
	if (document.URL.indexOf("dbindex=")<1){
		URL +="&dbindex="+dbindex;
	}else {
		var index = (document.URL.split("dbindex=")[1].indexOf("&")>0) ? document.URL.split("dbindex=")[1].indexOf("&"):document.URL.split("dbindex=")[1].length;
		dbindex = parseFloat(document.URL.split("dbindex=")[1].substr(0,index));
		URL +="&dbindex="+dbindex;
	}
} else {
	URL +="?&n="+key+"&dbindex="+dbindex;
}
history.pushState('data', '', URL);
var w_qstringd = getDates(key);
//end

//starts initializing the map
var map = new L.Map('map', {	
	minZoom : 6,
	maxZoom : 19	
});

var OSMURL    = "http://{s}.mqcdn.com/tiles/1.0.0/osm/{z}/{x}/{y}.png";
var aerialURL = "http://{s}.mqcdn.com/tiles/1.0.0/sat/{z}/{x}/{y}.png";
var tonerMap = new L.StamenTileLayer("toner");
var terrainMap = new L.StamenTileLayer("terrain");

var osmAttrib = 'Map by &copy; <a href="http://osm.org/copyright" target="_blank">OpenStreetMap</a> contributors'+' | Census & shapes by &copy; <a href="http://www.census.gov" target="_blank">US Census Bureau</a> 2010 | <a href="https://github.com/tnatool/beta" target="_blank">TNA Software Tool</a> '+getVersion()+' beta';
var osmLayer = new L.TileLayer(OSMURL, 
		{subdomains: ["otile1-s","otile2-s","otile3-s","otile4-s"], maxZoom: 19, attribution: osmAttrib});

/*var aerialLayer = new L.TileLayer(aerialURL, 
		{subdomains: ["oatile1","oatile2","oatile3","oatile4"], maxZoom: 19, attribution: osmAttrib});*/

map.addLayer(terrainMap);
$("body").css("display","");
$("#overlay").show();
//end

///******Variables declared for the on-Map connected agencies report ********///
var connectionMarkers = new L.FeatureGroup();
var connectionPolylines = new L.FeatureGroup();
var gap=0.1;
var selectedAgency;
var selectedAgencies=Array();
var polylines = Array();
var stopsCluster;
var text='';
map.addLayer(connectionMarkers);
map.addLayer(connectionPolylines);

///*********Beginning of on-Map connected agencies report*******///
var dialog2=$("#connectedAgencies-form").dialog({
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
    close: function() {
    	//miniMap._restore();
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
    	//miniMap._minimize(); 
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

function loadDialog2(node){
	$mylist.dialogExtend("collapse");
    dialog2.dialog( "open" );
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
			text = data.agency;
			$('#dialogSelectedAgency').html(data.agency);
    		$('#dialogNoOfConnectedAgencies').html(data.ClusterR.length);
			html = '<table id="connectedAgenciesTable" class="display" align="center">';
			var tmp = 	'<th>Agency ID </th>'+
            			'<th>Agency Name</th>'+
            			'<th>Number of Connections</th></tr>';	
			html += '<thead>'+tmp+'</thead><tbody>';
			//var html2 = '<tfoot>'+tmp+'</tfoot>';
			
			for (var i = 0; i < data.ClusterR.length; i++) {
				html += '<td>'+ data.ClusterR[i].id +'</td>'+
				'<td>'+ data.ClusterR[i].name +'</td>'+
				'<td>'+ data.ClusterR[i].size +'</td></tr>';
			}
			
			html += '</tbody></table>';
			$('#displayConAgenciesTable').append(html);
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
		    connectedAgenciesTable.$('tr').click( function () {
		    	if($(this).hasClass('selected')){
		    		connectionMarkers.removeLayer(connectionsClusters[$(this).index()]);
		    		connectionPolylines.removeLayer(polylines[$(this).index()]);
		    		// remove the selected agency from the list of agencies.
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
    									'<b>Stop Name: </b>'+item.name);
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
						var marker = new L.marker([item.lat,item.lon] /*,{icon: onMapIcon})*/).on('click',onMarkerClick);
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

function onMarkerClick(){
	var id = this._leaflet_id;
	
	/*
	 * A shallow copy of selectedAgencies. This array is trimmed based on whether a stop
	 * of the original agency is clicked or a stops for one of the agencies in the list.
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
						var destMarker = new L.marker([item.lat,item.lon] /*,{className: 'ycluster', iconSize: new L.Point(10, 10)}).on('click',onClick*/);
						bounds.push(destMarker.getLatLng());
						latlngs.push(sourceMarker.getLatLng());
						latlngs.push(destMarker.getLatLng());
						var polyline = L.polyline(latlngs, {color: color});
						tmpConnectionsPolylines.addLayer(polyline);
					}		
				});
				polylines[id] = tmpConnectionsPolylines;
				connectionPolylines.addLayer(polylines[id]);
				dialog2.dialogExtend("minimize");
				map.fitBounds(bounds);
			}
		});
//		selectedAgencies.push(this.agencyId);
	}else{
		this.closePopup();
		connectionPolylines.removeLayer(polylines[id]);
		delete polylines[id];
	}
}
function reloadDialog2(input){
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
	loadDialog2(selectedAgency);
}

function isNormalInteger(str) {
    var n = ~~Number(str);
    return String(n) === str && n >= 0;
}

///*****************Leaflet Draw******************///
var dialogheight = Math.round((window.innerHeight)*.9); 
if (dialogheight > 1000){
	dialogheight = 1000;
}
if (dialogheight < 400){
	dialogheight = 400;
}
var dialogAgencies = new Array();
var dialogAgenciesId = new Array();
var dialog = $( "#dialog-form" ).dialog({
    autoOpen: false,
    height: dialogheight,
    width: 500,
    modal: false,
    draggable: false,
    resizable: false,
    closeOnEscape: false,
    position: { my: "right top", at: "right-50 top", of: window },    
    buttons: {
        },
    close: function() {
    	//miniMap._restore();
    	map.removeLayer(onMapCluster);
      },
    open: function( event, ui ) {
    	//miniMap._minimize();
    	dialog2.dialog('close');
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

// the blocks legend
var blockDensityValue;
var newDensityValue;
(function () {
	var html = $('#blocksLengend');		
    var grades = [-1, 0, 20, 100, 500, 1000, 5000, 10000];	
    // loop through our density intervals and generate a label with a colored square for each interval		
    html =  '<p id="legendFirstP"><input type="checkbox" id="blockSvc" >Level of Service</p>'+
    		'<p id="legendSelect" style="display: none;">Search Radius <select onchange="legendSelectChange(this)">'+
    		'<option value="0.1" selected>0.1</option>'+
    		'<option value="0.25">0.25</option>'+
    		'<option value="0.5">0.5</option>'+
    		'<option value="1">1</option>'+
    		'</select> mi</p>'+
    		'<p style="margin-top: 0.5em;"><input type="radio" name="blocksDensity" value="pop" checked>Population Density</p>'+
    		'<p><input type="radio" name="blocksDensity" value="rac">Employment Density</p>'+
    		'<p><input type="radio" name="blocksDensity" value="wac">Employee Density</p></br>';	
    html+='<div id="blockLegendDiv">';
    for (var i = 0; i < grades.length; i++) {		
        html += '<i style="background:' + getColorBlocks(grades[i] + 1) + '"></i> ';
        if(i==0){
        	html +=  '0<br>';
        }else if(i==1){
        	html += "0.01"+ (grades[i + 1] ? '&ndash;' + grades[i + 1] + '<br>' : '+');	
        }else{
        	html += grades[i] + (grades[i + 1] ? '&ndash;' + grades[i + 1] + '<br>' : '+');		
    	}
    }		
    html+='</div>';
    $('#blocksLengend').html(html);
    
    $('#blocksLengend > p').css("margin-bottom","0px");
    $('#legendFirstP').css("margin-bottom","10px");
    $('#blocksLengend > p > input').css({"vertical-align":"text-bottom","margin-right":"3px"});
    
    $('#blocksLengend').hide();
    
    blockDensityValue = "pop";
    
    
    $( "#blockSvc" ).change(function() {
    	var b = this.checked;
    	if(b){
    		$('#legendSelect').show();
    		newDensityValue = blockDensityValue;
    	}else{
    		$('#legendSelect').hide();
    	}
    	
    	blockDensityValue = "svc";
		changeSvc(b,newDensityValue);
	});
    
    $( "input[name='blocksDensity']" ).each(function() {
    	  $( this ).click(function() {
    		  //alert( $( this ).val() );
    		  //blockDensityValue = $( this ).val();
    		  changeDensityStyle($( this ).val());
    	  });
    });
})();

var drawnItems = new L.FeatureGroup();
map.addLayer(drawnItems);

var drawControl = new L.Control.Draw({
	draw: {
		polyline: false,
		polygon: {
			metric: false,
			allowIntersection: false,
			showArea: false,
			drawError: {
				color: '#b00b00',
				timeout: 1000
			},
			shapeOptions: {
				color: 'blue'
			}
		},
		circle: {
			metric: false,
			shapeOptions: {
				color: '#662d91'
			}
		},
		marker: false
	},
	edit: {
		featureGroup: drawnItems,
	}
});
map.addControl(drawControl);

map.on('draw:drawstart', function (e) {
	$('.jstree-checked').each(function() {
		$( this ).children('a').children('.jstree-checkbox').click();
	});
	$mylist.dialogExtend("collapse");
	drawnItems.clearLayers();
	dialog.dialog( "close" );
	dialog2.dialog( "close" );
});

$('.leaflet-draw-edit-remove').click(function(event){
	drawnItems.clearLayers();
	dialog.dialog( "close" );
});

var getCentroid = function (arrr) { 
	var arr = new Array();
	for(var i=0;i<arrr.length;i++){
		var tmpP = [arrr[i].lat,arrr[i].lng];
		arr[i]=tmpP;
	}
    return arr.reduce(function (x,y) {
        return [x[0] + y[0]/arr.length, x[1] + y[1]/arr.length]; 
    }, [0,0]); 
};
var drawCentroid = [0,0];
var area=0;
var popX=0;
var currentLayer;
var currentCircleCenter;
var currentCircleCenterTmp;
var currentDate = new Date();
var currentX=0;
var currentLats;
var currentLngs;
function editCancel(){
	$('#circleRadius1').css('visibility','hidden');
	currentCircleCenterTmp=currentCircleCenter;
}
function pad(s) { return (s < 10) ? '0' + s : s; }

map.on('draw:created', function (e) {
	currentLats = new Array();
	currentLngs = new Array();
	type = e.layerType,
	layer = e.layer;
	currentLayer = layer;
	map.fitBounds(layer.getBounds().pad(0.5));
	var radius='';
	if (type === 'circle') {		
		currentCircleCenter = layer.getLatLng();
		currentCircleCenterTmp= layer.getLatLng();
		drawCentroid[0] = layer.getLatLng().lat;
		drawCentroid[1] = layer.getLatLng().lng;
		currentLats.push((Math.round(drawCentroid[0] * 1000000) / 1000000).toString());
		currentLngs.push((Math.round(drawCentroid[1] * 1000000) / 1000000).toString());
		currentX = layer.getRadius();		
		area = Math.pow(layer.getRadius()*0.000621371,2)*Math.PI;
		radius='<p><b>Radius:</b> <input type="number" value="' + (currentX*0.000621371).toFixed(2) + '" min="0.01" step="0.01" id="POPradius"> miles</p>';
	}else{
		area = L.GeometryUtil.geodesicArea(layer.getLatLngs())*0.000000386102;
		drawCentroid = getCentroid(layer.getLatLngs());
		var tmpPoints = layer.getLatLngs();
		for(var ii=0; ii<tmpPoints.length; ii++){
			currentLats.push((Math.round(tmpPoints[ii].lat * 1000000) / 1000000).toString());
			currentLngs.push((Math.round(tmpPoints[ii].lng * 1000000) / 1000000).toString());
		}		
	}
	drawnItems.addLayer(layer);
	
	drawCentroid[0]= (Math.round(drawCentroid[0] * 1000000) / 1000000).toString();
	drawCentroid[1]= (Math.round(drawCentroid[1] * 1000000) / 1000000).toString();
	area = Math.round(area * 100) / 100;
	layer.on('popupopen', function(e) {
		
		$( "#POPdatepicker" ).datepicker({
		    showButtonPanel: true,
			onSelect: function (date) {
				currentDate = date;				
		    }
		});
		$("#POPdatepicker").datepicker( "setDate", new Date());
		var d = new Date();
		currentDate = [pad(d.getMonth()+1), pad(d.getDate()), d.getFullYear()].join('/');
		$('.leaflet-popup-content-wrapper').css('opacity','0.75');
		$('.leaflet-popup-close-button').css({'color':'#9B9A9A','z-index':'1'});
	});
	layer.bindPopup(
			'<p><b>Centroid:</b><br>'+
			'<span style="padding-left:1em">Latitude: <span id="POPlat" style="padding-left:1.5em">'+drawCentroid[0]+'</span></span><br>'+
	    	'<span style="padding-left:1em">Longitude: <span id="POPlon">'+drawCentroid[1]+'</span></span>'+
	    	radius+
			'<p><b>Area:</b> <span id="POParea">'+area+'</span> mi<sup>2</sup></p>'+
			'<p><b>Date</b>: <input readonly type="text" class="POPcal" id="POPdatepicker"></p>'+
			'<p><button type="button" style="width:100%" id="POPbutton" onclick="onMapSubmit()">Generate Report</button></p>'
	,{closeOnClick:false,draggable:true}).openPopup();		
});
map.on('popupopen',function (e) {
	try{
		
		// Checks to see if the popup is for a circle drawn around a stop or a coordinate.
		if (e.popup._content.indexOf('POPradius')>-1){
			
			// updates the radius and area on the popup based on the current size of the circle
			$('#POPradius').html(layer.getRadius()/1609.344);
			$('#POParea').html((Math.pow(layer.getRadius()*0.000621371,2)*Math.PI).toFixed(2));
			
			// Changes the radius of the drawn circle by changing the radius in the popup and current radius update.
			$('#POPradius').on('keyup change wheel', function(e) {
				layer.setRadius(this.value*1609.344);		
				currentX = this.value*1609.344;
				area = (Math.pow(layer.getRadius()*0.000621371,2)*Math.PI).toFixed(2);
				$('#POParea').html(area);
				//alert();
				//layer.pop
			});
		}
	}catch (e) {
		console.log(e);
	}		
});
setDialog();
function circleMove(latlng){
	currentCircleCenterTmp = latlng;
}
function circleResize(radius){	
	radius = Math.round(radius*0.0621371)/100;
	$('#circleRadius1').css('visibility','visible');
	$('#circleRadius2').html(radius);	
}

map.on('draw:editstart', function (e) {
	currentLayer.closePopup();
	dialog.dialog( "close" );
});
map.on('draw:edited', function (e) {
	$('#circleRadius1').css('visibility','hidden');
	var layers = e.layers;
    layers.eachLayer(function (layer) {
    	currentLats = new Array();
		currentLngs = new Array();
    	map.fitBounds(layer.getBounds().pad(0.5));
        try{
        	drawCentroid[0] = layer.getLatLng().lat;
    		drawCentroid[1] = layer.getLatLng().lng;
    		currentX = layer.getRadius();    		
    		currentLats.push((Math.round(drawCentroid[0] * 1000000) / 1000000).toString());
    		currentLngs.push((Math.round(drawCentroid[1] * 1000000) / 1000000).toString());
    		area = Math.pow(layer.getRadius()*0.000621371,2)*Math.PI;
    		currentCircleCenter = layer.getLatLng();
    		currentCircleCenterTmp= layer.getLatLng();    	    			
        }catch(err){
        	area = L.GeometryUtil.geodesicArea(layer.getLatLngs())*0.000000386102;
    		drawCentroid = getCentroid(layer.getLatLngs());
    		var tmpPoints = layer.getLatLngs();
    		for(var ii=0; ii<tmpPoints.length; ii++){
    			currentLats.push((Math.round(tmpPoints[ii].lat * 1000000) / 1000000).toString());
    			currentLngs.push((Math.round(tmpPoints[ii].lng * 1000000) / 1000000).toString());
    		}
        }
        drawCentroid[0]= (Math.round(drawCentroid[0] * 1000000) / 1000000).toString();
    	drawCentroid[1]= (Math.round(drawCentroid[1] * 1000000) / 1000000).toString();
    	area = Math.round(area * 100) / 100;
    	layer.openPopup();
    	try{
    		$('#POPradius').val((currentX*0.000621371).toFixed(2));
    	}catch (e) {
		}
    	$('#POPlat').html(drawCentroid[0]);
    	$('#POPlon').html(drawCentroid[1]);
    	$('#POParea').html(area);
    	
    });	
	dialog.dialog( "close" );	
}); 
////////*************************************/////////////////////
//var Layers = 0;
var colorIndex;
function getdata(type,agency,route,variant,k,callback,popup,node) {	
	switch (type){
	case 1:
		var points = [];
		$.ajax({
			type: 'GET',
			datatype: 'json',
			url: '/TNAtoolAPI-Webapp/queries/transit/stops?&agency='+agency+"&dbindex="+dbindex,
			success: function(d){		
			$.each(d.stops, function(i,stop){        	
				points.push([new L.LatLng(Number(stop.lat), Number(stop.lon)),stop.StopName]);							
	        });				
			if (points.length!=0) callback("A"+agency,k,points,popup,node);
	    }});
		break;
	case 2:
		var points = [];
		$.ajax({
			type: 'GET',
			datatype: 'json',
			url: '/TNAtoolAPI-Webapp/queries/transit/stopsbyroute?&agency='+agency+'&route='+route+"&dbindex="+dbindex,
			success: function(d){		
			$.each(d.stops, function(i,stop){        	
				points.push([new L.LatLng(Number(stop.lat), Number(stop.lon)),stop.stopName]);				 			
	        });				
			if (points.length!=0) callback("R"+agency+route,k,points,popup,node);
	    }});
		break;
	case 3:	
		$.ajax({
			type: 'GET',
			datatype: 'json',
			url: '/TNAtoolAPI-Webapp/queries/transit/shape?&agency='+agency+'&trip='+variant+"&dbindex="+dbindex,
			success: function(d){
			if (d.points!= null) callback(k,d,"V"+agency+route+variant,node);
	    }});
		break;
	}		
};

var info = L.control();
info.onAdd = function (map) {
    this._div = L.DomUtil.create('div', 'info'); // create a div with a class "info"
    this.update();
    return this._div;
};

info.update = function (props) {
    this._div.innerHTML = (props ?

        '<div id="box"><b>Name: </b>' + props.name + ' <br/>' + '<b>Area: </b>'+ props.area + ' mi<sup>2</sup><br/><b>Population (2010): </b>'+ props.population+'</div>' 
        :'');
};

var legend = L.control({position: 'bottomright'});		
legend.onAdd = function (map) {		
    var div = L.DomUtil.create('div', 'legend'),		
        grades = [0, 10, 20, 50, 100, 200, 500, 1000];		
    // loop through our density intervals and generate a label with a colored square for each interval		
    div.innerHTML = '<p>Population Density</p>';		
    for (var i = 0; i < grades.length; i++) {		
        div.innerHTML +=		
            '<i style="background:' + getColor(grades[i] + 1) + '"></i> ' +		
            grades[i] + (grades[i + 1] ? '&ndash;' + grades[i + 1] + '<br>' : '+');		
    }		
    return div;		
};

function style(feature) {
    return {
        fillColor: getColor(feature.properties.density),
        weight: 0.5,
        opacity: 1,
        color: 'white',
        dashArray: '',
        fillOpacity: 0.5
    };
}
function styleU(feature) {
    return {
        fillColor: 'red',
        weight: 2,
        opacity: 1,
        color: 'black',
        dashArray: '',
        fillOpacity: 0.4
    };
}
function zoomToFeature(e) {
    map.fitBounds(e.target.getBounds());
}
function resetHighlight(e) {	
	var layer = e.target;
    layer.setStyle({              
        fillOpacity: 0.5,
        weight: 0.5,		
        dashArray: '3',
    });
    info.update();
}
function highlightFeature(e) {
    var layer = e.target;
    layer.setStyle({              
        fillOpacity: 0.6,
        weight: 2,		
        opacity: 1,		
        color: 'white',		
        dashArray: '',
    });    
    info.update(layer.feature.properties);    
}
function resetHighlightU(e) {	
	var layer = e.target;
    layer.setStyle({              
        fillOpacity: 0.4,
        color: 'black',
    });
    info.update();
}
function onEachFeatureU(feature, layer) {
    layer.on({
        mouseover: highlightFeature,
        mouseout: resetHighlightU,
        click: zoomToFeature        
    });
}
function onEachFeature(feature, layer) {
    layer.on({
        mouseover: highlightFeature,
        mouseout: resetHighlight,
        click: zoomToFeature        
    });
}
var stops = new L.LayerGroup().addTo(map);
var routes = new L.LayerGroup().addTo(map);

var county = L.geoJson(countyshape, {style: style, onEachFeature: onEachFeature});
var tract = L.geoJson(tractshape, {style: style, onEachFeature: onEachFeature});
var odot = L.geoJson(odotregionshape, {style: style, onEachFeature: onEachFeature}); 
var congdist = L.geoJson(congdistshape, {style: style, onEachFeature: onEachFeature});
var urban50k = L.geoJson(urban50shapes, {style: styleU, onEachFeature: onEachFeatureU});
var urban25k = L.geoJson(urban25shapes, {style: styleU, onEachFeature: onEachFeatureU});
function getColor(d) {
    return d > 1000 ? '#800026' :
           d > 500  ? '#BD0026' :
           d > 200  ? '#E31A1C' :
           d > 100  ? '#FC4E2A' :
           d > 50   ? '#FD8D3C' :
           d > 20   ? '#FEB24C' :
           d > 10   ? '#FED976' :
                      '#FFEDA0';
}
function getColorBlocks(d) {
    return d > 10000 ? '#800026' :
           d > 5000  ? '#BD0026' :
           d > 1000  ? '#E31A1C' :
           d > 500   ? '#FC4E2A' :
           d > 100   ? '#FD8D3C' :
           d > 20    ? '#FEB24C' :
           d > 0     ? '#FED976' :
                       '#FFEDA0';
}
var colorset = ["#6ECC39","#FF33FF","#006DFF","#FE0A0A", "#7A00F5", "#CC6600"];
function disponmap2(layerid,k,points,popup){	
	var geojsonMarkerOptions = {
		    radius: 5,
		    fillColor: colorset[k],
		    color: "#000",
		    weight: 1,
		    opacity: 1,
		    fillOpacity: 0.6
		};
	var geojsonFeature = {
		    "type": "Feature",
		    "properties": {
		        "name": "stop",
		        "popupContent": popup
		    },
		    "geometry": {
		        "type": "MultiPoint",
		        "coordinates": points
		    }
		};
	
	function onEachFeature(feature, layer) {
		var popupContent = "";

		if (feature.properties && feature.properties.popupContent) {
			popupContent += feature.properties.popupContent;
		}

		layer.bindPopup(popupContent);
	}
	var mylayer = new L.geoJson(geojsonFeature, {
	    pointToLayer: function (feature, latlng) {
	        return L.circleMarker(latlng, geojsonMarkerOptions);
	    },
	    onEachFeature: onEachFeature
	});
	
	mylayer._leaflet_id = layerid;	
	stops.addLayer(mylayer);	
};
var popupOptions = {'offset': L.point(0, -8),
		'closeOnClick':false,
		'draggable':true
		};
function disponmap(layerid,k,points,popup,node){
	
	var markers;
	switch (k){
	case 0:
		markers = new L.MarkerClusterGroup({
			maxClusterRadius: 120,
			iconCreateFunction: function (cluster) {
			return new L.DivIcon({ html: cluster.getChildCount(), className: 'gcluster', iconSize: new L.Point(30, 30) });
		},
		spiderfyOnMaxZoom: true, showCoverageOnHover: true, zoomToBoundsOnClick: true, singleMarkerMode: false, maxClusterRadius: 30
	});
	break;
	case 1:
		markers = new L.MarkerClusterGroup({
			maxClusterRadius: 120,
			iconCreateFunction: function (cluster) {
				return new L.DivIcon({ html: cluster.getChildCount(), className: 'picluster', iconSize: new L.Point(30, 30) });
			},
			spiderfyOnMaxZoom: true, showCoverageOnHover: true, zoomToBoundsOnClick: true, singleMarkerMode: true, maxClusterRadius: 30
		});
		break;
	case 2:
		markers = new L.MarkerClusterGroup({
			maxClusterRadius: 120,
			iconCreateFunction: function (cluster) {
				return new L.DivIcon({ html: cluster.getChildCount(), className: 'ccluster', iconSize: new L.Point(30, 30) });
			},
			spiderfyOnMaxZoom: true, showCoverageOnHover: true, zoomToBoundsOnClick: true, singleMarkerMode: true, maxClusterRadius: 30
		});
		break;
	case 3:
		markers = new L.MarkerClusterGroup({
			maxClusterRadius: 120,
			iconCreateFunction: function (cluster) {
				return new L.DivIcon({ html: cluster.getChildCount(), className: 'rcluster', iconSize: new L.Point(30, 30) });
			},
			spiderfyOnMaxZoom: true, showCoverageOnHover: true, zoomToBoundsOnClick: true, singleMarkerMode: true, maxClusterRadius: 30
		});
		break;
	case 4:
		markers = new L.MarkerClusterGroup({
			maxClusterRadius: 120,
			iconCreateFunction: function (cluster) {
				return new L.DivIcon({ html: cluster.getChildCount(), className: 'pucluster', iconSize: new L.Point(30, 30) });
			},
			spiderfyOnMaxZoom: true, showCoverageOnHover: true, zoomToBoundsOnClick: true, singleMarkerMode: true, maxClusterRadius: 30
		});
		break;
	default:
		markers = new L.MarkerClusterGroup({
			maxClusterRadius: 120,
			iconCreateFunction: function (cluster) {
				return new L.DivIcon({ html: cluster.getChildCount(), className: 'brcluster', iconSize: new L.Point(30, 30) });
			},
			spiderfyOnMaxZoom: true, showCoverageOnHover: true, zoomToBoundsOnClick: true, singleMarkerMode: true, maxClusterRadius: 30
		});
		//break;
	}	
	for (var i = 0; i < points.length; i++) {
		var p = points[i];
		//var marker = new L.Marker(p[0],{title:popup});
		var marker = new L.CircleMarker(p[0], {		
			title: popup,		
	        radius: 8,		
	        fillColor: colorset[k],		
	        color: "#333333",		
	        weight: 2,		
	        opacity: 1,		
	        fillOpacity: 0.8,		
	    });
		var marLat = (Math.round(marker.getLatLng().lat * 1000000) / 1000000).toString().replace('.','').replace('-','');
		var marLng = (Math.round(marker.getLatLng().lng * 1000000) / 1000000).toString().replace('.','').replace('-','');
		marker.on('popupopen', function(e) {
			dialog.dialog( "close" );
			var markerLat = (Math.round(this.getLatLng().lat * 1000000) / 1000000).toString().replace('.','').replace('-','');
			var markerLng = (Math.round(this.getLatLng().lng * 1000000) / 1000000).toString().replace('.','').replace('-','');
			$( '#'+markerLat+'POPdatepicker'+markerLng).datepicker({
				showButtonPanel: true,
				onSelect: function (date) {
					currentDate = date;
					}
			});
			$('#'+markerLat+'POPdatepicker'+markerLng).datepicker( "setDate", new Date());
			var d = new Date();
			currentDate = [pad(d.getMonth()+1), pad(d.getDate()), d.getFullYear()].join('/');			
			$('.leaflet-popup-content-wrapper').css('opacity','0.80');
			$('.leaflet-popup-close-button').css({'color':'#9B9A9A','z-index':'1'});
		});
		marker.bindPopup(
				'<p><b>'+p[1]+'</b></p>'+
				'<p><b>Location:</b><br>'+
				'<span style="padding-left:1em">Latitude: <span style="padding-left:1.5em">'+p[0].lat+'</span></span><br>'+
		    	'<span style="padding-left:1em">Longitude: <span>'+p[0].lng+'</span></span>'+
				'<p><b>Date:</b> <input readonly type="text" class="POPcal" id="'+marLat+'POPdatepicker'+marLng+'"></p>'+
				'<p><b>Population Search Radius (miles):</b> <input type="text" value="0.25" id="'+marLat+'POPx'+marLng+'" style="width:40px"></p>'+
				'<p><button type="button" id="'+marLat+'POPbutton'+marLng+'" style="width:100%" onclick="onMapBeforeSubmit('+p[0].lat+','+p[0].lng+','+marLat+','+marLng+')">Generate Report</button></p>'+
				'<p><button type="button" style="width:100%" id="'+marLat+'streetViewButton" onclick="openStreetView('+p[0].lat+','+p[0].lng+')">Open Street View</button></p>'				
				,popupOptions);
		markers.addLayer(marker);
	}
	markers._leaflet_id = layerid;
	stops.addLayer(markers);
	$.jstree._reference($mylist).set_type("default", $(node));
}

function onMapBeforeSubmit(lat,lng,mlat,mlng){
	var x = $('#'+mlat+'POPx'+mlng).val();
	if(isNaN(x)||x<=0){
		alert('Please enter a valid radius');
		return false;
	}
	drawCentroid[0]= lat;
	drawCentroid[1]= lng;	
	area = Math.pow(x,2)*Math.PI;
	drawCentroid[0]= (Math.round(drawCentroid[0] * 1000000) / 1000000).toString();
	drawCentroid[1]= (Math.round(drawCentroid[1] * 1000000) / 1000000).toString();
	area = Math.round(area * 100) / 100;
	var that = drawControl._toolbars[L.DrawToolbar.TYPE]._modes.circle.handler;
	that.enable();
	that._startLatLng = [lat,lng];
	that._shape = new L.Circle([lat,lng], x*1609.34, that.options.shapeOptions);
	that._map.addLayer(that._shape);	
	that._fireCreatedEvent();
	that.disable();	
	
	onMapSubmit();
}

var maxFreq;		
var zoomToRouteShapeFlag = true;		
var frequencyFlag = true;		
$('.jstree-checkbox').click(function(){		
	zoomToRouteShapeFlag = true;		
});		
function scaledFreq(freq){		
	var weight = Math.ceil(Math.log2(freq));		
	if(weight==1){		
		return 2;		
	}else{		
		return weight;		
	}		
}

function dispronmap(k,d,name,node){	
	var popHtml = '<b>Agency Name:</b> '+d.agencyName+ '<br><b>Agency ID:</b> '+d.agency+'<br><b>Route Head Sign:</b> '+d.headSign;
	var polyline;
	if(frequencyFlag){
		var freq = node.attr("freq");
		popHtml+='<br><b>Frequency:</b> '+freq;
		if(freq==1){
			freq++;
		}
		polyline = L.Polyline.fromEncoded(d.points, {	
			weight: scaledFreq(freq),
			color: colorset[k],
			//color: "#006DFF",
			opacity: .5,
			smoothFactor: 9
		});	
		if(zoomToRouteShapeFlag){
			map.fitBounds(polyline.getBounds());
		}
		polyline.bindPopup(popHtml,{autoPan:false});
		
		polyline.on("mouseover", function (e) {
			polyline.setStyle({opacity: .9, color: "#B50045"});
			polyline.openPopup();
		});
		
		polyline.on("mouseout", function (e) {
			polyline.setStyle({opacity: .5, color: colorset[k]});
			polyline.closePopup();
		});
	}else{
		polyline = L.Polyline.fromEncoded(d.points, {	
			weight: 5,
			color: colorset[(k+Math.floor(Math.random() * 6))%6],
			opacity: .5,
			smoothFactor: 9
		});	
		polyline.bindPopup(popHtml);
	}
	polyline._leaflet_id = name;	
	routes.addLayer(polyline);
	$.jstree._reference($mylist).set_type("default", $(node));
};
var dateID;
function addDate(date){
	$( "<li title='Click to remove.' id="+dateID+" onclick=\"dateRemove(this, '"+date+"')\">"+Date.parse(date).toString('dddd, MMMM d, yyyy')+"</li>" ).appendTo( "#datesarea" );
	$("#"+dateID).css({"text-align":"center","border":"1px solid black","padding":"1px 5px 1px 5px", "font-size":"82%","display":"block","width":"90%","background-color":"grey","text-decoration":"none","color":"white","margin":"3px","border-radius":"5px"});
	$("#"+dateID).hover(function(){
		  $(this).css({"cursor":"pointer","-moz-transform":"scale(1.1,1.1)","-webkit-transform":"scale(1.1,1.1)","transform":"scale(1.1,1.1)"});
	},function(){
		  $(this).css({"cursor":"pointer","-moz-transform":"scale(1,1)","-webkit-transform":"scale(1,1)","transform":"scale(1,1)"});
	});
	if ($('#datepicker').multiDatesPicker('getDates').length>0){
		$("#datepick").css({"border":"2px solid #900"});
		};
};
function dateRemove(e, d){
	$(e).remove();
	$("#datepicker").multiDatesPicker('removeDates', d);	 
	if ($('#datepicker').multiDatesPicker('getDates').length==0){
			$("#datepick").css({"border":""});						
		}
}

var INIT_LOCATION = new L.LatLng(44.141894, -121.783660); 
var initLocation = INIT_LOCATION;

map.setView(initLocation,8);
var visible = true;
var ggl = new L.Google();
$('.leaflet-control-zoom').css('margin-top','50px');

L.control.scale({'metric': false, 'position': 'bottomright', 'maxWidth':200}).addTo(map);

var searchControl = L.Control.geocoder().addTo(map);
/*new L.Control.GeoSearch({
    provider: new L.GeoSearch.Provider.OpenStreetMap()
}).addTo(map);*/
map.on('click', function(){
	if (searchControl._geocodeMarker) {
		searchControl._map.removeLayer(searchControl._geocodeMarker);		
	}
});

/*function myFunction(){
	alert('myFunction ran');
}*/

osmLayer.on('load', function(e) {
	ggb = false;    
});
tonerMap.on('load', function(e) {
	ggb = false;	
});
terrainMap.on('load', function(e) {
	ggb = false;	
});
var mmRecLat = 0;
var mmRecLng = 0;

//var miniMap = new L.Control.MiniMap(new L.TileLayer(OSMURL, {subdomains: ["otile1","otile2","otile3","otile4"], minZoom: 5, maxZoom: 5, attribution: osmAttrib}),{position:'bottomright',toggleDisplay:true}).addTo(map);
$('.leaflet-control-scale-line').css({'border':'2px solid grey','line-height':'1.2','margin-left':'0px'});
var menucontent = '<ul id="rmenu1" class="dropdown-menu" role="menu" aria-labelledby="drop4">';
$.ajax({
	type: 'GET',
	datatype: 'json',
	url: '/TNAtoolAPI-Webapp/queries/transit/DBList',
	async: false,
	success: function(d){
		var menusize = 0;
	    $.each(d.DBelement, function(i,item){
	    	menucontent+='<li role="presentation"><a id="DB'+i+'" href="#">'+item+'</a></li>';
	    	menusize++;
	    });
	    menucontent+='</ul>';
	    if (dbindex<0 || dbindex>menusize-1){
	    	dbindex = default_dbindex;
	    	history.pushState('data', '', document.URL.split("?")[0]+"?&n="+key+'&dbindex='+default_dbindex);	    	
	    }
	}			
});
var baseMaps = {
	    "OSM": terrainMap,
	    "Toner": tonerMap,
	    "Google Aerial":ggl,
	    //"Aerial Photo": aerialLayer
	};
		        
var overlayMaps = {
		"Stops": stops,
		"Routes": routes,
		"Counties": county,
		"Tracts": tract,
		"ODOT Transit Regions": odot,
		"Congressional Districts": congdist,
		"Urbanized Areas": urban50k,
		"Urban Clusters 25k+": urban25k
	};

map.addControl(new L.Control.Layers(baseMaps,overlayMaps));
info.addTo(map);
var overlaysLayers = 0;		
for(var i=3;i<=6;i++){		
	$('div.leaflet-control-layers-overlays > label:nth-child('+i+') > input').change(function() {		
		if($(this).is(":checked")) {		
			if(overlaysLayers==0){		
				legend.addTo(map);		
			}		
			overlaysLayers++;		
		}else{		
			if(overlaysLayers==1){		
				legend.removeFrom(map);		
			}		
			overlaysLayers--;		
		}		
	});		
}
var $mylist = $("#list");
var popYear = 2010;
$mylist
.jstree({
	"checkbox": {        
        two_state: true,
        real_checkboxes: false,
        override_ui:false
     },
	"json_data" : {
		"ajax" : {
            "url" : "/TNAtoolAPI-Webapp/queries/transit/menu?day="+w_qstringd+"&dbindex="+dbindex+'&username='+getSession(),
            "type" : "get",	                
            "success" : function(ops) {  
            	
            	try {
            		$.each(ops.data, function(i,item){
                		dialogAgencies.push(item.data);
                		dialogAgenciesId.push(item.attr.id);
                	});
            		maxFreq = ops.maxFreq;		
            		if(w_qstringd==null){		
            			frequencyFlag=false;		
            		}
            	}
            	catch(err) {
            		console.log("error in /menu ajax");
            	}
            	$("#overlay").hide();
            	return ops.data;            	
            }    	               
        },
        "progressive_render" : true       
	},
	"types" : {
		"types" : {
			"default": {	
				"icon" : {
	            	"image" : "resources/images/spacer.png"
	            	},
            	"select_node" : false,
            	"check_node" : true, 
                "uncheck_node" : true,                
                "open_node" :true,
                "hover_node" : true
			},
			"disabled" : {
				"icon" : {
	            	"image" : "resources/images/loader.png"
	            	},
	            "check_node" : false, 
	            "uncheck_node" : false,
	            "select_node" : false,
	            "open_node" :false,
	            "hover_node" : false
	          }
		}
	},
	"themes": {
        "theme": "default-rtl",
        "url": "vendors/jstree-v.pre1.0/themes/default-rtl/style.css",
        "dots": false,
        "icons":true
    },
    "contextmenu" : {
        "items" : function (node) {        	
        	if ((node.attr("type"))!=="variant" && $.jstree._reference($mylist)._get_type($(node))!="disabled" && node.attr("type")=="agency") {        	       	 
        	return { 
        		"show" : {
                    "label" : "Show Route Shapes",
                    "action" : function (node) { 
                    	//alert(node.attr("type"));
                    	zoomToRouteShapeFlag = false;
                    	if ($.jstree._reference($mylist)._is_loaded(node)){
                    			$.each($.jstree._reference($mylist)._get_children(node), function(i,child){
                    				if ($.jstree._reference($mylist)._is_loaded(child)){
                    					$.each($.jstree._reference($mylist)._get_children(child), function(i,gchild){                    						
                    						//if ($.jstree._reference($mylist).is_checked(gchild)){
                    						$.jstree._reference($mylist).change_state(gchild, true);
                    						//}
                    						if ($(gchild).attr("longest")==1){
                    							$.jstree._reference($mylist).change_state(gchild, false);
                    						}            								
            								});
                    				}else{
                    					$.jstree._reference($mylist).load_node_json(child, function(){$.each($.jstree._reference($mylist)._get_children(child), function(i,gchild){
            								$.jstree._reference($mylist).change_state(gchild, true);
            								if ($(gchild).attr("longest")==1){
                    							$.jstree._reference($mylist).change_state(gchild, false);
                    						}
            								});},function(){alert("Node Load Error");});
                    				}                        			
                                		});
                			} else{
                				$.jstree._reference($mylist).load_node_json(node,function(){$.each($.jstree._reference($mylist)._get_children(node), function(i,child){
                					if ($.jstree._reference($mylist)._is_loaded(child)){
                						$.each($.jstree._reference($mylist)._get_children(child), function(i,gchild){
            								$.jstree._reference($mylist).change_state(gchild, true);
            								if ($(gchild).attr("longest")==1){
                    							$.jstree._reference($mylist).change_state(gchild, false);
                    						}
            								});
                					}else{
                						$.jstree._reference($mylist).load_node_json(child, function(){$.each($.jstree._reference($mylist)._get_children(child), function(i,gchild){
            								$.jstree._reference($mylist).change_state(gchild, true);
            								if ($(gchild).attr("longest")==1){
                    							$.jstree._reference($mylist).change_state(gchild, false);
                    						}
            								});},function(){alert("Node Load Error");});
                					}                        			
                                		});},function(){alert("Node Load Error");});
                			}
                    }
                },
                "hide" : {
                    "label" : "Hide Route Shapes",
                    "action" : function (node) { 
                    	$.each($.jstree._reference($mylist)._get_children(node), function(i,child){
                    		$.each($.jstree._reference($mylist)._get_children(child), function(i,gchild){
                    			$.jstree._reference($mylist).uncheck_node(gchild);
                    			});
                    		});
                    	}
                },
                "connectedAgencies" : {
                	"label" : "Connected Agencies",
                	"action" : function(node){
                		selectedAgency=node;
                		selectedAgencies.push(node.attr("id"));
                		loadDialog2(selectedAgency);
           			}
                }
        	};        	     
                }else if ((node.attr("type"))!=="variant" && $.jstree._reference($mylist)._get_type($(node))!="disabled" && node.attr("type")=="route"){
                	return { 
                		"show" : {
                            "label" : "Show Route Shapes",
                            "action" : function (node) { 
                            	//alert(node.attr("type"));
                            	zoomToRouteShapeFlag = true;
                            		if ($.jstree._reference($mylist)._is_loaded(node)){
        	                    		$.each($.jstree._reference($mylist)._get_children(node), function(i,child){
        	                    		$.jstree._reference($mylist).change_state(child, true);
        	                    		if ($(child).attr("longest")==1){
                							$.jstree._reference($mylist).change_state(child, false);
                						}
        	                    		}); 
                            		}else {
                            			$.jstree._reference($mylist).load_node_json(node, function(){$.each($.jstree._reference($mylist)._get_children(node), function(i,child){
                                    		$.jstree._reference($mylist).change_state(child, true);
                                    		if ($(child).attr("longest")==1){
                    							$.jstree._reference($mylist).change_state(child, false);
                    						}
                                    		});},function(){alert("Node Load Error");});
                            		}
                            }
                        },
                        "hide" : {
                            "label" : "Hide Route Shapes",
                            "action" : function (node) { 
                            		$.each($.jstree._reference($mylist)._get_children(node), function(i,child){
                            		$.jstree._reference($mylist).uncheck_node(child);
                            		});
                            }
                        }
                	};
                }
        }
    },
	"plugins" : [ "themes","types","json_data", "checkbox", "sort", "ui" ,"contextmenu"]			
})
.bind("loaded.jstree", function (event, data) {
	$mylist
	.dialog({ 
		"title" : "Oregon Transit Agencies", 
		width : 400,
		height: dialogheight,
		maxHeight: 820,
		maxWidth: 600,
		closeOnEscape: false,
		position: [40,2],
		show: {
		 effect: "blind",
		 duration: 1000
		 }		 
		})	
	.dialogExtend({
	  "closable" : false,
	  "expandTitle":"test",
	  "minimizable" : true,
	  "collapsable" : true,
	  "maximizable" : false,
	  "dblclick" : "collapse",
	  "titlebar" : "transparent",
	  "icons" : { 
		  "collapse" : "ui-icon-circle-arrow-n",
	      "restore" : "ui-icon-newwin",
	      "close": "ui-icon-document",	     
	    },
	    "load" : function(evt, dlg) {  	
	    	$(".ui-dialog-titlebar-minimize:eq( 2 )").attr("title", "Minimize");
	    	$(".ui-dialog-titlebar-buttonpane:eq( 2 )").css("right", 68 + "px");    	
		    var titlebar = $(".ui-dialog-titlebar:eq( 2 )");		
			var div2 = $("<div/>");			
		    div2.addClass("ui-dialog-titlebar-other");	    
		    var button2 = $( "<button/>" ).text( "Databases" );	
		    button2.attr("id", "dbb");
		    button2.attr("data-toggle", "dropdown");		    
		    button2.button( { icons: { primary: "ui-icon-gear" }, text: false } )	    
	        .addClass( "ui-dialog-titlebar-other" )	       
	        .css( "right", 43 + "px" )
	        .css( "top", 55 + "%" )
	        .appendTo(div2);
		    div2.append(menucontent);
		    div2.appendTo(titlebar);
		    
		    var div3 = $("<div/>");	
		    //div3.attr("id", "datepickarea");
		    div3.addClass("ui-dialog-titlebar-other");		    
		    var button3 = $( "<button/>" ).text( "Date Picker" );
		    button3.attr("id", "datepick");
		    button3.attr("data-toggle", "dropdown");
		    button3.button( { icons: { primary: "ui-icon-calculator" }, text: false } )	    
	        .addClass( "ui-dialog-titlebar-other" )	       
	        .css( "right", 22 + "px" )
	        .css( "top", 55 + "%" )
	        .appendTo(div3);
		    div3.append('<div id="datepicker"><br></div>');
		    div3.appendTo(titlebar);
		    
		    /*var div4 = $("<div/>");
		    div4.attr("id", "datepickerdiv");
		    div4.appendTo(titlebar);*/
		    
		    document.getElementById('DB'+dbindex).innerHTML = '&#9989 '+document.getElementById('DB'+dbindex).innerHTML;
		    var div = $("<div/>");
		    div.addClass("ui-dialog-titlebar-other");	    
		    var button = $( "<button/>" ).text( "Reports" );	
		    button.attr("id", "repb");
		    button.attr("data-toggle", "dropdown");		    
		    button.button( { icons: { primary: "ui-icon-document" }, text: false } )	    
	        .addClass( "ui-dialog-titlebar-other" )	       
	        .css( "right", 1 + "px" )
	        .css( "top", 55 + "%" )
	        .appendTo(div);		    

		    div.append('<ul id="rmenu" class="dropdown-menu" role="menu" aria-labelledby="drop4">'+
		    		'<li role="presentation"><a id="SSR" href="#"><b>Statewide Reports</b></a>'+
			    		'<ul>'+
			    		'<li role="presentation"><a id="ASR" href="#"><b>Transit Agency Reports</b></a></li>'+
			    		'<li role="presentation"><a id="" href="#" style="cursor:default">Geographical Reports</a>'+
			    			'<ul>'+
				    		'<li role="presentation"><a id="CSR" href="#"><b>Counties Reports</b></a></li>'+
				    		'<li role="presentation"><a id="CPSR" href="#"><b>Census Places Reports</b></a></li>'+
				    		'<li role="presentation"><a id="CDSR" href="#"><b>Congressional Districts Reports</b></a></li>'+
				    		'<li role="presentation"><a id="UASR" href="#"><b>Urban Areas Reports</b></a></li>'+
				    		'<li role="presentation"><a id="AUASR" href="#"><b>Aggregated Urban Areas Reports</b></a></li>'+
				    		'<li role="presentation"><a id="ORSR" href="#"><b>ODOT Transit Regions Reports</b></a></li>'+
				    		'</ul>'+
			    		'</li>'+
			    		'</ul>'+
		    		'</li>'+
		    		'<li role="presentation"><a id="" href="#" style="cursor:default">Connectivity Reports</a>'+
		    			'<ul>'+
			    		'<li role="presentation"><a id="THR" href="#"><b>Transit Hubs Reports</b></a></li>'+
			    		'<li role="presentation"><a id="KTHR" href="#"><b>Key Transit Hubs Reports</b></a></li>'+
			    		'<li role="presentation"><a id="TCR" href="#"><b>Timing Connections Reports</b></a></li>'+
			    		'<li role="presentation"><a id="CNSR" href="#"><b>Connected Transit Networks Reports</b></a></li>'+
			    		'<li role="presentation"><a id="CASR" href="#"><b>Connected Transit Agencies Reports</b></a></li>'+
			    		'<li role="presentation"><a id="PNRR" href="#"><b>Park & Ride Reports</b></a></li>'+
			    		'<li role="presentation"><a id="CNGPH" href="#"><b>Connectivity Graph</b></a></li>'+
			    		'</ul>'+
		    		'</li>'+
		    		'<li role="presentation" onclick="return;"><a id="Emp" href="#"><b>Employment Reports</b></a></li>'+
		    		'<li role="presentation" onclick="return;"><a id="T6" href="#"><b>Title VI Reports</b></a></li>'+
					'<li role="presentation"><a id="" href="#" style="cursor:default;"><b>Other</b></a>'+
		    			'<ul>'+
		    			'<li role="presentation"><a id="DDRPT" href="#"><b>Data Dump Report</b></a></li>'+
		    			'<li role="presentation"><a id="FLXRPT" href="#"><b>Flexible Reporting Wizard</b></a></li>'+
		    			'<li role="presentation"><a id="SHPFL" href="#"><b>Shapefile Generator</b></a></li>'+
		    			'</ul>'+
	    			'</li>');
		    		
		 
			div.appendTo(titlebar);
			$( "#rmenu" ).menu();
			$( ".ui-menu" ).css('width','21em');
			$( ".ui-menu-item" ).css('width','21em');
			$('.ui-dialog-titlebar-other').dropdown();			
			$("#datepicker").multiDatesPicker({
				changeMonth: false,
		      	changeYear: false,
				  onSelect: function(date, inst) {					  
					  dateID = date.replace("/","").replace("/","");					  
						if($("#"+dateID).length==0){
							//alert("add triggered");
							addDate(date);							
						}else{
							//alert("del triggered");
							$("#"+dateID).remove();							
						}
						if($('#datepicker').multiDatesPicker('getDates').length>0){
								$("#datepick").css({"border":"2px solid #900"});
						} else {
								$("#datepick").css({"border":""});
						}
			      }
			});
			
			//$("#datesdiv").css({"width":"100%"});
			$("#datepicker").append("<input type='button' value='Submit Dates' id='datepickerSubmit' onclick='updatepicker()'/>");
			$("#datepicker").css({"display":"inline-block", "z-index":"1000", "position":"fixed"});
			//$("#datesarea").css({"list-style-type":"none","margin":"0","padding":"0"}); 
			$("#datepicker").hide();
			
			$('#datepick').click(function () {
				$("#datepicker").toggle();
				//updatepicker();
				
			});
			$('#dbb').click(function () {
				$("#datepicker").hide();
				//updatepicker();
			});
			$('#repb').click(function () {
				$("#datepicker").hide();
				//updatepicker();
			});		
			
			$mylist.dialogExtend("collapse");
			$("#minimize").attr("title", "Minimize");
			$('a').click(function(e){				
				var casestring = '';
				if ($(this).attr('id') != undefined) {
				casestring = $(this).attr('id');
				}
				if (casestring=="THR"){
					var d = new Date();
					var qstringx = '0.08';	// clustering radius
					var qstringx2 = '0.25'; // population search radius					
					var qstringx3 = '2.0'; // park and ride search radius
					var qstringd = [pad(d.getMonth()+1), pad(d.getDate()), d.getFullYear()].join('/');
					var keyName = setDates(qstringd);
			    	window.open('/TNAtoolAPI-Webapp/HubSreport.html?&x1='+qstringx+'&x2='+qstringx2+ '&x3='+qstringx3+'&n='+keyName+'&dbindex='+dbindex+'&popYear='+popYear/*+'&username='+getSession()*/);
			    }else if (casestring=="KTHR"){
				    var d = new Date();
					var qstringx = '0.08';	// clustering radius
					var qstringx2 = '0.25'; // population search radius					
					var qstringx3 = '2.0'  // park and ride search radius
					var qstringd = [pad(d.getMonth()+1), pad(d.getDate()), d.getFullYear()].join('/');
					var keyName = setDates(qstringd);
			    	window.open('/TNAtoolAPI-Webapp/KeyHubSreport.html?&x1='+qstringx+'&x2='+qstringx2+ '&x3='+qstringx3+'&n='+keyName+'&dbindex='+dbindex+'&popYear='+popYear);
			    }else if (casestring=="SSR"){			    	
			    	window.open('/TNAtoolAPI-Webapp/StateSreport.html?&dbindex='+dbindex+'&popYear='+popYear);
			    }else if (casestring=="TCR") {
			    	window.open('/TNAtoolAPI-Webapp/TimingConnection.html?&dbindex=' + dbindex);
			    }else if (casestring=="ASR"){
			    	window.open('/TNAtoolAPI-Webapp/AgenSReport.html?&dbindex='+dbindex+'&popYear='+popYear);
			    }else if (casestring=="CASR"){
			    	var qstringx = '0.1';
			    	window.open('/TNAtoolAPI-Webapp/ConAgenSReport.html?&gap='+qstringx+'&dbindex='+dbindex+'&popYear='+popYear);
			    }else if (casestring=="CNSR"){
			    	var qstringx = '0.1';
			    	window.open('/TNAtoolAPI-Webapp/ConNetSReport.html?&gap='+qstringx+'&dbindex='+dbindex+'&popYear='+popYear);
			    }else if(casestring=="CSR"){
			    	window.open('/TNAtoolAPI-Webapp/GeoCountiesReport.html'+'?&dbindex='+dbindex+'&popYear='+popYear);	    		
			    }else if(casestring=="CPSR"){
			    	window.open('/TNAtoolAPI-Webapp/GeoPlacesReport.html'+'?&dbindex='+dbindex+'&popYear='+popYear);	    		
			    }else if(casestring=="CDSR"){
			    	window.open('/TNAtoolAPI-Webapp/GeoCongDistsReport.html'+'?&dbindex='+dbindex+'&popYear='+popYear);	    		
			    }else if(casestring=="UASR"){
			    	var popMax='2000000';
			    	var popMin='0'	;
			    	window.open('/TNAtoolAPI-Webapp/GeoUAreasReport.html?&pop=-1'+'&dbindex='+dbindex+'&uc=3'+'&popYear='+popYear+'&popMin='+popMin+'&popMax='+popMax+'&areaid='+null+'&type=-1');    		
			    }else if(casestring=="AUASR"){
			    	var popMax='2000000';
			    	var popMin='0'	;
			    	window.open('/TNAtoolAPI-Webapp/GeoUAreasRReport.html'+'&popMin='+popMin+'&popMax='+popMax+'&dbindex='+dbindex+'&popYear='+popYear/*+'&username='+getSession()*/);	    		
			    }else if(casestring=="ORSR"){
			    	window.open('/TNAtoolAPI-Webapp/GeoRegionsReport.html'+'?&dbindex='+dbindex+'&popYear='+popYear/*+'&username='+getSession()*/);	    		
			    }else if(casestring=="PNRR"){
			    	window.open('/TNAtoolAPI-Webapp/ParkRideReport.html'+'?&dbindex='+dbindex+'&popYear='+popYear);
			    }else if(casestring=="CNGPH"){
			    	toggleConGraphDialog();
			    }else if(casestring=="FLXRPT"){
			    	flexRepDialog();
			    }else if(casestring=="SHPFL"){
			    	ShapeFileExpStart();
			    }else if(casestring=="DDRPT"){
			    	openDatadumpRep();
			    }else if(casestring.substring(0,2)=="DB"){
			    	if (dbindex!=parseInt(casestring.substring(2)))
			    		if ($('#datepicker').multiDatesPicker('getDates').length>0){
			    			var dates = $('#datepicker').multiDatesPicker('getDates');			    			
			    			//localStorage.setItem(key, dates.join(","));	
			    			key = setDates(dates.join(","));
			    		}
			    		location.replace(document.URL.split("?")[0]+"?&n="+key+'&dbindex='+parseInt(casestring.substring(2)));			    		    		
			    }else if(casestring=="Emp"){
			    	var d = new Date();
			    	var qstringd = [pad(d.getMonth()+1), pad(d.getDate()), d.getFullYear()].join('/');
			    	//var keyName = Math.random();
		    		///localStorage.setItem(keyName, qstringd);
					var keyName = setDates(qstringd);
			    	window.open('/TNAtoolAPI-Webapp/Emp.html?&n='+keyName+'&dbindex='+dbindex);
			    }else if(casestring=="T6"){	
			    	var d = new Date();
			    	var qstringd = [pad(d.getMonth()+1), pad(d.getDate()), d.getFullYear()].join('/');
			    	//var keyName = Math.random();
		    		///localStorage.setItem(keyName, qstringd);
					var keyName = setDates(qstringd);
			    	window.open('/TNAtoolAPI-Webapp/T6.html?&n='+keyName+'&dbindex='+dbindex);
			    }
			});
    	
		  $mylist.dialogExtend("collapse");
		  $("#minimize").attr("title", "Minimize");		  
		  	  
	    },
	    "restore": function(evt,dlg){
	    	$("#collapse").attr("title", "Collapse");	    	
	    	$(".dropdown-menu").css("top", 100+"%" );
	    	$(".dropdown-menu").css("bottom", "auto" );
	    	$('.leaflet-draw-edit-remove').click();	    	
	    	drawControl._toolbars[L.DrawToolbar.TYPE]._modes.rectangle.handler.disable();
	    	drawControl._toolbars[L.DrawToolbar.TYPE]._modes.polygon.handler.disable();
	    	drawControl._toolbars[L.DrawToolbar.TYPE]._modes.circle.handler.disable();
	    	drawControl._toolbars[L.EditToolbar.TYPE]._modes.edit.handler.disable();
	    	dialog2.dialog('close');
	    },
	    "collapse" : function(evt,dlg){	    	
	    	$(".dropdown-menu").css("top", 100+"%" );
	    	$(".dropdown-menu").css("bottom", "auto" );
	    	$(".ui-dialog-titlebar-collapse:eq( 1 )").attr("title", "Collapse");
	    },	    
	    "minimize" : function(evt,dlg){
	    	$(".ui-dialog-titlebar-collapse:eq( 1 )").attr("title", "Restore");
	    	$(".ui-dialog-titlebar-restore:eq( 1 )").attr("title", "Maximize");
	    	$(".dropdown-menu").css("top", "auto" );
	    	$(".dropdown-menu").css("bottom", 100+"%" );	    	
	    },
	    iconButtons: [
		                {
		                    text: "Reports",
		                    icon: "ui-icon-document",
		                    click: function( e ) {
		                        $( "#dialog" ).html( "<p>Searching...</p>" );
		                    }
		                 }
		            ]
	});   
	updateListDialog(dialogAgenciesId);
})
.bind("change_state.jstree", function (e, d) {
    var tagName = d.args[0].tagName;
    var refreshing = d.inst.data.core.refreshing;
    //alert(tagName);
    if ((tagName == "A" || tagName == "INS"|| tagName == "LI")&&(refreshing != true && refreshing != "undefined")) {    	
    	node = d.rslt;    	
    	switch (node.attr("type")){
    	case "agency":   		
    		//checkbox is checked
    		if ($.jstree._reference($mylist).is_checked(node)){
    			//$(node).disabled = true;
    			$.jstree._reference($mylist).set_type("disabled", $(node));    			   			
    			$.jstree._reference($mylist)._get_children(node).each( function( idx, listItem ) {    				
    				if ($.jstree._reference($mylist).is_checked($(listItem))){
	    				stops.eachLayer(function (layer) {
	    				if (layer._leaflet_id == "R"+node.attr("id")+ $(listItem).attr("id")){
	    					stops.removeLayer(layer);
	    				}
	    				});
	    				$(listItem).css("background-color","");	    				
	    				$.jstree._reference($mylist).uncheck_node($(listItem));
    				};
                 });    
    			colorIndex = dialogAgenciesId.indexOf(node.attr("id"));
    			node.css("opacity", "1");
    			node.css("background-color", colorset[colorIndex%6]);    			
    			getdata(1,node.attr("id"),"","",colorIndex%6,disponmap,$.jstree._reference($mylist).get_text(node),node);
    			//Layers = Layers + 1;    			
    		} else {    			
    			node.css("background-color","");
    			if ((($($mylist).jstree("get_checked",node,true)).length)>0) node.css("opacity", "0.6");
    			stops.eachLayer(function (layer) {
    			if (layer._leaflet_id == "A"+node.attr("id")){    					
    				stops.removeLayer(layer);
    				}
    			});	    				
    		}
    		break;
    	case "route":
    		if ($.jstree._reference($mylist).is_checked(node)){
    			$.jstree._reference($mylist).set_type("disabled", $(node));    			
    			rparent = $.jstree._reference($mylist)._get_parent(node);    			
    			if($.jstree._reference($mylist).is_checked(rparent)){    			
    				stops.eachLayer(function (layer) {
    				if (layer._leaflet_id == "A"+ rparent.attr("id")){
    				stops.removeLayer(layer);
    				}
    				});
    				rparent.css("background-color","");    				
    				$.jstree._reference($mylist).uncheck_node(rparent);
    			}
    			colorIndex = dialogAgenciesId.indexOf(rparent.attr("id"));
    			node.css("background-color", colorset[colorIndex%6]); 
    			rparent.css("opacity", "0.6");
    			getdata(2,rparent.attr("id"),node.attr("id"),"",colorIndex%6,disponmap,$.jstree._reference($mylist).get_text(node),node);
    			//Layers = Layers + 1;    			
    		} else {    			
    			if ((($($mylist).jstree("get_checked",rparent,true)).length)==0) rparent.css("opacity", "1");
    			node.css("background-color","");    			    			
    			stops.eachLayer(function (layer) {
    				if (layer._leaflet_id == "R"+d.inst._get_parent(node).attr("id")+node.attr("id")){
    				stops.removeLayer(layer);
    				}
    			});	    				
    		}
    		break;
    	case "variant":
    		vparent = $.jstree._reference($mylist)._get_parent(node);  
    		rvparent = $.jstree._reference($mylist)._get_parent(vparent); 
    		colorIndex = dialogAgenciesId.indexOf(rvparent.attr("id"));
    		if ($.jstree._reference($mylist).is_checked(node)){
    			$.jstree._reference($mylist).set_type("disabled", $(node));    			
    			node.css("background-color", colorset[colorIndex%6]);
    			vparent.css("font-weight", "bold");
    			$.jstree._reference($mylist)._get_parent(vparent).css("opacity", "0.6");
    			getdata(3,d.inst._get_parent((d.inst._get_parent(node))).attr("id"),(d.inst._get_parent(node)).attr("id"),node.attr("id"),colorIndex%6,dispronmap,node.attr("id"),node);
    			//Layers = Layers + 1;     			
    		} else {    			
    			node.css("background-color","");
    			if ((($($mylist).jstree("get_checked",vparent,true)).length)==0) {
    				vparent.css("font-weight", "normal");
    				if ((($($mylist).jstree("get_checked",$.jstree._reference($mylist)._get_parent(vparent),true)).length)==0) {
    					$.jstree._reference($mylist)._get_parent(vparent).css("opacity", "1");
    				}
    			}    			
    			routes.eachLayer(function (layer) {
    				if (layer._leaflet_id == "V"+d.inst._get_parent((d.inst._get_parent(node))).attr("id")+(d.inst._get_parent(node)).attr("id")+node.attr("id")){
    				routes.removeLayer(layer);
    				}
    			});	    				
    		}
    		break;
    	}    	
    };
});
function updatepicker(){
	newdates = null;
	if ($('#datepicker').multiDatesPicker('getDates').length>0){
		var dates = $('#datepicker').multiDatesPicker('getDates');
		newdates = dates.join(",");
		//localStorage.setItem(key, newdates);
		key = setDates(newdates);
	} else {						
			$("#datepick").css({"border":""});
			//localStorage.removeItem(key);
			key="--";
	}
	location.replace(document.URL.split("?")[0]+"?&n="+key+'&dbindex='+dbindex);
}

function updateListDialog(agenciesIds){
	var aList = $( "li[type='agency']" );
	var count = aList.length;
	var today = currentDateFormatted();
	$.ajax({
		type: 'GET',
		datatype: 'json',
		url: '/TNAtoolAPI-Webapp/queries/transit/agenciesCalendarRange?agencies='+agenciesIds.join(',')+'&dbindex='+dbindex,
		async: false,
		success: function(d){
			$.each(d.SEDList, function(i,item){
				if(today>item.Enddate){
					$(aList[i]).children('a').css('color','red');
				}
		    	$(aList[i]).children('a').attr( "title", "Active Service Dates: "+stringToDate(item.Startdate)+" to "+stringToDate(item.Enddate));
		    	
			});	
		}
	});
	$('.jstree-no-dots').prepend("<p style='margin-left:3%'><b>List of Agencies:</b></p>");
	$('.jstree-no-dots').css({"height": "74%","padding-top": "2%"});
	
	var noGTFS = [//"Albany Transit System",
	              "Burns Paiute Tribal Transit Service",
	              //"Corvallis Transit System", 
	              //"Linn-Benton Loop",
	              //"Malheur Council on Aging & Community Services",
	              "Shawn's Rideshare",
	              "South Clackamas Transportation District",
	              "Warm Springs Transit"];
	
	if (!w_qstringd && getSession()=='admin'){
		var html = 	"<br><br><p style='margin-left:3%'><b>Agencies with no GTFS feed:</b></p>";
		html +=	"<ul style='margin-bottom: 20px;'>";
		for(var i=0; i<noGTFS.length; i++){
			html +=	"<li style='margin-left:52px'>"+(++count)+". "+noGTFS[i]+"</li>";
		}
		html +=	"</ul>";
		$('.jstree-no-dots').append(html);
	}
	
	$mylist.append( "<div id='listLegend'><p style='font-size: 90%;margin-left:2%;color:red;margin-top:1%'>-<i>Agencies in red color have an expired GTFS feed</i></p></div>" );
	$mylist.append( "<div id='dateList'><p style='margin-left:3%'><b>Selected Dates:</b></p></div>" );
	$("#dateList").append("<div id='datesdiv' style='padding-left: 4%;'><ul id='datesarea'></ul></div>");
	//$("#datesdiv").css({"width":"100%"});
	$("#datesarea").css({"list-style-type":"none","margin":"0","padding":"0"});
	
	if (w_qstringd){				
		$( "#datepicker" ).multiDatesPicker({
			addDates: w_qstringd.split(","),
			onSelect: function(date, inst) {					  
				  dateID = date.replace("/","").replace("/","");					  
					if($("#"+dateID).length==0){
						//alert("add triggered");
						addDate(date);							
					}else{
						//alert("del triggered");
						$("#"+dateID).remove();							
					}
					if ($('#datepicker').multiDatesPicker('getDates').length>0){
							$("#datepick").css({"border":"2px solid #900"});
						} else {
							$("#datepick").css({"border":""});
						}
		      }
		});	
		//alert(w_qstringd);
		var cdate;
		for(var i=0; i<w_qstringd.split(",").length; i++){
			cdate = w_qstringd.split(",")[i];
			dateID = cdate.replace("/","").replace("/","");
			addDate(cdate);		
		}									
	}
}
