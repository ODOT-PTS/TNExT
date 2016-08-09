package com.model.database.queries.objects;


public class County {
	private String countyId;
	private String name;
	private long population;
    private long housing;
    private double latitude;
    private double longitude;
    private long landarea;
    private long waterarea;
    private String regionId;
    private String regionName;
    private int population2010;
    private int population2015;
    private int population2020;
    private int population2025;
    private int population2030;
    private int population2035;
    private int population2040;
    private int population2045;
    private int population2050;
    
	public County(){
	}

	public County(County c) {		
		this.countyId = c.countyId;
		this.name = c.name;
		this.population = c.population;
		this.housing = c.housing;
		this.latitude = c.latitude;
		this.longitude = c.longitude;
		this.landarea = c.landarea;
		this.waterarea = c.waterarea;
		this.regionId = c.regionId;
		this.regionName = c.regionName;
		this.population2010 = c.population2010;
		this.population2015 = c.population2015;
		this.population2020 = c.population2020;
		this.population2025 = c.population2025;
		this.population2030 = c.population2030;
		this.population2035 = c.population2035;
		this.population2040 = c.population2040;
		this.population2045 = c.population2045;
		this.population2050 = c.population2050;
	}

	public String getCountyId() {
		return countyId;
	}

	public void setCountyId(String countyId) {
		this.countyId = countyId;
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

	public String getRegionId() {
		return regionId;
	}

	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
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
