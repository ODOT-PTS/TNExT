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

@XmlRootElement(name = "GeoXR")
public class GeoXR {
	
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
    public String racWithinX;
	
	@XmlAttribute
    @JsonSerialize
    public String wacWithinX;
	
	@XmlAttribute
    @JsonSerialize
    public String UPopWithinX;
	
	@XmlAttribute
    @JsonSerialize
    public String RPopWithinX;

    @XmlAttribute
    @JsonSerialize
    public String ULandareaWithinX;

    @XmlAttribute
    @JsonSerialize
    public String RLandareaWithinX;

	@XmlAttribute
    @JsonSerialize
    public String UracWithinX;
	
	@XmlAttribute
    @JsonSerialize
    public String RracWithinX;
	
	@XmlAttribute
    @JsonSerialize
    public String RwacWithinX;
	
	@XmlAttribute
    @JsonSerialize
    public String UwacWithinX;
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
    public String racServed;
	
	@XmlAttribute
    @JsonSerialize
    public String wacServed;
	
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
    public String racUnServed;
	
	@XmlAttribute
    @JsonSerialize
    public String wacUnServed;
	
	@XmlAttribute
    @JsonSerialize
    public String UracServed;
	
	@XmlAttribute
    @JsonSerialize
    public String RracServed;
	
	@XmlAttribute
    @JsonSerialize
    public String UwacServed;
	
	@XmlAttribute
    @JsonSerialize
    public String RwacServed;
	
	
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
    public String UServiceStops;
	
	
	@XmlAttribute
    @JsonSerialize
    public String RServiceStops;
	
	
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
	@XmlAttribute
    @JsonSerialize
    public String racServedByService;
	
	@XmlAttribute
    @JsonSerialize
    public String UracServedByService;
	
	@XmlAttribute
    @JsonSerialize
    public String RracServedByService;
	
	@XmlAttribute
    @JsonSerialize
    public String racServedAtLoService;
	
	@XmlAttribute
    @JsonSerialize
    public String totalracServedAtLoService;
	
	@XmlAttribute
    @JsonSerialize
    public String totalwacServedAtLoService;
	
	
	@XmlAttribute
    @JsonSerialize
    public String UracServedAtLoService;
	
	@XmlAttribute
    @JsonSerialize
    public String RracServedAtLoService;
	@XmlAttribute
    @JsonSerialize
    public String wacServedByService;
	
	@XmlAttribute
    @JsonSerialize
    public String UwacServedByService;
	
	@XmlAttribute
    @JsonSerialize
    public String RwacServedByService;
	
	@XmlAttribute
    @JsonSerialize
    public String wacServedAtLoService;
	
	@XmlAttribute
    @JsonSerialize
    public String UwacServedAtLoService;
	
	@XmlAttribute
    @JsonSerialize
    public String RwacServedAtLoService;
	
	@XmlAttribute
    @JsonSerialize
	public String RacAtLoService;
 
	@XmlAttribute
    @JsonSerialize
	public String WacAtLoService;


}




