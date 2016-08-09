package com.model.database.queries.objects;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name = "MapG")
public class MapGeo {
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
	
	@XmlAttribute
	@JsonSerialize
	public int TotalBlocks;
	
	@XmlAttribute
	@JsonSerialize
	public int TotalTracts;
	
	@XmlAttribute
	@JsonSerialize
	public long TotalLandArea;
	
	@XmlAttribute
	@JsonSerialize
	public TitleVIDataFloat TitleVI;
	
	@XmlElement(name = "MapCL")
    public Collection<MapCounty> MapCL = new ArrayList<MapCounty>();
}
