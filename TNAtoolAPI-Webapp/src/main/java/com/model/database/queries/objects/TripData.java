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


	