//   Copyright (C) 2015 Oregon State University - School of Mechanical,Industrial and Manufacturing Engineering 
//   This file is part of Transit Network Analysis Software Tool.
//
//    Transit Network Analysis Software Tool is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Transit Network Analysis Software Tool is distributed in the hope that it will be useful,
//    but WITHOUT ANY WstartANTY; without even the implied wstartanty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU  General Public License for more details.
//
//    You should have received a copy of the GNU  General Public License
//    along with Transit Network Analysis Software Tool.  If not, see <http://www.gnu.org/licenses/>.

package com.model.database.queries.timingcon;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name = "ConTrip")
public class ConTrip {
	
	@XmlAttribute
	@JsonSerialize
	public String agencyId;
	
	// hour, minute and second at which the trips starts.
	@XmlAttribute
	@JsonSerialize
	public String agencyName;
	
	// source stop.
	@XmlAttribute
	@JsonSerialize
	public String stopId1;
	
	@XmlAttribute
	@JsonSerialize
	public String stopName1;
	
	@XmlAttribute
	@JsonSerialize
	public double stopLat1;
	
	@XmlAttribute
	@JsonSerialize
	public double stopLon1;
	
	// destination stop.
	@XmlAttribute
	@JsonSerialize
	public String stopId2;
	
	@XmlAttribute
	@JsonSerialize
	public String stopName2;
	
	@XmlAttribute
	@JsonSerialize
	public double stopLat2;
	
	@XmlAttribute
	@JsonSerialize
	public double stopLon2;
	
	@XmlAttribute
	@JsonSerialize
	public String routeId;
	
	@XmlAttribute
	@JsonSerialize
	public String routeName;
	
	@XmlAttribute
	@JsonSerialize
	public String tripId;
	
	// Arrival time at stop 1
	@XmlAttribute
	@JsonSerialize
	public int arrival1;
	
	// Departure time from stop 1
	@XmlAttribute
	@JsonSerialize
	public int departure1;
	
	// Arrival time at stop 2
	@XmlAttribute
	@JsonSerialize
	public int arrival2;
	
	// Departure time from stop 2
	@XmlAttribute
	@JsonSerialize
	public int departure2;
	
	// Difference between arrival at stop1 and departure from stop2
	@XmlAttribute
	@JsonSerialize
	public int timeDiff;
	
	// shape of the source trip
	@XmlAttribute
	@JsonSerialize
	public String tripShape1;
	
	// shape of the destination trip
	@XmlAttribute
	@JsonSerialize
	public String tripShape2;
}