var datasources = {
	0 : {
		survey : null,
		dataset : "General Transit Feed Specification (GTFS)",
		table : null,
		common : {
			flag:true, 
			exceptions: [
			"Park and Ride Summary Report",
			"Park and Ride Extended Report"
			]
			},
		reports : []
	},
	1 :
	{
		survey : "American Community Survey",
		dataset : "2009-2013 American Community Survey 5-Year Estimates",
		table : "B11001 - HOUSEHOLD TYPE (INCLUDING LIVING ALONE)",
		common : {
			flag:false, 
			exceptions: []
			},
		reports : ["Title VI Report"]
	},
	2 :
	{
		survey : "American Community Survey",
		dataset : "2009-2013 American Community Survey 5-Year Estimates",
		table : "B19037 - AGE OF HOUSEHOLDER BY HOUSEHOLD INCOME IN THE PAST 12 MONTHS (IN 2013 INFLATION-ADJUSTED DOLLARS)",
		common : {
			flag:false, 
			exceptions: []
			},
		reports : ["Title VI Report"]
	},
	3 :
	{
		survey : "American Community Survey",
		dataset : "2009-2013 American Community Survey 5-Year Estimates",
		table : "B03002 - HISPANIC OR LATINO ORIGIN BY RACE",
		common : {
			flag:false, 
			exceptions: []
			},
		reports : ["Title VI Report"]
	},
	4 :
	{
		survey : "American Community Survey",
		dataset : "2009-2013 American Community Survey 5-Year Estimates",
		table : "B16004 - AGE BY LANGUAGE SPOKEN AT HOME BY ABILITY TO SPEAK ENGLISH FOR THE POPULATION 5 YEARS AND OVER",
		common : {
			flag:false, 
			exceptions: []
			},
		reports : ["Title VI Report"]
	},
	5 :
	{
		survey : "American Community Survey",
		dataset : "2009-2013 American Community Survey 5-Year Estimates",
		table : "B17021 - POVERTY STATUS OF INDIVIDUALS IN THE PAST 12 MONTHS BY LIVING ARRANGEMENT",
		common : {
			flag:false, 
			exceptions: []
			},
		reports : ["Title VI Report"]
	},
	6 :
	{
		survey : "American Community Survey",
		dataset : "2009-2013 American Community Survey 5-Year Estimates",
		table : "B18101 - SEX BY AGE BY DISABILITY STATUS",
		common : {
			flag:false, 
			exceptions: []
			},
		reports : ["Title VI Report"]
	},	
	6 :
	{
		survey : "United States Census Bureau",
		dataset : "LEHD Origin-Destination Employment Statistics (LODES) Dataset Structure Format Version 7.0",
		table : "Residence Area Characteristics (RAC)",
		common : {
			flag:false, 
			exceptions: []
			},
		reports : ["Employment Report"]
	},
	7 :
	{
		survey : "United States Census Beureau",
		dataset : "LEHD Origin-Destination Employment Statistics (LODES) Dataset Structure Format Version 7.0",
		table : "Workplace Area Characteristics (WAC)",
		common : {
			flag:false, 
			exceptions: []
			},
		reports : ["Employment Report"]
	},
	8 :
	{
		survey : "United States Census Beureau",
		dataset : "2010 Census Data",
		table : null,
		common : {
			flag:true, 
			exceptions: [
			"Employment Report",
			"Park and Ride Summary Report",
	        "Park and Ride Extended Report",
			"Title VI Report",
			"Employment Report",
			"Connected Agencies Summary Report",
			"Connected Agencies Extended Report",
			"Connected Networks Summary Report",
			"Transit Agency Summary Report"
			]
			},
		reports : []
	},
	9 :
	{
		survey : "Oregon Department of Transportation (ODOT)",
		dataset : "ParkRide_Inventory_2015_forGIS",
		table : null,
		common : {
			flag:false, 
			exceptions: []
			},
		reports : [
		"Park and Ride Summary Report",
		"Park and Ride Extended Report",
		"Transit Hubs Summary Report"
		]
	}	
};