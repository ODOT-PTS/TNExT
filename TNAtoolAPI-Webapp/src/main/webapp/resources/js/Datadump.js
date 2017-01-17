var reportType,popYear,dbIndex,sRadius,los,dates,csvContent,progressVal,agencies,areaIDs,areaType;
var tempHeaders = 'Route Miles' + ','
	+ 'Stops Per Square Mile' + ','
	+ 'Stops Per Service Mile' + ','
	+ 'Service Hours' + ','
	+ 'Service Miles' + ','
	+ 'Service Miles Per Square Mile' + ','
	+ 'Miles of Service Per Capita' + ','
	+ 'Urban Population Served' + ','
	+ 'Rural Population Served' + ','
	+ 'Percent of Population Served' + ','
	+ 'Percent of Population Served at Level of Service' + ','
	+ 'Urban Population Served at Level of Service' + ','
	+ 'Rural Population Served at Level of Service' + ','
	+ 'Percent of Population Unserved' + ','
	+ 'Service Stops' + ','
	+ 'Urban Population Served By Service' + ','
	+ 'Rural Population Served By Service' + ','
	+ 'Employment Served (RAC)' + ','
	+ 'Percent of Employment (RAC)  Served' + ','
	+ 'Percent of Employment (RAC)  Served at Level of Service' + ','
	+ 'Employment (RAC)  Served at Level of Service' + ','
	+ 'Percent of Employment (RAC) Unserved' + ','
	+ 'Employment (RAC) Served By Service' + ','
	+ 'Employees (WAC) Served' + ','
	+ 'Percent of Employees (WAC) Served' + ','
	+ 'Percent of Employees (WAC) Served at Level of Service' + ','
	+ 'Employees (WAC)  Served at Level of Service' + ','
	+ 'Percent of Employee (WAC) Unserved' + ','
	+ 'Employees (WAC) Served By Service' + ','
	+ 'Connected Communities' + ','
	+ 'Hours of Service' + ','
	+ 'Minimum Fare' + ','
	+ 'Average Fare' + ','
	+ 'Median Fare' + ','
	+ 'Maximum Fare' + '\n';

function openDatadumpRep() {
	var d = new Date();
	var qstringd = [ pad(d.getMonth() + 1), pad(d.getDate()), d.getFullYear() ]
			.join('/');
	var keyName = setDates(qstringd);
	window.open('/TNAtoolAPI-Webapp/Datadump.html?&n=' + keyName
			+ '&dbindex=3&popYear=' + popYear);
}

function generateDatadump() {
	areaIDs = [];
	progressVal = 1;
	runProgressbar();
	reportType = $('#reportType').val();
	popYear = $('#popselect').val();
	dbIndex = $('#dbselect').val();
	sRadius = $('#Sradius').val();
	los = $('#los').val();
	dates = $('#datepicker').multiDatesPicker('getDates');
	csvContent = '';
	
	switch (parseInt(reportType)) {
	case 0: // counties
		areaType = 0;
		csvContent = 'County' + ',' + tempHeaders;		
		areaIDs = getAreaIDs('county');
		runAjaxAreas(0, areaType, 'Counties_Extended_Reports_Dump');
		break;
	case 1: // places
		areaType = 2;
		csvContent += 'Census Place' + ',' + tempHeaders;
		areaIDs = getAreaIDs('place');
		runAjaxAreas(0, areaType, 'Places_Extended_Reports_Dump');
		break;
	case 2: // congressional district
		areaType = 5;
		csvContent += 'Congressional  District' + ',' + tempHeaders;
		areaIDs = getAreaIDs('congDist');
		runAjaxAreas(0, areaType, 'Congressional_Districts_Extended_Reports_Dump');
		break;
	case 3: // urban areas
		areaType = 3;
		csvContent += 'Urban Area' + ',' + tempHeaders;
		areaIDs = getAreaIDs('urban');
		runAjaxAreas(0, areaType, 'Urban_Areas_Extended_Reports_Dump');
		break;
	case 4: // ODOT transit regions
		areaType = 4;
		csvContent += 'ODOT Transit Region' + ',' + tempHeaders;
		areaIDs = getAreaIDs('odotRegion');
		runAjaxAreas(0, areaType, 'ODOT_Transit_Regions_Extended_Reports_Dump');
		break;
	case 5: // Agencies
		csvContent += 'AgencyName' + ',' + 'Route Miles' + ',' + 'Route Stops'
				+ ',' + 'Stops Per Route Mile' + ',' + 'Service Hours' + ','
				+ 'Service Miles' + ',' + 'Urban Population Served' + ','
				+ 'Rural Population Served' + ',' + 'Employment (RAC) Served'
				+ ',' + 'Employees (WAC) Served' + ',' + 'Service Stops' + ','
				+ 'Urban Pop. Served By Service' + ','
				+ 'Rural Pop. Served By Service' + ','
				+ 'Employment (RAC) Served By Service' + ','
				+ 'Employees (WAC) Served By Service' + ',' + 'Service Days'
				+ ',' + 'Hours of Service\n';
		// getting agencies list
		agencies = [];
		$.ajax({
			type : 'GET',
			datatype : 'json',
			url : '/TNAtoolAPI-Webapp/queries/transit/allAgencies?&dbindex='
					+ dbIndex + '&username=' + getSession(),
			async : false,
			success : function(d) {
				$.each(d, function(index,item){
					agencies.push(item.id);	
				});
			},
			failure : function(e) {
				alert('The was a problem loading the list of agencies')
			}
		});
		runAjaxAgency (0, 'Agencies_Extended_Reports_Dump');
		break;
	}	
}

function runAjaxAreas(ind,areaType,fileName){
	console.log(areaIDs[ind]);
	$.ajax({
		type: 'GET',
		datatype: 'json',
		url: '/TNAtoolAPI-Webapp/queries/transit/geoAreaXR?&type='+areaType+'&areaid='+areaIDs[ind]
			+ '&x='+sRadius+'&l=2'+'&n='+keyName+'&day='+w_qstringd+'&key='+ key
			+ '&dbindex=' + dbindex + '&los' + los + '&popYear='+popYear
			+'&username='+getSession() + '&geotype=' + -1 + '&geoid=' + null,
		async: true,
		success: function(d){
			if (areaType == 4) // area names of some of the ODOT regions are null, thus they are replaced by the region ID. 
				d.AreaName = 'Region ' + areaIDs[ind];
			
			csvContent += d.AreaName + ","
				+ d.RouteMiles + ","
				+ d.StopsPersqMile + ","
				+ d.StopPerServiceMile + ","
				+ d.ServiceHours + ","
				+ d.ServiceMiles + ","
				+ d.ServiceMilesPersqMile + ","
				+ d.MilesofServicePerCapita + ","
				+ d.UPopWithinX + ","
				+ d.RPopWithinX + ","
				+ addPercent(numberconv(d.PopServed)) + ","
				+ addPercent(numberconv(d.PopServedAtLoService)) + ","
				+ d.UPopServedAtLoService + ","
				+ d.RPopServedAtLoService + ","
				+ addPercent(numberconv(d.PopUnServed)) + ","
				+ d.ServiceStops + ","
				+ d.UPopServedByService + ","
				+ d.RPopServedByService + ","
				+ d.racWithinX + ","
				+ addPercent(numberconv(d.racServed)) + ","
				+ addPercent(numberconv(d.racServedAtLoService)) + ","
				+ d.totalracServedAtLoService + ","
				+ addPercent(numberconv(d.racUnServed)) + ","
				+ d.racServedByService + ",";
				if(popYear==2010){
					csvContent += d.wacWithinX + ","
						+ addPercent(numberconv(d.wacServed)) + ","
						+ addPercent(numberconv(d.wacServedAtLoService)) + ","
						+ d.totalwacServedAtLoService + ","
						+ addPercent(numberconv(d.wacUnServed)) + ","
						+ d.wacServedByService + ",";
				}else
					csvContetn += "N/A,N/A,N/A,N/A,N/A,N/A";
				csvContent +=(d.ConnectedCommunities).replace(/County/g,'') + ","
					+ d.HoursOfService + ","
					+ d.MinFare + ","
					+ d.AverageFare + ","
					+ d.MedianFare + ","
					+ d.MaxFare + "\n";
				progressVal = Math.max(Math.floor((ind + 1) / areaIDs.length * 100),1);
				ind += 1;
				if (ind < areaIDs.length)
					setTimeout(function(){runAjaxAreas(ind,areaType,fileName);},100);
				else
					createCSV(fileName);
			}
	});
}

function runAjaxAgency(ind, fileName){
	if (ind > agencies.length)
		return false;
	console.log(agencies[ind]);
	$.ajax({
		type : 'GET',
		datatype : 'json',
		url : '/TNAtoolAPI-Webapp/queries/transit/AgencyXR?agency='
				+ agencies[ind] + '&day=' + dates + '&key=' + key + '&popYear='
				+ popYear + '&areaId=null&type=' + 0 + '&username='
				+ getSession() + '&x=' + sRadius + '&geotype=' + -1
				+ '&geoid=null&dbindex=' + dbindex,
		async : false,
		success : function(d) {
			csvContent += d.AgencyName.replace(/,/g,'') + ',' + d.RouteMiles + ','
					+ d.StopCount + ',' + d.StopPerRouteMile + ','
					+ d.ServiceHours + ',' + d.ServiceMiles + ','
					+ d.UPopWithinX + ',' + d.RPopWithinX + ','
					+ d.racWithinX + ',';
			if (popYear > 2010)
				csvContetn += 'N/A,';
			else
				csvContent += d.wacWithinX + ',';
			csvContent += d.ServiceStops + ',' + d.UPopServedByService
					+ ',' + d.RPopServedByService + ','
					+ d.racServedByService + ',' + d.wacServedByService
					+ ',';
			if (popYear > 2010)
				csvContetn += 'N/A,';
			else
				csvContent += d.wacServedByService + ',';
			csvContent += d.HoursOfService + '\n';
			progressVal = Math.max(Math.floor((ind + 1) / agencies.length * 100),1);
			ind += 1;
			if (ind < agencies.length)
				setTimeout(function(){runAjaxAgency(ind,fileName);},100);
			else
				createCSV(fileName);			
		}
	});	
}

function loadProgressBar() {	
	var progressLabel = $(".progress-label");
	$("#progressbar")
	.progressbar({
				value : false,
				change : function() {
					progressLabel
							.html('<table><tr><td>Report in progress... </td><td>'
									+ $(this).progressbar("value") + '% </td></tr></table>');
				}
			});	
	$("#progressbar").hide();	
}

function runProgressbar(){
	var prog = false;
	function progress() {
		if (progressVal == 0) {
			if (prog) {
				progressVal = 100;
				clearTimeout(timeVar);
			} else {
				progressVal = false;
			}
		} else {
			prog = true;
		}
		$("#progressbar").progressbar("value", progressVal);
		if (progressVal >= 100) {
			clearTimeout(timeVar);
			$('#progressbar').fadeOut(1000);
			setTimeout(function(){$("#progressbar").progressbar("value", 0);}, 1100);
		}
	}	
	timeVar = setInterval(progress, 100);
}

function createCSV(fileName){
	var a = document.createElement('a');
	a.href = 'data:attachment/csv,' + encodeURIComponent(csvContent);
	a.target = '_blank';
	a.download = fileName+'.csv';
	document.body.appendChild(a);
	a.click();
	a.remove();
}

function getAreaIDs(areaType){
	var areas = [];
	$.ajax({
		type : 'GET',
		datatype : 'json',
		url : '/TNAtoolAPI-Webapp/queries/transit/getAreaList?&dbindex='
				+ dbindex + '&areaType=' + areaType,
		async : false,
		success : function(d) {
			$.each(d,function(index,item){
				areas.push(item.id);
			});
			}
		});
	return areas;
}