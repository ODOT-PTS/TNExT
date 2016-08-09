package com.model.database.queries.objects;

public class Tract {
	private String tractId;
	private String name;
	private String longname;
    private long population;
    private long housing;
    private double latitude;
    private double longitude;
    private long landarea;
    private long waterarea;   
    private int population2010;
    private int population2015;
    private int population2020;
    private int population2025;
    private int population2030;
    private int population2035;
    private int population2040;
    private int population2045;
    private int population2050;
    
    public Tract(){
    }

    public Tract(Tract t) {
		this.tractId = t.tractId;
		this.name = t.name;
		this.longname = t.longname;
		this.population = t.population;
		this.housing = t.housing;
		this.latitude = t.latitude;
		this.longitude = t.longitude;
		this.landarea = t.landarea;
		this.waterarea = t.waterarea;
		this.population2010 = t.population2010;
		this.population2015 = t.population2015;
		this.population2020 = t.population2020;
		this.population2025 = t.population2025;
		this.population2030 = t.population2030;
		this.population2035 = t.population2035;
		this.population2040 = t.population2040;
		this.population2045 = t.population2045;
		this.population2050 = t.population2050;
	}


	public String getTractId() {
		return tractId;
	}


	public void setTractId(String tractId) {
		this.tractId = tractId;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getLongname() {
		return longname;
	}


	public void setLongname(String longname) {
		this.longname = longname;
	}


	public long getPopulation() {
		return population;
	}


	public void setPopulation(long population) {
		this.population = population;
	}


	public long getHousing() {
		return housing;
	}


	public void setHousing(long housing) {
		this.housing = housing;
	}


	public double getLatitude() {
		return latitude;
	}


	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}


	public double getLongitude() {
		return longitude;
	}


	public void setLongitude(double longitude) {
		this.longitude = longitude;
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
	
	public void setPopulation2010(int population2010) {
        this.population2010 = population2010;
    }
    
    public Integer getPopulation2010() {
        return population2010;
    }
    
    public void setPopulation2015(int population2015) {
        this.population2015 = population2015;
    }
    
    public Integer getPopulation2015() {
        return population2015;
    }
    
    public void setPopulation2020(int population2020) {
        this.population2020 = population2020;
    }
    
    public Integer getPopulation2020() {
        return population2020;
    }
    
    public void setPopulation2025(int population2025) {
        this.population2025 = population2025;
    }
    
    public Integer getPopulation2025() {
        return population2025;
    }
    
    public void setPopulation2030(int population2030) {
        this.population2030 = population2030;
    }
    
    public Integer getPopulation2030() {
        return population2030;
    }
    
    public void setPopulation2035(int population2035) {
        this.population2035 = population2035;
    }
    
    public Integer getPopulation2035() {
        return population2035;
    }
    
    public void setPopulation2040(int population2040) {
        this.population2040 = population2040;
    }
    
    public Integer getPopulation2040() {
        return population2040;
    }
    
    public void setPopulation2045(int population2045) {
        this.population2045 = population2045;
    }
    
    public Integer getPopulation2045() {
        return population2045;
    }
    
    public void setPopulation2050(int population2050) {
        this.population2050 = population2050;
    }
    
    public Integer getPopulation2050() {
        return population2050;
    }
    
    public Integer getPopulation(int year) {
    	switch (year){
    		case 2010:
    			return getPopulation2010();
    		case 2015:
    			return getPopulation2015();
    		case 2020:
    			return getPopulation2020();
    		case 2025:
    			return getPopulation2025();
    		case 2030:
    			return getPopulation2030();
    		case 2035:
    			return getPopulation2035();
    		case 2040:
    			return getPopulation2040();
    		case 2045:
    			return getPopulation2045();
    		default:
    			return getPopulation2050();
    	}
    }
}
