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
