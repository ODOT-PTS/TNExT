package com.model.database.queries.objects;


public class GeoArea {
	private int type;
	private String typeName;	
	private String Id;
	private String name;
	private long population;    
    private long landarea;
    private long waterarea;
    
    
    
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
}
