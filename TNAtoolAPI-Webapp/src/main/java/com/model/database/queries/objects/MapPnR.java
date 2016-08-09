package com.model.database.queries.objects;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name = "MapPnR")
public class MapPnR {
	
	@XmlAttribute
	@JsonSerialize
	public int totalPnR;
	
	@XmlAttribute
	@JsonSerialize
	public int totalSpaces;
	 
	/*@XmlAttribute
	@JsonSerialize
	public int totalRoutes;*/
	
	@XmlElement(name = "MapPnrCounty")
    public Collection<MapPnrCounty> MapPnrCounty = new ArrayList<MapPnrCounty>();
}
