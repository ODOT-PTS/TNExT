package com.model.database.queries;

import javax.xml.bind.annotation.XmlAttribute;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Agencyselect {
	@XmlAttribute
	@JsonSerialize
    public String AgencyId;
	
	@XmlAttribute
	@JsonSerialize
    public String DefaultId;
	
	@XmlAttribute
	@JsonSerialize
	public String Agencyname;
	
	@XmlAttribute
	@JsonSerialize
	public Boolean Hidden;
	
	@XmlAttribute
	@JsonSerialize
	public String Feedname;

	@XmlAttribute
	@JsonSerialize
	public String StartDate;

	@XmlAttribute
	@JsonSerialize
	public String EndDate;	
}
