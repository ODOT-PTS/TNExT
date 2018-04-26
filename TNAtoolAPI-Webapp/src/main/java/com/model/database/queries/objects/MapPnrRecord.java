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

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement (name = "MapPnrRecord")
public class MapPnrRecord {
	@XmlAttribute
	@JsonSerialize
	public String id;
	
	@XmlAttribute
	@JsonSerialize
	public String countyId;
	
	@XmlAttribute
	@JsonSerialize
	public String countyName;
	
	@XmlAttribute
	@JsonSerialize
	public String lat;
	
	@XmlAttribute
	@JsonSerialize
	public String lon;
	
	@XmlAttribute
	@JsonSerialize
	public String lotName;
	
	@XmlAttribute
	@JsonSerialize
	public String spaces;
	
	@XmlAttribute
	@JsonSerialize
	public String availability;
	
	@XmlAttribute
	@JsonSerialize
	public String transitSerives;
	
	@XmlElement(name = "MapPnrTr")
	public Collection<MapAgency> MapTransit = new ArrayList<MapAgency>();
	
	@XmlElement(name = "MapPnrRL")
    public Collection<MapRoute> MapPnrRL = new ArrayList<MapRoute>();

	@XmlElement(name = "MapPnrSL")
    public Collection<MapStop> MapPnrSL = new ArrayList<MapStop>();
}
