package com.model.database.queries.objects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name = "StartEndDate")
public class StartEndDates {
	
	@XmlAttribute
    @JsonSerialize
	public String Agency;
	
	@XmlAttribute
    @JsonSerialize
	public String Startdate;
	
	@XmlAttribute
	@JsonSerialize
	public String Enddate;
	
	@XmlAttribute
    @JsonSerialize
	public String Startdateunion;
	
	@XmlAttribute
	@JsonSerialize
	public String Enddateunion;
	
	
}
