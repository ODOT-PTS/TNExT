package com.model.database.queries.objects;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement (name = "MapPnrRecord")
public class MapPnrRecord {
	@XmlAttribute
	@JsonSerialize
	public String id;
	
	@XmlAttribute
	@JsonSerialize
	public String countyId;
	
	@XmlAttribute
	@JsonSerialize
	public String countyName;
	
	@XmlAttribute
	@JsonSerialize
	public String lat;
	
	@XmlAttribute
	@JsonSerialize
	public String lon;
	
	@XmlAttribute
	@JsonSerialize
	public String lotName;
	
	@XmlAttribute
	@JsonSerialize
	public String spaces;
	
	@XmlAttribute
	@JsonSerialize
	public String availability;
	
	@XmlAttribute
	@JsonSerialize
	public String transitSerives;
	
	@XmlElement(name = "MapPnrTr")
	public Collection<MapAgency> MapTransit = new ArrayList<MapAgency>();
	
	@XmlElement(name = "MapPnrRL")
    public Collection<MapRoute> MapPnrRL = new ArrayList<MapRoute>();

	@XmlElement(name = "MapPnrSL")
    public Collection<MapStop> MapPnrSL = new ArrayList<MapStop>();
}
