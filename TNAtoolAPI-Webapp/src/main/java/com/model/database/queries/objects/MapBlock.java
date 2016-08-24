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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name = "MapB")
public class MapBlock {
	@XmlAttribute
	@JsonSerialize
	public String ID;
	
	@XmlAttribute
	@JsonSerialize
	public long Population;
	
	@XmlAttribute
	@JsonSerialize
	public long Rac;
	
	@XmlAttribute
	@JsonSerialize
	public long Wac;
	
	@XmlAttribute
	@JsonSerialize
	public String Type;
	
	@XmlAttribute
	@JsonSerialize
	public long LandArea;
	
	@XmlAttribute
	@JsonSerialize
	public long Density;
	
	@XmlAttribute
	@JsonSerialize
	public long RacDensity;
	
	@XmlAttribute
	@JsonSerialize
	public long WacDensity;
	
	@XmlAttribute
	@JsonSerialize
	public double Lat;
	
	@XmlAttribute
	@JsonSerialize
	public double Lng;
	
	@XmlAttribute
	@JsonSerialize
	public String County;
	
	/*@XmlAttribute
	@JsonSerialize
	public TitleVIDataFloat Title6;*/
	
}
