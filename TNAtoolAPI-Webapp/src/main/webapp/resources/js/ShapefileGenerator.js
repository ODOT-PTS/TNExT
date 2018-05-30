var dbName = '';
var flag = '';
var agencyIDs = [];
var selectedDB = '';

function ShapeFileExpStart() {
	window.open('/TNAtoolAPI-Webapp/ShapeFileExport.html');
}

function getDatabases() {
	// Get database list
	$.ajax({
		type : 'GET',
		datatype : 'json',
		url : '/TNAtoolAPI-Webapp/queries/transit/DBList',
		async : false,
		success : function(d) {
			var html = '<form>';
			for (var i = 0; i < d.DBelement.length; i++) {
				html = html
						.concat('<input type="radio" class="dbCheckbox" name="'
								+ d.DBid[i] + '" value="' + i
								+ '" onchange=getAgencies(this)>'
								+ d.DBelement[i] + '<br>');
			}
			html = html.concat('</form>');
			$('#databases').append(html);
		}
	});
}

function getAgencies(input) {

	$('#agencies').empty();
	$('.flagRadio').each(function(index, item) {
		$(item).attr("disabled", true);
		$(item).attr("checked", false);
	})
	$('#submitButton').attr("disabled", true);

	// Deselecting the other agency check boxes.
	selectedDB = input.value;
	$('.dbCheckbox').each(function(i, item) {
		if (item.value != selectedDB)
			$(item).attr({
				checked : false
			});
	});

	// Getting the list of agencies from the selected DB.
	var sortedAgencyList = [];
	$
			.ajax({
				type : 'GET',
				datatype : 'json',
				url : '/TNAtoolAPI-Webapp/queries/transit/getSelectedAgencies?&dbindex='
						+ selectedDB + '&username=' + getSession(),
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
					$
							.each(
									sortedAgencyList,
									function(i, item) {
										html = html
												.concat('<input type="checkbox" class="agencyCheckbox" name="'
														+ item.name
														+ '" value="'
														+ item.id
														+ '" onchange=toggleFlagCheckboxes(this)>'
														+ item.name + '<br>');
									});
					$('#agencies').append(html);
				}
			});
}

function toggleFlagCheckboxes() {
	var flag = false;
	$('.agencyCheckbox').each(function(i, item) {
		if (item.checked) {
			flag = true;
			return false;
		}
	});

	if (flag) {
		$('.flagRadio').each(function(i, item) {
			$(item).attr("disabled", false);
		})
	} else {
		$('.flagRadio').each(function(i, item) {
			$(item).attr("disabled", true);
			$(item).attr({
				checked : false
			});
		})
		$('#submitButton').attr("disabled", true);
	}
}

function createShapeFiles() {
	$('#overlay').show();
	var dbName = document.querySelector('input[class="dbCheckbox"]:checked').name;
	var flag = document.querySelector('input[class="flagRadio"]:checked').value;
	var date = document.querySelector('input[id="onDate"]').value;
	var agencies = [];
	$('.agencyCheckbox').each(function(index, item) {
		if (item.checked)
			agencies.push(item.value);
	})
	
	$.ajax({
		type : 'GET',
		datatype : 'json',
		url : '/TNAtoolAPI-Webapp/queries/transit/getshapefile?agencyids='
				+ agencies + '&flag=' + flag + '&dbName=' + dbName
				+ '&username=' + getSession() + '&dbIndex=' + selectedDB + '&date=' + date, 
		async : true,
		success : function(d) {
			console.log(d);
			alert('done');
			downloadURL(d);
		},
		error : function(e) {
			window.open("/" + e.responseText);
			$('#overlay').fadeOut(1500,null);
		}
	});
}