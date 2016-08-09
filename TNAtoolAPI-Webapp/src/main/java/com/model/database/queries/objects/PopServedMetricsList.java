package com.model.database.queries.objects;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name = "PopServedMetricsList")
public class PopServedMetricsList {
	@XmlAttribute
	@JsonSerialize
	public String metadata;

	@XmlElement(name = "List")
	public ArrayList<PopServedMetrics> List = new ArrayList<PopServedMetrics>();
}
