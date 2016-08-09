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
	for(var i=0; i<dbInfo[0].length; i++){
		html+="<label>"+dbInfo[0][i]+"</label><br>";
		var htmlD = "";
		if(i==0 || i==7 || i==8 || i==9 || i==2 || i==3){
			htmlD = "disabled";
		}else if(i==4){
			html += "Host: <input type='text' id='"+dbInfo[0][i]+"' value='"+urlV[0]+"' required style='width:120px;font-size:80%;margin-right:2em'>";
			html += "Port: <input type='text' id='"+dbInfo[0][i]+"p"+"' value='"+urlV[1]+"' required style='width:120px;font-size:80%;margin-right:2em'>";
			html += "Name: <input type='text' id='"+dbInfo[0][i]+"n"+"' value='"+urlValue[3]+"' required style='width:120px;font-size:80%'><br><br>";
			continue;
		}
		html+="<input type='text' id='"+dbInfo[0][i]+"' value='"+info[i]+"' required "+htmlD+" style='width:600px;font-size:80%'><br><br>";
	}
	
	html+="<input type='submit' id='dialogSubmit' tabindex='-1' style='position:absolute; top:-1000px'>";
	$('#dialogFields').html(html);
	
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
	    $.ajax({
	        type: "GET",
	        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/checkInput?&dbname="+dbname+"&cURL="+cURL+"&user="+user+"&pass="+pass+"&db="+db+"&oldURL="+oldURL+"&olddbname="+olddbname,
	        dataType: "json",
	        async: false,
	        success: function(d) {
	        	if(d.DBError=="true"){
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
	newDB[3]= "classpath:org/onebusaway/gtfs/examples/"+newDB[3];
	newDB[4]= "jdbc:postgresql://"+newDB[4]+":"+$('#'+dbInfo[0][4]+"p").val()+"/"+$('#'+dbInfo[0][4]+"n").val();
	
    var db = newDB.toString();
	if(ind!=-1){
		var oldcfg1 = dbInfo[ind+1][2];
		var oldcfg2 = dbInfo[ind+1][3];
		$.ajax({
	        type: "GET",
	        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/updateDB?&db="+db+"&oldName="+oldName+"&oldcfg1="+oldcfg1+"&oldcfg2="+oldcfg2,
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
        	//alert(d.DBError);
        	if(undefined != d.DBError){
        		aList = d.DBError;
        		return aList;
        	}else{
	        	var html = "<p>List of feeds in the database</p>";
	        	html += "<p><input type='button' value='Select All' onclick='selectAllCheckBoxes("+indexx+")'>";
	        	html += "<input type='button' value='Deselect All' onclick='deselectAllCheckBoxes("+indexx+")' style='margin-left:1em'></p>";
	        	$.each(d.feeds, function(i,item){
	        		if(d.names[i]!=null){
		        		var agencynames = d.names[i].split(",");
		        		var html1 = "<br><span style='margin-left:3.5em'>Agencies:</span>";
		        		for(var k=0; k<agencynames.length; k++){
		        			html1 += "<br><span style='margin-left:6em'>"+agencynames[k]+"<span>";
		        		}
		        		html += "<p><input type='checkbox' class='selectFeed"+indexx+"' id='"+item+"' ><span style='margin-left:2em'>"+item+"</span>"
		        		+"<br><span style='margin-left:3.5em'>Start Date: "+stringToDate(d.startdates[i])+"</span>"+"<br><span style='margin-left:3.5em'>End Date: "+stringToDate(d.enddates[i])+"</span>"+html1+"</p>";
	        		}
	        	});
	        	html += "<br><input type='button' value='Delete selected feeds' onclick='deleteFeed("+index+")'>";
	        	html += "<br><input type='button' value='Update selected feeds' onclick='updateFeed("+index+")'>";
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
	var db = dbInfo[index].toString();
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
	});
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
	feeds.join("$$");
	
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
	location.reload(true);
}

function addFeed(index){
	var db = dbInfo[index].toString();
	var feeds = new Array();
	
	$('.selectFeed'+index).each(function(){
		if($(this).is(':checked')){
			feeds.push($(this).attr('id'));
		}
	});
	feeds.join("$$");
	for(var i=0; i<feeds.length; i++){
		$.ajax({
	        type: "GET",
	        url: "/TNAtoolAPI-Webapp/modifiers/dbupdate/addfeed?&feedname="+folder+"Feeds/"+feeds[i]+"&db="+db,
	        dataType: "json",
	        async: false,
	        success: function(d) {
	        	
	        }
		});
	}
	location.reload(true);
}

var ind;
var dialog;
var dbInfo = [[]];
var defaultInfo = ["","","","","jdbc:postgresql://localhost:5432/gtfsdb","","","mapping.hbm.xml","org/onebusaway/gtfs/model/GtfsMapping.hibernate.xml","org/onebusaway/gtfs/impl/HibernateGtfsRelationalDaoImpl.hibernate.xml"];
var folder = "C:/Users/tnatool/Development/Repository/test/TNAtoolAPI-Webapp/WebContent/admin/Development/";
var fList;
var aList;
var oldName;
$(document).ready(function(){
	
	$.ajax({
        type: "GET",
        url: "dbInfo.csv",
        dataType: "text",
        async: false,
        success: function(data) {
        	var lines = data.split(/\r\n|\n/);
        	for(var i=0; i<lines.length-1; i++){
        		dbInfo[i] = lines[i].split(',');
        	}
        	var html="";
        	for(var i=1; i<lines.length-1; i++){
        		html += "<table border=1>";
            	for(var j=0; j<dbInfo[0].length; j++){
            		html += "<tr><td>"+dbInfo[0][j]+"</td><td>"+dbInfo[i][j]+"</td></tr>";
            	}
            	
            	html += "</table><br><input type='button' value='Delete/Drop' onclick='deleteDb("+dbInfo[i][0]+")'>";
            	html += "<input type='button' value='Modify Information' onclick='addModifyDB("+i+", "+dbInfo[i][0]+")' >";
            	html += "<input type='button' value='Run the Update Queries' onclick='runQueries("+i+")' >";
            	/*html += "<input type='button' value='Add Functions' onclick='addPsqlFunctions("+i+")' >";*/
            	html += "<input type='button' value='Add Index' onclick='addIndex("+i+")' >";
            	html += "<div style='width: auto; font-size:80%' id='accordion"+dbInfo[i][0]+"' class='accordion'><h3>Database Status</h3><div>";
            	html += "<div id='listOfFeeds"+dbInfo[i][0]+"' style='float:left'>"+listOfFeeds(i)+"</div>";
            	html += "<div id='listOfAgencies"+dbInfo[i][0]+"' style='width:50%; float:right; '>"+listOfAgencies(i)+"</div>";
            	html += "</div></div><br><br><br>";
        	}
        	$('#dbList').html(html);
        	
        }
     });
	
	$( ".accordion" ).accordion({
      collapsible: true
    });
	$( ".ui-accordion-header" ).click();
	dialog = $( "#dialog-form" ).dialog({
	      autoOpen: false,
	      height: 700,
	      width: 700,
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
 
	/* $.ajax({
		type: 'GET',
		datatype: 'json',
		url: '/TNAtoolAPI-Webapp/queries/transit/DBList',
		async: false,
		success: function(d){	
			var select = document.getElementById("dbselect");
			select.options.length = 0;
		    var menusize = 0;
		    $.each(d.DBelement, function(i,item){
		    	var option = document.createElement('option');
		        option.text = item;
		        option.value = 'DB'+i;
		        select.add(option, i);		    	
		    	menusize++;
		    });		    		    
		    if (dbindex<0 || dbindex>menusize-1){
		    	dbindex = 0;
		    	history.pushState('data', '', document.URL.split("?")[0]+'?&x=0.1&dbindex=0');
		    }
		    select.options.size = menusize;
		    select.selectedIndex = dbindex;		    				    
		}			
	});	 */
	/* $.ajax({
		type: 'GET',
		datatype: 'json',
		url: '/TNAtoolAPI-Webapp/modifiers/dbupdate/feedlist?&foldername='+folder,
		async: false,
		success: function(d){	
				    				    
		}			
	}); */
});
				
