var metricDef = [
		// 0. State Summary Report
		{
			"report" : "Statewide Summary Report",
			"metric" : "State",
			"definition" : "Name of the state"
		},
		{
			"report" : "Statewide Summary Report",
			"metric" : "Land Area",
			"definition" : "Total Land area of the state in square miles."
		},
		{
			"report" : "Statewide Summary Report",
			"metric" : "Total Population",
			"definition" : "Total population of the state."
		},
		{
			"report" : "Statewide Summary Report",
			"metric" : "Urban Population",
			"definition" : "Total population of the state living in urban areas."
		},
		{
			"report" : "Statewide Summary Report",
			"metric" : "Rural Population",
			"definition" : "Total population of the state living in non-urban areas."
		},
		{
			"report" : "Statewide Summary Report",
			"metric" : "Employment (RAC)",
			"definition" : "Total Employment (RAC) of the geographic area."
		},
		{
			"report" : "Statewide Summary Report",
			"metric" : "Employees (WAC)",
			"definition" : "Total Employees (WAC) of the geographic area."
		},
		{
			"report" : "Statewide Summary Report",
			"metric" : "Transit Agencies Count",
			"definition" : "Count of transit agencies operating in the state."
		},
		{
			"report" : "Statewide Summary Report",
			"metric" : "Routes Count",
			"definition" : "Total number of routes operated by the transit agency."
		},
		{
			"report" : "Statewide Summary Report",
			"metric" : "Stops Count",
			"definition" : "Total number of stops operated by the transit agency."
		},
		{
			"report" : "Statewide Summary Report",
			"metric" : "Urbanized Areas Count",
			"definition" : "Count of urbanized areas within the state."
		},
		{
			"report" : "Statewide Summary Report",
			"metric" : "Congressional Districts Count",
			"definition" : "Count of congressional districts within the state."
		},
		{
			"report" : "Statewide Summary Report",
			"metric" : "ODOT Transit Regions Count",
			"definition" : "Count of ODOT transit regions within the state."
		},
		{
			"report" : "Statewide Summary Report",
			"metric" : "Census Places Count",
			"definition" : "Count of census designated places within the state."
		},
		{
			"report" : "Statewide Summary Report",
			"metric" : "Counties Count",
			"definition" : "Count of Counties within the state."
		},
		{
			"report" : "Statewide Summary Report",
			"metric" : "Census Tracts Count",
			"definition" : "Count of census tracts within the state."
		},

		// 1. Transit Agency Summary Report
		{
			"report" : "Transit Agencies Summary Report",
			"metric" : "Agency ID",
			"definition" : "Identification number reported in the transit agency GTFS feed.",
		},
		{
			"report" : "Transit Agencies Summary Report",
			"metric" : "Agency Name",
			"definition" : "Agency name reported in the transit agency GTFS feed.",
		},
		{
			"report" : "Transit Agencies Summary Report",
			"metric" : "Phone #",
			"definition" : "Phone number to contact the transit agency.",
		},
		{
			"report" : "Transit Agencies Summary Report",
			"metric" : "Total Routes",
			"definition" : "Total number of routes operated by the transit agency.",
		},
		{
			"report" : "Transit Agencies Summary Report",
			"metric" : "Total Stops",
			"definition" : "Total number of stops operated by the transit agency.",
		},
		{
			"report" : "Transit Agencies Summary Report",
			"metric" : "Geographic Areas",
			"definition" : "Count of geographic areas that the transit agency operates within.",
		},
		{
			"report" : "Transit Agencies Summary Report",
			"metric" : "Fare",
			"definition" : "If available, this field points to the fare information published by the transit agency on its web site.",
		},
		{
			"report" : "Transit Agencies Summary Report",
			"metric" : "Service Start Date",
			"definition" : "The earliest service date specified in the transit agency feed in YYYYMMDD format.",
		},
		{
			"report" : "Transit Agencies Summary Report",
			"metric" : "Service End Date",
			"definition" : "The latest service date specified in the transit agency feed in YYYYMMDD format.",
		},
		{
			"report" : "Transit Agencies Summary Report",
			"metric" : "Feed Information",
			"definition" : "If available, this field points to the feed information such as name, version, publisher name and publisher URL.",
		},

		// 2. Transit Agency Extended Report
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Route Miles",
			"definition" : "Summation of the lengths of the longest route variant for all routes that the transit agency serves."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Route Stops",
			"definition" : "Total number of stops served by the transit agency."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Stops Per Route Mile",
			"definition" : "Number of stops in the agency\'s routes divided by Route Miles."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Service Hours",
			"definition" : "Total hours the transit agency spends on serving all round trips of routes. Service hours may be calculated for a specific date or a set of dates specified using the calendar. Reported number are cumulative over the selected dates."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Service Miles",
			"definition" : "Total miles driven by a transit agency over all round trips of a route. Service miles may be calculated for a specific date or a set of dates specified using the calendar. Reported number are cumulative over the selected dates."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Urban Population Served",
			"definition" : "Summation of unduplicated urban population within X-mile radius (i.e., stop distance) of all stops that the transit agency serves."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Rural Population Served",
			"definition" : "Summation of unduplicated rural population within X-mile radius (i.e., stop distance) of all stops that the transit agency serves."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Employment Served (RAC)",
			"definition" : "Summation of  employed people residing within X-mile radius (i.e., stop distance) of all stops that the transit agency serves. Each person is counted once (unduplicated)."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Employees Served (WAC)",
			"definition" : "Summation of employed people working within X-mile radius (i.e., stop distance) of all stops that the transit agency serves. Each person is counted once (unduplicated)."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Service Stops",
			"definition" : "Number of trips scheduled at a stop in a route. The service stops for a route is calculated as its stop count multiplied by the number of visits per stop. Reported number are cumulative over the selected dates."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Urban Population Served By Service",
			"definition" : "Total unduplicated urban population impacted within an X-mile radius (i.e., stop distance) of all stops that the transit agency serves. Urban population served by service is calculated as service stops multiplied by the unduplicated urban population within an X-mile radius (i.e., stop distance) of all stops that the transit agency serves. Reported number is cumulative over the selected dates."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Rural Population Served By Service",
			"definition" : "Total unduplicated rural population impacted within an X-mile radius (i.e., stop distance) of all stops that the transit agency serves. Rural population served by service is calculated as service stops multiplied by the unduplicated rural population within an X-mile radius (i.e., stop distance) of all stops that the transit agency serves. Reported number is cumulative over the selected dates."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Employment Served By Service (RAC)",
			"definition" : "Total Employment (RAC) impacted within an X-mile radius (i.e., stop distance) of all stops that the transit agency serves. Employment (RAC) served by service is calculated as service stops multiplied by the unduplicated Employment (RAC) within an X-mile radius (i.e., stop distance) of all stops that the transit agency serves. Reported number is cumulative over the selected dates."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Employees Served By Service (WAC)",
			"definition" : "Total Employees (WAC)  impacted within an X-mile radius (i.e., stop distance) of all stops that the transit agency serves. Employees (WAC) served by service is calculated as service stops multiplied by the unduplicated Employees (WAC) within an X-mile radius (i.e., stop distance) of all stops that the transit agency serves. Reported number is cumulative over the selected dates."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Service Days",
			"definition" : "Set of days (from the selected days) in which at least one trip is served by the selected transit agency."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Hours of Service",
			"definition" : "Earliest and latest arrival and departure times of all stops served by the transit agency."
		},

		// 3. Counties Summary Report
		{
			"report" : "Counties Summary Report",
			"metric" : "Geo ID",
			"definition" : "Identification number associated with the geographic area."
		},
		{
			"report" : "Counties Summary Report",
			"metric" : "ODOT Region ID",
			"definition" : "ODOT transit region id associated with the geographic area."
		},
		{
			"report" : "Counties Summary Report",
			"metric" : "ODOT Transit Region",
			"definition" : "ODOT transit region name associated with the geographic area."
		},
		{
			"report" : "Counties Summary Report",
			"metric" : "Population",
			"definition" : "Total population of the geographic area."
		},
		{
			"report" : "Counties Summary Report",
			"metric" : "Employment (RAC)",
			"definition" : "Total number of employed people residing in the geographic area."
		},
		{
			"report" : "Counties Summary Report",
			"metric" : "Employees (WAC)",
			"definition" : "Total number of employed people working in the geographic area."
		},
		{
			"report" : "Counties Summary Report",
			"metric" : "Land Area",
			"definition" : "Total land area of the geographic area in square miles."
		},
		{
			"report" : "Counties Summary Report",
			"metric" : "Water Area",
			"definition" : "Total water area of the geographic area in square miles."
		},
		{
			"report" : "Counties Summary Report",
			"metric" : "Total Agencies",
			"definition" : "Total number of transit agencies operating in the given geographic area."
		},
		{
			"report" : "Counties Summary Report",
			"metric" : "Total Routes",
			"definition" : "Total number of routes serving stops in the given geographic area."
		},
		{
			"report" : "Counties Summary Report",
			"metric" : "Total Stops",
			"definition" : "Total number of stops within the given geographic area."
		},
		{
			"report" : "Counties Summary Report",
			"metric" : "Urban Areas",
			"definition" : "Total number of Urban Areas within the geographic area."
		},
		{
			"report" : "Counties Summary Report",
			"metric" : "Tracts",
			"definition" : "Total number of census tracts within the geographic area."
		},

		// 4. Counties Extended Report
		{
			"report" : "County Extended Report",
			"metric" : "Geo ID",
			"definition" : "Identification number associated with the geographic area.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Name",
			"definition" : "Name of the geographic area.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Route Miles",
			"definition" : "Summation of the lengths of the longest trips within the given geographic area.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Stops Per Square Mile",
			"definition" : "Stop count in the given geographic area divided by the area of the geographic area.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Stops Per Service Mile",
			"definition" : "Stop count in the given geographic area divided by service miles.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Service Hours",
			"definition" : "Total hours a transit agency spends on serving all round trips of routes within the given geographic area. Service hours may be calculated for a specific date or a set of dates specified using the calendar. Reported number are cumulative over the selected dates.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Service Miles",
			"definition" : "Total miles driven over all round trips of routes within the given geographic area. Service miles may be calculated for a specific date or a set of dates specified using the calendar. Reported number are cumulative over the selected dates.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Service Miles Per Square Mile",
			"definition" : "Service miles divided by the area of the geographic area.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Miles of Service Per Capita",
			"definition" : "Service miles divided by the population of the geographic area.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Urban Population Served (Unduplicated)",
			"definition" : "Summation of unduplicated urban population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Rural Population Served",
			"definition" : "Summation of unduplicated rural population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Percent of Population Served",
			"definition" : "Summation of unduplicated population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area divided by total population of the given geographic area.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Percent of Population Served at Level of Service",
			"definition" : "Summation of unduplicated population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service divided by total population of the given geographic area.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Urban Population Served at Level of Service",
			"definition" : "Summation of unduplicated urban population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Rural Population Served at Level of Service",
			"definition" : "Summation of unduplicated rural population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Percent of Population Unserved",
			"definition" : "100 minus percent of population served.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Service Stops",
			"definition" : "Total stops within the given geographic area multiplied by the number of times each stop is being served for the given date(s). Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Urban Population Served By Service",
			"definition" : "Total unduplicated urban population impacted within an X-mile radius (i.e., stop distance) of all stops within the given geographic area. Urban population served by service is calculated as service stops multiplied by the unduplicated urban population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area for every trip. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Rural Population Served By Service",
			"definition" : "Total unduplicated rural population impacted within an X-mile radius (i.e., stop distance) of all stops within the given geographic area. Rural population served by service is calculated as service stops multiplied by the unduplicated rural population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area for every trip. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Employment Served (RAC)",
			"definition" : "Summation of unduplicated  employment(RAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Percent of Employment Served (RAC)",
			"definition" : "Summation of unduplicated employment (RAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area divided by total employment (RAC) of the given geographic area.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Percent of Employment  Served at Level of Service (RAC)",
			"definition" : "Summation of unduplicated employment (RAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service divided by total employment (RAC) of the given geographic area.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Employment Served at Level of Service (RAC)",
			"definition" : "Summation of unduplicated  employment (RAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Percent of Employment Unserved (RAC)",
			"definition" : "100 minus percent of Employment Served (RAC).",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Employment Served By Service (RAC)",
			"definition" : "Total unduplicated  Employment (RAC) impacted within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.  Employment (RAC) served by service is calculated as service stops multiplied by the unduplicated  Employment (RAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area for every trip. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Employees Served (WAC)",
			"definition" : "Summation of unduplicated employees (WAC)within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Percent of Employees Served (WAC)",
			"definition" : "Summation of unduplicated employees(WAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area divided by total employee(WAC) of the given geographic area.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Percent of Employees Served at Level of Service (WAC)",
			"definition" : "Summation of unduplicated employees(WAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service divided by total employee(WAC) of the given geographic area.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Employees Served at Level of Service (WAC)",
			"definition" : "Summation of unduplicated  employees(WAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Percent of Employee Unserved (WAC)",
			"definition" : "100 minus percent of employees (WAC) served.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Employees Served By Service (WAC)",
			"definition" : "Total unduplicated  employees (WAC) impacted within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.  Employees (WAC) served by service is calculated as service stops multiplied by the unduplicated urban Employee (WAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area for every trip. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Service Days",
			"definition" : "Set of days (from the selected days) in which at least one trip within the given geographic area is served.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Connected Communities",
			"definition" : "List of geographic areas of the same type that are connected to the area of interest through routes.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Hours of Service",
			"definition" : "Earliest and latest arrival and departure times of all transit stops within the given geographic area.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Minimum Fare",
			"definition" : "If available, this field points to the fare minimum for the given geographic area.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Average Fare",
			"definition" : "If available, this field points to the fare average for the given geographic area.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Median Fare",
			"definition" : "If available, this field points to the fare median for the given geographic area.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Maximum Fare",
			"definition" : "If available, this field points to the fare maximum for the given geographic area.",
		},

		// 5. Census Places Summary Report
		{
			"report" : "Census Places Summary Report",
			"metric" : "Geo ID",
			"definition" : "Identification number associated with the geographic area.",
		},
		{
			"report" : "Census Places Summary Report",
			"metric" : "Name",
			"definition" : "Name of the geographic area.",
		},
		{
			"report" : "Census Places Summary Report",
			"metric" : "Population",
			"definition" : "Total population of the geographic area.",
		},
		{
			"report" : "Census Places Summary Report",
			"metric" : "Employment (RAC)",
			"definition" : "Total number of employed people residing in the geographic area.",
		},
		{
			"report" : "Census Places Summary Report",
			"metric" : "Employees (WAC)",
			"definition" : "Total number of employed people working in the geographic area.",
		},
		{
			"report" : "Census Places Summary Report",
			"metric" : "Land Area",
			"definition" : "Total land area of the geographic area in square miles.",
		},
		{
			"report" : "Census Places Summary Report",
			"metric" : "Water Area",
			"definition" : "Total water area of the geographic area in square miles.",
		},
		{
			"report" : "Census Places Summary Report",
			"metric" : "Total Agencies",
			"definition" : "Total number of transit agencies operating in the given geographic area.",
		},
		{
			"report" : "Census Places Summary Report",
			"metric" : "Total Routes",
			"definition" : "Total number of routes serving stops in the given geographic area.",
		},
		{
			"report" : "Census Places Summary Report",
			"metric" : "Total Stops",
			"definition" : "Total number of stops within the given geographic area.",
		},
		{
			"report" : "Census Places Summary Report",
			"metric" : "Urban Areas",
			"definition" : "Total number of Urban Areas within the geographic area.",
		},

		// 5.5. Census Places Extended Report
		{
			"report" : "Census Place Extended Report",
			"metric" : "Geo ID",
			"definition" : "Identification number associated with the geographic area.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Name",
			"definition" : "Name of the geographic area.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Route Miles",
			"definition" : "Summation of the lengths of the longest trips within the given geographic area.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Stops Per Square Mile",
			"definition" : "Stop count in the given geographic area divided by the area of the geographic area.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Stops Per Service Mile",
			"definition" : "Stop count in the given geographic area divided by service miles.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Service Hours",
			"definition" : "Total hours a transit agency spends on serving all round trips of routes within the given geographic area. Service hours may be calculated for a specific date or a set of dates specified using the calendar. Reported number are cumulative over the selected dates.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Service Miles",
			"definition" : "Total miles driven over all round trips of routes within the given geographic area. Service miles may be calculated for a specific date or a set of dates specified using the calendar. Reported number are cumulative over the selected dates.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Service Miles Per Square Mile",
			"definition" : "Service miles divided by the area of the geographic area.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Miles of Service Per Capita",
			"definition" : "Service miles divided by the population of the geographic area.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Urban Population Served (Unduplicated)",
			"definition" : "Summation of unduplicated urban population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Rural Population Served",
			"definition" : "Summation of unduplicated rural population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Percent of Population Served",
			"definition" : "Summation of unduplicated population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area divided by total population of the given geographic area.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Percent of Population Served at Level of Service",
			"definition" : "Summation of unduplicated population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service divided by total population of the given geographic area.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Urban Population Served at Level of Service",
			"definition" : "Summation of unduplicated urban population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Rural Population Served at Level of Service",
			"definition" : "Summation of unduplicated rural population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Percent of Population Unserved",
			"definition" : "100 minus percent of population served.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Service Stops",
			"definition" : "Total stops within the given geographic area multiplied by the number of times each stop is being served for the given date(s). Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Urban Population Served By Service",
			"definition" : "Total unduplicated urban population impacted within an X-mile radius (i.e., stop distance) of all stops within the given geographic area. Urban population served by service is calculated as service stops multiplied by the unduplicated urban population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area for every trip. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Rural Population Served By Service",
			"definition" : "Total unduplicated rural population impacted within an X-mile radius (i.e., stop distance) of all stops within the given geographic area. Rural population served by service is calculated as service stops multiplied by the unduplicated rural population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area for every trip. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Employment Served (RAC)",
			"definition" : "Summation of unduplicated  employment(RAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Percent of Employment Served (RAC)",
			"definition" : "Summation of unduplicated employment (RAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area divided by total employment (RAC) of the given geographic area.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Percent of Employment Served at Level of Service (RAC)",
			"definition" : "Summation of unduplicated employment (RAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service divided by total employment (RAC) of the given geographic area.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Employment Served at Level of Service (RAC)",
			"definition" : "Summation of unduplicated  employment (RAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Percent of Employment Unserved (RAC)",
			"definition" : "100 minus percent of Employment (RAC) served.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Employment Served By Service (RAC)",
			"definition" : "Total unduplicated  Employment (RAC) impacted within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.  Employment (RAC) served by service is calculated as service stops multiplied by the unduplicated  Employment (RAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area for every trip. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Employees Served (WAC)",
			"definition" : "Summation of unduplicated employees (WAC)within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Percent of Employees Served (WAC)",
			"definition" : "Summation of unduplicated employees(WAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area divided by total employee(WAC) of the given geographic area.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Percent of Employees Served at Level of Service (WAC)",
			"definition" : "Summation of unduplicated employees(WAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service divided by total employee(WAC) of the given geographic area.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Employees Served at Level of Service (WAC)",
			"definition" : "Summation of unduplicated  employees(WAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Percent of Employee Unserved (WAC)",
			"definition" : "100 minus percent of employees (WAC) served.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Employees Served By Service (WAC)",
			"definition" : "Total unduplicated  employees (WAC) impacted within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.  Employees (WAC) served by service is calculated as service stops multiplied by the unduplicated urban Employee (WAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area for every trip. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Service Days",
			"definition" : "Set of days (from the selected days) in which at least one trip within the given geographic area is served.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Connected Communities",
			"definition" : "List of geographic areas of the same type that are connected to the area of interest through routes.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Hours of Service",
			"definition" : "Earliest and latest arrival and departure times of all transit stops within the given geographic area.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Minimum Fare",
			"definition" : "If available, this field points to the fare minimum for the given geographic area.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Average Fare",
			"definition" : "If available, this field points to the fare average for the given geographic area.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Median Fare",
			"definition" : "If available, this field points to the fare median for the given geographic area.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Maximum Fare",
			"definition" : "If available, this field points to the fare maximum for the given geographic area.",
		},
		// 6. Congressional Districts Summary Report
		{
			"report" : "Congressional Districts Summary Report",
			"metric" : "Geo ID",
			"definition" : "Identification number associated with the geographic area.",
		},
		{
			"report" : "Congressional Districts Summary Report",
			"metric" : "Name",
			"definition" : "Name of the geographic area.",
		},
		{
			"report" : "Congressional Districts Summary Report",
			"metric" : "Population",
			"definition" : "Total population of the geographic area.",
		},
		{
			"report" : "Congressional Districts Summary Report",
			"metric" : "Employment (RAC)",
			"definition" : "Total number of employed people residing in the geographic area.",
		},
		{
			"report" : "Congressional Districts Summary Report",
			"metric" : "Employees (WAC)",
			"definition" : "Total number of employed people working in the geographic area.",
		},
		{
			"report" : "Congressional Districts Summary Report",
			"metric" : "Land Area",
			"definition" : "Total land area of the geographic area in square miles.",
		},
		{
			"report" : "Congressional Districts Summary Report",
			"metric" : "Water Area",
			"definition" : "Total water area of the geographic area in square miles.",
		},
		{
			"report" : "Congressional Districts Summary Report",
			"metric" : "Total Agencies",
			"definition" : "Total number of transit agencies operating in the given geographic area.",
		},
		{
			"report" : "Congressional Districts Summary Report",
			"metric" : "Total Routes",
			"definition" : "Total number of routes serving stops in the given geographic area.",
		},
		{
			"report" : "Congressional Districts Summary Report",
			"metric" : "Total Stops",
			"definition" : "Total number of stops within the given geographic area.",
		},
		{
			"report" : "Congressional Districts Summary Report",
			"metric" : "Urban Areas",
			"definition" : "Total number of Urban Areas within the geographic area.",
		},

		// 6.5 Congressional Districts Extended Report
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Geo ID",
			"definition" : "Identification number associated with the geographic area.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Name",
			"definition" : "Name of the geographic area.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Route Miles",
			"definition" : "Summation of the lengths of the longest trips within the given geographic area.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Stops Per Square Mile",
			"definition" : "Stop count in the given geographic area divided by the area of the geographic area.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Stops Per Service Mile",
			"definition" : "Stop count in the given geographic area divided by service miles.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Service Hours",
			"definition" : "Total hours a transit agency spends on serving all round trips of routes within the given geographic area. Service hours may be calculated for a specific date or a set of dates specified using the calendar. Reported number are cumulative over the selected dates.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Service Miles",
			"definition" : "Total miles driven over all round trips of routes within the given geographic area. Service miles may be calculated for a specific date or a set of dates specified using the calendar. Reported number are cumulative over the selected dates.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Service Miles Per Square Mile",
			"definition" : "Service miles divided by the area of the geographic area.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Miles of Service Per Capita",
			"definition" : "Service miles divided by the population of the geographic area.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Urban Population Served (Unduplicated)",
			"definition" : "Summation of unduplicated urban population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Rural Population Served",
			"definition" : "Summation of unduplicated rural population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Percent of Population Served",
			"definition" : "Summation of unduplicated population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area divided by total population of the given geographic area.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Percent of Population Served at Level of Service",
			"definition" : "Summation of unduplicated population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service divided by total population of the given geographic area.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Urban Population Served at Level of Service",
			"definition" : "Summation of unduplicated urban population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Rural Population Served at Level of Service",
			"definition" : "Summation of unduplicated rural population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Percent of Population Unserved",
			"definition" : "100 minus percent of population served.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Service Stops",
			"definition" : "Total stops within the given geographic area multiplied by the number of times each stop is being served for the given date(s). Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Urban Population Served By Service",
			"definition" : "Total unduplicated urban population impacted within an X-mile radius (i.e., stop distance) of all stops within the given geographic area. Urban population served by service is calculated as service stops multiplied by the unduplicated urban population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area for every trip. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Rural Population Served By Service",
			"definition" : "Total unduplicated rural population impacted within an X-mile radius (i.e., stop distance) of all stops within the given geographic area. Rural population served by service is calculated as service stops multiplied by the unduplicated rural population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area for every trip. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Employment Served (RAC)",
			"definition" : "Summation of unduplicated  employment(RAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Percent of Employment Served (RAC)",
			"definition" : "Summation of unduplicated employment (RAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area divided by total employment (RAC) of the given geographic area.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Percent of Employment Served at Level of Service (RAC)",
			"definition" : "Summation of unduplicated employment (RAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service divided by total employment (RAC) of the given geographic area.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Employment Served at Level of Service (RAC)",
			"definition" : "Summation of unduplicated  employment (RAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Percent of Employment Unserved (RAC)",
			"definition" : "100 minus percent of Employment (RAC) served.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Employment Served By Service (RAC)",
			"definition" : "Total unduplicated  Employment (RAC) impacted within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.  Employment (RAC) served by service is calculated as service stops multiplied by the unduplicated  Employment (RAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area for every trip. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Employees Served (WAC)",
			"definition" : "Summation of unduplicated employees (WAC)within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Percent of Employees Served (WAC)",
			"definition" : "Summation of unduplicated employees(WAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area divided by total employee(WAC) of the given geographic area.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Percent of Employees Served at Level of Service (WAC)",
			"definition" : "Summation of unduplicated employees(WAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service divided by total employee(WAC) of the given geographic area.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Employees Served at Level of Service (WAC)",
			"definition" : "Summation of unduplicated  employees(WAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Percent of Employee Unserved (WAC)",
			"definition" : "100 minus percent of employees (WAC) served.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Employees Served By Service (WAC)",
			"definition" : "Total unduplicated  employees (WAC) impacted within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.  Employees (WAC) served by service is calculated as service stops multiplied by the unduplicated urban Employee (WAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area for every trip. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Service Days",
			"definition" : "Set of days (from the selected days) in which at least one trip within the given geographic area is served.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Connected Communities",
			"definition" : "List of geographic areas of the same type that are connected to the area of interest through routes.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Hours of Service",
			"definition" : "Earliest and latest arrival and departure times of all transit stops within the given geographic area.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Minimum Fare",
			"definition" : "If available, this field points to the fare minimum for the given geographic area.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Average Fare",
			"definition" : "If available, this field points to the fare average for the given geographic area.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Median Fare",
			"definition" : "If available, this field points to the fare median for the given geographic area.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Maximum Fare",
			"definition" : "If available, this field points to the fare maximum for the given geographic area.",
		},
		// 7. Urban Areas Summary Report
		{
			"report" : "Urban Areas Summary Report",
			"metric" : "Geo ID",
			"definition" : "Identification number associated with the geographic area.",
		},
		{
			"report" : "Urban Areas Summary Report",
			"metric" : "Name",
			"definition" : "Name of the geographic area.",
		},
		{
			"report" : "Urban Areas Summary Report",
			"metric" : " Population",
			"definition" : "Population of the geographic area..",
		},
		{
			"report" : "Urban Areas Summary Report",
			"metric" : " Employment (RAC)",
			"definition" : "Total number of employed people residing in the geographic area.",
		},
		{
			"report" : "Urban Areas Summary Report",
			"metric" : "Employees (WAC)",
			"definition" : "Total number of employed people working in the geographic area.",
		},
		{
			"report" : "Urban Areas Summary Report",
			"metric" : "Land Area",
			"definition" : "  land area of the geographic area  in square miles.",
		},
		{
			"report" : "Urban Areas Summary Report",
			"metric" : "Water Area",
			"definition" : " water area of the geographic area  in square miles.",
		},
		{
			"report" : "Urban Areas Summary Report",
			"metric" : "Agencies Count",
			"definition" : "Number of  transit agencies operating in the given geographic area.",
		},
		{
			"report" : "Urban Areas Summary Report",
			"metric" : "Routes Count",
			"definition" : "Number of routes serving stops in the given geographic area.",
		},
		{
			"report" : "Urban Areas Summary Report",
			"metric" : "Stops Count",
			"definition" : " number of stops within the given geographic area.",
		},

		// 7.5 Urban Areas Extended Report
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Geo ID",
			"definition" : "Identification number associated with the geographic area.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Name",
			"definition" : "Name of the geographic area.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Route Miles",
			"definition" : "Summation of the lengths of the longest trips within the given geographic area.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Stops Per Square Mile",
			"definition" : "Stop count in the given geographic area divided by the area of the geographic area.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Stops Per Service Mile",
			"definition" : "Stop count in the given geographic area divided by service miles.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Service Hours",
			"definition" : "Total hours a transit agency spends on serving all round trips of routes within the given geographic area. Service hours may be calculated for a specific date or a set of dates specified using the calendar. Reported number are cumulative over the selected dates.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Service Miles",
			"definition" : "Total miles driven over all round trips of routes within the given geographic area. Service miles may be calculated for a specific date or a set of dates specified using the calendar. Reported number are cumulative over the selected dates.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Service Miles Per Square Mile",
			"definition" : "Service miles divided by the area of the geographic area.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Miles of Service Per Capita",
			"definition" : "Service miles divided by the population of the geographic area.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Urban Population Served (Unduplicated)",
			"definition" : "Summation of unduplicated urban population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Rural Population Served",
			"definition" : "Summation of unduplicated rural population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Percent of Population Served",
			"definition" : "Summation of unduplicated population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area divided by total population of the given geographic area.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Percent of Population Served at Level of Service",
			"definition" : "Summation of unduplicated population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service divided by total population of the given geographic area.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Urban Population Served at Level of Service",
			"definition" : "Summation of unduplicated urban population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Rural Population Served at Level of Service",
			"definition" : "Summation of unduplicated rural population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Percent of Population Unserved",
			"definition" : "100 minus percent of population served.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Service Stops",
			"definition" : "Total stops within the given geographic area multiplied by the number of times each stop is being served for the given date(s). Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Urban Population Served By Service",
			"definition" : "Total unduplicated urban population impacted within an X-mile radius (i.e., stop distance) of all stops within the given geographic area. Urban population served by service is calculated as service stops multiplied by the unduplicated urban population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area for every trip. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Rural Population Served By Service",
			"definition" : "Total unduplicated rural population impacted within an X-mile radius (i.e., stop distance) of all stops within the given geographic area. Rural population served by service is calculated as service stops multiplied by the unduplicated rural population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area for every trip. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Employment Served (RAC)",
			"definition" : "Summation of unduplicated  employment(RAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Percent of Employment Served (RAC)",
			"definition" : "Summation of unduplicated employment (RAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area divided by total employment (RAC) of the given geographic area.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Percent of Employment Served at Level of Service (RAC)",
			"definition" : "Summation of unduplicated employment (RAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service divided by total employment (RAC) of the given geographic area.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Employment Served at Level of Service (RAC)",
			"definition" : "Summation of unduplicated  employment (RAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Percent of Employment Unserved (RAC)",
			"definition" : "100 minus percent of Employment (RAC) served.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Employment Served By Service (RAC)",
			"definition" : "Total unduplicated  Employment (RAC) impacted within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.  Employment (RAC) served by service is calculated as service stops multiplied by the unduplicated  Employment (RAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area for every trip. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Employees Served (WAC)",
			"definition" : "Summation of unduplicated employees (WAC)within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Percent of Employees Served (WAC)",
			"definition" : "Summation of unduplicated employees(WAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area divided by total employee(WAC) of the given geographic area.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Percent of Employees Served at Level of Service (WAC)",
			"definition" : "Summation of unduplicated employees(WAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service divided by total employee(WAC) of the given geographic area.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Employees Served at Level of Service (WAC)",
			"definition" : "Summation of unduplicated  employees(WAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Percent of Employee Unserved (WAC)",
			"definition" : "100 minus percent of employees (WAC) served.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Employees Served By Service (WAC)",
			"definition" : "Total unduplicated  employees (WAC) impacted within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.  Employees (WAC) served by service is calculated as service stops multiplied by the unduplicated urban Employee (WAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area for every trip. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Service Days",
			"definition" : "Set of days (from the selected days) in which at least one trip within the given geographic area is served.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Connected Communities",
			"definition" : "List of geographic areas of the same type that are connected to the area of interest through routes.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Hours of Service",
			"definition" : "Earliest and latest arrival and departure times of all transit stops within the given geographic area.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Minimum Fare",
			"definition" : "If available, this field points to the fare minimum for the given geographic area.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Average Fare",
			"definition" : "If available, this field points to the fare average for the given geographic area.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Median Fare",
			"definition" : "If available, this field points to the fare median for the given geographic area.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Maximum Fare",
			"definition" : "If available, this field points to the fare maximum for the given geographic area.",
		},

		// 8. Aggregated Urban Areas Summary Report
		{
			"report" : "Aggregated Urban Areas Summary Report",
			"metric" : "Geo ID",
			"definition" : "Identification number associated with the geographic area.",
		},
		{
			"report" : "Aggregated Urban Areas Summary Report",
			"metric" : "Name",
			"definition" : "Name of the geographic area.",
		},
		{
			"report" : "Aggregated Urban Areas Summary Report",
			"metric" : "Population",
			"definition" : "Total population of the geographic area.",
		},
		{
			"report" : "Aggregated Urban Areas Summary Report",
			"metric" : "Land Area",
			"definition" : "Total land area of the geographic area in square miles.",
		},
		{
			"report" : "Aggregated Urban Areas Summary Report",
			"metric" : "Water Area",
			"definition" : "Total water area of the geographic area in square miles.",
		},
		{
			"report" : "Aggregated Urban Areas Summary Report",
			"metric" : "Urban Areas",
			"definition" : "Total number of urbanized areas aggregated to generate this report.",
		},
		{
			"report" : "Aggregated Urban Areas Summary Report",
			"metric" : "Total Routes",
			"definition" : "Total number of routes serving stops in the given geographic area.",
		},
		{
			"report" : "Aggregated Urban Areas Summary Report",
			"metric" : "Total Stops",
			"definition" : "Total number of stops within the given geographic area.",
		},

		// 8.5 Aggregated Urban Areas Extended Report
		{
			"report" : "Aggregated Urban Areas Extended Report",
			"metric" : "Name",
			"definition" : "Name of the geographic area.",
		},
		{
			"report" : "Aggregated Urban Areas Extended Report",
			"metric" : "Fare",
			"definition" : "If available, this field points to the fare information published by the transit agencies in their GTFS data.",
		},
		{
			"report" : "Aggregated Urban Areas Extended Report",
			"metric" : "Connected Communities",
			"definition" : "List of geographic areas of the same type that are connected to the area of interest through routes.",
		},
		{
			"report" : "Aggregated Urban Areas Extended Report",
			"metric" : "Route Miles",
			"definition" : "Summation of the lengths of the longest trips within the given geographic area.",
		},
		{
			"report" : "Aggregated Urban Areas Extended Report",
			"metric" : "Stops Per Square Mile",
			"definition" : "Stop count in the given geographic area divided by the area of the geographic area.",
		},
		{
			"report" : "Aggregated Urban Areas Extended Report",
			"metric" : "Stops Per Service Mile",
			"definition" : "Stop count in the given geographic area divided by service miles.",
		},
		{
			"report" : "Aggregated Urban Areas Extended Report",
			"metric" : "Service Hours",
			"definition" : "Total hours a transit agency spends on serving all round trips of routes within the given geographic area. Service hours may be calculated for a specific date or a set of dates specified using the calendar. Reported number are cumulative over the selected dates.",
		},
		{
			"report" : "Aggregated Urban Areas Extended Report",
			"metric" : "Service Miles",
			"definition" : "Total miles driven over all round trips of routes within the given geographic area. Service miles may be calculated for a specific date or a set of dates specified using the calendar. Reported number are cumulative over the selected dates.",
		},
		{
			"report" : "Aggregated Urban Areas Extended Report",
			"metric" : "Service Miles Per Square Mile",
			"definition" : "Service miles divided by the area of the geographic area.",
		},
		{
			"report" : "Aggregated Urban Areas Extended Report",
			"metric" : "Miles of Service Per Capita",
			"definition" : "Service miles divided by the population of the geographic area.",
		},
		{
			"report" : "Aggregated Urban Areas Extended Report",
			"metric" : "Population Served",
			"definition" : "Summation of unduplicated population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.",
		},
		{
			"report" : "Aggregated Urban Areas Extended Report",
			"metric" : "Percent of Population Served",
			"definition" : "Summation of unduplicated population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area divided by total population of the given geographic area.",
		},
		{
			"report" : "Aggregated Urban Areas Extended Report",
			"metric" : "Percent of Population Served at Level of Service",
			"definition" : "Summation of unduplicated population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service divided by total population of the given geographic area.",
		},
		{
			"report" : "Aggregated Urban Areas Extended Report",
			"metric" : "Percent of Population Unserved",
			"definition" : "100 minus percent of population served.",
		},
		{
			"report" : "Aggregated Urban Areas Extended Report",
			"metric" : "Service Stops",
			"definition" : "Total stops within the given geographic area multiplied by the number of times each stop is being served for the given date(s). Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Aggregated Urban Areas Extended Report",
			"metric" : "Population Served By Service",
			"definition" : "Total unduplicated population impacted within an X-mile radius (i.e., stop distance) of all stops within the given geographic area. Population served by service is calculated as service stops multiplied by the unduplicated population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area for every trip. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Aggregated Urban Areas Extended Report",
			"metric" : "Service Days",
			"definition" : "Set of days (from the selected days) in which at least one trip within the given geographic area is served.",
		},
		{
			"report" : "Aggregated Urban Areas Extended Report",
			"metric" : "Hours of Service",
			"definition" : "Earliest and latest arrival and departure times of all transit stops within the given geographic area.",
		},
		// 9. ODOT Transit Regions Summary Report
		{
			"report" : "ODOT Transit Regions Summary Report",
			"metric" : "Geo ID",
			"definition" : "Identification number associated with the geographic area.",
		},
		{
			"report" : "ODOT Transit Regions Summary Report",
			"metric" : "Name",
			"definition" : "Name of the geographic area.",
		},
		{
			"report" : "ODOT Transit Regions Summary Report",
			"metric" : "Population",
			"definition" : "Total population of the geographic area.",
		},
		{
			"report" : "ODOT Transit Regions Summary Report",
			"metric" : "Employment (RAC)",
			"definition" : "Total number of employed people residing in the geographic area.",
		},
		{
			"report" : "ODOT Transit Regions Summary Report",
			"metric" : "Employees (WAC)",
			"definition" : "Total number of employed people working the geographic area.",
		},
		{
			"report" : "ODOT Transit Regions Summary Report",
			"metric" : "Land Area",
			"definition" : "Total land area of the geographic area in square miles.",
		},
		{
			"report" : "ODOT Transit Regions Summary Report",
			"metric" : "Water Area",
			"definition" : "Total water area of the geographic area in square miles.",
		},
		{
			"report" : "ODOT Transit Regions Summary Report",
			"metric" : "Total Agencies",
			"definition" : "Total number of transit agencies operating in the given geographic area.",
		},
		{
			"report" : "ODOT Transit Regions Summary Report",
			"metric" : "Total Routes",
			"definition" : "Total number of routes serving stops in the given geographic area.",
		},
		{
			"report" : "ODOT Transit Regions Summary Report",
			"metric" : "Total Stops",
			"definition" : "Total number of stops within the given geographic area.",
		},
		{
			"report" : "ODOT Transit Regions Summary Report",
			"metric" : "Urban Areas",
			"definition" : "Total number of Urban Areas within the geographic area.",
		},
		{
			"report" : "ODOT Transit Regions Summary Report",
			"metric" : "Counties",
			"definition" : "List of Counties within the geographic area.",
		},

		// 9.5 ODOT Transit Regions Extended Report
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Geo ID",
			"definition" : "Identification number associated with the geographic area.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Name",
			"definition" : "Name of the geographic area.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Route Miles",
			"definition" : "Summation of the lengths of the longest trips within the given geographic area.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Stops Per Square Mile",
			"definition" : "Stop count in the given geographic area divided by the area of the geographic area.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Stops Per Service Mile",
			"definition" : "Stop count in the given geographic area divided by service miles.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Service Hours",
			"definition" : "Total hours a transit agency spends on serving all round trips of routes within the given geographic area. Service hours may be calculated for a specific date or a set of dates specified using the calendar. Reported number are cumulative over the selected dates.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Service Miles",
			"definition" : "Total miles driven over all round trips of routes within the given geographic area. Service miles may be calculated for a specific date or a set of dates specified using the calendar. Reported number are cumulative over the selected dates.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Service Miles Per Square Mile",
			"definition" : "Service miles divided by the area of the geographic area.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Miles of Service Per Capita",
			"definition" : "Service miles divided by the population of the geographic area.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Urban Population Served (Unduplicated)",
			"definition" : "Summation of unduplicated urban population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Rural Population Served",
			"definition" : "Summation of unduplicated rural population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Percent of Population Served",
			"definition" : "Summation of unduplicated population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area divided by total population of the given geographic area.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Percent of Population Served at Level of Service",
			"definition" : "Summation of unduplicated population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service divided by total population of the given geographic area.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Urban Population Served at Level of Service",
			"definition" : "Summation of unduplicated urban population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Rural Population Served at Level of Service",
			"definition" : "Summation of unduplicated rural population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Percent of Population Unserved",
			"definition" : "100 minus percent of population served.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Service Stops",
			"definition" : "Total stops within the given geographic area multiplied by the number of times each stop is being served for the given date(s). Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Urban Population Served By Service",
			"definition" : "Total unduplicated urban population impacted within an X-mile radius (i.e., stop distance) of all stops within the given geographic area. Urban population served by service is calculated as service stops multiplied by the unduplicated urban population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area for every trip. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Rural Population Served By Service",
			"definition" : "Total unduplicated rural population impacted within an X-mile radius (i.e., stop distance) of all stops within the given geographic area. Rural population served by service is calculated as service stops multiplied by the unduplicated rural population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area for every trip. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Employment Served (RAC)",
			"definition" : "Summation of unduplicated  employment(RAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Percent of Employment Served (RAC)",
			"definition" : "Summation of unduplicated employment (RAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area divided by total employment (RAC) of the given geographic area.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Percent of Employment Served at Level of Service (RAC)",
			"definition" : "Summation of unduplicated employment (RAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service divided by total employment (RAC) of the given geographic area.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Employment Served at Level of Service (RAC)",
			"definition" : "Summation of unduplicated  employment (RAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Percent of Employment Unserved (RAC)",
			"definition" : "100 minus percent of Employment (RAC) served.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Employment Served By Service (RAC)",
			"definition" : "Total unduplicated  Employment (RAC) impacted within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.  Employment (RAC) served by service is calculated as service stops multiplied by the unduplicated  Employment (RAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area for every trip. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Employees Served (WAC)",
			"definition" : "Summation of unduplicated employees (WAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Percent of Employees Served (WAC)",
			"definition" : "Summation of unduplicated employees(WAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area divided by total employee(WAC) of the given geographic area.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Percent of Employees Served at Level of Service (WAC)",
			"definition" : "Summation of unduplicated employees(WAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service divided by total employee(WAC) of the given geographic area.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Employees Served at Level of Service (WAC)",
			"definition" : "Summation of unduplicated  employees(WAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Percent of Employee Unserved (WAC)",
			"definition" : "100 minus percent of employees (WAC) served.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Employees Served By Service (WAC)",
			"definition" : "Total unduplicated  employees (WAC) impacted within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.  Employees (WAC) served by service is calculated as service stops multiplied by the unduplicated urban Employee (WAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area for every trip. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Service Days",
			"definition" : "Set of days (from the selected days) in which at least one trip within the given geographic area is served.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Connected Communities",
			"definition" : "List of geographic areas of the same type that are connected to the area of interest through routes.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Hours of Service",
			"definition" : "Earliest and latest arrival and departure times of all transit stops within the given geographic area.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Minimum Fare",
			"definition" : "If available, this field points to the fare minimum for the given geographic area.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Average Fare",
			"definition" : "If available, this field points to the fare average for the given geographic area.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Median Fare",
			"definition" : "If available, this field points to the fare median for the given geographic area.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Maximum Fare",
			"definition" : "If available, this field points to the fare maximum for the given geographic area.",
		},
		// 9.6 Stops Report
		{
			"report" : "Stops Summary Report",
			"metric" : "Agency Name",
			"definition" : "Agency name reported in the transit agency GTFS feed.",
		},
		{
			"report" : "Stops Summary Report",
			"metric" : "Stop ID",
			"definition" : "Unique stop identification number reported in the transit agency GTFS feed.",
		},
		{
			"report" : "Stops Summary Report",
			"metric" : "Stop Name",
			"definition" : "Name of a stop reported in the transit agency GTFS feed.",
		},
		{
			"report" : "Stops Summary Report",
			"metric" : "County",
			"definition" : "The County in which the stop is located.",
		},
		{
			"report" : "Stops Summary Report",
			"metric" : "Urban Area",
			"definition" : "The Urban Area in which the stop is located.",
		},
		{
			"report" : "Stops Summary Report",
			"metric" : "ODOT Transit Region",
			"definition" : "The ODOT Transit Region in which the stop is located.",
		},
		{
			"report" : "Stops Summary Report",
			"metric" : "Congressional District",
			"definition" : "The Congressional District in which the stop is located.",
		},
		{
			"report" : "Stops Summary Report",
			"metric" : "Census Place",
			"definition" : "The Census Place in which the stop is located.",
		},
		{
			"report" : "Stops Summary Report",
			"metric" : "Routes Stop Belongs To",
			"definition" : "Unique route ID (or IDs) that the stop belongs to.",
		},
		{
			"report" : "Stops Summary Report",
			"metric" : "Visits",
			"definition" : "Number of times the stops is visited during the selected dates.",
		},
		{
			"report" : "Stops Summary Report",
			"metric" : "Population Served",
			"definition" : "Unduplicated population count within an X-mile radius of a stop. The default value for X is 0.25 miles. However, the value of the radius can be changed by the user on the text box shown on the upper right corner of the report and then pressing the <Submit> button.",
		},
		{
			"report" : "Stops Summary Report",
			"metric" : "Urban Population Served",
			"definition" : "Unduplicated urban population count within an X-mile radius of a stop. The default value for X is 0.25 miles. However, the value of the radius can be changed by the user on the text box shown on the upper right corner of the report and then pressing the <Submit> button.",
		},
		{
			"report" : "Stops Summary Report",
			"metric" : "Rural Population Served",
			"definition" : "Unduplicated rural population count within an X-mile radius of a stop. The default value for X is 0.25 miles. However, the value of the radius can be changed by the user on the text box shown on the upper right corner of the report and then pressing the <Submit> button.",
		},
		{
			"report" : "Stops Summary Report",
			"metric" : "Employment Served (RAC)",
			"definition" : "Number of employed people living within an X-mile radius of a stop. The default value for X is 0.25 miles. However, the value of the radius can be changed by the user on the text box shown on the upper right corner of the report and then pressing the <Submit> button.",
		},
		{
			"report" : "Stops Summary Report",
			"metric" : "Employees Served (WAC)",
			"definition" : "Number of employed people working within an X-mile radius of a stop. The default value for X is 0.25 miles. However, the value of the radius can be changed by the user on the text box shown on the upper right corner of the report and then pressing the <Submit> button.",
		},
		{
			"report" : "Stops Summary Report",
			"metric" : "Urban Population",
			"definition" : "Urban population count within an X-mile radius of a stop. The default value for X is 0.25 miles. However, the value of the radius can be changed by the user on the text box shown on the upper right corner of the report and then pressing the <Submit> button.",
		},
		{
			"report" : "Stops Summary Report",
			"metric" : "Rural Population",
			"definition" : "Rural population count within an X-mile radius of a stop. The default value for X is 0.25 miles. However, the value of the radius can be changed by the user on the text box shown on the upper right corner of the report and then pressing the <Submit> button.",
		},
		{
			"report" : "Stops Summary Report",
			"metric" : "Urban area with over 50k population",
			"definition" : "The population of the urban area with population over 50,000 in which the stop is located. If the value is 0, it means that the stop is either not located in an urban area, or the population of the area is less than 50,000.",
		},
		{
			"report" : "Stops Summary Report",
			"metric" : "Latitude",
			"definition" : "Latitude of the stop",
		},
		{
			"report" : "Stops Summary Report",
			"metric" : "Longitude",
			"definition" : "Longitude of the stops",
		},

		// 9.7 Routes Summary Report
		{
			"report" : "Routes Summary Report",
			"metric" : "Agency ID",
			"definition" : "Identification number reported in the transit agency GTFS feed.",
		},
		{
			"report" : "Routes Summary Report",
			"metric" : "Agency Name",
			"definition" : "Agency name reported in the transit agency GTFS feed.",
		},
		{
			"report" : "Routes Summary Report",
			"metric" : "Route ID",
			"definition" : "Unique route identification number reported in the transit agency GTFS feed.",
		},
		{
			"report" : "Routes Summary Report",
			"metric" : "Route Name",
			"definition" : "Route short name as reported in the transit agency GTFS feed.",
		},
		{
			"report" : "Routes Summary Report",
			"metric" : "Route Long Name",
			"definition" : "Route long name as reported in the transit agency GTFS feed.",
		},
		{
			"report" : "Routes Summary Report",
			"metric" : "Route Type",
			"definition" : "Type of transportation used on a route: 0-Tram, Streetcar & Light rail 1-Subway & Metro 2-Rail 3-Bus 4-Ferry 5-Cable car 6-Gondola & Suspended cable car 7-Funicular",
		},
		{
			"report" : "Routes Summary Report",
			"metric" : "Route Length",
			"definition" : "Length of the longest route variant for the given route.",
		},
		{
			"report" : "Routes Summary Report",
			"metric" : "Total Stops",
			"definition" : "Total number of stops on the route.",
		},
		{
			"report" : "Routes Summary Report",
			"metric" : "Counties",
			"definition" : "Counties in which the route operates.",
		},
		{
			"report" : "Routes Summary Report",
			"metric" : "Urban Areas",
			"definition" : "Urban Areas in which the route operates.",
		},
		{
			"report" : "Routes Summary Report",
			"metric" : "Census Places",
			"definition" : "Census Places in which the route operates.",
		},
		{
			"report" : "Routes Summary Report",
			"metric" : "Congressional Districts",
			"definition" : "Congressional Districts in which the route operates.",
		},
		{
			"report" : "Routes Summary Report",
			"metric" : "ODOT Transit Regions",
			"definition" : "ODOT Transit Regions in which the route operates.",
		},
		{
			"report" : "Routes Summary Report",
			"metric" : "Unduplicated Urban Population",
			"definition" : "Summation of the unduplicated urban population count within an X radius (i.e., stop distance) of each stop on a route. The default value for X is 0.25 miles. However, the value of the radius can be changed by the user on the text box shown on the upper right corner of the report and then pressing the <Submit> button.",
		},
		{
			"report" : "Routes Summary Report",
			"metric" : "Unduplicated Rural Population",
			"definition" : "Summation of the unduplicated rural population count within an X radius (i.e., stop distance) of each stop on a route. The default value for X is 0.25 miles. However, the value of the radius can be changed by the user on the text box shown on the upper right corner of the report and then pressing the <Submit> button.",
		},
		{
			"report" : "Routes Summary Report",
			"metric" : "Service Stops",
			"definition" : "Number of stops scheduled on all trips in a route. The service stops for a route is calculated as its stop count multiplied by the number of visits per stop.",
		},
		{
			"report" : "Routes Summary Report",
			"metric" : "Urban Population Served",
			"definition" : "Total unduplicated urban population impacted within an X-mile radius (i.e., stop distance) of all stops on all trips. The Population Served by Route for a route is calculated as route service stops multiplied by the unduplicated population within an X-mile radius (i.e., stop distance) of all stops on all trips.",
		},
		{
			"report" : "Routes Summary Report",
			"metric" : "Rural Population Served",
			"definition" : "Total unduplicated rural population impacted within an X-mile radius (i.e., stop distance) of all stops on all trips. The Population Served by Route for a route is calculated as route service stops multiplied by the unduplicated population within an X-mile radius (i.e., stop distance) of all stops on all trips.",
		},
		{
			"report" : "Routes Summary Report",
			"metric" : "Service Miles",
			"definition" : "Total miles driven by a transit agency over all round trips of a route. Service miles may be calculated for a specific date or a set of dates specified using the calendar.",
		},
		{
			"report" : "Routes Summary Report",
			"metric" : "Service Hours",
			"definition" : "Total hours a transit agency spends on serving all round trips of a route. Service hours may be calculated for a specific date or a set of dates specified using the calendar.",
		},
		{
			"report" : "Routes Summary Report",
			"metric" : "More..",
			"definition" : "If available, this field contains a description of the route (the value is null otherwise).",
		},

		// 10. Transit Hubs Report
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "ID",
			"definition" : "ID number randomly assigned to the cluster.",
		},
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "Cluster Centroid Latitude",
			"definition" : "Latitude of the transit hub centroid.",
		},
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "Cluster Centroid Longitude",
			"definition" : "Longitude of the transit hub centroid.",
		},
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "Stops Count",
			"definition" : "Total number of stops in the cluster.",
		},
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "Routes Count",
			"definition" : "Total number of routes that serve stops in the cluster.",
		},
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "Agencies Count",
			"definition" : "Total number of transit agencies that serve stops in the cluster.",
		},
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "Visits Count",
			"definition" : "Total number of times all stops in the cluster are served.",
		},
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "Park and Ride Lots Count",
			"definition" : "Total number of park and ride lots within X distance of the cluster centroid.",
		},
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "Counties Count",
			"definition" : "Total number of counties that the cluster is located in.",
		},
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "Census Places Count",
			"definition" : "Total number of census places that the cluster is located in.",
		},
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "Population Served",
			"definition" : "Unduplicated sum of the population within the X radius distance of all stops in the cluster. This metric is date-independent.",
		},
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "Employment Served (RAC)",
			"definition" : "Unduplicated sum of the Employment (RAC) within the X radius distance of all stops in the cluster. This metric is date-independent.",
		},
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "Employees Served (WAC)",
			"definition" : "Unduplicated sum of the Employees (WAC) within the X radius distance of all stops in the cluster. This metric is date-independent.",
		},
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "Urban Population",
			"definition" : "Sum of the population of urban areas that stops in the cluster are located in.",
		},
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "Transit Agencies",
			"definition" : "Unduplicated list of transit agencies that serve stops in the cluster.",
		},
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "Stops",
			"definition" : "Detailed list of stops in the cluster.",
		},
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "Routes",
			"definition" : "Unduplicated detailed list of routes in the cluster.",
		},
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "Park and Ride Lots",
			"definition" : "Detailed list of park and ride lots within the X radius of the cluster centroid.",
		},
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "Census Places",
			"definition" : "Census places that stops in the cluster are locate in.",
		},
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "Counties",
			"definition" : "Counties that stops in the cluster are located in.",
		},
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "Urban Areas",
			"definition" : "Areas that stops in the cluster are located in.",
		},
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "ODOT Transit Regions",
			"definition" : "ODOT transit regions that stops in the cluster are located in.",
		},

		// 11. Key Transit Hubs Report
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "ID",
			"definition" : "ID number randomly assigned to the cluster.",
		},
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "Cluster Centroid Latitude",
			"definition" : "Latitude of the transit hub centroid.",
		},
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "Cluster Centroid Longitude",
			"definition" : "Longitude of the transit hub centroid.",
		},
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "Stops Count",
			"definition" : "Total number of stops in the cluster.",
		},
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "Routes Count",
			"definition" : "Total number of routes that serve stops in the cluster.",
		},
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "Agencies Count",
			"definition" : "Total number of transit agencies that serve stops in the cluster.",
		},
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "Visits Count",
			"definition" : "Total number of times all stops in the cluster are served.",
		},
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "Park and Ride Lots Count",
			"definition" : "Total number of park and ride lots within X distance of the cluster centroid.",
		},
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "Counties Count",
			"definition" : "Total number of counties that the cluster is located in.",
		},
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "Census Places Count",
			"definition" : "Total number of census places that the cluster is located in.",
		},
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "Population Served",
			"definition" : "Unduplicated sum of the population within the X radius distance of all stops in the cluster. This metric is date-independent.",
		},
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "Employment Served (RAC)",
			"definition" : "Unduplicated sum of the Employment (RAC) within the X radius distance of all stops in the cluster. This metric is date-independent.",
		},
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "Employees Served (WAC)",
			"definition" : "Unduplicated sum of the Employees (WAC) within the X radius distance of all stops in the cluster. This metric is date-independent.",
		},
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "Urban Population",
			"definition" : "Sum of the population of urban areas that stops in the cluster are located in.",
		},
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "Transit Agencies",
			"definition" : "Unduplicated list of transit agencies that serve stops in the cluster.",
		},
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "Stops",
			"definition" : "Detailed list of stops in the cluster.",
		},
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "Routes",
			"definition" : "Unduplicated detailed list of routes in the cluster.",
		},
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "Park and Ride Lots",
			"definition" : "Detailed list of park and ride lots within the X radius of the cluster centroid.",
		},
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "Census Places",
			"definition" : "Census places that stops in the cluster are locate in.",
		},
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "Counties",
			"definition" : "Counties that stops in the cluster are located in.",
		},
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "Urban Areas",
			"definition" : "Areas that stops in the cluster are located in.",
		},
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "ODOT Transit Regions",
			"definition" : "ODOT transit regions that stops in the cluster are located in.",
		},

		// 12. Timing Connection Report
		{
			"report" : "Timing Connection Report",
			"metric" : "#",
			"definition" : "Randomly assigned row number.",
		},
		{
			"report" : "Timing Connection Report",
			"metric" : "From - Stop ID",
			"definition" : "Stop belonging to the selected trip.",
		},
		{
			"report" : "Timing Connection Report",
			"metric" : "From - Stop Name",
			"definition" : "Stop belonging to the selected trip.",
		},
		{
			"report" : "Timing Connection Report",
			"metric" : "To - Stop ID",
			"definition" : "Stop belonging to the connected trip.",
		},
		{
			"report" : "Timing Connection Report",
			"metric" : "To - Stop Name",
			"definition" : "Stop belonging to the connected trip.",
		},
		{
			"report" : "Timing Connection Report",
			"metric" : "To - Agency",
			"definition" : "The agency that is connected to the selected agency by having a stop and route accessable within the specified radius and time window.",
		},
		{
			"report" : "Timing Connection Report",
			"metric" : "To - Route ID",
			"definition" : "The route that is connected to the selected route by having a stop and trip accessable within the specified radius and time window.",
		},
		{
			"report" : "Timing Connection Report",
			"metric" : "To - Route Name",
			"definition" : "The route that is connected to the selected route by having a stop and trip accessable within the specified radius and time window.",
		},
		{
			"report" : "Timing Connection Report",
			"metric" : "Arrival at Stop 1",
			"definition" : "Time of arrival at stop 1.",
		},
		{
			"report" : "Timing Connection Report",
			"metric" : "Departure from Stop 2",
			"definition" : "Time of departure from stop 2.",
		},
		{
			"report" : "Timing Connection Report",
			"metric" : "Time Difference",
			"definition" : "The difference between arriving at stop1 and departing from stop2.",
		},

		// 13. Connected Networks Summary Report
		{
			"report" : "Connected Networks Summary Report",
			"metric" : "Cluster ID",
			"definition" : "Transit network cluster ID.",
		},
		{
			"report" : "Connected Networks Summary Report",
			"metric" : "Network Cluster Size",
			"definition" : "Number of transit agencies that have at least one stop within the specified distance of at least one transit agency in the cluster.",
		},
		{
			"report" : "Connected Networks Summary Report",
			"metric" : "Connected Agency IDs",
			"definition" : "Agency IDs corresponding with transit agencies that have at least one stop within the specified distance of any other agencies in the list.",
		},
		{
			"report" : "Connected Networks Summary Report",
			"metric" : "Connected Agency Names",
			"definition" : "List of transit agencies that have at least one stop within the specified distance of any other agencies in the list.",
		},

		// 14. Connected Transit Agencies Summary Report
		{
			"report" : "Connected Agencies Summary Report",
			"metric" : "Agency ID",
			"definition" : "Identification number reported in the transit agency GTFS feed.",
		},
		{
			"report" : "Connected Agencies Summary Report",
			"metric" : "Agency Name",
			"definition" : "Agency name reported in the transit agency GTFS feed.",
		},
		{
			"report" : "Connected Agencies Summary Report",
			"metric" : "Number of Connected Agenies",
			"definition" : "Number of transit agencies that have at least one stop within the specified distance of the transit agency stops",
		},
		{
			"report" : "Connected Agencies Summary Report",
			"metric" : "Connected Agency Names",
			"definition" : "Name of transit agencies that have at least one stop within the specified distance of the transit agency stops",
		},
		{
			"report" : "Connected Agencies Summary Report",
			"metric" : "Connected Agency IDs",
			"definition" : "Agency IDs corresponding with transit agencies that have at least one stop within the specified distance of the transit agency stops",
		},

		// 14.5 Connected Agencies Extended Report
		{
			"report" : "Connected Agencies Extended Report",
			"metric" : "Name",
			"definition" : "Agency name reported in the transit agency GTFS feed.",
		},
		{
			"report" : "Connected Agencies Extended Report",
			"metric" : "Number of Connections",
			"definition" : "Number of transit stops that have at least one stop within the specified distance of the specified transit agency stops",
		},
		{
			"report" : "Connected Agencies Extended Report",
			"metric" : "Min Connection distance (ft.)",
			"definition" : "Smallest distance in ft between the transit stops of the specified transit agency with the current transit agency",
		},
		{
			"report" : "Connected Agencies Extended Report",
			"metric" : "Max Connection distance (ft.)",
			"definition" : "Largest distance in ft between the transit stops of the specified transit agency with the current transit agency",
		},
		{
			"report" : "Connected Agencies Extended Report",
			"metric" : "Average Connection distance (ft.)",
			"definition" : "Average distance in ft between the transit stops of the specified transit agency with the current transit agency",
		},
		{
			"report" : "Connected Agencies Extended Report",
			"metric" : "Connections",
			"definition" : "List of transit stops within the specified distance of the specified transit agency stops and the distance between them in ft",
		},

		// 15. Park and Ride Summary Report
		{
			"report" : "Park and Ride Summary Report",
			"metric" : "County ID",
			"definition" : "County ID conforming to the census data.",
		},
		{
			"report" : "Park and Ride Summary Report",
			"metric" : "County Name",
			"definition" : "County name.",
		},
		{
			"report" : "Park and Ride Summary Report",
			"metric" : "Total number of P&R lots",
			"definition" : "Total number of park and ride lots in the county.",
		},
		{
			"report" : "Park and Ride Summary Report",
			"metric" : "Total parking spaces",
			"definition" : "Total number of parking spaces in the county.",
		},
		{
			"report" : "Park and Ride Summary Report",
			"metric" : "Total accessible parking spaces",
			"definition" : "Total number of handicap parking spaces in the county.",
		},

		// 16. Employment Report

		/*
		 * { "report" : "Employment Report", "metric" : "Number of employees",
		 * "definition" : "Number of employees working in the area", }, {
		 * "report" : "Employment Report", "metric" : "CA01", "definition" :
		 * "Total CA01 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CA01 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CA01 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CA01
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CA01 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CA01 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CA01 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CA02", "definition" :
		 * "Total CA02 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CA02 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CA02 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CA02
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CA02 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CA02 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CA02 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CA03", "definition" :
		 * "Total CA03 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CA03 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CA03 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CA03
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CA03 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CA03 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CA03 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CD01", "definition" :
		 * "Total CD01 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CD01 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CD01 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CD01
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CD01 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CD01 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CD01 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CD02", "definition" :
		 * "Total CD02 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CD02 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CD02 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CD02
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CD02 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CD02 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CD02 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CD03", "definition" :
		 * "Total CD03 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CD03 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CD03 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CD03
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CD03 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CD03 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CD03 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CD04", "definition" :
		 * "Total CD04 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CD04 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CD04 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CD04
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CD04 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CD04 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CD04 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CE01", "definition" :
		 * "Total CE01 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CE01 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CE01 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CE01
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CE01 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CE01 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CE01 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CE02", "definition" :
		 * "Total CE02 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CE02 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CE02 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CE02
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CE02 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CE02 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CE02 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CE03", "definition" :
		 * "Total CE03 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CE03 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CE03 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CE03
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CE03 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CE03 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CE03 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CNS01", "definition" :
		 * "Total CNS01 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CNS01 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CNS01 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CNS01
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CNS01 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CNS01 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CNS01 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CNS02", "definition" :
		 * "Total CNS02 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CNS02 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CNS02 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CNS02
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CNS02 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CNS02 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CNS02 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CNS03", "definition" :
		 * "Total CNS03 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CNS03 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CNS03 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CNS03
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CNS03 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CNS03 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CNS03 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CNS04", "definition" :
		 * "Total CNS04 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CNS04 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CNS04 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CNS04
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CNS04 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CNS04 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CNS04 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CNS05", "definition" :
		 * "Total CNS05 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CNS05 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CNS05 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CNS05
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CNS05 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CNS05 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CNS05 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CNS06", "definition" :
		 * "Total CNS06 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CNS06 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CNS06 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CNS06
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CNS06 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CNS06 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CNS06 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CNS07", "definition" :
		 * "Total CNS07 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CNS07 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CNS07 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CNS07
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CNS07 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CNS07 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CNS07 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CNS08", "definition" :
		 * "Total CNS08 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CNS08 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CNS08 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CNS08
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CNS08 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CNS08 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CNS08 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CNS09", "definition" :
		 * "Total CNS09 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CNS09 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CNS09 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CNS09
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CNS09 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CNS09 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CNS09 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CNS10", "definition" :
		 * "Total CNS10 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CNS10 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CNS10 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CNS10
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CNS10 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CNS10 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CNS10 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CNS11", "definition" :
		 * "Total CNS11 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CNS11 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CNS11 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CNS11
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CNS11 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CNS11 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CNS11 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CNS12", "definition" :
		 * "Total CNS12 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CNS12 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CNS12 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CNS12
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CNS12 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CNS12 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CNS12 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CNS13", "definition" :
		 * "Total CNS13 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CNS13 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CNS13 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CNS13
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CNS13 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CNS13 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CNS13 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CNS14", "definition" :
		 * "Total CNS14 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CNS14 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CNS14 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CNS14
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CNS14 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CNS14 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CNS14 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CNS15", "definition" :
		 * "Total CNS15 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CNS15 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CNS15 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CNS15
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CNS15 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CNS15 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CNS15 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CNS16", "definition" :
		 * "Total CNS16 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CNS16 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CNS16 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CNS16
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CNS16 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CNS16 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CNS16 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CNS17", "definition" :
		 * "Total CNS17 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CNS17 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CNS17 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CNS17
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CNS17 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CNS17 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CNS17 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CNS18", "definition" :
		 * "Total CNS18 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CNS18 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CNS18 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CNS18
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CNS18 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CNS18 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CNS18 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CNS19", "definition" :
		 * "Total CNS19 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CNS19 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CNS19 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CNS19
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CNS19 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CNS19 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CNS19 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CNS20", "definition" :
		 * "Total CNS20 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CNS20 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CNS20 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CNS20
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CNS20 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CNS20 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CNS20 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CR01", "definition" :
		 * "Total CR01 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CR01 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CR01 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CR01
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CR01 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CR01 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CR01 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CR02", "definition" :
		 * "Total CR02 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CR02 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CR02 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CR02
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CR02 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CR02 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CR02 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CR03", "definition" :
		 * "Total CR03 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CR03 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CR03 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CR03
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CR03 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CR03 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CR03 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CR04", "definition" :
		 * "Total CR04 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CR04 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CR04 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CR04
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CR04 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CR04 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CR04 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CR05", "definition" :
		 * "Total CR05 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CR05 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CR05 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CR05
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CR05 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CR05 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CR05 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CR07", "definition" :
		 * "Total CR07 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CR07 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CR07 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CR07
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CR07 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CR07 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CR07 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CS01", "definition" :
		 * "Total CS01 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CS01 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CS01 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CS01
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CS01 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CS01 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CS01 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CS02", "definition" :
		 * "Total CS02 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CS02 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CS02 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CS02
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CS02 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CS02 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CS02 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CT01", "definition" :
		 * "Total CT01 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CT01 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CT01 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CT01
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CT01 employees that receive the specified
		 * minimum level of service.", }, { "report" : "Employment Report",
		 * "metric" : "CT01 EMPSS", "definition" : "Employees Served by Service -
		 * Unduplicated summation of CT01 employees served by service is
		 * calculated as service stops multiplied by the unduplicated employees
		 * working within an X-mile radius (i.e., stop distance) of all stops.
		 * Reported number is cumulative over the selected dates.", }, {
		 * "report" : "Employment Report", "metric" : "CT02", "definition" :
		 * "Total CT02 employees working in the area.", }, { "report" :
		 * "Employment Report", "metric" : "CT02 EMPS", "definition" :
		 * "Employees Served - Unduplicated summation of CT02 employees working
		 * within X distance of any stop. This metric is date/service
		 * independent.", }, { "report" : "Employment Report", "metric" : "CT02
		 * EMPSLOS", "definition" : "Employees Served at Level of Service -
		 * Unduplicated summation of CT02 employees that receive the specified
		 * minimum level of service.", },
		 */

		// 17. Title VI
		{
			"report" : "Title VI Report",
			"metric" : "18 to 64 years",
			"definition" : "Total number of individuals of that belong to the selected category and are living in the area.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "18 to 64 years-S",
			"definition" : "Number of Individuals Served: Unduplicated summation of individuals of the selected category who are living within X distance of any stop. This metric is date/service independent.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "18 to 64 years-SLOS",
			"definition" : "Number of Individuals Served at Level of Service: Unduplicated summation of individuals of the selected category who receive the specified minimum level of service.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "18 to 64 years-SS",
			"definition" : "Number of Individuals Served by Service: Unduplicated summation of individuals of the selected category who are served by service is calculated as service stops multiplied by the unduplicated individuals living within an X-mile radius (i.e., stop distance) of all stops. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "5 to 17 years",
			"definition" : "Total number of individuals of that belong to the selected category and are living in the area.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "5 to 17 years-S",
			"definition" : "Number of Individuals Served: Unduplicated summation of individuals of the selected category who are living within X distance of any stop. This metric is date/service independent.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "5 to 17 years-SLOS",
			"definition" : "Number of Individuals Served at Level of Service: Unduplicated summation of individuals of the selected category who receive the specified minimum level of service.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "5 to 17 years-SS",
			"definition" : "Number of Individuals Served by Service: Unduplicated summation of individuals of the selected category who are served by service is calculated as service stops multiplied by the unduplicated individuals living within an X-mile radius (i.e., stop distance) of all stops. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "65 and older",
			"definition" : "Total number of individuals of that belong to the selected category and are living in the area.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "65 and older-S",
			"definition" : "Number of Individuals Served: Unduplicated summation of individuals of the selected category who are living within X distance of any stop. This metric is date/service independent.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "65 and older-SLOS",
			"definition" : "Number of Individuals Served at Level of Service: Unduplicated summation of individuals of the selected category who receive the specified minimum level of service.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "65 and older-SS",
			"definition" : "Number of Individuals Served by Service: Unduplicated summation of individuals of the selected category who are served by service is calculated as service stops multiplied by the unduplicated individuals living within an X-mile radius (i.e., stop distance) of all stops. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "With Disability",
			"definition" : "Total number of individuals of that belong to the selected category and are living in the area.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "With Disability-S",
			"definition" : "Number of Individuals Served: Unduplicated summation of individuals of the selected category who are living within X distance of any stop. This metric is date/service independent.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "With Disability-SLOS",
			"definition" : "Number of Individuals Served at Level of Service: Unduplicated summation of individuals of the selected category who receive the specified minimum level of service.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "With Disability-SS",
			"definition" : "Number of Individuals Served by Service: Unduplicated summation of individuals of the selected category who are served by service is calculated as service stops multiplied by the unduplicated individuals living within an X-mile radius (i.e., stop distance) of all stops. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Without Disability",
			"definition" : "Total number of individuals of that belong to the selected category and are living in the area.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Without Disability-S",
			"definition" : "Number of Individuals Served: Unduplicated summation of individuals of the selected category who are living within X distance of any stop. This metric is date/service independent.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Without Disability-SLOS",
			"definition" : "Number of Individuals Served at Level of Service: Unduplicated summation of individuals of the selected category who receive the specified minimum level of service.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Without Disability-SS",
			"definition" : "Number of Individuals Served by Service: Unduplicated summation of individuals of the selected category who are served by service is calculated as service stops multiplied by the unduplicated individuals living within an X-mile radius (i.e., stop distance) of all stops. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "American Indian or Alaska Native",
			"definition" : "Total number of individuals of that belong to the selected category and are living in the area.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "American Indian or Alaska Native-S",
			"definition" : "Number of Individuals Served: Unduplicated summation of individuals of the selected category who are living within X distance of any stop. This metric is date/service independent.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "American Indian or Alaska Native-SLOS",
			"definition" : "Number of Individuals Served at Level of Service: Unduplicated summation of individuals of the selected category who receive the specified minimum level of service.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "American Indian or Alaska Native-SS",
			"definition" : "Number of Individuals Served by Service: Unduplicated summation of individuals of the selected category who are served by service is calculated as service stops multiplied by the unduplicated individuals living within an X-mile radius (i.e., stop distance) of all stops. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Asian",
			"definition" : "Total number of individuals of that belong to the selected category and are living in the area.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Asian-S",
			"definition" : "Number of Individuals Served: Unduplicated summation of individuals of the selected category who are living within X distance of any stop. This metric is date/service independent.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Asian-SLOS",
			"definition" : "Number of Individuals Served at Level of Service: Unduplicated summation of individuals of the selected category who receive the specified minimum level of service.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Asian-SS",
			"definition" : "Number of Individuals Served by Service: Unduplicated summation of individuals of the selected category who are served by service is calculated as service stops multiplied by the unduplicated individuals living within an X-mile radius (i.e., stop distance) of all stops. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Black or African American",
			"definition" : "Total number of individuals of that belong to the selected category and are living in the area.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Black or African American-S",
			"definition" : "Number of Individuals Served: Unduplicated summation of individuals of the selected category who are living within X distance of any stop. This metric is date/service independent.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Black or African American-SLOS",
			"definition" : "Number of Individuals Served at Level of Service: Unduplicated summation of individuals of the selected category who receive the specified minimum level of service.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Black or African American-SS",
			"definition" : "Number of Individuals Served by Service: Unduplicated summation of individuals of the selected category who are served by service is calculated as service stops multiplied by the unduplicated individuals living within an X-mile radius (i.e., stop distance) of all stops. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Hispanic or Latino",
			"definition" : "Total number of individuals of that belong to the selected category and are living in the area.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Hispanic or Latino-S",
			"definition" : "Number of Individuals Served: Unduplicated summation of individuals of the selected category who are living within X distance of any stop. This metric is date/service independent.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Hispanic or Latino-SLOS",
			"definition" : "Number of Individuals Served at Level of Service: Unduplicated summation of individuals of the selected category who receive the specified minimum level of service.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Hispanic or Latino-SS",
			"definition" : "Number of Individuals Served by Service: Unduplicated summation of individuals of the selected category who are served by service is calculated as service stops multiplied by the unduplicated individuals living within an X-mile radius (i.e., stop distance) of all stops. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Native Hawaiian and Other Pacific Islander",
			"definition" : "Total number of individuals of that belong to the selected category and are living in the area.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Native Hawaiian and Other Pacific Islander-S",
			"definition" : "Number of Individuals Served: Unduplicated summation of individuals of the selected category who are living within X distance of any stop. This metric is date/service independent.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Native Hawaiian and Other Pacific Islander-SLOS",
			"definition" : "Number of Individuals Served at Level of Service: Unduplicated summation of individuals of the selected category who receive the specified minimum level of service.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Native Hawaiian and Other Pacific Islander-SS",
			"definition" : "Number of Individuals Served by Service: Unduplicated summation of individuals of the selected category who are served by service is calculated as service stops multiplied by the unduplicated individuals living within an X-mile radius (i.e., stop distance) of all stops. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Other Races",
			"definition" : "Total number of individuals of that belong to the selected category and are living in the area.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Other Races-S",
			"definition" : "Number of Individuals Served: Unduplicated summation of individuals of the selected category who are living within X distance of any stop. This metric is date/service independent.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Other Races-SLOS",
			"definition" : "Number of Individuals Served at Level of Service: Unduplicated summation of individuals of the selected category who receive the specified minimum level of service.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Other Races-SS",
			"definition" : "Number of Individuals Served by Service: Unduplicated summation of individuals of the selected category who are served by service is calculated as service stops multiplied by the unduplicated individuals living within an X-mile radius (i.e., stop distance) of all stops. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Two or More Races",
			"definition" : "Total number of individuals of that belong to the selected category and are living in the area.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Two or More Races-S",
			"definition" : "Number of Individuals Served: Unduplicated summation of individuals of the selected category who are living within X distance of any stop. This metric is date/service independent.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Two or More Races-SLOS",
			"definition" : "Number of Individuals Served at Level of Service: Unduplicated summation of individuals of the selected category who receive the specified minimum level of service.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Two or More Races-SS",
			"definition" : "Number of Individuals Served by Service: Unduplicated summation of individuals of the selected category who are served by service is calculated as service stops multiplied by the unduplicated individuals living within an X-mile radius (i.e., stop distance) of all stops. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "White",
			"definition" : "Total number of individuals of that belong to the selected category and are living in the area.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "White-S",
			"definition" : "Number of Individuals Served: Unduplicated summation of individuals of the selected category who are living within X distance of any stop. This metric is date/service independent.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "White-SLOS",
			"definition" : "Number of Individuals Served at Level of Service: Unduplicated summation of individuals of the selected category who receive the specified minimum level of service.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "White-SS",
			"definition" : "Number of Individuals Served by Service: Unduplicated summation of individuals of the selected category who are served by service is calculated as service stops multiplied by the unduplicated individuals living within an X-mile radius (i.e., stop distance) of all stops. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Asian & Pacific Islander",
			"definition" : "Total number of individuals of that belong to the selected category and are living in the area.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Asian & Pacific Islander-S",
			"definition" : "Number of Individuals Served: Unduplicated summation of individuals of the selected category who are living within X distance of any stop. This metric is date/service independent.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Asian & Pacific Islander-SLOS",
			"definition" : "Number of Individuals Served at Level of Service: Unduplicated summation of individuals of the selected category who receive the specified minimum level of service.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Asian & Pacific Islander-SS",
			"definition" : "Number of Individuals Served by Service: Unduplicated summation of individuals of the selected category who are served by service is calculated as service stops multiplied by the unduplicated individuals living within an X-mile radius (i.e., stop distance) of all stops. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "English",
			"definition" : "Total number of individuals of that belong to the selected category and are living in the area.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "English-S",
			"definition" : "Number of Individuals Served: Unduplicated summation of individuals of the selected category who are living within X distance of any stop. This metric is date/service independent.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "English-SLOS",
			"definition" : "Number of Individuals Served at Level of Service: Unduplicated summation of individuals of the selected category who receive the specified minimum level of service.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "English-SS",
			"definition" : "Number of Individuals Served by Service: Unduplicated summation of individuals of the selected category who are served by service is calculated as service stops multiplied by the unduplicated individuals living within an X-mile radius (i.e., stop distance) of all stops. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Indo European",
			"definition" : "Total number of individuals of that belong to the selected category and are living in the area.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Indo European-S",
			"definition" : "Number of Individuals Served: Unduplicated summation of individuals of the selected category who are living within X distance of any stop. This metric is date/service independent.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Indo European-SLOS",
			"definition" : "Number of Individuals Served at Level of Service: Unduplicated summation of individuals of the selected category who receive the specified minimum level of service.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Indo European-SS",
			"definition" : "Number of Individuals Served by Service: Unduplicated summation of individuals of the selected category who are served by service is calculated as service stops multiplied by the unduplicated individuals living within an X-mile radius (i.e., stop distance) of all stops. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Other Languages",
			"definition" : "Total number of individuals of that belong to the selected category and are living in the area.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Other Languages-S",
			"definition" : "Number of Individuals Served: Unduplicated summation of individuals of the selected category who are living within X distance of any stop. This metric is date/service independent.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Other Languages-SLOS",
			"definition" : "Number of Individuals Served at Level of Service: Unduplicated summation of individuals of the selected category who receive the specified minimum level of service.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Other Languages-SS",
			"definition" : "Number of Individuals Served by Service: Unduplicated summation of individuals of the selected category who are served by service is calculated as service stops multiplied by the unduplicated individuals living within an X-mile radius (i.e., stop distance) of all stops. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Spanish",
			"definition" : "Total number of individuals of that belong to the selected category and are living in the area.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Spanish-S",
			"definition" : "Number of Individuals Served: Unduplicated summation of individuals of the selected category who are living within X distance of any stop. This metric is date/service independent.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Spanish-SLOS",
			"definition" : "Number of Individuals Served at Level of Service: Unduplicated summation of individuals of the selected category who receive the specified minimum level of service.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Spanish-SS",
			"definition" : "Number of Individuals Served by Service: Unduplicated summation of individuals of the selected category who are served by service is calculated as service stops multiplied by the unduplicated individuals living within an X-mile radius (i.e., stop distance) of all stops. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Above Poverty Line",
			"definition" : "Total number of individuals of that belong to the selected category and are living in the area.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Above Poverty Line-S",
			"definition" : "Number of Individuals Served: Unduplicated summation of individuals of the selected category who are living within X distance of any stop. This metric is date/service independent.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Above Poverty Line-SLOS",
			"definition" : "Number of Individuals Served at Level of Service: Unduplicated summation of individuals of the selected category who receive the specified minimum level of service.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Above Poverty Line-SS",
			"definition" : "Number of Individuals Served by Service: Unduplicated summation of individuals of the selected category who are served by service is calculated as service stops multiplied by the unduplicated individuals living within an X-mile radius (i.e., stop distance) of all stops. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Below Poverty Line",
			"definition" : "Total number of individuals of that belong to the selected category and are living in the area.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Below Poverty Line-S",
			"definition" : "Number of Individuals Served: Unduplicated summation of individuals of the selected category who are living within X distance of any stop. This metric is date/service independent.",
		},
		{
			"report" : "Title VI Report",
			"metric" : "Below Poverty Line-SLOS",
			"definition" : "Number of Individuals Served at Level of Service: Unduplicated summation of individuals of the selected category who receive the specified minimum level of service.",
		},

		// 18. Census Tract Summary Report
		{
			"report" : "Tracts Summary Report",
			"metric" : "Geo ID",
			"definition" : "Identification number associated with the geographic area.",
		},
		{
			"report" : "Tracts Summary Report",
			"metric" : "Name",
			"definition" : "Name of the geographic area.",
		},
		{
			"report" : "Tracts Summary Report",
			"metric" : "Population",
			"definition" : "Total population of the geographic area.",
		},
		{
			"report" : "Tracts Summary Report",
			"metric" : "Employment (RAC)",
			"definition" : "Total Employment(RAC) of the geographic area.",
		},
		{
			"report" : "Tracts Summary Report",
			"metric" : "Employees (WAC)",
			"definition" : "Total Employees(WAC) of the geographic area.",
		},
		{
			"report" : "Tracts Summary Report",
			"metric" : "Land Area",
			"definition" : "Total land area of the geographic area in square miles.",
		},
		{
			"report" : "Tracts Summary Report",
			"metric" : "Water Area",
			"definition" : "Total water area of the geographic area in square miles.",
		},
		{
			"report" : "Tracts Summary Report",
			"metric" : "Total Agencies",
			"definition" : "Total number of transit agencies operating in the given geographic area.",
		},
		{
			"report" : "Tracts Summary Report",
			"metric" : "Total Routes",
			"definition" : "Total number of routes serving stops in the given geographic area.",
		},
		{
			"report" : "Tracts Summary Report",
			"metric" : "Total Stops",
			"definition" : "Total number of stops within the given geographic area.",
		},
		{
			"report" : "Tracts Summary Report",
			"metric" : "Urban Areas",
			"definition" : "Total number of Urban Areas within the geographic area.",
		},

		// 18.5 Census Tracts Extended Report
		{
			"report" : "Tract Extended Report",
			"metric" : "Geo ID",
			"definition" : "Identification number associated with the geographic area.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Name",
			"definition" : "Name of the geographic area.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Route Miles ",
			"definition" : "Summation of the lengths of the longest trips within the given geographic area.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Stops Per Square Mile",
			"definition" : "Stop count in the given geographic area divided by the area of the geographic area.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Stops Per Service Mile",
			"definition" : "Stop count in the given geographic area divided by service miles.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Service Hours",
			"definition" : "Total hours a transit agency spends on serving all round trips of routes within the given geographic area. Service hours may be calculated for a specific date or a set of dates specified using the calendar. Reported number are cumulative over the selected dates.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Service Miles",
			"definition" : "Total miles driven over all round trips of routes within the given geographic area. Service miles may be calculated for a specific date or a set of dates specified using the calendar. Reported number are cumulative over the selected dates.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Service Miles Per Square Mile",
			"definition" : "Service miles divided by the area of the geographic area.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Miles of Service Per Capita",
			"definition" : "Service miles divided by the population of the geographic area.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Urban Population Served (Unduplicated)",
			"definition" : "Summation of unduplicated urban population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Rural Population Served (Unduplicated)",
			"definition" : "Summation of unduplicated rural population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Percent of Population Served",
			"definition" : "Summation of unduplicated population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area divided by total population of the given geographic area.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Percent of Population Served at Level of Service",
			"definition" : "Summation of unduplicated population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service divided by total population of the given geographic area.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Urban Population Served at Level of Service",
			"definition" : "Summation of unduplicated urban population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Rural Population Served at Level of Service",
			"definition" : "Summation of unduplicated rural population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Percent of Population Unserved",
			"definition" : "100 minus percent of population served.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Service Stops",
			"definition" : "Total stops within the given geographic area multiplied by the number of times each stop is being served for the given date(s). Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Urban Population Served By Service",
			"definition" : "Total unduplicated urban population impacted within an X-mile radius (i.e., stop distance) of all stops within the given geographic area. Urban population served by service is calculated as service stops multiplied by the unduplicated urban population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area for every trip. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Rural Population Served By Service",
			"definition" : "Total unduplicated rural population impacted within an X-mile radius (i.e., stop distance) of all stops within the given geographic area. Rural population served by service is calculated as service stops multiplied by the unduplicated rural population within an X-mile radius (i.e., stop distance) of all stops within the given geographic area for every trip. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Employment Served (RAC)",
			"definition" : "Summation of unduplicated  employment(RAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Percent of Employment Served (RAC)",
			"definition" : "Summation of unduplicated employment (RAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area divided by total employment (RAC) of the given geographic area.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Percent of Employment Served at Level of Service (RAC)",
			"definition" : "Summation of unduplicated employment (RAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service divided by total employment (RAC) of the given geographic area.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : " Employment Served at Level of Service (RAC)",
			"definition" : "Summation of unduplicated  employment (RAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Percent of Employment Unserved (RAC)",
			"definition" : "100 minus percent of Employment (RAC) served.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : " Employment Served By Service (RAC)",
			"definition" : "Total unduplicated  Employment (RAC) impacted within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.  Employment (RAC) served by service is calculated as service stops multiplied by the unduplicated  Employment (RAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area for every trip. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : " Employees Served (WAC)",
			"definition" : "Summation of unduplicated employees (WAC)within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Percent of Employees Served (WAC)",
			"definition" : "Summation of unduplicated employees(WAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area divided by total employee(WAC) of the given geographic area.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Percent of Employees  Served at Level of Service (WAC)",
			"definition" : "Summation of unduplicated employees(WAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service divided by total employee(WAC) of the given geographic area.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Employees Served at Level of Service (WAC)",
			"definition" : "Summation of unduplicated  employees(WAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area that receives a specified minimum level of service.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Percent of Employee Unserved (WAC)",
			"definition" : "100 minus percent of employees (WAC) served.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Employees Served By Service (WAC)",
			"definition" : "Total unduplicated  employees (WAC) impacted within an X-mile radius (i.e., stop distance) of all stops within the given geographic area.  Employees (WAC) served by service is calculated as service stops multiplied by the unduplicated urban Employee (WAC) within an X-mile radius (i.e., stop distance) of all stops within the given geographic area for every trip. Reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Service Days",
			"definition" : "Set of days (from the selected days) in which at least one trip within the given geographic area is served.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Connected Communities",
			"definition" : "List of geographic areas of the same type that are connected to the area of interest through routes.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Hours of Service",
			"definition" : "Earliest and latest arrival and departure times of all transit stops within the given geographic area.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Minimum Fare",
			"definition" : "If available, this field points to the fare minimum for the given geographic area.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Average Fare",
			"definition" : "If available, this field points to the fare average for the given geographic area.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Median Fare",
			"definition" : "If available, this field points to the fare median for the given geographic area.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Maximum Fare",
			"definition" : "If available, this field points to the fare maximum for the given geographic area.",
		},

];