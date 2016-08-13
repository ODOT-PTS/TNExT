package com.model.database.queries;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; 
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map.Entry;

import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.model.database.Databases;
import com.model.database.queries.objects.*;
import com.model.database.queries.util.Types;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class PgisEventManager {
	//public static Connection connection;
	
	public static Connection makeConnection(int dbindex){
		String url = "";
		switch (dbindex){
		case 0:
			url = "gtfsdb";
			break;
		case 1:
			url = "gtfsdb1";
			break;
		}
		Connection response = null;
		try {
		Class.forName("org.postgresql.Driver");
		response = DriverManager
           .getConnection(Databases.connectionURLs[dbindex],
           Databases.usernames[dbindex], Databases.passwords[dbindex]);
		}catch ( Exception e ) {
	        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
	         
	      }
		return response;
	}
	
	public static void dropConnection(Connection connection){
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/////GEO AREA EXTENDED REPORTS QUERIES
	
	///// CONNECTED AGENCIES ON-MAP REPORT QUERIES
	/**
	 * Queries stops within a certain distance of a given stop while filtering the agencies.
	 */
	public static CAStopsList getConnectedStops(double lat, double lon, double gap, String agencies, int dbindex){
		CAStopsList results=new CAStopsList();	// This object is declared to hold the stops
		
		Connection connection = makeConnection(dbindex);
	    Statement stmt = null;
	    String query = "WITH stops AS (select map.agencyid as agencyid, stop.description, stop.lat, stop.lon, stop.name as name, stop.id as id, stop.url, location "
	    		+ "   		from gtfs_stops stop inner join gtfs_stop_service_map map on map.agencyid_def=stop.agencyid and map.stopid=stop.id "
	    		+ "    		and map.agencyid=ANY('{"+agencies+"}'::text[]) ), "
	    		+ "    	main AS (SELECT agencyid, id, name, description, lat, lon "
	    		+ "    		FROM stops WHERE ST_Dwithin(ST_transform(ST_setsrid(ST_MakePoint("+lon+", "+lat+"),4326), 2993), location, "+gap+")) "
	    		+ "     SELECT main.id, main.name stopname, main.description, main.agencyid, gtfs_agencies.name agencyname, main.lat, main.lon "
	    		+ "    		FROM main INNER JOIN gtfs_agencies ON main.agencyid=gtfs_agencies.id";
	    System.out.println(query);

	    try {
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()){
				CAStop instance  = new CAStop();
				
				instance.id = rs.getString("id");
				instance.name = rs.getString("stopname");
				instance.agencyName = rs.getString("agencyname");
				instance.agencyId = rs.getString("agencyid");
				instance.lat = rs.getString("lat");
				instance.lon = rs.getString("lon");
				results.stopsList.add(instance);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	      
		return results;
	}
	
	/////GEO AREA EXTENDED REORTS QUERIES
	
	/**
	 *Queries Route miles for a geographic area
	 */
	public static float RouteMiles(int type, String areaId, String username, int dbindex) 
    {	
	  Connection connection = makeConnection(dbindex);
      Statement stmt = null;
      float RouteMiles = 0;
      String query = "with aids as (select distinct agency_id as aid from gtfs_selected_feeds where username='"+username+"'), trips as (select agencyid, routeid, "
      		+ "round(max(length)::numeric,2) as length from "+Types.getTripMapTableName(type)+" map inner join aids on map.agencyid_def=aids.aid where "+
        Types.getIdColumnName(type)+" ='"+areaId +"'  group by agencyid, routeid) select sum(length) as routemiles from trips;";
      try {
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);        
        while ( rs.next() ) {
        	RouteMiles = rs.getFloat("routemiles");                     
        }
        rs.close();
        stmt.close();        
      } catch ( Exception e ) {
        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
         
      }
      dropConnection(connection);      
      return RouteMiles;
    }
	
	/**
	 *Queries Fare Information for a geographic area. keys are; minfare, maxfare, medianfare, averagefare
	 */
	public static HashMap<String, Float> FareInfo(int type, String[] date, String[] day, String areaId, String username, int dbindex) 
    {	
	  Connection connection = makeConnection(dbindex);
      Statement stmt = null;
      HashMap<String, Float> response = new HashMap<String, Float>();
      ArrayList<Float> faredata = new ArrayList<Float>();
      String query = "with aids as (select distinct agency_id as aid from gtfs_selected_feeds where username='"+username+"'), svcids as (";
      for (int i=0; i<date.length; i++){
    	  query+= "(select serviceid_agencyid, serviceid_id from gtfs_calendars gc inner join aids on gc.serviceid_agencyid = aids.aid where startdate::int<="+date[i]+
    			  " and enddate::int>="+date[i]+" and "+day[i]+" = 1 and serviceid_agencyid||serviceid_id not in (select serviceid_agencyid||serviceid_id from " +
    			  "gtfs_calendar_dates where date='"+date[i]+"' and exceptiontype=2) union select serviceid_agencyid, "+
    			  "serviceid_id from gtfs_calendar_dates gcd inner join aids on gcd.serviceid_agencyid = aids.aid where date='"+date[i]+"' and exceptiontype=1)";
    	  if (i+1<date.length)
				query+=" union all ";
		}      
    query +="), trips as (select trip.route_agencyid as aid, trip.route_id as routeid from svcids inner join gtfs_trips trip using(serviceid_agencyid, serviceid_id) inner join "+
    		Types.getTripMapTableName(type)+" map on trip.id = map.tripid and trip.agencyid = map.agencyid where map."+Types.getIdColumnName(type)+"='"+areaId+"'), fare as "+
    		"(select frule.route_agencyid as aid, frule.route_id as routeid, ftrb.price as price from gtfs_fare_rules frule inner join gtfs_fare_attributes ftrb on "+
    		"ftrb.agencyid= frule.fare_agencyid and ftrb.id=frule.fare_id inner join aids on aids.aid=ftrb.agencyid) select round(avg(price)::numeric,2) as fare from fare inner "+
    		"join trips using (aid,routeid) group by fare.routeid order by fare";
    //System.out.println(query);
      try {
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);        
        while ( rs.next() ) {
        	faredata.add(rs.getFloat("fare"));                      
        }
        rs.close();
        stmt.close();        
      } catch ( Exception e ) {
        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
         
      }
      dropConnection(connection);
      if (faredata.size()>0){
    	  Collections.sort(faredata);     
    	  response.put("minfare", faredata.get(0));
    	  response.put("maxfare", faredata.get(faredata.size()-1));    	  
		  if (faredata.size()%2==0){
				response.put("medianfare", (float)(Math.round((faredata.get(faredata.size()/2)+faredata.get((faredata.size()/2)-1))*100.00/2)/100.00));		
		  } else {
				response.put("medianfare", faredata.get((int)(Math.ceil(faredata.size()/2))));		
		  }
		  float faresum = 0;
	      for (int i=0; i<faredata.size();i++){
	    	  faresum+=faredata.get(i);
	      }
	      response.put("averagefare", (float)(Math.round(faresum*100.00/faredata.size())/100.00));
      } else {
    	  response.put("minfare", null);
    	  response.put("maxfare", null);
    	  response.put("medianfare", null);
    	  response.put("averagefare", null);
      }
      return response;
    }
	
	/** Queries all P&Rs nationwide grouped by county*/
	public static ParknRideCountiesList getCountiesPnrs(int dbindex){
		ParknRideCountiesList results = new ParknRideCountiesList();
		Connection connection = makeConnection(dbindex);
		Statement stmt = null;
		String querytext =	"SELECT pr.countyid, pr.county, count(pr.countyid) count, sum(pr.spaces) spaces, sum(pr.accessiblespaces) accessiblespaces " +
				"FROM parknride pr " + 
				"GROUP BY pr.countyid, pr.county;";
		try {
	        stmt = connection.createStatement();
	        ResultSet rs = stmt.executeQuery(querytext); 
	        List<ParknRideCounties> list=new ArrayList<ParknRideCounties>();
	        while ( rs.next() ) {
	        	ParknRideCounties instance = new ParknRideCounties();
	        	instance.countyId = rs.getString("countyid");
	        	instance.cname = rs.getString("county");
	        	instance.count = rs.getString("count");
	        	instance.spaces = rs.getString("spaces");
	        	instance.accessibleSpaces = rs.getString("accessiblespaces");
	        	list.add(instance);
	        }
//	        results.PnrCountiesList.add(instance);
	        rs.close();
	        stmt.close(); 
	        results.PnrCountiesList=list;
	      } catch ( Exception e ) {
	        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
	        e.getStackTrace();
	        // 
	      }
	      dropConnection(connection);
	      
	      return results;
	}
	
	/** Queries all detailed information on all the PnRs within a given county.
	 * 
	 */
	public static PnrInCountyList  getPnrsInCounty(int countyId, int radius, int dbindex, String username){
		PnrInCountyList results = new PnrInCountyList();
		String id = countyId+"";
		Connection connection = makeConnection(dbindex);
		Statement stmt = null;
		String query =	"WITH aids AS ("
						+ "SELECT agency_id AS aid "
						+ "FROM gtfs_selected_feeds "
						+ "WHERE username='"+username+"'), "
						
						+ "temp1 AS ("
						+ "SELECT parknride.*,"+
							"	gtfs_stops.name stopname, "+
							"	gtfs_stops.id stopid "+
							"FROM parknride CROSS JOIN gtfs_stops "+
							"WHERE ST_dwithin(parknride.geom, gtfs_stops.location, "+radius+") "+
							"ORDER BY pnrid), "+
			
							"temp2 AS (SELECT temp1.*, gtfs_stop_route_map.agencyid , gtfs_stop_route_map.routeid "+
							"FROM temp1 CROSS JOIN gtfs_stop_route_map "+
							"WHERE temp1.stopid=gtfs_stop_route_map.stopid), "+
			
							"temp3 AS (SELECT temp2.*, gtfs_routes.longname routename, gtfs_routes.agencyid agenid "+
							"FROM temp2 CROSS JOIN gtfs_routes "+
							"WHERE temp2.routeid = gtfs_routes.id), "
							+ "temp4 AS ("
							+ "	SELECT temp3.* "
							+ "	FROM temp3 INNER JOIN aids "
							+ "	ON temp3.agenid = aids.aid "
							+ "	)," +
							
							"temp5 AS (SELECT temp4.*, gtfs_agencies.name agencyname "+
							"FROM temp4 CROSS JOIN gtfs_agencies "+
							"WHERE temp4.agenid=gtfs_agencies.id), "+
			
							"temp6 AS (SELECT pnrid,  "+
							"	lotname, "+
							"	location, "+
							"	city, "+
							"	zipcode, "+
							"	spaces,  "+
							"	accessiblespaces, "+
							"	lat, "+
							"	lon, "+
							"	bikerackspaces, "+
							"	bikelockerspaces,  "+
							"	electricvehiclespaces, "+ 
							"	carsharing, "+
							"	transitservice, "+
							"	availability, "+
							"	timelimit, "+
							"	restroom, "+
							"	benches, "+
							"	shelter, "+
							"	indoorwaitingarea, "+ 
							"	trashcan, "+
							"	lighting, "+
							"	securitycameras, "+
							"	sidewalks, "+
							"	pnrsignage, "+
							"	lotsurface, "+
							"	propertyowner, "+
							"	localexpert, "+
							"	county, "+
							"   countyid, "+
							"	array_agg(agencyname) agencies,"
							+ "	array_agg(stopid) stopids,"
							+ "	array_agg(stopname) stopnames,"
							+ "	array_agg(routeid) routeids,"
							+ "	array_agg(routename) routenames,"
							+ "array_agg(stopname) stops, "+
							" count(stopname) count "+
							"FROM temp5 "+
							"GROUP BY pnrid, "+
							"lotname, "+
							"	location, "+
							"	city, "+
							"	zipcode, "+
							"	spaces, "+
							"	accessiblespaces, "+
							"	lat, "+
							"	lon, "+
							"	bikerackspaces, "+
							"	bikelockerspaces, "+
							"	electricvehiclespaces, "+
							"	carsharing, "+
							"	transitservice, "+
							"	availability, "+
							"	timelimit, "+
							"	restroom, "+
							"	benches, "+
							"	shelter, "+
							"	indoorwaitingarea,"+
							"	trashcan, "+
							"	lighting, "+
							"	securitycameras, "+
							"	sidewalks, "+
							"	pnrsignage, "+
							"	lotsurface,"+
							"	propertyowner, "+
							"	localexpert, "+
							"	county, "+
							"   countyid),"
							+ " temp7 AS (SELECT parknride.*, temp6.agencies, temp6.stopids, temp6.stopnames, temp6.routeids, temp6.routenames FROM parknride LEFT OUTER JOIN temp6"
							+ "	ON temp6.pnrid = parknride.pnrid)"
							
							+ " SELECT * FROM temp7"
							+ " WHERE countyid='"+id+"'"
							+ "	ORDER BY pnrid;";
		
		try {
	        stmt = connection.createStatement();
	        ResultSet rs = stmt.executeQuery(query); 
	        List<PnrInCounty> list=new ArrayList<PnrInCounty>();
	        while ( rs.next() ) {
	        	PnrInCounty instance = new PnrInCounty();
	        	instance.pnrid = rs.getString("pnrid");
	        	instance.lotname = rs.getString("lotname");
	        	instance.city = rs.getString("city");
	        	instance.zipcode=rs.getString("zipcode");
	        	instance.location = rs.getString("location");	  
	        	if (rs.getString("spaces").equals("0"))
	        		instance.spaces = "N/A";
	        	else
	        		instance.spaces =instance.spaces = rs.getString("spaces");	
	        	if (rs.getString("accessiblespaces").equals("0"))
	        		instance.accessiblespaces = "N/A";
	        	else 
	        		instance.accessiblespaces = rs.getString("accessiblespaces");	
	        	instance.transitservices = rs.getString("transitservice");
	        	if (rs.getString("lat").length()>8)
	        		instance.lat = rs.getString("lat").substring(0,7);
	        	else
	        		instance.lat = rs.getString("lat");
	        	if (rs.getString("lon").length()>10)
	        		instance.lon = rs.getString("lon").substring(0,9);
	        	else
	        		instance.lon = rs.getString("lon");
	        	if (rs.getString("bikerackspaces").equals("0"))
	        		instance.bikerackspaces = "N/A";
	        	else
	        		instance.bikerackspaces = rs.getString("bikerackspaces");
	        	if (rs.getString("bikelockerspaces").equals("0"))
	        		instance.bikelockerspaces = "N/A";
	        	else
	        		instance.bikelockerspaces = rs.getString("bikelockerspaces");
	        	if (rs.getString("electricvehiclespaces").equals("0"))
	        		instance.electricvehiclespaces = "N/A";
	        	else
	        		instance.electricvehiclespaces = rs.getString("electricvehiclespaces");
	        	instance.carsharing = rs.getString("carsharing");
	        	instance.availability = rs.getString("availability");
	        	instance.timelimit = rs.getString("timelimit");
	        	instance.restroom = rs.getString("restroom");
	        	instance.benches = rs.getString("benches");
	        	instance.shelter = rs.getString("shelter");
	        	instance.indoorwaitingarea = rs.getString("indoorwaitingarea");
	        	instance.trashcan = rs.getString("trashcan");
	        	instance.lighting = rs.getString("lighting");
	        	instance.securitycameras = rs.getString("securitycameras");
	        	instance.sidewalks = rs.getString("sidewalks");
	        	instance.pnrsignage = rs.getString("pnrsignage");
	        	instance.lotsurface = rs.getString("lotsurface");
	        	instance.propertyowner = rs.getString("propertyowner");
	        	instance.localexpert = rs.getString("localexpert");
	        	instance.county = rs.getString("county");
	        	if (rs.getString("routenames")==null){
	        		instance.agencies = "N/A";
	        		instance.stopids = "N/A";
	        		instance.stopnames = "N/A";
	        		instance.routeids = "N/A";
	        		instance.routenames = "N/A";
	        	}else{
	        		instance.agencies = rs.getString("agencies");
		        	instance.stopids = rs.getString("stopids");
		        	instance.stopnames = rs.getString("stopnames");
		        	instance.routeids = rs.getString("routeids");
		        	instance.routenames = rs.getString("routenames");
		        		 
	        	}
	        	list.add(instance);
	        }
	        rs.close();
	        stmt.close(); 
	        results.PnrCountiesList=list;
	      } catch ( Exception e ) {
	        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
	        e.getStackTrace();
	        // 
	      }
	      dropConnection(connection);
		
		return results;
	}
	
	/**
	 * Queries the list of all agencies' IDs in the database
	 */
	public static List<String> getAgencyList(String username, int dbindex){
		List<String> result = new ArrayList<String>();
		Connection connection = makeConnection(dbindex);
	    Statement stmt = null;
	    String query = "with aids AS (SELECT agency_id AS aid FROM gtfs_selected_feeds where username='" + username + "') "
	    		+ "SELECT id AS agencyid FROM gtfs_agencies INNER JOIN aids ON gtfs_agencies.defaultid = aids.aid";
	    try {
	    	stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query); 
			
			while (rs.next()){
				result.add(rs.getString("agencyid"));
			}
	    } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    dropConnection(connection);
	    return result;		
	}
	
	/**
	 * Queries employment data on a given area (based on the reportType)
	 */
	public static EmpDataList getEmpData(String projection, String DS, String reportType, String[] dates, String[] day, String[] fulldates, double radius, int L, int dbindex, String username){
		String projectionYear = "";
		EmpDataList results = new EmpDataList();
		Connection connection = makeConnection(dbindex);
	    Statement stmt = null;
	    String query = "";
	    
	    if (reportType.equals("Agencies")){ // returns employment data for a specific agency.
    		query = "with aids as (select agency_id as aid from gtfs_selected_feeds where username='"+username+"'), svcids as (";
	    	for (int i=0; i<dates.length; i++){
	  	    	  query+= "(select serviceid_agencyid, serviceid_id, '"+fulldates[i]+"' as day from gtfs_calendars gc inner join aids on gc.serviceid_agencyid = aids.aid where "
	  	    	  		+ "startdate::int<="+dates[i]+" and enddate::int>="+dates[i]+" and "+day[i]+" = 1 and serviceid_agencyid||serviceid_id not in (select "
	  	    	  		+ "serviceid_agencyid||serviceid_id from gtfs_calendar_dates where date='"+dates[i]+"' and exceptiontype=2) union select serviceid_agencyid, serviceid_id, '"
	  	    	  		+ fulldates[i] + "' from gtfs_calendar_dates gcd inner join aids on gcd.serviceid_agencyid = aids.aid where date='"+dates[i]+"' and exceptiontype=1)";
	  	    	  if (i+1<dates.length)
	  					query+=" union all ";
	  			} 
	    	query += "), trips as (select trip.agencyid as aid, trip.id as tripid, trip.route_id as routeid, round((map.length)::numeric,2) as length, map.tlength as tlength, map.stopscount as stops "
			+"from svcids inner join gtfs_trips trip using(serviceid_agencyid, serviceid_id) "
			+"inner join census_counties_trip_map map on trip.id = map.tripid and trip.agencyid = map.agencyid), "
	    	+ "stops as (select stime.trip_agencyid as aid, stime.stop_id as stopid, stop.location as location, min(stime.arrivaltime) as arrival, max(stime.departuretime) as departure, count(trips.aid) as service "
			+"from gtfs_stops stop inner join gtfs_stop_times stime on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id "
			+"inner join trips on stime.trip_agencyid =trips.aid and stime.trip_id=trips.tripid where stime.arrivaltime>0 and stime.departuretime>0	and stop.agencyid IN (SELECT aid FROM aids) "
			+"group by stime.trip_agencyid, stime.stop_agencyid, stime.stop_id, stop.location), "
		
		+"stopsatlostemp as (select stime.stop_agencyid as aid, stime.stop_id as stopid, stop.location as location, count(trips.aid) as service "
		+"from gtfs_stops stop inner join gtfs_stop_times stime on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id "
		+"inner join trips on stime.trip_agencyid =trips.aid and stime.trip_id=trips.tripid group by stime.stop_agencyid, stime.stop_id, stop.location having count(trips.aid)>="+L+"), "
		+"stopsatlos0 AS (select map.agencyid, stopsatlostemp.stopid, stopsatlostemp.location, stopsatlostemp.service "
		+"FROM stopsatlostemp INNER JOIN gtfs_stop_service_map AS map ON stopsatlostemp.stopid = map.stopid AND stopsatlostemp.aid = map.agencyid_def), "
		+"popatlos as (select c000,ca01,ca02,ca03,ce01,ce02,ce03,cns01,cns02,cns03,cns04,cns05,cns06,cns07,cns08,cns09,cns10,cns11,cns12,cns13,cns14,cns15,cns16,cns17,cns18,cns19, "
		+"cns20,cr01,cr02,cr03,cr04,cr05,cr07,ct01,ct02,cd01,cd02,cd03,cd04,cs01,cs02,rac.blockid,stopsatlos0.agencyid "
		+"from "+DS+" rac inner join stopsatlos0 on st_dwithin(rac.location,stopsatlos0.location,"+radius+") GROUP BY rac.blockid,agencyid), "
		+"popatlos1 as (select sum(c000) AS c000los,sum(ca01) AS ca01los,sum(ca02) AS ca02los,sum(ca03) AS ca03los,sum(ce01) AS ce01los,  "
		+"sum(ce02) AS ce02los,sum(ce03) AS ce03los,sum(cns01) AS cns01los,sum(cns02) AS cns02los,sum(cns03) AS cns03los,"
		+ "sum(cns04) AS cns04los,sum(cns05) AS cns05los,sum(cns06) AS cns06los,sum(cns07) AS cns07los,sum(cns08) AS cns08los,"
		+ "sum(cns09) AS cns09los,sum(cns10) AS cns10los,sum(cns11) AS cns11los,sum(cns12) AS cns12los,sum(cns13) AS cns13los,"
		+ "sum(cns14) AS cns14los,sum(cns15) AS cns15los,sum(cns16) AS cns16los,sum(cns17) AS cns17los,sum(cns18) AS cns18los,"
		+ "sum(cns19) AS cns19los,sum(cns20) AS cns20los,sum(cr01) AS cr01los,sum(cr02) AS cr02los,sum(cr03) AS cr03los,"
		+ "sum(cr04) AS cr04los,sum(cr05) AS cr05los,sum(cr07) AS cr07los,sum(ct01) AS ct01los,sum(ct02) AS ct02los,"
		+ "sum(cd01) AS cd01los,sum(cd02) AS cd02los,sum(cd03) AS cd03los,sum(cd04) AS cd04los,sum(cs01) AS cs01los,sum(cs02) AS cs02los, agencyid AS aid "
		+"FROM popatlos GROUP BY agencyid), "
		+"tempstops0 as (select id, agencyid, blockid, location "
		+"	from gtfs_stops stop inner join aids on stop.agencyid = aids.aid), " 
		+"tempstops AS (SELECT tempstops0.id, map.agencyid, tempstops0.blockid, tempstops0.location " 
			+"FROM tempstops0 INNER JOIN  gtfs_stop_service_map AS map ON tempstops0.id = map.stopid AND tempstops0.agencyid = map.agencyid_def), " 
		+"census as (select block.blockid, block.urbanid, block.regionid, block.congdistid, block.placeid "
		+ "from census_blocks block inner join tempstops on st_dwithin(block.location, tempstops.location, "+radius+") group by block.blockid), "
		+"popwithinx0 as (select c000,ca01,ca02,ca03,ce01,ce02,ce03,cns01,cns02,cns03,cns04,cns05,cns06,cns07,cns08,cns09,cns10,cns11,cns12,"
		+ "cns13,cns14,cns15,cns16,cns17,cns18,cns19,cns20,cr01,cr02,cr03,cr04,cr05,cr07,ct01,ct02,cd01,cd02,cd03,cd04,cs01,cs02, "+DS+".blockid, agencyid "
		+"from tempstops INNER JOIN "+DS+" ON ST_Dwithin("+DS+".location, tempstops.location, "+radius+") GROUP BY agencyid, "+DS+".blockid), "
		+"popwithinx as (select sum(c000) AS c000withinx,sum(ca01) AS ca01withinx,sum(ca02) AS ca02withinx,sum(ca03) AS ca03withinx,sum(ce01) AS ce01withinx,"
		+ "sum(ce02) AS ce02withinx,sum(ce03) AS ce03withinx,sum(cns01) AS cns01withinx,sum(cns02) AS cns02withinx,sum(cns03) AS cns03withinx,"
		+ "sum(cns04) AS cns04withinx,sum(cns05) AS cns05withinx,sum(cns06) AS cns06withinx,sum(cns07) AS cns07withinx,sum(cns08) AS cns08withinx,"
		+ "sum(cns09) AS cns09withinx,sum(cns10) AS cns10withinx,sum(cns11) AS cns11withinx,sum(cns12) AS cns12withinx,sum(cns13) AS cns13withinx,"
		+ "sum(cns14) AS cns14withinx,sum(cns15) AS cns15withinx,sum(cns16) AS cns16withinx,sum(cns17) AS cns17withinx,sum(cns18) AS cns18withinx,"
		+ "sum(cns19) AS cns19withinx,sum(cns20) AS cns20withinx,sum(cr01) AS cr01withinx,sum(cr02) AS cr02withinx,sum(cr03) AS cr03withinx,"
		+ "sum(cr04) AS cr04withinx,sum(cr05) AS cr05withinx,sum(cr07) AS cr07withinx,sum(ct01) AS ct01withinx,sum(ct02) AS ct02withinx,"
		+ "sum(cd01) AS cd01withinx,sum(cd02) AS cd02withinx,sum(cd03) AS cd03withinx,sum(cd04) AS cd04withinx,sum(cs01) AS cs01withinx,"
		+ "sum(cs02) AS cs02withinx,agencyid AS aid "
			+"FROM popwithinx0 GROUP BY agencyid), "
		+"popserved0 AS(select aid, "
		 		+"MAX(service) AS maxservice, rac.blockid "
				+"from stops inner join "+DS+" AS rac ON st_dwithin(rac.location, stops.location,"+radius+") "
				+"GROUP BY aid, blockid), "
		+"popserved AS (SELECT  "
			+"  aid,  "
			+"SUM(maxservice*c000) c000served,"
			  +"SUM(maxservice*ca01) ca01served,"
			  +"SUM(maxservice*ca02) ca02served,"
			  +"SUM(maxservice*ca03) ca03served,"
			  +"SUM(maxservice*ce01) ce01served,"
			  +"SUM(maxservice*ce02) ce02served,"
			  +"SUM(maxservice*ce03) ce03served,"
			  +"SUM(maxservice*cns01) cns01served,"
			  +"SUM(maxservice*cns02) cns02served,"
			  +"SUM(maxservice*cns03) cns03served,"
			  +"SUM(maxservice*cns04) cns04served,"
			  +"SUM(maxservice*cns05) cns05served,"
			  +"SUM(maxservice*cns06) cns06served,"
			  +"SUM(maxservice*cns07) cns07served,"
			  +"SUM(maxservice*cns08) cns08served,"
			  +"SUM(maxservice*cns09) cns09served,"
			  +"SUM(maxservice*cns10) cns10served,"
			  +"SUM(maxservice*cns11) cns11served,"
			  +"SUM(maxservice*cns12) cns12served,"
			  +"SUM(maxservice*cns13) cns13served,"
			  +"SUM(maxservice*cns14) cns14served,"
			  +"SUM(maxservice*cns15) cns15served,"
			  +"SUM(maxservice*cns16) cns16served,"
			  +"SUM(maxservice*cns17) cns17served,"
			  +"SUM(maxservice*cns18) cns18served,"
			  +"SUM(maxservice*cns19) cns19served,"
			  +"SUM(maxservice*cns20) cns20served,"
			  +"SUM(maxservice*cr01) cr01served,"
			  +"SUM(maxservice*cr02) cr02served,"
			  +"SUM(maxservice*cr03) cr03served,"
			  +"SUM(maxservice*cr04) cr04served,"
			  +"SUM(maxservice*cr05) cr05served,"
			  +"SUM(maxservice*cr07) cr07served,"
			  +"SUM(maxservice*ct01) ct01served,"
			  +"SUM(maxservice*ct02) ct02served,"
			  +"SUM(maxservice*cd01) cd01served,"
			  +"SUM(maxservice*cd02) cd02served,"
			  +"SUM(maxservice*cd03) cd03served,"
			  +"SUM(maxservice*cd04) cd04served,"
			  +"SUM(maxservice*cs01) cs01served,"
			  +"SUM(maxservice*cs02) cs02served "
			+"FROM popserved0 INNER JOIN "+DS+" USING(blockid) GROUP BY aid) " 
		+"SELECT agencies.name AS agency_name, agencies.id AS agency_id, popserved.*, popatlos1.*, popwithinx.* " 
		+"from gtfs_agencies AS agencies LEFT JOIN popwithinx ON agencies.id=aid "
		+"LEFT JOIN popatlos1 USING(aid) "
		+"LEFT JOIN popserved USING(aid) "
		+ "WHERE agencies.defaultid IN (SELECT aid FROM aids) "
		+"ORDER BY agencies.id ";
	    System.out.println(query);
		    	
	    	try {
				stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query); 
		    	while (rs.next()){
		    		EmpData i = new EmpData();
		    		i.id = rs.getString("agency_id");
		    		i.name = rs.getString("agency_name");
		    		i.c000within = rs.getInt("c000withinx");
					i.ca01within = rs.getInt("ca01withinx");
					i.ca02within = rs.getInt("ca02withinx");
					i.ca03within = rs.getInt("ca03withinx");
					i.ce01within = rs.getInt("ce01withinx");
					i.ce02within = rs.getInt("ce02withinx");
					i.ce03within = rs.getInt("ce03withinx");				
					i.cns01within = rs.getInt("cns01withinx");
					i.cns02within = rs.getInt("cns02withinx");
					i.cns03within = rs.getInt("cns03withinx");
					i.cns04within = rs.getInt("cns04withinx");
					i.cns05within = rs.getInt("cns05withinx");
					i.cns06within = rs.getInt("cns06withinx");
					i.cns07within = rs.getInt("cns07withinx");
					i.cns08within = rs.getInt("cns08withinx");
					i.cns09within = rs.getInt("cns09withinx");
					i.cns10within = rs.getInt("cns10withinx");
					i.cns11within = rs.getInt("cns11withinx");
					i.cns12within = rs.getInt("cns12withinx");
					i.cns13within = rs.getInt("cns13withinx");
					i.cns14within = rs.getInt("cns14withinx");
					i.cns15within = rs.getInt("cns15withinx");
					i.cns16within = rs.getInt("cns16withinx");
					i.cns17within = rs.getInt("cns17withinx");
					i.cns18within = rs.getInt("cns18withinx");
					i.cns19within = rs.getInt("cns19withinx");
					i.cns20within = rs.getInt("cns20withinx");
					i.cr01within = rs.getInt("cr01withinx");
					i.cr02within = rs.getInt("cr02withinx");
					i.cr03within = rs.getInt("ce03withinx");	
					i.cr04within = rs.getInt("cr04withinx");
					i.cr05within = rs.getInt("cr05withinx");
					i.cr07within = rs.getInt("cr07withinx");	
					i.ct01within = rs.getInt("ct01withinx");
					i.ct02within = rs.getInt("ct02withinx");
					i.cd01within = rs.getInt("cd01withinx");
					i.cd02within = rs.getInt("cd02withinx");
					i.cd03within = rs.getInt("cd03withinx");
					i.cd04within = rs.getInt("cd04withinx");
					i.cs01within = rs.getInt("cs01withinx");
					i.cs02within = rs.getInt("cs02withinx");
					i.c000served = rs.getInt("c000served");
					i.ca01served = rs.getInt("ca01served");
					i.ca02served = rs.getInt("ca02served");
					i.ca03served = rs.getInt("ca03served");
					i.ce01served = rs.getInt("ce01served");
					i.ce02served = rs.getInt("ce02served");
					i.ce03served = rs.getInt("ce03served");				
					i.cns01served = rs.getInt("cns01served");
					i.cns02served = rs.getInt("cns02served");
					i.cns03served = rs.getInt("cns03served");
					i.cns04served = rs.getInt("cns04served");
					i.cns05served = rs.getInt("cns05served");
					i.cns06served = rs.getInt("cns06served");
					i.cns07served = rs.getInt("cns07served");
					i.cns08served = rs.getInt("cns08served");
					i.cns09served = rs.getInt("cns09served");
					i.cns10served = rs.getInt("cns10served");
					i.cns11served = rs.getInt("cns11served");
					i.cns12served = rs.getInt("cns12served");
					i.cns13served = rs.getInt("cns13served");
					i.cns14served = rs.getInt("cns14served");
					i.cns15served = rs.getInt("cns15served");
					i.cns16served = rs.getInt("cns16served");
					i.cns17served = rs.getInt("cns17served");
					i.cns18served = rs.getInt("cns18served");
					i.cns19served = rs.getInt("cns19served");
					i.cns20served = rs.getInt("cns20served");
					i.cr01served = rs.getInt("cr01served");
					i.cr02served = rs.getInt("cr02served");
					i.cr03served = rs.getInt("ce03served");	
					i.cr04served = rs.getInt("cr04served");
					i.cr05served = rs.getInt("cr05served");
					i.cr07served = rs.getInt("cr07served");	
					i.ct01served = rs.getInt("ct01served");
					i.ct02served = rs.getInt("ct02served");
					i.cd01served = rs.getInt("cd01served");
					i.cd02served = rs.getInt("cd02served");
					i.cd03served = rs.getInt("cd03served");
					i.cd04served = rs.getInt("cd04served");
					i.cs01served = rs.getInt("cs01served");
					i.cs02served = rs.getInt("cs02served");
					i.c000los = rs.getInt("c000los");
					i.ca01los = rs.getInt("ca01los");
					i.ca02los = rs.getInt("ca02los");
					i.ca03los = rs.getInt("ca03los");
					i.ce01los = rs.getInt("ce01los");
					i.ce02los = rs.getInt("ce02los");
					i.ce03los = rs.getInt("ce03los");				
					i.cns01los = rs.getInt("cns01los");
					i.cns02los = rs.getInt("cns02los");
					i.cns03los = rs.getInt("cns03los");
					i.cns04los = rs.getInt("cns04los");
					i.cns05los = rs.getInt("cns05los");
					i.cns06los = rs.getInt("cns06los");
					i.cns07los = rs.getInt("cns07los");
					i.cns08los = rs.getInt("cns08los");
					i.cns09los = rs.getInt("cns09los");
					i.cns10los = rs.getInt("cns10los");
					i.cns11los = rs.getInt("cns11los");
					i.cns12los = rs.getInt("cns12los");
					i.cns13los = rs.getInt("cns13los");
					i.cns14los = rs.getInt("cns14los");
					i.cns15los = rs.getInt("cns15los");
					i.cns16los = rs.getInt("cns16los");
					i.cns17los = rs.getInt("cns17los");
					i.cns18los = rs.getInt("cns18los");
					i.cns19los = rs.getInt("cns19los");
					i.cns20los = rs.getInt("cns20los");
					i.cr01los = rs.getInt("cr01los");
					i.cr02los = rs.getInt("cr02los");
					i.cr03los = rs.getInt("ce03los");	
					i.cr04los = rs.getInt("cr04los");
					i.cr05los = rs.getInt("cr05los");
					i.cr07los = rs.getInt("cr07los");	
					i.ct01los = rs.getInt("ct01los");
					i.ct02los = rs.getInt("ct02los");
					i.cd01los = rs.getInt("cd01los");
					i.cd02los = rs.getInt("cd02los");
					i.cd03los = rs.getInt("cd03los");
					i.cd04los = rs.getInt("cd04los");
					i.cs01los = rs.getInt("cs01los");
					i.cs02los = rs.getInt("cs02los");					
					results.EmpDataList.add(i);
		    	}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		dropConnection(connection);
	    }else{
	        String criteria1 = "";
			String criteria2 = ""; 
			String criteria3 = "";
			String criteria4 = "";
			String criteria5 = "";
		    if (reportType.equals("Counties")){
		    	criteria1 = "LEFT(blockid,5)";
				criteria2 = "census_counties"; 
				criteria3 = "census_counties"; 
				criteria4 = "countyid";
				criteria5 = "census_counties.cname";
		    }else if(reportType.equals("Census Places")){
		    	criteria1 = "placeid";
				criteria2 = "census_places"; 
				criteria3 = "census_places";
				criteria4 = "placeid";
				criteria5 = "census_places.pname";
		    }else if(reportType.equals("Congressional Districts")){
		    	criteria1 = "congdistid";
				criteria2 = "census_congdists"; 
				criteria3 = "census_congdists";
				criteria4 = "congdistid";
				criteria5 = "census_congdists.cname";
		    }else if(reportType.equals("Urban Areas")){
		    	criteria1 = "urbanid";
				criteria2 = "census_urbans"; 
				criteria3 = "census_urbans";
				criteria4 = "urbanid";
				criteria5 = "census_urbans.uname";
		    }else if(reportType.equals("ODOT Transit Regions")){
		    	criteria1 = "regionid";
		    	criteria2 = "regions";
				criteria3 = "(SELECT odotregionid AS regionid, SUM(population) AS population FROM census_counties GROUP BY odotregionid) AS regions"; 
				criteria4 = "regionid";
				criteria5 = "'Region ' || regions.regionid";
		    }
		    
		    // Checks to see whether current data or projection data is queried 
		    if (!projection.equals("current")) {
		    	DS = "lodes_rac_projection_block"; projectionYear = "_" + projection;
		    }
		    
		    query = "with aids as (select agency_id as aid from gtfs_selected_feeds where username='admin'), svcids AS (";
		    for (int i=0; i<dates.length; i++){
  	    	  query+= "(select serviceid_agencyid, serviceid_id, '"+fulldates[i]+"' as day from gtfs_calendars gc inner join aids on gc.serviceid_agencyid = aids.aid where "
  	    	  		+ "startdate::int<="+dates[i]+" and enddate::int>="+dates[i]+" and "+day[i]+" = 1 and serviceid_agencyid||serviceid_id not in (select "
  	    	  		+ "serviceid_agencyid||serviceid_id from gtfs_calendar_dates where date='"+dates[i]+"' and exceptiontype=2) union select serviceid_agencyid, serviceid_id, '"
  	    	  		+ fulldates[i] + "' from gtfs_calendar_dates gcd inner join aids on gcd.serviceid_agencyid = aids.aid where date='"+dates[i]+"' and exceptiontype=1)";
  	    	  if (i+1<dates.length)
  					query+=" union all ";
  			} 
		    query+="), trips as (select trip.agencyid as aid, trip.id as tripid, trip.route_id as routeid, round((map.length)::numeric,2) as length, map.tlength as tlength, map.stopscount as stops "
				+"	from svcids inner join gtfs_trips trip using(serviceid_agencyid, serviceid_id) "
				+"	inner join census_counties_trip_map map on trip.id = map.tripid and trip.agencyid = map.agencyid), "
				
				+"service as (select COALESCE(+ sum(length),0) as svcmiles, COALESCE(+ sum(tlength),0) as svchours, COALESCE(+ sum(stops),0) as svcstops from trips), "
				+"stopsatlos as (select stime.stop_agencyid as aid, stime.stop_id as stopid, stop.location as location, count(trips.aid) as service "
				+"	from gtfs_stops stop inner join gtfs_stop_times stime on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id "
				+"	inner join trips on stime.trip_agencyid =trips.aid and stime.trip_id=trips.tripid group by stime.stop_agencyid, stime.stop_id, stop.location having count(trips.aid)>=" + L + "), "
				+"stops as (select stime.stop_agencyid as aid, stime.stop_id as stopid, stop.location as location, min(stime.arrivaltime) as arrival, max(stime.departuretime) as departure, count(trips.aid) as service "
				+"	from gtfs_stops stop inner join gtfs_stop_times stime on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id "
				+"	inner join trips on stime.trip_agencyid =trips.aid and stime.trip_id=trips.tripid where stime.arrivaltime>0 and stime.departuretime>0 "
				+"	group by stime.stop_agencyid, stime.stop_id, stop.location), "				
				+"popatlos as (select "
				+"	c000" + projectionYear + ","
				+"	ca01" + projectionYear + ","
				+"	ca02" + projectionYear + ","
				+"	ca03" + projectionYear + ","
				+"	ce01" + projectionYear + ","
				+"	ce02" + projectionYear + ","
				+"	ce03" + projectionYear + ","
				+"	cns01" + projectionYear + ","
				+"	cns02" + projectionYear + ","
				+"	cns03" + projectionYear + ","
				+"	cns04" + projectionYear + ","
				+"	cns05" + projectionYear + ","
				+"	cns06" + projectionYear + ","
				+"	cns07" + projectionYear + ","
				+"	cns08" + projectionYear + ","
				+"	cns09" + projectionYear + ","
				+"	cns10" + projectionYear + ","
				+"	cns11" + projectionYear + ","
				+"	cns12" + projectionYear + ","
				+"	cns13" + projectionYear + ","
				+"	cns14" + projectionYear + ","
				+"	cns15" + projectionYear + ","
				+"	cns16" + projectionYear + ","
				+"	cns17" + projectionYear + ","
				+"	cns18" + projectionYear + ","
				+"	cns19" + projectionYear + ","
				+"	cns20" + projectionYear + ","
				+"	cr01" + projectionYear + ","
				+"	cr02" + projectionYear + ","
				+"	cr03" + projectionYear + ","
				+"	cr04" + projectionYear + ","
				+"	cr05" + projectionYear + ","
				+"	cr07" + projectionYear + ","
				+"	ct01" + projectionYear + ","
				+"	ct02" + projectionYear + ","
				+"	cd01" + projectionYear + ","
				+"	cd02" + projectionYear + ","
				+"	cd03" + projectionYear + ","
				+"	cd04" + projectionYear + ","
				+"	cs01" + projectionYear + ","
				+"	cs02" + projectionYear + ","
				+ DS + ".blockid, blocks.urbanid, blocks.regionid, blocks.congdistid, blocks.placeid "
				+"	from "+ DS + " inner join stopsatlos on st_dwithin(" + DS + ".location,stopsatlos.location,"+radius+")"
				+"	inner join census_blocks blocks ON " + DS + ".blockid = blocks.blockid "
				+ "GROUP BY " + DS + ".blockid, blocks.urbanid, blocks.regionid, blocks.congdistid, blocks.placeid), "
				+"popatlos1 as (select "
				+ "sum(c000" + projectionYear + ") AS c000los, "
				+ "sum(ca01" + projectionYear + ") AS ca01los, "
				+ "sum(ca02" + projectionYear + ") AS ca02los, "
				+ "sum(ca03" + projectionYear + ") AS ca03los, "
				+ "sum(ce01" + projectionYear + ") AS ce01los, "
				+ "sum(ce02" + projectionYear + ") AS ce02los, "
				+ "sum(ce03" + projectionYear + ") AS ce03los, "
				+ "sum(cns01" + projectionYear + ") AS cns01los, "
				+ "sum(cns02" + projectionYear + ") AS cns02los, "
				+ "sum(cns03" + projectionYear + ") AS cns03los, "
				+ "sum(cns04" + projectionYear + ") AS cns04los, "
				+ "sum(cns05" + projectionYear + ") AS cns05los, "
				+ "sum(cns06" + projectionYear + ") AS cns06los, "
				+ "sum(cns07" + projectionYear + ") AS cns07los, "
				+ "sum(cns08" + projectionYear + ") AS cns08los, "
				+ "sum(cns09" + projectionYear + ") AS cns09los, "
				+ "sum(cns10" + projectionYear + ") AS cns10los, "
				+ "sum(cns11" + projectionYear + ") AS cns11los, "
				+ "sum(cns12" + projectionYear + ") AS cns12los, "
				+ "sum(cns13" + projectionYear + ") AS cns13los, "
				+ "sum(cns14" + projectionYear + ") AS cns14los, "
				+ "sum(cns15" + projectionYear + ") AS cns15los, "
				+ "sum(cns16" + projectionYear + ") AS cns16los, "
				+ "sum(cns17" + projectionYear + ") AS cns17los, "
				+ "sum(cns18" + projectionYear + ") AS cns18los, "
				+ "sum(cns19" + projectionYear + ") AS cns19los, "
				+ "sum(cns20" + projectionYear + ") AS cns20los, "
				+ "sum(cr01" + projectionYear + ") AS cr01los, "
				+ "sum(cr02" + projectionYear + ") AS cr02los, "
				+ "sum(cr03" + projectionYear + ") AS cr03los, "
				+ "sum(cr04" + projectionYear + ") AS cr04los, "
				+ "sum(cr05" + projectionYear + ") AS cr05los, "
				+ "sum(cr07" + projectionYear + ") AS cr07los, "
				+ "sum(ct01" + projectionYear + ") AS ct01los, "
				+ "sum(ct02" + projectionYear + ") AS ct02los, "
				+ "sum(cd01" + projectionYear + ") AS cd01los, "
				+ "sum(cd02" + projectionYear + ") AS cd02los, "
				+ "sum(cd03" + projectionYear + ") AS cd03los, "
				+ "sum(cd04" + projectionYear + ") AS cd04los, "
				+ "sum(cs01" + projectionYear + ") AS cs01los, "
				+ "sum(cs02" + projectionYear + ") AS cs02los, "
				+ criteria1 + " AS " + criteria4 + " FROM popatlos GROUP BY " + criteria1 + "), "
				+ "popserved as (select "
				+ " c000" + projectionYear + "*(stops.service) as c000served, "
				 + " ca01" + projectionYear + "*(stops.service) as ca01served, "
				 + " ca02" + projectionYear + "*(stops.service) as ca02served, "
				 + " ca03" + projectionYear + "*(stops.service) as ca03served, "
				 + " ce01" + projectionYear + "*(stops.service) as ce01served, "
				 + " ce02" + projectionYear + "*(stops.service) as ce02served, "
				 + " ce03" + projectionYear + "*(stops.service) as ce03served, "
				 + " cns01" + projectionYear + "*(stops.service) as cns01served, "
				 + " cns02" + projectionYear + "*(stops.service) as cns02served, "
				 + " cns03" + projectionYear + "*(stops.service) as cns03served, "
				 + " cns04" + projectionYear + "*(stops.service) as cns04served, "
				 + " cns05" + projectionYear + "*(stops.service) as cns05served, "
				 + " cns06" + projectionYear + "*(stops.service) as cns06served, "
				 + " cns07" + projectionYear + "*(stops.service) as cns07served, "
				 + " cns08" + projectionYear + "*(stops.service) as cns08served, "
				 + " cns09" + projectionYear + "*(stops.service) as cns09served, "
				 + " cns10" + projectionYear + "*(stops.service) as cns10served, "
				 + " cns11" + projectionYear + "*(stops.service) as cns11served, "
				 + " cns12" + projectionYear + "*(stops.service) as cns12served, "
				 + " cns13" + projectionYear + "*(stops.service) as cns13served, "
				 + " cns14" + projectionYear + "*(stops.service) as cns14served, "
				 + " cns15" + projectionYear + "*(stops.service) as cns15served, "
				 + " cns16" + projectionYear + "*(stops.service) as cns16served, "
				 + " cns17" + projectionYear + "*(stops.service) as cns17served, "
				 + " cns18" + projectionYear + "*(stops.service) as cns18served, "
				 + " cns19" + projectionYear + "*(stops.service) as cns19served, "
				 + " cns20" + projectionYear + "*(stops.service) as cns20served, "
				 + " cr01" + projectionYear + "*(stops.service) as cr01served, "
				 + " cr02" + projectionYear + "*(stops.service) as cr02served, "
				 + " cr03" + projectionYear + "*(stops.service) as cr03served, "
				 + " cr04" + projectionYear + "*(stops.service) as cr04served, "
				 + " cr05" + projectionYear + "*(stops.service) as cr05served, "
				 + " cr07" + projectionYear + "*(stops.service) as cr07served, "
				 + " ct01" + projectionYear + "*(stops.service) as ct01served, "
				 + " ct02" + projectionYear + "*(stops.service) as ct02served, "
				 + " cd01" + projectionYear + "*(stops.service) as cd01served, "
				 + " cd02" + projectionYear + "*(stops.service) as cd02served, "
				 + " cd03" + projectionYear + "*(stops.service) as cd03served, "
				 + " cd04" + projectionYear + "*(stops.service) as cd04served, "
				 + " cs01" + projectionYear + "*(stops.service) as cs01served, "
				 + " cs02" + projectionYear + "*(stops.service) as cs02served, "
				+ "	blocks.blockid, census_blocks.urbanid, census_blocks.regionid, census_blocks.congdistid, census_blocks.placeid  "
				+ "	from " + DS + " blocks inner join stops on st_dwithin(blocks.location, stops.location,"+radius+") "
				+ "	inner join census_blocks ON blocks.blockid=census_blocks.blockid "
				+ "	GROUP BY stops.service, blocks.blockid,census_blocks.urbanid, census_blocks.regionid, census_blocks.congdistid, census_blocks.placeid), "
				+ " popserved1 as (select "
				+ "sum(c000served) AS c000served, "
				+ "sum(ca01served) AS ca01served, "
				+ "sum(ca02served) AS ca02served, "
				+ "sum(ca03served) AS ca03served, "
				+ "sum(ce01served) AS ce01served, "
				+ "sum(ce02served) AS ce02served, "
				+ "sum(ce03served) AS ce03served, "
				+ "sum(cns01served) AS cns01served, "
				+ "sum(cns02served) AS cns02served, "
				+ "sum(cns03served) AS cns03served, "
				+ "sum(cns04served) AS cns04served, "
				+ "sum(cns05served) AS cns05served, "
				+ "sum(cns06served) AS cns06served, "
				+ "sum(cns07served) AS cns07served, "
				+ "sum(cns08served) AS cns08served, "
				+ "sum(cns09served) AS cns09served, "
				+ "sum(cns10served) AS cns10served, "
				+ "sum(cns11served) AS cns11served, "
				+ "sum(cns12served) AS cns12served, "
				+ "sum(cns13served) AS cns13served, "
				+ "sum(cns14served) AS cns14served, "
				+ "sum(cns15served) AS cns15served, "
				+ "sum(cns16served) AS cns16served, "
				+ "sum(cns17served) AS cns17served, "
				+ "sum(cns18served) AS cns18served, "
				+ "sum(cns19served) AS cns19served, "
				+ "sum(cns20served) AS cns20served, "
				+ "sum(cr01served) AS cr01served, "
				+ "sum(cr02served) AS cr02served, "
				+ "sum(cr03served) AS cr03served, "
				+ "sum(cr04served) AS cr04served, "
				+ "sum(cr05served) AS cr05served, "
				+ "sum(cr07served) AS cr07served, "
				+ "sum(ct01served) AS ct01served, "
				+ "sum(ct02served) AS ct02served, "
				+ "sum(cd01served) AS cd01served, "
				+ "sum(cd02served) AS cd02served, "
				+ "sum(cd03served) AS cd03served, "
				+ "sum(cd04served) AS cd04served, "
				+ "sum(cs01served) AS cs01served, "
				+ "sum(cs02served) AS cs02served, "
				+ criteria1 + " AS " + criteria4 + " from popserved GROUP BY " + criteria1 + "), "
				+ "tempstops as (select id, agencyid, blockid, location from gtfs_stops stop inner join aids on stop.agencyid = aids.aid),  "
				+ "census as (select block.blockid, block.urbanid, block.regionid, block.congdistid, block.placeid  "
				+ "from census_blocks block inner join tempstops on st_dwithin(block.location, tempstops.location, "+radius+")  "
				+ "group by block.blockid),  "
				+ "popwithinx as (select  "
				+ "sum(c000" + projectionYear + ") AS c000withinx, "
				+ "sum(ca01" + projectionYear + ") AS ca01withinx, "
				+ "sum(ca02" + projectionYear + ") AS ca02withinx, "
				+ "sum(ca03" + projectionYear + ") AS ca03withinx, "
				+ "sum(ce01" + projectionYear + ") AS ce01withinx, "
				+ "sum(ce02" + projectionYear + ") AS ce02withinx, "
				+ "sum(ce03" + projectionYear + ") AS ce03withinx, "
				+ "sum(cns01" + projectionYear + ") AS cns01withinx, "
				+ "sum(cns02" + projectionYear + ") AS cns02withinx, "
				+ "sum(cns03" + projectionYear + ") AS cns03withinx, "
				+ "sum(cns04" + projectionYear + ") AS cns04withinx, "
				+ "sum(cns05" + projectionYear + ") AS cns05withinx, "
				+ "sum(cns06" + projectionYear + ") AS cns06withinx, "
				+ "sum(cns07" + projectionYear + ") AS cns07withinx, "
				+ "sum(cns08" + projectionYear + ") AS cns08withinx, "
				+ "sum(cns09" + projectionYear + ") AS cns09withinx, "
				+ "sum(cns10" + projectionYear + ") AS cns10withinx, "
				+ "sum(cns11" + projectionYear + ") AS cns11withinx, "
				+ "sum(cns12" + projectionYear + ") AS cns12withinx, "
				+ "sum(cns13" + projectionYear + ") AS cns13withinx, "
				+ "sum(cns14" + projectionYear + ") AS cns14withinx, "
				+ "sum(cns15" + projectionYear + ") AS cns15withinx, "
				+ "sum(cns16" + projectionYear + ") AS cns16withinx, "
				+ "sum(cns17" + projectionYear + ") AS cns17withinx, "
				+ "sum(cns18" + projectionYear + ") AS cns18withinx, "
				+ "sum(cns19" + projectionYear + ") AS cns19withinx, "
				+ "sum(cns20" + projectionYear + ") AS cns20withinx, "
				+ "sum(cr01" + projectionYear + ") AS cr01withinx, "
				+ "sum(cr02" + projectionYear + ") AS cr02withinx, "
				+ "sum(cr03" + projectionYear + ") AS cr03withinx, "
				+ "sum(cr04" + projectionYear + ") AS cr04withinx, "
				+ "sum(cr05" + projectionYear + ") AS cr05withinx, "
				+ "sum(cr07" + projectionYear + ") AS cr07withinx, "
				+ "sum(ct01" + projectionYear + ") AS ct01withinx, "
				+ "sum(ct02" + projectionYear + ") AS ct02withinx, "
				+ "sum(cd01" + projectionYear + ") AS cd01withinx, "
				+ "sum(cd02" + projectionYear + ") AS cd02withinx, "
				+ "sum(cd03" + projectionYear + ") AS cd03withinx, "
				+ "sum(cd04" + projectionYear + ") AS cd04withinx, "
				+ "sum(cs01" + projectionYear + ") AS cs01withinx, "
				+ "sum(cs02" + projectionYear + ") AS cs02withinx, "
				+ criteria1 + " AS " + criteria4 + " FROM census INNER JOIN " + DS + " USING(blockid) GROUP BY " + criteria4 + "), "
			
				+ "totalpop AS (SELECT " + DS + ".*, LEFT(" + DS + ".blockid,5) AS countyid, census_blocks.urbanid, census_blocks.regionid, census_blocks.congdistid, census_blocks.placeid "
				+ "	FROM " + DS + " INNER JOIN census_blocks USING(blockid)), "
				+ "totalpop1 AS (SELECT sum(c000" + projectionYear + ") AS c000, "
				+ "sum(ca01" + projectionYear + ") AS ca01,"
				+ "sum(ca02" + projectionYear + ") AS ca02,"
				+ "sum(ca03" + projectionYear + ") AS ca03,"
				+ "sum(ce01" + projectionYear + ") AS ce01,"
				+ "sum(ce02" + projectionYear + ") AS ce02,"
				+ "sum(ce03" + projectionYear + ") AS ce03,"
				+ "sum(cns01" + projectionYear + ") AS cns01,"
				+ "sum(cns02" + projectionYear + ") AS cns02,"
				+ "sum(cns03" + projectionYear + ") AS cns03,"
				+ "sum(cns04" + projectionYear + ") AS cns04,"
				+ "sum(cns05" + projectionYear + ") AS cns05,"
				+ "sum(cns06" + projectionYear + ") AS cns06,"
				+ "sum(cns07" + projectionYear + ") AS cns07,"
				+ "sum(cns08" + projectionYear + ") AS cns08,"
				+ "sum(cns09" + projectionYear + ") AS cns09,"
				+ "sum(cns10" + projectionYear + ") AS cns10,"
				+ "sum(cns11" + projectionYear + ") AS cns11,"
				+ "sum(cns12" + projectionYear + ") AS cns12,"
				+ "sum(cns13" + projectionYear + ") AS cns13,"
				+ "sum(cns14" + projectionYear + ") AS cns14,"
				+ "sum(cns15" + projectionYear + ") AS cns15,"
				+ "sum(cns16" + projectionYear + ") AS cns16,"
				+ "sum(cns17" + projectionYear + ") AS cns17,"
				+ "sum(cns18" + projectionYear + ") AS cns18,"
				+ "sum(cns19" + projectionYear + ") AS cns19,"
				+ "sum(cns20" + projectionYear + ") AS cns20,"
				+ "sum(cr01" + projectionYear + ") AS cr01,"
				+ "sum(cr02" + projectionYear + ") AS cr02,"
				+ "sum(cr03" + projectionYear + ") AS cr03,"
				+ "sum(cr04" + projectionYear + ") AS cr04,"
				+ "sum(cr05" + projectionYear + ") AS cr05,"
				+ "sum(cr07" + projectionYear + ") AS cr07,"
				+ "sum(ct01" + projectionYear + ") AS ct01,"
				+ "sum(ct02" + projectionYear + ") AS ct02,"
				+ "sum(cd01" + projectionYear + ") AS cd01,"
				+ "sum(cd02" + projectionYear + ") AS cd02,"
				+ "sum(cd03" + projectionYear + ") AS cd03,"
				+ "sum(cd04" + projectionYear + ") AS cd04,"
				+ "sum(cs01" + projectionYear + ") AS cs01,"
				+ "sum(cs02" + projectionYear + ") AS cs02,"
				+ criteria4
				+ " FROM totalpop GROUP BY " + criteria4 + ") "
				+ "select popserved1.*, popatlos1.*, popwithinx.*, totalpop1.*, " + criteria2 + "." + criteria4 + " AS areaid, " + criteria5 + " AS areaname "
				+ "FROM " + criteria3 + " LEFT JOIN popserved1 USING(" + criteria4 + ") "
				+ "LEFT JOIN popatlos1 USING(" + criteria4 + ") "
				+ "LEFT JOIN popwithinx USING(" + criteria4 + ") "
				+ "LEFT JOIN totalpop1 USING(" + criteria4 + ") ";
						    System.out.println(query);
			    try {
			    	stmt = connection.createStatement();
					ResultSet rs = stmt.executeQuery(query); 
					
					while (rs.next()){
						EmpData i = new EmpData();
						i.id = rs.getString("areaid");
						i.name = rs.getString("areaname");
						i.c000 = rs.getInt("c000");
						i.ca01 = rs.getInt("ca01");
						i.ca02 = rs.getInt("ca02");
						i.ca03 = rs.getInt("ca03");
						i.ce01 = rs.getInt("ce01");
						i.ce02 = rs.getInt("ce02");
						i.ce03 = rs.getInt("ce03");				
						i.cns01 = rs.getInt("cns01");
						i.cns02 = rs.getInt("cns02");
						i.cns03 = rs.getInt("cns03");
						i.cns04 = rs.getInt("cns04");
						i.cns05 = rs.getInt("cns05");
						i.cns06 = rs.getInt("cns06");
						i.cns07 = rs.getInt("cns07");
						i.cns08 = rs.getInt("cns08");
						i.cns09 = rs.getInt("cns09");
						i.cns10 = rs.getInt("cns10");
						i.cns11 = rs.getInt("cns11");
						i.cns12 = rs.getInt("cns12");
						i.cns13 = rs.getInt("cns13");
						i.cns14 = rs.getInt("cns14");
						i.cns15 = rs.getInt("cns15");
						i.cns16 = rs.getInt("cns16");
						i.cns17 = rs.getInt("cns17");
						i.cns18 = rs.getInt("cns18");
						i.cns19 = rs.getInt("cns19");
						i.cns20 = rs.getInt("cns20");
						i.cr01 = rs.getInt("cr01");
						i.cr02 = rs.getInt("cr02");
						i.cr03 = rs.getInt("ce03");	
						i.cr04 = rs.getInt("cr04");
						i.cr05 = rs.getInt("cr05");
						i.cr07 = rs.getInt("cr07");	
						i.ct01 = rs.getInt("ct01");
						i.ct02 = rs.getInt("ct02");
						i.cd01 = rs.getInt("cd01");
						i.cd02 = rs.getInt("cd02");
						i.cd03 = rs.getInt("cd03");
						i.cd04 = rs.getInt("cd04");
						i.cs01 = rs.getInt("cs01");
						i.cs02 = rs.getInt("cs02");
						i.c000within = rs.getInt("c000withinx");
						i.ca01within = rs.getInt("ca01withinx");
						i.ca02within = rs.getInt("ca02withinx");
						i.ca03within = rs.getInt("ca03withinx");
						i.ce01within = rs.getInt("ce01withinx");
						i.ce02within = rs.getInt("ce02withinx");
						i.ce03within = rs.getInt("ce03withinx");				
						i.cns01within = rs.getInt("cns01withinx");
						i.cns02within = rs.getInt("cns02withinx");
						i.cns03within = rs.getInt("cns03withinx");
						i.cns04within = rs.getInt("cns04withinx");
						i.cns05within = rs.getInt("cns05withinx");
						i.cns06within = rs.getInt("cns06withinx");
						i.cns07within = rs.getInt("cns07withinx");
						i.cns08within = rs.getInt("cns08withinx");
						i.cns09within = rs.getInt("cns09withinx");
						i.cns10within = rs.getInt("cns10withinx");
						i.cns11within = rs.getInt("cns11withinx");
						i.cns12within = rs.getInt("cns12withinx");
						i.cns13within = rs.getInt("cns13withinx");
						i.cns14within = rs.getInt("cns14withinx");
						i.cns15within = rs.getInt("cns15withinx");
						i.cns16within = rs.getInt("cns16withinx");
						i.cns17within = rs.getInt("cns17withinx");
						i.cns18within = rs.getInt("cns18withinx");
						i.cns19within = rs.getInt("cns19withinx");
						i.cns20within = rs.getInt("cns20withinx");
						i.cr01within = rs.getInt("cr01withinx");
						i.cr02within = rs.getInt("cr02withinx");
						i.cr03within = rs.getInt("ce03withinx");	
						i.cr04within = rs.getInt("cr04withinx");
						i.cr05within = rs.getInt("cr05withinx");
						i.cr07within = rs.getInt("cr07withinx");	
						i.ct01within = rs.getInt("ct01withinx");
						i.ct02within = rs.getInt("ct02withinx");
						i.cd01within = rs.getInt("cd01withinx");
						i.cd02within = rs.getInt("cd02withinx");
						i.cd03within = rs.getInt("cd03withinx");
						i.cd04within = rs.getInt("cd04withinx");
						i.cs01within = rs.getInt("cs01withinx");
						i.cs02within = rs.getInt("cs02withinx");
						i.c000served = rs.getInt("c000served");
						i.ca01served = rs.getInt("ca01served");
						i.ca02served = rs.getInt("ca02served");
						i.ca03served = rs.getInt("ca03served");
						i.ce01served = rs.getInt("ce01served");
						i.ce02served = rs.getInt("ce02served");
						i.ce03served = rs.getInt("ce03served");				
						i.cns01served = rs.getInt("cns01served");
						i.cns02served = rs.getInt("cns02served");
						i.cns03served = rs.getInt("cns03served");
						i.cns04served = rs.getInt("cns04served");
						i.cns05served = rs.getInt("cns05served");
						i.cns06served = rs.getInt("cns06served");
						i.cns07served = rs.getInt("cns07served");
						i.cns08served = rs.getInt("cns08served");
						i.cns09served = rs.getInt("cns09served");
						i.cns10served = rs.getInt("cns10served");
						i.cns11served = rs.getInt("cns11served");
						i.cns12served = rs.getInt("cns12served");
						i.cns13served = rs.getInt("cns13served");
						i.cns14served = rs.getInt("cns14served");
						i.cns15served = rs.getInt("cns15served");
						i.cns16served = rs.getInt("cns16served");
						i.cns17served = rs.getInt("cns17served");
						i.cns18served = rs.getInt("cns18served");
						i.cns19served = rs.getInt("cns19served");
						i.cns20served = rs.getInt("cns20served");
						i.cr01served = rs.getInt("cr01served");
						i.cr02served = rs.getInt("cr02served");
						i.cr03served = rs.getInt("ce03served");	
						i.cr04served = rs.getInt("cr04served");
						i.cr05served = rs.getInt("cr05served");
						i.cr07served = rs.getInt("cr07served");	
						i.ct01served = rs.getInt("ct01served");
						i.ct02served = rs.getInt("ct02served");
						i.cd01served = rs.getInt("cd01served");
						i.cd02served = rs.getInt("cd02served");
						i.cd03served = rs.getInt("cd03served");
						i.cd04served = rs.getInt("cd04served");
						i.cs01served = rs.getInt("cs01served");
						i.cs02served = rs.getInt("cs02served");
						i.c000los = rs.getInt("c000los");
						i.ca01los = rs.getInt("ca01los");
						i.ca02los = rs.getInt("ca02los");
						i.ca03los = rs.getInt("ca03los");
						i.ce01los = rs.getInt("ce01los");
						i.ce02los = rs.getInt("ce02los");
						i.ce03los = rs.getInt("ce03los");				
						i.cns01los = rs.getInt("cns01los");
						i.cns02los = rs.getInt("cns02los");
						i.cns03los = rs.getInt("cns03los");
						i.cns04los = rs.getInt("cns04los");
						i.cns05los = rs.getInt("cns05los");
						i.cns06los = rs.getInt("cns06los");
						i.cns07los = rs.getInt("cns07los");
						i.cns08los = rs.getInt("cns08los");
						i.cns09los = rs.getInt("cns09los");
						i.cns10los = rs.getInt("cns10los");
						i.cns11los = rs.getInt("cns11los");
						i.cns12los = rs.getInt("cns12los");
						i.cns13los = rs.getInt("cns13los");
						i.cns14los = rs.getInt("cns14los");
						i.cns15los = rs.getInt("cns15los");
						i.cns16los = rs.getInt("cns16los");
						i.cns17los = rs.getInt("cns17los");
						i.cns18los = rs.getInt("cns18los");
						i.cns19los = rs.getInt("cns19los");
						i.cns20los = rs.getInt("cns20los");
						i.cr01los = rs.getInt("cr01los");
						i.cr02los = rs.getInt("cr02los");
						i.cr03los = rs.getInt("ce03los");	
						i.cr04los = rs.getInt("cr04los");
						i.cr05los = rs.getInt("cr05los");
						i.cr07los = rs.getInt("cr07los");	
						i.ct01los = rs.getInt("ct01los");
						i.ct02los = rs.getInt("ct02los");
						i.cd01los = rs.getInt("cd01los");
						i.cd02los = rs.getInt("cd02los");
						i.cd03los = rs.getInt("cd03los");
						i.cd04los = rs.getInt("cd04los");
						i.cs01los = rs.getInt("cs01los");
						i.cs02los = rs.getInt("cs02los");						
						results.EmpDataList.add(i);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    dropConnection(connection);
		}
	    return results;
	}
	
	/**
	 * Queries Title VI data on a given area (based on the reportType)
	 */
	public static TitleVIDataList getTitleVIData(String reportType, String[] dates, String[] day, String[] fulldates, double radius, int L, int dbindex, String username){
		TitleVIDataList results = new TitleVIDataList();
		Connection connection = makeConnection(dbindex);
	    Statement stmt = null;
	    String query = "";
	    
	    if (reportType.contains("Agencies")){ // returns title data for a agencies.
    		query = "with aids as (select agency_id as aid from gtfs_selected_feeds where username='"+username+"'), svcids as (";
    		for (int i=0; i<dates.length; i++){
  	    	  query+= "(select serviceid_agencyid, serviceid_id, '"+fulldates[i]+"' as day from gtfs_calendars gc inner join aids on gc.serviceid_agencyid = aids.aid where "
  	    	  		+ "startdate::int<="+dates[i]+" and enddate::int>="+dates[i]+" and "+day[i]+" = 1 and serviceid_agencyid||serviceid_id not in (select "
  	    	  		+ "serviceid_agencyid||serviceid_id from gtfs_calendar_dates where date='"+dates[i]+"' and exceptiontype=2) union select serviceid_agencyid, serviceid_id, '"
  	    	  		+ fulldates[i] + "' from gtfs_calendar_dates gcd inner join aids on gcd.serviceid_agencyid = aids.aid where date='"+dates[i]+"' and exceptiontype=1)";
  	    	  if (i+1<dates.length)
  					query+=" union all ";
  			} 
	    	query += "), trips as (select trip.agencyid as aid, trip.id as tripid, trip.route_id as routeid, round((map.length)::numeric,2) as length, map.tlength as tlength, map.stopscount as stops "
			+ " from svcids inner join gtfs_trips trip using(serviceid_agencyid, serviceid_id) "
			+ " inner join census_counties_trip_map map on trip.id = map.tripid and trip.agencyid = map.agencyid), "
	    	+ " stops as (select stime.trip_agencyid as aid, stime.stop_id as stopid, stop.location as location, min(stime.arrivaltime) as arrival, max(stime.departuretime) as departure, count(trips.aid) as service "
			+ " from gtfs_stops stop inner join gtfs_stop_times stime on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id "
			+ " inner join trips on stime.trip_agencyid =trips.aid and stime.trip_id=trips.tripid where stime.arrivaltime>0 and stime.departuretime>0 and stop.agencyid in (select aid from aids)	"
			+ " group by stime.trip_agencyid, stime.stop_agencyid, stime.stop_id, stop.location), "
			+ " stopsatlostemp as (select stime.stop_agencyid as aid, stime.stop_id as stopid, stop.location as location, count(trips.aid) as service "
			+ " from gtfs_stops stop inner join gtfs_stop_times stime on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id "
			+ "	inner join trips on stime.trip_agencyid =trips.aid and stime.trip_id=trips.tripid group by stime.stop_agencyid, stime.stop_id, stop.location having count(trips.aid)>="+L+"), "
			+ " stopsatlos0 AS (select map.agencyid, stopsatlostemp.stopid, stopsatlostemp.location, stopsatlostemp.service "
			+ " FROM stopsatlostemp INNER JOIN gtfs_stop_service_map AS map ON stopsatlostemp.stopid = map.stopid AND stopsatlostemp.aid = map.agencyid_def), "
			+ " popatlos as (select   "
			+ "	  english ,"
			+ "	  spanish ,"
			+ "	  indo_european ,"
			+ "	  asian_and_pacific_island ,"
			+ "	  other_languages ,"
			+ "	  below_poverty ,"
			+ "	  above_poverty ,"
			+ "	  with_disability ,"
			+ "	  without_disability ,"
			+ "	  from5to17 ,"
			+ "	  from18to64 ,"
			+ "	  above65 ,"
			+ "	  black_or_african_american ,"
			+ "	  american_indian_and_alaska_native ,"
			+ "	  asian ,"
			+ "	  native_hawaiian_and_other_pacific_islander ,"
			+ "	  other_races ,"
			+ "	  two_or_more ,"
			+ "	  white ,"
			+ "	  hispanic_or_latino ,"
			+ "	  t6.blockid,"
			+ "	  stopsatlos0.agencyid "
			+ " from title_vi_blocks_float AS t6 inner join stopsatlos0 on st_dwithin(t6.location,stopsatlos0.location,"+radius+") GROUP BY t6.blockid,agencyid), "
			+ " popatlos1 as (select  SUM(english) AS english_atlos,"
			+ "   SUM(spanish) AS spanish_atlos,"
			+ "	  SUM(indo_european) AS indo_european_atlos,"
			+ "	  SUM(asian_and_pacific_island) AS asian_and_pacific_island_atlos,"
			+ "	  SUM(other_languages) AS other_languages_atlos,"
			+ "	  SUM(below_poverty) AS below_poverty_atlos,"
			+ "	  SUM(above_poverty) AS above_poverty_atlos,"
			+ "	  SUM(with_disability) AS with_disability_atlos,"
			+ "	  SUM(without_disability) AS without_disability_atlos,"
			+ "	  SUM(from5to17) AS from5to17_atlos,"
			+ "	  SUM(from18to64) AS from18to64_atlos,"
			+ "	  SUM(above65) AS above65_atlos,"
			+ "	  SUM(black_or_african_american) AS black_or_african_american_atlos,"
			+ "	  SUM(american_indian_and_alaska_native) AS american_indian_and_alaska_native_atlos,"
			+ "	  SUM(asian) AS asian_atlos,"
			+ "	  SUM(native_hawaiian_and_other_pacific_islander) AS native_hawaiian_and_other_pacific_islander_atlos,"
			+ "	  SUM(other_races) AS other_races_atlos,"
			+ "	  SUM(two_or_more) AS two_or_more_atlos,"
			+ "	  SUM(white) AS white_atlos,"
			+ "	  SUM(hispanic_or_latino) AS hispanic_or_latino_atlos,"
			+ "	  agencyid AS aid "
			+ "	FROM popatlos GROUP BY agencyid),"
			+ " tempstops0 as (select id, agencyid, blockid, location "
			+ " from gtfs_stops stop inner join aids on stop.agencyid = aids.aid),"
			+ " tempstops AS (SELECT tempstops0.id, map.agencyid, tempstops0.blockid, tempstops0.location "
			+ "	FROM tempstops0 INNER JOIN  gtfs_stop_service_map AS map ON tempstops0.id = map.stopid AND tempstops0.agencyid = map.agencyid_def), "
			+ " census as (select block.blockid, block.urbanid, block.regionid, block.congdistid, block.placeid "
			+ "	from census_blocks block inner join tempstops on st_dwithin(block.location, tempstops.location, "+radius+")"
			+ "	group by block.blockid),"
			+ " popwithinx0 as (select english ,"
			+ "	  spanish ,"
			+ "	  indo_european ,"
			+ "	  asian_and_pacific_island ,"
			+ "	  other_languages ,"
			+ "  below_poverty ,"
			+ "	  above_poverty ,"
			+ "	  with_disability ,"
			+ "	  without_disability ,"
			+ "	  from5to17 ,"
			+ "	  from18to64 ,"
			+ "	  above65 ,"
			+ "	  black_or_african_american ,"
			+ "	  american_indian_and_alaska_native ,"
			+ "	  asian ,"
			+ "	  native_hawaiian_and_other_pacific_islander ,"
			+ "	  other_races ,"
			+ "	  two_or_more ,"
			+ "	  white ,"
			+ "	  hispanic_or_latino ,"
			+ "	  t6.blockid,"
			+ "	  agencyid "
			+ "from tempstops INNER JOIN title_vi_blocks_float AS t6 ON ST_Dwithin(t6.location, tempstops.location, "+radius+") GROUP BY agencyid, t6.blockid),"
			+ "popwithinx as (select SUM(english) AS english_withinx,"
			+ "	  SUM(spanish) AS spanish_withinx,"
			+ "	  SUM(indo_european) AS indo_european_withinx,"
			+ "	  SUM(asian_and_pacific_island) AS asian_and_pacific_island_withinx,"
			+ "	  SUM(other_languages) AS other_languages_withinx,"
			+ "	  SUM(below_poverty) AS below_poverty_withinx,"
			+ "	  SUM(above_poverty) AS above_poverty_withinx,"
			+ "	  SUM(with_disability) AS with_disability_withinx,"
			+ "	  SUM(without_disability) AS without_disability_withinx,"
			+ "	  SUM(from5to17) AS from5to17_withinx,"
			+ "	  SUM(from18to64) AS from18to64_withinx,"
			+ "	  SUM(above65) AS above65_withinx,"
			+ "	  SUM(black_or_african_american) AS black_or_african_american_withinx,"
			+ "	  SUM(american_indian_and_alaska_native) AS american_indian_and_alaska_native_withinx,"
			+ "	  SUM(asian) AS asian_withinx,"
			+ "	  SUM(native_hawaiian_and_other_pacific_islander) AS native_hawaiian_and_other_pacific_islander_withinx,"
			+ "	  SUM(other_races) AS other_races_withinx,"
			+ "	  SUM(two_or_more) AS two_or_more_withinx,"
			+ "	  SUM(white) AS white_withinx,"
			+ "	  SUM(hispanic_or_latino) AS hispanic_or_latino_withinx,"
			+ "	  agencyid AS aid"
			+ "	  FROM popwithinx0 GROUP BY agencyid),"
			+ "popserved0 AS(select aid,"
			+ " 		MAX(service) AS maxservice,"
			+ "		t6.blockid"
			+ "		from stops inner join title_vi_blocks_float AS t6 ON st_dwithin(t6.location, stops.location,"+radius+")"
			+ "		GROUP BY aid, blockid),"
			+ "popserved AS (SELECT "
			+ "	  aid,"
			+ "	  SUM(maxservice*english) AS english_served,"
			+ "	  SUM(maxservice*spanish) AS spanish_served,"
			+ "	  SUM(maxservice*indo_european) AS indo_european_served,"
			+ "	  SUM(maxservice*asian_and_pacific_island) AS asian_and_pacific_island_served,"
			+ "	  SUM(maxservice*other_languages) AS other_languages_served,"
			+ "	  SUM(maxservice*below_poverty) AS below_poverty_served,"
			+ "	  SUM(maxservice*above_poverty) AS above_poverty_served,"
			+ "	  SUM(maxservice*with_disability) AS with_disability_served,"
			+ "	  SUM(maxservice*without_disability) AS without_disability_served,"
			+ "	  SUM(maxservice*from5to17) AS from5to17_served,"
			+ "	  SUM(maxservice*from18to64) AS from18to64_served,"
			+ "	  SUM(maxservice*above65) AS above65_served,"
			+ "	  SUM(maxservice*black_or_african_american) AS black_or_african_american_served,"
			+ "	  SUM(maxservice*american_indian_and_alaska_native) AS american_indian_and_alaska_native_served,"
			+ "	  SUM(maxservice*asian) AS asian_served,"
			+ "	  SUM(maxservice*native_hawaiian_and_other_pacific_islander) AS native_hawaiian_and_other_pacific_islander_served,"
			+ "	  SUM(maxservice*other_races) AS other_races_served,"
			+ "	  SUM(maxservice*two_or_more) AS two_or_more_served,"
			+ "	  SUM(maxservice*white) AS white_served,"
			+ "	  SUM(maxservice*hispanic_or_latino) AS hispanic_or_latino_served "
			+ "	FROM popserved0 INNER JOIN title_vi_blocks_float USING(blockid) GROUP BY aid) "
			+ "SELECT agencies.name AS agency_name, agencies.id AS agency_id, popserved.*, popatlos1.*, popwithinx.* "
			+ "from gtfs_agencies AS agencies LEFT JOIN popwithinx ON agencies.id=aid "
			+ "LEFT JOIN popatlos1 USING(aid) "			
			+ "LEFT JOIN popserved USING(aid) "
			+ "WHERE agencies.defaultid IN (SELECT aid FROM aids) "
			+ "ORDER BY agencies.id ";
//	    	System.out.println(query);
	    	}else{
		    	String criteria1 = "";
				String criteria2 = ""; 
				String criteria3 = "";
				String criteria4 = "";
				String criteria5 = "";
			    if (reportType.equals("Counties")){
			    	criteria1 = "LEFT(blockid,5)";
					criteria2 = "census_counties"; 
					criteria3 = "census_counties"; 
					criteria4 = "countyid";
					criteria5 = "census_counties.cname";
			    }else if(reportType.equals("Census Places")){
			    	criteria1 = "placeid";
					criteria2 = "census_places"; 
					criteria3 = "census_places";
					criteria4 = "placeid";
					criteria5 = "census_places.pname";
			    }else if(reportType.equals("Congressional Districts")){
			    	criteria1 = "congdistid";
					criteria2 = "census_congdists"; 
					criteria3 = "census_congdists";
					criteria4 = "congdistid";
					criteria5 = "census_congdists.cname";
			    }else if(reportType.equals("Urban Areas")){
			    	criteria1 = "urbanid";
					criteria2 = "census_urbans"; 
					criteria3 = "census_urbans";
					criteria4 = "urbanid";
					criteria5 = "census_urbans.uname";
			    }else if(reportType.equals("ODOT Transit Regions")){
			    	criteria1 = "regionid";
			    	criteria2 = "regions";
					criteria3 = "(SELECT odotregionid AS regionid, SUM(population) AS population FROM census_counties GROUP BY odotregionid) AS regions"; 
					criteria4 = "regionid";
					criteria5 = "'Region ' || regions.regionid";
			    }
			    
			    query = "with aids as (select agency_id as aid from gtfs_selected_feeds where username='admin'), svcids AS (";
			    for (int i=0; i<dates.length; i++){
	  	    	  query+= "(select serviceid_agencyid, serviceid_id, '"+fulldates[i]+"' as day from gtfs_calendars gc inner join aids on gc.serviceid_agencyid = aids.aid where "
	  	    	  		+ "startdate::int<="+dates[i]+" and enddate::int>="+dates[i]+" and "+day[i]+" = 1 and serviceid_agencyid||serviceid_id not in (select "
	  	    	  		+ "serviceid_agencyid||serviceid_id from gtfs_calendar_dates where date='"+dates[i]+"' and exceptiontype=2) union select serviceid_agencyid, serviceid_id, '"
	  	    	  		+ fulldates[i] + "' from gtfs_calendar_dates gcd inner join aids on gcd.serviceid_agencyid = aids.aid where date='"+dates[i]+"' and exceptiontype=1)";
	  	    	  if (i+1<dates.length)
	  					query+=" union all ";
	  			} 
			    query+="), trips as (select trip.agencyid as aid, trip.id as tripid, trip.route_id as routeid, round((map.length)::numeric,2) as length, map.tlength as tlength, map.stopscount as stops "
					+"	from svcids inner join gtfs_trips trip using(serviceid_agencyid, serviceid_id) "
					+"	inner join census_counties_trip_map map on trip.id = map.tripid and trip.agencyid = map.agencyid), "
					
					+"service as (select COALESCE(+ sum(length),0) as svcmiles, COALESCE(+ sum(tlength),0) as svchours, COALESCE(+ sum(stops),0) as svcstops from trips), "
					+"stopsatlos as (select stime.stop_agencyid as aid, stime.stop_id as stopid, stop.location as location, count(trips.aid) as service "
					+"	from gtfs_stops stop inner join gtfs_stop_times stime on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id "
					+"	inner join trips on stime.trip_agencyid =trips.aid and stime.trip_id=trips.tripid group by stime.stop_agencyid, stime.stop_id, stop.location having count(trips.aid)>=" + L + "), "
					+"stops as (select stime.stop_agencyid as aid, stime.stop_id as stopid, stop.location as location, min(stime.arrivaltime) as arrival, max(stime.departuretime) as departure, count(trips.aid) as service "
					+"	from gtfs_stops stop inner join gtfs_stop_times stime on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id "
					+"	inner join trips on stime.trip_agencyid =trips.aid and stime.trip_id=trips.tripid where stime.arrivaltime>0 and stime.departuretime>0 "
					+"	group by stime.stop_agencyid, stime.stop_id, stop.location), "
					+"popatlos as (select "
					+ "	  english ,"
					+ "	  spanish ,"
					+ "	  indo_european ,"
					+ "	  asian_and_pacific_island ,"
					+ "	  other_languages ,"
					+ "   below_poverty ,"
					+ "	  above_poverty ,"
					+ "	  with_disability ,"
					+ "	  without_disability ,"
					+ "	  from5to17 ,"
					+ "	  from18to64 ,"
					+ "	  above65 ,"
					+ "	  black_or_african_american ,"
					+ "	  american_indian_and_alaska_native ,"
					+ "	  asian ,"
					+ "	  native_hawaiian_and_other_pacific_islander ,"
					+ "	  other_races ,"
					+ "	  two_or_more ,"
					+ "	  white ,"
					+ "	  hispanic_or_latino ,"
					+ " title_vi_blocks_float.blockid, blocks.urbanid, blocks.regionid, blocks.congdistid, blocks.placeid "
					+"	from title_vi_blocks_float inner join stopsatlos on st_dwithin(title_vi_blocks_float.location,stopsatlos.location,"+radius+")"
					+"	inner join census_blocks blocks ON title_vi_blocks_float.blockid = blocks.blockid GROUP BY title_vi_blocks_float.blockid, blocks.urbanid, blocks.regionid, blocks.congdistid, blocks.placeid), "
					+" popatlos1 as (select "
					+ "   SUM(english) AS english_atlos,"
					+ "   SUM(spanish) AS spanish_atlos,"
					+ "	  SUM(indo_european) AS indo_european_atlos,"
					+ "	  SUM(asian_and_pacific_island) AS asian_and_pacific_island_atlos,"
					+ "	  SUM(other_languages) AS other_languages_atlos,"
					+ "	  SUM(below_poverty) AS below_poverty_atlos,"
					+ "	  SUM(above_poverty) AS above_poverty_atlos,"
					+ "	  SUM(with_disability) AS with_disability_atlos,"
					+ "	  SUM(without_disability) AS without_disability_atlos,"
					+ "	  SUM(from5to17) AS from5to17_atlos,"
					+ "	  SUM(from18to64) AS from18to64_atlos,"
					+ "	  SUM(above65) AS above65_atlos,"
					+ "	  SUM(black_or_african_american) AS black_or_african_american_atlos,"
					+ "	  SUM(american_indian_and_alaska_native) AS american_indian_and_alaska_native_atlos,"
					+ "	  SUM(asian) AS asian_atlos,"
					+ "	  SUM(native_hawaiian_and_other_pacific_islander) AS native_hawaiian_and_other_pacific_islander_atlos,"
					+ "	  SUM(other_races) AS other_races_atlos,"
					+ "	  SUM(two_or_more) AS two_or_more_atlos,"
					+ "	  SUM(white) AS white_atlos,"
					+ "	  SUM(hispanic_or_latino) AS hispanic_or_latino_atlos,"
					+ criteria1 + " AS " + criteria4 + " FROM popatlos GROUP BY " + criteria1 + "), "
					+ "popserved as (select "
					+ "	  english*(stops.service) AS english,"
					+ "	  spanish*(stops.service) AS spanish,"
					+ "	  indo_european*(stops.service) AS indo_european,"
					+ "	  asian_and_pacific_island*(stops.service) AS asian_and_pacific_island,"
					+ "	  other_languages*(stops.service) AS other_languages,"
					+ "	  below_poverty*(stops.service) AS below_poverty,"
					+ "	  above_poverty*(stops.service) AS above_poverty,"
					+ "	  with_disability*(stops.service) AS with_disability,"
					+ "	  without_disability*(stops.service) AS without_disability,"
					+ "	  from5to17*(stops.service) AS from5to17,"
					+ "	  from18to64*(stops.service) AS from18to64,"
					+ "	  above65*(stops.service) AS above65,"
					+ "	  black_or_african_american*(stops.service) AS black_or_african_american,"
					+ "	  american_indian_and_alaska_native*(stops.service) AS american_indian_and_alaska_native,"
					+ "	  asian*(stops.service) AS asian,"
					+ "	  native_hawaiian_and_other_pacific_islander*(stops.service) AS native_hawaiian_and_other_pacific_islander,"
					+ "	  other_races*(stops.service) AS other_races,"
					+ "	  two_or_more*(stops.service) AS two_or_more,"
					+ "	  white*(stops.service) AS white,"
					+ "	  hispanic_or_latino*(stops.service) AS hispanic_or_latino, "
					+ "	blocks.blockid, census_blocks.urbanid, census_blocks.regionid, census_blocks.congdistid, census_blocks.placeid  "
					+ "	from title_vi_blocks_float blocks inner join stops on st_dwithin(blocks.location, stops.location,"+radius+") "
					+ "	inner join census_blocks ON blocks.blockid=census_blocks.blockid "
					+ "	group by stops.service, blocks.blockid,census_blocks.urbanid, census_blocks.regionid, census_blocks.congdistid, census_blocks.placeid), "
					+ " popserved1 as (select "
					+ "SUM(english) AS english_served,"
					+ "	  SUM(spanish) AS spanish_served,"
					+ "	  SUM(indo_european) AS indo_european_served,"
					+ "	  SUM(asian_and_pacific_island) AS asian_and_pacific_island_served,"
					+ "	  SUM(other_languages) AS other_languages_served,"
					+ "	  SUM(below_poverty) AS below_poverty_served,"
					+ "	  SUM(above_poverty) AS above_poverty_served,"
					+ "	  SUM(with_disability) AS with_disability_served,"
					+ "	  SUM(without_disability) AS without_disability_served,"
					+ "	  SUM(from5to17) AS from5to17_served,"
					+ "	  SUM(from18to64) AS from18to64_served,"
					+ "	  SUM(above65) AS above65_served,"
					+ "	  SUM(black_or_african_american) AS black_or_african_american_served,"
					+ "	  SUM(american_indian_and_alaska_native) AS american_indian_and_alaska_native_served,"
					+ "	  SUM(asian) AS asian_served,"
					+ "	  SUM(native_hawaiian_and_other_pacific_islander) AS native_hawaiian_and_other_pacific_islander_served,"
					+ "	  SUM(other_races) AS other_races_served,"
					+ "	  SUM(two_or_more) AS two_or_more_served,"
					+ "	  SUM(white) AS white_served,"
					+ "	  SUM(hispanic_or_latino) AS hispanic_or_latino_served,"
					+ criteria1 + " AS " + criteria4 + " from popserved GROUP BY " + criteria1 + "), "
					+ "tempstops as (select id, agencyid, blockid, location from gtfs_stops stop inner join aids on stop.agencyid = aids.aid),  "
					+ "census as (select block.blockid, block.urbanid, block.regionid, block.congdistid, block.placeid  "
					+ "from census_blocks block inner join tempstops on st_dwithin(block.location, tempstops.location, "+radius+")  "
					+ "group by block.blockid),  "
					+ "popwithinx as (select  "
					+ "	  SUM(english) AS english_withinx,"
					+ "	  SUM(spanish) AS spanish_withinx,"
					+ "	  SUM(indo_european) AS indo_european_withinx,"
					+ "	  SUM(asian_and_pacific_island) AS asian_and_pacific_island_withinx,"
					+ "	  SUM(other_languages) AS other_languages_withinx,"
					+ "	  SUM(below_poverty) AS below_poverty_withinx,"
					+ "	  SUM(above_poverty) AS above_poverty_withinx,"
					+ "	  SUM(with_disability) AS with_disability_withinx,"
					+ "	  SUM(without_disability) AS without_disability_withinx,"
					+ "	  SUM(from5to17) AS from5to17_withinx,"
					+ "	  SUM(from18to64) AS from18to64_withinx,"
					+ "	  SUM(above65) AS above65_withinx,"
					+ "	  SUM(black_or_african_american) AS black_or_african_american_withinx,"
					+ "	  SUM(american_indian_and_alaska_native) AS american_indian_and_alaska_native_withinx,"
					+ "	  SUM(asian) AS asian_withinx,"
					+ "	  SUM(native_hawaiian_and_other_pacific_islander) AS native_hawaiian_and_other_pacific_islander_withinx,"
					+ "	  SUM(other_races) AS other_races_withinx,"
					+ "	  SUM(two_or_more) AS two_or_more_withinx,"
					+ "	  SUM(white) AS white_withinx,"
					+ "	  SUM(hispanic_or_latino) AS hispanic_or_latino_withinx,"
					+ criteria1 + " AS " + criteria4 + " FROM census INNER JOIN title_vi_blocks_float USING(blockid) GROUP BY " + criteria4 + "), "
					
					+ "totalpop AS (SELECT title_vi_blocks_float.*, LEFT(title_vi_blocks_float.blockid,5) AS countyid, census_blocks.urbanid, census_blocks.regionid, census_blocks.congdistid, census_blocks.placeid "
					+ "	FROM title_vi_blocks_float INNER JOIN census_blocks USING(blockid)), "
					+ "totalpop1 AS (SELECT "
					+ "	  SUM(english) AS english,"
					+ "   SUM(spanish) AS spanish,"
					+ "	  SUM(indo_european) AS indo_european,"
					+ "	  SUM(asian_and_pacific_island) AS asian_and_pacific_island,"
					+ "	  SUM(other_languages) AS other_languages,"
					+ "	  SUM(below_poverty) AS below_poverty,"
					+ "	  SUM(above_poverty) AS above_poverty,"
					+ "	  SUM(with_disability) AS with_disability,"
					+ "	  SUM(without_disability) AS without_disability,"
					+ "	  SUM(from5to17) AS from5to17,"
					+ "	  SUM(from18to64) AS from18to64,"
					+ "	  SUM(above65) AS above65,"
					+ "	  SUM(black_or_african_american) AS black_or_african_american,"
					+ "	  SUM(american_indian_and_alaska_native) AS american_indian_and_alaska_native,"
					+ "	  SUM(asian) AS asian,"
					+ "	  SUM(native_hawaiian_and_other_pacific_islander) AS native_hawaiian_and_other_pacific_islander,"
					+ "	  SUM(other_races) AS other_races,"
					+ "	  SUM(two_or_more) AS two_or_more,"
					+ "	  SUM(white) AS white,"
					+ "	  SUM(hispanic_or_latino) AS hispanic_or_latino,"
					+ 	  criteria4
					+ " FROM totalpop GROUP BY " + criteria4 + ") "
					+ "select popserved1.*, popatlos1.*, popwithinx.*, totalpop1.*, " + criteria2 + "." + criteria4 + " AS areaid, " + criteria5 + " AS areaname "
					+ "FROM " + criteria3 + " LEFT JOIN popserved1 USING(" + criteria4 + ") "
					+ "LEFT JOIN popatlos1 USING(" + criteria4 + ") "
					+ "LEFT JOIN popwithinx USING(" + criteria4 + ") "
					+ "LEFT JOIN totalpop1 USING(" + criteria4 + ")";
//			    System.out.println(query);
			}
	
	    try {
	    	stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query); 		
			while (rs.next()){
				TitleVIData i = new TitleVIData();				
				if (!reportType.equals("Agencies")){
					i.id = rs.getString("areaid");
					i.name = rs.getString("areaname");
					i.english = rs.getInt("english");
					  i.spanish = rs.getInt("spanish");
					  i.indo_european = rs.getInt("indo_european");
					  i.asian_and_pacific_island = rs.getInt("asian_and_pacific_island");
					  i.other_languages = rs.getInt("other_languages");
					  i.below_poverty = rs.getInt("below_poverty");
					  i.above_poverty = rs.getInt("above_poverty");
					  i.with_disability = rs.getInt("with_disability");
					  i.without_disability = rs.getInt("without_disability");
					  i.from5to17 = rs.getInt("from5to17");
					  i.from18to64 = rs.getInt("from18to64");
					  i.above65 = rs.getInt("above65");
					  i.black_or_african_american = rs.getInt("black_or_african_american");
					  i.american_indian_and_alaska_native = rs.getInt("american_indian_and_alaska_native");
					  i.asian = rs.getInt("asian");
					  i.native_hawaiian_and_other_pacific_islander = rs.getInt("native_hawaiian_and_other_pacific_islander");
					  i.other_races = rs.getInt("other_races");
					  i.two_or_more = rs.getInt("two_or_more");
					  i.white = rs.getInt("white");
					  i.hispanic_or_latino = rs.getInt("hispanic_or_latino");
				}else{
					i.id = rs.getString("agency_id");
					i.name = rs.getString("agency_name");
				}
			  i.english_served = rs.getInt("english_served");
			  i.spanish_served = rs.getInt("spanish_served");
			  i.indo_european_served = rs.getInt("indo_european_served");
			  i.asian_and_pacific_island_served = rs.getInt("asian_and_pacific_island_served");
			  i.other_languages_served = rs.getInt("other_languages_served");
			  i.below_poverty_served = rs.getInt("below_poverty_served");
			  i.above_poverty_served = rs.getInt("above_poverty_served");
			  i.with_disability_served = rs.getInt("with_disability_served");
			  i.without_disability_served = rs.getInt("without_disability_served");
			  i.from5to17_served = rs.getInt("from5to17_served");
			  i.from18to64_served = rs.getInt("from18to64_served");
			  i.above65_served = rs.getInt("above65_served");
			  i.black_or_african_american_served = rs.getInt("black_or_african_american_served");
			  i.american_indian_and_alaska_native_served = rs.getInt("american_indian_and_alaska_native_served");
			  i.asian_served = rs.getInt("asian_served");
			  i.native_hawaiian_and_other_pacific_islander_served = rs.getInt("native_hawaiian_and_other_pacific_islander_served");
			  i.other_races_served = rs.getInt("other_races_served");
			  i.two_or_more_served = rs.getInt("two_or_more_served");
			  i.white_served = rs.getInt("white_served");
			  i.hispanic_or_latino_served = rs.getInt("hispanic_or_latino_served");
			  i.english_withinx = rs.getInt("english_withinx");
			  i.spanish_withinx = rs.getInt("spanish_withinx");
			  i.indo_european_withinx = rs.getInt("indo_european_withinx");
			  i.asian_and_pacific_island_withinx = rs.getInt("asian_and_pacific_island_withinx");
			  i.other_languages_withinx = rs.getInt("other_languages_withinx");
			  i.below_poverty_withinx = rs.getInt("below_poverty_withinx");
			  i.above_poverty_withinx = rs.getInt("above_poverty_withinx");
			  i.with_disability_withinx = rs.getInt("with_disability_withinx");
			  i.without_disability_withinx = rs.getInt("without_disability_withinx");
			  i.from5to17_withinx = rs.getInt("from5to17_withinx");
			  i.from18to64_withinx = rs.getInt("from18to64_withinx");
			  i.above65_withinx = rs.getInt("above65_withinx");
			  i.black_or_african_american_withinx = rs.getInt("black_or_african_american_withinx");
			  i.american_indian_and_alaska_native_withinx = rs.getInt("american_indian_and_alaska_native_withinx");
			  i.asian_withinx = rs.getInt("asian_withinx");
			  i.native_hawaiian_and_other_pacific_islander_withinx = rs.getInt("native_hawaiian_and_other_pacific_islander_withinx");
			  i.other_races_withinx = rs.getInt("other_races_withinx");
			  i.two_or_more_withinx = rs.getInt("two_or_more_withinx");
			  i.white_withinx = rs.getInt("white_withinx");
			  i.hispanic_or_latino_withinx = rs.getInt("hispanic_or_latino_withinx");
			  i.english_atlos = rs.getInt("english_atlos");
			  i.spanish_atlos = rs.getInt("spanish_atlos");
			  i.indo_european_atlos = rs.getInt("indo_european_atlos");
			  i.asian_and_pacific_island_atlos = rs.getInt("asian_and_pacific_island_atlos");
			  i.other_languages_atlos = rs.getInt("other_languages_atlos");
			  i.below_poverty_atlos = rs.getInt("below_poverty_atlos");
			  i.above_poverty_atlos = rs.getInt("above_poverty_atlos");
			  i.with_disability_atlos = rs.getInt("with_disability_atlos");
			  i.without_disability_atlos = rs.getInt("without_disability_atlos");
			  i.from5to17_atlos = rs.getInt("from5to17_atlos");
			  i.from18to64_atlos = rs.getInt("from18to64_atlos");
			  i.above65_atlos = rs.getInt("above65_atlos");
			  i.black_or_african_american_atlos = rs.getInt("black_or_african_american_atlos");
			  i.american_indian_and_alaska_native_atlos = rs.getInt("american_indian_and_alaska_native_atlos");
			  i.asian_atlos = rs.getInt("asian_atlos");
			  i.native_hawaiian_and_other_pacific_islander_atlos = rs.getInt("native_hawaiian_and_other_pacific_islander_atlos");
			  i.other_races_atlos = rs.getInt("other_races_atlos");
			  i.two_or_more_atlos = rs.getInt("two_or_more_atlos");
			  i.white_atlos = rs.getInt("white_atlos");
			  i.hispanic_or_latino_atlos = rs.getInt("hispanic_or_latino_atlos");
			  results.TitleVIDataList.add(i);
			}
		    dropConnection(connection);    	
			}catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    return results;
	}
	
	/**
	 *Queries Stops count, unduplicated urban pop and rural pop within x meters of all stops within the given geographic area
	 */
	public static long[] stopsPop(int type, String areaId, String username, double x, int dbindex, String popYear) 
    {	
	  Connection connection = makeConnection(dbindex);
      Statement stmt = null;
      String column = "";
      String criteria1 = "";
      String criteria2 = "";
      if (type==0){//county
    	  criteria1 = "left(blockid,5)";
    	  criteria2 = "left(block.blockid,5)";
    	  column = "blockid";
      } else if (type==1){//census tract
    	  criteria1 = "left(blockid,11)";
    	  criteria2 = "left(block.blockid,11)";
    	  column = "blockid";
      } else {// census place, urban area, ODOT region, or congressional district
    	  column = Types.getIdColumnName(type);
    	  criteria1 = Types.getIdColumnName(type);
    	  criteria2 = "block."+Types.getIdColumnName(type);
      }
      String querytext = "with aids as (select agency_id as aid from gtfs_selected_feeds where username='"+username+"'), stops as (select id, agencyid, "+column+", location from "
      		+ "gtfs_stops stop inner join aids on stop.agencyid = aids.aid), census as (select population"+popYear+" as population, poptype from "
      		+ "census_blocks block inner join stops on st_dwithin(block.location, stops.location, "+String.valueOf(x)+") where "+criteria2 +"='"+areaId+"' group by block.blockid), urbanpop as "
      		+ "(select COALESCE(sum(population),0) upop from census where poptype = 'U'), ruralpop as (select COALESCE(sum(population),0) rpop from census where poptype = 'R'),"
      		+ " stopcount as (select count(stops.id) as stopscount from stops) select COALESCE(stopscount,0) as stopscount, COALESCE(upop,0) as urbanpop, COALESCE(rpop,0) as "
      		+ "ruralpop from stopcount inner join urbanpop on true inner join ruralpop on true";
//      System.out.println(querytext);
      long[] results = new long[3];
      try {
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(querytext);        
        while ( rs.next() ) {
        	results[0] = rs.getLong("stopscount");
        	results[1] = rs.getLong("urbanpop"); 
        	results[2] = rs.getLong("ruralpop"); 
        }
        rs.close();
        stmt.close();        
      } catch ( Exception e ) {
        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
         
      }
      dropConnection(connection);      
      return results;
    }
	
	/**
	 *Queries Service miles, service hours, service stops, served pop at level of service (urban and rural), served population (urban and rural), service days, hours of service,
	 *and connected communities for a geographic area. keys are: svcmiles, svchours, svcstops, upopatlos, rpopatlos, uspop, rspop, svcdays, fromtime, totime, connections
	 */
	public static HashMap<String, String> ServiceMetrics(int type, String[] date, String[] day, String[] fulldates, String areaId, String username, int LOS, double x, int dbindex, String popYear) 
    {	
	  Connection connection = makeConnection(dbindex);
      Statement stmt = null;
      HashMap<String, String> response = new HashMap<String, String>();      
      String criteria = "";
      if (type==0){//county
    	  criteria = "left(blockid,5)";    	 
      } else if (type==1){//census tract
    	  criteria = "left(blockid,11)";    	 
      } else {// census place, urban area, ODOT region, or congressional district    	  
    	  criteria = Types.getIdColumnName(type);
      }
      String query = "with aids as (select agency_id as aid from gtfs_selected_feeds where username='"+username+"'), svcids as (";
      for (int i=0; i<date.length; i++){
    	  query+= "(select serviceid_agencyid, serviceid_id, '"+fulldates[i]+"' as day from gtfs_calendars gc inner join aids on gc.serviceid_agencyid = aids.aid where "
    	  		+ "startdate::int<="+date[i]+" and enddate::int>="+date[i]+" and "+day[i]+" = 1 and serviceid_agencyid||serviceid_id not in (select "
    	  		+ "serviceid_agencyid||serviceid_id from gtfs_calendar_dates where date='"+date[i]+"' and exceptiontype=2) union select serviceid_agencyid, serviceid_id, '"
    	  		+fulldates[i]+"' from gtfs_calendar_dates gcd inner join aids on gcd.serviceid_agencyid = aids.aid where date='"+date[i]+"' and exceptiontype=1)";
    	  if (i+1<date.length)
				query+=" union all ";
		}      
      query +="), trips as (select trip.agencyid as aid, trip.id as tripid, trip.route_id as routeid, round((map.length)::numeric,2) as length, map.tlength as tlength, "
      		+ "map.stopscount as stops from svcids inner join gtfs_trips trip using(serviceid_agencyid, serviceid_id) inner join "+Types.getTripMapTableName(type)+ " map on "
      		+"trip.id = map.tripid and trip.agencyid = map.agencyid where map."+Types.getIdColumnName(type)+"='"+areaId+"'),service as (select COALESCE(sum(length),0) as svcmiles,"
      		+ " COALESCE(sum(tlength),0) as svchours, COALESCE(sum(stops),0) as svcstops from trips),stopsatlos as (select stime.stop_agencyid as aid, stime.stop_id as stopid, "
      		+ "stop.location as location, count(trips.aid) as service from gtfs_stops stop inner join gtfs_stop_times stime on stime.stop_agencyid = stop.agencyid and "
      		+ "stime.stop_id = stop.id inner join trips on stime.trip_agencyid =trips.aid and stime.trip_id=trips.tripid group by "
      		+ "stime.stop_agencyid, stime.stop_id, stop.location having count(trips.aid)>="+LOS+"),stops as (select stime.stop_agencyid as aid, stime.stop_id as stopid, stop.location "
      		+ "as location, min(stime.arrivaltime) as arrival, max(stime.departuretime) as departure, count(trips.aid) as service from gtfs_stops stop inner join gtfs_stop_times "
      		+ "stime on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id inner join trips on stime.trip_agencyid =trips.aid and stime.trip_id=trips.tripid where "
      		+ "stime.arrivaltime>0 and stime.departuretime>0 group by stime.stop_agencyid, stime.stop_id, stop.location), svchrs as (select "
      		+ "COALESCE(min(arrival),-1) as fromtime, COALESCE(max(departure),-1) as totime from stops), concom as (select distinct map."+Types.getIdColumnName(type)+" from "
      		+Types.getTripMapTableName(type)+" map inner join trips on trips.aid=map.agencyid and trips.tripid=map.tripid),concomnames as (select coalesce(string_agg(distinct "
      		+Types.getNameColumn(type)+",'; ' order by "+Types.getNameColumn(type)+"),'-') as connections from concom inner join "+Types.getTableName(type)+" using("
      		+Types.getIdColumnName(type)+")), popserved as (select (population"+popYear+"*sum(service)) as population, poptype from census_blocks block inner join stops on "
      		+ "st_dwithin(block.location, stops.location,"+String.valueOf(x)+") where "+criteria+"='"+areaId+"' group by blockid), popatlos as (select population"+popYear+" as population, poptype from "
      		+ "census_blocks block inner join stopsatlos on st_dwithin(block.location,stopsatlos.location,"+String.valueOf(x)+") where "+criteria+"='"+areaId+"' group by blockid),"
      		+ "upopatlos as (select COALESCE(sum(population),0) as upoplos from popatlos where poptype='U'), rpopatlos as (select COALESCE(sum(population),0) as rpoplos from "
      		+ "popatlos where poptype='R'), upopserved as (select COALESCE(sum(population),0) as uspop from popserved where poptype='U'), rpopserved as (select "
      		+ "COALESCE(sum(population),0) as rspop from popserved where poptype='R'), svcdays as (select COALESCE(array_agg(distinct day)::text,'-') as svdays from svcids) select"
      		+ " svcmiles,svchours,svcstops,upoplos,rpoplos,uspop,rspop,svdays,fromtime,totime,connections from service inner join upopatlos on true inner join rpopatlos on true "
      		+ "inner join upopserved on true inner join rpopserved on true inner join svcdays on true inner join svchrs on true inner join concomnames on true";
      System.out.println(query);    
      try {
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);        
        while ( rs.next() ) {
        	response.put("svcmiles", String.valueOf(rs.getFloat("svcmiles")));
        	response.put("svchours", String.valueOf(Math.round(rs.getLong("svchours")/36.00)/100.00));
        	response.put("svcstops", String.valueOf(rs.getLong("svcstops")));
        	response.put("upopatlos", String.valueOf(rs.getLong("upoplos")));
        	response.put("rpopatlos", String.valueOf(rs.getLong("rpoplos")));
        	response.put("uspop", String.valueOf(rs.getFloat("uspop")));
        	response.put("rspop", String.valueOf(rs.getFloat("rspop")));
        	response.put("svcdays", String.valueOf(rs.getString("svdays")));
        	response.put("fromtime", String.valueOf(rs.getInt("fromtime")));
        	response.put("totime", String.valueOf(rs.getInt("totime")));
        	response.put("connections", rs.getString("connections"));        	                      
        }
        rs.close();
        stmt.close();        
      } catch ( Exception e ) {
        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
         
      }
      dropConnection(connection);      
      return response;
    }
	
	/**
	 *Queries geographic allocation of service for transit agency reports
	 *0:county 1:census tracts 2:census places 3:urban areas 4:ODOT region 5:congressional district
	 */
	public static List<GeoR> geoallocation(int type, String agencyId, int dbindex, String username, int urbanPop, String popYear) 
    {	
	  Connection connection = makeConnection(dbindex);
      Statement stmt = null;
      List<GeoR> response = new ArrayList<GeoR>();      
      String criteria = "";
      String stoproutes = "areaid";
      String areaquery = "";
      String selectquery = "";
      String query = "with ";
      String join = "";
      String aidsjoin = "";
      String routesidsjoin = "";
      String routesquery = "";
      String tractsFilter = "";
      if (agencyId==null){
    	  query += "aids as (select distinct agency_id as aid from gtfs_selected_feeds where username='"+username+"'),";
    	  join = "left";
    	  aidsjoin = "inner join aids on aid=stop.agencyid";
      }else {
    	  join = "inner";
    	  aidsjoin = "where map.agencyid='"+agencyId+"'";
    	  routesidsjoin = aidsjoin;		
    	  tractsFilter = " INNER JOIN stops ON ST_Contains(census_tracts.shape, stops.location) ";
      }
      if (type==0){//county: tracts are queries and summed up to reflect the true number of census tracts in the results
    	  criteria = "left(blockid,11)";      
    	  areaquery = "areas as (select countyid as areaid, cname as areaname, population"+popYear+" as population, landarea, waterarea, odotregionid, regionname from census_counties), "
    	  		+ "tracts as (select count(distinct tractid) as tracts, left(tractid,5) as areaid from census_tracts " + tractsFilter + " group by left(tractid,5)) ";    	  
    	  selectquery = "select areaid, areaname, population, landarea, waterarea, coalesce(agencies,0) as agencies, coalesce(routes,0) as routes, coalesce(stops,0) as stops,"
    	  		+ " odotregionid, regionname, tracts from areas "+join+" join stoproutes using(areaid) left join tracts using (areaid) left join routes using(areaid)";
    	  routesquery = "routes AS (SELECT count(distinct agencyid||routeid) as routes, countyid AS areaid FROM census_counties_trip_map AS map " + routesidsjoin + " GROUP BY areaid),";
    	  stoproutes = "left(areaid,5)";
      } else if (type==1){//census tract
    	  criteria = "left(blockid,11)";      
    	  areaquery = "areas as (select tractid as areaid, tname as areaname, tract.population"+popYear+" as population, tract.landarea, tract.waterarea from census_tracts tract)";
    	  routesquery = "routes AS (SELECT count(distinct agencyid||routeid) as routes, tractid AS areaid FROM census_tracts_trip_map AS map " + routesidsjoin + " GROUP BY areaid),";
    	  selectquery = " select areas.areaid, areaname, population, landarea, waterarea, coalesce(agencies,0) as agencies, coalesce(routes,0) as routes, "
    	  		+ "coalesce(stops,0) as stops from areas "+join+" join stoproutes using(areaid) left join routes using(areaid)";
      } else if (type==3){//census urban
    	  criteria = Types.getIdColumnName(type);
    	  routesquery = "routes AS (SELECT count(distinct agencyid||routeid) as routes, urbanid AS areaid FROM census_urbans_trip_map AS map " + routesidsjoin + " GROUP BY areaid),";
    	  areaquery = "areas as (select "+ criteria +" as areaid, "+Types.getNameColumn(type)+" as areaname, population"+popYear+" as population, landarea, waterarea from " + Types.getTableName(type)+" where population"+popYear+">"+urbanPop+")";
    	  selectquery = " select areas.areaid, areaname, population, landarea, waterarea,coalesce(agencies,0) as agencies, coalesce(routes,0) as routes, "
      	  		+ "coalesce(stops,0) as stops from areas "+join+" join stoproutes using(areaid) left join routes using(areaid)";
      } else if (type==4) { //ODOT Regions
    	  criteria = Types.getIdColumnName(type);
    	  areaquery = "areas as (select odotregionid as areaid, regionname as areaname, string_agg(trim(trailing 'County' from cname), ';' order by cname) as counties, "
    	  		+ "sum(population"+popYear+") as population, sum(landarea) as landarea, sum(waterarea) as waterarea from census_counties group by odotregionid, regionname)";
    	  routesquery = "routes AS (SELECT count(distinct agencyid||routeid) as routes, regionid AS areaid FROM census_counties_trip_map AS map " + routesidsjoin + " GROUP BY areaid),";
    	  selectquery = " select areas.areaid, areaname, counties, population, landarea, waterarea, coalesce(agencies,0) as agencies, coalesce(routes,0) as routes, "
    	  		+ "coalesce(stops,0) as stops from areas "+join+" join stoproutes using(areaid)  left join routes USING(areaid)";
      } else {// census place or congressional district    	  
    	  criteria = Types.getIdColumnName(type);
    	  if (type == 2) routesquery = "routes AS (SELECT count(distinct agencyid||routeid) as routes, placeid AS areaid FROM census_places_trip_map AS map " + routesidsjoin + " GROUP BY areaid),";
    	  else if (type == 5) routesquery = "routes AS (SELECT count(distinct agencyid||routeid) as routes, congdistid AS areaid FROM census_congdists_trip_map AS map " + routesidsjoin + " GROUP BY areaid),";
    	  areaquery = "areas as (select "+ criteria +" as areaid, "+Types.getNameColumn(type)+" as areaname, population"+popYear+" as population, landarea, waterarea from " + Types.getTableName(type)+")";
    	  selectquery = " select areas.areaid, areaname, population, landarea, waterarea,coalesce(agencies,0) as agencies, coalesce(routes,0) as routes, "
    	  		+ "coalesce(stops,0) as stops from areas "+join+" join stoproutes using(areaid) left join routes using(areaid)";
      }      
     query +="stops as (select stop.id as stopid,stop.agencyid as aid_def, map.agencyid as aid, "+criteria+" as areaid, ST_SetSRID(ST_Makepoint(lon,lat),4326) AS location FROM gtfs_stops stop inner join "
    		+ "gtfs_stop_service_map map on stop.id=map.stopid and stop.agencyid=map.agencyid_def "+aidsjoin+"), stoproutes as (select "
    		+ "coalesce(count(distinct(concat(aid,stops.stopid))),0) as stops, coalesce(count(distinct aid),0) as agencies,"
    		+stoproutes+" as areaid from stops inner join gtfs_stop_route_map map on stops.aid_def = map.agencyid_def and stops.stopid = map.stopid group by "
    		+stoproutes+"),"+routesquery+areaquery+ selectquery;
    System.out.println(query);
      try {
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query); 
        GeoR instance;
        while ( rs.next() ) {
        	instance = new GeoR();
        	instance.id = rs.getString("areaid");
        	instance.Name = rs.getString("areaname");        	
        	instance.population = String.valueOf(rs.getLong("population"));
        	instance.landArea = String.valueOf(Math.round(rs.getLong("landarea")/2.58999e4)/100.0);
        	instance.waterArea = String.valueOf(Math.round(rs.getLong("waterarea")/2.58999e4)/100.0);
        	instance.RoutesCount = String.valueOf(rs.getLong("routes"));
        	instance.StopsCount = String.valueOf(rs.getLong("stops"));
        	instance.AgenciesCount = String.valueOf(rs.getLong("agencies"));
        	if (type==0){
        		instance.ODOTRegion = rs.getString("odotregionid");
        		instance.ODOTRegionName = rs.getString("regionname");
        		instance.TractsCount = String.valueOf(rs.getLong("tracts"));
        	}
        	if (type==4){
        		//String[] buffer = (String[]) rs.getArray("names").getArray();
        		instance.CountiesCount = rs.getString("counties");
        	}
        	response.add(instance);
        }
        rs.close();
        stmt.close();        
      } catch ( Exception e ) {
        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
         
      }
      dropConnection(connection);      
      return response;
    }	
	
	/**
	 *Queries the transit agency summary report for geographic areas : Agency allocation of service for geographic areas
	 */
	public static ArrayList <AgencySR> agencyGeosr(String username, int type, String areaId, int dbindex) {
		ArrayList <AgencySR> response = new ArrayList<AgencySR>();		
		AgencySR instance;
		Connection connection = makeConnection(dbindex);		
		String mainquery ="";
		String criteria = "";
		String tripFilter1 = "";
		String tripFilter2 = "";
		if (type==0){//county
	    	  criteria = "left(blockid,5)";
	    	  tripFilter1 = "countyid";
	    	  tripFilter2 = "census_counties_trip_map";
	      }else if(type==1){
	    	  tripFilter2 = "census_tracts_trip_map";
	    	  tripFilter1 = "tractid";
	    	  criteria = "left(blockid,11)";
	      }	else  { //census place, urban area, ODOT Regions or congressional district	      
	    	  criteria = Types.getIdColumnName(type);
	    	  if (type == 2) {tripFilter1 = "placeid"; tripFilter2 = "census_places_trip_map";}	    	  
	    	  else if (type == 3) {tripFilter1 = "urbanid"; tripFilter2 = "census_urbans_trip_map";}
	    	  else if (type == 4) {tripFilter1 = "regionid"; tripFilter2 = "census_counties_trip_map";}
	    	  else  {tripFilter1 = "congdistid"; tripFilter2 = "census_congdists_trip_map";}
	      }
		mainquery += "with aids as (select distinct agency_id as aid from gtfs_selected_feeds where username='"+username+"'), stops as (select map.agencyid as agencyid, count(id) "
			+ "as stops from gtfs_stops stop inner join gtfs_stop_service_map map on map.agencyid_def=stop.agencyid and map.stopid=stop.id inner join aids on stop.agencyid=aid "
			+ "where "+criteria+"='"+areaId+"' group by map.agencyid), agencies as (select agencies.id as id, name, fareurl, phone, url, feedname, version, startdate, enddate, "
			+ "publishername, publisherurl from gtfs_agencies agencies inner join stops on agencies.id = stops.agencyid inner join gtfs_feed_info info on "
			+ "agencies.defaultid=info.defaultid ), fare as (select frule.route_agencyid as aid, frule.route_id as routeid, (round(avg(ftrb.price)*100))::integer as price "
			+ "from gtfs_fare_rules frule inner join gtfs_fare_attributes ftrb on ftrb.agencyid= frule.fare_agencyid and ftrb.id=frule.fare_id group by "
			+ "frule.route_agencyid,frule.route_id), "
			+ "froutes as (select routes.agencyid as aid, array_agg(coalesce(price, -1) order by price) as fprices "
			+ "	from gtfs_routes routes left join fare on routes.agencyid = fare.aid and routes.id = fare.routeid "
			+ "	inner join stops on stops.agencyid=routes.agencyid group by routes.agencyid), "
			+ " sroutes as (select agencyid as aid, routeid from " + tripFilter2 + " map inner join agencies on agencies.id = map.agencyid where " + tripFilter1 + "='" + areaId + "' group by agencyid,routeid),"
			+ " routes as (select count(distinct route.id) AS routes, route.agencyid "
			+ "	from gtfs_routes route inner join sroutes "
			+ "	on route.agencyid=aid and route.id = routeid  group by route.agencyid) "
			+ "	select id, name, fareurl, phone, url, feedname, version, startdate, enddate, publishername, publisherurl, fprices, routes, stops"
			+ " 	from agencies inner join stops "
			+ "		on agencies.id=stops.agencyid "
			+ "		inner join froutes"
			+ " 	on stops.agencyid = froutes.aid "
			+ "		left join routes"
			+ " 	on agencies.id = routes.agencyid";		
		//System.out.println(mainquery);
		try{
			PreparedStatement stmt = connection.prepareStatement(mainquery);
			ResultSet rs = stmt.executeQuery();			
			double faresum;
			int farecount;
			double faremin;
			double faremax;
			ArrayList<Integer> fareList;
			while (rs.next()) {
				instance = new AgencySR();
				instance.AgencyId = rs.getString("id");
				instance.AgencyName = rs.getString("name");
				instance.FareURL = rs.getString("fareurl");
				instance.Phone = rs.getString("phone");
				instance.URL = rs.getString("url");
				instance.FeedName = rs.getString("feedname");
				instance.FeedVersion = rs.getString("version");
				instance.FeedStartDate = rs.getString("startdate");
				instance.FeedStartDate = instance.FeedStartDate.substring(0, 4)+"-"+instance.FeedStartDate.substring(4, 6)+"-"+instance.FeedStartDate.substring(6, 8);
				instance.FeedEndDate = rs.getString("enddate");
				instance.FeedEndDate = instance.FeedEndDate.substring(0, 4)+"-"+instance.FeedEndDate.substring(4, 6)+"-"+instance.FeedEndDate.substring(6, 8);
				instance.FeedPublisherName = rs.getString("publishername");
				instance.FeedPublisherUrl = rs.getString("publisherurl");								
				instance.RoutesCount = rs.getLong("routes")>0 ?	String.valueOf(rs.getInt("routes")):"0";
				instance.StopsCount = rs.getLong("stops")>0 ?	String.valueOf(rs.getInt("stops")):"0";
				Integer[] fares = (Integer[])(rs.getArray("fprices").getArray());
				faremin = 0;
				faremax = 0;
				faresum = 0;
				farecount = 0;
				fareList = new ArrayList<Integer>();
				for (int i=0; i<fares.length; i++){
					if (fares[i]>0){
						faresum +=fares[i];
						farecount++;
						fareList.add(fares[i]);
						if (fares[i]<faremin)
							faremin = fares[i];
						if (fares[i]>faremax)
							faremax = fares[i];
					}					
				}
				instance.MinFare = (faremin>=0 && farecount>0)? String.valueOf(faremin/100.00):"NA";
				instance.MaxFare = (faremax>=0 && farecount>0)? String.valueOf(faremax/100.00):"NA";
				if (farecount>0){
					instance.AverageFare = String.valueOf(Math.round(faresum/farecount)/100.00);
					if (farecount == 1){
						instance.MedianFare = String.valueOf(fareList.get(0)/100.00);
					}
					else if (farecount%2!=0){
						instance.MedianFare = String.valueOf((fareList.get((int)Math.floor(farecount/2)))/100.00);
					} else {
						instance.MedianFare = String.valueOf(Math.round((fareList.get(farecount/2)+fareList.get((farecount/2)-1))/200.00));
					}
				} else {
					instance.MedianFare = "NA";
					instance.AverageFare = "NA";
				}
				response.add(instance);
	        	}		
			rs.close();
			stmt.close();
		} catch ( Exception e ) {
	        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
	         
	      }					
		dropConnection(connection);
		return response;
	}
	
	/**
	 *Queries the stops reports:
	 *stops by agency
	 *stops by agency and route 
	 *stops by geographic area
	 *stops by geographic area and agency
	 *stops by geographic are, agency, and route
	 * @throws SQLException 
	*/
	public static ArrayList <StopR> stopGeosr(String username, int type, String[] dates, String[] days, String areaId, String agency, String route, double x, int dbindex, String popYear) throws SQLException {
		ArrayList <StopR> response = new ArrayList<StopR>();		
		StopR instance;
		Connection connection = makeConnection(dbindex);	
		String stopsfilter = "";
		String routesfilter = "";
		String agenciesfilter = "";
		String popsfilter = "";
		String mainquery ="with ";
		String criteria = "";
		String agencyDefaultID = new String();
		if (agency != null) {agencyDefaultID = getDefaultAgencyID(agency, dbindex);}
		HashMap<String, Integer> stopsVisits = new HashMap<String, Integer>();
		if (dates != null && days != null){
			
			stopsVisits = stopFrequency(agency, dates, days, username, dbindex);
		}			
				
		if (areaId==null){//stops by agency or stops by agency and route
			stopsfilter = "where map.agencyid='"+agency+"'";			
			agenciesfilter = "where id='"+agency+"'";
			if (route==null){//stops by agency
				routesfilter = " where map.agencyid='"+agency+"'";
			}else {//stops by agency and route
				routesfilter = " where map.agencyid='"+agency+"' and map.routeid='"+route+"'";
			}
		}else {//stops by areaId and ...
			if (type==0){//county
		    	  criteria = "left(blockid,5)";
		    	  popsfilter = "left(block.blockid,5) ='"+areaId+"' and";
		      }else if (type==1){//tract
		    	  criteria = "left(blockid,11)"; 
		    	  popsfilter = "left(block.blockid,11) ='"+areaId+"' and";
		      }	else  { //census place, urban area, ODOT Regions or congressional district
		    	  criteria = Types.getIdColumnName(type);
		    	  popsfilter = "block." + criteria + "='" + areaId + "' and";
		      }
			if (route==null){
				if (agency==null){//stops by areaId
					mainquery +="aids as (select distinct agency_id as aid from gtfs_selected_feeds where username='"+username+"'), ";
					stopsfilter = "inner join aids on stop.agencyid=aid and "+criteria+"='"+areaId+"' ";					
				}else {//stops by areaId and agency
					stopsfilter = "where map.agencyid='"+agency+"' and "+criteria+"='"+areaId+"'";
					agenciesfilter = "where agencies.id='"+agency+"'";
					routesfilter = "where map.agencyid='"+agency+"'";
				}			
			}else {//stops by areaId, agency, and route
				stopsfilter = "where map.agencyid='"+agency+"' and "+criteria+"='"+areaId+"'";
				agenciesfilter = "where agencies.id='"+agency+"'";
				routesfilter = "where map.agencyid='"+agency+"' and map.routeid='"+route+"'";
			}			
		}
		mainquery += "stops0 as (select map.agencyid as agencyid,stop.lat, stop.lon, stop.name as name, stop.id as id, url, location, urbanid, regionid, congdistid, placeid, blockid "
				+ "	from gtfs_stops stop inner join gtfs_stop_service_map map on map.agencyid_def=stop.agencyid and map.stopid=stop.id " + stopsfilter + "), "
				+ "stops AS (select stops0.*, urban.population"+popYear+" AS urbanpop FROM stops0 LEFT JOIN census_urbans AS urban ON urban.urbanid = stops0.urbanid AND population"+popYear+" >50000), "
				+ "agencies as (select agencies.id as agencyid, agencies.name as aname from gtfs_agencies "
				+ "agencies "+agenciesfilter+"), routes as (select map.agencyid, stops.id, coalesce(string_agg(map.routeid,'; '),'-') as routes from gtfs_stop_route_map map inner "
				+ "join stops on stops.agencyid=map.agencyid and stops.id=map.stopid "+routesfilter+" group by map.agencyid, stops.id), upops as (select stops.agencyid, stops.id, "
				+ "coalesce(sum(population"+popYear+"),0) as upop from census_blocks block inner join stops on st_dwithin(block.location, stops.location, "+String.valueOf(x)+") where "
				+ popsfilter+" poptype='U' group by agencyid, id), rpops as (select stops.agencyid, stops.id, coalesce(sum(population"+popYear+"),0) as rpop from census_blocks block inner "
				+ "join stops on st_dwithin(block.location, stops.location, "+String.valueOf(x)+") where "+popsfilter+" poptype='R' group by agencyid, id), "
				+ "result AS (select stops.agencyid, stops.lat, stops.lon, aname, id, name, url, stops.urbanid, stops.regionid, stops.congdistid, stops.placeid, stops.blockid, "
				+ "routes, coalesce(upop,0) as upop, coalesce(rpop,0) as rpop, COALESCE(urbanpop,0) AS overfiftypop "
				+ "	from stops inner join agencies using(agencyid) inner join routes "
				+ "	using(agencyid,id) left join upops using(agencyid,id) left join rpops using(agencyid,id)) "
				+ "select result.agencyid, result.lat, result.lon, aname, id, name, url, routes, upop, rpop, overfiftypop, COALESCE(census_urbans.uname,'N/A') AS urbanname, COALESCE(census_places.pname,'N/A') AS placename, "
				+ "COALESCE(result.regionid,'N/A') AS regionname, COALESCE(census_congdists.cname,'N/A') AS congdistname, COALESCE(census_counties.cname,'N/A') AS countyname "
				+ "	FROM result LEFT JOIN census_urbans using(urbanid) "
				+ "	LEFT JOIN census_places USING(placeid) "
				+ "	LEFT JOIN census_congdists USING(congdistid) "
				+ "	LEFT JOIN census_counties ON census_counties.countyid = LEFT(result.blockid,5)";
		System.out.println(mainquery);
		try{
			PreparedStatement stmt = connection.prepareStatement(mainquery);
			ResultSet rs = stmt.executeQuery();				
			while (rs.next()) {
				instance = new StopR();
				instance.AgencyId = rs.getString("agencyid");
				instance.AgencyName = rs.getString("aname");
				instance.Routes = rs.getString("routes");
				instance.UPopWithinX = String.valueOf(rs.getLong("upop"));
				instance.RPopWithinX = String.valueOf(rs.getLong("rpop"));
				instance.URL = rs.getString("url");
				instance.StopId = rs.getString("id");
				instance.StopName = rs.getString("name");
				instance.OverFiftyPop = String.valueOf(rs.getInt("overfiftypop"));
				instance.PlaceName = rs.getString("placename");
				instance.CountyName = rs.getString("countyname");
				instance.UrbanName = rs.getString("urbanname");
				if (rs.getString("regionname") != "N/A") instance.RegionName = "Region " + rs.getString("regionname");
				else instance.RegionName = rs.getString("regionname");
				instance.CongDistName = rs.getString("congdistname");
				
				if (agency == null)
					instance.visits = stopsVisits.get(instance.AgencyId + instance.StopId) + "";
				else 
					instance.visits = stopsVisits.get(agencyDefaultID + instance.StopId) + "";
				instance.lat = String.valueOf(rs.getDouble("lat"));
				instance.lon = String.valueOf(rs.getDouble("lon"));
				
				response.add(instance);
	        }		
			rs.close();
			stmt.close();
		} catch ( Exception e ) {
	        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
	         
	      }					
		dropConnection(connection);
		return response;
	}
	
	/**
	 * Gets real ID of the agency and returns default ID.
	 * @param agencyid
	 * @param dbindex
	 * @return
	 * @throws SQLException
	 */
	public static String getDefaultAgencyID(String agencyid, int dbindex) throws SQLException{
		String output = new String();
		String query = "SELECT defaultid FROM gtfs_agencies WHERE id = '" + agencyid + "'";
		Connection connection = makeConnection(dbindex);
		PreparedStatement stmt = connection.prepareStatement(query);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			output = rs.getString("defaultid");
		}
		return output;
	}
	
	/**
	 *Queries the routes reports:
	 *routes by agency	 
	 *routes by geographic area	
	 *routes by agency and geographic area  
	*/
	public static ArrayList <RouteR> RouteGeosr(String username, int type, String areaId, String agency, String[] date, String[] day, double x, int dbindex, String popYear) {
		ArrayList <RouteR> response = new ArrayList<RouteR>();		
		RouteR instance;
		Connection connection = makeConnection(dbindex);		
		String agenciesfilter = "";
		String routesfilter = "";
		String tripsfilter = "";
		String blocksfilter = "";
		String stopscountfilter = "";
		String scriteria = "";
		String bcriteria = "";
		String mainquery ="with ";		
		if (areaId==null){//routes by agency
			agenciesfilter = "agencies as (select id as agencyid, name as aname, defaultid from gtfs_agencies agencies where id='"+agency+"'), ";
			routesfilter = "routes as (select route.agencyid, route.id, shortname, longname, type, url, description, COALESCE(max(length+estlength),0) as rlength "
					+ " from gtfs_routes route inner join agencies using(agencyid) left join gtfs_trips trip on "
					+ "route.agencyid=trip.route_agencyid and route.id = trip.route_id group by route.agencyid, route.id), ";
			tripsfilter = "trips as (select trip.agencyid as aid, trip.id as tripid, trip.route_id as routeid, round((length+estlength)::numeric,2) as length, tlength, "
					+ "stopscount as stops from svcids inner join gtfs_trips trip using(serviceid_agencyid, serviceid_id)), ";
			stopscountfilter = "stopscount AS (SELECT count(distinct (stopid||agencyid)) AS stops, routeid FROM gtfs_stop_route_map WHERE agencyid = '" + agency + "' GROUP BY routeid),";
		}else {//routes by areaId or agency & areaId
			if (type==0){//county
		    	  bcriteria = "left(block.blockid,5)";
		    	  scriteria = "left(stop.blockid,5)"; 
		      }else if (type==1){//tract
		    	  bcriteria = "left(block.blockid,11)";
		    	  scriteria = "left(stop.blockid,11)";
		      }	else  { //census place, urban area, ODOT Regions or congressional district
		    	  bcriteria = "block."+Types.getIdColumnName(type);
		    	  scriteria = "stop."+Types.getIdColumnName(type);
		      }
			routesfilter = "sroutes as (select agencyid as aid, routeid, COALESCE(max(length),0) as length from "+Types.getTripMapTableName(type)+" map "
					+ "inner join agencies using(agencyid) where "+Types.getIdColumnName(type)+"='"+areaId+"' group by agencyid,routeid), "
					+ "routes as (select route.agencyid, route.id, shortname, longname, type, url, description, length as rlength from gtfs_routes route inner join sroutes on route.agencyid=aid and "
					+ "route.id = routeid), ";
			tripsfilter = "trips as (select trip.agencyid as aid, tripid, routeid, length, tlength, stopscount as stops from svcids inner join "+ Types.getTripMapTableName(type)+" trip on "
					+ "trip.agencyid_def=serviceid_agencyid and trip.serviceid=serviceid_id where "+Types.getIdColumnName(type)+"='"+areaId+"'), ";
			blocksfilter = "where "+bcriteria+"='"+areaId+"' and "+scriteria+"='"+areaId+"'";
			if (agency==null){//routes by areaId
				agenciesfilter = "aids as (select distinct agency_id as aid from gtfs_selected_feeds where username='"+username+"'), agencies as (select agencies.id as "
						+ "agencyid, agencies.name as aname, defaultid from gtfs_agencies agencies inner join aids on aids.aid=agencies.defaultid), ";
				stopscountfilter = "stopscount AS (SELECT count(distinct (map.stopid||map.agencyid)) AS stops, map.routeid "
						+ "	FROM gtfs_stop_route_map AS map INNER JOIN gtfs_stops AS stop ON stop.id=map.stopid AND stop.agencyid=map.agencyid_def WHERE " + scriteria + "='" + areaId + "' GROUP BY routeid),";
			}else {//routes by areaId and agency
				agenciesfilter ="agencies as (select id as agencyid, name as aname, defaultid from gtfs_agencies agencies where id='"+agency+"'), ";
				stopscountfilter = "stopscount AS (SELECT count(distinct (map.stopid||map.agencyid)) AS stops, map.routeid "
						+ "	FROM gtfs_stop_route_map AS map INNER JOIN gtfs_stops AS stop ON stop.id=map.stopid AND stop.agencyid=map.agencyid_def WHERE map.agencyid = '" + agency + "' AND " + scriteria + "='" + areaId + "' GROUP BY routeid),";
			}
		}
		mainquery += agenciesfilter+"svcids as (";
		for (int i=0; i<date.length; i++){
			mainquery+= "(select serviceid_agencyid, serviceid_id from gtfs_calendars gc inner join agencies on gc.serviceid_agencyid = agencies.defaultid where "
	    	  		+ "startdate::int<="+date[i]+" and enddate::int>="+date[i]+" and "+day[i]+" = 1 and serviceid_agencyid||serviceid_id not in (select "
	    	  		+ "serviceid_agencyid||serviceid_id from gtfs_calendar_dates where date='"+date[i]+"' and exceptiontype=2) union select serviceid_agencyid, serviceid_id "
	    	  		+"from gtfs_calendar_dates gcd inner join agencies on gcd.serviceid_agencyid = agencies.defaultid where date='"+date[i]+"' and exceptiontype=1)";
	    	  if (i+1<date.length)
					mainquery+=" union all ";
			}
		mainquery+= "), "+ routesfilter + tripsfilter + stopscountfilter 
				+ "freq as (select aid, routeid, coalesce(count(concat(routeid)),0) as frequency from trips group by aid, routeid), service as (select aid, routeid, COALESCE(sum(length),0) "
				+ "as svcmiles, COALESCE(sum(tlength),0) as svchours, COALESCE(sum(stops),0) as svcstops from trips group by aid,routeid), "
				+ "blocks as (select trips.aid, trips.routeid, population"+popYear+" as population, poptype, block.blockid, block.regionid, block.urbanid, block.placeid, block.congdistid "
				+ " from gtfs_stops stop inner join gtfs_stop_times stime on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id "
				+ "inner join trips on stime.trip_agencyid =trips.aid and stime.trip_id=trips.tripid inner join census_blocks block on "
				+ "st_dwithin(block.location, stop.location, "+String.valueOf(x)+") "+blocksfilter+" group by aid,routeid,block.blockid),"
				+ " upop as (select aid, routeid, "
				+ "coalesce(sum(population),0) as upop, coalesce(sum(population*frequency),0) as svcupop from blocks inner join freq using(aid,routeid) where poptype ='U' "
				+ "group by aid, routeid), upopr as (select aid, routeid, sum(upop) as upop, sum(svcupop) as svcupop from upop inner join trips using(aid,routeid) group by aid, "
				+ "routeid), rpop as (select aid, routeid, coalesce(sum(population),0) as rpop, coalesce(sum(population*frequency),0) as svcrpop from blocks inner join freq "
				+ "using(aid,routeid) where poptype ='R' group by aid, routeid), rpopr as (select aid, routeid, sum(rpop) as rpop, sum(svcrpop) as svcrpop from rpop inner join trips "
				+ "using(aid,routeid) group by aid, routeid), "
				+ "routesblocks AS (select routes.agencyid AS aid, routes.id AS routeid, gtfs_stops.blockid, gtfs_stops.urbanid, gtfs_stops.placeid, gtfs_stops.congdistid, gtfs_stops.regionid "
				+ "	from routes INNER JOIN gtfs_stop_route_map AS map ON routes.agencyid = map.agencyid AND routes.id=map.routeid "
				+ "	INNER JOIN gtfs_stops ON map.stopid=gtfs_stops.id AND map.agencyid_def=gtfs_stops.agencyid),"
				+ " areas as (SELECT aid,routeid, string_agg(distinct census_urbans.uname,',') AS urbans, string_agg(distinct census_counties.cname,',') AS counties, "
				+ "	string_agg(distinct census_places.pname,',') AS places, string_agg(distinct census_congdists.cname,',') AS congdists, "
				+ "	string_agg(distinct 'Region '||routesblocks.regionid,',') AS regions "
				+ "	FROM routesblocks LEFT JOIN census_urbans USING(urbanid) "
				+ "	LEFT JOIN census_counties ON census_counties.countyid=LEFT(routesblocks.blockid,5) "
				+ "	LEFT JOIN census_places USING(placeid) "
				+ "	LEFT JOIN census_congdists USING(congdistid) "
				+ "	WHERE census_urbans.uname IS NOT NULL "
				+ "	AND census_places.pname IS NOT NULL "
				+ "	AND census_counties.cname IS NOT NULL "
				+ "	AND census_congdists.cname IS NOT NULL "
				+ "	AND routesblocks.regionid IS NOT NULL "
				+ "	GROUP BY aid,routeid) "
				+ "select agencies.agencyid, aname, routes.id, shortname, longname, type, url, description, rlength, stops, "
				+ "coalesce(upop,0) as upop, coalesce(rpop,0) as rpop, COALESCE(svcstops,0) as svcstops, COALESCE(svchours,0) as svchours, COALESCE(svcmiles,0) as svcmiles, "
				+ "COALESCE(svcupop,0) as usvcpop, COALESCE(svcrpop,0) as rsvcpop, COALESCE(urbans,'N/A') AS urbans, COALESCE(counties,'N/A') AS counties, "
				+ "COALESCE(places,'N/A') AS places, COALESCE(congdists,'N/A') AS congdists, COALESCE(regions,'N/A') AS regions "
				+ "from routes inner join agencies on routes.agencyid=agencies.agencyid "
				+ "left join service on routes.agencyid=service.aid and routes.id=service.routeid "
				+ "left join upop on routes.agencyid=upop.aid and routes.id= upop.routeid "
				+ "left join rpop on routes.agencyid=rpop.aid and routes.id=rpop.routeid "
				+ "left join stopscount on routes.id = stopscount.routeid "
				+ "left join areas on routes.id = areas.routeid and routes.agencyid = areas.aid";					
		System.out.println(mainquery);
		try{
			PreparedStatement stmt = connection.prepareStatement(mainquery);
			ResultSet rs = stmt.executeQuery();				
			while (rs.next()) {
				instance = new RouteR();
				instance.AgencyId = rs.getString("agencyid");				
				instance.AgencyName = rs.getString("aname");
				instance.RouteId = rs.getString("id");
				instance.RouteSName = rs.getString("shortname");
				instance.RouteLName = rs.getString("longname");
				instance.RouteType = rs.getString("type");
				instance.RouteURL = rs.getString("url");
				instance.RouteDesc = rs.getString("description");
				instance.RouteLength = String.valueOf(Math.round(rs.getDouble("rlength")*100.00)/100.00);
				instance.StopsCount = String.valueOf(rs.getInt("stops"));
				instance.UPopWithinX = String.valueOf(rs.getLong("upop"));
				instance.RPopWithinX = String.valueOf(rs.getLong("rpop"));
				instance.ServiceStops = String.valueOf(rs.getLong("svcstops"));
				instance.ServiceHours = String.valueOf(Math.round(rs.getLong("svchours")/36.0)/100.0);
				instance.ServiceMiles = String.valueOf(Math.round(rs.getDouble("svcmiles")*100.0)/100.0);
				instance.UServicePop = String.valueOf(rs.getLong("usvcpop"));
				instance.RServicePop = String.valueOf(rs.getLong("rsvcpop"));
				instance.counties = rs.getString("counties");
				instance.congdists = rs.getString("congdists");
				instance.places = rs.getString("places");
				instance.urbans = rs.getString("urbans");
				instance.regions = rs.getString("regions");
				response.add(instance);
	        }		
			rs.close();
			stmt.close();
		} catch ( Exception e ) {
	        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
//	         
	      }					
		dropConnection(connection);
		return response;
	}
	
	/**
	 *Queries the transit agency summary report	  
	 */
	public static ArrayList <AgencySR> agencysr(String username, int dbindex) {
		ArrayList <AgencySR> response = new ArrayList<AgencySR>();		
		AgencySR instance;
		Connection connection = makeConnection(dbindex);		
		String mainquery ="";
		mainquery += "with aids as (select distinct agency_id as aid from gtfs_selected_feeds where username='"+username+"'), agencies as (select id, name, fareurl, phone, url, "
				+ "feedname, version, startdate, enddate, publishername, publisherurl from gtfs_agencies agencies inner join aids on agencies.defaultid = aids.aid inner join "
				+ "gtfs_feed_info info on agencies.defaultid=info.defaultid), stops as (select map.agencyid as aid, coalesce((count(id))::int, -1) as stops, "
				+ "coalesce((count(distinct placeid))::int,-1) as places, coalesce((count(distinct left(blockid, 5)))::int,-1) as counties, "
				+ "coalesce((count(distinct regionid))::int,-1) as odotregions, coalesce((count(distinct urbanid))::int,-1) as urbans, "
				+ "coalesce((count(distinct congdistid))::int,-1) as congdists from gtfs_stops stop inner join gtfs_stop_service_map map on stop.agencyid = map.agencyid_def and "
				+ "stop.id=map.stopid group by map.agencyid), fare as (select frule.route_agencyid as aid, frule.route_id as routeid, (round(avg(ftrb.price)*100))::integer as price from gtfs_fare_rules "
				+ "frule inner join gtfs_fare_attributes ftrb on ftrb.agencyid= frule.fare_agencyid and ftrb.id=frule.fare_id group by frule.route_agencyid,frule.route_id), "
				+ "froutes as (select routes.agencyid as aid, coalesce((count(routes.id))::int,-1) as routes, array_agg(coalesce(price, -1) order by price) as fprices from "
				+ "gtfs_routes routes left join fare on routes.agencyid = fare.aid and routes.id = fare.routeid group by routes.agencyid) select id, name, fareurl, phone, url, "
				+ "feedname, version, startdate, enddate, publishername, publisherurl, fprices, routes, stops, places, counties, odotregions, urbans, congdists from agencies "
				+ "inner join stops on agencies.id=stops.aid inner join froutes on stops.aid = froutes.aid";		
//		System.out.println("agency sr: " + mainquery);
		try{
			PreparedStatement stmt = connection.prepareStatement(mainquery);
			ResultSet rs = stmt.executeQuery();			
			double faresum;
			int farecount;
			double faremin;
			double faremax;
			ArrayList<Integer> fareList;
			while (rs.next()) {
				instance = new AgencySR();
				instance.AgencyId = rs.getString("id");
				instance.AgencyName = rs.getString("name");
				instance.FareURL = rs.getString("fareurl");
				instance.Phone = rs.getString("phone");
				instance.URL = rs.getString("url");
				instance.FeedName = rs.getString("feedname");
				instance.FeedVersion = rs.getString("version");
				instance.FeedStartDate = rs.getString("startdate");
				instance.FeedStartDate = instance.FeedStartDate.substring(0, 4)+"-"+instance.FeedStartDate.substring(4, 6)+"-"+instance.FeedStartDate.substring(6, 8);
				instance.FeedEndDate = rs.getString("enddate");
				instance.FeedEndDate = instance.FeedEndDate.substring(0, 4)+"-"+instance.FeedEndDate.substring(4, 6)+"-"+instance.FeedEndDate.substring(6, 8);
				instance.FeedPublisherName = rs.getString("publishername");
				instance.FeedPublisherUrl = rs.getString("publisherurl");				
				instance.CountiesCount = rs.getLong("counties")>0 ?	String.valueOf(rs.getInt("counties")):"0";
				instance.PlacesCount = rs.getLong("places")>0 ?	String.valueOf(rs.getInt("places")):"0";
				instance.OdotRegionsCount = rs.getLong("odotregions")>0 ?	String.valueOf(rs.getLong("odotregions")):"0";
				instance.UrbansCount = rs.getLong("urbans")>0 ?	String.valueOf(rs.getInt("urbans")):"0";
				instance.CongDistsCount = rs.getLong("congdists")>0 ?	String.valueOf(rs.getLong("congdists")):"0";				
				instance.RoutesCount = rs.getLong("routes")>0 ?	String.valueOf(rs.getInt("routes")):"0";
				instance.StopsCount = rs.getLong("stops")>0 ?	String.valueOf(rs.getInt("stops")):"0";
				Integer[] fares = (Integer[])(rs.getArray("fprices").getArray());
				faremin = 0;
				faremax = 0;
				faresum = 0;
				farecount = 0;
				fareList = new ArrayList<Integer>();
				for (int i=0; i<fares.length; i++){
					if (fares[i]>0){
						faresum +=fares[i];
						farecount++;
						fareList.add(fares[i]);
						if (fares[i]<faremin)
							faremin = fares[i];
						if (fares[i]>faremax)
							faremax = fares[i];
					}					
				}
				instance.MinFare = (faremin>=0 && farecount>0)? String.valueOf(faremin/100.00):"NA";
				instance.MaxFare = (faremax>=0 && farecount>0)? String.valueOf(faremax/100.00):"NA";
				if (farecount>0){
					instance.AverageFare = String.valueOf(Math.round(faresum/farecount)/100.00);
					if (farecount == 1){
						instance.MedianFare = String.valueOf(fareList.get(0)/100.00);
					}
					else if (farecount%2!=0){
						instance.MedianFare = String.valueOf((fareList.get((int)Math.floor(farecount/2)))/100.00);
					} else {
						instance.MedianFare = String.valueOf(Math.round((fareList.get(farecount/2)+fareList.get((farecount/2)-1))/200.00));
					}
				} else {
					instance.MedianFare = "NA";
					instance.AverageFare = "NA";
				}
				response.add(instance);
	        	}		
			rs.close();
			stmt.close();
		} catch ( Exception e ) {
	        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
	         
	      }					
		dropConnection(connection);
		return response;
	}	
	
/////AGGREGATED URBAN AREAS EXTENDED REPORTS QUERIES
	
	/**
	 *Queries Route miles for all urban areas with population => pop
	 */
	public static float AUrbansRouteMiles(long pop, String username, int dbindex, String popYear) 
    {	
	  Connection connection = makeConnection(dbindex);
      Statement stmt = null;
      float RouteMiles = 0;
      String query = "with aids as (select distinct agency_id as aid from gtfs_selected_feeds where username='"+username+"'), areas as (select urbanid from census_urbans where "
      		+ "population"+popYear+">="+String.valueOf(pop)+"), trips as (select agencyid, routeid, round(max(length)::numeric,2) as length from census_urbans_trip_map map inner "
      		+ "join aids on map.agencyid_def=aids.aid inner join areas on areas.urbanid = map.urbanid group by agencyid, routeid) select sum(length) as routemiles from trips";
      try {
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);        
        while ( rs.next() ) {
        	RouteMiles = rs.getFloat("routemiles");                     
        }
        rs.close();
        stmt.close();        
      } catch ( Exception e ) {
        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
         
      }
      dropConnection(connection);      
      return RouteMiles;
    }
	
	/**
	 *Queries Fare Information for urban areas with population >= pop. keys are; minfare, maxfare, medianfare, averagefare
	 */
	public static HashMap<String, Float> AUrbansFareInfo(String[] date, String[] day, long pop, String username, int dbindex, String popYear) 
    {	
	  Connection connection = makeConnection(dbindex);
      Statement stmt = null;
      HashMap<String, Float> response = new HashMap<String, Float>();
      ArrayList<Float> faredata = new ArrayList<Float>();
      String query = "with aids as (select distinct agency_id as aid from gtfs_selected_feeds where username='"+username+"'), areas as (select urbanid from census_urbans where population"+popYear+">="
      +String.valueOf(pop)+"), svcids as (";
      for (int i=0; i<date.length; i++){
    	  query+= "(select serviceid_agencyid, serviceid_id from gtfs_calendars gc inner join aids on gc.serviceid_agencyid = aids.aid where startdate::int<="+date[i]+
    			  " and enddate::int>="+date[i]+" and "+day[i]+" = 1 and serviceid_agencyid||serviceid_id not in (select serviceid_agencyid||serviceid_id from " +
    			  "gtfs_calendar_dates where date='"+date[i]+"' and exceptiontype=2) union select serviceid_agencyid, "+
    			  "serviceid_id from gtfs_calendar_dates gcd inner join aids on gcd.serviceid_agencyid = aids.aid where date='"+date[i]+"' and exceptiontype=1)";
    	  if (i+1<date.length)
				query+=" union all ";
		}      
    query +="), trips as (select trip.route_agencyid as aid, trip.route_id as routeid from svcids inner join gtfs_trips trip using(serviceid_agencyid, serviceid_id) inner join "
    	+ "census_urbans_trip_map map on trip.id = map.tripid and trip.agencyid = map.agencyid inner join areas on areas.urbanid = map.urbanid), fare as (select "
    	+ "frule.route_agencyid as aid, frule.route_id as routeid, ftrb.price as price from gtfs_fare_rules frule inner join gtfs_fare_attributes ftrb on "
    	+ "ftrb.agencyid= frule.fare_agencyid and ftrb.id=frule.fare_id inner join aids on aids.aid=ftrb.agencyid) select round(avg(price)::numeric,2) as fare from fare "
    	+ "inner join trips using (aid,routeid) group by fare.routeid order by fare";
    //System.out.println(query);
      try {
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);        
        while ( rs.next() ) {
        	faredata.add(rs.getFloat("fare"));                      
        }
        rs.close();
        stmt.close();        
      } catch ( Exception e ) {
        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
         
      }
      dropConnection(connection);
      if (faredata.size()>0){
    	  Collections.sort(faredata);     
    	  response.put("minfare", faredata.get(0));
    	  response.put("maxfare", faredata.get(faredata.size()-1));    	  
		  if (faredata.size()%2==0){
				response.put("medianfare", (float)(Math.round((faredata.get(faredata.size()/2)+faredata.get((faredata.size()/2)-1))*100.00/2)/100.00));		
		  } else {
				response.put("medianfare", faredata.get((int)(Math.ceil(faredata.size()/2))));		
		  }
		  float faresum = 0;
	      for (int i=0; i<faredata.size();i++){
	    	  faresum+=faredata.get(i);
	      }
	      response.put("averagefare", (float)(Math.round(faresum*100.00/faredata.size())/100.00));
      } else {
    	  response.put("minfare", null);
    	  response.put("maxfare", null);
    	  response.put("medianfare", null);
    	  response.put("averagefare", null);
      }
      return response;
    }
	
	/**
	 *Queries Stops count and unduplicated urban pop within x meters of all stops within urban areas with population >= pop
	 */
	public static long[] AUrbansstopsPop(long pop, String username, double x, int dbindex, String popYear) 
    {	
	  Connection connection = makeConnection(dbindex);
      Statement stmt = null;
      String querytext = "with aids as (select distinct agency_id as aid from gtfs_selected_feeds where username='"+username+"'), areas as (select urbanid from census_urbans where "
      		+ "population"+popYear+">="+pop+"), stops as (select id, agencyid, blockid, location from gtfs_stops stop inner join aids on stop.agencyid = aids.aid inner join areas on "
      		+ "stop.urbanid = areas.urbanid), census as (select population"+popYear+" as population from census_blocks block inner join stops on st_dwithin(block.location, stops.location,"
      		+ String.valueOf(x)+") inner join areas on block.urbanid = areas.urbanid group by block.blockid), urbanpop as (select COALESCE(sum(population),0) upop from census ),"
      		+ "stopcount as (select count(stops.id) as stopscount from stops) select COALESCE(stopscount,0) as stopscount, COALESCE(upop,0) as urbanpop from stopcount inner join "
      		+ "urbanpop on true";
      long[] results = new long[3];
      //System.out.println(querytext);
      try {
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(querytext);        
        while ( rs.next() ) {
        	results[0] = rs.getLong("stopscount");
        	results[1] = rs.getLong("urbanpop"); 
        	//results[2] = rs.getLong("ruralpop"); 
        }
        rs.close();
        stmt.close();        
      } catch ( Exception e ) {
        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
         
      }
      dropConnection(connection);      
      return results;
    }
	
	/**
	 *Queries Service miles, service hours, service stops, served pop at level of service (urban and rural), served population (urban and rural), service days, hours of service,
	 *and connected communities for urban areas with population >= pop. keys are: svcmiles, svchours, svcstops, upopatlos, uspop, svcdays, fromtime, totime, connections
	 */
	public static HashMap<String, String> UAreasServiceMetrics(String[] date, String[] day, String[] fulldates, long pop, String username, int LOS, double x, int dbindex, String popYear) 
    {	
	  Connection connection = makeConnection(dbindex);
      Statement stmt = null;
      HashMap<String, String> response = new HashMap<String, String>();      
      String query = "with aids as (select distinct agency_id as aid from gtfs_selected_feeds where username='"+username+"'), areas as (select urbanid from census_urbans where "
      		+ "population"+popYear+">="+pop+"), svcids as (";
      for (int i=0; i<date.length; i++){
    	  query+= "(select serviceid_agencyid, serviceid_id, '"+fulldates[i]+"' as day from gtfs_calendars gc inner join aids on gc.serviceid_agencyid = aids.aid where "
    	  		+ "startdate::int<="+date[i]+" and enddate::int>="+date[i]+" and "+day[i]+" = 1 and serviceid_agencyid||serviceid_id not in (select "
    	  		+ "serviceid_agencyid||serviceid_id from gtfs_calendar_dates where date='"+date[i]+"' and exceptiontype=2) union select serviceid_agencyid, serviceid_id, '"
    	  		+fulldates[i]+"' from gtfs_calendar_dates gcd inner join aids on gcd.serviceid_agencyid = aids.aid where date='"+date[i]+"' and exceptiontype=1)";
    	  if (i+1<date.length)
				query+=" union all ";
		}      
    query +="), trips as (select trip.agencyid as aid, trip.id as tripid, trip.route_id as routeid, round((map.length)::numeric,2) as length, map.tlength as tlength, map.stopscount"
    		+ " as stops from svcids inner join gtfs_trips trip using(serviceid_agencyid, serviceid_id) inner join census_urbans_trip_map map on trip.id = map.tripid and "
    		+ "trip.agencyid = map.agencyid inner join areas on areas.urbanid = map.urbanid), service as (select COALESCE(sum(length),0) as svcmiles, COALESCE(sum(tlength),0) "
    		+ "as svchours, COALESCE(sum(stops),0) as svcstops from trips), stopsatlos as (select stime.stop_agencyid as aid, stime.stop_id as stopid, stop.location as location, "
    		+ "count(trips.aid) as service from gtfs_stops stop inner join gtfs_stop_times stime on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id inner join "
    		+ "trips on stime.trip_agencyid =trips.aid and stime.trip_id=trips.tripid inner join areas on stop.urbanid = areas.urbanid group by stime.stop_agencyid, stime.stop_id, "
    		+ "stop.location having count(trips.aid)>="+String.valueOf(LOS)+" ), stops as (select stime.stop_agencyid as aid, stime.stop_id as stopid, stop.location as location, "
    		+ "min(stime.arrivaltime) as arrival, max(stime.departuretime) as departure, count(trips.aid) as service from gtfs_stops stop inner join gtfs_stop_times stime on "
    		+ "stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id inner join trips on stime.trip_agencyid =trips.aid and stime.trip_id=trips.tripid inner join areas "
    		+ "on stop.urbanid = areas.urbanid where stime.arrivaltime>0 and stime.departuretime>0 group by stime.stop_agencyid, stime.stop_id, stop.location), undupblocks as "
    		+ "(select block.population"+popYear+" as population, max(stops.service) as service from census_blocks block inner join stops on st_dwithin(block.location, stops.location, "+String.valueOf(x)
    		+") inner join areas on block.urbanid = areas.urbanid group by blockid), undupblocksatlos as (select block.population"+popYear+" as population from census_blocks block inner join stopsatlos "
    		+ "on st_dwithin(block.location, stopsatlos.location, "+String.valueOf(x)+") inner join areas on block.urbanid = areas.urbanid group by blockid), svchrs as (select "
    		+ "COALESCE(min(arrival),-1) as fromtime, COALESCE(max(departure),-1) as totime from stops), concom as (select distinct map.urbanid from census_urbans_trip_map map "
    		+ "inner join trips on trips.aid=map.agencyid and trips.tripid=map.tripid), concomnames as (select array_agg(uname order by uname)::text as connections from concom "
    		+ "inner join census_urbans using(urbanid)), upopatlos as (select COALESCE(sum(population),0) as upoplos from undupblocksatlos), upopserved as (select "
    		+ "COALESCE(sum(population*service),0) as uspop from undupblocks), svcdays as (select COALESCE(array_agg(distinct day)::text,'-') as svdays from svcids) select "
    		+ "svcmiles, svchours, svcstops, upoplos, uspop, svdays, fromtime, totime, connections from service inner join upopatlos on true inner join upopserved on true "
    		+ "inner join svcdays on true inner join svchrs on true inner join concomnames on true";
    //System.out.println(query);
      try {
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);        
        while ( rs.next() ) {
        	response.put("svcmiles", String.valueOf(rs.getFloat("svcmiles")));
        	response.put("svchours", String.valueOf(Math.round(rs.getLong("svchours")/36.00)/100.00));
        	response.put("svcstops", String.valueOf(rs.getLong("svcstops")));
        	response.put("upopatlos", String.valueOf(rs.getLong("upoplos")));
        	//response.put("rpopatlos", String.valueOf(rs.getLong("rpoplos")));
        	response.put("uspop", String.valueOf(rs.getFloat("uspop")));
        	//response.put("rspop", String.valueOf(rs.getFloat("rspop")));
        	response.put("svcdays", String.valueOf(rs.getString("svdays")));
        	response.put("fromtime", String.valueOf(rs.getInt("fromtime")));
        	response.put("totime", String.valueOf(rs.getInt("totime")));
        	response.put("connections", rs.getString("connections"));        	                      
        }
        rs.close();
        stmt.close();        
      } catch ( Exception e ) {
        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
         
      }
      dropConnection(connection);      
      return response;
    }
	
	/////Agency Extended Report Queries
	
	/**
	 *Queries Stops count, unduplicated urban pop and rural pop within x meters of all stops, and route miles for a givn transit agency
	 */
	public static double[] stopsPopMiles(String agencyId, double x, int dbindex, String popYear) 
    {	
	  Connection connection = makeConnection(dbindex);
      Statement stmt = null;      
      String querytext = "with census as (select population"+popYear+" as population, poptype from census_blocks block inner join gtfs_stops stop on st_dwithin(block.location, stop.location, "+
      String.valueOf(x)+") inner join gtfs_stop_service_map map on map.stopid=stop.id and map.agencyid_def = stop.agencyid where map.agencyid= '" + agencyId +
      "' group by block.blockid), urbanpop as (select COALESCE(sum(population),0) upop from census where poptype = 'U'), ruralpop as (select COALESCE(sum(population),0) rpop " +
      "from census where poptype = 'R'), stopcount as (select count(stop.id) as stopscount from gtfs_stops stop inner join gtfs_stop_service_map map on map.stopid=stop.id and " +
      "map.agencyid_def = stop.agencyid where map.agencyid= '"+agencyId+"'), routes as (select max(round((trip.length+trip.estlength)::numeric,2)) as length, trip.route_id as" +
      " routeid from gtfs_trips trip where trip.agencyid='"+agencyId+"' group by trip.route_id), rmiles as (select sum(length) as rtmiles from routes) select COALESCE(stopscount,0) as "+
      "stopscount, COALESCE(upop,0) as urbanpop, COALESCE(rpop,0) as ruralpop, COALESCE(rtmiles,0) as rtmiles from stopcount inner join urbanpop on true inner join " +
      "ruralpop on true inner join rmiles on true";
      double[] results = new double[4];
      //System.out.println(querytext);
      try {
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(querytext);        
        while ( rs.next() ) {
        	results[0] = rs.getLong("stopscount");
        	results[1] = rs.getLong("urbanpop"); 
        	results[2] = rs.getLong("ruralpop");
        	results[3] = Math.round(rs.getFloat("rtmiles")*100.0)/100.0;        	
        }
        rs.close();
        stmt.close();        
      } catch ( Exception e ) {
        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
         
      }
      dropConnection(connection);      
      return results;
    }
	
	/**
	 *Queries Service miles, service hours, service stops, served population (urban and rural), service days, and hours of service for a given agency id. 
	 *keys are: svcmiles, svchours, svcstops, uspop, rspop, svcdays, fromtime, and totime
	 */
	public static HashMap<String, String> AgencyServiceMetrics(String[] date, String[] day, String[] fulldates, String agencyId, double x, int dbindex, String popYear) 
    {	
	  Connection connection = makeConnection(dbindex);
      Statement stmt = null;
      HashMap<String, String> response = new HashMap<String, String>();      
      String query = "with svcids as (";
      for (int i=0; i<date.length; i++){
    	  query+= "(select serviceid_agencyid, serviceid_id, '"+fulldates[i]+"' as day from gtfs_calendars gc where startdate::int<="+date[i]+" and enddate::int>="+date[i]
    			+" and "+day[i]+" = 1 and serviceid_agencyid||serviceid_id not in (select serviceid_agencyid||serviceid_id from gtfs_calendar_dates where date='"+date[i]
    			+"' and exceptiontype=2) union select serviceid_agencyid, serviceid_id, '"+fulldates[i]+"' from gtfs_calendar_dates gcd where date='"+date[i]+"' and exceptiontype=1)";
    	  if (i+1<date.length)
				query+=" union all ";
		}      
    query +="), trips as (select trip.agencyid as aid, trip.id as tripid, trip.route_id as routeid, round((trip.length+trip.estlength)::numeric,2) as length,trip.tlength as tlength,"
    		+ " trip.stopscount as stops from svcids inner join gtfs_trips trip using(serviceid_agencyid, serviceid_id) where trip.agencyid ='"+agencyId+"'), service as (select "
    		+ "COALESCE(sum(length),0) as svcmiles, COALESCE(sum(tlength),0) as svchours, COALESCE(sum(stops),0) as svcstops from trips), stops as (select stime.stop_agencyid as "
    		+ "aid, stime.stop_id as stopid, min(stime.arrivaltime) as arrival, max(stime.departuretime) as departure, stop.location, count(trips.aid) as service from gtfs_stops "
    		+ "stop inner join gtfs_stop_times stime on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id inner join trips on stime.trip_agencyid =trips.aid "
    		+ "and stime.trip_id=trips.tripid where stime.arrivaltime>0 and stime.departuretime>0 group by stime.stop_agencyid, stime.stop_id, stop.location), undupblocks as "
    		+ "(select block.population"+popYear+" as population, block.poptype, max(stops.service) as service from census_blocks block inner join stops on st_dwithin(block.location, stops.location, "+
    		String.valueOf(x)+") group by blockid), svchrs as (select COALESCE(min(arrival),-1) as fromtime, COALESCE(max(departure),-1) as totime from stops), upopserved as "
    		+ "(select COALESCE(sum(population*service),0) as uspop from undupblocks where poptype='U'), rpopserved as (select COALESCE(sum(population*service),0) as rspop from "
    		+ "undupblocks where poptype='R'), svcdays as (select COALESCE(array_agg(distinct day)::text,'-') as svdays from svcids) select svcmiles, svchours, svcstops, uspop, "
    		+ "rspop, svdays, fromtime, totime from service inner join upopserved on true inner join rpopserved on true inner join svcdays on true inner join svchrs on true";
    //System.out.println(query);
      try {
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);        
        while ( rs.next() ) {
        	response.put("svcmiles", String.valueOf(rs.getFloat("svcmiles")));
        	response.put("svchours", String.valueOf(Math.round(rs.getLong("svchours")/36.00)/100.00));
        	response.put("svcstops", String.valueOf(rs.getLong("svcstops")));        	
        	response.put("uspop", String.valueOf(rs.getFloat("uspop")));
        	response.put("rspop", String.valueOf(rs.getFloat("rspop")));
        	response.put("svcdays", String.valueOf(rs.getString("svdays")));
        	response.put("fromtime", String.valueOf(rs.getInt("fromtime")));
        	response.put("totime", String.valueOf(rs.getInt("totime")));        	       	                      
        }
        rs.close();
        stmt.close();        
      } catch ( Exception e ) {
        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
         
      }
      dropConnection(connection);      
      return response;
    }
	
	//////STATEWIDE EXTENDED REPORT QUERIES
	
	/**
	 *Queries Service miles, service hours, service stops, served pop at level of service (urban and rural), served population (urban and rural), service days, and hours of service
	 * for the whole state. keys are: svcmiles, svchours, svcstops, upopatlos, rpopatlos, uspop, rspop, svcdays, fromtime, totime.
	 */
	public static HashMap<String, String> StatewideServiceMetrics(String[] date, String[] day, String[] fulldates, String username, int LOS, double x, int dbindex, String popYear) 
    {	
	  Connection connection = makeConnection(dbindex);
      Statement stmt = null;
      HashMap<String, String> response = new HashMap<String, String>();      
      
      String query = "with aids as (select distinct agency_id as aid from gtfs_selected_feeds where username='"+username+"'), svcids as (";
      for (int i=0; i<date.length; i++){
    	  query+= "(select serviceid_agencyid, serviceid_id, '"+fulldates[i]+"' as day from gtfs_calendars gc inner join aids on gc.serviceid_agencyid = aids.aid where "
    	  		+ "startdate::int<="+date[i]+" and enddate::int>="+date[i]+" and "+day[i]+" = 1 and serviceid_agencyid||serviceid_id not in (select "
    	  		+ "serviceid_agencyid||serviceid_id from gtfs_calendar_dates where date='"+date[i]+"' and exceptiontype=2) union select serviceid_agencyid, serviceid_id, '"
    	  		+fulldates[i]+"' from gtfs_calendar_dates gcd inner join aids on gcd.serviceid_agencyid = aids.aid where date='"+date[i]+"' and exceptiontype=1)";
    	  if (i+1<date.length)
				query+=" union all ";
		}      
    query +="), trips as (select agencyid as aid, id as tripid, route_id as routeid, round((estlength+length)::numeric,2) as length, tlength, stopscount as stops "
    		+ "from svcids inner join gtfs_trips trip using(serviceid_agencyid, serviceid_id)), service as (select COALESCE(sum(length),0) as svcmiles, COALESCE(sum(tlength),0) "
    		+ "as svchours, COALESCE(sum(stops),0) as svcstops from trips), stopsatlos as (select stime.stop_agencyid as aid, stime.stop_id as stopid, stop.location as location,"
    		+ " count(trips.aid) as service from gtfs_stops stop inner join gtfs_stop_times stime on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id inner join "
    		+ "trips on stime.trip_agencyid =trips.aid and stime.trip_id=trips.tripid group by stime.stop_agencyid, stime.stop_id, stop.location having count(trips.aid)>="+LOS+"), "
    		+ "stops as (select stime.stop_agencyid as aid, stime.stop_id as stopid, stop.location as location, min(stime.arrivaltime) as arrival, max(stime.departuretime) as "
    		+ "departure, count(trips.aid) as service from gtfs_stops stop inner join gtfs_stop_times stime on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id "
    		+ "inner join trips on stime.trip_agencyid =trips.aid and stime.trip_id=trips.tripid where stime.arrivaltime>0 and stime.departuretime>0 group by stime.stop_agencyid, "
    		+ "stime.stop_id, stop.location),undupblocks as (select block.population"+popYear+" as population, block.poptype, max(stops.service) as service from census_blocks block inner join stops on "
    		+ "st_dwithin(block.location, stops.location,"+String.valueOf(x)+") group by blockid), undupblocksatlos as (select block.population"+popYear+" as population, block.poptype from census_blocks "
    		+ "block inner join stopsatlos on st_dwithin(block.location, stopsatlos.location,"+String.valueOf(x)+") group by blockid), svchrs as (select COALESCE(min(arrival),-1)"
    		+ " as fromtime, COALESCE(max(departure),-1) as totime from stops), upopatlos as (select COALESCE(sum(population),0) as upoplos from undupblocksatlos where poptype='U')"
    		+ ", rpopatlos as (select COALESCE(sum(population),0) as rpoplos from undupblocksatlos where poptype='R'), upopserved as (select COALESCE(sum(population*service),0) "
    		+ "as uspop from undupblocks where poptype='U'), rpopserved as (select COALESCE(sum(population*service),0) as rspop from undupblocks where poptype='R'), svcdays as "
    		+ "(select COALESCE(array_agg(distinct day)::text,'-') as svdays from svcids) select svcmiles, svchours, svcstops, upoplos, rpoplos, uspop, rspop, svdays, fromtime, "
    		+ "totime from service inner join upopatlos on true inner join rpopatlos on true inner join upopserved on true inner join rpopserved on true inner join svcdays "
    		+ "on true inner join svchrs on true";
    // System.out.println(query);
      try {
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);        
        while ( rs.next() ) {
        	response.put("svcmiles", String.valueOf(rs.getFloat("svcmiles")));
        	response.put("svchours", String.valueOf(Math.round(rs.getLong("svchours")/36.00)/100.00));
        	response.put("svcstops", String.valueOf(rs.getLong("svcstops")));
        	response.put("upopatlos", String.valueOf(rs.getLong("upoplos")));
        	response.put("rpopatlos", String.valueOf(rs.getLong("rpoplos")));
        	response.put("uspop", String.valueOf(rs.getFloat("uspop")));
        	response.put("rspop", String.valueOf(rs.getFloat("rspop")));
        	response.put("svcdays", String.valueOf(rs.getString("svdays")));
        	response.put("fromtime", String.valueOf(rs.getInt("fromtime")));
        	response.put("totime", String.valueOf(rs.getInt("totime")));        	        	                      
        }
        rs.close();
        stmt.close();        
      } catch ( Exception e ) {
        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
         
      }
      dropConnection(connection);      
      return response;
    }
	
	/**
	 *returns sum of unduplicated population within x distance of all stops in the state
	 */
	public static long PopWithinX(double x, String username, int dbindex, String popYear){
		long response = 0;
		Connection connection = makeConnection(dbindex);
		Statement stmt = null;
		try{
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery( "with aids as (select distinct agency_id as aid from gtfs_selected_feeds where username='"+username+"'), pops as (select population"+popYear+" as population "
					+ "from census_blocks block inner join gtfs_stops stop on st_dwithin(block.location,stop.location,"+String.valueOf(x)+") inner join aids on "
					+ "stop.agencyid=aids.aid group by block.blockid) select sum(population) as pop from pops");	
			while ( rs.next() ) {
				response = rs.getLong("pop");
			}
		} catch ( Exception e ) {
	        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
	         
	      }
		dropConnection(connection);
		return response;
	}
	
	/**
	 * returns the population within x distance of all stops in the state that are located in urban areas
	 * with over 50k population as well as the population of urban areas with less than 50k pop.
	 * @param x
	 * @param username
	 * @param dbindex
	 * @return
	 */
	public static long[] cutOff50(double x, String username, int dbindex, String popYear){
		long[] response = new long[4];
		Connection connection = makeConnection(dbindex);
		Statement stmt = null;
		try{
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery( "with aids as (select distinct agency_id as aid from gtfs_selected_feeds where username='" + username + "'), "
					+ "pops as (select population"+popYear+" as population, poptype, block.urbanid "
					+ "from census_blocks block inner join gtfs_stops stop on st_dwithin(block.location,stop.location," + String.valueOf(x) + ") inner join aids on "
					+ "stop.agencyid=aids.aid group by block.blockid), "
					+ "overfiftypop AS (select sum(pops.population) as overfiftypop from pops INNER JOIN census_urbans USING(urbanid) where census_urbans.population"+popYear+">50000), "
					+ "belowfiftypop AS (select sum(pops.population) as belowfiftypop from pops INNER JOIN census_urbans USING(urbanid) where census_urbans.population"+popYear+"<50000),"
					+ "totaloverfifty AS (select sum(population"+popYear+") as totaloverfifty from census_urbans WHERE census_urbans.population"+popYear+">50000), "
					+ "totalbelowfifty AS (select sum(population"+popYear+") as totalbelowfifty from census_urbans WHERE census_urbans.population"+popYear+"<50000)"
					+ "select * from belowfiftypop INNER JOIN overfiftypop ON TRUE INNER JOIN totaloverfifty ON TRUE INNER JOIN totalbelowfifty ON TRUE");	
			while ( rs.next() ) {
				response[0] = rs.getLong("overfiftypop");
				response[1] = rs.getLong("totaloverfifty");
				response[2] = rs.getLong("belowfiftypop");
				response[3] = rs.getLong("totalbelowfifty");
			}
		} catch ( Exception e ) {
	        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
	      }
		dropConnection(connection);
		return response;
	}
	/**
	 *returns route Miles for the whole state 
	 */
	public static double StateRouteMiles(String username, int dbindex){
		double response = 0;
		Statement stmt = null;
		Connection connection = makeConnection(dbindex);
		try{
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("with aids as (select distinct agency_id as aid from gtfs_selected_feeds where username='"+username+"'), trips as "
					+ "(select max(length+estlength) as rlength from gtfs_trips trip inner join aids on trip.serviceid_agencyid = aids.aid group by trip.route_agencyid, "
					+ "trip.route_id) select coalesce(sum(rlength), 0) as rtmiles from trips");	
			while ( rs.next() ) {
				response = rs.getDouble("rtmiles"); 				
			}
		} catch ( Exception e ) {
	        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
	         
	      }
		dropConnection(connection);
		return response;
	}
	
	
	/**
	 *returns sum of rural/urban population served at specified level of service for the whole state : ALREADY IMPLEMENTED IN EARLIER SERVICE QUERY
	 */
	/*public static long[] PopServedatLOS(double x, String date, String day, String username, int l, int dbindex){
		long[] response = new long[2];
		Statement stmt = null;
		Connection connection = makeConnection(dbindex);
		try{
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("with aids as (select agency_id as aid from gtfs_selected_feeds where username='"+username+"'), svcids as (select serviceid_agencyid, "
					+ "serviceid_id from gtfs_calendars gc inner join aids on gc.serviceid_agencyid = aids.aid where startdate::int<="+date+" and enddate::int>="+date+" and "+day
					+"=1 and serviceid_agencyid||serviceid_id not in (select serviceid_agencyid||serviceid_id from gtfs_calendar_dates where date='"+date+"' and exceptiontype=2) "
					+ "union select serviceid_agencyid, serviceid_id from gtfs_calendar_dates gcd inner join aids on gcd.serviceid_agencyid = aids.aid where date='"+date
					+"' and exceptiontype=1),trips as (select trip.agencyid as aid, trip.id as tripid from svcids inner join gtfs_trips trip using(serviceid_agencyid, "
					+ "serviceid_id)),stopsatlos as (select stime.stop_agencyid as aid, stime.stop_id as stopid, stop.location as location from gtfs_stops stop inner join "
					+ "gtfs_stop_times stime on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id inner join trips on stime.trip_agencyid =trips.aid and "
					+ "stime.trip_id=trips.tripid group by stime.stop_agencyid, stime.stop_id, stop.location having count(trips.aid)>="+l+" ),undupblocksatlos as "
					+ "(select block.population, block.poptype from census_blocks block inner join stopsatlos on st_dwithin(block.location, stopsatlos.location,"
					+String.valueOf(x)+") group by blockid),upopatlos as (select COALESCE(sum(population),0) as upoplos from undupblocksatlos where poptype='U'), rpopatlos as "
					+ "(select COALESCE(sum(population),0) as rpoplos from undupblocksatlos where poptype='R') select upoplos, rpoplos from upopatlos inner join rpopatlos on true");	
			while ( rs.next() ) {
				response[0] = rs.getLong("upoplos");
				response[0] = rs.getLong("rpoplos");
			}
		} catch ( Exception e ) {
	        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
	         
	      }
		dropConnection(connection);
		return response;
	}*/
	
	/**
	 *returns service miles in miles for the whole state for a single day in YYYYMMDD format and day of week in all lower case : ALREADY IMPLEMENTED IN EARLIER SERVICE QUERY
	 */
	/*public static double ServiceMiles(String date, String day, String username, int dbindex){
		double response = 0;
		Connection connection = makeConnection(dbindex);
		Statement stmt = null;
		try{
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery( "with aids as (select agency_id as aid from gtfs_selected_feeds where username='"+username+"'), svcids as (select serviceid_agencyid, "
					+ "serviceid_id from gtfs_calendars gc inner join aids on gc.serviceid_agencyid = aids.aid where startdate::int<="+date+" and enddate::int>="+date+" and "+day
					+"=1 and serviceid_agencyid||serviceid_id not in (select serviceid_agencyid||serviceid_id from gtfs_calendar_dates where date='"+date+"' and exceptiontype=2) "
					+ "union select serviceid_agencyid, serviceid_id from gtfs_calendar_dates gcd inner join aids on gcd.serviceid_agencyid = aids.aid where date='"+date
					+"' and exceptiontype=1) select sum(length+estlength) as svcmiles from svcids inner join gtfs_trips trip using(serviceid_agencyid, serviceid_id)");	
			while ( rs.next() ) {
				response = rs.getDouble("svcmiles");
			}
		} catch ( Exception e ) {
	        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
	         
	      }
		dropConnection(connection);
		return response;
	}*/
	
	/**
	 *returns service hours in seconds for the whole state for a single day in YYYYMMDD format and day of week in all lower case : ALREADY IMPLEMENTED IN EARLIER SERVICE QUERY
	 */
	/*public static long ServiceHours(String date, String day, String username, int dbindex){
		long response = 0;
		Connection connection = makeConnection(dbindex);
		Statement stmt = null;
		try{
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("with aids as (select agency_id as aid from gtfs_selected_feeds where username='"+username+"'), svcids as (select serviceid_agencyid, "
					+ "serviceid_id from gtfs_calendars gc inner join aids on gc.serviceid_agencyid = aids.aid where startdate::int<="+date+" and enddate::int>="+date+" and "+day
					+" = 1 and serviceid_agencyid||serviceid_id not in (select serviceid_agencyid||serviceid_id from gtfs_calendar_dates where date='"+date+"' and exceptiontype=2)"
					+ " union select serviceid_agencyid, serviceid_id from gtfs_calendar_dates gcd inner join aids on gcd.serviceid_agencyid = aids.aid where date='"+date
					+"' and exceptiontype=1) select sum(trip.tlength) as svchours from svcids inner join gtfs_trips trip using(serviceid_agencyid, serviceid_id)");	
			while ( rs.next() ) {
				response = rs.getLong("svchours");
			}
		} catch ( Exception e ) {
	        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
	         
	      }
		dropConnection(connection);
		return response;
	}*/
	
	/**
	 *returns min and max Hours of service in int time (epoch) fromat for a given date and day of week in an integer array : ALREADY IMPLEMENTED IN EARLIER SERVICE QUERY
	 */
	/*public static int[] HoursofService(String date, String day, int dbindex){
		int[] response = new int[2];
		Statement stmt = null;
		Connection connection = makeConnection(dbindex);
		try{
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery( "select min(arrivaltime) as start, max(departuretime) as finish from gtfs_stop_times stimes inner join "
					+ "(select agencyid, id from gtfs_trips where serviceid_agencyid||serviceid_id in "
					+ "(select  serviceid_agencyid||serviceid_id from gtfs_calendars where startdate::int<=" + date	+ " and enddate::int>="	+ date + " and " + day + " = 1 "
					+ "and serviceid_agencyid||serviceid_id not in (select serviceid_agencyid||serviceid_id from gtfs_calendar_dates where date='" + date
					+ "' and exceptiontype=2) union select serviceid_agencyid||serviceid_id from gtfs_calendar_dates where date='" + date + "' and exceptiontype=1))as trips "
					+ "on trips.agencyid = stimes.trip_agencyid and trips.id = stimes.trip_id where arrivaltime>=0 and departuretime>=0");	
			while ( rs.next() ) {
				response[0] = rs.getInt("start"); 
				response[1] = rs.getInt("finish"); 
			}
		} catch ( Exception e ) {
	        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
	         
	      }
		dropConnection(connection);
		return response;
	}*/
	
	////////AGGREGATED URBAN AREAS EXTENDED REPORT QUERIES

	
	/**
	 *Queries undupliated population within x meters of all stops in urban areas with population larger than pop
	 */
	public static long UrbanCensusbyPop(int pop, int dbindex, String username, double x) 
    {	
	  Connection connection = makeConnection(dbindex);      
      Statement stmt = null;
      long population = 0;
      try {
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery( "with aids as (select distinct agency_id as aid from gtfs_selected_feeds where username='"+username+"'),uids as (select urbanid from "
        		+ "census_urbans where population>="+String.valueOf(pop)+"),blocks as (select distinct block.blockid, population from census_blocks block inner join gtfs_stops stop"
        		+ " on st_dwithin(block.location,stop.location,"+String.valueOf(x)+") inner join uids on stop.urbanid=uids.urbanid and uids.urbanid=block.urbanid inner join aids on "
        		+ "stop.agencyid=aids.aid) select sum(population) as pop from blocks");        
        while ( rs.next() ) {
           population = rs.getLong("pop");                     
        }
        rs.close();
        stmt.close();
        //c.close();
      } catch ( Exception e ) {
        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
         
      }
      dropConnection(connection);      
      return population;
    }
	
	///////OTHER QUERIES FOR CONNECTED AGENCIES, CONNECTED NETWORKS AND TRANSIT HUBS REPORTS
	
	/**
	 *Queries agency clusters (connected agencies) and returns a list of all transit agencies with their connected agencies
	 */
	public static List<agencyCluster> agencyCluster(double dist, String username, int dbindex){
		List<agencyCluster> response = new ArrayList<agencyCluster>();
		Connection connection = makeConnection(dbindex);
		Statement stmt = null;
		try{
			stmt = connection.createStatement();
			String query = "with aids as (select agency_id as aid from gtfs_selected_feeds where username='" + username + "'),"
					+ " stops AS (SELECT map.agencyid, stops.location"
					+ " 	FROM gtfs_stops AS stops JOIN gtfs_stop_service_map AS map"
					+ " 	ON map.agencyid_def = stops.agencyid AND map.stopid = stops.id"
					+ " 	WHERE stops.agencyid IN (SELECT * FROM aids)),"
					+ " agencyshapes AS (SELECT agencyid, ST_COLLECT(location) AS shape FROM stops GROUP BY agencyid),"
					+ " connections AS (SELECT agencyshapes.agencyid, gtfs_agencies.name AS agencyname, stops.agencyid AS connectedagency"
					+ " 	FROM stops INNER JOIN agencyshapes ON ST_DWITHIN(agencyshapes.shape,stops.location," + dist + ")"
					+ " 	INNER JOIN gtfs_agencies ON agencyshapes.agencyid = gtfs_agencies.id"
					+ " 	WHERE stops.agencyid != agencyshapes.agencyid"
					+ " 	GROUP BY agencyshapes.agencyid, agencyshapes.shape, stops.agencyid, gtfs_agencies.name),"
					+  "connectedagencies AS (SELECT agencyid AS aid, agencyname AS name, count(connectedagency) AS size, array_agg(connectedagency) AS aids, array_agg(gtfs_agencies.name) AS names"
					+ "	FROM connections INNER JOIN gtfs_agencies ON connections.connectedagency = gtfs_agencies.id"
					+ " 	GROUP BY agencyid, agencyname),"
					+ " disconnectedagencies AS (SELECT agencies.id, agencies.name, 0, NULL::char varying[], NULL::char varying[] FROM gtfs_agencies AS agencies "
					+ "		WHERE agencies.id NOT IN (SELECT agencyid FROM connections) AND agencies.id IN (SELECT aid FROM aids)) "
					+ " SELECT * FROM connectedagencies UNION ALL SELECT * FROM disconnectedagencies";
			System.out.println(query);
			
			ResultSet rs = stmt.executeQuery(query);
			while ( rs.next() ) {
				agencyCluster instance = new agencyCluster();
				instance.agencyId = rs.getString("aid");
				instance.agencyName = rs.getString("name");
				if (!rs.getString("size").equals("0")){
					instance.clusterSize = rs.getLong("size");
					String[] buffer = (String[]) rs.getArray("aids").getArray();
					instance.agencyIds= Arrays.asList(buffer);
					buffer = (String[]) rs.getArray("names").getArray();
					instance.agencyNames= Arrays.asList(buffer);
//					buffer = (String[]) rs.getArray("min_gaps").getArray();
//					instance.minGaps= Arrays.asList(buffer);
				} else {
					instance.clusterSize = 0;
					instance.agencyIds= new ArrayList<String>();
					instance.agencyNames= new ArrayList<String>();
//					instance.minGaps= new ArrayList<String>();
				}
				
		        response.add(instance);
		        }
		} catch ( Exception e ) {
	        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
	         
	      }
		dropConnection(connection);
		return response;
	}
	/**
	 *Queries connected transit agencies and list of connections for a given transit agency (connected agencies extended report)
	 */
	public static List<agencyCluster> agencyClusterDetails(double dist, String agencyId, String username, int dbindex){
		List<agencyCluster> response = new ArrayList<agencyCluster>();
		Connection connection = makeConnection(dbindex);
		Statement stmt = null;
		try{
			stmt = connection.createStatement();
			String query = "with aids as (select agency_id as aid from gtfs_selected_feeds where username='" + username + "'), "
					+ "stops AS (SELECT map.agencyid, stops.location, stops.lat, stops.lon, name "
					+ "	FROM gtfs_stops AS stops JOIN gtfs_stop_service_map AS map "
					+ "	ON map.agencyid_def = stops.agencyid AND map.stopid = stops.id "
					+ "	WHERE stops.agencyid IN (SELECT * FROM aids)), "
					+ " agencyshape AS (SELECT agencyid, ST_COLLECT(location) AS shape FROM stops WHERE agencyid = '" + agencyId + "' GROUP BY agencyid), "
					+ " connections AS (SELECT agencyshape.agencyid, stops.agencyid AS connectedagency "
					+ "	FROM stops INNER JOIN agencyshape ON ST_DWITHIN(agencyshape.shape,stops.location," + dist + ") WHERE stops.agencyid != agencyshape.agencyid "
					+ "	GROUP BY agencyshape.agencyid, agencyshape.shape, stops.agencyid ), "
					+ " distances AS (SELECT connections.*, agencies.name AS agencyname, 3.28084*ST_DISTANCE(stops1.location,stops2.location)::NUMERIC AS dist, stops1.name AS name1, stops2.name AS name2, "
					+ "	array[stops1.lat,stops1.lon]::TEXT AS stop1loc, ARRAY[stops2.lat,stops2.lon]::TEXT AS stop2loc "
					+ "	FROM connections INNER JOIN stops AS stops1 USING(agencyid) "
					+ "	INNER JOIN stops AS stops2 ON connections.connectedagency = stops2.agencyid "
					+ "	INNER JOIN gtfs_agencies AS agencies ON connections.agencyid = agencies.id "
					+ "	WHERE stops1.agencyid = '" + agencyId + "' AND ST_DISTANCE(stops1.location,stops2.location) <" + dist + ") "
					+ " SELECT agencyid AS aid1, agencyname AS aname1, connectedagency AS aid2, gtfs_agencies.name AS aname2, COUNT(dist) AS size, ROUND(MIN(dist),2) AS min_gap, ROUND(MAX(dist),2) AS max_gap, ROUND(AVG(dist),2) AS avg_gap, "
					+ "	ARRAY_AGG(name1) AS names1, ARRAY_AGG(name2) AS names2, ARRAY_AGG(ROUND(dist,2)::TEXT) AS dists, ARRAY_AGG(stop1loc) AS locs1, ARRAY_AGG(stop2loc) AS locs2 "
					+ "	FROM distances INNER JOIN gtfs_agencies ON connectedagency = gtfs_agencies.id GROUP BY agencyid, connectedagency, agencyname, gtfs_agencies.name";
			System.out.println(query);
			ResultSet rs = stmt.executeQuery(query);
			while ( rs.next() ) {
				agencyCluster instance = new agencyCluster();
				instance.agencyId = rs.getString("aid2");
				instance.agencyName = rs.getString("aname2");
				instance.clusterSize = rs.getLong("size");
				instance.minGap = rs.getFloat("min_gap");
				instance.maxGap = rs.getFloat("max_gap");
				instance.meanGap = rs.getFloat("avg_gap");
				String[] buffer = (String[]) rs.getArray("names1").getArray();
				instance.sourceStopNames = Arrays.asList(buffer);
				buffer = (String[]) rs.getArray("names2").getArray();
				instance.destStopNames = Arrays.asList(buffer);
				buffer = (String[]) rs.getArray("dists").getArray();
				instance.minGaps = Arrays.asList(buffer);
				buffer = (String[]) rs.getArray("locs1").getArray();
				instance.sourceStopCoords = Arrays.asList(buffer);
				buffer = (String[]) rs.getArray("locs2").getArray();
				instance.destStopCoords = Arrays.asList(buffer);
//				buffer = (String[]) rs.getArray("sids2").getArray();
//				instance.destStopIds = Arrays.asList(buffer);
		        response.add(instance);
		        }
		} catch ( Exception e ) {
	        System.err.println( e.getClass().getName()+": "+ e.getMessage() );	         
	    }
		dropConnection(connection);
		return response;
	}
	
	/**
	 * Queries stops of a given agency.
	 * Used to generate Connecteg Agencies On-map report,
	 */
	public static CAStopsList getAgenStops(String agencyId, int dbindex){
		CAStopsList result = new CAStopsList();
		Connection connection = makeConnection(dbindex);
		Statement stmt = null;
		
		try {
			stmt = connection.createStatement();
			String query = "select map.agencyid as agencyid,gtfs_agencies.name as agencyname, stop.lat, stop.lon, stop.name as stopname, stop.id as stopid, stop.url, location "
					+ " from gtfs_stops stop inner join gtfs_stop_service_map map on map.agencyid_def=stop.agencyid and map.stopid=stop.id "
					+ " inner join gtfs_agencies on gtfs_agencies.id=map.agencyid where map.agencyid='"+ agencyId + "'";
			
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()){
				CAStop instance = new CAStop();
				instance.id = rs.getString("stopid");
				instance.name = rs.getString("stopname");
				instance.agencyId = rs.getString("agencyid");
				instance.agencyName = rs.getString("agencyname");
				instance.lat = rs.getString("lat");
				instance.lon = rs.getString("lon");
				result.stopsList.add(instance);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 *Queries frequency of service for all stops in the database for a set of dates and days
	 */
	public static HashMap<String, Integer> stopFrequency(String agency, String[] date, String[] day, String username, int dbindex){				
		HashMap<String, Integer> response = new HashMap<String, Integer>();
		Connection connection = makeConnection(dbindex);
		String agencyFilter = "";
		if (agency != null){
			agencyFilter = " AND agency_id IN (SELECT defaultid FROM gtfs_agencies WHERE id='" + agency + "')";
		}
		String mainquery ="with aids as (select distinct agency_id as aid from gtfs_selected_feeds where username='"+username+"'" + agencyFilter + "), svcids as (";
		Statement stmt = null;
		for (int i=0; i<date.length; i++){
			mainquery+= "(select serviceid_agencyid, serviceid_id from gtfs_calendars gc inner join aids on gc.serviceid_agencyid = aids.aid where startdate::int<="+date[i]
					+" and enddate::int>="+date[i]+" and "+day[i]+"=1 and serviceid_agencyid||serviceid_id not in (select serviceid_agencyid||serviceid_id from "
					+ "gtfs_calendar_dates where date='"+date[i]+"' and exceptiontype=2) union select serviceid_agencyid as aid, serviceid_id as sid from gtfs_calendar_dates gcd "
					+ "inner join aids on gcd.serviceid_agencyid = aids.aid where date='"+date[i]+"' and exceptiontype=1)";
			if (i+1<date.length)
				mainquery+=" union all ";
		}
		mainquery +="), trips as (select agencyid as aid, id as tripid from svcids inner join gtfs_trips trip using(serviceid_agencyid, serviceid_id)), "
				+ "stopservices0 as (select stime.stop_agencyid as aid, stime.stop_agencyid||stime.stop_id as stopid, COALESCE(count(trips.aid),0) as service "
				+ "		from gtfs_stop_times stime JOIN trips on stime.trip_agencyid =trips.aid and stime.trip_id=trips.tripid "
				+ "		group by stime.stop_agencyid, stime.stop_id), "
				+ "stopservices1 as (select stop_agencyid as aid, stop_agencyid||stop_id as stopid, 0 as service "
				+ "		FROM gtfs_stop_times  where stop_agencyid||stop_id NOT IN (SELECT stopid FROM stopservices0) "
				+ "		group by stop_agencyid, stop_id), "
				+ "stopservices as (select * from stopservices0 UNION ALL select * from stopservices1)"
				+ " select stopservices.stopid, stopservices.service from aids INNER JOIN stopservices USING(aid)";
		System.out.println("visit frequency: " + mainquery);
			try{
				stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(mainquery);
				while (rs.next()) {
					response.put(rs.getString("stopid"), rs.getInt("service"));
			        }
				rs.close();
				stmt.close();
			} catch ( Exception e ) {
		        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		         
		      }
						
		dropConnection(connection);
		return response;
	}
	
	/**
	 * A hashmap of the clusters is generated (containing the AgencyIDs and IDs of the stops in the cluster)
	 * @param radius
	 * @param dbindex
	 * @param username
	 * @return HashMap<String, HashSet<String>>
	 */
	public static HashMap<String, KeyClusterHashMap> getClusters(double radius, int dbindex, String username){
		HashMap<String, KeyClusterHashMap> y = new HashMap<String, KeyClusterHashMap>();
		String query = "WITH aids AS (SELECT distinct agency_id AS aid FROM gtfs_selected_feeds WHERE username='" + username + "'), "
				+ " stops0 AS (SELECT stops1.id AS stop1, stops2.id AS stop2,stops1.id || ':' || stops1.agencyid AS clusterid, stops1.agencyid AS agencyid1, stops2.id || ':' || stops2.agencyid AS stop, stops2.agencyid AS agencyid2"
				+ " FROM gtfs_stops AS stops1 LEFT JOIN gtfs_stops AS stops2	"
				+ " ON ST_Dwithin(stops1.location, stops2.location, " + radius + ")),"
				+ " stops1 AS (SELECT stops0.* FROM stops0 INNER JOIN aids ON agencyid1 IN (aids.aid)),"
				+ " stops2 AS (SELECT stops1.* FROM stops1 INNER JOIN aids ON agencyid2 IN (aids.aid)),"
				+ " stops3 AS (SELECT stops2.* FROM stops2 INNER JOIN gtfs_stop_service_map AS map ON stop1=map.stopid AND stops2.agencyid1=map.agencyid_def),"
				+ " stops4 AS (SELECT stops3.* FROM stops3 INNER JOIN gtfs_stop_service_map AS map ON stop2=map.stopid AND stops3.agencyid2=map.agencyid_def)"
				+ " SELECT stops4.clusterid, array_agg(stop) AS stops, array_agg(distinct agencyid2) AS agencies FROM stops4 GROUP BY clusterid"; 
		System.out.println(query);
		Statement stmt = null;
		try {
			Connection connection = makeConnection(dbindex);			
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);				
			while(rs.next()){
				KeyClusterHashMap e = new KeyClusterHashMap();
				String[] stops = (String[]) rs.getArray("stops").getArray();
				e.values.addAll(new HashSet<String>(Arrays.asList(stops)));
				String[] agencies = (String[]) rs.getArray("agencies").getArray();
				e.keyAgencyIDs.addAll(Arrays.asList(agencies));
				y.put(rs.getString("clusterid"), e);
			}
			dropConnection(connection);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return y;
	}
	
	/**
	 * Generate additional metrics for the a given cluster.
	 * @param x
	 * @param dbindex
	 * @param radius
	 * @return HubCluster
	 * @throws SQLException 
	 */
	/*public static HubsClusterList getClusterData(HashMap<String, HashSet<String>> x, String[] dates, String[] days, int dbindex, double popRadius, String username) throws SQLException{
		HubsClusterList output = new HubsClusterList();
		HashMap<String, Integer> serviceMap = stopFrequency(dates, days, username, dbindex);
		String query = new String();
		Connection connection = makeConnection(dbindex);
		Statement stmt = null;
		stmt = connection.createStatement();
		
		for (Entry<String, HashSet<String>> entry : x.entrySet()){
			HubCluster response = new HubCluster();
			List<String> agencyids = new ArrayList<String>();
			List<String> stopids = new ArrayList<String>();
			int visits = 0;
			for(String instance : entry.getValue()){
				String[] temp = instance.split(":");
				stopids.add(temp[0]);
				agencyids.add(temp[1]);
				int stopVisits = serviceMap.get(temp[1]+temp[0]);
				response.stopsVisits.add(stopVisits);
				visits += stopVisits;
			}
			
			query = "WITH list AS (VALUES ";
			String temp = "";
			for (int i=0; i<stopids.size(); i++){
				temp += "('" + stopids.get(i) + "','" + agencyids.get(i) + "'),";
			}
			query += temp.substring(0, temp.length()-1);
			query += "), "
					+ "stops AS (SELECT gtfs_stops.* FROM gtfs_stops INNER JOIN list ON id = column1 AND agencyid = column2), "
					+ "stopsarray0 AS (SELECT stops.id, stops.name, map.agencyid, lat, lon FROM stops INNER JOIN gtfs_stop_service_map AS map ON stops.id = map.stopid AND stops.agencyid = map.agencyid_def),"
					+ "stopsarray1 AS (SELECT stopsarray0.*, gtfs_agencies.name AS stopsagenciesnames FROM stopsarray0 INNER JOIN gtfs_agencies ON stopsarray0.agencyid = gtfs_agencies.id),"
					+ "stopsarray AS (SELECT array_agg(agencyid) AS stopsagencyids, array_agg(stopsagenciesnames) AS stopsagenciesnames, array_agg(id) AS stopsids, array_agg(name) AS stopsnames ,array_agg(lat) AS stopslats,array_agg(lon) AS stopslons FROM stopsarray1),"
					+ "stopscount AS (SELECT count(distinct agencyid||id) AS stopscount FROM stops), "
					+ "clustercoor AS (SELECT avg(stops.lat) AS lat, avg(stops.lon) AS lon FROM stops),"
					+ "agenciesarray AS (SELECT array_agg(distinct stopsagenciesnames) AS agenciesnames FROM stopsarray1), "
					+ "agenciescount AS (SELECT count(distinct stopsagenciesnames) AS agenciescount FROM stopsarray1), "
					+ "tmproutes AS (SELECT map.* FROM gtfs_stop_route_map AS map INNER JOIN stops ON map.agencyid_def = stops.agencyid AND map.stopid = stops.id), "
					+ "routes AS (SELECT gtfs_routes.* FROM gtfs_routes INNER JOIN tmproutes ON tmproutes.routeid = gtfs_routes.id AND tmproutes.agencyid = gtfs_routes.agencyid), "
					+ "routesarray0 AS (SELECT routes.*, gtfs_agencies.name AS agencyname FROM routes INNER JOIN gtfs_agencies ON routes.agencyid = gtfs_agencies.id), "
					+ "routesarray  AS (SELECT COALESCE(array_agg(agencyid),'{N/A}') AS routesagencies, COALESCE(array_agg(agencyname),'{N/A}') AS routesagenciesnames, COALESCE(array_agg(id),'{N/A}') AS routesids, COALESCE(array_agg(shortname),'{N/A}') AS routesshortnames, COALESCE(array_agg(longname),'{N/A}') AS routeslongnames FROM routesarray0),  "
					+ "routescount AS (SELECT count(distinct agencyid||id) AS routescount FROM routes), "
					+ "counties AS (SELECT counties.countyid, counties.cname FROM census_counties AS counties INNER JOIN stops ON LEFT(stops.blockid,5) = counties.countyid), "
					+ "countiesarray AS (SELECT COALESCE(array_agg(distinct countyid),'{N/A}') AS countiesids, COALESCE(array_agg(distinct cname),'{N/A}') AS countiesnames FROM counties), "
					+ "countiescount AS (SELECT count(distinct counties.countyid) AS countiescount FROM counties), "
					+ "urbanarray AS (SELECT COALESCE(array_agg(distinct stops.urbanid),'{N/A}') AS urbanids, COALESCE(array_agg(distinct uname),'{N/A}') AS urbannames FROM census_urbans INNER JOIN stops ON census_urbans.urbanid = stops.urbanid),"
					+ "regionsarray  AS (SELECT  COALESCE(array_agg(distinct ' Region '||regionid),'{N/A}') AS regionids FROM stops),"
					+ "pop0 AS (SELECT distinct census_blocks.blockid, population FROM census_blocks INNER JOIN stops ON ST_Dwithin(census_blocks.location, stops.location, " + popRadius + ")), "
					+ "pop AS (SELECT COALESCE(sum(population),0) AS pop FROM pop0)"
					+ "SELECT clustercoor.*, stopscount.*, agenciescount.*, pop.* AS pop, regionsarray.*, urbanarray.*, countiesarray.*, agenciesarray.*, countiescount.*, routescount.*, stopsarray.*, routesarray.* FROM stopsarray CROSS JOIN stopscount CROSS JOIN agenciesarray CROSS JOIN routesarray CROSS JOIN routescount CROSS JOIN countiescount CROSS JOIN countiesarray CROSS JOIN pop CROSS JOIN clustercoor CROSS JOIN urbanarray CROSS JOIN regionsarray CROSS JOIN agenciescount";
			System.out.println(query);			
			try {
				
				ResultSet rs = stmt.executeQuery(query);
					
				while(rs.next()){
					response.lat = rs.getString("lat");
					response.lon = rs.getString("lon");
					response.agenciescount = rs.getString("agenciescount");
					response.countiescount = rs.getString("countiescount");
					response.pop = rs.getString("pop");
					response.stopscount = rs.getString("stopscount");
					response.routescount = rs.getString("routescount");
					String[] tempCountiesNames = (String[]) rs.getArray("countiesnames").getArray();
					response.countiesNames =  Arrays.asList(tempCountiesNames);
					String[] tempAgenciesNames = (String[]) rs.getArray("agenciesnames").getArray();
					response.agenciesNames =  Arrays.asList(tempAgenciesNames);
					String[] tempUrbanNames = (String[]) rs.getArray("urbannames").getArray();
					response.urbanNames = Arrays.asList(tempUrbanNames);
					String[] tempRoutesAgencies = (String[]) rs.getArray("routesagencies").getArray();
					response.routesAgencies = Arrays.asList(tempRoutesAgencies);
					String[] tempRoutesAgenciesNames = (String[]) rs.getArray("routesagenciesnames").getArray();
					response.routesAgenciesNames = Arrays.asList(tempRoutesAgenciesNames);
					String[] tempRoutesIDs = (String[]) rs.getArray("routesids").getArray();
					response.routesIDs = Arrays.asList(tempRoutesIDs);
					String[] tempRoutesShortnames = (String[]) rs.getArray("routesshortnames").getArray();
					response.routeShortnames = Arrays.asList(tempRoutesShortnames);
					String[] tempRoutesLongnames = (String[]) rs.getArray("routeslongnames").getArray();
					response.routesLongnames = Arrays.asList(tempRoutesLongnames);
					String[] tempStopsIDs = (String[]) rs.getArray("stopsids").getArray();
					response.stopsIDs =  Arrays.asList(tempStopsIDs);
					String[] tempStopsAgencies = (String[]) rs.getArray("stopsagencyids").getArray();
					response.stopsAgencies = Arrays.asList(tempStopsAgencies);
					String[] tempStopsAgenciesNames = (String[]) rs.getArray("stopsagenciesnames").getArray();
					response.stopsAgenciesNames = Arrays.asList(tempStopsAgenciesNames);
					String[] tempStopsNames = (String[]) rs.getArray("stopsnames").getArray();
					response.stopsNames = Arrays.asList(tempStopsNames);
					String[] tempregionsIDs = (String[]) rs.getArray("regionids").getArray();
					response.regionsIDs = Arrays.asList(tempregionsIDs);
					Double[] tempStopsLats = (Double[]) rs.getArray("stopslats").getArray();
					response.stopsLats = Arrays.asList(tempStopsLats);
					Double[] tempStopsLons = (Double[]) rs.getArray("stopslons").getArray();
					response.stopsLons =  Arrays.asList(tempStopsLons);					
					response.visits = visits + "";
					
					output.Clusters.add(response);
				}				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}				
		}		
		dropConnection(connection);
		return output;
	}
	*/
	/**
	 *Queries the transit part of the on map report	
	 */
	public static MapTransit onMapStops(String[] date, String[] day, String username, double d, double[] lat, double[] lon, int dbindex) {
		CoordinateReferenceSystem sourceCRS = null;
		CoordinateReferenceSystem targetCRS = null;
		MathTransform transform = null;
		try {
			sourceCRS = CRS.decode("EPSG:4326");
			targetCRS = CRS.decode("EPSG:2993");
			transform = CRS.findMathTransform(sourceCRS, targetCRS);
		} catch (NoSuchAuthorityCodeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (FactoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		//HashMap<String, Integer> response = new HashMap<String, Integer>();
		MapTransit response = new MapTransit();		
		int TotalStops = 0;
		//int TotalRoutes = 0;
		ArrayList<String> RouteIdList = new ArrayList<String>();
		ArrayList<String> AllRouteIds = new ArrayList<String>();
		int ServiceStops = 0;
		double FareSum = 0;
		ArrayList<Double> Fares = new ArrayList<Double>();
		Collection<MapAgency> agencies = new ArrayList<MapAgency>();
		MapAgency instance = new MapAgency();
		ArrayList<MapRoute> Routes = new ArrayList<MapRoute>();
		Collection<MapStop> Stops = new ArrayList<MapStop>();
		Connection connection = makeConnection(dbindex);		
		//((org.postgresql.PGConnection)connection).addDataType("geometry",Class.forName("org.postgis.PGgeometry"));
		String mainquery = "with aids as (select distinct agency_id as aid from gtfs_selected_feeds where username='"+username+"'), svcids as (";
		for (int i=0; i<date.length; i++){
			mainquery+= "(select  serviceid_agencyid, serviceid_id from gtfs_calendars gc inner join aids on gc.serviceid_agencyid = aids.aid where startdate::int<="+date[i]
					+" and enddate::int>="+date[i]+" and "+day[i]+" = 1 and serviceid_agencyid||serviceid_id not in (select serviceid_agencyid||serviceid_id from "
					+ "gtfs_calendar_dates where date='"+date[i]+"' and exceptiontype=2) union select serviceid_agencyid, serviceid_id from gtfs_calendar_dates gcd inner join "
					+ "aids on gcd.serviceid_agencyid = aids.aid where date='"+date[i]+"' and exceptiontype=1)";
			if (i+1<date.length)
				mainquery+=" union all ";
		}
		mainquery+="), trips as (select trip.agencyid as aid, trip.id as tripid, trip.uid as uid, trip.route_id as routeid, round((trip.length+trip.estlength)::numeric,2)::varchar as length, "+
				"trip.routeshortname as rname, trip.epshape as shape, count(svcids.serviceid_id) as service from gtfs_trips trip inner join svcids "+
				"using(serviceid_agencyid, serviceid_id) group by trip.agencyid, trip.id),fare as (select fare.route_agencyid as aid, fare.route_id as routeid ,"
				+ " avg(fareat.price) as price from gtfs_fare_rules fare inner join gtfs_fare_attributes fareat on fare.fare_agencyid = fareat.agencyid and fare.fare_id=id "
				+ "group by fare.route_agencyid, fare.route_id),trip_fare as (select trips.aid, trips.uid, trips.tripid, trips.routeid, trips.rname, trips.length, trips.shape, "
				+ "trips.service, COALESCE(fare.price::text,'-') as price from trips left join fare using (aid, routeid)),stopids as (select trip_fare.aid, "
				+ "array_agg(concat(trip_fare.uid,chr(196),trip_fare.routeid,chr(196),trip_fare.rname,chr(196),trip_fare.length::text,chr(196),trip_fare.service::text,chr(196),trip_fare.price::text,chr(196),trip_fare.shape)) as routes, "
				+ "stimes.stop_id as stopid, stimes.stop_agencyid as aid_def, sum(service) as svc from trip_fare inner join gtfs_stop_times stimes on "
				+ "trip_fare.aid = stimes.trip_agencyid and trip_fare.tripid = stimes.trip_id group by aid_def, aid, stopid),";
		if (lat.length<2){
			Point point = geometryFactory.createPoint(new Coordinate(lat[0], lon[0]));
			Geometry targetGeometry = null;
			try {
				targetGeometry = JTS.transform( point, transform);
			} catch (MismatchedDimensionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			point = targetGeometry.getCentroid();
			point.setSRID(2993);
			mainquery+="stops as (select stopids.aid, stopids.routes, stopids.stopid, stop.name, array[stop.lat, stop.lon] as location, stop.location as loc, stopids.svc from stopids inner join gtfs_stops stop on "
					+ "st_dwithin(stop.location,ST_GeomFromText('"+point+"',2993), "+String.valueOf(d)+")=true "
					+ "where stop.id = stopids.stopid and stop.agencyid=stopids.aid_def),";			
		} else {
			Coordinate[] coords = new Coordinate[lat.length+1];
			for(int i=0;i<lat.length;i++){
				coords[i]= new Coordinate(lat[i], lon[i]);
			}
			coords[coords.length-1]= new Coordinate(lat[0], lon[0]);
			LinearRing ring = geometryFactory.createLinearRing( coords );
			LinearRing holes[] = null; 
			Polygon polygon = geometryFactory.createPolygon(ring, holes );
			Geometry targetGeometry = null;
			try {
				targetGeometry = JTS.transform( polygon, transform);
			} catch (MismatchedDimensionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			targetGeometry.setSRID(2993);
			mainquery+="stops as (select stopids.aid, stopids.routes, stopids.stopid, stop.name, array[stop.lat, stop.lon] as location, stop.location as loc, stopids.svc from "
					+ "stopids inner join gtfs_stops stop on st_within(stop.location,ST_GeomFromText('"+targetGeometry+"',2993))=true where stop.id = stopids.stopid and stop.agencyid=stopids.aid_def),";			
		}
		mainquery+="stopsas as (select agency.name as agency, stops.* from stops inner join gtfs_agencies agency on agency.id = stops.aid order by aid, stopid), "
				+ "stopsa as (select stopsas.*, array_agg(blockid) blks from stopsas , census_blocks where st_dwithin(census_blocks.location, loc,163) group by stopsas.agency, stopsas.aid, stopsas.routes, stopsas.stopid, stopsas.name, stopsas.location, stopsas.loc, stopsas.svc)"
				+ "select * from stopsa";
		System.out.println(mainquery);
		try{
			PreparedStatement stmt = connection.prepareStatement(mainquery);
			ResultSet rs = stmt.executeQuery();	
			String aid = "";
			String cid = "";
			//System.out.println("Query was run successfully.");
			int i = 0;
			MapStop stp;
			HashMap<String,Integer> blockSvc = new HashMap<String, Integer>();
			while (rs.next()) {
				String[] blks = (String[]) rs.getArray("blks").getArray();
				for(String blkid: blks){
					if(blockSvc.containsKey(blkid)){
						blockSvc.put(blkid,blockSvc.get(blkid)+1);
					}else{
						blockSvc.put(blkid, 1);
					}
				}
				
				cid = rs.getString("aid");
				stp = new MapStop();
				stp.AgencyId = cid;
				stp.Id = rs.getString("stopid");
				stp.Name = rs.getString("name");
				stp.Frequency = rs.getInt("svc");
				Double[] location = (Double[]) rs.getArray("location").getArray();
				stp.Lat = String.valueOf(location[0]);
				stp.Lng = String.valueOf(location[1]);				
				if (!(aid.equals(cid))){
					if (i>0){
						instance.MapRL = Routes;
						instance.MapSL = Stops;
						instance.ServiceStop = ServiceStops;
						instance.RoutesCount = RouteIdList.size();
						agencies.add(instance);
						TotalStops +=Stops.size();						
						RouteIdList = new ArrayList<String>();					
						instance = new MapAgency();
						Routes = new ArrayList<MapRoute>();
						Stops = new ArrayList<MapStop>();
						ServiceStops = 0;
					}					
					aid = cid;				
					instance.Id = aid;
					instance.Name = rs.getString("agency");					
				}	
				ServiceStops += stp.Frequency;								
				String[] stoproutes = (String[]) rs.getArray("routes").getArray();
				for (int j = 0; j<stoproutes.length; j++){
					String[] routeinfo = stoproutes[j].split(Character.toString((char)196), 7);
					if (!stp.MapRI.contains(routeinfo[1]))
							stp.MapRI.add(routeinfo[1]);
					if (!RouteIdList.contains(cid+routeinfo[1])){
							RouteIdList.add(cid+routeinfo[1]);							
					}
					if (!(AllRouteIds.contains(cid+routeinfo[1]))){
							AllRouteIds.add(cid+routeinfo[1]);
							if (!routeinfo[5].equals("-")){
								FareSum+=Double.parseDouble(routeinfo[5]);
								Fares.add(Double.parseDouble(routeinfo[5]));
							}
					}
					MapRoute rt = new MapRoute();
					rt.AgencyId = cid;
					rt.uid = routeinfo[0];
					rt.Id = routeinfo[1];
					if (!(Routes.contains(rt))){						
						rt.Name = routeinfo[2];
						rt.Length = Float.parseFloat(routeinfo[3]);
						rt.Frequency = Integer.parseInt(routeinfo[4]);
						if (!routeinfo[5].equals("-")){
							rt.Fare = "$"+String.valueOf(Math.round(Double.parseDouble(routeinfo[5])*100.00)/100.00);
						} else {
							rt.Fare = "NA";
						}											
						rt.Shape = routeinfo[6];
						rt.hasDirection = false;
						Routes.add(rt);							
					} else {						
						int index = Routes.indexOf(rt);
						rt = Routes.get(index);
						Routes.remove(index);
						rt.Frequency += Integer.parseInt(routeinfo[4]);
						if (rt.Length < Float.parseFloat(routeinfo[3])){
							rt.Length = Float.parseFloat(routeinfo[3]);
							rt.Shape = routeinfo[6];
						}						
						Routes.add(rt);
					}
				}
				Stops.add(stp);
				i++;
		        }		
			instance.MapRL = Routes;
			instance.MapSL = Stops;
			instance.ServiceStop = ServiceStops;
			instance.RoutesCount = RouteIdList.size();
			agencies.add(instance);
			TotalStops +=Stops.size();
			response.MapAL = agencies;
			response.TotalStops = String.valueOf(TotalStops);
			response.TotalRoutes = String.valueOf(AllRouteIds.size());
			response.MapBL = blockSvc.keySet();
			response.MapBLS = blockSvc.values();
			if (Fares.size()>0){
				response.AverageFare = String.valueOf(Math.round(FareSum*100.00/Fares.size())/100.00);
				Collections.sort(Fares);				
					if (Fares.size()%2==0){
						response.MedianFare = String.valueOf(Math.round((Fares.get(Fares.size()/2)+Fares.get((Fares.size()/2)-1))*100.00/2)/100.00);
					} else {
						response.MedianFare = String.valueOf(Math.round(Fares.get((int)(Math.ceil(Fares.size()/2)))*100.00)/100.00);
					}			
			}else {
				response.AverageFare = "0";
				response.MedianFare = "0";
			}			
			rs.close();
			stmt.close();
		} catch ( Exception e ) {
	        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
	         
	      }					
		dropConnection(connection);
		return response;
	}
	
	/**
	 *Queries the census population, employment, and title vi parts of the on map report	  
	 */
	public static MapGeo onMapBlocks(double d, double[] lat, double[] lon, int dbindex) {
		CoordinateReferenceSystem sourceCRS = null;
		CoordinateReferenceSystem targetCRS = null;
		MathTransform transform = null;;
		try {
			sourceCRS = CRS.decode("EPSG:4326");
			targetCRS = CRS.decode("EPSG:2993");
			transform = CRS.findMathTransform(sourceCRS, targetCRS);
		} catch (NoSuchAuthorityCodeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (FactoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		//HashMap<String, Integer> response = new HashMap<String, Integer>();
		MapGeo response = new MapGeo ();		
		ArrayList<MapCounty> Counties = new ArrayList<MapCounty>();
		ArrayList<MapBlock> Blocks = new ArrayList<MapBlock>();
		ArrayList<MapTract> Tracts = new ArrayList<MapTract>();
		long TotalUrbanPop = 0;
		long TotalRuralPop = 0;
		long TotalRac = 0;
		long TotalWac = 0;
		long CountyUrbanPop = 0;
		long CountyRuralPop = 0;
		long CountyRac = 0;
		long CountyWac = 0 ;
		long TractArea = 0;
		long TotalLandArea = 0;
		int TotalBlocks = 0;
		int TotalTracts = 0;
		MapCounty instance = new MapCounty();
		MapTract tinstance = new MapTract();
		Connection connection = makeConnection(dbindex);		
		String mainquery ="";		
		if (lat.length<2){
			Point point = geometryFactory.createPoint(new Coordinate(lat[0], lon[0]));
			Geometry targetGeometry = null;
			try {
				targetGeometry = JTS.transform( point, transform);
			} catch (MismatchedDimensionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			point = targetGeometry.getCentroid();
			point.setSRID(2993);
			mainquery += "with block as (select blockid, poptype as btype, waterarea as warea, landarea as barea, round((population2010/((landarea+waterarea)*3.86102e-7)),2) as density ,population as bpop, array[block.lat, block.lon] as blocation"
					+ " from census_blocks block where "
					+ "st_dwithin(ST_Transform(ST_SetSRID(ST_MakePoint(block.lon, block.lat),4326), 2993),ST_GeomFromText('"+point+"',2993), "+String.valueOf(d)+")=true),"
					+ "county as (select countyid, cname from census_counties), tract as (select tractid, population as tpop, landarea as tarea, array[tract.lat, tract.lon] as tlocation "
					+ "from census_tracts tract where "
					+ "st_dwithin(ST_Transform(ST_SetSRID(ST_MakePoint(tract.lon, tract.lat),4326), 2993),ST_GeomFromText('"+point+"',2993), "+String.valueOf(d)+")=true), ";			
		} else {
			Coordinate[] coords = new Coordinate[lat.length+1];
			for(int i=0;i<lat.length;i++){
				coords[i]= new Coordinate(lat[i], lon[i]);
			}
			coords[coords.length-1]= new Coordinate(lat[0], lon[0]);
			LinearRing ring = geometryFactory.createLinearRing( coords );
			LinearRing holes[] = null; 
			Polygon polygon = geometryFactory.createPolygon(ring, holes );
			Geometry targetGeometry = null;
			try {
				targetGeometry = JTS.transform( polygon, transform);
			} catch (MismatchedDimensionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			targetGeometry.setSRID(2993);
			mainquery += "with block as (select blockid, poptype as btype, waterarea as warea, landarea as barea, round((population2010/((landarea+waterarea)*3.86102e-7)),2) as density , population as bpop, array[block.lat, block.lon] as blocation"
					+ " from census_blocks block where "
					+ "st_within(ST_Transform(ST_SetSRID(ST_MakePoint(block.lon, block.lat),4326), 2993),ST_GeomFromText('"+targetGeometry+"',2993))=true),"
					+ "county as (select countyid, cname from census_counties), tract as (select tractid, population as tpop, landarea as tarea, array[tract.lat, tract.lon] as tlocation "
					+ "from census_tracts tract where "
					+ "st_within(ST_Transform(ST_SetSRID(ST_MakePoint(tract.lon, tract.lat),4326), 2993),ST_GeomFromText('"+targetGeometry+"',2993))=true), ";						
		}
		mainquery+= "blockcounty as (select blockid, btype, warea, barea, density, bpop, blocation, cname from block inner join county on left(blockid,5)= countyid), "
				+ "blocktract as (select blockid, btype, warea, barea, density, bpop, blocation, cname, tpop, tarea, tlocation from blockcounty left join tract on left(blockid,11)= tractid), "
				+ "blocktractrac as (select blocktract.*, c000 as brac, round((c000/((barea+warea)*3.86102e-7)),2) as racdensity from blocktract left join lodes_blocks_rac emprac on blocktract.blockid=emprac.blockid), "
				+ "blocktractracwac as (select blocktractrac.*, c000 as bwac, round((c000/((barea+warea)*3.86102e-7)),2) as wacdensity from blocktractrac left join lodes_blocks_wac empwac on blocktractrac.blockid=empwac.blockid) "
				+ "select * from blocktractracwac left join title_vi_blocks_float t6 on blocktractracwac.blockid=t6.blockid order by blocktractracwac.blockid";
		System.out.println(mainquery);
		try{
			PreparedStatement stmt = connection.prepareStatement(mainquery);
			ResultSet rs = stmt.executeQuery();	
			String cname = "";
			String ccname = "";
			//System.out.println("Query was ran successfully.");
			int i = 0;
			MapBlock blk;
			TitleVIDataFloat T6;		
			TitleVIDataFloat title6 = new TitleVIDataFloat();
			while (rs.next()) {
				cname = rs.getString("cname");
				blk = new MapBlock();
				blk.County = cname;
				blk.ID = rs.getString("blockid");
				blk.LandArea = rs.getLong("barea");
				blk.Density = rs.getLong("density");
				blk.RacDensity = rs.getLong("racdensity");
				blk.WacDensity = rs.getLong("wacdensity");
				Double[] location = (Double[]) rs.getArray("blocation").getArray();
				blk.Lat = location[0];
				blk.Lng = location[1];
				blk.Population = rs.getLong("bpop");
				blk.Rac = rs.getLong("brac");
				blk.Wac = rs.getLong("bwac");
				blk.Type = rs.getString("btype");
				T6 = new TitleVIDataFloat();		
				T6.id = rs.getString("blockid");		
				T6.english = rs.getFloat("english");		
				T6.spanish = rs.getFloat("spanish");		
				T6.spanishverywell = rs.getFloat("spanishverywell");		
				T6.spanishwell = rs.getFloat("spanishwell");		
				T6.spanishnotwell = rs.getFloat("spanishnotwell");		
				T6.spanishnotatall = rs.getFloat("spanishnotatall");		
				T6.indo_european = rs.getFloat("indo_european");		
				T6.indo_europeanverywell = rs.getFloat("indo_europeanverywell");		
				T6.indo_europeanwell = rs.getFloat("indo_europeanwell");		
				T6.indo_europeannotwell = rs.getFloat("indo_europeannotwell");		
				T6.indo_europeannotatall = rs.getFloat("indo_europeannotatall");		
				T6.asian_and_pacific_island = rs.getFloat("asian_and_pacific_island");		
				T6.asian_and_pacific_islandverywell = rs.getFloat("asian_and_pacific_islandverywell");		
				T6.asian_and_pacific_islandwell = rs.getFloat("asian_and_pacific_islandwell");		
				T6.asian_and_pacific_islandnotwell = rs.getFloat("asian_and_pacific_islandnotwell");		
				T6.asian_and_pacific_islandnotatall = rs.getFloat("asian_and_pacific_islandnotatall");		
				T6.other_languages = rs.getFloat("other_languages");		
				T6.other_languagesverywell = rs.getFloat("other_languagesverywell");		
				T6.other_languageswell = rs.getFloat("other_languageswell");		
				T6.other_languagesnotwell = rs.getFloat("other_languagesnotwell");		
				T6.other_languagesnotatall = rs.getFloat("other_languagesnotatall");		
				T6.below_poverty = rs.getFloat("below_poverty");		
				T6.above_poverty = rs.getFloat("above_poverty");		
				T6.with_disability = rs.getFloat("with_disability");		
				T6.without_disability = rs.getFloat("without_disability");		
				T6.from5to17 = rs.getFloat("from5to17");		
				T6.from18to64 = rs.getFloat("from18to64");		
				T6.above65 = rs.getFloat("above65");		
				T6.black_or_african_american = rs.getFloat("black_or_african_american");		
				T6.american_indian_and_alaska_native = rs.getFloat("american_indian_and_alaska_native");		
				T6.asian = rs.getFloat("asian");		
				T6.native_hawaiian_and_other_pacific_islander = rs.getFloat("native_hawaiian_and_other_pacific_islander");		
				T6.other_races = rs.getFloat("other_races");		
				T6.two_or_more = rs.getFloat("two_or_more");		
				T6.white = rs.getFloat("white");		
				T6.hispanic_or_latino = rs.getFloat("hispanic_or_latino");		
						
				//blk.Title6 = T6;		
				addTitle6(title6,T6);
				if (!(ccname.equals(cname))){
					if (i>0){
						instance.MapBL = Blocks;
						instance.MapTL = Tracts;
						instance.UrbanPopulation = CountyUrbanPop;
						instance.RuralPopulation = CountyRuralPop;
						instance.Rac = CountyRac;
						instance.Wac = CountyWac;
						TotalUrbanPop += CountyUrbanPop;
						TotalRuralPop += CountyRuralPop;
						TotalRac += CountyRac;
						TotalWac += CountyWac;
						TotalBlocks += Blocks.size();
						TotalTracts += Tracts.size();
						Counties.add(instance);
						instance = new MapCounty();
						Blocks = new ArrayList<MapBlock>();
						Tracts = new ArrayList<MapTract>();
						CountyUrbanPop = 0;
						CountyRuralPop = 0;
						CountyRac = 0;
						CountyWac = 0;
					}					
					ccname = cname;				
					instance.Name = cname;
					instance.Id = blk.ID.substring(0,5);
					TotalLandArea+= blk.LandArea;
				}
				if (blk.Type.equals("U")){
					CountyUrbanPop += blk.Population;
				} else {
					CountyRuralPop +=blk.Population;
				}
				CountyRac += blk.Rac;
				CountyWac += blk.Wac;
				TractArea = rs.getLong("tarea");
				tinstance = new MapTract();
				tinstance.ID = blk.ID.substring(0,11);
				if (!(Tracts.contains(tinstance)) && TractArea>0){
					tinstance.County = cname;
					tinstance.LandArea = TractArea;
					tinstance.Population = rs.getLong("tpop");
					Double[] tlocation = (Double[]) rs.getArray("tlocation").getArray();
					tinstance.Lat = tlocation[0];
					tinstance.Lng = tlocation[1];
					Tracts.add(tinstance);
				}				
				Blocks.add(blk);
				i++;
		        }
			instance.MapBL = Blocks;
			instance.MapTL = Tracts;
			instance.UrbanPopulation = CountyUrbanPop;
			instance.RuralPopulation = CountyRuralPop;
			instance.Rac = CountyRac;
			instance.Wac = CountyWac;
			TotalUrbanPop += CountyUrbanPop;
			TotalRuralPop += CountyRuralPop;
			TotalRac += CountyRac;
			TotalWac += CountyWac;
			TotalBlocks += Blocks.size();
			TotalTracts += Tracts.size();
			Counties.add(instance);			
			response.MapCL = Counties;
			response.RuralPopulation = TotalRuralPop;
			response.UrbanPopulation = TotalUrbanPop;
			response.Rac = TotalRac;
			response.Wac = TotalWac;
			response.TotalBlocks = TotalBlocks;
			response.TotalLandArea = TotalLandArea;
			response.TotalTracts = TotalTracts;		
			response.TitleVI = title6;
			rs.close();
			stmt.close();
		} catch ( Exception e ) {
	        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
	         
	      }					
		dropConnection(connection);
		return response;
	}
	
	/**
	 *Queries the transit agency tree menu	  
	 */
	public static AgencyRouteList agencyMenu(String[] date, String[] day, String username, int dbindex) {
		AgencyRouteList response = new AgencyRouteList();		
		AgencyRoute instance = new AgencyRoute();
		RouteListm rinstance = new RouteListm();
		VariantListm tinstance = new VariantListm();
		Attr attribute = new Attr();
		Connection connection = makeConnection(dbindex);		
		String mainquery ="";		
		if (day!=null){
			//the query with dates
			mainquery += "with aids as (select distinct agency_id as aid from gtfs_selected_feeds where username='"+username+"'), svcids as (";
			for (int i=0; i<date.length; i++){
				mainquery+= "(select  serviceid_agencyid, serviceid_id from gtfs_calendars gc inner join aids on gc.serviceid_agencyid = aids.aid where startdate::int<="+date[i]
						+" and enddate::int>="+date[i]+" and "+day[i]+" = 1 and serviceid_agencyid||serviceid_id not in (select serviceid_agencyid||serviceid_id from "
						+ "gtfs_calendar_dates where date='"+date[i]+"' and exceptiontype=2)union select serviceid_agencyid, serviceid_id from gtfs_calendar_dates gcd inner join "
						+ "aids on gcd.serviceid_agencyid = aids.aid where date='"+date[i]+"' and exceptiontype=1)";
				if (i+1<date.length)
					mainquery+=" union all ";
			}
			mainquery += "), trips as (select trip.agencyid as aid, trip.tripshortname as sname, trip.tripheadsign as sign, round((trip.length+trip.estlength)::numeric,2) as length, "
					+ "trip.id as tripid, trip.uid as uid, trip.route_id as routeid, count(svcids.serviceid_id) as service from gtfs_trips trip inner join svcids "
					+ "using(serviceid_agencyid, serviceid_id) group by trip.agencyid, trip.id having count(svcids.serviceid_id)>0),tripagency as (select trips.aid, "
					+ "agency.name as agency, trips.sname, trips.sign, trips.length, trips.tripid, trips.uid, trips.routeid from trips inner join gtfs_agencies agency on "
					+ "trips.aid=agency.id), tripstops as (select trip.aid, trip.agency, trip.tripid, trip.routeid, trip.sname, trip.sign, trip.length, trip.uid, "
					+ "stop.stop_agencyid_origin, stop.stop_id_origin, stop.stop_name_origin, stop.stop_agencyid_destination, stop.stop_id_destination, stop.stop_name_destination "
					+ "from tripagency trip inner join gtfs_trip_stops stop on trip.aid = stop.trip_agencyid and trip.tripid = stop.trip_id), triproute as (select trip.aid, "
					+ "trip.agency, trip.tripid, trip.routeid, trip.sname, trip.sign, trip.length, trip.uid, trip.stop_agencyid_origin, trip.stop_id_origin, trip.stop_name_origin, "
					+ "trip.stop_agencyid_destination, trip.stop_id_destination, trip.stop_name_destination, route.shortname as rsname, route.longname as rlname from tripstops trip "
					+ "inner join gtfs_routes route on trip.aid = route.agencyid and trip.routeid = route.id) select * from triproute order by agency, routeid, length desc, tripid";
		}else {	
			mainquery += "with aids as (select distinct agency_id as aid from gtfs_selected_feeds where username='"+username+"'), trips as (select trip.agencyid as aid, trip.tripshortname "
					+ "as sname, round((trip.length+trip.estlength)::numeric,2) as length, trip.tripheadsign as sign, trip.id as tripid, trip.uid as uid, trip.route_id as routeid "
					+ "from gtfs_trips trip inner join aids on trip.serviceid_agencyid = aids.aid), tripagency as (select trips.aid, agency.name as agency, trips.sname, "
					+ "trips.sign, trips.length, trips.tripid, trips.uid, trips.routeid from trips inner join gtfs_agencies agency on trips.aid=agency.id), tripstops as (select "
					+ "trip.aid, trip.agency, trip.tripid, trip.routeid, trip.sname, trip.sign, trip.length, trip.uid, stop.stop_agencyid_origin, stop.stop_id_origin, "
					+ "stop.stop_name_origin, stop.stop_agencyid_destination, stop.stop_id_destination, stop.stop_name_destination from tripagency trip inner join "
					+ "gtfs_trip_stops stop on trip.aid = stop.trip_agencyid and trip.tripid = stop.trip_id), triproute as (select trip.aid, trip.agency, trip.tripid, trip.routeid, "
					+ "trip.sname, trip.sign, trip.uid, trip.length, trip.stop_agencyid_origin, trip.stop_id_origin, trip.stop_name_origin, trip.stop_agencyid_destination, "
					+ "trip.stop_id_destination, trip.stop_name_destination, route.shortname as rsname, route.longname as rlname from tripstops trip inner join gtfs_routes route on "
					+ "trip.aid = route.agencyid and trip.routeid = route.id) select * from triproute order by agency, routeid, length desc, tripid";							
		}		
		System.out.println(mainquery);
		try{
			PreparedStatement stmt = connection.prepareStatement(mainquery);
			ResultSet rs = stmt.executeQuery();	
			String aid = "";
			String caid = "";
			String rid = "";
			String crid = "";
			String rsname = "";
			String rlname = "";
			String uid = "";
			String tsname = "";
			String thsign = "";	
			int freq = 0;		
			int maxfreq = 0;
			ArrayList<String> tripuids = new ArrayList<String>();			
			//System.out.println("Query was ran successfully.");
			int agencies_cntr = 0;
			int routes_cntr = 0;
			int trips_cntr = 0;
			int c = 1;
			boolean achanged;
			while (rs.next()) {	 
				achanged=false;
				aid = rs.getString("aid");					        	
	        	if (!(caid.equals(aid))){
	        		achanged = true;
					if (agencies_cntr>0){
						///add some children to it and add it to the rfesponse
						instance.children.add(rinstance);
						response.data.add(instance);
						instance = new AgencyRoute();
						rinstance = new RouteListm();
						attribute = new Attr();
						routes_cntr = 0;
						trips_cntr = 0;
						tripuids = new ArrayList<String>();
					}					
					caid  = aid;				
					instance.state = "closed";
					instance.data = formattedIndex(c)+". "+rs.getString("agency");
					c+=1;
					attribute.id = caid;
					attribute.type = "agency";
					instance.attr = attribute;					
					agencies_cntr++;										
	        	}
	        	rid = rs.getString("routeid");					        	
	        	if (!(crid.equals(rid)) || achanged){
					if (routes_cntr>0){
						///add some children to it and add it to the rfesponse
						instance.children.add(rinstance);
						rinstance = new RouteListm();						
					}
					crid  = rid;				
					rinstance.state = "closed";
					attribute = new Attr();
					attribute.id = rid;
					attribute.type = "route";
					rinstance.attr = attribute;					
					rsname = rs.getString("rsname");
					rlname = rs.getString("rlname");
	                if (rlname!= null && !rlname.equals("")){
	                	if ((rsname!= null) && !rsname.equals("")){
		                	rinstance.data = rlname + "(" + rsname+ ")";		                		
		                	} else {
		                		rinstance.data = rlname;
		                	}
		                } else {
		                	rinstance.data = rsname;
		                }
	                trips_cntr= 0;
	                routes_cntr++;
	                tripuids = new ArrayList<String>();
	                }
	        	uid = rs.getString("uid");
        		if (!tripuids.contains(uid)){
        			freq = 1;
        			tripuids.add(uid);
        			tinstance = new VariantListm();
        			tinstance.state = "leaf";
        			
        			attribute = new Attr();
        			attribute.type = "variant";
        			attribute.id = rs.getString("tripid");
        			attribute.freq = freq+"";
        			thsign = rs.getString("sign");	        				        					
                	if (thsign!=null && !thsign.equals("")) {
                		tinstance.data = thsign;  
                		tsname = rs.getString("sname");
                	}else {
                		if (tsname!=null && !tsname.equals("")){
                			tinstance.data = tsname;                    			
                		}else{
                			tinstance.data = "From "+ rs.getString("stop_name_origin") + " to "+ rs.getString("stop_name_destination");
                		}
                	}
                	attribute.longest = (trips_cntr > 0 )? 0:1;                    		
	                tinstance.attr = attribute;       			
        			rinstance.children.add(tinstance);
        			trips_cntr++;
        		}else{
        			freq++;
        			if(freq>maxfreq){
        				maxfreq = freq;
        			}
        			((ArrayList<VariantListm>) rinstance.children).get(rinstance.children.size()-1).attr.freq=freq+"";
        		}
        		
        	}
			instance.children.add(rinstance);
			response.data.add(instance);
			response.maxFreq = maxfreq+"";
			rs.close();
			stmt.close();
		} catch ( Exception e ) {
	        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
	         
	      }					
		dropConnection(connection);
		return response;
	}
	
	/**
	 *Queries calendar start and end dates by username or username and agency id	  
	 */
	public static StartEndDates getsedates(String username, String agency, int dbindex) {
		StartEndDates response = new StartEndDates();		
		Connection connection = makeConnection(dbindex);		
		//Setting default start and end dates in case no start and end dates exist in the database
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMdd");
	    Date today = new Date();
	    Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, -45); //default start date set to 45 days before the current date
		String startDate = sdfDate.format(cal.getTime());
		cal.add(Calendar.DAY_OF_MONTH, +90); //default end dates set to 45 days after the current date
		String endDate = sdfDate.format(cal.getTime());
		//System.out.println("Default start date: "+startDate+" Default end date: "+endDate);
		String mainquery ="";
		if (agency==null){
			mainquery = "with aids as (select distinct agency_id as aid from gtfs_selected_feeds where username='"+username+"') select coalesce(min(startdate::int),"+startDate
					+") as start, coalesce(max(enddate::int),"+endDate+") as end from gtfs_feed_info inner join aids on aid=defaultid";
			} else {
			mainquery = "select coalesce(min(startdate::int),"+startDate+") as start, coalesce(max(enddate::int),"+endDate+") as end from gtfs_feed_info inner join gtfs_agencies "
					+ "using(defaultid) where id='"+agency+"'";
		}				
		//System.out.println(mainquery);
		try{
			PreparedStatement stmt = connection.prepareStatement(mainquery);
			ResultSet rs = stmt.executeQuery();				
			while (rs.next()) {
				startDate = rs.getString("start");
				endDate = rs.getString("end");				
	        	}		
			rs.close();
			stmt.close();
		} catch ( Exception e ) {
	        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
	         
	      }					
		dropConnection(connection);
		response.Startdate = startDate;
		response.Enddate = endDate;		
		return response;
	}
	
	public static String formattedIndex(int c){
		
		if(c<10){
			return "0"+c;
		}else{
			return ""+c;
		}
	}
	
	/**
	 * Adds up title vi data for on map reports	  
	 */
	public static void addTitle6(TitleVIDataFloat all, TitleVIDataFloat single){
		all.id += single.id;
		all.english += single.english;
		all.spanish += single.spanish;
		all.spanishverywell += single.spanishverywell;
		all.spanishwell += single.spanishwell;
		all.spanishnotwell += single.spanishnotwell;
		all.spanishnotatall += single.spanishnotatall;
		all.indo_european += single.indo_european;
		all.indo_europeanverywell += single.indo_europeanverywell;
		all.indo_europeanwell += single.indo_europeanwell;
		all.indo_europeannotwell += single.indo_europeannotwell;
		all.indo_europeannotatall += single.indo_europeannotatall;
		all.asian_and_pacific_island += single.asian_and_pacific_island;
		all.asian_and_pacific_islandverywell += single.asian_and_pacific_islandverywell;
		all.asian_and_pacific_islandwell += single.asian_and_pacific_islandwell;
		all.asian_and_pacific_islandnotwell += single.asian_and_pacific_islandnotwell;
		all.asian_and_pacific_islandnotatall += single.asian_and_pacific_islandnotatall;
		all.other_languages += single.other_languages;
		all.other_languagesverywell += single.other_languagesverywell;
		all.other_languageswell += single.other_languageswell;
		all.other_languagesnotwell += single.other_languagesnotwell;
		all.other_languagesnotatall += single.other_languagesnotatall;
		all.below_poverty += single.below_poverty;
		all.above_poverty += single.above_poverty;
		all.with_disability += single.with_disability;
		all.without_disability += single.without_disability;
		all.from5to17 += single.from5to17;
		all.from18to64 += single.from18to64;
		all.above65 += single.above65;
		all.black_or_african_american += single.black_or_african_american;
		all.american_indian_and_alaska_native += single.american_indian_and_alaska_native;
		all.asian += single.asian;
		all.native_hawaiian_and_other_pacific_islander += single.native_hawaiian_and_other_pacific_islander;
		all.other_races += single.other_races;
		all.two_or_more += single.two_or_more;
		all.white += single.white;
		all.hispanic_or_latino += single.hispanic_or_latino;
	}
}

