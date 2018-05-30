// Copyright (C) 2015 Oregon State University - School of Mechanical,Industrial and Manufacturing Engineering 
//   This file is part of Transit Network Explorer Tool.
//
//    Transit Network Explorer Tool is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Transit Network Explorer Tool is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU  General Public License for more details.
//
//    You should have received a copy of the GNU  General Public License
//    along with Transit Network Explorer Tool.  If not, see <http://www.gnu.org/licenses/>.
// =========================================================================================================
//	  This script contains JavaScript variables and methods used to load Flexible Reporting Wizard 
//	  in the Transit Network Explorer Tool and all its features.
// =========================================================================================================
var numSelectedAgencies = 0;
var numSelectedAreas = 0; 
var maxAgenSelect = 3;
var maxAreaSelect = 3;
var maxDateSelect = 3;
var dates = [];
var agencies = [];
var areas = [];
var metrics={};
metrics['values'] = [];
metrics['names'] = [];
var areaType;
var reportType;
var minUrbanPop = 2500;
var maxUrbanPop = 2000000;
var filterOnUrban = false;
var wac = false;
var rac = true;

var empYears = [
		{value:"current", text:"2013"},
		{value:"2015", text:"2015 (Projection)"},
		{value:"2020", text:"2020 (Projection)"},
		{value:"2025", text:"2025 (Projection)"},
		{value:"2030", text:"2030 (Projection)"},
		{value:"2035", text:"2035 (Projection)"},
		{value:"2040", text:"2040 (Projection)"},
		{value:"2045", text:"2045 (Projection)"},
		{value:"2050", text:"2050 (Projection)"}
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
                  {value:["rural"], text:"Rural Population"},
                  {value:["urban"], text:"Urban Population"}
                  ];

var serviceMetrics = [
                  {value:["routeMiles"], text:"Route Miles"},
                  {value:["routeStops"], text:"Route Stops"},
                  {value:["stopsPerMile"], text:"Stops Per Route Mile"},
                  {value:["serviceHours"], text:"Service Hours"},
                  {value:["serviceMiles"], text:"Service Miles"},
                  {value:["serviceStops"], text:"Service Stops"}
                  ];

var empMetrics = [
                  {value:["ca01","ca02","ca03"], text:"Age"},
                  {value:["cd01","cd02","cd03","cd04"], text:"Educational Attainment"},
                  {value:["ce01","ce02","ce03"], text:"Earning"},
                  {value:["cns01","cns02","cns03","cns04","cns05","cns06","cns07","cns08","cns09","cns10",
      		            "cns11","cns12","cns13","cns14","cns15","cns16","cns17","cns18","cns19","cns20"], text:"NAICS Sectors"},
                  {value:["cr01","cr02","cr03","cr04","cr05","cr07"], text:"Race"},
                  {value:["cs01","cs02"], text:"Sex"},
                  {value: ["ct01","ct02"], text:"Ethnicity"},
                  ];

var T6Metrics = [
                  {value:["from5to17", "from18to64","above65"],
                	  name:["Age: 5-17", "Age: 18-64", "65 and older"],
                	  text:"Age"},
                  {value:["with_disability", "without_disability"],
                		  name:["With Disability", "No Disability"],
                		  text:"Disability"},
                  {value:["american_indian_and_alaska_native",
                          "asian",
                          "black_or_african_american",
                          "hispanic_or_latino",
                          "native_hawaiian_and_other_pacific_islander",
                          "other_races",
                          "two_or_more",
                          "white"],
                          name:["American Indian or Alaska Native",
                                 "Asian",
                                 "Black or African American",
                                 "Hispanic or Latino",
                                 "Native Hawaiian or Other Pacific Islander",
                                 "Other Races",
                                 "Two or More",
                                 "White"],
                          text:"Ethnicity"},
                  {value:["asian_and_pacific_island",
                        	  "other_languages",
                        	  "english",
                        	  "spanish",
                        	  "indo_european"],
                        	  name:["Asian & Pacific Islander",
                        	        "English",
                        	        "Indo European",
                        	        "Other Languages",
                        	        "Spanish"],
                        	  text:"Language"},
                  {value:["below_poverty",
                          "above_poverty"],
                          name:["Above Poverty Line",
                                "Below Poverty Line"],
                          text:"Poverty"}
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
	dates = agencies = areas = [];
	
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
			position : {
				my : "left+40 top+45",
				at : "left top",
				of : "#map"
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
	
	
	//////////////// appending list of report types ////////////////
	$('#FlexRepTypes').append('<div class="FlexRepSectionHeader"><b>1. Report Type</b><div><br>');
	$('#FlexRepTypes').append('<div id="FlexRepTypesContainer" class="FlexRepSectionContainer"><div>');
	$('#FlexRepTypesContainer').append('<span class="header2" >Select report type:</span><hr>');
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
	$('#FlexRepTypesContainer').append(html + "</form>");
	
	
	//////////////// appending parameters to select from ////////////////
	$('#FlexRepParamsHeader').append('<div class="FlexRepSectionHeader"><b>2. Report Parameters</b><div><br>');
	$('#FlexRepParamsHeader').append('<div id="FlexRepclass="FlexRepSectionHeader"><div>');
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
  		else{
  			if (dates.length < maxDateSelect)
  				dates.push(date);
  			else{
  				$('#FlexRepDate').multiDatesPicker('toggleDate', date);
  				alert('Select at most ' + maxDateSelect + ' agencies.');
  			}
  		}
  		$('#datesLen').text('(' + dates.length + ') ');
  	},
  	addDates : [new Date()]
  });
  dates.push($('#FlexRepDate').multiDatesPicker('getDates')[0]);

  
	html = 
		'<table id ="FlexRepParamsTable">'
		+ '<tr>'
    	+ '<td>&nbsp;Pop/Emp Source:</td>'
    	+ '<td><select id ="yearSel" class="FlexRepParamInput" onchange="updateEmpType(this);updateEmpMetrics(this);" disabled></td></tr>'
    	+ '<tr>'
    	+ '<td>&nbsp;Emp. Type: </td>'
    	+ '<td><select id ="empType" class="FlexRepParamInput" onchange="updateEmpType(this);" disabled>'
    	+ ' <option id="racSelect" value="RAC">RAC - Residence Area Characteristics</option>'
    	+ ' <option value="WAC">WAC - Workplace Area Characteristics</option>'    	
    	+ ' <option value="WACRAC">WAC & RAC - Residence and Workplace Area Characteristics</option>'
    	+ '</td></tr>'
    	+ '</tr>'
	
	
	html = html.concat(
		'<tr>'
    	+ '<td>&nbsp;Search Radius (mi.):</td>'
    	+ '<td><input type="text" id="flexSradius" class="FlexRepParamInput" value="0.25" onkeypress="return isNumber(event)" disabled></td>'
    	+ '</tr>'
    	+ '<tr>'
    	+ '<td>&nbsp;Min. Level of Service (times): </td>'
    	+ '<td><input type="text" id="LoS" class="FlexRepParamInput" min="1" value="2" onkeypress="return isWholeNumber(event);" disabled></td>'
    	+ '</tr>'    	
    	+ '</table>');
    $('#FlexRepParams').append(html);
    $('.FlexRepParamInput').css('width',$('.FlexRepSectionHeader').width() - document.getElementById('FlexRepParamsTable').rows[0].cells[0].offsetWidth-10+'px');
    
    
	///////////////// appending List of agencies ////////////////
	$('#FlexRepAgencies').append('<div class="FlexRepSectionHeader"><b>3. Agencies</b><div><br>');
	$('#FlexRepAgencies').append('<div id="FlexRepAgenciesContainer" class="FlexRepSectionContainer"><div>');
	$('#FlexRepAgenciesContainer').append('<span class="header2">&nbsp;Select up to ' + maxAgenSelect + ' agencies:</span><hr>');
	var agencies = [];
	var sortedAgencyList = [];
	$.ajax({
			type : 'GET',
			datatype : 'json',
			url : '/TNAtoolAPI-Webapp/queries/transit/getSelectedAgencies?&dbindex='
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
				$('#FlexRepAgenciesContainer').append(html);
			}
		});

	//////////////// appending geographical areas ////////////////
	var areas = [
			//{name:"State", id:0},
			{name:"County", id:1},
			{name:"Census Place", id:2},
			{name:"Congressional District", id:3},
			{name:"ODOT Transit Region", id:4},
			{name:"Urban Area", id:5},
	];
	
	$('#FlexRepAreas').append('<div class="FlexRepSectionHeader"><b>4. Geographical Areas</b><div><br>');
	$('#FlexRepAreas').append('<div id="FlexRepAreasContainer" class="FlexRepSectionContainer"><div>');
	
	$('#FlexRepAreasContainer').append("<div id='areaSelect' ></div>");
	html = "<span class='header2'> Select up to 3 area:</span><hr>";
	html = html.concat("<select name='GeoArea' id ='GeoArea' onchange='loadAreaOptions(this)'>");
	html = html.concat("<option disabled selected> Select</option>");
	$(areas).each(function(index,item){
		html = html.concat('<option value="'
				+ item.id + '" onchange="loadAreaOptions(this)"> '
				+ item.name + '</option>'); 
	});
	html = html.concat('</select>');
	$('#areaSelect').append(html);
	

	$('#FlexRepAreasContainer').append('<div id="areaList"></div>');
	
	//////////////// appending urban areas as additional filter ////////////////
	 $('#FlexRepUAreas').append('<div class="FlexRepSectionHeader"><b>5. Urban Areas Filter</b><div>');
	 $('#FlexRepUAreas').append('<div id="FlexRepUAreasContainer" class="FlexRepSectionContainer"></div>');
	 html = "<span class='header2'>&nbspFilter on urban areas (optional):</span><hr>";
	 html = html.concat("&nbsp<input type='checkbox' name='uAreaFilter' id='uAreaFilter' onchange='toggleUrbanFilters(this)' unchecked> Filter on urban areas<br><br>");
	 
	 
	 html = html.concat("<table id='urbanFilterTable'><tr>" +
			 "	<td><span>&nbspProjection Year:</span></td>" );
	 html = html.concat("<td><select disabled name='uAreaYear' id ='uAreaYear' class='urbanFilter' style='height:20px;width:84px;'>");
	 $.each(popYears,function(index, item){
		html = html.concat('<option value="'
			+ item.value + '" onchange="alert(this)"> '
			+ item.text + '</option>');
	 });
	 html = html.concat('</select></tr><tr>');
 	 html = html.concat("<td><span>&nbspMin. Urban Pop:</span></td>" +
	 		"	<td><input id='minUrbanPop' class='urbanFilter' min='2500' max='2000000' type='number' onkeypress='minUrbanPop=$(this).val();return isWholeNumber(event)' value='2500' style='width:80px' disabled></td>" +
	 		"</tr><tr>" +
	 		"	<td><span>&nbspMax. Urban Pop:</span></td>" +
	 		"	<td><input id='maxUrbanPop' class='urbanFilter' min='2500' max='2000000' type='number' onkeypress='maxUrbanPop=$(this).val();return isWholeNumber(event)' value='2000000' style='width:80px' disabled></td>" +
	 		"</tr></table>");
	 $('#FlexRepUAreasContainer').append(html);
}

/**
 * check if all required input is provided by the 
 * user and opens up the report page in a new tab. 
 */
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

/**
 * populates list of geographical areas areas
 * @param input
 */
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

/**
 * method called for select/deselect all
 * @param input
 */
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

/**
 * updates flags as urban filter is toggled 
 */
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

/**
 * updates the array holding selected agencies' list and makes
 * sure the number of selected agencies does not exceed the limit.
 * @param input
 */
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
}

/**
 * displays the list of parameter based on selecter report. 
 * @param input
 */
function showParams(input){
	// Disabling all inputs and removing list of years.
	$('.FlexRepParamInput').prop('disabled',true);
	$('#yearSel').find('.yearOption').remove();
	$('#yearSel').find('option').prop('selected',true);
	$('#FlexRepMetrics').empty();
	metrics = [];
	reportType = $(input).val();
	
	// Enabling the inputs and showing metric options based on selected report type.
	switch(parseInt($(input).val())) {
    case 0: // Transit Services
    	$('#accordion').accordion( "option", "disabled", false );
    	appendMetrics(serviceMetrics, 'FlexRepMetrics');
    	break;
    case 1: // Population  
    	$('#accordion').accordion( "option", "disabled", false );
    	$('.FlexRepParamInput').prop('disabled',false);
    	$('#empType').prop('disabled',true);
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
    	$('#empType').prop('disabled',true);
    	$('#yearSel').append(getYearsOption($(input).val()));
    	appendMetrics(T6Metrics, 'FlexRepMetrics');
        break;
	};
	
}

/**
 * populates the year selection drop down based on selected report 
 * @param input
 * @returns {String}
 */
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

/**
 * appends metrics based on selected reports
 * @param input
 * @param objID
 */
function appendMetrics(input,objID){
	$.each(input,function(index,item){
		$('#'+objID).append('&nbsp;<input type="checkbox" class="metricCheckbox" name="'
				+ item.text
				+ '" value="'
				+ item.value
				+ '"names="'
				+ item.name
				+ '" onchange="updateMetrics(this)"> '
				+ item.text + '<br>');
	});
}



/**
 * keeps track of selected areas and makes sure the number of selected 
 * areas does not exceed the limit.
 * @param input
 */
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

/**
 * updates the array of selected metrics based on user selection
 * @param input
 */
function updateMetrics( input ){
	if(input.checked){
		if (metrics.values == undefined){
			metrics.values = [];
			metrics.names = [];
		}
		metrics.values = metrics.values.concat(input.value.split(","));
		metrics.names = metrics.names.concat($(input).attr('names').split(","))
	}else{
		$.each(input.value.split(","), function(index, item){
			metrics.values.splice(metrics.values.indexOf(item),1);
		});
		$.each($(input).attr('names').split(","), function(index, item){
			metrics.names.splice(metrics.names.indexOf(item),1);
		});
	}
}

/**
 * disables the employment metrics that there is no projection data for them
 * in the database, i.e., all metrics except for NAICS sectors.
 * @param input
 */
function updateEmpMetrics(input){
	var year = $(input).val();
	if (reportType == 2){ // For employment report
		if ( year != 'current'){
			$('#FlexRepMetrics').find('.metricCheckbox').each(function(index, item){
				if(item.name != "NAICS Sectors"){
					$(item).prop('disabled',true);
					$(item).prop('checked', false);
				}
			});
		}else{
			$('#FlexRepMetrics').find('.metricCheckbox').each(function(index, item){
					$(item).prop('disabled',false);
			});
		}	
	}
}

/**
 * updates flags for employment datasets (WAC/RAC), based on selection.
 * for 
 * @param input
 */
function updateEmpType(input){
	if (input.id == 'yearSel'){
		if(reportType == 2){
			if (input.value == 'current'){
				$("select option[value*='WAC']").prop('disabled',false);
				$("select option[value*='WACRAC']").prop('disabled',false);
			}else{
				$("select option[value*='WAC']").prop('disabled',true);
				$("select option[value*='WACRAC']").prop('disabled',true);
				$("#empType").val("RAC");
				rac = true; wac = false;
			}		
		}
	}else if (input.id == 'empType'){
		var empType = input.value;
		if ( empType == 'WACRAC'){
			wac = rac = true;
		} else if ( empType == 'WAC'){
			wac = true;
			rac = false;
		} else {
			wac = false;
			rac = true;
		}
	}
}