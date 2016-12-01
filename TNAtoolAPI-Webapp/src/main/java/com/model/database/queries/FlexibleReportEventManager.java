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
import com.model.database.queries.flexibleReport.FlexRepT6;
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
		return output;
	}

	public static List<FlexRepPop> getFlexRepPop(int dbindex, String agencies,
			String[] date, String[] day, String popYear, String areas, int los,
			double sradius, String areaType, String username, int minUrbanPop,
			int maxUrbanPop) throws SQLException {
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
		} else if (areaType.equals("odotRegion")) {
			areaId = "regionid";
			Id = "regionid";
			mapTable = "census_counties_trip_map";
			table = "(SELECT '1' AS regionid UNION SELECT '2a' UNION SELECT '2b' UNION SELECT '3' UNION SELECT '4' UNION SELECT '5') AS census_regions ";
			areaTypeName = "'Region '||regionid";
		}

		String query = "WITH aids AS (SELECT id AS aid, name AS agencyname FROM gtfs_agencies WHERE id IN "
				+ agencies
				+ "), "

				+ "allstops AS (SELECT stops.agencyid AS agencyid_def, map.agencyid, map.stopid, stops.name AS stopname, stops.location "
				+ "FROM gtfs_stop_service_map AS map INNER JOIN gtfs_stops AS stops  "
				+ "ON map.stopid = stops.id AND agencyid_def = stops.agencyid "
				+ "WHERE map.agencyid IN "
				+ agencies
				+ ") , "

				+ "allblocks AS (SELECT blocks.blockid, poptype, "
				+ areaId
				+ " AS areaid, urbanid,  "
				+ popYear
				+ " AS population, "
				+ "stops.stopid, stops.stopname, stops.agencyid "
				+ "FROM census_blocks AS blocks JOIN allstops AS stops "
				+ "ON ST_DISTANCE(blocks.location, stops.location)< "
				+ sradius
				+ " " + "WHERE " + areaId + " IN " + areas + "), "

				+ "svcids AS (";
		for (int i = 0; i < date.length; i++) {
			query += "(select serviceid_agencyid, serviceid_id from gtfs_calendars gc inner join aids on gc.serviceid_agencyid = aids.aid where startdate::int<="
					+ date[i]
					+ " and enddate::int>="
					+ date[i]
					+ " and "
					+ day[i]
					+ " = 1 and serviceid_agencyid||serviceid_id not in (select serviceid_agencyid||serviceid_id from "
					+ "gtfs_calendar_dates where date='"
					+ date[i]
					+ "' and exceptiontype=2) union select serviceid_agencyid, "
					+ "serviceid_id from gtfs_calendar_dates gcd inner join aids on gcd.serviceid_agencyid = aids.aid where date='"
					+ date[i] + "' and exceptiontype=1)";
			if (i + 1 < date.length)
				query += " union all ";
		}
		query += "), trips as (select trip.route_agencyid as aid, trip.route_id as routeid, map.tripid, map.length, map.tlength  "
				+ "FROM svcids INNER JOIN gtfs_trips trip USING(serviceid_agencyid, serviceid_id)  "
				+ "INNER JOIN "
				+ mapTable
				+ "  AS map ON trip.id = map.tripid AND trip.agencyid = map.agencyid WHERE map."
				+ Id
				+ " IN "
				+ areas
				+ "), "

				+ "tempstops AS (SELECT stoptimes.stop_agencyid AS agencyid_def, trip_agencyid AS agencyid, stoptimes.stop_id, count(tripid) AS visits "
				+ "FROM trips INNER JOIN gtfs_stop_times AS stoptimes  "
				+ "ON stoptimes.trip_agencyid = trips.aid AND trips.tripid = stoptimes.trip_id GROUP BY stop_agencyid, stop_id,trip_agencyid), "

				+ "stops AS (SELECT tempstops.agencyid, stops.agencyid AS agencyid_def, stops.id AS stopid, stops.name AS stopname, stops.location, tempstops.visits "
				+ "FROM tempstops INNER JOIN gtfs_stops AS stops ON stop_id = id AND agencyid_def = stops.agencyid), "

				+ "blocks AS (SELECT blocks.blockid, poptype, "
				+ areaId
				+ " AS areaid, urbanid, "
				+ popYear
				+ " AS population, "
				+ "stops.stopid, stops.stopname, stops.agencyid, visits "
				+ "FROM census_blocks AS blocks JOIN stops  "
				+ "ON ST_DISTANCE(blocks.location, stops.location)<"
				+ sradius
				+ "WHERE "
				+ areaId
				+ " IN "
				+ areas
				+ "), "
				+ "urbanfilter AS (SELECT urbanid, uname, utype, landarea, waterarea, "
				+ popYear
				+ " FROM census_urbans  "
				+ "WHERE "
				+ popYear
				+ " >= "
				+ minUrbanPop
				+ "  AND "
				+ popYear
				+ "<="
				+ maxUrbanPop
				+ "), "
				+ "urbanfilteredblocks AS (SELECT blocks.* FROM blocks INNER JOIN urbanfilter USING(urbanid) UNION ALL SELECT * FROM blocks WHERE poptype='R'), "

				+ "urbanfilteredallblocks AS (SELECT allblocks.* FROM allblocks INNER JOIN urbanfilter USING(urbanid) UNION ALL SELECT * FROM allblocks WHERE poptype='R'), "

				+ "pop_served AS (SELECT blockid, poptype, areaid, population, agencyid FROM allblocks GROUP BY blockid, poptype, areaid, population, agencyid), "
				+ "pop_los AS (SELECT blockid, poptype, areaid, population, agencyid FROM urbanfilteredblocks WHERE visits > "
				+ los
				+ " GROUP BY blockid, poptype, areaid, population, agencyid ), "
				+ "pop_served_cumulated AS (select poptype, areaid, agencyid, sum(population) AS pop_served from pop_served GROUP BY poptype, areaid, agencyid), "
				+ "pop_los_cumulated AS (select poptype, areaid, agencyid, sum(population) AS pop_los from pop_los GROUP BY poptype, areaid, agencyid), "
				+ "pop_ss_cumulated AS (select poptype, areaid, agencyid, sum(population*visits) AS pop_ss from urbanfilteredblocks GROUP BY poptype, areaid, agencyid), "
				+ "combination_list AS (select aids.aid AS agencyid, aids.agencyname, "
				+ Id
				+ " AS areaid, "
				+ areaTypeName
				+ " AS areaname, poptype  "
				+ "FROM aids CROSS JOIN "
				+ table
				+ " "
				+ "CROSS JOIN (SELECT 'U' AS poptype UNION SELECT 'R' AS poptype ) AS poptype "
				+ "WHERE "
				+ Id
				+ " IN "
				+ areas
				+ ")  "

				+ " select combination_list.*, served.pop_served, ss.pop_ss, los.pop_los  "
				+ "FROM combination_list LEFT JOIN pop_served_cumulated  AS served USING(areaid,agencyid,poptype) "
				+ "LEFT JOIN pop_ss_cumulated AS ss USING(areaid,agencyid,poptype) "
				+ "LEFT JOIN pop_los_cumulated AS los USING(areaid,agencyid,poptype)";

		ResultSet rs = stmt.executeQuery(query);
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
		}
		connection.close();
		return output;
	};

	public static List<FlexRepEmp> getFlexRepEmp(int dbindex, String agencies,
			String[] date, String[] day, String empYear, String areas, int los,
			double sradius, String areaType, String username, int minUrbanPop,
			int maxUrbanPop, boolean wac, boolean rac, String[] categories)
			throws SQLException, NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		String urbanYear = "";
		String wacDataset = "lodes_blocks_wac";
		String racDataset = "lodes_blocks_rac";
		
		if (empYear.equals("current")){
			urbanYear = "2010";
			empYear = "";
		}
		else{
			urbanYear = empYear;
			empYear = "_" + empYear;
			racDataset = "lodes_rac_projection_block";
			wac = false; // We don't have projection data on WAC.
		}
			
		List<FlexRepEmp> output = new ArrayList<FlexRepEmp>();
		agencies = "('" + agencies.replace(",", "','") + "')";
		areas = "('" + areas.replace(",", "','") + "')";

		Connection connection = PgisEventManager.makeConnection(dbindex);
		Statement stmt = connection.createStatement();

		String areaId = "";
		String Id = "";
		String mapTable = "";
		String table = "";
		String areaTypeName = "";
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

		String query = "WITH aids AS (SELECT id AS aid, name AS agencyname FROM gtfs_agencies WHERE id IN "
				+ agencies
				+ "), "

				+ "allstops AS (SELECT stops.agencyid AS agencyid_def, map.agencyid, map.stopid, stops.name AS stopname, stops.location "
				+ "FROM gtfs_stop_service_map AS map INNER JOIN gtfs_stops AS stops  "
				+ "ON map.stopid = stops.id AND agencyid_def = stops.agencyid "
				+ "WHERE map.agencyid IN "
				+ agencies
				+ ") , "

				+ "allblocks AS (SELECT blocks.blockid, poptype, "
				+ areaId
				+ " AS areaid, urbanid, "
				+ "stops.stopid, stops.stopname, stops.agencyid "
				+ "FROM census_blocks AS blocks JOIN allstops AS stops "
				+ "ON ST_DISTANCE(blocks.location, stops.location)< "
				+ sradius
				+ " " + "WHERE " + areaId + " IN " + areas + "), "

				+ "svcids AS (";
		for (int i = 0; i < date.length; i++) {
			query += "(select serviceid_agencyid, serviceid_id from gtfs_calendars gc inner join aids on gc.serviceid_agencyid = aids.aid where startdate::int<="
					+ date[i]
					+ " and enddate::int>="
					+ date[i]
					+ " and "
					+ day[i]
					+ " = 1 and serviceid_agencyid||serviceid_id not in (select serviceid_agencyid||serviceid_id from "
					+ "gtfs_calendar_dates where date='"
					+ date[i]
					+ "' and exceptiontype=2) union select serviceid_agencyid, "
					+ "serviceid_id from gtfs_calendar_dates gcd inner join aids on gcd.serviceid_agencyid = aids.aid where date='"
					+ date[i] + "' and exceptiontype=1)";
			if (i + 1 < date.length)
				query += " union all ";
		}
		query += "), trips as (select trip.route_agencyid as aid, trip.route_id as routeid, map.tripid, map.length, map.tlength  "
				+ "FROM svcids INNER JOIN gtfs_trips trip USING(serviceid_agencyid, serviceid_id)  "
				+ "INNER JOIN "
				+ mapTable
				+ "  AS map ON trip.id = map.tripid AND trip.agencyid = map.agencyid WHERE map."
				+ Id
				+ " IN "
				+ areas
				+ "), "

				+ "tempstops AS (SELECT stoptimes.stop_agencyid AS agencyid_def, trip_agencyid AS agencyid, stoptimes.stop_id, count(tripid) AS visits "
				+ "FROM trips INNER JOIN gtfs_stop_times AS stoptimes  "
				+ "ON stoptimes.trip_agencyid = trips.aid AND trips.tripid = stoptimes.trip_id GROUP BY stop_agencyid, stop_id,trip_agencyid), "

				+ "stops AS (SELECT tempstops.agencyid, stops.agencyid AS agencyid_def, stops.id AS stopid, stops.name AS stopname, stops.location, tempstops.visits "
				+ "FROM tempstops INNER JOIN gtfs_stops AS stops ON stop_id = id AND agencyid_def = stops.agencyid), "

				+ "blocks AS (SELECT blocks.blockid, poptype, "
				+ areaId
				+ " AS areaid, urbanid, "
				+ "stops.stopid, stops.stopname, stops.agencyid, visits "
				+ "FROM census_blocks AS blocks JOIN stops  "
				+ "ON ST_DISTANCE(blocks.location, stops.location)<"
				+ sradius
				+ "WHERE "
				+ areaId
				+ " IN "
				+ areas
				+ "), "
				+ "urbanfilter AS (SELECT urbanid, uname, utype, landarea, waterarea, population"
				+ urbanYear
				+ " FROM census_urbans  "
				+ "WHERE population"
				+ urbanYear
				+ " >= "
				+ minUrbanPop
				+ "  AND population"
				+ urbanYear
				+ "<="
				+ maxUrbanPop
				+ "), "
				+ "urbanfilteredblocks AS (SELECT blocks.* FROM blocks INNER JOIN urbanfilter USING(urbanid) UNION ALL SELECT * FROM blocks WHERE poptype='R'), "

				+ "urbanfilteredallblocks AS (SELECT allblocks.* FROM allblocks INNER JOIN urbanfilter USING(urbanid) UNION ALL SELECT * FROM allblocks WHERE poptype='R'), ";

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

			query += "	FROM " + wacDataset + " INNER JOIN urbanfilteredallblocks USING (blockid) "
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

			query += "	FROM " + racDataset + " INNER JOIN urbanfilteredallblocks USING (blockid) "
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
			query += " combination_list AS (select aids.aid AS agencyid, aids.agencyname, " + Id + " AS areaid, cname AS areaname "
					+ "			FROM aids CROSS JOIN " + table
					+ "			WHERE " + Id + " IN "
					+ areas
					+ ") "
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
			query += " combination_list AS (select aids.aid AS agencyid, aids.agencyname, " + Id + " AS areaid, cname AS areaname "
					+ "			FROM aids CROSS JOIN " + table
					+ "			WHERE " + Id + " IN "
					+ areas
					+ ") "
					+ "SELECT combination_list.* ";
			for (String category : categories) {
				query += "," + category + "_served_wac, " + category
						+ "_los_wac, " + category + "_ss_wac";
			}
			query += "	FROM  combination_list LEFT JOIN emp_ss_wac USING(areaid,agencyid) "
					+ "	LEFT JOIN emp_served_cumulated_wac USING (areaid,agencyid) "
					+ "	LEFT JOIN emp_los_cumulated_wac USING (areaid,agencyid) ";
		} else {
			query += " combination_list AS (select aids.aid AS agencyid, aids.agencyname, " + Id + " AS areaid, cname AS areaname "
					+ "			FROM aids CROSS JOIN " + table
					+ "			WHERE " + Id + " IN "
					+ areas
					+ ") "
					+ "SELECT combination_list.* ";
			for (String category : categories) {
				query += "," + category + "_served_rac, " + category
						+ "_los_rac, " + category + "_ss_rac";
			}
			query += "	FROM  combination_list LEFT JOIN emp_ss_rac USING(areaid,agencyid) "
					+ "	LEFT JOIN emp_served_cumulated_rac USING (areaid,agencyid) "
					+ "	LEFT JOIN emp_los_cumulated_rac USING (areaid,agencyid) ";
		}
		System.out.println(query);
		ResultSet rs = stmt.executeQuery(query);
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
		}
		connection.close();
		return output;
	}

	public static List<FlexRepT6> getFlexRepT6(int dbindex, String agencies,
			String[] date, String[] day, String areas, int los,
			double sradius, String areaType, String username, int minUrbanPop,
			int maxUrbanPop, String[] categories)
			throws SQLException, NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		
			
		List<FlexRepT6> output = new ArrayList<FlexRepT6>();
		agencies = "('" + agencies.replace(",", "','") + "')";
		areas = "('" + areas.replace(",", "','") + "')";

		Connection connection = PgisEventManager.makeConnection(dbindex);
		Statement stmt = connection.createStatement();

		String areaId = "";
		String Id = "";
		String mapTable = "";
		String table = "";
		String areaTypeName = "";
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

		String query = "WITH aids AS (SELECT id AS aid, name AS agencyname FROM gtfs_agencies WHERE id IN "
				+ agencies
				+ "), "

				+ "allstops AS (SELECT stops.agencyid AS agencyid_def, map.agencyid, map.stopid, stops.name AS stopname, stops.location "
				+ "FROM gtfs_stop_service_map AS map INNER JOIN gtfs_stops AS stops  "
				+ "ON map.stopid = stops.id AND agencyid_def = stops.agencyid "
				+ "WHERE map.agencyid IN "
				+ agencies
				+ ") , "

				+ "allblocks AS (SELECT blocks.blockid, poptype, "
				+ areaId
				+ " AS areaid, urbanid, "
				+ "stops.stopid, stops.stopname, stops.agencyid "
				+ "FROM census_blocks AS blocks JOIN allstops AS stops "
				+ "ON ST_DISTANCE(blocks.location, stops.location)< "
				+ sradius
				+ " " + "WHERE " + areaId + " IN " + areas + "), "

				+ "svcids AS (";
		for (int i = 0; i < date.length; i++) {
			query += "(select serviceid_agencyid, serviceid_id from gtfs_calendars gc inner join aids on gc.serviceid_agencyid = aids.aid where startdate::int<="
					+ date[i]
					+ " and enddate::int>="
					+ date[i]
					+ " and "
					+ day[i]
					+ " = 1 and serviceid_agencyid||serviceid_id not in (select serviceid_agencyid||serviceid_id from "
					+ "gtfs_calendar_dates where date='"
					+ date[i]
					+ "' and exceptiontype=2) union select serviceid_agencyid, "
					+ "serviceid_id from gtfs_calendar_dates gcd inner join aids on gcd.serviceid_agencyid = aids.aid where date='"
					+ date[i] + "' and exceptiontype=1)";
			if (i + 1 < date.length)
				query += " union all ";
		}
		query += "), trips as (select trip.route_agencyid as aid, trip.route_id as routeid, map.tripid, map.length, map.tlength  "
				+ "FROM svcids INNER JOIN gtfs_trips trip USING(serviceid_agencyid, serviceid_id)  "
				+ "INNER JOIN "
				+ mapTable
				+ "  AS map ON trip.id = map.tripid AND trip.agencyid = map.agencyid WHERE map."
				+ Id
				+ " IN "
				+ areas
				+ "), "

				+ "tempstops AS (SELECT stoptimes.stop_agencyid AS agencyid_def, trip_agencyid AS agencyid, stoptimes.stop_id, count(tripid) AS visits "
				+ "FROM trips INNER JOIN gtfs_stop_times AS stoptimes  "
				+ "ON stoptimes.trip_agencyid = trips.aid AND trips.tripid = stoptimes.trip_id GROUP BY stop_agencyid, stop_id,trip_agencyid), "

				+ "stops AS (SELECT tempstops.agencyid, stops.agencyid AS agencyid_def, stops.id AS stopid, stops.name AS stopname, stops.location, tempstops.visits "
				+ "FROM tempstops INNER JOIN gtfs_stops AS stops ON stop_id = id AND agencyid_def = stops.agencyid), "

				+ "blocks AS (SELECT blocks.blockid, poptype, "
				+ areaId
				+ " AS areaid, urbanid, "
				+ "stops.stopid, stops.stopname, stops.agencyid, visits "
				+ "FROM census_blocks AS blocks JOIN stops  "
				+ "ON ST_DISTANCE(blocks.location, stops.location)<"
				+ sradius
				+ "WHERE "
				+ areaId
				+ " IN "
				+ areas
				+ "), "
				+ "urbanfilter AS (SELECT urbanid, uname, utype, landarea, waterarea, population2010 FROM census_urbans  "
				+ "WHERE population2010 >= " + minUrbanPop + " AND population2010 <="
				+ maxUrbanPop
				+ "), "
				+ "urbanfilteredblocks AS (SELECT blocks.* FROM blocks INNER JOIN urbanfilter USING(urbanid) UNION ALL SELECT * FROM blocks WHERE poptype='R'), "

				+ "urbanfilteredallblocks AS (SELECT allblocks.* FROM allblocks INNER JOIN urbanfilter USING(urbanid) UNION ALL SELECT * FROM allblocks WHERE poptype='R'), ";

		
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

			query += "	FROM title_vi_blocks_float INNER JOIN urbanfilteredallblocks USING (blockid) "
					+ "	GROUP BY blockid, areaid, agencyid";

			for (String category : categories)
				query += "," + category;

			query += "), t6_served_cumulated AS (SELECT agencyid, areaid";

			for (String category : categories)
				query += ", sum( " + category + ")::int AS " + category
						+ "_served";

			query += "	FROM t6_served GROUP BY agencyid, areaid), ";
		

		
			query += " combination_list AS (select aids.aid AS agencyid, aids.agencyname, " + Id + " AS areaid, cname AS areaname "
					+ "			FROM aids CROSS JOIN " + table
					+ "			WHERE " + Id + " IN "
					+ areas
					+ ") "
					+ "SELECT combination_list.* ";
			for (String category : categories) {
				query += "," + category + "_served, " + category
						+ "_los, " + category + "_ss";
			}
			query += "	FROM  combination_list LEFT JOIN t6_ss USING(areaid,agencyid) "
					+ "	LEFT JOIN t6_served_cumulated USING (areaid,agencyid) "
					+ "	LEFT JOIN t6_los_cumulated USING (areaid,agencyid) ";
		
		System.out.println(query);
		ResultSet rs = stmt.executeQuery(query);
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
		}
		connection.close();
		return output;
	}
}
