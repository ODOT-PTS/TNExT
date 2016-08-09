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
