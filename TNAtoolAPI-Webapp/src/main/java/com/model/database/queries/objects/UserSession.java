package com.model.database.queries.objects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name="UserSession")
public class UserSession {
	
	@XmlAttribute
    @JsonSerialize
	public String User;
}
