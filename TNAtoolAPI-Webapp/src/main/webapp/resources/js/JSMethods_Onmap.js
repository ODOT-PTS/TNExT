function myMethod(latLng) {
	var lat = latLng[0];
	var lng = latLng[1];
	var x = 0.25;
	var marLat = (Math.round(lat * 1000000) / 1000000).toString().replace('.',
			'').replace('-', '');
	var marLng = (Math.round(lng * 1000000) / 1000000).toString().replace('.',
			'').replace('-', '');
	drawCentroid[0] = lat;
	drawCentroid[1] = lng;
	area = Math.pow(x, 2) * Math.PI;
	drawCentroid[0] = (Math.round(drawCentroid[0] * 1000000) / 1000000)
			.toString();
	drawCentroid[1] = (Math.round(drawCentroid[1] * 1000000) / 1000000)
			.toString();
	area = Math.round(area * 100) / 100;
	var that = drawControl._toolbars[L.DrawToolbar.TYPE]._modes.circle.handler;
	that.enable();
	that._startLatLng = [ lat, lng ];
	that._shape = new L.Circle([ lat, lng ], x * 1609.34,
			that.options.shapeOptions);
	that._map.addLayer(that._shape);
	that._fireCreatedEvent();
	that.disable();
}
	