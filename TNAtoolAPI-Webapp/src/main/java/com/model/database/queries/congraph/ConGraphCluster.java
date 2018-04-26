// Copyright (C) 2015 Oregon State University - School of Mechanical,Industrial and Manufacturing Engineering 
//   This file is part of Transit Network Explorer Tool.
//
//    Transit Network Explorer Tool is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Transit Network Explorer Tool is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU  General Public License for more details.
//
//    You should have received a copy of the GNU  General Public License
//    along with Transit Network Explorer Tool.  If not, see <http://www.gnu.org/licenses/>.

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
