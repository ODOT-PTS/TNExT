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


import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name = "RouteR")
public class RouteR {
	
	@XmlAttribute
	@JsonSerialize
	public String AgencyId;
	
	@XmlAttribute
	@JsonSerialize
	public String AgencyName;
	
	@XmlAttribute
	@JsonSerialize
	public String RouteId;	
	
	@XmlAttribute
	@JsonSerialize
	public String RouteSName;
	
	@XmlAttribute
	@JsonSerialize
	public String RouteLName;	
	
	@XmlAttribute
	@JsonSerialize
	public String RouteDesc;	
	
	@XmlAttribute
	@JsonSerialize
	public String RouteType;
		
	@XmlAttribute
	@JsonSerialize
    public String RouteURL;	
	
	@XmlAttribute
	@JsonSerialize
    public String StopsCount;	
	
	@XmlAttribute
	@JsonSerialize
    public String Trips;	
	
	@XmlAttribute
	@JsonSerialize
    public String ServiceHours;	
	
	@XmlAttribute
	@JsonSerialize
    public String RouteLength;	
	
	@XmlAttribute
	@JsonSerialize
    public String ServiceMiles;	
	
	@XmlAttribute
	@JsonSerialize
    public String ServiceStops;	
	
	@XmlAttribute
	@JsonSerialize
    public String UServicePop;
	
	@XmlAttribute
	@JsonSerialize
    public String RServicePop;	
	
	@XmlAttribute
	@JsonSerialize
    public String AreaServed;    
	
	@XmlAttribute
	@JsonSerialize
    public String UPopWithinX;
	
	@XmlAttribute
	@JsonSerialize
    public String RPopWithinX;
	
	@XmlAttribute
	@JsonSerialize
    public String counties;

	@XmlAttribute
	@JsonSerialize
    public String places;
	
	@XmlAttribute
	@JsonSerialize
    public String regions;
	
	@XmlAttribute
	@JsonSerialize
    public String urbans;
	
	@XmlAttribute
	@JsonSerialize
    public String congdists;
}
