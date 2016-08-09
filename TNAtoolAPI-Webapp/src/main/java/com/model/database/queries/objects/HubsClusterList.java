package com.model.database.queries.objects;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement( name = "HubClusterList")
public class HubsClusterList {
	@XmlAttribute
    @JsonSerialize
    public String metadata;
	
	@XmlAttribute
    @JsonSerialize
    public List<String> keyAgecies;
	
	@XmlElement(name = "Clusters")
	public List<HubCluster> Clusters = new ArrayList<HubCluster>();

}
