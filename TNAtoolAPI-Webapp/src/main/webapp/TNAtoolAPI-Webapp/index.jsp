<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta charset="utf-8" />
	<title>Transit Network Analysis Tool</title>
	<script type="text/javascript" src="resources/js/jquery-1.9.1.min.js"></script>	
    <script type="text/javascript" src="resources/js/jquery-ui.js"></script>	
	<script type="text/javascript" src="resources/js/jQueryContent.js"></script>
	<script type="text/javascript" src="resources/js/jquery-ui.multidatespicker.js"></script>
	<script type="text/javascript" src="resources/js/bootstrap-dropdown.js"></script>
	<!-- <script type="text/javascript" src="resources/js/simple-expand.min.js"></script> -->
	<script type="text/javascript" src="resources/js/jquery_dialogextend.js"></script>	
	<script type="text/javascript" src="resources/data/shapes.js"></script>
	<script type="text/javascript" src="resources/data/urbanShapes.js"></script>	
	<script type="text/javascript" src="resources/data/tractsShape.js"></script>
	<script type="text/javascript" src="resources/js/date.js"></script>
	<script type="text/javascript" src="resources/data/Datasources.js"></script>
	<script type="text/javascript" src="resources/js/ConnectivityGraph.js"></script>
	<script type="text/javascript" src="resources/js/ConnectivityGraph0.js"></script>
	<script type="text/javascript" src="vendors/jstree-v.pre1.0/jquery.jstree.js"></script>		
	<script type="text/javascript" src="vendors/DataTables/js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="vendors/DataTables/js/dataTables.tableTools.js"></script>
	<!-- <script type="text/javascript" src="vendors/leaflet-0.7/minimap/html2canvas.js"></script> -->
	<script type="text/javascript" src="vendors/leaflet-0.7/leaflet-src.js"></script>
	<script type="text/javascript" src="vendors/leaflet-0.7/search/Control.Geocoder.js"></script>
	<script type="text/javascript" src="vendors/leaflet-0.7/Control.MiniMap.js"></script>
	<script type="text/javascript" src="vendors/leaflet-0.7/leaflet.contextmenu.js"></script>
	<script type="text/javascript" src="vendors/leaflet-0.7/Google.js"></script>
	<script type="text/javascript" src="vendors/leaflet-0.7/leaflet.draw.js"></script>
	<script type="text/javascript" src="vendors/MarkerCluster/leaflet_markercluster-src.js"></script>
    <script type="text/javascript" src="resources/js/tile.stamen.js"></script>
	<script type="text/javascript" src="resources/js/Polylinencoded.js"></script>
	<script type="text/javascript" src="resources/js/onMapReport.js"></script>
	<script type="text/javascript" src="https://maps.google.com/maps/api/js?v=3&sensor=false"></script>
	<script type="text/javascript" src="resources/js/JSMethods_Onmap.js"></script>
	<script type="text/javascript" src="resources/js/TimingConnection.js"></script>
	<script type="text/javascript" src="resources/js/JSMethods.js"></script>
	<script type="text/javascript" src="resources/js/ShapefileGenerator.js"></script>
	<script type="text/javascript" src="resources/js/FlexibleReporting.js"></script>
	
	<link rel="stylesheet" type="text/css" href="resources/css/client.css" />	
	<link rel="stylesheet" type="text/css" href="resources/css/jquery-ui.css" />
	<link rel="stylesheet" type="text/css" href="resources/css/jquery-ui.multidatespicker.css">
	<link rel="stylesheet" type="text/css" href="resources/css/bootstrap.css" />
	<link rel="stylesheet" type="text/css" href="resources/css/FlexibleReporting.css" />
	<link rel="stylesheet" type="text/css" href="vendors/MarkerCluster/MarkerCluster.css" />
	<link rel="stylesheet" type="text/css" href="vendors/MarkerCluster/MarkerCluster.styles.css" />
	<link rel="stylesheet" type="text/css" href="vendors/MarkerCluster/MarkerCluster.Default.css" />	
	<link rel="stylesheet" type="text/css" href="vendors/leaflet-0.7/leaflet.draw.css" />
	<link rel="stylesheet" type="text/css" href="vendors/leaflet-0.7/Control.MiniMap.css" />
	<link rel="stylesheet" type="text/css" href="vendors/leaflet-0.7/leaflet.contextmenu.css" /> 
	<link rel="stylesheet" type="text/css" href="vendors/DataTables/css/jquery.dataTables.css" />
	<link rel="stylesheet" type="text/css" href="vendors/DataTables/css/dataTables.tableTools.css" />
	<link rel="stylesheet" type="text/css" href="vendors/leaflet-0.7/leaflet.css" /> 
	<link rel="stylesheet" type="text/css" href="vendors/leaflet-0.7/search/Control.Geocoder.css" />
	
	<!-- Google Analytics Tracking -->	
	<script>
	  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
	  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
	  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
	  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
	
	  ga('create', 'UA-64807119-1', 'auto');
	  ga('send', 'pageview');
	</script>
</head>
<body style="display:none">
	<div id="overlay"></div>
	<div id='list' class='list'>
	</div>
	
	<div id="connectedAgencies-form" title="On-Map Connected Agencies Report" style="text-size=50%">
		<p style="font-size:80%"> Maximum Spatial Gap (miles) <input type="number" name="Sradius" id="Sradius" class="utbox" required min="0.1" value='0.1' step="0.1" onkeypress='return isNumber(event)'/>
				    	<input id="submit" type="button" onclick="reloadDialog2(document.getElementById('Sradius').value)" title="Click submit to refresh the report"  value="Submit" class="button" /></p>		    			    	    	
		<p><br><span>Agencies connected to: <b><br>- <span style="font-size:80%; align:center;" id="dialogSelectedAgency"></span></b></span><br>
		<p id="displayConAgenciesTable"></p>
		<img id="dialogPreLoader2" src="../resources/images/287.GIF" alt="Page Loading" width="85" height="85" style="margin:100px" >
	</div>
	
	<div id="dialog-form" title="On-Map Report">
		<div style="width:100%; display:flex">
			<div style="width:50%;margin-bottom: 13%;margin-top: 5.5%;">
				<p><b>Centroid</b>:<br>
				<span style="padding-left:3em">Latitude: <span style="padding-left:1.5em" id="dialogLat"></span></span><br>
				<span style="padding-left:3em">Longitude: <span id="dialogLng"></span></span>
				</p><br>
				<p><b>Area</b>:<span id="dialogArea" style="padding-left:1em"></span> Square Miles
				</p><br>
				<p><b>Date</b>: <input readonly type="text" id="dialogDate" style="width: 60%">
				</p>
				<input  type="text" autofocus style="visibility:hidden">
			</div>
			<div style="width:40%; margin-left:5%; margin-bottom:6%">
				<div id="blocksLengend" style="width:100%">
				
				</div>
			</div>
		</div>
	  
	  <div id="tabs">
		  <ul>
		    <li><a href="#transit">Transit Agencies</a></li>
		    <li><a href="#geography">Census</a></li>
		    <li><a href="#title6">Title VI</a></li>
		    <li><a href="#parknride">Park and Ride</a></li>
		  </ul>
		  <div id="transit">
		  	<b>Show Transit: </b><input type="radio" name="transitShow" id="stopsCheck" value="stops" style="margin-left:2em" onclick="transitRadio(this)">Stops
		  				 <input type="radio" name="transitShow" id="routeCheck" value="routes" style="margin-left:1em" onclick="transitRadio(this)">Routes
		  				 <input type="radio" name="transitShow" class="bothCheck" value="both" style="margin-left:1em" checked onclick="transitRadio(this)">Both<br><br>
		  	<table id="stopRoute" style="border:none; font-size:100%; width:90%">
		  		<tr>
		  			<td><b>Number of Stops:</b></td>
		  			<td class="onMapdata" id="ts"></td>
		  		</tr>
		  		<tr>
		  			<td><b>Number of Routes:</b></td>
		  			<td class="onMapdata" id="tr"></td>
		  		</tr>
		  		<tr>
		  			<td><b>Average Fare:</b></td>
		  			<td class="onMapdata" id="af"></td>
		  		</tr>
		  		<tr>
		  			<td><b>Median Fare:</b></td>
		  			<td class="onMapdata" id="mff"></td>
		  		</tr>
		  	</table>
		  	<p id="displayTransitReport"></p>
		  </div>
		  <div id="geography">
		  	<b>Show Census: </b><input type="radio" name="censusShow" id="blocksCheck" value="blocks" style="margin-left:2em" onclick="geoRadio(this)">Blocks
		  						<input type="radio" name="censusShow" id="tractsCheck" value="tracts" style="margin-left:1em" onclick="geoRadio(this)">Tracts
		  				 		<input type="radio" name="censusShow" class="bothCheck" value="both" style="margin-left:1em" checked onclick="geoRadio(this)">Both<br><br>
		  	<table id="blockTract" style="border:none; font-size:100%; width:90%">
		  		<tr>
		  			<td><b>Urban Population (2010):</b></td>
		  			<td class="onMapdata" id="tpu"></td>
		  		</tr>
		  		<tr>
		  			<td><b>Rural Population (2010):</b></td>
		  			<td class="onMapdata" id="tpr"></td>
		  		</tr>
		  		<tr>
		  			<td><b>Employees (2013):</b></td>
		  			<td class="onMapdata" id="tee"></td>
		  		</tr>
		  		<tr>
		  			<td><b>Employment (2013):</b></td>
		  			<td class="onMapdata" id="tem"></td>
		  		</tr>
		  		<tr>
		  			<td><b>Number of Census Blocks:</b></td>
		  			<td class="onMapdata" id="tb"></td>
		  		</tr>
		  		<tr>
		  			<td><b>Number of Census Tracts:</b></td>
		  			<td class="onMapdata" id="tt"></td>
		  		</tr>
		  	</table>	  	
		  	<p id="displayGeoReport"></p>
		  </div>
		  <div id="title6">
		  	<table style="width:100%; font-size:100%">
		  		<tr>
		  			<td><b>Percent of Population Disabled:</b></td>
		  			<td class="onMapdata" id="pd"></td>
		  		</tr>
		  		<tr><td><br></td></tr>
		  		<tr>
		  			<td><b>Percent of Population Below Poverty:</b></td>
		  			<td class="onMapdata" id="pp"></td>
		  		</tr>
		  	</table>
		  	<br>
		  	<p><b>Ethnicity:</b><br></p>
		  	<table style="font-size:95%;width:100%">
		  		<tr>
		  			<td>American Indian or Alaska Native:</td>
		  			<td class="onMapdata" id="pei"></td>
		  		</tr>
		  		<tr>
		  			<td>Asian:</td>
		  			<td class="onMapdata" id="pea"></td>
		  		</tr>
		  		<tr>
		  			<td>Black or African American:</td>
		  			<td class="onMapdata" id="peb"></td>
		  		</tr>
		  		<tr>
		  			<td>Hispanic or Latino:</td>
		  			<td class="onMapdata" id="peh"></td>
		  		</tr>
		  		<tr>
		  			<td>Native Hawaiian and Other Pacific Islander:</td>
		  			<td class="onMapdata" id="pen"></td>
		  		</tr>
		  		<tr>
		  			<td>White:</td>
		  			<td class="onMapdata" id="pew"></td>
		  		</tr>
		  		<tr>
		  			<td>Two or More Races:</td>
		  			<td class="onMapdata" id="pet"></td>
		  		</tr>
		  		<tr>
		  			<td>Other Races:</td>
		  			<td class="onMapdata" id="peo"></td>
		  		</tr>
		  	</table>
		  	<br>
		  	<p><b>Age:</b></p>
		  	<table style="font-size:95%;width:100%">
		  		<tr>
		  			<td>5 to 17 years old:</td>
		  			<td class="onMapdata" id="pa5"></td>
		  		</tr>
		  		<tr>
		  			<td>18 to 64 years old:</td>
		  			<td class="onMapdata" id="pa18"></td>
		  		</tr>
		  		<tr>
		  			<td>65 years old and above:</td>
		  			<td class="onMapdata" id="pa64"></td>
		  		</tr>
		  	</table>	
		  	<br>
		  	<p><b>Language Spoken:</b></p>
		  	<table style="font-size:95%;width:100%">
		  		<tr>
		  			<td>English:</td>
		  			<td class="onMapdata" id="plse"></td>
		  		</tr>
		  	</table><br>
		  	<table style="font-size:95%;width:100%">	
		  		<tr>
		  			<td>Spanish:</td>
		  			<td class="onMapdata" id="plss"></td>
		  		</tr>
		  	</table>
		  	<p>English Proficiency:</p>
		  	<table class="LEP" style="font-size:95%;width:100%">
		  		<tr>
		  			<td>very well</td>
		  			<td>well</td>
		  			<td>not well</td>
		  			<td>not at all</td>
		  		</tr>
		  		<tr>
		  			<td id="plss1"></td>
		  			<td id="plss2"></td>
		  			<td id="plss3"></td>
		  			<td id="plss4"></td>
		  		</tr>
		  	</table><br>
		  		
		  	<table style="font-size:95%;width:100%">
		  		<tr>
		  			<td>Asian &amp; Pacific Islander:</td>
		  			<td class="onMapdata" id="plsa"></td>
		  		</tr>
		  	</table>
		  	<p>English Proficiency:</p>
		  	<table class="LEP" style="font-size:95%;width:100%">
		  		<tr>
		  			<td>very well</td>
		  			<td>well</td>
		  			<td>not well</td>
		  			<td>not at all</td>
		  		</tr>
		  		<tr>
		  			<td id="plsa1"></td>
		  			<td id="plsa2"></td>
		  			<td id="plsa3"></td>
		  			<td id="plsa4"></td>
		  		</tr>
		  	</table><br>
		  	<table style="font-size:95%;width:100%">
		  		<tr>
		  			<td>Indo European:</td>
		  			<td class="onMapdata" id="plsi"></td>
		  		</tr>
		  	</table>
		  	<p>English Proficiency:</p>
		  	<table class="LEP" style="font-size:95%;width:100%">
		  		<tr>
		  			<td>very well</td>
		  			<td>well</td>
		  			<td>not well</td>
		  			<td>not at all</td>
		  		</tr>
		  		<tr>
		  			<td id="plsi1"></td>
		  			<td id="plsi2"></td>
		  			<td id="plsi3"></td>
		  			<td id="plsi4"></td>
		  		</tr>
		  	</table><br>
		  	<table style="font-size:95%;width:100%">
		  		<tr>
		  			<td>Other Languages:</td>
		  			<td class="onMapdata" id="plso"></td>
		  		</tr>
		  	</table>
		  	<p>English Proficiency</p>
		  	<table class="LEP" style="font-size:95%;width:100%">
		  		<tr>
		  			<td>very well</td>
		  			<td>well</td>
		  			<td>not well</td>
		  			<td>not at all</td>
		  		</tr>
		  		<tr>
		  			<td id="plso1"></td>
		  			<td id="plso2"></td>
		  			<td id="plso3"></td>
		  			<td id="plso4"></td>
		  		</tr>
		  	</table><br>
		  </div>
		  <div id="parknride">
		  	<table id="parknrideInfo" style="border:none; font-size:100%;width:90% ">
		  		<tr>
		  			<td><b>Number of P&amp;R Lots:</b></td>
		  			<td class="onMapdata" id="npnr"></td>
		  		</tr>
		  		<tr>
		  			<td><b>Number of Available Spaces:</b></td>
		  			<td class="onMapdata" id="nspc"></td>
		  		</tr>
		  	</table>
		  	<br>
		  	<p id="displayPnrCounties"></p>
		  </div>
		  
	  </div>
      <img id="dialogPreLoader" src="../resources/images/287.GIF" alt="Page Loading" style="margin:9em;margin-top:22em" >     
    </div>
    <div id="dialogResults" title="Report Results">
	</div>
    <div id="map"> </div>
    <div id="con-graph-dialog" title="Transit Connectivity Graph" style="min-height:350px"></div>
    <div id="flexRepDialog" title="Flexible Reporting Wizard" style="min-height:350px;">
    	<div id="FlexRepContainer">
	   		<div id="FlexRepTypes" class="flexRepDialogSection"></div>
	    	<div id="FlexRepParamsMetrics" class="flexRepDialogSection">
	    		<div id="FlexRepParamsHeader"></div>
	    		<div id="FlexRepParamsContainer" class="FlexRepSectionContainer">
		   			<span class="header2" >Select report parameters:</span><hr><div id="FlexRepParams" class="FlexRepParamsMetricsContainer"></div>
					<br><br>
		   			<span class="header2" >Select desired metrics:</span><hr><div id="FlexRepMetrics" class="FlexRepParamsMetricsContainer"></div>
	   			</div>
	    	</div>   
	    	<div id="FlexRepAgencies" class="flexRepDialogSection"></div>
	    	<div id="FlexRepAreas" class="flexRepDialogSection"></div>
	    	<div id="FlexRepUAreas" class="flexRepDialogSection"></div>
    	</div>
    	<br><br><br>
    	<div style="clear:both;"><input type='submit' id='flexRepSubmit' value='Submit' onclick='openFlexRepTable()'></div>
    </div>
   	<script type="text/javascript" src="resources/js/client.js"></script>
</body>
</html>
