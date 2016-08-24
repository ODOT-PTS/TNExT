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
