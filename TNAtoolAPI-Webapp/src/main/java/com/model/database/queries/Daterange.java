package com.model.database.queries;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name = "Daterange")
public class Daterange {

	@XmlAttribute
    @JsonSerialize
	public String feedname;
	
	@XmlAttribute
    @JsonSerialize
	public String agencynames;
	
	@XmlAttribute
    @JsonSerialize
	public String agencyids;
	
	@XmlAttribute
    @JsonSerialize
	public int startdate;
    
	@XmlAttribute
    @JsonSerialize
	public int enddate;
    
	@XmlAttribute
    @JsonSerialize
	public int eday;
	
	@XmlAttribute
    @JsonSerialize
	public int emonth;
	
	@XmlAttribute
    @JsonSerialize
	public int eyear;
    
	@XmlAttribute
    @JsonSerialize
	public int sday;
	
	@XmlAttribute
    @JsonSerialize
	public int smonth;
	
	@XmlAttribute
    @JsonSerialize
	public int syear;

}
