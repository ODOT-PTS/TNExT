package com.model.database.queries.objects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement (name = "PopServedMetrics")
public class PopServedMetrics {
	@XmlAttribute
	@JsonSerialize
	public int popServed;
	
	@XmlAttribute
	@JsonSerialize
	public double popAtLOS;
	
	@XmlAttribute
	@JsonSerialize
	public double popWithinX;
	
	@XmlAttribute
	@JsonSerialize
	public String areaID;
}
