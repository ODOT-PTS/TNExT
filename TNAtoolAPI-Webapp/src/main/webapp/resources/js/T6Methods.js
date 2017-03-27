function toggleCheckbox(checkbox){	
		var tree= $('#jstree').jstree(true);
		if (checkbox.checked){
		tree.select_all();
		}else{
		tree.deselect_all();
		}	
	}
	
	function getTableHeaders(){
		var nodesList = [];
		var tree = $('#jstree').jstree(true);
		$('.jstree-node').each(function(){
			if ((tree.is_selected($(this)) && tree.get_parent($(this)).indexOf('_aggregate') < 0 && tree.get_parent($(this)) != '#')
					|| ($(this).attr('id').indexOf('_aggregate') > -1 && tree.get_children_dom($(this)) != null) ) {
				nodesList.push($(this));
			}
		});
		
		var y = "";
		if ($('#reportType').val() == 'Agencies'){
			nodesList.forEach (function (item, index, array){
				 y += '<th class="metric" title="Number of Individuals Served: Unduplicated summation of individuals of the selected category who are living within X distance of any stop. This metric is date/service independent.">'+ tree.get_text(item) +'-S<span class="IOSym">(1)</span></th>'
					+ '<th class="metric" title="Number of Individuals Served at Level of Service: Unduplicated summation of individuals of the selected category who receive the specified minimum level of service.">'+ tree.get_text(item) +'-SLOS<span class="IOSym">(1)(2)(3)</span></th>'
					+ '<th class="metric" title="Number of Individuals Served by Service: Unduplicated summation of individuals of the selected category who are served by service is calculated as service stops multiplied by the unduplicated individuals living within an X-mile radius (i.e., stop distance) of all stops. Reported number is cumulative over the selected dates.">'+ tree.get_text(item) +'-SS<span class="IOSym">(1)(3)</span></th>';
				});
		}else{
			nodesList.forEach (function (item, index, array){
			 y += '<th class="metric" title="Total number of individuals of that belong to the selected category and are living in the area.">'+ tree.get_text(item) +'</th>'
				+' <th class="metric" title="Number of Individuals Served: Unduplicated summation of individuals of the selected category who are living within X distance of any stop. This metric is date/service independent.">'+ tree.get_text(item) +'-S<span class="IOSym">(1)</span></th>'
				+' <th class="metric" title="Number of Individuals Served at Level of Service: Unduplicated summation of individuals of the selected category who receive the specified minimum level of service.">'+ tree.get_text(item) +'-SLOS<span class="IOSym">(1)(2)(3)</span></th>'
				+' <th class="metric" title="Number of Individuals Served by Service: Unduplicated summation of individuals of the selected category who are served by service is calculated as service stops multiplied by the unduplicated individuals living within an X-mile radius (i.e., stop distance) of all stops. Reported number is cumulative over the selected dates.">'+ tree.get_text(item) +'-SS<span class="IOSym">(1)(3)</span></th>';
			});
		}
		return y;
	} 
	
	function clearReport(){
		document.getElementById('displayReport').innerHTML = "";
		$('#initialText').show();
	}
	
	function selectFunction(){
		$('#reportTitle').html($('#reportType').val());
	}

	function openReport(){	
		
		if (exceedsMaxRadius($('#Sradius').val())){	// Checks if the entered search radius exceeds the maximum.
			alert('Enter a number less than ' + maxRadius + ' miles for the population search radius.');
			return;
		}
		
		if ($('#jstree').jstree(true).get_selected()==""){
			alert('At least select 1 category');
			return 0;
		}
		
		// update dates
		var dates = $('#datepicker').multiDatesPicker('getDates');
		if(dates.length==0){
			$( "#datepicker" ).multiDatesPicker({
				addDates: [new Date()]
			});
		}
		w_qstringd = dates.join(",")
		
		// close slidebar
		mySlidebar.slidebars.close();
		$('#dialogPreLoader').show();
		$("#initialText").hide();
		document.getElementById('displayReport').innerHTML = "";
		
		// making a 2D array based on the selected categories and aggregated categories.
		var tableContent = [];
		var rootList = [];
		var tree = $('#jstree').jstree(true);
		$('.jstree-node').each(function(){
		  if ((tree.is_selected($(this)) && tree.get_parent($(this)).indexOf('_aggregate') < 0 && tree.get_parent($(this)) != '#')
					|| ($(this).attr('id').indexOf('_aggregate') > -1 && tree.get_children_dom($(this)) != null) ){
			 	rootList.push(this);
			 }
		});
		
		tree.open_all();
		rootList.forEach (function (item, index, array){
			var parentID = $(item).attr('id');
			if (tree.is_parent(item)){						
				var children = tree.get_children_dom(item);
				var childrenIDs = [];
				$.each(children, function(ind,obj){
					if (tree.is_selected(obj)){
						childrenIDs.push($(obj).attr('id'));
					}							
				});
				tableContent.push([parentID,childrenIDs]);
			}else{
				var childrenIDs = [parentID];
				tableContent.push([parentID,childrenIDs]);
			}
		});
		
		// Building header row of the table 
		var html = '<table id="RT" class="display" align="center">';
		if ($('#reportType').val() == 'Agencies'){
			tmp = 	'<tr><th>Agency ID</em></th>'+
				'<th>Agency Name</em></th>';
		}else{
			tmp = 	'<tr><th>Area ID</em></th>'+
			'<th>Area Name</em></th>';
		}
		tmp += getTableHeaders() + '</tr>';
		html += '<thead>'+tmp+'</thead><tbody>';
		
		$.ajax({
			type: 'GET',
			datatype: 'json',
			url: '/TNAtoolAPI-Webapp/queries/transit/titlevi?emp?&report=' + $("#reportType").val() + '&day=' + w_qstringd + '&radius='+ $('#Sradius').val() * 1609.34 + '&L=' + $('#LOS').val() +'&dbindex='+dbindex+'&username='+getSession(),
			async: true,
			success: function(d){// making a hashmap of the query results.\
				docMetadata = d.metadata;
				var temp = d.TitleVIDataList;
				t6.TitleVIDataList = [];
				temp.forEach(function(i, ind, arr){
					t6.TitleVIDataList.push(i);
				});
				for (var i=0; i<t6.TitleVIDataList.length;i++){
					html += '<tr><td>'+ t6.TitleVIDataList[i].id + '</td>'+
						'<td>' + t6.TitleVIDataList[i].name + '</td>';
					var resultSet= {};
					$('.jstree-leaf').each(function(){
						if ($('#reportType').val() != 'Agencies') resultSet[$(this).attr('id')] = t6.TitleVIDataList[i][$(this).attr('id')];//console.log($(this).attr('id'));
						resultSet[$(this).attr('id')+'_atlos'] = t6.TitleVIDataList[i][($(this).attr('id')).toLowerCase()+'_atlos'];
						resultSet[$(this).attr('id')+'_withinx'] = t6.TitleVIDataList[i][($(this).attr('id')).toLowerCase()+'_withinx'];
						resultSet[$(this).attr('id')+'_served'] = t6.TitleVIDataList[i][($(this).attr('id')).toLowerCase()+'_served'];
					});	
							
					// Filling in other columns of the table
					for (var index in tableContent){
						var Summation = 0;
						var PopServedSummation = 0;
						var PopAtLOSSummation = 0;
						var ServedByServiceSummation = 0;
						tableContent[index][1].forEach(function(j, ind, arr){
							var node = $('#jstree').find("[id='"+j+"']");
							if (tree.is_selected(node)){
								if ($('#reportType').val() != 'Agencies') Summation += resultSet[j];
								PopServedSummation += resultSet[j+'_withinx'];
								PopAtLOSSummation += resultSet[j+'_atlos'];
								ServedByServiceSummation += resultSet[j+'_served'];	
							}							
						});
						if ($('#reportType').val() != 'Agencies') html+='<td>' + numWithCommas(Summation) + '</td>';
						html += '<td>' + numWithCommas(PopServedSummation) + '</td>'
						+ '<td>' + numWithCommas(PopAtLOSSummation) + '</td>'
						+ '<td>' + numWithCommas(ServedByServiceSummation) + '</td>';
					}
					html += '</tr>';
				}
				$('#displayReport').append($(html));
				$('#dialogPreLoader').hide();
				
				tableProperties.hiddenCols =  [$('#RT thead th').length - 1];
				table = buildDatatables();				
			}
		});		
	}		