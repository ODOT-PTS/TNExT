// Copyright (C) 2015 Oregon State University - School of Mechanical,Industrial and Manufacturing Engineering 
//   This file is part of Transit Network Analysis Software Tool.
//
//    Transit Network Analysis Software Tool is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Transit Network Analysis Software Tool is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU  General Public License for more details.
//
//    You should have received a copy of the GNU  General Public License
//    along with Transit Network Analysis Software Tool.  If not, see <http://www.gnu.org/licenses/>.

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
