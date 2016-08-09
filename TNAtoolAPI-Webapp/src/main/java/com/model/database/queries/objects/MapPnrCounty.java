package com.model.database.queries.objects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.Collection;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name = "MapPnrCounty")
public class MapPnrCounty {

	@XmlAttribute
	@JsonSerialize
	public String countyId;

	@XmlAttribute
	@JsonSerialize
	public String countyName;

	@XmlAttribute
	@JsonSerialize
	public String totalPnRs;

	@XmlAttribute
	@JsonSerialize
	public String totalSpaces;

	@XmlElement(name = "MapPnrRecords")
	public Collection<MapPnrRecord> MapPnrRecords = new ArrayList<MapPnrRecord>();
}