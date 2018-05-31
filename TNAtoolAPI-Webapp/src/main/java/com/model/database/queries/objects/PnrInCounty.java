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

package com.model.database.queries.objects;

import java.sql.Array;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement (name = "PnrInCounty")
public class PnrInCounty {
	
	@XmlAttribute
	@JsonSerialize
	public String pnrid;
	
	@XmlAttribute
	@JsonSerialize
	public String county;
	
	@XmlAttribute
	@JsonSerialize
	public String lotname;
	
	@XmlAttribute
	@JsonSerialize
	public String city;
	
	@XmlAttribute
	@JsonSerialize
	public String location;
	
	@XmlAttribute
	@JsonSerialize
	public String zipcode;
	
	@XmlAttribute
	@JsonSerialize
	public String spaces;
	
	@XmlAttribute
	@JsonSerialize
	public String accessiblespaces;
	
	@XmlAttribute
	@JsonSerialize
	public String transitservices;
	
	@XmlAttribute
	@JsonSerialize
	public String lat;
	
	@XmlAttribute
	@JsonSerialize
	public String lon;
	
	@XmlAttribute
	@JsonSerialize
	public String bikerackspaces;
	
	@XmlAttribute
	@JsonSerialize
	public String bikelockerspaces;
	
	@XmlAttribute
	@JsonSerialize
	public String electricvehiclespaces;
	
	@XmlAttribute
	@JsonSerialize
	public String carsharing;
	
	@XmlAttribute
	@JsonSerialize
	public String availability;
	
	@XmlAttribute
	@JsonSerialize
	public String timelimit;
	
	@XmlAttribute
	@JsonSerialize
	public String restroom;
	
	@XmlAttribute
	@JsonSerialize
	public String benches;
	
	@XmlAttribute
	@JsonSerialize
	public String shelter;
	
	@XmlAttribute
	@JsonSerialize
	public String indoorwaitingarea;
	
	@XmlAttribute
	@JsonSerialize
	public String trashcan;
	
	@XmlAttribute
	@JsonSerialize
	public String lighting;
	
	@XmlAttribute
	@JsonSerialize
	public String securitycameras;
	
	@XmlAttribute
	@JsonSerialize
	public String sidewalks;
	
	@XmlAttribute
	@JsonSerialize
	public String pnrsignage;
	
	@XmlAttribute
	@JsonSerialize
	public String lotsurface;
	
	@XmlAttribute
	@JsonSerialize
	public String propertyowner;
	
	@XmlAttribute
	@JsonSerialize
	public String localexpert;
	
	@XmlAttribute
	@JsonSerialize
	public String metadata;
	
	@XmlAttribute
	@JsonSerialize
	public String agencies;
	
	@XmlAttribute
	@JsonSerialize
	public String stopids;
	
	@XmlAttribute
	@JsonSerialize
	public String stopnames;
	
	@XmlAttribute
	@JsonSerialize
	public String routeids;
	
	@XmlAttribute
	@JsonSerialize
	public String routenames;
}
