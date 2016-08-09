package com.model.database.queries.objects;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name = "EmpDataL")
public class EmpDataList {
	
	@XmlAttribute
	@JsonSerialize
	public String metadata;

	@XmlElement(name = "EmpDataList")
	public List<EmpData> EmpDataList = new ArrayList<EmpData>();
	
}
