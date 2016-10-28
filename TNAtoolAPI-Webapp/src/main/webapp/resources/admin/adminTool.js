function runPlayground(b){
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/FileUpload?&runPlayground="+b,
        dataType: "json",
        async: false,
        success: function(d) {
        	
        }
	});
}

function deleteDb(i){
    if (confirm("Are you sure you want to permanently delete this database?") == false) {
        return;
    }
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/deleteDB?&index="+i,
        dataType: "json",
        async: false,
        success: function(data) {
    	    	alert(data.DBError);
    	    	location.reload(true);
        }
     });
}

function addModifyDB(j, index){
	ind = index;
	var urlValue = "//:/";
	if(ind!=-1){
		info = dbInfo[j].slice();
		urlValue = info[4];
		var tmp = info[3].split('/');
		info[3]=tmp[tmp.length-1];
	}else{
		if(checkForDeactivated()){
			return;
		}
		info = defaultInfo;
		$.ajax({
	        type: "GET",
	        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/getIndex",
	        dataType: "json",
	        async: false,
	        success: function(d) {
	        	info[0]=d;
	        }
		});
	}
	urlValue = urlValue.split("/");
	var urlV = urlValue[2].split(":");
	oldName = urlValue[3];
	var html="";
	
	html+="<label>Database Number:</label><br>";
	html+="<input type='text' id='"+dbInfo[0][0]+"' value='"+(Number(info[0])+1)+"' required disabled style='width:90%;font-size:80%'><br><br>";
	html+="<label>Database Name:</label><br>";
	html+="<input type='text' id='"+dbInfo[0][4]+"n"+"' value='"+urlValue[3]+"' required style='width:90%;font-size:80%'><br><br>";
	html+="<label>Database Display Name:</label><br>";
	html+="<input type='text' id='"+dbInfo[0][1]+"' value='"+info[1]+"' required style='width:90%;font-size:80%'><br><br>";
	
	html+="<label>Connection URL:</label><br>";
	html+="<input type='text' id='"+dbInfo[0][4]+"' value='"+urlV[0]+"' required style='width:90%;font-size:80%'><br><br>";
	html+="<label>Connection Port:</label><br>";
	html+="<input type='text' id='"+dbInfo[0][4]+"p"+"' value='"+urlV[1]+"' required style='width:90%;font-size:80%'><br><br>";
	
	html+="<label>Username:</label><br>";
	html+="<input type='text' id='"+dbInfo[0][5]+"' value='"+info[5]+"' required style='width:90%;font-size:80%'><br><br>";
	html+="<label>Password:</label><br>";
	html+="<input type='text' id='"+dbInfo[0][6]+"' value='"+info[6]+"' required style='width:90%;font-size:80%'><br><br>";

	html+="<input type='submit' id='dialogSubmit' tabindex='-1' style='position:absolute; top:-1000px'>";
	$('#dialogFields').html(html);
//	if(ind!=-1){
//		$('#'+dbInfo[0][4]+"n").prop('disabled', true);
//	}
	$('#connectionURLn').keyup(function () { 
		$('#'+dbInfo[0][2]).val($('#'+dbInfo[0][4]+'n').val()+".cfg.xml"); 
		$('#'+dbInfo[0][3]).val($('#'+dbInfo[0][4]+'n').val()+".cfg.xml"); 
		});
	$('#connectionURLn').change(function () { 
		$('#'+dbInfo[0][2]).val($('#'+dbInfo[0][4]+'n').val()+".cfg.xml"); 
		$('#'+dbInfo[0][3]).val($('#'+dbInfo[0][4]+'n').val()+".cfg.xml"); 
		});
	
	 form = dialog.find( "form" ).on( "submit", function( event ) {
	    event.preventDefault();
	    var dbname = $('#'+dbInfo[0][1]).val();
	    var user = $('#'+dbInfo[0][5]).val();
	    var pass = $('#'+dbInfo[0][6]).val();
	    var cURL = "jdbc:postgresql://"+$('#'+dbInfo[0][4]).val()+":"+$('#'+dbInfo[0][4]+"p").val()+"/";
	    var db = $('#'+dbInfo[0][4]+"n").val();
	    var regex = new RegExp("^[a-z][a-z0-9\_]+$");
	    var oldURL = info[4];
	    var olddbname = info[1];
	    if (!regex.test(db)) {
	       alert("The \"Name\" under the \"Connection URL\" can only accept lower-case letters, digits, and underscores, starting with a letter.");
	       return;
	    }
	    if(db.length>254){
	    	alert("The database name is too long (max-length=254 characters).");
	    }
	    $.ajax({
	        type: "GET",
	        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/checkInput?&dbname="+dbname+"&cURL="+cURL+"&user="+user+"&pass="+pass+"&db="+db+"&oldURL="+oldURL+"&olddbname="+olddbname,
	        dataType: "json",
	        async: false,
	        success: function(d) {
	        	if(d.DBError=="true"){
	        		if(ind!=-1){
	        			if (confirm("Are you sure you want to modify the connection information of this database?") == false) {
	        		        return;
	        		    }
	        		}else{
	        			if (confirm("Are you sure you want to add this database?") == false) {
	        		        return;
	        		    }
	        		}
	        		addDB();
	        	}else{
	        		alert(d.DBError);
	        	}
	        }
		});
	    
	 });
	 dialog.dialog( "open" );
}

function runQueries(index){
	db = dbInfo[index].toString();
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/updateFeeds?&db="+db+"&folder="+folder+"PGSQL/",
        dataType: "json",
        async: false,
        success: function(d) {
        	location.reload(true);
        }
	});
	
}

/*function addPsqlFunctions(index){
	db = dbInfo[index].toString();
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/addPsqlFunctions?&db="+db,
        dataType: "json",
        async: false,
        success: function(d) {
        	
        }
	});
	
}*/

function addIndex(index){
	db = dbInfo[index].toString();
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/addIndex?&db="+db,
        dataType: "json",
        async: false,
        success: function(d) {
        	
        }
	});
	
}

function dSubmit(){
	$('#dialogSubmit').click();
}

function addDB(){
	var newDB = [];
	for(var i=0; i<dbInfo[0].length; i++){
    	newDB.push($('#'+dbInfo[0][i]).val());
    }
	newDB[0] --; 
	newDB[2] = defaultInfo[2]+$('#'+dbInfo[0][4]+"n").val()+".cfg.xml";
	newDB[3] = defaultInfo[3]+$('#'+dbInfo[0][4]+"n").val()+".cfg.xml";
	newDB[4]= "jdbc:postgresql://"+newDB[4]+":"+$('#'+dbInfo[0][4]+"p").val()+"/"+$('#'+dbInfo[0][4]+"n").val();
	newDB[7] = defaultInfo[7];
	newDB[8] = defaultInfo[8];
	newDB[9] = defaultInfo[9];
    var db = newDB.toString();
	if(ind!=-1){
		var oldcfgSpatial = dbInfo[ind+1][2];
		var oldcfgTransit = dbInfo[ind+1][3];
		$.ajax({
	        type: "GET",
	        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/updateDB?&db="+db+"&oldName="+oldName+"&oldcfgSpatial="+oldcfgSpatial+"&oldcfgTransit="+oldcfgTransit,
	        dataType: "json",
	        async: false,
	        success: function(d) {
	        	alert(d.DBError);
		    	location.reload(true);
	        }
	    });
	}else{
		$.ajax({
	        type: "GET",
	        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/addDB?&db="+db,
	        dataType: "json",
	        async: false,
	        success: function(d) {
	        	alert(d.DBError);
		    	location.reload(true);
	        }
	    });
	} 
}

function stringToDate(str){
	if(str==null){
		return "";
	}
	var sArr = new Array();
	sArr.push(str.substring(4, 6));
	sArr.push(str.substring(6, 8));
	sArr.push(str.substring(0, 4));
	return sArr.join("/");
}


function listOfAgencies(index){
	var db = dbInfo[index].toString();
	var indexx = 1001+index;
	
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/agencyList?&db="+db,
        dataType: "json",
        async: false,
        success: function(d) {
//        	if(d.feeds.length==0){
//        		return "";
//        	}
//        	console.log(d);
        	if(undefined != d.DBError){
        		aList = "There is currently no GTFS feeds uploaded into the database.";
        		return aList;
        	}else{
	        	var html = "";
	        	var htmlAgencies="";
	        	var j;
//	        	html += "<p><input type='button' value='Select All' onclick='selectAllCheckBoxes("+indexx+")'>";
//	        	html += "<input type='button' value='Deselect All' onclick='deselectAllCheckBoxes("+indexx+")' style='margin-left:1em'></p>";
	        	html += "<input type='button' value='Delete selected feeds' class='btn btn-danger delete' onclick='deleteFeed("+index+")'><br><br>";
	        	html += "<table class='table table-striped' style='border-collapse: collapse;'><tr><th></th><th>#</th><th>Feed Name</th><th>Containing Agencies</th><th>Start Date</th><th>End Date</th></tr>";
	        	$.each(d.feeds, function(i,item){
	        		if(d.names[i]!=null){
	        			j=i+1;
	        			htmlAgencies = "";
		        		var agencynames = d.names[i].split(",");
//		        		var html1 = "<span style='margin-left:3.5em'>Agencies:</span>";
		        		for(var k=0; k<agencynames.length; k++){
//		        			html1 += "<br><span style='margin-left:6em'>"+agencynames[k]+"<span>";
		        			htmlAgencies += "<p>"+agencynames[k]+"</p>";
		        		}
		        		html += "<tr><td><input type='checkbox' class='selectFeed"+indexx+"' id='"+item+"' ></td>";
		        		html +=	"<td>"+j+"</td>";
		        		html += "<td>"+item+"</td>";
		        		html += "<td>"+htmlAgencies+"</td>";
		        		html += "<td>"+stringToDate(d.startdates[i])+"</td>";
		        		html += "<td>"+stringToDate(d.enddates[i])+"</td>";
		        		
//		        		html += "<td><input type='button' value='Delete' class='btn btn-danger delete' onclick='' style='font-size:80%'></td>";
		        		html += "</tr>";	
//		        		html += "<p><input type='checkbox' class='selectFeed"+indexx+"' id='"+item+"' ><span style='margin-left:2em'>"+item+"</span>"
//		        		+"<br><span style='margin-left:3.5em'>Start Date: "+stringToDate(d.startdates[i])+"</span>"+"<br><span style='margin-left:3.5em'>End Date: "+stringToDate(d.enddates[i])+"</span>"+html1+"</p>";
	        		}
	        	});
	        	html += "</table>";
	        	
//	        	html += "<br><input type='button' value='Update selected feeds' onclick='updateFeed("+index+")'>";
	        	aList = html;
        	}
        }
	});
	return aList;
}

function selectAllCheckBoxes(index){
	$('.selectFeed'+index).prop('checked', true);
}

function deselectAllCheckBoxes(index){
	$('.selectFeed'+index).prop('checked', false);
}

function listOfFeeds(index){
	var html="";
	/*var db = dbInfo[index].toString();
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/feedlist?&foldername="+folder+"Feeds/&db="+db,
        dataType: "json",
        async: false,
        success: function(d) {
        	html = "<p>List of available feeds</p>";
        	html += "<p><input type='button' value='Select All' onclick='selectAllCheckBoxes("+index+")'>";
        	html += "<input type='button' value='Deselect All' onclick='deselectAllCheckBoxes("+index+")' style='margin-left:1em'></p>";
        	$.each(d.feeds, function(i,item){
        		html += "<p><input type='checkbox' class='selectFeed"+index+"' id='"+item+"'><span style='margin-left:2em'>"+item+"</span></p>";
        	});
        	html += "<br><input type='button' value='Add feeds to the database' onclick='addFeed("+index+")'>";
        }
	});*/
	return html;
}

function updateFeed(index){
	var indexx = 1001+index;
	var dbindex = index-1;
	
	var feeds = new Array();
	$('.selectFeed'+indexx).each(function(){
		if($(this).is(':checked')){
			feeds.push($(this).attr('id'));
		}
	});
	
	for(var i=0; i<feeds.length; i++){
		//alert(feeds[i]);
		$.ajax({
	        type: "GET",
	        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/updateSingleFeed?&feedname="+feeds[i]+"&dbindex="+dbindex,
	        dataType: "json",
	        async: false,
	        success: function(d) {
	        }
		});
	}
	
	location.reload(true);
}

function deleteFeed(index){
	var db = dbInfo[index].toString();
	indexx = 1001+index;
	var feeds = new Array();
	
	$('.selectFeed'+indexx).each(function(){
		if($(this).is(':checked')){
			feeds.push($(this).attr('id'));
		}
	});
//	console.log(feeds);
	for(var i=0; i<feeds.length; i++){
		$.ajax({
	        type: "GET",
	        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/deletefeed?&feedname="+feeds[i]+"&db="+db,
	        dataType: "json",
	        async: false,
	        success: function(d) {
	        }
		});
	}
	
	var feeds = listOfAgencies(index);
	$('#agencyList').html(feeds);
//	location.reload(true);
}

function addFeed(feeds){
	var db = dbInfo[currentINDEX].toString();
	/*var feeds = new Array();
	
	$('.selectFeed'+index).each(function(){
		if($(this).is(':checked')){
			feeds.push($(this).attr('id'));
		}
	});
	feeds.join("$$");*/
	for(var i=0; i<feeds.length; i++){
		$.ajax({
	        type: "GET",
	        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/addfeed?&feedname="+feeds[i]+"&db="+db,
//	        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/addfeed?&feedname="+folder+"Feeds/"+feeds[i]+"&db="+db,
	        dataType: "text",
	        async: false,
	        success: function(d) {
	        	if(d=="done"){
	        		console.log(feeds[i]+" was successfully added");
	        	}else{
	        		console.log(feeds[i]+" could not be added. Error: "+d);
	        	}
	        }
		});
	}
//	location.reload(true);
}

function callDBfuntions(dbFunction){
	for(var i=1;i<dbInfo.length;i++){
		dbFunction(i);
	}
}

function setButtonStatus(dbNumber){
	var status = dbStatus[dbNumber];
	$('#dbButtons'+dbNumber+' img').each(function(){
		if(!status[classMap[$(this).attr('class')]]){
			$(this).attr('src','../resources/images/cross.png');
		}else{
			$(this).attr('src','../resources/images/check.png');
		}
	});
	if(status.Activated){
		$('#dbButtons'+dbNumber+' input').each(function(){
			$(this).prop('disabled', true);
		});
		$('#dbButtons'+dbNumber+' select').each(function(){
			$(this).prop('disabled', true);
		});
		$('#dbButtons'+dbNumber+' input.activate').prop('disabled', false);
//		$('#dbButtons'+dbNumber+' input.modify').prop('disabled', false);
		$('#dbButtons'+dbNumber+' input.activate').val('Deactivate Database');
	}else{
		$('#dbButtons'+dbNumber+' input').each(function(){
			$(this).prop('disabled', false);
		});
		$('#dbButtons'+dbNumber+' select').each(function(){
			$(this).prop('disabled', false);
		});
		$('#dbButtons'+dbNumber+' input.activate').prop('disabled', false);
		$('#dbButtons'+dbNumber+' input.activate').val('Activate Database');
		if(!status.Updated && (dbStatus[dbNumber].GtfsFeeds && dbStatus[dbNumber].Census)){
			$('#dbButtons'+dbNumber+' input.update').prop('disabled', false);
		}else{
			$('#dbButtons'+dbNumber+' input.update').prop('disabled', true);
		}
	}
}

function checkDBStatus(dbNumber){
	var db = dbInfo[dbNumber].toString();
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/checkDBStatus?&db="+db,
        dataType: "json",
        async: false,
        success: function(d) {
        	dbStatus[dbNumber] = d;
        }
	});
}

/**
 * checks for any existing deactivated database.
 */
function checkForDeactivated(){
	var b = false;
	var db;
	for(var i=1;i<dbInfo.length;i++){
		db = dbInfo[i].toString();
		$.ajax({
	        type: "GET",
	        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/checkForDeactivated?&db="+db,
	        dataType: "text",
	        async: false,
	        success: function(d) {
	        	if(d=="true"){
	        		b = true;
	        		alert("Database #"+i+" is not active. 'Delete' or 'Activate' this database before making changes to other databases.");
	        		
	        	}
	        }
		});
	}
	return b;
}

/**
 * Activates the databases.
 */
function activateDBs(index){
	if(!dbStatus[index].GtfsFeeds || !dbStatus[index].Census || !dbStatus[index].Employment 
			|| !dbStatus[index].Parknride || !dbStatus[index].Title6 || !dbStatus[index].FutureEmp || !dbStatus[index].FuturePop){
		alert("Comlete all the datasets before activatig the database.");
		return;
	}
	if(!dbStatus[index].Updated){
		alert("The update queries must be run on the database before activation.");
		return;
	}
	if (confirm("Are you sure you want to activate the database?") == false) {
        return;
    }
	var db = dbInfo[index].toString();
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/activateDBs?&db="+db,
        dataType: "text",
        async: false,
        success: function(d) {
        	alert("Database #"+index+" is activated.");
        	location.reload(true);
        }
	});
}


/**
 * Deactivates the databases.
 */
function deactivateDBs(index){
	if(checkForDeactivated()){
		return;
	}
	if (confirm("Are you sure you want to deactivate this database?") == false) {
        return;
    }
	var db = dbInfo[index].toString();
	index --;
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/deactivateDBs?&db="+db+"&index="+index,
        dataType: "text",
        async: false,
        success: function(d) {
        	index++;
        	alert("Database #"+index+" is deactivated.");
        	location.reload(true);
        }
	});
}

/**
 * Changes DB status.
 */
function changeStatus(index, field, b, updateBool){
	if(updateBool==undefined || updateBool==null){
		updateBool=false;
	}
	var db = dbInfo[index].toString();
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/changeDBStatus?&db="+db+"&field="+field+"&b="+b,
        dataType: "text",
        async: false,
        success: function(d) {
        	callDBfuntions(checkDBStatus);
        	callDBfuntions(setButtonStatus);
        	if(updateBool){
        		return;
        	}
        	
//        	location.reload(true);
        }
	});
}

function checkDuplicateFeeds(feed){
	var db = dbInfo[currentINDEX].toString();
	var b = false;
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/checkDuplicateFeeds?&db="+db+"&feed="+feed,
        dataType: "text",
        async: false,
        success: function(d) {
        	if(d=="true"){
        		b=true;
        	}
        }
	});
	return b;
}

/**
 * Opens GTFS dialog.
 */
function openGTFS(index){
	var db = dbInfo[index].toString();
	
	currentINDEX = index;
	
	
	$( "#gtfs_current_feeds" ).accordion({
	      collapsible: true,
	      active : false,
	      beforeActivate: function( event, ui ) {
	    	  if(!$.isNumeric($(this).accordion( "option", "active" ))){
	    		  var feeds = listOfAgencies(index);
	    		  $('#agencyList').html(feeds);
	    	  }
	    	  
	      }
	    });
	$('#gtfs_current_feeds .ui-accordion-content').css('height','400px');
	GTFSdialog.dialog( "open" );
	
//	var status = dbStatus[index];
//	var b = status.GtfsFeeds;
//	changeStatus(index, "gtfs_feeds", !b);
//	if(!b){
//		changeStatus(index, "update_process", false, true);
//	}
}

/**
 * Runs Update Queries.
 */
function runUpdates(index){
	var db = dbInfo[index].toString();
	var status = dbStatus[index];
//	var b = status.Updated;
	changeStatus(index, "update_process", true);
}

/**
 * Opens Future Population dialog.
 */
function openFpop(index){
	var db = dbInfo[index].toString();
	var status = dbStatus[index];
	var b = status.FuturePop;
	changeStatus(index, "future_pop", !b);
}

/**
 * Opens Future Employment dialog.
 */
function openFemp(index){
	var db = dbInfo[index].toString();
	var status = dbStatus[index];
	var b = status.FutureEmp;
	changeStatus(index, "future_emp", !b);
}

/**
 * Opens Title 6 dialog.
 */
function openT6(index){
	var db = dbInfo[index].toString();
	var status = dbStatus[index];
	var b = status.Title6;
	changeStatus(index, "title6", !b);
	
}

/**
 * Opens PnR dialog.
 */
function openPnr(index){
	var db = dbInfo[index].toString();
	var status = dbStatus[index];
	var b = status.Parknride;
	changeStatus(index, "parknride", !b);
}

function copyPnr(index){
	var dbTo = dbInfo[index].toString();
	var dbFrom = dbInfo[$("#pnr-select"+index).val()].toString();
}

/**
 * Opens Employment dialog.
 */
function openEmp(index){
	var db = dbInfo[index].toString();
	var status = dbStatus[index];
	var b = status.Employment;
	changeStatus(index, "employment", !b);
	
}

/**
 * Opens Census dialog.
 */
function openCensus(index){
	var db = dbInfo[index].toString();
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/restoreCensus?&db="+db,
        dataType: "text",
        async: false,
        success: function(d) {
        	
        }
	});
	
	var status = dbStatus[index];
	var b = status.Census;
	changeStatus(index, "census", !b);
	if(!b){
		changeStatus(index, "update_process", false, true);
	}
}

/**
 * Deletes uploaded GTFS feeds.
 */
function deleteUploadedGTFS(){
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/deleteUploadedGTFS",
        dataType: "text",
        async: false,
        success: function(d) {
        	
        }
	});
}

function deleteProcessGTFS(){
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/deleteProcessGTFS",
        dataType: "text",
        async: false,
        success: function(d) {
        	
        }
	});
}

function activeDeactive(index){
	var status = dbStatus[index];
	if(status.Activated){
		deactivateDBs(index);
	}else{
		activateDBs(index);
	}
}

function addDBSelect(index){
	var html = "";
	for(var i=1;i<dbInfo.length;i++){
		if(i==index){
			continue;
		}
		html+="<option value='"+i+"'>"+dbInfo[i][1]+"</option>";
	}
	return html;
}

var ind;
var currentINDEX;
var currentFiles;
var dialog;
var GTFSdialog;
var dbInfo = [[]];
var dbStatus = [{}]; //the first object is empty 
var defaultInfo = ["","","com/model/database/connections/spatial/","com/model/database/connections/transit/",
                   "","","",
                   "com/model/database/connections/spatial/mapping/mapping.hbm.xml",
                   "com/model/database/connections/transit/mapping/GtfsMapping.hibernate.xml",
                   "com/model/database/connections/transit/mapping/HibernateGtfsRelationalDaoImpl.hibernate.xml"];
var folder = "../resources/admin/Development/";
var fList;
var aList;
var oldName;
var classMap = {"activate":"Activated","gtfs":"GtfsFeeds","census":"Census","emp":"Employment",
                "pnr":"Parknride","t6":"Title6","femp":"FutureEmp","fpop":"FuturePop","update":"Updated"};
$(document).ready(function(){
	
	$.ajax({
        type: "GET",
        //url: "../resources/admin/dbInfo.csv",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/readDBinfo",
        dataType: "text",
        async: false,
        success: function(data) {
//        	var lines = data.split(/\r\n|\n/);
        	var lines = data.split('#$#');
        	var connection = [];
        	for(var i=0; i<lines.length; i++){
        		if(lines[i].split(',').length>1){
        			dbInfo[i] = lines[i].split(',');
        		}
        	}
        	var html="<div id='dbAccordion'>";
        	for(var i=dbInfo.length-1; i>0; i--){
        		html += "<h3>Database #"+i+": <i>"+dbInfo[i][1]+"</i></h3><div>";
        		html += "<table class='databaseOuterTable' cellpadding='0'><tr><td style='padding:0px'>";
        		html += "<div class='databaseDivs'>";
        		html += "<table>";
            	
            	
            	connection = dbInfo[i][4].split("/");
            	html += "<tr><td>Name: </td><td>"+connection[connection.length-1]+"</td></tr>";
//            	html += "<tr><td>Display Name: </td><td>"+dbInfo[i][1]+"</td></tr>";
            	html += "<tr><td>Connection URL: </td><td>"+connection[connection.length-2].split(":")[0]+"</td></tr>";
            	html += "<tr><td>Connection Port: </td><td>"+connection[connection.length-2].split(":")[1]+"</td></tr>";
            	html += "<tr><td>Username: </td><td>"+dbInfo[i][5]+"</td></tr>";
            	html += "<tr><td>Password: </td><td>"+dbInfo[i][6]+"</td></tr>";

            	html += "</table></div></td>";
            	html += "<td><div id='dbButtons"+i+"' style='font-size:85%'><table>" +
            			"<tr><td><input type='button' class='gtfs dbButtons-class single' value='Import GTFS Feeds' onclick='openGTFS("+i+")'></td>" +
            			"<td><img src='../resources/images/check.png' alt='dataset status' style='width: 1.2em;margin-left: 0.3em;' class='gtfs'></td></tr>"+
            			"<tr><td><input type='button' class='census dbButtons-class' value='Import Census' onclick='openCensus("+i+")'> or " +
            					"<input type='button' class='census dbButtons-class second' value='Copy from' onclick='copyCensus("+i+")'>" +
            					"<select class='census select-class' id='census-select"+i+"'>"+addDBSelect(i)+"</select></td>"+
            			"<td><img src='../resources/images/check.png' alt='dataset status' style='width: 1.2em;margin-left: 0.3em;' class='census'></td></tr>"+
            			"<tr><td><input type='button' class='emp dbButtons-class' value='Import Employment' onclick='openEmp("+i+")'> or " +
            					"<input type='button' class='emp dbButtons-class second' value='Copy from' onclick='copyEmp("+i+")'>" +
            					"<select class='census select-class' id='emp-select"+i+"'>"+addDBSelect(i)+"</select></td>"+
            			"<td><img src='../resources/images/check.png' alt='dataset status' style='width: 1.2em;margin-left: 0.3em;' class='emp'></td></tr>"+
            			"<tr><td><input type='button' class='pnr dbButtons-class' value='Park & Ride' onclick='openPnr("+i+")'> or " +
            					"<input type='button' class='pnr dbButtons-class second' value='Copy from' onclick='copyPnr("+i+")'>" +
            					"<select class='pnr select-class' id='pnr-select"+i+"'>"+addDBSelect(i)+"</select></td>"+
            			"<td><img src='../resources/images/check.png' alt='dataset status' style='width: 1.2em;margin-left: 0.3em;' class='pnr'></td></tr>"+
            			"<tr><td><input type='button' class='t6 dbButtons-class' value='Title VI' onclick='openT6("+i+")'> or " +
            					"<input type='button' class='t6 dbButtons-class second' value='Copy from' onclick='copyT6("+i+")'>" +
            					"<select class='t6 select-class' id='t6-select"+i+"'>"+addDBSelect(i)+"</select></td>"+
            			"<td><img src='../resources/images/check.png' alt='dataset status' style='width: 1.2em;margin-left: 0.3em;' class='t6'></td></tr>"+
            			"<tr><td><input type='button' class='fpop dbButtons-class' value='Future Population' onclick='openFpop("+i+")'> or " +
            					"<input type='button' class='fpop dbButtons-class second' value='Copy from' onclick='copyFpop("+i+")'>" +
            					"<select class='fpop select-class' id='fpop-select"+i+"'>"+addDBSelect(i)+"</select></td>"+
            			"<td><img src='../resources/images/check.png' alt='dataset status' style='width: 1.2em;margin-left: 0.3em;' class='fpop'></td></tr>"+
            			"<tr><td><input type='button' class='femp dbButtons-class' value='Future Employment' onclick='openFemp("+i+")'> or " +
            					"<input type='button' class='femp dbButtons-class second' value='Copy from' onclick='copyFemp("+i+")'>" +
            					"<select class='femp select-class' id='femp-select"+i+"'>"+addDBSelect(i)+"</select></td>"+
            			"<td><img src='../resources/images/check.png' alt='dataset status' style='width: 1.2em;margin-left: 0.3em;' class='femp'></td></tr>"+
            			"<tr><td><input type='button' class='update dbButtons-class no_css single' value='Run Update Queries' style='background-color: rgba(0, 111, 128, 0.21);' onclick='runUpdates("+i+")'></td>"+
            			"<td><img src='../resources/images/check.png' alt='dataset status' style='width: 1.2em;margin-left: 0.3em;' class='update'></td></tr>"+
            			"<tr><td><input type='button' class='activate dbButtons-class no_css single' value='Activate Database' style='background-color: rgba(4, 128, 0, 0.48);' onclick='activeDeactive("+i+")'></td>"+
            			"<td><img src='../resources/images/check.png' alt='dataset status' style='width: 1.2em;margin-left: 0.3em;' class='activate'></td></tr>"+
            			"<tr><td><input type='button' class='modify dbButtons-class no_css single' value='Modify Database Information' style='background-color:rgba(128, 87, 0, 0.46)' onclick='addModifyDB("+i+", "+dbInfo[i][0]+")'></td>"+
            			"<td></td></tr>"+
            			"<tr><td><input type='button' class='delete dbButtons-class no_css single' value='Delete Database' style='background-color:rgba(205, 10, 10, 0.65)' onclick='deleteDb("+dbInfo[i][0]+")'></td>"+
            			"<td></td></tr>"+
            			"</table></div></td>";
            	
            	html += "</tr></table></div><br><br>";
            	
            	
            	
        	}	
        	html += "</div>";
        	$('#dbList').html(html);
        	
        }
     });
	callDBfuntions(checkDBStatus);
	callDBfuntions(setButtonStatus);
	
	$( "#dbAccordion" ).accordion({
      collapsible: true,
      active : false
    });

	dialog = $( "#dialog-form" ).dialog({
	      autoOpen: false,
	      height: 630,
	      width: 500,
	      modal: true,
	      buttons: {
	        "Submit": dSubmit,
	        Cancel: function() {
	          dialog.dialog( "close" );
	        }
	      },
	      close: function() {
	        form[ 0 ].reset();
	      }
	 });
	
	//gtfs dialog
	deleteUploadedGTFS();
	
	'use strict';
	$('#gtfs_upload_form').fileupload({
        url: '/TNAtoolAPI-Webapp/admin',
        acceptFileTypes: /(zip)$/i,
        singleFileUploads: true,
//        formData: {user: username},
        sequentialUploads: true,
        maxFileSize: 50000000,
    }).bind('fileuploadalways', function (e, data){
//    	addFeed(data.files[0].name);
    	currentFiles.push(data.files[0].name);
    }).bind('fileuploadstopped', function (e) {
//    	alert();
    	addFeed(currentFiles);
    }).bind('fileuploadstart', function (e) {
    	currentFiles = [];
    	deleteProcessGTFS();
    });
	$('#gtfs_upload_form > div').css('margin-right','0px');
	
	/*$('#gtfs_upload_form').addClass('fileupload-processing');
    $.ajax({
        url: $('#fileupload').fileupload('option', 'url'),
        dataType: 'json',
        context: $('#fileupload')[0]
    }).always(function () {
        $(this).removeClass('fileupload-processing');
    }).done(function (result) {
        $(this).fileupload('option', 'done')
            .call(this, $.Event('done'), {result: result});
    });*/
	
	GTFSdialog = $( "#gtfs_upload" ).dialog({
	      autoOpen: false,
	      height: $(window).height()*0.9,
	      width: 900,
	      modal: true,
	      buttons: {
//	        "Submit": dSubmit,
	        Close: function() {
	        	GTFSdialog.dialog( "close" );
	        }
	      },
	      close: function() {
	    	  $('#gtfs_upload_form')[0].reset();
	    	  $("table tbody.files").empty();
	    	  deleteUploadedGTFS();
	      }
	 });
	
	
	
	$('body').css('display','');
	$('#dbAccordion .ui-accordion-content').css('height','100%');
	
	
});
				
