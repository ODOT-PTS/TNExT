package com.model.database.queries.objects;

import java.util.List;


public class agencyCluster {
	public String agencyId;
	public String agencyName;
    public long clusterSize;
    public float minGap;
    public float maxGap;
    public float meanGap;
    public List<String> agencyIds;
    public List<String> agencyNames;
    public List<String> minGaps;
    public List<String> sourceStopNames;
    public List<String> destStopNames;
    public List<String> sourceStopCoords;
    public List<String> destStopCoords;
    public List<String> destStopIds;
    
    
    public agencyCluster(){    	
    }

	public String getAgencyId() {
		return agencyId;
	}

	public void setAgencyId(String agencyId) {
		this.agencyId = agencyId;
	}

	public String getAgencyName() {
		return agencyName;
	}

	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}

	public long getClusterSize() {
		return clusterSize;
	}

	public void setClusterSize(long clusterSize) {
		this.clusterSize = clusterSize;
	}

	public float getMinGap() {
		return minGap;
	}

	public void setMinGap(float minGap) {
		this.minGap = minGap;
	}

	public float getMaxGap() {
		return maxGap;
	}

	public void setMaxGap(float maxGap) {
		this.maxGap = maxGap;
	}

	public float getMeanGap() {
		return meanGap;
	}

	public void setMeanGap(float meanGap) {
		this.meanGap = meanGap;
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

	public List<String> getMinGaps() {
		return minGaps;
	}

	public void setMinGaps(List<String> minGaps) {
		this.minGaps = minGaps;
	}

	public List<String> getSourceStopNames() {
		return sourceStopNames;
	}

	public void setSourceStopNames(List<String> sourceStopNames) {
		this.sourceStopNames = sourceStopNames;
	}

	public List<String> getDestStopNames() {
		return destStopNames;
	}

	public void setDestStopNames(List<String> destStopNames) {
		this.destStopNames = destStopNames;
	}

	public List<String> getSourceStopCoords() {
		return sourceStopCoords;
	}

	public void setSourceStopCoords(List<String> sourceStopCoords) {
		this.sourceStopCoords = sourceStopCoords;
	}

	public List<String> getDestStopCoords() {
		return destStopCoords;
	}

	public void setDestStopCoords(List<String> destStopCoords) {
		this.destStopCoords = destStopCoords;
	}
	   
	public List<String> getDestStopIds() {
		return destStopIds;
	}

	public void setDestStopIds(List<String> destStopIds) {
		this.destStopCoords = destStopIds;
	}
}
