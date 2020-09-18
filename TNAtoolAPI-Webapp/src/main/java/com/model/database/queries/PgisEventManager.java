// Copyright (C) 2015 Oregon State University - School of Mechanical,Industrial and Manufacturing Engineering 
//   This file is part of Transit Network Explorer Tool.
//
//    Transit Network Explorer Tool is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Transit Network Explorer Tool is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU  General Public License for more details.
//
//    You should have received a copy of the GNU  General Public License
//    along with Transit Network Explorer Tool.  If not, see <http://www.gnu.org/licenses/>.

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import org.apache.log4j.Logger;

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
import com.model.database.DatabaseConfig;
import com.model.database.queries.objects.AgencyRoute;
import com.model.database.queries.objects.AgencyRouteList;
import com.model.database.queries.objects.AgencySR;
import com.model.database.queries.objects.Attr;
import com.model.database.queries.objects.CAStop;
import com.model.database.queries.objects.CAStopsList;
import com.model.database.queries.objects.EmpData;
import com.model.database.queries.objects.EmpDataList;
import com.model.database.queries.objects.GeoArea;
import com.model.database.queries.objects.GeoR;
import com.model.database.queries.objects.GeoStopRouteMap;
import com.model.database.queries.objects.HeatMap;
import com.model.database.queries.objects.KeyClusterHashMap;
import com.model.database.queries.objects.MapAgency;
import com.model.database.queries.objects.MapBlock;
import com.model.database.queries.objects.MapCounty;
import com.model.database.queries.objects.MapGeo;
import com.model.database.queries.objects.MapRoute;
import com.model.database.queries.objects.MapStop;
import com.model.database.queries.objects.MapTract;
import com.model.database.queries.objects.MapTransit;
import com.model.database.queries.objects.ParknRideCounties;
import com.model.database.queries.objects.ParknRideCountiesList;
import com.model.database.queries.objects.PnrInCounty;
import com.model.database.queries.objects.PnrInCountyList;
import com.model.database.queries.objects.RouteListm;
import com.model.database.queries.objects.RouteR;
import com.model.database.queries.objects.StartEndDates;
import com.model.database.queries.objects.StopR;
import com.model.database.queries.objects.TitleVIData;
import com.model.database.queries.objects.TitleVIDataFloat;
import com.model.database.queries.objects.TitleVIDataList;
import com.model.database.queries.objects.ServiceLevel;
import com.model.database.queries.objects.VariantListm;
import com.model.database.queries.objects.agencyCluster;
import com.model.database.queries.util.Types;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class PgisEventManager {
	final static Logger logger = Logger.getLogger(PgisEventManager.class);	

	//public static Connection connection;

	/**
	 * makes a connection to a database based on the database index
	 * 
	 * @param dbindex
	 * @return Connection
	 */
	public static Connection makeConnection(int dbindex){
		return DatabaseConfig.getConfig(dbindex).getConnection();
	}

	public static Connection makeConnectionByUrl(String url, String user, String pass) throws SQLException {
		Connection response = null;
		try {
			Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e) {
      logger.error("PostgreSQL DataSource unable to load PostgreSQL JDBC Driver");
    }
		try {
			response = DriverManager.getConnection(url, user, pass);
		} catch ( SQLException e ) {
			e.printStackTrace();
			throw e;
		}
		return response;
	}

	/**
	 * Drops the connection
	 * @param connection
	 */
	public static void dropConnection(Connection connection){
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Get best service day window
	public static ServiceLevel getBestServiceWindow(String start, String end, int windowSize, int dbindex) {
		DateTimeFormatter dtformat = DateTimeFormatter.ofPattern("yyyyMMdd");
		Connection connection = makeConnection(dbindex);

		// Get the feed window
		String minFeedStartDate = "";
		String maxFeedEndDate = "";
		try {
			String q1 = "SELECT min(startdate) as startdate, max(enddate) as enddate FROM gtfs_feed_info;";
			PreparedStatement ps;
			ps = connection.prepareStatement(q1);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				minFeedStartDate = rs.getString("startdate");
				maxFeedEndDate = rs.getString("enddate");
			}
		} catch (SQLException e) {
			logger.error(e);
		}
		if (start == null) {
			start = minFeedStartDate;
		}
		if (end == null) {
			end = maxFeedEndDate;
		}
		if (windowSize < 1) {
			windowSize = 7;
		}
		
		// Parse the dates
		LocalDate startDate = LocalDate.parse(start, dtformat);
		LocalDate endDate = LocalDate.parse(end, dtformat);
		List<LocalDate> totalDates = new ArrayList<>();
		while (!startDate.isAfter(endDate)) {
			totalDates.add(startDate);
			startDate = startDate.plusDays(1);
		}

		List<Map<String,Double>> values = new ArrayList<Map<String,Double>>();
		List<Map<String,Double>> dowmax = new ArrayList<Map<String,Double>>();
		while(dowmax.size() < 7) {
			Map<String,Double>dow = new HashMap<String,Double>();
			dowmax.add(dow);
		}
		Set<String> agencies = new HashSet<String>();

		for (LocalDate date : totalDates) {
			DayOfWeek dow = date.getDayOfWeek();
			String d = date.format(dtformat);
			String query = "with svcids as ( ( select serviceid_agencyid, serviceid_id from gtfs_calendars gc where startdate <= ? and enddate >= ? and "+dow.name()+" = 1 and serviceid_agencyid || serviceid_id not in ( select serviceid_agencyid || serviceid_id from gtfs_calendar_dates where date = ? and exceptiontype = 2 ) union select serviceid_agencyid, serviceid_id from gtfs_calendar_dates gcd where date = ? and exceptiontype = 1 ) ), trips as ( select trip.agencyid as aid, trip.id as tripid, trip.route_id as routeid, round( (trip.length + trip.estlength):: numeric, 2 ) as length, trip.tlength as tlength, trip.stopscount as stops from svcids inner join gtfs_trips trip using( serviceid_agencyid, serviceid_id ) ) SELECT aid, SUM(tlength) as tlength, COUNT(tripid) FROM trips GROUP BY aid;";
			Map<String, Double> m = new HashMap<String, Double>();
			PreparedStatement ps;
			try {
				ps = connection.prepareStatement(query);
				ps.setString(1, d);
				ps.setString(2, d);
				ps.setString(3, d);
				ps.setString(4, d);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					String agency = rs.getString("aid");
					Double value = rs.getDouble("tlength");
					Double amax = dowmax.get(dow.getValue()-1).getOrDefault(agency, 0.0);
					if (value > amax) {
						dowmax.get(dow.getValue()-1).put(agency, value);
					}
					m.put(agency, value);
					agencies.add(agency);
				}
				rs.close();
				ps.close();
			} catch ( Exception e ) {
				logger.error(e);
			}
			values.add(m);
		}

		ArrayList<String> sortedAgencies = new ArrayList<String>();
		for (String s : agencies) {
			sortedAgencies.add(s);
		}

		Double[][] table = new Double[sortedAgencies.size()][totalDates.size()];
		for (int i=0; i<sortedAgencies.size(); i++) {
			String agency = sortedAgencies.get(i);
			for (int j=0; j<totalDates.size(); j++) {
				Double dvalue = values.get(j).getOrDefault(agency, 0.0);
				// Normalize
				// Double amax = dowmax.get(totalDates.get(i).getDayOfWeek().getValue()-1).getOrDefault(agency, 0.0);
				// Double norm = 0.0;
				// if (amax > 0) {
				// 	norm = dvalue / amax;
				// }
				table[i][j] = dvalue;
			}
		}

		Double[] scores = new Double[totalDates.size()];
		Double maxScore = 0.0;
		LocalDate bestDate = totalDates.get(0);
		for (int i=0;i<totalDates.size()-windowSize;i++) {
			Double sum = 0.0;
			for (int a=0;a<sortedAgencies.size();a++) {
				Double asum = 0.0;
				for (int j=0;j<windowSize;j++) {
					if (table[a][i+j] > 0) {
						asum += 1.0;
					}
				}
				if (asum >= 5.0) {
					sum += 1.0;
				}
			}
			scores[i] = sum;
			if (sum > maxScore) {
				maxScore = sum;
				bestDate = totalDates.get(i);
			}
		}

		ServiceLevel ret = new ServiceLevel();		
		ret.scores = scores;
		ret.values = table;
		ret.bestDate = bestDate.format(dtformat);
		ret.agencies = new String[sortedAgencies.size()];
		ret.agencies = sortedAgencies.toArray(ret.agencies);
		ret.dates = new String[totalDates.size()];
		for (int i=0; i<totalDates.size(); i++) {
			ret.dates[i] = totalDates.get(i).format(dtformat);
		}
		dropConnection(connection);
		return ret;
	}

	/////GEO AREA EXTENDED REPORTS QUERIES
	
	///// CONNECTED AGENCIES ON-MAP REPORT QUERIES
	/**
	 * Queries stops within a certain distance of a given stop while filtering the agencies.
	 * 
	 * @param lat - stop latitude
	 * @param lon - stops longitude
	 * @param gap - search radius
	 * @param agencies - list of agencies
	 * @param dbindex
	 * @return CAStopsList
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
	    dropConnection(connection);
		return results;
	}
		
	/**
	 * Queries Route miles for a geographic area
	 * @param type -  area type
	 * @param areaId - area ID
	 * @param username - user session
	 * @param dbindex - database index
	 * @return routeMiles (float)
	 */
	public static float RouteMiles(int type, String areaId, String username, int dbindex) 
    {	
	  Connection connection = makeConnection(dbindex);
      Statement stmt = null;
      float RouteMiles = 0;      
      String query = "with aids as (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true), "
      		+ "trips as (select agencyid, routeid, round(max(length)::numeric,2) as length "
      		+ "		from "+Types.getTripMapTableName(type)+" map inner join aids on map.agencyid_def=aids.aid "
			+ "		where "+Types.getIdColumnName(type)+" ='"+areaId +"' "
			+ "		group by agencyid, routeid) "
			+ "select sum(length) as routemiles from trips;";
      try {
    	  stmt = connection.createStatement();
    	  ResultSet rs = stmt.executeQuery(query);
    	  while ( rs.next() ) {
    		  RouteMiles = rs.getFloat("routemiles");
    	  }
    	  rs.close();
    	  stmt.close();
    	  } catch ( Exception e ) {
    		  e.printStackTrace();
    		  }
      dropConnection(connection);
      return RouteMiles;
      }
	
	/**
	 * Queries Fare Information for a geographic area. keys are; minfare, maxfare, medianfare, averagefare
	 * 
	 * @param type - area type
	 * @param date
	 * @param day
	 * @param areaId
	 * @param username - user session
	 * @param dbindex
	 * @return
	 */
	public static HashMap<String, Float> FareInfo(int type, String[] date, String[] day, String areaId, String username, int dbindex) 
    {	
	  Connection connection = makeConnection(dbindex);
      Statement stmt = null;
      HashMap<String, Float> response = new HashMap<String, Float>();
      ArrayList<Float> faredata = new ArrayList<Float>();
      String query = "with aids as (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true), svcids as (";
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
      try {
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);        
        while ( rs.next() ) {
        	faredata.add(rs.getFloat("fare"));                      
        }
        rs.close();
        stmt.close();        
      } catch ( Exception e ) {
    	  e.printStackTrace();
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
	 * Queries all P&Rs nationwide grouped by county
	 * 
	 * @param dbindex
	 * @return ParknRideCountiesList
	 */
	public static ParknRideCountiesList getCountiesPnrs(int dbindex){
		ParknRideCountiesList results = new ParknRideCountiesList();
		Connection connection = makeConnection(dbindex);
		Statement stmt = null;
		String querytext =	"SELECT county.countyid, county.cname AS countyname, COUNT(pnrid) AS count, SUM(pr.spaces) AS spaces, SUM(pr.accessiblespaces) AS accessiblespaces "
				+ "		FROM parknride AS pr INNER JOIN census_counties AS county "
				+ "		ON ST_Within(ST_SetSrid(ST_Makepoint(pr.lon,pr.lat),4326), county.shape) "
				+ "		GROUP BY county.countyid, county.cname";
		try {
	        stmt = connection.createStatement();
	        ResultSet rs = stmt.executeQuery(querytext); 
	        List<ParknRideCounties> list=new ArrayList<ParknRideCounties>();
	        while ( rs.next() ) {
	        	ParknRideCounties instance = new ParknRideCounties();
	        	instance.countyId = rs.getString("countyid");
	        	instance.cname = rs.getString("countyname");
	        	instance.count = rs.getString("count");
	        	instance.spaces = rs.getString("spaces");
	        	instance.accessibleSpaces = rs.getString("accessiblespaces");
	        	list.add(instance);
	        }
	        rs.close();
	        stmt.close(); 
	        results.PnrCountiesList=list;
	      } catch ( Exception e ) {
	        e.printStackTrace();
	      }
	      dropConnection(connection);
	      return results;
	}
	
	/** 
	 * Queries all detailed information on all the P&Rs within a given county.
	 * @param countyId
	 * @param radius - radius to search for transit stops around P&Rs
	 * @param dbindex
	 * @param username
	 * @return PnrInCountyList
	 */
	public static PnrInCountyList  getPnrsInCounty(int countyId, int radius, int dbindex, String username){
		PnrInCountyList results = new PnrInCountyList();
		Connection connection = makeConnection(dbindex);
		Statement stmt = null;
		String query =	"WITH aids AS (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true), "
				+ "pnr AS (SELECT  parknride.* FROM parknride INNER JOIN census_counties AS counties ON ST_contains(ST_Transform(counties.shape,2993), parknride.geom) WHERE counties.countyid = '" + countyId + "'),"
				+ "temp1 AS (SELECT pnr.*,	gtfs_stops.name stopname, 	gtfs_stops.id stopid, gtfs_stops.agencyid AS agencyid_def "
				+ "		FROM pnr LEFT JOIN gtfs_stops "
				+ "		ON ST_dwithin(pnr.geom, gtfs_stops.location, " + radius + ") ORDER BY pnrid), "
				+ "temp2 AS (SELECT temp1.*, map.routeid, map.agencyid FROM temp1 LEFT JOIN (SELECT * FROM gtfs_stop_route_map WHERE agencyid_def IN (SELECT aid FROM aids)) AS map ON temp1.stopid=map.stopid AND temp1.agencyid_def = map.agencyid_def ), "
				+ "temp3 AS (SELECT temp2.*, gtfs_routes.longname routename FROM temp2 LEFT JOIN gtfs_routes ON temp2.routeid = gtfs_routes.id AND temp2.agencyid = gtfs_routes.agencyid ), "
				+ "temp4 AS (SELECT temp3.*, gtfs_agencies.name agencyname FROM temp3 LEFT JOIN gtfs_agencies ON temp3.agencyid=gtfs_agencies.id ), "
				+ "temp5 AS (SELECT pnrid, lotname, location, city, zipcode, spaces, accessiblespaces, lat, lon, bikerackspaces, bikelockerspaces, electricvehiclespaces, carsharing, transitservice, availability, timelimit, restroom, benches,shelter,indoorwaitingarea,trashcan,"
				+ " 	lighting, 	securitycameras, 	sidewalks, 	pnrsignage, 	lotsurface, 	propertyowner, 	localexpert, 	county,countyid, array_agg(agencyname) agencies,array_agg(stopid) stopids,array_agg(stopname) stopnames,array_agg(routeid) routeids,array_agg(routename) routenames,array_agg(stopname) stops,  count(stopname) count "
				+ "		FROM temp4 GROUP BY pnrid, lotname,location,city, zipcode,spaces,accessiblespaces,lat,lon,bikerackspaces,bikelockerspaces,electricvehiclespaces,carsharing, transitservice, availability, timelimit, restroom,benches,shelter,indoorwaitingarea,trashcan, 	lighting, 	securitycameras, 	sidewalks, 	pnrsignage, 	lotsurface,	propertyowner, 	localexpert, 	county,countyid) "
				+ "SELECT * FROM temp5 ORDER BY pnrid";		
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
	        e.printStackTrace();
	      }
	      dropConnection(connection);
		
		return results;
	}
	
	/**
	 * Queries the list of all agency IDs in the database
	 * 
	 * @param username
	 * @param dbindex
	 * @return
	 */
	public static List<String> getAgencyList(String username, int dbindex){
		List<String> result = new ArrayList<String>();
		Connection connection = makeConnection(dbindex);
	    Statement stmt = null;
	    String query = "with aids AS (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true) "
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
	 * 
	 * @param projection - employment projection year
	 * @param DS - dataset to query from (WAC or RAC)
	 * @param reportType - determines whether the report is on agencies or geographical areas 
	 * @param dates - array of the dates to report on
	 * @param day - array of days of the week associated with selected dates
	 * @param fulldates - array of selected dates in full format
	 * @param radius - search radius
	 * @param L - minimum level of service
	 * @param dbindex - database index
	 * @param username - user session
	 * @return EmpDataList
	 */
	public static EmpDataList getEmpData(String projection, String DS, String reportType, String[] dates, String[] day, String[] fulldates, double radius, int L, int dbindex, String username){
		String projectionYear = "";
		EmpDataList results = new EmpDataList();
		Connection connection = makeConnection(dbindex);
	    Statement stmt = null;
	    String query = "";
	    
	    if (reportType.equals("Agencies")){ // returns employment data for a specific agency.
    		query = "with aids as (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true), svcids as (";
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
		    
		    query = "with aids as (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true), svcids AS (";
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
	 * 
	 * @param reportType - determines whether the report is on agencies or geographical areas 
	 * @param dates - array of the dates to report on
	 * @param day - array of days of the week associated with selected dates
	 * @param fulldatess - array of selected dates in full format
	 * @param radius - search radius
	 * @param L - minimum level of service
	 * @param dbindex - database index
	 * @param username - user session
	 * @return TitleVIDataList
	 */
	public static TitleVIDataList getTitleVIDataNonAgency(String reportType, String[] dates, String[] day, String[] fulldates, double radius, int L, int dbindex, String username){
		Connection connection = makeConnection(dbindex);
		
		String tripsViewQuery = "";

		tripsViewQuery = "CREATE TEMP VIEW trips_tmp_view AS "
			+ " with aids as (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true), svcids AS (";
		for (int i = 0; i < dates.length; i++){
			tripsViewQuery+= "(select serviceid_agencyid, serviceid_id, '"+fulldates[i]+"' as day from gtfs_calendars gc inner join aids on gc.serviceid_agencyid = aids.aid where "
				+ "startdate::int<="+dates[i]+" and enddate::int>="+dates[i]+" and "+day[i]+" = 1 and serviceid_agencyid||serviceid_id not in (select "
				+ "serviceid_agencyid||serviceid_id from gtfs_calendar_dates where date='"+dates[i]+"' and exceptiontype=2) union select serviceid_agencyid, serviceid_id, '"
				+ fulldates[i] + "' from gtfs_calendar_dates gcd inner join aids on gcd.serviceid_agencyid = aids.aid where date='"+dates[i]+"' and exceptiontype=1)";
			if ( i + 1 <dates.length)
				tripsViewQuery += " union all ";
		} 
		tripsViewQuery+=") select trip.agencyid as aid, trip.id as tripid, trip.route_id as routeid, round((map.length)::numeric,2) as length, map.tlength as tlength, map.stopscount as stops "
			+"	from svcids inner join gtfs_trips trip using(serviceid_agencyid, serviceid_id) "
			+"	inner join census_counties_trip_map map on trip.id = map.tripid and trip.agencyid = map.agencyid";
	    
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
		
		String popAtLOSQuery = "with stopsatlos1 AS ("
			+ " 	SELECT "
			+ " 		stime.stop_agencyid AS aid,"
			+ " 		stime.stop_id AS stopid,"
			+ " 		count(trips_tmp_view.aid) AS service"
			+ " 	FROM gtfs_stop_times stime"
			+ " 	INNER JOIN trips_tmp_view"
			+ " 		ON stime.trip_agencyid =trips_tmp_view.aid"
			+ " 		AND stime.trip_id=trips_tmp_view.tripid"
			+ " 	GROUP BY  stime.stop_agencyid, stime.stop_id"
			+ " 	HAVING count(trips_tmp_view.aid)>= " + L + " ), "
			+ " stopsatlos as ("
			+ " 	select "
			+ " 		stopsatlos1.*,"
			+ " 		gtfs_stops.location"
			+ " 	from stopsatlos1"
			+ " 	JOIN gtfs_stops "
			+ " 		on gtfs_stops.agencyid = stopsatlos1.aid"
			+ " 		and gtfs_stops.id = stopsatlos1.stopid ), "
			+ " popatlos as (select "
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
			+ "	from title_vi_blocks_float inner join stopsatlos on st_dwithin(title_vi_blocks_float.location,stopsatlos.location,"+radius+")"
			+ "	inner join census_blocks blocks ON title_vi_blocks_float.blockid = blocks.blockid GROUP BY title_vi_blocks_float.blockid, blocks.urbanid, blocks.regionid, blocks.congdistid, blocks.placeid), "
			+ " popatlos1 as (select "
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
			+ " " + criteria1 + " AS " + criteria4 + " FROM popatlos GROUP BY " + criteria1 + ")"
			+ " select popatlos1.*, " + criteria2 + "." + criteria4 + " AS areaid, " + criteria5 + " AS areaname "
			+ " FROM " + criteria3
			+ " LEFT JOIN popatlos1 USING(" + criteria4 + ")";

		String popServedQuery = "with aids as (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a "
			+ " LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '" + username + "' "
			+ " AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true), "
		    + " stops1 AS ("
			+ " 	SELECT "
			+ " 		stime.stop_agencyid AS aid,"
			+ " 		stime.stop_id AS stopid,"
			+ " 		min(stime.arrivaltime) AS arrival,"
			+ " 		max(stime.departuretime) AS departure,"
			+ " 		count(trips_tmp_view.aid) AS service"
			+ " 	FROM gtfs_stop_times stime"
			+ " 	INNER JOIN trips_tmp_view"
			+ " 		ON stime.trip_agencyid =trips_tmp_view.aid"
			+ " 		AND stime.trip_id=trips_tmp_view.tripid"
			+ " 	WHERE stime.arrivaltime>0"
			+ " 	AND stime.departuretime>0"
			+ " 	GROUP BY  stime.stop_agencyid, stime.stop_id),"
			+ " stops as ("
			+ " 	select "
			+ " 		stops1.*,"
			+ " 		gtfs_stops.location"
			+ " 	from stops1"
			+ " 	JOIN gtfs_stops "
			+ " 		on gtfs_stops.agencyid = stops1.aid"
			+ " 		and gtfs_stops.id = stops1.stopid ),"
			+ " popserved as (select "
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
			+ "	  blocks.blockid, census_blocks.urbanid, census_blocks.regionid, census_blocks.congdistid, census_blocks.placeid  "
			+ "	from title_vi_blocks_float blocks inner join stops on st_dwithin(blocks.location, stops.location,"+radius+") "
			+ "	inner join census_blocks ON blocks.blockid=census_blocks.blockid "
			+ "	group by stops.service, blocks.blockid,census_blocks.urbanid, census_blocks.regionid, census_blocks.congdistid, census_blocks.placeid), "
			+ " popserved1 as (select "
			+ "   SUM(english) AS english_served,"
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
			+ " " + criteria1 + " AS " + criteria4 + " from popserved GROUP BY " + criteria1 + "), "
			+ " tempstops as (select id, agencyid, blockid, location from gtfs_stops stop inner join aids on stop.agencyid = aids.aid),  "
			+ " census as (select block.blockid, block.urbanid, block.regionid, block.congdistid, block.placeid  "
			+ " from census_blocks block inner join tempstops on st_dwithin(block.location, tempstops.location, "+radius+")  "
			+ " group by block.blockid),  "
			+ " popwithinx as (select  "
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
			+ " " + criteria1 + " AS " + criteria4 + " FROM census INNER JOIN title_vi_blocks_float USING(blockid) GROUP BY " + criteria4 + "), "
			+ " totalpop AS (SELECT title_vi_blocks_float.*, LEFT(title_vi_blocks_float.blockid,5) AS countyid, census_blocks.urbanid, census_blocks.regionid, census_blocks.congdistid, census_blocks.placeid "
			+ "	FROM title_vi_blocks_float INNER JOIN census_blocks USING(blockid)), "
			+ " totalpop1 AS (SELECT "
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
			+ " select popserved1.*, popwithinx.*, totalpop1.*, " + criteria2 + "." + criteria4 + " AS areaid, " + criteria5 + " AS areaname "
			+ " FROM " + criteria3 + " LEFT JOIN popserved1 USING(" + criteria4 + ") "
			+ " LEFT JOIN popwithinx USING(" + criteria4 + ") "
			+ " LEFT JOIN totalpop1 USING(" + criteria4 + ")";

		String dropTripsViewQuery = "DROP VIEW trips_tmp_view";

		TitleVIDataList results = new TitleVIDataList();

	    try {

			PreparedStatement createTripsViewStatement = connection.prepareStatement(tripsViewQuery);
			PreparedStatement popLOSStatement = connection.prepareStatement(popAtLOSQuery);
			PreparedStatement popServedStatement = connection.prepareStatement(popServedQuery);
			PreparedStatement dropTripsViewStatement = connection.prepareStatement(dropTripsViewQuery);

			createTripsViewStatement.execute();
			
			ResultSet popLOSRs = popLOSStatement.executeQuery();

			while (popLOSRs.next()) {
				TitleVIData titleVIRow = new TitleVIData();		
				titleVIRow.id = popLOSRs.getString("areaid");
				titleVIRow.name = popLOSRs.getString("areaname");
				titleVIRow.english_atlos = popLOSRs.getInt("english_atlos");
				titleVIRow.spanish_atlos = popLOSRs.getInt("spanish_atlos");
				titleVIRow.indo_european_atlos = popLOSRs.getInt("indo_european_atlos");
				titleVIRow.asian_and_pacific_island_atlos = popLOSRs.getInt("asian_and_pacific_island_atlos");
				titleVIRow.other_languages_atlos = popLOSRs.getInt("other_languages_atlos");
				titleVIRow.below_poverty_atlos = popLOSRs.getInt("below_poverty_atlos");
				titleVIRow.above_poverty_atlos = popLOSRs.getInt("above_poverty_atlos");
				titleVIRow.with_disability_atlos = popLOSRs.getInt("with_disability_atlos");
				titleVIRow.without_disability_atlos = popLOSRs.getInt("without_disability_atlos");
				titleVIRow.from5to17_atlos = popLOSRs.getInt("from5to17_atlos");
				titleVIRow.from18to64_atlos = popLOSRs.getInt("from18to64_atlos");
				titleVIRow.above65_atlos = popLOSRs.getInt("above65_atlos");
				titleVIRow.black_or_african_american_atlos = popLOSRs.getInt("black_or_african_american_atlos");
				titleVIRow.american_indian_and_alaska_native_atlos = popLOSRs.getInt("american_indian_and_alaska_native_atlos");
				titleVIRow.asian_atlos = popLOSRs.getInt("asian_atlos");
				titleVIRow.native_hawaiian_and_other_pacific_islander_atlos = popLOSRs.getInt("native_hawaiian_and_other_pacific_islander_atlos");
				titleVIRow.other_races_atlos = popLOSRs.getInt("other_races_atlos");
				titleVIRow.two_or_more_atlos = popLOSRs.getInt("two_or_more_atlos");
				titleVIRow.white_atlos = popLOSRs.getInt("white_atlos");
				titleVIRow.hispanic_or_latino_atlos = popLOSRs.getInt("hispanic_or_latino_atlos");
				results.TitleVIDataList.add(titleVIRow);
			}

			ResultSet popServedRs = popServedStatement.executeQuery();
			while (popServedRs.next()) {
				String id = popServedRs.getString("areaid");
				TitleVIData row = results.getTitleVIDataFromId(id);
				row.english = popServedRs.getInt("english");
				row.spanish = popServedRs.getInt("spanish");
				row.indo_european = popServedRs.getInt("indo_european");
				row.asian_and_pacific_island = popServedRs.getInt("asian_and_pacific_island");
				row.other_languages = popServedRs.getInt("other_languages");
				row.below_poverty = popServedRs.getInt("below_poverty");
				row.above_poverty = popServedRs.getInt("above_poverty");
				row.with_disability = popServedRs.getInt("with_disability");
				row.without_disability = popServedRs.getInt("without_disability");
				row.from5to17 = popServedRs.getInt("from5to17");
				row.from18to64 = popServedRs.getInt("from18to64");
				row.above65 = popServedRs.getInt("above65");
				row.black_or_african_american = popServedRs.getInt("black_or_african_american");
				row.american_indian_and_alaska_native = popServedRs.getInt("american_indian_and_alaska_native");
				row.asian = popServedRs.getInt("asian");
				row.native_hawaiian_and_other_pacific_islander = popServedRs.getInt("native_hawaiian_and_other_pacific_islander");
				row.other_races = popServedRs.getInt("other_races");
				row.two_or_more = popServedRs.getInt("two_or_more");
				row.white = popServedRs.getInt("white");
				row.hispanic_or_latino = popServedRs.getInt("hispanic_or_latino");
				row.english_served = popServedRs.getLong("english_served");
				row.spanish_served = popServedRs.getLong("spanish_served");
				row.indo_european_served = popServedRs.getLong("indo_european_served");
				row.asian_and_pacific_island_served = popServedRs.getLong("asian_and_pacific_island_served");
				row.other_languages_served = popServedRs.getLong("other_languages_served");
				row.below_poverty_served = popServedRs.getLong("below_poverty_served");
				row.above_poverty_served = popServedRs.getLong("above_poverty_served");
				row.with_disability_served = popServedRs.getLong("with_disability_served");
				row.without_disability_served = popServedRs.getLong("without_disability_served");
				row.from5to17_served = popServedRs.getLong("from5to17_served");
				row.from18to64_served = popServedRs.getLong("from18to64_served");
				row.above65_served = popServedRs.getLong("above65_served");
				row.black_or_african_american_served = popServedRs.getLong("black_or_african_american_served");
				row.american_indian_and_alaska_native_served = popServedRs.getLong("american_indian_and_alaska_native_served");
				row.asian_served = popServedRs.getLong("asian_served");
				row.native_hawaiian_and_other_pacific_islander_served = popServedRs.getLong("native_hawaiian_and_other_pacific_islander_served");
				row.other_races_served = popServedRs.getLong("other_races_served");
				row.two_or_more_served = popServedRs.getLong("two_or_more_served");
				row.white_served = popServedRs.getLong("white_served");
				row.hispanic_or_latino_served = popServedRs.getLong("hispanic_or_latino_served");
				row.english_withinx = popServedRs.getInt("english_withinx");
				row.spanish_withinx = popServedRs.getInt("spanish_withinx");
				row.indo_european_withinx = popServedRs.getInt("indo_european_withinx");
				row.asian_and_pacific_island_withinx = popServedRs.getInt("asian_and_pacific_island_withinx");
				row.other_languages_withinx = popServedRs.getInt("other_languages_withinx");
				row.below_poverty_withinx = popServedRs.getInt("below_poverty_withinx");
				row.above_poverty_withinx = popServedRs.getInt("above_poverty_withinx");
				row.with_disability_withinx = popServedRs.getInt("with_disability_withinx");
				row.without_disability_withinx = popServedRs.getInt("without_disability_withinx");
				row.from5to17_withinx = popServedRs.getInt("from5to17_withinx");
				row.from18to64_withinx = popServedRs.getInt("from18to64_withinx");
				row.above65_withinx = popServedRs.getInt("above65_withinx");
				row.black_or_african_american_withinx = popServedRs.getInt("black_or_african_american_withinx");
				row.american_indian_and_alaska_native_withinx = popServedRs.getInt("american_indian_and_alaska_native_withinx");
				row.asian_withinx = popServedRs.getInt("asian_withinx");
				row.native_hawaiian_and_other_pacific_islander_withinx = popServedRs.getInt("native_hawaiian_and_other_pacific_islander_withinx");
				row.other_races_withinx = popServedRs.getInt("other_races_withinx");
				row.two_or_more_withinx = popServedRs.getInt("two_or_more_withinx");
				row.white_withinx = popServedRs.getInt("white_withinx");
				row.hispanic_or_latino_withinx = popServedRs.getInt("hispanic_or_latino_withinx");
			}

			dropTripsViewStatement.execute();

		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dropConnection(connection);    	
	    return results;
	}


	/**
	 * Queries Title VI data on a given area (based on the reportType)
	 * 
	 * @param dates - array of the dates to report on
	 * @param day - array of days of the week associated with selected dates
	 * @param fulldatess - array of selected dates in full format
	 * @param radius - search radius
	 * @param L - minimum level of service
	 * @param dbindex - database index
	 * @param username - user session
	 * @return TitleVIDataList
	 */
	public static TitleVIDataList getTitleVIDataAgencies( String[] dates, String[] day, String[] fulldates, double radius, int L, int dbindex, String username){
		TitleVIDataList results = new TitleVIDataList();
		Connection connection = makeConnection(dbindex);

	    Statement stmt = null;
	    String query = "";
	    
		query = "with aids as (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true), svcids as (";
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

	    try {
			stmt = connection.createStatement();
			
			ResultSet rs = stmt.executeQuery(query); 		
			while (rs.next()){
				TitleVIData i = new TitleVIData();				
				i.id = rs.getString("agency_id");
				i.name = rs.getString("agency_name");
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
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dropConnection(connection);    	
	    return results;
	}
	
	/**
	 * Queries Stops count, unduplicated urban pop and rural pop within x meters of all stops within the given geographic area
	 * 
	 * @param type - defines geographical area type
	 * @param areaId - geographical area ID
	 * @param username - user session
	 * @param x - population search radius
	 * @param dbindex - database index
	 * @param popYear - population projection year
	 * @param geoid - ID of the geographical to be intersected with the original area
	 * @param geotype - type of the geographical to be intersected with the original area
	 * @return long[]
	 */
	public static long[] stopsPop(int type, String areaId, String username, double x, int dbindex, String popYear,String geoid,int geotype) 
    {	
	  Connection connection = makeConnection(dbindex);
      Statement stmt = null;
      String column = "";
      String criteria1 = "";
      String criteria2 = "";
      String geocriteria1 = "";
      String geocriteria2 = "";
      String querytext="";
     
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
      if(geotype!=-1)
      {
      if (geotype==0){//county
    	  geocriteria1 = "left(blockid,5)";
    	  geocriteria2 = "left(block.blockid,5)";
    	  column = "blockid";
      } else if (geotype==1){//census tract
    	  geocriteria1 = "left(blockid,11)";
    	  geocriteria2 = "left(block.blockid,11)";
    	  column = "blockid";
      } else {// census place, urban area, ODOT region, state, or congressional district
    	  column = Types.getIdColumnName(geotype);
    	  geocriteria1 = Types.getIdColumnName(geotype);
    	  geocriteria2 = "block."+Types.getIdColumnName(geotype);
      }
      }
	  
	  String clause1="and "+geocriteria1+"='"+geoid+"'";
	  String clause2="and "+geocriteria2+"='"+geoid+"'";
	  if(geotype==-1||geotype==3) {
		  clause1="";
		  clause2="";
      }
		querytext = "with"
			+ " aids as (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true),"
			+ " stops as (select id, agencyid, "+column+", location from gtfs_stops stop inner join aids on stop.agencyid = aids.aid where "+criteria1+"='"+areaId+"' "+clause1+"),"
			+ " census as (select population"+popYear+" as population,poptype,block.blockid,landarea from census_blocks block inner join stops on st_dwithin(block.location, stops.location, "+String.valueOf(x)+") where "+criteria2 +"='"+areaId+"' "+clause2+" group by block.blockid),"
			+ " urbanpop as (select COALESCE(sum(population),0) upop, sum(landarea) as landarea from census where poptype = 'U'), "
			+ " ruralpop as (select COALESCE(sum(population),0) rpop, sum(landarea) as landarea from census where poptype = 'R'),"
			+ " censusemployment  as (select C000_"+popYear+"  as employment,poptype from census left join  lodes_rac_projection_block using(blockid)),"
			+ " censusemployee as (select C000 as employee,poptype from census left join lodes_blocks_wac using (blockid)),"
			+ " urbanrac as (select COALESCE(sum(employment),0) uemployment from censusemployment where poptype = 'U')," 
			+ " ruralrac as (select COALESCE(sum(employment),0) remployment from censusemployment where poptype = 'R'),"
			+ " urbanwac as (select COALESCE(sum(employee),0) uemployee from censusemployee where poptype = 'U')," 
			+ " ruralwac as (select COALESCE(sum(employee),0) remployee from censusemployee where poptype = 'R'),"	
			+ " stopcount as (select count(stops.id) as stopscount from stops) "
			+ " select COALESCE(stopscount,0) as stopscount, COALESCE(upop,0) as urbanpop, COALESCE(rpop,0) as ruralpop ,COALESCE(uemployment,0) as urbanemployment, COALESCE(remployment,0) as ruralemployment,COALESCE(uemployee,0) as urbanemployee, COALESCE(remployee,0) as ruralemployee, urbanpop.landarea as urbanlandarea, ruralpop.landarea as rurallandarea "
			+ " from stopcount"
			+ " inner join urbanpop on true inner join ruralpop on true inner join ruralrac on true inner join ruralwac on true inner join urbanrac on true inner join urbanwac on true";     
          
      long[] results = new long[9];
      try {
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(querytext);        
        while ( rs.next() ) {
        	results[0] = rs.getLong("stopscount");
        	results[1] = rs.getLong("urbanpop"); 
        	results[2] = rs.getLong("ruralpop"); 
        	results[3] = rs.getLong("urbanemployment"); 
        	results[4] = rs.getLong("ruralemployment"); 
        	results[5] = rs.getLong("urbanemployee"); 
			results[6] = rs.getLong("ruralemployee");
			results[7] = rs.getLong("urbanlandarea");
			results[8] = rs.getLong("rurallandarea");
        }
        rs.close();
        stmt.close();        
      } catch ( Exception e ) {
    	  e.printStackTrace();         
      }
      dropConnection(connection);      
      return results;
    }
	
	/**
	 * Queries Service miles, service hours, service stops, served pop at level of 
	 * service (urban and rural), served population (urban and rural), service days, 
	 * hours of service, and connected communities for a geographic area. keys are: 
	 * svcmiles, svchours, svcstops, upopatlos, rpopatlos, uspop, rspop, svcdays, 
	 * fromtime, totime, connections
	 *
	 * @param type - determines the of the geographical area 
	 * @param date - array of dates selected by the user 
	 * @param day - array of week day selected by the user. For example {"sunday","monday"}
	 * @param fulldates - array of dates selected by the users in full format
	 * @param areaId
	 * @param username
	 * @param LOS - minimum level of service
	 * @param x - population search radius
	 * @param dbindex
	 * @param popYear - population projection year
	 * @param geotype - type of the geographical to be intersected with the original area
	 * @param geoid - ID of the geographical to be intersected with the original area
	 * @return
	 */
	public static HashMap<String, String> ServiceMetrics(int type, String[] date, String[] day, String[] fulldates, String areaId, String username, int LOS, double x, int dbindex, String popYear,int geotype,String geoid) 
    {	
	  Connection connection = makeConnection(dbindex);
      Statement stmt = null;
      HashMap<String, String> response = new HashMap<String, String>();      
      String criteria = "";
      String geocriteria="";
      if (type==0){//county
    	  criteria = "left(blockid,5)";    	 
      } else if (type==1){//census tract
    	  criteria = "left(blockid,11)";    	 
      } else {// census place, urban area, ODOT region, or congressional district    	  
    	  criteria = Types.getIdColumnName(type);
      }
      if (geotype==0){//county
    	  geocriteria = "left(blockid,5)";    	 
      } else if (geotype==1){//census tract
    	  geocriteria = "left(blockid,11)";    	 
      } else {// census place, urban area, ODOT region, or congressional district    	  
    	  geocriteria = Types.getIdColumnName(type);
      }
      
      String query = "with aids as (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true), svcids as (";
      for (int i=0; i<date.length; i++){
    	  query+= "(select serviceid_agencyid, serviceid_id, '"+fulldates[i]+"' as day from gtfs_calendars gc inner join aids on gc.serviceid_agencyid = aids.aid where "
    	  		+ "startdate::int<="+date[i]+" and enddate::int>="+date[i]+" and "+day[i]+" = 1 and serviceid_agencyid||serviceid_id not in (select "
    	  		+ "serviceid_agencyid||serviceid_id from gtfs_calendar_dates where date='"+date[i]+"' and exceptiontype=2) union select serviceid_agencyid, serviceid_id, '"
    	  		+fulldates[i]+"' from gtfs_calendar_dates gcd inner join aids on gcd.serviceid_agencyid = aids.aid where date='"+date[i]+"' and exceptiontype=1)";
    	  if (i+1<date.length)
				query+=" union all ";
		}      
     if(geotype==-1||geotype==3){
      query +="), trips as (select trip.agencyid as aid, trip.id as tripid, trip.route_id as routeid, round((map.length)::numeric,2) as length, map.tlength as tlength, "
      		+ "map.stopscount as stops from svcids inner join gtfs_trips trip using(serviceid_agencyid, serviceid_id) inner join "+Types.getTripMapTableName(type)+ " map on "
      		+"trip.id = map.tripid and trip.agencyid = map.agencyid where map."+Types.getIdColumnName(type)+"='"+areaId+"'),service as (select COALESCE(sum(length),0) as svcmiles,"
      		+ " COALESCE(sum(tlength),0) as svchours, COALESCE(sum(stops),0) as svcstops from trips),stopsatlos as (select stime.stop_agencyid as aid, stime.stop_id as stopid, "
      		+ "stop.location as location, count(trips.aid) as service from gtfs_stops stop inner join gtfs_stop_times stime on stime.stop_agencyid = stop.agencyid and "
      		+ "stime.stop_id = stop.id inner join trips on stime.trip_agencyid =trips.aid and stime.trip_id=trips.tripid where "+criteria+"='"+areaId+"' group by "
      		+ "stime.stop_agencyid, stime.stop_id, stop.location having count(trips.aid)>="+LOS+"),stops as (select stime.stop_agencyid as aid, stime.stop_id as stopid, stop.location "
      		+ "as location, min(stime.arrivaltime) as arrival, max(stime.departuretime) as departure, count(trips.aid) as service from gtfs_stops stop inner join gtfs_stop_times "
      		+ "stime on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id inner join trips on stime.trip_agencyid =trips.aid and stime.trip_id=trips.tripid where "
      		+ "stime.arrivaltime>0 and stime.departuretime>0 group by stime.stop_agencyid, stime.stop_id, stop.location), "
      		+"stops1 as (select stopid,blockid,service from stops join gtfs_stops on stopid=id ),"
      		+ "rsvc as (select sum(coalesce(service,0)) as rsvcstops  from census_blocks join stops1 using(blockid) where poptype= 'R' And "+Types.getIdColumnName(type)+"='"+areaId+"'),"
      		 + "usvc as (select sum(coalesce(service,0)) as usvcstops  from census_blocks join stops1 using(blockid) where poptype= 'U' And "+Types.getIdColumnName(type)+"='"+areaId+"'),"
      		+ "svchrs as (select COALESCE(min(arrival),-1) as fromtime, COALESCE(max(departure),-1) as totime from stops), concom as (select distinct map."+Types.getIdColumnName(type)+" from "
      		+Types.getTripMapTableName(type)+" map inner join trips on trips.aid=map.agencyid and trips.tripid=map.tripid),concomnames as (select coalesce(string_agg(distinct "
      		+Types.getNameColumn(type)+",'; ' order by "+Types.getNameColumn(type)+"),'-') as connections from concom inner join "+Types.getTableName(type)+" using("
      		+Types.getIdColumnName(type)+")), popserved as (select (population"+popYear+"*sum(service)) as population, poptype,block.blockid,sum(service) as freq  from census_blocks block inner join stops on "
      		+ "st_dwithin(block.location, stops.location,"+String.valueOf(x)+") where "+criteria+"='"+areaId+"' group by blockid), "
      		+"racserved as (select (C000_"+popYear+"*freq)  as rac ,poptype from popserved left join lodes_rac_projection_block using (blockid)),"
      		+"wacserved as (select (C000*freq)  as wac ,poptype,blockid from popserved left join lodes_blocks_wac using (blockid)),	"
      		+ "popatlos as (select population"+popYear+" as population, poptype,block.blockid,block.landarea from "
      		+ "census_blocks block inner join stopsatlos on st_dwithin(block.location,stopsatlos.location,"+String.valueOf(x)+") where "+criteria+"='"+areaId+"' group by blockid),"
      		+ "upopatlos as (select COALESCE(sum(population),0) as upoplos, SUM(landarea) AS landarea from popatlos where poptype='U'), rpopatlos as (select COALESCE(sum(population),0) as rpoplos, SUM(landarea) AS landarea from "
      		+ "popatlos where poptype='R'), "
      		+"racatlos as (select C000_"+popYear+"  as rac ,poptype from popatlos left join lodes_rac_projection_block using (blockid)),"
            +"wacatlos as (select C000  as wac ,poptype from popatlos left join lodes_blocks_wac using (blockid)),"
      		+ "upopserved as (select COALESCE(sum(population),0) as uspop from popserved where poptype='U'), rpopserved as (select "
      		+ "COALESCE(sum(population),0) as rspop from popserved where poptype='R'), "
      		+"uracatlos as (select COALESCE(sum(rac),0) as uraclos from racatlos where poptype='U'),"
      		+"rracatlos as (select COALESCE(sum(rac),0) as rraclos from racatlos where poptype='R')," 
      		+"uracserved as (select COALESCE(sum(rac),0) as usrac from racserved where poptype='U'),"
      		+"rracserved as (select COALESCE(sum(rac),0) as rsrac from racserved where poptype='R'),"
      		+"uwacatlos as (select COALESCE(sum(wac),0) as uwaclos from wacatlos where poptype='U')," 
      		+"rwacatlos as (select COALESCE(sum(wac),0) as rwaclos from wacatlos where poptype='R')," 
      		+"uwacserved as (select COALESCE(sum(wac),0) as uswac from wacserved where poptype='U'),"
      		+"rwacserved as (select COALESCE(sum(wac),0) as rswac from wacserved where poptype='R'),"
      		+ "svcdays as (select COALESCE(array_agg(distinct day)::text,'-') as svdays from svcids) "
      	  +"select svcmiles,svchours,coalesce(usvcstops,0) as usvcstops ,coalesce(rsvcstops,0) as rsvcstops,svcstops,upoplos,rpoplos,uspop,rspop,uraclos,rraclos,usrac,rsrac,uwaclos,rwaclos,uswac,rswac,svdays,fromtime,totime,connections, upopatlos.landarea AS urbanlandarea, rpopatlos.landarea AS rurallandarea "
      	  + "from service inner join upopatlos on true inner join rpopatlos on true inner join upopserved on true inner join rpopserved on true inner join svcdays on true inner join svchrs on true inner join concomnames on true inner join uracatlos on true inner join rracatlos on true inner join uracserved on true inner join rracserved on true inner join uwacatlos on true inner join rwacatlos on true inner join uwacserved on true inner join rwacserved on true inner join rsvc on true inner join usvc on true";
   }
     else
     {
    	 query +="), trips as (select trip.agencyid as aid, trip.id as tripid, trip.route_id as routeid, round((map.length)::numeric,2) as length, map.tlength as tlength, "
    	      		+ "map.stopscount as stops,(ST_Length(st_transform(st_intersection(maps.shape, map.shape),2993))/1609.34) as s1 from svcids inner join gtfs_trips trip using(serviceid_agencyid, serviceid_id) inner join "+Types.getTripMapTableName(type)+ " map on "
    	      		+"trip.id = map.tripid and trip.agencyid = map.agencyid inner join "+Types.getTripMapTableName(geotype)+ " maps on "
    	      		+"trip.id = maps.tripid and trip.agencyid = maps.agencyid where map."+Types.getIdColumnName(type)+"='"+areaId+"' And maps."+Types.getIdColumnName(geotype)+"='"+geoid+"' ),service as (select COALESCE(sum(s1),0) as svcmiles,"
    	      		+ " COALESCE(sum(tlength),0) as svchours, COALESCE(sum(stops),0) as svcstops from trips),stopsatlos as (select stime.stop_agencyid as aid, stime.stop_id as stopid, "
    	      		+ "stop.location as location, count(trips.aid) as service from gtfs_stops stop inner join gtfs_stop_times stime on stime.stop_agencyid = stop.agencyid and "
    	      		+ "stime.stop_id = stop.id inner join trips on stime.trip_agencyid =trips.aid and stime.trip_id=trips.tripid group by "
    	      		+ "stime.stop_agencyid, stime.stop_id, stop.location having count(trips.aid)>="+LOS+"),"
    	      		+ "stops as (select stime.stop_agencyid as aid, stime.stop_id as stopid, stop.location "
    	      		+ "as location, min(stime.arrivaltime) as arrival, max(stime.departuretime) as departure, count(trips.aid) as service from gtfs_stops stop inner join gtfs_stop_times "
    	      		+ "stime on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id inner join trips on stime.trip_agencyid =trips.aid and stime.trip_id=trips.tripid where "
    	      		+ "stime.arrivaltime>0 and stime.departuretime>0 group by stime.stop_agencyid, stime.stop_id, stop.location),"
    	      		+"stops1 as (select stopid,blockid,service from stops join gtfs_stops on stopid=id ),"
    	      		+ "rsvc as (select sum(coalesce(service,0)) as rsvcstops  from census_blocks join stops1 using(blockid) where poptype= 'R' And  "+Types.getIdColumnName(geotype)+"='"+geoid+"'),"
    	      		 + "usvc as (select sum(coalesce(service,0)) as usvcstops  from census_blocks join stops1 using(blockid) where poptype= 'U' And  "+Types.getIdColumnName(geotype)+"='"+geoid+"'),"
    	           + "svchrs as (select COALESCE(min(arrival),-1) as fromtime, COALESCE(max(departure),-1) as totime from stops), concom as (select distinct map."+Types.getIdColumnName(type)+" from "
    	      		+Types.getTripMapTableName(type)+" map inner join trips on trips.aid=map.agencyid and trips.tripid=map.tripid),concomnames as (select coalesce(string_agg(distinct "
    	      		+Types.getNameColumn(type)+",'; ' order by "+Types.getNameColumn(type)+"),'-') as connections from concom inner join "+Types.getTableName(type)+" using("
    	      		+Types.getIdColumnName(type)+")), popserved as (select (population"+popYear+"*sum(service)) as population, poptype,block.blockid,sum(service) as freq  from census_blocks block inner join stops on "
    	      		+ "st_dwithin(block.location, stops.location,"+String.valueOf(x)+") where "+criteria+"='"+areaId+"' and "+geocriteria+"='"+geoid+"' group by blockid), "
    	      		+"racserved as (select (C000_"+popYear+"*freq)  as rac ,poptype from popserved left join lodes_rac_projection_block using (blockid)),"
    	      		+"wacserved as (select (C000*freq)  as wac ,poptype,blockid from popserved left join lodes_blocks_wac using (blockid)),	"
    	      		+ "popatlos as (select population"+popYear+" as population, poptype,block.blockid,block.landarea from "
    	      		+ "census_blocks block inner join stopsatlos on st_dwithin(block.location,stopsatlos.location,"+String.valueOf(x)+") where "+criteria+"='"+areaId+"' and "+geocriteria+"='"+geoid+"' group by blockid),"
    	      		+ "upopatlos as (select COALESCE(sum(population),0) as upoplos, SUM(landarea) AS landarea from popatlos where poptype='U'), rpopatlos as (select COALESCE(sum(population),0) as rpoplos, SUM(landarea) AS landarea from "
    	      		+ "popatlos where poptype='R'), "
    	      		+"racatlos as (select C000_"+popYear+"  as rac ,poptype from popatlos left join lodes_rac_projection_block using (blockid)),"
    	            +"wacatlos as (select C000  as wac ,poptype from popatlos left join lodes_blocks_wac using (blockid)),"
    	      		+ "upopserved as (select COALESCE(sum(population),0) as uspop from popserved where poptype='U'), rpopserved as (select "
    	      		+ "COALESCE(sum(population),0) as rspop from popserved where poptype='R'), "
    	      		+"uracatlos as (select COALESCE(sum(rac),0) as uraclos from racatlos where poptype='U'),"
    	      		+"rracatlos as (select COALESCE(sum(rac),0) as rraclos from racatlos where poptype='R')," 
    	      		+"uracserved as (select COALESCE(sum(rac),0) as usrac from racserved where poptype='U'),"
    	      		+"rracserved as (select COALESCE(sum(rac),0) as rsrac from racserved where poptype='R'),"
    	      		+"uwacatlos as (select COALESCE(sum(wac),0) as uwaclos from wacatlos where poptype='U')," 
    	      		+"rwacatlos as (select COALESCE(sum(wac),0) as rwaclos from wacatlos where poptype='R')," 
    	      		+"uwacserved as (select COALESCE(sum(wac),0) as uswac from wacserved where poptype='U'),"
    	      		+"rwacserved as (select COALESCE(sum(wac),0) as rswac from wacserved where poptype='R'),"
    	      		+ "svcdays as (select COALESCE(array_agg(distinct day)::text,'-') as svdays from svcids) "
    	      	    +"select svcmiles,svchours,coalesce(usvcstops,0) as usvcstops ,coalesce(rsvcstops,0) as rsvcstops,svcstops,upoplos,rpoplos,uspop,rspop,uraclos,rraclos,usrac,rsrac,uwaclos,rwaclos,uswac,rswac,svdays,fromtime,totime,connections, upopatlos.landarea AS urbanlandarea, rpopatlos.landarea AS rurallandarea"
    	      	    + " from service inner join upopatlos on true inner join rpopatlos on true inner join upopserved on true inner join rpopserved on true inner join svcdays on true inner join svchrs on true inner join concomnames on true inner join uracatlos on true inner join rracatlos on true inner join uracserved on true inner join rracserved on true inner join uwacatlos on true inner join rwacatlos on true inner join uwacserved on true inner join rwacserved on true inner join rsvc on true inner join usvc on true";
    	 }
      try {
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);        
        while ( rs.next() ) {
        	response.put("svcmiles", String.valueOf(rs.getFloat("svcmiles")));
        	response.put("svchours", String.valueOf(Math.round(rs.getLong("svchours")/36.00)/100.00));
        	response.put("svcstops", String.valueOf(rs.getLong("svcstops")));
         	response.put("usvcstops", String.valueOf(rs.getLong("usvcstops")));
         	response.put("rsvcstops", String.valueOf(rs.getLong("rsvcstops")));
            response.put("upopatlos", String.valueOf(rs.getLong("upoplos")));
        	response.put("rpopatlos", String.valueOf(rs.getLong("rpoplos")));
        	response.put("uspop", String.valueOf(rs.getFloat("uspop")));
        	response.put("rspop", String.valueOf(rs.getFloat("rspop")));
         	response.put("uracatlos", String.valueOf(rs.getLong("uraclos")));
        	response.put("rracatlos", String.valueOf(rs.getLong("rraclos")));
        	response.put("usrac", String.valueOf(rs.getFloat("usrac")));
        	response.put("rsrac", String.valueOf(rs.getFloat("rsrac")));
         	response.put("uwacatlos", String.valueOf(rs.getLong("uwaclos")));
        	response.put("rwacatlos", String.valueOf(rs.getLong("rwaclos")));
        	response.put("uswac", String.valueOf(rs.getFloat("uswac")));
        	response.put("rswac", String.valueOf(rs.getFloat("rswac")));
        	response.put("svcdays", String.valueOf(rs.getString("svdays")));
        	response.put("fromtime", String.valueOf(rs.getInt("fromtime")));
        	response.put("totime", String.valueOf(rs.getInt("totime")));
			response.put("connections", rs.getString("connections"));
			response.put("urbanlandarea", String.valueOf(rs.getLong("urbanlandarea")));
			response.put("rurallandarea", String.valueOf(rs.getLong("rurallandarea")));
		}
        rs.close();
        stmt.close();        
      } catch ( Exception e ) {
    	  e.printStackTrace();         
      }
      dropConnection(connection);      
      return response;
    }
	
	/**
	 * Queries geographic allocation of service for transit agency reports
	 * 0:county 1:census tracts 2:census places 3:urban areas 4:ODOT region 5:congressional district
	 * @param type - type of the geographical area to filter stops
	 * @param agencyId
	 * @param dbindex
	 * @param username
	 * @param urbanPop - minimum population of urban areas to filter
	 * @param popYear - population projection year
	 * @param popMax - maximum population of urban areas to filter
	 * @param popMin - minimum population of urban areas to filter
	 * @param areaid
	 * @param geotype - type of the geographical area to filter stops
	 * @param uc - flag to filter on urbanized areas (uc == 0) or on urban clusters (uc ==1)
	 * @return List<GeoR>
	 */
	public static List<GeoR> geoallocation(int type, String agencyId, int dbindex, String username, int urbanPop, 
			String popYear,String popMax,String popMin,String areaid,int geotype,int uc) 
    {	
	  Connection connection = makeConnection(dbindex);
      Statement stmt = null;
      List<GeoR> response = new ArrayList<GeoR>();      
      String criteria = "";
      String stoproutes = "areaid";
      String areaquery = "";
      String employmentquery = "";
      String selectquery = "";
      String query = "with ";
      String join = "";
      String aidsjoin = "";
      String routesidsjoin = "";
      String routesquery = "";
      String urbanquery="";
      String tractsFilter = "";
      String stopsquery="";
      if (agencyId==null){
    	  query += "aids as (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true),";
    	  join = "left";
    	  aidsjoin = "inner join aids on aid=stop.agencyid";
		  routesidsjoin = "inner join aids on aid=map.agencyid_def";
	  } else {
    	  join = "inner";
    	  if(type==3){
    		  if(geotype == -1 || geotype==3)
    		  { 
    		  aidsjoin = "where map.agencyid='"+agencyId+"'";
    		  }
    		  else
    		  { 
    			  aidsjoin = "and map.agencyid='"+agencyId+"'";}
    	    	
    	  }
    	  else
    	  {
    		  aidsjoin = "where map.agencyid='"+agencyId+"'";
    	  }
  		  routesidsjoin = aidsjoin;		
    	  tractsFilter = " INNER JOIN stops ON ST_Contains(census_tracts.shape, stops.location) ";
      }
      if (type==0){//county: tracts are queries and summed up to reflect the true number of census tracts in the results
          
    	  criteria = "left(blockid,11)";   
    	  if (agencyId==null){
        	   areaquery = "areas as (select countyid as areaid, cname as areaname, population"+popYear+" as population, landarea, waterarea, odotregionid, regionname from census_counties), "
             	  		+ "tracts as (select count(distinct tractid) as tracts, left(tractid,5) as areaid from census_tracts " + tractsFilter + " group by left(tractid,5)), ";    	  
        	   urbanquery="urbans1 as (select urbanid ,left(blockid,5) as areaid  from census_blocks),"
             	 		+ "urbans as (select count(distinct( urbanid)) as urbancount ,left(blockid,5) as areaid  from census_blocks group by areaid ),"
                        +"ua as (select  count(distinct(urbanid)) as ua,areaid from urbans1 join census_urbans using(urbanid) where population"+popYear+" >= 50000 group by areaid),"
                          +"uc as (select  count(distinct(urbanid))as uc,areaid from urbans1 join census_urbans using(urbanid) where population"+popYear+"  between 2500 and 49999 group by areaid),";
                 
          }
          else
          {
        	  areaquery = "areas as (select distinct countyid as areaid, cname as areaname, population"+popYear+" as population, landarea, waterarea, odotregionid, regionname from census_counties join stops on countyid=left(areaid,5)), "
            	  		+ "tracts as (select count(distinct tractid) as tracts, left(tractid,5) as areaid from census_tracts " + tractsFilter + " group by left(tractid,5)), ";    	  
        	  urbanquery="urbans1 as (select urbanid ,left(blockid,5) as areaid  from census_blocks join stops using(blockid)),"
            	 		+ "urbans as (select count(distinct( urbanid)) as urbancount ,left(blockid,5) as areaid  from census_blocks group by areaid ),"
                       +"ua as (select  count(distinct(urbanid)) as ua,areaid from urbans1 join census_urbans using(urbanid) where population"+popYear+" >= 50000 group by areaid),"
                         +"uc as (select  count(distinct(urbanid))as uc,areaid from urbans1 join census_urbans using(urbanid) where population"+popYear+"  between 2500 and 49999 group by areaid),";
                 
          }
    	  stopsquery="stops as (select stop.id as stopid,stop.agencyid as aid_def, map.agencyid as aid, "+criteria+" as areaid,blockid, ST_SetSRID(ST_Makepoint(lon,lat),4326) AS location FROM gtfs_stops stop inner join "
    	    	  + "gtfs_stop_service_map map on stop.id=map.stopid and stop.agencyid=map.agencyid_def "+aidsjoin+"), "
    			  +"rstops as ( select left(areaid,5) as areaid,count(distinct(concat(aid,stopid)))as rstops from stops join census_blocks using(blockid) where poptype='R' group by  left(areaid,5)),"
                  +"ustops as (select left(areaid,5) as areaid,count(distinct(concat(aid,stopid))) as ustops from stops join census_blocks using(blockid) where poptype='U' group by  left(areaid,5)),";
           employmentquery="employment as (select e"+popYear+" as employment,countyid as areaid from lodes_rac_projection_county), employees as (select sum(c000) as employees,left(blockid,5) as areaid from lodes_blocks_wac group by areaid )";
      	   selectquery = "select areaid, areaname, population,employment,employees,landarea, waterarea,coalesce(urbancount,0) as urbancount,coalesce(ua,0) as ua,"
      	  		+ "coalesce(uc,0) as uc, coalesce(agencies,0) as agencies, coalesce(routes,0) as routes, coalesce(stops,0) as stops,  coalesce(rstops,0) as rstops,coalesce(ustops,0) as ustops, odotregionid, regionname, tracts from areas left join stoproutes using(areaid) left join tracts using (areaid) left join routes using(areaid) left join employment using(areaid)left join employees using(areaid)left join urbans using(areaid) left join rstops using(areaid) left join ustops using(areaid) left join ua using(areaid) left join uc using(areaid)";
      	   routesquery = "routes AS (SELECT count(distinct agencyid||routeid) as routes, countyid AS areaid FROM census_counties_trip_map AS map " + routesidsjoin + " GROUP BY areaid),";
    	  stoproutes = "left(areaid,5)";
      } else if (type==1){//census tract
    	
    	  criteria = "left(blockid,11)";    
    	  stopsquery="stops as (select stop.id as stopid,stop.agencyid as aid_def, map.agencyid as aid, "+criteria+" as areaid,blockid, ST_SetSRID(ST_Makepoint(lon,lat),4326) AS location FROM gtfs_stops stop inner join "
    	    	  + "gtfs_stop_service_map map on stop.id=map.stopid and stop.agencyid=map.agencyid_def "+aidsjoin+"), "
    			  +"rstops as ( select left(areaid,11) as areaid,count(distinct(concat(aid,stopid)))as rstops from stops join census_blocks using(blockid) where poptype='R' group by  left(areaid,11)),"
                  +"ustops as (select left(areaid,11) as areaid,count(distinct(concat(aid,stopid))) as ustops from stops join census_blocks using(blockid) where poptype='U' group by  left(areaid,11)),";
    	  if (agencyId==null){
    		  areaquery = "areas as (select tractid as areaid, tname as areaname, tract.population"+popYear+" as population, tract.landarea, tract.waterarea from census_tracts tract),";
    		  urbanquery="urbans1 as (select urbanid ,left(blockid,11) as areaid  from census_blocks),"
          	 		+ "urbans as (select count(distinct( urbanid)) as urbancount ,left(blockid,11) as areaid  from census_blocks group by areaid ),"
                     +"ua as (select  count(distinct(urbanid)) as ua,areaid from urbans1 join census_urbans using(urbanid) where population"+popYear+" >= 50000 group by areaid),"
                       +"uc as (select  count(distinct(urbanid))as uc,areaid from urbans1 join census_urbans using(urbanid) where population"+popYear+"  between 2500 and 49999 group by areaid),";
             
         }
         else
         {
        	 areaquery = "areas as (select distinct tractid as areaid, tname as areaname, tract.population"+popYear+" as population, tract.landarea, tract.waterarea from census_tracts tract join stops on tractid=left(areaid,11)),";
        	 urbanquery="urbans1 as (select urbanid ,left(blockid,11) as areaid  from census_blocks join stops using(blockid)),"
         	 		+ "urbans as (select count(distinct( urbanid)) as urbancount ,left(blockid,11) as areaid  from census_blocks group by areaid ),"
                    +"ua as (select  count(distinct(urbanid)) as ua,areaid from urbans1 join census_urbans using(urbanid) where population"+popYear+" >= 50000 group by areaid),"
                      +"uc as (select  count(distinct(urbanid))as uc,areaid from urbans1 join census_urbans using(urbanid) where population"+popYear+"  between 2500 and 49999 group by areaid),";
       
         }
    	  routesquery = "routes AS (SELECT count(distinct agencyid||routeid) as routes, tractid AS areaid FROM census_tracts_trip_map AS map " + routesidsjoin + " GROUP BY areaid),";
    	     	  employmentquery="employment as ( select sum(c000_"+popYear+") as employment ,left(blockid,11) as areaid from lodes_rac_projection_block group by areaid),employees as (select sum(c000)as employees ,left(blockid,11) as areaid from lodes_blocks_rac group by areaid)";
       	  selectquery = "select areaid, areaname, population,employment,employees,landarea, waterarea,coalesce(urbancount,0) as urbancount,coalesce(ua,0) as ua,"
        	  		+ "coalesce(uc,0) as uc, coalesce(agencies,0) as agencies, coalesce(routes,0) as routes, coalesce(stops,0) as stops,  coalesce(rstops,0) as rstops,coalesce(ustops,0) as ustops from areas left join stoproutes using(areaid)  left join routes using(areaid) left join employment using(areaid)left join employees using(areaid)left join urbans using(areaid) left join rstops using(areaid) left join ustops using(areaid) left join ua using(areaid) left join uc using(areaid)";
        	  } else if (type==3){//census urban
    	  if (areaid.equals("null")|| areaid==null|| geotype==3){
    		  
    		  //urbanized areas 
    		  if (uc==0){
    	  criteria = Types.getIdColumnName(type);
     	  if (agencyId==null){
     		 areaquery = "areas as (select "+ criteria +" as areaid, "+Types.getNameColumn(type)+" as areaname, population"+popYear+" as population, landarea, waterarea from " + Types.getTableName(type)+" where population"+popYear+">= 50000),";
         	   	     
         }
         else
         {
        	 areaquery = "areas as (select distinct "+ criteria +" as areaid, "+Types.getNameColumn(type)+" as areaname, population"+popYear+" as population, landarea, waterarea from " + Types.getTableName(type)+" join stops on urbanid=areaid where population"+popYear+"> 50000),";
         	  
         }
    	  routesquery = "routes AS (SELECT count(distinct agencyid||routeid) as routes, urbanid AS areaid FROM census_urbans_trip_map AS map " + routesidsjoin + " GROUP BY areaid),";
    	   employmentquery="temp as (select census_blocks.blockid ,areaid  from areas left join census_blocks on areaid=urbanid),employment as (select Sum(lodes_rac_projection_block.C000_"+popYear+") as employment,areaid from temp left join lodes_rac_projection_block on lodes_rac_projection_block.blockid=temp.blockid Group by areaid ),employees as (select Sum(lodes_blocks_wac.C000) as employees,areaid from temp left join lodes_blocks_wac on lodes_blocks_wac.blockid=temp.blockid Group by areaid )  ";
    	 
    	  selectquery = " select areas.areaid, areaname,employment,employees,population, landarea, waterarea,coalesce(agencies,0) as agencies, coalesce(routes,0) as routes, "
      	  		+ "coalesce(stops,0) as stops from areas "+join+" join stoproutes using(areaid) left join routes using(areaid)left join employment using(areaid)left join employees using(areaid) Where population between "+popMax+" And "+popMin+"";
    		  }
    		  //urban clusters
    		  else  if (uc==1){
    			  criteria = Types.getIdColumnName(type);
    			  if (agencyId==null){
    		     		 areaquery = "areas as (select "+ criteria +" as areaid, "+Types.getNameColumn(type)+" as areaname, population"+popYear+" as population, landarea, waterarea from " + Types.getTableName(type)+" where population"+popYear+"< 50000),";
    		         	   	     
    		         }
    		         else
    		         {
    		        	 areaquery = "areas as (select distinct "+ criteria +" as areaid, "+Types.getNameColumn(type)+" as areaname, population"+popYear+" as population, landarea, waterarea from " + Types.getTableName(type)+" join stops on urbanid=areaid where population"+popYear+"< 50000),";
    		         	  
    		         }
    	    
    	    	  routesquery = "routes AS (SELECT count(distinct agencyid||routeid) as routes, urbanid AS areaid FROM census_urbans_trip_map AS map " + routesidsjoin + " GROUP BY areaid),";
    	    		  employmentquery="temp as (select census_blocks.blockid ,areaid  from areas left join census_blocks on areaid=urbanid),employment as (select Sum(lodes_rac_projection_block.C000_"+popYear+") as employment,areaid from temp left join lodes_rac_projection_block on lodes_rac_projection_block.blockid=temp.blockid Group by areaid ),employees as (select Sum(lodes_blocks_wac.C000) as employees,areaid from temp left join lodes_blocks_wac on lodes_blocks_wac.blockid=temp.blockid Group by areaid )  ";
    	    	 
    	    	  selectquery = " select areas.areaid, areaname,employment,employees,population, landarea, waterarea,coalesce(agencies,0) as agencies, coalesce(routes,0) as routes, "
    	      	  		+ "coalesce(stops,0) as stops from areas "+join+" join stoproutes using(areaid) left join routes using(areaid)left join employment using(areaid)left join employees using(areaid) Where population between "+popMax+" And "+popMin+"";
    	    		  }
    		  else {
    			  criteria = Types.getIdColumnName(type);
    			  if (agencyId==null){
    		     		 areaquery = "areas as (select "+ criteria +" as areaid, "+Types.getNameColumn(type)+" as areaname, population"+popYear+" as population, landarea, waterarea from " + Types.getTableName(type)+"),";
    		         	   	     
    		         }
    		         else
    		         {
    		        	 areaquery = "areas as (select distinct  "+ criteria +" as areaid, "+Types.getNameColumn(type)+" as areaname, population"+popYear+" as population, landarea, waterarea from " + Types.getTableName(type)+" join stops on urbanid=areaid ),";
    		         	  
    		         }
    	    	 
    	    	  routesquery = "routes AS (SELECT count(distinct agencyid||routeid) as routes, urbanid AS areaid FROM census_urbans_trip_map AS map " + routesidsjoin + " GROUP BY areaid),";
    	    		  employmentquery="temp as (select census_blocks.blockid ,areaid  from areas left join census_blocks on areaid=urbanid),employment as (select Sum(lodes_rac_projection_block.C000_"+popYear+") as employment,areaid from temp left join lodes_rac_projection_block on lodes_rac_projection_block.blockid=temp.blockid Group by areaid ),employees as (select Sum(lodes_blocks_wac.C000) as employees,areaid from temp left join lodes_blocks_wac on lodes_blocks_wac.blockid=temp.blockid Group by areaid )  ";
    	    	 
    	    	  selectquery = " select areas.areaid, areaname,employment,employees,population, landarea, waterarea,coalesce(agencies,0) as agencies, coalesce(routes,0) as routes, "
    	      	  		+ "coalesce(stops,0) as stops from areas "+join+" join stoproutes using(areaid) left join routes using(areaid)left join employment using(areaid)left join employees using(areaid) Where population between "+popMax+" And "+popMin+"";
    	    		  }
    		  
    		  }
    	  else{
    	  if(geotype==0)//census counties 
    	  { 
    		if(uc==0)
    		{
    		criteria = Types.getIdColumnName(type);
    		 if (agencyId==null){
    		stopsquery="stops as (select stop.id as stopid,stop.agencyid as aid_def, map.agencyid as aid, "+criteria+" as areaid,left(blockid,5), ST_SetSRID(ST_Makepoint(lon,lat),4326) AS location FROM gtfs_stops stop inner join "
                    +"gtfs_stop_service_map map on stop.id=map.stopid and stop.agencyid=map.agencyid_def "+aidsjoin+" where left(blockid,5)='"+areaid+"'),";    		
    		 }
    		else{
    			stopsquery="stops as (select stop.id as stopid,stop.agencyid as aid_def, map.agencyid as aid, "+criteria+" as areaid,left(blockid,5), ST_SetSRID(ST_Makepoint(lon,lat),4326) AS location FROM gtfs_stops stop inner join "
                        +"gtfs_stop_service_map map on stop.id=map.stopid and stop.agencyid=map.agencyid_def where left(blockid,5)='"+areaid+"'"+aidsjoin+"),";    		

    		}
    		routesquery = " routes AS (SELECT distinct(agencyid ||routeid) as route , urbanid AS areaid,shape FROM areas left join census_urbans_trip_map AS map on urbanid=areaid),"
                  +"routes1 as ( select  distinct (urbanid) as areaid,census_counties.shape from census_blocks join census_counties on left(blockid,5)=census_counties.countyid where left(blockid,5)='"+areaid+"' and urbanid is not null),"
           +"routesa AS (select count(distinct agencyid||routeid) as routes1 ,areaid from stops left join gtfs_stop_route_map m on stops.stopid=m.stopid and agencyid_def=stops.aid_def where areaid is not null group by areaid   ),"
                  +"routesf as ( select count(distinct routes) as routes ,areaid from routes1 join routes using(areaid) where st_intersects(routes.shape,routes1.shape)='t' group by areaid),";	
           
     	areaquery="areas1 as (select "+ criteria +" as areaid,population"+popYear+",blockid,landarea as landarea , waterarea as waterarea from census_blocks where left(blockid,5)='"+areaid+"' and(poptype='U' OR urbanid is not null)),";
         employmentquery="employment as (select Sum(lodes_rac_projection_block.C000_"+popYear+") as employment,areaid from areas1 left join lodes_rac_projection_block on lodes_rac_projection_block.blockid=areas1.blockid Group by areaid ),employees as (select Sum(lodes_blocks_wac.C000) as employees,areaid from areas1 left join lodes_blocks_wac on lodes_blocks_wac.blockid=areas1.blockid Group by areaid )  ";
     	urbanquery="areas as (select areaid,sum(areas1.population"+popYear+") as population, sum(areas1.landarea) as landarea , sum(areas1.waterarea) as waterarea,"+Types.getNameColumn(type)+" as areaname  from areas1 left join census_urbans on urbanid=areaid where population >= 50000  group by areaid,areaname),"
     			+"urbanpop as (select areaid,sum(census_blocks.population"+popYear+") as urbanpop from areas  left join census_blocks on areaid=urbanid group by areaid), ";
     	selectquery = " select areas.areaid, urbanpop as totalpop,areaname,employment,employees,population, landarea, waterarea,coalesce(agencies,0) as agencies, coalesce(routes1,0) as routes, "
     	  		+ "coalesce(stops,0) as stops from areas "+join+" join stoproutes using(areaid) left join routesf using(areaid) left join routesa using(areaid) left join employment using(areaid)left join employees using(areaid) left join urbanpop using(areaid) Where urbanpop between "+popMax+" And "+popMin+"";
   	
    		}
    		else if( uc==1)
    		{   criteria = Types.getIdColumnName(type);
    		 if (agencyId==null){
    	    		stopsquery="stops as (select stop.id as stopid,stop.agencyid as aid_def, map.agencyid as aid, "+criteria+" as areaid,left(blockid,5), ST_SetSRID(ST_Makepoint(lon,lat),4326) AS location FROM gtfs_stops stop inner join "
    	                    +"gtfs_stop_service_map map on stop.id=map.stopid and stop.agencyid=map.agencyid_def "+aidsjoin+" where left(blockid,5)='"+areaid+"'),";    		
    	    		 }
    	    		else{
    	    			stopsquery="stops as (select stop.id as stopid,stop.agencyid as aid_def, map.agencyid as aid, "+criteria+" as areaid,left(blockid,5), ST_SetSRID(ST_Makepoint(lon,lat),4326) AS location FROM gtfs_stops stop inner join "
    	                        +"gtfs_stop_service_map map on stop.id=map.stopid and stop.agencyid=map.agencyid_def where left(blockid,5)='"+areaid+"'"+aidsjoin+"),";    		

    	    		}	 routesquery = " routes AS (SELECT distinct(agencyid ||routeid) as route , urbanid AS areaid,shape FROM areas left join census_urbans_trip_map AS map on urbanid=areaid),"
                  +"routes1 as ( select  distinct (urbanid) as areaid,census_counties.shape from census_blocks join census_counties on left(blockid,5)=census_counties.countyid where left(blockid,5)='"+areaid+"' and urbanid is not null),"
           +"routesa AS (select count(distinct agencyid||routeid) as routes1 ,areaid from stops left join gtfs_stop_route_map m on stops.stopid=m.stopid and agencyid_def=stops.aid_def where areaid is not null group by areaid   ),"
                  +"routesf as ( select count(distinct routes) as routes ,areaid from routes1 join routes using(areaid) where st_intersects(routes.shape,routes1.shape)='t' group by areaid),";	
           
     	areaquery="areas1 as (select "+ criteria +" as areaid,population"+popYear+",blockid,landarea as landarea , waterarea as waterarea from census_blocks where left(blockid,5)='"+areaid+"' and(poptype='U' OR urbanid is not null) ),";
         employmentquery="employment as (select Sum(lodes_rac_projection_block.C000_"+popYear+") as employment,areaid from areas1 left join lodes_rac_projection_block on lodes_rac_projection_block.blockid=areas1.blockid Group by areaid ),employees as (select Sum(lodes_blocks_wac.C000) as employees,areaid from areas1 left join lodes_blocks_wac on lodes_blocks_wac.blockid=areas1.blockid Group by areaid )  ";
     	urbanquery="areas as (select areaid,sum(areas1.population"+popYear+") as population, sum(areas1.landarea) as landarea , sum(areas1.waterarea) as waterarea,"+Types.getNameColumn(type)+" as areaname  from areas1 left join census_urbans on urbanid=areaid where population between 2500 AND 49999 group by areaid,areaname),"
     			+"urbanpop as (select areaid,sum(census_blocks.population"+popYear+") as urbanpop from areas  left join census_blocks on areaid=urbanid group by areaid), ";
     	selectquery = " select areas.areaid, urbanpop as totalpop,areaname,employment,employees,population, landarea, waterarea,coalesce(agencies,0) as agencies, coalesce(routes1,0) as routes, "
     	  		+ "coalesce(stops,0) as stops from areas "+join+" join stoproutes using(areaid) left join routesf using(areaid) left join routesa using(areaid) left join employment using(areaid)left join employees using(areaid) left join urbanpop using(areaid) Where urbanpop between "+popMax+" And "+popMin+"";
     
 		
    			}
    	
    	  }
    	  if(geotype==1)//census tracts
    	  {
    		  if (uc==0)
    		  {
    		  criteria = Types.getIdColumnName(type);
    		  if (agencyId==null){
    		  stopsquery="stops as (select stop.id as stopid,stop.agencyid as aid_def, map.agencyid as aid, "+criteria+" as areaid,left(blockid,11), ST_SetSRID(ST_Makepoint(lon,lat),4326) AS location FROM gtfs_stops stop inner join "
                      +"gtfs_stop_service_map map on stop.id=map.stopid and stop.agencyid=map.agencyid_def "+aidsjoin+" where left(blockid,11)='"+areaid+"' ),";    		
    		  }
    		  else{
        		  stopsquery="stops as (select stop.id as stopid,stop.agencyid as aid_def, map.agencyid as aid, "+criteria+" as areaid,left(blockid,11), ST_SetSRID(ST_Makepoint(lon,lat),4326) AS location FROM gtfs_stops stop inner join "
                          +"gtfs_stop_service_map map on stop.id=map.stopid and stop.agencyid=map.agencyid_def  where left(blockid,11)='"+areaid+"'"+aidsjoin+" ),";    		
        		  }
    		  routesquery = " routes AS (SELECT distinct(agencyid ||routeid) as route , urbanid AS areaid,shape FROM areas left join census_urbans_trip_map AS map on urbanid=areaid),"
                      +"routes1 as ( select  distinct (urbanid) as areaid,census_tracts.shape from census_blocks join census_tracts on left(blockid,11)=census_tracts.tractid where left(blockid,11)='"+areaid+"' and urbanid is not null),"
                          +"routesa AS (select count(distinct agencyid||routeid) as routes1 ,areaid from stops left join gtfs_stop_route_map m on stops.stopid=m.stopid and agencyid_def=stops.aid_def where areaid is not null group by areaid   ),"
         
                      +"routesf as ( select count(distinct routes) as routes ,areaid from routes1 join routes using(areaid) where st_intersects(routes.shape,routes1.shape)='t' group by areaid),";	
               
        	  areaquery="areas1 as (select "+ criteria +" as areaid,population"+popYear+",blockid,landarea as landarea , waterarea as waterarea from census_blocks where left(blockid,11)='"+areaid+"' and (poptype='U' OR urbanid is not null) ),";
              
        	  employmentquery="employment as (select Sum(lodes_rac_projection_block.C000_"+popYear+") as employment,areaid from areas1 left join lodes_rac_projection_block on lodes_rac_projection_block.blockid=areas1.blockid Group by areaid ),employees as (select Sum(lodes_blocks_wac.C000) as employees,areaid from areas1 left join lodes_blocks_wac on lodes_blocks_wac.blockid=areas1.blockid Group by areaid )  ";
        		urbanquery="areas as (select areaid,sum(areas1.population"+popYear+") as population, sum(areas1.landarea) as landarea , sum(areas1.waterarea) as waterarea,"+Types.getNameColumn(type)+" as areaname  from areas1 left join census_urbans on urbanid=areaid  where population > 50000 group by areaid,areaname),"
    			+"urbanpop as (select areaid,sum(census_blocks.population"+popYear+") as urbanpop from areas  left join census_blocks on areaid=urbanid group by areaid), ";
            	
        		selectquery = " select areas.areaid, urbanpop as totalpop,areaname,employment,employees,population, landarea, waterarea,coalesce(agencies,0) as agencies, coalesce(routes1,0) as routes, "
            	  		+ "coalesce(stops,0) as stops from areas "+join+" join stoproutes using(areaid) left join routesf using(areaid) left join routesa using(areaid) left join employment using(areaid)left join employees using(areaid) left join urbanpop using(areaid) Where urbanpop between "+popMax+" And "+popMin+"";
    		  }
    		  else
    		  {
    			  criteria = Types.getIdColumnName(type);
    			  if (agencyId==null){
    	    		  stopsquery="stops as (select stop.id as stopid,stop.agencyid as aid_def, map.agencyid as aid, "+criteria+" as areaid,left(blockid,11), ST_SetSRID(ST_Makepoint(lon,lat),4326) AS location FROM gtfs_stops stop inner join "
    	                      +"gtfs_stop_service_map map on stop.id=map.stopid and stop.agencyid=map.agencyid_def "+aidsjoin+" where left(blockid,11)='"+areaid+"' ),";    		
    	    		  }
    	    		  else{
    	        		  stopsquery="stops as (select stop.id as stopid,stop.agencyid as aid_def, map.agencyid as aid, "+criteria+" as areaid,left(blockid,11), ST_SetSRID(ST_Makepoint(lon,lat),4326) AS location FROM gtfs_stops stop inner join "
    	                          +"gtfs_stop_service_map map on stop.id=map.stopid and stop.agencyid=map.agencyid_def  where left(blockid,11)='"+areaid+"'"+aidsjoin+" ),";    		
    	        		  }
    	    		   routesquery = " routes AS (SELECT distinct(agencyid ||routeid) as route , urbanid AS areaid,shape FROM areas left join census_urbans_trip_map AS map on urbanid=areaid),"
                          +"routes1 as ( select  distinct (urbanid) as areaid,census_tracts.shape from census_blocks join census_tracts on left(blockid,11)=census_tracts.tractid where left(blockid,11)='"+areaid+"' and urbanid is not null),"
                              +"routesa AS (select count(distinct agencyid||routeid) as routes1 ,areaid from stops left join gtfs_stop_route_map m on stops.stopid=m.stopid and agencyid_def=stops.aid_def where areaid is not null group by areaid   ),"
             
                          +"routesf as ( select count(distinct routes) as routes ,areaid from routes1 join routes using(areaid) where st_intersects(routes.shape,routes1.shape)='t' group by areaid),";	
                   
            	  areaquery="areas1 as (select "+ criteria +" as areaid,population"+popYear+",blockid,landarea as landarea , waterarea as waterarea from census_blocks where left(blockid,11)='"+areaid+"' and (poptype='U' OR urbanid is not null) ),";
                  
            	  employmentquery="employment as (select Sum(lodes_rac_projection_block.C000_"+popYear+") as employment,areaid from areas1 left join lodes_rac_projection_block on lodes_rac_projection_block.blockid=areas1.blockid Group by areaid ),employees as (select Sum(lodes_blocks_wac.C000) as employees,areaid from areas1 left join lodes_blocks_wac on lodes_blocks_wac.blockid=areas1.blockid Group by areaid )  ";
            		urbanquery="areas as (select areaid,sum(areas1.population"+popYear+") as population, sum(areas1.landarea) as landarea , sum(areas1.waterarea) as waterarea,"+Types.getNameColumn(type)+" as areaname  from areas1 left join census_urbans on urbanid=areaid  where population between 2500 And 49999 group by areaid,areaname),"
        			+"urbanpop as (select areaid,sum(census_blocks.population"+popYear+") as urbanpop from areas  left join census_blocks on areaid=urbanid group by areaid), ";
                	
            		selectquery = " select areas.areaid, urbanpop as totalpop,areaname,employment,employees,population, landarea, waterarea,coalesce(agencies,0) as agencies, coalesce(routes1,0) as routes, "
                	  		+ "coalesce(stops,0) as stops from areas "+join+" join stoproutes using(areaid) left join routesf using(areaid) left join routesa using(areaid) left join employment using(areaid)left join employees using(areaid) left join urbanpop using(areaid) Where urbanpop between "+popMax+" And "+popMin+"";
        		   
    		  }
    	  }
    	  if(geotype==2)//census places
    	  { if(uc==0)
    	  {
    		  criteria = Types.getIdColumnName(type);
    		  if (agencyId==null){
    		  stopsquery="stops as (select stop.id as stopid,stop.agencyid as aid_def, map.agencyid as aid, "+criteria+" as areaid,placeid, ST_SetSRID(ST_Makepoint(lon,lat),4326) AS location FROM gtfs_stops stop inner join "
                  +"gtfs_stop_service_map map on stop.id=map.stopid and stop.agencyid=map.agencyid_def "+aidsjoin+" where placeid='"+areaid+"' ),";    		
    		  }
    		  else{
        		  stopsquery="stops as (select stop.id as stopid,stop.agencyid as aid_def, map.agencyid as aid, "+criteria+" as areaid,placeid, ST_SetSRID(ST_Makepoint(lon,lat),4326) AS location FROM gtfs_stops stop inner join "
                      +"gtfs_stop_service_map map on stop.id=map.stopid and stop.agencyid=map.agencyid_def where placeid='"+areaid+"'"+aidsjoin+" ),";    		
        		  }

        	  routesquery = " routes AS (SELECT distinct(agencyid ||routeid) as route , urbanid AS areaid,shape FROM areas left join census_urbans_trip_map AS map on urbanid=areaid),"
                      +"routes1 as ( select  distinct (urbanid) as areaid,census_places.shape from census_blocks join census_places using(placeid) where placeid='"+areaid+"' and urbanid is not null),"
                           +"routesa AS (select count(distinct agencyid||routeid) as routes1 ,areaid from stops left join gtfs_stop_route_map m on stops.stopid=m.stopid and agencyid_def=stops.aid_def where areaid is not null group by areaid   ),"
         
                      +"routesf as ( select count(distinct routes) as routes ,areaid from routes1 join routes using(areaid) where st_intersects(routes.shape,routes1.shape)='t' group by areaid),";	
               	
        	  areaquery="areas1 as (select "+ criteria +" as areaid,population"+popYear+",census_blocks.blockid,landarea as landarea , waterarea as waterarea from census_blocks where placeid='"+areaid+"' and (poptype='U' OR urbanid is not null) ),";
              
         	 
        	  employmentquery="employment as (select Sum(lodes_rac_projection_block.C000_"+popYear+") as employment,areaid from areas1 left join lodes_rac_projection_block on lodes_rac_projection_block.blockid=areas1.blockid Group by areaid ),employees as (select Sum(lodes_blocks_wac.C000) as employees,areaid from areas1 left join lodes_blocks_wac on lodes_blocks_wac.blockid=areas1.blockid Group by areaid )  ";
        	  urbanquery="areas as (select areaid,sum(areas1.population"+popYear+") as population, sum(areas1.landarea) as landarea , sum(areas1.waterarea) as waterarea,"+Types.getNameColumn(type)+" as areaname  from areas1 left join census_urbans on urbanid=areaid where population > 50000 group by areaid,areaname),"
  			+"urbanpop as (select areaid,sum(census_blocks.population"+popYear+") as urbanpop from areas  left join census_blocks on areaid=urbanid group by areaid), ";
        	  selectquery = " select areas.areaid, urbanpop as totalpop,areaname,employment,employees,population, landarea, waterarea,coalesce(agencies,0) as agencies, coalesce(routes1,0) as routes, "
          	  		+ "coalesce(stops,0) as stops from areas "+join+" join stoproutes using(areaid) left join routesf using(areaid) left join routesa using(areaid) left join employment using(areaid)left join employees using(areaid) left join urbanpop using(areaid) Where urbanpop between "+popMax+" And "+popMin+"";
    	  }
    	  else
    	  {
    		  criteria = Types.getIdColumnName(type);
    		  if (agencyId==null){
        		  stopsquery="stops as (select stop.id as stopid,stop.agencyid as aid_def, map.agencyid as aid, "+criteria+" as areaid,placeid, ST_SetSRID(ST_Makepoint(lon,lat),4326) AS location FROM gtfs_stops stop inner join "
                      +"gtfs_stop_service_map map on stop.id=map.stopid and stop.agencyid=map.agencyid_def "+aidsjoin+" where placeid='"+areaid+"' ),";    		
        		  }
        		  else{
            		  stopsquery="stops as (select stop.id as stopid,stop.agencyid as aid_def, map.agencyid as aid, "+criteria+" as areaid,placeid, ST_SetSRID(ST_Makepoint(lon,lat),4326) AS location FROM gtfs_stops stop inner join "
                          +"gtfs_stop_service_map map on stop.id=map.stopid and stop.agencyid=map.agencyid_def where placeid='"+areaid+"'"+aidsjoin+" ),";    		
            		  }

        	  routesquery = " routes AS (SELECT distinct(agencyid ||routeid) as route , urbanid AS areaid,shape FROM areas left join census_urbans_trip_map AS map on urbanid=areaid),"
                      +"routes1 as ( select  distinct (urbanid) as areaid,census_places.shape from census_blocks join census_places using(placeid) where placeid='"+areaid+"' and urbanid is not null),"
                           +"routesa AS (select count(distinct agencyid||routeid) as routes1 ,areaid from stops left join gtfs_stop_route_map m on stops.stopid=m.stopid and agencyid_def=stops.aid_def where areaid is not null group by areaid   ),"
         
                      +"routesf as ( select count(distinct routes) as routes ,areaid from routes1 join routes using(areaid) where st_intersects(routes.shape,routes1.shape)='t' group by areaid),";	
               	
        	  areaquery="areas1 as (select "+ criteria +" as areaid,population"+popYear+",census_blocks.blockid,landarea as landarea , waterarea as waterarea from census_blocks where placeid='"+areaid+"' and (poptype='U' OR urbanid is not null) ),";
              
         	 
        	  employmentquery="employment as (select Sum(lodes_rac_projection_block.C000_"+popYear+") as employment,areaid from areas1 left join lodes_rac_projection_block on lodes_rac_projection_block.blockid=areas1.blockid Group by areaid ),employees as (select Sum(lodes_blocks_wac.C000) as employees,areaid from areas1 left join lodes_blocks_wac on lodes_blocks_wac.blockid=areas1.blockid Group by areaid )  ";
        	  urbanquery="areas as (select areaid,sum(areas1.population"+popYear+") as population, sum(areas1.landarea) as landarea , sum(areas1.waterarea) as waterarea,"+Types.getNameColumn(type)+" as areaname  from areas1 left join census_urbans on urbanid=areaid where population between 2500 And 49999 group by areaid,areaname),"
  			+"urbanpop as (select areaid,sum(census_blocks.population"+popYear+") as urbanpop from areas  left join census_blocks on areaid=urbanid group by areaid), ";
        	  selectquery = " select areas.areaid, urbanpop as totalpop,areaname,employment,employees,population, landarea, waterarea,coalesce(agencies,0) as agencies, coalesce(routes1,0) as routes, "
          	  		+ "coalesce(stops,0) as stops from areas "+join+" join stoproutes using(areaid) left join routesf using(areaid) left join routesa using(areaid) left join employment using(areaid)left join employees using(areaid) left join urbanpop using(areaid) Where urbanpop between "+popMax+" And "+popMin+"";
    
    	  }
    	  }
    	  if(geotype==4)//ODOT regions
    	  { if(uc==0){
    		  criteria = Types.getIdColumnName(type);
    		  if (agencyId==null){
    		  stopsquery="stops as (select stop.id as stopid,stop.agencyid as aid_def, map.agencyid as aid, "+criteria+" as areaid,placeid, ST_SetSRID(ST_Makepoint(lon,lat),4326) AS location FROM gtfs_stops stop inner join "
                      +"gtfs_stop_service_map map on stop.id=map.stopid and stop.agencyid=map.agencyid_def "+aidsjoin+"  where regionid='"+areaid+"'),";    		
    		  }
    		  else {
        		  stopsquery="stops as (select stop.id as stopid,stop.agencyid as aid_def, map.agencyid as aid, "+criteria+" as areaid,placeid, ST_SetSRID(ST_Makepoint(lon,lat),4326) AS location FROM gtfs_stops stop inner join "
                          +"gtfs_stop_service_map map on stop.id=map.stopid and stop.agencyid=map.agencyid_def  where regionid='"+areaid+"'"+aidsjoin+" ),";    		
        		  }
    		  
    		  
        	  routesquery = " routes AS (SELECT distinct(agencyid ||routeid) as route , urbanid AS areaid,shape FROM areas left join census_urbans_trip_map AS map on urbanid=areaid),"
                      +"rshape as (select st_union(shape) as rshape ,odotregionid from census_counties where odotregionid='"+areaid+"' group by odotregionid),"
        			  +"rshape1 as (select rshape,areaid from rshape cross join areas),"
        			     +"routesa AS (select count(distinct agencyid||routeid) as routes1 ,areaid from stops left join gtfs_stop_route_map m on stops.stopid=m.stopid and agencyid_def=stops.aid_def where areaid is not null group by areaid   ),"
        		         
        			  +"routesf as ( select count(distinct routes) as routes ,areaid from rshape1 join routes using(areaid) where st_intersects(routes.shape,rshape)='t' group by areaid),";
                       
        	  areaquery="areas1 as (select "+ criteria +" as areaid,population"+popYear+",census_blocks.blockid,landarea as landarea , waterarea as waterarea from census_blocks where regionid='"+areaid+"' and (poptype='U' OR urbanid is not null)),"; 
        	  employmentquery="employment as (select Sum(lodes_rac_projection_block.C000_"+popYear+") as employment,areaid from areas1 left join lodes_rac_projection_block on lodes_rac_projection_block.blockid=areas1.blockid Group by areaid ),employees as (select Sum(lodes_blocks_wac.C000) as employees,areaid from areas1 left join lodes_blocks_wac on lodes_blocks_wac.blockid=areas1.blockid Group by areaid )  ";
        	  urbanquery="areas as (select areaid,sum(areas1.population"+popYear+") as population, sum(areas1.landarea) as landarea , sum(areas1.waterarea) as waterarea,"+Types.getNameColumn(type)+" as areaname  from areas1 left join census_urbans on urbanid=areaid where population > 50000 group by areaid,areaname),"
  			+"urbanpop as (select areaid,sum(census_blocks.population"+popYear+") as urbanpop from areas  left join census_blocks on areaid=urbanid group by areaid), ";
        	
        	  
        	  selectquery = " select areas.areaid, urbanpop as totalpop,areaname,employment,employees,population, landarea, waterarea,coalesce(agencies,0) as agencies, coalesce(routes1,0) as routes, "
          	  		+ "coalesce(stops,0) as stops from areas "+join+" join stoproutes using(areaid) left join routesf using(areaid) left join routesa using(areaid) left join employment using(areaid)left join employees using(areaid) left join urbanpop using(areaid) Where urbanpop between "+popMax+" And "+popMin+"";
    	  }
    	  else
    	  {
    		  criteria = Types.getIdColumnName(type);
    		  if (agencyId==null){
        		  stopsquery="stops as (select stop.id as stopid,stop.agencyid as aid_def, map.agencyid as aid, "+criteria+" as areaid,placeid, ST_SetSRID(ST_Makepoint(lon,lat),4326) AS location FROM gtfs_stops stop inner join "
                          +"gtfs_stop_service_map map on stop.id=map.stopid and stop.agencyid=map.agencyid_def "+aidsjoin+"  where regionid='"+areaid+"'),";    		
        		  }
        		  else {
            		  stopsquery="stops as (select stop.id as stopid,stop.agencyid as aid_def, map.agencyid as aid, "+criteria+" as areaid,placeid, ST_SetSRID(ST_Makepoint(lon,lat),4326) AS location FROM gtfs_stops stop inner join "
                              +"gtfs_stop_service_map map on stop.id=map.stopid and stop.agencyid=map.agencyid_def  where regionid='"+areaid+"'"+aidsjoin+" ),";    		
            		  }
    		  
        	  routesquery = " routes AS (SELECT distinct(agencyid ||routeid) as route , urbanid AS areaid,shape FROM areas left join census_urbans_trip_map AS map on urbanid=areaid),"
                      +"rshape as (select st_union(shape) as rshape ,odotregionid from census_counties where odotregionid='"+areaid+"' group by odotregionid),"
        			  +"rshape1 as (select rshape,areaid from rshape cross join areas),"
        			     +"routesa AS (select count(distinct agencyid||routeid) as routes1 ,areaid from stops left join gtfs_stop_route_map m on stops.stopid=m.stopid and agencyid_def=stops.aid_def where areaid is not null group by areaid   ),"
        		         
        			  +"routesf as ( select count(distinct routes) as routes ,areaid from rshape1 join routes using(areaid) where st_intersects(routes.shape,rshape)='t' group by areaid),";
                       
        	  areaquery="areas1 as (select "+ criteria +" as areaid,population"+popYear+",census_blocks.blockid,landarea as landarea , waterarea as waterarea from census_blocks where regionid='"+areaid+"' and (poptype='U' OR urbanid is not null)),"; 
        	  employmentquery="employment as (select Sum(lodes_rac_projection_block.C000_"+popYear+") as employment,areaid from areas1 left join lodes_rac_projection_block on lodes_rac_projection_block.blockid=areas1.blockid Group by areaid ),employees as (select Sum(lodes_blocks_wac.C000) as employees,areaid from areas1 left join lodes_blocks_wac on lodes_blocks_wac.blockid=areas1.blockid Group by areaid )  ";
        	  urbanquery="areas as (select areaid,sum(areas1.population"+popYear+") as population, sum(areas1.landarea) as landarea , sum(areas1.waterarea) as waterarea,"+Types.getNameColumn(type)+" as areaname  from areas1 left join census_urbans on urbanid=areaid where population between 2500 and  49999 group by areaid,areaname),"
  			+"urbanpop as (select areaid,sum(census_blocks.population"+popYear+") as urbanpop from areas  left join census_blocks on areaid=urbanid group by areaid), ";
        	
        	  
        	  selectquery = " select areas.areaid, urbanpop as totalpop,areaname,employment,employees,population, landarea, waterarea,coalesce(agencies,0) as agencies, coalesce(routes1,0) as routes, "
          	  		+ "coalesce(stops,0) as stops from areas "+join+" join stoproutes using(areaid) left join routesf using(areaid) left join routesa using(areaid) left join employment using(areaid)left join employees using(areaid) left join urbanpop using(areaid) Where urbanpop between "+popMax+" And "+popMin+"";
     
    	  }
    	  }
    	  if(geotype==5)//Congressional districts
    	  {
    		  criteria = Types.getIdColumnName(type);
    		
    		  if(uc==0)
    		  {
    			  if (agencyId==null){
    			  stopsquery="stops as (select stop.id as stopid,stop.agencyid as aid_def, map.agencyid as aid, "+criteria+" as areaid,placeid, ST_SetSRID(ST_Makepoint(lon,lat),4326) AS location FROM gtfs_stops stop inner join "
                          +"gtfs_stop_service_map map on stop.id=map.stopid and stop.agencyid=map.agencyid_def "+aidsjoin+" where congdistid='"+areaid+"'),";    		
    			  }
    			  else {
    				  stopsquery="stops as (select stop.id as stopid,stop.agencyid as aid_def, map.agencyid as aid, "+criteria+" as areaid,placeid, ST_SetSRID(ST_Makepoint(lon,lat),4326) AS location FROM gtfs_stops stop inner join "
                              +"gtfs_stop_service_map map on stop.id=map.stopid and stop.agencyid=map.agencyid_def  where congdistid='"+areaid+"'"+aidsjoin+" ),";    		
        		
    			  }
    		  routesquery = " routes AS (SELECT distinct(agencyid ||routeid) as route , urbanid AS areaid,shape FROM areas left join census_urbans_trip_map AS map on urbanid=areaid),"
                      +"routes1 as ( select  distinct (urbanid) as areaid,census_congdists.shape from census_blocks join census_congdists using(congdistid) where congdistid='"+areaid+"' and urbanid is not null),"
                           +"routesa AS (select count(distinct agencyid||routeid) as routes1 ,areaid from stops left join gtfs_stop_route_map m on stops.stopid=m.stopid and agencyid_def=stops.aid_def where areaid is not null group by areaid   ),"
         
                      +"routesf as ( select count(distinct routes) as routes ,areaid from routes1 join routes using(areaid) where st_intersects(routes.shape,routes1.shape)='t' group by areaid),";	
               	
    		  
    		  
    		  areaquery="areas1 as (select "+ criteria +" as areaid,population"+popYear+",blockid,landarea as landarea , waterarea as waterarea from census_blocks where congdistid='"+areaid+"' and (poptype='U' OR urbanid is not null) ),"; 
          	
        	 employmentquery="employment as (select Sum(lodes_rac_projection_block.C000_"+popYear+") as employment,areaid from areas1 left join lodes_rac_projection_block on lodes_rac_projection_block.blockid=areas1.blockid Group by areaid ),employees as (select Sum(lodes_blocks_wac.C000) as employees,areaid from areas1 left join lodes_blocks_wac on lodes_blocks_wac.blockid=areas1.blockid Group by areaid )  ";
        	  urbanquery="areas as (select areaid,sum(areas1.population"+popYear+") as population, sum(areas1.landarea) as landarea , sum(areas1.waterarea) as waterarea,"+Types.getNameColumn(type)+" as areaname  from areas1 left join census_urbans on urbanid=areaid  where population > 50000 group by areaid,areaname),"
  			+"urbanpop as (select areaid,sum(census_blocks.population"+popYear+") as urbanpop from areas  left join census_blocks on areaid=urbanid group by areaid), ";
        	
        	  
        	  selectquery = " select areas.areaid, urbanpop as totalpop,areaname,employment,employees,population, landarea, waterarea,coalesce(agencies,0) as agencies, coalesce(routes1,0) as routes, "
          	  		+ "coalesce(stops,0) as stops from areas "+join+" join stoproutes using(areaid) left join routesf using(areaid) left join routesa using(areaid) left join employment using(areaid)left join employees using(areaid) left join urbanpop using(areaid) Where urbanpop between "+popMax+" And "+popMin+"";
        	
    		  }
    		  else if(uc==1)
    		  {
    			  if (agencyId==null){
        			  stopsquery="stops as (select stop.id as stopid,stop.agencyid as aid_def, map.agencyid as aid, "+criteria+" as areaid,placeid, ST_SetSRID(ST_Makepoint(lon,lat),4326) AS location FROM gtfs_stops stop inner join "
                              +"gtfs_stop_service_map map on stop.id=map.stopid and stop.agencyid=map.agencyid_def "+aidsjoin+" where congdistid= '"+areaid+"'),";    		
        			  }
        			  else {
        				  stopsquery="stops as (select stop.id as stopid,stop.agencyid as aid_def, map.agencyid as aid, "+criteria+" as areaid,placeid, ST_SetSRID(ST_Makepoint(lon,lat),4326) AS location FROM gtfs_stops stop inner join "
                                  +"gtfs_stop_service_map map on stop.id=map.stopid and stop.agencyid=map.agencyid_def  where congdistid='"+areaid+"'"+aidsjoin+" ),";    		
            		
        			  }
    			  routesquery = " routes AS (SELECT distinct(agencyid ||routeid) as route , urbanid AS areaid,shape FROM areas left join census_urbans_trip_map AS map on urbanid=areaid),"
                      +"routes1 as ( select  distinct (urbanid) as areaid,census_congdists.shape from census_blocks join census_congdists using(congdistid) where congdistid='"+areaid+"' and urbanid is not null),"
                           +"routesa AS (select count(distinct agencyid||routeid) as routes1 ,areaid from stops left join gtfs_stop_route_map m on stops.stopid=m.stopid and agencyid_def=stops.aid_def where areaid is not null group by areaid   ),"
         
                      +"routesf as ( select count(distinct routes) as routes ,areaid from routes1 join routes using(areaid) where st_intersects(routes.shape,routes1.shape)='t' group by areaid),";	
               	
    		  
    		  
    		  areaquery="areas1 as (select "+ criteria +" as areaid,population"+popYear+",blockid,landarea as landarea , waterarea as waterarea from census_blocks where congdistid='"+areaid+"' and (poptype='U' OR urbanid is not null) ),"; 
          	
        	 employmentquery="employment as (select Sum(lodes_rac_projection_block.C000_"+popYear+") as employment,areaid from areas1 left join lodes_rac_projection_block on lodes_rac_projection_block.blockid=areas1.blockid Group by areaid ),employees as (select Sum(lodes_blocks_wac.C000) as employees,areaid from areas1 left join lodes_blocks_wac on lodes_blocks_wac.blockid=areas1.blockid Group by areaid )  ";
        	  urbanquery="areas as (select areaid,sum(areas1.population"+popYear+") as population, sum(areas1.landarea) as landarea , sum(areas1.waterarea) as waterarea,"+Types.getNameColumn(type)+" as areaname  from areas1 left join census_urbans on urbanid=areaid where population between 2500 AND 49999 group by areaid,areaname),"
  			+"urbanpop as (select areaid,sum(census_blocks.population"+popYear+") as urbanpop from areas  left join census_blocks on areaid=urbanid group by areaid), ";
        	
        	  
        	  selectquery = " select areas.areaid, urbanpop as totalpop,areaname,employment,employees,population, landarea, waterarea,coalesce(agencies,0) as agencies, coalesce(routes1,0) as routes, "
          	  		+ "coalesce(stops,0) as stops from areas "+join+" join stoproutes using(areaid) left join routesf using(areaid) left join routesa using(areaid) left join employment using(areaid)left join employees using(areaid) left join urbanpop using(areaid) Where urbanpop between "+popMax+" And "+popMin+"";
        	
    		  }
    		  }
    	  }
    	  }
       
    	  else if (type==4) { //ODOT Regions
    	  criteria = Types.getIdColumnName(type);
    	  
    	  stopsquery="stops as (select stop.id as stopid,stop.agencyid as aid_def, map.agencyid as aid, "+criteria+" as areaid,blockid, ST_SetSRID(ST_Makepoint(lon,lat),4326) AS location FROM gtfs_stops stop inner join "
    	    	  + "gtfs_stop_service_map map on stop.id=map.stopid and stop.agencyid=map.agencyid_def "+aidsjoin+"), "
    			  +"rstops as ( select regionid as areaid,count(distinct(concat(aid,stopid)))as rstops from stops join census_blocks using(blockid) where poptype='R' group by  regionid),"
                  +"ustops as (select regionid as areaid,count(distinct(concat(aid,stopid))) as ustops from stops join census_blocks using(blockid) where poptype='U' group by regionid),";
    	  if(agencyId==null)
    	  {
    	  areaquery = "areas as (select odotregionid as areaid,countyid as cid, regionname as areaname, string_agg(trim(trailing 'County' from cname), ';' order by cname) as counties,"
      	  		+ "sum(population"+popYear+") as population, sum(landarea) as landarea, sum(waterarea) as waterarea from census_counties group by odotregionid, regionname,cid)," + "areas1 as (select odotregionid as areaid, regionname as areaname, string_agg(trim(trailing 'County' from cname), ';' order by cname) as counties, "
      	    	  		+ "sum(population"+popYear+") as population, sum(landarea) as landarea, sum(waterarea) as waterarea from census_counties group by odotregionid, regionname),";
    	  urbanquery="urbans1 as (select urbanid ,regionid as areaid  from census_blocks),"
      	 		+ "urbans as (select count(distinct( urbanid)) as urbancount ,regionid as areaid  from census_blocks group by areaid ),"
                 +"ua as (select  count(distinct(urbanid)) as ua,areaid from urbans1 join census_urbans using(urbanid) where population"+popYear+" >= 50000 group by areaid),"
                   +"uc as (select  count(distinct(urbanid))as uc,areaid from urbans1 join census_urbans using(urbanid) where population"+popYear+"  between 2500 and 49999 group by areaid),";
   
    	  }
    	  else
    	  {
    		  areaquery = "areas as (select odotregionid as areaid,countyid as cid, regionname as areaname, string_agg(trim(trailing 'County' from cname), ';' order by cname) as counties,"
   	  		+ "sum(population"+popYear+") as population, sum(landarea) as landarea, sum(waterarea) as waterarea from census_counties group by odotregionid, regionname,cid)," +"areai as (select distinct areaid from stops),"+ "areas1 as (select distinct odotregionid as areaid, regionname as areaname, string_agg(trim(trailing 'County' from cname), ';' order by cname) as counties, "
   	    	  		+ "sum(population"+popYear+") as population, sum(landarea) as landarea, sum(waterarea) as waterarea from census_counties join areai on odotregionid=areaid group by odotregionid, regionname),";
    		  urbanquery="urbans1 as (select urbanid ,regionid as areaid  from census_blocks join stops using(blockid)),"
  	 		+ "urbans as (select count(distinct( urbanid)) as urbancount ,regionid as areaid  from census_blocks  group by areaid  ),"
             +"ua as (select  count(distinct(urbanid)) as ua,areaid from urbans1 join census_urbans using(urbanid) where population"+popYear+" >= 50000 group by areaid),"
               +"uc as (select  count(distinct(urbanid))as uc,areaid from urbans1 join census_urbans using(urbanid) where population"+popYear+"  between 2500 and 49999 group by areaid),";

}
    	  routesquery = "routes AS (SELECT count(distinct agencyid||routeid) as routes, regionid AS areaid FROM census_counties_trip_map AS map " + routesidsjoin + " GROUP BY areaid),";
    	  employmentquery= "employment as (select Sum(lodes_rac_projection_county.e"+popYear+") as employment,areas.areaid as areaid from areas left join lodes_rac_projection_county on lodes_rac_projection_county.countyid=areas.cid Group by areaid ),employees as (select sum(C000) as employees , areaid from areas left join lodes_blocks_wac on left(lodes_blocks_wac.blockid,5)=cid group by areaid)";
    	         selectquery = "select areaid, areaname, population,employment,employees,landarea, waterarea,coalesce(urbancount,0) as urbancount,coalesce(ua,0) as ua,"
        	  		+ "coalesce(uc,0) as uc, coalesce(agencies,0) as agencies, coalesce(routes,0) as routes, counties, coalesce(stops,0) as stops,  coalesce(rstops,0) as rstops,coalesce(ustops,0) as ustops from areas1 left join stoproutes using(areaid) left join routes using(areaid) left join employment using(areaid)left join employees using(areaid)left join urbans using(areaid) left join rstops using(areaid) left join ustops using(areaid) left join ua using(areaid) left join uc using(areaid)";
          } else {// census place or congressional district    	  
    	  criteria = Types.getIdColumnName(type);
    	  if (type == 2) {
    		  stopsquery="stops as (select stop.id as stopid,stop.agencyid as aid_def, map.agencyid as aid, "+criteria+" as areaid,blockid, ST_SetSRID(ST_Makepoint(lon,lat),4326) AS location FROM gtfs_stops stop inner join "
        	    	  + "gtfs_stop_service_map map on stop.id=map.stopid and stop.agencyid=map.agencyid_def "+aidsjoin+"), "
        			  +"rstops as ( select placeid as areaid,count(distinct(concat(aid,stopid)))as rstops from stops join census_blocks using(blockid) where poptype='R' group by  placeid),"
                      +"ustops as (select placeid as areaid,count(distinct(concat(aid,stopid))) as ustops from stops join census_blocks using(blockid) where poptype='U' group by placeid),";
    		if(agencyId==null){
    		  areaquery = "areas as (select "+ criteria +" as areaid, "+Types.getNameColumn(type)+" as areaname, population"+popYear+" as population, landarea, waterarea from " + Types.getTableName(type)+"),";
    		  urbanquery="urbans1 as (select urbanid ,placeid as areaid  from census_blocks),"
    	      	 		+ "urbans as (select count(distinct( urbanid)) as urbancount ,placeid as areaid  from census_blocks group by areaid ),"
    	                 +"ua as (select  count(distinct(urbanid)) as ua,areaid from urbans1 join census_urbans using(urbanid) where population"+popYear+" >= 50000 group by areaid),"
    	                   +"uc as (select  count(distinct(urbanid))as uc,areaid from urbans1 join census_urbans using(urbanid) where population"+popYear+"  between 2500 and 49999 group by areaid),";
    	       
    		}
    		else
    		{
    			areaquery = "areas as (select distinct "+ criteria +" as areaid, "+Types.getNameColumn(type)+" as areaname, population"+popYear+" as population, landarea, waterarea from " + Types.getTableName(type)+" join stops on areaid=placeid),";
    			urbanquery="urbans1 as (select urbanid ,placeid as areaid  from census_blocks join stops using(blockid)),"
    	      	 		+ "urbans as (select count(distinct( urbanid)) as urbancount ,placeid as areaid  from census_blocks group by areaid ),"
    	                 +"ua as (select  count(distinct(urbanid)) as ua,areaid from urbans1 join census_urbans using(urbanid) where population"+popYear+" >= 50000 group by areaid),"
    	                   +"uc as (select  count(distinct(urbanid))as uc,areaid from urbans1 join census_urbans using(urbanid) where population"+popYear+"  between 2500 and 49999 group by areaid),";
    	       	
    		}
    		  routesquery = "routes AS (SELECT count(distinct agencyid||routeid) as routes, placeid AS areaid FROM census_places_trip_map AS map " + routesidsjoin + " GROUP BY areaid),";
    	  employmentquery="temp as (select census_blocks.blockid ,areaid  from areas left join census_blocks on areaid=placeid),employment as (select Sum(lodes_rac_projection_block.C000_"+popYear+") as employment,areaid from temp left join lodes_rac_projection_block on lodes_rac_projection_block.blockid=temp.blockid Group by areaid ), employees as (select sum(C000) as employees,areaid from temp left join lodes_blocks_wac on lodes_blocks_wac.blockid=temp.blockid group by areaid ) ";  
    	       selectquery = "select areaid, areaname, population,employment,employees,landarea, waterarea,coalesce(urbancount,0) as urbancount,coalesce(ua,0) as ua,"
      	  		+ "coalesce(uc,0) as uc, coalesce(agencies,0) as agencies, coalesce(routes,0) as routes, coalesce(stops,0) as stops,  coalesce(rstops,0) as rstops,coalesce(ustops,0) as ustops from areas left join stoproutes using(areaid) left join routes using(areaid) left join employment using(areaid)left join employees using(areaid)left join urbans using(areaid) left join rstops using(areaid) left join ustops using(areaid) left join ua using(areaid) left join uc using(areaid)";
    
    	 
    	  }
    	  if (type == 5){
    		  criteria = Types.getIdColumnName(type);
    		  stopsquery="stops as (select stop.id as stopid,stop.agencyid as aid_def, map.agencyid as aid, "+criteria+" as areaid,blockid, ST_SetSRID(ST_Makepoint(lon,lat),4326) AS location FROM gtfs_stops stop inner join "
        	    	  + "gtfs_stop_service_map map on stop.id=map.stopid and stop.agencyid=map.agencyid_def "+aidsjoin+"), "
        			  +"rstops as ( select congdistid as areaid,count(distinct(concat(aid,stopid)))as rstops from stops join census_blocks using(blockid) where poptype='R' group by  congdistid),"
                      +"ustops as (select congdistid as areaid,count(distinct(concat(aid,stopid))) as ustops from stops join census_blocks using(blockid) where poptype='U' group by congdistid),";
    		  if(agencyId==null){
        		  areaquery = "areas as (select "+ criteria +" as areaid, "+Types.getNameColumn(type)+" as areaname, population"+popYear+" as population, landarea, waterarea from " + Types.getTableName(type)+"),";
        		  urbanquery="urbans1 as (select urbanid ,congdistid as areaid  from census_blocks),"
              	 		+ "urbans as (select count(distinct( urbanid)) as urbancount ,congdistid as areaid  from census_blocks group by areaid ),"
                         +"ua as (select  count(distinct(urbanid)) as ua,areaid from urbans1 join census_urbans using(urbanid) where population"+popYear+" >= 50000 group by areaid),"
                           +"uc as (select  count(distinct(urbanid))as uc,areaid from urbans1 join census_urbans using(urbanid) where population"+popYear+"  between 2500 and 49999 group by areaid),";
                  
    		  }
        		else
        		{
        			areaquery = "areas as (select distinct "+ criteria +" as areaid, "+Types.getNameColumn(type)+" as areaname, population"+popYear+" as population, landarea, waterarea from " + Types.getTableName(type)+" join stops on areaid=congdistid),";
        			urbanquery="urbans1 as (select urbanid ,congdistid as areaid  from census_blocks join stops using(blockid)),"
                	 		+ "urbans as (select count(distinct( urbanid)) as urbancount ,congdistid as areaid  from census_blocks group by areaid ),"
                           +"ua as (select  count(distinct(urbanid)) as ua,areaid from urbans1 join census_urbans using(urbanid) where population"+popYear+" >= 50000 group by areaid),"
                             +"uc as (select  count(distinct(urbanid))as uc,areaid from urbans1 join census_urbans using(urbanid) where population"+popYear+"  between 2500 and 49999 group by areaid),";
                    		
        		}  
    	  routesquery = "routes AS (SELECT count(distinct agencyid||routeid) as routes, congdistid AS areaid FROM census_congdists_trip_map AS map " + routesidsjoin + " GROUP BY areaid),";
    	  employmentquery="temp as (select census_blocks.blockid ,areaid  from areas left join census_blocks on areaid=congdistid),employment as (select Sum(lodes_rac_projection_block.C000_"+popYear+") as employment,areaid from temp left join lodes_rac_projection_block on lodes_rac_projection_block.blockid=temp.blockid Group by areaid ), employees as (select sum(C000) as employees,areaid from temp left join lodes_blocks_wac on lodes_blocks_wac.blockid=temp.blockid group by areaid ) "; 
    	    selectquery = "select areaid, areaname, population,employment,employees,landarea, waterarea,coalesce(urbancount,0) as urbancount,coalesce(ua,0) as ua,"
        	  		+ "coalesce(uc,0) as uc, coalesce(agencies,0) as agencies, coalesce(routes,0) as routes, coalesce(stops,0) as stops,  coalesce(rstops,0) as rstops,coalesce(ustops,0) as ustops from areas left join stoproutes using(areaid) left join routes using(areaid) left join employment using(areaid)left join employees using(areaid)left join urbans using(areaid) left join rstops using(areaid) left join ustops using(areaid) left join ua using(areaid) left join uc using(areaid)";
      
      	 
    	  }
    		 // selectquery = " select areas.areaid,employment,employees,areaname,coalesce(urbancount,0) as urbancount, population, landarea, waterarea,coalesce(agencies,0) as agencies, coalesce(routes,0) as routes, "
    	  		// + "coalesce(stops,0) as stops from areas "+join+" join stoproutes using(areaid) left join routes using(areaid) left join employment using(areaid) left join employees using(areaid)left join urbans using(areaid)";
      }      
     if (stopsquery.isEmpty())
     {
      query +="stops as (select stop.id as stopid,stop.agencyid as aid_def, map.agencyid as aid, "+criteria+" as areaid, ST_SetSRID(ST_Makepoint(lon,lat),4326) AS location FROM gtfs_stops stop inner join "
    		+ "gtfs_stop_service_map map on stop.id=map.stopid and stop.agencyid=map.agencyid_def "+aidsjoin+"), stoproutes as (select "
    		+ "coalesce(count(distinct(concat(aid,stops.stopid))),0) as stops, coalesce(count(distinct aid),0) as agencies,"
    		+stoproutes+" as areaid from stops inner join gtfs_stop_route_map map on stops.aid_def = map.agencyid_def and stops.stopid = map.stopid group by "
    		+stoproutes+"),"+areaquery+urbanquery+routesquery+employmentquery+selectquery;
     }
     else
     {
    	 query +=  stopsquery+"stoproutes as (select "
    	    		+ "coalesce(count(distinct(concat(aid,stops.stopid))),0) as stops, coalesce(count(distinct aid),0) as agencies,"
    	    		+stoproutes+" as areaid from stops inner join gtfs_stop_route_map map on stops.aid_def = map.agencyid_def and stops.stopid = map.stopid group by "
    	    		+stoproutes+"),"+areaquery+urbanquery+routesquery+employmentquery+selectquery;
     }
     try {
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query); 
        GeoR instance;
        while ( rs.next() ) {
        	instance = new GeoR();
        	instance.id = rs.getString("areaid");
        	instance.Name = rs.getString("areaname");        	
        	instance.population = String.valueOf(rs.getLong("population"));
        	instance.employment =  String.valueOf(rs.getLong("employment"));
        	instance.employees =  String.valueOf(rs.getLong("employees"));
        	instance.landArea = String.valueOf(Math.round(rs.getLong("landarea")/2.58999e4)/100.0);
        	instance.waterArea = String.valueOf(Math.round(rs.getLong("waterarea")/2.58999e4)/100.0);
        	instance.RoutesCount = String.valueOf(rs.getLong("routes"));
        	instance.StopsCount = String.valueOf(rs.getLong("stops"));
        	instance.AgenciesCount = String.valueOf(rs.getLong("agencies"));
        if (type!=3)
        {
        	instance.UrbanClustersCount=rs.getString("uc");
    		instance.UrbanizedAreasCount=rs.getString("ua");
    		instance.UrbanStops=rs.getString("ustops");
    		instance.RuralStops=rs.getString("rstops");
        }
        	
        	if (!areaid.equals("null"))
        	{instance.Totalupop= String.valueOf(rs.getLong("totalpop"));
        	}
        	if(type!=3){
        	instance.UrbanAreasCount = String.valueOf(rs.getString("urbancount"));
        	}
        	if (type==0){
        	
        		instance.RuralStops=rs.getString("rstops");
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
    	  e.printStackTrace();         
      }
      dropConnection(connection);      
      return response;
    }	


	/**
	 * Queries the transit agency summary report for geographic areas : Agency 
	 * allocation of service for geographic areas
	 * 
	 * @param username
	 * @param type - type of the geographical area
	 * @param areaId - ID of the geographical area
	 * @param dbindex
	 * @param geotype - type of the geographical area to be intersected with urban areas for filtering service
	 * @param geoid - ID of the geographical area to be intersected with urban areas for filtering service 
	 * @return ArrayList<AgencySR> 
	 */
	public static ArrayList <AgencySR> agencyGeosr(String username, int type, String areaId, int dbindex,int geotype,String geoid ) {
		ArrayList <AgencySR> response = new ArrayList<AgencySR>();		
		AgencySR instance;
		Connection connection = makeConnection(dbindex);		
		String mainquery ="";
		String criteria = "";
		String tripFilter1 = "";
		String tripFilter2 = "";
		String geocriteria = "";
		String geotripFilter1 = "";
		String geotripFilter2 = "";
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
		if(!geoid.equals(null))
		{
		if (geotype==0){//county
	    	  geocriteria = "left(blockid,5)";
	    	  geotripFilter1 = "countyid";
	    	  geotripFilter2 = "census_counties_trip_map";
	      }else if(geotype==1){
	    	  geotripFilter2 = "census_tracts_trip_map";
	    	  geotripFilter1 = "tractid";
	    	  geocriteria = "left(blockid,11)";
	      }	else  { //census place, urban area, ODOT Regions or congressional district	      
	    	  geocriteria = Types.getIdColumnName(geotype);
	    	  if (geotype == 2) {geotripFilter1 = "placeid"; geotripFilter2 = "census_places_trip_map";}	    	  
	    	  else if (geotype == 3) {geotripFilter1 = "urbanid"; geotripFilter2 = "census_urbans_trip_map";}
	    	  else if (geotype == 4) {geotripFilter1 = "regionid"; geotripFilter2 = "census_counties_trip_map";}
	    	  else  {geotripFilter1 = "congdistid"; geotripFilter2 = "census_congdists_trip_map";}
	      }
		}
		if(geotype==-1||geotype==3)
		{
		mainquery += "with aids as (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true), stops as (select map.agencyid as agencyid, count(id) "
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
		}
		else
		{
			mainquery += "with aids as (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true), stops as (select map.agencyid as agencyid, count(id) "
				+ "as stops from gtfs_stops stop inner join gtfs_stop_service_map map on map.agencyid_def=stop.agencyid and map.stopid=stop.id inner join aids on stop.agencyid=aid "
				+ "where "+criteria+"='"+areaId+"' And "+geocriteria+"='"+geoid+"' group by map.agencyid), "	
				+"stops1 as (select map.agencyid as agencyid, id as stop "
				+ " from gtfs_stops stop inner join gtfs_stop_service_map map on map.agencyid_def=stop.agencyid and map.stopid=stop.id inner join aids on stop.agencyid=aid "
				+ "where "+criteria+"='"+areaId+"' And "+geocriteria+"='"+geoid+"'),"
						+ "agencies as (select agencies.id as id, name, fareurl, phone, url, feedname, version, startdate, enddate, "
				+ "publishername, publisherurl from gtfs_agencies agencies inner join stops on agencies.id = stops.agencyid inner join gtfs_feed_info info on "
				+ "agencies.defaultid=info.defaultid ), fare as (select frule.route_agencyid as aid, frule.route_id as routeid, (round(avg(ftrb.price)*100))::integer as price "
				+ "from gtfs_fare_rules frule inner join gtfs_fare_attributes ftrb on ftrb.agencyid= frule.fare_agencyid and ftrb.id=frule.fare_id group by "
				+ "frule.route_agencyid,frule.route_id), "
				+ "froutes as (select routes.agencyid as aid, array_agg(coalesce(price, -1) order by price) as fprices "
				+ "	from gtfs_routes routes left join fare on routes.agencyid = fare.aid and routes.id = fare.routeid "
				+ "	inner join stops on stops.agencyid=routes.agencyid group by routes.agencyid), "
				+ " sroutes as (select map.agencyid as aid, map.routeid from " + tripFilter2 + " map inner join agencies on agencies.id = map.agencyid inner join " + geotripFilter2 + " maps on agencies.id = maps.agencyid where " + tripFilter1 + "='" + areaId + "' And " + geotripFilter1 + "='" + geoid + "' group by map.agencyid,map.routeid),"
				+"routes as (select count(distinct(routeid || gtfs_stop_route_map.agencyid)) as routes ,gtfs_stop_route_map.agencyid as agencyid  from stops1 left join gtfs_stop_route_map on stop=stopid group by gtfs_stop_route_map.agencyid ) "
				+ "	select id, name, fareurl, phone, url, feedname, version, startdate, enddate, publishername, publisherurl, fprices, routes, stops"
				+ " 	from agencies inner join stops "
				+ "		on agencies.id=stops.agencyid "
				+ "		inner join froutes"
				+ " 	on stops.agencyid = froutes.aid "
				+ "		left join routes"
				+ " 	on agencies.id = routes.agencyid";		
		}
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
			e.printStackTrace();	         
	      }					
		dropConnection(connection);
		return response;
	}
	
	
	/**
	 * Queries the stops reports:
	 *stops by agency
	 *stops by agency and route 
	 *stops by geographic area
	 *stops by geographic area and agency
	 *stops by geographic are, agency, and route
	 *
	 * @param username
	 * @param type - type of the geographical area
	 * @param dates - array of dates in yyyymmdd format
	 * @param days - array of days. E.g. "tuesday"
	 * @param areaId - geographical area ID
	 * @param agency - agency ID
	 * @param route - route ID
	 * @param x - search radius
	 * @param dbindex
	 * @param popYear - population projection year
	 * @param geotype - type of the geographical area to be intersected with urban areas for filtering service
	 * @param geoid - ID of the geographical area to be intersected with urban areas for filtering service 
	 * @param rc - integer to distinguish rural and urban stops
	 * @return ArrayList<StopR>
	 * @throws SQLException
	 */
	public static ArrayList <StopR> stopGeosr(
			String username, 
			int type, 
			String[] dates, 
			String[] days, 
			String areaId, 
			String agency, 
			String route, 
			double x, 
			int dbindex, 
			String popYear,
			int geotype,
			String geoid,
			int rc,
			String stime,
			String etime
			) throws SQLException {
		ArrayList <StopR> response = new ArrayList<StopR>();		
		StopR instance;
		Connection connection = makeConnection(dbindex);	
		String stopsfilter = "";
		String routesfilter = "";
		String agenciesfilter = "";
		String popsfilter = "";
		String mainquery ="with aids as (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true), ";		
		String criteria = "";
		if (stime == null || "".equals(stime)) {stime = "00:00";}
		if (etime == null || "".equals(etime)) {etime = "23:59";}
		String [] st=stime.split(":");
		String [] et=etime.split(":");
		int stime1=0;
		int etime1=0;
		logger.debug(etime+"="+et[0]+et[1]);
		logger.debug(stime+"="+st[0]+st[1]);
		stime1=(Integer.parseInt(st[0])*100)+Integer.parseInt(st[1]);
		etime1=(Integer.parseInt(et[0])*100)+Integer.parseInt(et[1]);
		logger.debug(stime1+"-"+etime1);
		String agencyDefaultID = new String();
		if (agency != null) {agencyDefaultID = getDefaultAgencyID(agency, dbindex);}
		HashMap<String, Integer> stopsVisits = new HashMap<String, Integer>();
		if (dates != null && days != null){
			
			stopsVisits = stopFrequency(agency, dates, days, username, dbindex,stime1,etime1);
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
					stopsfilter = "where "+criteria+"='"+areaId+"' ";	// INNER JOIN aids on map.agencyid_def=aids.aid				
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
		
		 if (geotype!=-1)
		 	{if (geotype==0){//county
		    	  criteria = "left(blockid,5)";
		    	  stopsfilter = "where urbanid='"+areaId+"' and left(blockid,5) ='"+geoid+"'";
		      }else if (geotype==1){//tract
		    	  criteria = "left(blockid,11)"; 
		    	  stopsfilter = "where urbanid='"+areaId+"' and left(blockid,11) ='"+geoid+"'";  
		      }	else  { //census place, urban area, ODOT Regions or congressional district
		    	  criteria = Types.getIdColumnName(geotype);
		    	  stopsfilter =  "where urbanid='"+areaId+"' and "+ criteria + "='" + geoid + "' ";
		      
		      }
		 }
		if(rc==1)//rural stops
		{
		 mainquery += "stops0 as (select map.agencyid as agencyid,map.agencyid_def as defagencyid,stop.lat, stop.lon, stop.name as name, stop.id as id, url, location, urbanid, regionid, congdistid, placeid, blockid "
				+ "	from gtfs_stops stop inner join gtfs_stop_service_map map on map.agencyid_def=stop.agencyid and map.stopid=stop.id INNER JOIN aids on stop.agencyid=aids.aid " + stopsfilter + "), "
				+"stops1 as (select stops0.* from stops0 join census_blocks using(blockid) where poptype='R'),"
				+ "stops AS (select stops1.*, urban.population"+popYear+" AS urbanpop FROM stops1 LEFT JOIN census_urbans AS urban ON urban.urbanid = stops1.urbanid AND population"+popYear+" >50000), "
				+ "agencies as (select agencies.id as agencyid, agencies.name as aname from gtfs_agencies "
				+ "agencies "+agenciesfilter+"), routes as (select map.agencyid, stops.id, coalesce(string_agg(map.routeid,'; '),'-') as routes from gtfs_stop_route_map map inner "
				+ "join stops on stops.agencyid=map.agencyid and stops.id=map.stopid "+routesfilter+" group by map.agencyid, stops.id), "
				+"pops as (select stops.agencyid, stops.id, coalesce(sum(population2010),0) as pop,block.blockid from census_blocks block inner join stops on st_dwithin(block.location, stops.location,"+String.valueOf(x)+" )group by agencyid, id,block.blockid)," 
                +"rac as (select sum(COALESCE(C000_"+popYear+",0)) as employment,agencyid,id from  pops left join lodes_rac_projection_block using(blockid) group by agencyid, id),"
                +"wac as (select sum(COALESCE(C000,0)) as employees,agencyid,id from  pops left join lodes_blocks_wac using(blockid) group by agencyid, id),"
	            + "upops as (select stops.agencyid, stops.id, "
				+ "coalesce(sum(population"+popYear+"),0) as upop from census_blocks block inner join stops on st_dwithin(block.location, stops.location, "+String.valueOf(x)+") where "
				+ popsfilter+" poptype='U' group by agencyid, id), rpops as (select stops.agencyid, stops.id, coalesce(sum(population"+popYear+"),0) as rpop from census_blocks block inner "
				+ "join stops on st_dwithin(block.location, stops.location, "+String.valueOf(x)+") where "+popsfilter+" poptype='R' group by agencyid, id), "
				+ "result AS (select stops.agencyid,stops.defagencyid, stops.lat, stops.lon, aname,employment,employees, id, name, url, stops.urbanid, stops.regionid, stops.congdistid, stops.placeid, stops.blockid, "
				+ "routes, coalesce(upop,0) as upop, coalesce(rpop,0) as rpop, COALESCE(urbanpop,0) AS overfiftypop "
				+ "	from stops inner join agencies using(agencyid) inner join routes "
				+ "	using(agencyid,id) left join upops using(agencyid,id) left join rpops using(agencyid,id)left join rac using(agencyid,id)left join wac using(agencyid,id)) "
				+ "select result.agencyid, result.defagencyid,result.lat, result.lon, aname, id, name,employment,employees, url, routes, upop, rpop, overfiftypop, COALESCE(census_urbans.uname,'N/A') AS urbanname, COALESCE(census_places.pname,'N/A') AS placename, "
				+ "COALESCE(result.regionid,'N/A') AS regionname, COALESCE(census_congdists.cname,'N/A') AS congdistname, COALESCE(census_counties.cname,'N/A') AS countyname "
				+ "	FROM result LEFT JOIN census_urbans using(urbanid) "
				+ "	LEFT JOIN census_places USING(placeid) "
				+ "	LEFT JOIN census_congdists USING(congdistid) "
				+ "	LEFT JOIN census_counties ON census_counties.countyid = LEFT(result.blockid,5)";
		}
		else if (rc==0)// urban stops
		{ mainquery += "stops0 as (select map.agencyid as agencyid,map.agencyid_def as defagencyid,stop.lat, stop.lon, stop.name as name, stop.id as id, url, location, urbanid, regionid, congdistid, placeid, blockid "
				+ "	from gtfs_stops stop inner join gtfs_stop_service_map map on map.agencyid_def=stop.agencyid and map.stopid=stop.id INNER JOIN aids on stop.agencyid=aids.aid " + stopsfilter + "), "
				+"stops1 as (select stops0.* from stops0 join census_blocks using(blockid) where poptype='U'),"
				+ "stops AS (select stops1.*, urban.population"+popYear+" AS urbanpop FROM stops1 LEFT JOIN census_urbans AS urban ON urban.urbanid = stops1.urbanid AND population"+popYear+" >50000), "
				+ "agencies as (select agencies.id as agencyid, agencies.name as aname from gtfs_agencies "
				+ "agencies "+agenciesfilter+"), routes as (select map.agencyid, stops.id, coalesce(string_agg(map.routeid,'; '),'-') as routes from gtfs_stop_route_map map inner "
				+ "join stops on stops.agencyid=map.agencyid and stops.id=map.stopid "+routesfilter+" group by map.agencyid, stops.id), "
				+"pops as (select stops.agencyid, stops.id, coalesce(sum(population2010),0) as pop,block.blockid from census_blocks block inner join stops on st_dwithin(block.location, stops.location,"+String.valueOf(x)+" )group by agencyid, id,block.blockid)," 
                +"rac as (select sum(COALESCE(C000_"+popYear+",0)) as employment,agencyid,id from  pops left join lodes_rac_projection_block using(blockid) group by agencyid, id),"
                +"wac as (select sum(COALESCE(C000,0)) as employees,agencyid,id from  pops left join lodes_blocks_wac using(blockid) group by agencyid, id),"
	            + "upops as (select stops.agencyid, stops.id, "
				+ "coalesce(sum(population"+popYear+"),0) as upop from census_blocks block inner join stops on st_dwithin(block.location, stops.location, "+String.valueOf(x)+") where "
				+ popsfilter+" poptype='U' group by agencyid, id), rpops as (select stops.agencyid, stops.id, coalesce(sum(population"+popYear+"),0) as rpop from census_blocks block inner "
				+ "join stops on st_dwithin(block.location, stops.location, "+String.valueOf(x)+") where "+popsfilter+" poptype='R' group by agencyid, id), "
				+ "result AS (select stops.agencyid, stops.defagencyid , stops.lat, stops.lon, aname,employment,employees, id, name, url, stops.urbanid, stops.regionid, stops.congdistid, stops.placeid, stops.blockid, "
				+ "routes, coalesce(upop,0) as upop, coalesce(rpop,0) as rpop, COALESCE(urbanpop,0) AS overfiftypop "
				+ "	from stops inner join agencies using(agencyid) inner join routes "
				+ "	using(agencyid,id) left join upops using(agencyid,id) left join rpops using(agencyid,id)left join rac using(agencyid,id)left join wac using(agencyid,id)) "
				+ "select result.agencyid, result.defagencyid, result.lat, result.lon, aname, id, name,employment,employees, url, routes, upop, rpop, overfiftypop, COALESCE(census_urbans.uname,'N/A') AS urbanname, COALESCE(census_places.pname,'N/A') AS placename, "
				+ "COALESCE(result.regionid,'N/A') AS regionname, COALESCE(census_congdists.cname,'N/A') AS congdistname, COALESCE(census_counties.cname,'N/A') AS countyname "
				+ "	FROM result LEFT JOIN census_urbans using(urbanid) "
				+ "	LEFT JOIN census_places USING(placeid) "
				+ "	LEFT JOIN census_congdists USING(congdistid) "
				+ "	LEFT JOIN census_counties ON census_counties.countyid = LEFT(result.blockid,5)";
		}
		else
		{
			 mainquery += "stops0 as (select map.agencyid as agencyid,map.agencyid_def as defagencyid,stop.lat, stop.lon, stop.name as name, stop.id as id, url, location, urbanid, regionid, congdistid, placeid, blockid "
						+ "	from gtfs_stops stop inner join gtfs_stop_service_map map on map.agencyid_def=stop.agencyid and map.stopid=stop.id INNER JOIN aids on stop.agencyid=aids.aid " + stopsfilter + "), "
						+ "stops AS (select stops0.*, urban.population"+popYear+" AS urbanpop FROM stops0 LEFT JOIN census_urbans AS urban ON urban.urbanid = stops0.urbanid AND population"+popYear+" >50000), "
						+ "agencies as (select agencies.id as agencyid, agencies.name as aname from gtfs_agencies "
						+ "agencies "+agenciesfilter+"), routes as (select map.agencyid, stops.id, coalesce(string_agg(map.routeid,'; '),'-') as routes from gtfs_stop_route_map map inner "
						+ "join stops on stops.agencyid=map.agencyid and stops.id=map.stopid "+routesfilter+" group by map.agencyid, stops.id), "
						+"pops as (select stops.agencyid, stops.id, coalesce(sum(population2010),0) as pop,block.blockid from census_blocks block inner join stops on st_dwithin(block.location, stops.location,"+String.valueOf(x)+" )group by agencyid, id,block.blockid)," 
		                +"rac as (select sum(COALESCE(C000_"+popYear+",0)) as employment,agencyid,id from  pops left join lodes_rac_projection_block using(blockid) group by agencyid, id),"
		                +"wac as (select sum(COALESCE(C000,0)) as employees,agencyid,id from  pops left join lodes_blocks_wac using(blockid) group by agencyid, id),"
			            + "upops as (select stops.agencyid, stops.id, "
						+ "coalesce(sum(population"+popYear+"),0) as upop from census_blocks block inner join stops on st_dwithin(block.location, stops.location, "+String.valueOf(x)+") where "
						+ popsfilter+" poptype='U' group by agencyid, id), rpops as (select stops.agencyid, stops.id, coalesce(sum(population"+popYear+"),0) as rpop from census_blocks block inner "
						+ "join stops on st_dwithin(block.location, stops.location, "+String.valueOf(x)+") where "+popsfilter+" poptype='R' group by agencyid, id), "
						+ "result AS (select stops.agencyid,  stops.defagencyid , stops.lat, stops.lon, aname,employment,employees, id, name, url, stops.urbanid, stops.regionid, stops.congdistid, stops.placeid, stops.blockid, "
						+ "routes, coalesce(upop,0) as upop, coalesce(rpop,0) as rpop, COALESCE(urbanpop,0) AS overfiftypop "
						+ "	from stops inner join agencies using(agencyid) inner join routes "
						+ "	using(agencyid,id) left join upops using(agencyid,id) left join rpops using(agencyid,id)left join rac using(agencyid,id)left join wac using(agencyid,id)) "
						+ "select result.agencyid, result.defagencyid , result.lat, result.lon, aname, id, name,employment,employees, url, routes, upop, rpop, overfiftypop, COALESCE(census_urbans.uname,'N/A') AS urbanname, COALESCE(census_places.pname,'N/A') AS placename, "
						+ "COALESCE(result.regionid,'N/A') AS regionname, COALESCE(census_congdists.cname,'N/A') AS congdistname, COALESCE(census_counties.cname,'N/A') AS countyname "
						+ "	FROM result LEFT JOIN census_urbans using(urbanid) "
						+ "	LEFT JOIN census_places USING(placeid) "
						+ "	LEFT JOIN census_congdists USING(congdistid) "
						+ "	LEFT JOIN census_counties ON census_counties.countyid = LEFT(result.blockid,5)";
				
		}
		logger.debug(mainquery);
		try{
			PreparedStatement stmt = connection.prepareStatement(mainquery);
			ResultSet rs = stmt.executeQuery();				
			while (rs.next()) {
				instance = new StopR();
				instance.AgencyId = rs.getString("agencyid");
				instance.defAgencyId = rs.getString("defagencyid");
				instance.AgencyName = rs.getString("aname");
				instance.Routes = rs.getString("routes");
				instance.UPopWithinX = String.valueOf(rs.getLong("upop"));
				instance.RPopWithinX = String.valueOf(rs.getLong("rpop"));
				instance.racWithinX = String.valueOf(rs.getLong("employment"));
				instance.wacWithinX = String.valueOf(rs.getLong("employees"));
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
					instance.visits = stopsVisits.get(instance.defAgencyId + instance.StopId) + "";
				else 
					instance.visits = stopsVisits.get(agencyDefaultID + instance.StopId) + "";
				instance.lat = String.valueOf(rs.getDouble("lat"));
				instance.lon = String.valueOf(rs.getDouble("lon"));
				
				response.add(instance);
	        }		
			rs.close();
			stmt.close();
		} catch ( Exception e ) {
			e.printStackTrace();	         
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
		dropConnection(connection);
		return output;
	}
	
	/**
	 * Queries the routes reports:
	 *  routes by agency	 
	 *  routes by geographic area	
	 *  routes by agency and geographic area
	 * 
	 * @param username
	 * @param type - type of the geographical area to filter on
	 * @param areaId - ID of the geographical area to filter on
	 * @param agency - Agency ID
	 * @param date 
	 * @param day
	 * @param x - population search radius
	 * @param dbindex
	 * @param popYear - population projection year
	 * @return ArrayList <RouteR>
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
				agenciesfilter = "aids as (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true), agencies as (select agencies.id as "
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
				+ "freq as (select aid, routeid, coalesce(count(concat(routeid)),0) as frequency "
				+ "		from trips group by aid, routeid), "
				+ "service as (select aid, routeid, COALESCE(sum(length),0) as svcmiles, COALESCE(sum(tlength),0) as svchours, COALESCE(sum(stops),0) as svcstops "
				+ "		from trips group by aid,routeid), "
				+ "blocks as (select trips.aid, trips.routeid, population"+popYear+" as population, poptype, block.blockid, block.regionid, block.urbanid, block.placeid, block.congdistid "
				+ " 	from gtfs_stops stop inner join gtfs_stop_times stime on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id "
				+ "		inner join trips on stime.trip_agencyid =trips.aid and stime.trip_id=trips.tripid inner join census_blocks block on "
				+ "		st_dwithin(block.location, stop.location, "+String.valueOf(x)+") "+blocksfilter+" group by aid,routeid,block.blockid),"
				+ "upop as (select aid, routeid, coalesce(sum(population),0) as upop, coalesce(sum(population*frequency),0) as svcupop "
				+ "		from blocks inner join freq using(aid,routeid) where poptype ='U' group by aid, routeid), "
				/*+ "upopr as (select aid, routeid, sum(upop) as upop, sum(svcupop) as svcupop "
				+ "		from upop inner join trips using(aid,routeid) group by aid, routeid), "*/
				+ "rpop as (select aid, routeid, coalesce(sum(population),0) as rpop, coalesce(sum(population*frequency),0) as svcrpop "
				+ "		from blocks inner join freq using(aid,routeid) "
				+ "		where poptype ='R' group by aid, routeid), "
				/*+ "rpopr as (select aid, routeid, sum(rpop) as rpop, sum(svcrpop) as svcrpop "
				+ "		from rpop inner join trips using(aid,routeid) group by aid, routeid), "*/
				+ "routesblocks AS (select routes.agencyid AS aid, routes.id AS routeid, gtfs_stops.blockid, gtfs_stops.urbanid, gtfs_stops.placeid, "
				+ "		gtfs_stops.congdistid, gtfs_stops.regionid, gtfs_stops.location "
				+ "		from routes INNER JOIN gtfs_stop_route_map AS map ON routes.agencyid = map.agencyid AND routes.id=map.routeid "
				+ "		INNER JOIN gtfs_stops ON map.stopid=gtfs_stops.id AND map.agencyid_def=gtfs_stops.agencyid),"
				+ "upop_served AS (select sum(blocks.population"+popYear+") AS upop_served, routesblocks.aid, routeid"
				+ "		FROM routesblocks INNER JOIN census_blocks AS blocks "
				+ "		ON ST_DWithin(blocks.location,routesblocks.location, "+String.valueOf(x)+") WHERE poptype='U' GROUP BY poptype,routesblocks.aid, routeid ),"
				+ "rpop_served AS (select sum(blocks.population"+popYear+") AS rpop_served, routesblocks.aid, routeid "
				+ "		FROM routesblocks INNER JOIN census_blocks AS blocks"
				+ "		ON ST_DWithin(blocks.location,routesblocks.location, "+String.valueOf(x)+") WHERE poptype='R' GROUP BY poptype,routesblocks.aid, routeid ),"
				+ "areas as (SELECT aid,routeid, string_agg(distinct census_urbans.uname,',') AS urbans, string_agg(distinct census_counties.cname,',') AS counties, "
				+ "		string_agg(distinct census_places.pname,',') AS places, string_agg(distinct census_congdists.cname,',') AS congdists, "
				+ "		string_agg(distinct 'Region '||routesblocks.regionid,',') AS regions "
				+ "		FROM routesblocks LEFT JOIN census_urbans USING(urbanid) "
				+ "		LEFT JOIN census_counties ON census_counties.countyid=LEFT(routesblocks.blockid,5) "
				+ "		LEFT JOIN census_places USING(placeid) "
				+ "		LEFT JOIN census_congdists USING(congdistid) "
				+ "		WHERE census_urbans.uname IS NOT NULL "
				+ "		AND census_places.pname IS NOT NULL "
				+ "		AND census_counties.cname IS NOT NULL "
				+ "		AND census_congdists.cname IS NOT NULL "
				+ "		AND routesblocks.regionid IS NOT NULL "
				+ "		GROUP BY aid,routeid) "
				+ "select agencies.agencyid, aname, routes.id, shortname, longname, type, url, description, rlength, stops, "
				+ "		COALESCE(upop,0) as upop, COALESCE(rpop,0) as rpop, COALESCE(rpop_served,0) AS rpop_served, COALESCE(upop_served,0) AS upop_served,"
				+ "		COALESCE(svcstops,0) as svcstops, COALESCE(svchours,0) as svchours, COALESCE(svcmiles,0) as svcmiles, "
				+ "		COALESCE(svcupop,0) as usvcpop, COALESCE(svcrpop,0) as rsvcpop, COALESCE(urbans,'N/A') AS urbans, COALESCE(counties,'N/A') AS counties, "
				+ "		COALESCE(places,'N/A') AS places, COALESCE(congdists,'N/A') AS congdists, COALESCE(regions,'N/A') AS regions "
				+ "		from routes inner join agencies on routes.agencyid=agencies.agencyid "
				+ "		left join service on routes.agencyid=service.aid and routes.id=service.routeid "
				+ "		left join upop on routes.agencyid=upop.aid and routes.id= upop.routeid "
				+ "		left join rpop on routes.agencyid=rpop.aid and routes.id=rpop.routeid "
				+ "		left join upop_served on routes.agencyid=upop_served.aid and routes.id=upop_served.routeid "
				+ "		left join rpop_served on routes.agencyid=rpop_served.aid and routes.id=rpop_served.routeid "
				+ "		left join stopscount on routes.id = stopscount.routeid "
				+ "		left join areas on routes.id = areas.routeid and routes.agencyid = areas.aid";					
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
				instance.UPopWithinX = String.valueOf(rs.getLong("upop_served"));
				instance.RPopWithinX = String.valueOf(rs.getLong("rpop_served"));
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
			e.printStackTrace();	         
	      }					
		dropConnection(connection);
		return response;
	}
	
	/**
	 * Queries the transit agency summary report
	 * 
	 * @param username
	 * @param dbindex
	 * @return ArrayList<AgencySR>
	 */
	public static ArrayList <AgencySR> agencysr(String username, int dbindex) {
		ArrayList <AgencySR> response = new ArrayList<AgencySR>();		
		AgencySR instance;
		Connection connection = makeConnection(dbindex);		
		String mainquery ="";
		mainquery += "with "
		        + " aids as (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true), "
				+ "agencies as (select id, name, fareurl, phone, url, feedname, version, startdate, enddate, publishername, publisherurl"
				+ "		from gtfs_agencies agencies inner join aids on agencies.defaultid = aids.aid "
				+ "		inner join gtfs_feed_info info on agencies.defaultid=info.defaultid), "
				+ "stops as (select map.agencyid as aid, coalesce((count(id))::int, -1) as stops, coalesce((count(distinct placeid))::int,-1) as places, "
				+ "		coalesce((count(distinct left(blockid, 5)))::int,-1) as counties, coalesce((count(distinct regionid))::int,-1) as odotregions, "
				+ "		coalesce((count(distinct urbanid))::int,-1) as urbans, coalesce((count(distinct congdistid))::int,-1) as congdists "
				+ "		from gtfs_stops stop inner join gtfs_stop_service_map map on stop.agencyid = map.agencyid_def and stop.id=map.stopid "
				+ "		group by map.agencyid), "
				+ "fare as (select frule.route_agencyid as aid, frule.route_id as routeid, (round(avg(ftrb.price)*100))::integer as price "
				+ "		from gtfs_fare_rules frule inner join gtfs_fare_attributes ftrb "
				+ "		on ftrb.agencyid= frule.fare_agencyid and ftrb.id=frule.fare_id "
				+ "		group by frule.route_agencyid,frule.route_id), "
				+ "froutes as (select routes.agencyid as aid, coalesce((count(routes.id))::int,-1) as routes, array_agg(coalesce(price, -1) order by price) as fprices "
				+ "		from gtfs_routes routes left join fare on routes.agencyid = fare.aid and routes.id = fare.routeid "
				+ "		group by routes.agencyid) "
				+ "select id, name, fareurl, phone, url, feedname, version, startdate, enddate, publishername, publisherurl, fprices, "
				+ "		routes, stops, places, counties, odotregions, urbans, congdists "
				+ "		from agencies inner join stops on agencies.id=stops.aid "
				+ "		inner join froutes on stops.aid = froutes.aid";		
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
			e.printStackTrace();	         
	      }					
		dropConnection(connection);
		return response;
	}	
		
	/**
	 * Queries Route miles for all urban areas with minimum and maximum
	 * filter on urban areas population
	 * 
	 * @param popmin - minimum population of the urban areas
	 * @param popmax -  maximum population of the urban areas
	 * @param username
	 * @param dbindex
	 * @param popYear - population projection year
	 * @return float - route miles
	 */
	public static float AUrbansRouteMiles(Integer popmin, Integer popmax, String username, int dbindex, String popYear) 
    {	
	  Connection connection = makeConnection(dbindex);
      Statement stmt = null;
      float RouteMiles = 0;
      String query = "with aids as (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true), areas as (select urbanid from census_urbans where "
      		+ "population"+popYear+" Between " +String.valueOf(popmin)+" And "+String.valueOf(popmax)+"), trips as (select agencyid, routeid, round(max(length)::numeric,2) as length from census_urbans_trip_map map inner "
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
    	  e.printStackTrace();         
      }
      dropConnection(connection);      
      return RouteMiles;
    }
	
	/**
	 * Queries Fare Information for urban areas with filter on population. keys are; minfare, maxfare, medianfare, averagefare
	 *  
	 * @param date - array of selected dates
	 * @param day - array of selected days of the week. E.g. [sunday, monday] 
	 * @param popmin - minimum population of urban areas to filter on
	 * @param popmax - maximum population of urban areas to filter on
	 * @param username
	 * @param dbindex
	 * @param popYear - population projection year
	 * @return HashMap<String, Float>
	 */
	public static HashMap<String, Float> AUrbansFareInfo(String[] date, String[] day, Integer popmin,Integer popmax, String username, int dbindex, String popYear) 
    {	
	  Connection connection = makeConnection(dbindex);
      Statement stmt = null;
      HashMap<String, Float> response = new HashMap<String, Float>();
      ArrayList<Float> faredata = new ArrayList<Float>();
      String query = "with aids as (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true), areas as (select urbanid from census_urbans where population"+popYear+" Between "
      +String.valueOf(popmin)+" And "+String.valueOf(popmax)+"), svcids as (";
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
      try {
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);        
        while ( rs.next() ) {
        	faredata.add(rs.getFloat("fare"));                      
        }
        rs.close();
        stmt.close();        
      } catch ( Exception e ) {
    	  e.printStackTrace();         
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
	 * Queries Stops count and unduplicated urban pop within x meters of all stops within urban areas 
	 * with minimum population of popmin and maximum population of popmax.
	 * 
	 * @param popmin - minimum population of the urban areas
	 * @param popmax -  maximum population of the urban areas
	 * @param username - user session
	 * @param x - population search radius
	 * @param dbindex - databse index
	 * @param popYear - poulation projection year
	 * @return long[]
	 */
	public static long[] AUrbansstopsPop(Integer popmin,Integer popmax, String username, double x, int dbindex, String popYear) 
    {	
	  Connection connection = makeConnection(dbindex);
      Statement stmt = null;
      String querytext = "with aids as (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true), areas as (select urbanid from census_urbans where "
      		+ "population"+popYear+" Between "+String.valueOf(popmin)+" And "+String.valueOf(popmax)+"), stops as (select id, agencyid, blockid, location from gtfs_stops stop inner join aids on stop.agencyid = aids.aid inner join areas on "
      		+ "stop.urbanid = areas.urbanid), census as (select population"+popYear+" as population, block.blockid from census_blocks block inner join stops on st_dwithin(block.location, stops.location,"
      		+ String.valueOf(x)+") inner join areas on block.urbanid = areas.urbanid group by block.blockid), urbanpop as (select COALESCE(sum(population),0) as upop from census ),"
      		+"wac as (select sum(C000) as wac from lodes_blocks_wac join census using(blockid)),"
          +"rac as (select sum(C000_"+popYear+") as rac from lodes_rac_projection_block join census using(blockid)),"
            +"stopcount as (select count(stops.id) as stopscount from stops)"
            +"select COALESCE(stopscount,0) as stopscount, COALESCE(upop,0) as urbanpop,rac,wac from stopcount inner join urbanpop on true inner join wac on true inner join rac on true ";

      long[] results = new long[7];
      try {
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(querytext);        
        while ( rs.next() ) {
        	results[0] = rs.getLong("stopscount");
        	results[1] = rs.getLong("urbanpop"); 
            results[5]=rs.getLong("rac");
            results[6]=rs.getLong("wac");
       
       
        }
        rs.close();
        stmt.close();        
      } catch ( Exception e ) {
      	e.printStackTrace();         
      }
      dropConnection(connection);      
      return results;
    }
	
	/**
	 * Queries Service miles, service hours, service stops, served pop at level of service (urban and rural), 
	 * served population (urban and rural), service days, hours of service, and connected communities for 
	 * urban areas with population >= pop. keys are: svcmiles, svchours, svcstops, upopatlos, uspop, svcdays, fromtime, totime, connections
	 * 
	 * @param date - array of dates in yyyymmdd format
	 * @param day - array of selected day of the week. E.g. [sunday,monday]
	 * @param fulldates - array of selected dates in full format. E.g., [Mon 15 May 2017]
	 * @param popmin - minimum population of urban areas to filter on
	 * @param popmax - maximum population of urban areas to filter on
	 * @param username - user session
	 * @param LOS - minimum level of service
	 * @param x -  population search radius
	 * @param dbindex - databsase index
	 * @param popYear - population projection year
	 * @return HashMap<String, String>
	 */
	public static HashMap<String, String> UAreasServiceMetrics(String[] date, String[] day, String[] fulldates, int popmin,int popmax, String username, int LOS, double x, int dbindex, String popYear) 
    {	
	  Connection connection = makeConnection(dbindex);
      Statement stmt = null;
      HashMap<String, String> response = new HashMap<String, String>();      
      String query = "with aids as (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true), areas as (select urbanid from census_urbans where "
      		+ "population"+popYear+" Between "+String.valueOf(popmin)+" And "+String.valueOf(popmax)+"), svcids as (";
      for (int i=0; i<date.length; i++){
    	  query+= "(select serviceid_agencyid, serviceid_id, '"+fulldates[i]+"' as day from gtfs_calendars gc inner join aids on gc.serviceid_agencyid = aids.aid where "
    	  		+ "startdate::int<="+date[i]+" and enddate::int>="+date[i]+" and "+day[i]+" = 1 and serviceid_agencyid||serviceid_id not in (select "
    	  		+ "serviceid_agencyid||serviceid_id from gtfs_calendar_dates where date='"+date[i]+"' and exceptiontype=2) union select serviceid_agencyid, serviceid_id, '"
    	  		+fulldates[i]+"' from gtfs_calendar_dates gcd inner join aids on gcd.serviceid_agencyid = aids.aid where date='"+date[i]+"' and exceptiontype=1)";
    	  if (i+1<date.length)
				query+=" union all ";
		} 
		 query +="), trips as (select trip.agencyid as aid, trip.id as tripid, trip.route_id as routeid, round((map.length)::numeric,2) as length, map.tlength as tlength, map.stopscount"
    		+ " 	as stops "
    		+ "		from svcids inner join gtfs_trips trip using(serviceid_agencyid, serviceid_id) "
    		+ "		inner join census_urbans_trip_map map on trip.id = map.tripid and "
    		+ "		trip.agencyid = map.agencyid inner join areas on areas.urbanid = map.urbanid), "
    		+ "service as (select COALESCE(sum(length),0) as svcmiles, COALESCE(sum(tlength),0) as svchours, COALESCE(sum(stops),0) as svcstops "
    		+ "		from trips), "
    		+ "stopsatlos as (select stime.stop_agencyid as aid, stime.stop_id as stopid, stop.location as location, count(trips.aid) as service "
    		+ "		from gtfs_stops stop inner join gtfs_stop_times stime "
    		+ "		on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id "
    		+ "		inner join trips on stime.trip_agencyid =trips.aid and stime.trip_id=trips.tripid "
    		+ "		inner join areas on stop.urbanid = areas.urbanid group by stime.stop_agencyid, stime.stop_id, stop.location "
    		+ "		having count(trips.aid)>="+String.valueOf(LOS)+" ), "
			+ "stops as (select stime.stop_agencyid as aid, stime.stop_id as stopid, stop.location as location, min(stime.arrivaltime) as arrival, "
			+ "		max(stime.departuretime) as departure, count(trips.aid) as service "
			+ "		from gtfs_stops stop inner join gtfs_stop_times stime on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id "
			+ "		inner join trips on stime.trip_agencyid =trips.aid and stime.trip_id=trips.tripid "
			+ "		inner join areas on stop.urbanid = areas.urbanid "
			+ "		where stime.arrivaltime>0 and stime.departuretime>0 "
			+ "		group by stime.stop_agencyid, stime.stop_id, stop.location), "
			+ "undupblocks as (select block.population"+popYear+" as population, max(stops.service) as service "
			+ "		from census_blocks block inner join stops on st_dwithin(block.location, stops.location, "+String.valueOf(x)+") "
			+ "		inner join areas on block.urbanid = areas.urbanid group by blockid), "
			+ "undupblocks1 as (select block.population"+popYear+" as population, max(stops.service) as service ,blockid"
			+ "		from census_blocks block inner join stops on st_dwithin(block.location, stops.location, "+String.valueOf(x)+") "
			+ "		inner join areas on block.urbanid = areas.urbanid group by blockid), "
			+"swac as (select sum(C000*service) as swac from lodes_blocks_wac join undupblocks1 using(blockid)),"
            +"srac as (select sum(((C000_"+popYear+")*service):: int) as srac from lodes_rac_projection_block join undupblocks1 using(blockid)),"
			+ "undupblocksatlos as (select block.population"+popYear+" as population "
			+ "		from census_blocks block inner join stopsatlos on st_dwithin(block.location, stopsatlos.location, "+String.valueOf(x)+") "
			+ "		inner join areas on block.urbanid = areas.urbanid group by blockid), "
			+ "undupblocksatlos1 as (select block.population"+popYear+" as population,block.blockid "
			+ "		from census_blocks block inner join stopsatlos on st_dwithin(block.location, stopsatlos.location, "+String.valueOf(x)+") "
			+ "		inner join areas on block.urbanid = areas.urbanid group by blockid), "
			+"wacatlos as (select sum(C000) as wacatlos from lodes_blocks_wac join undupblocksatlos1 using(blockid)),"
            +"racatlos as (select sum(C000_"+popYear+" :: int) as racatlos from lodes_rac_projection_block join undupblocksatlos1 using(blockid)),"
            + "svchrs as (select COALESCE(min(arrival),-1) as fromtime, COALESCE(max(departure),-1) as totime from stops), "
			+ "concom as (select distinct map.urbanid from census_urbans_trip_map map "
    		+ "		inner join trips on trips.aid=map.agencyid and trips.tripid=map.tripid), "
    		+ "concomnames as (select array_agg(uname order by uname)::text as connections "
    		+ "		from concom inner join census_urbans using(urbanid)), "
    		+ "upopatlos as (select COALESCE(sum(population),0) as upoplos from undupblocksatlos), "
    		+ "upopserved as (select COALESCE(sum(population*service),0) as uspop from undupblocks), "
    		+ "svcdays as (select COALESCE(array_agg(distinct day)::text,'-') as svdays from svcids) "
    		+ "select svcmiles, svchours, svcstops, upoplos,swac,srac,racatlos,wacatlos,uspop, svdays, fromtime, totime, connections "
    		+ "		from service inner join upopatlos on true "
    		+ "		inner join upopserved on true "
    		+ "		inner join svcdays on true "
    		+ "		inner join svchrs on true "
    		+ "		inner join concomnames on true"
    		+"      inner join srac on true"
    		+"      inner join swac on true"
    		+"      inner join racatlos on true"
    		+"      inner join wacatlos on true"; 
      try {
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);        
        while ( rs.next() ) {
        	response.put("svcmiles", String.valueOf(rs.getFloat("svcmiles")));
        	response.put("svchours", String.valueOf(Math.round(rs.getLong("svchours")/36.00)/100.00));
        	response.put("svcstops", String.valueOf(rs.getLong("svcstops")));
        	response.put("upopatlos", String.valueOf(rs.getLong("upoplos")));
        	response.put("racatlos", String.valueOf(rs.getLong("racatlos")));
        	response.put("wacatlos", String.valueOf(rs.getLong("wacatlos")));
        	response.put("uspop", String.valueOf(rs.getFloat("uspop")));
        	response.put("srac", String.valueOf(rs.getFloat("srac")));
        	response.put("swac", String.valueOf(rs.getFloat("swac")));
        	response.put("svcdays", String.valueOf(rs.getString("svdays")));
        	response.put("fromtime", String.valueOf(rs.getInt("fromtime")));
        	response.put("totime", String.valueOf(rs.getInt("totime")));
        	response.put("connections", rs.getString("connections"));        	                      
        }
        rs.close();
        stmt.close();        
      } catch ( Exception e ) {
    	  e.printStackTrace();         
      }
      dropConnection(connection);      
      return response;
    }
	
	/**
	 * Queries Stops count, unduplicated urban pop and rural pop within x meters of all 
	 * stops, and route miles for a given transit agency
	 * 
	 * @param type - type of the geographical area
	 * @param agencyId - agency ID
	 * @param x - population search radius
	 * @param dbindex - database index
	 * @param popYear - population projection year
	 * @param areaid - ID of the geographical area to filter on
	 * @param geoid - ID of the geographical to be intersected with the original area
	 * @param geotype - type of the geographical to be intersected with the original area
	 * @return
	 */
	public static double[] stopsPopMiles(int type, String agencyId, double x, int dbindex, String popYear,
			String areaid, String geoid, int geotype) {
		Connection connection = makeConnection(dbindex);
		Statement stmt = null;
		String query = ""
		+ "with census as ( select population:POPYEAR as population, poptype, block.:AREAID_FIELD, block.blockid from census_blocks block inner join gtfs_stops stop on st_dwithin( block.location, stop.location, :RADIUS ) inner join gtfs_stop_service_map map on map.stopid = stop.id and map.agencyid_def = stop.agencyid where map.agencyid = :AGENCYID :CENSUS_WHERE group by block.blockid ),"
		+ "employment as (  select     sum(c000_:POPYEAR) as employment,    poptype  from     census     left join lodes_rac_projection_block using(blockid)    group by poptype), "
		+ "employees as (  select     sum(c000) as employees,    poptype  from     census     left join lodes_blocks_wac using(blockid)    group by poptype), "
		+ "uxrac as (    select sum(employment) as uxrac from employment where poptype = 'U'), "
		+ "rxrac as (    select sum(employment) as rxrac from employment where poptype = 'R'), "
		+ "uxwac as (    select sum(employees) as uxwac from employees where poptype = 'U'), "
		+ "rxwac as (    select sum(employees) as rxwac from employees where poptype = 'R'), "
		+ "urbanpop as ( select COALESCE(sum(population), 0) upop from census where poptype = 'U' ), "
		+ "ruralpop as ( select COALESCE(sum(population), 0) rpop from census where poptype = 'R' ), "
		+ "urbanstopcount as ( select count(stop.id) as urbanstopscount from gtfs_stops stop inner join gtfs_stop_service_map map on map.stopid = stop.id and map.agencyid_def = stop.agencyid inner join census_blocks using(blockid) where map.agencyid = :AGENCYID :STOPCOUNT_WHERE and poptype = 'U' ), "
		+ "ruralstopcount as ( select count(stop.id) as ruralstopscount from gtfs_stops stop inner join gtfs_stop_service_map map on map.stopid = stop.id and map.agencyid_def = stop.agencyid inner join census_blocks using(blockid) where map.agencyid = :AGENCYID :STOPCOUNT_WHERE and poptype = 'R' ), "
		+ "routes AS (SELECT DISTINCT ON(routeid) round((trip.length + trip.estlength):: numeric, 2) AS length, trip.route_id AS routeid, id FROM gtfs_trips trip WHERE trip.agencyid = :AGENCYID ORDER BY routeid, length DESC, id),"
		+ "segments AS (SELECT ST_Length(ST_Transform(ST_Intersection(segs.shape, blocks.shape), 2993)) as length, blocks.poptype FROM gtfs_trip_segments segs INNER JOIN routes ON segs.id = routes.id INNER JOIN census_blocks blocks ON ST_Intersects(segs.shape, blocks.shape) :SEGMENTS_JOIN :SEGMENTS_WHERE),"
		+ "rtmiles as (select sum(length)/1609.34 as rtmiles FROM segments), "
		+ "urtmiles as (select sum(length)/1609.34 as urtmiles FROM segments WHERE poptype = 'U'), "
		+ "rrtmiles as (select sum(length)/1609.34 as rrtmiles FROM segments WHERE poptype = 'R') "
		+ "select   COALESCE(urbanstopscount, 0) as urbanstopcount,   COALESCE(ruralstopscount, 0) as ruralstopcount,   COALESCE(upop, 0) as urbanpop,   COALESCE(rpop, 0) as ruralpop,   coalesce(uxrac, 0) as uxrac,   coalesce(rxrac, 0) as rxrac,   coalesce(uxwac, 0) as uxwac,   coalesce(rxwac, 0) as rxwac,   COALESCE(rtmiles, 0) as rtmiles,   COALESCE(urtmiles, 0) as urtmiles,   COALESCE(rrtmiles, 0) as rrtmiles from   urbanpop   inner join ruralpop on true   inner join rtmiles on true   inner join urtmiles on true   inner join rrtmiles on true   left join uxrac on true  left join rxrac on true  left join uxwac on true  left join rxwac on true  inner join urbanstopcount on true   inner join ruralstopcount on true;"
		+ "";

		String census_where = "";
		String employment_group = "";
		String stopcount_where = "";
		String segments_join = "";
		String segments_where = "";

		if (areaid == null || areaid.equals("null")) {
			// 1
		} else {
			census_where = "AND block.:AREAID_FIELD = :AREAID ";
			employment_group = "GROUP BY :AREAID_FIELD";
			stopcount_where = "AND census_blocks.:AREAID_FIELD = :AREAID ";
			segments_join = ""; // "INNER JOIN :AREAID_CENSUS_TABLE census ON ST_Intersects(segs.shape, census.shape)";
			segments_where = "WHERE blocks.:AREAID_FIELD = :AREAID ";
			if (type == 0) {
				// 2 - counties
			} else if (type == 1) {
				// 3 - census tracts
			} else if (type == 4) {
				// 4 - odot regions
			} else if (type == 2) {
				// 11 - census place
			} else if (type == 5) {
				// 12 - census congdist
			} else if ((type == 3) && (geotype == -1 || geotype == 3)) {
				// 5 - census urbans
			} else if (type == 3) {
				census_where = "AND block.:AREAID_FIELD = :AREAID AND block.:GEOID_FIELD = :GEOID ";
				stopcount_where = "AND census_blocks.:AREAID_FIELD = :AREAID AND census_blocks.:GEOID_FIELD = :GEOID ";
				segments_join = "";
				segments_where = "WHERE blocks.:AREAID_FIELD = :AREAID AND blocks.:GEOID_FIELD = :GEOID ";
				if (geotype == 0) {
					// 6 - urbans - counties
				} else if (geotype == 1) {
					// 7 - urbans - tracts
				} else if (geotype == 2) {
					// 8 - urbans - places
				} else if (geotype == 4) {
					// 9 - urbans - odot regions
				} else if (geotype == 5) { 
					// 10 - urbans - congdists
				}
			}
		}

		// query fragments
		query = query.replace(":POPYEAR", popYear);
		query = query.replace(":CENSUS_WHERE", census_where);
		query = query.replace(":EMPLOYMENT_GROUP", employment_group);
		query = query.replace(":SEGMENTS_JOIN", segments_join);
		query = query.replace(":SEGMENTS_WHERE", segments_where);
		query = query.replace(":STOPCOUNT_WHERE", stopcount_where);
		query = query.replace(":AREAID_CENSUS_TABLE", Types.getTableName(type));
		query = query.replace(":AREAID_FIELD", Types.getIdColumnName(type));
		query = query.replace(":GEOID_CENSUS_TABLE", Types.getTableName(geotype));
		query = query.replace(":GEOID_FIELD", Types.getIdColumnName(geotype));

		// parameters
		query = query.replace(":AGENCYID", "'"+agencyId+"'");
		query = query.replace(":AREAID", "'"+areaid+"'");
		query = query.replace(":GEOID", "'"+geoid+"'");
		query = query.replace(":RADIUS", String.valueOf(x));

		double[] results = new double[11];
		logger.debug(query);
		try {
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				results[0] = rs.getLong("urbanstopcount");
				results[1] = rs.getLong("ruralstopcount");
				results[2] = rs.getLong("urbanpop");
				results[3] = rs.getLong("ruralpop");
				results[4] = Math.round(rs.getFloat("rtmiles") * 100.0) / 100.0;
				results[5] = Math.round(rs.getFloat("urtmiles") * 100.0) / 100.0;
				results[6] = Math.round(rs.getFloat("rrtmiles") * 100.0) / 100.0;
				results[7] = rs.getLong("uxrac");
				results[8] = rs.getLong("rxrac");
				results[9] = rs.getLong("uxwac");
				results[10] = rs.getLong("rxwac");
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		dropConnection(connection);
		return results;
	}

	public static String BuildServiceIDs(String[] date, String[] day, String[] fulldates, String agencyId) {
		String query = "";
		for (int i = 0; i < date.length; i++) {
			query += "(select serviceid_agencyid, serviceid_id, '" + fulldates[i]
					+ "' as day from gtfs_calendars gc where startdate::int<=" + date[i] + " and enddate::int>="
					+ date[i] + " and " + day[i]
					+ " = 1 and serviceid_agencyid||serviceid_id not in (select serviceid_agencyid||serviceid_id from gtfs_calendar_dates where date='"
					+ date[i] + "' and exceptiontype=2) union select serviceid_agencyid, serviceid_id, '" + fulldates[i]
					+ "' from gtfs_calendar_dates gcd where date='" + date[i] + "' and exceptiontype=1)";
			if (i + 1 < date.length)
				query += " union all ";
		}
		return query;
	}

	/**
	 * Queries Service miles, service hours, service stops, served population (urban and rural), 
	 * service days, and hours of service for a given agency id. 
	 * keys are: svcmiles, svchours, svcstops, uspop, rspop, svcdays, fromtime, and totime
	 * 
	 * @param date - array of dates in yyyymmdd format
	 * @param day - array of selected day of the week. E.g. [sunday,monday]
	 * @param fulldates - array of selected dates in full format. E.g., [Mon 15 May 2017]
	 * @param agencyId - agency ID
	 * @param x - population search radius
	 * @param LOS - minimum level of service
	 * @param dbindex
	 * @param popYear - population projection year
	 * @param areaid - ID of the geographical area to filter on
	 * @param type- type of the geographical area to filter on
	 * @param geotype - type of the geographical area to be intersected with urban areas for filtering service
	 * @param geoid - ID of the geographical area to be intersected with urban areas for filtering service 
	 * @return HashMap<String, String>
	 */
	public static HashMap<String, String> AgencyServiceMetrics(String[] date, String[] day, String[] fulldates,
			String agencyId, double x, int LOS, int dbindex, String popYear, String areaid, int type, int geotype,
			String geoid) {
		Connection connection = makeConnection(dbindex);
		Statement stmt = null;
		HashMap<String, String> response = new HashMap<String, String>();

		String query = "";
		query += "with svcids as (" + BuildServiceIDs(date, day, fulldates, agencyId) + "),";
		query += ""
		+ "regions as (select st_union(shape) as shape, odotregionid as regionid from census_counties where odotregionid = :GEOID group by odotregionid), "		
		+ "trips as ( select :SELECT_TRIP_LENGTH trip.agencyid as aid, trip.id as tripid, trip.route_id as routeid from svcids inner join gtfs_trips trip using( serviceid_agencyid, serviceid_id ) :AREAID_JOIN :GEOID_JOIN where trip.agencyid = :AGENCYID :TRIP_WHERE ), "
		+ "service as ( select COALESCE( sum(length), 0 ) as svcmiles, COALESCE( sum(tlength), 0 ) as svchours, COALESCE( sum(stops), 0 ) as svcstops from trips ), "
		+ "stops as ( select stop.blockid, trips.aid as aid, stime.stop_id as stopid, min(stime.arrivaltime) as arrival, max(stime.departuretime) as departure, stop.location, count(trips.aid) as service from gtfs_stops stop inner join gtfs_stop_times stime on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id inner join trips on stime.trip_agencyid = trips.aid and stime.trip_id = trips.tripid group by trips.aid, stime.stop_id, stop.location, stop.blockid ), "
		+ "svcstops_urban AS ( SELECT SUM(service) AS svcstops_urban FROM stops INNER JOIN census_blocks USING(blockid) WHERE poptype = 'U' ), "
		+ "svcstops_rural AS ( SELECT SUM(service) AS svcstops_rural FROM stops INNER JOIN census_blocks USING(blockid) WHERE poptype = 'R' ), "
		+ "stops_with_arrivals as ( select trips.aid as aid, stime.stop_id as stopid, min(stime.arrivaltime) as arrival, max(stime.departuretime) as departure, stop.location, count(trips.aid) as service from gtfs_stops stop inner join gtfs_stop_times stime on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id inner join trips on stime.trip_agencyid = trips.aid and stime.trip_id = trips.tripid where stime.arrivaltime > 0 and stime.departuretime > 0 group by trips.aid, stime.stop_id, stop.location ), "
		+ "undupblocks as ( select :AREAID_FIELD as areaid, block.population:POPYEAR as population, block.poptype, block.landarea, block.blockid, sum(stops.service) as service from census_blocks block inner join stops on st_dwithin( block.location, stops.location, :RADIUS ) :UNDUPBLOCKS_WHERE group by block.blockid ), "
		+ "svchrs as ( select COALESCE( min(arrival), -1 ) as fromtime, COALESCE( max(departure), -1 ) as totime from stops_with_arrivals ), "
		+ "employment as (  select     sum(c000_:POPYEAR) as employment,     service, poptype   from     undupblocks     left join lodes_rac_projection_block using(blockid)   group by     areaid,     service, poptype), "
		+ "employees as (  select     sum(c000) as employees,     service, poptype  from     undupblocks     left join lodes_blocks_wac using(blockid)   group by     areaid,     service, poptype), "

		+ "uracatlos as (  select     COALESCE(      sum(employment),       0    ) as uracatlos   from     employment  WHERE poptype = 'U'), "
		+ "rracatlos as (  select     COALESCE(      sum(employment),       0    ) as rracatlos   from     employment  WHERE poptype = 'R'), "
		+ "uracserved as (  select     COALESCE(      sum(employment * service),       0    ) as uracserved   from     employment  WHERE poptype = 'U'), "
		+ "rracserved as (  select     COALESCE(      sum(employment * service),       0    ) as rracserved   from     employment  WHERE poptype = 'R'), "
		+ "uwacatlos as (  select     COALESCE(      sum(employees),       0    ) as uwacatlos   from     employees  WHERE poptype = 'U'), "
		+ "rwacatlos as (  select     COALESCE(      sum(employees),       0    ) as rwacatlos   from     employees  WHERE poptype = 'R'), "
		+ "uwacserved as (  select     COALESCE(      sum(employees * service),       0    ) as uwacserved   from     employees  WHERE poptype = 'U'), "
		+ "rwacserved as (  select     COALESCE(      sum(employees * service),       0    ) as rwacserved   from     employees  WHERE poptype = 'R'), "

		+ "upopserved as ( select COALESCE( sum(population * service), 0 ) as uspop, SUM(landarea) as urbanlandarea_served from undupblocks where poptype = 'U' ), "
		+ "rpopserved as ( select COALESCE( sum(population * service), 0 ) as rspop, SUM(landarea) as rurallandarea_served from undupblocks where poptype = 'R' ), "		
		+ "upop_los as ( select COALESCE( sum(population), 0 ) as upop_los, SUM(landarea) AS urbanlandarea_los from undupblocks where poptype = 'U' AND service >= :SERVICE ), "
		+ "rpop_los as ( select COALESCE( sum(population), 0 ) as rpop_los, SUM(landarea) AS rurallandarea_los from undupblocks where poptype = 'R' AND service >= :SERVICE ), "
		+ "svcdays as ( select COALESCE( array_agg(distinct day)::text, '-' ) as svdays from svcids ) "
		+ "select   svcmiles,   svchours,   svcstops_urban,   svcstops_rural,   upop_los,   urbanlandarea_los,   rpop_los,   rurallandarea_los,   uspop,   urbanlandarea_served,   rspop,   rurallandarea_served,   uracatlos,   rracatlos,  uracserved,   rracserved,  uwacatlos,   rwacatlos,  uwacserved,   rwacserved,  svdays,   fromtime,   totime from   service   inner join upopserved on true   inner join rpopserved on true   inner join svcdays on true   left join uracatlos on true  left join rracatlos on true  left join uracserved on true  left join rracserved on true  left join uwacatlos on true  left join rwacatlos on true  left join uwacserved on true  left join rwacserved on true  inner join svchrs on true   inner join svcstops_urban on true   inner join svcstops_rural on true   inner join upop_los on true   inner join rpop_los on true"
		+ "";

		// Query options
		String select_trip_length = "";
		String trip_where = "";
		String undupblocks_where = "";
		String areaid_join = "";
		String geoid_join = "";

		if (areaid.equals("null") || areaid == null) {
			// 1 - Default
			select_trip_length = "trip.stopscount as stops, trip.tlength as tlength, round((trip.length + trip.estlength):: numeric, 2) as length,";
		} else {
			// AREAID
			select_trip_length =  "map.tlength as tlength, map.stopscount as stops, round((map.length):: numeric, 2) as length,";
			trip_where = "AND map.:AREAID_FIELD = :AREAID ";
			areaid_join = "INNER JOIN :AREAID_TABLE map ON trip.id = map.tripid AND trip.agencyid = map.agencyid";
			undupblocks_where = "WHERE :AREAID_FIELD = :AREAID ";
			if (type == 0) {
				// 2 - counties
			} else if (type == 1) {
				// 3 - census tracts
			} else if (type == 2) {
				// 4 - census places
			} else if (type == 4) {
				// 11 - ODOT regions
			} else if (type == 5) {
				// 12 - congressional districts
			} else if ((type == 3) && (geotype == -1 || geotype == 3)) {
				// 5 - census urbans
			} else if (type == 3) {
				// AREAID AND GEOID
				select_trip_length = "map.tlength as tlength, map.stopscount as stops, (ST_Length(st_transform(st_intersection(maps.shape, map.shape),2993))/ 1609.34) as length,";				
				trip_where = "AND map.:AREAID_FIELD = :AREAID AND maps.:GEOID_FIELD = :GEOID ";
				geoid_join = "INNER JOIN :GEOID_TABLE maps on trip.id = maps.tripid and trip.agencyid = maps.agencyid";
				undupblocks_where = "WHERE :AREAID_FIELD = :AREAID AND :GEOID_FIELD = :GEOID ";
				if (geotype == 0) {
					// 6 - urban - counties
				} else if (geotype == 1) {
					// 7 - urban - tracts
				} else if (geotype == 2) {
					// 8 - urban - places
				} else if (geotype == 5) {
					// 10 - urban - congressional districts
				} else if (geotype == 4) {
					// 9 - urbans - odot regions
					geoid_join = "CROSS JOIN regions maps";
				}
			}
		}

		// query fragments
		query = query.replace(":POPYEAR", popYear);
		query = query.replace(":SELECT_TRIP_LENGTH", select_trip_length);
		query = query.replace(":TRIP_WHERE", trip_where);
		query = query.replace(":UNDUPBLOCKS_WHERE", undupblocks_where);
		query = query.replace(":AREAID_JOIN", areaid_join);
		query = query.replace(":GEOID_JOIN", geoid_join);
		query = query.replace(":AREAID_TABLE", Types.getTripMapTableName(type));
		query = query.replace(":GEOID_TABLE", Types.getTripMapTableName(geotype));
		query = query.replace(":AREAID_FIELD", Types.getIdColumnName(type));
		query = query.replace(":GEOID_FIELD", Types.getIdColumnName(geotype));

		// parameters
		query = query.replace(":AGENCYID", "'"+agencyId+"'");
		query = query.replace(":AREAID", "'"+areaid+"'");
		query = query.replace(":GEOID", "'"+geoid+"'");
		query = query.replace(":RADIUS", String.valueOf(x));
		query = query.replace(":SERVICE", String.valueOf(LOS));

		try {
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				response.put("svcmiles", String.valueOf(rs.getFloat("svcmiles")));
				response.put("svchours", String.valueOf(Math.round(rs.getLong("svchours") / 36.00) / 100.00));
				response.put("svcstops_rural", String.valueOf(rs.getLong("svcstops_rural")));
				response.put("svcstops_urban", String.valueOf(rs.getLong("svcstops_urban")));
				response.put("uspop", String.valueOf(rs.getLong("uspop")));
				response.put("rspop", String.valueOf(rs.getLong("rspop")));
				response.put("upop_los", String.valueOf(rs.getLong("upop_los")));
				response.put("rpop_los", String.valueOf(rs.getLong("rpop_los")));				

				response.put("racatlos", String.valueOf(rs.getLong("uracatlos") + rs.getLong("rracatlos")));
				response.put("racserved", String.valueOf(rs.getLong("uracserved") + rs.getLong("rracserved")));
				response.put("wacatlos", String.valueOf(rs.getLong("uwacatlos") + rs.getLong("rwacatlos")));
				response.put("wacserved", String.valueOf(rs.getLong("uwacserved") + rs.getLong("rwacserved")));

				response.put("uracatlos", String.valueOf(rs.getLong("uracatlos")));
				response.put("rracatlos", String.valueOf(rs.getLong("rracatlos")));
				response.put("uracserved", String.valueOf(rs.getLong("uracserved")));
				response.put("rracserved", String.valueOf(rs.getLong("rracserved")));
				response.put("uwacatlos", String.valueOf(rs.getLong("uwacatlos")));
				response.put("rwacatlos", String.valueOf(rs.getLong("rwacatlos")));
				response.put("uwacserved", String.valueOf(rs.getLong("uwacserved")));

				response.put("svcdays", String.valueOf(rs.getString("svdays")));
				response.put("fromtime", String.valueOf(rs.getInt("fromtime")));
				response.put("totime", String.valueOf(rs.getInt("totime")));
				response.put("urbanlandarea_served", String.valueOf((int)rs.getFloat("urbanlandarea_served")));
				response.put("rurallandarea_served", String.valueOf((int)rs.getFloat("rurallandarea_served")));
				response.put("urbanlandarea_los", String.valueOf((int)rs.getFloat("urbanlandarea_los")));
				response.put("rurallandarea_los", String.valueOf((int)rs.getFloat("rurallandarea_los")));
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		dropConnection(connection);
		return response;
	}


	/**
	 * Queries Service miles, service hours, service stops, served pop at level of service (urban and rural), 
	 * served population (urban and rural), service days, and hours of service
	 * for the whole state. keys are: svcmiles, svchours, svcstops, upopatlos, rpopatlos, 
	 * uspop, rspop, svcdays, fromtime, totime.
	 * 
	 * @param date - array of dates in yyyymmdd format
	 * @param day - array of selected day of the week. E.g. [sunday,monday]
	 * @param fulldates - array of selected dates in full format. E.g., [Mon 15 May 2017]
	 * @param username
	 * @param LOS - minimum level of service
	 * @param x - population search radius
	 * @param dbindex
	 * @param popYear - population projection year
	 * @return HashMap<String, String>
	 */
	public static HashMap<String, String> StatewideServiceMetrics(String[] date, String[] day, String[] fulldates, String username, int LOS, double x, int dbindex, String popYear) 
    {	
	  Connection connection = makeConnection(dbindex);
      Statement stmt = null;
      HashMap<String, String> response = new HashMap<String, String>();      
      
      String query = "with aids as (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true), svcids as (";
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
    		+ "st_dwithin(block.location, stops.location,"+String.valueOf(x)+") group by blockid), "
    		+"undupblocksatlos1 as (select blockid from census_blocks block inner join stopsatlos on st_dwithin(block.location, stopsatlos.location,"+String.valueOf(x)+") group by blockid), "
    		+"undupblocks1 as (select blockid,max(stops.service) as service from census_blocks block inner join stops on st_dwithin(block.location, stops.location,"+String.valueOf(x)+") group by blockid)," 
       		+ "undupblocksatlos as (select block.population"+popYear+" as population, block.poptype from census_blocks "
    		+ "block inner join stopsatlos on st_dwithin(block.location, stopsatlos.location,"+String.valueOf(x)+") group by blockid), svchrs as (select COALESCE(min(arrival),-1)"
    		+ " as fromtime, COALESCE(max(departure),-1) as totime from stops), upopatlos as (select COALESCE(sum(population),0) as upoplos from undupblocksatlos where poptype='U'), "
    		+"racatlos as (Select COALESCE(sum(c000_"+popYear+"),0) as racatlos from undupblocksatlos1 left join lodes_rac_projection_block using (blockid)),"
            +"wacatlos as (Select COALESCE(sum(c000),0) as wacatlos from undupblocksatlos1 left join lodes_blocks_wac  using(blockid)),"
    		+ "rpopatlos as (select COALESCE(sum(population),0) as rpoplos from undupblocksatlos where poptype='R'), upopserved as (select COALESCE(sum(population*service),0) "
    		+ "as uspop from undupblocks where poptype='U'), rpopserved as (select COALESCE(sum(population*service),0) as rspop from undupblocks where poptype='R'), "
    		+"racserved as (Select COALESCE(sum(c000_"+popYear+" * service),0) as srac from undupblocks1 left join lodes_rac_projection_block using (blockid)),"
            +"wacserved as (Select COALESCE(sum(c000 * service),0) as swac from undupblocks1 left join lodes_blocks_wac  using(blockid)),"
    		+ "svcdays as (select COALESCE(array_agg(distinct day)::text,'-') as svdays from svcids) "
    		+ "select svcmiles, svchours, svcstops, upoplos, rpoplos, uspop, rspop, svdays, fromtime, totime,racatlos,wacatlos,srac,swac from service inner join upopatlos on true inner join rpopatlos on true inner join upopserved on true inner join rpopserved on true inner join svcdays on true inner join svchrs on true inner join racatlos on true  inner join wacatlos on true inner join wacserved on true inner join racserved on true ";
      try {
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);        
        while ( rs.next() ) {
        	response.put("svcmiles", String.valueOf(rs.getFloat("svcmiles")));
        	response.put("svchours", String.valueOf(Math.round(rs.getLong("svchours")/36.00)/100.00));
        	response.put("svcstops", String.valueOf(rs.getLong("svcstops")));
        	response.put("upopatlos", String.valueOf(rs.getLong("upoplos")));
        	response.put("rpopatlos", String.valueOf(rs.getLong("rpoplos")));
        	response.put("racatlos", String.valueOf(rs.getLong("racatlos")));
        	response.put("wacatlos", String.valueOf(rs.getLong("wacatlos")));
        	response.put("srac", String.valueOf(rs.getLong("srac")));
        	response.put("swac", String.valueOf(rs.getLong("swac")));
        	response.put("uspop", String.valueOf(rs.getFloat("uspop")));
        	response.put("rspop", String.valueOf(rs.getFloat("rspop")));
        	response.put("svcdays", String.valueOf(rs.getString("svdays")));
        	response.put("fromtime", String.valueOf(rs.getInt("fromtime")));
        	response.put("totime", String.valueOf(rs.getInt("totime")));        	        	                      
       
        }
        rs.close();
        stmt.close();        
      } catch ( Exception e ) {
    	  e.printStackTrace();         
      }
      dropConnection(connection);      
      return response;
    }
	
	/**
	 * returns sum of unduplicated population and employment within x distance of all stops in the state
	 * 
	 * @param x - population search radius
	 * @param username
	 * @param dbindex
	 * @param popYear - population projection year
	 * @return long[]
	 */
	public static long [] PopWithinX(double x, String username, int dbindex, String popYear){
		long[] response = new long [6];
		Connection connection = makeConnection(dbindex);
		Statement stmt = null;
		try{
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery( "with aids as (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true), "
					+ "pops as (select population"+popYear+" as population from census_blocks block inner join gtfs_stops stop on st_dwithin(block.location,stop.location,"+String.valueOf(x)+") inner join aids on "
					+ "stop.agencyid=aids.aid group by block.blockid), "
					+ "pops1 as (select block.blockid from census_blocks block inner join gtfs_stops stop on st_dwithin(block.location,stop.location,"+String.valueOf(x)+") inner join aids on "
					+ "stop.agencyid=aids.aid ), "
					+"rac as (select sum(C000_"+popYear+") as rac  from pops1 left join lodes_rac_projection_block using(blockid)),"
					+"wac as (select sum(C000) as wac  from  pops1 left join lodes_blocks_wac using(blockid)),"
					+ "pop as (select sum(population) as pop from pops)"
					+"select pop,rac,wac from pop cross join rac cross join wac");	
			while ( rs.next() ) {
				response[0] = rs.getLong("pop");
				response[1] = rs.getLong("rac");
				response[2] = rs.getLong("wac");
			}
		} catch ( Exception e ) {
			e.printStackTrace();	         
	      }
		dropConnection(connection);
	
		return response;
	}
	
	/**
	 * returns the population within x distance of all stops in the state that are located in urban areas
	 * with over 50k population as well as the population of urban areas with less than 50k pop.
	 * 
	 * @param x - population search radius
	 * @param username
	 * @param dbindex
	 * @return long[]
	 */
	public static long[] cutOff50(double x, String username, int dbindex, String popYear){
		long[] response = new long[4];
		Connection connection = makeConnection(dbindex);
		Statement stmt = null;
		try{
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery( "with aids as (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true), "
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
			e.printStackTrace();
			}
		dropConnection(connection);
		return response;
	}
	
	/**
	 * returns route Miles for the whole state
	 * @param username
	 * @param dbindex
	 * @return
	 */
	public static double StateRouteMiles(String username, int dbindex){
		double response = 0;
		Statement stmt = null;
		Connection connection = makeConnection(dbindex);
		try{
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("with aids as (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true), trips as "
					+ "(select max(length+estlength) as rlength from gtfs_trips trip inner join aids on trip.serviceid_agencyid = aids.aid group by trip.route_agencyid, "
					+ "trip.route_id) select coalesce(sum(rlength), 0) as rtmiles from trips");	
			while ( rs.next() ) {
				response = rs.getDouble("rtmiles"); 				
			}
		} catch ( Exception e ) {
			e.printStackTrace();	         
	      }
		dropConnection(connection);
		return response;
	}
		
	/**
	 * Queries undupliated population within x meters of all stops in urban areas with population larger than pop
	 * 
	 * @param pop
	 * @param dbindex
	 * @param username
	 * @param x - population search radius
	 * @return
	 */
	public static long UrbanCensusbyPop(int pop, int dbindex, String username, double x) 
    {	
	  Connection connection = makeConnection(dbindex);      
      Statement stmt = null;
      long population = 0;
      try {
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery( "with aids as (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true),uids as (select urbanid from "
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
    	  e.printStackTrace();         
      }
      dropConnection(connection);      
      return population;
    }
		
	/**
	 * Queries agency clusters (connected agencies) and returns a list of all transit agencies with their connected agencies
	 * 
	 * @param dist - stop search radius
	 * @param username
	 * @param dbindex
	 * @return List<agencyCluster>
	 */
	public static List<agencyCluster> agencyCluster(double dist, String username, int dbindex){
		List<agencyCluster> response = new ArrayList<agencyCluster>();
		Connection connection = makeConnection(dbindex);
		Statement stmt = null;
		try{
			stmt = connection.createStatement();
			String query = "with aids as (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true),"
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
			e.printStackTrace();	         
	      }
		dropConnection(connection);
		return response;
	}
	
	/**
	 * Queries connected transit agencies and list of connections for a given transit agency (connected agencies extended report)
	 * 
	 * @param dist - stop search radius
	 * @param agencyId  -agency ID
	 * @param username
	 * @param dbindex
	 * @return List<agencyCluster>
	 */
	public static List<agencyCluster> agencyClusterDetails(double dist, String agencyId, String username, int dbindex){
		List<agencyCluster> response = new ArrayList<agencyCluster>();
		Connection connection = makeConnection(dbindex);
		Statement stmt = null;
		try{
			stmt = connection.createStatement();
			String query = "with aids as (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true), "
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
			e.printStackTrace();	    }
		dropConnection(connection);
		return response;
	}
	
	/**
	 * Queries stops of a given agency.
	 * Used to generate Connected Agencies On-map report.
	 * 
	 * @param agencyId
	 * @param dbindex
	 * @return CAStopsList
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
		dropConnection(connection);
		return result;
	}
	
	/**
	 * Queries frequency of service for all stops in the database for a set of dates and days
	 * 
	 * @param agency
	 * @param date - array of selected dates in yyyymmdd format
	 * @param day - array of selected days of the week. For example [sunday,monday] 
	 * @param username
	 * @param dbindex
	 * @return
	 */
	public static HashMap<String, Integer> stopFrequency(String agency, String[] date, String[] day, String username, int dbindex,int stime,int etime){				
		HashMap<String, Integer> response = new HashMap<String, Integer>();
		Connection connection = makeConnection(dbindex);
		String agencyFilter = "";
		logger.debug(stime+"-"+etime);
		if (agency != null){
			agencyFilter = " AND agency_id IN (SELECT defaultid FROM gtfs_agencies WHERE id='" + agency + "')";
		}
		String mainquery ="with aids as (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true" + agencyFilter + "), svcids as (";
		Statement stmt = null;
		for (int i=0; i<date.length; i++){
			mainquery+= "(select serviceid_agencyid, serviceid_id from gtfs_calendars gc inner join aids on gc.serviceid_agencyid = aids.aid where startdate::int<="+date[i]
					+" and enddate::int>="+date[i]+" and "+day[i]+"=1 and serviceid_agencyid||serviceid_id not in (select serviceid_agencyid||serviceid_id from "
					+ "gtfs_calendar_dates where date='"+date[i]+"' and exceptiontype=2) union select serviceid_agencyid as aid, serviceid_id as sid from gtfs_calendar_dates gcd "
					+ "inner join aids on gcd.serviceid_agencyid = aids.aid where date='"+date[i]+"' and exceptiontype=1)";
			if (i+1<date.length)
				mainquery+=" union all ";
		}
		mainquery +="), trips as (select agencyid as aid, id as tripid,count(id) as servicecount from svcids inner join gtfs_trips trip using(serviceid_agencyid, serviceid_id )group by id,aid), "
				+"triptimes as (SELECT times.trip_agencyid, times.trip_id, FLOOR(MIN(arrivaltime)/3600) AS arrival_hour, FLOOR(MIN(arrivaltime)%3600/60) AS arrival_minute, FLOOR((MIN(arrivaltime)%3600%60)/60) AS arrival_second, "
	 			+" 	FLOOR(MAX(departuretime)/3600) AS departure_hour, FLOOR(MAX(departuretime)%3600/60) AS departure_minute, FLOOR((MAX(departuretime)%3600%60)/60) AS departure_second,servicecount"
	 		    +"	FROM trips INNER JOIN gtfs_stop_times AS times "
	 			+"ON trips.tripid = times.trip_id "
				+"WHERE times.arrivaltime > 0 "
	 			+"GROUP BY times.trip_agencyid, times.trip_id,servicecount),"
	 			+"trips1 as ( select trip_agencyid as aid ,trip_id as tripid,(arrival_hour*100+arrival_minute),arrival_hour,arrival_minute,servicecount from triptimes where (arrival_hour*100+arrival_minute) between "+stime+" and "+etime+" ),"	
				+ "stopservices0 as (select stime.stop_agencyid as aid, stime.stop_agencyid||stime.stop_id as stopid, COALESCE(count(trips1.aid)*servicecount,0) as service "
				+ "		from gtfs_stop_times stime JOIN trips1 on stime.trip_agencyid =trips1.aid and stime.trip_id=trips1.tripid "
				+ "		group by stime.stop_agencyid, stime.stop_id,servicecount), "
				+"stopservices01 as (select aid,stopid, sum(service)as service from stopservices0 group by stopid,aid),"
				+ "stopservices1 as (select stop_agencyid as aid, stop_agencyid||stop_id as stopid, 0 as service "
				+ "		FROM gtfs_stop_times  where stop_agencyid||stop_id NOT IN (SELECT stopid FROM stopservices01) "
				+ "		group by stop_agencyid, stop_id), "
				+ "stopservices as (select * from stopservices01 UNION ALL select * from stopservices1)"
				+ " select stopservices.stopid, stopservices.service from aids INNER JOIN stopservices USING(aid)";
		try{
				stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(mainquery);
				while (rs.next()) {
					response.put(rs.getString("stopid"), rs.getInt("service"));
			        }
				rs.close();
				stmt.close();
			} catch ( Exception e ) {
				e.printStackTrace();		         
		      }
						
		dropConnection(connection);
		return response;
	}
	
	public static HashMap<String, Integer> stopFrequency1(String agency, String[] date, String[] day, String username, int dbindex){				
		HashMap<String, Integer> response = new HashMap<String, Integer>();
		Connection connection = makeConnection(dbindex);
		String agencyFilter = "";
		if (agency != null){
			agencyFilter = " AND agency_id IN (SELECT defaultid FROM gtfs_agencies WHERE id='" + agency + "')";
		}
		String mainquery ="with aids as (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true" + agencyFilter + "), svcids as (";
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
			try{
				stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(mainquery);
				while (rs.next()) {
					response.put(rs.getString("stopid"), rs.getInt("service"));
			        }
				rs.close();
				stmt.close();
			} catch ( Exception e ) {
				e.printStackTrace();		         
		      }
						
		dropConnection(connection);
		return response;
	}
	
	/**
	 * A HashMap of the clusters is generated. Each stop ID forms a key and the values are the stops that are 
	 * within the given radius of the key. (the values reflect ID and Agency IDs stops in a cluster)
	 * 
	 * @param radius
	 * @param dbindex
	 * @param username
	 * @return HashMap<String, HashSet<String>>
	 */
	public static HashMap<String, KeyClusterHashMap> getClusters(double radius, int dbindex, String username){
		HashMap<String, KeyClusterHashMap> y = new HashMap<String, KeyClusterHashMap>();
		String query = "WITH aids AS (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true), "
				+ " stops0 AS (SELECT stops1.id AS stop1, stops2.id AS stop2,stops1.id || ':' || stops1.agencyid AS clusterid, stops1.agencyid AS agencyid_def1, stops2.id || ':' || stops2.agencyid AS stop, stops2.agencyid AS agencyid_def2"
				+ " FROM gtfs_stops AS stops1 LEFT JOIN gtfs_stops AS stops2	"
				+ " ON ST_Dwithin(stops1.location, stops2.location, " + radius + ")),"
				+ " stops1 AS (SELECT stops0.* FROM stops0 INNER JOIN aids ON agencyid_def1 IN (aids.aid)),"
				+ " stops2 AS (SELECT stops1.* FROM stops1 INNER JOIN aids ON agencyid_def2 IN (aids.aid)),"
				+ " stops3 AS (SELECT stops2.*, map.agencyid as agencyid1 FROM stops2 INNER JOIN gtfs_stop_service_map AS map ON stop1=map.stopid AND stops2.agencyid_def1=map.agencyid_def),"
				+ " stops4 AS (SELECT stops3.*, map.agencyid as agencyid2 FROM stops3 INNER JOIN gtfs_stop_service_map AS map ON stop2=map.stopid AND stops3.agencyid_def2=map.agencyid_def)"
				+ " SELECT stops4.clusterid, array_agg(stop) AS stops, array_agg(distinct agencyid2) AS agencies FROM stops4 GROUP BY clusterid"; 
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
	 * Queries the transit part of the on map report
	 * 
	 * @param date - selected date
	 * @param day - selected day of the week
	 * @param username
	 * @param d - radius of the circle drawn on map
	 * @param lat - latitude of the centroid of the circle drawn on map or array of latitudes of points forming the polygon on map
	 * @param lon - longitude of the centroid of the circle drawn on map or array of longitudes of points forming the polygon on map
	 * @param losR - search radius for minimum level service
	 * @param dbindex
	 * @return MapTransit
	 */
	public static MapTransit onMapStops(String[] date, String[] day, String username, double d, double[] lat, double[] lon, double losR, int dbindex) {
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
		String mainquery = "with aids as (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true), svcids as (";
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
				+ "stopsa as (select stopsas.*, array_agg(blockid) blks from stopsas left join census_blocks on st_dwithin(census_blocks.location, loc,"+losR+") group by stopsas.agency, stopsas.aid, stopsas.routes, stopsas.stopid, stopsas.name, stopsas.location, stopsas.loc, stopsas.svc)"
				+ "select * from stopsa";
		try{
			PreparedStatement stmt = connection.prepareStatement(mainquery);
			ResultSet rs = stmt.executeQuery();	
			String aid = "";
			String cid = "";
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
			e.printStackTrace();	         
	      }					
		dropConnection(connection);
		return response;
	}
	
	/**
	 * Queries the census population, employment, and title vi parts of the on map report
	 * @param d - search radius
	 * @param lat - latitude of the centroid of the circle drawn on map or array of latitudes of points forming the polygon on map
	 * @param lon - longitude of the centroid of the circle drawn on map or array of longitudes of points forming the polygon on map
	 * @param dbindex
	 * @return MapGeo
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
		try{
			PreparedStatement stmt = connection.prepareStatement(mainquery);
			ResultSet rs = stmt.executeQuery();	
			String cname = "";
			String ccname = "";
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
			e.printStackTrace();	         
	      }					
		dropConnection(connection);
		return response;
	}
	
	/**
	 * Queries the transit agency tree menu
	 * 
	 * @param date - selected date
	 * @param day - selected day of the week
	 * @param username
	 * @param dbindex
	 * @return AgencyRouteList
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
			mainquery += "with aids as (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true), svcids as (";
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
			mainquery += "with aids as (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true), trips as (select trip.agencyid as aid, trip.tripshortname "
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
			e.printStackTrace();	         
	      }					
		dropConnection(connection);
		return response;
	}
	
	/**
	 * Queries calendar start and end dates by username or username and agency ID
	 * 
	 * @param username
	 * @param agency - agency ID
	 * @param dbindex
	 * @return StartEndDates
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
		String mainquery ="";
		if (agency==null){
			mainquery = "with aids as (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true) select coalesce(min(startdate::int),"+startDate
					+") as start, coalesce(max(enddate::int),"+endDate+") as end from gtfs_feed_info inner join aids on aid=defaultid";
			} else {
			mainquery = "select coalesce(min(startdate::int),"+startDate+") as start, coalesce(max(enddate::int),"+endDate+") as end from gtfs_feed_info inner join gtfs_agencies "
					+ "using(defaultid) where id='"+agency+"'";
		}				
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
			e.printStackTrace();	         
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
	 * Adds up title VI data in single with all.	  
	 * 
	 * @param all (TitleVIDataFloat)
	 * @param single (TitleVIDataFloat)
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

	/**
	 * returns statewide summary report
	 *  
	 * @param dbindex
	 * @param username
	 * @param popYear
	 * @return HashMap<String, Long>
	 */
	public static HashMap<String, Long> getStateInfo(int dbindex, String username, String popYear){			
		Connection connection = makeConnection(dbindex);
		String query="";
		Statement stmt = null;
		HashMap<String, Long> response = new HashMap<String, Long>();
		query = "WITH "
				+ "aids as (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true ORDER BY aid), "
			    + "counties AS (SELECT count(countyid) AS county, stateid FROM census_counties GROUP BY stateid), "
				+ "tracts AS (SELECT COUNT(tractid) AS tract, stateid FROM census_tracts GROUP BY stateid), "
				+ "places AS (SELECT COUNT(placeId) AS place, stateid FROM census_places GROUP BY stateid), "
				+ "urbanized_areas AS (SELECT COUNT(urbanId) AS urbanized_area, stateid "
				+ "		FROM census_urbans AS urban INNER JOIN census_states AS state "
				+ "		ON ST_INTERSECTS(urban.shape,state.shape) "
				+ "		WHERE urban.population" + popYear + " >= 50000 GROUP BY stateid),"
				+ "urban_clusters AS (SELECT COUNT(urbanId) AS urban_cluster, stateid "
				+ "		FROM census_urbans AS urban INNER JOIN census_states AS state "
				+ "		ON ST_INTERSECTS(urban.shape,state.shape) "
				+ "		WHERE urban.population" + popYear + " >= 2500 AND urban.population" + popYear + " < 50000 GROUP BY stateid),"
				+ "congdists AS (SELECT COUNT(congdistId) AS congdist, stateid FROM census_congdists  GROUP BY stateid),"
				+ "regions AS (SELECT COUNT(distinct odotregionid) as region , stateid FROM census_counties GROUP BY stateid),"
				+ "urban_pop AS (SELECT SUM(population" + popYear + ") AS urbanpop, stateid FROM census_blocks WHERE poptype = 'U' GROUP BY stateid),"
				+ "rural_pop AS (SELECT SUM(population" + popYear + ") AS ruralpop, stateid FROM census_blocks WHERE poptype = 'R' GROUP BY stateid),"
				+ "emp_rac AS (SELECT sum(C000_" + popYear + ")::bigint AS rac, LEFT(blockid,2) AS stateid FROM lodes_rac_projection_block GROUP BY LEFT(blockid,2)),"
				+ "emp_wac AS (SELECT sum(C000)AS wac, LEFT(blockid,2) AS stateid FROM lodes_blocks_wac GROUP BY LEFT(blockid,2)),"
				+ "agencies AS (SELECT COUNT(DISTINCT map.agencyid) AS agency,stateid"
				+ "		FROM gtfs_stops AS stops INNER JOIN gtfs_stop_service_map AS map "
				+ "		ON stops.id = map.stopid AND stops.agencyid = map.agencyid_def "
				+ "     INNER JOIN aids ON map.agencyid_def = aids.aid "				
				+ "		GROUP BY stops.stateid),"
				+ "stops AS (SELECT COUNT(id) AS stop, stateid FROM gtfs_stops INNER JOIN gtfs_stop_service_map AS map ON gtfs_stops.id = map.stopid AND gtfs_stops.agencyid = map.agencyid_def INNER JOIN aids ON map.agencyid_def = aids.aid GROUP BY stateid),"
				+ "trip_states_map AS (SELECT times.trip_id, stops.stateid from  gtfs_stop_times AS times inner join gtfs_stops as stops ON times.stop_id = stops.id AND times.stop_agencyid = stops.agencyid GROUP BY stateid,trip_id),"
				+ "routes AS (SELECT COUNT(DISTINCT(route_id, route_agencyid)) AS route, stateid FROM gtfs_trips INNER JOIN trip_states_map ON gtfs_trips.id = trip_states_map.trip_id INNER JOIN aids ON gtfs_trips.serviceid_agencyid = aids.aid GROUP BY stateid)"
				+ "SELECT stateid, sname, county, tract, place, urbanized_area, urban_cluster, congdist, region, landarea, stop, "
				+ "		population" + popYear + " AS pop, urbanpop, ruralpop,rac, wac, agency, route FROM census_states "
				+ "		INNER JOIN counties USING (stateid)"
				+ "		INNER JOIN tracts USING (stateid)"
				+ "		INNER JOIN places USING (stateid)"
				+ "		INNER JOIN urbanized_areas USING (stateid)"
				+ "		INNER JOIN urban_clusters USING (stateid) "
				+ "		INNER JOIN congdists USING (stateid) "
				+ "		INNER JOIN regions USING (stateid) "
				+ "		INNER JOIN urban_pop USING (stateid) "
				+ "		INNER JOIN rural_pop USING (stateid) "
				+ "		INNER JOIN emp_rac USING (stateid) "
				+ "		INNER JOIN emp_wac USING (stateid) "
				+ "		INNER JOIN stops USING (stateid) "
				+ "		INNER JOIN agencies USING(stateid) "
				+ "		INNER JOIN routes USING(stateid)";		
			try {
		        stmt = connection.createStatement();
		        ResultSet rs = stmt.executeQuery(query); 
		        while ( rs.next() ) {
			    response = new HashMap<String, Long>();
				response.put("county",  rs.getLong("county"));
				response.put("tract",  rs.getLong("tract"));
				response.put("place",  rs.getLong("place"));
				response.put("urbanized_area", rs.getLong("urbanized_area"));
				response.put("urban_cluster", rs.getLong("urban_cluster"));
				response.put("congdist", rs.getLong("congdist"));
				response.put("region", rs.getLong("region"));
				response.put("pop", rs.getLong("pop"));
				response.put("landarea", rs.getLong("landarea"));
				response.put("urbanpop", rs.getLong("urbanpop"));
				response.put("ruralpop", rs.getLong("ruralpop"));
				response.put("rac", rs.getLong("rac"));
				response.put("wac", rs.getLong("wac"));
				response.put("agency", rs.getLong("agency"));
				response.put("route", rs.getLong("route"));
				response.put("stop", rs.getLong("stop"));
	        }	  
		        rs.close();
		        stmt.close();  
		    	}
		 catch ( Exception e ) {
			 e.printStackTrace();
			 dropConnection(connection);
			 }
		return response;	
	}

	/**
	 * return information on a geographical area 
	 * @param type - type of the geographical area
	 * @param id - ID of the geographical area
	 * @param dbindex
	 * @param username
	 * @param popYear - population projection year
	 * @param geotype - type of the geographical area to be intersected with urban areas for filtering service
	 * @param geoid - ID of the geographical area to be intersected with urban areas for filtering service 
	 * @return GeoArea
	 */
	public static GeoArea QueryGeoAreabyId(int type, String id, int dbindex, String username, String popYear,int geotype,String geoid)
		{Connection connection = makeConnection(dbindex);
		String query="";
		String areaID="";
		String areaName="";
		String populationyear="";
		String id1="'"+id+"'";
		
		Statement stmt = null;
		GeoArea response = new GeoArea();
	
		 if (type==0){//county: tracts are queries and summed up to reflect the true number of census tracts in the results
		    	query="with temp as (Select * from census_counties where countyId="+id1+"),employment as (select e"+popYear+" as employment from temp left join lodes_rac_projection_county using(countyId) )," 
		    			+"employees as (select sum(c000) as employees from temp left join lodes_blocks_wac on temp.countyid=left(lodes_blocks_wac.blockid,5)  group by countyid,left(lodes_blocks_wac.blockid,5) )"
		    			+"select * from temp cross join employment cross join employees ";
		      areaID="countyId";
			  areaName="cname";
			  } else if (type==1){//census tract
		    	 query="with temp as (Select * from census_tracts where tractid="+id1+"),"
		    			+" employment as (select sum(C000_"+popYear+") as employment from temp left join lodes_rac_projection_block on temp.tractid=left(lodes_rac_projection_block.blockid,11)  group by tractid,left(lodes_rac_projection_block.blockid,11)  ),"
		    			 +"employees as (select sum(c000) as employees from temp left join lodes_blocks_wac on temp.tractid=left(lodes_blocks_wac.blockid,11)  group by tractid,left(lodes_blocks_wac.blockid,11) )"
		    			 +"select * from temp cross join employment cross join employees ";
				 areaID="tractid";
				 areaName="tlongname";
		      } else if (type==3){//census urban
		    	  if(geotype==-1||geotype==3)
		    	  {
		    	  query="with temp as (Select * from census_urbans where urbanid="+id1+"),"
		    			  +"temp1 as (select blockid ,urbanid  from temp left join census_blocks  using(urbanid)),"
		    			  +" employment as (select sum(C000_"+popYear+") as employment,urbanid from temp1 left join lodes_rac_projection_block  using(blockid) group by  urbanid),"
		     			 +"employees as (select sum(c000) as employees,urbanid from temp1 left join lodes_blocks_wac using(blockid) group by urbanid )"
		     			 +"select * from temp cross join employment cross join employees ";
		      areaID="urbanid";
			  areaName="uname";
	    	  }
	    	  else if(geotype==0)
	    	  {
	    		  query="with temp as (Select * from census_urbans where urbanid="+id1+"),"
	        			  +"temp1 as (select blockid ,temp.urbanid  from temp left join census_blocks on temp.urbanid="+id1+" And left(blockid,5)='"+geoid+"'),"
	        			 +"temp2 as (select blockid,urbanid from  census_blocks where urbanid="+id1+" And left(blockid,5)='"+geoid+"' ), "
	        			  +" employment as (select sum(C000_"+popYear+") as employment,urbanid from temp2 left join lodes_rac_projection_block  using(blockid) group by  urbanid),"
	         			 +"employees as (select sum(c000) as employees,urbanid from temp2 left join lodes_blocks_wac using(blockid) group by urbanid )"
	         			 +"select * from temp cross join employment cross join employees ";
	          areaID="urbanid";
	    	  areaName="uname";
	    	  }
	    	  else if(geotype==1)
	    	  {
	    		  query="with temp as (Select * from census_urbans where urbanid="+id1+"),"
	        			  +"temp1 as (select blockid ,temp.urbanid  from temp left join census_blocks on temp.urbanid="+id1+" And left(blockid,11)='"+geoid+"'),"
	        			 +"temp2 as (select blockid,urbanid from  census_blocks where urbanid="+id1+" And left(blockid,11)='"+geoid+"' ), "
	        			  +" employment as (select sum(C000_"+popYear+") as employment,urbanid from temp2 left join lodes_rac_projection_block  using(blockid) group by  urbanid),"
	         			 +"employees as (select sum(c000) as employees,urbanid from temp2 left join lodes_blocks_wac using(blockid) group by urbanid )"
	         			 +"select * from temp cross join employment cross join employees ";
	          areaID="urbanid";
	    	  areaName="uname";
	    	  }
	    	  else if(geotype==2)
	    	  {  query="with temp as (Select * from census_urbans where urbanid="+id1+"),"
	    			  +"temp1 as (select blockid ,temp.urbanid  from temp left join census_blocks on temp.urbanid="+id1+" And placeid='"+geoid+"'),"
	    			 +"temp2 as (select blockid,urbanid from  census_blocks where urbanid="+id1+" And placeid='"+geoid+"' ), "
	    			  +" employment as (select sum(C000_"+popYear+") as employment,urbanid from temp2 left join lodes_rac_projection_block  using(blockid) group by  urbanid),"
	     			 +"employees as (select sum(c000) as employees,urbanid from temp2 left join lodes_blocks_wac using(blockid) group by urbanid )"
	     			 +"select * from temp cross join employment cross join employees ";
	      areaID="urbanid";
		  areaName="uname";
	    		  
	    	  }
	    	  
	    	  else if(geotype==4)
	    	  {
	    		  query="with temp as (Select * from census_urbans where urbanid="+id1+"),"
	        			  +"temp1 as (select blockid ,temp.urbanid  from temp left join census_blocks on temp.urbanid="+id1+" And regionid='"+geoid+"'),"
	        			 +"temp2 as (select blockid,urbanid from  census_blocks where urbanid="+id1+" And regionid='"+geoid+"' ), "
	        			  +" employment as (select sum(C000_"+popYear+") as employment,urbanid from temp2 left join lodes_rac_projection_block  using(blockid) group by  urbanid),"
	         			 +"employees as (select sum(c000) as employees,urbanid from temp2 left join lodes_blocks_wac using(blockid) group by urbanid )"
	         			 +"select * from temp cross join employment cross join employees ";
	          areaID="urbanid";
	    	  areaName="uname";
	    	  }
	    	  else if(geotype==5)
	    	  {
	    		  query="with temp as (Select * from census_urbans where urbanid="+id1+"),"
	        			  +"temp1 as (select blockid ,temp.urbanid  from temp left join census_blocks on temp.urbanid="+id1+" And congdistid='"+geoid+"'),"
	        			 +"temp2 as (select blockid,urbanid from  census_blocks where urbanid="+id1+" And congdistid='"+geoid+"' ), "
	        			  +" employment as (select sum(C000_"+popYear+") as employment,urbanid from temp2 left join lodes_rac_projection_block  using(blockid) group by  urbanid),"
	         			 +"employees as (select sum(c000) as employees,urbanid from temp2 left join lodes_blocks_wac using(blockid) group by urbanid )"
	         			 +"select * from temp cross join employment cross join employees ";
	          areaID="urbanid";
	    	  areaName="uname";
	    	  }
	       
	      } else if (type==4) { //ODOT Regions
	    	  query="with temp as (Select sum(waterarea) as waterarea,sum(landarea) as landarea,odotregionname,odotregionid,sum(population"+popYear+") as population"+popYear+" from census_counties where odotregionid="+id1+" group by odotregionid,odotregionname),"
	
			   	 +"temp1 as (select blockid ,odotregionid  from temp left join census_blocks  on census_blocks.regionid=temp.odotregionid),"
			   	 +" employment as (select sum(C000_"+popYear+") as employment,odotregionid from temp1 left join lodes_rac_projection_block  using(blockid) group by  odotregionid),"
			     +"employees as (select sum(c000) as employees,odotregionid from temp1 left join lodes_blocks_wac using(blockid) group by odotregionid )"
			   	 +"select * from temp cross join employment cross join employees ";
			     areaID="odotregionid";
			 areaName="odotregionname";
		  } else if (type == 6){// states
			      query = "with temp as (Select stateid, sname, landarea from census_states where stateid=" + id1 + "),"
			      		+ "employment as (select sum(e"+popYear+") as employment "
	      				+ "		from temp left join lodes_rac_projection_county on left(countyId,2)=" + id1 + "),"
			      		+ "employees as (select sum(c000) as employees "
			      		+ "		from temp left join lodes_blocks_wac on temp.stateid=left(lodes_blocks_wac.blockid,2) ), "
			      		+ "population as (select sum(population" + popYear + ") as population" + popYear + " from census_counties where left(countyid,2)=" + id1 + ") "
			      		+ "select * from temp cross join population cross join employment cross join employees";
			      areaID = "stateid";
			      areaName = "sname";		    		  
		  }else {// census place or congressional district
	    	  if (type == 2) {
			  query="with temp as (Select * from census_places where placeid="+id1+"),"
			  +"temp1 as (select blockid ,placeid  from temp left join census_blocks  using(placeid)),"
			  +" employment as (select sum(C000_"+popYear+") as employment,placeid from temp1 left join lodes_rac_projection_block  using(blockid) group by  placeid),"
				 +"employees as (select sum(c000) as employees,placeid from temp1 left join lodes_blocks_wac using(blockid) group by placeid )"
				 +"select * from temp cross join employment cross join employees ";
	    	   areaID="placeid";
			   areaName="pname";
			  }
			  
	    	  if (type == 5){ 
			  query="with temp as (Select * from census_congdists where congdistid="+id1+"),"
					  +"temp1 as (select blockid ,congdistid  from temp left join census_blocks  using(congdistid)),"
					  +" employment as (select sum(C000_"+popYear+") as employment,congdistid from temp1 left join lodes_rac_projection_block  using(blockid) group by  congdistid),"
						 +"employees as (select sum(c000) as employees,congdistid from temp1 left join lodes_blocks_wac using(blockid) group by congdistid )"
						 +"select * from temp cross join employment cross join employees ";
	    	  areaID="congdistid";
			   areaName="cname";
			 }
	    	  }
	populationyear="population"+popYear;
	try {
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query); 
       
        while ( rs.next() ) {
        	response = new GeoArea();
        	response.setId( rs.getString(areaID));
        	response.setName ( rs.getString(areaName));        	
        	response.setPopulation (rs.getLong(populationyear));
        	response.setLandarea(rs.getLong("landarea"));
        	response.setEmployee(rs.getLong("employees"));
        	response.setEmployment(rs.getLong("employment"));        	
        }
        rs.close();
        stmt.close();        
      } catch ( Exception e ) {
    	  e.printStackTrace();         
      }
      dropConnection(connection);      
      return response;
    }	

	/**
	 * return the number of stops belonging to selectedAgencies and located a give urban area 
	 * @param urbanId - urban area ID
	 * @param selectedAgencies - array of selected agency IDs
	 * @param dbindex
	 * @param username
	 * @return number of stops (long)
	 * @throws FactoryException
	 * @throws TransformException
	 */
	public static long getstopscountbyurban(String urbanId, List<String> selectedAgencies, int dbindex,String username) throws FactoryException, TransformException {			
	Connection connection = makeConnection(dbindex);
		String query="";
		String id1="'"+urbanId+"'";
		Statement stmt = null;
		long response=0;
		query ="select count(Id) as stopscount from gtfs_stops where urbanid="+id1+" and agencyId in (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true)";
		try {
	        stmt = connection.createStatement();
	        ResultSet rs = stmt.executeQuery(query); 
	        while ( rs.next() ) {
			response=0;
	        rs.getInt("stopscount");
			}
			 rs.close();
	    stmt.close();  
		}
		 catch ( Exception e ) {
			 e.printStackTrace();
			 dropConnection(connection);
	    }
		return response;	
	}

	/**
	 * returns the number of stops in a give urban area
	 * 
	 * @param urbanId - urban area ID
	 * @param dbindex
	 * @return number of stops (long)
	 * @throws FactoryException
	 * @throws TransformException
	 */
	public static long getstopscountbyurban(String urbanId, int dbindex) throws FactoryException, TransformException {		
		{			
		Connection connection = makeConnection(dbindex);
			String query="";
			String id1="'"+urbanId+"'";
			Statement stmt = null;
			long response=0;
			query ="select count(Id) as stopscount from gtfs_stops where urbanid="+id1+" ";
			try {
		        stmt = connection.createStatement();
		        ResultSet rs = stmt.executeQuery(query); 
		        while ( rs.next() ) {
				response=0;
		        rs.getInt("stopscount");
				}
				 rs.close();
		    stmt.close();  
			}
			 catch ( Exception e ) {
				 e.printStackTrace();
				 dropConnection(connection);
				 }
			return response;	
		}}

	/**
	 * returns list of routes in an urban area
	 * 
	 * @param urbanId - urban area ID
	 * @param dbindex
	 * @return List<GeoStopRouteMap>
	 * @throws FactoryException
	 * @throws TransformException
	 */
	public static List<GeoStopRouteMap> getroutesbyurban(String urbanId, int dbindex) throws FactoryException, TransformException
	{
	Connection  connection = makeConnection(dbindex);
	String query="";
	Statement stmt = null;
	String id1="'"+urbanId+"'";
	GeoStopRouteMap r = new GeoStopRouteMap() ;
	List<GeoStopRouteMap> response = new ArrayList<GeoStopRouteMap>();
	query =	"select routeid,stopid,gsr.agencyid,gtfs_stops.agencyid as defaultid "
			+ "		from gtfs_Stop_Route_Map gsr join gtfs_stops on agencyid_def=gtfs_stops.agencyid and stopid=id "
			+ "		where urbanid='="+id1+"'";
	try {		
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query); 
    
        while ( rs.next() ) {
			r=new GeoStopRouteMap() ;
			r.setagencyId(rs.getString("agencyid"));
			r.setrouteId(rs.getString("routeid"));
			r.setstopId(rs.getString("stopid"));
			r.setagencyId_def(rs.getString("defaultid"));
			response.add(r);
		}
		rs.close();
		stmt.close();  
		}catch ( Exception e ) {
			e.printStackTrace();
			dropConnection(connection);
		}
	return response;	
	}
	
	/**
	 * returns list of routes belonging to a set of selected agencies in a
	 * given urban area.
	 * 
	 * @param urbanId - urban area ID
	 * @param selectedAgencies - list of selected agency IDs
	 * @param dbindex
	 * @param username
	 * @return List<GeoStopRouteMap>
	 * @throws FactoryException
	 * @throws TransformException
	 */
	public static List<GeoStopRouteMap> getroutesbyurban(String urbanId, List<String> selectedAgencies, int dbindex,String username) throws FactoryException, TransformException
	{
	Connection  connection = makeConnection(dbindex);
	String query="";
	Statement stmt = null;
	String id1="'"+urbanId+"'";
	GeoStopRouteMap r = new GeoStopRouteMap() ;
	String list= String.join( "','",selectedAgencies);
	list = "'" + list + "'";
	List<GeoStopRouteMap> response = new ArrayList<GeoStopRouteMap>();
	query ="select routeid,stopid,gsr.agencyid,gtfs_stops.agencyid as defaultid from gtfs_Stop_Route_Map gsr join gtfs_stops on agencyid_def=gtfs_stops.agencyid and stopid=id where urbanid="+id1+" and gtfs_stops.agencyid in (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true)";
	try {
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query); 
        while ( rs.next() ) {
		 r=new GeoStopRouteMap() ;
		r.setagencyId(rs.getString("agencyid"));
		r.setrouteId(rs.getString("routeid"));
		r.setstopId(rs.getString("stopid"));
		r.setagencyId_def(rs.getString("defaultid"));
		response.add(r);
		}
		 rs.close();
    stmt.close();  
	}
	 catch ( Exception e ) {
		 e.printStackTrace();
		 dropConnection(connection);
		 }
	dropConnection(connection);
	return response;	
	}

	/**
	 * returns employment and employees information for aggregated
	 * urban areas.
	 * 
	 * @param dbindex
	 * @param username
	 * @param popmax - maximum population of the areas to filter on
	 * @param popmin - minimum population of the areas to filter on
	 * @param popYear - population projection year
	 * @return int[]
	 * @throws FactoryException
	 * @throws TransformException
	 */
	public static int[] aggurbanemp( int dbindex,String username,int popmax,int popmin,String popYear ) 
			throws FactoryException, TransformException	{
		Connection  connection = makeConnection(dbindex);
		String query="";
		Statement stmt = null;
		int[] empArray =  new int[2];
		query ="with temp as (select urbanid,population"+popYear+" as population from census_urbans where population"+popYear+" between "+popmin+" and "+popmax+"),"
				+"block as (select blockid,temp.urbanid from temp join census_blocks using(urbanid)),"
				+"wac as (select sum(C000) as wac from lodes_blocks_wac join block using(blockid)),"
				+"rac as (select sum(C000_"+popYear+" :: int) as rac from lodes_rac_projection_block join block using(blockid))"
				+"select coalesce(rac,0) as rac , coalesce(wac,0) as wac  from wac cross join rac";
	
		try {
	        stmt = connection.createStatement();
	        ResultSet rs = stmt.executeQuery(query); 
	        while ( rs.next() ) {
	        	empArray[0]=rs.getInt("rac");
	        	empArray[1]=rs.getInt("wac");
	        }
			 rs.close();
			 stmt.close(); 
			 dropConnection(connection);
		}
		 catch ( Exception e ) {
			 e.printStackTrace();
		}
		return empArray;	
	}

	/**
	 * returns the total trip count and number of active agencies
	 * for a given date in a given database
	 *  
	 * @param dbindex
	 * @param popYear
	 * @return Map<String, HeatMap>
	 * @throws ParseException
	 */
		public static Map<String, HeatMap> Heatmap (int dbindex, String popYear,String username) throws ParseException 
		{	
		 Connection connection = makeConnection(dbindex);
		 Statement stmt = null;
		 Map<String,HeatMap> r = new LinkedHashMap<String,HeatMap>();
		 int w=0;
		 int day=01;
		 int month1=00;
		 String date="";
		 String datekey="";
		
		 for ( int month=0; month<12; month++){
			 //Create a calendar object and set year and month
			  Calendar mycal = new GregorianCalendar(Integer.valueOf(popYear),month ,1);
			  //Get the number of days in that month
			  int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH); 
			  for(day=1;day<=daysInMonth;day++){
				  month1=month+1;
				  String dateString = String.format("%d-%d-%d",Integer.valueOf(popYear), month1, day);
				  Date date1 = new SimpleDateFormat("yyyy-M-d").parse(dateString);		
				  // Then get the day of week from the Date based on specific locale.
				  String dayOfWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date1);
				  w=w+1;
				  String query = "";
				  if (month1< 10 && day < 10 )
					  {
						date=popYear+"0"+month1+"0"+day;
						datekey="0"+month1+"/"+"0"+day+"/"+popYear;
						}
				  else if (month1 >= 10 && day < 10)
					  {
					  date=popYear+month1+"0"+day;
					  datekey=month1+"/"+"0"+day+"/"+popYear;
					  }
				  else if (day >= 10 && month1 < 10)
					  {
					  date=popYear+"0"+month1+day;
					  datekey="0"+month1+"/"+day+"/"+popYear;
					  }
				  else
					  {
					  date=popYear+month1+day;
					  datekey=month1+"/"+day+"/"+popYear;
					  }
				  query="with aids as (SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true),"
				  		+ "svcids as (select serviceid_agencyid, serviceid_id  from gtfs_calendars gc where startdate::int<="+date+" and enddate::int>="+date+" and "+dayOfWeek+" = 1 and serviceid_agencyid||serviceid_id not in (select serviceid_agencyid||serviceid_id from gtfs_calendar_dates where date='"+date+"' and exceptiontype=2)union select serviceid_agencyid, serviceid_id from gtfs_calendar_dates gcd inner join aids on gcd.serviceid_agencyid = aids.aid where date='"+date+"' and exceptiontype=1),"
						  +"trips as (select trip.agencyid as aid, trip.id as tripid,trip.stopscount as stops from svcids inner join gtfs_trips trip using(serviceid_agencyid, serviceid_id) INNER JOIN aids ON trip.serviceid_agencyid = aids.aid ),"
						  +"activeAgencies as (select count(distinct aid) as active from trips),"
						  +"totalAgencies as (SELECT COUNT(distinct id) as total FROM gtfs_agencies INNER JOIN aids ON gtfs_agencies.defaultid = aids.aid ), " 
						  +"tripcount as (select count (distinct tripid) as tripcount from trips) "
						  +"select tripcount,active,total from activeAgencies Cross join totalAgencies cross join tripcount";
				logger.debug(query);
				  try {
					  stmt = connection.createStatement();
					  ResultSet rs = stmt.executeQuery(query);
					  while ( rs.next() ){
						  HeatMap q= new HeatMap();
						  q.active=rs.getInt("active");
						  q.total=rs.getInt("total");
						  q.tripcount=rs.getInt("tripcount");
						  r.put(datekey,q);
						  }
					  rs.close();
					  stmt.close();
					  } catch ( Exception e ) {
						  e.printStackTrace();
						  }
				  }
			  }
		 dropConnection(connection);
		 return(r);
		 }
	
		 public static Map<String, Daterange> daterange( int dbindex) 
		 throws FactoryException, TransformException	{
			Connection  connection = makeConnection(dbindex);
			String query="";
			Statement stmt = null;
			String defaultDate = DatabaseConfig.getConfig(dbindex).getDefaultDate();
		
			Map<String, Daterange> r = new LinkedHashMap<String, Daterange>();
			query = "select feedname,startdate,enddate,agencynames,agencyids from gtfs_feed_info";
			int start = 0;
			int end = 1000000000;
			int starta = 0;
			int enda = 1000000000;
			try {
				stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);

				while (rs.next()) {
					Daterange a = new Daterange();
					a.agencyids = rs.getString("agencyids");
					a.agencynames = rs.getString("agencynames");

					a.feedname = rs.getString("feedname");
					a.startdate = rs.getInt("startdate");
					a.syear = a.startdate / 10000;
					a.smonth = (a.startdate % 10000) / 100;
					a.sday = a.startdate % 100;
					a.enddate = rs.getInt("enddate");
					a.eyear = a.enddate / 10000;
					a.emonth = (a.enddate % 10000) / 100;
					a.eday = a.enddate % 100;
					r.put(a.feedname, a);
					if (a.startdate > start && a.startdate < end) {
						start = a.startdate;
					}
					if (a.enddate < end && a.enddate > start) {
						end = a.enddate;
					}

				}
				rs.close();
				stmt.close();
				dropConnection(connection);
				int u = 0;

				for (Entry<String, Daterange> entry : r.entrySet()) {
					u = u + 1;
					if (entry.getValue().startdate <= end && entry.getValue().enddate >= start) {
						// start=20260101;
						// end=20160101;
						starta = starta + 1;
					}

				}

				if (u != starta) {
					start = 20250101;
					end = 20250101;
				}
				Daterange a = new Daterange();
				a.feedname = "Overlap";

				a.startdate = start;
				a.syear = start / 10000;
				a.smonth = (start % 10000) / 100;
				a.sday = start % 100;
				a.enddate = end;
				a.eyear = end / 10000;
				a.emonth = (end % 10000) / 100;
				a.eday = end % 100;
				r.put(a.feedname, a);

				if (defaultDate.length() == 8) {
					Daterange d = new Daterange();
					d.feedname = "Default";
					try {
						d.startdate = Integer.parseInt(defaultDate);
						r.put(d.feedname, d);	
					} catch (NumberFormatException e) {
					}					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return r;
		}
		
		public static Map<String,Agencyselect> setHiddenAgencies(int dbindex, String username, String[] agency_ids) throws SQLException, FactoryException, TransformException {
			DatabaseConfig db = DatabaseConfig.getConfig(dbindex);
			Connection connection = db.getConnection();
			String query = ""
			+ " INSERT INTO user_selected_agencies (username, agency_id, hidden) ("
			+ "   SELECT ?::text AS username, a.id, CASE WHEN id = ANY(?) THEN true ELSE false END AS hidden "
			+ "   FROM gtfs_agencies AS a "
			+ "   LEFT OUTER JOIN user_selected_agencies AS b"
			+ "     ON (b.username = ? AND a.defaultid = b.agency_id)"
			+ " ) ON CONFLICT (username, agency_id) DO UPDATE SET hidden = EXCLUDED.hidden"
			+ "";
			try {
				PreparedStatement ps = connection.prepareStatement(query);
				ps.setString(1, username);
				ps.setArray(2, connection.createArrayOf("VARCHAR", agency_ids));
				ps.setString(3, username);
				logger.info("setHiddenAgencies query:\n "+ ps.toString());
				ps.executeUpdate();
				ps.close();
			} catch ( Exception e ) {
				logger.error(e);
			}
			connection.close();
			return Agencyget(dbindex, username);
		}

		public static void removeHiddenAgencies(int dbindex, String username) 
		throws SQLException, FactoryException, TransformException {
			DatabaseConfig db = DatabaseConfig.getConfig(dbindex);
			Connection connection = db.getConnection();
			String query = ""
				+ " DELETE FROM user_selected_agencies"
				+ " WHERE username = ?::text";
			try {
				PreparedStatement ps = connection.prepareStatement(query);
				ps.setString(1, username);
				logger.info("removeHiddenAgencies query:\n "+ ps.toString());
				ps.executeUpdate();
				ps.close();
			} catch ( Exception e ) {
				logger.error(e);
			}
			connection.close();
		}

		public static void ensureDBSessionInitialized(int dbindex, String username) {
			Connection  connection = makeConnection(dbindex);
			String query="";
			PreparedStatement stmt = null;

			query = ""
			+ " INSERT INTO user_selected_agencies (username, agency_id, hidden) ("
			+ "   SELECT '" + username + "', gtfs_agencies.defaultid, false"
			+ "   FROM gtfs_agencies "
			+ " ) ON CONFLICT (username, agency_id) DO NOTHING"
			+ "";

			logger.info("ensureDBSessionInitialized query: " + query);

			try {
		        stmt = connection.prepareStatement(query);
		        stmt.executeUpdate(); 
				stmt.close(); 
				dropConnection(connection);
			}
			 catch ( Exception e ) {
				 e.printStackTrace();
			}
		}


		public static Map<String,Agencyselect> Agencyget(int dbindex, String username) 
				throws FactoryException, TransformException	{

			// Kind of a hack, but this is the only place I found that guarantees
			// selected agencies are initialized and that has access to dbindex and username 
			// -- Annie
			ensureDBSessionInitialized(dbindex, username);
			Connection  connection = makeConnection(dbindex);
			String query="";
			Statement stmt = null;
		
			Map<String,Agencyselect> r = new LinkedHashMap<String,Agencyselect>();

			query = "SELECT a.id,a.name,a.defaultid,b.hidden,f.feedname,f.startdate,f.enddate FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) INNER JOIN gtfs_feed_info f ON f.defaultid = a.defaultid ORDER BY name";

			try {
		        stmt = connection.createStatement();
		        ResultSet rs = stmt.executeQuery(query); 
		        while ( rs.next() ) {
		        	Agencyselect a=new Agencyselect();
		        	a.AgencyId=rs.getString("id");
		        	a.Agencyname=rs.getString("name");
					a.DefaultId=rs.getString("defaultid");
					a.Hidden=rs.getBoolean("hidden");
					// This is necessary to distinguish a false 'hidden' from a nonexistent one
					if (rs.wasNull()) {
						a.Hidden = null;
					}
					a.Feedname=rs.getString("feedname");
					a.StartDate=rs.getString("startdate");
					a.EndDate=rs.getString("enddate");
		        	r.put(a.Agencyname, a);     
		        }
				 rs.close();
				 stmt.close(); 
				 dropConnection(connection);
			}
			 catch ( Exception e ) {
				 e.printStackTrace();
			}
			return r;	
		}

		// moved from DbUpdate.getSelectedAgencies - did not support dbindex
		public static List<String> getSelectedAgencies(int dbindex, String username) {
			List<String> selectedAgencies = new ArrayList<String>();
			DatabaseConfig db = DatabaseConfig.getConfig(dbindex);
			Connection c = db.getConnection();
			Statement statement = null;
			ResultSet rs = null;
			try {
			  statement = c.createStatement();
			  rs = statement
				  .executeQuery("SELECT DISTINCT a.defaultid AS aid FROM gtfs_agencies AS a LEFT OUTER JOIN user_selected_agencies AS b ON (b.username = '"+username+"' AND a.defaultid = b.agency_id) WHERE b.hidden IS NOT true");
			  while (rs.next()) {
				selectedAgencies.add(rs.getString("aid"));
			  }
			} catch (SQLException e) {
			  logger.error(e);
			} finally {
			  if (rs != null)
				try {
				  rs.close();
				} catch (SQLException e) {
				}
			  if (statement != null)
				try {
				  statement.close();
				} catch (SQLException e) {
				}
			  if (c != null)
				try {
				  c.close();
				} catch (SQLException e) {
				}
			}
			if (selectedAgencies.isEmpty()) {
			  selectedAgencies.add("null");
			}
			return selectedAgencies;
		}
}