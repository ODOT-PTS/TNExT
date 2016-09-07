// Copyright (C) 2015 Oregon State University - School of Mechanical,Industrial and Manufacturing Engineering 
//   This file is part of Transit Network Analysis Software Tool.
//
//    Transit Network Analysis Software Tool is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Transit Network Analysis Software Tool is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU  General Public License for more details.
//
//    You should have received a copy of the GNU  General Public License
//    along with Transit Network Analysis Software Tool.  If not, see <http://www.gnu.org/licenses/>.

package com.model.database.queries.objects;


public class GeoArea {
	private int type;
	private String typeName;	
	private String Id;
	private String name;
	private long population;    
    private long landarea;
    private long waterarea;
    private long employment;
    private long employee;
    
    
    
	public GeoArea(){
	}

	public GeoArea(GeoArea c) {	
		this.type = c.type;
		this.typeName = c.typeName;
		this.Id = c.Id;
		this.name = c.name;
		this.population = c.population;		
		this.landarea = c.landarea;
		this.waterarea = c.waterarea;		
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getId() {
		return Id;
	}

	public void setId(String Id) {
		this.Id = Id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getPopulation() {
		return population;
	}

	public void setPopulation(long population) {
		this.population = population;
	}	

	public long getLandarea() {
		return landarea;
	}

	public void setLandarea(long landarea) {
		this.landarea = landarea;
	}

	public long getWaterarea() {
		return waterarea;
	}

	public void setWaterarea(long waterarea) {
		this.waterarea = waterarea;
	}	  
	public double getEmployment() {
		return employment;
	}

	public void setEmployment(long employment) {
		this.employment = employment;
	}	  
	public double getEmployee() {
		return employee;
	}

	public void setEmployee(long employee) {
		this.employee = employee;
	}	  

}


