var children;
var navigationIdMap = {
	"Statewide Summary Report" : "stateS",
	"Statewide Extended Report" : "stateX",
    "Aggregated Urban Areas Summary Report" : "AggUAS",
    "Aggregated Urban Areas Extended Report" : "AggUAX",
	"Transit Agencies Summary Report" : "agencyS",
	"Transit Agency Extended Report" : "agencyX",
	"Counties Summary Report" : "countyS",
	"County Extended Report" : "countyX",
	"Tracts Summary Report" : "tractS",
	"Tract Extended Report" : "tractX",
	"Congressional Districts Summary Report" : "congS",
	"Congressional District Extended Report" : "congX",
	"ODOT Transit Regions Summary Report" : "regionS",
	"ODOT Transit Region Extended Report" : "regionX",
	"Census Places Summary Report" : "placeS",
	"Census Place Extended Report" : "placeX",
	"Urban Areas Summary Report" : "urbanS",
	"Urban Area Extended Report" : "urbanX",
	"Routes Summary Report" : "route",
	"Stops Summary Report" : "stop"
};
var navigationMap = { 
	'core' : {
		'data' : [
		    //statewide statewide    
 	       	{ "id" : "stateS", "parent" : "#", "text" : "Statewide Summary Report", "data" : {"styleClass" : "test"}},
	 	    	//nodes under statewide summary
		       	{ "id" : "stateS-stateX", "parent" : "stateS", "text" : "Statewide Extended Report"},
		       	{ "id" : "stateS-agencyS", "parent" : "stateS", "text" : "Transit Agencies Summary Report" },
		       	
		       	//nodes under agency summary
		       		{ "id" : "stateS-agencyS-agencyX", "parent" : "stateS-agencyS", "text" : "Transit Agency Extended Report" },
		       		{ "id" : "stateS-agencyS-route", "parent" : "stateS-agencyS", "text" : "Routes Summary Report" },
		       	  		//nodes under agency->route summary
			       		{ "id" : "stateS-agencyS-route-stop", "parent" : "stateS-agencyS-route", "text" : "Stops Summary Report" },
			       		{ "id" : "stateS-agencyS-route-schedule", "parent" : "stateS-agencyS-route", "text" : "Route Schedule Report" },
		       		{ "id" : "stateS-agencyS-stop", "parent" : "stateS-agencyS", "text" : "Stops Summary Report" },
		       		{ "id" : "stateS-agencyS-urbanS", "parent" : "stateS-agencyS", "text" : "Urban Areas Summary Report" },
		       			//nodes under agency->urban summary
				       	{ "id" : "stateS-agencyS-urbanS-urbanX", "parent" : "stateS-agencyS-urbanS", "text" : "Urban Area Extended Report" },
				       	{ "id" : "stateS-agencyS-urbanS-route", "parent" : "stateS-agencyS-urbanS", "text" : "Routes Summary Report" },
					       	//nodes under agency->urban->route summary
				       		{ "id" : "stateS-agencyS-urbanS-route-stop", "parent" : "stateS-agencyS-urbanS-route", "text" : "Stops Summary Report" },
				       		{ "id" : "stateS-agencyS-urbanS-route-schedule", "parent" : "stateS-agencyS-urbanS-route", "text" : "Route Schedule Report" },
				       	{ "id" : "stateS-agencyS-urbanS-stop", "parent" : "stateS-agencyS-urbanS", "text" : "Stops Summary Report" },
		       		{ "id" : "stateS-agencyS-countyS", "parent" : "stateS-agencyS", "text" : "Counties Summary Report" },
			       		//nodes under agency->county summary
					   	{ "id" : "stateS-agencyS-countyS-countyX", "parent" : "stateS-agencyS-countyS", "text" : "County Extended Report" },
					   	{ "id" : "stateS-agencyS-countyS-route", "parent" : "stateS-agencyS-countyS", "text" : "Routes Summary Report" },
						   	//nodes under agency->county->route summary
				       		{ "id" : "stateS-agencyS-countyS-route-stop", "parent" : "stateS-agencyS-countyS-route", "text" : "Stops Summary Report" },
				       		{ "id" : "stateS-agencyS-countyS-route-schedule", "parent" : "stateS-agencyS-countyS-route", "text" : "Route Schedule Report" },
					   	{ "id" : "stateS-agencyS-countyS-stop", "parent" : "stateS-agencyS-countyS", "text" : "Stops Summary Report" },
					   	{ "id" : "stateS-agencyS-countyS-urbanS", "parent" : "stateS-agencyS-countyS", "text" : "Urban Areas Summary Report" },
					       //nodes under agency->county->urban summary
					       { "id" : "stateS-agencyS-countyS-urbanS-urbanX", "parent" : "stateS-agencyS-countyS-urbanS", "text" : "Urban Area Extended Report" },
					       { "id" : "stateS-agencyS-countyS-urbanS-route", "parent" : "stateS-agencyS-countyS-urbanS", "text" : "Routes Summary Report" },
						       	//nodes under agency->county->urban->route summary
					       		{ "id" : "stateS-agencyS-countyS-urbanS-route-stop", "parent" : "stateS-agencyS-countyS-urbanS-route", "text" : "Stops Summary Report" },
					       		{ "id" : "stateS-agencyS-countyS-urbanS-route-schedule", "parent" : "stateS-agencyS-countyS-urbanS-route", "text" : "Route Schedule Report" },
					       { "id" : "stateS-agencyS-countyS-urbanS-stop", "parent" : "stateS-agencyS-countyS-urbanS", "text" : "Stops Summary Report" },
					    { "id" : "stateS-agencyS-countyS-tractS", "parent" : "stateS-agencyS-countyS", "text" : "Tracts Summary Report" },
					        //nodes under agency->county->tract summary
					        { "id" : "stateS-agencyS-countyS-tractS-tractX", "parent" : "stateS-agencyS-countyS-tractS", "text" : "Tract Extended Report" },
					        { "id" : "stateS-agencyS-countyS-tractS-route", "parent" : "stateS-agencyS-countyS-tractS", "text" : "Routes Summary Report" },
						        //nodes under agency->county->tract->route summary
					       		{ "id" : "stateS-agencyS-countyS-tractS-route-stop", "parent" : "stateS-agencyS-countyS-tractS-route", "text" : "Stops Summary Report" },
					       		{ "id" : "stateS-agencyS-countyS-tractS-route-schedule", "parent" : "stateS-agencyS-countyS-tractS-route", "text" : "Route Schedule Report" },
					        { "id" : "stateS-agencyS-countyS-tractS-stop", "parent" : "stateS-agencyS-countyS-tractS", "text" : "Stops Summary Report" },
					        { "id" : "stateS-agencyS-countyS-tractS-urbanS", "parent" : "stateS-agencyS-countyS-tractS", "text" : "Urban Areas Summary Report" },
						       //nodes under agency->county->tract->urban summary
						       { "id" : "stateS-agencyS-countyS-tractS-urbanS-urbanX", "parent" : "stateS-agencyS-countyS-tractS-urbanS", "text" : "Urban Area Extended Report" },
						       { "id" : "stateS-agencyS-countyS-tractS-urbanS-route", "parent" : "stateS-agencyS-countyS-tractS-urbanS", "text" : "Routes Summary Report" },
							       //nodes under agency->county->tract->urban->route summary
						       		{ "id" : "stateS-agencyS-countyS-tractS-urbanS-route-stop", "parent" : "stateS-agencyS-countyS-tractS-urbanS-route", "text" : "Stops Summary Report" },
						       		{ "id" : "stateS-agencyS-countyS-tractS-urbanS-route-schedule", "parent" : "stateS-agencyS-countyS-tractS-urbanS-route", "text" : "Route Schedule Report" },
						       { "id" : "stateS-agencyS-countyS-tractS-urbanS-stop", "parent" : "stateS-agencyS-countyS-tractS-urbanS", "text" : "Stops Summary Report" },
		       		{ "id" : "stateS-agencyS-congS", "parent" : "stateS-agencyS", "text" : "Congretional Districts Summary Report" },
			       		//nodes under agency->cong summary
					   	{ "id" : "stateS-agencyS-congS-countyX", "parent" : "stateS-agencyS-congS", "text" : "County Extended Report" },
					   	{ "id" : "stateS-agencyS-congS-route", "parent" : "stateS-agencyS-congS", "text" : "Routes Summary Report" },
						   	//nodes under agency->cong->route summary
						   	{ "id" : "stateS-agencyS-congS-route-stop", "parent" : "stateS-agencyS-congS-route", "text" : "Stops Summary Report" },
						   	{ "id" : "stateS-agencyS-congS-route-schedule", "parent" : "stateS-agencyS-congS-route", "text" : "Route Schedule Report" },
					   	{ "id" : "stateS-agencyS-congS-stop", "parent" : "stateS-agencyS-congS", "text" : "Stops Summary Report" },
					   	{ "id" : "stateS-agencyS-congS-urbanS", "parent" : "stateS-agencyS-congS", "text" : "Urban Areas Summary Report" },
					       //nodes under agency->cong->urban summary
					       { "id" : "stateS-agencyS-congS-urbanS-urbanX", "parent" : "stateS-agencyS-congS-urbanS", "text" : "Urban Area Extended Report" },
					       { "id" : "stateS-agencyS-congS-urbanS-route", "parent" : "stateS-agencyS-congS-urbanS", "text" : "Routes Summary Report" },
						       //nodes under agency->cong->urban->route summary
						       { "id" : "stateS-agencyS-congS-urbanS-route-stop", "parent" : "stateS-agencyS-congS-urbanS-route", "text" : "Stops Summary Report" },
						       { "id" : "stateS-agencyS-congS-urbanS-route-schedule", "parent" : "stateS-agencyS-congS-urbanS-route", "text" : "Route Schedule Report" },
					       { "id" : "stateS-agencyS-congS-urbanS-stop", "parent" : "stateS-agencyS-congS-urbanS", "text" : "Stops Summary Report" },
		       		{ "id" : "stateS-agencyS-regionS", "parent" : "stateS-agencyS", "text" : "ODOT Transit Regions Summary Report" },
			       		//nodes under agency->region summary
					   	{ "id" : "stateS-agencyS-regionS-countyX", "parent" : "stateS-agencyS-regionS", "text" : "County Extended Report" },
					   	{ "id" : "stateS-agencyS-regionS-route", "parent" : "stateS-agencyS-regionS", "text" : "Routes Summary Report" },
						   	//nodes under agency->region->route summary
						   	{ "id" : "stateS-agencyS-regionS-route-stop", "parent" : "stateS-agencyS-regionS-route", "text" : "Stops Summary Report" },
						   	{ "id" : "stateS-agencyS-regionS-route-schedule", "parent" : "stateS-agencyS-regionS-route", "text" : "Route Schedule Report" },
					   	{ "id" : "stateS-agencyS-regionS-stop", "parent" : "stateS-agencyS-regionS", "text" : "Stops Summary Report" },
					   	{ "id" : "stateS-agencyS-regionS-urbanS", "parent" : "stateS-agencyS-regionS", "text" : "Urban Areas Summary Report" },
					       //nodes under agency->region->urban summary
					       { "id" : "stateS-agencyS-regionS-urbanS-urbanX", "parent" : "stateS-agencyS-regionS-urbanS", "text" : "Urban Area Extended Report" },
					       { "id" : "stateS-agencyS-regionS-urbanS-route", "parent" : "stateS-agencyS-regionS-urbanS", "text" : "Routes Summary Report" },
						       //nodes under agency->region->urban->route summary
						       { "id" : "stateS-agencyS-regionS-urbanS-route-stop", "parent" : "stateS-agencyS-regionS-urbanS-route", "text" : "Stops Summary Report" },
						       { "id" : "stateS-agencyS-regionS-urbanS-route-schedule", "parent" : "stateS-agencyS-regionS-urbanS-route", "text" : "Route Schedule Report" },
					       { "id" : "stateS-agencyS-regionS-urbanS-stop", "parent" : "stateS-agencyS-regionS-urbanS", "text" : "Stops Summary Report" },
		       		{ "id" : "stateS-agencyS-placeS", "parent" : "stateS-agencyS", "text" : "Census Places Summary Report" },
			       		//nodes under agency->place summary
					   	{ "id" : "stateS-agencyS-placeS-countyX", "parent" : "stateS-agencyS-placeS", "text" : "County Extended Report" },
					   	{ "id" : "stateS-agencyS-placeS-route", "parent" : "stateS-agencyS-placeS", "text" : "Routes Summary Report" },
						   	//nodes under agency->place->route summary
						   	{ "id" : "stateS-agencyS-placeS-route-stop", "parent" : "stateS-agencyS-placeS-route", "text" : "Stops Summary Report" },
						   	{ "id" : "stateS-agencyS-placeS-route-schedule", "parent" : "stateS-agencyS-placeS-route", "text" : "Route Schedule Report" },
					   	{ "id" : "stateS-agencyS-placeS-stop", "parent" : "stateS-agencyS-placeS", "text" : "Stops Summary Report" },
					   	{ "id" : "stateS-agencyS-placeS-urbanS", "parent" : "stateS-agencyS-placeS", "text" : "Urban Areas Summary Report" },
					       //nodes under agency->place->urban summary
					       { "id" : "stateS-agencyS-placeS-urbanS-urbanX", "parent" : "stateS-agencyS-placeS-urbanS", "text" : "Urban Area Extended Report" },
					       { "id" : "stateS-agencyS-placeS-urbanS-route", "parent" : "stateS-agencyS-placeS-urbanS", "text" : "Routes Summary Report" },
						       //nodes under agency->place->urban->route summary
						       { "id" : "stateS-agencyS-placeS-urbanS-route-stop", "parent" : "stateS-agencyS-placeS-urbanS-route", "text" : "Stops Summary Report" },
						       { "id" : "stateS-agencyS-placeS-urbanS-route-schedule", "parent" : "stateS-agencyS-placeS-urbanS-route", "text" : "Route Schedule Report" },
					       { "id" : "stateS-agencyS-placeS-urbanS-stop", "parent" : "stateS-agencyS-placeS-urbanS", "text" : "Stops Summary Report" },
		       	{ "id" : "stateS-urbanS", "parent" : "stateS", "text" : "Urban Areas Summary Report" },
			       //nodes under urban summary
			       { "id" : "stateS-urbanS-urbanX", "parent" : "stateS-urbanS", "text" : "Urban Area Extended Report" },
			       { "id" : "stateS-urbanS-route", "parent" : "stateS-urbanS", "text" : "Routes Summary Report" },
				       //nodes under urban->route summary
				       { "id" : "stateS-urbanS-route-stop", "parent" : "stateS-urbanS-route", "text" : "Stops Summary Report" },
				       { "id" : "stateS-urbanS-route-schedule", "parent" : "stateS-urbanS-route", "text" : "Route Schedule Report" },
			       { "id" : "stateS-urbanS-stop", "parent" : "stateS-urbanS", "text" : "Stops Summary Report" },
			       { "id" : "stateS-urbanS-agencyS", "parent" : "stateS-urbanS", "text" : "Transit Agencies Summary Report" },
				       //nodes under urban->agency summary
				       { "id" : "stateS-urbanS-agencyS-agencyX", "parent" : "stateS-urbanS-agencyS", "text" : "Transit Agency Extended Report" },
				       { "id" : "stateS-urbanS-agencyS-route", "parent" : "stateS-urbanS-agencyS", "text" : "Routes Summary Report" },
					       //nodes under urban->agency->route summary
					       { "id" : "stateS-urbanS-agencyS-route-stop", "parent" : "stateS-urbanS-agencyS-route", "text" : "Stops Summary Report" },
					       { "id" : "stateS-urbanS-agencyS-route-schedule", "parent" : "stateS-urbanS-agencyS-route", "text" : "Route Schedule Report" },
				       { "id" : "stateS-urbanS-agencyS-stop", "parent" : "stateS-urbanS-agencyS", "text" : "Stops Summary Report" },
		       	{ "id" : "stateS-countyS", "parent" : "stateS", "text" : "Counties Summary Report" },
				 	//nodes under county summary
				   	{ "id" : "stateS-countyS-countyX", "parent" : "stateS-countyS", "text" : "County Extended Report" },
				   	{ "id" : "stateS-countyS-route", "parent" : "stateS-countyS", "text" : "Routes Summary Report" },
					   	//nodes under county->route summary
					   	{ "id" : "stateS-countyS-route-stop", "parent" : "stateS-countyS-route", "text" : "Stops Summary Report" },
					   	{ "id" : "stateS-countyS-route-schedule", "parent" : "stateS-countyS-route", "text" : "Route Schedule Report" },
				   	{ "id" : "stateS-countyS-stop", "parent" : "stateS-countyS", "text" : "Stops Summary Report" },
				   	{ "id" : "stateS-countyS-urbanS", "parent" : "stateS-countyS", "text" : "Urban Areas Summary Report" },
				       //nodes under county->urban summary
				       { "id" : "stateS-countyS-urbanS-urbanX", "parent" : "stateS-countyS-urbanS", "text" : "Urban Area Extended Report" },
				       { "id" : "stateS-countyS-urbanS-route", "parent" : "stateS-countyS-urbanS", "text" : "Routes Summary Report" },
					       //nodes under county->urban->route summary
					       { "id" : "stateS-countyS-urbanS-route-stop", "parent" : "stateS-countyS-urbanS-route", "text" : "Stops Summary Report" },
					       { "id" : "stateS-countyS-urbanS-route-schedule", "parent" : "stateS-countyS-urbanS-route", "text" : "Route Schedule Report" },
				       { "id" : "stateS-countyS-urbanS-stop", "parent" : "stateS-countyS-urbanS", "text" : "Stops Summary Report" },
				       { "id" : "stateS-countyS-urbanS-agencyS", "parent" : "stateS-countyS-urbanS", "text" : "Transit Agencies Summary Report" },
					       //nodes under county->urban->agency summary
					       { "id" : "stateS-countyS-urbanS-agencyS-agencyX", "parent" : "stateS-countyS-urbanS-agencyS", "text" : "Transit Agency Extended Report" },
					       { "id" : "stateS-countyS-urbanS-agencyS-route", "parent" : "stateS-countyS-urbanS-agencyS", "text" : "Routes Summary Report" },
						       //nodes under county->urban->agency->route summary
						       { "id" : "stateS-countyS-urbanS-agencyS-route-stop", "parent" : "stateS-countyS-urbanS-agencyS-route", "text" : "Stops Summary Report" },
						       { "id" : "stateS-countyS-urbanS-agencyS-route-schedule", "parent" : "stateS-countyS-urbanS-agencyS-route", "text" : "Route Schedule Report" },
					       { "id" : "stateS-countyS-urbanS-agencyS-stop", "parent" : "stateS-countyS-urbanS-agencyS", "text" : "Stops Summary Report" },
				   	{ "id" : "stateS-countyS-agencyS", "parent" : "stateS-countyS", "text" : "Transit Agencies Summary Report" },
				       //nodes under county->agency summary
				       { "id" : "stateS-countyS-agencyS-agencyX", "parent" : "stateS-countyS-agencyS", "text" : "Transit Agency Extended Report" },
				       { "id" : "stateS-countyS-agencyS-route", "parent" : "stateS-countyS-agencyS", "text" : "Routes Summary Report" },
					       //nodes under county->agency->route summary
					       { "id" : "stateS-countyS-agencyS-route-stop", "parent" : "stateS-countyS-agencyS-route", "text" : "Stops Summary Report" },
					       { "id" : "stateS-countyS-agencyS-route-schedule", "parent" : "stateS-countyS-agencyS-route", "text" : "Route Schedule Report" },
				       { "id" : "stateS-countyS-agencyS-stop", "parent" : "stateS-countyS-agencyS", "text" : "Stops Summary Report" },
				   	{ "id" : "stateS-countyS-tractS", "parent" : "stateS-countyS", "text" : "Tracts Summary Report" },
				       //nodes under county->tract summary
				       { "id" : "stateS-countyS-tractS-tractX", "parent" : "stateS-countyS-tractS", "text" : "Tract Extended Report" },
				       { "id" : "stateS-countyS-tractS-route", "parent" : "stateS-countyS-tractS", "text" : "Routes Summary Report" },
					       //nodes under county->tract->route summary
					       { "id" : "stateS-countyS-tractS-route-stop", "parent" : "stateS-countyS-tractS-route", "text" : "Stops Summary Report" },
					       { "id" : "stateS-countyS-tractS-route-schedule", "parent" : "stateS-countyS-tractS-route", "text" : "Route Schedule Report" },
				       { "id" : "stateS-countyS-tractS-stop", "parent" : "stateS-countyS-tractS", "text" : "Stops Summary Report" },
				       { "id" : "stateS-countyS-tractS-urbanS", "parent" : "stateS-countyS-tractS", "text" : "Urban Areas Summary Report" },
					       //nodes under county->tract->urban summary
					       { "id" : "stateS-countyS-tractS-urbanS-urbanX", "parent" : "stateS-countyS-tractS-urbanS", "text" : "Urban Area Extended Report" },
					       { "id" : "stateS-countyS-tractS-urbanS-route", "parent" : "stateS-countyS-tractS-urbanS", "text" : "Routes Summary Report" },
						       //nodes under county->tract->urban->route summary
						       { "id" : "stateS-countyS-tractS-urbanS-route-stop", "parent" : "stateS-countyS-tractS-urbanS-route", "text" : "Stops Summary Report" },
						       { "id" : "stateS-countyS-tractS-urbanS-route-schedule", "parent" : "stateS-countyS-tractS-urbanS-route", "text" : "Route Schedule Report" },
					       { "id" : "stateS-countyS-tractS-urbanS-stop", "parent" : "stateS-countyS-tractS-urbanS", "text" : "Stops Summary Report" },
					       { "id" : "stateS-countyS-tractS-urbanS-agencyS", "parent" : "stateS-countyS-tractS-urbanS", "text" : "Transit Agencies Summary Report" },
						       //nodes under county->tract->urban->agency summary
						       { "id" : "stateS-countyS-tractS-urbanS-agencyS-agencyX", "parent" : "stateS-countyS-tractS-urbanS-agencyS", "text" : "Transit Agency Extended Report" },
						       { "id" : "stateS-countyS-tractS-urbanS-agencyS-route", "parent" : "stateS-countyS-tractS-urbanS-agencyS", "text" : "Routes Summary Report" },
							       //nodes under county->tract->urban->agency->route summary
							       { "id" : "stateS-countyS-tractS-urbanS-agencyS-route-stop", "parent" : "stateS-countyS-tractS-urbanS-agencyS-route", "text" : "Stops Summary Report" },
							       { "id" : "stateS-countyS-tractS-urbanS-agencyS-route-schedule", "parent" : "stateS-countyS-tractS-urbanS-agencyS-route", "text" : "Route Schedule Report" },
						       { "id" : "stateS-countyS-tractS-urbanS-agencyS-stop", "parent" : "stateS-countyS-tractS-urbanS-agencyS", "text" : "Stops Summary Report" },
				       { "id" : "stateS-countyS-tractS-agencyS", "parent" : "stateS-countyS-tractS", "text" : "Transit Agencies Summary Report" },
					       //nodes under county->tract->agency summary
					       { "id" : "stateS-countyS-tractS-agencyS-agencyX", "parent" : "stateS-countyS-tractS-agencyS", "text" : "Transit Agency Extended Report" },
					       { "id" : "stateS-countyS-tractS-agencyS-route", "parent" : "stateS-countyS-tractS-agencyS", "text" : "Routes Summary Report" },
						       //nodes under county->tract->agency->route summary
						       { "id" : "stateS-countyS-tractS-agencyS-route-stop", "parent" : "stateS-countyS-tractS-agencyS-route", "text" : "Stops Summary Report" },
						       { "id" : "stateS-countyS-tractS-agencyS-route-schedule", "parent" : "stateS-countyS-tractS-agencyS-route", "text" : "Route Schedule Report" },
					       { "id" : "stateS-countyS-tractS-agencyS-stop", "parent" : "stateS-countyS-tractS-agencyS", "text" : "Stops Summary Report" },
				{ "id" : "stateS-congS", "parent" : "stateS", "text" : "Congressional Districts Summary Report" },
			       //nodes under cong summary
			       { "id" : "stateS-congS-congX", "parent" : "stateS-congS", "text" : "Congressional District Extended Report" },
			       { "id" : "stateS-congS-route", "parent" : "stateS-congS", "text" : "Routes Summary Report" },
				       //nodes under cong->route summary
				       { "id" : "stateS-congS-route-stop", "parent" : "stateS-congS-route", "text" : "Stops Summary Report" },
				       { "id" : "stateS-congS-route-schedule", "parent" : "stateS-congS-route", "text" : "Route Schedule Report" },
			       { "id" : "stateS-congS-stop", "parent" : "stateS-congS", "text" : "Stops Summary Report" },
			       { "id" : "stateS-congS-urbanS", "parent" : "stateS-congS", "text" : "Urban Areas Summary Report" },
				       //nodes under cong->urban summary
				       { "id" : "stateS-congS-urbanS-urbanX", "parent" : "stateS-congS-urbanS", "text" : "Urban Area Extended Report" },
				       { "id" : "stateS-congS-urbanS-route", "parent" : "stateS-congS-urbanS", "text" : "Routes Summary Report" },
					       //nodes under cong->urban->route summary
					       { "id" : "stateS-congS-urbanS-route-stop", "parent" : "stateS-congS-urbanS-route", "text" : "Stops Summary Report" },
					       { "id" : "stateS-congS-urbanS-route-schedule", "parent" : "stateS-congS-urbanS-route", "text" : "Route Schedule Report" },
				       { "id" : "stateS-congS-urbanS-stop", "parent" : "stateS-congS-urbanS", "text" : "Stops Summary Report" },
				       { "id" : "stateS-congS-urbanS-agencyS", "parent" : "stateS-congS-urbanS", "text" : "Transit Agencies Summary Report" },
					       //nodes under cong->urban->agency summary
					       { "id" : "stateS-congS-urbanS-agencyS-agencyX", "parent" : "stateS-congS-urbanS-agencyS", "text" : "Transit Agency Extended Report" },
					       { "id" : "stateS-congS-urbanS-agencyS-route", "parent" : "stateS-congS-urbanS-agencyS", "text" : "Routes Summary Report" },
						       //nodes under cong->urban->agency->route summary
						       { "id" : "stateS-congS-urbanS-agencyS-route-stop", "parent" : "stateS-congS-urbanS-agencyS-route", "text" : "Stops Summary Report" },
						       { "id" : "stateS-congS-urbanS-agencyS-route-schedule", "parent" : "stateS-congS-urbanS-agencyS-route", "text" : "Route Schedule Report" },
					       { "id" : "stateS-congS-urbanS-agencyS-stop", "parent" : "stateS-congS-urbanS-agencyS", "text" : "Stops Summary Report" },
			       { "id" : "stateS-congS-agencyS", "parent" : "stateS-congS", "text" : "Transit Agencies Summary Report" },
				       //nodes under cong->agency summary
				       { "id" : "stateS-congS-agencyS-agencyX", "parent" : "stateS-congS-agencyS", "text" : "Transit Agency Extended Report" },
				       { "id" : "stateS-congS-agencyS-route", "parent" : "stateS-congS-agencyS", "text" : "Routes Summary Report" },
					       //nodes under cong->agency->route summary
					       { "id" : "stateS-congS-agencyS-route-stop", "parent" : "stateS-congS-agencyS-route", "text" : "Stops Summary Report" },
					       { "id" : "stateS-congS-agencyS-route-schedule", "parent" : "stateS-congS-agencyS-route", "text" : "Route Schedule Report" },
				       { "id" : "stateS-congS-agencyS-stop", "parent" : "stateS-congS-agencyS", "text" : "Stops Summary Report" },
				{ "id" : "stateS-regionS", "parent" : "stateS", "text" : "ODOT Transit Regions Summary Report" },
			       //nodes under region summary
			       { "id" : "stateS-regionS-regionX", "parent" : "stateS-regionS", "text" : "ODOT Transit Region Extended Report" },
			       { "id" : "stateS-regionS-route", "parent" : "stateS-regionS", "text" : "Routes Summary Report" },
				       //nodes under region->route summary
				       { "id" : "stateS-regionS-route-stop", "parent" : "stateS-regionS-route", "text" : "Stops Summary Report" },
				       { "id" : "stateS-regionS-route-schedule", "parent" : "stateS-regionS-route", "text" : "Route Schedule Report" },
			       { "id" : "stateS-regionS-stop", "parent" : "stateS-regionS", "text" : "Stops Summary Report" },
			       { "id" : "stateS-regionS-urbanS", "parent" : "stateS-regionS", "text" : "Urban Areas Summary Report" },
				       //nodes under region->urban summary
				       { "id" : "stateS-regionS-urbanS-urbanX", "parent" : "stateS-regionS-urbanS", "text" : "Urban Area Extended Report" },
				       { "id" : "stateS-regionS-urbanS-route", "parent" : "stateS-regionS-urbanS", "text" : "Routes Summary Report" },
					       //nodes under region->urban->route summary
					       { "id" : "stateS-regionS-urbanS-route-stop", "parent" : "stateS-regionS-urbanS-route", "text" : "Stops Summary Report" },
					       { "id" : "stateS-regionS-urbanS-route-schedule", "parent" : "stateS-regionS-urbanS-route", "text" : "Route Schedule Report" },
				       { "id" : "stateS-regionS-urbanS-stop", "parent" : "stateS-regionS-urbanS", "text" : "Stops Summary Report" },
				       { "id" : "stateS-regionS-urbanS-agencyS", "parent" : "stateS-regionS-urbanS", "text" : "Transit Agencies Summary Report" },
					       //nodes under region->urban->agency summary
					       { "id" : "stateS-regionS-urbanS-agencyS-agencyX", "parent" : "stateS-regionS-urbanS-agencyS", "text" : "Transit Agency Extended Report" },
					       { "id" : "stateS-regionS-urbanS-agencyS-route", "parent" : "stateS-regionS-urbanS-agencyS", "text" : "Routes Summary Report" },
						       //nodes under region->urban->agency->route summary
						       { "id" : "stateS-regionS-urbanS-agencyS-route-stop", "parent" : "stateS-regionS-urbanS-agencyS-route", "text" : "Stops Summary Report" },
						       { "id" : "stateS-regionS-urbanS-agencyS-route-schedule", "parent" : "stateS-regionS-urbanS-agencyS-route", "text" : "Route Schedule Report" },
					       { "id" : "stateS-regionS-urbanS-agencyS-stop", "parent" : "stateS-regionS-urbanS-agencyS", "text" : "Stops Summary Report" },
			       { "id" : "stateS-regionS-agencyS", "parent" : "stateS-regionS", "text" : "Transit Agencies Summary Report" },
				       //nodes under region->agency summary
				       { "id" : "stateS-regionS-agencyS-agencyX", "parent" : "stateS-regionS-agencyS", "text" : "Transit Agency Extended Report" },
				       { "id" : "stateS-regionS-agencyS-route", "parent" : "stateS-regionS-agencyS", "text" : "Routes Summary Report" },
					       //nodes under region->agency->route summary
					       { "id" : "stateS-regionS-agencyS-route-stop", "parent" : "stateS-regionS-agencyS-route", "text" : "Stops Summary Report" },
					       { "id" : "stateS-regionS-agencyS-route-schedule", "parent" : "stateS-regionS-agencyS-route", "text" : "Route Schedule Report" },
				       { "id" : "stateS-regionS-agencyS-stop", "parent" : "stateS-regionS-agencyS", "text" : "Stops Summary Report" },
				{ "id" : "stateS-placeS", "parent" : "stateS", "text" : "Census Places Summary Report" },
			       //nodes under place summary
			       { "id" : "stateS-placeS-placeX", "parent" : "stateS-placeS", "text" : "Census Place Extended Report" },
			       { "id" : "stateS-placeS-route", "parent" : "stateS-placeS", "text" : "Routes Summary Report" },
				       //nodes under place->route summary
				       { "id" : "stateS-placeS-route-stop", "parent" : "stateS-placeS-route", "text" : "Stops Summary Report" },
				       { "id" : "stateS-placeS-route-schedule", "parent" : "stateS-placeS-route", "text" : "Route Schedule Report" },
			       { "id" : "stateS-placeS-stop", "parent" : "stateS-placeS", "text" : "Stops Summary Report" },
			       { "id" : "stateS-placeS-urbanS", "parent" : "stateS-placeS", "text" : "Urban Areas Summary Report" },
				       //nodes under place->urban summary
				       { "id" : "stateS-placeS-urbanS-urbanX", "parent" : "stateS-placeS-urbanS", "text" : "Urban Area Extended Report" },
				       { "id" : "stateS-placeS-urbanS-route", "parent" : "stateS-placeS-urbanS", "text" : "Routes Summary Report" },
					       //nodes under place->urban->route summary
					       { "id" : "stateS-placeS-urbanS-route-stop", "parent" : "stateS-placeS-urbanS-route", "text" : "Stops Summary Report" },
					       { "id" : "stateS-placeS-urbanS-route-schedule", "parent" : "stateS-placeS-urbanS-route", "text" : "Route Schedule Report" },
				       { "id" : "stateS-placeS-urbanS-stop", "parent" : "stateS-placeS-urbanS", "text" : "Stops Summary Report" },
				       { "id" : "stateS-placeS-urbanS-agencyS", "parent" : "stateS-placeS-urbanS", "text" : "Transit Agencies Summary Report" },
					       //nodes under place->urban->agency summary
					       { "id" : "stateS-placeS-urbanS-agencyS-agencyX", "parent" : "stateS-placeS-urbanS-agencyS", "text" : "Transit Agency Extended Report" },
					       { "id" : "stateS-placeS-urbanS-agencyS-route", "parent" : "stateS-placeS-urbanS-agencyS", "text" : "Routes Summary Report" },
						       //nodes under place->urban->agency->route summary
						       { "id" : "stateS-placeS-urbanS-agencyS-route-stop", "parent" : "stateS-placeS-urbanS-agencyS-route", "text" : "Stops Summary Report" },
						       { "id" : "stateS-placeS-urbanS-agencyS-route-schedule", "parent" : "stateS-placeS-urbanS-agencyS-route", "text" : "Route Schedule Report" },
					       { "id" : "stateS-placeS-urbanS-agencyS-stop", "parent" : "stateS-placeS-urbanS-agencyS", "text" : "Stops Summary Report" },
			       { "id" : "stateS-placeS-agencyS", "parent" : "stateS-placeS", "text" : "Transit Agencies Summary Report" },
				       //nodes under place->agency summary
				       { "id" : "stateS-placeS-agencyS-agencyX", "parent" : "stateS-placeS-agencyS", "text" : "Transit Agency Extended Report" },
				       { "id" : "stateS-placeS-agencyS-route", "parent" : "stateS-placeS-agencyS", "text" : "Routes Summary Report" },
					       //nodes under place->agency->route summary
					       { "id" : "stateS-placeS-agencyS-route-stop", "parent" : "stateS-placeS-agencyS-route", "text" : "Stops Summary Report" },
					       { "id" : "stateS-placeS-agencyS-route-schedule", "parent" : "stateS-placeS-agencyS-route", "text" : "Route Schedule Report" },
				       { "id" : "stateS-placeS-agencyS-stop", "parent" : "stateS-placeS-agencyS", "text" : "Stops Summary Report" },
				   	{ "id" : "stateS-AggUAS", "parent" : "stateS", "text" : "Aggregated Urban Areas Summary Report" },
				      //nodes under Aggregated Urban Areas Report summary
				       	{ "id" : "stateS-AggUAS-AggUAX", "parent" : "stateS-AggUAS", "text" : "Aggregated Urban Areas Extended Report"},
				       	{ "id" : "stateS-AggUAS-urbanS", "parent" : "stateS-AggUAS", "text" : "Urban Areas Summary Report"},
			       		//nodes under Aggregated Urban Areas -> Urban Areas  				        
					       { "id" : "stateS-AggUAS-urbanS-urbanX", "parent" : "stateS-AggUAS-urbanS", "text" : "Urban Area Extended Report" },
					       { "id" : "stateS-AggUAS-urbanS-route", "parent" : "stateS-AggUAS-urbanS", "text" : "Routes Summary Report" },
					       //nodes under Aggregated Urban Areas -> Urban Areas ->route summary
						       { "id" : "stateS-AggUAS-urbanS-route-stop", "parent" : "stateS-AggUAS-urbanS-route", "text" : "Stops Summary Report" },
						       { "id" : "stateS-AggUAS-urbanS-route-schedule", "parent" : "stateS-AggUAS-urbanS-route", "text" : "Route Schedule Report" },
					       { "id" : "stateS-AggUAS-urbanS-stop", "parent" : "stateS-AggUAS-urbanS", "text" : "Stops Summary Report" },
					       { "id" : "stateS-AggUAS-urbanS-agencyS", "parent" : "stateS-AggUAS-urbanS", "text" : "Transit Agencies Summary Report" },
						       //nodes under urban->agency summary
						       { "id" : "stateS-AggUAS-urbanS-agencyS-agencyX", "parent" : "stateS-AggUAS-urbanS-agencyS", "text" : "Transit Agency Extended Report" },
						       { "id" : "stateS-AggUAS-urbanS-agencyS-route", "parent" : "stateS-AggUAS-urbanS-agencyS", "text" : "Routes Summary Report" },
							       //nodes under urban->agency->route summary
							       { "id" : "stateS-AggUAS-urbanS-agencyS-route-stop", "parent" : "stateS-AggUAS-urbanS-agencyS-route", "text" : "Stops Summary Report" },
							       { "id" : "stateS-AggUAS-urbanS-agencyS-route-schedule", "parent" : "stateS-AggUAS-urbanS-agencyS-route", "text" : "Route Schedule Report" },
						       { "id" : "stateS-AggUAS-urbanS-agencyS-stop", "parent" : "stateS-AggUAS-urbanS-agencyS", "text" : "Stops Summary Report" },	
	    ]
	}
};