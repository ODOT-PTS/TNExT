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
}
