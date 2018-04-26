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
