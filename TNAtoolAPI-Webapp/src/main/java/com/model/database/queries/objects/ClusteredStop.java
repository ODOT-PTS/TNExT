package com.model.database.queries.objects;

import java.util.List;

public class ClusteredStop {
	public String name;
	public String id;
	public String agencyId;
	public List<String> agencies;
	public List<String> agencyNames;
	public double lat;
	public double lon;
	public String county;
	public String urban;
	public String odotregion;
	public String odotregionname;
	public long urbanPop;
	public List<String> routes;
	public int visits;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAgencyId() {
		return agencyId;
	}
	public void setAgencyId(String agencyId) {
		this.agencyId = agencyId;
	}
	public List<String> getAgencies() {
		return agencies;
	}
	public void setAgencies(List<String> agencies) {
		this.agencies = agencies;
	}
	public void addAgency(String agency){
		agencies.add(agency);
	}
	public List<String> getAgencyNames() {
		return agencyNames;
	}
	public void setAgencyNames(List<String> agencyNames) {
		this.agencyNames = agencyNames;
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
	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
		this.county = county;
	}
	public String getUrban() {
		return urban;
	}
	public void setUrban(String urban) {
		this.urban = urban;
	}
	public String getOdotregion() {
		return odotregion;
	}
	public void setOdotregion(String odotregion) {
		this.odotregion = odotregion;
	}
	public String getOdotregionname() {
		return odotregionname;
	}
	public void setOdotregionname(String odotregionname) {
		this.odotregionname = odotregionname;
	}
	public long getUrbanPop() {
		return urbanPop;
	}
	public void setUrbanPop(long urbanPop) {
		this.urbanPop = urbanPop;
	}
	public List<String> getRoutes() {
		return routes;
	}
	public void setRoutes(List<String> routes) {
		this.routes = routes;
	}
	public void addRoute(String route){
		routes.add(route);
	}
	public int getVisits() {
		return visits;
	}
	public void setVisits(int visits) {
		this.visits = visits;
	}
	public boolean equals(Object o) {
		  if (!(o instanceof ClusteredStop)) {
		    return false;
		  }
		  ClusteredStop other = (ClusteredStop) o;
		  return agencyId.equals(other.agencyId) && id.equals(other.id);
		}
}
