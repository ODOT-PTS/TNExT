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
    nProcess = true;
	$('#otherFeedbackMessage').html('<img src="../resources/images/loadingGif.gif" alt="loading" style="width:20px;height:20px">'
									+'Removing the database... Please do not close or refresh the page.');
	otherFeedbackDialog.dialog( "open" );
	
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/deleteDB?&index="+i,
        dataType: "json",
        async: true,
        success: function(data) {
        		$('#otherFeedbackMessage').html(data.DBError);
        		inProcess = false;
            	otherFeedbackDialog.dialog('option', 'buttons', closeButtonReload);
//    	    	alert(data.DBError);
//    	    	location.reload(true);
        }
     });
}

function addModifyDB(j, index, existing){
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
	        		if(existing==1){
	        			addExistingDb();
	        		}else{
	        			addDB();
	        		}
	        		
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

function addExistingDb(){
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
    $.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/addExistingDB?&db="+db,
        dataType: "text",
        async: false,
        success: function(d) {
        	alert(d);
	    	location.reload(true);
        }
    });
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
		if(dbStatus[dbNumber].Census){
			$('#dbButtons'+dbNumber+' input.fpop').prop('disabled', false);
			$('#dbButtons'+dbNumber+' input.region').prop('disabled', false);
			$('#dbButtons'+dbNumber+' input.t6').prop('disabled', false);
			$('#dbButtons'+dbNumber+' input.pnr').prop('disabled', false);
			$('#dbButtons'+dbNumber+' input.emp').prop('disabled', false);
		}else{
			$('#dbButtons'+dbNumber+' input.fpop').prop('disabled', true);
			$('#dbButtons'+dbNumber+' input.region').prop('disabled', true);
			$('#dbButtons'+dbNumber+' input.t6').prop('disabled', true);
			$('#dbButtons'+dbNumber+' input.pnr').prop('disabled', true);
			$('#dbButtons'+dbNumber+' input.emp').prop('disabled', true);
		}
		if(dbStatus[dbNumber].Employment){
			$('#dbButtons'+dbNumber+' input.femp').prop('disabled', false);
		}else{
			$('#dbButtons'+dbNumber+' input.femp').prop('disabled', true);
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
			|| !dbStatus[index].Parknride || !dbStatus[index].Title6 || !dbStatus[index].FutureEmp || !dbStatus[index].FuturePop || !dbStatus[index].Region){
		alert("Complete all the data sources before activating the database.");
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
	nProcess = true;
	$('#otherFeedbackMessage').html('<img src="../resources/images/loadingGif.gif" alt="loading" style="width:20px;height:20px">'
									+'Activating the database... Please do not close or refresh the page.');
	otherFeedbackDialog.dialog( "open" );
	
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/activateDBs?&db="+db,
        dataType: "text",
        async: true,
        success: function(d) {
        	$('#otherFeedbackMessage').html("Database #"+index+" is activated.");
    		inProcess = false;
        	otherFeedbackDialog.dialog('option', 'buttons', closeButtonReload);
//        	alert("Database #"+index+" is activated.");
//        	location.reload(true);
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
	nProcess = true;
	$('#otherFeedbackMessage').html('<img src="../resources/images/loadingGif.gif" alt="loading" style="width:20px;height:20px">'
									+'Deactivating the database... Please do not close or refresh the page.');
	otherFeedbackDialog.dialog( "open" );
	
	index --;
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/deactivateDBs?&db="+db+"&index="+index,
        dataType: "text",
        async: true,
        success: function(d) {
        	index++;
        	$('#otherFeedbackMessage').html("Database #"+index+" is deactivated.");
    		inProcess = false;
        	otherFeedbackDialog.dialog('option', 'buttons', closeButtonReload);
//        	alert("Database #"+index+" is deactivated.");
//        	location.reload(true);
        }
	});
}

/**
 * Changes DB status.
 */
function changeStatus(index, field, b){
//	if(updateBool==undefined || updateBool==null){
//		updateBool=false;
//	}
	var db = dbInfo[index].toString();
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/changeDBStatus?&db="+db+"&field="+field+"&b="+b,
        dataType: "text",
        async: false,
        success: function(d) {
        	callDBfuntions(checkDBStatus);
        	callDBfuntions(setButtonStatus);
//        	if(updateBool){
//        		return;
//        	}
        	
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
 * GTFS add/remove.
 */
function openGTFS(index){
	var db = dbInfo[index].toString();
	
	currentINDEX = index;
	
	$( "#gtfsNotes" ).accordion({
	      collapsible: true,
	      active : false
	    });
	$('#gtfsNotes .ui-accordion-content').css('height','100%');
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
	
}

function fillSelectStateDialog(d){
	var html = "";
	for(var i=0; i<d.states.length;i++){
		html+="<option value='"+d.stateids[i]+"'>"+d.states[i]+"</option>";
	}
	$('#selectStateSelector').html(html);
}

function deletenext(i,db){
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/deletefeed?&feedname="+currentFiles[i]+"&db="+db,
        dataType: "text",
        async: true,
        success: function(d) {
        	var f = d.split("%%")[0];
        	d = d.split("%%")[1];
        	if(d=="done"){
        		$('#del_'+f).html("Successfully removed").css('color','green');
        	}else{
        		$('#del_'+f).html("Could not be removed. Error: "+d).css('color','red');
        	}
        	gtfsIndex++;
        	if(gtfsIndex<currentFiles.length){
        		deletenext(gtfsIndex,db);
        	}else{
//        		gtfsFeedbackDialog.dialog( "close" );
        		$('#gtfsFeedbackMessage').html("Completed.");
        		gtfsFeedbackDialog.dialog('option', 'buttons', closeButton);
        		inProcess = false;
        		$('#agencyList').html(listOfAgencies(currentINDEX));
        		checkGTFSstatus(currentINDEX);
        	}
        }
	});
}

function deleteFeed(index){
	inProcess = true;
	var db = dbInfo[index].toString();
	indexx = 1001+index;
	currentFiles = [];
	
	$('.selectFeed'+indexx).each(function(){
		if($(this).is(':checked')){
			currentFiles.push($(this).attr('id'));
		}
	});
	
	if(currentFiles.length>0){
		$('#gtfsFeedbackMessage').html('<img src="../resources/images/loadingGif.gif" alt="loading" style="width:20px;height:20px">'
				+"Deleting GTFS Feeds... Please do not close or refresh the page.");
		var html='<tr style="font-size:bold"><th style="width:30%; text-align:center">GTFS Feed Name</th><th style="width:70%; text-align:center">Status</th></tr>';
		for(var i=0; i<currentFiles.length; i++){
			html+="<tr><td style='text-align:center'>"+currentFiles[i]+"</td><td id='del_"+currentFiles[i]+"' style='text-align:center'>In progress..</td></tr>";
		}
		$('#gtfsProgressTable').html( html );
		gtfsFeedbackDialog.dialog( "open" );
//		setTimeout(function(){
//			gtfsIndex=0;
//			deletenext(gtfsIndex,db);
//		}, 2000);
		gtfsIndex=0;
		deletenext(gtfsIndex,db);
	}
	
}

function addnext(i,db){
	
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/addfeed?&feedname="+currentFiles[i]+"&feedsize="+currentSizes[i]+"&db="+db,
        dataType: "text",
        async: true,
        success: function(d) {
        	var f = d.split("%%")[0];
        	d = d.split("%%")[1];
        	if(d=="done"){
//        		console.log(feeds[i]+" was successfully added");
        		$('#'+f).html("Successfully added").css('color','green');
        	}else{
//        		console.log(feeds[i]+" could not be added. Error: "+d);
        		$('#'+f).html("Could not be added. Error: "+d).css('color','red');
        	}
        	gtfsIndex++;
        	if(gtfsIndex<currentFiles.length){
        		addnext(gtfsIndex,db);
        	}else{
//        		gtfsFeedbackDialog.dialog( "close" );
        		$('#gtfsFeedbackMessage').html("Completed.");
        		gtfsFeedbackDialog.dialog('option', 'buttons', closeButton);
        		inProcess = false;
        		checkGTFSstatus(currentINDEX);
        	}
        	
        }
	});
}

function addFeed(){
	var largeFeed = false;
	for(var i=0;i<currentSizes.length;i++){
		if(currentSizes[i]>5000000){
			largeFeed=true;
		}
	}
	if(currentFiles.length>0){
		inProcess = true;
		var html = '<img src="../resources/images/loadingGif.gif" alt="loading" style="width:20px;height:20px">'
			+"Uploading GTFS Feeds... Please do not close or refresh the page.";
		if(largeFeed){
			html+="<br><br>One (or more) of the selected feeds is larger than 5MB.<br>This process can take up to an hour.";
		}
		$('#gtfsFeedbackMessage').html(html);
		html='<tr style="font-size:bold"><th style="width:30%; text-align:center">GTFS Feed Name</th><th style="width:70%; text-align:center">Status</th></tr>';
		for(var i=0; i<currentFiles.length; i++){
			html+="<tr><td style='text-align:center'>"+currentFiles[i]+"</td><td id='"+currentFiles[i].split(".")[0]+"' style='text-align:center'>In progress..</td></tr>";
		}
		$('#gtfsProgressTable').html( html );
		gtfsFeedbackDialog.dialog( "open" );
		var db = dbInfo[currentINDEX].toString();
		gtfsIndex=0;
		addnext(gtfsIndex,db);
	}
}

function checkGTFSstatus(index){
	var db = dbInfo[index].toString();
	
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/checkGTFSstatus?&db="+db,
        dataType: "text",
        async: false,
        success: function(b) {
        	if(b=="true"){
        		b = true;
        	}else{
        		b = false;
        	}
        	if(dbStatus[index].GtfsFeeds!=b){
        		changeStatus(index, "gtfs_feeds", b);
        	}
        	checkUpdatestatus(index);
//        	runUpdates(index);
        	/*if(!b){
        		if(dbStatus[index].Updated){
        			changeStatus(index, "update_process", false);
        		}
        	}*/
        }
	});
}

/**
 * Runs Update Queries.
 */
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
function updatenext(i,db){
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/updateNext?&feed="+currentFiles[i]+"&db="+db+"&agency="+currentAgencies[i]+"&username="+"admin",
        dataType: "text",
        async: true,
        success: function(d) {
        	var f = d.split("%%")[0];
        	d = d.split("%%")[1];
        	if(d=="done"){
        		$('#'+f).html("Successfully updated").css('color','green');
        	}else{
        		$('#'+f).html("Could not be updated. Error: "+d).css('color','red');
        	}
        	gtfsIndex++;
        	if(gtfsIndex<currentFiles.length){
        		updatenext(gtfsIndex,db);
        	}else{
//        		gtfsFeedbackDialog.dialog( "close" );
        		$('#gtfsFeedbackMessage').html("Completed.");
        		gtfsFeedbackDialog.dialog('option', 'buttons', closeButton);
        		inProcess = false;
        		checkUpdatestatus(currentINDEX);
        	}
        }
	});
}

function runUpdates(index){
	var db = dbInfo[index].toString();
	currentINDEX = index;
	
	$.ajax({
	    type: "GET",
	    url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/updateFeeds?&db="+db+"&username=admin",
	    dataType: "json",
	    async: false,
	    success: function(b) {
	    	var largeFeed = false;
	    	var sizes = b.sizes;
	    	currentFiles = b.feeds;
	    	currentAgencies = b.agencies;
	    	for(var i=0;i<sizes.length;i++){
	    		if(sizes[i]>5000000){
	    			largeFeed = true;
	    		}
	    	}
	    	if(currentAgencies.length>0){
	    		inProcess = true;
	    		var html = '<img src="../resources/images/loadingGif.gif" alt="loading" style="width:20px;height:20px">'
	    			+"Updating GTFS Feeds... Please do not close or refresh the page.";
	    		if(largeFeed){
	    			html+="<br><br>One (or more) of the selected feeds is larger than 5MB.<br>This process can take up to a few hours.";
	    		}
	    		$('#gtfsFeedbackMessage').html(html);
	    		html='<tr style="font-size:bold"><th style="width:30%; text-align:center">Agency ID</th><th style="width:70%; text-align:center">Status</th></tr>';
	    		for(var i=0; i<currentAgencies.length; i++){
	    			html+="<tr><td style='text-align:center'>"+currentAgencies[i]+"</td><td id='"+currentAgencies[i]+"' style='text-align:center'>In progress..</td></tr>";
	    		}
	    		$('#gtfsProgressTable').html( html );
	    		gtfsFeedbackDialog.dialog( "open" );
	    		gtfsIndex=0;
		    	updatenext(gtfsIndex,db);
	    	}
	    	
	    	
	    }
	});
}

function checkUpdatestatus(index){
	var db = dbInfo[index].toString();
	
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/checkUpdatestatus?&db="+db,
        dataType: "text",
        async: false,
        success: function(b) {
        	if(b=="true"){
        		b = true;
        	}else{
        		b = false;
        	}
        	if(dbStatus[index].Updated!=b){
        		changeStatus(index, "update_process", b);
        	}
        }
	});
}

/**
 * Opens Future Population dialog.
 */
function openFpop(index){
	var db = dbInfo[index].toString();
	var status = dbStatus[index];
	currentINDEX = index;
	$( "#fpopNotes" ).accordion({
	      collapsible: true,
	      active : false
	    });
	$('#fpopNotes .ui-accordion-content').css('height','100%');
//	if(!status.FuturePop){
//		$('#deletefPop').prop('disabled', true);
//	}
	checkFpopstatus(currentINDEX);
	FPOPdialog.dialog( "open" );
//	var b = status.FuturePop;
//	changeStatus(index, "future_pop", !b);
}

function checkFpopstatus(index){
	var db = dbInfo[index].toString();
	var stateids = [];
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/getImportedStates?&db="+db, 
        dataType: "json",
        async: false,
        success: function(d) {
        	currentImported = d.stateids;
        	fillSelectStateDialog(d);
        }
	});
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/checkFpopstatus?&db="+db,
        dataType: "json",
        async: false,
        success: function(d) {
        	var b = false;
        	stateids = d.stateids;
        	var html = "";
        	$.each(d.states, function(i,item){
        		html+="<tr><td></td><td>"+item+" ("+stateids[i]+")</td></tr>";
        		html+="<tr><td></td><td>Notes: "+d.metadata[i]+"</td></tr>";
        	});
        	$('#importedstatesfpop').html(html);
//        	if(b=="true"){
//        		b = true;
//        	}else{
//        		b = false;
//        	}
        	if(stateids!=undefined||stateids!=null){
        		if(stateids.sort().join(',')== currentImported.sort().join(',')){
            	    b = true;
            	}
        	}
        	
        	if(dbStatus[index].FuturePop!=b){
        		changeStatus(index, "future_pop", b);
        	}
        	
        }
	});
}

function addfPop(){
	
//	var stateid = stateSelector;
//	var metadata = prompt("Please add a note (e.g. Prepared by PSU, August 2016)");
	var db = dbInfo[currentINDEX].toString();
	inProcess = true;
	$('#otherFeedbackMessage').html('<img src="../resources/images/loadingGif.gif" alt="loading" style="width:20px;height:20px">'
									+'Uploading Population Projection data... Please do not close or refresh the page.'
									+'<br>This process takes up to a few minutes.');
	otherFeedbackDialog.dialog( "open" );
	
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/addfPop?&db="+db+"&stateid="+stateSelector+"&metadata="+stateMetaData, 
        dataType: "text",
        async: true,
        success: function(d) {
        	if(d=="done"){
        		$('#otherFeedbackMessage').html("Population Projecion data was successfully added");
//        		$('#deletefPop').prop('disabled', false);
        	}else{
        		$('#otherFeedbackMessage').html("Population Projection data could not be added. Error: "+d);
        	}
        	inProcess = false;
        	otherFeedbackDialog.dialog('option', 'buttons', closeButton);
        	checkFpopstatus(currentINDEX);
        }
	});
	
	
}
function deletefPop(){
	var db = dbInfo[currentINDEX].toString();
	inProcess = true;
	$('#otherFeedbackMessage').html('<img src="../resources/images/loadingGif.gif" alt="loading" style="width:20px;height:20px">'
									+'Deleting Population Projection data... Please do not close or refresh the page.'
									+'<br>This process takes up to a few minutes.');
	otherFeedbackDialog.dialog( "open" );
	
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/deletefPop?&db="+db,
        dataType: "text",
        async: false,
        success: function(b) {
        	if(b = "done"){
        		$('#otherFeedbackMessage').html("Population Projection data was successfully removed.");
//        		changeStatus(currentINDEX, "future_pop", false);
//            	$('#deletefPop').prop('disabled', true);
        	}else{
        		$('#otherFeedbackMessage').html("Population Projection data could not be removed. Error: "+b);
        	}
        	checkFpopstatus(currentINDEX);
        	inProcess = false;
        	otherFeedbackDialog.dialog('option', 'buttons', closeButton);
        	
        }
	});
}


/**
 * Opens Future Employment dialog.
 */
function openFemp(index){
	var db = dbInfo[index].toString();
	var status = dbStatus[index];
	currentINDEX = index;
	$( "#fempNotes" ).accordion({
	      collapsible: true,
	      active : false
	    });
	$('#fempNotes .ui-accordion-content').css('height','100%');
//	if(!status.FutureEmp){
//		$('#deletefEmp').prop('disabled', true);
//	}
	checkfEmpstatus(currentINDEX);
	FEMPdialog.dialog( "open" );
}
function addfEmp(){
//	var stateid = stateSelector;
//	var metadata = prompt("Please add a note (e.g. Prepared by ODOT, March 2016)");
	var db = dbInfo[currentINDEX].toString();
	inProcess = true;
	$('#otherFeedbackMessage').html('<img src="../resources/images/loadingGif.gif" alt="loading" style="width:20px;height:20px">'
									+'Uploading Future Employment data... Please do not close or refresh the page.'
									+'<br>This process takes up to a few minutes.');
	otherFeedbackDialog.dialog( "open" );
	
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/addfEmp?&db="+db+"&stateid="+stateSelector+"&metadata="+stateMetaData, 
        dataType: "text",
        async: true,
        success: function(d) {
        	if(d=="done"){
        		$('#otherFeedbackMessage').html("Employment Projecion data was successfully added");
//        		$('#deletefEmp').prop('disabled', false);
        	}else{
        		$('#otherFeedbackMessage').html("Future Employment data could not be added. Error: "+d);
        	}
        	inProcess = false;
        	otherFeedbackDialog.dialog('option', 'buttons', closeButton);
        	checkfEmpstatus(currentINDEX);
        }
	});
	
	
}

function checkfEmpstatus(index){
	var db = dbInfo[index].toString();
	var stateids = [];
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/getImportedStates?&db="+db, 
        dataType: "json",
        async: false,
        success: function(d) {
        	currentImported = d.stateids;
        	fillSelectStateDialog(d);
        }
	});
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/checkfEmpstatus?&db="+db,
        dataType: "json",
        async: false,
        success: function(d) {
        	var b = false;
        	stateids = d.stateids;
        	
        	var html = "";
        	$.each(d.states, function(i,item){
        		html+="<tr><td><input type='button' class='btn btn-danger delete' value='Delete' onclick='removeFemp(\""+stateids[i]+"\")'></td><td>"+item+" ("+stateids[i]+")</td></tr>";
        		html+="<tr><td></td><td>Notes: "+d.metadata[i]+"</td></tr>";
        	});
        	$('#importedstatesfemp').html(html);

        	if(stateids!=undefined||stateids!=null){
        		if(stateids.sort().join(',')== currentImported.sort().join(',')){
            	    b = true;
            	}
        	}
        	
        	if(dbStatus[index].FutureEmp!=b){
        		changeStatus(index, "future_emp", b);
        	}
        	
        }
	});
}

//function deletefEmp(){
function removeFemp(stateid){
	var db = dbInfo[currentINDEX].toString();
	inProcess = true;
	$('#otherFeedbackMessage').html('<img src="../resources/images/loadingGif.gif" alt="loading" style="width:20px;height:20px">'
									+'Deleting Future Employment data... Please do not close or refresh the page.'
									+'<br>This process takes up to a few minutes.');
	otherFeedbackDialog.dialog( "open" );
	
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/deletefEmp?&stateid="+stateid+"&db="+db,
        dataType: "text",
        async: true,
        success: function(b) {
        	if(b = "done"){
        		$('#otherFeedbackMessage').html("Future Employment data was successfully removed.");
//        		changeStatus(currentINDEX, "future_emp", false);
//            	$('#deletefEmp').prop('disabled', true);
        	}else{
        		$('#otherFeedbackMessage').html("Future Employment data could not be removed. Error: "+b);
        	}
        	checkfEmpstatus(currentINDEX);
        	inProcess = false;
        	otherFeedbackDialog.dialog('option', 'buttons', closeButton);
        	
        }
	});
}


/**
 * Title 6 add/remove.
 */
function addT6(){
//	var stateid = stateSelector;
//	var metadata = prompt("Please add a note (e.g. 2014 5-year summary file)");
	var db = dbInfo[currentINDEX].toString();
	inProcess = true;
	$('#otherFeedbackMessage').html('<img src="../resources/images/loadingGif.gif" alt="loading" style="width:20px;height:20px">'
									+'Uploading Title VI data... Please do not close or refresh the page.'
									+'<br>This process takes up to a few minutes.');
	otherFeedbackDialog.dialog( "open" );
	
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/addT6?&db="+db+"&stateid="+stateSelector+"&metadata="+stateMetaData, 
        dataType: "text",
        async: true,
        success: function(d) {
        	if(d=="done"){
        		$('#otherFeedbackMessage').html("Title VI data was successfully added");
//        		$('#deleteT6').prop('disabled', false);
        	}else{
        		$('#otherFeedbackMessage').html("Title VI data could not be added. Error: "+d);
        	}
        	inProcess = false;
        	otherFeedbackDialog.dialog('option', 'buttons', closeButton);
        	checkT6status(currentINDEX);
        }
	});
	
	
}
function openT6(index){
	var db = dbInfo[index].toString();
	var status = dbStatus[index];
	currentINDEX = index;
	$( "#t6Notes" ).accordion({
	      collapsible: true,
	      active : false
	    });
	$('#t6Notes .ui-accordion-content').css('height','100%');
//	if(!status.Parknride){
//		$('#deleteT6').prop('disabled', true);
//	}
	checkT6status(currentINDEX);
	T6dialog.dialog( "open" );
	
//	var b = status.Title6;
//	changeStatus(index, "title6", !b);
	
}

function checkT6status(index){
	var db = dbInfo[index].toString();
	var stateids = [];
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/getImportedStates?&db="+db, 
        dataType: "json",
        async: false,
        success: function(d) {
        	currentImported = d.stateids;
        	fillSelectStateDialog(d);
        }
	});
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/checkT6status?&db="+db,
        dataType: "json",
        async: false,
        success: function(d) {
        	var b = false;
        	stateids = d.stateids;
        	var html = "";
        	$.each(d.states, function(i,item){
        		html+="<tr><td><input type='button' class='btn btn-danger delete' value='Delete' onclick='removeT6(\""+stateids[i]+"\")'></td><td>"+item+" ("+stateids[i]+")</td></tr>";
        		html+="<tr><td></td><td>Notes: "+d.metadata[i]+"</td></tr>";
        	});
        	$('#importedstatest6').html(html);
        	if(stateids!=undefined||stateids!=null){
        		if(stateids.sort().join(',')== currentImported.sort().join(',')){
            	    b = true;
            	}
        	}
        	
        	if(dbStatus[index].Title6!=b){
        		changeStatus(index, "title6", b);
        	}
        	
        }
	});
}

//function deleteT6(){
function removeT6(stateid){
	var db = dbInfo[currentINDEX].toString();
	inProcess = true;
	$('#otherFeedbackMessage').html('<img src="../resources/images/loadingGif.gif" alt="loading" style="width:20px;height:20px">'
									+'Deleting Title VI data... Please do not close or refresh the page.'
									+'<br>This process takes up to a few minutes.');
	otherFeedbackDialog.dialog( "open" );
	
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/deleteT6?&stateid="+stateid+"&db="+db,
        dataType: "text",
        async: true,
        success: function(b) {
        	if(b = "done"){
        		$('#otherFeedbackMessage').html("Title VI data was successfully removed.");
//        		changeStatus(currentINDEX, "title6", false);
//            	$('#deleteT6').prop('disabled', true);
        	}else{
        		$('#otherFeedbackMessage').html("Title VI data could not be removed. Error: "+b);
        	}
        	checkT6status(currentINDEX);
        	inProcess = false;
        	otherFeedbackDialog.dialog('option', 'buttons', closeButton);
        	
        }
	});
}

/**
 * Opens PnR dialog.
 */
function openPnr(index){
	var db = dbInfo[index].toString();
	var status = dbStatus[index];
	currentINDEX = index;
	$( "#pnrNotes" ).accordion({
	      collapsible: true,
	      active : false
	    });
	$('#pnrNotes .ui-accordion-content').css('height','100%');
//	if(!status.Parknride){
//		$('#deletePNR').prop('disabled', true);
//	}
	checkPNRstatus(currentINDEX);
	PNRdialog.dialog( "open" );
	
//	var db = dbInfo[index].toString();
//	var status = dbStatus[index];
//	var b = status.Parknride;
//	changeStatus(index, "parknride", !b);
}

function addPnr(){
//	var stateid = stateSelector;
//	var metadata = prompt("Please add a note (e.g. Prepared in January 2017)");
	var db = dbInfo[currentINDEX].toString();
	inProcess = true;
	$('#otherFeedbackMessage').html('<img src="../resources/images/loadingGif.gif" alt="loading" style="width:20px;height:20px">'
									+'Uploading Park & Ride data... Please do not close or refresh the page.'
									+'<br>This process takes up to a few minutes.');
	otherFeedbackDialog.dialog( "open" );
	
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/addPnr?&fileName="+pnrFile+"&db="+db+"&stateid="+stateSelector+"&metadata="+stateMetaData,
        dataType: "text",
        async: true,
        success: function(d) {
        	if(d=="done"){
        		$('#otherFeedbackMessage').html("Park & Ride data was successfully added");
//        		$('#deletePNR').prop('disabled', false);
        	}else{
        		$('#otherFeedbackMessage').html("Park & Ride data could not be added. Error: "+d);
        	}
        	
        	inProcess = false;
        	otherFeedbackDialog.dialog('option', 'buttons', closeButton);
        	checkPNRstatus(currentINDEX);
        }
	});
	
}


//function deletePNR(){
function removePnr(stateid){
	var db = dbInfo[currentINDEX].toString();
	inProcess = true;
	$('#otherFeedbackMessage').html('<img src="../resources/images/loadingGif.gif" alt="loading" style="width:20px;height:20px">'
									+'Deleting Park & Ride data... Please do not close or refresh the page.'
									+'<br>This process takes up to a few minutes.');
	otherFeedbackDialog.dialog( "open" );
	
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/deletePNR?&stateid="+stateid+"&db="+db,
        dataType: "text",
        async: true,
        success: function(b) {
        	if(b = "done"){
        		$('#otherFeedbackMessage').html("Park & Ride data was successfully removed.");
//        		changeStatus(currentINDEX, "parknride", false);
//            	$('#deletePNR').prop('disabled', true);
        	}else{
        		$('#otherFeedbackMessage').html("Park & Ride data could not be removed. Error: "+b);
        	}
        	checkPNRstatus(currentINDEX);
        	inProcess = false;
        	otherFeedbackDialog.dialog('option', 'buttons', closeButton);
        	
        }
	});
}


function checkPNRstatus(index){
	var db = dbInfo[index].toString();
	var stateids = [];
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/getImportedStates?&db="+db, 
        dataType: "json",
        async: false,
        success: function(d) {
        	currentImported = d.stateids;
        	fillSelectStateDialog(d);
        }
	});
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/checkPNRstatus?&db="+db,
        dataType: "json",
        async: false,
        success: function(d) {
        	var b = false;
        	stateids = d.stateids;
        	var html = "";
        	$.each(d.states, function(i,item){
        		html+="<tr><td><input type='button' class='btn btn-danger delete' value='Delete' onclick='removePnr(\""+stateids[i]+"\")'></td><td>"+item+" ("+stateids[i]+")</td></tr>";
        		html+="<tr><td></td><td>Notes: "+d.metadata[i]+"</td></tr>";
        	});
        	$('#importedstatespnr').html(html);
        	if(stateids!=undefined||stateids!=null){
        		if(stateids.sort().join(',')== currentImported.sort().join(',')){
            	    b = true;
            	}
        	}
        	
        	if(dbStatus[index].Parknride!=b){
        		changeStatus(index, "parknride", b);
        	}
        	
        }
	});
}




/**
 * Opens Employment dialog.
 */
function openEmp(index){
	var db = dbInfo[index].toString();
	var status = dbStatus[index];
	currentINDEX = index;
	$( "#empNotes" ).accordion({
	      collapsible: true,
	      active : false
	    });
	$('#empNotes .ui-accordion-content').css('height','100%');
//	if(!status.Employment){
//		$('#deleteEmp').prop('disabled', true);
//	}
	checkEmpstatus(currentINDEX);
	EMPdialog.dialog( "open" );
//	var b = status.Employment;
//	changeStatus(index, "employment", !b);
	
}
function addEmp(){
//	var stateid = stateSelector;
//	var metadata = prompt("Please add a note (e.g. Census employment Data 2014)");
	var db = dbInfo[currentINDEX].toString();
	inProcess = true;
	$('#otherFeedbackMessage').html('<img src="../resources/images/loadingGif.gif" alt="loading" style="width:20px;height:20px">'
									+'Uploading Employment data... Please do not close or refresh the page.'
									+'<br>This process takes up to a few minutes.');
	otherFeedbackDialog.dialog( "open" );
	
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/addEmp?&db="+db+"&stateid="+stateSelector+"&metadata="+stateMetaData, 
        dataType: "text",
        async: true,
        success: function(d) {
        	if(d=="done"){
        		$('#otherFeedbackMessage').html("Employment data was successfully added");
//        		$('#deleteEmp').prop('disabled', false);
        	}else{
        		$('#otherFeedbackMessage').html("Employment data could not be added. Error: "+d);
        	}
        	inProcess = false;
        	otherFeedbackDialog.dialog('option', 'buttons', closeButton);
        	checkEmpstatus(currentINDEX);
        }
	});
	
	
}

//function deleteEmp(){
function removeWac(stateid){
	var db = dbInfo[currentINDEX].toString();
	inProcess = true;
	$('#otherFeedbackMessage').html('<img src="../resources/images/loadingGif.gif" alt="loading" style="width:20px;height:20px">'
									+'Deleting Employment data... Please do not close or refresh the page.'
									+'<br>This process takes up to a few minutes.');
	otherFeedbackDialog.dialog( "open" );
	
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/deleteEmpWac?&stateid="+stateid+"&db="+db,
        dataType: "text",
        async: true,
        success: function(b) {
        	if(b = "done"){
        		$('#otherFeedbackMessage').html("WAC Employment data was successfully removed.");
//        		changeStatus(currentINDEX, "employment", false);
//            	$('#deleteEmp').prop('disabled', true);
        	}else{
        		$('#otherFeedbackMessage').html("WAC Employment data could not be removed. Error: "+b);
        	}
        	checkEmpstatus(currentINDEX);
        	inProcess = false;
        	otherFeedbackDialog.dialog('option', 'buttons', closeButton);
        	
        	
        }
	});
}

function removeRac(stateid){
	var db = dbInfo[currentINDEX].toString();
	inProcess = true;
	$('#otherFeedbackMessage').html('<img src="../resources/images/loadingGif.gif" alt="loading" style="width:20px;height:20px">'
									+'Deleting Employment data... Please do not close or refresh the page.'
									+'<br>This process takes up to a few minutes.');
	otherFeedbackDialog.dialog( "open" );
	
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/deleteEmpRac?&stateid="+stateid+"&db="+db,
        dataType: "text",
        async: true,
        success: function(b) {
        	if(b = "done"){
        		$('#otherFeedbackMessage').html("RAC Employment data was successfully removed.");
//        		changeStatus(currentINDEX, "employment", false);
//            	$('#deleteEmp').prop('disabled', true);
        	}else{
        		$('#otherFeedbackMessage').html("RAC Employment data could not be removed. Error: "+b);
        	}
        	checkEmpstatus(currentINDEX);
        	inProcess = false;
        	otherFeedbackDialog.dialog('option', 'buttons', closeButton);
        	
        	
        }
	});
}


function checkEmpstatus(index){
	var db = dbInfo[index].toString();
	var rac = false;
	var wac = false;
	var stateids = [];
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/getImportedStates?&db="+db, 
        dataType: "json",
        async: false,
        success: function(d) {
        	currentImported = d.stateids;
        	fillSelectStateDialog(d);
        }
	});
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/checkEmpstatus?&db="+db,
        dataType: "json",
        async: false,
        success: function(d) {
        	var b = false;
        	stateids = d.stateids;
        	var html = "";
        	$.each(d.states, function(i,item){
        		html+="<tr><td><input type='button' class='btn btn-danger delete' value='Delete' onclick='removeWac(\""+stateids[i]+"\")'></td><td>"+item+" ("+stateids[i]+")</td></tr>";
        		html+="<tr><td></td><td>Notes: "+d.metadata[i]+"</td></tr>";
        	});
        	$('#importedstateswac').html(html);
        	if(stateids!=undefined||stateids!=null){
        		if(stateids.sort().join(',')== currentImported.sort().join(',')){
            	    wac = true;
            	}
        	}
        	
        	
        	stateids = d.agencies;
        	html = "";
        	$.each(d.feeds, function(i,item){
        		html+="<tr><td><input type='button' class='btn btn-danger delete' value='Delete' onclick='removeRac(\""+stateids[i]+"\")'></td><td>"+item+" ("+stateids[i]+")</td></tr>";
        		html+="<tr><td></td><td>Notes: "+d.sizes[i]+"</td></tr>";
        	});
        	$('#importedstatesrac').html(html);
        	
        	if(stateids!=undefined||stateids!=null){
        		if(stateids.sort().join(',')== currentImported.sort().join(',')){
            	    rac = true;
            	}
        	}
        	   
        	
        	b = wac&&rac;
        	if(dbStatus[index].Employment!=b){
        		changeStatus(index, "employment", b);
        	}
        	
        }
	});
}

/**
* Opens Region dialog.
*/
function openRegion(index){
	var db = dbInfo[index].toString();
	var status = dbStatus[index];
	currentINDEX = index;
	$( "#regionNotes" ).accordion({
	      collapsible: true,
	      active : false
	    });
	$('#regionNotes .ui-accordion-content').css('height','100%');

	checkRegionstatus(currentINDEX);
	REGIONdialog.dialog( "open" );
}

function checkRegionstatus(index){
	var db = dbInfo[index].toString();
	var stateids = [];
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/getImportedStates?&db="+db, 
        dataType: "json",
        async: false,
        success: function(d) {
        	currentImported = d.stateids;
        	fillSelectStateDialog(d);
        }
	});
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/checkRegionstatus?&db="+db,
        dataType: "json",
        async: false,
        success: function(d) {
        	var b = false;
        	stateids = d.stateids;
        	html = "";
        	$.each(d.states, function(i,item){
        		html+="<tr><td></td><td>"+item+" ("+stateids[i]+")</td></tr>";
        		html+="<tr><td></td><td>Notes: "+d.metadata[i]+"</td></tr>";
        	});
        	$('#importedstatesregion').html(html);
        	if(stateids!=undefined||stateids!=null){
        		if(stateids.sort().join(',')== currentImported.sort().join(',')){
            	    b = true;
            	}
        	}
        	
        	if(dbStatus[index].Region!=b){
        		changeStatus(index, "region", b);
        	}
        	
        }
	});
}

function addRegion(){
//	var stateid = stateSelector;
//	var metadata = prompt("Please addstateMetaData a note (e.g. Prepared by ODOT August 2016)");
	var db = dbInfo[currentINDEX].toString();
	inProcess = true;
	$('#otherFeedbackMessage').html('<img src="../resources/images/loadingGif.gif" alt="loading" style="width:20px;height:20px">'
									+'Uploading Region data... Please do not close or refresh the page.'
									+'<br>This process takes up to a few minutes.');
	otherFeedbackDialog.dialog( "open" );
	
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/addRegion?&db="+db+"&stateid="+stateSelector+"&metadata="+stateMetaData, 
        dataType: "text",
        async: true,
        success: function(d) {
        	if(d=="done"){
        		$('#otherFeedbackMessage').html("Region data was successfully added");
        	}else{
        		$('#otherFeedbackMessage').html("Region data could not be added. Error: "+d);
        	}
        	inProcess = false;
        	otherFeedbackDialog.dialog('option', 'buttons', closeButton);
        	checkRegionstatus(currentINDEX);
        }
	});
	
	
}



/**
 * Opens Census dialog.
 */
function openCensus(index){
	var db = dbInfo[index].toString();
	var status = dbStatus[index];
	currentINDEX = index;
	$( "#censusNotes" ).accordion({
	      collapsible: true,
	      active : false
	    });
	$('#censusNotes .ui-accordion-content').css('height','100%');
	fillStates(db);
	
	CENSUSdialog.dialog( "open" );
	
//	var status = dbStatus[index];
//	var b = status.Census;
//	changeStatus(index, "census", !b);
////	if(!b){
////		changeStatus(index, "update_process", false, true);
////	}
}

function removeCensus(state, states){
	var db = dbInfo[currentINDEX].toString();
	inProcess = true;
	$('#otherFeedbackMessage').html('<img src="../resources/images/loadingGif.gif" alt="loading" style="width:20px;height:20px">'
			+'Deleting Census population data... Please do not close or refresh the page.'
			+'<br>This process takes up to a few minutes.');
	otherFeedbackDialog.dialog( "open" );
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/removeCensus?&stateid="+state+"&states="+states+"&db="+db, 
        dataType: "text",
        async: true,
        success: function(d) {
        	if(d=="done"){
        		$('#otherFeedbackMessage').html("Census population data for the state "+state+" was successfully removed.");
        	}else{
        		$('#otherFeedbackMessage').html("Census population data could not be removed. Error: "+d);
        	}
        	inProcess = false;
        	otherFeedbackDialog.dialog('option', 'buttons', closeButton);
        	checkCensusstatus(currentINDEX);
        	checkFpopstatus(currentINDEX);
        	checkRegionstatus(currentINDEX);
        	checkfEmpstatus(currentINDEX);
        	checkEmpstatus(currentINDEX);
        	checkT6status(currentINDEX);
        	checkPNRstatus(currentINDEX);
        	fillStates(db);
        }
	});
}

function fillStates(db){
	var stateids = [];
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/getImportedStates?&db="+db, 
        dataType: "json",
        async: false,
        success: function(d) {
        	currentImported = d.stateids;
        	stateids = d.stateids;
        	html = "";
        	$.each(d.states, function(i,item){
        		html+="<tr><td><input type='button' class='btn btn-danger delete' id='"+stateids[i]+"stateRemove' value='Delete' onclick='removeCensus(\""+stateids[i]+"\",\""+currentImported.join(",")+"\")'></td><td>"+item+" ("+stateids[i]+")</td></tr>";
        		html+="<tr><td></td><td>Notes: "+d.metadata[i]+"</td></tr>";
        	});
        	$('#importedstates').html(html);
        }
	});
	
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/getAvailableStates?&db="+db, 
        dataType: "json",
        async: false,
        success: function(d) {
        	html = "";
        	$.each(d.states, function(i,item){
        		if(!(stateids.indexOf(d.stateids[i]) > -1)){
        			html+="<tr><td><input type='checkbox' value='"+d.stateids[i]+"' class='stateCheckbox"+currentINDEX+"'></td><td>"+item+" ("+d.stateids[i]+")</td></tr>";
        		}
        		
        	});
        	$('#availablestates').html(html);
        }
	
	});
}

function checkCensusstatus(index){
	var db = dbInfo[index].toString();
	
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/checkCensusstatus?&db="+db,
        dataType: "text",
        async: false,
        success: function(b) {
        	if(b=="true"){
        		b = true;
        	}else{
        		b = false;
        	}
        	if(dbStatus[index].Census!=b){
        		changeStatus(index, "census", b);
        	}
        	checkUpdatestatus(index);
//        	runUpdates(index);
        	
        }
	});
}
function openCensusState(){
	$('#stateSelectSpan').hide();
	currentFunction = addCensus;
	stateSelectDialog.dialog( "open" );
	
}
function addCensus(){
//	var metadata = prompt("Please add a note (e.g. Census population 2010)");
	inProcess = true;
	var db = dbInfo[currentINDEX].toString();
	currentStates=[];

	$('.stateCheckbox'+currentINDEX).each(function(){
		if($(this).is(':checked')){
			currentStates.push($(this).val());
		}
	});
	for(var i=0; i<currentImported.length;i++){
		currentStates.push(currentImported[i]);
	}
	if(currentStates.length>0){
		var states = currentStates.join(",");
		$('#otherFeedbackMessage').html('<img src="../resources/images/loadingGif.gif" alt="loading" style="width:20px;height:20px">'
				+'Importing Census population data... Please do not close or refresh the page.'
				+'<br>This process takes up to a few minutes.');
		otherFeedbackDialog.dialog( "open" );
		$.ajax({
	        type: "GET",
	        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/importCensus?&metadata="+stateMetaData+"&stateid="+states+"&db="+db,
	        dataType: "text",
	        async: true,
	        success: function(d) {
	        	if(d=="done"){
	        		$('#otherFeedbackMessage').html("Census population data was successfully added");
	        	}else{
	        		$('#otherFeedbackMessage').html("Census population data could not be added. Error: "+d);
	        	}
	        	inProcess = false;
	        	otherFeedbackDialog.dialog('option', 'buttons', closeButton);
	        	checkCensusstatus(currentINDEX);
	        	checkFpopstatus(currentINDEX);
	        	checkRegionstatus(currentINDEX);
	        	checkfEmpstatus(currentINDEX);
	        	checkEmpstatus(currentINDEX);
	        	checkT6status(currentINDEX);
	        	checkPNRstatus(currentINDEX);
	        	fillStates(db);
	        	
	        }
		});
	}
	

	/*if(currentStates.length>0){
		$('#censusFeedbackMessage').html("Importing Census Population data... Please do not close or refresh the page." 
//				+"This process can take several minutes."
				);
		var html='<tr style="font-size:bold"><th style="width:30%; text-align:center">State Name</th><th style="width:70%; text-align:center">Status</th></tr>';
		for(var i=0; i<currentStates.length; i++){
			html+="<tr><td style='text-align:center'>"+currentStates[i]+"</td><td id='delState_"+currentStates[i]+"' style='text-align:center'>In progress..</td></tr>";
		}
		$('#censusProgressTable').html( html );
		censusFeedbackDialog.dialog( "open" );

		censusIndex=0;
		importnext(censusIndex,db);
	}*/
}

/*function importnext(i,db){
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/importCensus?&stateid="+currentStates[i]+"&db="+db,
        dataType: "text",
        async: true,
        success: function(d) {
        	var f = d.split("%%")[0];
        	d = d.split("%%")[1];
        	if(d=="done"){
        		$('#delState_'+f).html("Successfully imported").css('color','green');
        	}else{
        		$('#delState_'+f).html("Could not be removed. Error: "+d).css('color','red');
        	}
        	censusIndex++;
        	if(censusIndex<currentStates.length){
        		importnext(censusIndex,db);
        	}else{
//        		gtfsFeedbackDialog.dialog( "close" );
        		$('#censusFeedbackMessage').html("Completed.");
        		censusFeedbackDialog.dialog('option', 'buttons', closeButton);
        		inProcess = false;
        		fillStates(db);
        		checkCensusstatus(currentINDEX);
        	}
        }
	});
}*/

function copyCensus(index, section, classs){
//	alert(section);
	var dbTo = dbInfo[index].toString();
	var dbFrom = dbInfo[$("#"+classs+"-select"+index).val()].toString();
	nProcess = true;
	$('#otherFeedbackMessage').html('<img src="../resources/images/loadingGif.gif" alt="loading" style="width:20px;height:20px">'
									+'Copying data... Please do not close or refresh the page.'
									+'<br>This process takes up to a few minutes.');
	otherFeedbackDialog.dialog( "open" );
	
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/copyCensus?&dbFrom="+dbFrom+"&dbTo="+dbTo+"&section="+section,
        dataType: "text",
        async: true,
        success: function(d) {
        	switch(section) {
        	case "census": 
        			checkCensusstatus(index);
        			checkFpopstatus(index);
        			checkRegionstatus(index);
        			checkfEmpstatus(index);
    	        	checkEmpstatus(index);
    	        	checkT6status(index);
    	        	checkPNRstatus(index);
            		break;
		    case "employment": 
		    		checkEmpstatus(index);
 		            break;
		    case "parknride": 
		    		checkPNRstatus(index);
			        break;
		    case "title6": 
		    		checkT6status(index);
		    		break;
		    case "femployment": 
		    		checkfEmpstatus(index);
					break;
			default:
					break;
        	}
        	
        	$('#otherFeedbackMessage').html("Data was successfully copied.");
    		inProcess = false;
        	otherFeedbackDialog.dialog('option', 'buttons', closeButton);
        }
	});
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
	for(var i=dbInfo.length-1;i>0;i--){
		if(i==index){
			continue;
		}
		html+="<option value='"+i+"'>"+dbInfo[i][1]+"</option>";
	}
	return html;
}

var inProcess = false;
var ind;
var gtfsIndex;
var censusIndex;
var currentINDEX;
var currentFiles;
var currentSizes;
var currentAgencies;
var currentStates;
var currentImported;
var dialog;
var GTFSdialog;
var PNRdialog;
var T6dialog;
var CENSUSdialog;
var EMPdialog;
var FEMPdialog;
var FPOPdialog;
var REGIONdialog;
var gtfsFeedbackDialog;
var otherFeedbackDialog;
var stateSelectDialog;
var stateSelector;
var stateMetaData;
var pnrFile;
var currentFunction;
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
var closeButton = {
        "Close": function () {
            $(this).dialog("close");
            $(this).dialog("option", "buttons", {});
        }
    };
var closeButtonReload = {
        "Close": function () {
            $(this).dialog("close");
            $(this).dialog("option", "buttons", {});
            location.reload(true);
        }
    };
var oldName;
var classMap = {"activate":"Activated","gtfs":"GtfsFeeds","census":"Census","emp":"Employment",
                "pnr":"Parknride","t6":"Title6","femp":"FutureEmp","fpop":"FuturePop","region":"Region","update":"Updated"};

window.onbeforeunload = function(event)
{
	if(inProcess){
    	return confirm("Confirm refresh");
	}
};

$(document).ready(function(){
	
	$.ajax({
        type: "GET",
        //url: "../resources/admin/dbInfo.csv",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/readDBinfo",
        dataType: "json",
        async: false,
        success: function(data) {
			// ian: includes header as dbInfo[0]
			for (var i=0; i<data.length; i++) {
				dbInfo[i] = data[i];
			}
        	var html="<div id='dbAccordion'>";
        	for(var i=dbInfo.length-1; i>0; i--){
        		html += "<h3>Database #"+dbInfo[i][0]+": <i>"+dbInfo[i][1]+"</i></h3><div>";
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
            			"<tr><td><input type='button' class='census dbButtons-class' value='Import Population' onclick='openCensus("+i+")'> or " +
            					"<input type='button' class='census dbButtons-class second' value='Copy from' onclick='copyCensus("+i+", \"census\", \"census\")'>" +
            					"<select class='census select-class' id='census-select"+i+"'>"+addDBSelect(i)+"</select></td>"+
            			"<td><img src='../resources/images/check.png' alt='dataset status' style='width: 1.2em;margin-left: 0.3em;' class='census'></td></tr>"+
            			"<tr><td><input type='button' class='emp dbButtons-class' value='Import Employment' onclick='openEmp("+i+")'> or " +
            					"<input type='button' class='emp dbButtons-class second' value='Copy from' onclick='copyCensus("+i+", \"employment\", \"emp\")'>" +
            					"<select class='census select-class' id='emp-select"+i+"'>"+addDBSelect(i)+"</select></td>"+
            			"<td><img src='../resources/images/check.png' alt='dataset status' style='width: 1.2em;margin-left: 0.3em;' class='emp'></td></tr>"+
            			"<tr><td><input type='button' class='pnr dbButtons-class' value='Import Park & Ride' onclick='openPnr("+i+")'> or " +
            					"<input type='button' class='pnr dbButtons-class second' value='Copy from' onclick='copyCensus("+i+", \"parknride\", \"pnr\")'>" +
            					"<select class='pnr select-class' id='pnr-select"+i+"'>"+addDBSelect(i)+"</select></td>"+
            			"<td><img src='../resources/images/check.png' alt='dataset status' style='width: 1.2em;margin-left: 0.3em;' class='pnr'></td></tr>"+
            			"<tr><td><input type='button' class='t6 dbButtons-class' value='Import Title VI' onclick='openT6("+i+")'> or " +
            					"<input type='button' class='t6 dbButtons-class second' value='Copy from' onclick='copyCensus("+i+", \"title6\", \"t6\")'>" +
            					"<select class='t6 select-class' id='t6-select"+i+"'>"+addDBSelect(i)+"</select></td>"+
            			"<td><img src='../resources/images/check.png' alt='dataset status' style='width: 1.2em;margin-left: 0.3em;' class='t6'></td></tr>"+
            			"<tr><td><input type='button' class='fpop dbButtons-class single' value='Import Future Population' onclick='openFpop("+i+")'>" +
            			"<td><img src='../resources/images/check.png' alt='dataset status' style='width: 1.2em;margin-left: 0.3em;' class='fpop'></td></tr>"+
            			"<tr><td><input type='button' class='region dbButtons-class single' value='Import Regions' onclick='openRegion("+i+")'>" +
            			"<td><img src='../resources/images/check.png' alt='dataset status' style='width: 1.2em;margin-left: 0.3em;' class='region'></td></tr>"+
            			"<tr><td><input type='button' class='femp dbButtons-class' value='Import Future Employment' onclick='openFemp("+i+")'> or " +
            					"<input type='button' class='femp dbButtons-class second' value='Copy from' onclick='copyCensus("+i+", \"femployment\", \"femp\")'>" +
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
	
	runGTFSfunctions();
	runCENSUSfunctions();
	runPnRfunctions();
	runT6functions();
	runEMPfunctions();
	runFEMPfunctions();
	runFPOPfunctions();
	runREGIONfunctions();
	
	$('body').css('display','');
	$('#dbAccordion .ui-accordion-content').css('height','100%');
	
	gtfsFeedbackDialog = $( "#gtfsFeedbackDialog" ).dialog({
	      autoOpen: false,
	      height: 630,
	      width: 800,
	      modal: true,	
	      closeOnEscape: false,
	});
	
	
	otherFeedbackDialog = $( "#otherFeedbackDialog" ).dialog({
	      autoOpen: false,
	      height: 250,
	      width: 700,
	      modal: true,	
	      closeOnEscape: false,
	});
	
	stateSelectDialog = $( "#stateSelectDialog" ).dialog({
	      autoOpen: false,
	      height: 250,
	      width: 400,
	      modal: true,	
	      closeOnEscape: false,
	      close: function( event, ui ) {
	    	  stateSelector = $('#selectStateSelector').val();
	    	  stateMetaData = $('#selectStateText').val();
	    	  $('#stateSelectSpan').show();
//	    	  alert(stateSelector);
	    	  currentFunction();
	      },
	      open: function( event, ui ) {
	    	  $('#selectStateText').val("");
	      },
	      buttons: {
		        
		        "Done": function() {
		        	stateSelectDialog.dialog( "close" );
		        	
		        }
		      }
	});
});


function deleteUploadedT6(){
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/deleteUploadedT6",
        dataType: "text",
        async: false,
        success: function(d) {
        	
        }
	});
}

function deleteUploadedEmp(){
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/deleteUploadedEmp",
        dataType: "text",
        async: false,
        success: function(d) {
        	
        }
	});
}

function deleteUploadedfEmp(){
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/deleteUploadedfEmp",
        dataType: "text",
        async: false,
        success: function(d) {
        	
        }
	});
}

function deleteUploadedfPop(){
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/deleteUploadedfPop",
        dataType: "text",
        async: false,
        success: function(d) {
        	
        }
	});
}

function deleteUploadedRegion(){
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/deleteUploadedRegion",
        dataType: "text",
        async: false,
        success: function(d) {
        	
        }
	});
}

function deleteUploadedPNR(){
	$.ajax({
        type: "GET",
        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/deleteUploadedPNR",
        dataType: "text",
        async: false,
        success: function(d) {
        	
        }
	});
}

function runPnRfunctions(){
	deleteUploadedPNR(); 
	
	'use strict';
	$('#pnr_upload_form').fileupload({
        url: '/TNAtoolAPI-Webapp/admin',
        acceptFileTypes: /(csv)$/i,
        singleFileUploads: true,
        formData: {data: "pnr"},
        sequentialUploads: true,
        maxFileSize: 50000000,
    }).bind('fileuploadalways', function (e, data){
//    	addFeed(data.files[0].name);
//    	currentFiles.push(data.files[0].name);
//    	console.log(data);
    	pnrFile = data.files[0].name;
    	currentFunction = addPnr;
    	stateSelectDialog.dialog( "open" );
    });/*.bind('fileuploadstopped', function (e) {
//    	alert();
    	addFeed(currentFiles);
    }).bind('fileuploadstart', function (e) {
    	currentFiles = [];
    	deleteProcessGTFS();
    });*/
	$('#pnr_upload_form > div').css('margin-right','0px');
	
	PNRdialog = $( "#pnr_upload" ).dialog({
	      autoOpen: false,
	      height: $(window).height()*0.9,
	      width: 900,
	      modal: true,
	      buttons: {
//	        "Submit": dSubmit,
	        Close: function() {
	        	PNRdialog.dialog( "close" );
	        }
	      },
	      close: function() {
	    	  $("table tbody.files").empty();
	    	  deleteUploadedPNR();
	      }
	 });
}

function runFEMPfunctions(){
	deleteUploadedfEmp(); 
	
	'use strict';
	$('#femp_upload_form').fileupload({
        url: '/TNAtoolAPI-Webapp/admin',
        acceptFileTypes: /(csv)$/i,
        singleFileUploads: true,
        formData: {data: "femp"},
        sequentialUploads: true,
        maxFileSize: 50000000,
    }).bind('fileuploadalways', function (e, data){
//    	addfEmp(data.files[0].name);
    	currentFunction = addfEmp;
    	stateSelectDialog.dialog( "open" );
    });
	$('#femp_upload_form > div').css('margin-right','0px');
	
	FEMPdialog = $( "#femp_upload" ).dialog({
	      autoOpen: false,
	      height: $(window).height()*0.9,
	      width: 900,
	      modal: true,
	      buttons: {
//	        "Submit": dSubmit,
	        Close: function() {
	        	FEMPdialog.dialog( "close" );
	        }
	      },
	      close: function() {
	    	  $("table tbody.files").empty();
	    	  deleteUploadedfEmp();
	      }
	 });
}

function runCENSUSfunctions(){
	CENSUSdialog = $( "#census_upload" ).dialog({
	      autoOpen: false,
	      height: $(window).height()*0.9,
	      width: 900,
	      modal: true,
	      buttons: {
	        Close: function() {
	        	CENSUSdialog.dialog( "close" );
	        }
	      },
	 });
}

function runT6functions(){
	deleteUploadedT6(); 
	
	'use strict';
	$('#t6_upload_form').fileupload({
        url: '/TNAtoolAPI-Webapp/admin',
        acceptFileTypes: /(csv)$/i,
        singleFileUploads: true,
        formData: {data: "t6"},
        sequentialUploads: true,
        maxFileSize: 50000000,
    }).bind('fileuploadstopped', function (e) {
//    	addT6();
    	currentFunction = addT6;
    	stateSelectDialog.dialog( "open" );
    });
	$('#t6_upload_form > div').css('margin-right','0px');
	
	T6dialog = $( "#t6_upload" ).dialog({
	      autoOpen: false,
	      height: $(window).height()*0.9,
	      width: 900,
	      modal: true,
	      buttons: {
//	        "Submit": dSubmit,
	        Close: function() {
	        	T6dialog.dialog( "close" );
	        }
	      },
	      close: function() {
	    	  $("table tbody.files").empty();
	    	  deleteUploadedT6();
	      }
	 });
}

function runEMPfunctions(){
	deleteUploadedEmp(); 
	
	'use strict';
	$('#emp_upload_form').fileupload({
        url: '/TNAtoolAPI-Webapp/admin',
        acceptFileTypes: /(csv)$/i,
        singleFileUploads: true,
        formData: {data: "emp"},
        sequentialUploads: true,
        maxFileSize: 50000000,
    }).bind('fileuploadstopped', function (e) {
//    	addEmp();
    	currentFunction = addEmp;
    	stateSelectDialog.dialog( "open" );
    });
	$('#emp_upload_form > div').css('margin-right','0px');
	
	EMPdialog = $( "#emp_upload" ).dialog({
	      autoOpen: false,
	      height: $(window).height()*0.9,
	      width: 900,
	      modal: true,
	      buttons: {
//	        "Submit": dSubmit,
	        Close: function() {
	        	EMPdialog.dialog( "close" );
	        }
	      },
	      close: function() {
	    	  $("table tbody.files").empty();
	    	  deleteUploadedEmp();
	      }
	 });
}

function runFPOPfunctions(){
	deleteUploadedfPop(); 
	
	'use strict';
	$('#fpop_upload_form').fileupload({
        url: '/TNAtoolAPI-Webapp/admin',
        acceptFileTypes: /(csv)$/i,
        singleFileUploads: true,
        formData: {data: "fpop"},
        sequentialUploads: true,
        maxFileSize: 50000000,
    }).bind('fileuploadalways', function (e, data){
    	currentFunction = addfPop;
    	stateSelectDialog.dialog( "open" );
    });
	$('#fpop_upload_form > div').css('margin-right','0px');
	
	FPOPdialog = $( "#fpop_upload" ).dialog({
	      autoOpen: false,
	      height: $(window).height()*0.9,
	      width: 900,
	      modal: true,
	      buttons: {
//	        "Submit": dSubmit,
	        Close: function() {
	        	FPOPdialog.dialog( "close" );
	        }
	      },
	      close: function() {
	    	  $("table tbody.files").empty();
	    	  deleteUploadedfPop();
	      }
	 });
}

function runREGIONfunctions(){
	deleteUploadedRegion(); 
	
	'use strict';
	$('#region_upload_form').fileupload({
        url: '/TNAtoolAPI-Webapp/admin',
        acceptFileTypes: /(csv)$/i,
        singleFileUploads: true,
        formData: {data: "region"},
        sequentialUploads: true,
        maxFileSize: 50000000,
    }).bind('fileuploadalways', function (e, data){
//    	addRegion(data.files[0].name);
    	currentFunction = addRegion;
    	stateSelectDialog.dialog( "open" );
    });
	$('#region_upload_form > div').css('margin-right','0px');
	
	REGIONdialog = $( "#region_upload" ).dialog({
	      autoOpen: false,
	      height: $(window).height()*0.9,
	      width: 900,
	      modal: true,
	      buttons: {
//	        "Submit": dSubmit,
	        Close: function() {
	        	REGIONdialog.dialog( "close" );
	        }
	      },
	      close: function() {
	    	  $("table tbody.files").empty();
	    	  deleteUploadedRegion();
	      }
	 });
}

function runGTFSfunctions(){
	//gtfs dialog
	deleteUploadedGTFS();
	
	'use strict';
	$('#gtfs_upload_form').fileupload({
        url: '/TNAtoolAPI-Webapp/admin',
        acceptFileTypes: /(zip)$/i,
        singleFileUploads: true,
        formData: {data: "gtfs"},
        sequentialUploads: true,
        maxFileSize: 50000000,
    }).bind('fileuploadalways', function (e, data){
//    	addFeed(data.files[0].name);
    	currentFiles.push(data.files[0].name);
    	currentSizes.push(data.files[0].size);
    }).bind('fileuploadstopped', function (e) {
//    	alert();
    	addFeed();
//    	callAddFeed(currentFiles);
    }).bind('fileuploadstart', function (e) {
    	currentFiles = [];
    	currentSizes = [];
    	deleteProcessGTFS();
    });
	$('#gtfs_upload_form > div').css('margin-right','0px');
	
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
}