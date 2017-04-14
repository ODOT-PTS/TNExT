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
			e.printStackTrace();
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
			String areaTypeName = "";
			String stopsUrbanFilter = "";
			String mapTable = "";
			String tripIntersections = "";
			String urbanStopsFilter = "";
			
			if (areaType.equals("county")) {
				areaId = "LEFT(blockid,5)";
				Id = "countyid";
				table = "census_counties";
				areaTypeName = "cname";
				mapTable = "census_counties_trip_map"; 
			} else if (areaType.equals("urban")) {
				areaId = "urbanid";
				Id = "urbanid";
				table = "census_urbans";
				areaTypeName = "uname";
				mapTable = "census_urbans_trip_map";
			} else if (areaType.equals("congDist")) {
				areaId = "congdistid";
				Id = "congdistid";
				table = "census_congdists";
				areaTypeName = "cname";
				mapTable = "census_congdists_trip_map";
			} else if (areaType.equals("place")) {
				areaId = "placeid";
				Id = "placeid";
				table = "census_places";
				areaTypeName = "pname";
				mapTable = "census_places_trip_map";
			} else if (areaType.equals("odotRegion")) {
				areaId = "regionid";
				Id = "regionid";
				table = "(SELECT odotregionid AS regionid, ST_UNION(shape) AS shape FROM census_counties GROUP BY odotregionid) AS census_regions ";
				areaTypeName = "'Region '||regionid";
				mapTable = "census_counties_trip_map"; 
			}
			
			if (urbanFilter){
				stopsUrbanFilter = "SELECT tripstops.* FROM tripstops INNER JOIN urbanfilter USING(urbanid)";
				tripIntersections = "trip_intersections_with_urbanfilter";
				urbanStopsFilter = "stops.urbanid IN (SELECT urbanid FROM urbanfilter)";
			}else{
				stopsUrbanFilter = "SELECT tripstops.* FROM tripstops";				
				tripIntersections = "trip_intersections_without_urbanfilter";
				urbanStopsFilter = "true";
			}
			
			for ( int i = 0 ; i < day.length ; i++ ){
				//-------------------------------------route miles----------------------------------------------------------------
				String query = "WITH aids AS (SELECT id AS aid, name AS agencyname FROM gtfs_agencies WHERE id IN " + agencies + "), "
						+ "trip_intersections_without_urbanfilter AS (SELECT trips.id, map.agencyid, map.agencyid_def, trips.serviceid_id, trips.route_id, " + Id + " AS areaid, map.shape, map.length "
						+ "		FROM gtfs_trips AS trips INNER JOIN " + mapTable + " AS map "
						+ "		ON map.agencyid = trips.agencyid AND trips.id = map.tripid "
						+ "		WHERE trips.agencyid IN " + agencies + " "
						+ "		AND map." + Id + " IN " + areas + "), "
						+ "trip_intersections_with_urbanfilter0 AS (SELECT id, agencyid, serviceid_id, route_id, areaid, ST_Union(ST_Intersection(urbans.shape,intersections.shape)) AS shape "
						+ "		FROM trip_intersections_without_urbanfilter AS intersections INNER JOIN census_urbans AS urbans "
						+ "		ON ST_Intersects(urbans.shape,intersections.shape) "
						+ "		GROUP BY id, agencyid, serviceid_id, route_id, areaid), "
						+ "trip_intersections_with_urbanfilter AS (SELECT id, agencyid, serviceid_id, route_id, areaid, "
						+ "		st_multi(ST_CollectionExtract(st_union(shape),2)) AS shape, (ST_Length(st_transform(shape,2993))/1609.34) AS length "
						+ "		FROM trip_intersections_with_urbanfilter0 "
						+ "		GROUP BY id, agencyid, serviceid_id, route_id, areaid, shape), "
						+ "route_intersections AS (SELECT agencyid, route_id, areaid, MAX(length) AS length "
						+ "		FROM " + tripIntersections + " GROUP BY agencyid, route_id, areaid), "
						+ "route_miles AS (SELECT agencyid AS agencyid, areaid, SUM(length) AS route_miles "
						+ " 	FROM route_intersections GROUP BY agencyid, areaid),"; 

				//-----------------------------------service hours--------------------------------------------------------------	
				query += "svcids AS (SELECT serviceid_agencyid, serviceid_id "
						+ "		FROM gtfs_calendars AS gc INNER JOIN aids "
						+ "		ON gc.serviceid_agencyid = aids.aid WHERE startdate::int<=" + date[i] 
						+ "		AND enddate::int>=" + date[i] +" AND " + day[i] +" = 1 "
						+ " 	AND serviceid_agencyid||serviceid_id NOT IN (SELECT serviceid_agencyid||serviceid_id "
						+ "		FROM gtfs_calendar_dates WHERE date='" + date[i] + "' AND exceptiontype=2) "
						+ "		UNION SELECT serviceid_agencyid, serviceid_id "
						+ "		FROM gtfs_calendar_dates gcd INNER JOIN aids ON gcd.serviceid_agencyid = aids.aid "
						+ "		WHERE date='" + date[i] + "' AND exceptiontype=1) ,"
						+ "trips AS (SELECT trip.route_agencyid AS aid, trip.route_id AS routeid, trip.id AS tripid, trip.shape "
						+ "		FROM svcids INNER JOIN gtfs_trips trip USING(serviceid_agencyid, serviceid_id)), "
						+ "tripstops0 AS (SELECT stoptimes.stop_agencyid AS agencyid_def, trip_agencyid AS agencyid, MIN(arrivaltime) AS arrival, MAX(departuretime) AS departure, stoptimes.stop_id, count(tripid) AS visits "
						+ "		FROM trips INNER JOIN gtfs_stop_times AS stoptimes "
						+ " 	ON stoptimes.trip_agencyid = trips.aid AND trips.tripid = stoptimes.trip_id GROUP BY stop_agencyid, stop_id,trip_agencyid),"
						+ "tripstops AS (SELECT tripstops0.agencyid, stops.agencyid AS agencyid_def, stops.id AS stopid, stops.name AS stopname,"
						+ "		arrival, departure, " + areaId + " AS areaid,stops.urbanid, stops.location, tripstops0.visits "
						+ "		FROM tripstops0 INNER JOIN gtfs_stops AS stops ON stop_id = id AND agencyid_def = stops.agencyid), "
						+ "urbanfilter AS (SELECT urbanid, shape FROM census_urbans "
						+ "		WHERE " + uAreaYear + " >= " + minUrbanPop + " AND " + uAreaYear + "<=" + maxUrbanPop + " ), "
						+ "urbanfilteredstops AS (" + stopsUrbanFilter + "), "
						+ "trip_times AS (SELECT stime.trip_agencyid as agencyid, stops.areaid, stime.trip_id as tripid, MAX(stime.arrivaltime)-MIN(stime.departuretime) AS time "
						+ "		FROM gtfs_stop_times stime INNER JOIN urbanfilteredstops AS stops on stime.trip_agencyid = stops.agencyid and stime.stop_id = stops.stopid"
						+ "		WHERE arrivaltime>0 AND departuretime>0 "
						+ "		GROUP BY stime.trip_agencyid,areaid,stime.trip_id),"
						+ "svc_hours AS (SELECT agencyid, areaid, SUM(time) AS service_seconds "
						+ "		FROM trip_times GROUP BY agencyid, areaid),";
				
				//-------------------------------------route stops----------------------------------------------------------------
				query += "stops_count AS (SELECT map.agencyid, " + areaId + " AS areaid, COUNT(map.stopid) AS stops_count "
						+ "		FROM gtfs_stop_service_map AS map INNER JOIN  gtfs_stops AS stops "
						+ "		ON map.stopid = stops.id AND agencyid_def = stops.agencyid "
						+ "		WHERE map.agencyid IN " + agencies + " AND " + urbanStopsFilter
						+ "		GROUP BY map.agencyid, " + areaId + "),";

				//-----------------------------------service miles------------------------------------------------------------------
				query += "service_miles AS (SELECT agencyid,areaid, sum(length) AS service_miles FROM " + tripIntersections + " GROUP BY agencyid,areaid), ";

				//-----------------------------------service stops------------------------------------------------------------------
				query += "svc_stops AS (SELECT agencyid, areaid, sum(visits) AS service_stops FROM urbanfilteredstops GROUP BY agencyid, areaid), ";

				//-----------------------------------putting all together------------------------------------------------------------
				query += "combination_list AS (SELECT aids.aid AS agencyid, aids.agencyname, " + Id + " AS areaid, " + areaTypeName + " AS areaname "
						+ "		FROM aids CROSS JOIN " + table
						+ "		WHERE " + Id + " IN " + areas + ") "
						+ "SELECT combination_list.*, COALESCE(stops_count,0) AS route_stops, COALESCE(service_stops,0) AS service_stops, "
						+ "		COALESCE(route_miles,0) AS route_miles, COALESCE(service_miles,0) AS service_miles, COALESCE(service_seconds,0) AS service_seconds "
						+ "		FROM  combination_list LEFT JOIN stops_count USING(areaid,agencyid) "
						+ "		LEFT JOIN service_miles USING (areaid,agencyid) "
						+ "		LEFT JOIN route_miles USING (areaid,agencyid) "
						+ "		LEFT JOIN svc_hours USING (areaid,agencyid) "
						+ "		LEFT JOIN svc_stops USING (areaid,agencyid)";
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
						instance.serviceSeconds = rs.getInt("service_seconds");
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
			e.printStackTrace();
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
			String blocksUrbanFilter = "";
			
			if (areaType.equals("county")) {
				areaId = "LEFT(blockid,5)";
				Id = "countyid";
				mapTable = "census_counties_trip_map";
				table = "census_counties";
				areaTypeName = "cname";
			} else if (areaType.equals("urban")) {
				areaId = "block.urbanid";
				Id = "urbanid";
				mapTable = "census_urbans_trip_map";
				table = "census_urbans";
				areaTypeName = "uname";
			} else if (areaType.equals("congDist")) {
				areaId = "block.congdistid";
				Id = "congdistid";
				mapTable = "census_congdists_trip_map";
				table = "census_congdists";
				areaTypeName = "cname";
			} else if (areaType.equals("place")) {
				areaId = "block.placeid";
				Id = "placeid";
				mapTable = "census_places_trip_map";
				table = "census_places";
				areaTypeName = "pname";
			} else if (areaType.equals("odotRegion")) {
				areaId = "block.regionid";
				Id = "regionid";
				mapTable = "census_counties_trip_map";
				table = "(SELECT '1' AS regionid UNION SELECT '2a' UNION SELECT '2b' UNION SELECT '3' UNION SELECT '4' UNION SELECT '5') AS census_regions ";
				areaTypeName = "'Region '||regionid";
			}
		
			if (urbanFilter){
				blocksUrbanFilter = "INNER JOIN urbanfilter USING(urbanid)";			
			}else{
				blocksUrbanFilter = "";
			}

			String query = "WITH aids AS (SELECT id AS aid, name AS agencyname FROM gtfs_agencies WHERE id IN " + agencies + "), "
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
			query += "), trips as (select trip.agencyid as aid, map." + Id +" as areaid, trip.id as tripid, trip.route_id as routeid, round((map.length)::numeric,2) as length, "
					+ "		map.tlength as tlength, map.stopscount as stops,trip.stopscount as  ss " 
					+ "		from svcids inner join gtfs_trips trip using(serviceid_agencyid, serviceid_id)  "
					+ "		inner join " + mapTable + " map on trip.id = map.tripid and trip.agencyid = map.agencyid " 
					+ "		where trip.agencyid in " + agencies + " and map." + Id + " IN " + areas + " ), "

					+ " stops as (select stime.trip_agencyid as agencyid, areaid as stop_areaid, stime.stop_id as stopid, stop.location, stop.urbanid, "
					+ "		count(trips.aid) as service " 
					+ "		from gtfs_stops stop inner join gtfs_stop_times stime "
					+ "		on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id " 
					+ "		inner join trips on stime.trip_agencyid =trips.aid and stime.trip_id=trips.tripid "
					+ "		group by stime.trip_agencyid, areaid, stime.stop_id, stop.location, stop.urbanid), "
		
					+ " blocks as (select stops.agencyid, stop_areaid, block.urbanid, " + areaId + " as areaid, block." + popYear + " as population, block.poptype, "
					+ "		sum(stops.service) as service " 
					+ "		from census_blocks block inner join stops on st_dwithin(block.location, stops.location, " + sradius + ")  "
					+ "		where " + areaId + " IN " + areas + " group by stops.agencyid,stop_areaid,blockid), "
					
					//---------------------------------- population served
					+ " urbanfilter AS (SELECT urbanid, uname FROM census_urbans "
					+ "		WHERE " + uAreaFliterYear + " >= " + minUrbanPop + "  AND " + uAreaFliterYear + "<=" + maxUrbanPop + "), "
					+ " urbanfilteredblocks AS (SELECT blocks.* FROM blocks " + blocksUrbanFilter + "),"
					
					
					//---------------------------------- pop served by service and served at level of service
					+ " pop_ss as (select agencyid, areaid, poptype, pop_ss "
					+ "		from (select agencyid, areaid, stop_areaid, poptype, COALESCE(sum(population*service),0) as pop_ss " 
					+ "			from urbanfilteredblocks where areaid = stop_areaid "
					+ "			group by agencyid, areaid, stop_areaid, poptype) as temp "
					+ "		group by agencyid, areaid, poptype, pop_ss), "
					
					+ " pop_los as (select agencyid, areaid, poptype, pop_los "
					+ "		from (select agencyid, areaid, poptype, COALESCE(sum(population*service),0) as pop_los  "
					+ "			from urbanfilteredblocks where service > " + los + " and areaid = stop_areaid "
					+ "			group by agencyid, areaid, stop_areaid, poptype	) as temp "
					+ "		group by agencyid, areaid, poptype, pop_los), "

					//---------------------------------- unduplicated pop served
					+ "allstops AS (SELECT stops.agencyid AS agencyid_def, map.agencyid, map.stopid, stops.name AS stopname, stops.location "
					+ "		FROM gtfs_stop_service_map AS map INNER JOIN gtfs_stops AS stops "
					+ "		ON map.stopid = stops.id AND agencyid_def = stops.agencyid "
					+ "		WHERE map.agencyid IN " + agencies + ") ,"
					+ "allblocks AS (SELECT block.blockid, poptype, " + areaId + " AS areaid, urbanid, " + popYear + " AS population, "
					+ "		stops.stopid, stops.stopname, stops.agencyid "
					+ "		FROM census_blocks AS block JOIN allstops AS stops "
					+ "		ON ST_DISTANCE(block.location, stops.location)< " + sradius
					+ "		WHERE " + areaId + " IN " + areas + "),"
					+ " urbanfilteredallblocks AS (SELECT allblocks.* FROM allblocks " + blocksUrbanFilter + ") , "
					+ "pop_served0 AS (SELECT blockid, poptype, areaid, population, agencyid "
					+ "		FROM urbanfilteredallblocks GROUP BY blockid, poptype, areaid, population, agencyid), "
					+ "pop_served AS (select poptype, areaid, agencyid, sum(population) AS pop_served "
					+ "		FROM pop_served0 GROUP BY poptype, areaid, agencyid), "
					
					//---------------------------------- putting it all together
					+ "combination_list AS (select aids.aid AS agencyid, aids.agencyname, "	+ Id + " AS areaid," + areaTypeName	+ " AS areaname, poptype "
					+ "		FROM aids CROSS JOIN " + table	+ " CROSS JOIN (SELECT 'U' AS poptype UNION SELECT 'R' AS poptype ) AS poptype "
					+ "		WHERE " + Id + " IN " + areas + ") "
					+ " select combination_list.*, COALESCE(pop_served,0) AS pop_served, COALESCE(pop_ss,0) AS pop_ss, COALESCE(pop_los,0) AS pop_los "
					+ " 	FROM combination_list LEFT JOIN pop_served USING(areaid,agencyid,poptype) "
					+ " 	LEFT JOIN pop_ss AS ss USING(areaid,agencyid,poptype) "
					+ " 	LEFT JOIN pop_los AS los USING(areaid,agencyid,poptype) order by agencyname,areaname ";

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
			e.printStackTrace();
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
				blocksUrbanFilter = " INNER JOIN urbanfilter USING(urbanid)";
			else
				blocksUrbanFilter = "";
			

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
			query += "), trips as (select trip.agencyid as aid, map." + Id +" as areaid, trip.id as tripid, trip.route_id as routeid, round((map.length)::numeric,2) as length, "
					+ "		map.tlength as tlength, map.stopscount as stops,trip.stopscount as  ss " 
					+ "		from svcids inner join gtfs_trips trip using(serviceid_agencyid, serviceid_id)  "
					+ "		inner join " + mapTable + " map on trip.id = map.tripid and trip.agencyid = map.agencyid " 
					+ "		where trip.agencyid in " + agencies + " and map." + Id + " IN " + areas + " ), "

					+ " stops as (select stime.trip_agencyid as agencyid, areaid as stop_areaid, stime.stop_id as stopid, stop.location, stop.urbanid, "
					+ "		count(trips.aid) as service " 
					+ "		from gtfs_stops stop inner join gtfs_stop_times stime "
					+ "		on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id " 
					+ "		inner join trips on stime.trip_agencyid =trips.aid and stime.trip_id=trips.tripid "
					+ "		group by stime.trip_agencyid, areaid, stime.stop_id, stop.location, stop.urbanid), "
		
					+ " blocks as (select stops.agencyid, stop_areaid, block.urbanid, " + areaId + " as areaid, block.blockid, block.poptype, "
					+ "		sum(stops.service) as visits " 
					+ "		from census_blocks block inner join stops on st_dwithin(block.location, stops.location, " + sradius + ")  "
					+ "		where " + areaId + " IN " + areas + " group by stops.agencyid,stop_areaid,blockid), "
					
					//---------------------------------- population served
					+ " urbanfilter AS (SELECT urbanid, uname FROM census_urbans "
					+ "		WHERE " + uAreaFliterYear + " >= " + minUrbanPop + "  AND " + uAreaFliterYear + "<=" + maxUrbanPop + "), "
					+ " urbanfilteredblocks AS (SELECT blocks.* FROM blocks " + blocksUrbanFilter + "),";

			if (wac) {
				query += " emp_ss_wac AS (SELECT areaid, agencyid,sum(c000*visits) AS c000_ss_wac";
				for (String category : categories)
					query += ",sum(" + category + "*visits) AS " + category + "_ss_wac";

				query += " FROM " + wacDataset + " AS wac INNER JOIN urbanfilteredblocks USING(blockid) GROUP BY areaid, agencyid), "
						+ "emp_los_wac AS (SELECT blockid, areaid, sum(visits) AS visits, agencyid, c000 ";
				for (String category : categories)
					query += "," + category;

				query += "	FROM urbanfilteredblocks INNER JOIN " + wacDataset + " AS wac USING (blockid) "
						+ "	GROUP BY blockid, areaid, agencyid, c000";

				for (String category : categories)
					query += "," + category;

				query += "), "
						+ "emp_los_cumulated_wac AS (SELECT agencyid, areaid,sum(c000) AS c000_los_wac";
				for (String category : categories)
					query += ",sum(" + category + ") AS " + category + "_los_wac";

				query += "	FROM emp_los_wac GROUP BY areaid, agencyid), "
						+ "emp_served_wac AS (SELECT blockid, areaid, agencyid, c000";

				for (String category : categories)
					query += "," + category;

				query += "	FROM " + wacDataset + " INNER JOIN urbanfilteredblocks USING (blockid) "
						+ "	GROUP BY blockid, areaid, agencyid, c000";

				for (String category : categories)
					query += "," + category;

				query += "), emp_served_cumulated_wac AS (SELECT agencyid, areaid, sum(c000) AS c000_served_wac";

				for (String category : categories)
					query += ",sum( " + category + ") AS " + category
							+ "_served_wac";

				query += "	FROM emp_served_wac GROUP BY agencyid, areaid), ";

			}
			if (rac) {
				query += " emp_ss_rac AS (SELECT areaid, agencyid,sum(c000" + empYear + "*visits) AS c000_ss_rac";
				for (String category : categories)
					query += ",sum(" + category + empYear + "*visits) AS " + category + "_ss_rac";

				query += " FROM " + racDataset + " AS rac INNER JOIN urbanfilteredblocks USING(blockid) GROUP BY areaid, agencyid), "
						+ "emp_los_rac AS (SELECT blockid, areaid, sum(visits) AS visits, agencyid,c000 ";
				for (String category : categories)
					query += "," + category + empYear ;

				query += "	FROM urbanfilteredblocks INNER JOIN " + racDataset + " AS rac USING (blockid) "
						+ "	GROUP BY blockid, areaid, agencyid, c000" + empYear;

				for (String category : categories)
					query += "," + category + empYear;

				query += "), "
						+ "emp_los_cumulated_rac AS (SELECT agencyid, areaid, sum(c000" + empYear + ") AS c000_los_rac ";
				for (String category : categories)
					query += ",sum(" + category + empYear + ") AS " + category + "_los_rac";

				query += "	FROM emp_los_rac GROUP BY areaid, agencyid), "
						+ "emp_served_rac AS (SELECT blockid, areaid, agencyid, c000" + empYear;

				for (String category : categories)
					query += "," + category + empYear;

				query += "	FROM " + racDataset + " INNER JOIN urbanfilteredblocks USING (blockid) "
						+ "	GROUP BY blockid, areaid, agencyid,c000" + empYear;

				for (String category : categories)
					query += "," + category + empYear;

				query += "), emp_served_cumulated_rac AS (SELECT agencyid, areaid, sum(c000" + empYear + ") AS c000_served_rac";

				for (String category : categories)
					query += ", sum( " + category + empYear + ") AS " + category
							+ "_served_rac";

				query += "	FROM emp_served_rac GROUP BY agencyid, areaid), ";
			}

			if (wac && rac) {
				query += " combination_list AS (select aids.aid AS agencyid, aids.agencyname, " + Id + " AS areaid, " + areaTypeName + " AS areaname "
						+ "			FROM aids CROSS JOIN " + table
						+ "			WHERE " + Id + " IN " + areas + ") "
						+ "SELECT combination_list.*, c000_served_wac, c000_los_wac, c000_ss_wac, c000_served_rac, c000_los_rac, c000_ss_rac";
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
						+ "SELECT combination_list.*, c000_served_wac, c000_los_wac, c000_ss_wac ";
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
						+ "SELECT combination_list.*, c000_served_rac, c000_los_rac, c000_ss_rac ";
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
			e.printStackTrace();
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
			e.printStackTrace();
		}finally{
			if (stmt != null)try{stmt.close();}catch(SQLException e){};
			if (connection != null)try{connection.close();}catch(SQLException e){};
		}
		return output;
	}
}
