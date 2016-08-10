package com.model.database.queries.objects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name="Stoptime")
public class Stoptime {
	
	@XmlAttribute
    @JsonSerialize
	public String StopTime;
	
	@XmlAttribute
    @JsonSerialize
	public String StopName;
	
	@XmlAttribute
    @JsonSerialize
    public String StopId;
}
