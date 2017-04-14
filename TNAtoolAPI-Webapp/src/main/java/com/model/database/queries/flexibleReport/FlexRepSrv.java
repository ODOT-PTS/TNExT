// Copyright (C) 2015 Oregon State University - School of Mechanical,Industrial and Manufacturing Engineering 
//   This file is part of Transit Network Analysis Software Tool.
//
//    Transit Network Analysis Software Tool is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Transit Network Analysis Software Tool is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU  General Public License for more details.
//
//    You should have received a copy of the GNU  General Public License
//    along with Transit Network Analysis Software Tool.  If not, see <http://www.gnu.org/licenses/>.

package com.model.database.queries.flexibleReport;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement( name = "FlexRepSrv")
public class FlexRepSrv {
	@XmlAttribute
	@JsonSerialize
	public String areaId;
	
	@XmlAttribute
	@JsonSerialize
	public String areaName;
	
	@XmlAttribute
	@JsonSerialize
	public String agencyId;
	
	@XmlAttribute
	@JsonSerialize
	public String agencyName;
	
	@XmlAttribute
	@JsonSerialize
	public int routeStops;
	
	@XmlAttribute
	@JsonSerialize
	public int serviceStops;
	
	@XmlAttribute
	@JsonSerialize
	public double routeMiles;
	
	@XmlAttribute
	@JsonSerialize
	public double serviceMiles;
	
	@XmlAttribute
	@JsonSerialize
	public long serviceSeconds;
}