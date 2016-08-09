package com.model.database.queries.objects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name = "MapB")
public class MapBlock {
	@XmlAttribute
	@JsonSerialize
	public String ID;
	
	@XmlAttribute
	@JsonSerialize
	public long Population;
	
	@XmlAttribute
	@JsonSerialize
	public long Rac;
	
	@XmlAttribute
	@JsonSerialize
	public long Wac;
	
	@XmlAttribute
	@JsonSerialize
	public String Type;
	
	@XmlAttribute
	@JsonSerialize
	public long LandArea;
	
	@XmlAttribute
	@JsonSerialize
	public long Density;
	
	@XmlAttribute
	@JsonSerialize
	public long RacDensity;
	
	@XmlAttribute
	@JsonSerialize
	public long WacDensity;
	
	@XmlAttribute
	@JsonSerialize
	public double Lat;
	
	@XmlAttribute
	@JsonSerialize
	public double Lng;
	
	@XmlAttribute
	@JsonSerialize
	public String County;
	
	/*@XmlAttribute
	@JsonSerialize
	public TitleVIDataFloat Title6;*/
	
}
