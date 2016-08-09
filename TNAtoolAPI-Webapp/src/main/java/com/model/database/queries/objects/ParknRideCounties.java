package com.model.database.queries.objects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name = "ParknRideCounty")
public class ParknRideCounties {
	
	@XmlAttribute
	@JsonSerialize
	public String countyId;
	
	@XmlAttribute
	@JsonSerialize
	public String cname;
	
	@XmlAttribute
	@JsonSerialize
	public String count; // Total number of P&R lots in the county
	
	@XmlAttribute
	@JsonSerialize
	public String spaces; // Total number of spaces is the county
	
	@XmlAttribute
	@JsonSerialize
	public String accessibleSpaces; // Total number of accessible spaces in the
									// county
}
