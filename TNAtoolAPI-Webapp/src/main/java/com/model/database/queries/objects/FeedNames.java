package com.model.database.queries.objects;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="FeedNames")
public class FeedNames {
	
	@XmlElement(name="feeds")	
	public ArrayList<String> feeds = new ArrayList<String>(); 
	
	@XmlElement(name="names")	
	public ArrayList<String> names = new ArrayList<String>(); 
	
	@XmlElement(name="startdates")	
	public ArrayList<String> startdates = new ArrayList<String>(); 
	
	@XmlElement(name="enddates")	
	public ArrayList<String> enddates = new ArrayList<String>(); 
	
	@XmlElement(name="isPublic")	
	public ArrayList<String> isPublic = new ArrayList<String>(); 
	
	@XmlElement(name="ownerFirstname")	
	public ArrayList<String> ownerFirstname = new ArrayList<String>(); 

	@XmlElement(name="ownerLastname")	
	public ArrayList<String> ownerLastname = new ArrayList<String>(); 
	
	@XmlElement(name="ownerUsername")	
	public ArrayList<String> ownerUsername = new ArrayList<String>(); 
}
