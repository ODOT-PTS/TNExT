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


public class NetworkCluster {
	public int clusterId;	
    public long clusterSize;    
    public List<String> agencyIds = new ArrayList<String>();
    public List<String> agencyNames = new ArrayList<String>();
        
    public long getClusterSize() {
		return clusterSize;
	}

	public void setClusterSize(long clusterSize) {
		this.clusterSize = clusterSize;
	}
	
	public List<String> getAgencyIds() {
		return agencyIds;
	}

	public void setAgencyIds(List<String> agencyIds) {
		this.agencyIds = agencyIds;
	}

	public List<String> getAgencyNames() {
		return agencyNames;
	}

	public void setAgencyNames(List<String> agencyNames) {
		this.agencyNames = agencyNames;
	}
	
	public boolean addAgency(String agencyId, String agencyName){
		boolean response = false;
		if (!agencyIds.contains(agencyId)){
			agencyIds.add(agencyId);
			agencyNames.add(agencyName);
			clusterSize++;
			response = true;
		}
		return response;
		
	}
	
	public boolean addAgencyCluster(agencyCluster cluster){
		boolean response = false;
		if (agencyIds.size()>0){
			if (agencyIds.contains(cluster.agencyId)){
				response = true;
			} else {
				test: for (String id: cluster.agencyIds){
					if (agencyIds.contains(id)){
						response = true;
						break test;
					}
				}
			}
		} else {
			response = true;
		}
		if (response){
			if (!agencyIds.contains(cluster.agencyId)){
				agencyIds.add(cluster.agencyId);
				agencyNames.add(cluster.agencyName);
				clusterSize++;
			}
			for (int i=0; i<cluster.agencyIds.size(); i++){
				if (!agencyIds.contains(cluster.agencyIds.get(i))){
					agencyIds.add(cluster.agencyIds.get(i));
					agencyNames.add(cluster.agencyNames.get(i));
					clusterSize++;
				}			
			}		
		}
		return response;
	}
}
