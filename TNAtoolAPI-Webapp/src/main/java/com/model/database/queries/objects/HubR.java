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
import java.util.Collection;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.model.database.queries.util.StringUtils;


@XmlRootElement(name = "HubR")
public class HubR {
	
	
	@XmlAttribute
    @JsonSerialize
	public String clusterid;
	
	@XmlAttribute
    @JsonSerialize
	public String agencies;
	
	@XmlAttribute
    @JsonSerialize
	public String aNames;
	
	@XmlAttribute
    @JsonSerialize
	public String cNames;
	
	@XmlAttribute
    @JsonSerialize
	public String uNames;
	
	@XmlAttribute
    @JsonSerialize
	public String uPop;
	
	@XmlAttribute
    @JsonSerialize
	public String rNames;
	
	@XmlAttribute
    @JsonSerialize
	public String regions;
	
	@XmlAttribute
    @JsonSerialize
	public String lat;
	
	@XmlAttribute
    @JsonSerialize
	public String lon;
	
	@XmlAttribute
    @JsonSerialize
    public String routes;
	
	@XmlAttribute
    @JsonSerialize
    public String services;
	
	@XmlElement(name = "Stops")
    public Collection<StopCLR> StopCLR = new ArrayList<StopCLR>();
	
	public void addCluster(StopCluster cluster, int clusterId){
		this.clusterid = String.valueOf(clusterId);
		this.agencies = StringUtils.join(cluster.getAgencies(), "; ");
		this.aNames = StringUtils.join(cluster.getAnames(), "; ");
		this.cNames = StringUtils.join(cluster.getCnames(), "; ");
		this.uNames = StringUtils.join(cluster.getUnames(), "; ");
		this.rNames = StringUtils.join(cluster.getRnames(), "; ");
		this.regions = StringUtils.join(cluster.getRegions(), "; ");
		this.lat = String.valueOf(Math.round(cluster.getLat()*1E6)/1E6);
		this.lon = String.valueOf(Math.round(cluster.getLon()*1E6)/1E6);
		this.uPop = String.valueOf(cluster.getUpop());
		this.routes = StringUtils.join(cluster.routes, "; ");
		this.services = String.valueOf(cluster.visits);
		for (ClusteredStop instance: cluster.getStops()){
			StopCLR cstop = new StopCLR();
			cstop.agencyId = instance.getAgencyId();
			cstop.aNames = StringUtils.join(instance.getAgencyNames(), "; ");
			cstop.realAgencyIds = StringUtils.join(instance.getAgencies(), "; ");
			cstop.cName = instance.getCounty();
			cstop.uName = instance.getUrban();
			cstop.rName = instance.getOdotregionname();
			cstop.region = instance.getOdotregion();
			cstop.uPop = String.valueOf(instance.getUrbanPop());
			cstop.lat = String.valueOf(Math.round(instance.getLat()*1E6)/1E6);
			cstop.lon = String.valueOf(Math.round(instance.getLon()*1E6)/1E6);
			cstop.routeIds = StringUtils.join(instance.routes, "; ");
			cstop.services = String.valueOf(instance.getVisits());
			cstop.stopId = instance.id;
			cstop.stopName = instance.getName();
			this.StopCLR.add(cstop);
		}		
	}
}
