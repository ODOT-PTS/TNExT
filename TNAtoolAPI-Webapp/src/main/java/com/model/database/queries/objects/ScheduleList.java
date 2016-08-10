package com.model.database.queries.objects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name="ScheduleList")
public class ScheduleList {
	
	@XmlElement(name="directions")	
    public Schedule[] directions = new Schedule[2];
	//public List <Schedule> directions = new ArrayList<Schedule>(2);
	
	@XmlAttribute
    @JsonSerialize
	public String metadata;
	
	@XmlAttribute
    @JsonSerialize
	public String Fare;
	
	@XmlAttribute
    @JsonSerialize
	public String Agency;
	
	@XmlAttribute
    @JsonSerialize
	public String Route;
}
