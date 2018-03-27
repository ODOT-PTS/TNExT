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
								+ '" onchange=getCounties(this)>'
								+ d.DBelement[i] + '<br>');
			}
			html = html.concat('</form>');
			$('#databases').append(html);
		}
	});
}

function getCounties(input) {
    // Clear
	$('#counties').empty();
	$('#submitButton').attr("disabled", true);

	// Deselecting the other db check boxes.
	selectedDB = input.value;
	$('.dbCheckbox').each(function(i, item) {
		if (item.value != selectedDB)
			$(item).attr({
				checked : false
			});
	});

	// Getting the list of counties from the selected DB.
	$.ajax({
        type : 'GET',
        datatype : 'json',
        url : '/TNAtoolAPI-Webapp/queries/transit/getAreaList?&areaType=county&dbindex=' + selectedDB,
        async : false,
        success : function(d) {
            var a = $('#counties');
            a.empty();
            d
                .map(function(i) { return [i.name, i.id]})
                .sort()
                .forEach(function (i) {
                    a.append(
                        $('<input />').attr('type','checkbox').addClass('countyCheckbox').attr('name',i[0]).attr('value',i[1]).change(toggleFlagCheckboxes)
                    );
                    a.append($('<span>').text(i[0]));
                    a.append('<br />');
                });
        }
    });
}

function toggleFlagCheckboxes() {
	var flag = false;
	$('.countyCheckbox').each(function(i, item) {
		if (item.checked) {flag = true}
	});
	if (flag) {
		$('#submitButton').attr("disabled", false);
    } else {
		$('#submitButton').attr("disabled", true);
    }
}

function createCSV() {
	$('#overlay').show();
	var dbName = document.querySelector('input[class="dbCheckbox"]:checked').name;
	var counties = [];
    $('.countyCheckbox').each(function(index, item) {
		if (item.checked) counties.push(item.value);
	});
	$.ajax({
		type : 'GET',
		datatype : 'json',
		url : '/TNAtoolAPI-Webapp/queries/transit/getdemographics?countyids='+ counties + '&dbIndex=' + selectedDB,
		async : true,
		success : function(d) {
			alert('done');
			downloadURL(d);
		},
		error : function(e) {
			window.open("/" + e.responseText);
			$('#overlay').fadeOut(1500,null);
		}
	});
}