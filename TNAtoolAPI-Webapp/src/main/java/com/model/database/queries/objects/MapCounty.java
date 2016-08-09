package com.model.database.queries.objects;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name = "MapC")
public class MapCounty {
	@XmlAttribute
	@JsonSerialize
	public String Id;
	
	@XmlAttribute
	@JsonSerialize
	public String Name;
	
	@XmlAttribute
	@JsonSerialize
	public long UrbanPopulation;
	
	@XmlAttribute
	@JsonSerialize
	public long RuralPopulation;
	
	@XmlAttribute
	@JsonSerialize
	public long Rac;
	
	@XmlAttribute
	@JsonSerialize
	public long Wac;
	
	@XmlElement(name = "MapTL")
    public Collection<MapTract> MapTL = new ArrayList<MapTract>();

	@XmlElement(name = "MapBL")
    public Collection<MapBlock> MapBL = new ArrayList<MapBlock>();
}
