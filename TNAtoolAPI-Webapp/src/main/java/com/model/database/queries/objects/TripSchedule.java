package com.model.database.queries.objects;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="TripSchedule")
public class TripSchedule {
	@XmlElement(name="stoptimes")	
    public List<Stoptime> stoptimes = new ArrayList<Stoptime>();
	
	
}
