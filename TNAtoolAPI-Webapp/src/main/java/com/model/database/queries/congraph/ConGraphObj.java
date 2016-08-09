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
