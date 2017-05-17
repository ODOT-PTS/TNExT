// Copyright (C) 2015 Oregon State University - School of Machanical,Industrial and Manufacturing Engineering 
//   This file is part of Transit Network Analysis Software Tool.
//
//    Transit Network Analysis Software Tool is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Transit Network Analysis Software Tool is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU  General Public License
//    along with Transit Network Analysis Software Tool.  If not, see <http://www.gnu.org/licenses/>.

package com.webapp.api;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.swing.Timer;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.codehaus.jettison.json.JSONException;
import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.FareRule;
import org.onebusaway.gtfs.model.FeedInfo;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.ServiceCalendar;
import org.onebusaway.gtfs.model.ServiceCalendarDate;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import com.model.database.Databases;
import com.model.database.onebusaway.gtfs.hibernate.ext.GtfsHibernateReaderExampleMain;
import com.model.database.queries.EventManager;
import com.model.database.queries.FlexibleReportEventManager;
import com.model.database.queries.PgisEventManager;
import com.model.database.queries.SpatialEventManager;
import com.model.database.queries.congraph.ConGraphAgency;
import com.model.database.queries.congraph.ConGraphAgencyGraph;
import com.model.database.queries.congraph.ConGraphAgencyGraphList;
import com.model.database.queries.congraph.ConGraphObj;
import com.model.database.queries.congraph.ConGraphObjSet;
import com.model.database.queries.objects.AgencyRouteList;
import com.model.database.queries.objects.AgencySR;
import com.model.database.queries.objects.AgencySRList;
import com.model.database.queries.objects.AgencyXR;
import com.model.database.queries.objects.CAStopsList;
import com.model.database.queries.objects.Census;
import com.model.database.queries.objects.CensusList;
import com.model.database.queries.objects.Centroid;
import com.model.database.queries.objects.ClusterR;
import com.model.database.queries.objects.ClusterRList;
import com.model.database.queries.objects.CongDist;
import com.model.database.queries.objects.County;
import com.model.database.queries.objects.DBList;
import com.model.database.queries.objects.EmpDataList;
import com.model.database.queries.objects.GeoArea;
import com.model.database.queries.objects.GeoR;
import com.model.database.queries.objects.GeoRList;
import com.model.database.queries.objects.GeoStop;
import com.model.database.queries.objects.GeoStopRouteMap;
import com.model.database.queries.objects.GeoXR;
import com.model.database.queries.objects.HubCluster;
import com.model.database.queries.objects.HubsClusterList;
import com.model.database.queries.objects.KeyClusterHashMap;
import com.model.database.queries.objects.MapDisplay;
import com.model.database.queries.objects.MapGeo;
import com.model.database.queries.objects.MapPnR;
import com.model.database.queries.objects.MapPnrCounty;
import com.model.database.queries.objects.MapPnrRecord;
import com.model.database.queries.objects.MapRoute;
import com.model.database.queries.objects.MapStop;
import com.model.database.queries.objects.MapTransit;
import com.model.database.queries.objects.NetworkCluster;
import com.model.database.queries.objects.ParknRide;
import com.model.database.queries.objects.ParknRideCountiesList;
import com.model.database.queries.objects.Place;
import com.model.database.queries.objects.PnrInCountyList;
import com.model.database.queries.objects.ProgVal;
import com.model.database.queries.objects.RouteListR;
import com.model.database.queries.objects.RouteR;
import com.model.database.queries.objects.Rshape;
import com.model.database.queries.objects.Schedule;
import com.model.database.queries.objects.ScheduleList;
import com.model.database.queries.objects.StartEndDates;
import com.model.database.queries.objects.StartEndDatesList;
import com.model.database.queries.objects.StopList;
import com.model.database.queries.objects.StopListR;
import com.model.database.queries.objects.StopR;
import com.model.database.queries.objects.Stoptime;
import com.model.database.queries.objects.TitleVIDataList;
import com.model.database.queries.objects.Tract;
import com.model.database.queries.objects.TransitError;
import com.model.database.queries.objects.TripSchedule;
import com.model.database.queries.objects.Urban;
import com.model.database.queries.objects.agencyCluster;
import com.model.database.queries.timingcon.ConTrip;
import com.model.database.queries.timingcon.TripTime;
import com.model.database.queries.util.StringUtils;
import com.model.database.queries.util.Types;
import com.webapp.modifiers.DbUpdate;

@Path("/transit")
@XmlRootElement
public class Queries {
	
	private static final double STOP_SEARCH_RADIUS = 0.1;
	private static final int LEVEL_OF_SERVICE = 2;
	private static int default_dbindex = Databases.dbsize-1;
	static AgencyRouteList[] menuResponse = new AgencyRouteList[Databases.dbsize];
	static int dbsize = Databases.dbsize;	
	
	/**
	 * updates attributes to point to the latest database index 
	 */
	public static void updateDefaultDBindex() {
		default_dbindex = Databases.dbsize - 1;
		dbsize = Databases.dbsize;
		menuResponse = new AgencyRouteList[Databases.dbsize];
	}
	
	/**
	 * returns the names and IDs of databases.
	 * @return DBList
	 * @throws JSONException
	 */
	@GET
	@Path("/DBList")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getDBList() throws JSONException {
		String[] DBNames = Databases.dbnames;
		String[] DBIds = Databases.connectionURLs.clone();
		for (int i = 0 ; i < DBIds.length ; i++){
			String[] temp = DBIds[i].split("/");
			DBIds[i] = temp[temp.length-1];;
		}	
		DBList response = new DBList();
		for (int s = 0; s < dbsize; s++) {
			response.DBelement.add(DBNames[s]);
			response.DBid.add(DBIds[s]);
		}
		return response;
	}
	
	  /**
     * Lists stops within a certain distance of a 
     * given stop while filtering the agencies.
     * Used in Connected agencies on-map report.
     * 
     */
    @GET
    @Path("/castops")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
    public Object getCAS(@QueryParam("lat") double lat, @QueryParam("lon") double lon, @QueryParam ("agencies") String agencies,
 		   @QueryParam("radius") double gap, @QueryParam("dbindex") Integer dbindex) throws JSONException {
    	if (dbindex==null || dbindex<0 || dbindex>dbsize-1){
        	dbindex = default_dbindex;
        }
    	double temp = gap * 1609.34;
    	gap = (int) temp;
    	CAStopsList results = PgisEventManager.getConnectedStops(lat, lon, gap, agencies, dbindex);
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
 	   return results;	   
    }
    
    /**
	 * Generates the shapefile of routes/stops, compresses the 
	 * files into a zipfile on server and return the zipfile's path.
	 *  
	 * @param agencyIDs
	 * @param flag
	 * @param dbName
	 * @param username
	 * @return String - zipfile path
	 * @throws SQLException
	 * @throws IOException
	 * @throws ZipException
	 * @throws InterruptedException
	 */
    @GET
	@Path("/getshapefile")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getShapeFile(@QueryParam("agencyids") String agencyIDs,
			@QueryParam("flag") String flag,
			@QueryParam("dbName") String dbName,
			@QueryParam("dbIndex") Integer dbIndex,
			@QueryParam("username") String username) throws SQLException,
			IOException, ZipException, InterruptedException {

		String[] agencies = agencyIDs.split(",");
		String feeds = "";

		// get database parameters
		String path = MainMap.class.getProtectionDomain().getCodeSource()
				.getLocation().getPath();

		BufferedReader reader = new BufferedReader(
				new FileReader(
						path
								+ "../../src/main/resources/admin/resources/databaseParams.csv"));
		reader.readLine();
		String[] params = reader.readLine().trim().split(",");
		reader.close();
		
		//shapefile generator path
		String generatorPath = path+"../../src/main/resources/com/model/shapefile/shapefilegenerator.bat";
		generatorPath = generatorPath.substring(1, generatorPath.length());
		// Creating shapefiles on the server
		path = path + "../../src/main/webapp/downloadables/shapefiles";

		// Making downloadables/shapefiles directory
		File shapefiles = new File(path);
		shapefiles.mkdirs();

		// Making unique file names
		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("hhmmss");
		String uniqueString = ft.format(dNow);

		// The folder that contains agencies shapefile folders.
		String folderName = flag + "_shape_" + uniqueString;
		File mainFolder = new File(path + "/" + folderName);
		mainFolder.mkdir();

		// Getting hashmap of agencies (AgencyId -> AgencyName)
		HashMap<String, ConGraphAgency> agenciesHashMap = SpatialEventManager
				.getAllAgencies(username, dbIndex);

		for (int i = 0; i < agencies.length; i++) {
			String agencyId = agencies[i];
			ArrayList<String> query = new ArrayList<String>();
			feeds = "(SELECT '"
					+ agencyId
					+ "'::text AS aid, startdate,enddate FROM gtfs_feed_info WHERE agencyids LIKE '%' || '"
					+ agencyId + "' || '%')";

			if (flag.equals("routes")) {
				query.add("SELECT agencies.name AS PRVDR_NM, routes.id AS route_id, routes.shortname AS route_shor, routes.longname AS route_long, "
						+ "	routes.description AS route_desc, routes.url AS route_url, trips.tripshortname, tripheadsign AS trip_headsign,"
						+ " length AS trip_length, estlength AS trip_estLength, feeds.startdate AS efct_dt_start, feeds.enddate AS efct_dt_end, "
						+ " shape AS trip_shape "
						+ "	FROM gtfs_routes AS routes "
						+ "	INNER JOIN gtfs_trips AS trips ON routes.id = trips.route_id "
						+ "	INNER JOIN gtfs_agencies AS agencies ON routes.agencyid = agencies.id "
						+ " INNER JOIN "
						+ feeds
						+ " AS feeds ON feeds.aid = routes.agencyid "
						+ " WHERE trips.agencyid = '" + agencyId + "'");
			} else if (flag.equals("stops")) {
				// check if the number of records are larger than a threshold, split stops_times in to multiple shapefiles
				Connection connection = PgisEventManager.makeConnection(dbIndex);
			    Statement stmt = connection.createStatement();
			    int THRESHOLD = 750000;
			    ResultSet rs = stmt.executeQuery("SELECT count(gid) FROM gtfs_stop_times AS stoptimes WHERE stoptimes.trip_agencyid ='" + agencyId + "'");
			    rs.next();
			    int numOfRows = rs.getInt("count");
				if ( numOfRows < THRESHOLD){
					query.add("SELECT agencies.name AS PRVDR_NM, "
							+ "		stops.id AS stop_id, stops.name AS stop_name, stops.agencyid AS stop_agencyid,"
							+ "		stops.lat AS stop_lat, stops.lon AS stop_lon, stops.url AS stop_url,"
							+ "		feeds.startdate AS efct_dt_start, feeds.enddate AS efct_dt_end,"
							+ "		CASE "
							+ "         WHEN stoptimes.arrivaltime > 86400 "
							+ "				THEN TO_CHAR((stoptimes.arrivaltime-86400||' second')::interval, 'HH24:MI:SS')::time "
							+ "         WHEN stoptimes.arrivaltime = -999 "
							+ "				THEN TO_CHAR(('0 second')::interval, 'HH24:MI:SS')::time"
							+ "		  	ELSE "
							+ "				TO_CHAR((stoptimes.arrivaltime||' second')::interval, 'HH24:MI:SS')::time "
							+ "		END AS arrival_time,"
							+ "		CASE "
							+ "         	WHEN stoptimes.departuretime > 86400 "
							+ "			THEN TO_CHAR((stoptimes.departuretime-86400||' second')::interval, 'HH24:MI:SS')::time "
							+ "             WHEN stoptimes.departuretime = -999 "
							+ "			THEN TO_CHAR(('0 second')::interval, 'HH24:MI:SS')::time "
							+ "		  ELSE TO_CHAR((stoptimes.departuretime||' second')::interval, 'HH24:MI:SS')::time "
							+ "		END AS departure_time,"
							+ "		stoptimes.stopsequence AS stop_sequence,"
							+ "		stops.location AS shape"
							+ "		FROM gtfs_stops AS stops"
							+ "		INNER JOIN gtfs_stop_service_map AS map ON stops.id = map.stopid AND stops.agencyid = map.agencyid_def"
							+ "		INNER JOIN gtfs_agencies AS agencies ON map.agencyid=agencies.id"
							+ "		INNER JOIN  (SELECT '"
							+ agencyId
							+ "'::text AS aid, startdate,enddate FROM gtfs_feed_info WHERE agencyids LIKE '%' || '"
							+ agencyId
							+ "' || '%') AS feeds ON feeds.aid = map.agencyid"
							+ "		INNER JOIN gtfs_stop_times AS stoptimes ON stops.id = stoptimes.stop_id AND stops.agencyid = stoptimes.stop_agencyid"
							+ "		WHERE map.agencyid ='" + agencyId
							+ "' ORDER BY stop_id");
				}else{
					int counter = 0;
					while (counter <= numOfRows){
						query.add("SELECT agencies.name AS PRVDR_NM, "
							+ "		stops.id AS stop_id, stops.name AS stop_name, stops.agencyid AS stop_agencyid,"
							+ "		stops.lat AS stop_lat, stops.lon AS stop_lon, stops.url AS stop_url,"
							+ "		feeds.startdate AS efct_dt_start, feeds.enddate AS efct_dt_end,"
							+ "		CASE "
							+ "         WHEN stoptimes.arrivaltime > 86400 "
							+ "				THEN TO_CHAR((stoptimes.arrivaltime-86400||' second')::interval, 'HH24:MI:SS')::time "
							+ "         WHEN stoptimes.arrivaltime = -999 "
							+ "				THEN TO_CHAR(('0 second')::interval, 'HH24:MI:SS')::time"
							+ "		  	ELSE "
							+ "				TO_CHAR((stoptimes.arrivaltime||' second')::interval, 'HH24:MI:SS')::time "
							+ "		END AS arrival_time,"
							+ "		CASE "
							+ "         	WHEN stoptimes.departuretime > 86400 "
							+ "			THEN TO_CHAR((stoptimes.departuretime-86400||' second')::interval, 'HH24:MI:SS')::time "
							+ "             WHEN stoptimes.departuretime = -999 "
							+ "			THEN TO_CHAR(('0 second')::interval, 'HH24:MI:SS')::time "
							+ "		  ELSE TO_CHAR((stoptimes.departuretime||' second')::interval, 'HH24:MI:SS')::time "
							+ "		END AS departure_time,"
							+ "		stoptimes.stopsequence AS stop_sequence,"
							+ "		stops.location AS shape"
							+ "		FROM gtfs_stops AS stops"
							+ "		INNER JOIN gtfs_stop_service_map AS map ON stops.id = map.stopid AND stops.agencyid = map.agencyid_def"
							+ "		INNER JOIN gtfs_agencies AS agencies ON map.agencyid=agencies.id"
							+ "		INNER JOIN  (SELECT '"
							+ agencyId
							+ "'::text AS aid, startdate,enddate FROM gtfs_feed_info WHERE agencyids LIKE '%' || '"
							+ agencyId
							+ "' || '%') AS feeds ON feeds.aid = map.agencyid"
							+ "		INNER JOIN gtfs_stop_times AS stoptimes ON stops.id = stoptimes.stop_id AND stops.agencyid = stoptimes.stop_agencyid"
							+ "		WHERE map.agencyid ='" + agencyId
							+ "' ORDER BY stop_id LIMIT " + THRESHOLD + " OFFSET " + counter);
						counter += THRESHOLD;
					}
				}	
				connection.close();
				rs.close();
			}
			// Folder that contains agency's shapefiles
			String tempAgencyname = agenciesHashMap.get(agencyId).name
					.replaceAll("[^a-zA-Z0-9\\-]", "");			
			File agencyFolder = new File(path + "/" + flag + "_shape_"
					+ uniqueString + "/" + tempAgencyname + "_" + flag);
			agencyFolder.mkdirs();

			// Run the command to generate shapefiles for each agency
			for ( int j = 0 ; j < query.size() ; j++ ){
				ProcessBuilder pb = new ProcessBuilder("cmd", "/c", generatorPath,
						agencyFolder.getAbsolutePath() + "\\" + tempAgencyname
								+ "_" + flag + "_shape_" + j, "localhost" /*params[0]*/, params[2],
						params[3], dbName, "\"" + query.get(j) + "\"", "pgsql2shp");
				pb.redirectErrorStream(true);
				Process pr = pb.start();
				BufferedReader reader2 = new BufferedReader(new InputStreamReader(
						pr.getInputStream()));
				while (reader2.readLine() != null) {
				}
				pr.waitFor(5, TimeUnit.MINUTES);
			}			
		}
		
		// creating a zip-file to archive the shape files
		ZipParameters parameters = new ZipParameters();
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
		parameters.setIncludeRootFolder(false);

		ZipFile zipF = new ZipFile(path + "/" + flag + "_shape_" + uniqueString
				+ ".zip");
		zipF.createZipFileFromFolder(mainFolder, parameters, false, 0);
		
		FileUtils.deleteDirectory(mainFolder);

		// delete the zip-file after 5 minutes.
		Timer timer;
		ActionListener a = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				zipF.getFile().delete();
			}
		};
		timer = new Timer(1, a);
		timer.setRepeats(false);
		timer.setInitialDelay(5 * 60000);
		timer.start();
		return "downloadables/shapefiles/" + zipF.getFile().getName();
	}

	/**
	 * retrieves park and ride summary report data 
	 */ 
	@GET
	@Path("/CountiesPnR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object countiesPnR(@QueryParam("key") double key,
			@QueryParam("dbindex") Integer dbindex) throws JSONException {
		if (dbindex == null || dbindex < 0 || dbindex > dbsize - 1) {
			dbindex = default_dbindex;
		}
		ParknRideCountiesList response = new ParknRideCountiesList();
		response = PgisEventManager.getCountiesPnrs(dbindex);
		setprogVal(key, 100);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		progVal.remove(key);
		return response;
	}

	/** 
	 * Generates park and ride extended report 
	 * @param key - key to update the progress
	 * @param countyId
	 * @param radius
	 * @param dbindex
	 * @param username
	 * @return PnrInCountyList
	 * @throws JSONException
	 */
	@GET
	@Path("/pnrsInCounty")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object pnrsInCounty(@QueryParam("key") double key,
			@QueryParam("countyId") String countyId,
			@QueryParam("radius") double radius,
			@QueryParam("dbindex") Integer dbindex,
			@QueryParam("username") String username) throws JSONException {
		if (dbindex == null || dbindex < 0 || dbindex > dbsize - 1) {
			dbindex = default_dbindex;
		}
		PnrInCountyList response = new PnrInCountyList();
		int tmpRadius = (int) (radius * 1609.34);
		response = PgisEventManager.getPnrsInCounty(Integer.parseInt(countyId),
				tmpRadius, dbindex, username);
		setprogVal(key, 100);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		progVal.remove(key);
		return response;
	}

	/**
	 * returns a list of census block within x radius of the
	 * coordinates (lat,lon)
	 * @param lat - latitude
	 * @param x - search radius
	 * @param lon - longitude
	 * @param dbindex - database index
	 * @return CensusList 
	 * @throws JSONException
	 */
	@GET
	@Path("/NearBlocks")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getNearBlocks(
			@QueryParam("lat") double lat,
			@QueryParam("lon") double lon,
			@QueryParam("x") double x,
			@QueryParam("dbindex") Integer dbindex
			) throws JSONException {

		if (Double.isNaN(x) || x <= 0) {
			x = STOP_SEARCH_RADIUS;
		}
		if (dbindex == null || dbindex < 0 || dbindex > dbsize - 1) {
			dbindex = default_dbindex;
		}
		x = x * 1609.34;
		List<Census> centroids = new ArrayList<Census>();
		try {
			centroids = EventManager.getcentroids(x, lat, lon, dbindex);
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CensusList response = new CensusList();
		// Census C;
		for (Census c : centroids) {
			Centroid cntr = new Centroid();
			cntr.setcentroid(c);
			response.centroids.add(cntr);
		}
		return response;
	}

	/**
	 * returns the population of the blocks within x-miles radius
	 * of the given coordinates (lat, lon)
	 * @param x - search radius
	 * @param lat
	 * @param lon
	 * @param dbindex
	 * @return
	 */
	@GET
	@Path("/poparound")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getpop(@QueryParam("radius") double x,
			@QueryParam("lat") double lat, @QueryParam("lon") double lon,
			@QueryParam("dbindex") Integer dbindex) {
		if (Double.isNaN(x) || x <= 0) {
			x = STOP_SEARCH_RADIUS;
		}
		if (dbindex == null || dbindex < 0 || dbindex > dbsize - 1) {
			dbindex = default_dbindex;
		}
		x = x * 1609.34;
		long response = 0;
		try {
			response = EventManager.getpop(x, lat, lon, dbindex);
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Census> centroids = new ArrayList<Census>();
		try {
			centroids = EventManager.getcentroids(x, lat, lon, dbindex);
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long sum = 0;
		for (Census centroid : centroids) {
			sum += centroid.getPopulation();
		}
		return new TransitError("Sum of Population is: " + response
				+ " Sum of centroids is: " + sum);
	}

	/**
	 * retrieves all the information needed to generate the on map report
	 * @param lats - list of latitudes
	 * @param lons - list of longitudes
	 * @param date
	 * @param x - search radius
	 * @param dbindex  - database index
	 * @param losRadius - search radius for population served at level of service
	 * @param username - session
	 * @return MapDisplay
	 * @throws JSONException
	 * @throws SQLException
	 */
	@GET
	@Path("/onmapreport")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getOnMapReport(
			@QueryParam("lat") String lats,
			@QueryParam("lon") String lons,			
			@QueryParam("day") String date,
			@QueryParam("x") double x, 
			@QueryParam("dbindex") Integer dbindex,
			@QueryParam("losRadius") String losRadius,
			@QueryParam("username") String username
			) throws JSONException,
			SQLException {
		if (Double.isNaN(x) || x <= 0) {
			x = 0;
		}
		if (dbindex == null || dbindex < 0 || dbindex > dbsize - 1) {
			dbindex = default_dbindex;
		}
		double losR = Double.parseDouble(losRadius) * 1609.34;
		String[] latss = lats.split(",");
		double[] lat = new double[latss.length];
		int ind = 0;
		for (String la : latss) {
			lat[ind] = Double.parseDouble(la);
			ind++;
		}
		String[] lonss = lons.split(",");
		double[] lon = new double[lonss.length];
		ind = 0;
		for (String ln : lonss) {
			lon[ind] = Double.parseDouble(ln);
			ind++;
		}
		String[] dates = date.split(",");
		String[][] datedays = daysOfWeekString(dates);
		String[] fulldates = datedays[0];
		String[] days = datedays[1];
		MapDisplay response = new MapDisplay();
		
		// --------------- information to be displayed on the transit tab ----------
		MapTransit stops = PgisEventManager.onMapStops(fulldates, days,
				username, x, lat, lon, losR, dbindex);
		response.MapTR = stops;
		
		// --------------- information to be displayed on the census tab ----------
		MapGeo blocks = PgisEventManager.onMapBlocks(x, lat, lon, dbindex);		
		response.MapG = blocks;
		
		//--------------- information to be displayed on the park and ride tab ------------
		MapPnR pnr = new MapPnR();
		List<ParknRide> PnRs = new ArrayList<ParknRide>();
		PnRs = SpatialEventManager.getPnRs(lat, lon, x, dbindex);

		Map<String, List<MapPnrRecord>> mapPnr = new HashMap<String, List<MapPnrRecord>>();
		List<MapPnrCounty> mapPnrCounties = new ArrayList<MapPnrCounty>();
		MapPnrRecord mapPnrRecord;
		MapPnrCounty mapPnrCounty;
		for (ParknRide p : PnRs) {
			mapPnrRecord = new MapPnrRecord();
			mapPnrRecord.countyId = p.getCountyid();
			mapPnrRecord.countyName = p.getCounty();
			mapPnrRecord.id = p.getPnrid() + "";
			mapPnrRecord.lat = p.getLat() + "";
			mapPnrRecord.lon = p.getLon() + "";
			mapPnrRecord.lotName = p.getLotname();
			mapPnrRecord.spaces = p.getSpaces() + "";
			mapPnrRecord.transitSerives = p.getTransitservice();
			mapPnrRecord.availability = p.getAvailability();

			if (!mapPnr.containsKey(p.getCountyid())) {
				mapPnr.put(p.getCountyid(), new ArrayList<MapPnrRecord>());
				mapPnr.get(p.getCountyid()).add(mapPnrRecord);
				mapPnrCounty = new MapPnrCounty();
				mapPnrCounty.countyId = p.getCountyid();
				mapPnrCounty.countyName = p.getCounty();
				mapPnrCounties.add(mapPnrCounty);
			} else {
				mapPnr.get(p.getCountyid()).add(mapPnrRecord);
			}
		}
		int Spaces;
		int totalSpaces = 0;
		int totalPnrs = 0;
		List<MapPnrRecord> mapPnrRecords;
		for (MapPnrCounty mp : mapPnrCounties) {
			Spaces = 0;
			mapPnrRecords = mapPnr.get(mp.countyId);
			mp.MapPnrRecords = mapPnrRecords;
			mp.totalPnRs = mapPnrRecords.size() + "";
			totalPnrs += mapPnrRecords.size();
			for (MapPnrRecord t : mapPnrRecords) {
				Spaces += Integer.parseInt(t.spaces);
			}
			totalSpaces += Spaces;
			mp.totalSpaces = Spaces + "";
		}

		pnr.totalPnR = totalPnrs;
		pnr.totalSpaces = totalSpaces;
		pnr.MapPnrCounty = mapPnrCounties;

		response.MapPnR = pnr;
		return response;
	}

	/**
	 * Identifies the stops and routes within a given radius of a park&ride lot.
	 * @param pnrId - park and ride ID
	 * @param lat - latitude of the P&R centroid
	 * @param lng - longitude of the P&R centroid
	 * @param radius - search radius
	 * @param dbindex
	 * @param username
	 * @return MapPnrRecord
	 * @throws JSONException
	 * @throws SQLException
	 */
	@GET
	@Path("/pnrstopsroutes")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getPnrStopsRoutes(
			@QueryParam("pnrId") String pnrId,
			@QueryParam("lat") Double lat, 
			@QueryParam("lng") Double lng,
			@QueryParam("radius") Double radius,
			@QueryParam("dbindex") Integer dbindex,
			@QueryParam("username") String username
			) throws JSONException,
			SQLException {

		MapPnrRecord response = new MapPnrRecord();
		response.id = pnrId;
		MapStop mapPnrStop;
		MapRoute mapPnrRoute;
		List<String> agencyList = DbUpdate.getSelectedAgencies(username);
		System.out.println(agencyList);
		List<GeoStop> pnrGeoStops = new ArrayList<GeoStop>();
		List<GeoStopRouteMap> sRoutes = new ArrayList<GeoStopRouteMap>();
		try {
			// getting list of stops around the lot
			pnrGeoStops = EventManager.getstopswithincircle2(radius, lat, lng,
					dbindex, agencyList);
			for (GeoStop s : pnrGeoStops) {
				mapPnrStop = new MapStop();
				mapPnrStop.AgencyId = s.getAgencyId();
				mapPnrStop.Id = s.getStopId();
				mapPnrStop.Lat = s.getLat() + "";
				mapPnrStop.Lng = s.getLon() + "";
				mapPnrStop.Name = s.getName();

				response.MapPnrSL.add(mapPnrStop);

				// getting routes for each stop
				List<GeoStopRouteMap> stmpRoutes = EventManager.getroutebystop(
						s.getStopId(), s.getAgencyId(), dbindex);
				for (GeoStopRouteMap r : stmpRoutes) {
					if (!sRoutes.contains(r)) {
						sRoutes.add(r);
					}
				}
			}
			for (GeoStopRouteMap r : sRoutes) {
				mapPnrRoute = new MapRoute();
				// getting route information
				Route _r = GtfsHibernateReaderExampleMain.QueryRoutebyid(
						new AgencyAndId(r.getagencyId(), r.getrouteId()),
						dbindex);
				mapPnrRoute.AgencyId = _r.getId().getAgencyId();
				mapPnrRoute.Id = _r.getId().getId();
				mapPnrRoute.Name = _r.getLongName();
				// getting trips for route to display the route on map
				List<Trip> ts = SpatialEventManager.QueryTripsbyRoute(_r
						.getId().getAgencyId(), _r.getId().getId(), dbindex);
				mapPnrRoute.Shape = ts.get(0).getEpshape();
				response.MapPnrRL.add(mapPnrRoute);
			}
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

	/**
	 * Generates a list of routes, sorted by agency ID, for the on-map floating menu
	 * @param date
	 * @param dbindex
	 * @param username
	 * @return AgencyRouteList
	 * @throws JSONException
	 */

	@GET
	@Path("/menu")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getmenu(
			@QueryParam("day") String date,
			@QueryParam("dbindex") Integer dbindex,
			@QueryParam("username") String username
			) throws JSONException {
		if (dbindex == null || dbindex < 0 || dbindex > dbsize - 1) {
			dbindex = default_dbindex;
		}
		String[] fulldates = null;
		String[] days = null;
		if (date != null && !date.equals("") && !date.equals("null")) {
			String[] dates = date.split(",");
			String[][] datedays = daysOfWeekString(dates);
			fulldates = datedays[0];
			days = datedays[1];
			AgencyRouteList response = PgisEventManager.agencyMenu(fulldates,
					days, username, dbindex);
			return response;
		} else {
			if (!username.equals("admin")) {
				AgencyRouteList response = PgisEventManager.agencyMenu(null,
						null, username, dbindex);
				return response;
			}
			List<String> selectedAgencies = DbUpdate
					.getSelectedAgencies(username);
			Collection<Agency> allagencies = GtfsHibernateReaderExampleMain
					.QueryAllAgencies(selectedAgencies, dbindex);
			if (menuResponse[dbindex] == null
					|| menuResponse[dbindex].data.size() != allagencies.size()) {
				menuResponse[dbindex] = new AgencyRouteList();
				menuResponse[dbindex] = PgisEventManager.agencyMenu(null, null,
						username, dbindex);
			}
			return menuResponse[dbindex];
		}
	}

	/**
	 * returns a list of stops for a given agency
	 * @param agencyid
	 * @param dbindex
	 * @return StopList
	 * @throws SQLException
	 */
	@GET
	@Path("/stops")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object dbstopsforagency(
			@QueryParam("agency") String agencyid,
			@QueryParam("dbindex") Integer dbindex
			) throws SQLException {
		if (dbindex == null || dbindex < 0 || dbindex > dbsize - 1) {
			dbindex = default_dbindex;
		}
		StopList response = new StopList();
		Connection connection = PgisEventManager.makeConnection(dbindex);
		Statement stmt = connection.createStatement();
		String query;
		query = "SELECT stops.* FROM gtfs_stops AS stops INNER JOIN gtfs_stop_service_map AS map"
				+ " ON stops.id = map.stopid AND stops.agencyid = map.agencyid_def"
				+ " WHERE map.agencyid = '" + agencyid + "'";
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			StopR e = new StopR();
			e.StopId = rs.getString("id");
			e.StopName = rs.getString("name");
			e.lat = rs.getString("lat");
			e.lon = rs.getString("lon");
			response.stops.add(e);
		}
		connection.close();
		rs.close();
		return response;
	}

	/**
	 * Return shape for a given trip and agency
	 * @param agency - agency ID
	 * @param trip - trip ID
	 * @param dbindex
	 * @return Rshape - route shape
	 */
	@GET
	@Path("/shape")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object dbshapesforagency(
			@QueryParam("agency") String agency,
			@QueryParam("trip") String trip,
			@QueryParam("dbindex") Integer dbindex
			) {
		if (dbindex == null || dbindex < 0 || dbindex > dbsize - 1) {
			dbindex = default_dbindex;
		}
		AgencyAndId agencyandtrip = new AgencyAndId();
		agencyandtrip.setAgencyId(agency);
		agencyandtrip.setId(trip);
		Trip tp = GtfsHibernateReaderExampleMain
				.getTrip(agencyandtrip, dbindex);
		Rshape shape = new Rshape();
		shape.points = tp.getEpshape();// to be fixed
		shape.length = tp.getLength(); // to be fixed
		shape.agency = agency;
		if (tp.getTripHeadsign() == null) {
			shape.headSign = "N/A";
		} else {
			shape.headSign = tp.getTripHeadsign();
		}
		shape.estlength = tp.getEstlength();// to be fixed
		Agency agencyObject = GtfsHibernateReaderExampleMain.QueryAgencybyid(
				agency, dbindex);
		shape.agencyName = agencyObject.getName();
		return shape;
	}

	/**
	 * Returns all the trips that have a stops within a given distance from the
	 * stops of a selected trip and run within the given time window. The method
	 * is used to generate Missed Opportunity and Timing Connection Reports.
	 * 
	 * @param radius
	 * @param agencyId
	 * @param date
	 * @param dbindex
	 * @param timeWin
	 * @param tripId
	 * @return List of trips along with the time difference between the time one
	 *         arrives at a stop and the time connected trip passes through the
	 *         other stop.
	 * @throws SQLException
	 */
	@GET
	@Path("/timingCon")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getTimingCon(@QueryParam("radius") String radius,
			@QueryParam("agencyId") String agencyId,
			@QueryParam("date") String date,
			@QueryParam("dbindex") Integer dbindex,
			@QueryParam("timeWin") Integer timeWin,
			@QueryParam("tripId") String tripId) throws SQLException {

		List<ConTrip> response = new ArrayList<ConTrip>();
		if (dbindex == null || dbindex < 0 || dbindex > dbsize - 1) {
			dbindex = default_dbindex;
		}

		// Setting date
		String fulldate = null;
		String day = null;
		String[] dates = date.split(",");
		String[][] datedays = daysOfWeekString(dates);
		fulldate = datedays[0][0];
		day = datedays[1][0];

		Connection connection = PgisEventManager.makeConnection(dbindex);
		Statement stmt = connection.createStatement();
		String query = "WITH tripstops AS (SELECT trips.epshape AS shape1, stop_id, stop_agencyid, stopsequence, arrivaltime, departuretime, stops.location FROM gtfs_stop_times AS times INNER JOIN gtfs_stops AS stops ON stop_id=id AND stop_agencyid=agencyid "
				+ "	INNER JOIN gtfs_trips AS trips ON trips.agencyid = '"+ agencyId + "' and trips.id = '" + tripId + "' "
				+ "	WHERE times.trip_agencyid = '" + agencyId + "' AND times.trip_id = '" + tripId + "'), "
				+ "connectedstops AS (SELECT arrivaltime, departuretime, stopsequence, stop_id AS stopid1 ,id AS stopid2, name AS stopname2, lat AS stoplat2, lon AS stoplon2, agencyid AS agencyid2, lat, lon, stops.location, shape1 "
				+ "	FROM gtfs_stops AS stops  "
				+ "	INNER JOIN tripstops ON ST_DISTANCE(tripstops.location, stops.location) < " + radius
				+ "	WHERE id <> stop_id "
				+ "	ORDER BY stopsequence), "
				+ "connectedroutes AS (SELECT stopid1, map.agencyid, map.agencyid_def, map.routeid, stopid2, stopname2, stoplat2, stoplon2, arrivaltime, departuretime, shape1 "
				+ "	FROM gtfs_stop_route_map AS map  "
				+ "	INNER JOIN connectedstops ON map.agencyid_def = connectedstops.agencyid2 and stopid = stopid2), "
				+ " "
				+ "connectedtrips AS (SELECT trips.id AS tripid, trips.agencyid AS tripagencyid, routes.shape1, trips.epshape AS shape2, trips.route_id AS routeid, trips.route_agencyid,  "
				+ "	routes.agencyid_def AS route_agencyid_def, serviceid_id AS serviceid, arrivaltime, departuretime, stopid1, stopid2, stopname2, stoplat2, stoplon2 "
				+ "	FROM gtfs_trips AS trips  "
				+ "	INNER JOIN connectedroutes AS routes "
				+ "	ON trips.route_id = routes.routeid AND trips.route_agencyid = routes.agencyid), "
				+ "exceptiondates AS (SELECT * FROM gtfs_calendar_dates "
				+ "	WHERE serviceid_agencyid IN (SELECT DISTINCT route_agencyid_def FROM connectedtrips) AND date::int = '" + fulldate + "'), "
				+ "tripsoftheday1 AS (SELECT trips.*  "
				+ "	FROM connectedtrips AS trips  "
				+ "	INNER JOIN gtfs_calendars AS cal ON cal.serviceid_agencyid = trips.route_agencyid_def AND cal.serviceid_id = trips.serviceid "
				+ "	WHERE startdate::int <= " + fulldate + " AND enddate::int >= " + fulldate + " AND " + day + "= 1 " + "	), "
				+ " "
				+ "tripsoftheday2 AS (SELECT trips.* FROM connectedtrips AS trips  "
				+ "	INNER JOIN exceptiondates AS dates ON dates.serviceid_agencyid = trips.route_agencyid_def AND dates.serviceid_id = trips.serviceid "
				+ "	WHERE exceptiontype = 1	), "
				+ " "
				+ "alltripsoftheday AS (select * from tripsoftheday1 UNION ALL (select * from tripsoftheday2)), "
				+ "tripsoftheday AS (SELECT alltrips.* FROM alltripsoftheday AS alltrips  "
				+ "	LEFT JOIN (SELECT * FROM exceptiondates WHERE exceptiontype=2) AS offdates "
				+ "	ON alltrips.route_agencyid_def = offdates.serviceid_agencyid AND alltrips.serviceid = offdates.serviceid_id  "
				+ "	WHERE offdates.serviceid_id IS NULL  "
				+ "	AND arrivaltime <> -999 AND tripid <> '" + tripId + "'), "
				+ " "
				+ "tripswithintimewindow AS (SELECT trips.stopid1, trips.stopid2, trips.stopname2, stoplat2, stoplon2, trips.routeid, trips.route_agencyid, trips.tripid, tripagencyid,  "
				+ "	serviceid, trips.arrivaltime as arrival1, trips.departuretime AS departure1, "
				+ "	times.arrivaltime AS arrival2, times.departuretime AS departure2, (times.departuretime - trips.arrivaltime) AS diff, trips.shape1, trips.shape2 "
				+ "	FROM tripsoftheday AS trips  "
				+ "	INNER join gtfs_stop_times AS times "
				+ "	ON trips.tripagencyid = times.trip_agencyid AND trips.tripid=times.trip_id AND trips.stopid2 = times.stop_id "
				+ "	WHERE ABS(trips.arrivaltime - times.departuretime) <" + timeWin + "*60) "
				+ " "
				+ "SELECT agencies.name AS agency_name, routes.longname AS route_name, stops.name AS stopname1, stops.lat AS stoplat1, stops.lon AS stoplon1, tripswithintimewindow.* FROM tripswithintimewindow "
				+ "	INNER JOIN gtfs_agencies AS agencies ON tripswithintimewindow.tripagencyid = agencies.id "
				+ "	INNER JOIN gtfs_routes AS routes ON tripswithintimewindow.route_agencyid = routes.agencyid "
				+ "	AND tripswithintimewindow.routeid = routes.id "
				+ "	INNER JOIN gtfs_stops AS stops ON stopid1 = stops.id AND stops.agencyid IN (SELECT id FROM gtfs_agencies WHERE defaultid = '" + agencyId + "') ";
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			ConTrip e = new ConTrip();
			e.agencyId = rs.getString("route_agencyid");
			e.agencyName = rs.getString("agency_name");
			e.arrival1 = rs.getInt("arrival1");
			e.arrival2 = rs.getInt("arrival2");
			e.departure1 = rs.getInt("departure1");
			e.departure2 = rs.getInt("departure2");
			e.routeId = rs.getString("routeid");
			e.routeName = rs.getString("route_name");
			e.stopId1 = rs.getString("stopid1");
			e.stopName1 = rs.getString("stopname1");
			e.stopName2 = rs.getString("stopname2");
			e.stopId2 = rs.getString("stopid2");
			e.stopLat1 = rs.getDouble("stoplat1");
			e.stopLon1 = rs.getDouble("stoplon1");
			e.stopLat2 = rs.getDouble("stoplat2");
			e.stopLon2 = rs.getDouble("stoplon2");
			e.timeDiff = rs.getInt("diff");
			e.tripShape1 = rs.getString("shape1");
			e.tripShape2 = rs.getString("shape2");
			response.add(e);
		}
		connection.close();
		rs.close();
		return response;
	}

	/**
	 * Return a list of stops for a given route
	 * @param agencyid
	 * @param routeid
	 * @param dbindex
	 * @return StopList
	 * @throws SQLException
	 */
	@GET
	@Path("/stopsbyroute")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object dbstopsforroute(@QueryParam("agency") String agencyid,
			@QueryParam("route") String routeid,
			@QueryParam("dbindex") Integer dbindex) throws SQLException {
		if (dbindex == null || dbindex < 0 || dbindex > dbsize - 1) {
			dbindex = default_dbindex;
		}
		StopList response = new StopList();
		Connection connection = PgisEventManager.makeConnection(dbindex);
		Statement stmt = connection.createStatement();
		String query;
		query = "SELECT stops.* FROM gtfs_stops AS stops INNER JOIN gtfs_stop_route_map AS map"
				+ " ON stops.id = map.stopid AND stops.agencyid = map.agencyid_def"
				+ " WHERE map.agencyid = '"
				+ agencyid
				+ "' AND map.routeid = '" + routeid + "'";
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			StopR e = new StopR();
			e.StopId = rs.getString("id");
			e.StopName = rs.getString("name");
			e.lat = rs.getString("lat");
			e.lon = rs.getString("lon");
			response.stops.add(e);
		}
		connection.close();
		rs.close();
		return response;
	}

	/**
	 * Returns the progress value
	 * @param key
	 * @return ProgVal
	 */
	@GET
	@Path("/PorgVal")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getProgVal(@QueryParam("key") double key) {
		ProgVal progress = new ProgVal();
		progress.progVal = getprogVal(key);
		return progress;
	}

	public static Map<Double, Integer> progVal = new HashMap<Double, Integer>();

	/**
	 * progress value setter
	 * @param key
	 * @param val
	 */
	public static void setprogVal(double key, int val) {
		progVal.put(key, val);
	}

	/**
	 * progress value getter
	 * @param key
	 * @param val
	 */
	public int getprogVal(double key) {
		if (progVal.get(key) == null) {
			return 0;
		} else {
			return progVal.get(key);
		}
	}

	/**
	 * Returns a 2D array , [0][i] is date in YYYYMMDD format, [1][i] is day of
	 * week as integer 1(sunday) to 7(friday)
	 * @param dates
	 * @return int[][]
	 */
	public int[][] daysOfWeek(String[] dates) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		int[][] days = new int[2][dates.length];
		for (int i = 0; i < dates.length; i++) {
			days[0][i] = Integer.parseInt(dates[i].split("/")[2]
					+ dates[i].split("/")[0] + dates[i].split("/")[1]);
			try {
				calendar.setTime(sdf.parse(dates[i]));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			days[1][i] = calendar.get(Calendar.DAY_OF_WEEK);
		}
		return days;
	}

	/**
	 * Returns a 2D array , [0][i] is date in YYYYMMDD format, [1][i] is day of
	 * week string (all lower case): sunday, monday, tuesday, wednesday, thursday, friday
	 * 
	 * @param dates
	 * @return
	 */
	public String[][] daysOfWeekString(String[] dates) {
		Calendar calendar = Calendar.getInstance();
		String[] weekdays = { "sunday", "monday", "tuesday", "wednesday",
				"thursday", "friday", "saturday" };
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		String[][] days = new String[2][dates.length];
		for (int i = 0; i < dates.length; i++) {
			days[0][i] = dates[i].split("/")[2] + dates[i].split("/")[0]
					+ dates[i].split("/")[1];
			try {
				calendar.setTime(sdf.parse(dates[i]));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			days[1][i] = weekdays[calendar.get(Calendar.DAY_OF_WEEK) - 1];
		}
		return days;
	}

	/**
	 * Returns full date for the dates selected on calendar in EEE dd MMM yyyy
	 * fromat	 
	 * @param dates
	 * @return
	 */
	public String[] fulldate(String[] dates) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		SimpleDateFormat tdf = new SimpleDateFormat("EEE dd MMM yyyy");
		String[] result = new String[dates.length];
		for (int i = 0; i < dates.length; i++) {
			try {
				result[i] = tdf.format(sdf.parse(dates[i]));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * Generates The Agency Extended Report
	 * @param agency - agency ID
	 * @param date
	 * @param popYear - population projection year
	 * @param x - population search radius
	 * @param key - unique key to update progress
	 * @param LOS - minimum level of service
	 * @param dbindex
	 * @param areaid - ID of the geographical area to filter the service
	 * @param type - type of the geographical area
	 * @param username - user session
	 * @param geoid - ID of the geographical area to be intersected with urban areas for filtering service
	 * @param geotype - type of the geographical area to be intersected with urban areas for filtering service
 	 * @return AgencyXR
	 * @throws JSONException
	 * @throws SQLException
	 */
	@GET
	@Path("/AgencyXR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getAXR(@QueryParam("agency") String agency,
			@QueryParam("day") String date,
			@QueryParam("popYear") String popYear, @QueryParam("x") double x,
			@QueryParam("key") double key,
			@QueryParam("l") Integer LOS,
			@QueryParam("dbindex") Integer dbindex,
			@QueryParam("areaId") String areaid, @QueryParam("type") int type,
			@QueryParam("username") String username,
			@QueryParam("geoid") String geoid,
			@QueryParam("geotype") int geotype) throws JSONException,
			SQLException {

		if (Double.isNaN(x) || x <= 0) {
			x = STOP_SEARCH_RADIUS;
		}

		if (dbindex == null || dbindex < 0 || dbindex > dbsize - 1) {
			dbindex = default_dbindex;
		}
		if (popYear == null || popYear.equals("null")) {
			popYear = "2010";
		}
		int totalLoad = 10;
		int index = 0;
		String[] dates = date.split(",");
		String[][] datedays = daysOfWeekString(dates);
		String[] fulldates = fulldate(dates);
		String[] sdates = datedays[0];
		String[] days = datedays[1];
		HashMap<String, String> ServiceMetrics = new HashMap<String, String>();
		double[] StopsPopMiles;

		AgencyXR response = new AgencyXR();
		x = x * 1609.34;
		response.AgencyId = agency;
		HashMap<String, ConGraphAgency> allAgencies = new HashMap<String, ConGraphAgency>();
		allAgencies = SpatialEventManager.getAllAgencies(username, dbindex);
		response.AgencyName = allAgencies.get(agency).name;
		index++;
		setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		if (areaid.equals("null")) {
			StopsPopMiles = PgisEventManager.stopsPopMiles(0, agency, x,
					dbindex, popYear, null, null, -1);
			index += 2;
		}

		else {
			if (geotype == -1) {
				StopsPopMiles = PgisEventManager.stopsPopMiles(type, agency, x,
						dbindex, popYear, areaid, null, -1);
			} else {
				StopsPopMiles = PgisEventManager.stopsPopMiles(type, agency, x,
						dbindex, popYear, areaid, geoid, geotype);
			}
			index += 2;
		}
		setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		response.UrbanStopCount = String.valueOf(Math.round(StopsPopMiles[0]));
		response.RuralStopCount = String.valueOf(Math.round(StopsPopMiles[1]));
		response.StopCount = String.valueOf(Math.round(StopsPopMiles[0] + StopsPopMiles[1]));
		response.UPopWithinX = String.valueOf(Math.round(StopsPopMiles[2]));
		response.RPopWithinX = String.valueOf(Math.round(StopsPopMiles[3]));
		response.racWithinX = String.valueOf(Math.round(StopsPopMiles[5]));
		response.wacWithinX = String.valueOf(Math.round(StopsPopMiles[6]));

		double RouteMiles = StopsPopMiles[4];
		response.RouteMiles = String.valueOf(RouteMiles);
		if (RouteMiles > 0)
			response.StopPerRouteMile = String.valueOf(Math
					.round((Integer.parseInt(response.StopCount) * 10000.0) / (RouteMiles)) / 10000.0);
		else
			response.StopPerRouteMile = "NA";
		if (geotype != -1) {
			ServiceMetrics = PgisEventManager.AgencyServiceMetrics(sdates,
					days, fulldates, agency, x, LOS, dbindex, popYear, areaid, type,
					geotype, geoid);
		} else {
			ServiceMetrics = PgisEventManager.AgencyServiceMetrics(sdates,
					days, fulldates, agency, x, LOS, dbindex, popYear, areaid, type,
					-1, null);
		}

		index += 6;
		setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		response.ServiceMiles = ServiceMetrics.get("svcmiles");
		response.ServiceHours = ServiceMetrics.get("svchours");
		response.UrbanServiceStops = ServiceMetrics.get("svcstops_urban");
		response.RuralServiceStops = ServiceMetrics.get("svcstops_rural");
		response.UPopServedByService = ServiceMetrics.get("uspop");
		response.RPopServedByService = ServiceMetrics.get("rspop");
		response.UPopLos = ServiceMetrics.get("upop_los");
		response.RPopLos = ServiceMetrics.get("rpop_los");
		response.racServedByService = ServiceMetrics.get("srac");
		response.wacServedByService = ServiceMetrics.get("swac");
		String serviceDays = ServiceMetrics.get("svcdays");
		if (serviceDays.length() > 2) {
			serviceDays = serviceDays.replace("\"", "");
			serviceDays = serviceDays.substring(1, serviceDays.length() - 1);
			String[] svcdays = serviceDays.split(",");
			serviceDays = StringUtils.join(Arrays.asList(svcdays), ";");
		}
		response.ServiceDays = serviceDays;
		int HOSstart = Integer.parseInt(ServiceMetrics.get("fromtime"));
		int HOSend = Integer.parseInt(ServiceMetrics.get("totime"));
		response.HoursOfService = ((HOSstart == -1) ? "NA" : StringUtils
				.timefromint(HOSstart))
				+ "-"
				+ ((HOSend == -1) ? "NA" : StringUtils.timefromint(HOSend));
		index++;
		setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		progVal.remove(key);
		return response;
	}

	/**
	 * Generates The Stops report for agency agency and route geographic area
	 * geographic area and agency geographic area and route and agency
	 * 
	 * @param agency - agency ID 
	 * @param x - search radius
	 * @param routeid
	 * @param areaid - ID of the geographical area to filter stops
	 * @param type - type of the geographical area to filter stops
	 * @param key - unique key to update progress
	 * @param popYear - population projection year
	 * @param date
	 * @param dbindex
	 * @param username
	 * @param geotype - type of the geographical area to intersect with urban areas
	 * @param geoid - ID of the geographical area to intersect with urban areas
	 * @param rc - integer to distinguish rural and urban stops
	 * @return StopListR
	 * @throws JSONException
	 * @throws SQLException
	 */
	@GET
	@Path("/StopsR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getTAS(
			@QueryParam("agency") String agency,
			@QueryParam("x") double x, 
			@QueryParam("route") String routeid,
			@QueryParam("areaid") String areaid,
			@QueryParam("type") Integer type, 
			@QueryParam("key") double key,
			@QueryParam("popYear") String popYear,
			@QueryParam("day") String date,
			@QueryParam("dbindex") Integer dbindex,
			@QueryParam("username") String username,
			@QueryParam("geotype") Integer geotype,
			@QueryParam("geoid") String geoid,
			@QueryParam("rc") Integer rc 
			) throws JSONException,
			SQLException {
		if (Double.isNaN(x) || x <= 0) {
			x = STOP_SEARCH_RADIUS;
		}
		if (dbindex == null || dbindex < 0 || dbindex > dbsize - 1) {
			dbindex = default_dbindex;
		}
		if (popYear == null || popYear.equals("null")) {
			popYear = "2010";
		}
		String[] dates = date.split(",");
		String[][] datedays = daysOfWeekString(dates);
		String[] fulldates = datedays[0];
		String[] days = datedays[1];
		StopListR response = new StopListR();
		response.AgencyName = "";
		int index = 0;
		int totalLoad = 4;
		if (agency != null)
			agency = (agency.equals("null")) ? null : agency;
		if (routeid != null)
			routeid = (routeid.equals("null")) ? null : routeid;
		if (areaid != null)
			areaid = (areaid.equals("null")) ? null : areaid;

		setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		ArrayList<StopR> report = new ArrayList<StopR>();
		if (areaid == null) {
			HashMap<String, ConGraphAgency> allAgencies = new HashMap<String, ConGraphAgency>();
			allAgencies = SpatialEventManager.getAllAgencies(username, dbindex);
			response.AgencyName = allAgencies.get(agency).name;
			index++;
			setprogVal(key, (int) Math.round(index * 100 / totalLoad));
			if (routeid == null) { // agency
				response.metadata = "Report Type:Transit Stops Report for Agency;Report Date:"
						+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + ";"
						+ "Selected Database:" + Databases.dbnames[dbindex] + ";Population Search Radius(miles):"
						+ String.valueOf(x) + ";Selected Transit Agency:" + agency + ";" + DbUpdate.VERSION;
				report = PgisEventManager.stopGeosr(username, 0, fulldates, days, null, agency, null, x * 1609.34,dbindex, popYear,-1,null,rc);
				index++;
				setprogVal(key, (int) Math.round(index * 100 / totalLoad));
			} else {// agency and route
				response.metadata = "Report Type:Transit Stops Report for Agency and Route;Report Date:"
						+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
								.format(Calendar.getInstance().getTime())
						+ ";"
						+ "Selected Database:"
						+ Databases.dbnames[dbindex]
						+ ";Population Search Radius(miles):"
						+ String.valueOf(x)
						+ ";Selected Transit Agency:"
						+ agency
						+ ";Selected Route:"
						+ routeid
						+ ";"
						+ DbUpdate.VERSION;
				report = PgisEventManager.stopGeosr(username, 0, fulldates, days, null, agency, routeid, x * 1609.34,
						dbindex, popYear,-1,null,rc);
				index++;
				setprogVal(key, (int) Math.round(index * 100 / totalLoad));
			}
		} else {
			response.AreaType = Types.getAreaName(type);
			if (!geoid.equals("null")) {
				response.AreaName = PgisEventManager.QueryGeoAreabyId(type,
						areaid, dbindex, username, popYear, geotype, geoid)
						.getName();
			} else {
				response.AreaName = PgisEventManager.QueryGeoAreabyId(type,
						areaid, dbindex, username, popYear, -1, null).getName();
			}
			index++;
			setprogVal(key, (int) Math.round(index * 100 / totalLoad));
			if (routeid == null) {
				if (agency == null) {// area
					response.metadata = "Report Type:Transit Stops Report for "
							+ Types.getAreaName(type)
							+ ";Report Date:"
							+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
									.format(Calendar.getInstance().getTime())
							+ ";" + "Selected Database:"
							+ Databases.dbnames[dbindex]
							+ ";Population Search Radius(miles):"
							+ String.valueOf(x) + ";Selected Geographic Area:"
							+ areaid + ";" + DbUpdate.VERSION;
					if (!geoid.equals("null")) {
						report = PgisEventManager.stopGeosr(username, type,
								fulldates, days, areaid, null, null,
								x * 1609.34, dbindex, popYear, geotype, geoid,rc);
					} else {
						report = PgisEventManager.stopGeosr(username, type,
								fulldates, days, areaid, null, null,
								x * 1609.34, dbindex, popYear, -1, null, rc);
					}
					index++;
					setprogVal(key, (int) Math.round(index * 100 / totalLoad));
				} else {// area and agency
					HashMap<String, ConGraphAgency> allAgencies = new HashMap<String, ConGraphAgency>();
					allAgencies = SpatialEventManager.getAllAgencies(username,
							dbindex);
					response.AgencyName = allAgencies.get(agency).name;
					index++;
					setprogVal(key, (int) Math.round(index * 100 / totalLoad));
					response.metadata = "Report Type:Transit Stops Report for "
							+ Types.getAreaName(type)
							+ ";Report Date:"
							+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
									.format(Calendar.getInstance().getTime())
							+ ";" + "Selected Database:"
							+ Databases.dbnames[dbindex]
							+ ";Population Search Radius(miles):"
							+ String.valueOf(x) + ";Selected Geographic Area:"
							+ areaid + ";Selected Transit Agency:" + agency
							+ ";" + DbUpdate.VERSION;
					if (!geoid.equals("null")) {
						report = PgisEventManager.stopGeosr(username, type,
								fulldates, days, areaid, agency, null,
								x * 1609.34, dbindex, popYear, geotype, geoid, rc);
					} else {
						report = PgisEventManager.stopGeosr(username, type,
								fulldates, days, areaid, agency, null,
								x * 1609.34, dbindex, popYear, -1, null, rc);
					}
					index++;
					setprogVal(key, (int) Math.round(index * 100 / totalLoad));
				}
			} else {// area, agency, and route
				HashMap<String, ConGraphAgency> allAgencies = new HashMap<String, ConGraphAgency>();
				allAgencies = SpatialEventManager.getAllAgencies(username,
						dbindex);
				response.AgencyName = allAgencies.get(agency).name;
				index++;
				setprogVal(key, (int) Math.round(index * 100 / totalLoad));
				response.metadata = "Report Type:Transit Stops Report for "
						+ Types.getAreaName(type)
						+ ";Report Date:"
						+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
								.format(Calendar.getInstance().getTime()) + ";"
						+ "Selected Database:" + Databases.dbnames[dbindex]
						+ ";Population Search Radius(miles):"
						+ String.valueOf(x) + ";Selected Geographic Area:"
						+ areaid + ";Selected Transit Agency:" + agency
						+ ";Selected Route:" + routeid + ";" + DbUpdate.VERSION;
				if (!geoid.equals("null")) {
					report = PgisEventManager.stopGeosr(username, type,
							fulldates, days, areaid, agency, routeid,
							x * 1609.34, dbindex, popYear, geotype, geoid, rc);
				} else {
					report = PgisEventManager.stopGeosr(username, type,
							fulldates, days, areaid, agency, routeid,
							x * 1609.34, dbindex, popYear, -1, null, rc);
				}
				index++;
				setprogVal(key, (int) Math.round(index * 100 / totalLoad));
			}

		}
		response.StopR = report;
		setprogVal(key, 100);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		progVal.remove(key);
		return response;
	}

	/**
	 * Generates The Agency Summary report and geographic allocation of service
	 * for transit agencies
	 * 
	 * @param key - unique key to update progress
	 * @param dbindex - database index
	 * @param popYear - population projection year
	 * @param username - user session
	 * @param areaId - ID of the geographical area to filter service
	 * @param type - type of the geographical area to filter service
	 * @param geotype - type of the geographical area to intersect with urban areas
	 * @param geoid - ID of the geographical area to intersect with urban areas
	 * @return AgencySRList
	 * @throws JSONException
	 */
	@GET
	@Path("/AgencySR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getASR(@QueryParam("key") double key,
			@QueryParam("dbindex") Integer dbindex,
			@QueryParam("popYear") String popYear,
			@QueryParam("username") String username,
			@QueryParam("areaid") String areaId,
			@QueryParam("type") Integer type,
			@QueryParam("geotype") Integer geotype,
			@QueryParam("geoid") String geoid) throws JSONException {
		if (dbindex == null || dbindex < 0 || dbindex > dbsize - 1) {
			dbindex = default_dbindex;
		}
		if (popYear == null || popYear.equals("null")) {
			popYear = "2010";
		}
		int index = 0;
		int totalLoad = 2;
		setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		ArrayList<AgencySR> agencies = new ArrayList<AgencySR>();
		AgencySRList response = new AgencySRList();
		if (type != null && areaId != null) {
			GeoArea instance = EventManager.QueryGeoAreabyId(areaId, type,
					dbindex, Integer.parseInt(popYear));
			if (geoid != null) {
				agencies = PgisEventManager.agencyGeosr(username, type, areaId,
						dbindex, geotype, geoid);

			} else {
				agencies = PgisEventManager.agencyGeosr(username, type, areaId,
						dbindex, -1, null);

			}
			response.metadata = "Report Type:Agency Allocation of Service (Transit Agency Summary Report) for "
					+ instance.getName()
					+ "("
					+ Types.getAreaName(type)
					+ ")"
					+ ";Report Date:"
					+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
							.format(Calendar.getInstance().getTime())
					+ ";Selected Database:"
					+ Databases.dbnames[dbindex]
					+ ";"
					+ DbUpdate.VERSION;
			response.areaName = instance.getName();
			// might need to add area type for census tracts and some other geo
			// areas
			response.areaType = instance.getTypeName();
		} else {
			agencies = PgisEventManager.agencysr(username, dbindex);
			response.metadata = "Report Type:Transit Agency Summary Report;Report Date:"
					+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
							.format(Calendar.getInstance().getTime())
					+ ";"
					+ "Selected Database:"
					+ Databases.dbnames[dbindex]
					+ ";"
					+ DbUpdate.VERSION;
		}
		index++;
		setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		response.agencySR = agencies;
		index++;
		setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		progVal.remove(key);
		return response;
	}

	/**
	 * Returns the list of routes operated by and agency regardless of being
	 * active or not.
	 * 
	 * @param agencyId
	 * @param dbindex
	 * @param username
	 * @return RouteListR
	 * @throws SQLException
	 */
	@GET
	@Path("/agencyRoutes")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getAgencyRoutes(@QueryParam("agency") String agencyId,
			@QueryParam("dbindex") Integer dbindex,
			@QueryParam("username") String username) throws SQLException {
		RouteListR response = new RouteListR();
		if (dbindex == null || dbindex < 0 || dbindex > dbsize - 1) {
			dbindex = default_dbindex;
		}
		Connection connection = PgisEventManager.makeConnection(dbindex);
		Statement stmt = connection.createStatement();
		String query = "SELECT * FROM gtfs_routes WHERE agencyid = '"
				+ agencyId + "'";
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			RouteR i = new RouteR();
			i.AgencyId = agencyId;
			i.RouteId = rs.getString("id");
			i.RouteLName = rs.getString("longname");
			i.RouteSName = rs.getString("shortname");
			response.RouteR.add(i);
		}
		connection.close();
		return response;
	}

	/**
	 * Returns the list of the trips running on the given date along with their
	 * start and end time in hh:mm:ss.
	 * 
	 * @param agencyId
	 * @param date
	 * @param radius
	 * @param dbindex
	 * @return
	 * @throws SQLException
	 */
	@GET
	@Path("/tripsOfTheDayByRoute")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object tripsOfTheDayByRoute(@QueryParam("agency") String agencyId,
			@QueryParam("date") String date,
			@QueryParam("radius") Double radius,
			@QueryParam("dbindex") Integer dbindex,
			@QueryParam("routeId") String routeId) throws SQLException {
		if (dbindex == null || dbindex < 0 || dbindex > dbsize - 1) {
			dbindex = default_dbindex;
		}
		List<TripTime> response = new ArrayList<TripTime>();

		String[] dates = date.split(",");
		String[][] datedays = daysOfWeekString(dates);
		String[] sdates = datedays[0];
		String[] days = datedays[1];
		Connection connection = PgisEventManager.makeConnection(dbindex);
		Statement stmt = connection.createStatement();
		String query = "WITH aid AS (SELECT defaultid FROM gtfs_agencies WHERE id = '"
				+ agencyId
				+ "'), "
				+ " services0 AS (SELECT * FROM gtfs_calendars WHERE startdate::int <= "
				+ sdates[0]
				+ " AND enddate::int >= "
				+ sdates[0]
				+ " AND serviceid_agencyid IN (SELECT * FROM aid) AND "
				+ days[0]
				+ " = 1), "
				+ " exceptiondates AS (SELECT * FROM gtfs_calendar_dates WHERE serviceid_agencyid IN (SELECT * FROM aid) AND date::int = '"
				+ sdates[0]
				+ "'), "
				+ " svcids0 AS (SELECT services0.serviceid_agencyid, services0.serviceid_id FROM services0 "
				+ "	LEFT JOIN (SELECT * FROM exceptiondates WHERE exceptiontype=2) AS offdates "
				+ "	ON services0.serviceid_id = offdates.serviceid_id WHERE offdates.serviceid_id IS NULL), "
				+ " svcids AS (SELECT * FROM svcids0 UNION ALL (SELECT serviceid_agencyid, serviceid_id FROM exceptiondates WHERE exceptiontype=1)), "

				// SET OF ALL TRIPS OF THE AGENCY RUNNING ON THE GIVEN DATE "
				+ " trips AS (SELECT trips.* FROM gtfs_trips AS trips INNER JOIN svcids USING(serviceid_agencyid,serviceid_id) "
				+ " WHERE route_agencyid='"
				+ agencyId
				+ "' AND route_id = '"
				+ routeId
				+ "')"

				// SET OF FEASIBLE ROUTES ON THE SELECTED DATE ARE LISTED WITH
				// THEIR RESPECTIVE TIME INTERVALS
				+ " SELECT times.trip_agencyid, times.trip_id, FLOOR(MIN(arrivaltime)/3600) AS arrival_hour, FLOOR(MIN(arrivaltime)%3600/60) AS arrival_minute, FLOOR((MIN(arrivaltime)%3600%60)/60) AS arrival_second, "
				+ "	FLOOR(MAX(departuretime)/3600) AS departure_hour, FLOOR(MAX(departuretime)%3600/60) AS departure_minute, FLOOR((MAX(departuretime)%3600%60)/60) AS departure_second "
				+ "	FROM trips INNER JOIN gtfs_stop_times AS times "
				+ "	ON trips.id = times.trip_id "
				+ "	WHERE times.arrivaltime > 0 "
				+ "	GROUP BY times.trip_agencyid, times.trip_id "
				+ "	ORDER BY MIN(arrivaltime)";
		// System.out.println(query);
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			TripTime t = new TripTime();
			t.tripId = rs.getString("trip_id");
			t.start_hr = (int) rs.getDouble("arrival_hour");
			t.start_min = (int) rs.getDouble("arrival_minute");
			t.start_sec = (int) rs.getDouble("arrival_second");
			t.end_hr = (int) rs.getDouble("departure_hour");
			t.end_min = (int) rs.getDouble("departure_minute");
			t.end_sec = (int) rs.getDouble("departure_second");
			response.add(t);
		}
		connection.close();
		rs.close();
		return response;
	}

	/**
	 * Generates The Routes report: routes report by agency routes report by
	 * agency and geographic area routes report by geographic area
	 * @param agency - agency ID
	 * @param x - population search radius
	 * @param date
	 * @param key - unique key to track progress
	 * @param dbindex - database index
	 * @param popYear - population projection year
	 * @param username - user session
	 * @param areaid - ID of the geographical area to filter stops
	 * @param type - type of the geographical area to filter stops
	 * @return RouteListR
	 * @throws JSONException
	 */
	@GET
	@Path("/RoutesR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getTAR(
			@QueryParam("agency") String agency,
			@QueryParam("x") double x, @QueryParam("day") String date,
			@QueryParam("key") double key,
			@QueryParam("dbindex") Integer dbindex,
			@QueryParam("popYear") String popYear,
			@QueryParam("username") String username,
			@QueryParam("areaid") String areaid,
			@QueryParam("type") Integer type
			) throws JSONException {
		if (Double.isNaN(x) || x <= 0) {
			x = STOP_SEARCH_RADIUS;
		}
		if (dbindex == null || dbindex < 0 || dbindex > dbsize - 1) {
			dbindex = default_dbindex;
		}
		if (agency != null)
			agency = (agency.equals("null")) ? null : agency;
		if (areaid != null)
			areaid = (areaid.equals("null")) ? null : areaid;
		if (popYear == null || popYear.equals("null")) {
			popYear = "2010";
		}
		String[] dates = date.split(",");
		String[][] datedays = daysOfWeekString(dates);
		String[] sdates = datedays[0]; // date in YYYYMMDD format
		String[] days = datedays[1]; // day of week string (all lower case)
		ArrayList<RouteR> report = new ArrayList<RouteR>();
		RouteListR response = new RouteListR();
		int index = 0;
		int totalLoad = 4;
		if (areaid == null) {// routes report by agency
			response.AgencyName = GtfsHibernateReaderExampleMain
					.QueryAgencybyid(agency, dbindex).getName();
			index++;
			setprogVal(key, (int) Math.round(index * 100 / totalLoad));
			response.metadata = "Report Type:Transit Routes Report for Agency;Report Date:"
					+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
							.format(Calendar.getInstance().getTime())
					+ ";Selected Database:"
					+ Databases.dbnames[dbindex]
					+ ";Selected Date(s):"
					+ date
					+ ";Population Search Radius(miles):"
					+ String.valueOf(x)
					+ ";Selected Transit Agency:"
					+ agency
					+ ";"
					+ DbUpdate.VERSION;
			report = PgisEventManager.RouteGeosr(username, 0, null, agency,
					sdates, days, x * 1609.34, dbindex, popYear);
			index++;
			setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		} else {
			response.AreaType = Types.getAreaName(type);
			response.AreaName = EventManager.QueryGeoAreabyId(areaid, type,
					dbindex, Integer.parseInt(popYear)).getName();
			index++;
			setprogVal(key, (int) Math.round(index * 100 / totalLoad));
			if (agency != null) {// routes report by agency and areaId
				response.AgencyName = GtfsHibernateReaderExampleMain
						.QueryAgencybyid(agency, dbindex).getName();
				index++;
				setprogVal(key, (int) Math.round(index * 100 / totalLoad));
				response.metadata = "Report Type:Transit Routes Report for "
						+ Types.getAreaName(type)
						+ ";Report Date:"
						+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
								.format(Calendar.getInstance().getTime())
						+ ";Selected Database:" + Databases.dbnames[dbindex]
						+ ";Selected Date(s):" + date
						+ ";Population Search Radius(miles):"
						+ String.valueOf(x) + ";Selected Geographic Area:"
						+ areaid + ";Selected Transit Agency:" + agency + ";"
						+ DbUpdate.VERSION;
				report = PgisEventManager.RouteGeosr(username, type, areaid,
						agency, sdates, days, x * 1609.34, dbindex, popYear);
				index++;
				setprogVal(key, (int) Math.round(index * 100 / totalLoad));
			} else {// routes report by areaId
				response.metadata = "Report Type:Transit Routes Report for "
						+ Types.getAreaName(type)
						+ ";Report Date:"
						+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
								.format(Calendar.getInstance().getTime())
						+ ";Selected Database:" + Databases.dbnames[dbindex]
						+ ";Selected Date(s):" + date
						+ ";Population Search Radius(miles):"
						+ String.valueOf(x) + ";Selected Geographic Area:"
						+ areaid + ";" + DbUpdate.VERSION;
				report = PgisEventManager.RouteGeosr(username, type, areaid,
						null, sdates, days, x * 1609.34, dbindex, popYear);
				index++;
				setprogVal(key, (int) Math.round(index * 100 / totalLoad));
			}
		}
		response.RouteR = report;
		index = totalLoad;
		setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		progVal.remove(key);
		return response;
	}

	/**
	 * Generates The Route Schedule/Fare report
	 * 
	 * @param agency
	 * @param routeid
	 * @param date
	 * @param key - unique key to track and update the progress
	 * @param dbindex
	 * @return ScheduleList
	 * @throws JSONException
	 * @throws SQLException
	 */
	@GET
	@Path("/ScheduleR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getSchedule(
			@QueryParam("agency") String agency,
			@QueryParam("route") String routeid,
			@QueryParam("day") String date, 
			@QueryParam("key") double key,
			@QueryParam("dbindex") Integer dbindex
			) throws JSONException,
			SQLException {
		if (dbindex == null || dbindex < 0 || dbindex > dbsize - 1) {
			dbindex = default_dbindex;
		}
		ScheduleList response = new ScheduleList();
		response.metadata = "Report Type:Route Schedule/Fare Report;Report Date:"
				+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar
						.getInstance().getTime())
				+ ";"
				+ "Selected Database:"
				+ Databases.dbnames[dbindex]
				+ ";Selected Date(s):"
				+ date
				+ ";Selected Transit Agency:"
				+ agency
				+ ";Selected Route:"
				+ routeid + ";" + DbUpdate.VERSION;
		String[] dates = date.split(",");
		int[][] days = daysOfWeek(dates);
		// System.out.println(days[0][0]);
		AgencyAndId routeId = new AgencyAndId(agency, routeid);
		response.Agency = GtfsHibernateReaderExampleMain.QueryAgencybyid(
				agency, dbindex).getName()
				+ "";
		Route route = GtfsHibernateReaderExampleMain.QueryRoutebyid(routeId,
				dbindex);
		response.Route = route.getId().getId() + "";
		List<FareRule> fareRules = GtfsHibernateReaderExampleMain
				.QueryFareRuleByRoute(route, dbindex);
		if (fareRules.size() == 0) {
			response.Fare = "N/A";
		} else {
			response.Fare = fareRules.get(0).getFare().getPrice() + "";
		}
		List<Trip> routeTrips = SpatialEventManager.QueryTripsbyRoute(route
				.getId().getAgencyId(), route.getId().getId(), dbindex);
		int totalLoad = routeTrips.size();
		response.directions[0] = new Schedule();
		response.directions[1] = new Schedule();
		Stoptime stoptime;
		int[] maxSize = { 0, 0 };
		int index = 0;
		String serviceAgency = routeTrips.get(0).getServiceId().getAgencyId();
		int startDate;
		int endDate;
		List<ServiceCalendar> agencyServiceCalendar = GtfsHibernateReaderExampleMain
				.QueryCalendarforAgency(serviceAgency, dbindex);
		List<ServiceCalendarDate> agencyServiceCalendarDates = GtfsHibernateReaderExampleMain
				.QueryCalendarDatesforAgency(serviceAgency, dbindex);
		Loop: for (Trip trip : routeTrips) {
			index++;
			boolean isIn = false;
			ServiceCalendar sc = null;
			if (agencyServiceCalendar != null) {
				for (ServiceCalendar scs : agencyServiceCalendar) {
					if (scs.getServiceId().getId()
							.equals(trip.getServiceId().getId())) {
						sc = scs;
						break;
					}
				}
			}
			List<ServiceCalendarDate> scds = new ArrayList<ServiceCalendarDate>();
			for (ServiceCalendarDate scdss : agencyServiceCalendarDates) {
				if (scdss.getServiceId().getId()
						.equals(trip.getServiceId().getId())) {
					scds.add(scdss);
				}
			}

			for (ServiceCalendarDate scd : scds) {
				if (days[0][0] == Integer.parseInt(scd.getDate().getAsString())) {
					if (scd.getExceptionType() == 1) {
						isIn = true;
						break;
					}
					continue Loop;
				}
			}
			if (sc != null && !isIn) {
				startDate = Integer.parseInt(sc.getStartDate().getAsString());
				endDate = Integer.parseInt(sc.getEndDate().getAsString());
				if (!(days[0][0] >= startDate && days[0][0] <= endDate)) {
					continue;
				}
				switch (days[1][0]) {
				case 1:
					if (sc.getSunday() == 1) {
						isIn = true;
					}
					break;
				case 2:
					if (sc.getMonday() == 1) {
						isIn = true;
					}
					break;
				case 3:
					if (sc.getTuesday() == 1) {
						isIn = true;
					}
					break;
				case 4:
					if (sc.getWednesday() == 1) {
						isIn = true;
					}
					break;
				case 5:
					if (sc.getThursday() == 1) {
						isIn = true;
					}
					break;
				case 6:
					if (sc.getFriday() == 1) {
						isIn = true;
					}
					break;
				case 7:
					if (sc.getSaturday() == 1) {
						isIn = true;
					}
					break;
				}
			}
			if (isIn) {
				AgencyAndId agencyAndTrip = trip.getId();
				List<StopTime> stopTimes = SpatialEventManager
						.Querystoptimebytrip(agencyAndTrip.getAgencyId(),
								agencyAndTrip.getId(), dbindex); // to be fixed
				TripSchedule ts = new TripSchedule();
				for (StopTime st : stopTimes) {
					if (st.isArrivalTimeSet()) {
						stoptime = new Stoptime();
						stoptime.StopTime = strArrivalTime(st.getArrivalTime());
						stoptime.StopName = st.getStop().getName() + "";
						stoptime.StopId = st.getStop().getId().getId();
						ts.stoptimes.add(stoptime);
					}
				}
				if (trip.getDirectionId() != null
						&& trip.getDirectionId().equals("1")) {
					if (ts.stoptimes.size() > maxSize[1]) {
						response.directions[1].stops = ts.stoptimes;
						// System.out.println(response.stops.get(0).StopId);
						maxSize[1] = ts.stoptimes.size();
					}
					response.directions[1].schedules.add(ts);
				} else {
					if (ts.stoptimes.size() > maxSize[0]) {
						// System.out.println(ts.stoptimes.size());
						response.directions[0].stops = ts.stoptimes;
						// System.out.println(response.stops.get(0).StopId);
						maxSize[0] = ts.stoptimes.size();
					}
					response.directions[0].schedules.add(ts);
				}
			}
			setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		progVal.remove(key);
		return response;
	}

	/**
	 * Converts seconds to arrival time in hh:mm format
	 * @param arrivalTime (int)
	 * @return arrivalTimeSTR (String)
	 */
	public String strArrivalTime(int arrivalTime) {
		int hour = arrivalTime / 3600;
		int minute = (arrivalTime % 3600) / 60;
		String arrivalTimeSTR = zeroStartValue(hour) + ":"
				+ zeroStartValue(minute);

		return arrivalTimeSTR;
	}

	/**
	 * adds a zero to the end of beginning of the input
	 * and returns a two digit progress value
	 * @param value (int)
	 * @return value (String)
	 */
	public String zeroStartValue(int value) {
		if (value < 10) {
			return "0" + value;
		} else {
			return "" + value;
		}
	}

	/**
	 * Generates The geographic area summary by agency report (geographic
	 * allocation of service)
	 * @param urbanPop - minimum population of urban areas to filter
	 * @param key - unique key to track and update progress
	 * @param dbindex -  database index
	 * @param popYear - population projection year
	 * @param type - type of the geographical area to filter stops
	 * @param agency - agency ID
	 * @param username - user session
	 * @param popmax - maximum population of urban areas to filter
	 * @param popmin - minimum population of urban areas to filter
	 * @param areaid - ID of the geographical area to filter stops
	 * @param geotype - type of the geographical area to filter stops
	 * @param uc - flag to filter on urbanized areas (uc == 0) or on urban clusters (uc ==1)
	 * @return GeoRList
	 * @throws JSONException
	 * @throws SQLException
	 */
	@GET
	@Path("/GeoCSRA")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getGCSRA(
			@QueryParam("upop") int urbanPop,
			@QueryParam("key") double key,
			@QueryParam("dbindex") Integer dbindex,
			@QueryParam("popYear") String popYear,
			@QueryParam("type") Integer type,
			@QueryParam("agency") String agency,
			@QueryParam("username") String username,
			@QueryParam("popMax") String popmax,
			@QueryParam("popMin") String popmin,
			@QueryParam("areaid") String areaid,
			@QueryParam("geotype") Integer geotype,
			@QueryParam("uc") Integer uc
			) throws JSONException,
			SQLException {
		if (dbindex == null || dbindex < 0 || dbindex > dbsize - 1) {
			dbindex = default_dbindex;
		}
		if (popYear == null || popYear.equals("null")) {
			popYear = "2010";
		}
		int index = 1;
		int totalLoad = 3;
		setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		GeoRList response = new GeoRList();
		List<GeoR> results;
		if (agency != null)
			agency = (agency.equals("null")) ? null : agency;
		if (agency != null) {			
			response.agencyId = agency;
			HashMap<String, ConGraphAgency> allAgencies = new HashMap<String, ConGraphAgency>();
			allAgencies = SpatialEventManager.getAllAgencies(username, dbindex);
			response.agencyName = allAgencies.get(agency).name;
		} 
		if (type == 3) {
			results = PgisEventManager.geoallocation(type, agency, dbindex,
					username, urbanPop, popYear, popmin, popmax, areaid,
					geotype,uc);
		} else {
			results = PgisEventManager.geoallocation(type, agency, dbindex,
					username, urbanPop, popYear, "null", "null", "null", 0, 6);
		}
		index++;
		setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		response.type = Types.getAreaName(type);
		response.GeoR = results;
		index++;
		setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		progVal.remove(key);
		return response;
	}

	/**
	 * generates employment report
	 * @param popyear - population projection year
	 * @param dataSet - selectsRAC and/or WAC for retrieve the data
	 * @param reportType - determines whether the report is for agencies, or any of the geographical areas 
	 * @param date - dates list
	 * @param radius - search radius
	 * @param L - minimum level of service
	 * @param dbindex
	 * @param username
	 * @return EmpDataList
	 * @throws JSONException
	 */
	@GET
	@Path("/emp")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getEmp(@QueryParam("projection") String popyear,
			@QueryParam("dataSet") String dataSet,
			@QueryParam("report") String reportType,
			@QueryParam("day") String date,
			@QueryParam("radius") double radius, @QueryParam("L") int L,
			@QueryParam("dbindex") int dbindex,
			@QueryParam("username") String username) throws JSONException {
		EmpDataList results = new EmpDataList();
		String[] dates = date.split(",");
		String[][] datedays = daysOfWeekString(dates);
		String[] fulldates = fulldate(dates);
		String[] sdates = datedays[0];
		String[] days = datedays[1];
		results = PgisEventManager.getEmpData(popyear, dataSet, reportType,
				sdates, days, fulldates, radius, L, dbindex, username);
		results.metadata = "Report Type: "
				+ reportType
				+ " Employment Report;Report Date:"
				+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar
						.getInstance().getTime()) + ";" + "Selected Database:"
				+ Databases.dbnames[dbindex] + ";" + DbUpdate.VERSION;
		return results;
	}

	/**
	 * generates Title VI report
	 * @param reportType - determines whether the report is for agencies, or any of the geographical areas 
	 * @param date - dates list
	 * @param radius - search radius
	 * @param L - minimum level of service
	 * @param dbindex
	 * @param username
	 * @return TitleVIDataList
	 * @throws JSONException
	 */
	@GET
	@Path("/titlevi")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getTitleVIData(@QueryParam("report") String reportType,
			@QueryParam("day") String date,
			@QueryParam("radius") double radius, @QueryParam("L") int L,
			@QueryParam("dbindex") int dbindex,
			@QueryParam("username") String username) throws JSONException {
		TitleVIDataList results = new TitleVIDataList();
		String[] dates = date.split(",");
		String[][] datedays = daysOfWeekString(dates);
		String[] fulldates = fulldate(dates);
		String[] sdates = datedays[0];
		String[] days = datedays[1];
		results = PgisEventManager.getTitleVIData(reportType, sdates, days,
				fulldates, radius, L, dbindex, username);
		results.metadata = "Report Type: "
				+ reportType
				+ " Title VI Report;Report Date:"
				+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar
						.getInstance().getTime()) + ";" + "Selected Database:"
				+ Databases.dbnames[dbindex] + ";" + DbUpdate.VERSION;
		return results;
	}

	/**
	 * Generates The counties Summary report
	 * @param key - unique key to track and update progress
	 * @param dbindex
	 * @param username
	 * @return GeoRList
	 * @throws JSONException
	 */
	@GET
	@Path("/GeoCSR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getGCSR(@QueryParam("key") double key,
			@QueryParam("dbindex") Integer dbindex,
			@QueryParam("username") String username) throws JSONException {
		if (dbindex == null || dbindex < 0 || dbindex > dbsize - 1) {
			dbindex = default_dbindex;
		}
		List<County> allcounties = new ArrayList<County>();
		List<String> selectedAgencies = DbUpdate.getSelectedAgencies(username);
		try {
			allcounties = EventManager.getcounties(dbindex);
		} catch (FactoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (TransformException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		GeoRList response = new GeoRList();
		response.type = "County";
		int index = 0;
		int totalLoad = allcounties.size();
		for (County instance : allcounties) {
			index++;
			GeoR each = new GeoR();
			each.Name = instance.getName();
			each.id = instance.getCountyId();
			each.ODOTRegion = instance.getRegionId();
			each.ODOTRegionName = instance.getRegionName();
			each.waterArea = String
					.valueOf(Math.round(instance.getWaterarea() / 2.58999e4) / 100.0);
			each.landArea = String
					.valueOf(Math.round(instance.getLandarea() / 2.58999e4) / 100.0);
			each.population = String.valueOf(instance.getPopulation());
			each.TractsCount = "0";
			try {
				each.TractsCount = String
						.valueOf(EventManager.gettractscountbycounty(
								instance.getCountyId(), dbindex));
			} catch (FactoryException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (TransformException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			each.AverageFare = "0";
			each.MedianFare = "0";
			each.StopsCount = String.valueOf(0);
			try {
				each.StopsCount = String.valueOf(EventManager
						.getstopscountbycounty(instance.getCountyId(),
								selectedAgencies, dbindex));
			} catch (FactoryException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (TransformException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			each.RoutesCount = String.valueOf(0);
			each.AgenciesCount = String.valueOf(0);			
			response.GeoR.add(each);
			setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		progVal.remove(key);
		return response;
	}

	/**
	 * Generates The Tracts (by county) Summary report
	 * 
	 * @param county - ID of the county in which the tracts are to reported
	 * @param key - unique key to track and update progress
	 * @param dbindex
	 * @param username
	 * @return GeoRList
	 * @throws JSONException
	 */
	@GET
	@Path("/GeoCTSR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getGCTSR(
			@QueryParam("county") String county,
			@QueryParam("key") double key, 
			@QueryParam("dbindex") Integer dbindex,
			@QueryParam("username") String username
			) throws JSONException {
		if (dbindex == null || dbindex < 0 || dbindex > dbsize - 1) {
			dbindex = default_dbindex;
		}
		List<Tract> alltracts = new ArrayList<Tract>();
		List<String> selectedAgencies = DbUpdate.getSelectedAgencies(username);
		try {
			alltracts = EventManager.gettractsbycounty(county, dbindex);
		} catch (FactoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (TransformException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		GeoRList response = new GeoRList();
		response.type = "Tract";
		int index = 0;
		int totalLoad = alltracts.size();
		for (Tract instance : alltracts) {
			index++;
			GeoR each = new GeoR();
			each.id = instance.getTractId();
			each.Name = instance.getLongname();
			each.waterArea = String
					.valueOf(Math.round(instance.getWaterarea() / 2.58999e4) / 100.0);
			each.landArea = String
					.valueOf(Math.round(instance.getLandarea() / 2.58999e4) / 100.0);
			each.population = String.valueOf(instance.getPopulation());
			each.RoutesCount = String.valueOf(0);
			each.AverageFare = "0";
			each.MedianFare = "0";
			each.StopsCount = String.valueOf(0);
			try {
				each.StopsCount = String.valueOf(EventManager
						.getstopscountbytract(instance.getTractId(),
								selectedAgencies, dbindex));
			} catch (FactoryException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (TransformException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			each.RoutesCount = String.valueOf(0);
			each.AgenciesCount = String.valueOf(0);
			try {
				each.AgenciesCount = String.valueOf(EventManager
						.getAgencyCountByArea(instance.getTractId(), 1,
								selectedAgencies, dbindex));
				each.RoutesCount = String.valueOf(EventManager
						.getroutescountsbytract(instance.getTractId(),
								selectedAgencies, dbindex));
			} catch (FactoryException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (TransformException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			response.GeoR.add(each);
			setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		progVal.remove(key);
		return response;
	}

	/**
	 * Generates The Census Places Summary report
	 * @param key - unique key to track and update progress
	 * @param dbindex
	 * @param username
	 * @return GeoRList
	 * @throws JSONException
	 */
	@Path("/GeoCPSR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getGCPSR(@QueryParam("key") double key,
			@QueryParam("dbindex") Integer dbindex,
			@QueryParam("username") String username) throws JSONException {
		if (dbindex == null || dbindex < 0 || dbindex > dbsize - 1) {
			dbindex = default_dbindex;
		}
		List<Place> allplaces = new ArrayList<Place>();
		List<String> selectedAgencies = DbUpdate.getSelectedAgencies(username);
		try {
			allplaces = EventManager.getplaces(dbindex);
		} catch (FactoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (TransformException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		GeoRList response = new GeoRList();		
		response.type = "Place";
		int index = 0;
		int totalLoad = allplaces.size();
		for (Place instance : allplaces) {
			index++;
			GeoR each = new GeoR();
			each.Name = instance.getName();
			each.id = instance.getPlaceId();
			each.waterArea = String
					.valueOf(Math.round(instance.getWaterarea() / 2.58999e4) / 100.0);
			each.landArea = String
					.valueOf(Math.round(instance.getLandarea() / 2.58999e4) / 100.0);
			each.population = String.valueOf(instance.getPopulation());
			each.RoutesCount = String.valueOf(0);
			each.AverageFare = "0";
			each.MedianFare = "0";
			each.StopsCount = String.valueOf(0);
			try {
				each.StopsCount = String.valueOf(EventManager
						.getstopscountbyplace(instance.getPlaceId(),
								selectedAgencies, dbindex));
			} catch (FactoryException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (TransformException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			each.RoutesCount = String.valueOf(0);
			each.AgenciesCount = String.valueOf(0);
			try {
				each.AgenciesCount = String.valueOf(EventManager
						.getAgencyCountByArea(instance.getPlaceId(), 2,
								selectedAgencies, dbindex));
				each.RoutesCount = String.valueOf(EventManager
						.getroutescountsbyplace(instance.getPlaceId(),
								selectedAgencies, dbindex));
			} catch (FactoryException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (TransformException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			response.GeoR.add(each);
			setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		progVal.remove(key);
		return response;
	}

	/**
	 * Generates The Aggregated urban/rural Summary report
	 * 
	 * @param upop - minimum population of urban areas to report on
	 * @param key - unique key to track and update progress
	 * @param type - type of the geographical area to filter service
	 * @param dbindex
	 * @param popYear - population projection year
	 * @param username
	 * @param popmax - maximum population of urban areas to report on
	 * @param popmin - minimum population of urban areas to report on
	 * @return GeoRList
	 * @throws JSONException
	 */
	@GET
	@Path("/GeoURSR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getGURSR(@QueryParam("pop") Integer upop,
			@QueryParam("key") double key, @QueryParam("type") String type,
			@QueryParam("dbindex") Integer dbindex,
			@QueryParam("popYear") String popYear,
			@QueryParam("username") String username,
			@QueryParam("popMax") Integer popmax
			,@QueryParam("popMin") Integer popmin) throws JSONException {
		if (dbindex == null || dbindex < 0 || dbindex > dbsize - 1) {
			dbindex = default_dbindex;
		}
		if (upop == null || upop <= 0) {
			upop = 50000;
		}
		if (popYear == null || popYear.equals("null")) {
			popYear = "2010";
		}
		List<Urban> allurbanareas = new ArrayList<Urban>();
		List<String> selectedAgencies = DbUpdate.getSelectedAgencies(username);
		try {
			allurbanareas = EventManager.geturbansbypopbet(popmin,popmax,dbindex, popYear);
		} catch (FactoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (TransformException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		GeoRList response = new GeoRList();
		response.type = "UrbanArea";
		int index = 0;
		int totalLoad = allurbanareas.size();
		GeoR each = new GeoR();
		each.Name = "Oregon Urban Areas with population between "+ String.valueOf(popmin)+" and "+ String.valueOf(popmax);
		each.UrbansCount = String.valueOf(allurbanareas.size());
		each.id = "00001";
		long landarea = 0;
		long waterarea = 0;
		long population = 0;
		int stopscount = 0;
		List<String> routeL = new ArrayList<String>();
		for (Urban instance : allurbanareas) {
			index++;
			landarea += instance.getLandarea();
			waterarea += instance.getWaterarea();
			population += instance.getPopulation();
			try {
				List<GeoStopRouteMap> routesL = EventManager.getroutesbyurban(
						instance.getUrbanId(), selectedAgencies, dbindex);
				for (int x = 0; x < routesL.size(); x++) {
					String routeID = routesL.get(x).getrouteId()
							+ routesL.get(x).getagencyId();
					if (!routeL.contains(routeID)) {
						routeL.add(routeID);
					}
				}
			} catch (FactoryException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (TransformException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			int stopscnt = 0;
			try {
				stopscnt = (int) EventManager.getstopscountbyurban(
						instance.getUrbanId(), selectedAgencies, dbindex);
			} catch (FactoryException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (TransformException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			stopscount += stopscnt;
			setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		progVal.remove(key);
		each.waterArea = String
				.valueOf(Math.round(waterarea / 2.58999e4) / 100.0);
		each.landArea = String
				.valueOf(Math.round(landarea / 2.58999e4) / 100.0);
		each.population = String.valueOf(population);
		// each.RoutesCount = String.valueOf(routescount);
		each.RoutesCount = String.valueOf(routeL.size());
		each.StopsCount = String.valueOf(stopscount);
		response.GeoR.add(each);
		return response;
	}

	/**
	 * Generates The Aggregated urban/rural area extended report
	 * 
	 * @param date - selected dates to generate report for
	 * @param x - population search radius
	 * @param L - minimum level of service
	 * @param key - unique key to track and update progress
	 * @param dbindex - database index
	 * @param popYear - population projection year
	 * @param username - user session
	 * @param popmax - maximum population of urban areas to report on
	 * @param popmin - minimum population of urban areas to report on
	 * @return GeoXR
	 * @throws JSONException
	 */
	@GET
	@Path("/UrbanrXR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getGURXR(
			@QueryParam("day") String date, 
			@QueryParam("x") double x,
			@QueryParam("l") Integer L, 
			@QueryParam("key") double key,
			@QueryParam("dbindex") Integer dbindex,
			@QueryParam("popYear") String popYear,
			@QueryParam("username") String username,
			@QueryParam("popMax") Integer popmax, 
			@QueryParam("popMin") Integer popmin
			) throws JSONException {
		if (Double.isNaN(x) || x <= 0) {
			x = STOP_SEARCH_RADIUS;
		}
		if (dbindex == null || dbindex < 0 || dbindex > dbsize - 1) {
			dbindex = default_dbindex;
		}
		if (L == null || L < 0) {
			L = LEVEL_OF_SERVICE;
		}
		if (popYear == null || popYear.equals("null")) {
			popYear = "2010";
		}
		String[] dates = date.split(",");
		String[][] datedays = daysOfWeekString(dates);
		String[] fulldates = fulldate(dates);
		String[] sdates = datedays[0];
		String[] days = datedays[1];
		int totalLoad = 10;
		int index = 0;
		setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		GeoXR response = new GeoXR();
		List<Urban> urbans = new ArrayList<Urban>();
		try {
			urbans = EventManager.geturbansbypopbet(popmin,popmax,dbindex, popYear);
		} catch (FactoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (TransformException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
    	long population = 0;
    	long LandArea = 0;
    	for (Urban inst: urbans){
    		population += inst.getPopulation();
    		LandArea +=inst.getLandarea();
    	}
    	index++;
		setprogVal(key, (int) Math.round(index*100/totalLoad));
    	x = x * 1609.34;    	
		response.AreaId = "01";		
		response.AreaName = "Urban Areas with population between "+String.valueOf(popmin)+" and "+String.valueOf(popmax);		
		HashMap<String, Float> FareData =PgisEventManager.AUrbansFareInfo(sdates, days, popmin,popmax, username, dbindex, popYear);
		response.MinFare = String.valueOf(FareData.get("minfare"));
		response.AverageFare = String.valueOf(FareData.get("averagefare"));
		response.MaxFare = String.valueOf(FareData.get("maxfare"));
		response.MedianFare = String.valueOf(FareData.get("medianfare"));	
		index ++;
		setprogVal(key, (int) Math.round(index*100/totalLoad));		
		float RouteMiles = PgisEventManager.AUrbansRouteMiles(popmin, popmax , username, dbindex, popYear);
		index++;
		setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		response.RouteMiles = String.valueOf(RouteMiles);
		index ++;
		setprogVal(key, (int) Math.round(index*100/totalLoad));
		
		long[] stopspop= PgisEventManager.AUrbansstopsPop(popmin,popmax, username, x, dbindex, popYear);
		index ++;
		setprogVal(key, (int) Math.round(index*100/totalLoad));
		response.StopsPersqMile = String.valueOf(Math.round(stopspop[0]*25899752356.00/LandArea)/10000.00);
		response.PopWithinX = String.valueOf(stopspop[1]+stopspop[2]);
		response.UPopWithinX = String.valueOf(stopspop[1]);
		response.PopServed = String.valueOf(Math.round((10000.00*(stopspop[1])/population))/100.00);
		response.UPopServed = String.valueOf(Math.round((10000.00*(stopspop[1])/population))/100.00);	
		response.PopUnServed = String.valueOf(Math.round(1E4-((10000.00*(stopspop[1])/population)))/100.0);
		HashMap<String, String> servicemetrics = PgisEventManager.UAreasServiceMetrics(sdates, days, fulldates, popmin,popmax, username, L, x, dbindex, popYear);
		index +=6;
		setprogVal(key, (int) Math.round(index*100/totalLoad));
		double ServiceMiles = Float.parseFloat(servicemetrics.get("svcmiles"));
		float svcPop = Float.parseFloat(servicemetrics.get("uspop"));
		response.ServiceMiles = servicemetrics.get("svcmiles");
		response.ServiceHours = servicemetrics.get("svchours");
		response.ServiceStops = servicemetrics.get("svcstops");
		response.PopServedAtLoService = String
				.valueOf(Math.round(10000.0
						* Long.parseLong(servicemetrics.get("upopatlos"))
						/ population) / 100.0);
		response.UPopServedAtLoService = String.valueOf(Long
				.parseLong(servicemetrics.get("upopatlos")));

		String serviceDays = servicemetrics.get("svcdays");
		if (serviceDays.length() > 2) {
			serviceDays = serviceDays.replace("\"", "");
			serviceDays = serviceDays.substring(1, serviceDays.length() - 1);
			String[] svcdays = serviceDays.split(",");
			serviceDays = StringUtils.join(Arrays.asList(svcdays), ";");
		}
		response.ServiceDays = serviceDays;
		response.MilesofServicePerCapita = (population > 0) ? String
				.valueOf(Math.round((ServiceMiles * 10000.00) / population) / 10000.00)
				: "NA";
		response.StopPerServiceMile = (ServiceMiles > 0.01) ? String
				.valueOf(Math.round((stopspop[0] * 100)
						/ Float.parseFloat(servicemetrics.get("svcmiles"))) / 100.0)
				: "NA";
		response.ServiceMilesPersqMile = (LandArea > 0.01) ? String
				.valueOf(Math.round((ServiceMiles * 258999752.356) / LandArea) / 10000.00)
				: "NA";
		int HOSstart = Integer.parseInt(servicemetrics.get("fromtime"));
		int HOSend = Integer.parseInt(servicemetrics.get("totime"));
		response.HoursOfService = ((HOSstart == -1) ? "NA" : StringUtils
				.timefromint(HOSstart))
				+ "-"
				+ ((HOSend == -1) ? "NA" : StringUtils.timefromint(HOSend));
		String connections = servicemetrics.get("connections") + "";
		if (connections.length() > 2) {
			connections = connections.replace("\"", "");
			connections = connections.substring(1, connections.length() - 1);
			String[] conns = connections.split(",");
			connections = StringUtils.join(Arrays.asList(conns), " ;");
		}
		response.ConnectedCommunities = connections;
		response.PopServedByService = String.valueOf(svcPop);
		response.UPopServedByService = String.valueOf(Float
				.parseFloat(servicemetrics.get("uspop")));
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		progVal.remove(key);
		return response;
	}

	/**
	 * Generates list of stops for a given agency. Used to generated Connected
	 * Agencies On-map Report.
	 * 
	 * @param agencyId
	 * @param dbindex
	 * @param username
	 * @return
	 * @throws JSONException
	 */
	@GET
	@Path("/agenStops")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getAgenStops(@QueryParam("agency") String agencyId,
			@QueryParam("dbindex") Integer dbindex,
			@QueryParam("username") String username) throws JSONException {
		if (dbindex == null || dbindex < 0 || dbindex > dbsize - 1) {
			dbindex = default_dbindex;
		}
		CAStopsList response = new CAStopsList();
		response = PgisEventManager.getAgenStops(agencyId, dbindex);
		return response;
	}

	/**
	 * Generates The Congressional Districts Summary report
	 * @param key - unique key to track and update progress
	 * @param type - type of the geographical area to intersect with congressional districts
	 * @param dbindex
	 * @param username
	 * @return response
	 * @throws JSONException
	 */
	@GET
	@Path("/GeoCDSR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getGCDSR(@QueryParam("key") double key,
			@QueryParam("type") String type,
			@QueryParam("dbindex") Integer dbindex,
			@QueryParam("username") String username) throws JSONException {
		if (dbindex == null || dbindex < 0 || dbindex > dbsize - 1) {
			dbindex = default_dbindex;
		}
		List<CongDist> allcongdists = new ArrayList<CongDist>();
		List<String> selectedAgencies = DbUpdate.getSelectedAgencies(username);
		try {
			allcongdists = EventManager.getcongdist(dbindex);
		} catch (FactoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (TransformException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		GeoRList response = new GeoRList();
		response.type = "CongressionalDistrict";
		int index = 0;
		int totalLoad = allcongdists.size();
		for (CongDist instance : allcongdists) {
			index++;
			GeoR each = new GeoR();
			each.Name = instance.getName();
			each.id = instance.getCongdistId();
			each.waterArea = String
					.valueOf(Math.round(instance.getWaterarea() / 2.58999e4) / 100.0);
			each.landArea = String
					.valueOf(Math.round(instance.getLandarea() / 2.58999e4) / 100.0);
			each.population = String.valueOf(instance.getPopulation());
			each.RoutesCount = String.valueOf(0);
			each.AgenciesCount = String.valueOf(0);
			try {
				each.AgenciesCount = String.valueOf(EventManager
						.getAgencyCountByArea(instance.getCongdistId(), 5,
								selectedAgencies, dbindex));
				each.RoutesCount = String.valueOf(EventManager
						.getroutescountbycongdist(instance.getCongdistId(),
								selectedAgencies, dbindex));
			} catch (FactoryException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (TransformException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			each.StopsCount = String.valueOf(0);
			try {
				each.StopsCount = String.valueOf(EventManager
						.getstopscountbycongdist(instance.getCongdistId(),
								selectedAgencies, dbindex));
			} catch (FactoryException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (TransformException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			each.AverageFare = "0";
			each.MedianFare = "0";
			response.GeoR.add(each);
			setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		progVal.remove(key);
		return response;
	}

	/**
	 * Generates ODOT Regions Summary report
	 * 
	 * @param key
	 * @param type
	 * @param dbindex
	 * @param username
	 * @return
	 * @throws JSONException
	 */
	@GET
	@Path("/GeoORSR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getGORSR(@QueryParam("key") double key,
			@QueryParam("dbindex") Integer dbindex,
			@QueryParam("username") String username) throws JSONException {
		if (dbindex == null || dbindex < 0 || dbindex > dbsize - 1) {
			dbindex = default_dbindex;
		}
		List<County> allcounties = new ArrayList<County>();
		List<String> selectedAgencies = DbUpdate.getSelectedAgencies(username);
		try {
			allcounties = EventManager.getcounties(dbindex);
		} catch (FactoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (TransformException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		GeoRList response = new GeoRList();
		response.type = "ODOT Region";		
		int index = 0;
		int totalLoad = allcounties.size();
		String regionId = "";
		double waterArea = 0;
		double landArea = 0;
		long population = 0;
		long countiesCount = 0;
		String regionName = "";
		boolean notfirst = false;
		for (County instance : allcounties) {

			if (!(regionId.equals(instance.getRegionId()))) {
				if (notfirst) {
					GeoR each = new GeoR();
					each.id = regionId;
					each.Name = regionName;
					each.landArea = String.valueOf(Math
							.round(landArea / 2.58999e4) / 100.0);
					each.waterArea = String.valueOf(Math
							.round(waterArea / 2.58999e4) / 100.0);
					each.population = String.valueOf(population);
					each.CountiesCount = String.valueOf(countiesCount);
					each.AverageFare = "0";
					each.MedianFare = "0";
					each.StopsCount = String.valueOf(0);
					try {
						each.StopsCount = String.valueOf(EventManager
								.getstopscountbyregion(regionId,
										selectedAgencies, dbindex));
					} catch (FactoryException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (TransformException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					each.RoutesCount = String.valueOf(0);
					each.AgenciesCount = String.valueOf(0);
					try {
						each.AgenciesCount = String.valueOf(EventManager
								.getAgencyCountByArea(regionId, 4,
										selectedAgencies, dbindex));
						each.RoutesCount = String.valueOf(EventManager
								.getroutescountsbyregion(regionId,
										selectedAgencies, dbindex));
					} catch (FactoryException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (TransformException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					response.GeoR.add(each);
				} else {
					notfirst = true;
				}
				regionId = instance.getRegionId();
				regionName = instance.getRegionName();
				waterArea = instance.getWaterarea();
				landArea = instance.getLandarea();
				population = instance.getPopulation();
				countiesCount = 1;
			} else {
				waterArea += instance.getWaterarea();
				landArea += instance.getLandarea();
				population += instance.getPopulation();
				countiesCount++;
				index++;
			}
			setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		}
		GeoR each = new GeoR();
		each.id = regionId;
		each.Name = regionName;
		each.landArea = String
				.valueOf(Math.round(landArea / 2.58999e4) / 100.0);
		each.waterArea = String
				.valueOf(Math.round(waterArea / 2.58999e4) / 100.0);
		each.population = String.valueOf(population);
		each.CountiesCount = String.valueOf(countiesCount);
		each.AverageFare = "0";
		each.MedianFare = "0";
		each.StopsCount = String.valueOf(0);
		try {
			each.StopsCount = String.valueOf(EventManager
					.getstopscountbyregion(regionId, dbindex));
		} catch (FactoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (TransformException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		each.RoutesCount = String.valueOf(0);
		each.AgenciesCount = String.valueOf(0);
		try {
			each.AgenciesCount = String.valueOf(EventManager
					.getAgencyCountByArea(regionId, 4, selectedAgencies,
							dbindex));
			each.RoutesCount = String.valueOf(EventManager
					.getroutescountsbyregion(regionId, dbindex));
		} catch (FactoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (TransformException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		response.GeoR.add(each);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		progVal.remove(key);
		return response;
	}

	/**
	 * Generates The connected agencies summary report
	 * @param gap - search radius
	 * @param key - unique key to track and update progress
	 * @param dbindex
	 * @param username
	 * @return ClusterRList
	 * @throws JSONException
	 */
	@GET
	@Path("/ConAgenSR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getGURSRd(@QueryParam("gap") double gap,
			@QueryParam("key") double key,
			@QueryParam("dbindex") Integer dbindex,
			@QueryParam("username") String username) throws JSONException {
		if (dbindex == null || dbindex < 0 || dbindex > dbsize - 1) {
			dbindex = default_dbindex;
		}
		if (gap <= 0) {
			gap = 0.1;
		}
		ClusterRList response = new ClusterRList();
		gap = gap * 1609.34;
		response.type = "AgencyGapReport";
		List<agencyCluster> results = new ArrayList<agencyCluster>();
		results = PgisEventManager.agencyCluster(gap, username, dbindex);
		int totalLoad = results.size();
		int index = 0;
		for (agencyCluster acl : results) {
			index++;
			ClusterR instance = new ClusterR();
			instance.id = acl.getAgencyId();
			instance.name = acl.getAgencyName();
			instance.size = String.valueOf(acl.getClusterSize());
			instance.ids = StringUtils.join(acl.getAgencyIds(), ";");
			instance.names = StringUtils.join(acl.getAgencyNames(), ";");
			// instance.distances = StringUtils.join(acl.getMinGaps(), ";");
			response.ClusterR.add(instance);
			setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		progVal.remove(key);
		return response;

	}

	/**
	 * generates connected agencies extended report. agencies that have at least
	 * one pair of stops within x-mile radius of each other are considered connected.
	 * 
	 * @param agencyId
	 * @param gap - search radius (maximum distance that is considered a connection)
	 * @param key - unique key to track and update progress
	 * @param dbindex
	 * @param username
	 * @return ClusterRList
	 * @throws JSONException
	 */
	@GET
	@Path("/ConAgenXR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getGURXRd(
			@QueryParam("agency") String agencyId,
			@QueryParam("gap") double gap, 
			@QueryParam("key") double key,
			@QueryParam("dbindex") Integer dbindex,
			@QueryParam("username") String username
			) throws JSONException {
		if (dbindex == null || dbindex < 0 || dbindex > dbsize - 1) {
			dbindex = default_dbindex;
		}
		if (gap <= 0) {
			gap = 0.1;
		}
		ClusterRList response = new ClusterRList();
		response.type = "ExtendedGapReport";
		response.agency = GtfsHibernateReaderExampleMain.QueryAgencybyid(
				agencyId, dbindex).getName();
		gap = gap * 1609.34;
		List<agencyCluster> results = new ArrayList<agencyCluster>();
		results = PgisEventManager.agencyClusterDetails(gap, agencyId,
				username, dbindex);
		int totalLoad = results.size();
		int index = 0;
		for (agencyCluster acl : results) {
			index++;
			ClusterR instance = new ClusterR();
			instance.id = acl.getAgencyId();
			instance.name = acl.getAgencyName();
			instance.size = String.valueOf(acl.getClusterSize());
			instance.minGap = String.valueOf(acl.getMinGap());
			instance.maxGap = String.valueOf(acl.getMaxGap());
			instance.meanGap = String.valueOf(acl.getMeanGap());
			for (int i = 0; i < acl.getClusterSize(); i++) {
				ClusterR inst = new ClusterR();
//				inst.name = acl.sourceStopNames.get(i);
				inst.names = acl.destStopNames.get(i);
				inst.scoords = acl.sourceStopCoords.get(i);
				inst.dcoords = acl.destStopCoords.get(i);
				inst.minGap = acl.minGaps.get(i);
				instance.connections.add(inst);
			}
			response.ClusterR.add(instance);
			setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		progVal.remove(key);
		return response;

	}

	/**
	 * Generates The connected networks summary report
	 * @param gap - search radius (maximum distance that is considered a connection)
	 * @param key - unique key to track and update progress
	 * @param dbindex
	 * @param username
	 * @return ClusterRList
	 * @throws JSONException
	 */
	@GET
	@Path("/ConNetSR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getCTNSR(@QueryParam("gap") double gap,
			@QueryParam("key") double key,
			@QueryParam("dbindex") Integer dbindex,
			@QueryParam("username") String username) throws JSONException {
		if (dbindex == null || dbindex < 0 || dbindex > dbsize - 1) 
			dbindex = default_dbindex;		
		if (gap <= 0) 
			gap = 0.1;
		ClusterRList response = new ClusterRList();
		gap = gap * 1609.34;
		List<agencyCluster> agencies = new ArrayList<agencyCluster>();
		agencies = PgisEventManager.agencyCluster(gap, username, dbindex);
		int totalLoad = agencies.size();
		int index = 1;
		List<NetworkCluster> res = new ArrayList<NetworkCluster>();
		boolean changed = false;
		NetworkCluster current = new NetworkCluster();
		agencyCluster buffer = new agencyCluster();
		current.clusterId = index;
		boolean added = false;
		int clsize = totalLoad;
		while (clsize > 0) {
			changed = false;
			Iterator<agencyCluster> iterator = agencies.iterator();
			while (iterator.hasNext()) {
				added = false;
				buffer = iterator.next();
				added = added || current.addAgencyCluster(buffer);
				if (added) {
					iterator.remove();
					clsize--;
				}
				changed = changed || added;
			}
			if (!changed) {
				index++;
				res.add(current);
				current = new NetworkCluster();
				current.clusterId = index;
			}
			setprogVal(key,
					(int) Math.round((totalLoad - clsize) * 100 / totalLoad));
		}
		if (current.clusterSize > 0)
			res.add(current);
		for (NetworkCluster ncl : res) {
			ClusterR instance = new ClusterR();
			instance.id = String.valueOf(ncl.clusterId);
			instance.ids = StringUtils.join(ncl.getAgencyIds(), ";");
			instance.names = StringUtils.join(ncl.getAgencyNames(), ";");
			instance.size = String.valueOf(ncl.agencyIds.size());
			response.ClusterR.add(instance);
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		progVal.remove(key);
		return response;
	}

	/**
	 * Generates The Summary State-wide report
	 * 
	 * @param key - unique key to track and update progress
	 * @param dbindex
	 * @param popYear - population projection year
	 * @param username
	 * @return GeoRList
	 * @throws JSONException
	 * @throws FactoryException
	 * @throws TransformException
	 */
	@GET
	@Path("/stateSR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getStateSR(@QueryParam("key") double key,
			@QueryParam("dbindex") Integer dbindex,
			@QueryParam("popYear") String popYear,
			@QueryParam("username") String username) throws JSONException,
			FactoryException, TransformException {
		if (dbindex == null || dbindex < 0 || dbindex > dbsize - 1) 
			dbindex = default_dbindex;		
		if (popYear == null || popYear.equals("null")) 
			popYear = "2010";
		int totalLoad = 2;
		int index = 0;
		setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		GeoRList response = new GeoRList();		
		response.type = "StatewideReport";
		HashMap<String, Long> geocounts = new HashMap<String, Long>();
		geocounts = PgisEventManager.getStateInfo(dbindex, username, popYear);
		GeoR each = new GeoR();
		each.id = "41";
		each.Name = "Oregon";
		each.CountiesCount = String.valueOf(geocounts.get("county"));
		each.TractsCount = String.valueOf(geocounts.get("tract"));
		each.PlacesCount = String.valueOf(geocounts.get("place"));
		each.UrbanizedAreasCount = String.valueOf(geocounts.get("urbanized_area"));
		each.UrbanClustersCount= String.valueOf(geocounts.get("urban_cluster"));
		each.RegionsCount = String.valueOf(geocounts.get("region"));
		each.CongDistsCount = String.valueOf(geocounts.get("congdist"));
		each.population = String.valueOf(geocounts.get("pop"));
		each.landArea = String.valueOf(Math.round(geocounts.get("landarea") / 2.58999e4) / 100.0);
		each.urbanpop = String.valueOf(geocounts.get("urbanpop"));
		each.ruralpop = String.valueOf(geocounts.get("ruralpop"));
		each.employment = String.valueOf(geocounts.get("rac"));
		each.employees = String.valueOf(geocounts.get("wac"));
		each.StopsCount = String.valueOf(geocounts.get("stop"));
		each.RoutesCount = String.valueOf(geocounts.get("route"));
		each.AgenciesCount = String.valueOf(geocounts.get("agency"));
		index++;		
		response.GeoR.add(each);
		index++;
		setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		progVal.remove(key);
		return response;
	}

	/**
	 * Generates State-wide Extended Report
	 * @param date
	 * @param x - population search radius
	 * @param popYear - population projection year
	 * @param L - minimum level of service 
	 * @param key - unique key to track and update progress
	 * @param dbindex
	 * @param username
	 * @return GeoXR
	 * @throws JSONException
	 * @throws SQLException
	 */
	@GET
	@Path("/stateXR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getStateXR(@QueryParam("day") String date,
			@QueryParam("x") double x, @QueryParam("popYear") String popYear,
			@QueryParam("l") Integer L, @QueryParam("key") double key,
			@QueryParam("dbindex") Integer dbindex,
			@QueryParam("username") String username) throws JSONException,
			SQLException {
		if (Double.isNaN(x) || x <= 0) 
			x = STOP_SEARCH_RADIUS;
		
		if (dbindex == null || dbindex < 0 || dbindex > dbsize - 1) 
			dbindex = default_dbindex;
		
		if (L == null || L < 0) 
			L = LEVEL_OF_SERVICE;
		
		if (popYear == null || popYear.equals("null"))
			popYear = "2010";
		
		List<String> selectedAgencies = DbUpdate.getSelectedAgencies(username);
		String[] dates = date.split(",");
		String[][] datedays = daysOfWeekString(dates);
		String[] fulldates = fulldate(dates);
		String[] sdates = datedays[0]; // date in YYYYMMDD format
		String[] days = datedays[1]; // day of week string (all lower case)

		GeoXR response = new GeoXR();
		x = x * 1609.34;
		int totalLoad = 10;
		int index = 0;
		setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		response.AreaName = "Oregon";
		HashMap<String, Float> FareData = new HashMap<String, Float>();
		FareData = GtfsHibernateReaderExampleMain.QueryFareData(
				selectedAgencies, dbindex);
		index++;
		setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		response.MinFare = String.valueOf(FareData.get("min"));
		response.AverageFare = String.valueOf(FareData.get("avg"));
		response.MaxFare = String.valueOf(FareData.get("max"));
		int FareCount = FareData.get("count").intValue();
		float FareMedian = SpatialEventManager.getFareMedianForState(
				selectedAgencies, FareCount, dbindex);
		index++;
		setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		response.MedianFare = String.valueOf(FareMedian);
		Double RouteMiles = 0.00;
		RouteMiles = PgisEventManager.StateRouteMiles(username, dbindex);
		index++;
		setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		response.RouteMiles = String
				.valueOf(Math.round(RouteMiles * 100.00) / 100.00);
		Long StopsCount = GtfsHibernateReaderExampleMain.QueryStopsCount(
				selectedAgencies, dbindex);
		index++;
		setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		HashMap<String, Long> geocounts = new HashMap<String, Long>();
		geocounts = PgisEventManager.getStateInfo(dbindex, username, popYear);
		response.StopsPersqMile = String.valueOf(Math.round(StopsCount
				* 25899752356.00 / geocounts.get("landarea")) / 10000.00);
		index += 5;
		setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		HashMap<String, String> serviceMetrics = PgisEventManager
				.StatewideServiceMetrics(sdates, days, fulldates, username, L,
						x, dbindex, popYear);
		double ServiceMiles = Float.parseFloat(serviceMetrics.get("svcmiles"));
		long PopatLOS = (Long.parseLong(serviceMetrics.get("upopatlos")) + Long
				.parseLong(serviceMetrics.get("rpopatlos")));
		float svcPop = (Float.parseFloat(serviceMetrics.get("uspop")) + Float
				.parseFloat(serviceMetrics.get("rspop")));
		response.ServiceMiles = serviceMetrics.get("svcmiles");
		response.ServiceHours = serviceMetrics.get("svchours");
		response.ServiceStops = serviceMetrics.get("svcstops");
		response.PopServedAtLoService = String.valueOf(Math.round(10000.0
				* PopatLOS / geocounts.get("pop")) / 100.0);
		response.RacAtLoService = String.valueOf(Math.round(10000.0
				* (Long.parseLong(serviceMetrics.get("racatlos")))
				/ geocounts.get("rac")) / 100.0);
		response.WacAtLoService = String.valueOf(Math.round(10000.0
				* (Long.parseLong(serviceMetrics.get("wacatlos")))
				/ geocounts.get("wac")) / 100.0);
		response.PopServedByService = String.valueOf(svcPop);
		response.racServedByService = String.valueOf("srac");
		response.wacServedByService = String.valueOf("swac");

		String serviceDays = serviceMetrics.get("svcdays");
		if (serviceDays.length() > 2) {
			serviceDays = serviceDays.replace("\"", "");
			serviceDays = serviceDays.substring(1, serviceDays.length() - 1);
			String[] svcdays = serviceDays.split(",");
			serviceDays = StringUtils.join(Arrays.asList(svcdays), ";");
		}
		response.ServiceDays = serviceDays;
		response.MilesofServicePerCapita = (geocounts.get("pop") > 0) ? String
				.valueOf(Math.round((ServiceMiles * 10000.00)
						/ geocounts.get("pop")) / 10000.00) : "NA";
		response.StopPerServiceMile = (ServiceMiles > 0.01) ? String
				.valueOf(Math.round((StopsCount * 100)
						/ Float.parseFloat(serviceMetrics.get("svcmiles"))) / 100.0)
				: "NA";
		response.ServiceMilesPersqMile = (geocounts.get("landarea") > 0.01) ? String
				.valueOf(Math.round((ServiceMiles * 258999752.356)
						/ geocounts.get("landarea")) / 10000.00) : "NA";
		int HOSstart = Integer.parseInt(serviceMetrics.get("fromtime"));
		int HOSend = Integer.parseInt(serviceMetrics.get("totime"));
		response.HoursOfService = ((HOSstart == -1) ? "NA" : StringUtils
				.timefromint(HOSstart))
				+ "-"
				+ ((HOSend == -1) ? "NA" : StringUtils.timefromint(HOSend));

		long[] PopWithinX = PgisEventManager.PopWithinX(x, username, dbindex,
				popYear);
		index++;
		setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		response.PopWithinX = String.valueOf(PopWithinX[0]);
		response.racWithinX = String.valueOf(PopWithinX[1]);
		response.wacWithinX = String.valueOf(PopWithinX[2]);

		response.PopUnServed = String
				.valueOf(Math
						.round(1E4 - ((10000.00 * PopWithinX[0] / geocounts
								.get("pop")))) / 100.0);
		response.racUnServed = String
				.valueOf(Math
						.round(1E4 - ((10000.00 * PopWithinX[1] / geocounts
								.get("rac")))) / 100.0);
		response.wacUnServed = String
				.valueOf(Math
						.round(1E4 - ((10000.00 * PopWithinX[2] / geocounts
								.get("wac")))) / 100.0);

		response.PopServed = String
				.valueOf(Math.round((10000.00 * PopWithinX[0] / geocounts
						.get("pop"))) / 100.00);
		response.racServed = String
				.valueOf(Math.round((10000.00 * PopWithinX[0] / geocounts
						.get("rac"))) / 100.00);
		response.wacServed = String
				.valueOf(Math.round((10000.00 * PopWithinX[0] / geocounts
						.get("wac"))) / 100.00);

		long[] pop50kCutOff = PgisEventManager.cutOff50(x, username, dbindex,
				popYear);
		response.PopServedOver50k = String.valueOf(pop50kCutOff[0]);
		response.TotalPopOver50k = String.valueOf(pop50kCutOff[1]);
		response.PopServedBelow50k = String.valueOf(pop50kCutOff[2]);
		response.TotalPopBelow50k = String.valueOf(pop50kCutOff[3]);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		progVal.remove(key);
		return response;

	}

	/**
	 * Generates geographic area Extended reports types: 0=counties, 1=census
	 * tracts, 2=census places, 3=Urban Areas, 4=ODOT Regions, 5=Congressional
	 * districts
	 * 
	 * @param areaId - ID of the geographical area
	 * @param type - type of the geographical area
	 * @param date
	 * @param x - population search radius
	 * @param L - minimum level of service
	 * @param key - unique key to track and update progress
	 * @param dbindex - database index
	 * @param popYear - population projection year
	 * @param username - user session
	 * @param geoid - ID of the geographical to be intersected with the original area
	 * @param geotype - type of the geographical to be intersected with the original area
	 * @return
	 * @throws JSONException
	 */
	@GET
	@Path("/geoAreaXR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getGeoXR(@QueryParam("areaid") String areaId,
			@QueryParam("type") int type, 
			@QueryParam("day") String date,
			@QueryParam("x") double x, 
			@QueryParam("l") Integer L,
			@QueryParam("key") double key,
			@QueryParam("dbindex") Integer dbindex,
			@QueryParam("popYear") String popYear,
			@QueryParam("username") String username,
			@QueryParam("geoid") String geoid,
			@QueryParam("geotype") Integer geotype) throws JSONException {
		long stopspop[] = new long[20];
		if (Double.isNaN(x) || x <= 0) {
			x = STOP_SEARCH_RADIUS;
		}
		if (dbindex == null || dbindex < 0 || dbindex > dbsize - 1) {
			dbindex = default_dbindex;
		}
		if (L == null || L < 0) {
			L = LEVEL_OF_SERVICE;
		}
		if (popYear == null || popYear.equals("null")) {
			popYear = "2010";
		}
		String[] dates = date.split(",");
		String[][] datedays = daysOfWeekString(dates);
		String[] fulldates = fulldate(dates);
		String[] sdates = datedays[0];
		String[] days = datedays[1];
		HashMap<String, String> servicemetrics = new HashMap<String, String>();
		GeoXR response = new GeoXR();
		GeoArea instance = PgisEventManager.QueryGeoAreabyId(type, areaId,
				dbindex, username, popYear, -1, null);
		x = x * 1609.34;
		int totalLoad = 6;
		int index = 0;
		setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		response.AreaId = areaId;
		response.AreaName = instance.getName();

		HashMap<String, Float> FareData = PgisEventManager.FareInfo(type,
				sdates, days, areaId, username, dbindex);
		response.MinFare = String.valueOf(FareData.get("minfare"));
		response.AverageFare = String.valueOf(FareData.get("averagefare"));
		response.MaxFare = String.valueOf(FareData.get("maxfare"));
		response.MedianFare = String.valueOf(FareData.get("medianfare"));

		index++;
		setprogVal(key, (int) Math.round(index * 100 / totalLoad));

		float RouteMiles = PgisEventManager.RouteMiles(type, areaId, username,
				dbindex);
		response.RouteMiles = String.valueOf(RouteMiles);
		index++;

		setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		if (geoid != null) {
			stopspop = PgisEventManager.stopsPop(type, areaId, username, x,
					dbindex, popYear, geoid, geotype);
		} else {
			stopspop = PgisEventManager.stopsPop(type, areaId, username, x,
					dbindex, popYear, null, -1);

		}

		index++;
		setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		response.StopsPersqMile = String.valueOf(Math.round(stopspop[0]
				* 25899752356.00 / instance.getLandarea()) / 10000.00);
		response.PopWithinX = String.valueOf(stopspop[1] + stopspop[2]);
		response.racWithinX = String.valueOf(stopspop[3] + stopspop[4]);
		response.wacWithinX = String.valueOf(stopspop[5] + stopspop[6]);
		response.UPopWithinX = String.valueOf(stopspop[1]);
		response.RPopWithinX = String.valueOf(stopspop[2]);
		response.UracWithinX = String.valueOf(stopspop[3]);
		response.RracWithinX = String.valueOf(stopspop[4]);
		response.UwacWithinX = String.valueOf(stopspop[5]);
		response.RwacWithinX = String.valueOf(stopspop[6]);
		response.PopServed = String.valueOf(Math
				.round((10000.00 * (stopspop[1] + stopspop[2]) / instance
						.getPopulation())) / 100.00);
		response.racServed = String.valueOf(Math
				.round((10000.00 * (stopspop[3] + stopspop[4]) / instance
						.getEmployment())) / 100.00);
		response.wacServed = String.valueOf(Math
				.round((10000.00 * (stopspop[5] + stopspop[6]) / instance
						.getEmployee())) / 100.00);
		response.UPopServed = String
				.valueOf(Math.round((10000.00 * (stopspop[1]) / instance
						.getPopulation())) / 100.00);
		response.RPopServed = String
				.valueOf(Math.round((10000.00 * (stopspop[2]) / instance
						.getPopulation())) / 100.00);
		response.UracServed = String
				.valueOf(Math.round((10000.00 * (stopspop[3]) / instance
						.getEmployment())) / 100.00);
		response.RracServed = String
				.valueOf(Math.round((10000.00 * (stopspop[4]) / instance
						.getEmployment())) / 100.00);
		response.UwacServed = String
				.valueOf(Math.round((10000.00 * (stopspop[5]) / instance
						.getEmployee())) / 100.00);
		response.RwacServed = String
				.valueOf(Math.round((10000.00 * (stopspop[6]) / instance
						.getEmployee())) / 100.00);
		response.PopUnServed = String
				.valueOf(Math
						.round(1E4 - ((10000.00 * (stopspop[1] + stopspop[2]) / instance
								.getPopulation()))) / 100.0);
		response.racUnServed = String
				.valueOf(Math
						.round(1E4 - ((10000.00 * (stopspop[3] + stopspop[4]) / instance
								.getEmployment()))) / 100.0);
		response.wacUnServed = String
				.valueOf(Math
						.round(1E4 - ((10000.00 * (stopspop[5] + stopspop[6]) / instance
								.getEmployee()))) / 100.0);
		if (geoid != null) {
			servicemetrics = PgisEventManager.ServiceMetrics(type, sdates,
					days, fulldates, areaId, username, L, x, dbindex, popYear,
					geotype, geoid);
		} else {
			servicemetrics = PgisEventManager.ServiceMetrics(type, sdates,
					days, fulldates, areaId, username, L, x, dbindex, popYear,
					-1, null);

		}
		index++;
		setprogVal(key, (int) Math.round(index * 100 / totalLoad));
		double ServiceMiles = Float.parseFloat(servicemetrics.get("svcmiles"));
		long PopatLOS = (Long.parseLong(servicemetrics.get("upopatlos")) + Long
				.parseLong(servicemetrics.get("rpopatlos")));
		long racatLOS = (Long.parseLong(servicemetrics.get("uracatlos")) + Long
				.parseLong(servicemetrics.get("rracatlos")));
		long wacatLOS = (Long.parseLong(servicemetrics.get("uwacatlos")) + Long
				.parseLong(servicemetrics.get("rwacatlos")));
		float svcPop = (Float.parseFloat(servicemetrics.get("uspop")) + Float
				.parseFloat(servicemetrics.get("rspop")));
		float svcrac = (Float.parseFloat(servicemetrics.get("usrac")) + Float
				.parseFloat(servicemetrics.get("rsrac")));
		float svcwac = (Float.parseFloat(servicemetrics.get("uswac")) + Float
				.parseFloat(servicemetrics.get("rswac")));
		response.totalracServedAtLoService = String.valueOf(racatLOS);
		response.totalwacServedAtLoService = String.valueOf(wacatLOS);

		response.ServiceMiles = (stopspop[0] != 0) ? servicemetrics
				.get("svcmiles") : "0";
		response.ServiceHours = servicemetrics.get("svchours");
		response.ServiceStops = servicemetrics.get("svcstops");
		response.UServiceStops = servicemetrics.get("usvcstops");
		response.RServiceStops = servicemetrics.get("rsvcstops");
		response.PopServedAtLoService = String.valueOf(Math.round(10000.0*PopatLOS/instance.getPopulation())/100.0);
		response.UPopServedAtLoService = String.valueOf(Long.parseLong(servicemetrics.get("upopatlos")));
		response.RPopServedAtLoService = String.valueOf(Long.parseLong(servicemetrics.get("rpopatlos")));
		response.racServedAtLoService = String.valueOf(Math.round(10000.0*racatLOS/instance.getEmployment())/100.0);
	   
		response.UracServedAtLoService = String.valueOf(Long.parseLong(servicemetrics.get("uracatlos")));
		response.RracServedAtLoService = String.valueOf(Long.parseLong(servicemetrics.get("rracatlos")));
		response.wacServedAtLoService = String.valueOf(Math.round(10000.0*wacatLOS/instance.getEmployee())/100.0);
	    response.UwacServedAtLoService = String.valueOf(Long.parseLong(servicemetrics.get("uwacatlos")));
		response.RwacServedAtLoService = String.valueOf(Long.parseLong(servicemetrics.get("rwacatlos")));	
		String serviceDays = servicemetrics.get("svcdays");
		if (serviceDays.length() > 2) {
			serviceDays = serviceDays.replace("\"", "");
			serviceDays = serviceDays.substring(1, serviceDays.length() - 1);
			String[] svcdays = serviceDays.split(",");
			serviceDays = StringUtils.join(Arrays.asList(svcdays), ";");
		}
		response.ServiceDays = serviceDays;
		response.MilesofServicePerCapita = (instance.getPopulation() > 0 && stopspop[0] != 0) ? String
				.valueOf(Math.round((ServiceMiles * 10000.00)
						/ instance.getPopulation()) / 10000.00) : "0";
		response.StopPerServiceMile = (ServiceMiles > 0.01) ? String
				.valueOf(Math.round((stopspop[0] * 100)
						/ Float.parseFloat(servicemetrics.get("svcmiles"))) / 100.0)
				: "0";
		response.ServiceMilesPersqMile = (instance.getLandarea() > 0.01 && stopspop[0] != 0) ? String
				.valueOf(Math.round((ServiceMiles * 258999752.356)
						/ instance.getLandarea()) / 10000.00) : "0";
		int HOSstart = Integer.parseInt(servicemetrics.get("fromtime"));
		int HOSend = Integer.parseInt(servicemetrics.get("totime"));
		response.HoursOfService = ((HOSstart == -1) ? "NA" : StringUtils
				.timefromint(HOSstart))
				+ "-"
				+ ((HOSend == -1) ? "NA" : StringUtils.timefromint(HOSend));
		
		response.ConnectedCommunities = servicemetrics.get("connections");
		response.PopServedByService = String.valueOf(svcPop);
		response.UPopServedByService = String.valueOf(Float
				.parseFloat(servicemetrics.get("uspop")));
		response.RPopServedByService = String.valueOf(Float
				.parseFloat(servicemetrics.get("rspop")));
		response.racServedByService = String.valueOf(svcrac);
		response.UracServedByService = String.valueOf(Float
				.parseFloat(servicemetrics.get("usrac")));
		response.RracServedByService = String.valueOf(Float
				.parseFloat(servicemetrics.get("rsrac")));
		response.wacServedByService = String.valueOf(svcwac);
		response.UwacServedByService = String.valueOf(Float
				.parseFloat(servicemetrics.get("uswac")));
		response.RwacServedByService = String.valueOf(Float
				.parseFloat(servicemetrics.get("rswac")));

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		progVal.remove(key);
		return response;

	}

	/**
	 * generates Transit Hubs Report
	 * 
	 * @param x1 - stops search radius
	 * @param x2 - population search radius
	 * @param x3 - Park and ride search radius
	 * @param popYear - population projection year
	 * @param key - unique key to track and update progress
	 * @param date
	 * @param dbindex
	 * @param username
	 * @return HubsClusterList
	 * @throws JSONException
	 * @throws SQLException
	 */
	@GET
	@Path("/hubs")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getHubs(
			@QueryParam("x1") double x1,
			@QueryParam("x2") double x2, 
			@QueryParam("popYear") String popYear,
			@QueryParam("x3") double x3, 
			@QueryParam("key") double key,
			@QueryParam("day") String date,
			@QueryParam("dbindex") Integer dbindex,
			@QueryParam("username") String username
			) throws JSONException,
			SQLException {
		if (popYear == null || popYear.equals("null")) {
			popYear = "2010";
		}
		HubsClusterList response = new HubsClusterList();
		String[] dates = date.split(",");
		String[][] datedays = daysOfWeekString(dates);
		String[] fulldates = datedays[0];
		String[] days = datedays[1];
		x1 *= 1609.34;
		x2 *= 1609.34;
		x3 *= 1609.34;
		
		// for each agency (key), any agencies that have at least one stops within x1 distances
		// its stops are listed (value). 
		HashMap<String, KeyClusterHashMap> y = PgisEventManager.getClusters(x1,
				dbindex, username);
		
		// recursively cluster the HashMap so that each agencies is present only 
		// in one of the entries. This way we have a HashMap with each entries 
		// representing a cluster of agencies that are connected.
		HashMap<String, KeyClusterHashMap> counter = new HashMap<String, KeyClusterHashMap>(
				y);
		for (Entry<String, KeyClusterHashMap> entry : counter.entrySet()) {
			if (y.containsKey(entry.getKey())) {
				HashSet<String> counter2 = new HashSet<String>(
						entry.getValue().values);
				for (String stopID : counter2) {
					if (!entry.getKey().equals(stopID)) {
						y = recurse(y, entry, stopID);
					}
				}
			}
		}

		// get detailed information on clusters
		response = getClusterData(y, fulldates, days, dbindex, x2, x3,
				username, key, popYear);
		return response;
	}

	/**
	 * 
	 * @param x1 - stops search radius
	 * @param x2 - population search radius
	 * @param x3 - Park and ride search radius
	 * @param popYear - population projection year
	 * @param key - unique key to track and update progress
	 * @param date
	 * @param dbindex
	 * @param username
	 * @return HubsClusterList
	 * @throws JSONException
	 * @throws SQLException
	 */
	@GET
	@Path("/keyHubs")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getKeyHubs(
			@QueryParam("x1") double x1,
			@QueryParam("x2") double x2, 
			@QueryParam("x3") double x3, 
			@QueryParam("popYear") String popYear,
			@QueryParam("key") double key,
			@QueryParam("day") String date,
			@QueryParam("dbindex") Integer dbindex,
			@QueryParam("username") String username
			) throws JSONException,
			SQLException {
		if (popYear == null || popYear.equals("null")) {
			popYear = "2010";
		}

		HubsClusterList response = new HubsClusterList();

		String[] dates = date.split(",");
		String[][] datedays = daysOfWeekString(dates);
		String[] fulldates = datedays[0];
		String[] days = datedays[1];
		x1 *= 1609.34;
		x2 *= 1609.34;
		x3 *= 1609.34;
		
		// for each agency (key), any agencies that have at least one stops within x1 distances
		// its stops are listed (value). 
		HashMap<String, KeyClusterHashMap> y = PgisEventManager.getClusters(x1,
				dbindex, username);
		HashMap<String, KeyClusterHashMap> counter = new HashMap<String, KeyClusterHashMap>(
				y);
		for (Entry<String, KeyClusterHashMap> entry : counter.entrySet()) {
			if (y.containsKey(entry.getKey())) {
				HashSet<String> counter2 = new HashSet<String>(
						entry.getValue().values);
				for (String stopID : counter2) {
					if (!entry.getKey().equals(stopID)) {
						y = recurse(y, entry, stopID);
					}
				}
			}
		}

		// adding the hub clusters with 3 and more agencies to the new HashMap
		HashMap<String, KeyClusterHashMap> y1 = new HashMap<String, KeyClusterHashMap>();
		for (Entry<String, KeyClusterHashMap> entry : y.entrySet()) {
			if (entry.getValue().keyAgencyIDs.size() >= 3) {
				y1.put(entry.getKey(), entry.getValue());
			}
		}

		// removing agencies that are covered by any other agency in the list.
		HashMap<String, KeyClusterHashMap> h = new HashMap<String, KeyClusterHashMap>();
		for (Entry<String, KeyClusterHashMap> entry : y1.entrySet()) {
			if (entry.getValue().keyAgencyIDs.size() >= 3) {

				ArrayList<String> tempKeyAgencies = new ArrayList<String>();
				tempKeyAgencies.addAll(entry.getValue().keyAgencyIDs);
				entry.getValue().keyAgencyIDs.clear();
				entry.getValue().keyAgencyIDs.addAll(Agencycontainlist(
						tempKeyAgencies, dbindex));
				// after removing covered agencies, if the number of agencies in the cluster is
				// 3 or more, then add that list to the final HashMap.
				if (entry.getValue().keyAgencyIDs.size() >= 3)
					h.put(entry.getKey(), entry.getValue());

			}
		}
		
		// get detailed information on key clusters
		response = getClusterData(h, fulldates, days, dbindex, x2, x3,
				username, key, popYear);
		return response;
	}

	/**
	 * returns agencies from the input list that are not covered by any centralized 
	 * agency. Centralized agencies are the one that are provide service in a limited
	 * area like a city. Agencies like Greyhound or Amtrak are no centralized.
	 *  
	 * @param agencies
	 * @param dbindex
	 * @return ArrayList<String>
	 */
	private ArrayList<String> Agencycontainlist(ArrayList<String> agencies,
			int dbindex) {

		List<String> c_a = new ArrayList<String>();
		Connection connection = PgisEventManager.makeConnection(dbindex);

		for (int i = 0; i < agencies.size(); i++) {
			String query = " Select * from agencymapping WHERE agencyID= '"
					+ agencies.get(i) + "'";
			try {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				while (rs.next()) {
					String[] containedAgencies;

					boolean centralize = rs.getBoolean("centralized");

					containedAgencies = (String[]) rs.getArray("contained_agencies")
							.getArray();
					if (centralize) {
						c_a.addAll(Arrays.asList(containedAgencies));
						c_a.remove(agencies.get(i));
					}
				}
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		ArrayList<String> output = new ArrayList<String>();
		for (String string : new ArrayList<String>(agencies)) {
			if (!c_a.contains(string)) {
				output.add(string);
			}
		}
		PgisEventManager.dropConnection(connection);
		return output;
	}

	/**
	 * returns information on the input cluster. For example, number agencies, stops, routes,
	 * population served, and etc.
	 * 
	 * @param x - HashMpa of clusters
	 * @param dates - array of dates in yyyymmdd format
	 * @param days - array of days. E.g. "tuesday"
	 * @param dbindex
	 * @param popRadius - population search radius
	 * @param pnrRadius - park and ride search radius
	 * @param username - user session
	 * @param key - unique key to track and update progress
	 * @param popYear - population projection year
	 * @return HubsClusterList
	 * @throws SQLException
	 */
	public HubsClusterList getClusterData(
			HashMap<String, KeyClusterHashMap> x,
			String[] dates, 
			String[] days, 
			final int dbindex,
			final double popRadius, 
			final double pnrRadius, 
			String username,
			final double key, 
			final String popYear
			) throws SQLException {
		HubsClusterList output = new HubsClusterList();
		int progress = 0;
		setprogVal(key, 5);
		final HashMap<String, Integer> serviceMap = PgisEventManager
				.stopFrequency(null, dates, days, username, dbindex);

		setprogVal(key, 10);
		int totalLoad = x.entrySet().size();
		HashMap<String, KeyClusterHashMap> first = new HashMap<String, KeyClusterHashMap>();
		HashMap<String, KeyClusterHashMap> second = new HashMap<String, KeyClusterHashMap>();
		HashMap<String, KeyClusterHashMap> third = new HashMap<String, KeyClusterHashMap>();
		HashMap<String, KeyClusterHashMap> forth = new HashMap<String, KeyClusterHashMap>();
		int b = 0;
		for (Entry<String, KeyClusterHashMap> e : x.entrySet()) {
			if (b == 0)
				first.put(e.getKey(), e.getValue());
			else if (b == 1)
				second.put(e.getKey(), e.getValue());
			else if (b == 2)
				third.put(e.getKey(), e.getValue());
			else {
				forth.put(e.getKey(), e.getValue());
				b = 0;
				continue;
			}
			b++;
		}
		class fillClusters implements Runnable {
			private Thread t;
			private String threadName;
			private HashMap<String, KeyClusterHashMap> threadSet;
			HubsClusterList tOutput;

			public boolean bool = true;
			public int threadProgress = 0;

			// multi thread
			fillClusters(String name, HashMap<String, KeyClusterHashMap> set,
					HubsClusterList output) {
				threadName = name;
				threadSet = set;
				tOutput = output;
			}

			public void run() {
				String query;
				Connection connection = PgisEventManager
						.makeConnection(dbindex);
				try {
					Statement stmt = connection.createStatement();
					for (Entry<String, KeyClusterHashMap> entry : threadSet
							.entrySet()) {
						threadProgress++;
						HubCluster response = new HubCluster();
						List<String> agencyids = new ArrayList<String>();
						List<String> stopids = new ArrayList<String>();
						int visits = 0;
						for (String instance : entry.getValue().values) {
							String[] temp = instance.split(":");
							stopids.add(temp[0]);
							agencyids.add(temp[1]);
							int stopVisits = serviceMap.get(temp[1] + temp[0]);
							response.stopsVisits.add(stopVisits);
							visits += stopVisits;
						}

						query = "WITH list AS (VALUES ";
						String temp = "";
						for (int i = 0; i < stopids.size(); i++) {
							temp += "('" + stopids.get(i) + "','"
									+ agencyids.get(i) + "'),";
						}
						query += temp.substring(0, temp.length() - 1);
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
								+ "routesarray0 AS (SELECT routes.agencyid, routes.id, routes.shortname, routes.longname, gtfs_agencies.name AS agencyname "
								+ "	FROM routes INNER JOIN gtfs_agencies "
								+ "	ON routes.agencyid = gtfs_agencies.id "
								+ "	GROUP BY routes.agencyid, routes.id, routes.shortname, routes.longname, agencyname), "
								+ "routesarray  AS (SELECT COALESCE(array_agg(agencyid),'{N/A}') AS routesagencies, COALESCE(array_agg(agencyname),'{N/A}') AS routesagenciesnames, COALESCE(array_agg(id),'{N/A}') AS routesids, COALESCE(array_agg(shortname),'{N/A}') AS routesshortnames, COALESCE(array_agg(longname),'{N/A}') AS routeslongnames FROM routesarray0),  "
								+ "routescount AS (SELECT count(distinct agencyid||id) AS routescount FROM routes), "
								+ "counties AS (SELECT counties.countyid, counties.cname FROM census_counties AS counties INNER JOIN stops ON LEFT(stops.blockid,5) = counties.countyid), "
								+ "countiesarray AS (SELECT COALESCE(array_agg(distinct countyid),'{N/A}') AS countiesids, COALESCE(array_agg(distinct cname),'{N/A}') AS countiesnames FROM counties), "
								+ "countiescount AS (SELECT count(distinct counties.countyid) AS countiescount FROM counties), "
								+ "urbanarray AS (SELECT COALESCE(sum(distinct population" + popYear + ")::text, 'N/A') AS urbanareaspop, COALESCE(array_agg(distinct stops.urbanid),'{N/A}') AS urbanids, COALESCE(array_agg(distinct uname),'{N/A}') AS urbannames FROM census_urbans INNER JOIN stops ON census_urbans.urbanid = stops.urbanid),"
								+ "regionsarray  AS (SELECT  COALESCE(array_agg(distinct ' Region '||regionid),'{N/A}') AS regionids FROM stops),"
								+ "pop0 AS (SELECT distinct census_blocks.blockid, population"
								+ popYear
								+ " as population FROM census_blocks INNER JOIN stops ON ST_Dwithin(census_blocks.location, stops.location, "
								+ popRadius
								+ ")), "
								+ "pop AS (SELECT COALESCE(sum(population),0) AS pop FROM pop0),"
								+ "rac as (select COALESCE(sum(C000_"
								+ popYear
								+ "),0) as employment from  pop0 left join lodes_rac_projection_block using(blockid)),"
								+ "wac as (select COALESCE(sum(C000),0) as employees from  pop0 left join lodes_blocks_wac using(blockid)),"
								+ "pnr AS (SELECT pr.* FROM parknride AS pr INNER JOIN clustercoor ON ST_Dwithin(pr.geom,ST_transform(ST_setsrid(ST_MakePoint(clustercoor.lon, clustercoor.lat),4326), 2993),"
								+ pnrRadius
								+ ")), "
								+ "pnrarray AS (SELECT COALESCE(count(pnr.pnrid),0) AS pnrcount,COALESCE(array_agg(pnr.pnrid),'{}') AS pnrids, COALESCE(array_agg(pnr.lat),'{}') AS pnrlats, "
								+ "COALESCE(array_agg(pnr.lon),'{}') AS pnrlons, COALESCE(array_agg(pnr.lotname),'{N/A}') AS pnrnames, COALESCE(array_agg(pnr.city),'{N/A}') AS pnrcities, "
								+ "	COALESCE(array_agg(pnr.spaces),'{0}') AS pnrspaces "
								+ "	FROM pnr), "
								+ "placesarray0 AS (SELECT distinct places.placeid FROM census_places AS places INNER JOIN stops ON stops.placeid = places.placeid), "
								+ "placesarray AS (SELECT COALESCE(array_agg(places.placeid),'{N/A}') AS placesids, COALESCE(array_agg(pname),'{N/A}') AS placesnames, count(distinct places.placeid) AS placescount "
								+ "FROM census_places AS places INNER JOIN placesarray0 ON placesarray0.placeid = places.placeid) "
								+ ""
								+ "SELECT clustercoor.*, stopscount.*, agenciescount.*, pop.* AS pop, regionsarray.*, "
								+ "	urbanarray.*, countiesarray.*, agenciesarray.*, countiescount.*, routescount.*,"
								+ " stopsarray.*, routesarray.*,pnrarray.*, placesarray.*,employment,employees"
								+ "	FROM stopsarray CROSS JOIN stopscount CROSS JOIN agenciesarray CROSS JOIN routesarray"
								+ "	CROSS JOIN routescount CROSS JOIN countiescount CROSS JOIN countiesarray CROSS JOIN pop"
								+ "	CROSS JOIN clustercoor CROSS JOIN urbanarray CROSS JOIN regionsarray CROSS JOIN agenciescount"
								+ "	CROSS JOIN pnrarray CROSS JOIN placesarray CROSS JOIN rac CROSS JOIN wac";
						// System.out.println("this one"+query+"-------------");

						ResultSet rs = stmt.executeQuery(query);

						while (rs.next()) {
							response.lat = rs.getString("lat");
							response.lon = rs.getString("lon");
							response.agenciescount = rs
									.getString("agenciescount");
							response.countiescount = rs
									.getString("countiescount");
							response.pop = rs.getString("pop");
							response.rac = (rs.getLong("employment"));
							response.wac = (rs.getLong("employees"));
							response.stopscount = rs.getString("stopscount");
							response.routescount = rs.getString("routescount");
							response.pnrcount = rs.getInt("pnrcount");
							response.placescount = rs.getInt("placescount");
							response.urbanareaspop = rs
									.getString("urbanareaspop");
							response.visits = visits + "";
							String[] tempCountiesNames = (String[]) rs
									.getArray("countiesnames").getArray();
							response.countiesNames = Arrays
									.asList(tempCountiesNames);
							String[] tempAgenciesNames = (String[]) rs
									.getArray("agenciesnames").getArray();
							response.agenciesNames = Arrays
									.asList(tempAgenciesNames);
							String[] tempUrbanNames = (String[]) rs.getArray(
									"urbannames").getArray();
							response.urbanNames = Arrays.asList(tempUrbanNames);
							String[] tempRoutesAgencies = (String[]) rs
									.getArray("routesagencies").getArray();
							response.routesAgencies = Arrays
									.asList(tempRoutesAgencies);
							String[] tempRoutesAgenciesNames = (String[]) rs
									.getArray("routesagenciesnames").getArray();
							response.routesAgenciesNames = Arrays
									.asList(tempRoutesAgenciesNames);
							String[] tempRoutesIDs = (String[]) rs.getArray(
									"routesids").getArray();
							response.routesIDs = Arrays.asList(tempRoutesIDs);
							String[] tempRoutesShortnames = (String[]) rs
									.getArray("routesshortnames").getArray();
							response.routeShortnames = Arrays
									.asList(tempRoutesShortnames);
							String[] tempRoutesLongnames = (String[]) rs
									.getArray("routeslongnames").getArray();
							response.routesLongnames = Arrays
									.asList(tempRoutesLongnames);
							String[] tempStopsIDs = (String[]) rs.getArray(
									"stopsids").getArray();
							response.stopsIDs = Arrays.asList(tempStopsIDs);
							String[] tempStopsAgencies = (String[]) rs
									.getArray("stopsagencyids").getArray();
							response.stopsAgencies = Arrays
									.asList(tempStopsAgencies);
							String[] tempStopsAgenciesNames = (String[]) rs
									.getArray("stopsagenciesnames").getArray();
							response.stopsAgenciesNames = Arrays
									.asList(tempStopsAgenciesNames);
							String[] tempStopsNames = (String[]) rs.getArray(
									"stopsnames").getArray();
							response.stopsNames = Arrays.asList(tempStopsNames);
							String[] tempregionsIDs = (String[]) rs.getArray(
									"regionids").getArray();
							response.regionsIDs = Arrays.asList(tempregionsIDs);
							Double[] tempStopsLats = (Double[]) rs.getArray(
									"stopslats").getArray();
							response.stopsLats = Arrays.asList(tempStopsLats);
							Double[] tempStopsLons = (Double[]) rs.getArray(
									"stopslons").getArray();
							response.stopsLons = Arrays.asList(tempStopsLons);
							Integer[] tempPnrIDs = (Integer[]) rs.getArray(
									"pnrids").getArray();
							response.pnrIDs = Arrays.asList(tempPnrIDs);
							Double[] tempPnrLats = (Double[]) rs.getArray(
									"pnrlats").getArray();
							response.pnrLats = Arrays.asList(tempPnrLats);
							Double[] tempPnrLons = (Double[]) rs.getArray(
									"pnrlons").getArray();
							response.pnrLons = Arrays.asList(tempPnrLons);
							String[] tempPnrNames = (String[]) rs.getArray(
									"pnrnames").getArray();
							response.pnrNames = Arrays.asList(tempPnrNames);
							String[] tempPnrCities = (String[]) rs.getArray(
									"pnrcities").getArray();
							response.pnrCities = Arrays.asList(tempPnrCities);
							Integer[] tempPnrSpaces = (Integer[]) rs.getArray(
									"pnrspaces").getArray();
							response.pnrSpaces = Arrays.asList(tempPnrSpaces);
							String[] tempPlacesIDs = (String[]) rs.getArray(
									"placesids").getArray();
							response.placesIDs = Arrays.asList(tempPlacesIDs);
							String[] tempPlacesNames = (String[]) rs.getArray(
									"placesnames").getArray();
							response.placesNames = Arrays
									.asList(tempPlacesNames);
							tOutput.Clusters.add(response);
						}
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				PgisEventManager.dropConnection(connection);
				bool = false;
			}

			public void start() {
				if (t == null) {
					t = new Thread(this, threadName);
					t.start();
				}
			}

		}

		fillClusters fc1 = new fillClusters("Thread-1", first, output);
		fc1.start();

		fillClusters fc2 = new fillClusters("Thread-2", second, output);
		fc2.start();

		fillClusters fc3 = new fillClusters("Thread-2", third, output);
		fc3.start();

		fillClusters fc4 = new fillClusters("Thread-2", forth, output);
		fc4.start();

		while (fc1.bool || fc2.bool || fc3.bool || fc4.bool) {
			progress = fc1.threadProgress + fc2.threadProgress
					+ fc3.threadProgress + fc4.threadProgress;
			setprogVal(key, 10 + ((int) Math.round(progress * 90 / totalLoad)));
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		progVal.remove(key);
		return output;
	}

	private HashMap<String, KeyClusterHashMap> recurse(
			HashMap<String, KeyClusterHashMap> y,
			Entry<String, KeyClusterHashMap> entry, String stopID) {
		if (y.containsKey(stopID)) {
			HashSet<String> tmpStopsSet = y.get(stopID).values;
			HashSet<String> tmpAgencyIDs = y.get(stopID).keyAgencyIDs;
			y.get(entry.getKey()).values.addAll(tmpStopsSet);
			y.get(entry.getKey()).keyAgencyIDs.addAll(tmpAgencyIDs);
			y.remove(stopID);
			for (String x : tmpStopsSet) {
				if (!entry.getKey().equals(x))
					y = recurse(y, entry, x);
			}
		}
		return y;
	}

	/**
	 * returns a list of ConGraph objects that represent edges of the graph.
	 * Any two agency that have a pair of stops within x miles radius of each 
	 * other and serve those stops on the selected date, will have an edge on
	 * the graph.
	 * 
	 * @param x - connection distance
	 * @param key - unique key to track and update progress
	 * @param date - selected date in mm/dd/yyyy format
	 * @param session - user session
	 * @param dbindex
	 * @return ConGraphObjSet
	 * @throws SQLException
	 */
	@GET
	@Path("/connectivityGraph")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object connectivityGraph(
			@QueryParam("x") Double x,
			@QueryParam("key") double key,
			@QueryParam("day") String date,
			@QueryParam("username") String session,
			@QueryParam("dbindex") Integer dbindex
			) throws SQLException {
		// Setting date
		String fulldate = null;
		String day = null;
		String[] dates = date.split(",");
		String[][] datedays = daysOfWeekString(dates);
		fulldate = datedays[0][0];
		day = datedays[1][0];

		// Making connection to DB
		Connection connection = PgisEventManager.makeConnection(dbindex);
		Statement stmt = connection.createStatement();

		// Retrieving list of agencies and putting the IDs into an array.
		HashMap<String, ConGraphAgency> agencies = SpatialEventManager
				.getAllAgencies(session, dbindex);

		ConGraphObjSet response = new ConGraphObjSet();
		Set<ConGraphObj> e = new HashSet<ConGraphObj>();
		int totalLoad = agencies.size(); // this is for computing the progress 
		int counter = 1;
		for (Entry<String, ConGraphAgency> i : agencies.entrySet()) {
			e = SpatialEventManager.getConGraphObj(i.getKey(),
					i.getValue().name, fulldate, day, session, x, stmt);
			response.set.addAll(e);
			setprogVal(key, (int) Math.floor((counter*100)/totalLoad) );
			counter++;
		}
		stmt.close();
		connection.close();
		return response;
	}

	/**
	 * returns the number of connection of the given length between the two agency on the given date. 
	 * 
	 * @param dbindex
	 * @param username
	 * @param date
	 * @param radius
	 * @param agencyID1
	 * @param agencyID2
	 * @return
	 * @throws SQLException
	 */
	@GET
	@Path("/getConnections")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getConnections(
			@QueryParam("dbindex") int dbindex,
			@QueryParam("username") String username,
			@QueryParam("date") String date,
			@QueryParam("radius") double radius,
			@QueryParam("aid1") String agencyID1,
			@QueryParam("aid2") String agencyID2) throws SQLException{
		String fulldate = null;
		String day = null;
		String[] dates = date.split(",");
		String[][] datedays = daysOfWeekString(dates);
		fulldate = datedays[0][0];
		day = datedays[1][0];
		Connection connection = PgisEventManager.makeConnection(dbindex);
		Statement stmt = connection.createStatement();
		String query = "with aids as (select agency_id as aid from gtfs_selected_feeds where username='" + username + "'),"
				+ "svcids as (select serviceid_agencyid, serviceid_id "
				+ "	from gtfs_calendars gc inner join aids on gc.serviceid_agencyid = '" + agencyID1 + "'"
				+ " 	where startdate::int<=" + fulldate + " and enddate::int>=" + fulldate + " and " + day + "= 1 and serviceid_agencyid||serviceid_id "
				+ "	not in (select serviceid_agencyid||serviceid_id from gtfs_calendar_dates where date='" + fulldate + "' and exceptiontype=2)"
				+ "	union select serviceid_agencyid, serviceid_id "
				+ "	from gtfs_calendar_dates gcd inner join aids on gcd.serviceid_agencyid = '" + agencyID1 + "' where date='" + fulldate + "' and exceptiontype=1),"
				+ " trips AS (select trip.agencyid as aid, trip.id as tripid, trip.route_id as routeid"
				+ "	from svcids inner join gtfs_trips trip using(serviceid_agencyid, serviceid_id)),"
				+ " a1stops AS (select stime.trip_agencyid as agencyid, gtfs_agencies.name as agencyname, stime.stop_id as stopid, stop.name as name, stop.lat, stop.lon, stop.location as location"
				+ "	from gtfs_stops stop inner join gtfs_stop_times stime on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id"
				+ "	inner join trips on stime.trip_agencyid =trips.aid and stime.trip_id=trips.tripid"
				+ "	inner join gtfs_agencies ON stime.trip_agencyid = gtfs_agencies.id"
				+ "	where stop.agencyid IN (SELECT aid FROM aids)"
				+ "	group by stime.trip_agencyid, stime.stop_agencyid, stime.stop_id, stop.location, gtfs_agencies.name, stop.name, stop.lat, stop.lon),"
				+ " svcids1 as (select serviceid_agencyid, serviceid_id"
				+ "	from gtfs_calendars gc inner join aids on gc.serviceid_agencyid = aids.aid"
				+ "	where startdate::int<=" + fulldate + " and enddate::int>=" + fulldate + " and " + day  + "= 1 and gc.serviceid_agencyid = '" + agencyID2 + "'"
				+ "	and serviceid_agencyid||serviceid_id not in (select serviceid_agencyid||serviceid_id from gtfs_calendar_dates where date='" + fulldate + "' and exceptiontype=2)"
				+ " union select serviceid_agencyid, serviceid_id"
				+ "	from gtfs_calendar_dates gcd inner join aids on gcd.serviceid_agencyid = aids.aid where date='" + fulldate + "' and exceptiontype=1 and gcd.serviceid_agencyid = '" + agencyID2 + "' ),"
				+ " trips1 AS (select trip.agencyid as aid, trip.id as tripid, trip.route_id as routeid"
				+ " from svcids1 inner join gtfs_trips trip using(serviceid_agencyid, serviceid_id)),"
				+ " a2stops AS (select a1stops.agencyid AS a1id, a1stops.agencyname AS a1name, stime.trip_agencyid as a2id, gtfs_agencies.name as a2name,"
				+ " stime.stop_id as stopid, stop.name as name, stop.lat, stop.lon, stop.location as location, ST_DISTANCE(stop.location, a1stops.location)::NUMERIC AS dist"
				+ " from gtfs_stops stop inner join a1stops on ST_DISTANCE(stop.location, a1stops.location) < " + radius 
				+ "	inner join gtfs_stop_times stime on stime.stop_agencyid = stop.agencyid and stime.stop_id = stop.id"
				+ "	inner join trips1 on stime.trip_agencyid =trips1.aid and stime.trip_id=trips1.tripid"
				+ "	inner join gtfs_agencies ON stime.trip_agencyid = gtfs_agencies.id"
				+ "	where stop.agencyid IN (SELECT aid FROM aids)"
				+ "	group by stime.trip_agencyid, stime.stop_agencyid, stime.stop_id, stop.location, gtfs_agencies.name, stop.name, stop.lat, stop.lon,a1stops.agencyid, a1stops.agencyname,a1stops.location)"
				+ " select a1id, a1name, a2id, a2name, COUNT(dist) AS connections"
				+ "	FROM a2stops "
				+ "	GROUP BY a1id, a1name, a2id, a2name";
//		System.out.println(query);
		ResultSet rs = stmt.executeQuery(query);
		rs.next();
		double connections = rs.getInt("connections");
		stmt.close();
		connection.close();
		return connections;
	}
	
	
	/**
	 * Returns the centroid for agencies. If an agency is a centralized agency, 
	 * only on coordinate is reported. Otherwise, for agencies like Greyhound
	 * a simplified graph of with the stops as edges is returned. 
	 * a
	 * @param username
	 * @param dbindex
	 * @return ConGraphAgencyGraphList
	 * @throws SQLException
	 */
	@GET
	@Path("/agencyCentriods")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getAgencyCentroids(
			@QueryParam("username") String username,
			@QueryParam("dbindex") Integer dbindex
			) throws SQLException {
		// Making connection to DB
		Connection connection = PgisEventManager.makeConnection(dbindex);
		Statement stmt = connection.createStatement();
		ConGraphAgencyGraphList response = new ConGraphAgencyGraphList();

		Map<String, ConGraphAgency> agencies = SpatialEventManager
				.getAllAgencies(username, dbindex);
		for (Entry<String, ConGraphAgency> e : agencies.entrySet()) {
			if (!e.getValue().centralized) {
				try{
					ConGraphAgencyGraph i = SpatialEventManager.getAgencyCentroids(e.getKey(), stmt, 100);
					i.centralized = e.getValue().centralized;
					response.list.add(i);
				}catch(NullPointerException error){
					System.err.println("Angecy ID " + e.getKey() + " does not have any service.");
					error.printStackTrace();
				}
			} else {
				ConGraphAgencyGraph i = SpatialEventManager.getAgencyCentroids(
						e.getKey(), stmt, 100);
				i.centralized = e.getValue().centralized;
				response.list.add(i);
			}
		}
		stmt.close();
		connection.close();
		return response;
	}
	
	/**
	 * returns hashmap of all agencies
	 * @param username
	 * @param dbindex
	 * @return
	 * @throws SQLException
	 */
	@GET
	@Path("/allAgencies")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getAllAgencies(@QueryParam("username") String username,
			@QueryParam("dbindex") Integer dbindex) throws SQLException {
		HashMap<String, ConGraphAgency> response = new HashMap<String, ConGraphAgency>();
		try {
			response = SpatialEventManager.getAllAgencies(username, dbindex);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return response;
	}

	/**
	 * returns calendar range for a set of agencies
	 * @param agency
	 * @param dbindex
	 * @return StartEndDatesList
	 */
	@GET
	@Path("/agenciesCalendarRange")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object agenciesCalendarRange(@QueryParam("agencies") String agency,
			@QueryParam("dbindex") Integer dbindex) {
		if (dbindex == null || dbindex < 0 || dbindex > dbsize - 1) {
			dbindex = default_dbindex;
		}
		StartEndDatesList sedlist = new StartEndDatesList();
		String[] agencies = agency.split(",");
		StartEndDates seDates;
		String defaultAgency;
		FeedInfo feed;
		Agency ag;
		try {
			for (String a : agencies) {
				seDates = new StartEndDates();
				ag = GtfsHibernateReaderExampleMain.QueryAgencybyid(a, dbindex);
				defaultAgency = ag.getDefaultId();// to be fixed
				feed = GtfsHibernateReaderExampleMain
						.QueryFeedInfoByDefAgencyId(defaultAgency, dbindex)
						.get(0);
				seDates.Startdate = feed.getStartDate().getAsString();
				seDates.Enddate = feed.getEndDate().getAsString();
				seDates.Agency = ag.getName();
				sedlist.SEDList.add(seDates);
			}
		} catch (Exception e) {
			e.printStackTrace();			
		}
		return sedlist;
	}

	/**
	 * returns overall calendar range
	 * @param agency
	 * @param username
	 * @param dbindex
	 * @return StartEndDates
	 */
	@GET
	@Path("/calendarRange")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object calendarRange(
			@QueryParam("agency") String agency,
			@QueryParam("username") String username,
			@QueryParam("dbindex") Integer dbindex
			) {
		if (dbindex == null || dbindex < 0 || dbindex > dbsize - 1) {
			dbindex = default_dbindex;
		}
		if (username == null)
			username = "admin";
		if (agency != null) {
			if (agency.equals("null") || agency.equals("")
					|| agency.equals("undefined"))
				agency = null;
		}
		StartEndDates response = PgisEventManager.getsedates(username, agency,
				dbindex);
		return response;
	}

	/**
	 * Returns list of all areas of type "areaType"
	 * @param areaType
	 * @param dbindex
	 * @return List<GeoArea>
	 * @throws SQLException
	 */
	@GET
	@Path("/getAreaList")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getAreaList(
			@QueryParam("areaType") String areaType,
			@QueryParam("dbindex") Integer dbindex
			) throws SQLException {
		return FlexibleReportEventManager.getAreaList(areaType, dbindex);
	}

	/**
	 * returns data to populate Transit Service report through the Flexible
	 * Reporting Wizard
	 *  
	 * @param dbindex - database index
	 * @param agencies - IDs of the selected agencies
	 * @param date - list of selected dates
	 * @param areas - IDs of the selected areas
	 * @param areaType - type of area of interest
	 * @param username - user session
	 * @param urbanFilter - flag on whether to filter on urban areas or not
	 * @param minUrbanPop - minimum population filter for urban areas
	 * @param maxUrbanPop - maximum population filter for urban areas
	 * @param uAreaYear - urban areas population projection year
	 * @param key - unique key to track and update progress 
	 * @return List<FlexRepSrv>
	 * @throws SQLException
	 */
	@GET
	@Path("/flexRepSrv")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getFlexRepSrv(
			@QueryParam("dbindex") Integer dbindex,
			@QueryParam("agencies") String agencies,
			@QueryParam("dates") String date,
			@QueryParam("areas") String areas,
			@QueryParam("areaType") String areaType,
			@QueryParam("username") String username,
			@QueryParam("urbanFilter") Boolean urbanFilter,
			@QueryParam("minUrbanPop") Integer minUrbanPop,
			@QueryParam("maxUrbanPop") Integer maxUrbanPop,
			@QueryParam("uAreaYear") String uAreaYear,
			@QueryParam("key") Double key
			) throws SQLException {
		String[] dates = date.split(",");
		String[][] datedays = daysOfWeekString(dates);
		String[] sdates = datedays[0];
		String[] days = datedays[1];
		return FlexibleReportEventManager.getFlexRepSrv(dbindex, agencies,
				sdates, days, areas, areaType, username, urbanFilter,
				minUrbanPop, maxUrbanPop, uAreaYear, key);
	}

	/**
	 * returns data to populate Population report through the Flexible
	 * Reporting Wizard
	 * 
	 * @param dbindex - database index
	 * @param agencies - IDs of the selected agencies
	 * @param date - list of selected dates
	 * @param areas - IDs of the selected areas
	 * @param popyear - population projection year
	 * @param los - minimum level of service defined by user
	 * @param sradius - population search radius
	 * @param areaType  - Type of area of interest
	 * @param username - user session
	 * @param urbanFilter - flag on whether to filter on urban areas or not
	 * @param minUrbanPop - minimum population filter for urban areas
	 * @param maxUrbanPop - maximum population filter for urban areas
	 * @param uAreaYear - urban areas population projection year
	 * @param key - unique key to track and update progress 
	 * @return List<FlexRepPop>
	 * @throws SQLException
	 */
	@GET
	@Path("/flexRepPop")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getFlexRepPop(
			@QueryParam("dbindex") Integer dbindex,
			@QueryParam("agencies") String agencies,
			@QueryParam("dates") String date,
			@QueryParam("areas") String areas,
			@QueryParam("year") String popyear, @QueryParam("los") Integer los,
			@QueryParam("sradius") Double sradius,
			@QueryParam("areaType") String areaType,
			@QueryParam("username") String username,
			@QueryParam("urbanFilter") Boolean urbanFilter,
			@QueryParam("minUrbanPop") Integer minUrbanPop,
			@QueryParam("maxUrbanPop") Integer maxUrbanPop,
			@QueryParam("uAreaYear") String uAreaYear,
			@QueryParam("key") Double key
			) throws SQLException {
		String[] dates = date.split(",");
		String[][] datedays = daysOfWeekString(dates);
		String[] sdates = datedays[0];
		String[] days = datedays[1];
		return FlexibleReportEventManager.getFlexRepPop(dbindex, agencies,
				sdates, days, popyear, areas, los, sradius, areaType, username,
				urbanFilter, uAreaYear, minUrbanPop, maxUrbanPop, key);
	}

	/**
	 * returns data to populate Employment report through the Flexible
	 * Reporting Wizard
	 * 
	 * @param dbindex - database index
	 * @param agencies - IDs of the selected agencies
	 * @param date - list of selected dates
	 * @param areas - IDs of the selected areas
	 * @param popyear - population projection year
	 * @param los - minimum level of service defined by user
	 * @param sradius - population search radius
	 * @param areaType - type of area of interest
	 * @param username - user session
	 * @param urbanFilter - flag on whether to filter on urban areas or not
	 * @param minUrbanPop - minimum population filter for urban areas
	 * @param maxUrbanPop - maximum population filter for urban areas
	 * @param uAreaYear - urban areas population projection year
	 * @param wac - flag for Workplace Area Characteristics dataset select
	 * @param rac - flag for Residential Area Characteristics dataset select
	 * @param metrics - metrics selected by user to report on
	 * @param key - unique key to track and update progress
	 * @return List<FlexRepEmp>
	 * @throws SQLException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@GET
	@Path("/flexRepEmp")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getFlexRepEmp(@QueryParam("dbindex") Integer dbindex,
			@QueryParam("agencies") String agencies,
			@QueryParam("dates") String date,
			@QueryParam("areas") String areas,
			@QueryParam("year") String popyear, @QueryParam("los") Integer los,
			@QueryParam("sradius") Double sradius,
			@QueryParam("areaType") String areaType,
			@QueryParam("username") String username,
			@QueryParam("urbanFilter") Boolean urbanFilter,
			@QueryParam("urbanYear") String urbanYear,
			@QueryParam("minUrbanPop") Integer minUrbanPop,
			@QueryParam("maxUrbanPop") Integer maxUrbanPop,
			@QueryParam("wac") Boolean wac, 
			@QueryParam("rac") Boolean rac,
			@QueryParam("metrics") String metrics, 
			@QueryParam("key") Double key
			)throws SQLException, NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		String[] dates = date.split(",");
		String[][] datedays = daysOfWeekString(dates);
		String[] sdates = datedays[0];
		String[] days = datedays[1];
		return FlexibleReportEventManager.getFlexRepEmp(dbindex, agencies,
				sdates, days, popyear, areas, los, sradius, areaType, username,
				urbanFilter, urbanYear, minUrbanPop, maxUrbanPop, wac, rac,
				metrics.split(","), key);
	}

	/**
	 * returns data to populate Title VI report through the Flexible
	 * Reporting Wizard
	 * 
	 * @param dbindex - database index
	 * @param agencies - IDs of the selected agencies
	 * @param date - list of selected dates
	 * @param areas - IDs of the selected areas
	 * @param los - minimum level of service defined by user
	 * @param sradius - population search radius
	 * @param areaType - type of area of interest
	 * @param username - user session
	 * @param urbanFilter - flag on whether to filter on urban areas or not
	 * @param urbanYear - urban areas population projection year
	 * @param minUrbanPop - minimum population filter for urban areas
	 * @param maxUrbanPop - maximum population filter for urban areas
	 * @param metrics - metrics selected by user to report on
	 * @param key - unique key to track and update progress
	 * @return List<FlexRepT6>
	 * @throws SQLException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@GET
	@Path("/flexRepT6")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
			MediaType.TEXT_XML })
	public Object getFlexRepT6(@QueryParam("dbindex") Integer dbindex,
			@QueryParam("agencies") String agencies,
			@QueryParam("dates") String date,
			@QueryParam("areas") String areas, @QueryParam("los") Integer los,
			@QueryParam("sradius") Double sradius,
			@QueryParam("areaType") String areaType,
			@QueryParam("username") String username,
			@QueryParam("urbanFilter") Boolean urbanFilter,
			@QueryParam("urbanYear") String urbanYear,
			@QueryParam("minUrbanPop") Integer minUrbanPop,
			@QueryParam("maxUrbanPop") Integer maxUrbanPop,
			@QueryParam("metrics") String metrics, @QueryParam("key") Double key)
			throws SQLException, NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		String[] dates = date.split(",");
		String[][] datedays = daysOfWeekString(dates);
		String[] sdates = datedays[0];
		String[] days = datedays[1];
		return FlexibleReportEventManager.getFlexRepT6(dbindex, agencies,
				sdates, days, areas, los, sradius, areaType, username,
				urbanFilter, urbanYear, minUrbanPop, maxUrbanPop,
				metrics.split(","), key);
	}	
}
