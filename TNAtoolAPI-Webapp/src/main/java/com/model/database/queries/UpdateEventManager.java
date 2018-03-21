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
// =========================================================================================================
//	  This script contains JavaScript variables and methods used to update database schema 
//	  when creating a new database.
// =========================================================================================================

package com.model.database.queries;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.webapp.modifiers.DbUpdate;

public class UpdateEventManager {
	final static Logger logger = Logger.getLogger(UpdateEventManager.class);	

	/**
	 *Adds additional spatial methods to the current database
	 * @param connection
	 * @param dbInfo
	 */
	public static void createTables(Connection connection, String[] dbInfo){		
		addFunction(connection, dbInfo);
		create_playground_tables(connection);		
	}
	
	/**
	 * creates table that are used in Playground
	 * @param connection
	 */
	public static void create_playground_tables(Connection connection){
		Statement stmt = null;
	      try {
	        stmt = connection.createStatement();
	        stmt.executeUpdate("CREATE TABLE gtfs_pg_users("
	        		+ "  username text NOT NULL,"
	        		+ "  email text NOT NULL,"
	        		+ "  firstname text,"
	        		+ "  quota integer NOT NULL,"
	        		+ "  usedspace integer NOT NULL,"
	        		+ "  lastname text,"
	        		+ "  password text NOT NULL,"
	        		+ "  active boolean NOT NULL,"
	        		+ "  key integer,"
	        		+ "  CONSTRAINT users_pkey PRIMARY KEY (username))"
	        		+ "WITH ("
	        		+ "  OIDS=FALSE);");
	        stmt.executeUpdate("ALTER TABLE gtfs_pg_users"
	        		+ "  OWNER TO postgres;");
	        
	        stmt.executeUpdate("CREATE TABLE gtfs_uploaded_feeds("
	        		+ "  feedname text NOT NULL,"
	        		+ "  username text,"
	        		+ "  ispublic boolean NOT NULL,"
	        		+ "  updated boolean NOT NULL,"
	        		+ "  feedsize integer,"
	        		+ "  CONSTRAINT feeds_pkey PRIMARY KEY (feedname),"
	        		+ "  CONSTRAINT gtfs_uploaded_feeds_username_fkey FOREIGN KEY (username)"
	        		+ "      REFERENCES gtfs_pg_users (username) MATCH SIMPLE"
	        		+ "      ON UPDATE NO ACTION ON DELETE NO ACTION)"
	        		+ "WITH ("
	        		+ "  OIDS=FALSE);");
	        stmt.executeUpdate("ALTER TABLE gtfs_uploaded_feeds"
	        		+ "  OWNER TO postgres;");
	        
	        stmt.executeUpdate("CREATE TABLE gtfs_selected_feeds("
	        		+ "  username text NOT NULL,"
	        		+ "  feedname text NOT NULL,"
	        		+ "  agency_id text,"
	        		+ "  CONSTRAINT selected_feeds_pkey PRIMARY KEY (username, feedname),"
	        		+ "  CONSTRAINT gtfs_selected_feeds_username_fkey FOREIGN KEY (username)"
	        		+ "      REFERENCES gtfs_pg_users (username) MATCH SIMPLE"
	        		+ "      ON UPDATE NO ACTION ON DELETE NO ACTION)"
	        		+ "WITH ("
	        		+ "  OIDS=FALSE);");
	        stmt.executeUpdate("ALTER TABLE gtfs_selected_feeds"
	        		+ "  OWNER TO postgres;");
	        stmt.close();
	      } catch ( Exception e ) {
	    	  e.printStackTrace();
	      }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
	}

	/**
	 * creates census_urbans_trip_map table
	 * @param connection
	 */
	public static void create_census_urbans_trip_map(Connection connection){
		Statement stmt = null;
	      try {
	        stmt = connection.createStatement();
	        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS census_urbans_trip_map ("
	        		+ "  gid serial NOT NULL,"
	        		+ "  agencyid character varying(255),"
	        		+ "  agencyid_def character varying(255),"
	        		+ "  routeid character varying(255),"
	        		+ "  urbanid character varying(5),"
	        		+ "  tripid character varying(255),"
	        		+ "  serviceid character varying(255),"
	        		+ "  stopscount integer,"
	        		+ "  length float,"
	        		+ "  tlength int,"
	        		+ "  shape geometry(multilinestring),"
	        		+ "  uid varchar(512),"
	        		+ "  CONSTRAINT census_urbans_trip_map_pkey PRIMARY KEY (gid),"
	        		+ "  CONSTRAINT census_urbans_trip_map_fkey FOREIGN KEY (agencyid, tripid)"
	        		+ "      REFERENCES gtfs_trips (agencyid, id) MATCH SIMPLE"
	        		+ "      ON UPDATE NO ACTION ON DELETE NO ACTION)"
	        		+ "  WITH ("
	        		+ "  OIDS=FALSE"
	        		+ ");");
	        stmt.executeUpdate("ALTER TABLE census_urbans_trip_map"
	        		+ "  OWNER TO postgres;");
	        stmt.close();
	      } catch ( Exception e ) {
	    	  e.printStackTrace();
	      }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
	}
	
	/**
	 * creates census_places_trip_map table
	 * @param connection
	 */
	public static void create_census_places_trip_map(Connection connection){
		  Statement stmt = null;
		  try {
		    stmt = connection.createStatement();
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS census_places_trip_map ("
					+ "  gid serial NOT NULL,"
					+ "  agencyid character varying(255),"
					+ "  agencyid_def character varying(255),"
					+ "  routeid character varying(255),"
					+ "  placeid character varying(7),"
					+ "  tripid character varying(255),"
					+ "  serviceid character varying(255),"
					+ "  stopscount integer,"
					+ "  length float,"
					+ "  tlength int,"
					+ "  shape geometry(multilinestring),"
					+ "  uid varchar(512),"
					+ "  CONSTRAINT census_places_trip_map_pkey PRIMARY KEY (gid),"
					+ "  CONSTRAINT census_places_trip_map_fkey FOREIGN KEY (agencyid, tripid)"
					+ "      REFERENCES gtfs_trips (agencyid, id) MATCH SIMPLE"
					+ "      ON UPDATE NO ACTION ON DELETE NO ACTION)"
					+ "  WITH ("
					+ "  OIDS=FALSE);");
		  stmt.executeUpdate("ALTER TABLE census_places_trip_map"
				+ "  OWNER TO postgres;");
		    stmt.close();
		  } catch ( Exception e ) {
			  e.printStackTrace();
		  }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
	}
	
	/**
	 * creates census_tracts_trip map
	 * @param connection
	 */
	public static void create_census_tracts_trip_map(Connection connection){
		Statement stmt = null;
	      try {
	        stmt = connection.createStatement();
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS census_tracts_trip_map ("
	        		+ "  gid serial NOT NULL,"
	        		+ "  agencyid character varying(255),"
	        		+ "  agencyid_def character varying(255),"
	        		+ "  routeid character varying(255),"
	        		+ "  tractid character varying(11),"
	        		+ "  tripid character varying(255),"
	        		+ "  serviceid character varying(255),"
	        		+ "  stopscount integer,"
	        		+ "  length float,"
	        		+ "  tlength int,"
	        		+ "  shape geometry(multilinestring),"
	        		+ "  uid varchar(512),"
	        		+ "  CONSTRAINT census_tracts_trip_map_pkey PRIMARY KEY (gid),"
	        		+ "  CONSTRAINT census_tracts_trip_map_fkey FOREIGN KEY (agencyid, tripid)"
	        		+ "      REFERENCES gtfs_trips (agencyid, id) MATCH SIMPLE"
	        		+ "      ON UPDATE NO ACTION ON DELETE NO ACTION)"
	        		+ "  WITH ("
	        		+ "  OIDS=FALSE);");
	        stmt.executeUpdate("ALTER TABLE census_tracts_trip_map"
	        		+ "  OWNER TO postgres;");
	        stmt.close();
	      } catch ( Exception e ) {
	    	  e.printStackTrace();
	      }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
	}
	
	/**
	 * creates census_counties_trip_map table
	 * 
	 * @param connection
	 */
	public static void create_census_counties_trip_map(Connection connection){
		Statement stmt = null;
	      try {
	        stmt = connection.createStatement();
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS census_counties_trip_map ("
	        		+ "  gid serial NOT NULL,"
	        		+ "  agencyid character varying(255),"
	        		+ "  agencyid_def character varying(255),"
	        		+ "  routeid character varying(255),"
	        		+ "  countyid character varying(5),"
	        		+ "  regionid character varying(2),"
	        		+ "  tripid character varying(255),"
	        		+ "  serviceid character varying(255),"
	        		+ "  stopscount integer,  "
	        		+ "  length float,"
	        		+ "  tlength int,"
	        		+ "  shape geometry(multilinestring),"
	        		+ "  uid varchar(512),"
	        		+ "  CONSTRAINT census_counties_trip_map_pkey PRIMARY KEY (gid),"
	        		+ "  CONSTRAINT census_counties_trip_map_fkey FOREIGN KEY (agencyid, tripid)"
	        		+ "      REFERENCES gtfs_trips (agencyid, id) MATCH SIMPLE"
	        		+ "      ON UPDATE NO ACTION ON DELETE NO ACTION)"
	        		+ "  WITH ("
	        		+ "  OIDS=FALSE);");
	        stmt.executeUpdate("ALTER TABLE census_counties_trip_map"
	        		+ "  OWNER TO postgres;");
	        stmt.close();
	      } catch ( Exception e ) {
	    	  e.printStackTrace();
	      }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
	}
	
	/**
	 * creates census_states_trip_map table
	 * 
	 * @param connection
	 */
	public static void create_census_states_trip_map(Connection connection){
		Statement stmt = null;
	      try {
	        stmt = connection.createStatement();
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS census_states_trip_map ("
	        		+ "  gid serial NOT NULL,"
	        		+ "  agencyid character varying(255),"
	        		+ "  agencyid_def character varying(255),"
	        		+ "  routeid character varying(255),"
	        		+ "  stateid character varying(5),"
	        		+ "  tripid character varying(255),"
	        		+ "  serviceid character varying(255),"
	        		+ "  stopscount integer,  "
	        		+ "  length float,"
	        		+ "  tlength int,"
	        		+ "  shape geometry(multilinestring),"
	        		+ "  uid varchar(512),"
	        		+ "  CONSTRAINT census_states_trip_map_pkey PRIMARY KEY (gid),"
	        		+ "  CONSTRAINT census_states_trip_map_fkey FOREIGN KEY (agencyid, tripid)"
	        		+ "      REFERENCES gtfs_trips (agencyid, id) MATCH SIMPLE"
	        		+ "      ON UPDATE NO ACTION ON DELETE NO ACTION)"
	        		+ "  WITH ("
	        		+ "  OIDS=FALSE);");
	        stmt.executeUpdate("ALTER TABLE census_states_trip_map"
	        		+ "  OWNER TO postgres;");
	        stmt.close();
	      } catch ( Exception e ) {
	    	  e.printStackTrace();
	      }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
	}
	
	/**
	 * creates census_congdists_trip_map
	 * 
	 * @param connection
	 */
	public static void create_census_congdists_trip_map(Connection connection){
		Statement stmt = null;
	      try {
	        stmt = connection.createStatement();
	        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS census_congdists_trip_map ("
	        		+ "  gid serial NOT NULL,"
	        		+ "  agencyid character varying(255),"
	        		+ "  agencyid_def character varying(255),"
	        		+ "  routeid character varying(255),"
	        		+ "  congdistid character varying(4),"
	        		+ "  tripid character varying(255),"
	        		+ "  serviceid character varying(255),"
	        		+ "  stopscount integer,  "
	        		+ "  length float,"
	        		+ "  tlength int,"
	        		+ "  shape geometry(multilinestring),"
	        		+ "  uid varchar(512),"
	        		+ "  CONSTRAINT census_congdists_trip_map_pkey PRIMARY KEY (gid),"
	        		+ "  CONSTRAINT census_congdists_trip_map_fkey FOREIGN KEY (agencyid, tripid)"
	        		+ "      REFERENCES gtfs_trips (agencyid, id) MATCH SIMPLE"
	        		+ "      ON UPDATE NO ACTION ON DELETE NO ACTION)"
	        		+ "  WITH ("
	        		+ "  OIDS=FALSE);");
	        stmt.executeUpdate("ALTER TABLE census_congdists_trip_map"
	        		+ "  OWNER TO postgres;");
	        stmt.close();
	      } catch ( Exception e ) {
	    	  e.printStackTrace();
	      }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
	}
	
	/**
	 * creates gtfs_stop_route_map table
	 * 
	 * @param connection
	 */
	public static void create_gtfs_stop_route_map(Connection connection){
		Statement stmt = null;
	      try {
	        stmt = connection.createStatement();
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS gtfs_stop_route_map ("
	        		+ "  gid serial NOT NULL,"
	        		+ "  agencyid character varying(255),"
	        		+ "  routeid character varying(255),"
	        		+ "  agencyid_def character varying(50),"
	        		+ "  stopid character varying(255),"
	        		+ "  CONSTRAINT gtfs_stop_route_map_pkey PRIMARY KEY (gid),"
	        		+ "  CONSTRAINT gtfs_stop_route_map_fkey FOREIGN KEY (agencyid_def, stopid)"
	        		+ "      REFERENCES gtfs_stops (agencyid, id) MATCH SIMPLE"
	        		+ "      ON UPDATE NO ACTION ON DELETE NO ACTION)"
	        		+ "  WITH ("
	        		+ "  OIDS=FALSE);");
	        stmt.executeUpdate("ALTER TABLE gtfs_stop_route_map"
	        		+ "  OWNER TO postgres;");
	        stmt.close();
	    } catch ( Exception e ) {
	    	e.printStackTrace();
	    }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
	}
	
	/**
	 * creates gtfs_stop_service_map table
	 * 
	 * @param connection
	 */
	public static void create_gtfs_stop_service_map(Connection connection){
		Statement stmt = null;
	      try {
	        stmt = connection.createStatement();
	        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS gtfs_stop_service_map ("
	        		+ "  gid serial NOT NULL,"
	        		+ "  agencyid character varying(255),"
	        		+ "  agencyid_def character varying(50),"
	        		+ "  stopid character varying(255),"
	        		+ "  CONSTRAINT gtfs_stop_service_map_pkey PRIMARY KEY (gid),"
	        		+ "  CONSTRAINT fkf1c57f3ac6b68b22 FOREIGN KEY (agencyid_def, stopid)"
	        		+ "      REFERENCES gtfs_stops (agencyid, id) MATCH SIMPLE"
	        		+ "      ON UPDATE NO ACTION ON DELETE NO ACTION)"
	        		+ "  WITH ("
	        		+ "  OIDS=FALSE)");
	        stmt.executeUpdate("ALTER TABLE gtfs_stop_service_map"
	        		+ "  OWNER TO postgres;");
	        stmt.close();
	      } catch ( Exception e ) {
	    	  e.printStackTrace();
	      }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
	}
	
	/**
	 * creates gtfs_trip_stops table
	 * @param connection
	 */
	public static void create_gtfs_trip_stops(Connection connection){
		Statement stmt = null;
		try {
	        stmt = connection.createStatement();
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS gtfs_trip_stops ("
	        		+ "  trip_agencyid character varying(50),"
	        		+ "  trip_id character varying(255),"
	        		+ "  stop_agencyid_origin character varying(50),"
	        		+ "  stop_id_origin character varying(255),"
	        		+ "  stop_name_origin character varying(255),"
	        		+ "  stop_agencyid_destination character varying(50),"
	        		+ "  stop_id_destination character varying(255),"
	        		+ "  stop_name_destination character varying(255),"
	        		+ "  CONSTRAINT gtfs_trip_stops_pkey PRIMARY KEY (trip_agencyid, trip_id),"
	        		+ "  CONSTRAINT gtfs_trip_stops_fkey1 FOREIGN KEY (stop_agencyid_origin, stop_id_origin)"
	        		+ "      REFERENCES gtfs_stops (agencyid, id) MATCH SIMPLE"
	        		+ "      ON UPDATE NO ACTION ON DELETE NO ACTION,"
	        		+ "  CONSTRAINT gtfs_trip_stops_fkey2 FOREIGN KEY (stop_agencyid_destination, stop_id_destination)"
	        		+ "      REFERENCES gtfs_stops (agencyid, id) MATCH SIMPLE"
	        		+ "      ON UPDATE NO ACTION ON DELETE NO ACTION)"
	        		+ "  WITH ("
	        		+ "  OIDS=FALSE);");
	        stmt.executeUpdate("ALTER TABLE gtfs_trip_stops"
	        		+ "  OWNER TO postgres;");
	        stmt.close();
	      } catch ( Exception e ) {
	    	  e.printStackTrace();
	      }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
	}
	
	/**
	 * creates gtfs_stop_route_map table
	 * 
	 * @param connection
	 */
	public static void create_gtfs_trip_segments(Connection connection){
		Statement stmt = null;
	      try {
	        stmt = connection.createStatement();
			stmt.executeUpdate(""
				+ "CREATE TABLE IF NOT EXISTS gtfs_trip_segments AS"
				+ "  SELECT ROW_NUMBER() OVER() AS uid,"
				+ "         a.id AS id,"
				+ "         agencyid character varying(255),"
				+ "         n - 1 AS seg_id,"
				+ "         ST_SetSRID(ST_MakeLine(ST_PointN(a.shape, n - 1), ST_PointN(a.shape, n)), 4326) AS shape"
				+ "  FROM gtfs_trips AS a"
				+ "  CROSS JOIN generate_series(2, ST_NPoints(a.shape)) AS n;"				
			);
			stmt.executeUpdate("CREATE INDEX gtfs_trip_segments_shape_idx ON gtfs_trip_segments USING gist (shape);");
			stmt.executeUpdate("VACUUM ANALYZE gtfs_trip_segments;");
	        stmt.executeUpdate("ALTER TABLE gtfs_stop_route_map OWNER TO postgres;");
	        stmt.close();
	    } catch ( Exception e ) {
	    	e.printStackTrace();
	    }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
	}

	/**
	 * Updates all the additional tables. This is called every time a new feed is added to the database
	 * 
	 * @param connection
	 * @param agencyId
	 */
	public static void updateTables(Connection connection, String agencyId){	
		  logger.info("Updating gtfs_trips");
		  updateTrip(connection, agencyId);
		  logger.info("Update gtfs_trip_segments");
		  updateGtfsTripSegments(connection, agencyId);
		  logger.info("Updating gtfs_stops");
		  updateStopsAddGeolocation(connection, agencyId);
		  updateGtfsStopsGeoCoder(connection, agencyId);
		  logger.info("Updating gtfs_stop_route_map");
		  updateGtfsStopRouteMap(connection, agencyId);
		  logger.info("Updating gtfs_stop_service_map");
		  updateGtfsStopServiceMap(connection, agencyId);
		  logger.info("Updating gtfs_trip_stops");
		  updateGtfsTripStops(connection, agencyId);
		  logger.info("Updating census_counties_trip_map");
		  updateCountyTripMap(connection, agencyId);
		  logger.info("Updating census_states_trip_map");
		  updateStateTripMap(connection, agencyId);
		  logger.info("Updating census_tracts_trip_map");
		  updateTractTripMap(connection, agencyId);
		  logger.info("Updating census_urbans_trip_map");
		  updateUrbanTripMap(connection, agencyId);
		  logger.info("Updating census_places_trip_map");
		  updatePlaceTripMap(connection, agencyId);
		  logger.info("Updating census_congdists_trip_map");
		  updateCongdistTripMap(connection, agencyId);
		  logger.info("Updating gtfs_agencies");
		  updateGtfsAgencies(connection, agencyId);
		  logger.info("Updating agencymapping");
		  updateAgencyMapping(connection, agencyId);
//		  dropConnection(connection);
	}
	
	/**
	 * creates and populates agencymapping table
	 * 
	 * @param connection
	 * @param agencyId
	 */
	private static void updateAgencyMapping(Connection connection, String agencyId) {
		Statement stmt = null;  
		try{
			stmt = connection.createStatement();
			stmt.executeUpdate("DROP TABLE IF EXISTS agencymapping;");
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS agencymapping( "
					+ "agencyID text, "
					+ "contained_agencies text[], "
					+ "PRIMARY KEY( agencyID ));");
		}catch ( Exception e ) {
			  logger.debug( e.getClass().getName()+": "+ e.getMessage() );
		}finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
		
		try{
			stmt = connection.createStatement();
			stmt.executeUpdate("WITH temp1 as (SELECT stops.lat,stops.lon,map.stopid, map.agencyid , stops.location,stops.name "
					+ "FROM gtfs_stop_service_map AS map JOIN gtfs_stops AS stops ON map.agencyid_def=stops.agencyid AND map.stopid=stops.id), "
					+ "tempt AS (SELECT ST_convexhull(St_collect(location)) as b1, agencyid as a FROM temp1 group by a), "
					+ "tempx as (SELECT a.a as id1 ,b.a as id2,a.b1 as shape1,b.b1 as shape2,ST_within(b.b1,a.b1) as id1_in_id2 from tempt as a CROSS join tempt as b), "
					+ "tempy as (Select id1,array_agg(id2) as ca FROM tempx WHERE id1_in_id2='t'group by id1) "
					+ "INSERT INTO agencymapping(agencyID, contained_agencies) "
					+ "SELECT id1,ca FROM tempy;");
		}catch ( Exception e ) {
			  logger.debug( e.getClass().getName()+": "+ e.getMessage() );
		}finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
		
		try{
			stmt = connection.createStatement();
			stmt.executeUpdate("ALTER TABLE agencymapping ADD COLUMN centralized boolean;");
		}catch ( Exception e ) {
			  logger.debug( e.getClass().getName()+": "+ e.getMessage() );
		}finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
		
		try{
			stmt = connection.createStatement();
			stmt.executeUpdate("with temp1 as (select id,centralized from gtfs_agencies) "
					+ "UPDATE agencymapping "
					+ "SET centralized = (select centralized from temp1 "
					+ "WHERE agencyid=id)");
		}catch ( Exception e ) {
			  logger.debug( e.getClass().getName()+": "+ e.getMessage() );
		}finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }

		
	}

	/**
	 * adds centralized column to the gtfs_agencies table. This column is added
	 * to distinguish between agencies that have a relatively limited area of 
	 * service (like a city) from agencies like Greyhound or BoltBus that operate
	 * in a large areas
	 * 
	 * @param connection
	 * @param agencyId
	 */
	private static void updateGtfsAgencies(Connection connection, String agencyId) {
		Statement stmt = null;  
		try{
			stmt = connection.createStatement();
			stmt.executeUpdate("ALTER TABLE gtfs_agencies ADD centralized BOOLEAN;");
		}catch ( Exception e ) {
			  e.printStackTrace();
		}finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
		try{
			stmt = connection.createStatement();
			stmt.executeUpdate("WITH criteriaTable AS (WITH stops AS (SELECT stops.location, stops.id, map.agencyid "
					+ "FROM gtfs_stops AS stops INNER JOIN gtfs_stop_service_map AS map "
					+ "ON stops.id = map.stopid AND stops.agencyid = map.agencyid_def), "
					+ "stops1 AS (SELECT * FROM stops), "
					+ "stops2 AS (SELECT * FROM stops) "
					+ "SELECT stops1.agencyid, AVG(ST_Distance(stops1.location, stops2.location)) <= 45000 AS criteria "
					+ "FROM stops1 INNER JOIN stops2 USING(agencyid) "
					+ "GROUP BY stops1.agencyid, stops2.agencyid) "
					+ "UPDATE gtfs_agencies	"
					+ "SET centralized = (SELECT criteria FROM criteriaTable WHERE agencyid=id);");
		}catch ( Exception e ) {
			  e.printStackTrace();
		}finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }

		
	}

	/**
	 * Updates gtfs_trip_stops table
	 * @param connection
	 * @param agencyId
	 */
	public static void updateGtfsTripStops(Connection connection, String agencyId){
		Statement stmt = null;  
		try{
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM gtfs_trip_stops LIMIT 1");
			if(!rs.next()){
				stmt.executeUpdate("DROP TABLE gtfs_trip_stops;");
			}
		  }catch ( Exception e ) {
//			  logger.debug( e.getClass().getName()+": "+ e.getMessage() );
		  }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
		  create_gtfs_trip_stops(connection);
	      try {
	        stmt = connection.createStatement();
	        stmt.executeUpdate("ALTER TABLE gtfs_trip_stops DISABLE TRIGGER ALL;");
	        
	        stmt.executeUpdate("with seq as (select trip_agencyid as taid, trip_id as tripid, min(stopsequence) as mins, max(stopsequence) as maxs from gtfs_stop_times stime where stop_agencyid='"+agencyId+"' group by trip_agencyid, trip_id),"
	        		+ "tpoints as (select stime1.stop_agencyid as osaid, stime1.stop_id as ostopid, stime2.stop_agencyid as dsaid, stime2.stop_id as dstopid, seq.taid as taid, seq.tripid as tripid from gtfs_stop_times stime1 inner join seq "
	        		+ "on seq.taid = stime1.trip_agencyid and seq.tripid=stime1.trip_id and seq.mins= stime1.stopsequence inner join gtfs_stop_times stime2 "
	        		+ "on seq.taid = stime2.trip_agencyid and seq.tripid=stime2.trip_id and seq.mins= stime2.stopsequence), "
	        		+ "tripstops as (select tpoints.taid as trip_agencyid, tpoints.tripid as trip_id, stop1.name as stop_name_origin, tpoints.osaid as stop_agencyid_origin, tpoints.ostopid as stop_id_origin, "
	        		+ "stop2.name as stop_name_destination, tpoints.dsaid as stop_agencyid_destination, tpoints.dstopid as stop_id_destination from gtfs_stops stop1 inner join tpoints "
	        		+ "on stop1.agencyid = tpoints.osaid and stop1.id = tpoints.ostopid inner join gtfs_stops stop2 on stop2.agencyid = tpoints.dsaid and stop2.id = tpoints.dstopid) "
	        		+ "insert into gtfs_trip_stops (trip_agencyid, trip_id, stop_name_origin, stop_agencyid_origin, stop_id_origin, stop_name_destination, stop_agencyid_destination, stop_id_destination) select * from tripstops;");
	        
	        stmt.executeUpdate("ALTER TABLE gtfs_trip_stops ENABLE TRIGGER ALL;");
	        stmt.close();
	      } catch ( Exception e ) {
	    	  e.printStackTrace();
	      }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
	}

	public static void updateGtfsTripSegments(Connection connection, String agencyId) {
		Statement stmt = null;  
		try{
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM gtfs_trip_segments LIMIT 1");
			if(!rs.next()){
				stmt.executeUpdate("DROP TABLE gtfs_trip_segments;");
			}
		  }catch ( Exception e ) {
		  }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
		  create_gtfs_trip_segments(connection);
	      try {
	        stmt = connection.createStatement();
			stmt.executeUpdate("ALTER TABLE gtfs_trip_segments DISABLE TRIGGER ALL;");			
			stmt.executeUpdate("DELETE FROM gtfs_trip_segments WHERE agencyid = '"+agencyId+"';");
			stmt.executeUpdate(""
				+ "INSERT INTO gtfs_trip_segments (uid, agencyid, id, seg_id, shape)"
				+ "SELECT ROW_NUMBER() OVER() AS uid,"
				+ "		a.agencyid as agencyid,"
				+ "		a.id AS id,"
				+ "		n - 1 AS seg_id,"
				+ "		ST_SetSRID(ST_MakeLine(ST_PointN(a.shape, n - 1), ST_PointN(a.shape, n)), 4326) AS shape"
				+ "  FROM gtfs_trips AS a"
				+ "  CROSS JOIN generate_series(2, ST_NPoints(a.shape)) AS n"
				+ "  WHERE a.agencyid = '"+agencyId+"';"
			);
	        stmt.executeUpdate("ALTER TABLE gtfs_trip_segments ENABLE TRIGGER ALL;");
	        stmt.close();
	      } catch ( Exception e ) {
	    	  e.printStackTrace();
	      }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }		
	}

	/**
	 * Updates gtfs_stop_service_map table
	 * 
	 * @param connection
	 * @param agencyId
	 */
	public static void updateGtfsStopServiceMap(Connection connection, String agencyId){
		Statement stmt = null;  
		try{
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM gtfs_stop_service_map LIMIT 1");
			if(!rs.next()){
				stmt.executeUpdate("DROP TABLE gtfs_stop_service_map;");
			}
		  }catch ( Exception e ) {
			  logger.debug( e.getClass().getName()+": "+ e.getMessage() );
		  }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
		  create_gtfs_stop_service_map(connection);
	      try {
	        stmt = connection.createStatement();
	        stmt.executeUpdate("ALTER TABLE gtfs_stop_service_map DISABLE TRIGGER ALL;");
	        
	        stmt.executeUpdate("insert into gtfs_stop_service_map(stopid, agencyid, agencyid_def) "
	        		+ "select distinct stimes.stop_id , agencies.id, stimes.stop_agencyId  "
	        		+ "from gtfs_agencies agencies "
	        		+ "inner join gtfs_routes routes on agencies.id = routes.agency  "
	        		+ "inner join gtfs_trips trips on routes.id = trips.route_id and routes.agencyId = trips.agencyId "
	        		+ "inner join gtfs_stop_times stimes on trips.id = stimes.trip_id and trips.agencyId = stimes.trip_agencyId "
	        		+ "where agencies.defaultid = '"+agencyId+"';");
	        
	        stmt.executeUpdate("ALTER TABLE gtfs_stop_service_map ENABLE TRIGGER ALL;");
	        stmt.close();
	      } catch ( Exception e ) {
	    	  e.printStackTrace();
	      }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
	}
	
	/**
	 * Updates gtfs_stops table with Geolocations
	 * 
	 * @param connection
	 * @param agencyId
	 */
	public static void updateStopsAddGeolocation(Connection connection, String agencyId){
		Statement stmt = null;
	      try {
	        stmt = connection.createStatement();
	        stmt.executeUpdate("alter table IF EXISTS gtfs_stops add column location geometry(Point,2993);");
	        stmt.executeUpdate("CREATE INDEX ids_location ON gtfs_stops"
	        		+ "  USING GIST (location);");
	        stmt.executeUpdate("CLUSTER ids_location ON gtfs_stops;");
	        stmt.close();
	      } catch ( Exception e ) {
	    	  //logger.debug( e.getClass().getName()+": "+ e.getMessage() );
	      }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }	      
	      try{
	    	  stmt = connection.createStatement();
	    	  stmt.executeUpdate("ALTER TABLE gtfs_stops DISABLE TRIGGER ALL;");
	    	  
	    	  stmt.executeUpdate("update gtfs_stops set location = ST_transform(ST_setsrid(ST_MakePoint(lon, lat),4326), 2993) where agencyid='"+agencyId+"';");  
	    	  
	    	  stmt.executeUpdate("ALTER TABLE gtfs_stops ENABLE TRIGGER ALL;");
	    	  stmt.close();
	      }catch ( Exception e ) {
	    	  e.printStackTrace();
	      }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }	      
	}
	
	/**
	 * Adds SQL functions
	 * 
	 * @param connection
	 * @param dbInfo
	 */
	public static void addFunction(Connection connection, String[] dbInfo){
		logger.info("addFunction");
		DbUpdate.runSqlFromResource("admin/resources/Functions.sql", dbInfo[4], dbInfo[5], dbInfo[6]);
	}
	
	/**
	 * Updates gtfs_trips table
	 * 
	 * @param connection
	 * @param agencyId
	 */
	public static void updateTrip(Connection connection, String agencyId){
		Statement stmt = null;
	     
		try{
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT AddGeometryColumn( 'public', 'gtfs_trips', 'shape', 4326, 'linestring', 2 );");
		}catch ( Exception e ) {
			  //logger.debug( e.getClass().getName()+": "+ e.getMessage() );
		}finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
		try{
			stmt = connection.createStatement();
			stmt.executeUpdate("ALTER TABLE gtfs_trips ALTER COLUMN uid TYPE varchar(1000);");
		}catch ( Exception e ) {
			  //logger.debug( e.getClass().getName()+": "+ e.getMessage() );
		}finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
		try{
			stmt = connection.createStatement();
			stmt.executeUpdate("DROP INDEX IF EXISTS trips_shapeids");
			stmt.executeUpdate("create INDEX trips_shapeids on gtfs_trips (shapeid_agencyid,shapeid_id);");
			stmt.executeUpdate("DROP INDEX IF EXISTS tripids");
			stmt.executeUpdate("create unique index tripids on gtfs_trips (agencyid,id);");			
		  }catch ( Exception e ) {
			  //logger.debug( e.getClass().getName()+": "+ e.getMessage() );
		  }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
		
	      try{
	    	  stmt = connection.createStatement();
	    	  stmt.executeUpdate("ALTER TABLE gtfs_trips DISABLE TRIGGER ALL;"); 
	    	  
	    	  stmt.executeUpdate("update gtfs_trips trip set shape = tss.shape, epshape=GoogleEncodeLine(tss.shape), length = (tss.length)/1609.34, estlength=0 FROM "
	    	  		+ "(select ST_SimplifyPreserveTopology(ST_MakeLine(ST_setsrid(ST_MakePoint(shppoint.lon, shppoint.lat),4326)),0.00001) as shape, ST_Length(st_transform(ST_MakeLine(ST_setsrid(ST_MakePoint(shppoint.lon, shppoint.lat),4326)),2993)) as length,"
	    	  		+ "shppoint.shapeid_agencyid as agencyid, shppoint.shapeid_id as shapeid from (select * from gtfs_shape_points where shapeid_agencyid='"+agencyId+"' order by shapeid_agencyid, shapeid_id, sequence) as shppoint "
	    	  		+ "group by agencyid, shapeid) "
	    	  		+ "as tss where tss.agencyid = trip.shapeid_agencyid and tss.shapeid = trip.shapeid_id;");
	    	  stmt.executeUpdate("update gtfs_trips trip set shape = tes.shape, epshape=GoogleEncodeLine(tes.shape), estlength = (tes.estlength)/1609.34, length=0 FROM (select est.shape, est.estlength, est.agencyid, est.tripid from "
	    	  		+ "(select ST_MakeLine(ST_setsrid(ST_MakePoint(point.lon, point.lat),4326)) as shape, ST_Length(st_transform(ST_MakeLine(ST_setsrid(ST_MakePoint(point.lon, point.lat),4326)),2993)) as estlength, "
	    	  		+ "point.agencyid as agencyid, point.tripid as tripid from (select stop.lat as lat, stop.lon as lon, stime.gid as gid, stime.trip_id as tripid, stime.trip_agencyid as agencyid from gtfs_stops stop "
	    	  		+ "inner join gtfs_stop_times stime on stop.agencyid = stime.stop_agencyid and stop.id = stime.stop_id where stop.agencyid='"+agencyId+"' order by stime.trip_agencyid, stime.trip_id, stime.stopsequence) as point "
	    	  		+ "group by point.agencyid, point.tripid) as est inner join gtfs_trips trip on est.agencyid = trip.agencyid and est.tripid = trip.id where trip.shapeid_id isnull) as tes "
	    	  		+ "where tes.agencyid = trip.agencyid and tes.tripid = trip.id;");
	    	  stmt.executeUpdate("update gtfs_trips set stopscount = stpt.cnt from (select count(gid) as cnt, trip_id, trip_agencyid from gtfs_stop_times where stop_agencyid='"+agencyId+"' group by trip_id, trip_agencyid) as stpt "
	    	  		+ "where stpt.trip_agencyid = agencyid and stpt.trip_id=id ;");
	    	  stmt.executeUpdate("with tempstopcodes as (select stop.id as stopid, stop.agencyid as agencyid , makeuid(rank() over (partition by stop.agencyid order by stop.id)) as uid from gtfs_stops stop where stop.agencyid='"+agencyId+"') "
	    	  		+ "update gtfs_trips set uid = stpt.uid from (select string_agg(tmp.uid,'!' order by tmp.uid) as uid, stime.trip_id, stime.trip_agencyid from "
	    	  		+ "gtfs_stop_times stime inner join tempstopcodes tmp on tmp.agencyid=stime.stop_agencyid and tmp.stopid=stime.stop_id "
	    	  		+ "group by stime.trip_agencyid, stime.trip_id)as stpt where stpt.trip_agencyid = agencyid and stpt.trip_id=id;");
	    	  stmt.executeUpdate("with tempetriptimes as (select trip_id as tripid, trip_agencyid as agencyid, coalesce(max(departuretime),0) as tripfinish, coalesce(min(arrivaltime),0) as tripstart from gtfs_stop_times where arrivaltime>0 and departuretime>0 "
	    	  		+ "and stop_agencyid='"+agencyId+"' group by trip_id, trip_agencyid) "
	    	  		+ "update gtfs_trips trips set tlength=tripfinish-tripstart from tempetriptimes result where result.tripid = trips.id and result.agencyid = trips.agencyid;");
	    	  stmt.executeUpdate("update gtfs_trips set tlength=0 where tlength isnull or tlength<0;");

	    	  stmt.executeUpdate("ALTER TABLE gtfs_trips ENABLE TRIGGER ALL;");
	    	  stmt.close();
	      }catch ( Exception e ) {
	    	  e.printStackTrace();
	      }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }     
	}
	
	/**
	 * Updates gtfs_stop_route_map table
	 * 
	 * @param connection
	 * @param agencyId
	 */
	public static void updateGtfsStopRouteMap(Connection connection, String agencyId){
		Statement stmt = null;  
		try{
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM gtfs_stop_route_map LIMIT 1");
			if(!rs.next()){
				stmt.executeUpdate("DROP TABLE gtfs_stop_route_map;");
			}
		  }catch ( Exception e ) {
			  logger.debug( e.getClass().getName()+": "+ e.getMessage() );
		  }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
		create_gtfs_stop_route_map(connection);
		  
		  try {
		    stmt = connection.createStatement();
		    stmt.executeUpdate("ALTER TABLE gtfs_stop_route_map DISABLE TRIGGER ALL;");
		    
		    stmt.executeUpdate("insert into gtfs_stop_route_map(stopid, agencyid, agencyid_def, routeid) "
		    		+ "select distinct stimes.stop_id, routes.agency, stimes.stop_agencyId, routes.id "
		    		+ "from gtfs_routes routes "
		    		+ "inner join gtfs_trips trips on routes.id = trips.route_id and routes.agencyId = trips.agencyId "
		    		+ "inner join gtfs_stop_times stimes on trips.id = stimes.trip_id and trips.agencyId = stimes.trip_agencyId "
		    		+ "where routes.defaultid='"+agencyId+"';");
		    
		    stmt.executeUpdate("ALTER TABLE gtfs_stop_route_map ENABLE TRIGGER ALL;");
		    stmt.close();
		  } catch ( Exception e ) {
			  e.printStackTrace();
		  }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
	}
	
	/**
	 * Updates gtfs_stops table. Adds geographical areas GeoCoder
	 * 
	 * @param connection
	 * @param agencyId
	 */
	public static void updateGtfsStopsGeoCoder(Connection connection, String agencyId){
		Statement stmt = null;
	      try {
	        stmt = connection.createStatement();
	        stmt.executeUpdate("ALTER TABLE gtfs_stops DISABLE TRIGGER ALL;");
	        stmt.executeUpdate("ALTER TABLE gtfs_stops ADD COLUMN IF NOT EXISTS stateid character varying(2);");
	        stmt.executeUpdate("update gtfs_stops stop set stateid=shape.stateid from census_states shape where stop.agencyid='"+agencyId+"' and st_within(ST_SetSRID(ST_MakePoint(stop.lon, stop.lat),4326),shape.shape)=true ;");
	        stmt.executeUpdate("update gtfs_stops stop set blockid=shape.blockid from census_blocks shape where stop.agencyid='"+agencyId+"' and st_within(ST_SetSRID(ST_MakePoint(stop.lon, stop.lat),4326),shape.shape)=true ;");
	        stmt.executeUpdate("update gtfs_stops stop set placeid=shape.placeid from census_places shape where stop.agencyid='"+agencyId+"' and st_within(ST_SetSRID(ST_MakePoint(stop.lon, stop.lat),4326),shape.shape)=true ;");
	        stmt.executeUpdate("update gtfs_stops stop set congdistid=shape.congdistid from census_congdists shape where stop.agencyid='"+agencyId+"' and st_within(ST_SetSRID(ST_MakePoint(stop.lon, stop.lat),4326),shape.shape)=true;");
	        stmt.executeUpdate("update gtfs_stops stop set regionid = county.odotregionid from census_counties county where stop.agencyid='"+agencyId+"' and left(stop.blockid,5)= county.countyid::varchar(5);");
	        stmt.executeUpdate("update gtfs_stops stop set urbanid=shape.urbanid from census_urbans shape where stop.agencyid='"+agencyId+"' and st_within(ST_SetSRID(ST_MakePoint(stop.lon, stop.lat),4326),shape.shape)=true;");
	        
	        stmt.executeUpdate("ALTER TABLE gtfs_stops ENABLE TRIGGER ALL;");
	        stmt.close();
	      } catch ( Exception e ) {
	    	  e.printStackTrace();
	      }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }	      
	}
	
	/**
	 * Updates census_congdists_trip_map table
	 * 
	 * @param connection
	 * @param agencyId
	 */
	public static void updateCongdistTripMap(Connection connection, String agencyId){
		Statement stmt = null;  
		try{
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM census_congdists_trip_map LIMIT 1");
			if(!rs.next()){
				stmt.executeUpdate("DROP TABLE census_congdists_trip_map;");
			}
		  }catch ( Exception e ) {
//			  logger.debug( e.getClass().getName()+": "+ e.getMessage() );
		  }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
		create_census_congdists_trip_map(connection);
	      try {
	        stmt = connection.createStatement();
	        stmt.executeUpdate("ALTER TABLE census_congdists_trip_map DISABLE TRIGGER ALL;");
	        
	        stmt.executeUpdate("WITH covered_trips AS (SELECT congdists.congdistid,  trips.id,trips.agencyid "
	        		+ "FROM gtfs_trips AS trips INNER JOIN census_congdists AS congdists "
	        		+ "ON ST_Contains(congdists.shape, trips.shape) "
	        		+ "WHERE serviceid_agencyid='" + agencyId + "'), "
	        		+ "output1 AS (SELECT trip.id, trip.agencyid, trip.serviceid_agencyid, trip.serviceid_id, trip.route_id, covered_trips.congdistid,"
	        		+ "	ST_Multi(trip.shape) AS shape, ST_Length(st_transform(trip.shape,2993))/1609.34 AS length , trip.uid"
	        		+ "	FROM gtfs_trips AS trip INNER JOIN covered_trips USING(id,agencyid)), "
	        		+ "intersected_trips AS (SELECT trips.id,trips.agencyid FROM gtfs_trips AS trips LEFT JOIN covered_trips USING (id,agencyid) WHERE serviceid_agencyid='" + agencyId + "' AND covered_trips.id IS NULL),"
	        		+ "intersections as (select trip.id, trip.agencyid, trip.serviceid_agencyid, trip.serviceid_id, trip.route_id, county.congdistid,"
	        		+ "	ST_Intersection(trip.shape,county.shape) as intersection, trip.uid "
	        		+ "	from gtfs_trips trip "
	        		+ "	inner join intersected_trips USING (id, agencyid)"
	        		+ "	inner join census_congdists county on  st_intersects(county.shape,trip.shape)=true), "
	        		+ "output2 AS (select id, agencyid, serviceid_agencyid, serviceid_id, route_id, congdistid,"
	        		+ "	st_multi(ST_CollectionExtract(st_union(intersection),2)), (ST_Length(st_transform(intersection,2993))/1609.34), uid "
	        		+ "	from intersections"
	        		+ "	group by id, agencyid, serviceid_agencyid, serviceid_id, route_id, congdistid, intersection, uid) "
	        		+ "insert into census_congdists_trip_map(tripid, agencyid, agencyid_def, serviceid, routeid,  congdistid, shape, length, uid) "
	        		+ "SELECT * FROM output1 UNION SELECT * FROM output2");
	        stmt.executeUpdate("update census_congdists_trip_map set stopscount = res.cnt+0 from "
	        		+ "(select count(stop.id) as cnt, stop.congdistid as cid, stime.trip_agencyid as aid, stime.trip_id as tid "
	        		+ "from gtfs_stops stop inner join gtfs_stop_times stime "
	        		+ "on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id where stop.agencyid='"+agencyId+"' group by stime.trip_agencyid, stime.trip_id, stop.congdistid) as res "
	        		+ "where congdistid =  res.cid and agencyid = res.aid and tripid=res.tid;");
	        stmt.executeUpdate("update census_congdists_trip_map set stopscount=0 where stopscount IS NULL;");
	        stmt.executeUpdate("update census_congdists_trip_map map set tlength=res.ttime from ("
	        		+ "select max(stimes.departuretime)-min(stimes.arrivaltime) as ttime, "
	        		+ "stimes.agencyid, stimes.tripid, stimes.geoid from ("
	        		+ "select stime.arrivaltime, stime.departuretime, stime.trip_agencyid as agencyid, stime.trip_id as tripid, stop.congdistid as geoid "
	        		+ "from gtfs_stop_times stime inner join gtfs_stops stop on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id where stop.agencyid='"+agencyId+"') as stimes "
	        		+ "where stimes.arrivaltime>0 and stimes.departuretime>0 group by stimes.agencyid, stimes.tripid, stimes.geoid) as res "
	        		+ "where res.agencyid = map.agencyid and res.tripid=map.tripid and res.geoid=map.congdistid;");
	        stmt.executeUpdate("update census_congdists_trip_map set tlength=0 where tlength isnull;");

	        stmt.executeUpdate("ALTER TABLE census_congdists_trip_map ENABLE TRIGGER ALL;");
	        stmt.close();
	      } catch ( Exception e ) {
	    	  e.printStackTrace();
	      }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
	}
	
	/**
	 * Updates census_counties_trip_map table
	 *
	 * @param connection
	 * @param agencyId
	 */
	public static void updateCountyTripMap(Connection connection, String agencyId){
		Statement stmt = null;  
		try{
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM census_counties_trip_map LIMIT 1");
			if(!rs.next()){
				stmt.executeUpdate("DROP TABLE census_counties_trip_map;");
			}
		  }catch ( Exception e ) {
			  e.printStackTrace();
		  }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
		create_census_counties_trip_map(connection);
	      try {
	        stmt = connection.createStatement();
	        stmt.executeUpdate("ALTER TABLE census_counties_trip_map DISABLE TRIGGER ALL;");
	        
	        stmt.executeUpdate("WITH covered_trips AS (SELECT counties.countyid, counties.odotregionid, trips.id,trips.agencyid "
	        		+ "	FROM gtfs_trips AS trips INNER JOIN census_counties AS counties "
	        		+ "	ON ST_Contains(counties.shape, trips.shape) "
	        		+ "	WHERE serviceid_agencyid='"+agencyId+"'),"
	        		+ "output1 AS (SELECT trip.id, trip.agencyid, trip.serviceid_agencyid, trip.serviceid_id, trip.route_id, covered_trips.countyid, covered_trips.odotregionid, "
	        		+ "	ST_Multi(trip.shape) AS shape, ST_Length(st_transform(trip.shape,2993))/1609.34 AS length , trip.uid"
	        		+ "	FROM gtfs_trips AS trip INNER JOIN covered_trips USING(id,agencyid)),"
	        		+ "intersected_trips AS (SELECT trips.id,trips.agencyid FROM gtfs_trips AS trips LEFT JOIN covered_trips USING (id,agencyid) WHERE serviceid_agencyid='"+agencyId+"' AND covered_trips.id IS NULL),"
	        		+ "intersections as (select trip.id, trip.agencyid, trip.serviceid_agencyid, trip.serviceid_id, trip.route_id, county.countyid, county.odotregionid, "
	        		+ "	ST_Intersection(trip.shape,county.shape) as intersection, trip.uid "
	        		+ "	from gtfs_trips trip "
	        		+ "	inner join intersected_trips USING (id, agencyid)"
	        		+ "	inner join census_counties county on  st_intersects(county.shape,trip.shape)=true),"
	        		+ "output2 AS (select id, agencyid, serviceid_agencyid, serviceid_id, route_id, countyid, odotregionid, "
	        		+ "	st_multi(ST_CollectionExtract(st_union(intersection),2)), (ST_Length(st_transform(intersection,2993))/1609.34), uid "
	        		+ "	from intersections"
	        		+ "	group by id, agencyid, serviceid_agencyid, serviceid_id, route_id, countyid, odotregionid,intersection ,uid)"
	        		+ "insert into census_counties_trip_map(tripid, agencyid, agencyid_def, serviceid, routeid,  countyid, regionid, shape, length, uid) "
	        		+ "SELECT * FROM output1 UNION SELECT * FROM output2");
	        stmt.executeUpdate("update census_counties_trip_map set stopscount = res.cnt+0 from "
	        		+ "(select count(stop.id) as cnt, substring(stop.blockid,1,5) as cid, stime.trip_agencyid as aid, stime.trip_id as tid "
	        		+ "from gtfs_stops stop inner join gtfs_stop_times stime "
	        		+ "on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id where stop.agencyid='"+agencyId+"' group by stime.trip_agencyid, stime.trip_id, substring(stop.blockid,1,5)) as res "
	        		+ "where countyid =  res.cid and agencyid = res.aid and tripid=res.tid;");
	        stmt.executeUpdate("update census_counties_trip_map set stopscount=0 where stopscount IS NULL;");
	        stmt.executeUpdate("update census_counties_trip_map map set tlength=res.ttime from ("
	        		+ "select max(stimes.departuretime)-min(stimes.arrivaltime) as ttime,"
	        		+ "stimes.agencyid, stimes.tripid, stimes.geoid from ("
	        		+ "select stime.arrivaltime, stime.departuretime, stime.trip_agencyid as agencyid, stime.trip_id as tripid, substring(stop.blockid,1,5) as geoid "
	        		+ "from gtfs_stop_times stime inner join gtfs_stops stop on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id where stop.agencyid='"+agencyId+"') as stimes "
	        		+ "where stimes.arrivaltime>0 and stimes.departuretime>0 group by stimes.agencyid, stimes.tripid, stimes.geoid) as res "
	        		+ "where res.agencyid = map.agencyid and res.tripid=map.tripid and res.geoid=map.countyid;");
	        stmt.executeUpdate("update census_counties_trip_map set tlength=0 where tlength isnull;");

	        stmt.executeUpdate("ALTER TABLE census_counties_trip_map ENABLE TRIGGER ALL;");
	        stmt.close();
	      } catch ( Exception e ) {
	    	  e.printStackTrace();
	      }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
	}
	
	/**
	 * Updates census_states_trip_map table.
	 * 
	 * @param connection
	 * @param agencyId
	 */
	public static void updateStateTripMap(Connection connection, String agencyId){
		Statement stmt = null;  
		try{
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM census_states_trip_map LIMIT 1");
			if(!rs.next()){
				stmt.executeUpdate("DROP TABLE census_states_trip_map;");
			}
		  }catch ( Exception e ) {
//			  logger.debug( e.getClass().getName()+": "+ e.getMessage() );
		  }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
		create_census_states_trip_map(connection);
	      try {
	        stmt = connection.createStatement();
	        stmt.executeUpdate("ALTER TABLE census_states_trip_map DISABLE TRIGGER ALL;");
	        
	        stmt.executeUpdate("WITH covered_trips AS (SELECT states.stateid, trips.id,trips.agencyid "
	        		+ "	FROM gtfs_trips AS trips INNER JOIN census_states AS states "
	        		+ "	ON ST_Contains(states.shape, trips.shape) "
	        		+ "	WHERE serviceid_agencyid='"+agencyId+"'),"
	        		+ "output1 AS (SELECT trip.id, trip.agencyid, trip.serviceid_agencyid, trip.serviceid_id, trip.route_id, covered_trips.stateid, "
	        		+ "	ST_Multi(trip.shape) AS shape, ST_Length(st_transform(trip.shape,2993))/1609.34 AS length , trip.uid"
	        		+ "	FROM gtfs_trips AS trip INNER JOIN covered_trips USING(id,agencyid)),"
	        		+ "intersected_trips AS (SELECT trips.id,trips.agencyid FROM gtfs_trips AS trips LEFT JOIN covered_trips USING (id,agencyid) WHERE serviceid_agencyid='"+agencyId+"' AND covered_trips.id IS NULL),"
	        		+ "intersections as (select trip.id, trip.agencyid, trip.serviceid_agencyid, trip.serviceid_id, trip.route_id, state.stateid, "
	        		+ "	ST_Intersection(trip.shape,state.shape) as intersection, trip.uid "
	        		+ "	from gtfs_trips trip "
	        		+ "	inner join intersected_trips USING (id, agencyid)"
	        		+ "	inner join census_states state on  st_intersects(state.shape,trip.shape)=true),"
	        		+ "output2 AS (select id, agencyid, serviceid_agencyid, serviceid_id, route_id, stateid, "
	        		+ "	st_multi(ST_CollectionExtract(st_union(intersection),2)), (ST_Length(st_transform(intersection,2993))/1609.34), uid "
	        		+ "	from intersections"
	        		+ "	group by id, agencyid, serviceid_agencyid, serviceid_id, route_id, stateid,intersection ,uid)"
	        		+ "insert into census_states_trip_map(tripid, agencyid, agencyid_def, serviceid, routeid,  stateid, shape, length, uid) "
	        		+ "SELECT * FROM output1 UNION SELECT * FROM output2");
	        stmt.executeUpdate("update census_states_trip_map set stopscount = res.cnt+0 from "
	        		+ "(select count(stop.id) as cnt, substring(stop.blockid,1,2) as cid, stime.trip_agencyid as aid, stime.trip_id as tid "
	        		+ "from gtfs_stops stop inner join gtfs_stop_times stime "
	        		+ "on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id where stop.agencyid='"+agencyId+"' group by stime.trip_agencyid, stime.trip_id, substring(stop.blockid,1,2)) as res "
	        		+ "where stateid =  res.cid and agencyid = res.aid and tripid=res.tid;");
	        stmt.executeUpdate("update census_states_trip_map set stopscount=0 where stopscount IS NULL;");
	        stmt.executeUpdate("update census_states_trip_map map set tlength=res.ttime from ("
	        		+ "select max(stimes.departuretime)-min(stimes.arrivaltime) as ttime,"
	        		+ "stimes.agencyid, stimes.tripid, stimes.geoid from ("
	        		+ "select stime.arrivaltime, stime.departuretime, stime.trip_agencyid as agencyid, stime.trip_id as tripid, substring(stop.blockid,1,2) as geoid "
	        		+ "from gtfs_stop_times stime inner join gtfs_stops stop on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id where stop.agencyid='"+agencyId+"') as stimes "
	        		+ "where stimes.arrivaltime>0 and stimes.departuretime>0 group by stimes.agencyid, stimes.tripid, stimes.geoid) as res "
	        		+ "where res.agencyid = map.agencyid and res.tripid=map.tripid and res.geoid=map.stateid;");
	        stmt.executeUpdate("update census_states_trip_map set tlength=0 where tlength isnull;");

	        stmt.executeUpdate("ALTER TABLE census_states_trip_map ENABLE TRIGGER ALL;");
	        stmt.close();
	      } catch ( Exception e ) {
	    	  e.printStackTrace();
	      }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
	}
	
	/**
	 * Updates census_tracts_trip_map table.
	 * 
	 * @param connection
	 * @param agencyId
	 */
	public static void updateTractTripMap(Connection connection, String agencyId){
		Statement stmt = null;  
		try{
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM census_tracts_trip_map LIMIT 1");
			if(!rs.next()){
				stmt.executeUpdate("DROP TABLE census_tracts_trip_map;");
			}
		  }catch ( Exception e ) {
//			  logger.debug( e.getClass().getName()+": "+ e.getMessage() );
		  }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
		create_census_tracts_trip_map(connection);
	      try {
	        stmt = connection.createStatement();
	        stmt.executeUpdate("ALTER TABLE census_tracts_trip_map DISABLE TRIGGER ALL;");
	        
	        stmt.executeUpdate("WITH covered_trips AS (SELECT tracts.tractid, trips.id, trips.agencyid "
	        		+ "FROM gtfs_trips AS trips INNER JOIN census_tracts AS tracts "
	        		+ "	ON ST_Contains(tracts.shape, trips.shape) "
	        		+ "	WHERE serviceid_agencyid='" + agencyId + "'),"
	        		+ "output1 AS (SELECT trip.id, trip.agencyid, trip.serviceid_agencyid, trip.serviceid_id, trip.route_id, covered_trips.tractid,"
	        		+ "	ST_Multi(trip.shape) AS shape, ST_Length(st_transform(trip.shape,2993))/1609.34 AS length , trip.uid"
	        		+ "	FROM gtfs_trips AS trip INNER JOIN covered_trips USING(id,agencyid)),"
	        		+ "intersected_trips AS (SELECT trips.id,trips.agencyid FROM gtfs_trips AS trips LEFT JOIN covered_trips USING (id,agencyid) WHERE serviceid_agencyid='" + agencyId + "' AND covered_trips.id IS NULL),"
	        		+ "intersections as (select trip.id, trip.agencyid, trip.serviceid_agencyid, trip.serviceid_id, trip.route_id, tracts.tractid, "
	        		+ "	ST_Intersection(trip.shape,tracts.shape) as intersection, trip.uid "
	        		+ "	from gtfs_trips trip "
	        		+ "	inner join intersected_trips USING (id, agencyid)"
	        		+ "	inner join census_tracts tracts on st_intersects(tracts.shape,trip.shape)),"
	        		+ "output2 AS (select id, agencyid, serviceid_agencyid, serviceid_id, route_id, tractid,"
	        		+ "	st_multi(ST_CollectionExtract(st_union(intersection),2)), (ST_Length(st_transform(intersection,2993))/1609.34), uid "
	        		+ "	from intersections"
	        		+ "	group by id, agencyid, serviceid_agencyid, serviceid_id, route_id, tractid, intersection ,uid) "
	        		+ "insert into census_tracts_trip_map(tripid, agencyid, agencyid_def, serviceid, routeid,  tractid, shape, length, uid) "
	        		+ "SELECT * FROM output1 UNION SELECT * FROM output2");
	        stmt.executeUpdate("update census_tracts_trip_map set stopscount = res.cnt+0 from "
	        		+ "(select count(stop.id) as cnt, substring(stop.blockid,1,11) as cid, stime.trip_agencyid as aid, stime.trip_id as tid "
	        		+ "from gtfs_stops stop inner join gtfs_stop_times stime "
	        		+ "on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id where stop.agencyid='"+agencyId+"' group by stime.trip_agencyid, stime.trip_id, substring(stop.blockid,1,11)) as res "
	        		+ "where tractid =  res.cid and agencyid = res.aid and tripid=res.tid;");
	        stmt.executeUpdate("update census_tracts_trip_map set stopscount=0 where stopscount IS NULL;");
	        stmt.executeUpdate("update census_tracts_trip_map map set tlength=res.ttime from ("
	        		+ "select max(stimes.departuretime)-min(stimes.arrivaltime) as ttime,"
	        		+ "stimes.agencyid, stimes.tripid, stimes.geoid from ("
	        		+ "select stime.arrivaltime, stime.departuretime, stime.trip_agencyid as agencyid, stime.trip_id as tripid, substring(stop.blockid,1,11) as geoid "
	        		+ "from gtfs_stop_times stime inner join gtfs_stops stop on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id where stop.agencyid='"+agencyId+"') as stimes "
	        		+ "where stimes.arrivaltime>0 and stimes.departuretime>0 group by stimes.agencyid, stimes.tripid, stimes.geoid) as res "
	        		+ "where res.agencyid = map.agencyid and res.tripid=map.tripid and res.geoid=map.tractid;");
	        stmt.executeUpdate("update census_tracts_trip_map set tlength=0 where tlength isnull;");

	        stmt.executeUpdate("ALTER TABLE census_tracts_trip_map ENABLE TRIGGER ALL;");
	        stmt.close();
	      } catch ( Exception e ) {
	    	  e.printStackTrace();
	      }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
	}
	
	/**
	 * Updates census_places_trip_map table.
	 * 
	 * @param connection
	 * @param agencyId
	 */
	public static void updatePlaceTripMap(Connection connection, String agencyId){
		Statement stmt = null;  
		try{
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM census_places_trip_map LIMIT 1");
			if(!rs.next()){
				stmt.executeUpdate("DROP TABLE census_places_trip_map;");
			}
		  }catch ( Exception e ) {
//			  logger.debug( e.getClass().getName()+": "+ e.getMessage() );
		  }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
		create_census_places_trip_map(connection);
	      try {
	        stmt = connection.createStatement();
	        stmt.executeUpdate("ALTER TABLE census_places_trip_map DISABLE TRIGGER ALL;");
	        
	        stmt.executeUpdate("WITH covered_trips AS (SELECT places.placeid, trips.id,trips.agencyid "
	        		+ "	FROM gtfs_trips AS trips INNER JOIN census_places AS places "
	        		+ "	ON ST_Contains(places.shape, trips.shape) "
	        		+ "	WHERE serviceid_agencyid='" + agencyId + "'),"
	        		+ "output1 AS (SELECT trip.id, trip.agencyid, trip.serviceid_agencyid, trip.serviceid_id, trip.route_id, covered_trips.placeid,"
	        		+ "	ST_Multi(trip.shape) AS shape, ST_Length(st_transform(trip.shape,2993))/1609.34 AS length , trip.uid"
	        		+ "	FROM gtfs_trips AS trip INNER JOIN covered_trips USING(id,agencyid)),"
	        		+ "intersected_trips AS (SELECT trips.id,trips.agencyid FROM gtfs_trips AS trips LEFT JOIN covered_trips USING (id,agencyid) WHERE serviceid_agencyid='" + agencyId + "' AND covered_trips.id IS NULL),"
	        		+ "intersections as (select trip.id, trip.agencyid, trip.serviceid_agencyid, trip.serviceid_id, trip.route_id, places.placeid, "
	        		+ "	ST_Intersection(trip.shape,places.shape) as intersection, trip.uid "
	        		+ "	from gtfs_trips trip "
	        		+ "	inner join intersected_trips USING (id, agencyid)"
	        		+ "	inner join census_places places on st_intersects(places.shape,trip.shape)),"
	        		+ "output2 AS (select id, agencyid, serviceid_agencyid, serviceid_id, route_id, placeid,"
	        		+ "	st_multi(ST_CollectionExtract(st_union(intersection),2)), (ST_Length(st_transform(intersection,2993))/1609.34), uid "
	        		+ "	from intersections"
	        		+ "	group by id, agencyid, serviceid_agencyid, serviceid_id, route_id, placeid, intersection ,uid) "
	        		+ "insert into census_places_trip_map(tripid, agencyid, agencyid_def, serviceid, routeid,  placeid, shape, length, uid) "
	        		+ "SELECT * FROM output1 UNION SELECT * FROM output2");
	        stmt.executeUpdate("update census_places_trip_map set stopscount = res.cnt+0 from "
	        		+ "(select count(stop.id) as cnt, stop.placeid as cid, stime.trip_agencyid as aid, stime.trip_id as tid "
	        		+ "from gtfs_stops stop inner join gtfs_stop_times stime "
	        		+ "on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id where stop.agencyid='"+agencyId+"' group by stime.trip_agencyid, stime.trip_id, stop.placeid) as res "
	        		+ "where placeid =  res.cid and agencyid = res.aid and tripid=res.tid;");
	        stmt.executeUpdate("update census_places_trip_map set stopscount=0 where stopscount IS NULL;");
	        stmt.executeUpdate("update census_places_trip_map map set tlength=res.ttime from ("
	        		+ "select max(stimes.departuretime)-min(stimes.arrivaltime) as ttime,"
	        		+ "stimes.agencyid, stimes.tripid, stimes.geoid from ("
	        		+ "select stime.arrivaltime, stime.departuretime, stime.trip_agencyid as agencyid, stime.trip_id as tripid, stop.placeid as geoid "
	        		+ "from gtfs_stop_times stime inner join gtfs_stops stop on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id where stop.agencyid='"+agencyId+"') as stimes "
	        		+ "where stimes.arrivaltime>0 and stimes.departuretime>0 group by stimes.agencyid, stimes.tripid, stimes.geoid) as res "
	        		+ "where res.agencyid = map.agencyid and res.tripid=map.tripid and res.geoid=map.placeid;");
	        stmt.executeUpdate("update census_places_trip_map set tlength=0 where tlength isnull;");

	        stmt.executeUpdate("ALTER TABLE census_places_trip_map ENABLE TRIGGER ALL;");
	        stmt.close();
	      } catch ( Exception e ) {
	    	  e.printStackTrace();
	      }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
	}
	
	/**
	 * Updates census_urbans_trip_map table.
	 * 
	 * @param connection
	 * @param agencyId
	 */
	public static void updateUrbanTripMap(Connection connection, String agencyId){
		Statement stmt = null;  
		try{
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM census_urbans_trip_map LIMIT 1");
			if(!rs.next()){
				stmt.executeUpdate("DROP TABLE census_urbans_trip_map;");
			}
		  }catch ( Exception e ) {
//			  logger.debug( e.getClass().getName()+": "+ e.getMessage() );
		  }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
		create_census_urbans_trip_map(connection);
	      try {
	        stmt = connection.createStatement();
	        stmt.executeUpdate("ALTER TABLE census_urbans_trip_map DISABLE TRIGGER ALL;");
	        
	        stmt.executeUpdate("WITH covered_trips AS (SELECT urbans.urbanid, trips.id, trips.agencyid "
	        		+ "	FROM gtfs_trips AS trips INNER JOIN census_urbans AS urbans "
	        		+ "	ON ST_Contains(urbans.shape, trips.shape) "
	        		+ "	WHERE serviceid_agencyid='" + agencyId + "'),"
	        		+ "output1 AS (SELECT trip.id, trip.agencyid, trip.serviceid_agencyid, trip.serviceid_id, trip.route_id, covered_trips.urbanid,"
	        		+ "	ST_Multi(trip.shape) AS shape, ST_Length(st_transform(trip.shape,2993))/1609.34 AS length , trip.uid"
	        		+ "	FROM gtfs_trips AS trip INNER JOIN covered_trips USING(id,agencyid)),"
	        		+ "intersected_trips AS (SELECT trips.id,trips.agencyid FROM gtfs_trips AS trips LEFT JOIN covered_trips USING (id,agencyid) WHERE serviceid_agencyid='" + agencyId + "' AND covered_trips.id IS NULL),"
	        		+ "intersections as (select trip.id, trip.agencyid, trip.serviceid_agencyid, trip.serviceid_id, trip.route_id, urbans.urbanid, "
	        		+ "	ST_Intersection(trip.shape,urbans.shape) as intersection, trip.uid "
	        		+ "	from gtfs_trips trip "
	        		+ "	inner join intersected_trips USING (id, agencyid)"
	        		+ "	inner join census_urbans urbans on st_intersects(urbans.shape,trip.shape)),"
	        		+ "output2 AS (select id, agencyid, serviceid_agencyid, serviceid_id, route_id, urbanid,"
	        		+ "	st_multi(ST_CollectionExtract(st_union(intersection),2)), (ST_Length(st_transform(intersection,2993))/1609.34), uid "
	        		+ "	from intersections"
	        		+ "	group by id, agencyid, serviceid_agencyid, serviceid_id, route_id, urbanid, intersection ,uid) "
	        		+ "insert into census_urbans_trip_map(tripid, agencyid, agencyid_def, serviceid, routeid,  urbanid, shape, length, uid) "
	        		+ "SELECT * FROM output1 UNION SELECT * FROM output2");
	        stmt.executeUpdate("update census_urbans_trip_map set stopscount = res.cnt+0 from "
	        		+ "(select count(stop.id) as cnt, stop.urbanid as cid, stime.trip_agencyid as aid, stime.trip_id as tid "
	        		+ "from gtfs_stops stop inner join gtfs_stop_times stime "
	        		+ "on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id where stop.agencyid='"+agencyId+"' group by stime.trip_agencyid, stime.trip_id, stop.urbanid) as res "
	        		+ "where urbanid =  res.cid and agencyid = res.aid and tripid=res.tid;");
	        stmt.executeUpdate("update census_urbans_trip_map set stopscount=0 where stopscount IS NULL;");
	        stmt.executeUpdate("update census_urbans_trip_map map set tlength=res.ttime from ("
	        		+ "select max(stimes.departuretime)-min(stimes.arrivaltime) as ttime,"
	        		+ "stimes.agencyid, stimes.tripid, stimes.geoid from ("
	        		+ "select stime.arrivaltime, stime.departuretime, stime.trip_agencyid as agencyid, stime.trip_id as tripid, stop.urbanid as geoid "
	        		+ "from gtfs_stop_times stime inner join gtfs_stops stop on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id where stop.agencyid='"+agencyId+"') as stimes "
	        		+ "where stimes.arrivaltime>0 and stimes.departuretime>0 group by stimes.agencyid, stimes.tripid, stimes.geoid) as res "
	        		+ "where res.agencyid = map.agencyid and res.tripid=map.tripid and res.geoid=map.urbanid;");
	        stmt.executeUpdate("update census_urbans_trip_map set tlength=0 where tlength isnull;");

	        stmt.executeUpdate("ALTER TABLE census_urbans_trip_map ENABLE TRIGGER ALL;");
	        stmt.close();
	      } catch ( Exception e ) {
	    	  e.printStackTrace();
	      }finally{
	    	  if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
	      }
	}
}
