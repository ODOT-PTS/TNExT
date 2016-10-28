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

package com.model.database.queries;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.model.database.queries.flexibleReport.FlexRepPop;
import com.model.database.queries.objects.GeoArea;

public class FlexibleReportEventManager {

	public static List<GeoArea> getAreaList(String areaType, int dbindex)
			throws SQLException {
		List<GeoArea> output = new ArrayList<GeoArea>();

		Connection connection = PgisEventManager.makeConnection(dbindex);
		Statement stmt = connection.createStatement();
		String query = "";

		if (areaType.equals("state")) {
			query = "SELECT sum(landarea) as landarea, sum(waterarea) AS waterarea, sum(population) AS population "
					+ "	FROM census_counties";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				GeoArea i = new GeoArea();
				i.setId("41");
				i.setName("Oregon");
				i.setLandarea(rs.getLong("landarea"));
				i.setWaterarea(rs.getLong("waterarea"));
				i.setPopulation(rs.getLong("population"));
				output.add(i);
			}
		} else if (areaType.equalsIgnoreCase("county")) {
			query = "SELECT * FROM census_counties";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				GeoArea i = new GeoArea();
				i.setId(rs.getString("countyid"));
				i.setName(rs.getString("cname"));
				i.setLandarea(rs.getLong("landarea"));
				i.setWaterarea(rs.getLong("waterarea"));
				i.setPopulation(rs.getLong("population"));
				output.add(i);
			}
		} else if (areaType.equalsIgnoreCase("urban")) {
			query = "SELECT * FROM census_urbans";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				GeoArea i = new GeoArea();
				i.setId(rs.getString("urbanid"));
				i.setName(rs.getString("uname"));
				i.setLandarea(rs.getLong("landarea"));
				i.setWaterarea(rs.getLong("waterarea"));
				i.setPopulation(rs.getLong("population"));
				output.add(i);
			}
		} else if (areaType.equalsIgnoreCase("congDist")) {
			query = "SELECT * FROM census_congdists";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				GeoArea i = new GeoArea();
				i.setId(rs.getString("congdistid"));
				i.setName(rs.getString("cname"));
				i.setLandarea(rs.getLong("landarea"));
				i.setWaterarea(rs.getLong("waterarea"));
				i.setPopulation(rs.getLong("population"));
				output.add(i);
			}
		} else if (areaType.equalsIgnoreCase("odotRegion")) {
			query = " SELECT odotregionid AS regionid, odotregionname AS rname, sum(landarea) as landarea, "
					+ " sum(waterarea) AS waterarea, sum(population) AS population "
					+ "	FROM census_counties "
					+ "	GROUP BY odotregionid, odotregionname";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				GeoArea i = new GeoArea();
				i.setId(rs.getString("regionid"));
				i.setName(rs.getString("rname"));
				i.setLandarea(rs.getLong("landarea"));
				i.setWaterarea(rs.getLong("waterarea"));
				i.setPopulation(rs.getLong("population"));
				output.add(i);
			}
		} else if (areaType.equalsIgnoreCase("place")) {
			query = "SELECT * FROM census_places";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				GeoArea i = new GeoArea();
				i.setId(rs.getString("placeid"));
				i.setName(rs.getString("pname"));
				i.setLandarea(rs.getLong("landarea"));
				i.setWaterarea(rs.getLong("waterarea"));
				i.setPopulation(rs.getLong("population"));
				output.add(i);
			}
		}
		return output;
	}

	
	public static List<FlexRepPop> getFlexRepPop(int dbindex, String agencies, String[] date, String[] day, String popYear, String areas,int los, double sradius, 
			String areaType, String username, int minUrbanPop, int maxUrbanPop) throws SQLException{
		List<FlexRepPop> output = new ArrayList<FlexRepPop>();
		
    	agencies = "('" + agencies.replace(",", "','") + "')";
    	areas = "('" + areas.replace(",", "','") + "')";
    	
		Connection connection = PgisEventManager.makeConnection(dbindex);
		Statement stmt = connection.createStatement();
		
		String areaId = "";
		String Id = "";
		String mapTable = "";
		String table = ""; 
		String areaTypeName = "";
		if (areaType.equals("county")){
			areaId = "LEFT(blockid,5)";
			Id = "countyid";
			mapTable = "census_counties_trip_map";
			table = "census_counties";
			areaTypeName = "cname";
		}else if (areaType.equals("urban")){
			areaId = "urbanid";
			Id = "urbanid";
			mapTable = "census_urbans_trip_map";
			table = "census_urbans";
			areaTypeName = "uname";
		}else if (areaType.equals("congDist")){
			areaId = "congdistid";
			Id = "congdistid";
			mapTable = "census_congdists_trip_map";
			table = "census_congdists";
			areaTypeName = "cname";
		}else if (areaType.equals("place")){
			areaId = "placeid";
			Id = "placeid";
			mapTable = "census_places_trip_map";
			table = "census_places";
			areaTypeName = "pname";
		}else if (areaType.equals("state")){
			
		}else if (areaType.equals("odotRegion")){
			areaId = "regionid";
			Id = "regionid";
			mapTable = "census_counties_trip_map";
			table = "(SELECT '1' AS regionid UNION SELECT '2a' UNION SELECT '2b' UNION SELECT '3' UNION SELECT '4' UNION SELECT '5') AS census_regions ";
			areaTypeName = "'Region '||regionid";
		}
		
		String query = "WITH aids AS (SELECT id AS aid, name AS agencyname FROM gtfs_agencies WHERE id IN " + agencies + "), "

			+ "allstops AS (SELECT stops.agencyid AS agencyid_def, map.agencyid, map.stopid, stops.name AS stopname, stops.location "
			+ "FROM gtfs_stop_service_map AS map INNER JOIN gtfs_stops AS stops  "
			+ "ON map.stopid = stops.id AND agencyid_def = stops.agencyid "
			+ "WHERE map.agencyid IN " + agencies + ") , "
							
			+ "allblocks AS (SELECT blocks.blockid, poptype, " + areaId + " AS areaid, urbanid,  " + popYear + " AS population, "
			+ "stops.stopid, stops.stopname, stops.agencyid "
			+ "FROM census_blocks AS blocks JOIN allstops AS stops "
			+ "ON ST_DISTANCE(blocks.location, stops.location)< " + sradius + " "
			+ "WHERE " + areaId + " IN " + areas + "), "
							
			+ "svcids AS (";
		for (int i=0; i<date.length; i++){
	    	  query+= "(select serviceid_agencyid, serviceid_id from gtfs_calendars gc inner join aids on gc.serviceid_agencyid = aids.aid where startdate::int<="+date[i]+
	    			  " and enddate::int>="+date[i]+" and "+day[i]+" = 1 and serviceid_agencyid||serviceid_id not in (select serviceid_agencyid||serviceid_id from " +
	    			  "gtfs_calendar_dates where date='"+date[i]+"' and exceptiontype=2) union select serviceid_agencyid, "+
	    			  "serviceid_id from gtfs_calendar_dates gcd inner join aids on gcd.serviceid_agencyid = aids.aid where date='"+date[i]+"' and exceptiontype=1)";
	    	  if (i+1<date.length)
					query+=" union all ";
			}      
	    query +="), trips as (select trip.route_agencyid as aid, trip.route_id as routeid, map.tripid, map.length, map.tlength  "
			+ "FROM svcids INNER JOIN gtfs_trips trip USING(serviceid_agencyid, serviceid_id)  "
			+ "INNER JOIN " + mapTable + "  AS map ON trip.id = map.tripid AND trip.agencyid = map.agencyid WHERE map." + Id + " IN " + areas + "), "
							
			+ "tempstops AS (SELECT stoptimes.stop_agencyid AS agencyid_def, trip_agencyid AS agencyid, stoptimes.stop_id, count(tripid) AS visits "
			+ "FROM trips INNER JOIN gtfs_stop_times AS stoptimes  "
			+ "ON stoptimes.trip_agencyid = trips.aid AND trips.tripid = stoptimes.trip_id GROUP BY stop_agencyid, stop_id,trip_agencyid), "
							
			+ "stops AS (SELECT tempstops.agencyid, stops.agencyid AS agencyid_def, stops.id AS stopid, stops.name AS stopname, stops.location, tempstops.visits "
			+ "FROM tempstops INNER JOIN gtfs_stops AS stops ON stop_id = id AND agencyid_def = stops.agencyid), "
							
			+ "blocks AS (SELECT blocks.blockid, poptype, " + areaId + " AS areaid, urbanid, " + popYear + " AS population, "
			+ "stops.stopid, stops.stopname, stops.agencyid, visits "
			+ "FROM census_blocks AS blocks JOIN stops  "
			+ "ON ST_DISTANCE(blocks.location, stops.location)<" + sradius
			+ "WHERE " + areaId + " IN " + areas + "), "
			+ "urbanfilter AS (SELECT urbanid, uname, utype, landarea, waterarea, " + popYear + " FROM census_urbans  "
			+ "WHERE " + popYear + " >= " + minUrbanPop + "  AND " + popYear + "<=" + maxUrbanPop + "), "
			+ "urbanfilteredblocks AS (SELECT blocks.* FROM blocks INNER JOIN urbanfilter USING(urbanid) UNION ALL SELECT * FROM blocks WHERE poptype='R'), "
							
			+ "urbanfilteredallblocks AS (SELECT allblocks.* FROM allblocks INNER JOIN urbanfilter USING(urbanid) UNION ALL SELECT * FROM allblocks WHERE poptype='R'), "
								
			+ "pop_served AS (SELECT blockid, poptype, areaid, population, agencyid FROM allblocks GROUP BY blockid, poptype, areaid, population, agencyid), "
			+ "pop_los AS (SELECT blockid, poptype, areaid, population, agencyid FROM urbanfilteredblocks WHERE visits > " + los + " GROUP BY blockid, poptype, areaid, population, agencyid ), "
			+ "pop_served_cumulated AS (select poptype, areaid, agencyid, sum(population) AS pop_served from pop_served GROUP BY poptype, areaid, agencyid), "
			+ "pop_los_cumulated AS (select poptype, areaid, agencyid, sum(population) AS pop_los from pop_los GROUP BY poptype, areaid, agencyid), "
			+ "pop_ss_cumulated AS (select poptype, areaid, agencyid, sum(population*visits) AS pop_ss from urbanfilteredblocks GROUP BY poptype, areaid, agencyid), "
			+ "combination_list AS (select aids.aid AS agencyid, aids.agencyname, " + Id + " AS areaid, " + areaTypeName + " AS areaname, poptype  " 
			+ "FROM aids CROSS JOIN " + table + " "
			+ "CROSS JOIN (SELECT 'U' AS poptype UNION SELECT 'R' AS poptype ) AS poptype "
			+ "WHERE " + Id + " IN " + areas + ")  "
			
			+ " select combination_list.*, served.pop_served, ss.pop_ss, los.pop_los  "
			 + "FROM combination_list LEFT JOIN pop_served_cumulated  AS served USING(areaid,agencyid,poptype) "
			+ "LEFT JOIN pop_ss_cumulated AS ss USING(areaid,agencyid,poptype) "
			+ "LEFT JOIN pop_los_cumulated AS los USING(areaid,agencyid,poptype)";
	    
		System.out.println(query);
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()){
			FlexRepPop i = new FlexRepPop();
			i.agencyId = rs.getString("agencyid");
			i.agencyName = rs.getString("agencyname");
			i.areaId = rs.getString("areaid");
			i.areaName = rs.getString("areaname");
			i.popType = rs.getString("poptype");
			i.popServed = rs.getLong("pop_served");
			i.popLOS = rs.getLong("pop_los");
			i.popSS = rs.getLong("pop_ss");
			output.add(i);
		}
		return output;
	};
}
