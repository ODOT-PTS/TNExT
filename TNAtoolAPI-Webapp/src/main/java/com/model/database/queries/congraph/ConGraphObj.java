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

package com.model.database.queries.congraph;
/*
 * This object is used to store the connections for
 * a given agency to all the other agencies. The object
 * is composed of 2 nodes and an edge in between. The 
 * nodes are the agencies and edge is the number of 
 * connections between the two agencies. 
 * 06/06/2016. PB.
 *  
 */

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.model.database.queries.objects.TransitConnection;

@XmlRootElement( name = "ConGraphObj")
public class ConGraphObj {
	@XmlAttribute
	@JsonSerialize
	public String a1ID; // ID of Agency A.
	
	@XmlAttribute
	@JsonSerialize
	public String a2ID; // ID of Agency B.
	
	@XmlAttribute
	@JsonSerialize
	public String a1name;  // Name of Agency A.
	
	@XmlAttribute
	@JsonSerialize
	public String a2name;  // Name of Agency B.
	
	@XmlElement( name = "connections")
	public TransitConnection connections; // connection between Agency A and Agency B.	
		
	@Override
	public boolean equals (Object obj){
		if (obj == null)
	        return false;
	    if (!ConGraphObj.class.isAssignableFrom(obj.getClass()))
	        return false;
	    final ConGraphObj other = (ConGraphObj) obj;
	    if ( (this.a1ID.equals(other.a2ID) && this.a2ID.equals(other.a1ID))
	    		|| (this.a1ID.equals(other.a1ID) && this.a2ID.equals(other.a2ID)) )
			return true;
	    return false;
	}
	
	@Override
    public int hashCode() {
        return a1ID.hashCode()+a2ID.hashCode()+connections.size;
    }
	
}
