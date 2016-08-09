package com.model.database.queries.objects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name = "MapR")
public class MapRoute {
	@XmlAttribute
	@JsonSerialize
	public String uid;
	
	@XmlAttribute
	@JsonSerialize
	public String Id;
	
	@XmlAttribute
	@JsonSerialize
	public String Name;
	
	@XmlAttribute
	@JsonSerialize
	public boolean hasDirection;
	
	@XmlAttribute
	@JsonSerialize
	public String Shape="";
	
	@XmlAttribute
	@JsonSerialize
	public String Shape0="";
	
	@XmlAttribute
	@JsonSerialize
	public String Shape1="";
	
	@XmlAttribute
	@JsonSerialize
	public float Length;
	
	@XmlAttribute
	@JsonSerialize
	public String AgencyId;
	
	@XmlAttribute
	@JsonSerialize
	public String Fare;
	
	@XmlAttribute
	@JsonSerialize
	public int Frequency;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((AgencyId == null) ? 0 : AgencyId.hashCode());
		result = prime * result + ((Id == null) ? 0 : Id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MapRoute other = (MapRoute) obj;
		if (AgencyId == null) {
			if (other.AgencyId != null)
				return false;
		} else if (!AgencyId.equals(other.AgencyId))
			return false;
		if (Id == null) {
			if (other.Id != null)
				return false;
		} else if (!Id.equals(other.Id))
			return false;
		return true;
	}	
}
