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

package com.model.database.queries.util;

public class Types {

	private final static String[] GeoTypes = {
		"Counties", 
		"Census Tracts", 
		"Census Places", 
		"Urban Areas", 
		"ODOT Transit Regions", 
		"Congressional Districts",
		"States"};
	private final static String[] GeoTypeTripMapTables = {
		"census_counties_trip_map", 
		"census_tracts_trip_map", 
		"census_places_trip_map", 
		"census_urbans_trip_map", 
		"census_counties_trip_map", 
		"census_congdists_trip_map",
		"census_states_trip_map"};
	private final static String[] GeoTypeIdColumns = {
		"countyid", 
		"tractid", 
		"placeid", 
		"urbanid", 
		"regionid", 
		"congdistid",
		"stateid"};
	private final static String[] GeoTypeTableNames = {
		"census_counties", 
		"census_tracts", 
		"census_places", 
		"census_urbans", 
		"census_counties", 
		"census_congdists",
		"census_states"};
	private final static String[] GeoTypeNameColumns = {
		"cname", 
		"tname", 
		"pname", 
		"uname", 
		"regionname", 
		"cname",
		"sname"};
	
	public static String getAreaName(int type){
		if (type>GeoTypes.length-1 || type<0)
			type = 0;
		return GeoTypes[type];
	}
	
	public static String getTripMapTableName(int type){
		if (type>GeoTypes.length-1 || type<0)
			type = 0;
		return GeoTypeTripMapTables[type];
	}
	
	public static String getIdColumnName(int type){
		if (type>GeoTypes.length-1 || type<0)
			type = 0;
		return GeoTypeIdColumns[type];
	}
	
	public static String getTableName(int type){
		if (type>GeoTypes.length-1 || type<0)
			type = 0;
		return GeoTypeTableNames[type];
	}
	
	public static String getNameColumn(int type){
		if (type>GeoTypes.length-1 || type<0)
			type = 0;
		return GeoTypeNameColumns[type];
	}
	
}
