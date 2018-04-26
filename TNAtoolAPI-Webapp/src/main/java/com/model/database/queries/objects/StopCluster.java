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

import java.util.ArrayList;
import java.util.List;

public class StopCluster implements Comparable<StopCluster> {
	public String clid;	
	public List<String>agencies;
	public List<String>anames;
	public List<String>cnames;
	public List<String>unames;
	public List<String>rnames;
	public List<String>regions;
	public double lat;
	public double lon;
	public long upop;
	public List<String>routes;
	public int size;
	public int visits;
	public List<ClusteredStop>stops = new ArrayList<ClusteredStop>();
	
	public String getClid() {
		return clid;
	}
	public void setClid(String clid) {
		this.clid = clid;
	}
	public List<String> getAgencies() {
		return agencies;
	}
	public void setAgencies(List<String> agencies) {
		this.agencies = agencies;
	}
	public List<String> getAnames() {
		return anames;
	}
	public void setAnames(List<String> anames) {
		this.anames = anames;
	}
	public List<String> getCnames() {
		return cnames;
	}
	public void setCnames(List<String> cnames) {
		this.cnames = cnames;
	}
	public List<String> getUnames() {
		return unames;
	}
	public void setUnames(List<String> unames) {
		this.unames = unames;
	}
	public List<String> getRnames() {
		return rnames;
	}
	public void setRnames(List<String> rnames) {
		this.rnames = rnames;
	}
	public List<String> getRegions() {
		return regions;
	}
	public void setRegions(List<String> regions) {
		this.regions = regions;
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
	public long getUpop() {
		return upop;
	}
	public void setUpop(long upop) {
		this.upop = upop;
	}
	public List<String> getRoutes() {
		return routes;
	}
	public void setRoutes(List<String> routes) {
		this.routes = routes;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getVisits() {
		return visits;
	}
	public void setVisits(int visits) {
		this.visits = visits;
	}
	public List<ClusteredStop> getStops() {
		return stops;
	}
	public void addStop(ClusteredStop stop){
		stops.add(stop);
	}
	public void setStops(List<ClusteredStop> stops) {
		this.stops = stops;
	}
	public boolean removeStops(List<ClusteredStop> stoplist){
		boolean response = false;
		
		for (ClusteredStop stop: stoplist){
			response = response|stops.remove(stop);
		}
		return response;
	}
	public void syncParams(){
		size = 0;
		visits = 0;
		agencies = new ArrayList<String>();
		routes = new ArrayList<String>();
		if (!stops.isEmpty()){
			for (ClusteredStop stop: stops){
				size++;
				visits+=stop.getVisits();
				List<String> stoproutes = stop.getRoutes();
				for (String route: stoproutes){
					if (!routes.contains(route))
						routes.add(route);
				}			
				List<String> stopagencies = stop.getAgencies();
				for (String agency: stopagencies){
					if (!agencies.contains(agency))
						agencies.add(agency);
				}}}		
	}
	//this method synchronizes parameters not used in sorting clusters
	public void syncOtherParams(){
		anames = new ArrayList<String>();
		cnames = new ArrayList<String>();
		unames = new ArrayList<String>();
		rnames = new ArrayList<String>();
		regions = new ArrayList<String>();
		upop = 0;
		double clat = 0;
		double clon = 0;
		int count = 0;
		for (ClusteredStop stop: stops){
			for (String aname: stop.getAgencyNames()){
				if (!anames.contains(aname))
					anames.add(aname);
			}
			if (!cnames.contains(stop.getCounty()))
				cnames.add(stop.getCounty());
			if (!unames.contains(stop.getUrban())){
				unames.add(stop.getUrban());
				upop += stop.getUrbanPop();
			}				
			if (!rnames.contains(stop.getOdotregionname()))
				rnames.add(stop.getOdotregionname());
			if (!regions.contains(stop.getOdotregion()))
				regions.add(stop.getOdotregion());			
			clat +=stop.getLat();
			clon +=stop.getLon();
			count++;		
		}
		if (count>0){
			lat = clat/count;
			lon = clon/count;
		}		
	}
	
	@Override
	public int compareTo(StopCluster o) {
		if (this.agencies.size()==o.agencies.size()){
			if (this.routes.size()==o.routes.size()){
				if (this.visits==o.visits){
					if (this.size==o.size){
						return this.agencies.get(0).toString().compareTo(o.agencies.get(0).toString());
					} else return (this.stops.size()>o.stops.size() ? 1:-1);
				} else return (this.visits>o.visits ? 1:-1);
			} else return (this.routes.size()>o.routes.size() ? 1:-1);
		} else return (this.agencies.size()>o.agencies.size() ? 1:-1);		
	}
	
	public boolean equals(Object o) {
		  if (!(o instanceof StopCluster)) {
		    return false;
		  }
		  StopCluster other = (StopCluster) o;
		  return clid.equals(other.clid);
		}
}
