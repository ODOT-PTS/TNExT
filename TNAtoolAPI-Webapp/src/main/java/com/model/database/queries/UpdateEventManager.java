package com.model.database.queries;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; 

import com.model.database.Databases;


public class UpdateEventManager {
	
	private static Connection makeConnection(int dbindex){
		Connection response = null;
		try {
		Class.forName("org.postgresql.Driver");
		response = DriverManager
           .getConnection(Databases.connectionURLs[dbindex],
           Databases.usernames[dbindex], Databases.passwords[dbindex]);
		}catch ( Exception e ) {
	        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
	        System.exit(0);
	      }
		return response;
	}
	
	private static void dropConnection(Connection connection){
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
    
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
	      }
	}
	
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
		  }
	}
	
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
	      }
	}
	
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
	      }
	}
	
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
	      }
	}
	
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
	    }
	}
	
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
	      }
	}
	
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
	      }
	}
	
	/**
	 *Updates all the additional tables. This is called every time a new feed is added to the database
	 */
	public static void updateTables(int dbindex, String agencyId){	
		  Connection connection = makeConnection(dbindex);
		  
		  System.out.println("Adding Function");
		  addFunction(connection, dbindex);
		  System.out.println("Updating gtfs_trips");
		  updateTrip(connection, agencyId);
		  System.out.println("Updating gtfs_stops");
		  updateStopsAddGeolocation(connection, agencyId);
		  updateGtfsStopsGeoCoder(connection, agencyId);
		  System.out.println("Updating gtfs_stop_route_map");
		  updateGtfsStopRouteMap(connection, agencyId);
		  System.out.println("Updating gtfs_stop_service_map");
		  updateGtfsStopServiceMap(connection, agencyId);
		  System.out.println("Updating gtfs_trip_stops");
		  updateGtfsTripStops(connection, agencyId);
		  System.out.println("Updating census_counties_trip_map");
		  updateCountyTripMap(connection, agencyId);
		  System.out.println("Updating census_tracts_trip_map");
		  updateTractTripMap(connection, agencyId);
		  System.out.println("Updating census_urbans_trip_map");
		  updateUrbanTripMap(connection, agencyId);
		  System.out.println("Updating census_places_trip_map");
		  updatePlaceTripMap(connection, agencyId);
		  System.out.println("Updating census_congdists_trip_map");
		  updateCongdistTripMap(connection, agencyId);
		  System.out.println("Updating gtfs_feed_info");
		  updateCalendarRange(connection, agencyId);
		  
		  
		  dropConnection(connection);
	}
	
	/**
	 *Updates gtfs_trip_stops table
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
			  System.out.println( e.getClass().getName()+": "+ e.getMessage() );
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
	      }
	}

	/**
	 *Updates gtfs_feed_info table with the calendar range
	 */
	public static void updateCalendarRange(Connection connection, String agencyId){
		Statement stmt = null;
	      try {
	        stmt = connection.createStatement();
	        
	        stmt.executeUpdate("with calendars as (select serviceid_agencyid as agencyid, min(startdate::int) as calstart, max(enddate::int) as calend from gtfs_calendars where serviceid_agencyid='"+agencyId+"' group by serviceid_agencyid),"
	        		+ "calendardates as (select serviceid_agencyid as agencyid, min(date::int) as calstart, max(date::int) as calend from gtfs_calendar_dates where serviceid_agencyid='"+agencyId+"' group by serviceid_agencyid),"
	        		+ "calendar as (select cals.agencyid, least(cals.calstart, calds.calstart) as calstart, greatest(cals.calend, calds.calend) as calend from calendars cals full join calendardates calds using(agencyid)) "
	        		+ "update gtfs_feed_info set startdate= calendar.calstart::varchar , enddate=calendar.calend::varchar from calendar where defaultid = agencyid;");
	        
	        stmt.close();
	      } catch ( Exception e ) {
	    	  e.printStackTrace();
	      }
	}

	/**
	 *Updates gtfs_stop_service_map table
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
			  System.out.println( e.getClass().getName()+": "+ e.getMessage() );
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
	      }
	}
	
	/**
	 *Updates gtfs_stops table with Geolocations
	 */
	public static void updateStopsAddGeolocation(Connection connection, String agencyId){
		Statement stmt = null;
	      try {
	        stmt = connection.createStatement();
	        stmt.executeUpdate("alter table IF EXISTS gtfs_stops add column location geometry(Point,2993);");
	        stmt.executeUpdate("CREATE INDEX ids_location ON gtfs_stops"
	        		+ "  USING GIST (location);");
	        stmt.executeUpdate("CLUSTER ids_location ON gtfs_stops;");
	        
	        //stmt.executeUpdate("VACUUM ANALYZE gtfs_stops;");
	        
	        stmt.close();
	      } catch ( Exception e ) {
	    	  //System.out.println( e.getClass().getName()+": "+ e.getMessage() );
	      }
	      
	      try{
	    	  stmt = connection.createStatement();
	    	  stmt.executeUpdate("ALTER TABLE gtfs_stops DISABLE TRIGGER ALL;");
	    	  
	    	  stmt.executeUpdate("update gtfs_stops set location = ST_transform(ST_setsrid(ST_MakePoint(lon, lat),4326), 2993) where agencyid='"+agencyId+"';");  
	    	  
	    	  stmt.executeUpdate("ALTER TABLE gtfs_stops ENABLE TRIGGER ALL;");
	    	  stmt.close();
	      }catch ( Exception e ) {
	    	  e.printStackTrace();
	      }
	      
	}
	
	/**
	 *Adding SQL functions
	 */
	public static void addFunction(Connection connection, int dbindex){
		/*String basePath = System.getProperty("user.dir").replace('\\', '/')+"/";
		Process pr;
		ProcessBuilder pb;
		String name = Databases.dbnames[dbindex];
		String usrn = Databases.usernames[dbindex];
		String pass = Databases.passwords[dbindex];
		try {
			pb = new ProcessBuilder("cmd", "/c", "start", basePath+"TNAtoolAPI-Webapp/WebContent/admin/Development/PGSQL/addFunctions.bat", pass, usrn, name,
					psqlPath+"psql.exe",
					basePath+"TNAtoolAPI-Webapp/WebContent/admin/Development/PGSQL/");
			pb.redirectErrorStream(true);
			pr = pb.start();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	      
	}
	
	/**
	 *Updates gtfs_trips table
	 */
	public static void updateTrip(Connection connection, String agencyId){
		Statement stmt = null;
	     
		try{
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT AddGeometryColumn( 'public', 'gtfs_trips', 'shape', 4326, 'linestring', 2 );");
			stmt.executeUpdate("create INDEX trips_shapeids on gtfs_trips (shapeid_agencyid,shapeid_id);");
			stmt.executeUpdate("create unique index tripids on gtfs_trips (agencyid,id);");
			
		  }catch ( Exception e ) {
			  //System.out.println( e.getClass().getName()+": "+ e.getMessage() );
		  }
		
	      try{
	    	  stmt = connection.createStatement();
	    	  stmt.executeUpdate("ALTER TABLE gtfs_trips DISABLE TRIGGER ALL;");
	    	  
	    	  stmt.executeUpdate("update gtfs_trips trip set shape = tss.shape, epshape=GoogleEncodeLine(tss.shape), length = (tss.length)/1609.34, estlength=0 FROM "
	    	  		+ "(select ST_MakeLine(ST_setsrid(ST_MakePoint(shppoint.lon, shppoint.lat),4326)) as shape, ST_Length(st_transform(ST_MakeLine(ST_setsrid(ST_MakePoint(shppoint.lon, shppoint.lat),4326)),2993)) as length,"
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
	      }
	      
	}
	
	/**
	 *Updates gtfs_stop_route_map table
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
			  System.out.println( e.getClass().getName()+": "+ e.getMessage() );
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
		  }
	}
	
	/**
	 *Updates gtfs_stops table. GeoCoder
	 */
	public static void updateGtfsStopsGeoCoder(Connection connection, String agencyId){
		Statement stmt = null;
	      try {
	        stmt = connection.createStatement();
	        stmt.executeUpdate("ALTER TABLE gtfs_stops DISABLE TRIGGER ALL;");
	        
	        stmt.executeUpdate("update gtfs_stops stop set blockid=shape.geoid10 from census_blocks_reference shape where stop.agencyid='"+agencyId+"' and st_within(ST_MakePoint(stop.lon, stop.lat),shape.geom)=true ;");
	        stmt.executeUpdate("update gtfs_stops stop set placeid=shape.placeid from census_places shape where stop.agencyid='"+agencyId+"' and st_within(ST_SetSRID(ST_MakePoint(stop.lon, stop.lat),4326),shape.shape)=true ;");
	        stmt.executeUpdate("update gtfs_stops stop set congdistid=shape.congdistid from census_congdists shape where stop.agencyid='"+agencyId+"' and st_within(ST_SetSRID(ST_MakePoint(stop.lon, stop.lat),4326),shape.shape)=true;");
	        stmt.executeUpdate("update gtfs_stops stop set regionid = county.odotregionid from census_counties county where stop.agencyid='"+agencyId+"' and left(stop.blockid,5)= county.countyid::varchar(5);");
	        stmt.executeUpdate("update gtfs_stops stop set urbanid=shape.urbanid from census_urbans shape where stop.agencyid='"+agencyId+"' and st_within(ST_SetSRID(ST_MakePoint(stop.lon, stop.lat),4326),shape.shape)=true;");
	        
	        stmt.executeUpdate("ALTER TABLE gtfs_stops ENABLE TRIGGER ALL;");
	        stmt.close();
	      } catch ( Exception e ) {
	    	  e.printStackTrace();
	      }
	}
	
	/**
	 *Updates census_congdists_trip_map table.
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
			  System.out.println( e.getClass().getName()+": "+ e.getMessage() );
		  }
		create_census_congdists_trip_map(connection);
	      try {
	        stmt = connection.createStatement();
	        stmt.executeUpdate("ALTER TABLE census_congdists_trip_map DISABLE TRIGGER ALL;");
	        
	        stmt.executeUpdate("with groutes as(SELECT routeid, ST_multi(ST_Collect(ST_SetSRID(ST_MakePoint(stops.lon, stops.lat), 4326))) multi "
	        		+ "FROM gtfs_stop_route_map AS map INNER JOIN gtfs_stops AS stops "
	        		+ "ON map.agencyid = stops.agencyid AND map.stopid = stops.id "
	        		+ "GROUP BY routeid)"
	        		+ "insert into census_congdists_trip_map(tripid, agencyid, agencyid_def, serviceid, routeid,  congdistid, shape, length, uid) "
	        		+ "select trip.id, trip.agencyid, trip.serviceid_agencyid, trip.serviceid_id, trip.route_id, congdist.congdistid, st_multi(ST_CollectionExtract(st_union(ST_Intersection(trip.shape,congdist.shape)),2)), (ST_Length(st_transform(ST_Intersection(trip.shape,congdist.shape),2993))/1609.34), trip.uid "
	        		+ "from gtfs_trips trip "
	        		+ "inner join groutes on trip.route_id=groutes.routeid "
	        		+ "inner join census_congdists congdist on  st_intersects(congdist.shape,trip.shape)=true and st_intersects(congdist.shape,multi)=true where trip.serviceid_agencyid='"+agencyId+"' "
    				+ "group by trip.id, trip.agencyid, congdist.congdistid;");
	        stmt.executeUpdate("update census_congdists_trip_map set stopscount = res.cnt+0 from "
	        		+ "(select count(stop.id) as cnt, stop.congdistid as cid, stime.trip_agencyid as aid, stime.trip_id as tid "
	        		+ "from gtfs_stops stop inner join gtfs_stop_times stime "
	        		+ "on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id where stop.agencyid='"+agencyId+"' group by stime.trip_agencyid, stime.trip_id, stop.congdistid) as res "
	        		+ "where congdistid =  res.cid and agencyid = res.aid and tripid=res.tid;");
	        stmt.executeUpdate("update census_congdists_trip_map set stopscount=0 where stopscount IS NULL;");
	        stmt.executeUpdate("update census_congdists_trip_map map set tlength=res.time from (select max(departuretime)-min(arrivaltime) as time, trip_agencyid as agencyid, trip_id as id from gtfs_stop_times where stop_agencyid='"+agencyId+"' and arrivaltime>0 and departuretime>0 group by trip_agencyid, trip_id) as res "
	        		+ "where res.agencyid = map.agencyid and res.id=map.tripid;");
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
	      }
	}
	
	/**
	 *Updates census_counties_trip_map table.
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
			  System.out.println( e.getClass().getName()+": "+ e.getMessage() );
		  }
		create_census_counties_trip_map(connection);
	      try {
	        stmt = connection.createStatement();
	        stmt.executeUpdate("ALTER TABLE census_counties_trip_map DISABLE TRIGGER ALL;");
	        
	        stmt.executeUpdate("with groutes as(SELECT routeid, ST_multi(ST_Collect(ST_SetSRID(ST_MakePoint(stops.lon, stops.lat), 4326))) multi "
	        		+ "FROM gtfs_stop_route_map AS map INNER JOIN gtfs_stops AS stops "
	        		+ "ON map.agencyid = stops.agencyid AND map.stopid = stops.id "
	        		+ "GROUP BY routeid)"
	        		+ "insert into census_counties_trip_map(tripid, agencyid, agencyid_def, serviceid, routeid,  countyid, regionid, shape, length, uid) "
	        		+ "select trip.id, trip.agencyid, trip.serviceid_agencyid, trip.serviceid_id, trip.route_id, county.countyid, county.odotregionid, st_multi(ST_CollectionExtract(st_union(ST_Intersection(trip.shape,county.shape)),2)), (ST_Length(st_transform(ST_Intersection(trip.shape,county.shape),2993))/1609.34), trip.uid "
	        		+ "from gtfs_trips trip "
	        		+ "inner join groutes on trip.route_id=groutes.routeid "
	        		+ "inner join census_counties county on  st_intersects(county.shape,trip.shape)=true and st_intersects(county.shape,multi)=true where trip.serviceid_agencyid='"+agencyId+"' "
    				+ "group by trip.id, trip.agencyid, county.countyid;");
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
	      }
	}
	
	/**
	 *Updates census_tracts_trip_map table.
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
			  System.out.println( e.getClass().getName()+": "+ e.getMessage() );
		  }
		create_census_tracts_trip_map(connection);
	      try {
	        stmt = connection.createStatement();
	        stmt.executeUpdate("ALTER TABLE census_tracts_trip_map DISABLE TRIGGER ALL;");
	        
	        stmt.executeUpdate("with groutes as(SELECT routeid, ST_multi(ST_Collect(ST_SetSRID(ST_MakePoint(stops.lon, stops.lat), 4326))) multi "
	        		+ "FROM gtfs_stop_route_map AS map INNER JOIN gtfs_stops AS stops "
	        		+ "ON map.agencyid = stops.agencyid AND map.stopid = stops.id "
	        		+ "GROUP BY routeid)"
	        		+ "insert into census_tracts_trip_map(tripid, agencyid, agencyid_def, serviceid, routeid,  tractid, shape, length, uid) "
	        		+ "select trip.id, trip.agencyid, trip.serviceid_agencyid, trip.serviceid_id, trip.route_id, tract.tractid, st_multi(ST_CollectionExtract(st_union(ST_Intersection(trip.shape,tract.shape)),2)), (ST_Length(st_transform(ST_Intersection(trip.shape,tract.shape),2993))/1609.34), trip.uid "
	        		+ "from gtfs_trips trip "
	        		+ "inner join groutes on trip.route_id=groutes.routeid "
	        		+ "inner join census_tracts tract on  st_intersects(tract.shape,trip.shape)=true and st_intersects(tract.shape,multi)=true where trip.serviceid_agencyid='"+agencyId+"' "
    				+ "group by trip.id, trip.agencyid, tract.tractid;");
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
	      }
	}
	
	/**
	 *Updates census_places_trip_map table.
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
			  System.out.println( e.getClass().getName()+": "+ e.getMessage() );
		  }
		create_census_places_trip_map(connection);
	      try {
	        stmt = connection.createStatement();
	        stmt.executeUpdate("ALTER TABLE census_places_trip_map DISABLE TRIGGER ALL;");
	        
	        stmt.executeUpdate("with groutes as(SELECT routeid, ST_multi(ST_Collect(ST_SetSRID(ST_MakePoint(stops.lon, stops.lat), 4326))) multi "
	        		+ "FROM gtfs_stop_route_map AS map INNER JOIN gtfs_stops AS stops "
	        		+ "ON map.agencyid = stops.agencyid AND map.stopid = stops.id "
	        		+ "GROUP BY routeid)"
	        		+ "insert into census_places_trip_map(tripid, agencyid, agencyid_def, serviceid, routeid,  placeid, shape, length, uid) "
	        		+ "select trip.id, trip.agencyid, trip.serviceid_agencyid, trip.serviceid_id, trip.route_id, place.placeid, st_multi(ST_CollectionExtract(st_union(ST_Intersection(trip.shape,place.shape)),2)), (ST_Length(st_transform(ST_Intersection(trip.shape,place.shape),2993))/1609.34), trip.uid "
	        		+ "from gtfs_trips trip "
	        		+ "inner join groutes on trip.route_id=groutes.routeid "
	        		+ "inner join census_places place on  st_intersects(place.shape,trip.shape)=true and st_intersects(place.shape,multi)=true where trip.serviceid_agencyid='"+agencyId+"' "
    				+ "group by trip.id, trip.agencyid, place.placeid;");
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
	      }
	}
	
	/**
	 *Updates census_urbans_trip_map table.
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
			  System.out.println( e.getClass().getName()+": "+ e.getMessage() );
		  }
		create_census_urbans_trip_map(connection);
	      try {
	        stmt = connection.createStatement();
	        stmt.executeUpdate("ALTER TABLE census_urbans_trip_map DISABLE TRIGGER ALL;");
	        
	        stmt.executeUpdate("with groutes as(SELECT routeid, ST_multi(ST_Collect(ST_SetSRID(ST_MakePoint(stops.lon, stops.lat), 4326))) multi "
	        		+ "FROM gtfs_stop_route_map AS map INNER JOIN gtfs_stops AS stops "
	        		+ "ON map.agencyid = stops.agencyid AND map.stopid = stops.id "
	        		+ "GROUP BY routeid)"
	        		+ "insert into census_urbans_trip_map(tripid, agencyid, agencyid_def, serviceid, routeid,  urbanid, shape, length, uid) "
	        		+ "select trip.id, trip.agencyid, trip.serviceid_agencyid, trip.serviceid_id, trip.route_id, urban.urbanid, st_multi(ST_CollectionExtract(st_union(ST_Intersection(trip.shape,urban.shape)),2)), (ST_Length(st_transform(ST_Intersection(trip.shape,urban.shape),2993))/1609.34), trip.uid "
	        		+ "from gtfs_trips trip "
	        		+ "inner join groutes on trip.route_id=groutes.routeid "
	        		+ "inner join census_urbans urban on  st_intersects(urban.shape,trip.shape)=true and st_intersects(urban.shape,multi)=true where trip.serviceid_agencyid='"+agencyId+"' "
    				+ "group by trip.id, trip.agencyid, urban.urbanid;");
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
	      }
	}
}
