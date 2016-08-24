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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.onebusaway.gtfs.model.AgencyAndId;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.model.database.onebusaway.gtfs.hibernate.objects.ext.StopExt;


@XmlRootElement(name = "Stop")
@XmlAccessorType(XmlAccessType.FIELD)
public class StopType {	
		

    public StopType() {
    }

    public StopType(StopExt stop) {
        this.id = stop.getId();
        this.stopLat = stop.getLat();
        this.stopLon = stop.getLon();
        this.stopCode = stop.getCode();
        this.stopName = stop.getName();
        this.stopDesc = stop.getDesc();
        this.zoneId = stop.getZoneId();
        this.stopUrl = stop.getUrl();
        this.locationType = stop.getLocationType();
        this.parentStation = stop.getParentStation();
        // this.stopTimezone = stop.getTimezone();s
        this.wheelchairBoarding = stop.getWheelchairBoarding();
        this.direction = stop.getDirection();
    }

    public StopType(StopExt stop, Boolean extended) {
        this.id = stop.getId();
        this.stopLat = stop.getLat();
        this.stopLon = stop.getLon();
        this.stopCode = stop.getCode();
        this.stopName = stop.getName();
        if (extended != null && extended.equals(true)) {
            this.stopDesc = stop.getDesc();
            this.zoneId = stop.getZoneId();
            this.stopUrl = stop.getUrl();
            this.locationType = stop.getLocationType();
            this.parentStation = stop.getParentStation();
            // this.stopTimezone = stop.getTimezone();
            this.wheelchairBoarding = stop.getWheelchairBoarding();
        }
    }

    @XmlJavaTypeAdapter(AgencyAndIdAdapter.class)
    @JsonSerialize
    AgencyAndId id;

    @JsonSerialize
    String stopName;

    @JsonSerialize
    Double stopLat;

    @JsonSerialize
    Double stopLon;

    @JsonSerialize
    String stopCode;

    @JsonSerialize
    String stopDesc;

    @JsonSerialize
    String zoneId;

    @JsonSerialize
    String stopUrl;

    @JsonSerialize
    Integer locationType;

    @JsonSerialize
    String parentStation;

    @JsonSerialize
    Integer wheelchairBoarding;

    @JsonSerialize
    String direction;

    @XmlElements(value = @XmlElement(name = "route"))
    @JsonSerialize
    List<AgencyAndId> routes;

}
