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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.onebusaway.gtfs.model.*;

//import com.model.database.onebusaway.gtfs.hibernate.objects.ext.StopExt;
//import com.model.database.onebusaway.gtfs.hibernate.objects.ext.StopTimeExt;
//import com.model.database.onebusaway.gtfs.hibernate.objects.ext.TripExt;
import com.model.database.queries.congraph.AgencyCentroid;
import com.model.database.queries.congraph.ConGraphAgency;
/*import com.library.model.TransitConnection;
import com.library.model.ParknRide;
import com.library.model.agencyCluster;
import com.library.model.congrapph.Agency;
import com.library.model.congrapph.AgencyCentroid;
import com.library.model.congrapph.ConGraphAgencyGraph;
import com.library.model.congrapph.ConGraphCluster;
import com.library.model.congrapph.ConGraphObj;
import com.library.model.congrapph.Coordinate;*/
import com.model.database.queries.congraph.ConGraphAgencyGraph;
import com.model.database.queries.congraph.ConGraphCluster;
import com.model.database.queries.congraph.ConGraphObj;
import com.model.database.queries.congraph.Coordinate;
import com.model.database.queries.objects.ParknRide;
import com.model.database.queries.objects.TransitConnection;

public class SpatialEventManager {

	/**
	 * returns a list of park and rides that are located within
	 * a circle or polygon.
	 * 
	 * @param lat
	 * @param lon
	 * @param radius
	 * @param dbindex
	 * @return List<ParknRide>
	 * @throws SQLException
	 */
	public static List<ParknRide> getPnRs(double[] lat, double[] lon,
			double radius, int dbindex) throws SQLException {
		List<ParknRide> output = new ArrayList<ParknRide>();
		Connection connection = PgisEventManager.makeConnection(dbindex);
		Statement stmt = null;
		String query;
		if (lat.length == 1) // If selected area is a circle
			query = "SELECT parknride.*, county.countyid AS county_id, county.cname AS county_name "
					+ " FROM parknride INNER JOIN census_counties AS county "
					+ " ON ST_Within(ST_Setsrid(ST_Makepoint(parknride.lon,parknride.lat),4326), county.shape) "
					+ " WHERE ST_DWITHIN(parknride.geom,ST_transform(ST_setsrid(ST_MakePoint("
					+ lon[0] + ", " + lat[0] + "),4326), 2993), " + radius
					+ ")";
		else { // If selected area is a polygon or rectangle
			query = "SELECT parknride.*, county.countyid AS county_id, county.cname AS county_name "
					+ " FROM parknride INNER JOIN census_counties AS county "
					+ " ON ST_Within(ST_Setsrid(ST_Makepoint(parknride.lon,parknride.lat),4326), county.shape) "
					+ " WHERE ST_CONTAINS( ST_transform(st_geometryfromtext('POLYGON((";
			for (int i = 0; i < lat.length; i++) {
				query += lon[i] + " " + lat[i] + ",";
			}
			query = query += lon[0] + " " + lat[0]; // Closing the polygon loop
			query += "))', 4326),2993), parknride.geom)";
		}
		stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			ParknRide i = new ParknRide();
			i.pnrid = rs.getInt("pnrid");
			i.lat = rs.getDouble("lat");
			i.lon = rs.getDouble("lon");
			i.lotname = rs.getString("lotname");
			i.location = rs.getString("location");
			i.city = rs.getString("city");
			i.zipcode = rs.getInt("zipcode");
			i.countyid = rs.getString("county_id");
			i.county = rs.getString("county_name");
			i.spaces = rs.getInt("spaces");
			i.accessiblespaces = rs.getInt("accessiblespaces");
			i.bikerackspaces = rs.getInt("bikerackspaces");
			i.bikelockerspaces = rs.getInt("bikelockerspaces");
			i.electricvehiclespaces = rs.getInt("electricvehiclespaces");
			i.carsharing = rs.getString("carsharing");
			i.transitservice = rs.getString("transitservice");
			i.availability = rs.getString("availability");
			i.timelimit = rs.getString("timelimit");
			i.restroom = rs.getString("restroom");
			i.benches = rs.getString("benches");
			i.shelter = rs.getString("shelter");
			i.indoorwaitingarea = rs.getString("indoorwaitingarea");
			i.trashcan = rs.getString("trashcan");
			i.lighting = rs.getString("lighting");
			i.securitycameras = rs.getString("securitycameras");
			i.sidewalks = rs.getString("sidewalks");
			i.pnrsignage = rs.getString("pnrsignage");
			i.lotsurface = rs.getString("lotsurface");
			i.propertyowner = rs.getString("propertyowner");
			i.localexpert = rs.getString("localexpert");
			output.add(i);
		}
		return output;
	}
	
	/**
	 * returns trips of a route
	 * 
	 * @param agencyID
	 * @param routeID
	 * @param dbindex
	 * @return List<Trip>
	 * @throws SQLException
	 */
	public static List<Trip> QueryTripsbyRoute(String agencyID, String routeID, int dbindex) throws SQLException{
		List<Trip> output = new ArrayList<Trip>();
		String query = "SELECT * FROM gtfs_trips WHERE route_agencyid = '" + agencyID + "' AND route_id = '" + routeID + "'";
		Connection connection = PgisEventManager.makeConnection(dbindex);
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		Trip trip;
		while ( rs.next() ){
			trip = new Trip();
			trip.setId(new AgencyAndId(rs.getString("agencyid"),rs.getString("id")));
			trip.setEpshape(rs.getString("epshape"));
			trip.setLength( rs.getDouble("length") + rs.getDouble("tlength") );
			trip.setTripHeadsign(rs.getString("tripheadsign"));
			trip.setBlockId(rs.getString("blockid"));
			trip.setServiceId(new AgencyAndId(rs.getString("serviceid_agencyid"), rs.getString("serviceid_id")));			
			output.add(trip);
		}
		connection.close();
		return output;
	}

	/**
	 * returns stop-times of a trip
	 * 
	 * @param agencyID
	 * @param tripID
	 * @param dbindex
	 * @return
	 * @throws SQLException
	 */
	public static List<StopTime> Querystoptimebytrip(String agencyID, String tripID, int dbindex) throws SQLException{
		List<StopTime> output = new ArrayList<StopTime>();
		String query = "SELECT stoptimes.*, stops.name AS stopsname "
				+ " FROM gtfs_stop_times AS stoptimes INNER JOIN gtfs_stops AS stops ON stops.id = stoptimes.stop_id AND stops.agencyid=stoptimes.stop_agencyid "
				+ " WHERE trip_agencyid = '" + agencyID + "' AND trip_id = '" + tripID + "'";
		Connection connection = PgisEventManager.makeConnection(dbindex);
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		StopTime st;
		while ( rs.next() ){
			st = new StopTime();
			st.setId(rs.getInt("gid"));
			st.setStopSequence(rs.getInt("stopsequence"));
			st.setArrivalTime(rs.getInt("arrivaltime"));
			st.setDepartureTime(rs.getInt("departuretime"));
			st.setStopHeadsign(rs.getString("stopheadsign"));
			st.setTimepoint(rs.getInt("timepoint"));
			Stop stop = new Stop();
			AgencyAndId agencyAndId = new AgencyAndId(rs.getString("stop_agencyid"), rs.getString("stop_id"));
			stop.setName(rs.getString("stopsname"));
			stop.setId(agencyAndId);
			st.setStop(stop);			
			output.add(st);
		}
		connection.close();
		return output;
	}
	
	/**
	 * return all agencies active in the database
	 * 
	 * @param username
	 * @param dbindex
	 * @return HashMap<String, ConGraphAgency>
	 * @throws SQLException
	 */
	public static HashMap<String, ConGraphAgency> getAllAgencies ( String username, int dbindex ) throws SQLException {
		HashMap<String,ConGraphAgency> response = new HashMap<String, ConGraphAgency>();
		String query = "SELECT * FROM gtfs_agencies WHERE gtfs_agencies.defaultid IN (SELECT DISTINCT agency_id AS aid "
				+ " FROM gtfs_selected_feeds WHERE username='" + username + "') "
				+ " AND gtfs_agencies.id IN (SELECT trip_agencyid FROM gtfs_stop_times GROUP BY trip_agencyid) ORDER BY name";
		Connection connection = PgisEventManager.makeConnection(dbindex);
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		
		while ( rs.next() ){
			ConGraphAgency i = new ConGraphAgency();
			i.name = rs.getString("name");
			i.centralized = rs.getBoolean("centralized");
			i.id = rs.getString("id");
			response.put(rs.getString("id"), i);
		}
		connection.close();
		return response;
	}
	
	/**
	 * returns all the connections between the input agency and the other agencies
	 * in the database. Each ConGraphObj represents a connection which will be 
	 * displayed on the map as an edge on the Connectivity Graph.
	 * 
	 * @param agencyID
	 * @param agencyName
	 * @param fulldate
	 * @param day
	 * @param username
	 * @param radius - connection distance
	 * @param stmt - database statement
	 * @return Set<ConGraphObj>
	 * @throws SQLException
	 */
	public static Set<ConGraphObj> getConGraphObj(String agencyID, 
			String agencyName, 
			String fulldate, 
			String day, 
			String username, 
			double radius, 
			Statement stmt
			) throws SQLException{
		Set<ConGraphObj> response = new HashSet<ConGraphObj>();
		String query = "with aids as (select agency_id as aid from gtfs_selected_feeds where username='" + username + "'), "
				+ "svcids as (select serviceid_agencyid, serviceid_id "
				+ "	from gtfs_calendars gc inner join aids on gc.serviceid_agencyid = '" + agencyID + "' "
				+ "	where startdate::int<= " + fulldate + "  and enddate::int>= " + fulldate + "  and  " + day + " = 1 and serviceid_agencyid||serviceid_id "
				+ "	not in (select serviceid_agencyid||serviceid_id from gtfs_calendar_dates where date='" + fulldate + "' and exceptiontype=2) "
				+ "	union select serviceid_agencyid, serviceid_id "
				+ "	from gtfs_calendar_dates gcd inner join aids on gcd.serviceid_agencyid = '" + agencyID + "' where date='" + fulldate + "' and exceptiontype=1), "
				+ " trips AS (select trip.agencyid as aid, trip.id as tripid, trip.route_id as routeid "
				+ "	from svcids inner join gtfs_trips trip using(serviceid_agencyid, serviceid_id)), "
				+ " a1stops AS (select stime.trip_agencyid as agencyid, gtfs_agencies.name as agencyname, stime.stop_id as stopid, stop.name as name, stop.lat, stop.lon, stop.location as location "
				+ "	from gtfs_stops stop inner join gtfs_stop_times stime on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id "
				+ "	inner join trips on stime.trip_agencyid =trips.aid and stime.trip_id=trips.tripid "
				+ "	inner join gtfs_agencies ON stime.trip_agencyid = gtfs_agencies.id "
				+ "	where stop.agencyid IN (SELECT aid FROM aids) "
				+ "	group by stime.trip_agencyid, stime.stop_agencyid, stime.stop_id, stop.location, gtfs_agencies.name, stop.name, stop.lat, stop.lon), "
				+ " svcids1 as (select serviceid_agencyid, serviceid_id "
				+ "	from gtfs_calendars gc inner join aids on gc.serviceid_agencyid = aids.aid "
				+ "	where startdate::int<= " + fulldate + "  and enddate::int>= " + fulldate + "  and  " + day + "  = 1 and gc.serviceid_agencyid <> '" + agencyID + "' "
				+ "	and serviceid_agencyid||serviceid_id not in (select serviceid_agencyid||serviceid_id from gtfs_calendar_dates where date='" + fulldate + "' and exceptiontype=2) "
				+ " union select serviceid_agencyid, serviceid_id "
				+ "	from gtfs_calendar_dates gcd inner join aids on gcd.serviceid_agencyid = aids.aid where date='" + fulldate + "' and exceptiontype=1 and gcd.serviceid_agencyid <> '" + agencyID + "' ), "
				+ " trips1 AS (select trip.agencyid as aid, trip.id as tripid, trip.route_id as routeid "
				+ "	from svcids1 inner join gtfs_trips trip using(serviceid_agencyid, serviceid_id)), "
				+ " active_stops1 AS (SELECT stop_agencyid, trip_agencyid,stop_id "
				+ "	FROM gtfs_stop_times AS stoptimes INNER JOIN trips1 ON trip_id = tripid AND trip_agencyid = aid "
				+ "	GROUP BY stop_agencyid, trip_agencyid,stop_id), "
				+ " active_stops_aggregated1 AS (SELECT active_stops1.trip_agencyid AS aid, ST_Collect(stops.location) AS shape "
				+ "	FROM active_stops1 INNER JOIN gtfs_stops AS stops "
				+ " ON active_stops1.stop_id = stops.id AND active_stops1.stop_agencyid = stops.agencyid "
				+ "	GROUP BY active_stops1.trip_agencyid order by aid), "
				+ " connected_agencies AS (SELECT '" + agencyID + "'::text AS a1id, asa1.aid AS a2id FROM active_stops_aggregated1 AS asa1 "
				+ " INNER JOIN a1stops ON ST_DWithin(asa1.shape,a1stops.location," + radius + ") GROUP BY asa1.aid) "
				+ " select a1id, agencies1.name AS a1name, a2id, agencies2.name AS a2name "
				+ "	FROM connected_agencies JOIN gtfs_agencies AS agencies1 ON a1id=agencies1.id "
				+ "	JOIN gtfs_agencies AS agencies2 ON a2id=agencies2.id;";
		try{
			ResultSet rs = stmt.executeQuery(query);
			
			// Initialize a ConGraphObj for isolated agencies. 
			if (!rs.next()){
				ConGraphObj instance = new ConGraphObj();
				instance.a1ID = agencyID;
				instance.a1name = agencyName;
				instance.a2ID = "";
				instance.a2name = "";
				instance.connections = new TransitConnection(0);
				response.add(instance);
			}
			
			// Initialize ConGraphObj for the agency connections. 
			while (rs.next()){
				ConGraphObj instance = new ConGraphObj();
				// Getting the agency IDs
				instance.a1ID = rs.getString("a1id");
				instance.a1name = rs.getString("a1name");
				instance.a2ID = rs.getString("a2id");
				instance.a2name = rs.getString("a2name");
				instance.connections = new TransitConnection(1);
				response.add(instance);		
			}
			rs.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		return response;
	}
	
	/**
	 * Returns the centroid for agencies. If an agency is a centralized agency, 
	 * only on coordinate is reported. Otherwise, for agencies like Greyhound
	 * a simplified graph of with the stops as edges is returned.
	 * 
	 * @param agencyID
	 * @param stmt
	 * @return
	 * @throws SQLException 
	 */
	public static ConGraphAgencyGraph getAgencyCentroids(String agencyID, Statement stmt, double RADIUS) throws SQLException{
		String query = "SELECT name, lat, lon "
				+ "		FROM gtfs_stops AS stops INNER JOIN gtfs_stop_service_map AS map "
				+ " 	ON stops.id = map.stopid AND stops.agencyid = map.agencyid_def "
				+ " 	WHERE map.agencyid='" + agencyID + "' ORDER BY lat, lon";
		ResultSet rs = stmt.executeQuery(query);
		List<ConGraphCluster> clusters = new ArrayList<ConGraphCluster>();
		List<Coordinate> points = new ArrayList<Coordinate>();
		while (rs.next()){
			Coordinate c = new Coordinate(rs.getDouble("lat"), rs.getDouble("lon"));
			points.add(c);
		}
		
		// handling agencies with no trips scheduled for them
		if ( points.isEmpty())
			return null;
		
		// clustering stops that are within RADIUS distance of each other into one coordinate
		while (!points.isEmpty()){
			Set<Coordinate> clusterPoints = new HashSet<Coordinate>();
			Coordinate currenPoint = points.remove(0);
			clusterPoints.add(currenPoint);
			for ( Coordinate p : points ){
				if (ConGraphAgencyGraph.getDistance(currenPoint, p) < RADIUS){
					clusterPoints.add(p);
				}
			}
			ConGraphCluster c = new ConGraphCluster( clusterPoints );
			points.removeAll(clusterPoints);
			clusters.add(c); 
		}
		ConGraphAgencyGraph response = new ConGraphAgencyGraph(agencyID, clusters);
		return response;
	}
	
	/**
	 * gets coordinates of all stops belonging to the input agency
	 * and returns the average of stop coordinates as centroid 
	 * of the agency.
	 * 
	 * @param agencyID
	 * @param stmt
	 * @return
	 * @throws SQLException 
	 */
	public static AgencyCentroid getAgencyCentroid(String agencyID, Statement stmt) throws SQLException{
		String query = "SELECT AVG(lat) AS lat, AVG(lon) AS lng "
				+ " FROM gtfs_stops AS stops INNER JOIN gtfs_stop_service_map AS map "
				+ " ON stops.id = map.stopid AND stops.agencyid = map.agencyid_def WHERE map.agencyid='"+agencyID+"'";;
		ResultSet rs = stmt.executeQuery(query);
		AgencyCentroid response = new AgencyCentroid();
		while (rs.next()){
			response.id = agencyID;
			response.lat = rs.getDouble("lat");
			response.lng = rs.getDouble("lng");
		}		
		return response;
	}	

	/**
	 * returns the median fare for the state
	 * 
	 * @param selectedAgencies
	 * @param FareCount
	 * @param dbindex
	 * @return
	 * @throws SQLException
	 */
	public static float getFareMedianForState(List<String> selectedAgencies, int FareCount, int dbindex) throws SQLException{
		float output = 0;
		String query = "SELECT price::float FROM gtfs_fare_attributes WHERE agencyid IN (";
		for (String agencyId : selectedAgencies)
			query = query.concat("'" + agencyId + "',");
		query = query.concat("'') ORDER BY price");
		Connection connection = PgisEventManager.makeConnection(dbindex);
		Statement stmt = connection.createStatement();
		System.out.println(query);
		ResultSet rs = stmt.executeQuery(query);
		int medianIndex = (int) Math.ceil(FareCount/2); // the number that points to the median row in "gtfs_fare_attributes" table.
		int counter = 0;
		while(rs.next()){
			if (counter++ == medianIndex){
				output = (float) rs.getDouble("price");
				System.out.println(counter);
			}
		}
		return output;
	}
}
