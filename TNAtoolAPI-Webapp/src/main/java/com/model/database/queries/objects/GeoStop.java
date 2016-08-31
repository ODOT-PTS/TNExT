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

import com.vividsolutions.jts.geom.Point;

import java.io.Serializable;

public final class GeoStop implements Serializable {

  private static final long serialVersionUID = 1L;
  
  private static final int MISSING_VALUE = -999;

  private String agencyId;
  private String stopId;
  private String name;
  private double lat;
  private double lon;
  private String code;
  private String desc;
  private String zoneId;
  private String url;
  private int locationType = 0;
  private String parentStation;
  private int wheelchairBoarding = 0;
  private String direction;
  private String timezone;  
  private int vehicleType = MISSING_VALUE;
  private String platformCode;  
  private String blockId;  
  private String placeId;
  private String regionId;
  private String congdistId;
  private Point location;
  private Urban urban;
  
  public GeoStop(){
	}  
  
public GeoStop(GeoStop obj) {
    this.agencyId = obj.agencyId;
    this.stopId = obj.stopId;
    this.code = obj.code;
    this.name = obj.name;
    this.desc = obj.desc;
    this.lat = obj.lat;
    this.lon = obj.lon;
    this.zoneId = obj.zoneId;
    this.url = obj.url;
    this.locationType = obj.locationType;
    this.parentStation = obj.parentStation;
    this.wheelchairBoarding = obj.wheelchairBoarding;
    this.direction = obj.direction;
    this.timezone = obj.timezone;
    this.vehicleType = obj.vehicleType;
    this.platformCode = obj.platformCode;
    this.blockId = obj.blockId;
    this.placeId = obj.placeId;
    this.regionId = obj.regionId;
    this.location = obj.location;
    this.congdistId = obj.congdistId;
    this.urban = obj.urban;
  }

  public String getAgencyId() {
    return agencyId;
  }

  public void setAgencyId(String agencyId) {
    this.agencyId = agencyId;
  }
  
  public String getStopId() {
	    return stopId;
	  }

  public void setStopId(String stopId) {
    this.stopId = stopId;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public double getLat() {
    return lat;
  }

  public void setLat(double lat) {
    this.lat = lat;
  }

  public double getLon() {
    return lon;
  }

  public void setLon(double lon) {
    this.lon = lon;
  }

  public String getZoneId() {
    return zoneId;
  }

  public void setZoneId(String zoneId) {
    this.zoneId = zoneId;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public int getLocationType() {
    return locationType;
  }

  public void setLocationType(int locationType) {
    this.locationType = locationType;
  }

  public String getParentStation() {
    return parentStation;
  }
  
  public void setBlockId(String blockId) {
    this.blockId = blockId;
  }

  public String getBlockId() {
    return blockId;
  }

  public void setParentStation(String parentStation) {
    this.parentStation = parentStation;
  }

  @Override
  public String toString() {
    return "<Stop " + this.stopId + ">";
  }

  public void setWheelchairBoarding(int wheelchairBoarding) {
    this.wheelchairBoarding = wheelchairBoarding;
  }

  public int getWheelchairBoarding() {
    return wheelchairBoarding;
  }

  public String getDirection() {
    return direction;
  }

  public void setDirection(String direction) {
    this.direction = direction;
  }

  public String getTimezone() {
    return timezone;
  }

  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }
  
  public String getPlaceId() {
	return placeId;
}

public void setPlaceId(String placeId) {
	this.placeId = placeId;
}

public void setRegionId(String regionId) {
	this.regionId = regionId;
}

public String getRegionId() {
	return regionId;
}

public Urban getUrban() {
	return urban;
}

public void setUrban(Urban urban) {
	this.urban = urban;
}

public String getCongdistId() {
	return congdistId;
}

public void setCongdistId(String congdistId) {
	this.congdistId = congdistId;
}

public Point getLocation() {
	return location;
}

public void setLocation(Point location) {
	this.location = location;
}

public boolean isVehicleTypeSet() {
    return vehicleType != MISSING_VALUE;
  }

  public int getVehicleType() {
    return vehicleType;
  }

  public void setVehicleType(int vehicleType) {
    this.vehicleType = vehicleType;
  }
  
  public void clearVehicleType() {
    vehicleType = MISSING_VALUE;
  }

  public String getPlatformCode() {
    return platformCode;
  }

  public void setPlatformCode(String platformCode) {
    this.platformCode = platformCode;
  }
  
  @Override
  public int hashCode() {
    return agencyId.hashCode() ^ stopId.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof TripData))
      return false;
    GeoStop other = (GeoStop) obj;
    if (!agencyId.equals(other.agencyId))
      return false;
    if (!stopId.equals(other.stopId))
      return false;
    return true;
  }
}
