package com.model.database.queries.congraph;

import java.util.Set;

public class ConGraphCluster {

	Set<Coordinate> coordinates;
	Coordinate centroid;

	public ConGraphCluster(Set<Coordinate> coordinates) {
		this.coordinates = coordinates;
		this.centroid = getCentroid(this.coordinates);
	}

	public void addAll(Set<Coordinate> coordinates) {
		this.coordinates.addAll(coordinates);
		this.centroid = getCentroid(this.coordinates);
	}

	private static Coordinate getCentroid(Set<Coordinate> coordinates) {
		double lat = 0;
		double lng = 0;
		for (Coordinate p : coordinates) {
			lat += p.lat;
			lng += p.lng;
		}
		lat = lat / coordinates.size();
		lng = lng / coordinates.size();
		return (new Coordinate(lat, lng));
	}
	
}
