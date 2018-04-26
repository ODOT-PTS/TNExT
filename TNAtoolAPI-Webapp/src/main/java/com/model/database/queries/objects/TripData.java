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

import com.vividsolutions.jts.geom.LineString;

import java.io.Serializable;

public class TripData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String id;
    private String agencyId;    
    private double length;
    private double estlength;    
    private LineString shape;  
    
    public TripData(){    	
    }

    public TripData(TripData t) {
		this.id = t.id;
		this.agencyId = t.agencyId;		
		this.length = t.length;
		this.estlength = t.estlength;
		this.shape = t.shape;
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
	
	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public double getEstlength() {
		return estlength;
	}

	public void setEstlength(double estlength) {
		this.estlength = estlength;
	}

	public LineString getShape() {
		return shape;
	}

	public void setShape(LineString shape) {
		this.shape = shape;
	}
	
	  @Override
	  public int hashCode() {
	    return agencyId.hashCode() ^ id.hashCode();
	  }

	  @Override
	  public boolean equals(Object obj) {
	    if (this == obj)
	      return true;
	    if (obj == null)
	      return false;
	    if (!(obj instanceof TripData))
	      return false;
	    TripData other = (TripData) obj;
	    if (!agencyId.equals(other.agencyId))
	      return false;
	    if (!id.equals(other.id))
	      return false;
	    return true;
	  }
}


	