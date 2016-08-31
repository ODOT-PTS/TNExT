package com.model.database.queries.objects;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DBList")
public class DBList {

	@XmlElement(name = "DBelement")
    public Collection<String> DBelement = new ArrayList<String>();
	
	@XmlElement(name = "DBid")
    public Collection<String> DBid = new ArrayList<String>();
	
}	


