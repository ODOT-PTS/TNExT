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

public final class GeoStopRouteMap {

  private int id;
  private String agencyId;  
  private String agencyId_def;
  private String stopId;  
  private String routeId;  
  private GeoStop stop;  
      
  public GeoStopRouteMap() {	
}

public GeoStopRouteMap(GeoStopRouteMap a) {
    this.id = a.id;
    this.agencyId = a.agencyId;
    this.stopId = a.stopId;    
    this.routeId = a.routeId;
  }

  public int getId() {
    return id;
  }
  
  public String getrouteId() {
	    return routeId;
	  }
  
  public void setrouteId(String routeId) {
	    this.routeId= routeId;
	  }
  
  public GeoStop getStop(){
	  return stop;
  }
  
  public void setStop(GeoStop stop) {
      this.agencyId_def = stop.getAgencyId();
      this.stopId = stop.getStopId();
  }
  
  public void setId(int id) {
    this.id = id;
  }

  public String getagencyId() {
    return agencyId;
  }
  public String getagencyId_def() {
	    return agencyId_def;
	  }
  public void setagencyId(String agencyId) {
    this.agencyId = agencyId;
  }
  public void setagencyId_def(String agencyId_def) {
	    this.agencyId_def = agencyId_def;
	  }

  public String getstopId() {
    return stopId;
  }

  public void setstopId(String stopId) {
    this.stopId = stopId;
  }
  
  public String toString() {
    return "<Stop " + this.stopId + "Is Served by "+ this.routeId+ ">";
  }
}
