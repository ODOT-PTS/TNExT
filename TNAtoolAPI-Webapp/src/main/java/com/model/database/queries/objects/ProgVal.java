package com.model.database.queries.objects;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name = "ProgVal")
public class ProgVal {
	
    @JsonSerialize
	public int progVal;
}