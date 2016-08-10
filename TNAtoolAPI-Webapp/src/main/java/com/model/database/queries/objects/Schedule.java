package com.model.database.queries.objects;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Schedule")
public class Schedule {
	
	@XmlElement(name="schedules")	
    public List<TripSchedule> schedules = new ArrayList<TripSchedule>();
	
	@XmlElement(name="stops")	
    public List<Stoptime> stops = new ArrayList<Stoptime>();
}
