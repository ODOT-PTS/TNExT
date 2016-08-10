package com.model.database.queries.objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "MapD")
public class MapDisplay {
	
	@XmlElement(name="MapTR")	
	public MapTransit MapTR;
	
	@XmlElement(name="MapG")	
	public MapGeo MapG;
	
	@XmlElement(name="MapPnR")	
	public MapPnR MapPnR;
}

