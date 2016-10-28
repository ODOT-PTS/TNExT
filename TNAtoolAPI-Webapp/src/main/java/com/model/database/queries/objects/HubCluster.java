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

package com.model.database.queries.objects;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement( name = "HubCluster")
public class HubCluster {
	@XmlAttribute
	@JsonSerialize
	public String lat;
	
	@XmlAttribute
	@JsonSerialize
	public String lon;
	
	@XmlAttribute
	@JsonSerialize
	public String stopscount;
	
	@XmlAttribute
	@JsonSerialize
	public String pop;
	

	@XmlAttribute
	@JsonSerialize
	public long rac;


	@XmlAttribute
	@JsonSerialize
	public long wac;


	@XmlAttribute
	@JsonSerialize
	public String agenciescount;
	
	@XmlAttribute
	@JsonSerialize
	public String countiescount;
	
	@XmlAttribute
	@JsonSerialize
	public String routescount;
	
	@XmlAttribute
	@JsonSerialize
	public String visits;
	
	@XmlAttribute
	@JsonSerialize
	public Integer pnrcount;
	
	@XmlAttribute
	@JsonSerialize
	public Integer placescount;
	
	@XmlAttribute
	@JsonSerialize
	public String urbanareaspop;
	
	@XmlElement(name = "countiesNames")
	public List<String> countiesNames;
	
	@XmlElement(name = "agenciesNames")
	public List<String> agenciesNames;
	
	@XmlElement(name = "urbanNames")
	public List<String> urbanNames;
	
	@XmlElement(name = "regionsIDs")
	public List<String> regionsIDs;
	
	@XmlElement(name = "stopsAgencies")
	public List<String> stopsAgencies;
	
	@XmlElement(name = "stopsAgenciesNames")
	public List<String> stopsAgenciesNames;
	
	@XmlElement(name = "stopsIDs")
	public List<String> stopsIDs;
	
	@XmlElement(name = "stopsNames")
	public List<String> stopsNames;
	
	@XmlElement(name = "stopsLats")
	public List<Double> stopsLats;
	
	@XmlElement(name = "stopsLons")
	public List<Double> stopsLons;
	
	@XmlElement(name = "stopsVisits")
	public List<Integer> stopsVisits = new ArrayList<Integer>();
	
	@XmlElement(name = "routesAgencies")
	public List<String> routesAgencies;
	
	@XmlElement(name = "routesAgenciesNames")
	public List<String> routesAgenciesNames;
	
	@XmlElement(name = "routesIDs")
	public List<String> routesIDs;
	
	@XmlElement(name = "routeShortnames")
	public List<String> routeShortnames;
	
	@XmlElement(name = "routesLongnames")
	public List<String> routesLongnames;
	
	@XmlElement(name = "pnrIDs")
	public List<Integer> pnrIDs;
	
	@XmlElement(name = "pnrNames")
	public List<String> pnrNames;
	
	@XmlElement(name = "pnrCities")
	public List<String> pnrCities;
	
	@XmlElement(name = "pnrLats")
	public List<Double> pnrLats;
	
	@XmlElement(name = "pnrLons")
	public List<Double> pnrLons;
	
	@XmlElement(name = "pnrSpaces")
	public List<Integer> pnrSpaces;
	
	@XmlElement(name = "placesIDs")
	public List<String> placesIDs;
	
	@XmlElement(name = "placesNames")
	public List<String> placesNames;
	
}
