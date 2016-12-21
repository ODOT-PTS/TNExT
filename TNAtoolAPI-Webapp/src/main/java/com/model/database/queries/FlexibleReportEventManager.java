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

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.model.database.queries.flexibleReport.FlexRepEmp;
import com.model.database.queries.flexibleReport.FlexRepPop;
import com.model.database.queries.flexibleReport.FlexRepSrv;
import com.model.database.queries.flexibleReport.FlexRepT6;
import com.model.database.queries.objects.GeoArea;
import com.webapp.api.Queries;

public class FlexibleReportEventManager {
	
	public static List<GeoArea> getAreaList(String areaType, int dbindex) {
		List<GeoArea> output = new ArrayList<GeoArea>();
		Connection connection = null;
		Statement stmt = null;
		try{
			connection = PgisEventManager.makeConnection(dbindex);
			stmt = connection.createStatement();
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
				query = "SELECT * FROM census_counties ORDER BY cname";
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
				query = "SELECT * FROM census_urbans ORDER BY uname";
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
						+ "	GROUP BY odotregionid, odotregionname"
						+ " ORDER BY odotregionname";
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
		}catch ( SQLException e ){
			System.out.println(e);
		}finally{
			if (stmt != null)try{stmt.close();}catch(SQLException e){};
			if (connection != null)try{connection.close();}catch(SQLException e){};
		}		
		return output;
	}

	public static List<FlexRepSrv> getFlexRepSrv(int dbindex, String agencies,
			String[] date, String[] day, String areas, String areaType,
			String username, boolean urbanFilter,int minUrbanPop,int maxUrbanPop,
			String uAreaYear, double key) {
		List<FlexRepSrv> output = new ArrayList<FlexRepSrv>();
		agencies = "('" + agencies.replace(",", "','") + "')";
		areas = "('" + areas.replace(",", "','") + "')";
		Connection connection = null;
		Statement stmt = null;
		
		try{
			connection = PgisEventManager.makeConnection(dbindex);
			stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			String areaId = "";
			String Id = "";
			String table = "";
			String tableName = "";
			String areaTypeName = "";
			String stopsUrbanFilter = "";
			String tripsUrbanFilter = "";
			String areas_urbans_intersections = "";
			
			if (areaType.equals("county")) {
				areaId = "LEFT(blockid,5)";
				Id = "countyid";
				table = "census_counties";
				tableName = table;
				areaTypeName = "cname";
			} else if (areaType.equals("urban")) {
				areaId = "urbanid";
				Id = "urbanid";
				table = "census_urbans";
				tableName = table;
				areaTypeName = "uname";
			} else if (areaType.equals("congDist")) {
				areaId = "congdistid";
				Id = "congdistid";
				table = "census_congdists";
				tableName = table;
				areaTypeName = "cname";
			} else if (areaType.equals("place")) {
				areaId = "placeid";
				Id = "placeid";
				table = "census_places";
				tableName = table;
				areaTypeName = "pname";
			} else if (areaType.equals("odotRegion")) {
				areaId = "regionid";
				Id = "regionid";
				table = "(SELECT odotregionid AS regionid, ST_UNION(shape) AS shape FROM census_counties GROUP BY odotregionid) AS census_regions ";
				tableName = "census_regions";
				areaTypeName = "'Region '||regionid";
			}
			
			if (urbanFilter){
				stopsUrbanFilter = "SELECT tripstops.* FROM tripstops INNER JOIN urbanfilter USING(urbanid)";
				tripsUrbanFilter = "SELECT trip.route_agencyid AS aid, trip.route_id AS routeid"
							+ "		FROM svcids INNER JOIN gtfs_trips AS trip USING(serviceid_agencyid, serviceid_id) "
							+ "		INNER JOIN census_urbans_trip_map AS map ON trip.id = map.tripid "
							+ "		AND trip.agencyid = map.agencyid WHERE map.urbanid IN (SELECT urbanid FROM urbanfilter)";
				areas_urbans_intersections = "SELECT " + Id + " AS areaid, " + areaTypeName + " AS areaname, (ST_Intersection(" + tableName + ".shape, urbans_shape.shape)) AS shape"
						+ "		FROM " + table + " CROSS JOIN urbans_shape "
						+ "		WHERE " + Id + " IN " + areas; 
			}else{
				stopsUrbanFilter = "SELECT tripstops.* FROM tripstops";
				tripsUrbanFilter = "SELECT trip.route_agencyid AS aid, trip.route_id AS routeid"
							+ "		FROM svcids INNER JOIN gtfs_trips AS trip USING(serviceid_agencyid, serviceid_id) ";
				areas_urbans_intersections = "SELECT " + Id + " AS areaid, " + areaTypeName + " AS areaname, shape"
						+ "		FROM " + table + " WHERE " + Id + " IN " + areas ;
			}
			
			for ( int i = 0 ; i < day.length ; i++ ){
				String query = "WITH aids AS (SELECT id AS aid, name AS agencyname FROM gtfs_agencies WHERE id IN " + agencies + "),"
						+ "	allstops AS (SELECT stops.agencyid AS agencyid_def, map.agencyid, map.stopid, stops.name AS stopname, stops.location "
						+ "		FROM gtfs_stop_service_map AS map INNER JOIN gtfs_stops AS stops "
						+ "		ON map.stopid = stops.id AND agencyid_def = stops.agencyid "
						+ "		WHERE map.agencyid IN " + agencies + "), "
						+ "svcids AS (select serviceid_agencyid, serviceid_id from gtfs_calendars gc inner join aids on gc.serviceid_agencyid = aids.aid where startdate::int<="
						+ date[i] + " and enddate::int>=" + date[i] +" and " + day[i] + " = 1 and serviceid_agencyid||serviceid_id not in (select serviceid_agencyid||serviceid_id from "
						+ "gtfs_calendar_dates where date='" + date[i] + "' and exceptiontype=2) union select serviceid_agencyid, "
						+ "serviceid_id from gtfs_calendar_dates gcd inner join aids on gcd.serviceid_agencyid = aids.aid where date='"
						+ date[i] + "' and exceptiontype=1), "
						+ "trips AS (SELECT trip.route_agencyid AS aid, trip.route_id AS routeid, trip.id AS tripid, trip.shape "
						+ "		FROM svcids INNER JOIN gtfs_trips trip USING(serviceid_agencyid, serviceid_id)), "
						+ "tripstops0 AS (SELECT stoptimes.stop_agencyid AS agencyid_def, trip_agencyid AS agencyid, "
						+ "		MIN(arrivaltime) AS arrival, MAX(departuretime) AS departure, stoptimes.stop_id, count(tripid) AS visits "
						+ "		FROM trips INNER JOIN gtfs_stop_times AS stoptimes "
						+ "		ON stoptimes.trip_agencyid = trips.aid AND trips.tripid = stoptimes.trip_id GROUP BY stop_agencyid, stop_id,trip_agencyid),"
						+ "tripstops AS (SELECT tripstops0.agencyid, stops.agencyid AS agencyid_def, stops.id AS stopid, stops.name AS stopname, "
						+ "		arrival, departure, " + areaId + " AS areaid,stops.urbanid, stops.location, tripstops0.visits"
						+ "		FROM tripstops0 INNER JOIN gtfs_stops AS stops ON stop_id = id AND agencyid_def = stops.agencyid),"
						+ "urbanfilter AS (SELECT urbanid, uname, utype, landarea, waterarea, population2020, shape FROM census_urbans "
						+ "		WHERE " + uAreaYear + " >= " + minUrbanPop + " AND " + uAreaYear + " <= " + maxUrbanPop + "), "
						+ "urbanfilteredstops AS (" + stopsUrbanFilter + "),"
						+ "urbanfilteredtrips AS (" + tripsUrbanFilter + "),"
						+ "urbans_shape AS (SELECT ST_Union(shape) AS shape FROM urbanfilter),"
						+ "areas_urbans_intersection AS (" + areas_urbans_intersections + "),"
						+ "miles_metrics_temp AS (SELECT trips.aid AS agencyid, areas.areaid, areas.areaname, trips.routeid,"
						+ "		max(ST_Length(st_transform(ST_Intersection(trips.shape,areas.shape),2993))/1609.34) AS route_mile,"
						+ "		sum(ST_Length(st_transform(ST_Intersection(trips.shape,areas.shape),2993))/1609.34) AS service_mile"
						+ "		FROM trips CROSS JOIN areas_urbans_intersection AS areas "
						+ "		GROUP BY trips.aid, trips.routeid,areas.areaid, areas.areaname ORDER BY aid,routeid),"
						+ "miles_metrics AS (SELECT agencyid, areaid, areaname, sum(route_mile) AS route_miles, sum(service_mile) AS service_miles "
						+ "		FROM miles_metrics_temp GROUP BY agencyid,areaid,areaname),"
						+ "service_hours AS (SELECT agencyid, areaid, MAX(departure)-MIN(arrival) AS service_seconds "
						+ "		FROM urbanfilteredstops WHERE arrival>0 GROUP BY agencyid, areaid),"
						+ "stops_metrics AS (SELECT agencyid,areaid,count(stopid) AS route_stops, sum(visits) AS service_stops "
						+ "		FROM urbanfilteredstops GROUP BY agencyid,agencyid_def,areaid),"
						+ "combination_list AS (select aids.aid AS agencyid, aids.agencyname, " + Id + " AS areaid, " + areaTypeName + " AS areaname "
						+ "		FROM aids CROSS JOIN " + table 
						+ "		WHERE " + Id + " IN " + areas + ") "
						+ "SELECT combination_list.*, COALESCE(route_stops,0)::int AS route_stops, COALESCE(service_stops,0)::int AS service_stops, "
						+ "		route_miles, service_miles, COALESCE(service_seconds,0) AS service_seconds "
						+ "		FROM  combination_list LEFT JOIN stops_metrics USING(areaid,agencyid) "
						+ "		LEFT JOIN miles_metrics USING (areaid,agencyid) "
						+ "		LEFT JOIN service_hours USING (areaid,agencyid) ORDER BY agencyid, areaid";
//				System.out.println(query);
				ResultSet rs = stmt.executeQuery(query);
				
				// get the size of the result set
				rs.last();
				int rsSize = rs.getRow(); 
				rs.beforeFirst();
				
				if (i == 0){
					while (rs.next()) {
						FlexRepSrv instance = new FlexRepSrv();
						instance.agencyId = rs.getString("agencyid");
						instance.agencyName = rs.getString("agencyname");
						instance.areaId = rs.getString("areaid");
						instance.areaName = rs.getString("areaname");
						instance.routeMiles = rs.getDouble("route_miles");
						instance.serviceMiles = rs.getDouble("service_miles");
						instance.serviceSeconds = rs.getInt("service_Seconds");
						instance.routeStops = rs.getInt("route_stops");
						instance.serviceStops = rs.getInt("service_stops");
						output.add(instance);
						Queries.setprogVal(key, ((100*(i+1))/day.length)*(rs.getRow()/rsSize) );
					}
					
				}else{
					int counter = 0;
					FlexRepSrv instance;
					while (rs.next()) {
						instance = output.get(counter++);
						instance.routeMiles += rs.getDouble("route_miles");
						instance.serviceMiles += rs.getDouble("service_miles");
						instance.serviceSeconds += rs.getInt("service_seconds");
						instance.routeStops += rs.getInt("route_stops");
						instance.serviceStops += rs.getInt("service_stops");
						Queries.setprogVal(key, ((100*(i+1))/day.length)*(rs.getRow()/rsSize)  );
					}
				}
			}
			Queries.progVal.remove(key);
		}catch(SQLException e){
			System.out.println(e);
		}finally{
			if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
			if (connection != null) try { connection.close(); } catch (SQLException e) {}
		}
		return output;
	}
	
	public static List<FlexRepPop> getFlexRepPop(int dbindex, String agencies,
			String[] date, String[] day, String popYear, String areas, int los,
			double sradius, String areaType, String username, boolean urbanFilter,
			String uAreaFliterYear, int minUrbanPop, int maxUrbanPop, double key) {
		List<FlexRepPop> output = new ArrayList<FlexRepPop>();

		agencies = "('" + agencies.replace(",", "','") + "')";
		areas = "('" + areas.replace(",", "','") + "')";

		Connection connection = null;
		Statement stmt = null;
		
		try{
			connection = PgisEventManager.makeConnection(dbindex);
			stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			String areaId = "";
			String Id = "";
			String mapTable = "";
			String table = "";
			String areaTypeName = "";
			String stopsUrbanFilter = "";
			String blocksUrbanFilter = "";
			
			if (areaType.equals("county")) {
				areaId = "LEFT(blocks.blockid,5)";
				Id = "countyid";
				mapTable = "census_counties_trip_map";
				table = "census_counties";
//				tableName = table;
				areaTypeName = "cname";
			} else if (areaType.equals("urban")) {
				areaId = "blocks.urbanid";
				Id = "urbanid";
				mapTable = "census_urbans_trip_map";
				table = "census_urbans";
				areaTypeName = "uname";
			} else if (areaType.equals("congDist")) {
				areaId = "blocks.congdistid";
				Id = "congdistid";
				mapTable = "census_congdists_trip_map";
				table = "census_congdists";
				areaTypeName = "cname";
			} else if (areaType.equals("place")) {
				areaId = "blocks.placeid";
				Id = "placeid";
				mapTable = "census_places_trip_map";
				table = "census_places";
				areaTypeName = "pname";
			} else if (areaType.equals("odotRegion")) {
				areaId = "blocks.regionid";
				Id = "regionid";
				mapTable = "census_counties_trip_map";
				table = "(SELECT '1' AS regionid UNION SELECT '2a' UNION SELECT '2b' UNION SELECT '3' UNION SELECT '4' UNION SELECT '5') AS census_regions ";
				areaTypeName = "'Region '||regionid";
			}
		
			if (urbanFilter){
				stopsUrbanFilter = "SELECT tripstops.* FROM tripstops INNER JOIN urbanfilter USING(urbanid)";
				blocksUrbanFilter = "SELECT blocks.* FROM blocks INNER JOIN urbanfilter USING(urbanid)";			
			}else{
				stopsUrbanFilter = "SELECT tripstops.* FROM tripstops";
				blocksUrbanFilter = "SELECT blocks.* FROM blocks";
			}

			String query = "WITH aids AS (SELECT id AS aid, name AS agencyname FROM gtfs_agencies WHERE id IN "
					+ agencies
					+ "), "
					+ "svcids AS (";
			for (int i = 0; i < date.length; i++) {
				query += "(SELECT serviceid_agencyid, serviceid_id "
						+ "		FROM gtfs_calendars gc INNER JOIN aids ON gc.serviceid_agencyid = aids.aid "
						+ "		WHERE startdate::int<=" + date[i] + " AND enddate::int>=" + date[i] + " AND " + day[i] + " = 1 "
						+ "		AND serviceid_agencyid||serviceid_id NOT IN (select serviceid_agencyid||serviceid_id "
						+ "			FROM gtfs_calendar_dates where date='" + date[i]  + "' and exceptiontype=2) "
						+ "			UNION SELECT serviceid_agencyid, serviceid_id "
						+ "			FROM gtfs_calendar_dates gcd "
						+ "			INNER JOIN aids ON gcd.serviceid_agencyid = aids.aid "
						+ "			WHERE date='" + date[i] + "' AND exceptiontype=1)";
				if (i + 1 < date.length)
					query += " union all ";
			}
			query += "), trips as (select trip.route_agencyid as aid, trip.route_id as routeid, map.tripid, map.length, map.tlength  "
					+ "		FROM svcids INNER JOIN gtfs_trips trip USING(serviceid_agencyid, serviceid_id)  "
					+ "		INNER JOIN " + mapTable + "  AS map ON trip.id = map.tripid AND trip.agencyid = map.agencyid "
					+ " 	WHERE map."	+ Id + " IN " + areas + "), "
					+ "temptripstops AS (SELECT stoptimes.stop_agencyid AS agencyid_def, trip_agencyid AS agencyid, stoptimes.stop_id, count(tripid) AS visits "
					+ "		FROM trips INNER JOIN gtfs_stop_times AS stoptimes  "
					+ "		ON stoptimes.trip_agencyid = trips.aid AND trips.tripid = stoptimes.trip_id GROUP BY stop_agencyid, stop_id,trip_agencyid), "
					+ "tripstops AS (SELECT temptripstops.agencyid, stops.agencyid AS agencyid_def, stops.id AS stopid, stops.name AS stopname, stops.location, temptripstops.visits, stops.urbanid "
					+ "		FROM temptripstops INNER JOIN gtfs_stops AS stops ON stop_id = id AND agencyid_def = stops.agencyid), "
					+ "blocks AS (SELECT blocks.blockid, poptype, " + areaId + " AS areaid, blocks.urbanid, " + popYear + " AS population, tripstops.stopid, tripstops.stopname, tripstops.agencyid, visits "
					+ "		FROM census_blocks AS blocks INNER JOIN tripstops "
					+ "		ON ST_DISTANCE(blocks.location, tripstops.location)<" + sradius 
					+ "		WHERE " + areaId + " IN " + areas + "), "
					+ "urbanfilter AS (SELECT urbanid, uname, utype, landarea, waterarea, " + popYear 
					+ " 	FROM census_urbans  WHERE " + uAreaFliterYear + " >= " + minUrbanPop + "  AND " + popYear + "<=" + maxUrbanPop + "), "
					+ "urbanfilteredblocks AS (" + blocksUrbanFilter + "), "
					+ "urbanFilteredstops AS (" + stopsUrbanFilter + "), "
					+ "pop_served AS (SELECT blockid, poptype, areaid, population, agencyid "
					+ "		FROM urbanfilteredblocks GROUP BY blockid, poptype, areaid, population, agencyid), "
					+ "pop_los AS (SELECT blockid, poptype, areaid, population, agencyid "
					+ "		FROM urbanfilteredblocks WHERE visits > " + los + " GROUP BY blockid, poptype, areaid, population, agencyid ), "
					+ "pop_served_cumulated AS (select poptype, areaid, agencyid, sum(population) AS pop_served "
					+ "		FROM pop_served GROUP BY poptype, areaid, agencyid), "
					+ "pop_los_cumulated AS (select poptype, areaid, agencyid, sum(population) AS pop_los "
					+ "		FROM pop_los GROUP BY poptype, areaid, agencyid), "
					+ "pop_ss_cumulated AS (select poptype, areaid, agencyid, sum(population*visits) AS pop_ss "
					+ "		FROM urbanfilteredblocks GROUP BY poptype, areaid, agencyid), "
					+ "combination_list AS (select aids.aid AS agencyid, aids.agencyname, "	+ Id + " AS areaid," + areaTypeName	+ " AS areaname, poptype "
					+ "		FROM aids CROSS JOIN " + table	+ " CROSS JOIN (SELECT 'U' AS poptype UNION SELECT 'R' AS poptype ) AS poptype "
					+ "		WHERE " + Id + " IN " + areas + ") "
					+ "SELECT combination_list.*, served.pop_served, ss.pop_ss, los.pop_los "
					+ "		FROM combination_list LEFT JOIN pop_served_cumulated  AS served USING(areaid,agencyid,poptype) "
					+ "		LEFT JOIN pop_ss_cumulated AS ss USING(areaid,agencyid,poptype) "
					+ "		LEFT JOIN pop_los_cumulated AS los USING(areaid,agencyid,poptype)";

//			System.out.println(query);
			ResultSet rs = stmt.executeQuery(query);
			

			// get the size of the result set
			rs.last();
			int rsSize = rs.getRow(); 
			rs.beforeFirst();
			
			while (rs.next()) {
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
				Queries.setprogVal(key, (100*rs.getRow())/rsSize );
			}
			Queries.progVal.remove(key);
		}catch(SQLException e){
			System.out.println(e);
		}finally{
			if (stmt != null)try{stmt.close();}catch(SQLException e){};
			if (connection != null)try{connection.close();}catch(SQLException e){};
		}
		return output;
	};

	public static List<FlexRepEmp> getFlexRepEmp(int dbindex, String agencies,
			String[] date, String[] day, String empYear, String areas, int los,
			double sradius, String areaType, String username, boolean urbanFilter, String uAreaFliterYear,
			int minUrbanPop, int maxUrbanPop, boolean wac, boolean rac, String[] categories, double key)
			throws SQLException, NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		String wacDataset = "lodes_blocks_wac";
		String racDataset = "lodes_blocks_rac";
		
		List<FlexRepEmp> output = new ArrayList<FlexRepEmp>();
		agencies = "('" + agencies.replace(",", "','") + "')";
		areas = "('" + areas.replace(",", "','") + "')";

		if (empYear.equals("current"))
			empYear = "";
		else{
			empYear = "_" + empYear;
			racDataset = "lodes_rac_projection_block";
			wac = false; // We don't have projection data on WAC.
		}
		
		Connection connection = null;
		Statement stmt = null;
		try{
			connection = PgisEventManager.makeConnection(dbindex);
			stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

			String areaId = "";
			String Id = "";
			String mapTable = "";
			String table = "";
			String areaTypeName = "";
			String blocksUrbanFilter = "";
			
			if (areaType.equals("county")) {
				areaId = "LEFT(blockid,5)";
				Id = "countyid";
				mapTable = "census_counties_trip_map";
				table = "census_counties";
				areaTypeName = "cname";
			} else if (areaType.equals("urban")) {
				areaId = "urbanid";
				Id = "urbanid";
				mapTable = "census_urbans_trip_map";
				table = "census_urbans";
				areaTypeName = "uname";
			} else if (areaType.equals("congDist")) {
				areaId = "congdistid";
				Id = "congdistid";
				mapTable = "census_congdists_trip_map";
				table = "census_congdists";
				areaTypeName = "cname";
			} else if (areaType.equals("place")) {
				areaId = "placeid";
				Id = "placeid";
				mapTable = "census_places_trip_map";
				table = "census_places";
				areaTypeName = "pname";
			}  else if (areaType.equals("odotRegion")) {
				areaId = "regionid";
				Id = "regionid";
				mapTable = "census_counties_trip_map";
				table = "(SELECT '1' AS regionid UNION SELECT '2a' UNION SELECT '2b' UNION SELECT '3' UNION SELECT '4' UNION SELECT '5') AS census_regions ";
				areaTypeName = "'Region '||regionid";
			}
			
			if (urbanFilter)
				blocksUrbanFilter = "SELECT blocks.* FROM blocks INNER JOIN urbanfilter USING(urbanid)";
			else
				blocksUrbanFilter = "SELECT blocks.* FROM blocks";
			

			String query = "WITH aids AS (SELECT id AS aid, name AS agencyname FROM gtfs_agencies WHERE id IN "
					+ agencies
					+ "), "
					+ "svcids AS (";
			for (int i = 0; i < date.length; i++) {
				query += "(select serviceid_agencyid, serviceid_id from gtfs_calendars gc inner join aids on gc.serviceid_agencyid = aids.aid where startdate::int<="
						+ date[i] + " and enddate::int>=" + date[i] + " and " + day[i] + " = 1 and serviceid_agencyid||serviceid_id not in (select serviceid_agencyid||serviceid_id from "
						+ "gtfs_calendar_dates where date='" + date[i] + "' and exceptiontype=2) union select serviceid_agencyid, "
						+ "serviceid_id from gtfs_calendar_dates gcd inner join aids on gcd.serviceid_agencyid = aids.aid where date='"
						+ date[i] + "' and exceptiontype=1)";
				if (i + 1 < date.length)
					query += " union all ";
			}
			query += "), trips as (select trip.route_agencyid as aid, trip.route_id as routeid, map.tripid, map.length, map.tlength  "
					+ "		FROM svcids INNER JOIN gtfs_trips trip USING(serviceid_agencyid, serviceid_id)  "
					+ "		INNER JOIN " + mapTable + "  AS map ON trip.id = map.tripid AND trip.agencyid = map.agencyid WHERE map." + Id + " IN " + areas + "), "

					+ "tempstops AS (SELECT stoptimes.stop_agencyid AS agencyid_def, trip_agencyid AS agencyid, stoptimes.stop_id, count(tripid) AS visits "
					+ "		FROM trips INNER JOIN gtfs_stop_times AS stoptimes  "
					+ "		ON stoptimes.trip_agencyid = trips.aid AND trips.tripid = stoptimes.trip_id GROUP BY stop_agencyid, stop_id,trip_agencyid), "

					+ "stops AS (SELECT tempstops.agencyid, stops.agencyid AS agencyid_def, stops.id AS stopid, stops.name AS stopname, stops.location, tempstops.visits "
					+ "		FROM tempstops INNER JOIN gtfs_stops AS stops ON stop_id = id AND agencyid_def = stops.agencyid), "

					+ "blocks AS (SELECT blocks.blockid, poptype, " + areaId + " AS areaid, urbanid, "
					+ "stops.stopid, stops.stopname, stops.agencyid, visits "
					+ "		FROM census_blocks AS blocks JOIN stops  "
					+ "		ON ST_DISTANCE(blocks.location, stops.location)<" + sradius + "WHERE " + areaId + " IN " + areas + "), "
					+ "urbanfilter AS (SELECT urbanid, uname, utype, landarea, waterarea "
					+ " FROM census_urbans WHERE " + uAreaFliterYear + " >= " + minUrbanPop + "  AND " + uAreaFliterYear + "<=" + maxUrbanPop + "), "
					+ "urbanfilteredblocks AS (" + blocksUrbanFilter + "), ";

			if (wac) {
				query += " emp_ss_wac AS (SELECT areaid, agencyid";
				for (String category : categories)
					query += ",sum(" + category + "*visits) AS " + category + "_ss_wac";

				query += " FROM " + wacDataset + " AS wac INNER JOIN urbanfilteredblocks USING(blockid) GROUP BY areaid, agencyid), "
						+ "emp_los_wac AS (SELECT blockid, areaid, sum(visits) AS visits, agencyid ";
				for (String category : categories)
					query += "," + category;

				query += "	FROM urbanfilteredblocks INNER JOIN " + wacDataset + " AS wac USING (blockid) "
						+ "	GROUP BY blockid, areaid, agencyid ";

				for (String category : categories)
					query += "," + category;

				query += "), "
						+ "emp_los_cumulated_wac AS (SELECT agencyid, areaid ";
				for (String category : categories)
					query += ",sum(" + category + ") AS " + category + "_los_wac";

				query += "	FROM emp_los_wac GROUP BY areaid, agencyid), "
						+ "emp_served_wac AS (SELECT blockid, areaid, agencyid ";

				for (String category : categories)
					query += "," + category;

				query += "	FROM " + wacDataset + " INNER JOIN urbanfilteredblocks USING (blockid) "
						+ "	GROUP BY blockid, areaid, agencyid";

				for (String category : categories)
					query += "," + category;

				query += "), emp_served_cumulated_wac AS (SELECT agencyid, areaid";

				for (String category : categories)
					query += ",sum( " + category + ") AS " + category
							+ "_served_wac";

				query += "	FROM emp_served_wac GROUP BY agencyid, areaid), ";

			}
			if (rac) {
				query += " emp_ss_rac AS (SELECT areaid, agencyid";
				for (String category : categories)
					query += ",sum(" + category + empYear + "*visits) AS " + category + "_ss_rac";

				query += " FROM " + racDataset + " AS rac INNER JOIN urbanfilteredblocks USING(blockid) GROUP BY areaid, agencyid), "
						+ "emp_los_rac AS (SELECT blockid, areaid, sum(visits) AS visits, agencyid ";
				for (String category : categories)
					query += "," + category + empYear ;

				query += "	FROM urbanfilteredblocks INNER JOIN " + racDataset + " AS rac USING (blockid) "
						+ "	GROUP BY blockid, areaid, agencyid ";

				for (String category : categories)
					query += "," + category + empYear;

				query += "), "
						+ "emp_los_cumulated_rac AS (SELECT agencyid, areaid ";
				for (String category : categories)
					query += ",sum(" + category + empYear + ") AS " + category + "_los_rac";

				query += "	FROM emp_los_rac GROUP BY areaid, agencyid), "
						+ "emp_served_rac AS (SELECT blockid, areaid, agencyid ";

				for (String category : categories)
					query += "," + category + empYear;

				query += "	FROM " + racDataset + " INNER JOIN urbanfilteredblocks USING (blockid) "
						+ "	GROUP BY blockid, areaid, agencyid";

				for (String category : categories)
					query += "," + category + empYear;

				query += "), emp_served_cumulated_rac AS (SELECT agencyid, areaid";

				for (String category : categories)
					query += ", sum( " + category + empYear + ") AS " + category
							+ "_served_rac";

				query += "	FROM emp_served_rac GROUP BY agencyid, areaid), ";
			}

			if (wac && rac) {
				query += " combination_list AS (select aids.aid AS agencyid, aids.agencyname, " + Id + " AS areaid, " + areaTypeName + " AS areaname "
						+ "			FROM aids CROSS JOIN " + table
						+ "			WHERE " + Id + " IN " + areas + ") "
						+ "SELECT combination_list.* ";
				for (String category : categories) {
					query += "," + category + "_served_wac, " + category
							+ "_los_wac, " + category + "_ss_wac";
					query += "," + category + "_served_rac, " + category
							+ "_los_rac, " + category + "_ss_rac";
				}
				query += "	FROM  combination_list LEFT JOIN emp_ss_wac USING(areaid,agencyid) "
						+ "	LEFT JOIN emp_served_cumulated_wac USING (areaid,agencyid) "
						+ "	LEFT JOIN emp_los_cumulated_wac USING (areaid,agencyid) "
						+ " LEFT JOIN emp_ss_rac USING(areaid,agencyid) "
						+ "	LEFT JOIN emp_served_cumulated_rac USING (areaid,agencyid) "
						+ "	LEFT JOIN emp_los_cumulated_rac USING (areaid,agencyid) ";
			} else if (wac) {
				query += " combination_list AS (select aids.aid AS agencyid, aids.agencyname, " + Id + " AS areaid, " + areaTypeName + " AS areaname "
						+ "			FROM aids CROSS JOIN " + table
						+ "			WHERE " + Id + " IN " + areas + ") "
						+ "SELECT combination_list.* ";
				for (String category : categories) {
					query += "," + category + "_served_wac, " + category
							+ "_los_wac, " + category + "_ss_wac";
				}
				query += "	FROM  combination_list LEFT JOIN emp_ss_wac USING(areaid,agencyid) "
						+ "	LEFT JOIN emp_served_cumulated_wac USING (areaid,agencyid) "
						+ "	LEFT JOIN emp_los_cumulated_wac USING (areaid,agencyid) ";
			} else {
				query += " combination_list AS (select aids.aid AS agencyid, aids.agencyname, " + Id + " AS areaid, " + areaTypeName + " AS areaname "
						+ "			FROM aids CROSS JOIN " + table
						+ "			WHERE " + Id + " IN " + areas + ") "
						+ "SELECT combination_list.* ";
				for (String category : categories) {
					query += "," + category + "_served_rac, " + category
							+ "_los_rac, " + category + "_ss_rac";
				}
				query += "	FROM  combination_list LEFT JOIN emp_ss_rac USING(areaid,agencyid) "
						+ "	LEFT JOIN emp_served_cumulated_rac USING (areaid,agencyid) "
						+ "	LEFT JOIN emp_los_cumulated_rac USING (areaid,agencyid) ";
			}
//			System.out.println(query);
			ResultSet rs = stmt.executeQuery(query);
			
			// get the size of the result set
			rs.last();
			int rsSize = rs.getRow(); 
			rs.beforeFirst();
			
			while (rs.next()) {
				FlexRepEmp i = new FlexRepEmp();
				for (String cat : categories) {
					i.agencyId = rs.getString("agencyid");
					i.agencyName = rs.getString("agencyname");
					i.areaId = rs.getString("areaid");
					i.areaName = rs.getString("areaname");				
					if (wac) {
						Field f1 = i.getClass().getDeclaredField(
								cat + "_served_wac");
						f1.setAccessible(true);
						f1.set(i, rs.getInt(cat + "_served_wac"));

						Field f2 = i.getClass().getDeclaredField(cat + "_ss_wac");
						f2.setAccessible(true);
						f2.set(i, rs.getLong(cat + "_ss_wac"));

						Field f3 = i.getClass().getDeclaredField(cat + "_los_wac");
						f3.setAccessible(true);
						f3.set(i, rs.getInt(cat + "_los_wac"));
					}
					if (rac) {
						Field f1 = i.getClass().getDeclaredField(
								cat + "_served_rac");
						f1.setAccessible(true);
						f1.set(i, rs.getInt(cat + "_served_rac"));

						Field f2 = i.getClass().getDeclaredField(cat + "_ss_rac");
						f2.setAccessible(true);
						f2.set(i, rs.getLong(cat + "_ss_rac"));

						Field f3 = i.getClass().getDeclaredField(cat + "_los_rac");
						f3.setAccessible(true);
						f3.set(i, rs.getInt(cat + "_los_rac"));
					}
				}
				output.add(i);
				Queries.setprogVal(key, (100*rs.getRow())/rsSize );
			}
			Queries.progVal.remove(key);
		}catch(SQLException e) {
			System.out.println(e);
		}finally{
			if (stmt != null)try{stmt.close();}catch(SQLException e){};
			if (connection != null)try{connection.close();}catch(SQLException e){};
		}
		return output;
	}

	public static List<FlexRepT6> getFlexRepT6(int dbindex, String agencies,
			String[] date, String[] day, String areas, int los,
			double sradius, String areaType, String username, boolean urbanFilter,
			String urbanYear, int minUrbanPop, int maxUrbanPop, String[] categories, double key)
			throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		
			
		List<FlexRepT6> output = new ArrayList<FlexRepT6>();
		agencies = "('" + agencies.replace(",", "','") + "')";
		areas = "('" + areas.replace(",", "','") + "')";
		
		Connection connection = null;
		Statement stmt = null;
		
		
		try{
			connection = PgisEventManager.makeConnection(dbindex);
			stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			ResultSet rs = stmt.getResultSet();
			String areaId = "";
			String Id = "";
			String mapTable = "";
			String table = "";
			String areaTypeName = "";
			String blocksUrbanFilter = "";
			
			if (areaType.equals("county")) {
				areaId = "LEFT(blockid,5)";
				Id = "countyid";
				mapTable = "census_counties_trip_map";
				table = "census_counties";
				areaTypeName = "cname";
			} else if (areaType.equals("urban")) {
				areaId = "urbanid";
				Id = "urbanid";
				mapTable = "census_urbans_trip_map";
				table = "census_urbans";
				areaTypeName = "uname";
			} else if (areaType.equals("congDist")) {
				areaId = "congdistid";
				Id = "congdistid";
				mapTable = "census_congdists_trip_map";
				table = "census_congdists";
				areaTypeName = "cname";
			} else if (areaType.equals("place")) {
				areaId = "placeid";
				Id = "placeid";
				mapTable = "census_places_trip_map";
				table = "census_places";
				areaTypeName = "pname";
			}  else if (areaType.equals("odotRegion")) {
				areaId = "regionid";
				Id = "regionid";
				mapTable = "census_counties_trip_map";
				table = "(SELECT '1' AS regionid UNION SELECT '2a' UNION SELECT '2b' UNION SELECT '3' UNION SELECT '4' UNION SELECT '5') AS census_regions ";
				areaTypeName = "'Region '||regionid";
			}

			if (urbanFilter)
				blocksUrbanFilter = "SELECT blocks.* FROM blocks INNER JOIN urbanfilter USING(urbanid)";
			else
				blocksUrbanFilter = "SELECT blocks.* FROM blocks";
			
			String query = "WITH aids AS (SELECT id AS aid, name AS agencyname FROM gtfs_agencies WHERE id IN " + agencies + "), "
					+ "svcids AS (";
			for (int i = 0; i < date.length; i++) {
				query += "(select serviceid_agencyid, serviceid_id from gtfs_calendars gc inner join aids on gc.serviceid_agencyid = aids.aid where startdate::int<="
						+ date[i] + " and enddate::int>="
						+ date[i] + " and " + day[i] + " = 1 and serviceid_agencyid||serviceid_id not in (select serviceid_agencyid||serviceid_id from "
						+ "gtfs_calendar_dates where date='" + date[i] + "' and exceptiontype=2) union select serviceid_agencyid, "
						+ "serviceid_id from gtfs_calendar_dates gcd inner join aids on gcd.serviceid_agencyid = aids.aid where date='"
						+ date[i] + "' and exceptiontype=1)";
				if (i + 1 < date.length)
					query += " union all ";
			}
			query += "), trips as (select trip.route_agencyid as aid, trip.route_id as routeid, map.tripid, map.length, map.tlength  "
					+ "		FROM svcids INNER JOIN gtfs_trips trip USING(serviceid_agencyid, serviceid_id)  "
					+ "		INNER JOIN " + mapTable + "  AS map ON trip.id = map.tripid AND trip.agencyid = map.agencyid WHERE map." + Id + " IN " + areas + "), "

					+ "tempstops AS (SELECT stoptimes.stop_agencyid AS agencyid_def, trip_agencyid AS agencyid, stoptimes.stop_id, count(tripid) AS visits "
					+ "		FROM trips INNER JOIN gtfs_stop_times AS stoptimes  "
					+ "		ON stoptimes.trip_agencyid = trips.aid AND trips.tripid = stoptimes.trip_id GROUP BY stop_agencyid, stop_id,trip_agencyid), "

					+ "stops AS (SELECT tempstops.agencyid, stops.agencyid AS agencyid_def, stops.id AS stopid, stops.name AS stopname, stops.location, tempstops.visits "
					+ "		FROM tempstops INNER JOIN gtfs_stops AS stops ON stop_id = id AND agencyid_def = stops.agencyid), "

					+ "blocks AS (SELECT blocks.blockid, poptype, " + areaId + " AS areaid, urbanid, "
					+ "stops.stopid, stops.stopname, stops.agencyid, visits "
					+ "		FROM census_blocks AS blocks JOIN stops  "
					+ "		ON ST_DISTANCE(blocks.location, stops.location)<" + sradius
					+ "		WHERE " + areaId + " IN " + areas + "), "
					+ "urbanfilter AS (SELECT urbanid, uname, utype, landarea, waterarea, population2010 FROM census_urbans  "
					+ "		WHERE " + urbanYear + " >= " + minUrbanPop + " AND " + urbanYear + " <=" + maxUrbanPop + "), "
					+ "urbanfilteredblocks AS (" + blocksUrbanFilter + "), ";
			
				query += " t6_ss AS (SELECT areaid, agencyid";
				for (String category : categories)
					query += ",sum(" + category + "*visits)::bigint AS " + category + "_ss";

				query += " FROM title_vi_blocks_float AS t6 INNER JOIN urbanfilteredblocks USING(blockid) GROUP BY areaid, agencyid), "
						+ "t6_los AS (SELECT blockid, areaid, sum(visits) AS visits, agencyid ";
				for (String category : categories)
					query += "," + category ;

				query += "	FROM urbanfilteredblocks INNER JOIN title_vi_blocks_float AS t6 USING (blockid) "
						+ "	GROUP BY blockid, areaid, agencyid ";

				for (String category : categories)
					query += "," + category ;

				query += "), "
						+ "t6_los_cumulated AS (SELECT agencyid, areaid ";
				for (String category : categories)
					query += ",sum(" + category + ") AS " + category + "_los";

				query += "	FROM t6_los GROUP BY areaid, agencyid), "
						+ "t6_served AS (SELECT blockid, areaid, agencyid ";

				for (String category : categories)
					query += "," + category;

				query += "	FROM title_vi_blocks_float INNER JOIN urbanfilteredblocks USING (blockid) "
						+ "	GROUP BY blockid, areaid, agencyid";

				for (String category : categories)
					query += "," + category;

				query += "), t6_served_cumulated AS (SELECT agencyid, areaid";

				for (String category : categories)
					query += ", sum( " + category + ")::int AS " + category
							+ "_served";

				query += "	FROM t6_served GROUP BY agencyid, areaid), ";
			

			
				query += " combination_list AS (select aids.aid AS agencyid, aids.agencyname, " + Id + " AS areaid, " + areaTypeName + " AS areaname "
						+ "			FROM aids CROSS JOIN " + table
						+ "			WHERE " + Id + " IN " + areas + ") "
						+ "SELECT combination_list.* ";
				for (String category : categories) {
					query += "," + category + "_served, " + category
							+ "_los, " + category + "_ss";
				}
				query += "		FROM  combination_list LEFT JOIN t6_ss USING(areaid,agencyid) "
						+ "		LEFT JOIN t6_served_cumulated USING (areaid,agencyid) "
						+ "		LEFT JOIN t6_los_cumulated USING (areaid,agencyid) ";
			
//			System.out.println(query);
			rs = stmt.executeQuery(query);

			// get the size of the result set
			rs.last();
			int rsSize = rs.getRow(); 
			rs.beforeFirst();
						
			while (rs.next()) {
				FlexRepT6 i = new FlexRepT6();
				i.agencyId = rs.getString("agencyid");
				i.agencyName = rs.getString("agencyname");
				i.areaId = rs.getString("areaid");
				i.areaName = rs.getString("areaname");
				for (String cat : categories) {
					Field f1 = i.getClass().getDeclaredField(cat + "_served");
					f1.setAccessible(true);
					f1.set(i, rs.getInt(cat + "_served"));

					Field f2 = i.getClass().getDeclaredField(cat + "_ss");
					f2.setAccessible(true);
					f2.set(i, rs.getLong(cat + "_ss"));

					Field f3 = i.getClass().getDeclaredField(cat + "_los");
					f3.setAccessible(true);
					f3.set(i, rs.getInt(cat + "_los"));
				}
				output.add(i);
				Queries.setprogVal(key, (100*rs.getRow())/rsSize );
			}
			Queries.progVal.remove(key);
		}catch(SQLException e ){
			System.out.println(e);
		}finally{
			if (stmt != null)try{stmt.close();}catch(SQLException e){};
			if (connection != null)try{connection.close();}catch(SQLException e){};
		}
		return output;
	}
}
