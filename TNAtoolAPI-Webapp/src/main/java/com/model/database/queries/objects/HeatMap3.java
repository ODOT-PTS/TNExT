package com.model.database.queries.objects;

import javax.xml.bind.annotation.XmlAttribute;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class HeatMap3 {
	
	@XmlAttribute
    @JsonSerialize
	public String Date;
	
	@XmlAttribute
    @JsonSerialize
    public int total;
	
	@XmlAttribute
    @JsonSerialize
    public int active;
	
	@XmlAttribute
    @JsonSerialize
    public int tripcount;
}

