package com.model.database.queries.congraph;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement (name = "ConGraphAgencyGraphList") 
public class ConGraphAgencyGraphList {
	
	@XmlElement (name = "list")
	public List<ConGraphAgencyGraph> list = new ArrayList<ConGraphAgencyGraph>();

}
