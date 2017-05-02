package com.webapp.api;

import javax.xml.bind.annotation.XmlAttribute;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class HeatMap3 {
	
	@XmlAttribute
    @JsonSerialize
	String Date;
	
	@XmlAttribute
    @JsonSerialize
    int total;
	
	@XmlAttribute
    @JsonSerialize
	int active;
	
	@XmlAttribute
    @JsonSerialize
	int tripcount;
}

