package com.model.database.queries.congraph;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement( name = "AgencyCentroidList")
public class AgencyCentroidList {

	@XmlElement (name = "list")
	public List<AgencyCentroid> list = new ArrayList<AgencyCentroid>();
}
