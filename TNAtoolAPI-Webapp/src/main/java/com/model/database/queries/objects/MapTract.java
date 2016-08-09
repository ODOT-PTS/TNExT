package com.model.database.queries.objects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name = "MapT")
public class MapTract {
	@XmlAttribute
	@JsonSerialize
	public String ID;
	
	@XmlAttribute
	@JsonSerialize
	public long Population;
	
	@XmlAttribute
	@JsonSerialize
	public long LandArea;
	
	@XmlAttribute
	@JsonSerialize
	public double Lat;
	
	@XmlAttribute
	@JsonSerialize
	public double Lng;
	
	@XmlAttribute
	@JsonSerialize
	public String County;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ID == null) ? 0 : ID.hashCode());
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
		MapTract other = (MapTract) obj;
		if (ID == null) {
			if (other.ID != null)
				return false;
		} else if (!ID.equals(other.ID))
			return false;
		return true;
	}		
}