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
			"definition" : "Total land area of the state in square miles."
		},
		{
			"report" : "Statewide Summary Report",
			"metric" : "Population",
			"definition" : "Total population of the state."
		},
		{
			"report" : "Statewide Summary Report",
			"metric" : "Urban Population",
			"definition" : "Total aggregated population of urban census blocks within the state."
		},
		{
			"report" : "Statewide Summary Report",
			"metric" : "Rural Population",
			"definition" : "Total aggregated population of rural census blocks within the state."
		},
		{
			"report" : "Statewide Summary Report",
			"metric" : "Employment (RAC)",
			"definition" : "Total people employed residing in the geographic area. Metric is calculated using Residence Area Characteristic (RAC) data."
		},
		{
			"report" : "Statewide Summary Report",
			"metric" : "Employees (WAC)",
			"definition" : "Total people employed working in the geographic area. Metric is calculated using Working Area Characteristic (WAC) data."
		},
		{
			"report" : "Statewide Summary Report",
			"metric" : "Transit Agencies Count",
			"definition" : "Count of transit agencies operating in the state. Any transit agency with at least one stop in the geographic area is counted."
		},
		{
			"report" : "Statewide Summary Report",
			"metric" : "Routes Count",
			"definition" : "Total number of routes operated by the transit agencies in the geographic area."
		},
		{
			"report" : "Statewide Summary Report",
			"metric" : "Stops Count",
			"definition" : "Total number of stops operated by transit agencies in the geographic area."
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
			"definition" : "Count of counties within the state."
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
			"definition" : "Identification number reported in the transit agency’s GTFS feed.",
		},
		{
			"report" : "Transit Agencies Summary Report",
			"metric" : "Agency Name",
			"definition" : "Agency name reported in the transit agency’s GTFS feed.",
		},
		{
			"report" : "Transit Agencies Summary Report",
			"metric" : "Phone #",
			"definition" : "Contact phone number for the transit agency.",
		},
		{
			"report" : "Transit Agencies Summary Report",
			"metric" : "Total Routes",
			"definition" : "Total number of routes operated by the transit agency. This metric is date-independent.",
		},
		{
			"report" : "Transit Agencies Summary Report",
			"metric" : "Total Stops",
			"definition" : "Total number of stops operated by the transit agency. This metric is date-independent.",
		},
		{
			"report" : "Transit Agencies Summary Report",
			"metric" : "Geographic Areas",
			"definition" : "Count of geographic areas within which the transit agency operates. An agency is considered to operate in a geographic area if it has at least one stop within that area regardless of whether the stop is served or not.",
		},
		{
			"report" : "Transit Agencies Summary Report",
			"metric" : "Fare",
			"definition" : "If available, this is the fare information published by the transit agency on its web site. This metric is date-independent.",
		},
		{
			"report" : "Transit Agencies Summary Report",
			"metric" : "Service Start Date",
			"definition" : "The earliest service date specified in the transit agency’s GTFS feed in YYYYMMDD format.",
		},
		{
			"report" : "Transit Agencies Summary Report",
			"metric" : "Service End Date",
			"definition" : "The latest service date specified in the transit agency’s GTFS feed in YYYYMMDD format.",
		},
		{
			"report" : "Transit Agencies Summary Report",
			"metric" : "Feed Information",
			"definition" : "If available, this is the feed information such as name, version, publisher name, and publisher URL.",
		},

		// 2. Transit Agency Extended Report
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Agency ID",
			"definition" : "Identification number reported in the transit agency’s GTFS feed."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Agency Name",
			"definition" : "Agency name reported in the transit agency’s GTFS feed."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Route Miles",
			"definition" : "Summation of the lengths (in miles) of the routes operated by the transit agency. The length of the longest trip of a route is considered as the route length. This metric is date-independent."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Route Stops",
			"definition" : "Total number of stops served by the transit agency. This metric is date-independent."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Urban Stops",
			"definition" : "Total number of stops that are served by the transit agency that are located within urban census blocks. This metric is date-independent."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Rural Stops",
			"definition" : "Total number of stops that are served by the transit agency that are located within rural census blocks. This metric is date-independent."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Stops Per Route Mile",
			"definition" : "Route Stops of the transit agency divided by its Route Miles."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Service Hours",
			"definition" : "Total hours the transit agency serves all round trips of its routes. The service hours for a trip are calculated as the difference between the arrival time to the first stop of the trip and the departing time from the last stop of the trip. Service hours may be calculated for a specific date (or a set of dates) specified using the calendar. The services hours reported are cumulative over the selected dates."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Service Miles",
			"definition" : "Total miles driven over all round trips of routes running on selected date(s). Service miles may be calculated for a specific date (or a set of dates) specified using the calendar. The service miles reported are cumulative over the selected dates.",
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Urban Population Served",
			"definition" : "Total unduplicated population of urban census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops the transit agency serves. Each urban census block is counted once (unduplicated). This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Rural Population Served",
			"definition" : "Total unduplicated population of rural census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops the transit agency serves. Each rural census block is counted once (unduplicated). This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Urban Population Served at Level of Service",
			"definition" : " Total unduplicated population of urban census blocks whose centroids are located within an X-mile radius of any stop of the transit agency and served at least N-times on the selected date(s). X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Rural Population Served at Level of Service",
			"definition" : " Total unduplicated population of rural census blocks whose centroids are located within an X-mile radius of any stop of the transit agency and served at least N-times on the selected date(s). X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Employment Served (RAC)",
			"definition" : "Total number of unduplicated people employed who reside in census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops that the transit agency serves. Each census block is counted once (unduplicated). This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Employees Served (WAC)",
			"definition" : "Total number of unduplicated people employed who work in census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops that the transit agency serves. Each census block is counted once (unduplicated). This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."		
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Service Stops",
			"definition" : "Total number of times the agency stops are served on the given date(s). Service stops for a route is calculated as its stop count multiplied by the number of visits (i.e., trips or runs). The number reported is cumulative over the selected dates."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Urban Population Served By Service",
			"definition" : "Summation of Population Served by Service over all urban census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop served by the transit agency. Population served by service for a census block is calculated as the population of that block multiplied by the times the block is served by the transit agency on the selected date(s). The number reported is cumulative over the selected dates.",
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Rural Population Served By Service",
			"definition" : "Summation of Population Served by Service over all rural census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop served by the transit agency. Population served by service for a census block is calculated as the population of that block multiplied by the times the block is served by the transit agency on the selected date(s). The number reported is cumulative over the selected dates.",		
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Employment Served By Service (RAC)",
			"definition" : "Summation of Employment Served by Service over all census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop served by the transit agency. Employment Served by Service for a census block is calculated as the number of employed people residing in that block multiplied by the times the block is served by the transit agency on the selected date(s). The number reported is cumulative over the selected dates."		
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Employees Served By Service (WAC)",
			"definition" : "Summation of Employees Served by Service over all census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop served by the transit agency. Employees Served by Service for a census block is calculated as the number of employed people working in that block multiplied by the times the block is served by the transit agency on the selected date(s). The number reported is cumulative over the selected dates."		
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Service Days",
			"definition" : "Set of days (from the selected days) in which at least one trip is served by the selected transit agency."
		},
		{
			"report" : "Transit Agency Extended Report",
			"metric" : "Hours of Service",
			"definition" : "Difference between the earliest arrival time and latest departure time of all stops served by the transit agency. This metric is date-dependent."
		},

		// 3. Counties Summary Report
		{
			"report" : "Counties Summary Report",
			"metric" : "Geo ID",
			"definition" : "Identification number associated with the geographic area."
		},
		{
			"report" : "Counties Summary Report",
			"metric" : "Name",
			"definition" : "Name associated with the geographic area."
		},
		{
			"report" : "Counties Summary Report",
			"metric" : "ODOT Region ID",
			"definition" : "ODOT transit region ID associated with the geographic area."
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
			"definition" : "Total number of people employed living in the geographic area. Metric is calculated using Residence Area Characteristic (RAC) data."
		},
		{
			"report" : "Counties Summary Report",
			"metric" : "Employees (WAC)",
			"definition" : "Total number of people employed working in the geographic area. Metric is calculated using Working Area Characteristic (WAC) data."
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
			"definition" : "Total number of transit agencies operating in the given geographic area. An agency with at least one stop in the geographic area is counted."
		},
		{
			"report" : "Counties Summary Report",
			"metric" : "Total Urban Stops",
			"definition" : "Total number of stops within the given geographic area located in a urban census block.",
		},
		{
			"report" : "Counties Summary Report",
			"metric" : "Total Rural Stops",
			"definition" : "Total number of stops within the given geographic area located in an rural census block.",
		},
		{
			"report" : "Counties Summary Report",
			"metric" : "Urbanized Areas",
			"definition" : "Total number of Urbanized Areas within the geographic area."
		},
		{
			"report" : "Counties Summary Report",
			"metric" : "Urban Clusters",
			"definition" : "Total number of Urban Clusters within the geographic area."
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
			"definition" : "Summation of the lengths (in miles) of the routes operated by the transit agency within the given geographic area. The length of the longest trip of a route that is running on the selected date(s) is considered as the route length.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Stops Per Square Mile",
			"definition" : "Stop count in the given geographic area divided by the square miles of the geographic area. This metric is date-independent.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Stops Per Service Mile",
			"definition" : "Stop count in the given geographic area divided by Service Miles.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Service Hours",
			"definition" : "Total hours a transit agency spends serving all round trips of routes within the given geographic area. The service hours for a trip are calculated as the difference between the arrival time to the first stop of the trip and the departing time from the last stop of the trip. Service hours may be calculated for a specific date (or a set of dates) specified using the calendar. The services hours reported are cumulative over the selected dates.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Service Miles",
			"definition" : "Total miles driven over all round trips of routes running on the selected date(s) within the given geographic area. Service miles may be calculated for a specific date (or a set of dates) specified using the calendar. The service miles reported are cumulative over the selected dates."
		},
		{
			"report" : "County Extended Report",
			"metric" : "Service Miles Per Square Mile",
			"definition" : "Service Miles divided by the square miles of the geographic area.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Miles of Service Per Capita",
			"definition" : "Service Miles divided by the population of the geographic area.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Urban Population Served",
			"definition" : "Total unduplicated population of urban census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops within the given geographic area. This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "County Extended Report",
			"metric" : "Rural Population Served",
			"definition" : "Total unduplicated population of rural census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops within the given geographic area. This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "County Extended Report",
			"metric" : "Percent of Population Served",
			"definition" : "Total unduplicated population of census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops within the given geographic area divided by the total population of the geographic area. This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "County Extended Report",
			"metric" : "Percent of Population Served at Level of Service",
			"definition" : "Total unduplicated population of census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s) divided by the total population of the geographic area. X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "County Extended Report",
			"metric" : "Urban Population Served at Level of Service",
			"definition" : " Total unduplicated population of urban census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s). X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "County Extended Report",
			"metric" : "Rural Population Served at Level of Service",
			"definition" : " Total unduplicated population of rural census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s). X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "County Extended Report",
			"metric" : "Percent of Population Unserved",
			"definition" : "100 minus percent of population served.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Service Stops",
			"definition" : "Total number of times the stops within the geographic area are served on the given date(s). Service stops for a route is calculated as its stop count multiplied by the number of visits (i.e., trips or runs). The number reported is cumulative over the selected dates."
		},
		{
			"report" : "County Extended Report",
			"metric" : "Urban Population Served By Service",
			"definition" : "Summation of Population Served by Service over all urban census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop in the given geographic area. Population served by service for a census block is calculated as the population of that block multiplied by the times that block is served on the selected date(s) by all agencies. The number reported is cumulative over the selected dates."
		},
		{
			"report" : "County Extended Report",
			"metric" : "Rural Population Served By Service",
			"definition" : "Summation of Population Served by Service over all rural census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop in the given geographic area. Population served by service for a census block is calculated as the population of that block multiplied by the times that block is served on the selected date(s) by all agencies. The number reported is cumulative over the selected dates."
		},
		{
			"report" : "County Extended Report",
			"metric" : "Employment Served (RAC)",
			"definition" : "Total number of unduplicated people employed residing in census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops in the geographic area. Each census block is counted once (unduplicated). This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "County Extended Report",
			"metric" : "Percent of Employment Served (RAC)",
			"definition" : "Employment Served (RAC) divided by the number of people residing in the given geographic area.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Percent of Employment Served at Level of Service (RAC)",
			"definition" : "Total unduplicated people employed residing in census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s) divided by the total number of people employed working in the area. X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "County Extended Report",
			"metric" : "Employment Served at Level of Service (RAC)",
			"definition" : "Total unduplicated people employed residing in census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s). X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "County Extended Report",
			"metric" : "Percent of Employment Unserved (RAC)",
			"definition" : "100 minus percent of Employment Served (RAC).",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Employment Served By Service (RAC)",
			"definition" : "Summation of Employment Served by Service over all census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop in the geographic area. Employment Served by Service for a census block is calculated as the number of employed people residing in that block multiplied by the times that block is served on the selected date(s). The number reported is cumulative over the selected dates."
		},
		{
			"report" : "County Extended Report",
			"metric" : "Employees Served (WAC)",
			"definition" : "Total number of unduplicated people employed working in census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops in the geographic area. Each census block is counted once (unduplicated). This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "County Extended Report",
			"metric" : "Percent of Employees Served (WAC)",
			"definition" : "Employees Served (WAC) divided by the number of people working in the given geographic area."
		},
		{
			"report" : "County Extended Report",
			"metric" : "Percent of Employees Served at Level of Service (WAC)",
			"definition" : "Total unduplicated people employed working in census blocks whose centroids are located within an X-miles radius of any stop that is within the geographical area and served at least N-times on the selected date(s) divided by total number of employed people working in the area. X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "County Extended Report",
			"metric" : "Employees Served at Level of Service (WAC)",
			"definition" : "Total unduplicated people employed working in census blocks whose centroids are located within an X-miles radius of any stop that is within the geographical area and served at least N-times on the selected date(s). X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "County Extended Report",
			"metric" : "Percent of Employee Unserved (WAC)",
			"definition" : "100 minus percent of employees served (WAC).",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Employees Served By Service (WAC)",
			"definition" : "Summation of Employees Served by Service over all census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop in the geographic area. Employment Served by Service for a census block is calculated as the number of employed people working in that block multiplied by the times that block is served on the selected date(s). The number reported is cumulative over the selected dates."
		},
		{
			"report" : "County Extended Report",
			"metric" : "Service Days",
			"definition" : "Set of days (from the selected days) in which at least one trip within the given geographic area is served.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Connected Communities",
			"definition" : "List of geographic areas of the same type that are connected to the area of interest through routes that are served on the selected date(s).",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Hours of Service",
			"definition" : " Difference between the earliest arrival time and latest departure time of all transit stops within the given geographic area.",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Minimum Fare",
			"definition" : "If available, this field points to the minimum fare for the given geographic area during the selected date(s).",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Average Fare",
			"definition" : "If available, this field points to the average fare for the given geographic area during the selected date(s).",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Median Fare",
			"definition" : "If available, this field points to the median fare for the given geographic area during the selected date(s).",
		},
		{
			"report" : "County Extended Report",
			"metric" : "Maximum Fare",
			"definition" : "If available, this field points to the maximum fare for the given geographic area during the selected date(s).",
		},

		// 5. Census Places Summary Report
		{
			"report" : "Census Places Summary Report",
			"metric" : "Geo ID",
			"definition" : "Identification number associated with the geographic area."
		},
		{
			"report" : "Census Places Summary Report",
			"metric" : "Name",
			"definition" : "Name associated with the geographic area."
		},
		{
			"report" : "Census Places Summary Report",
			"metric" : "Population ",
			"definition" : "Total population of the geographic area."
		},
		{
			"report" : "Census Places Summary Report",
			"metric" : "Employment (RAC)",
			"definition" : "Total number of people employed residing in the geographic area. Metric is calculated using Residence Area Characteristic (RAC) data."
		},
		{
			"report" : "Census Places Summary Report",
			"metric" : "Employees (WAC)",
			"definition" : "Total number of people employed working in the geographic area. Metric is calculated using Working Area Characteristic (WAC) data."
		},
		{
			"report" : "Census Places Summary Report",
			"metric" : "Land Area",
			"definition" : "Total land area of the geographic area in square miles."
		},
		{
			"report" : "Census Places Summary Report",
			"metric" : "Water Area",
			"definition" : "Total water area of the geographic area in square miles."
		},
		{
			"report" : "Census Places Summary Report",
			"metric" : "Total Agencies",
			"definition" : "Total number of transit agencies operating in the given geographic area. An agency with at least one stop in the geographic area is counted."
		},
		{
			"report" : "Census Places Summary Report",
			"metric" : "Total Routes",
			"definition" : "Total number of routes serving stops in the given geographic area."
		},
		{
			"report" : "Census Places Summary Report",
			"metric" : "Total Urban Stops",
			"definition" : "Total number of stops within the given geographic area located in a urban census block.",
		},
		{
			"report" : "Census Places Summary Report",
			"metric" : "Total Rural Stops",
			"definition" : "Total number of stops within the given geographic area located in an rural census block.",
		},
		{
			"report" : "Census Places Summary Report",
			"metric" : "Urbanized Areas",
			"definition" : "Total number of Urbanized Areas within the geographic area."
		},
		{
			"report" : "Census Places Summary Report",
			"metric" : "Urban Clusters",
			"definition" : "Total number of Urban Clusters within the geographic area."
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
			"definition" : "Summation of the lengths (in miles) of the routes within the given geographic area. The length of the longest trip of a route that is running on the selected date(s) is considered as the route length.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Stops Per Square Mile",
			"definition" : "Stop count in the given geographic area divided by the square miles of the geographic area. This metric is date-independent.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Stops Per Service Mile",
			"definition" : "Stop count in the given geographic area divided by Service Miles.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Service Hours",
			"definition" : "Total hours a transit agency spends serving all round trips of routes within the given geographic area. The service hours for a trip is calculated as the difference between the arrival time to the first stop of the trip and the departure time from the last stop of the trip. Service hours may be calculated for a specific date (or a set of dates) specified using the calendar. The number reported is cumulative over the selected dates.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Service Miles",
			"definition" : "Total miles driven over all round trips of routes running on the selected date(s) within the given geographic area. Service miles may be calculated for a specific date (or a set of dates) specified using the calendar. The number reported is cumulative over the selected dates."
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Service Miles Per Square Mile",
			"definition" : "Service Miles divided by the square miles of the geographic area.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Miles of Service Per Capita",
			"definition" : "Service Miles divided by the population of the geographic area.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Urban Population Served",
			"definition" : "Total unduplicated population of urban census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops within the given geographic area. This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Rural Population Served",
			"definition" : "Total unduplicated population of rural census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops within the given geographic area. This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Percent of Population Served",
			"definition" : "Total unduplicated population of census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops within the given geographic area divided by the total population of the area. This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Percent of Population Served at Level of Service",
			"definition" : "Total unduplicated population of census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s) divided by the total population of the area. X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Urban Population Served at Level of Service",
			"definition" : "Total unduplicated population of urban census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s). X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Rural Population Served at Level of Service",
			"definition" : "Total unduplicated population of rural census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s). X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Percent of Population Unserved",
			"definition" : "100 minus percent of population served.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Service Stops",
			"definition" : "Total number of times the stops within the geographic area are served on the given date(s). Service stops for a route is calculated as its stop count multiplied by the number of visits (i.e., trips or runs). The number reported is cumulative over the selected dates."
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Urban Population Served By Service",
			"definition" : "Summation of Population Served by Service over all urban census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop in the given geographic area. Population served by service for a census block is calculated as the population of that block multiplied by the times that block is served on the selected date(s) by all agencies. The number reported is cumulative over the selected dates."
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Rural Population Served By Service",
			"definition" : "Summation of Population Served by Service over all rural census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop in the given geographic area. Population served by service for a census block is calculated as the population of that block multiplied by the times that block is served on the selected date(s) by all agencies. The number reported is cumulative over the selected dates."
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Employment Served (RAC)",
			"definition" : "Total number of unduplicated people employed residing in census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops in the geographic area. Each census block is counted once (unduplicated). This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Percent of Employment Served (RAC)",
			"definition" : "Employment Served (RAC) divided by the number of people residing in the given geographic area.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Percent of Employment Served at Level of Service (RAC)",
			"definition" : "Total unduplicated people employed residing in census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s) divided by total number of employed people working in the area. X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Employment Served at Level of Service (RAC)",
			"definition" : "Total unduplicated people employed residing in census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s). X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Percent of Employment Unserved (RAC)",
			"definition" : "100 minus percent of Employment Served (RAC).",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Employment Served By Service (RAC)",
			"definition" : "Summation of Employment Served by Service over all census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop in the geographic area. Employment Served by Service for a census block is calculated as the number of people employed residing in that block multiplied by the times that block is served on the selected date(s). The number reported is cumulative over the selected dates."
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Employees Served (WAC)",
			"definition" : "Total number of unduplicated people employed working in census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops in the geographic area. Each census block is counted once (unduplicated). This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Percent of Employees Served (WAC)",
			"definition" : "Employees Served (WAC) divided by the number of people working in the given geographic area."
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Percent of Employees Served at Level of Service (WAC)",
			"definition" : "Total unduplicated people employed working in census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s) divided by total number of employed people working in the area. X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Employees Served at Level of Service (WAC)",
			"definition" : "Total unduplicated people employed working in census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s). X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Percent of Employee Unserved (WAC)",
			"definition" : "100 minus percent of employees served (WAC).",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Employees Served By Service (WAC)",
			"definition" : "Summation of Employees Served by Service over all census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop in the geographic area. Employment Served by Service for a census block is calculated as the number of employed people working in that block multiplied by the times that block is served on the selected date(s). The number reported is cumulative over the selected dates."
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Service Days",
			"definition" : "Set of days (from the selected days) in which at least one trip within the given geographic area is served.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Connected Communities",
			"definition" : "List of geographic areas of the same type that are connected to the area of interest through routes that are served on the selected date(s).",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Hours of Service",
			"definition" : "Difference between the earliest arrival time and latest departure time of all transit stops within the given geographic area.",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Minimum Fare",
			"definition" : "If available, this field points to the minimum fare for the given geographic area during the selected date(s).",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Average Fare",
			"definition" : "If available, this field points to the average fare for the given geographic area during the selected date(s).",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Median Fare",
			"definition" : "If available, this field points to the median fare for the given geographic area during the selected date(s).",
		},
		{
			"report" : "Census Place Extended Report",
			"metric" : "Maximum Fare",
			"definition" : "If available, this field points to the maximum fare for the given geographic area during the selected date(s).",
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
			"definition" : "Total number of people employed residing in the geographic area.",
		},
		{
			"report" : "Congressional Districts Summary Report",
			"metric" : "Employees (WAC)",
			"definition" : "Total number of people employed working in the geographic area.",
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
			"metric" : "Total Urban Stops",
			"definition" : "Total number of stops within the given geographic area located in a urban census block.",
		},
		{
			"report" : "Congressional Districts Summary Report",
			"metric" : "Total Rural Stops",
			"definition" : "Total number of stops within the given geographic area located in an rural census block.",
		},
		{
			"report" : "Congressional Districts Summary Report",
			"metric" : "Urbanized Areas",
			"definition" : "Total number of Urbanized Areas within the geographic area."
		},
		{
			"report" : "Congressional Districts Summary Report",
			"metric" : "Urban Clusters",
			"definition" : "Total number of Urban Clusters within the geographic area."
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
			"definition" : "Summation of the lengths (in miles) of the routes operated within the given geographic area. The length of the longest trip of a route that is running on the selected date(s) is considered as the route length.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Stops Per Square Mile",
			"definition" : "Stop count in the given geographic area divided by the square miles of the geographic area. This metric is date-independent.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Stops Per Service Mile",
			"definition" : "Stop count in the given geographic area divided by Service Miles.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Service Hours",
			"definition" : "Total hours a transit agency spends serving all round trips of routes within the given geographic area. The service hours for a trip are calculated as the difference between the arrival time to the first stop of the trip and the departing time from the last stop of the trip. Service hours may be calculated for a specific date (or a set of dates) specified using the calendar. The services hours reported are cumulative over the selected dates.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Service Miles",
			"definition" : "Total miles driven over all round trips of routes running on the selected date(s) within the given geographic area. Service miles may be calculated for a specific date or a set of dates specified using the calendar. The service miles reported are cumulative over the selected dates."
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Service Miles Per Square Mile",
			"definition" : "Service Miles divided by the square miles of the geographic area.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Miles of Service Per Capita",
			"definition" : "Service Miles divided by the population of the geographic area.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Urban Population Served",
			"definition" : "Total unduplicated population of urban census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops within the given geographic area. This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Rural Population Served",
			"definition" : "Total unduplicated population of rural census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops within the given geographic area. This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Percent of Population Served",
			"definition" : "Total unduplicated population of census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops within the given geographic area divided by total population of the area. This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Percent of Population Served at Level of Service",
			"definition" : "Total unduplicated population of census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s) divided by total population of the area. X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Urban Population Served at Level of Service",
			"definition" : "Total unduplicated population of urban census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s). X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Rural Population Served at Level of Service",
			"definition" : "Total unduplicated population of rural census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s). X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Percent of Population Unserved",
			"definition" : "100 minus percent of population served.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Service Stops",
			"definition" : "Total number of times the stops within the geographic area are served on the given date(s). Service stops for a route is calculated as its stop count multiplied by the number of visits (i.e., trips or runs). The number reported is cumulative over the selected dates."
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Urban Population Served By Service",
			"definition" : "Summation of Population Served by Service over all urban census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop in the given geographic area. Population served by service for a census block is calculated as the population of that block multiplied by the times that block is served on the selected date(s) by all agencies. The number reported is cumulative over the selected dates."
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Rural Population Served By Service",
			"definition" : "Summation of Population Served by Service over all rural census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop in the given geographic area. Population served by service for a census block is calculated as the population of that block multiplied by the times that block is served on the selected date(s) by all agencies. The number reported is cumulative over the selected dates."
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Employment Served (RAC)",
			"definition" : "Total number of unduplicated people employed residing in census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops in the geographic area. Each census block is counted once (unduplicated). This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Percent of Employment Served (RAC)",
			"definition" : "Employment Served (RAC) divided by the number of people residing in the given geographic area.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Percent of Employment Served at Level of Service (RAC)",
			"definition" : "Total unduplicated people employed residing in census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s) divided by total number of employed people working in the area. X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Employment Served at Level of Service (RAC)",
			"definition" : "Total unduplicated people employed residing in census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s). X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Percent of Employment Unserved (RAC)",
			"definition" : "100 minus percent of Employment Served (RAC).",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Employment Served By Service (RAC)",
			"definition" : "Summation of Employment Served by Service over all census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop in the geographic area. Employment Served by Service for a census block is calculated as the number of employed people residing in that block multiplied by the times that block is served on the selected date(s). The number reported is cumulative over the selected dates."
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Employees Served (WAC)",
			"definition" : "Total number of unduplicated people employed working in census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops in the geographic area. Each census block is counted once (unduplicated). This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Percent of Employees Served (WAC)",
			"definition" : "Employees Served (WAC) divided by the number of people working in the given geographic area."
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Percent of Employees Served at Level of Service (WAC)",
			"definition" : "Total unduplicated people employed working in census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s) divided by total number of employed people working in the area. X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Employees Served at Level of Service (WAC)",
			"definition" : "Total unduplicated people employed working in census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s). X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Percent of Employee Unserved (WAC)",
			"definition" : "100 minus percent of employees served (WAC).",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Employees Served By Service (WAC)",
			"definition" : "Summation of Employees Served by Service over all census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop in the geographic area. Employment Served by Service for a census block is calculated as the number of employed people working in that block multiplied by the times that block is served on the selected date(s). The number reported is cumulative over the selected dates."
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Service Days",
			"definition" : "Set of days (from the selected days) in which at least one trip within the given geographic area is served.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Connected Communities",
			"definition" : "List of geographic areas of the same type that are connected to the area of interest through routes that are served on the selected date(s).",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Hours of Service",
			"definition" : "Difference between the earliest arrival time and latest departure time of all transit stops within the given geographic area.",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Minimum Fare",
			"definition" : "If available, this field points to the minimum fare for the given geographic area during the selected date(s).",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Average Fare",
			"definition" : "If available, this field points to the average fare for the given geographic area during the selected date(s).",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Median Fare",
			"definition" : "If available, this field points to the median fare for the given geographic area during the selected date(s).",
		},
		{
			"report" : "Congressional District Extended Report",
			"metric" : "Maximum Fare",
			"definition" : "If available, this field points to the maximum fare for the given geographic area during the selected date(s).",
		},
		
		// 7. Urban Areas Summary Report
		{
			"report" : "Urban Areas Summary Report",
			"metric" : "Geo ID",
			"definition" : "Identification number associated with the geographic area."
		},
		{
			"report" : "Urban Areas Summary Report",
			"metric" : "Name",
			"definition" : "Name associated with the geographic area."
		},
		{
			"report" : "Urban Areas Summary Report",
			"metric" : "Population",
			"definition" : "Total population of the geographic area."
		},
		{
			"report" : "Urban Areas Summary Report",
			"metric" : "Employment (RAC)",
			"definition" : "Total number of people employed residing in the geographic area. Metric is calculated using Residence Area Characteristic (RAC) data."
		},
		{
			"report" : "Urban Areas Summary Report",
			"metric" : "Employees (WAC)",
			"definition" : "Total number of people employed working in the geographic area. Metric is calculated using Working Area Characteristic (WAC) data."
		},
		{
			"report" : "Urban Areas Summary Report",
			"metric" : "Land Area",
			"definition" : "Total land area of the geographic area in square miles."
		},
		{
			"report" : "Urban Areas Summary Report",
			"metric" : "Water Area",
			"definition" : "Total water area of the geographic area in square miles."
		},
		{
			"report" : "Urban Areas Summary Report",
			"metric" : "Total Agencies",
			"definition" : "Total number of transit agencies operating in the given geographic area. An agency with at least one stop in the geographic area is counted."
		},
		{
			"report" : "Urban Areas Summary Report",
			"metric" : "Total Routes",
			"definition" : "Total number of routes serving stops in the given geographic area."
		},
		{
			"report" : "Urban Areas Summary Report",
			"metric" : "Total Stops",
			"definition" : "Total number of stops within the given geographic area."
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
			"definition" : "Summation of the lengths (in miles) of the routes within the given geographic area. The length of the longest trip of a route that is running on the selected date(s) is considered as the route length.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Stops Per Square Mile",
			"definition" : "Stop count in the given geographic area divided by the square miles of the geographic area. This metric is date-independent.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Stops Per Service Mile",
			"definition" : "Stop count in the given geographic area divided by Service Miles.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Service Hours",
			"definition" : "Total hours a transit agency spends serving all round trips of routes within the given geographic area. The service hours for a trip are calculated as the difference between the arrival time to the first stop of the trip and the departing time from the last stop of the trip. Service hours may be calculated for a specific date (or a set of dates) specified using the calendar. The number reported is cumulative over the selected dates.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Service Miles",
			"definition" : "Total miles driven over all round trips of routes running on the selected date(s) within the given geographic area. Service miles may be calculated for a specific date (or a set of dates) specified using the calendar. The reported number is cumulative over the selected dates."
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Service Miles Per Square Mile",
			"definition" : "Service Miles divided by the square miles of the geographic area.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Miles of Service Per Capita",
			"definition" : "Service Miles divided by the population of the geographic area.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Urban Population Served",
			"definition" : "Total unduplicated population of urban census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops within the given geographic area. This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Rural Population Served",
			"definition" : "Total unduplicated population of rural census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops within the given geographic area. This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Percent of Population Served",
			"definition" : "Total unduplicated population of census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops within the given geographic area divided by total population of the area. This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Percent of Population Served at Level of Service",
			"definition" : "Total unduplicated population of census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s) divided by total population of the area. X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Urban Population Served at Level of Service",
			"definition" : "Total unduplicated population of urban census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s). X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Rural Population Served at Level of Service",
			"definition" : "Total unduplicated population of rural census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s). X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Percent of Population Unserved",
			"definition" : "100 minus percent of population served.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Service Stops",
			"definition" : "Total number of times the stops within the geographic area are served on the given date(s). Service stops for a route is calculated as its stop count multiplied by the number of visits (i.e., trips or runs). The number reported is cumulative over the selected dates."
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Urban Population Served By Service",
			"definition" : "Summation of Population Served by Service over all urban census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop in the given geographic area. Population served by service for a census block is calculated as the population of that block multiplied by the times that block is served on the selected date(s) by all agencies. The number reported is cumulative over the selected dates."
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Rural Population Served By Service",
			"definition" : "Summation of Population Served by Service over all rural census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop in the given geographic area. Population served by service for a census block is calculated as the population of that block multiplied by the times that block is served on the selected date(s) by all agencies. The number reported is cumulative over the selected dates."
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Employment Served (RAC)",
			"definition" : "Total number of unduplicated people employed residing in census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops in the geographic area. Each census block is counted once (unduplicated). This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Percent of Employment Served (RAC)",
			"definition" : "Employment Served (RAC) divided by the number of people residing in the given geographic area.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Percent of Employment Served at Level of Service (RAC)",
			"definition" : "Total unduplicated people employed residing in census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s) divided by total number of employed people working in the area. X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Employment Served at Level of Service (RAC)",
			"definition" : "Total unduplicated people employed residing in census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s). X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Percent of Employment Unserved (RAC)",
			"definition" : "100 minus percent of Employment Served (RAC).",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Employment Served By Service (RAC)",
			"definition" : "Summation of Employment Served by Service over all census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop in the geographic area. Employment Served by Service for a census block is calculated as the number of employed people residing in that block multiplied by the times that block is served on the selected date(s). The number reported is cumulative over the selected dates."
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Employees Served (WAC)",
			"definition" : "Total number of unduplicated people employed working in census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops in the geographic area. Each census block is counted once (unduplicated). This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Percent of Employees Served (WAC)",
			"definition" : "Employees Served (WAC) divided by the number of people working in the given geographic area."
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Percent of Employees Served at Level of Service (WAC)",
			"definition" : "Total unduplicated people employed working in census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s) divided by total number of employed people working in the area. X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Employees Served at Level of Service (WAC)",
			"definition" : "Total unduplicated people employed working in census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s). X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Percent of Employee Unserved (WAC)",
			"definition" : "100 minus percent of employees served (WAC).",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Employees Served By Service (WAC)",
			"definition" : "Summation of Employees Served by Service over all census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop in the geographic area. Employment Served by Service for a census block is calculated as the number of employed people working in that block multiplied by the times that block is served on the selected date(s). The number reported is cumulative over the selected dates."
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Service Days",
			"definition" : "Set of days (from the selected days) in which at least one trip within the given geographic area is served.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Connected Communities",
			"definition" : "List of geographic areas of the same type that are connected to the area of interest through routes that are served on the selected date(s).",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Hours of Service",
			"definition" : "Difference between the earliest arrival time and latest departure time of all transit stops within the given geographic area.",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Minimum Fare",
			"definition" : "If available, this field points to the minimum fare for the given geographic area during the selected date(s).",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Average Fare",
			"definition" : "If available, this field points to the average fare for the given geographic area during the selected date(s).",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Median Fare",
			"definition" : "If available, this field points to the median fare for the given geographic area during the selected date(s).",
		},
		{
			"report" : "Urban Area Extended Report",
			"metric" : "Maximum Fare",
			"definition" : "If available, this field points to the maximum fare for the given geographic area during the selected date(s).",
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
			"metric" : "Employment (RAC)",
			"definition" : "Total number of people employed residing in the geographic area. Metric is calculated using Working Area Characteristic (WAC) data.",
		},
		{
			"report" : "Aggregated Urban Areas Summary Report",
			"metric" : "Employees (WAC)",
			"definition" : "Total number of people employed working in the geographic area. Metric is calculated using Working Area Characteristic (WAC) data.",
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
			"definition" : "Total number of stops within the given geographic area. This metric is date independent, i.e., stops may or may not be served on the selected date(s).",
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
			"definition" : "If available, this field points to the fare information of the services provided by agencies on the selected date(s). The fair information is published by transit agencies in their GTFS data.",
		},
		{
			"report" : "Aggregated Urban Areas Extended Report",
			"metric" : "Connected Communities",
			"definition" : "List of geographic areas of same type that are connected to the area of interest through routes that are served on the selected date(s).",
		},
		{
			"report" : "Aggregated Urban Areas Extended Report",
			"metric" : "Route Miles",
			"definition" : "Summation of the lengths (in miles) of the routes within the given geographic area. The length of the longest trip of a route that is running on the selected date(s) is considered as the route length.",
		},
		{
			"report" : "Aggregated Urban Areas Extended Report",
			"metric" : "Stops Per Square Mile",
			"definition" : "Stop count in the given geographic area divided by the area of the geographic area. This metric is date independent, i.e., the counted stops may or may not be served on the selected date(s).",
		},
		{
			"report" : "Aggregated Urban Areas Extended Report",
			"metric" : "Stops Per Service Mile",
			"definition" : "Stop count in the given geographic area divided by service miles. The stops counted here may or may not be served. Service Miles is date dependent.",
		},
		{
			"report" : "Aggregated Urban Areas Extended Report",
			"metric" : "Service Hours",
			"definition" : "Total hours a transit agency spends serving all round trips of routes within the given geographic area. The service hours for a trip are calculated as the difference between the arrival time to the first stop of the trip and the departing time from the last stop of the trip. Service hours may be calculated for a specific date or a set of dates specified using the calendar. The reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Aggregated Urban Areas Extended Report",
			"metric" : "Service Miles",
			"definition" : "Total miles driven over all round trips of routes running on the selected date(s) within the given geographic area. Service miles may be calculated for a specific date (or a set of dates) specified using the calendar. The reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Aggregated Urban Areas Extended Report",
			"metric" : "Service Miles Per Square Mile",
			"definition" : "Service miles divided by the square miles of the geographic area."
		},
		{
			"report" : "Aggregated Urban Areas Extended Report",
			"metric" : "Miles of Service Per Capita",
			"definition" : "Service miles divided by the population of the geographic area."
		},
		{
			"report" : "Aggregated Urban Areas Extended Report",
			"metric" : "Population Served",
			"definition" : "Total unduplicated population of census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops within the given geographic area. This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Aggregated Urban Areas Extended Report",
			"metric" : "Percent of Population Served",
			"definition" : "Population served divided by total population of the given geographic area. This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Aggregated Urban Areas Extended Report",
			"metric" : "Percent of Population Served at Level of Service",
			"definition" : "Total unduplicated population of census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s) divided by total population of the area. X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "Aggregated Urban Areas Extended Report",
			"metric" : "Percent of Population Unserved",
			"definition" : "100 minus percent of population served.",
		},
		{
			"report" : "Aggregated Urban Areas Extended Report",
			"metric" : "Service Stops",
			"definition" : "Total number of times the stops within the geographic area are served on the given date(s). Service stops for a route is calculated as its stop count multiplied by the number of visits (i.e., trips or runs). The number reported is cumulative over the selected dates."
		},
		{
			"report" : "Aggregated Urban Areas Extended Report",
			"metric" : "Population Served By Service",
			"definition" : "Summation of Population Served by Service over all census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop in the given geographic area. Population served by service for a census block is calculated as the population of that block multiplied by the times that block is served on the selected date(s) by all agencies. The number reported is cumulative over the selected dates.",
		},
		{
			"report" : "Aggregated Urban Areas Extended Report",
			"metric" : "Service Days",
			"definition" : "Set of days (from the selected days) in which at least one trip within the given geographic area is served.",
		},
		{
			"report" : "Aggregated Urban Areas Extended Report",
			"metric" : "Hours of Service",
			"definition" : "Difference between the earliest arrival time and latest departure time  of all transit stops within the given geographic area.",
		},
		
		// 9. ODOT Transit Regions Summary Report
		{
			"report" : "ODOT Transit Regions Summary Report",
			"metric" : "Geo ID",
			"definition" : "Identification number associated with the geographic area."
		},
		{
			"report" : "ODOT Transit Regions Summary Report",
			"metric" : "Name",
			"definition" : "Name associated with the geographic area."
		},
		{
			"report" : "ODOT Transit Regions Summary Report",
			"metric" : "Population",
			"definition" : "Total population of the geographic area."
		},
		{
			"report" : "ODOT Transit Regions Summary Report",
			"metric" : "Employment (RAC)",
			"definition" : "Total number of people employed residing in the geographic area. Metric is calculated using Residence Area Characteristic (RAC) data."
		},
		{
			"report" : "ODOT Transit Regions Summary Report",
			"metric" : "Employees (WAC)",
			"definition" : "Total number of people employed working in the geographic area. Metric is calculated using Working Area Characteristic (WAC) data."
		},
		{
			"report" : "ODOT Transit Regions Summary Report",
			"metric" : "Land Area",
			"definition" : "Total land area of the geographic area in square miles."
		},
		{
			"report" : "ODOT Transit Regions Summary Report",
			"metric" : "Water Area",
			"definition" : "Total water area of the geographic area in square miles."
		},
		{
			"report" : "ODOT Transit Regions Summary Report",
			"metric" : "Total Agencies",
			"definition" : "Total number of transit agencies operating in the given geographic area. An agency with at least one stop in the geographic area is counted."
		},
		{
			"report" : "ODOT Transit Regions Summary Report",
			"metric" : "Total Routes",
			"definition" : "Total number of routes serving stops in the given geographic area."
		},
		{
			"report" : "ODOT Transit Regions Summary Report",
			"metric" : "Total Urban Stops",
			"definition" : "Total number of stops within the given geographic area located in a urban census block.",
		},
		{
			"report" : "ODOT Transit Regions Summary Report",
			"metric" : "Total Rural Stops",
			"definition" : "Total number of stops within the given geographic area located in an rural census block.",
		},
		{
			"report" : "ODOT Transit Regions Summary Report",
			"metric" : "Urbanized Areas",
			"definition" : "Total number of Urbanized Areas within the geographic area."
		},
		{
			"report" : "ODOT Transit Regions Summary Report",
			"metric" : "Urban Clusters",
			"definition" : "Total number of Urban Clusters within the geographic area."
		},
		{
			"report" : "ODOT Transit Regions Summary Report",
			"metric" : "Counties",
			"definition" : "List of the counties within the geographic area."
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
			"definition" : "Summation of the lengths of the routes within the given geographic area. Length of the longest trip of a route that is running on the selected date(s) is considered as the route length.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Stops Per Square Mile",
			"definition" : "Stop count in the given geographic area divided by the area of the geographic area. This metric is date-independent.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Stops Per Service Mile",
			"definition" : "Stop count in the given geographic area divided by Service Miles.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Service Hours",
			"definition" : "Total hours a transit agency spends serving all round trips of routes within the given geographic area. The service hours for a trip are calculated as the difference between the arrival time to the first stop of the trip and the departing time from the last stop of the trip. Service hours may be calculated for a specific date or a set of dates specified using the calendar. The reported number is cumulative over the selected dates.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Service Miles",
			"definition" : "Total miles driven over all round trips of routes running on the selected date(s) within the given geographic area. Service miles may be calculated for a specific date or a set of dates specified using the calendar. The reported number is cumulative over the selected dates."
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Service Miles Per Square Mile",
			"definition" : "Service Miles divided by the area of the geographic area.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Miles of Service Per Capita",
			"definition" : "Service Miles divided by the population of the geographic area.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Urban Population Served",
			"definition" : "Total unduplicated population of urban census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops within the given geographic area. This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Rural Population Served",
			"definition" : "Total unduplicated population of rural census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops within the given geographic area. This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Percent of Population Served",
			"definition" : "Total unduplicated population of census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops within the given geographic area divided by total population of the area. This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Percent of Population Served at Level of Service",
			"definition" : "Total unduplicated population of census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s) divided by total population of the area. X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Urban Population Served at Level of Service",
			"definition" : "Total unduplicated population of urban census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s). X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Rural Population Served at Level of Service",
			"definition" : "Total unduplicated population of rural census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s). X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Percent of Population Unserved",
			"definition" : "100 minus percent of population served.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Service Stops",
			"definition" : "Total number of times the stops within the geographic area are served on the given date(s). Service stops for a route is calculated as its stop count multiplied by the number of visits (i.e., trips or runs). The number reported is cumulative over the selected dates."
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Urban Population Served By Service",
			"definition" : "Summation of Population Served by Service over all urban census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop in the given geographic area. Population served by service for a census block is calculated as the population of that block multiplied by the times that block is served on the selected date(s) by all agencies. The number reported is cumulative over the selected dates."
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Rural Population Served By Service",
			"definition" : "Summation of Population Served by Service over all rural census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop in the given geographic area. Population served by service for a census block is calculated as the population of that block multiplied by the times that block is served on the selected date(s) by all agencies. The number reported is cumulative over the selected dates."
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Employment Served (RAC)",
			"definition" : "Total number of unduplicated people employed residing in census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops in the geographic area. Each census block is counted once (unduplicated). This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Percent of Employment Served (RAC)",
			"definition" : "Employment Served (RAC) divided by the number of people residing in the given geographic area.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Percent of Employment Served at Level of Service (RAC)",
			"definition" : "Total unduplicated people employed residing in census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s) divided by total number of employed people working in the area. X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Employment Served at Level of Service (RAC)",
			"definition" : "Total unduplicated people employed residing in census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s). X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Percent of Employment Unserved (RAC)",
			"definition" : "100 minus percent of Employment Served (RAC).",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Employment Served By Service (RAC)",
			"definition" : "Summation of Employment Served by Service over all census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop in the geographic area. Employment Served by Service for a census block is calculated as the number of employed people residing in that block multiplied by the times that block is served on the selected date(s). The number reported is cumulative over the selected dates."
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Employees Served (WAC)",
			"definition" : "Total number of unduplicated people employed working in census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops in the geographic area. Each census block is counted once (unduplicated). This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Percent of Employees Served (WAC)",
			"definition" : "Employees Served (WAC) divided by the number of people working in the given geographic area."
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Percent of Employees Served at Level of Service (WAC)",
			"definition" : "Total unduplicated people employed working in census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s) divided by total number of employed people working in the area. X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Employees Served at Level of Service (WAC)",
			"definition" : "Total unduplicated people employed working in census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s). X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Percent of Employee Unserved (WAC)",
			"definition" : "100 minus percent of employees served (WAC).",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Employees Served By Service (WAC)",
			"definition" : "Summation of Employees Served by Service over all census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop in the geographic area. Employment Served by Service for a census block is calculated as the number of employed people working in that block multiplied by the times that block is served on the selected date(s). The number reported is cumulative over the selected dates."
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Service Days",
			"definition" : "Set of days (from the selected days) in which at least one trip within the given geographic area is served.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Connected Communities",
			"definition" : "List of geographic areas of the same type that are connected to the area of interest through routes that are served on the selected date(s).",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Hours of Service",
			"definition" : "Difference between the earliest arrival time and latest departure time  of all transit stops within the given geographic area.",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Minimum Fare",
			"definition" : "If available, this field points to the fare minimum for the given geographic area during the selected date(s).",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Average Fare",
			"definition" : "If available, this field points to the fare average for the given geographic area during the selected date(s).",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Median Fare",
			"definition" : "If available, this field points to the fare median for the given geographic area during the selected date(s).",
		},
		{
			"report" : "ODOT Transit Region Extended Report",
			"metric" : "Maximum Fare",
			"definition" : "If available, this field points to the fare maximum for the given geographic area during the selected date(s).",
		},
		// 9.6 Stops Report
		{
			"report" : "Stops Summary Report",
			"metric" : "Agency ID",
			"definition" : "Agency ID reported in the transit agency GTFS feed.",
		},
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
			"definition" : "Total unduplicated population of census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of the stop. This metric is date-independent, i.e., the stop may or may not be served on the selected date(s)."
		},
		{
			"report" : "Stops Summary Report",
			"metric" : "Urban Population Served",
			"definition" : "Total population of urban census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of the stop. This metric is date-independent, i.e., the stop may or may not be served on the selected date(s)."
		},
		{
			"report" : "Stops Summary Report",
			"metric" : "Rural Population Served",
			"definition" : "Total population of rural census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of the stop. This metric is date-independent, i.e., the stop may or may not be served on the selected date(s)."
		},
		{
			"report" : "Stops Summary Report",
			"metric" : "Employment Served (RAC)",
			"definition" : "Total number of employed people residing in census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of the stop. This metric is date-independent, i.e., the stop may or may not be served on the selected date(s)."
		},
		{
			"report" : "Stops Summary Report",
			"metric" : "Employees Served (WAC)",
			"definition" : "Total number of employed people working in census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of the stop. This metric is date-independent, i.e., the stop may or may not be served on the selected date(s)."
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
			"metric" : "Urban Population Served",
			"definition" : "Total unduplicated population of urban census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of the route stops. This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Routes Summary Report",
			"metric" : "Rural Population Served",
			"definition" : "Total unduplicated population of rural census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of the route stops. This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Routes Summary Report",
			"metric" : "Service Stops",
			"definition" : "Number of stops scheduled on all trips in a route. The service stops for a route is calculated as its stop count multiplied by the number of visits per stop.",
		},
		{
			"report" : "Routes Summary Report",
			"metric" : "Urban Population Served By Service",
			"definition" : "Summation of Population Served by Service over all urban census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of the route stops. Population served by service for a census block is calculated as the population of that block multiplied by the times that block is served on the selected date(s) by all agencies. The number reported is cumulative over the selected dates."
		},
		{
			"report" : "Routes Summary Report",
			"metric" : "Rural Population Served By Service",
			"definition" : "Summation of Population Served by Service over all rural census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of the route stops. Population served by service for a census block is calculated as the population of that block multiplied by the times that block is served on the selected date(s) by all agencies. The number reported is cumulative over the selected dates."
		},
		{
			"report" : "Routes Summary Report",
			"metric" : "Service Miles",
			"definition" : "Total miles driven by a transit agency over all round trips of a route running on the selected date(s). Service miles may be calculated for a specific date or a set of dates specified using the calendar.",
		},
		{
			"report" : "Routes Summary Report",
			"metric" : "Service Hours",
			"definition" : "Total hours transit agency spends serving all round trips of the routes within the given geographic area. The service hours for a trip are calculated as the difference between the arrival time to the first stop of the trip and the departing time from the last stop of the trip. Service hours may be calculated for a specific date or a set of dates specified using the calendar. The reported number is cumulative over the selected dates.",
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
			"definition" : "Latitude of the transit hub centroid. This is calculated as the average of latitudes of stops in the cluster.",
		},
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "Cluster Centroid Longitude",
			"definition" : "Longitude of the transit hub centroid. This is calculated as the average of longitudes of stops in the cluster.",
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
			"definition" : "Total number of times the stops in the cluster are served on the given date(s). Visits for a stop is calculated as summation of number of times a stop is served on selected date(s), i.e., summation of number of trips/runs over all routes where the stop belongs to. The number reported is cumulative over the selected dates."
		},
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "Park and Ride Lots Count",
			"definition" : "Total number of park and ride lots within X distance of the cluster centroid.",
		},
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "Counties Count",
			"definition" : "Total number of counties in which the cluster has at least one stop.",
		},
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "Census Places Count",
			"definition" : "Total number of census places in which the cluster has at least one stop.",
		},
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "Population Served",
			"definition" : "Total unduplicated population of census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops in the cluster. This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "Employment Served (RAC)",
			"definition" : "Total number of unduplicated people employed residing in census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops in the cluster. Each census block is counted once (unduplicated). This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "Employees Served (WAC)",
			"definition" : "Total number of unduplicated people employed working in census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops in the cluster. Each census block is counted once (unduplicated). This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "Urban Population",
			"definition" : "Sum of the population of urban areas (areas with population over 2,500) that stops in the cluster are located in.",
		},
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "Transit Agencies",
			"definition" : "List of transit agencies that serve stops in the cluster.",
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
			"definition" : "Detailed list of park and ride lots within the X-mile radius of the cluster centroid.",
		},
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "Census Places",
			"definition" : "Census places in which the cluster has at least one stop.",
		},
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "Counties",
			"definition" : "Counties in which the cluster has at least one stop.",
		},
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "Urban Areas",
			"definition" : "Areas in which the cluster has at least one stop.",
		},
		{
			"report" : "Transit Hubs Summary Report",
			"metric" : "ODOT Transit Regions",
			"definition" : "ODOT transit regions in which the cluster has at least one stop.",
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
			"definition" : "Latitude of the transit hub centroid. This is calculated as the average of latitudes of stops in the cluster.",
		},
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "Cluster Centroid Longitude",
			"definition" : "Longitude of the transit hub centroid. This is calculated as the average of longitudes of stops in the cluster.",
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
			"definition" : "Total number of times the stops in the cluster are served on the given date(s). Visits for a stop are calculated as the summation of the number of times a stop is served on selected date(s), i.e., summation of number of trips/runs over all routes where the stop belongs to. The number reported is cumulative over the selected dates."
		},
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "Park and Ride Lots Count",
			"definition" : "Total number of park and ride lots within X distance of the cluster centroid.",
		},
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "Counties Count",
			"definition" : "Total number of counties in which the cluster has at least one stop.",
		},
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "Census Places Count",
			"definition" : "Total number of census places in which the cluster has at least one stop.",
		},
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "Population Served",
			"definition" : "Total unduplicated population of census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops in the cluster. This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "Employment Served (RAC)",
			"definition" : "Total number of unduplicated people employed residing in census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops in the cluster. Each census block is counted once (unduplicated). This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "Employees Served (WAC)",
			"definition" : "Total number of unduplicated people employed working in census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops in the cluster. Each census block is counted once (unduplicated). This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "Urban Population",
			"definition" : "Sum of the population of urban areas (areas with population over 2,500) that stops in the cluster are located in.",
		},
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "Transit Agencies",
			"definition" : "List of transit agencies that serve stops in the cluster.",
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
			"definition" : "Detailed list of park and ride lots within the X-mile radius of the cluster centroid.",
		},
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "Census Places",
			"definition" : "Census places in which the cluster has at least one stop.",
		},
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "Counties",
			"definition" : "Counties in which the cluster has at least one stop.",
		},
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "Urban Areas",
			"definition" : "Areas in which the cluster has at least one stop.",
		},
		{
			"report" : "Key Transit Hubs Report",
			"metric" : "ODOT Transit Regions",
			"definition" : "ODOT transit regions in which the cluster has at least one stop.",
		},

		// 12. Timing Connection Report
		{
			"report" : "Timing Connection Report",
			"metric" : "#",
			"definition" : "Randomly assigned row number.",
		},
		{
			"report" : "Timing Connection Report",
			"metric" : "Stop1 ID",
			"definition" : "ID of the stop belonging to the selected route/trip. This stop is a connection to some other route.",
		},
		{
			"report" : "Timing Connection Report",
			"metric" : "Stop1 Name",
			"definition" : "Name of the stop belonging to the selected route/trip. This stop is a connection to some other route.",
		},
		{
			"report" : "Timing Connection Report",
			"metric" : "Stop2 ID",
			"definition" : "ID of the stop that is located within an X-mile radius of Stop1 and is served within the selected Time Window on the selected Date.",
		},
		{
			"report" : "Timing Connection Report",
			"metric" : "Stop2 Name",
			"definition" : "Name of the stop that is located within an X-mile radius of Stop1 and is served within the selected Time Window on the selected Date.",
		},
		{
			"report" : "Timing Connection Report",
			"metric" : "Stop2 Agency",
			"definition" : "The agency that serves Stop2.",
		},
		{
			"report" : "Timing Connection Report",
			"metric" : "Stop2 Route ID",
			"definition" : "ID of the route to which Stop2 belongs.",
		},
		{
			"report" : "Timing Connection Report",
			"metric" : "Stop2 Route Name",
			"definition" : "Name of the route to which Stop2 belongs.",
		},
		{
			"report" : "Timing Connection Report",
			"metric" : "Stop1 Arrival",
			"definition" : "Time of arrival at Stop1.",
		},
		{
			"report" : "Timing Connection Report",
			"metric" : "Stop2 Departure",
			"definition" : "Departure from Stop2.",
		},
		{
			"report" : "Timing Connection Report",
			"metric" : "Time Difference",
			"definition" : "The difference between arriving at Stop1 and departing Stop2.",
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
			"metric" : "Number of Connected Agencies",
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
			"definition" : "Smallest distance in ft. between the transit stops of the specified transit agency with the current transit agency",
		},
		{
			"report" : "Connected Agencies Extended Report",
			"metric" : "Max Connection distance (ft.)",
			"definition" : "Largest distance in ft. between the transit stops of the specified transit agency with the current transit agency",
		},
		{
			"report" : "Connected Agencies Extended Report",
			"metric" : "Average Connection distance (ft.)",
			"definition" : "Average distance in ft. between the transit stops of the specified transit agency with the current transit agency",
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

		// 18. Census Tract Summary Report
		{
			"report" : "Tracts Summary Report",
			"metric" : "Geo ID",
			"definition" : "Identification number associated with the geographic area."
		},
		{
			"report" : "Tracts Summary Report",
			"metric" : "Name",
			"definition" : "Name associated with the geographic area."
		},
		{
			"report" : "Tracts Summary Report",
			"metric" : "Population",
			"definition" : "Total population of the geographic area."
		},
		{
			"report" : "Tracts Summary Report",
			"metric" : "Employment (RAC)",
			"definition" : "Total number of people employed residing in the geographic area. Metric is calculated using Residence Area Characteristic (RAC) data."
		},
		{
			"report" : "Tracts Summary Report",
			"metric" : "Employees (WAC)",
			"definition" : "Total number of people employed working in the geographic area. Metric is calculated using Working Area Characteristic (WAC) data."
		},
		{
			"report" : "Tracts Summary Report",
			"metric" : "Land Area",
			"definition" : "Total land area of the geographic area in square miles."
		},
		{
			"report" : "Tracts Summary Report",
			"metric" : "Water Area",
			"definition" : "Total water area of the geographic area in square miles."
		},
		{
			"report" : "Tracts Summary Report",
			"metric" : "Total Agencies",
			"definition" : "Total number of transit agencies operating in the given geographic area. An agency with at least one stop in the geographic area is counted."
		},
		{
			"report" : "Tracts Summary Report",
			"metric" : "Total Routes",
			"definition" : "Total number of routes serving stops in the given geographic area."
		},
		{
			"report" : "Tracts Summary Report",
			"metric" : "Total Stops",
			"definition" : "Total number of stops within the given geographic area."
		},
		{
			"report" : "Tracts Summary Report",
			"metric" : "Urban Areas",
			"definition" : "Total number of Urban Areas within the geographic area."
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
			"metric" : "Route Miles",
			"definition" : "Summation of the lengths (in miles) of the routes within the given geographic area. The length of the longest trip of a route that is running on the selected date(s) is considered as the route length.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Stops Per Square Mile",
			"definition" : "Stop count in the given geographic area divided by the square miles of the geographic area. This metric is date-independent.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Stops Per Service Mile",
			"definition" : "Stop count in the given geographic area divided by Service Miles.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Service Hours",
			"definition" : "Total hours a transit agency spends serving all round trips of routes within the given geographic area. The service hours for a trip are calculated as the difference between the arrival time to the first stop of the trip and the departing time from the last stop of the trip. Service hours may be calculated for a specific date or a set of dates specified using the calendar. The reported number is cumulative over the selected dates.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Service Miles",
			"definition" : "Total miles driven over all round trips of routes running on the selected date(s) within the given geographic area. Service miles may be calculated for a specific date or a set of dates specified using the calendar. The reported number is cumulative over the selected dates."
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Service Miles Per Square Mile",
			"definition" : "Service Miles divided by the square miles of the geographic area.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Miles of Service Per Capita",
			"definition" : "Service Miles divided by the population of the geographic area.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Urban Population Served",
			"definition" : "Total unduplicated population of urban census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops within the given geographic area. This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Rural Population Served",
			"definition" : "Total unduplicated population of rural census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops within the given geographic area. This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Percent of Population Served",
			"definition" : "Total unduplicated population of census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops within the given geographic area divided by total population of the area. This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Percent of Population Served at Level of Service",
			"definition" : "Total unduplicated population of census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s) divided by total population of the area. X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Urban Population Served at Level of Service",
			"definition" : "Total unduplicated population of urban census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s). X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Rural Population Served at Level of Service",
			"definition" : "Total unduplicated population of rural census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s). X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Percent of Population Unserved",
			"definition" : "100 minus percent of population served.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Service Stops",
			"definition" : "Total number of times the stops within the geographic area are served on the given date(s). Service stops for a route is calculated as its stop count multiplied by the number of visits (i.e., trips or runs). The number reported is cumulative over the selected dates."
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Urban Population Served By Service",
			"definition" : "Summation of Population Served by Service over all urban census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop in the given geographic area. Population served by service for a census block is calculated as the population of that block multiplied by the times that block is served on the selected date(s) by all agencies. The number reported is cumulative over the selected dates."
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Rural Population Served By Service",
			"definition" : "Summation of Population Served by Service over all rural census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop in the given geographic area. Population served by service for a census block is calculated as the population of that block multiplied by the times that block is served on the selected date(s) by all agencies. The number reported is cumulative over the selected dates."
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Employment Served (RAC)",
			"definition" : "Total number of unduplicated people employed residing in census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops in the geographic area. Each census block is counted once (unduplicated). This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Percent of Employment Served (RAC)",
			"definition" : "Employment Served (RAC) divided by the number of people residing in the given geographic area.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Percent of Employment Served at Level of Service (RAC)",
			"definition" : "Total unduplicated people employed residing in census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s) divided by total number of employed people working in the area. X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Employment Served at Level of Service (RAC)",
			"definition" : "Total unduplicated people employed residing in census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s). X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Percent of Employment Unserved (RAC)",
			"definition" : "100 minus percent of Employment Served (RAC).",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Employment Served By Service (RAC)",
			"definition" : "Summation of Employment Served by Service over all census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop in the geographic area. Employment Served by Service for a census block is calculated as the number of employed people residing in that block multiplied by the times that block is served on the selected date(s). The number reported is cumulative over the selected dates."
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Employees Served (WAC)",
			"definition" : "Total number of unduplicated people employed working in census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of all stops in the geographic area. Each census block is counted once (unduplicated). This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Percent of Employees Served (WAC)",
			"definition" : "Employees Served (WAC) divided by the number of people working in the given geographic area."
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Percent of Employees Served at Level of Service (WAC)",
			"definition" : "Total unduplicated people employed working in census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s) divided by total number of employed people working in the area. X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Employees Served at Level of Service (WAC)",
			"definition" : "Total unduplicated people employed working in census blocks whose centroids are located within an X-mile radius of any stop that is within the geographical area and served at least N-times on the selected date(s). X is the population search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Percent of Employee Unserved (WAC)",
			"definition" : "100 minus percent of employees served (WAC).",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Employees Served By Service (WAC)",
			"definition" : "Summation of Employees Served by Service over all census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop in the geographic area. Employment Served by Service for a census block is calculated as the number of employed people working in that block multiplied by the times that block is served on the selected date(s). The number reported is cumulative over the selected dates."
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Service Days",
			"definition" : "Set of days (from the selected days) in which at least one trip within the given geographic area is served.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Connected Communities",
			"definition" : "List of geographic areas of the same type that are connected to the area of interest through routes that are served on the selected date(s).",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Hours of Service",
			"definition" : "Difference between the earliest arrival time and latest departure time of all transit stops within the given geographic area.",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Minimum Fare",
			"definition" : "If available, this field points to the minimum fare for the given geographic area during the selected date(s).",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Average Fare",
			"definition" : "If available, this field points to the average fare for the given geographic area during the selected date(s).",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Median Fare",
			"definition" : "If available, this field points to the median fare for the given geographic area during the selected date(s).",
		},
		{
			"report" : "Tract Extended Report",
			"metric" : "Maximum Fare",
			"definition" : "If available, this field points to the maximum fare for the given geographic area during the selected date(s).",
		},
		
		// 19. Employment Report
		{
			"report" : "Employment Report",
			"metric" : "Employment (RAC)",
			"definition" : "Total number of employed people residing in the geographic area."
		},
		{
			"report" : "Employment Report",
			"metric" : "Employees (WAC)",
			"definition" : "Total number of employed people working in the geographic area."
		},
		{
			"report" : "Employment Report",
			"metric" : "Employment Served (RAC)",
			"definition" : "Total number of people employed residing in the geographic area and served by the agency on selected date(s)."
		},
		{
			"report" : "Employment Report",
			"metric" : "Employees served (WAC)",
			"definition" : "Total number of people employed working in the geographic area and served by the agency on selected date(s)."
		},
		{
			"report" : "Employment Report",
			"metric" : "[Category] (RAC)",
			"definition" : "Total number of people employed, belonging to the category, residing in the geographic area."
		},
		{
			"report" : "Employment Report",
			"metric" : "[Category] (WAC)",
			"definition" : "Total number of people employed, belonging to the category, working in the geographic area."
		},
		{
			"report" : "Employment Report",
			"metric" : "[Category] - S (RAC)",
			"definition" : "[Category] Served: Total number of unduplicated people employed, belonging to the category, residing in census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop of the agency/geographic area. Each census block is counted once (unduplicated). This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Employment Report",
			"metric" : "[Category] - SS (RAC)",
			"definition" : "[Category] Served by Service: Summation of [Category] Served by Service over all census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop of the agency/geographic area. [Category] Served by Service for a census block is calculated as the number of employed people, belonging to the category, residing in that block multiplied by the times that block is served on the selected date(s). The number reported is cumulative over the selected dates."
		},
		{
			"report" : "Employment Report",
			"metric" : "[Category] - SLOS (RAC)",
			"definition" : "[Category] Served at Level of Service: Total unduplicated people employed, belonging to the category, residing in census blocks whose centroids are located within an X-mile radius of any stop of the agency/geographic area and served at least N-times on the selected date(s). X is the employment search radius and N is the minimum level of service set by the user."
		},
		{
			"report" : "Employment Report",
			"metric" : "[Category] - S (WAC)",
			"definition" : "[Category] Served: Total number of unduplicated people employed, belonging to the category, working in census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop of the agency/geographic area. Each census block is counted once (unduplicated). This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Employment Report",
			"metric" : "[Category] - SS (WAC)",
			"definition" : "[Category] Served by Service: Summation of [Category] Served by Service over all census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop of the agency/geographic area. [Category] Served by Service for a block is calculated as the number of employed people, belonging to the category, working in that block multiplied by the times that block is served on the selected date(s). The number reported is cumulative over the selected dates."
		},
		{
			"report" : "Employment Report",
			"metric" : "[Category] - SLOS (WAC)",
			"definition" : "[Category] Served at Level of Service: Total unduplicated people employed, belonging to the category, working in census blocks whose centroids are located within an X-mile radius of any stop of the agency/geographic area and served at least N-times on the selected date(s). X is the employment search radius and N is the minimum level of service set by the user."
		},
		
		// 17. Title VI
		{
			"report" : "Title VI Report",
			"metric" : "[Category]",
			"definition" : "Total number of individuals belonging to the category and living in the geographic area."
		},
		{
			"report" : "Title VI Report",
			"metric" : "[Category] - S",
			"definition" : "Number of Individuals Served: Total number of unduplicated individuals, belonging to the category, in census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop of the agency/geographic area. Each census block is counted once (unduplicated). This metric is date-independent, i.e., the stops may or may not be served on the selected date(s)."
		},
		{
			"report" : "Title VI Report",
			"metric" : "[Category] - SS",
			"definition" : "Number of Individuals Served by Service: Summation of [Category] Served by Service over all census blocks whose centroids are located within an X-mile radius (i.e., stop distance) of any stop of the agency/geographic area. [Category] Served by Service for a census block is calculated as the number of individuals, belonging to the category, in that block multiplied by the times that block is served on the selected date(s). The number reported is cumulative over the selected dates."
		},
		{
			"report" : "Title VI Report",
			"metric" : "[Category] - SLOS",
			"definition" : "Number of Individuals Served at Level of Service: Total unduplicated individuals, belonging to the category, in census blocks whose centroids are located within an X-mile radius of any stop of the agency/geographic area and served at least N-times on the selected date(s). X is the population search radius and N is the minimum level of service set by the user."
		},

];
