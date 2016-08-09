package com.model.database.queries.objects;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name = "ParknRideCountiesList")
public class ParknRideCountiesList {
	@XmlAttribute
    @JsonSerialize
    public String metadata;
	
	@XmlElement(name = "PnrCountiesList")
	public List<ParknRideCounties> PnrCountiesList = new ArrayList<ParknRideCounties>();
}
