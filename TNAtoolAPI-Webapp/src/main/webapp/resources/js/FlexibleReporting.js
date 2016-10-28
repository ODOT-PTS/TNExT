var numSelectedAgencies = 0;
var numSelectedAreas = 0; 
var maxAgenSelect = 3;
var maxAreaSelect = 3;
var dates = [];
var agencies = [];
var areas = [];
var metrics = [];
var areaType;
var reportType;
var year = 'population';
var minUrbanPop = 0;
var maxUrbanPop = 2000000;
var filterOnUrban = false;

var empYears = [
		{value:"population", text:"2013"},
		{value:"population2015", text:"2015 (Projection)"},
		{value:"population2020", text:"2020 (Projection)"},
		{value:"population2025", text:"2025 (Projection)"},
		{value:"population2030", text:"2030 (Projection)"},
		{value:"population2035", text:"2035 (Projection)"},
		{value:"population2040", text:"2040 (Projection)"},
		{value:"population2045", text:"2045 (Projection)"},
		{value:"population2050", text:"2050 (Projection)"}
];

var popYears = [
		{value:"population", text:"2010"},
		{value:"population2015", text:"2015 (Projection)"},
		{value:"population2020", text:"2020 (Projection)"},
		{value:"population2025", text:"2025 (Projection)"},
		{value:"population2030", text:"2030 (Projection)"},
		{value:"population2035", text:"2035 (Projection)"},
		{value:"population2040", text:"2040 (Projection)"},
		{value:"population2045", text:"2045 (Projection)"},
		{value:"population2050", text:"2050 (Projection)"}
];

var T6Years = [
        		{value:"current", text:"2015"}
        ];

var popMetrics = [
                  {value:"rural", text:"Rural Population"},
                  {value:"urban", text:"Urban Population"}
                  ];

var serviceMetrics = [
                  {value:"routeMiles", text:"Route Miles"},
                  {value:"routeStops", text:"Route Stops"},
                  {value:"stopsPerMile", text:"Stops Per Route Mile"},
                  {value:"serviceHours", text:"Service Hours"},
                  {value:"serviceMiles", text:"Service Miles"},
                  {value:"serviceStops", text:"Service Stops"}
                  ];

var empMetrics = [
                  {value:"age", text:"Age"},
                  {value:"edu", text:"Educational Attainment"},
                  {value:"earning", text:"Earning"},
                  {value:"sector", text:"NAICS sectors"},
                  {value:"race", text:"Race"},
                  {value:"sex", text:"Sex"},
                  {value:"ethnicity", text:"Ethnicity"},
                  ];

var T6Metrics = [
                  {value:"age", text:"Age"},
                  {value:"disability", text:"Disability"},
                  {value:"ethnicity", text:"Ethnicity"},
                  {value:"language", text:"Language"},
                  {value:"poverty", text:"Poverty"}
                  ];

function flexRepDialog() {
	$('#FlexRepUAreas').empty();
	$('#FlexRepAreas').empty();
	$('#FlexRepAgencies').empty();
	$('#FlexRepTypesFlexRepParamsMetrics').empty();
	$('#FlexRepParamsHeader').empty();
	$('#FlexRepSectionHeader').empty();
	$('#FlexRepParams').empty();
	$('#FlexRepSectionHeader').empty();
	$('#FlexRepTypes').empty();
	
	// loading dialog box
	$(function() {
		$("#flexRepDialog").dialog({
			width:"auto",
			resizable: false,
			modal: true,
			show : {
				effect : "slide",
				duration : 300
			},
			open:function(){
				numSelectedAgencies = 0;
				 numSelectedAreas = 0
				 dates = [];
				 agencies = [];
				 areas = []
			},
			close: function(){
				 numSelectedAgencies = 0;
				 numSelectedAreas = 0
				 dates = [];
				 agencies = [];
				 areas = []
				},
		});
	});
	$( "#flexRepDialog" ).css( "max-width", 0.85*$(window).width() );
	$( "#flexRepDialog" ).dialog("option", "position",{ my: "center", at: "center", of: window })
	
	
	//////////////// appending list of report types ////////////////
	$('#FlexRepTypes').append('<div class="FlexRepSectionHeader"><b>1. Report Type</b><div><br>');
	$('#FlexRepTypes').append('<span class="header2" >Select report type:</span><hr>');
	var repotTypes = [
	 			{name:"Transit Service", id:0},
	 			{name:"Population", id:1},
	 			{name:"Employment", id:2},
	 			{name:"Title VI", id:3}
	 	];
	html = "<form>";
	$(repotTypes).each(function(index,item){
		html = html.concat(' <input type="radio" name="reportType" class="reportType" value="' + item.id + '" onchange="showParams(this)">&nbsp;' + item.name + '<br>'); 
	});
	$('#FlexRepTypes').append(html + "</form>");
	
	
	//////////////// appending parameters to select from ////////////////
	$('#FlexRepParamsHeader').append('<div class="FlexRepSectionHeader"><b>2. Report Parameters</b><div><br>');
	html = '&nbsp;Date:<br><div id="accordion" style="text-align:center">'
  	  +'<h3><span id="datesLen">(1)&nbsp;</span>date(s) selected</h3>'
  	  +'<div id="FlexRepDate"></div>'
  	  +'</div>';
  $('#FlexRepParams').append(html);
  $('#accordion').accordion({
  	  active: 1,
  	  collapsible: true,
  	  heightStyle: "content",
  });
  
  $('#FlexRepDate').multiDatesPicker({
  	onSelect : function(date) {
  		if (dates.includes(date))
  			dates.splice(dates.indexOf(date),1);
  		else
  			dates.push(date);
  		$('#datesLen').text('(' + dates.length + ') ');
  	},
  	addDates : [new Date()]
  });
  dates.push($('#FlexRepDate').multiDatesPicker('getDates')[0]);

  
	html = 
		'<table id ="FlexRepParamsTable">'
		+ '<tr>'
    	+ '<td>&nbsp;Pop/Emp Source:</td>'
    	+ '<td><select id ="yearSel" class="FlexRepParamInput" onchange="year=$(this).val();"disabled></td>';
	html = html.concat(
    	'</tr>'
		+ '<tr>'
    	+ '<td>&nbsp;Search Radius (mi.):</td>'
    	+ '<td><input type="text" id="Sradius" class="FlexRepParamInput" value="0.25" onkeypress="return isNumber(event)" disabled></td>'
    	+ '</tr>'
    	+ '<tr>'
    	+ '<td>&nbsp;Min. Level of Service (times): </td>'
    	+ '<td><input type="text" id="LoS" class="FlexRepParamInput" min="1" value="2" onkeypress="return isWholeNumber(event);" disabled></td>'
    	+ '</tr>'
    	+ '</table>');
    $('#FlexRepParams').append(html);
    $('.FlexRepParamInput').css('width',$('.FlexRepSectionHeader').width() - document.getElementById('FlexRepParamsTable').rows[0].cells[0].offsetWidth-10+'px');
    
    
	///////////////// appending List of agencies ////////////////
	var header = '<div class="FlexRepSectionHeader"><b>3. Agencies</b><div><br>';
	$('#FlexRepAgencies').append(header);
	$('#FlexRepAgencies').append('<span class="header2">&nbsp;Select up to ' + maxAgenSelect + ' agencies:</span><hr>');
	var agencies = [];
	var sortedAgencyList = [];
	$.ajax({
			type : 'GET',
			datatype : 'json',
			url : '/TNAtoolAPI-Webapp/queries/transit/allAgencies?&dbindex='
					+ dbindex + '&username=' + getSession(),
			async : false,
			success : function(d) {
				$.each(d, function(index, item) {
					agencies[item.id] = item.name;
					sortedAgencyList.push({
						id : item.id,
						name : item.name
					});
				});
				sortedAgencyList.sort(function(a, b) {
					return (a.name > b.name) ? 1 : ((b.name > a.name) ? -1
							: 0);
				});
				var html = '';
				$.each(
					sortedAgencyList,
					function(i, item) {
						html = html
								.concat('&nbsp;<input type="checkbox" class="agencyCheckbox" name="'
										+ item.name
										+ '" value="'
										+ item.id
										+ '" onchange=agencySelCheck(this)> '
										+ item.name + '<br>');
					});
				$('#FlexRepAgencies').append(html);
			}
		});

	//////////////// appending geographical areas ////////////////
	var areas = [
			{name:"State", id:0},
			{name:"County", id:1},
			{name:"Census Place", id:2},
			{name:"Congressional District", id:3},
			{name:"ODOT Transit Region", id:4},
			{name:"Urban Area", id:5},
	];
	
	$('#FlexRepAreas').append('<div class="FlexRepSectionHeader"><b>4. Geographical Areas</b><div><br>');
	$('#FlexRepAreas').append("<div id='areaSelect' ></div>");
	html = "<span class='header2'> Select area type:</span><hr>";
	html = html.concat("<select name='GeoArea' id ='GeoArea' onchange='loadAreaOptions(this)'>");
	html = html.concat("<option disabled selected> Select</option>");
	$(areas).each(function(index,item){
		html = html.concat('<option value="'
				+ item.id + '" onchange="loadAreaOptions(this)"> '
				+ item.name + '</option>'); 
	});
	html = html.concat('</select>');
	$('#areaSelect').append(html);
	

	$('#FlexRepAreas').append('<div id="areaList"></div>');
	
	//////////////// appending urban areas as additional filter ////////////////
	 html = '<div class="FlexRepSectionHeader"><b>5. Urban Areas Filter</b><div>';	 
	 html = html.concat("<span class='header2'> Filter on urban areas:</span><hr>");
	 html = html.concat("<input type='checkbox' name='uAreaFilter' onchange='toggleUrbanFilters(this)' unchecked> Filter on urban areas<br>");
	 html = html.concat("<table id='urbanFilterTable'><tr>" +
	 		"	<td><span>Min. Urban Pop:</span></td>" +
	 		"	<td><input id='minUrbanPop' class='urbanFilter' min='0' max='2000000' type='number' onkeypress='minUrbanPop=$(this).val();return isWholeNumber(event)' value='0' style='width:80px' disabled></td>" +
	 		"</tr><tr>" +
	 		"	<td><span>Max. Urban Pop:</span></td>" +
	 		"	<td><input id='maxUrbanPop' class='urbanFilter' min='0' max='2000000' type='number' onkeypress='maxUrbanPop=$(this).val();return isWholeNumber(event)' value='2000000' style='width:80px' disabled></td>" +
	 		"</tr></table>");
	 $('#FlexRepUAreas').append(html);
}




function loadAreaOptions(input){
	$("#areaList").empty();
	areas = [];
	numSelectedAreas = 0;
	switch(parseInt($(input).val())) {
    case 0:
        areaType = "state";
        break;
    case 1:
    	areaType = "county";
        break;
    case 2:
    	areaType = "place";
        break;
    case 3:
    	areaType = "congDist";
        break;
    case 4:
    	areaType = "odotRegion";
        break;
    case 5:
    	areaType = "urban";
        break;
	};

	$.ajax({
		type : 'GET',
		datatype : 'json',
		url : '/TNAtoolAPI-Webapp/queries/transit/getAreaList?&dbindex='
				+ dbindex + '&areaType=' + areaType,
		async : false,
		success : function(d) {
			html = '';
			$.each(d, function(index, item){
				html = html.concat('&nbsp;<input type="checkbox" class="areaCheckbox" name="'
										+ item.name
										+ '" value="'
										+ item.id
										+ '" onchange=areaSelCheck(this)> '
										+ item.name + '<br>');
			})
			$("#areaList").append(html);
		}	
	});
}

function selectAll(input){
	if (input.value == "area")
		_class = ".areaCheckbox";
	
	if(input.checked){
		areas = [];
		$(_class).each(function(index, item){
			$(item).prop('checked', true);
			areas.push(item.value);
		});
	}else{
		$(_class).each(function(index, item){
			$(item).prop('checked', false);
		});
		areas = [];
	}
}

function toggleUrbanFilters(input){
	if(input.checked){
		flag = false;
		filterOnUrban = true;
	}
	else{
		flag = true;
		filterOnUrban = false;
	}
		
	$(".urbanFilter").each(function(index, item){
		$(item).attr('disabled', flag);
	});
}

function agencySelCheck(input){
	agencyID = input.value;
	if(input.checked){
		numSelectedAgencies++;
		agencies.push(agencyID);
	}else{
		numSelectedAgencies--;
		agencies.splice(agencies.indexOf(agencyID));
	}
	
	if (numSelectedAgencies > maxAgenSelect){
		$(input).prop('checked', false);
		numSelectedAgencies--;
		agencies.splice(agencies.indexOf(agencyID));
		alert('Select at most ' + maxAgenSelect + ' agencies.');
	}
	console.log(agencies);
}

function showParams(input){
	// Disabling all inputs and removing list of years.
	$('.FlexRepParamInput').prop('disabled',true);
	$('#yearSel').find('.yearOption').remove();
	$('#yearSel').find('option').prop('selected',true);
	$('#FlexRepMetrics').empty();
	reportType = $(input).val();
	
	// Enabling the inputs and showin metric options based on selected report type.
	switch(parseInt($(input).val())) {
    case 0: // Transit Services
    	$('#accordion').accordion( "option", "disabled", false );
    	appendMetrics(serviceMetrics, 'FlexRepMetrics');
    	break;
    case 1: // Population  
    	$('#accordion').accordion( "option", "disabled", false );
    	$('.FlexRepParamInput').prop('disabled',false);
    	$('#yearSel').append(getYearsOption($(input).val()));
    	appendMetrics(popMetrics, 'FlexRepMetrics');
    	break;
    case 2: // Employment
    	$('#accordion').accordion( "option", "disabled", false );
    	$('.FlexRepParamInput').prop('disabled',false);
    	$('#yearSel').append(getYearsOption($(input).val()));
    	appendMetrics(empMetrics, 'FlexRepMetrics');
        break;
    case 3: // Title VI
    	$('#accordion').accordion( "option", "disabled", false );
    	$('.FlexRepParamInput').prop('disabled',false);
    	$('#yearSel').append(getYearsOption($(input).val()));
    	appendMetrics(T6Metrics, 'FlexRepMetrics');
        break;
	};
	
}

function getYearsOption(input){
	var variable;
	if (input == 1)
		variable = popYears;
	else if (input == 2)
		variable = empYears;
	else if (input == 3)
		variable = T6Years;
	
	html = '';
	$.each(variable,function(index, item){
		html += "<option class='yearOption' value='" + item.value + "'> " + item.text + "</option>";
	});
	return html;
}

function appendMetrics(input,objID){
	$.each(input,function(index,item){
		$('#'+objID).append('&nbsp;<input type="checkbox" class="metricCheckbox" name="'
				+ item.text
				+ '" value="'
				+ item.value
				+ '" onchange="updateMetrics(this)"> '
				+ item.text + '<br>');
	});
}

function openFlexRepTable(){
	var reportTypeFlag = false;
	var datesFlag = false;
	var losFlag = false;
	var sRadiusFlag = false;
	var agenceisFlag = false;
	var areasFlag = false;
	var metricsFlag = false;
	var yearFlag = false;
	var reportType;
	
	if (dates.length>0 && agencies.length>0 && areas.length>0)
		datesFlag = agenceisFlag = areasFlag = true;
	
	if ($("input[name='reportType']:checked").val() != undefined){
		reportTypeFlag = true;
		reportType = $("input[name='reportType']:checked").val();
	}
		
	
	if ( $('#LoS').val() != undefined && $('#Sradius').val() != undefined )
		sRadiusFlag = losFlag = true;
	
	if ($('.metricCheckbox').length == 0)
		metricsFlag = true;
	else{
		$('.metricCheckbox').each(function(index, item){
			if ($(item).is(":checked"))
				metricsFlag = true;
		})
	}
	
	if ($('#yearSel').val())
		yearFlag = true;
	
	if (reportType == 0)
		if (reportTypeFlag && datesFlag && agenceisFlag && areasFlag)
			window.open('/TNAtoolAPI-Webapp/FlexRep.html');
		else
			alert('Enter all required inputs.');
	else
		if (reportTypeFlag && yearFlag && datesFlag && agenceisFlag && areasFlag && losFlag && sRadiusFlag && metricsFlag)
			window.open('/TNAtoolAPI-Webapp/FlexRep.html');
		else
			alert('Enter all required inputs.');
}

function areaSelCheck(input){
	areaID = input.value;
	if(input.checked){
		areas.push(areaID)
		numSelectedAreas++;
	}else{
		areas.splice(areas.indexOf(areaID),1);
		numSelectedAreas--;
	}
	
	if (numSelectedAreas > maxAreaSelect){
		$(input).prop('checked', false);
		numSelectedAreas--;
		areas.splice(agencies.indexOf(areaID));
		alert('Select at most ' + maxAreaSelect + ' areas.');
	}
}

function updateMetrics(input){
	metric = input.value;
	if(input.checked){
		metrics.push(metric);
	}else{
		metrics.splice(metics.indexOf(metric),1);
	}
}