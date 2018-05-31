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

import com.vividsolutions.jts.geom.MultiLineString;

public final class CountyTripMap {

  private int id;
  private String agencyId;
  private String agencyId_def;
  private String tripId; 
  private String routeId;
  private String countyId;
  private String regionId;
  private String serviceId;
  private double length;
  private int tlength; 
  private int stopscount;
  private MultiLineString shape;
  private String uid;
  private County county;
    
      
  public CountyTripMap() {	
}

public CountyTripMap(CountyTripMap a) {
    this.id = a.id;
    this.agencyId = a.agencyId;
    this.agencyId_def = a.agencyId_def;
    this.tripId = a.tripId;
    this.routeId = a.routeId;
    this.countyId = a.countyId;
    this.length = a.length;
    this.tlength = a.tlength;
    this.shape = a.shape;
    this.uid = a.uid;
    this.county = a.county;
    this.regionId = a.regionId;
  }

  public int getId() {
    return id;
  }
  
  public void setId(int id) {
	    this.id = id;
	  }
  
  public String getTripId() {
	    return tripId;
	  }
  
  public void setTripId(String tripId) {
	    this.tripId= tripId;
	  }
  
  public String getRegionId() {
	    return regionId;
	  }

public void setRegionId(String regionId) {
	    this.regionId= regionId;
	  }
  
  public String getRouteId() {
		return routeId;
	}

	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}
  
  public String getagencyId() {
    return agencyId;
  }
  
  public void setagencyId(String agencyId) {
    this.agencyId = agencyId;
  }
  
  public String getagencyId_def() {
	    return agencyId;
	  }
	  
  public void setagencyId_def(String agencyId_def) {
    this.agencyId_def = agencyId_def;
  }
  
  public String getCountyId() {
    return countyId;
  }

  public void setCountyId(String countyId) {
    this.countyId = countyId;
  }
  
  public String getServiceId() {
	    return serviceId;
	  }

  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
  }
  
  public int getStopscount() {
	    return stopscount;
	  }

  public void setStopscount(int stopscount) {
    this.stopscount = stopscount;
  }
  
  public double getLength() {
	    return length;
	  }

 public void setLength(double length) {
  this.length = length;
 	 }
 
 public int getTlength() {
	    return tlength;
	  }

public void setTlength(int tlength) {
this.tlength = tlength;
	 }

  public MultiLineString getShape(){
	  return shape;
  }
  
  public void setShape(MultiLineString shape){
	  this.shape = shape;
  }
  
  public String getUid(){
	  return uid;
  }
  
  public void setUid(String uid){
	  this.uid = uid;
  }
  
  public County getCounty() {
	return county;
}

public void setCounty(County county) {
	this.county = county;
}

public String toString() {
    return "<Trip " + this.tripId + "Intersects with county "+ this.countyId+ ">";
  }
}
