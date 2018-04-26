
// Copyright (C) 2015 Oregon State University - School of Mechanical,Industrial and Manufacturing Engineering 
//   This file is part of Transit Network Explorer Tool.
//
//    Transit Network Explorer Tool is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Transit Network Explorer Tool is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU  General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Transit Network Explorer Tool.  If not, see <http://www.gnu.org/licenses/>.

package com.model.database;

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
