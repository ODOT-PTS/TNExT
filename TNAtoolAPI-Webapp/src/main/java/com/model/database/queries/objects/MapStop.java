package com.model.database.queries.objects;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name = "MapS")
public class MapStop {
	@XmlAttribute
	@JsonSerialize
	public String Id;
	
	@XmlAttribute
	@JsonSerialize
	public String Name;
	
	@XmlAttribute
	@JsonSerialize
	public String Lat;
	
	@XmlAttribute
	@JsonSerialize
	public String Lng;
	
	@XmlAttribute
	@JsonSerialize
	public String AgencyId;
	
	@XmlElement(name = "MapRI")
    public Collection<String> MapRI = new ArrayList<String>();
	
	@XmlAttribute
	@JsonSerialize
	public int Frequency=0;
}
