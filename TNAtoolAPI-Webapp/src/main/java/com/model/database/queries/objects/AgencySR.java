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

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name = "AgencySR")
public class AgencySR {
	
	
	@XmlAttribute
    @JsonSerialize
	public String AgencyName;
	
	@XmlAttribute
    @JsonSerialize
    public String AgencyId;
	
	@XmlAttribute
    @JsonSerialize
    public String URL;
	
	@XmlAttribute
    @JsonSerialize
    public String Phone;
	
	@XmlAttribute
    @JsonSerialize
    public String FareURL;
	
	@XmlAttribute
    @JsonSerialize
    public String ServiceMiles;
	
	@XmlAttribute
    @JsonSerialize
    public String RouteMiles;
	
	@XmlAttribute
    @JsonSerialize
    public String PopServed;
	
	@XmlAttribute
    @JsonSerialize
    public String AreaServed;
	
	@XmlAttribute
    @JsonSerialize
    public String AverageFare;
	
	@XmlAttribute
    @JsonSerialize
    public String MedianFare;
	
	@XmlAttribute
    @JsonSerialize
    public String MinFare;
	
	@XmlAttribute
    @JsonSerialize
    public String MaxFare;
	
	@XmlAttribute
    @JsonSerialize
    public String FeedName;
	
	@XmlAttribute
    @JsonSerialize
    public String FeedVersion;
	
	@XmlAttribute
    @JsonSerialize
    public String FeedStartDate;
	
	@XmlAttribute
    @JsonSerialize
    public String FeedEndDate;
	
	@XmlAttribute
    @JsonSerialize
    public String FeedPublisherName;
	
	@XmlAttribute
    @JsonSerialize
    public String FeedPublisherUrl;
	
	@XmlAttribute
    @JsonSerialize
    public String PlacesCount;
	
	@XmlAttribute
    @JsonSerialize
    public String CountiesCount;
	
	@XmlAttribute
    @JsonSerialize
    public String OdotRegionsCount;
	
	@XmlAttribute
    @JsonSerialize
    public String UrbansCount;
	
	@XmlAttribute
    @JsonSerialize
    public String CongDistsCount;
	
	@XmlAttribute
    @JsonSerialize
    public String StopsCount;
	
	@XmlAttribute
    @JsonSerialize
    public String RoutesCount;
    
    /*@XmlElementWrapper
    public List<RouteType> routes;*/
	
}
