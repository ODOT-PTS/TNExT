package com.model.database.queries.objects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name = "Centroid")
public class Centroid {
	
	@XmlAttribute
    @JsonSerialize
	private String id;
	
	@XmlAttribute
    @JsonSerialize
	private int population;
	
	@XmlAttribute
    @JsonSerialize
	private double latitude;
	
	@XmlAttribute
    @JsonSerialize
	private double longitude;
        
    public void setcentroid(Census census) {
        this.id = census.getBlockId();
        this.population = census.getPopulation();
        this.latitude = census.getLatitude();
        this.longitude = census.getLongitude();
    }
}
