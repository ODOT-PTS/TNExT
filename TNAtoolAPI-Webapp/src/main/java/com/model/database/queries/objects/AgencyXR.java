package com.model.database.queries.objects;

//Copyright (C) 2015 Oregon State University - School of Mechanical,Industrial and Manufacturing Engineering 
//This file is part of Transit Network Analysis Software Tool.
//
//Transit Network Analysis Software Tool is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//Transit Network Analysis Software Tool is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU  General Public License for more details.
//
//You should have received a copy of the GNU  General Public License
//along with Transit Network Analysis Software Tool.  If not, see <http://www.gnu.org/licenses/>.

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name = "AgencyXR")
public class AgencyXR {
	
	@XmlAttribute
    @JsonSerialize
	public String metadata;
	
	@XmlAttribute
    @JsonSerialize
	public String AgencyName;
	
	@XmlAttribute
    @JsonSerialize
	public String AreaId;
	
	@XmlAttribute
    @JsonSerialize
	public String Type;
	
	
	@XmlAttribute
	@JsonSerialize
    public String AgencyId;
	
	@XmlAttribute
    @JsonSerialize
    public String ServiceMiles;
	
	@XmlAttribute
    @JsonSerialize
    public String RouteMiles;
	
	@XmlAttribute
    @JsonSerialize
    public String UPopWithinX;
	
	@XmlAttribute
    @JsonSerialize
    public String RPopWithinX;
	
	@XmlAttribute
    @JsonSerialize
    public String racWithinX;
	
	@XmlAttribute
    @JsonSerialize
    public String wacWithinX;
	
	@XmlAttribute
    @JsonSerialize
    public String StopCount;
	
	@XmlAttribute
    @JsonSerialize
    public String StopPerRouteMile;
	
	@XmlAttribute
    @JsonSerialize
    public String ServiceStops;
	
	@XmlAttribute
    @JsonSerialize
    public String UPopServedByService;
	
	@XmlAttribute
    @JsonSerialize
    public String RPopServedByService;
	
	@XmlAttribute
    @JsonSerialize
    public String racServedByService;
	
	@XmlAttribute
    @JsonSerialize
    public String wacServedByService;
	
	@XmlAttribute
    @JsonSerialize
    public String AreaServed; 
	
	@XmlAttribute
    @JsonSerialize
    public String HoursOfService;
	
	@XmlAttribute
    @JsonSerialize
    public String ServiceHours;
	
	@XmlAttribute
    @JsonSerialize
    public String ServiceDays;
}
