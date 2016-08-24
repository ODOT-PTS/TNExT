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
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name = "ClusterR")
public class ClusterR {
	
	
	@XmlAttribute
    @JsonSerialize
	public String name;
	
	@XmlAttribute
    @JsonSerialize
    public String id;
	
	@XmlAttribute
    @JsonSerialize
    public String size;
	
	@XmlAttribute
    @JsonSerialize
    public String minGap;
	
	@XmlAttribute
    @JsonSerialize
    public String maxGap;
	
	@XmlAttribute
    @JsonSerialize
    public String meanGap;
	
	@XmlAttribute
    @JsonSerialize
    public String ids;
	
	@XmlAttribute
    @JsonSerialize
    public String names;
	
	@XmlAttribute
    @JsonSerialize
    public String scoords;
	
	@XmlAttribute
    @JsonSerialize
    public String dcoords;
	
	@XmlAttribute
    @JsonSerialize
    public String distances;
	
	@XmlElement(name = "connections")
    public List<ClusterR> connections = new ArrayList<ClusterR>();	

}
