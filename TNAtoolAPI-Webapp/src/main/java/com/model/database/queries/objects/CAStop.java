/*
 *  This class in used to represent data for the connected stops
 *  in the Connected Agencies On-map Report
 *  
 */

package com.model.database.queries.objects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name = "CAStop")
public class CAStop {
	@XmlAttribute
	@JsonSerialize
	public String name;
	
	@XmlAttribute
	@JsonSerialize
	public String id;
	
	@XmlAttribute
	@JsonSerialize
	public String agencyId;
	
	@XmlAttribute
	@JsonSerialize
	public String agencyName;
	
	@XmlAttribute
	@JsonSerialize
	public String lat;
	
	@XmlAttribute
	@JsonSerialize
	public String lon;
}
