package com.model.database.queries.objects;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "StartEndDatesList")
public class StartEndDatesList {
	
	@XmlElement(name = "SEDList")
	public List<StartEndDates> SEDList = new ArrayList<StartEndDates>();

}