/*
 * This class is used in ConGraphObj.java
 */
package com.model.database.queries.objects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement( name = "TransitConnection")
public class TransitConnection{
	@XmlAttribute
	@JsonSerialize
	public int size; // number of connection from the anchor agency (source) to the destination agency.
	
	public TransitConnection () {
		
	}
	
	public TransitConnection (int size) {
		this.size = size;
	}
}