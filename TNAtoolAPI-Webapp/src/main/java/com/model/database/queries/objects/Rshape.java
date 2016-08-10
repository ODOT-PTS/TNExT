package com.model.database.queries.objects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name = "shapes")
public class Rshape {
	
	@XmlAttribute
	@JsonSerialize
	public String points;
	
	@XmlAttribute
	@JsonSerialize
	public String agency;
	
	@XmlAttribute
	@JsonSerialize
	public String agencyName;
	
	@XmlAttribute
	@JsonSerialize
	public String headSign;
	
	@XmlAttribute
	@JsonSerialize
	public double length;
	
	@XmlAttribute
	@JsonSerialize
	public double estlength;
	
	@XmlAttribute
	@JsonSerialize
	public String description;

}
