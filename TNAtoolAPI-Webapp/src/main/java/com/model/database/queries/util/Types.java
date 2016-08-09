package com.model.database.queries.util;

public class Types {

	private final static String[] GeoTypes = {"Counties", "Census Tracts", "Census Places", "Urban Areas", "ODOT Transit Regions", "Congressional Districts"};
	private final static String[] GeoTypeTripMapTables = {"census_counties_trip_map", "census_tracts_trip_map", "census_places_trip_map", "census_urbans_trip_map", "census_counties_trip_map", "census_congdists_trip_map"};
	private final static String[] GeoTypeIdColumns = {"countyid", "tractid", "placeid", "urbanid", "regionid", "congdistid"};
	private final static String[] GeoTypeTableNames = {"census_counties", "census_tracts", "census_places", "census_urbans", "census_counties", "census_congdists"};
	private final static String[] GeoTypeNameColumns = {"cname", "tname", "pname", "uname", "regionname", "cname"};
	
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
