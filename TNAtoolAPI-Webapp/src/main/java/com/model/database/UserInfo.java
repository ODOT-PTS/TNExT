package com.model.database;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name = "UserInfo")
public class UserInfo {
	
	@XmlAttribute
    @JsonSerialize
	public String Firstname;
	
	@XmlAttribute
    @JsonSerialize
    public String Lastname;
	
	@XmlAttribute
    @JsonSerialize
    public String Username;
	
	@XmlAttribute
    @JsonSerialize
    public String Quota;
	
	@XmlAttribute
    @JsonSerialize
    public String Usedspace;
}
