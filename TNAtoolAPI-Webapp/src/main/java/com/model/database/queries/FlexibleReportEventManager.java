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

}
