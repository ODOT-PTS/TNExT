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

import com.vividsolutions.jts.geom.Point;

public class Census {
	private String blockId;
	private String placeId;
	private String congdistId;
	private String regionId;
	private String urbanId;	
    private int population;
    private double latitude;
    private double longitude;
    private int landarea;
    private int waterarea;
    private String poptype;
    private Point location;
    private Urban urban;
    private int population2010;
    private int population2015;
    private int population2020;
    private int population2025;
    private int population2030;
    private int population2035;
    private int population2040;
    private int population2045;
    private int population2050;
    
    public Census(){    	
    }
    
	public Census(Census c) {
		super();
		this.blockId = c.blockId;
		this.placeId = c.placeId;
		this.congdistId = c.congdistId;
		this.regionId = c.regionId;
		this.urbanId = c.urbanId;
		this.population = c.population;
		this.latitude = c.latitude;
		this.longitude = c.longitude;
		this.landarea = c.landarea;
		this.waterarea = c.waterarea;
		this.poptype = c.poptype;
		this.location = c.location;
		this.urban = c.urban;
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


	public void setBlockId(String blockId) {
        this.blockId = blockId;
    }
    
    public String getBlockId() {
        return blockId;
    }  
    
    public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}
    
    public String getPlaceId() {
		return placeId;
	}

	public String getCongdistId() {
		return congdistId;
	}

	public void setCongdistId(String congdistId) {
		this.congdistId = congdistId;
	}

	public String getRegionId() {
		return regionId;
	}

	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}

	public String getUrbanId() {
		return urbanId;
	}

	public void setUrbanId(String urbanId) {
		this.urbanId = urbanId;
	}	
	
	public Urban getUrban() {
		return urban;
	}

	public void setUrban(Urban urban) {
		this.urban = urban;
	}

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    
    public int getLandarea() {
        return landarea;
    }
    
    public void setLandarea(int landarea) {
        this.landarea = landarea;
    }

    public void setWaterarea(int waterarea) {
        this.waterarea = waterarea;
    }
    
    public int getWaterarea() {
        return waterarea;
    }

    public void setPoptype(String poptype) {
        this.poptype = poptype;
    }
    
    public String getPoptype() {
        return poptype;
    }

    public Point getLocation() {
        return this.location;
    }

    public void setLocation(Point location) {
        this.location = location;
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
