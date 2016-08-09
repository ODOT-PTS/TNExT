package com.model.database.queries.objects;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name = "PnrInCountyList")
public class PnrInCountyList {

	@XmlAttribute
	@JsonSerialize
	public String metadata;

	@XmlElement(name = "PnrInCountyList")
	public List<PnrInCounty> PnrCountiesList;

}
