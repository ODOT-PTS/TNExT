package com.model.database.queries.objects;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name = "MapTR")
public class MapTransit {
	@XmlAttribute
	@JsonSerialize
	public String TotalStops;
	
	@XmlAttribute
	@JsonSerialize
	public String TotalRoutes;
	
	@XmlAttribute
	@JsonSerialize
	public String AverageFare;
	
	@XmlAttribute
	@JsonSerialize
	public String MedianFare;
	
	@XmlElement(name = "MapAL")
    public Collection<MapAgency> MapAL = new ArrayList<MapAgency>();
	
	@XmlElement(name = "MapBL")
    public Collection<String> MapBL = new ArrayList<String>();
	
	@XmlElement(name = "MapBLS")
    public Collection<Integer> MapBLS = new ArrayList<Integer>();
}
