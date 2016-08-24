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

@XmlRootElement(name = "GeoXR")
public class GeoXR {
	@XmlAttribute
    @JsonSerialize
	public String metadata;
	
	@XmlAttribute
    @JsonSerialize
	public String AreaName;
	
	@XmlAttribute
    @JsonSerialize
	public String AreaLongName;
	
	@XmlAttribute
    @JsonSerialize
    public String AreaId;
	
	@XmlAttribute
    @JsonSerialize
    public String StopsPersqMile;
	
	@XmlAttribute
    @JsonSerialize
    public String PopWithinX;
	
	@XmlAttribute
    @JsonSerialize
    public String UPopWithinX;
	
	@XmlAttribute
    @JsonSerialize
    public String RPopWithinX;
	
	@XmlAttribute
    @JsonSerialize
    public String PopServedOver50k;
	
	@XmlAttribute
    @JsonSerialize
    public String TotalPopOver50k;
	
	@XmlAttribute
    @JsonSerialize
    public String TotalPopBelow50k;
	
	@XmlAttribute
    @JsonSerialize
    public String PopServedBelow50k;
	
	@XmlAttribute
    @JsonSerialize
    public String PopServed;
	
	@XmlAttribute
    @JsonSerialize
    public String UPopServed;
	
	@XmlAttribute
    @JsonSerialize
    public String RPopServed;
	
	@XmlAttribute
    @JsonSerialize
    public String PopUnServed;
	
	@XmlAttribute
    @JsonSerialize
    public String AverageFare;
	
	@XmlAttribute
    @JsonSerialize
    public String MinFare;
	
	@XmlAttribute
    @JsonSerialize
    public String MaxFare;
	
	@XmlAttribute
    @JsonSerialize
    public String MedianFare;
	
	@XmlAttribute
    @JsonSerialize
    public String HoursOfService;
	
	@XmlAttribute
    @JsonSerialize
    public String ServiceDays;
	
	@XmlAttribute
    @JsonSerialize
    public String ConnectedCommunities;
	
	@XmlAttribute
    @JsonSerialize
    public String ServiceMiles;
	
	@XmlAttribute
    @JsonSerialize
    public String ServiceMilesPersqMile;
	
	@XmlAttribute
    @JsonSerialize
    public String MilesofServicePerCapita;
	
	@XmlAttribute
    @JsonSerialize
    public String StopPerServiceMile;
	
	@XmlAttribute
    @JsonSerialize
    public String RouteMiles;
	
	@XmlAttribute
    @JsonSerialize
    public String ServiceStops;
	
	@XmlAttribute
    @JsonSerialize
    public String ServiceHours;
	
	@XmlAttribute
    @JsonSerialize
    public String PopServedByService;
	
	@XmlAttribute
    @JsonSerialize
    public String UPopServedByService;
	
	@XmlAttribute
    @JsonSerialize
    public String RPopServedByService;
	
	@XmlAttribute
    @JsonSerialize
    public String PopServedAtLoService;
	
	@XmlAttribute
    @JsonSerialize
    public String UPopServedAtLoService;
	
	@XmlAttribute
    @JsonSerialize
    public String RPopServedAtLoService;
}
