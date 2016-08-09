package com.model.database.queries.congraph;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement( name = "Coordinate")
public class Coordinate {
	@XmlAttribute
	@JsonSerialize
	public double lat;
	
	@XmlAttribute
	@JsonSerialize
	public double lng;
	
	public Coordinate () {
		
	}
	
	public Coordinate (double lat, double lng){
		this.lat = lat;
		this.lng = lng;
	}
	
	@Override
	public String toString(){
		return "["+this.lat+","+this.lng+"]";
	}
}
