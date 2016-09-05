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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
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

import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipOutputStream;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.codehaus.jettison.json.JSONException;
/*import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.FareRule;
import org.onebusaway.gtfs.model.FeedInfo;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.ServiceCalendar;
import org.onebusaway.gtfs.model.ServiceCalendarDate;
import org.onebusaway.gtfs.model.ShapePoint;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;*/
import org.onebusaway.gtfs.model.AgencyAndId;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import com.model.database.Databases;
import com.model.database.onebusaway.gtfs.hibernate.ext.GtfsHibernateReaderExampleMain;
import com.model.database.onebusaway.gtfs.hibernate.objects.ext.AgencyExt;
import com.model.database.onebusaway.gtfs.hibernate.objects.ext.FareRuleExt;
import com.model.database.onebusaway.gtfs.hibernate.objects.ext.FeedInfoExt;
import com.model.database.onebusaway.gtfs.hibernate.objects.ext.RouteExt;
import com.model.database.onebusaway.gtfs.hibernate.objects.ext.ServiceCalendarDateExt;
import com.model.database.onebusaway.gtfs.hibernate.objects.ext.ServiceCalendarExt;
import com.model.database.onebusaway.gtfs.hibernate.objects.ext.StopTimeExt;
import com.model.database.onebusaway.gtfs.hibernate.objects.ext.TripExt;
import com.model.database.queries.EventManager;
import com.model.database.queries.PgisEventManager;
import com.model.database.queries.SpatialEventManager;
import com.model.database.queries.congraph.AgencyCentroid;
import com.model.database.queries.congraph.AgencyCentroidList;
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
import com.model.database.queries.util.StringUtils;
import com.model.database.queries.util.Types;
import com.webapp.modifiers.DbUpdate;


@Path("/transit")
@XmlRootElement
public class Queries {
	
	private static final double STOP_SEARCH_RADIUS = 0.1;
	private static final int LEVEL_OF_SERVICE = 2;
	private static final int default_dbindex = Databases.dbsize-1;
	static AgencyRouteList[] menuResponse = new AgencyRouteList[Databases.dbsize];
	static int dbsize = Databases.dbsize;	
	
	/**
	 * returns the name-list and id-list of databases.
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
			@QueryParam("username") String username) throws SQLException,
			IOException, ZipException, InterruptedException {
		String query = "";
		String[] agencies = agencyIDs.split(",");
		String feeds = "";
		if (agencies.length > 1){
			feeds = "((SELECT " + agencies[0] + "::text AS aid, startdate,enddate FROM gtfs_feed_info WHERE agencyids LIKE '%' || " + agencies[0] + " || '%') ";
			for (int i = 1; i < agencies.length ; i++)
				feeds += " UNION ALL (SELECT " + agencies[1] + "::text AS aid, startdate,enddate FROM gtfs_feed_info WHERE agencyids LIKE '%' || " + agencies[1] + " || '%')";
			feeds += " ) ";
		}else{
			feeds = "(SELECT " + agencies[0] + "::text AS aid, startdate,enddate FROM gtfs_feed_info WHERE agencyids LIKE '%' || " + agencies[0] + " || '%')";
		}
		if (flag.equals("routes")) {
			query = "SELECT agencies.name AS PRVDR_NM, routes.id AS route_id, routes.shortname AS route_shor, routes.longname AS route_long, "
					+ "	routes.description AS route_desc, routes.url AS route_url, trips.tripshortname, tripheadsign AS trip_headsign,"
					+ " length AS trip_length, estlength AS trip_estLength, feeds.startdate AS efct_dt_start, feeds.enddate AS efct_dt_end, "
					+ " shape AS trip_shape "
					+ "	FROM gtfs_routes AS routes "
					+ "	INNER JOIN gtfs_trips AS trips ON routes.id = trips.route_id "
					+ "	INNER JOIN gtfs_agencies AS agencies ON routes.agencyid = agencies.id "
					+ " INNER JOIN " + feeds + " AS feeds ON feeds.aid = routes.agencyid "
					+ "	WHERE routes.agencyid IN (" + agencyIDs + ")";
		} else if (flag.equals("stops")) {
			query += "SELECT agencies.name AS PRVDR_NM, '1899-12-30' AS arrival_ti, '1899-12-30' AS departure_, stops.id AS stop_id, stops.name AS stops_name,"
					+ " 	stops.lat AS stop_lat, stops.lon AS stop_lon, stops.url AS stops_url,"
					+ " 	CURRENT_DATE AS GIS_PRC_DT, feeds.startdate AS efct_dt_start, feeds.enddate AS efct_dt_end, stops.location AS shape "
					+ "		FROM gtfs_stops AS stops "
					+ "		INNER JOIN gtfs_stop_service_map AS map ON stops.id = map.stopid AND stops.agencyid = map.agencyid_def "
					+ "		INNER JOIN gtfs_agencies AS agencies ON map.agencyid=agencies.id "
					+ "		INNER JOIN " + feeds + " AS feeds ON feeds.aid = map.agencyid "
					+ "		WHERE map.agencyid IN (" + agencyIDs + ")";
		}
		
		// get database parameters
		String path = MainMap.class.getProtectionDomain().getCodeSource()
				.getLocation().getPath();

		BufferedReader reader = new BufferedReader(new FileReader(path
				+ "../../src/main/webapp/resources/admin/databaseParams.csv"));
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
			Date dNow = new Date( );
			SimpleDateFormat ft = new SimpleDateFormat ("hhmmss");
			String uniqueString = ft.format(dNow);
		
		String folderName = flag + "_shape_" + uniqueString;
		File folder = new File (path + "/" + folderName);
		folder.mkdir();
		String psqlPath = "C:/Program Files/PostgreSQL/9.4/bin/pgsql2shp.exe";
		ProcessBuilder pb = new ProcessBuilder("cmd","/c",generatorPath
				, folder.getAbsolutePath() + "\\" + flag + "_shape"
				, params[0]
				, params[2]
				, params[3]
				, dbName
				,"\"" + query + "\""
				, psqlPath);	
		Process pr = pb.start();
		pr.waitFor(5,TimeUnit.MINUTES);
		
		// start compression once the files are generated
		File zipF = new File(path + "/" + flag + "_shape_" + uniqueString + ".zip");
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipF));
        InputStream in = null;
        File[] sfiles = folder.listFiles();

        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        
        for(File f: sfiles){
        	out.putNextEntry(f, parameters);
        	
        	in = new FileInputStream(f);
            byte[] readBuff = new byte[4096];
            int readLen = -1;
            while ((readLen = in.read(readBuff)) != -1) {
            	out.write(readBuff, 0, readLen);
            }
        	
            out.closeEntry();
        	in.close();
        }
        out.finish();
        out.close();
        FileUtils.deleteDirectory(folder);
        
        // delete the file after 5 minutes.
        Timer timer;
        ActionListener a = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				zipF.delete();
			}
		};
		timer = new Timer(1, a);
		timer.setRepeats(false);
		timer.setInitialDelay(5*60000);
		timer.start();
		
        return "downloadables/shapefiles/" +  zipF.getName();
	}
	
	/** Generates Counties P&R Report*/
	@GET
	@Path("/CountiesPnR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
	public Object countiesPnR(@QueryParam("key") double key, @QueryParam("dbindex") Integer dbindex ) throws JSONException {
		if (dbindex==null || dbindex<0 || dbindex>dbsize-1){
        	dbindex = default_dbindex;
        }
		
		ParknRideCountiesList response = new ParknRideCountiesList();
		response = PgisEventManager.getCountiesPnrs(dbindex);
		
		response.metadata = "Report Type:Park&Ride Summary Report;Report Date:"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+";"+
    	    	"Selected Database:" +Databases.dbnames[dbindex] + ";" + DbUpdate.VERSION;
			    
	    setprogVal(key, 100);
	    
	    try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	    progVal.remove(key);
	    return response;
	}
	
	/** Generates P&R Report for a given county*/
	@GET
	@Path("/pnrsInCounty")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
	public Object pnrsInCounty(@QueryParam("key") double key, @QueryParam("countyId") String countyId, 
			@QueryParam("radius") double radius, @QueryParam("dbindex") Integer dbindex,
			@QueryParam("username") String username) throws JSONException {
		if (dbindex==null || dbindex<0 || dbindex>dbsize-1){
        	dbindex = default_dbindex;
        }
		PnrInCountyList response = new PnrInCountyList();
		int tmpRadius = (int) (radius * 1609.34);
		response = PgisEventManager.getPnrsInCounty(Integer.parseInt(countyId), tmpRadius, dbindex, username);
		response.metadata = "Report Type:Park&Ride Summary Report;Report Date:"+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+";"+
    	    	"Selected Database:" +Databases.dbnames[dbindex] + ";" + DbUpdate.VERSION;
			    
	    setprogVal(key, 100);
	    
	    try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	    progVal.remove(key);
	    return response;
	}
	
	@GET
    @Path("/NearBlocks")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
    public Object getNearBlocks(@QueryParam("lat") double lat,@QueryParam("x") double x, @QueryParam("lon") double lon, @QueryParam("dbindex") Integer dbindex ) throws JSONException {
        
        if (Double.isNaN(x) || x <= 0) {
            x = STOP_SEARCH_RADIUS;
        }
        if (dbindex==null || dbindex<0 || dbindex>dbsize-1){
        	dbindex = default_dbindex;
        }        	
        x = x * 1609.34;
        List <Census> centroids = new ArrayList<Census> ();
        try {
            centroids =EventManager.getcentroids(x, lat, lon, dbindex);
        } catch (FactoryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        CensusList response = new CensusList();        
        //Census C;
        for (Census c : centroids){
            Centroid cntr = new Centroid();
            cntr.setcentroid(c);
            response.centroids.add(cntr);            
        }
        return response;
    }
	
	@GET
    @Path("/poparound")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
    public Object getpop(@QueryParam("radius") double x,@QueryParam("lat") double lat,@QueryParam("lon") double lon, @QueryParam("dbindex") Integer dbindex){
		if (Double.isNaN(x) || x <= 0) {
            x = STOP_SEARCH_RADIUS;
        }
		if (dbindex==null || dbindex<0 || dbindex>dbsize-1){
        	dbindex = default_dbindex;
        } 
    	x = x * 1609.34;
		long response = 0;
        try {
			response =EventManager.getpop(x, lat, lon, dbindex);
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        List <Census> centroids = new ArrayList<Census>(); 
        try {
        	centroids =EventManager.getcentroids(x, lat, lon, dbindex);
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        long sum = 0;
        for (Census centroid: centroids){
        	sum+=centroid.getPopulation();
        }
        return new TransitError("Sum of Population is: "+ response+" Sum of centroids is: "+sum);
    }	
	
	/**
    * Generates The on map report
	 * @throws SQLException 
    *  
    */
   @GET
   @Path("/onmapreport")
   @Produces({ MediaType.APPLICATION_JSON , MediaType.APPLICATION_XML, MediaType.TEXT_XML})
   public Object getOnMapReport(@QueryParam("lat") String lats,@QueryParam("lon") String lons, @QueryParam("day") String date, @QueryParam("x") double x, @QueryParam("dbindex") Integer dbindex, @QueryParam("losRadius") String losRadius, @QueryParam("username") String username) throws JSONException, SQLException { 
   	if (Double.isNaN(x) || x <= 0) {
           x = 0;
       }
   	//x = Math.round(x*100.00)/100.00;
   	if (dbindex==null || dbindex<0 || dbindex>dbsize-1){
       	dbindex = default_dbindex;
       } 
   	double losR = Double.parseDouble(losRadius) * 1609.34;
   	String[] latss = lats.split(",");
   	double[] lat = new double[latss.length];
   	int ind = 0;
   	for(String la: latss){
   		lat[ind]=Double.parseDouble(la);
   		ind++;
   	}
   	String[] lonss = lons.split(",");
   	double[] lon = new double[lonss.length];
   	ind = 0;
   	for(String ln: lonss){
   		lon[ind]=Double.parseDouble(ln);
   		ind++;
   	}
   	//String username = "admin";
   	String[] dates = date.split(",");
   	String[][] datedays = daysOfWeekString(dates);
   	String[] fulldates = datedays[0];
   	String[] days = datedays[1];	   	
   	MapDisplay response = new MapDisplay();
   	MapTransit stops = PgisEventManager.onMapStops(fulldates,days,username, x, lat, lon, losR, dbindex);
   	MapGeo blocks = PgisEventManager.onMapBlocks(x, lat, lon, dbindex);
   	response.MapTR = stops;
   	response.MapG = blocks;
   	MapPnR pnr=new MapPnR();
   	List<ParknRide> PnRs=new ArrayList<ParknRide>();
   	PnRs=SpatialEventManager.getPnRs(lat, lon, x, dbindex);
	
	Map<String,List<MapPnrRecord>> mapPnr= new HashMap<String, List<MapPnrRecord>>(); 
	List<MapPnrCounty> mapPnrCounties=new ArrayList<MapPnrCounty>();
	MapPnrRecord mapPnrRecord;
	MapPnrCounty mapPnrCounty;
	for (ParknRide p:PnRs){
		mapPnrRecord=new MapPnrRecord();
		mapPnrRecord.countyId=p.getCountyid();
		mapPnrRecord.countyName=p.getCounty();
		mapPnrRecord.id=p.getPnrid()+"";
		mapPnrRecord.lat=p.getLat()+"";
		mapPnrRecord.lon=p.getLon()+"";
		mapPnrRecord.lotName=p.getLotname();
		mapPnrRecord.spaces=p.getSpaces()+"";
		mapPnrRecord.transitSerives=p.getTransitservice();
		mapPnrRecord.availability=p.getAvailability();
		
		
		if (!mapPnr.containsKey(p.getCountyid())){
			mapPnr.put(p.getCountyid(), new ArrayList<MapPnrRecord>());
			mapPnr.get(p.getCountyid()).add(mapPnrRecord);
			mapPnrCounty=new MapPnrCounty();
			mapPnrCounty.countyId=p.getCountyid();
			mapPnrCounty.countyName=p.getCounty();
			mapPnrCounties.add(mapPnrCounty);
		}else{
			mapPnr.get(p.getCountyid()).add(mapPnrRecord);
		}
	}
	
	int Spaces;
	int totalSpaces=0;
	int totalPnrs=0;
	List<MapPnrRecord> mapPnrRecords;
	for (MapPnrCounty mp:mapPnrCounties){
		Spaces=0;
		mapPnrRecords = mapPnr.get(mp.countyId);
		mp.MapPnrRecords=mapPnrRecords;
		mp.totalPnRs=mapPnrRecords.size()+"";
		totalPnrs+=mapPnrRecords.size();
		for (MapPnrRecord t:mapPnrRecords){
			Spaces+=Integer.parseInt(t.spaces);
		}
		totalSpaces+=Spaces;
		mp.totalSpaces=Spaces+"";
	}   	
		
   	pnr.totalPnR=totalPnrs;
   	pnr.totalSpaces=totalSpaces;
   	pnr.MapPnrCounty=mapPnrCounties;
   	
   	response.MapPnR=pnr;
   	return response;
    }
   
   /**
    * Identifies the stops and routes within 
    * a given radius of a park&ride lot.
    * 
    * @return MapPnrRecord
    * @throws SQLException 
    */
   @GET
   @Path("/pnrstopsroutes")
   @Produces({ MediaType.APPLICATION_JSON , MediaType.APPLICATION_XML, MediaType.TEXT_XML})
   public Object getPnrStopsRoutes(@QueryParam("pnrId") String pnrId, @QueryParam("pnrCountyId") String pnrCountyId,
		   @QueryParam("lat") Double lat, @QueryParam("lng") Double lng, @QueryParam ("radius") Double radius,
		   @QueryParam("dbindex") Integer dbindex, @QueryParam("username") String username) throws JSONException, SQLException {
	   
	   MapPnrRecord response = new MapPnrRecord();
	   response.id = pnrId;
	   MapStop mapPnrStop;
	   MapRoute mapPnrRoute;
	   List<String> agencyList = DbUpdate.getSelectedAgencies(username);
	   System.out.println(agencyList);
	   List<GeoStop> pnrGeoStops = new ArrayList<GeoStop>();
	   List<GeoStopRouteMap> sRoutes = new ArrayList<GeoStopRouteMap>();
		try { 
			pnrGeoStops = EventManager.getstopswithincircle2(radius, lat, lng, dbindex, agencyList);
			for (GeoStop s:pnrGeoStops){
				mapPnrStop=new MapStop();
				mapPnrStop.AgencyId=s.getAgencyId();
				mapPnrStop.Id=s.getStopId();
				mapPnrStop.Lat=s.getLat()+"";
				mapPnrStop.Lng=s.getLon()+"";
				mapPnrStop.Name=s.getName();
				
				response.MapPnrSL.add(mapPnrStop);
				
				List<GeoStopRouteMap> stmpRoutes = EventManager.getroutebystop(s.getStopId(), s.getAgencyId(), dbindex);
				for(GeoStopRouteMap r: stmpRoutes){
					if(!sRoutes.contains(r)){
						sRoutes.add(r);
					}
				}
			}
			for(GeoStopRouteMap r: sRoutes){
				mapPnrRoute = new MapRoute();
				RouteExt _r = GtfsHibernateReaderExampleMain.QueryRoutebyid(new AgencyAndId(r.getagencyId(), r.getrouteId()), dbindex);
				mapPnrRoute.AgencyId = _r.getId().getAgencyId();
				mapPnrRoute.Id=_r.getId().getId();
				mapPnrRoute.Name=_r.getLongName();
				List<TripExt> ts = SpatialEventManager.QueryTripsbyRoute(_r.getId().getAgencyId(), _r.getId().getId(), dbindex);
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
     * Generates a sorted by agency id list of routes for the LHS menu
     *  
     */
    @GET
    @Path("/menu")
    @Produces({ MediaType.APPLICATION_JSON , MediaType.APPLICATION_XML, MediaType.TEXT_XML})
    public Object getmenu(@QueryParam("day") String date, @QueryParam("dbindex") Integer dbindex, @QueryParam("username") String username) throws JSONException {  
    	if (dbindex==null || dbindex<0 || dbindex>dbsize-1){
        	dbindex = default_dbindex;
        }
    	String[] fulldates = null;
       	String[] days = null; 
    	if (date!=null && !date.equals("") && !date.equals("null")){
    		String[] dates = date.split(",");
           	String[][] datedays = daysOfWeekString(dates);
           	fulldates = datedays[0];
           	days = datedays[1];
           	AgencyRouteList response = PgisEventManager.agencyMenu(fulldates, days, username, dbindex);
           	return response;
    	} else {
    		if(!username.equals("admin")){
    			AgencyRouteList response = PgisEventManager.agencyMenu(null, null, username, dbindex);
    			return response;
    		}
    		List<String> selectedAgencies = DbUpdate.getSelectedAgencies(username);
	    	Collection <AgencyExt> allagencies = GtfsHibernateReaderExampleMain.QueryAllAgencies(selectedAgencies, dbindex);
	    	if (menuResponse[dbindex]==null || menuResponse[dbindex].data.size()!=allagencies.size() ){
	    		menuResponse[dbindex] = new AgencyRouteList();   	
	    		menuResponse[dbindex] = PgisEventManager.agencyMenu(null, null, username, dbindex);
	    	}    	
	    	return menuResponse[dbindex];
    	}
    }
    
    	/**
     * Return a list of all stops for a given agency in the database
    	 * @throws SQLException 
     */	
    @GET
    @Path("/stops")
   @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
    public Object dbstopsforagency(@QueryParam("agency") String agencyid, @QueryParam("dbindex") Integer dbindex) throws SQLException{
    	if (dbindex==null || dbindex<0 || dbindex>dbsize-1){
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
		while (rs.next()){
			StopR e = new StopR();
			e.StopId = rs.getString("id");
			e.StopName = rs.getString("name");
			e.lat = rs.getString("lat");
			e.lon = rs.getString("lon");
			response.stops.add(e);
		}
    	return response;
    }
    
 	/**
     * Return shape for a given trip and agency
     */	
    @GET
    @Path("/shape")
   @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
    public Object dbshapesforagency(@QueryParam("agency") String agency,@QueryParam("trip") String trip, @QueryParam("dbindex") Integer dbindex){
    	if (dbindex==null || dbindex<0 || dbindex>dbsize-1){
        	dbindex = default_dbindex;
        }
    	AgencyAndId agencyandtrip = new AgencyAndId();
    	agencyandtrip.setAgencyId(agency);
    	agencyandtrip.setId(trip);
    	TripExt tp = GtfsHibernateReaderExampleMain.getTrip(agencyandtrip, dbindex);    	
    	Rshape shape = new Rshape();
    	shape.points = tp.getEpshape();//to be fixed
    	shape.length = tp.getLength(); //to be fixed 
    	shape.agency = agency;
    	if(tp.getTripHeadsign()==null){
    		shape.headSign = "N/A";
    	}else{
    		shape.headSign = tp.getTripHeadsign();
    	}
    	shape.estlength = tp.getEstlength();//to be fixed
    	AgencyExt agencyObject = GtfsHibernateReaderExampleMain.QueryAgencybyid(agency, dbindex);
    	shape.agencyName = agencyObject.getName();
		return shape;
    }
  
 
    /**
     * Return a list of all stops for a given route id in the database
     * @throws SQLException 
     */	
    @GET
    @Path("/stopsbyroute")
   @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
    public Object dbstopsforroute(@QueryParam("agency") String agencyid,@QueryParam("route") String routeid, @QueryParam("dbindex") Integer dbindex) throws SQLException{
    	if (dbindex==null || dbindex<0 || dbindex>dbsize-1){
        	dbindex = default_dbindex;
        }
    	StopList response = new StopList();
    	Connection connection = PgisEventManager.makeConnection(dbindex);
		Statement stmt = connection.createStatement();
		String query;
		query = "SELECT stops.* FROM gtfs_stops AS stops INNER JOIN gtfs_stop_route_map AS map"
				+ " ON stops.id = map.stopid AND stops.agencyid = map.agencyid_def"
				+ " WHERE map.agencyid = '" + agencyid + "' AND map.routeid = '" + routeid + "'";
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()){
			StopR e = new StopR();
			e.StopId = rs.getString("id");
			e.StopName = rs.getString("name");
			e.lat = rs.getString("lat");
			e.lon = rs.getString("lon");
			response.stops.add(e);
		}
    	return response;
    }
    
    /**
     * Returns the progress value
     * @throws IOException 
     */
    @GET
    @Path("/PorgVal")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
    public Object getProgVal(@QueryParam("key") double key){
    	ProgVal progress = new ProgVal();	
    	progress.progVal = getprogVal(key);
       	return progress;
    	
    }
        
    static Map <Double, Integer> progVal = new HashMap<Double, Integer>();
    public void setprogVal(double key, int val){
    	progVal.put(key, val);
    }
    
    public int getprogVal(double key){
    	if(progVal.get(key)==null){
    		return 0;
    	}else{
    		return progVal.get(key);
    	}
    }
    
    /**
     * Returns a 2D array , [0][i] is date is YYYYMMDD format, [1][i] is day of week as integer 1(sunday) to 7(friday)
     */    
    public int[][] daysOfWeek(String[] dates){
    	Calendar calendar = Calendar.getInstance();
    	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    	int[][] days = new int[2][dates.length];
    	for(int i=0; i<dates.length; i++){
    		days[0][i] = Integer.parseInt(dates[i].split("/")[2] + dates[i].split("/")[0] + dates[i].split("/")[1]);
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
     * Returns a 2D array , [0][i] is date in YYYYMMDD format, [1][i] is day of week string (all lower case): sunday, monday, tuesday, wednesday, friday
     */
    public String[][] daysOfWeekString(String[] dates){
    	Calendar calendar = Calendar.getInstance();
    	String[] weekdays = {"sunday","monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
    	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    	String[][] days = new String[2][dates.length];
    	for(int i=0; i<dates.length; i++){
    		days[0][i] = dates[i].split("/")[2] + dates[i].split("/")[0] + dates[i].split("/")[1];
    		try {
				calendar.setTime(sdf.parse(dates[i]));
			} catch (ParseException e) {
				e.printStackTrace();
			}
    		days[1][i] = weekdays[calendar.get(Calendar.DAY_OF_WEEK)-1];
    	}
    	return days;
    }
    /**
     * Returns full date for the dates selected on calendar in EEE dd MMM yyyy fromat
     */
    public String[] fulldate(String[] dates){
    	//Calendar calendar = Calendar.getInstance();
    	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    	SimpleDateFormat tdf = new SimpleDateFormat("EEE dd MMM yyyy");    	
    	String[] result = new String [dates.length];     	
    	for(int i=0; i<dates.length; i++){    		
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
     * Generates The Agency Extended report
     */ 
    @GET
    @Path("/AgencyXR")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
    public Object getAXR(@QueryParam("agency") String agency, @QueryParam("day") String date, @QueryParam("popYear") String popYear,@QueryParam("x") double x, @QueryParam("key") double key, @QueryParam("dbindex") Integer dbindex) throws JSONException {
    	
       	if (Double.isNaN(x) || x <= 0) {
            x = STOP_SEARCH_RADIUS;
        }
       	 
       	if (dbindex==null || dbindex<0 || dbindex>dbsize-1){
        	dbindex = default_dbindex;
        }
       	if(popYear==null||popYear.equals("null")){
			popYear="2010";
		}
       	int totalLoad = 10;
       	int index =0;
       	String[] dates = date.split(",");
       	String[][] datedays = daysOfWeekString(dates);
       	String[] fulldates = fulldate(dates);
       	String[] sdates = datedays[0];
       	String[] days = datedays[1];
    	    	
    	AgencyXR response = new AgencyXR();
    	response.metadata = "Report Type:Transit Agency Extended Report;Report Date:"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+";"+
    	    	"Selected Database:" +Databases.dbnames[dbindex]+";Selected Date(s):"+date+"Population Search Radius(miles):"+String.valueOf(x)+
    	    	";Selected Transit Agency:"+agency + ";" + DbUpdate.VERSION;
    	x = x * 1609.34;
    	response.AgencyId = agency;    	
    	response.AgencyName = GtfsHibernateReaderExampleMain.QueryAgencybyid(agency, dbindex).getName();
    	index ++;
    	setprogVal(key, (int) Math.round(index*100/totalLoad));
    	double[] StopsPopMiles = PgisEventManager.stopsPopMiles(agency, x, dbindex, popYear);
    	index+=2;
    	setprogVal(key, (int) Math.round(index*100/totalLoad));
    	long StopCount = Math.round(StopsPopMiles[0]);
    	response.StopCount =  String.valueOf(StopCount);
    	response.UPopWithinX = String.valueOf(Math.round(StopsPopMiles[1]));
    	response.RPopWithinX = String.valueOf(Math.round(StopsPopMiles[2]));
    	double RouteMiles = StopsPopMiles[3];
    	response.RouteMiles = String.valueOf(RouteMiles);    	
    	if (RouteMiles >0)
        	response.StopPerRouteMile = String.valueOf(Math.round((StopCount*10000.0)/(RouteMiles))/10000.0);
        else 
        	response.StopPerRouteMile = "NA";
    			
    	HashMap<String, String> ServiceMetrics =  PgisEventManager.AgencyServiceMetrics(sdates,days,fulldates,agency,x,dbindex, popYear);
    	index +=6;
    	setprogVal(key, (int) Math.round(index*100/totalLoad));
    	response.ServiceMiles = ServiceMetrics.get("svcmiles");
    	response.ServiceHours = ServiceMetrics.get("svchours");
		response.ServiceStops = ServiceMetrics.get("svcstops");
		response.UPopServedByService = ServiceMetrics.get("uspop");
		response.RPopServedByService = ServiceMetrics.get("rspop");
		String serviceDays = ServiceMetrics.get("svcdays");
		if (serviceDays.length()>2){
			serviceDays = serviceDays.replace("\"", "");
			serviceDays= serviceDays.substring(1,serviceDays.length()-1);
			String[] svcdays = serviceDays.split(",");
			serviceDays = StringUtils.join(Arrays.asList(svcdays), ";");
        }
		response.ServiceDays = serviceDays;
		int HOSstart =Integer.parseInt(ServiceMetrics.get("fromtime"));
		int HOSend = Integer.parseInt(ServiceMetrics.get("totime"));			
        response.HoursOfService = ((HOSstart==-1)?"NA":StringUtils.timefromint(HOSstart))+"-"+ ((HOSend==-1)?"NA":StringUtils.timefromint(HOSend)); 
        index++;
        setprogVal(key, (int) Math.round(index*100/totalLoad));
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}        
        progVal.remove(key);                
    	return response;
    }
    
    /**
     * Generates The Stops report for
     * agency
     * agency and route
     * geographic area 
     * geographic area and agency
     * geographic area and route and agency
     * @throws SQLException 
     */
    @GET
    @Path("/StopsR")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
    public Object getTAS(@QueryParam("agency") String agency,@QueryParam("x") double x,@QueryParam("route") String routeid,@QueryParam("areaid") String areaid, 
    		@QueryParam("type") Integer type,@QueryParam("key") double key, @QueryParam("popYear") String popYear, @QueryParam("day") String date, @QueryParam("dbindex") Integer dbindex,@QueryParam("username") String username) throws JSONException, SQLException {
    	
    	if (Double.isNaN(x) || x <= 0) {
            x = STOP_SEARCH_RADIUS;
        }    	
    	if (dbindex==null || dbindex<0 || dbindex>dbsize-1){
        	dbindex = default_dbindex;
        }
    	if(popYear==null||popYear.equals("null")){
			popYear="2010";
		}
    	String[] dates = date.split(",");
    	String[][] datedays = daysOfWeekString(dates);
    	String[] fulldates = datedays[0];
    	String[] days = datedays[1];
    	StopListR response = new StopListR();  
    	response.AgencyName = "";
    	//x = x * 1609.34;
    	int index =0;
    	int totalLoad = 4;
    	if (agency!=null)
    		agency = (agency.equals("null"))? null:agency;
    	if (routeid !=null)
    		routeid = (routeid.equals("null"))? null:routeid;
    	if (areaid!=null)
    		areaid = (areaid.equals("null"))? null:areaid;
    	
    	setprogVal(key, (int) Math.round(index*100/totalLoad));
    	ArrayList<StopR> report = new ArrayList<StopR>();
    	//System.out.println("RouteID is : "+routeid);
    	if (areaid==null){
    		response.AgencyName = GtfsHibernateReaderExampleMain.QueryAgencybyid(agency, dbindex).getName();
    		index++;
    		setprogVal(key, (int) Math.round(index*100/totalLoad));
    		if (routeid==null){ //agency    			
    			response.metadata = "Report Type:Transit Stops Report for Agency;Report Date:"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+";"+
    	    	    	"Selected Database:" +Databases.dbnames[dbindex]+";Population Search Radius(miles):"+String.valueOf(x)+";Selected Transit Agency:"+agency + ";" + DbUpdate.VERSION;
    			report = PgisEventManager.stopGeosr(username, 0, fulldates, days, null, agency, null, x * 1609.34, dbindex, popYear);
    			index++;
        		setprogVal(key, (int) Math.round(index*100/totalLoad));
    		}else{//agency and route
    			response.metadata = "Report Type:Transit Stops Report for Agency and Route;Report Date:"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+";"+
    	    	    	"Selected Database:" +Databases.dbnames[dbindex]+";Population Search Radius(miles):"+String.valueOf(x)+";Selected Transit Agency:"+agency+";Selected Route:"+routeid + ";" + DbUpdate.VERSION;
    			report = PgisEventManager.stopGeosr(username, 0,  fulldates, days, null, agency, routeid, x * 1609.34, dbindex, popYear);
    			index++;
        		setprogVal(key, (int) Math.round(index*100/totalLoad));
    		}
    	}else{
    		response.AreaType = Types.getAreaName(type);
    		response.AreaName = EventManager.QueryGeoAreabyId(areaid, type, dbindex, Integer.parseInt(popYear)).getName();
    		index++;
    		setprogVal(key, (int) Math.round(index*100/totalLoad));
    		if(routeid==null){
    			if (agency==null){//area   				
    				response.metadata = "Report Type:Transit Stops Report for "+Types.getAreaName(type)+";Report Date:"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+";"+
        	    	    	"Selected Database:" +Databases.dbnames[dbindex]+";Population Search Radius(miles):"+String.valueOf(x)+";Selected Geographic Area:"+areaid + ";" + DbUpdate.VERSION;
    				report = PgisEventManager.stopGeosr(username, type, fulldates, days, areaid, null, null, x * 1609.34, dbindex, popYear);
    				index++;
    	    		setprogVal(key, (int) Math.round(index*100/totalLoad));
    			}else{//area and agency
    				response.AgencyName = GtfsHibernateReaderExampleMain.QueryAgencybyid(agency, dbindex).getName();
    				index++;
    	    		setprogVal(key, (int) Math.round(index*100/totalLoad));
    				response.metadata = "Report Type:Transit Stops Report for "+Types.getAreaName(type)+";Report Date:"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+";"+
        	    	    	"Selected Database:" +Databases.dbnames[dbindex]+";Population Search Radius(miles):"+String.valueOf(x)+";Selected Geographic Area:"+areaid+";Selected Transit Agency:"+agency + ";" + DbUpdate.VERSION;
    				report = PgisEventManager.stopGeosr(username, type, fulldates, days, areaid, agency, null, x * 1609.34, dbindex, popYear);
    				index++;
    	    		setprogVal(key, (int) Math.round(index*100/totalLoad));
    			}
    		}else{//area, agency, and route
    			response.AgencyName = GtfsHibernateReaderExampleMain.QueryAgencybyid(agency, dbindex).getName();
    			index++;
        		setprogVal(key, (int) Math.round(index*100/totalLoad));
    			response.metadata = "Report Type:Transit Stops Report for "+Types.getAreaName(type)+";Report Date:"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+";"+
    	    	    	"Selected Database:" +Databases.dbnames[dbindex]+";Population Search Radius(miles):"+String.valueOf(x)+";Selected Geographic Area:"+areaid+
    	    	    	";Selected Transit Agency:"+agency+";Selected Route:"+routeid + ";" + DbUpdate.VERSION;
    			report = PgisEventManager.stopGeosr(username, type, fulldates, days, areaid, agency, routeid, x * 1609.34, dbindex, popYear);
    			index++;
        		setprogVal(key, (int) Math.round(index*100/totalLoad));
    		}
    		
    	}    	
    	response.StopR = report;
    	setprogVal(key,100);		   	
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}   	
    	progVal.remove(key);
        return response;
    }

   
	/**
	 * Generates The Agency Summary report and geographic allocation of service for transit agencies
	 */
	    
	@GET
	@Path("/AgencySR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
	public Object getASR(@QueryParam("key") double key, @QueryParam("dbindex") Integer dbindex, @QueryParam("popYear") String popYear, @QueryParam("username") String username, @QueryParam("areaid") String areaId, 
			@QueryParam("type") Integer type) throws JSONException {
		if (dbindex==null || dbindex<0 || dbindex>dbsize-1){
        	dbindex = default_dbindex;
        }
		if(popYear==null||popYear.equals("null")){
			popYear="2010";
		}
		int index =0;
		int totalLoad = 2;
		setprogVal(key, (int) Math.round(index*100/totalLoad));
		ArrayList<AgencySR> agencies = new ArrayList<AgencySR>();
		AgencySRList response = new AgencySRList();		
		if (type!=null && areaId!=null){
			GeoArea instance = EventManager.QueryGeoAreabyId(areaId, type, dbindex,Integer.parseInt(popYear));
			agencies = PgisEventManager.agencyGeosr(username, type, areaId, dbindex);
			response.metadata = "Report Type:Agency Allocation of Service (Transit Agency Summary Report) for "+instance.getName()+"("+Types.getAreaName(type)+")"
			+";Report Date:"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+";Selected Database:" +Databases.dbnames[dbindex] + ";" + DbUpdate.VERSION;
			response.areaName = instance.getName();
			//might need to add area type for census tracts and some other geo areas
			response.areaType = instance.getTypeName();
		} else {
			agencies = PgisEventManager.agencysr(username, dbindex);
			response.metadata = "Report Type:Transit Agency Summary Report;Report Date:"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+";"+
	    	    	"Selected Database:" +Databases.dbnames[dbindex] + ";" + DbUpdate.VERSION;
		}		
		index++;
		setprogVal(key, (int) Math.round(index*100/totalLoad));     
	    response.agencySR = agencies;   
	    index++;
		setprogVal(key, (int) Math.round(index*100/totalLoad));
	    try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	    progVal.remove(key);
	    return response;
	}
	
	/**
	 * Generates The stops report and geographic allocation of service for transit agencies
	 */
	    
	/*@GET
	@Path("/StopsAR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
	public Object getSAR(@QueryParam("key") double key, @QueryParam("dbindex") Integer dbindex, @QueryParam("username") String username, @QueryParam("areaid") String areaId, @QueryParam("type") Integer type) throws JSONException {
		if (dbindex==null || dbindex<0 || dbindex>dbsize-1){
        	dbindex = default_dbindex;
        }
		int index =0;
		int totalLoad = 2;
		setprogVal(key, (int) Math.round(index*100/totalLoad));
		ArrayList<AgencySR> agencies = new ArrayList<AgencySR>();
		AgencySRList response = new AgencySRList();		
		if (type!=null && areaId!=null){
			GeoArea instance = EventManager.QueryGeoAreabyId(areaId, type, dbindex);
			agencies = PgisEventManager.agencyGeosr(username, type, areaId, dbindex);
			response.metadata = "Report Type:Agency Allocation of Service (Transit Agency Summary Report) for "+instance.getName()+"("+Types.getAreaName(type)+")"
			+";Report Date:"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+";Selected Database:" +Databases.dbnames[dbindex];
			response.areaName = instance.getName();
			//might need to add area type for census tracts and some other geo areas
			response.areaType = instance.getTypeName();
		} else {
			agencies = PgisEventManager.agencysr(username, dbindex);
			response.metadata = "Report Type:Transit Agency Summary Report;Report Date:"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+";"+
	    	    	"Selected Database:" +Databases.dbnames[dbindex];
		}		
		index++;
		setprogVal(key, (int) Math.round(index*100/totalLoad));     
	    response.agencySR = agencies;   
	    index++;
		setprogVal(key, (int) Math.round(index*100/totalLoad));
	    try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	    progVal.remove(key);
	    return response;
	}*/

	/**
	 * Generates The Routes report:
	 * routes report by agency
	 * routes report by agency and geographic area
	 * routes report by geographic area
	 */
	
	@GET
	@Path("/RoutesR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
	public Object getTAR(@QueryParam("agency") String agency, @QueryParam("x") double x, @QueryParam("day") String date, @QueryParam("key") double key, 
			@QueryParam("dbindex") Integer dbindex, @QueryParam("popYear") String popYear,@QueryParam("username") String username, @QueryParam("areaid") String areaid, @QueryParam("type") Integer type) throws JSONException {
		if (Double.isNaN(x) || x <= 0) {
	        x = STOP_SEARCH_RADIUS;
	    }		
		if (dbindex==null || dbindex<0 || dbindex>dbsize-1){
        	dbindex = default_dbindex;
        }
		if (agency!=null)
    		agency = (agency.equals("null"))? null:agency;    	
    	if (areaid!=null)
    		areaid = (areaid.equals("null"))? null:areaid;
    	if(popYear==null||popYear.equals("null")){
			popYear="2010";
		}
    	String[] dates = date.split(",");
    	String[][] datedays = daysOfWeekString(dates);
    	String[] sdates = datedays[0]; //date in YYYYMMDD format
    	String[] days = datedays[1]; //day of week string (all lower case)			
		ArrayList<RouteR> report = new ArrayList<RouteR>();
    	RouteListR response = new RouteListR();
    	int index =0;
		int totalLoad = 4;
		if (areaid==null){//routes report by agency
			response.AgencyName = GtfsHibernateReaderExampleMain.QueryAgencybyid(agency, dbindex).getName();
			index++;
    		setprogVal(key, (int) Math.round(index*100/totalLoad));
			response.metadata = "Report Type:Transit Routes Report for Agency;Report Date:"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+
					";Selected Database:" +Databases.dbnames[dbindex]+";Selected Date(s):"+date+";Population Search Radius(miles):"+String.valueOf(x)+";Selected Transit Agency:"+
					agency + ";" + DbUpdate.VERSION;
			report = PgisEventManager.RouteGeosr(username, 0, null, agency, sdates, days, x * 1609.34, dbindex, popYear);
			index++;
    		setprogVal(key, (int) Math.round(index*100/totalLoad));
		} else {
			response.AreaType = Types.getAreaName(type);
    		response.AreaName = EventManager.QueryGeoAreabyId(areaid, type, dbindex, Integer.parseInt(popYear)).getName();
    		index++;
    		setprogVal(key, (int) Math.round(index*100/totalLoad));
			if (agency!=null){//routes report by agency and areaId
				response.AgencyName = GtfsHibernateReaderExampleMain.QueryAgencybyid(agency, dbindex).getName();
				index++;
	    		setprogVal(key, (int) Math.round(index*100/totalLoad));
				response.metadata = "Report Type:Transit Routes Report for "+Types.getAreaName(type)+";Report Date:"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+
						";Selected Database:" +Databases.dbnames[dbindex]+";Selected Date(s):"+date+";Population Search Radius(miles):"+String.valueOf(x)+";Selected Geographic Area:"+areaid
						+";Selected Transit Agency:"+agency + ";" + DbUpdate.VERSION;
				report = PgisEventManager.RouteGeosr(username, type, areaid, agency, sdates, days, x * 1609.34, dbindex, popYear);
				index++;
	    		setprogVal(key, (int) Math.round(index*100/totalLoad));
			}else {//routes report by areaId				
				response.metadata = "Report Type:Transit Routes Report for "+Types.getAreaName(type)+";Report Date:"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+
						";Selected Database:" +Databases.dbnames[dbindex]+";Selected Date(s):"+date+";Population Search Radius(miles):"+String.valueOf(x)+";Selected Geographic Area:"+areaid + ";" + DbUpdate.VERSION;
				report = PgisEventManager.RouteGeosr(username, type, areaid, null, sdates, days, x * 1609.34, dbindex, popYear);
				index++;
	    		setprogVal(key, (int) Math.round(index*100/totalLoad));
			}			
		}		
		response.RouteR = report;
		index=totalLoad;
		setprogVal(key, (int) Math.round(index*100/totalLoad));		
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
	 * @throws SQLException 
     */
    @GET
    @Path("/ScheduleR")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
    public Object getSchedule(@QueryParam("agency") String agency, @QueryParam("route") String routeid, @QueryParam("day") String date, @QueryParam("key") double key, @QueryParam("dbindex") Integer dbindex) throws JSONException, SQLException {
    	if (dbindex==null || dbindex<0 || dbindex>dbsize-1){
        	dbindex = default_dbindex;
        }
    	ScheduleList response = new ScheduleList();
    	response.metadata = "Report Type:Route Schedule/Fare Report;Report Date:"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+";"+
    	    	"Selected Database:" +Databases.dbnames[dbindex]+";Selected Date(s):"+date+";Selected Transit Agency:"+agency+";Selected Route:"+routeid + ";" + DbUpdate.VERSION;
    	String[] dates = date.split(",");
		int[][] days = daysOfWeek(dates);
		//System.out.println(days[0][0]);
    	AgencyAndId routeId = new AgencyAndId(agency,routeid);
    	response.Agency = GtfsHibernateReaderExampleMain.QueryAgencybyid(agency, dbindex).getName()+"";
    	RouteExt route = GtfsHibernateReaderExampleMain.QueryRoutebyid(routeId, dbindex);
    	response.Route = route.getId().getId()+"";
    	List <FareRuleExt> fareRules = GtfsHibernateReaderExampleMain.QueryFareRuleByRoute(route, dbindex);
    	if(fareRules.size()==0){
    		response.Fare = "N/A";
    	}else{
    		response.Fare = fareRules.get(0).getFare().getPrice()+"";
    	}
    	List <TripExt> routeTrips = SpatialEventManager.QueryTripsbyRoute(route.getId().getAgencyId(), route.getId().getId(), dbindex);
    	int totalLoad = routeTrips.size();    	
    	response.directions[0]= new Schedule();
    	response.directions[1]= new Schedule();
    	Stoptime stoptime;
    	int[] maxSize={0,0};
    	int index =0;
    	String serviceAgency = routeTrips.get(0).getServiceId().getAgencyId();
	    int startDate;
	    int endDate;
		List <ServiceCalendarExt> agencyServiceCalendar = GtfsHibernateReaderExampleMain.QueryCalendarforAgency(serviceAgency, dbindex);
	    List <ServiceCalendarDateExt> agencyServiceCalendarDates = GtfsHibernateReaderExampleMain.QueryCalendarDatesforAgency(serviceAgency, dbindex);
Loop:  	for (TripExt trip: routeTrips){
    		index++;
    		boolean isIn = false;
    		ServiceCalendarExt sc = null;
			if(agencyServiceCalendar!=null){
				for(ServiceCalendarExt scs: agencyServiceCalendar){
					if(scs.getServiceId().getId().equals(trip.getServiceId().getId())){
						sc = scs;
						break;
					}
				}  
			}
			List <ServiceCalendarDateExt> scds = new ArrayList<ServiceCalendarDateExt>();
			for(ServiceCalendarDateExt scdss: agencyServiceCalendarDates){
				if(scdss.getServiceId().getId().equals(trip.getServiceId().getId())){
					scds.add(scdss);
				}
			}
			
			for(ServiceCalendarDateExt scd: scds){
				if(days[0][0]==Integer.parseInt(scd.getDate().getAsString())){
					if(scd.getExceptionType()==1){
						isIn = true;
						break;
					}
					continue Loop;
				}
			}
			if (sc!=null && !isIn){
				startDate = Integer.parseInt(sc.getStartDate().getAsString());
        		endDate = Integer.parseInt(sc.getEndDate().getAsString());
        		if(!(days[0][0]>=startDate && days[0][0]<=endDate)){
					continue;
				}
        		switch (days[1][0]){
					case 1:
						if (sc.getSunday()==1){
							isIn = true;											
						}
						break;
					case 2:
						if (sc.getMonday()==1){
							isIn = true;
						}
						break;
					case 3:
						if (sc.getTuesday()==1){
							isIn = true;
						}
						break;
					case 4:
						if (sc.getWednesday()==1){
							isIn = true;
						}
						break;
					case 5:
						if (sc.getThursday()==1){
							isIn = true;
						}
						break;
					case 6:
						if (sc.getFriday()==1){
							isIn = true;
						}
						break;
					case 7:
						if (sc.getSaturday()==1){
							isIn = true;
						}
						break;
				}
			}
			if(isIn){				
	    		AgencyAndId agencyAndTrip = trip.getId();
	    		List <StopTimeExt> stopTimes = SpatialEventManager.Querystoptimebytrip(agencyAndTrip.getAgencyId(), agencyAndTrip.getId(), dbindex); // to be fixed
	    		TripSchedule ts = new TripSchedule();
	    		for (StopTimeExt st: stopTimes){
	    			if(st.isArrivalTimeSet()){
	    				stoptime = new Stoptime();
	    				stoptime.StopTime = strArrivalTime(st.getArrivalTime());
	    				stoptime.StopName = st.getStop().getName()+"";
	    				stoptime.StopId = st.getStop().getId().getId();
	    				ts.stoptimes.add(stoptime);
	    			}
	    		}
	    		if(trip.getDirectionId()!=null && trip.getDirectionId().equals("1")){
		    		if(ts.stoptimes.size()>maxSize[1]){
		    			response.directions[1].stops = ts.stoptimes;
		    			//System.out.println(response.stops.get(0).StopId);
		    			maxSize[1]=ts.stoptimes.size();
		    		}
		    		response.directions[1].schedules.add(ts);
				}else{
					if(ts.stoptimes.size()>maxSize[0]){
						//System.out.println(ts.stoptimes.size());
						response.directions[0].stops = ts.stoptimes;
		    			//System.out.println(response.stops.get(0).StopId);
		    			maxSize[0]=ts.stoptimes.size();
		    		}
					response.directions[0].schedules.add(ts);
				}
			}
			setprogVal(key, (int) Math.round(index*100/totalLoad));
    	}
	    
	    try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	    
    	progVal.remove(key);
        return response;
    }
    
    public String strArrivalTime(int arrivalTime){
    	int hour = arrivalTime/3600;
    	int minute = (arrivalTime % 3600)/60;
    	String arrivalTimeSTR = zeroStartValue(hour)+":"+zeroStartValue(minute);
    	
    	return arrivalTimeSTR;
    }

    public String zeroStartValue(int value){
    	if(value<10){
    		return "0"+value;
    	}else{
    		return ""+value;
    	}
    }

    /**
	 * Generates The geographic area summary by agency report (geographic allocation of service) 
	 */
	    
	@GET
	@Path("/GeoCSRA")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
	public Object getGCSRA(@QueryParam("upop") int urbanPop, @QueryParam("key") double key, @QueryParam("dbindex") Integer dbindex, @QueryParam("popYear") String popYear, @QueryParam("type") Integer type, @QueryParam("agency") String agency, @QueryParam("username") String username ) throws JSONException {
		if (dbindex==null || dbindex<0 || dbindex>dbsize-1){
        	dbindex = default_dbindex;
        }
		if(popYear==null||popYear.equals("null")){
			popYear="2010";
		}
		int index = 1;
		int totalLoad = 3;
		setprogVal(key, (int) Math.round(index*100/totalLoad));
		GeoRList response = new GeoRList();
		if (agency!=null)
			agency = (agency.equals("null"))? null:agency;
		if (agency!=null){
			response.metadata = "Report Type:"+Types.getAreaName(type)+" Summary Report for "+agency+" ;Report Date:"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+";"+
	    	    	"Selected Database:" +Databases.dbnames[dbindex] + ";" + DbUpdate.VERSION;
			response.agencyId = agency;
			response.agencyName = GtfsHibernateReaderExampleMain.QueryAgencybyid(agency, dbindex).getName();
		}else {
			response.metadata = "Report Type:"+Types.getAreaName(type)+" Summary Report ;Report Date:"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+";"+
	    	    	"Selected Database:" +Databases.dbnames[dbindex] + ";" + DbUpdate.VERSION;
		}		
		List<GeoR> results = PgisEventManager.geoallocation(type, agency, dbindex, username, urbanPop, popYear);
		index++;
		setprogVal(key, (int) Math.round(index*100/totalLoad));		
		response.type = Types.getAreaName(type);		
		response.GeoR = results;
		index++;
	    setprogVal(key, (int) Math.round(index*100/totalLoad));	    
	    try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	    progVal.remove(key);
	    return response;
	}
       
    /**
     * Employment Summary Reports
     */
    @GET
	@Path("/emp")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
	public Object getEmp(@QueryParam("projection") String projection, @QueryParam("dataSet") String dataSet, @QueryParam("report") String reportType, @QueryParam("day") String date, @QueryParam("radius") double radius, @QueryParam("L") int L,
			@QueryParam("dbindex") int dbindex, @QueryParam("username") String username ) throws JSONException {
    	EmpDataList results = new EmpDataList();
    	String[] dates = date.split(",");
    	String[][] datedays = daysOfWeekString(dates);
    	String[] fulldates = fulldate(dates);
    	String[] sdates = datedays[0];
    	String[] days = datedays[1];
    	results = PgisEventManager.getEmpData(projection, dataSet, reportType, sdates, days, fulldates, radius, L, dbindex, username);
    	results.metadata = "Report Type: "+reportType+" Employment Report;Report Date:"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+";"+
    	    	"Selected Database:" +Databases.dbnames[dbindex] + ";" + DbUpdate.VERSION;  	
    	return results;
    }
    
    /**
     * Title VI Report
     */
    @GET
	@Path("/titlevi")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
	public Object getTitleVIData(@QueryParam("report") String reportType, @QueryParam("day") String date, @QueryParam("radius") double radius, @QueryParam("L") int L,
			@QueryParam("dbindex") int dbindex, @QueryParam("username") String username ) throws JSONException {
    	TitleVIDataList results = new TitleVIDataList();
    	String[] dates = date.split(",");
    	String[][] datedays = daysOfWeekString(dates);
    	String[] fulldates = fulldate(dates);
    	String[] sdates = datedays[0];
    	String[] days = datedays[1];
    	results = PgisEventManager.getTitleVIData(reportType, sdates, days, fulldates, radius, L, dbindex, username);
    	results.metadata = "Report Type: "+reportType+" Title VI Report;Report Date:"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+";"+
    	    	"Selected Database:" +Databases.dbnames[dbindex] + ";" + DbUpdate.VERSION;
    	return results;
    }
    
    
    /**
	 * Generates The counties Summary report
	 */	    
	@GET
	@Path("/GeoCSR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
	public Object getGCSR(@QueryParam("key") double key, @QueryParam("dbindex") Integer dbindex, @QueryParam("username") String username ) throws JSONException {
		if (dbindex==null || dbindex<0 || dbindex>dbsize-1){
        	dbindex = default_dbindex;
        }
		List<County> allcounties = new ArrayList<County> ();
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
		response.metadata = "Report Type:Counties Summary Report;Report Date:"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+";"+
    	    	"Selected Database:" +Databases.dbnames[dbindex] + ";" + DbUpdate.VERSION;
		response.type = "County";
	    int index =0;
		int totalLoad = allcounties.size();
	    for (County instance : allcounties){   
	    	index++;
	    	GeoR each = new GeoR();
	    	each.Name = instance.getName();
	    	each.id = instance.getCountyId();	    	
	    	each.ODOTRegion = instance.getRegionId();
	    	each.ODOTRegionName = instance.getRegionName();
	    	each.waterArea = String.valueOf(Math.round(instance.getWaterarea()/2.58999e4)/100.0);
	    	each.landArea = String.valueOf(Math.round(instance.getLandarea()/2.58999e4)/100.0);
	    	each.population = String.valueOf(instance.getPopulation());
	    	each.TractsCount = "0";
	    	try {
	    		each.TractsCount = String.valueOf(EventManager.gettractscountbycounty(instance.getCountyId(), dbindex));
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
	    		each.StopsCount = String.valueOf(EventManager.getstopscountbycounty(instance.getCountyId(), selectedAgencies, dbindex));
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
	    		ArrayList<String> test = new ArrayList<String>();
	    		test.add("22");	
	    		each.AgenciesCount = String.valueOf(EventManager.getAgencyCountByArea(instance.getCountyId(),0,test,dbindex));
	    		each.RoutesCount = String.valueOf(EventManager.getroutescountsbycounty(instance.getCountyId(),test,dbindex));
			} catch (FactoryException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (TransformException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        response.GeoR.add(each);
	        setprogVal(key, (int) Math.round(index*100/totalLoad));
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
	 */
	    
	@GET
	@Path("/GeoCTSR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
	public Object getGCTSR(@QueryParam("county") String county, @QueryParam("key") double key, @QueryParam("type") String type, @QueryParam("dbindex") Integer dbindex, @QueryParam("username") String username) throws JSONException {
		if (dbindex==null || dbindex<0 || dbindex>dbsize-1){
        	dbindex = default_dbindex;
        }
		List<Tract> alltracts = new ArrayList<Tract> ();
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
		response.metadata = "Report Type:Census Tracts Summary Report;Report Date:"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+";"+
    	    	"Selected Database:" +Databases.dbnames[dbindex] + ";" + DbUpdate.VERSION;
		response.type = "Tract";
	    int index =0;
		int totalLoad = alltracts.size();
	    for (Tract instance : alltracts){   
	    	index++;
	    	GeoR each = new GeoR();	    	
	    	each.id = instance.getTractId();
	    	each.Name = instance.getLongname();
	    	each.waterArea = String.valueOf(Math.round(instance.getWaterarea()/2.58999e4)/100.0);
	    	each.landArea = String.valueOf(Math.round(instance.getLandarea()/2.58999e4)/100.0);
	    	each.population = String.valueOf(instance.getPopulation());
	    	each.RoutesCount = String.valueOf(0);	    	    		    	
	    	each.AverageFare = "0";
	    	each.MedianFare = "0";	    	
	    	each.StopsCount = String.valueOf(0);
	    	try {
	    		each.StopsCount = String.valueOf(EventManager.getstopscountbytract(instance.getTractId(), selectedAgencies, dbindex));
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
	    		each.AgenciesCount = String.valueOf(EventManager.getAgencyCountByArea(instance.getTractId(),1,selectedAgencies,dbindex));
	    		each.RoutesCount = String.valueOf(EventManager.getroutescountsbytract(instance.getTractId(),selectedAgencies,dbindex));
			} catch (FactoryException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (TransformException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        response.GeoR.add(each);
	        setprogVal(key, (int) Math.round(index*100/totalLoad));
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
	 */
	    
	@GET
	@Path("/GeoCPSR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
	public Object getGCPSR(@QueryParam("key") double key, @QueryParam("dbindex") Integer dbindex, @QueryParam("username") String username) throws JSONException {
		if (dbindex==null || dbindex<0 || dbindex>dbsize-1){
        	dbindex = default_dbindex;
        }
		List<Place> allplaces = new ArrayList<Place> ();
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
		response.metadata = "Report Type:Census Places Summary Report;Report Date:"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+";"+
    	    	"Selected Database:" +Databases.dbnames[dbindex] + ";" + DbUpdate.VERSION;
		response.type = "Place";
	    int index =0;
		int totalLoad = allplaces.size();
	    for (Place instance : allplaces){   
	    	index++;
	    	GeoR each = new GeoR();
	    	each.Name = instance.getName();
	    	each.id = instance.getPlaceId();	    	
	    	each.waterArea = String.valueOf(Math.round(instance.getWaterarea()/2.58999e4)/100.0);
	    	each.landArea = String.valueOf(Math.round(instance.getLandarea()/2.58999e4)/100.0);
	    	each.population = String.valueOf(instance.getPopulation());
	    	each.RoutesCount = String.valueOf(0);	    		    		    	
	    	each.AverageFare = "0";
	    	each.MedianFare = "0";	    	
	    	each.StopsCount = String.valueOf(0);
	    	try {
	    		each.StopsCount = String.valueOf(EventManager.getstopscountbyplace(instance.getPlaceId(),selectedAgencies, dbindex));
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
	    		each.AgenciesCount = String.valueOf(EventManager.getAgencyCountByArea(instance.getPlaceId(),2,selectedAgencies,dbindex));
	    		each.RoutesCount = String.valueOf(EventManager.getroutescountsbyplace(instance.getPlaceId(),selectedAgencies, dbindex));	    		
			} catch (FactoryException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (TransformException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        response.GeoR.add(each);
	        setprogVal(key, (int) Math.round(index*100/totalLoad));
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
	 */
	    
    @GET
	@Path("/GeoURSR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
	public Object getGURSR(@QueryParam("pop") Integer upop, @QueryParam("key") double key, @QueryParam("type") String type, @QueryParam("dbindex") Integer dbindex, @QueryParam("popYear") String popYear, @QueryParam("username") String username) throws JSONException {
		if (dbindex==null || dbindex<0 || dbindex>dbsize-1){
        	dbindex = default_dbindex;
        }
		if (upop==null || upop<=0){
       		upop=50000;
       	}
		if(popYear==null||popYear.equals("null")){
			popYear="2010";
		}
		List<Urban> allurbanareas = new ArrayList<Urban> ();
		List<String> selectedAgencies = DbUpdate.getSelectedAgencies(username);
		try {
			allurbanareas = EventManager.geturbansbypop(upop,dbindex, popYear);
		} catch (FactoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (TransformException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		GeoRList response = new GeoRList();
		response.metadata = "Report Type:Aggregated Urban Areas Transit Summary Report;Report Date:"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+";"+
    	    	"Selected Database:" +Databases.dbnames[dbindex]+";Urban population Filter:"+String.valueOf(upop) + ";" + DbUpdate.VERSION;
		response.type = "UrbanArea";	
	    int index =0;
		int totalLoad = allurbanareas.size();
		GeoR each = new GeoR();
		each.Name = "Oregon Urbanized Areas with "+ String.valueOf(upop)+"+ Population";
		each.UrbansCount = String.valueOf(allurbanareas.size());
    	each.id = "00001";
    	long landarea=0;
    	long waterarea = 0;
    	long population = 0;
    	//int routescount = 0;
    	int stopscount = 0;
    	List<String> routeL = new ArrayList<String>();
	    for (Urban instance : allurbanareas){   
	    	index++;
	    	landarea+=instance.getLandarea();	    			
	    	waterarea +=instance.getWaterarea();
	    	population += instance.getPopulation();
	    	//int routescnt = 0;
	    	try {
	    		List<GeoStopRouteMap> routesL = EventManager.getroutesbyurban(instance.getUrbanId(), selectedAgencies, dbindex);
	    		for(int x=0;x<routesL.size();x++){
	    			String routeID = routesL.get(x).getrouteId()+routesL.get(x).getagencyId();
	    			if(!routeL.contains(routeID)){
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
	    	//routescount += routescnt;
	    	int stopscnt = 0;
	    	try {
	    		stopscnt = (int)EventManager.getstopscountbyurban(instance.getUrbanId(), selectedAgencies, dbindex);
			} catch (FactoryException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (TransformException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	    	stopscount += stopscnt;        
	        setprogVal(key, (int) Math.round(index*100/totalLoad));
	    }
	    try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	    progVal.remove(key);
	    each.waterArea = String.valueOf(Math.round(waterarea/2.58999e4)/100.0);
    	each.landArea = String.valueOf(Math.round(landarea/2.58999e4)/100.0);
    	each.population = String.valueOf(population);
    	//each.RoutesCount = String.valueOf(routescount);
    	each.RoutesCount = String.valueOf(routeL.size());
    	each.StopsCount = String.valueOf(stopscount);
    	response.GeoR.add(each);
	    return response;
	}
    
    /**
	 * Generates The Aggregated urban/rural area extended report
	 */
	    
    @GET
	@Path("/UrbanrXR")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
	public Object getGURXR(@QueryParam("pop") long pop, @QueryParam("day") String date,@QueryParam("x") double x, @QueryParam("l") Integer L, @QueryParam("key") double key, @QueryParam("dbindex") Integer dbindex, @QueryParam("popYear") String popYear, @QueryParam("username") String username) throws JSONException {
		if (Double.isNaN(x) || x <= 0) {
            x = STOP_SEARCH_RADIUS;
        }       			
		if (dbindex==null || dbindex<0 || dbindex>dbsize-1){
       	dbindex = default_dbindex;
        }		
		if (L==null || L<0){
       		L = LEVEL_OF_SERVICE;
       	}
		if(popYear==null||popYear.equals("null")){
			popYear="2010";
		}
		String[] dates = date.split(",");
    	String[][] datedays = daysOfWeekString(dates);
    	String[] fulldates = fulldate(dates);
    	String[] sdates = datedays[0];
    	String[] days = datedays[1];    	
    	int totalLoad = 10;
		int index = 0;
		setprogVal(key, (int) Math.round(index*100/totalLoad));
    	GeoXR response = new GeoXR();
    	List<Urban> urbans = new ArrayList<Urban>();
		try {
			urbans = EventManager.geturbansbypop((int)pop, dbindex, popYear);
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
    	response.metadata = "Report Type:Aggregated Urban Areas Extended Report;Report Date:"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+";"+
    	    	"Selected Database:" +Databases.dbnames[dbindex]+";Selected Date(s):"+date+";Population Search Radius(miles):"+String.valueOf(x)+
    	    	";Minimum Level of Service(times):"+String.valueOf(L)+";Urban Population Filter >=:"+String.valueOf(pop) + ";" + DbUpdate.VERSION;
    	x = x * 1609.34;    	
		response.AreaId = "01";		
		response.AreaName = "Urban Areas with "+String.valueOf(pop)+"+ Population";		
		HashMap<String, Float> FareData =PgisEventManager.AUrbansFareInfo(sdates, days, pop, username, dbindex, popYear);		
		response.MinFare = String.valueOf(FareData.get("minfare"));
		response.AverageFare = String.valueOf(FareData.get("averagefare"));
		response.MaxFare = String.valueOf(FareData.get("maxfare"));
		response.MedianFare = String.valueOf(FareData.get("medianfare"));
				
		index ++;
		setprogVal(key, (int) Math.round(index*100/totalLoad));		
		
		float RouteMiles = PgisEventManager.AUrbansRouteMiles(pop, username, dbindex, popYear);
		response.RouteMiles = String.valueOf(RouteMiles);
		index ++;
		setprogVal(key, (int) Math.round(index*100/totalLoad));
		
		long[] stopspop= PgisEventManager.AUrbansstopsPop(pop, username, x, dbindex, popYear);
		index ++;
		setprogVal(key, (int) Math.round(index*100/totalLoad));
		response.StopsPersqMile = String.valueOf(Math.round(stopspop[0]*25899752356.00/LandArea)/10000.00);
		response.PopWithinX = String.valueOf(stopspop[1]+stopspop[2]);
		response.UPopWithinX = String.valueOf(stopspop[1]);
		//response.RPopWithinX = String.valueOf(stopspop[2]);
		response.PopServed = String.valueOf(Math.round((10000.00*(stopspop[1])/population))/100.00);
		response.UPopServed = String.valueOf(Math.round((10000.00*(stopspop[1])/population))/100.00);	
		//response.RPopServed = String.valueOf(Math.round((10000.00*(stopspop[2])/population))/100.00);	
		response.PopUnServed = String.valueOf(Math.round(1E4-((10000.00*(stopspop[1])/population)))/100.0);
		HashMap<String, String> servicemetrics = PgisEventManager.UAreasServiceMetrics(sdates, days, fulldates, pop, username, L, x, dbindex, popYear);
		index +=6;
		setprogVal(key, (int) Math.round(index*100/totalLoad));
		double ServiceMiles = Float.parseFloat(servicemetrics.get("svcmiles"));
		//long PopatLOS = (Long.parseLong(servicemetrics.get("upopatlos"))+Long.parseLong(servicemetrics.get("rpopatlos")));
		float svcPop = Float.parseFloat(servicemetrics.get("uspop"));
		response.ServiceMiles = servicemetrics.get("svcmiles");
		response.ServiceHours = servicemetrics.get("svchours");
		response.ServiceStops = servicemetrics.get("svcstops");
		response.PopServedAtLoService = String.valueOf(Math.round(10000.0*Long.parseLong(servicemetrics.get("upopatlos"))/population)/100.0);
		response.UPopServedAtLoService = String.valueOf(Long.parseLong(servicemetrics.get("upopatlos")));
		//response.RPopServedAtLoService = String.valueOf(Long.parseLong(servicemetrics.get("rpopatlos")));
		
		String serviceDays = servicemetrics.get("svcdays");
		if (serviceDays.length()>2){
			serviceDays = serviceDays.replace("\"", "");
			serviceDays= serviceDays.substring(1,serviceDays.length()-1);
			String[] svcdays = serviceDays.split(",");
			serviceDays = StringUtils.join(Arrays.asList(svcdays), ";");
        }
		response.ServiceDays = serviceDays;
		response.MilesofServicePerCapita = (population>0) ? String.valueOf(Math.round((ServiceMiles*10000.00)/population)/10000.00): "NA";
		response.StopPerServiceMile = (ServiceMiles>0.01)? String.valueOf(Math.round((stopspop[0]*100)/Float.parseFloat(servicemetrics.get("svcmiles")))/100.0): "NA";
		response.ServiceMilesPersqMile = (LandArea>0.01) ? String.valueOf(Math.round((ServiceMiles*258999752.356)/LandArea)/10000.00):"NA";
		int HOSstart =Integer.parseInt(servicemetrics.get("fromtime"));
		int HOSend = Integer.parseInt(servicemetrics.get("totime"));			
        response.HoursOfService = ((HOSstart==-1)?"NA":StringUtils.timefromint(HOSstart))+"-"+ ((HOSend==-1)?"NA":StringUtils.timefromint(HOSend));
        String connections = servicemetrics.get("connections")+"";
		if (connections.length()>2){
			connections = connections.replace("\"", "");
			connections= connections.substring(1,connections.length()-1);
			String[] conns = connections.split(",");
			connections = StringUtils.join(Arrays.asList(conns), " ;");
        }
		response.ConnectedCommunities = connections;
		response.PopServedByService = String.valueOf(svcPop);
		response.UPopServedByService = String.valueOf(Float.parseFloat(servicemetrics.get("uspop")));	
		//response.RPopServedByService = String.valueOf(Float.parseFloat(servicemetrics.get("rspop")));	
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}        
        progVal.remove(key);		
		return response;
		
    }
    
	/**	     
     * Generates list of stops for a given agency.
     * Used to generated Connected Agencies On-map Report.
     * 
     */
    @GET
	@Path("/agenStops")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
	public Object getAgenStops(@QueryParam("agency") String agencyId, @QueryParam("dbindex") Integer dbindex, @QueryParam("username") String username) throws JSONException {
		if (dbindex==null || dbindex<0 || dbindex>dbsize-1){
        	dbindex = default_dbindex;
        }
		CAStopsList response = new CAStopsList();
		response = PgisEventManager.getAgenStops(agencyId, dbindex);
		return response;		
    }
    
	/**
	 * Generates The urban areas Summary report
	 *//*
	    
    @GET
	@Path("/GeoUASR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
	public Object getGUASR(@QueryParam("pop") Integer upop, @QueryParam("key") double key, @QueryParam("dbindex") Integer dbindex, @QueryParam("username") String username) throws JSONException {
		if (dbindex==null || dbindex<0 || dbindex>dbsize-1){
        	dbindex = default_dbindex;
        }
		
		List<Urban> allurbanareas = new ArrayList<Urban> ();
		List<String> selectedAgencies = DbUpdate.getSelectedAgencies(username);
		try {
			if (upop>-1){
				allurbanareas = EventManager.geturbansbypop(upop,dbindex);
	       	}else{
	       		allurbanareas = EventManager.geturban(dbindex);
	       	}
			
		} catch (FactoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (TransformException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		GeoRList response = new GeoRList();
		response.metadata = "Report Type:Urban Areas Summary Report;Report Date:"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+";"+
    	    	"Selected Database:" +Databases.dbnames[dbindex] + ";" + DbUpdate.VERSION;
		response.type = "UrbanArea";
	    int index =0;
		int totalLoad = allurbanareas.size();
	    for (Urban instance : allurbanareas){   
	    	index++;
	    	GeoR each = new GeoR();
	    	each.Name = instance.getName();
	    	each.id = instance.getUrbanId();	    	
	    	each.waterArea = String.valueOf(Math.round(instance.getWaterarea()/2.58999e4)/100.0);
	    	each.landArea = String.valueOf(Math.round(instance.getLandarea()/2.58999e4)/100.0);
	    	each.population = String.valueOf(instance.getPopulation());
	    	each.RoutesCount = String.valueOf(0);
	    	each.AgenciesCount = String.valueOf(0);
	    	try {
	    		each.AgenciesCount = String.valueOf(EventManager.getAgencyCountByArea(instance.getUrbanId(),3,selectedAgencies,dbindex));
	    		each.RoutesCount = String.valueOf(EventManager.getroutescountbyurban(instance.getUrbanId(), selectedAgencies, dbindex));
			} catch (FactoryException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (TransformException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	    	each.StopsCount = String.valueOf(0);	    	
	    	try {
	    		each.StopsCount = String.valueOf(EventManager.getstopscountbyurban(instance.getUrbanId(), selectedAgencies, dbindex));
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
	        setprogVal(key, (int) Math.round(index*100/totalLoad));
	    }
	    try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	    progVal.remove(key);
	    return response;
	}*/
    
	/**
	 * Generates The Congressional Districts Summary report
	 */
	
	@GET
	@Path("/GeoCDSR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
	public Object getGCDSR(@QueryParam("key") double key, @QueryParam("type") String type, @QueryParam("dbindex") Integer dbindex, @QueryParam("username") String username) throws JSONException {
		if (dbindex==null || dbindex<0 || dbindex>dbsize-1){
        	dbindex = default_dbindex;
        }
		List<CongDist> allcongdists = new ArrayList<CongDist> ();
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
		response.metadata = "Report Type:Congressional Districts Summary Report;Report Date:"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+";"+
    	    	"Selected Database:" +Databases.dbnames[dbindex] + ";" + DbUpdate.VERSION;
		response.type = "CongressionalDistrict";
	    int index =0;
		int totalLoad = allcongdists.size();
	    for (CongDist instance : allcongdists){   
	    	index++;
	    	GeoR each = new GeoR();
	    	each.Name = instance.getName();
	    	each.id = instance.getCongdistId();	    	
	    	each.waterArea = String.valueOf(Math.round(instance.getWaterarea()/2.58999e4)/100.0);
	    	each.landArea = String.valueOf(Math.round(instance.getLandarea()/2.58999e4)/100.0);
	    	each.population = String.valueOf(instance.getPopulation());
	    	each.RoutesCount = String.valueOf(0);
	    	each.AgenciesCount = String.valueOf(0);
	    	try {
	    		each.AgenciesCount = String.valueOf(EventManager.getAgencyCountByArea(instance.getCongdistId(),5,selectedAgencies,dbindex));
	    		each.RoutesCount = String.valueOf(EventManager.getroutescountbycongdist(instance.getCongdistId(),selectedAgencies,dbindex));
			} catch (FactoryException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (TransformException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	    	each.StopsCount = String.valueOf(0);	    	
	    	try {
	    		each.StopsCount = String.valueOf(EventManager.getstopscountbycongdist(instance.getCongdistId(),selectedAgencies,dbindex));
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
	        setprogVal(key, (int) Math.round(index*100/totalLoad));
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
	 */
	    
    @GET
	@Path("/GeoORSR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
	public Object getGORSR(@QueryParam("key") double key, @QueryParam("type") String type, @QueryParam("dbindex") Integer dbindex, @QueryParam("username") String username) throws JSONException {
		if (dbindex==null || dbindex<0 || dbindex>dbsize-1){
        	dbindex = default_dbindex;
        }
		List<County> allcounties = new ArrayList<County> ();
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
		response.metadata = "Report Type:ODOT Transit Regions Summary Report;Report Date:"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+";"+
    	    	"Selected Database:" +Databases.dbnames[dbindex] + ";" + DbUpdate.VERSION;
	    int index =0;
		int totalLoad = allcounties.size();
		String regionId = "";
		double waterArea = 0;
		double landArea = 0;
		long population = 0;		
		long countiesCount = 0;
		String regionName = "";
		boolean notfirst = false;
	    for (County instance : allcounties){   
	    		    	
	    	if (!(regionId.equals(instance.getRegionId()))){
	    		if (notfirst){
		    		GeoR each = new GeoR();
		    		each.id = regionId;
		    		each.Name = regionName;
		    		each.landArea = String.valueOf(Math.round(landArea/2.58999e4)/100.0);
		    		each.waterArea = String.valueOf(Math.round(waterArea/2.58999e4)/100.0);
		    		each.population = String.valueOf(population);		    		
		    		each.CountiesCount = String.valueOf(countiesCount);
		    		each.AverageFare = "0";
			    	each.MedianFare = "0";
			    	each.StopsCount = String.valueOf(0);
			    	try {
			    		each.StopsCount = String.valueOf(EventManager.getstopscountbyregion(regionId, selectedAgencies, dbindex));
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
			    		each.AgenciesCount = String.valueOf(EventManager.getAgencyCountByArea(regionId,4,selectedAgencies,dbindex));
			    		each.RoutesCount = String.valueOf(EventManager.getroutescountsbyregion(regionId, selectedAgencies, dbindex));
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
		    	countiesCount ++;
		    	index++;
	    	}
	    	setprogVal(key, (int) Math.round(index*100/totalLoad));
	    }
	    GeoR each = new GeoR();
		each.id = regionId;
		each.Name = regionName;
		each.landArea = String.valueOf(Math.round(landArea/2.58999e4)/100.0);
		each.waterArea = String.valueOf(Math.round(waterArea/2.58999e4)/100.0);
		each.population = String.valueOf(population);		    		
		each.CountiesCount = String.valueOf(countiesCount);
		each.AverageFare = "0";
    	each.MedianFare = "0";
    	each.StopsCount = String.valueOf(0);
    	try {
    		each.StopsCount = String.valueOf(EventManager.getstopscountbyregion(regionId,dbindex));
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
    		each.AgenciesCount = String.valueOf(EventManager.getAgencyCountByArea(regionId,4,selectedAgencies,dbindex));
    		each.RoutesCount = String.valueOf(EventManager.getroutescountsbyregion(regionId,dbindex));
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
	 */
	    
    @GET
	@Path("/ConAgenSR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
	public Object getGURSRd(@QueryParam("gap") double gap, @QueryParam("key") double key, @QueryParam("dbindex") Integer dbindex, @QueryParam("username") String username) throws JSONException {
		if (dbindex==null || dbindex<0 || dbindex>dbsize-1){
       	dbindex = default_dbindex;
       }
		if (gap<=0){
      		gap=0.1;
      	}
		//String username = "admin";
		ClusterRList response = new ClusterRList();
		response.metadata = "Report Type:Connected Transit Agencies Summary Report;Report Date:"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+";"+
    	    	"Selected Database:" +Databases.dbnames[dbindex]+";Minimum Spatial Gap (ft.):"+String.valueOf(gap) + ";" + DbUpdate.VERSION;
		gap = gap * 1609.34;
		response.type = "AgencyGapReport";
		//PgisEventManager.makeConnection(dbindex);
		List<agencyCluster> results= new ArrayList<agencyCluster>();
		results = PgisEventManager.agencyCluster(gap, username, dbindex);
		int totalLoad = results.size();
		int index = 0;
		for (agencyCluster acl: results){
			index++;
			ClusterR instance = new ClusterR();
			instance.id = acl.getAgencyId();
			instance.name = acl.getAgencyName();
			instance.size = String.valueOf(acl.getClusterSize());
			instance.ids = StringUtils.join(acl.getAgencyIds(), ";");
			instance.names = StringUtils.join(acl.getAgencyNames(), ";");
//			instance.distances = StringUtils.join(acl.getMinGaps(), ";");
			response.ClusterR.add(instance);
			setprogVal(key, (int) Math.round(index*100/totalLoad));
		}
		//PgisEventManager.dropConnection();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}        
        progVal.remove(key); 
		return response;
		
    }
	
	@GET
	@Path("/ConAgenXR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
	public Object getGURXRd(@QueryParam("agency") String agencyId, @QueryParam("gap") double gap, @QueryParam("key") double key, @QueryParam("dbindex") Integer dbindex, @QueryParam("username") String username) throws JSONException {
		if (dbindex==null || dbindex<0 || dbindex>dbsize-1){
       	dbindex = default_dbindex;
       }
		if (gap<=0){
      		gap=0.1;
      	}		
		ClusterRList response = new ClusterRList();
		response.metadata = "Report Type:Connected Transit Agencies Extended Report;Report Date:"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+";"+
    	    	"Selected Database:" +Databases.dbnames[dbindex]+";Minimum Spatial Gap (miles):"+String.valueOf(gap)+";Selected Agency:"+GtfsHibernateReaderExampleMain.QueryAgencybyid(agencyId, dbindex).getName() + ";" + DbUpdate.VERSION;
		response.type = "ExtendedGapReport";
		response.agency = GtfsHibernateReaderExampleMain.QueryAgencybyid(agencyId, dbindex).getName();
		gap = gap * 1609.34;		
		List<agencyCluster> results= new ArrayList<agencyCluster>();
		results = PgisEventManager.agencyClusterDetails(gap, agencyId, username, dbindex);
		int totalLoad = results.size();
		int index = 0;
		for (agencyCluster acl: results){
			index++;
			ClusterR instance = new ClusterR();
			instance.id = acl.getAgencyId();
			instance.name = acl.getAgencyName();
			instance.size = String.valueOf(acl.getClusterSize());
			instance.minGap = String.valueOf(acl.getMinGap());
			instance.maxGap = String.valueOf(acl.getMaxGap());
			instance.meanGap = String.valueOf(acl.getMeanGap());
			for (int i=0;i<acl.getClusterSize();i++){
				ClusterR inst = new ClusterR();
//				inst.id = acl.destStopIds.get(i);
				inst.name = acl.sourceStopNames.get(i);
				inst.names = acl.destStopNames.get(i);
				inst.scoords = acl.sourceStopCoords.get(i);
				inst.dcoords = acl.destStopCoords.get(i);
				inst.minGap = acl.minGaps.get(i);
				instance.connections.add(inst);
			}						
			response.ClusterR.add(instance);
			setprogVal(key, (int) Math.round(index*100/totalLoad));
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
	 */
	@GET
	@Path("/ConNetSR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
	public Object getCTNSR(@QueryParam("gap") double gap, @QueryParam("key") double key, @QueryParam("dbindex") Integer dbindex, @QueryParam("username") String username) throws JSONException {
		if (dbindex==null || dbindex<0 || dbindex>dbsize-1){
       	dbindex = default_dbindex;
       }
		if (gap<=0){
      		gap=0.1;
      	}
		//String username = "admin";
		ClusterRList response = new ClusterRList();
		response.metadata = "Report Type:Connected Transit Networks Summary Report;Report Date:"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+";"+
    	    	"Selected Database:" +Databases.dbnames[dbindex]+";Minimum Spatial Gap (ft.):"+String.valueOf(gap) + ";" + DbUpdate.VERSION;
		gap = gap * 1609.34;
		List<agencyCluster> agencies= new ArrayList<agencyCluster>();
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
		while (clsize>0){
			changed = false;
			Iterator<agencyCluster> iterator = agencies.iterator();
			while (iterator.hasNext()){
				added = false;
				buffer = iterator.next();				
				added = added||current.addAgencyCluster(buffer);				
				if (added){					
					iterator.remove();
					clsize--;					
				}
				changed = changed||added;
			}			
			if (!changed){
				index ++;
				res.add(current);
				current = new NetworkCluster();
				current.clusterId = index;				
			}			
			setprogVal(key, (int) Math.round((totalLoad-clsize)*100/totalLoad));
		}
		if (current.clusterSize>0)
			res.add(current);
			for (NetworkCluster ncl: res){
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
	 * Generates The Summary Statewide report
	 */
	    
	@GET
	@Path("/stateSR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
	public Object getStateSR(@QueryParam("key") double key, @QueryParam("dbindex") Integer dbindex, @QueryParam("popYear") String popYear, @QueryParam("username") String username) throws JSONException, FactoryException, TransformException {
		if (dbindex==null || dbindex<0 || dbindex>dbsize-1){
			dbindex = default_dbindex;
        }
		if(popYear==null||popYear.equals("null")){
			popYear="2010";
		}
		List<String> selectedAgencies = DbUpdate.getSelectedAgencies(username);
		int totalLoad = 2;
		int index = 0;
		setprogVal(key, (int) Math.round(index*100/totalLoad));
		GeoRList response = new GeoRList();
		response.metadata = "Report Type:Statewide Summary Report;Report Date:"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+";"+
    	    	"Selected Database:" +Databases.dbnames[dbindex] + ";" + DbUpdate.VERSION;
		response.type = "StatewideReport";
		HashMap<String, Long> geocounts = new HashMap<String, Long>();
		geocounts = PgisEventManager.getGeoCounts(dbindex,username,popYear);//needs to be changed		
		GeoR each = new GeoR();
		each.Name = "Oregon";
		each.CountiesCount = String.valueOf(geocounts.get("county"));
		each.TractsCount = String.valueOf(geocounts.get("tract"));
		each.PlacesCount = String.valueOf(geocounts.get("place"));
		each.UrbansCount = String.valueOf(geocounts.get("urban"));
		each.RegionsCount = String.valueOf(geocounts.get("region"));
		each.CongDistsCount = String.valueOf(geocounts.get("congdist"));
		each.population = String.valueOf(geocounts.get("pop"));
		each.landArea = String.valueOf(Math.round(geocounts.get("landarea")/2.58999e4)/100.0);
		each.urbanpop = String.valueOf(geocounts.get("urbanpop"));
		each.ruralpop = String.valueOf(geocounts.get("ruralpop"));		
		index++;
		setprogVal(key, (int) Math.round(index*100/totalLoad));
		HashMap<String, Integer> transcounts = new HashMap<String, Integer>();
		transcounts = PgisEventManager.QueryCounts(dbindex,username);
		each.StopsCount = String.valueOf(transcounts.get("stop"));
		each.RoutesCount = String.valueOf(transcounts.get("route"));
		each.AgenciesCount = String.valueOf(transcounts.get("agency"));
		response.GeoR.add(each);
		index++;
		setprogVal(key, (int) Math.round(index*100/totalLoad));
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}        
        progVal.remove(key);		
		return response;
		
    }
	
	/**
	 * Generates The Extended statewide report
	 */
	    
	@GET
	@Path("/stateXR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
	public Object getStateXR(@QueryParam("day") String date,@QueryParam("x") double x, @QueryParam("popYear") String popYear, @QueryParam("l") Integer L, @QueryParam("key") double key, @QueryParam("dbindex") Integer dbindex, @QueryParam("username") String username) throws JSONException {
		if (Double.isNaN(x) || x <= 0) {
            x = STOP_SEARCH_RADIUS;
        }       			
		if (dbindex==null || dbindex<0 || dbindex>dbsize-1){
       	dbindex = default_dbindex;
        }		
		if (L==null || L<0){
       		L = LEVEL_OF_SERVICE;
       	}
		if(popYear==null||popYear.equals("null")){
			popYear="2010";
		}
		List<String> selectedAgencies = DbUpdate.getSelectedAgencies(username);
		String[] dates = date.split(",");
    	String[][] datedays = daysOfWeekString(dates);
    	String[] fulldates = fulldate(dates);
    	String[] sdates = datedays[0]; //date in YYYYMMDD format
    	String[] days = datedays[1]; //day of week string (all lower case)
    	//String username = "admin";
    	GeoXR response = new GeoXR();
    	response.metadata = "Report Type:Statewide Extended Report;Report Date:"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+";"+
    	    	"Selected Database:" +Databases.dbnames[dbindex]+";Selected Date(s):"+date+";Population Search Radius(miles):"+String.valueOf(x)+
    	    	";Minimum Level of Service(times):"+String.valueOf(L) + ";" + DbUpdate.VERSION;
    	x = x * 1609.34;
    	int totalLoad = 10;
		int index = 0;
		setprogVal(key, (int) Math.round(index*100/totalLoad));
		response.AreaName = "Oregon";
		HashMap<String, Float> FareData = new HashMap<String, Float>();
		FareData = GtfsHibernateReaderExampleMain.QueryFareData(selectedAgencies, dbindex);
		index ++;
		setprogVal(key, (int) Math.round(index*100/totalLoad));
		response.MinFare = String.valueOf(FareData.get("min"));
		response.AverageFare = String.valueOf(FareData.get("avg"));
		response.MaxFare = String.valueOf(FareData.get("max"));
		int FareCount = FareData.get("count").intValue();
		float FareMedian = GtfsHibernateReaderExampleMain.QueryFareMedian(selectedAgencies, FareCount, dbindex);
		index ++;
		setprogVal(key, (int) Math.round(index*100/totalLoad));
		response.MedianFare = String.valueOf(FareMedian);
		Double RouteMiles = 0.00;
		RouteMiles = PgisEventManager.StateRouteMiles(username, dbindex);
		index ++;
		setprogVal(key, (int) Math.round(index*100/totalLoad));
		response.RouteMiles = String.valueOf(Math.round(RouteMiles*100.00)/100.00);
		Long StopsCount = GtfsHibernateReaderExampleMain.QueryStopsCount(selectedAgencies, dbindex);
		index ++;
		setprogVal(key, (int) Math.round(index*100/totalLoad));
		HashMap<String, Long> geocounts = new HashMap<String, Long>();
		try {
			geocounts = EventManager.getGeoCounts(dbindex, popYear);
		} catch (FactoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (TransformException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		response.StopsPersqMile = String.valueOf(Math.round(StopsCount*25899752356.00/geocounts.get("landarea"))/10000.00);
		index+=5;
		setprogVal(key, (int) Math.round(index*100/totalLoad));
		HashMap<String,String> serviceMetrics = PgisEventManager.StatewideServiceMetrics(sdates, days, fulldates, username, L,  x, dbindex, popYear);
		double ServiceMiles = Float.parseFloat(serviceMetrics.get("svcmiles"));
		long PopatLOS = (Long.parseLong(serviceMetrics.get("upopatlos"))+Long.parseLong(serviceMetrics.get("rpopatlos")));
		float svcPop = (Float.parseFloat(serviceMetrics.get("uspop"))+Float.parseFloat(serviceMetrics.get("rspop")));
		response.ServiceMiles = serviceMetrics.get("svcmiles");
		response.ServiceHours = serviceMetrics.get("svchours");
		response.ServiceStops = serviceMetrics.get("svcstops");
		response.PopServedAtLoService = String.valueOf(Math.round(10000.0*PopatLOS/geocounts.get("pop"))/100.0);				
		response.PopServedByService = String.valueOf(svcPop);
		String serviceDays = serviceMetrics.get("svcdays");
		if (serviceDays.length()>2){
			serviceDays = serviceDays.replace("\"", "");
			serviceDays= serviceDays.substring(1,serviceDays.length()-1);
			String[] svcdays = serviceDays.split(",");
			serviceDays = StringUtils.join(Arrays.asList(svcdays), ";");
        }
		response.ServiceDays = serviceDays;
		response.MilesofServicePerCapita = (geocounts.get("pop")>0) ? String.valueOf(Math.round((ServiceMiles*10000.00)/geocounts.get("pop"))/10000.00): "NA";		
		response.StopPerServiceMile = (ServiceMiles>0.01)? String.valueOf(Math.round((StopsCount*100)/Float.parseFloat(serviceMetrics.get("svcmiles")))/100.0): "NA";
		response.ServiceMilesPersqMile = (geocounts.get("landarea")>0.01) ? String.valueOf(Math.round((ServiceMiles*258999752.356)/geocounts.get("landarea"))/10000.00):"NA";
		int HOSstart =Integer.parseInt(serviceMetrics.get("fromtime"));
		int HOSend = Integer.parseInt(serviceMetrics.get("totime"));			
        response.HoursOfService = ((HOSstart==-1)?"NA":StringUtils.timefromint(HOSstart))+"-"+ ((HOSend==-1)?"NA":StringUtils.timefromint(HOSend));
        
		long PopWithinX = PgisEventManager.PopWithinX(x, username, dbindex, popYear);
		index++;
		setprogVal(key, (int) Math.round(index*100/totalLoad));
		response.PopWithinX = String.valueOf(PopWithinX);
		response.PopUnServed = String.valueOf(Math.round(1E4-((10000.00*PopWithinX/geocounts.get("pop"))))/100.0);
		response.PopServed = String.valueOf(Math.round((10000.00*PopWithinX/geocounts.get("pop")))/100.00);	
		long[] pop50kCutOff = PgisEventManager.cutOff50(x, username, dbindex, popYear);
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
	 * Generates geographic area Extended reports
	 * types: 0=counties, 1=census tracts, 2=census places, 3=Urban Areas, 4=ODOT Regions, 5=Congressional districts
	 * 
	 */
	    
	@GET
	@Path("/geoAreaXR")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
	public Object getGeoXR(@QueryParam("areaid") String areaId, @QueryParam("type") int type,@QueryParam("day") String date,@QueryParam("x") double x, @QueryParam("l") Integer L, @QueryParam("key") double key, @QueryParam("dbindex") Integer dbindex, @QueryParam("popYear") String popYear, @QueryParam("username") String username) throws JSONException {
		if (Double.isNaN(x) || x <= 0) {
            x = STOP_SEARCH_RADIUS;
        }       			
		if (dbindex==null || dbindex<0 || dbindex>dbsize-1){
       	dbindex = default_dbindex;
        }		
		if (L==null || L<0){
       		L = LEVEL_OF_SERVICE;
       	}
		if(popYear==null||popYear.equals("null")){
			popYear="2010";
		}
		String[] dates = date.split(",");
    	String[][] datedays = daysOfWeekString(dates);
    	String[] fulldates = fulldate(dates);
    	String[] sdates = datedays[0];
    	String[] days = datedays[1];
    	
    	GeoXR response = new GeoXR();
    	GeoArea instance = EventManager.QueryGeoAreabyId(areaId, type, dbindex, Integer.parseInt(popYear));
    	response.metadata = "Report Type:"+instance.getTypeName()+" Extended Report;Report Date:"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+";"+
    	    	"Selected Database:" +Databases.dbnames[dbindex]+";Selected Date(s):"+date+";Population Search Radius(miles):"+String.valueOf(x)+
    	    	";Minimum Level of Service(times):"+String.valueOf(L) + ";" + DbUpdate.VERSION;
    	x = x * 1609.34;
    	int totalLoad = 6;
		int index = 0;
		setprogVal(key, (int) Math.round(index*100/totalLoad));
		response.AreaId = areaId;		
		response.AreaName = instance.getName();			
		
		HashMap<String, Float> FareData =PgisEventManager.FareInfo(type,sdates,days,areaId,username,dbindex);		
		response.MinFare = String.valueOf(FareData.get("minfare"));
		response.AverageFare = String.valueOf(FareData.get("averagefare"));
		response.MaxFare = String.valueOf(FareData.get("maxfare"));
		response.MedianFare = String.valueOf(FareData.get("medianfare"));
				
		index ++;
		setprogVal(key, (int) Math.round(index*100/totalLoad));		
		
		float RouteMiles = PgisEventManager.RouteMiles(type, areaId, username, dbindex);
		response.RouteMiles = String.valueOf(RouteMiles);
		index ++;
		setprogVal(key, (int) Math.round(index*100/totalLoad));
		
		long[] stopspop= PgisEventManager.stopsPop(type,areaId,username,x,dbindex, popYear);
		index ++;
		setprogVal(key, (int) Math.round(index*100/totalLoad));
		response.StopsPersqMile = String.valueOf(Math.round(stopspop[0]*25899752356.00/instance.getLandarea())/10000.00);
		response.PopWithinX = String.valueOf(stopspop[1]+stopspop[2]);
		response.UPopWithinX = String.valueOf(stopspop[1]);
		response.RPopWithinX = String.valueOf(stopspop[2]);
		response.PopServed = String.valueOf(Math.round((10000.00*(stopspop[1]+stopspop[2])/instance.getPopulation()))/100.00);
		response.UPopServed = String.valueOf(Math.round((10000.00*(stopspop[1])/instance.getPopulation()))/100.00);	
		response.RPopServed = String.valueOf(Math.round((10000.00*(stopspop[2])/instance.getPopulation()))/100.00);	
		response.PopUnServed = String.valueOf(Math.round(1E4-((10000.00*(stopspop[1]+stopspop[2])/instance.getPopulation())))/100.0);
		HashMap<String, String> servicemetrics = PgisEventManager.ServiceMetrics(type,sdates,days,fulldates,areaId,username,L,x,dbindex,popYear);
		index ++;
		setprogVal(key, (int) Math.round(index*100/totalLoad));
		double ServiceMiles = Float.parseFloat(servicemetrics.get("svcmiles"));
		long PopatLOS = (Long.parseLong(servicemetrics.get("upopatlos"))+Long.parseLong(servicemetrics.get("rpopatlos")));
		float svcPop = (Float.parseFloat(servicemetrics.get("uspop"))+Float.parseFloat(servicemetrics.get("rspop")));
		response.ServiceMiles = (stopspop[0] != 0) ? servicemetrics.get("svcmiles") :"0";
		response.ServiceHours = servicemetrics.get("svchours");
		response.ServiceStops = servicemetrics.get("svcstops");
		response.PopServedAtLoService = String.valueOf(Math.round(10000.0*PopatLOS/instance.getPopulation())/100.0);
		response.UPopServedAtLoService = String.valueOf(Long.parseLong(servicemetrics.get("upopatlos")));
		response.RPopServedAtLoService = String.valueOf(Long.parseLong(servicemetrics.get("rpopatlos")));
		
		String serviceDays = servicemetrics.get("svcdays");
		if (serviceDays.length()>2){
			serviceDays = serviceDays.replace("\"", "");
			serviceDays= serviceDays.substring(1,serviceDays.length()-1);
			String[] svcdays = serviceDays.split(",");
			serviceDays = StringUtils.join(Arrays.asList(svcdays), ";");
        }
		response.ServiceDays = serviceDays;
		response.MilesofServicePerCapita = (instance.getPopulation()>0 && stopspop[0] != 0) ? String.valueOf(Math.round((ServiceMiles*10000.00)/instance.getPopulation())/10000.00): "0";
		response.StopPerServiceMile = (ServiceMiles>0.01)? String.valueOf(Math.round((stopspop[0]*100)/Float.parseFloat(servicemetrics.get("svcmiles")))/100.0): "0";
		response.ServiceMilesPersqMile = (instance.getLandarea()>0.01 && stopspop[0] != 0) ? String.valueOf(Math.round((ServiceMiles*258999752.356)/instance.getLandarea())/10000.00):"0";
		int HOSstart =Integer.parseInt(servicemetrics.get("fromtime"));
		int HOSend = Integer.parseInt(servicemetrics.get("totime"));			
        response.HoursOfService = ((HOSstart==-1)?"NA":StringUtils.timefromint(HOSstart))+"-"+ ((HOSend==-1)?"NA":StringUtils.timefromint(HOSend));
        /*String connections = servicemetrics.get("connections")+"";
		if (connections.length()>2){
			connections = connections.replace("\"", "");
			connections= connections.substring(1,connections.length()-1);
			String[] conns = connections.split(",");
			connections = StringUtils.join(Arrays.asList(conns), " ;");
        }
		response.ConnectedCommunities = connections;*/
        response.ConnectedCommunities = servicemetrics.get("connections");
		response.PopServedByService = String.valueOf(svcPop);
		response.UPopServedByService = String.valueOf(Float.parseFloat(servicemetrics.get("uspop")));	
		response.RPopServedByService = String.valueOf(Float.parseFloat(servicemetrics.get("rspop")));	
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}        
        progVal.remove(key);		
		return response;
		
    }
		
	/**
	 * Generates The multimodal hubs report
	 */
	@GET
	@Path("/hubs")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
	public Object getHubs(@QueryParam("x1") double x1, @QueryParam("x2") double x2, @QueryParam("popYear") String popYear, @QueryParam("x3") double x3, @QueryParam("key") double key, @QueryParam("day") String date, @QueryParam("dbindex") Integer dbindex, @QueryParam("username") String username) throws JSONException, SQLException {
		if(popYear==null||popYear.equals("null")){
			popYear="2010";
		}
		HubsClusterList response = new HubsClusterList();
		String[] dates = date.split(",");
    	String[][] datedays = daysOfWeekString(dates);
    	String[] fulldates = datedays[0];
    	String[] days = datedays[1];
		x1 *= 1609.34;
		x2 *= 1609.34;
		x3 *= 1609.34;
		HashMap<String, KeyClusterHashMap> y = PgisEventManager.getClusters(x1, dbindex, username);				
		HashMap<String, KeyClusterHashMap> counter = new HashMap<String, KeyClusterHashMap>(y);
		
		for (Entry<String, KeyClusterHashMap> entry:counter.entrySet()){
			if (y.containsKey(entry.getKey())){
				HashSet<String> counter2 = new HashSet<String>(entry.getValue().values);
				for (String stopID: counter2){
					if (!entry.getKey().equals(stopID)){
						y = recurse(y, entry, stopID);
					}						
				}
			}			
		}
		 
		response = getClusterData(y, fulldates, days, dbindex, x2, x3, username, key, popYear);
		response.metadata = "Report Type:Transit Hubs Report;Report Date:"+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime())+";"+
		    	"Selected Database:" +Databases.dbnames[dbindex]+";Stop Cluster Radius(miles):"+String.valueOf(x1) + ";"+";Pop. Search Radius(miles):"+String.valueOf(x2) + ";" + DbUpdate.VERSION;
		return response;
	}
	
	@GET
	@Path("/keyHubs")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
	public Object getKeyHubs(@QueryParam("x1") double x1, @QueryParam("x2") double x2, @QueryParam("popYear") String popYear, @QueryParam("x3") double x3, @QueryParam("key") double key, @QueryParam("day") String date, @QueryParam("dbindex") Integer dbindex, @QueryParam("username") String username) throws JSONException, SQLException {
		if(popYear==null||popYear.equals("null")){
			popYear="2010";
		}

		HubsClusterList response = new HubsClusterList();
		
		String[] dates = date.split(",");
    	String[][] datedays = daysOfWeekString(dates);
    	String[] fulldates = datedays[0];
    	String[] days = datedays[1];
		x1 *= 1609.34;
		x2 *= 1609.34;
		x3 *= 1609.34;
		
		HashMap<String, KeyClusterHashMap> y = PgisEventManager.getClusters(x1, dbindex, username);		
		HashMap<String, KeyClusterHashMap> counter = new HashMap<String, KeyClusterHashMap>(y);
		for (Entry<String, KeyClusterHashMap> entry:counter.entrySet()){
			if (y.containsKey(entry.getKey())){
				HashSet<String> counter2 = new HashSet<String>(entry.getValue().values);
				for (String stopID: counter2){
					if (!entry.getKey().equals(stopID)){
						y = recurse(y, entry, stopID);
					}						
				}
			}		
		}
		
		HashMap<String, KeyClusterHashMap> y1 = new HashMap<String,KeyClusterHashMap>();
		for (Entry<String, KeyClusterHashMap> entry:y.entrySet()){
			if (entry.getValue().keyAgencyIDs.size()>=3){ 
				y1.put(entry.getKey(), entry.getValue());
			}			
		}
		
		HashMap<String, KeyClusterHashMap> h = new HashMap<String, KeyClusterHashMap>();
		for (Entry<String, KeyClusterHashMap> entry:y1.entrySet()){
			if (entry.getValue().keyAgencyIDs.size() >= 3){
				
				ArrayList<String> tempKeyAgencies = new ArrayList<String>();
				tempKeyAgencies.addAll(entry.getValue().keyAgencyIDs);
				entry.getValue().keyAgencyIDs.clear();
				entry.getValue().keyAgencyIDs.addAll(Agencycontainlist(tempKeyAgencies, dbindex));
				if (entry.getValue().keyAgencyIDs.size() >= 3)
					h.put(entry.getKey(), entry.getValue());
				
			}
		}
		System.out.println(h.size());
		response = getClusterData(h, fulldates, days, dbindex, x2, x3, username, key, popYear);
		return response;
    }
	
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
					String[] con_a;
					
					boolean centralize = rs.getBoolean("centralized");
		
					con_a = (String[]) rs.getArray("contained_agencies")
							.getArray();
		if (centralize){
					c_a.addAll(Arrays.asList(con_a));
					c_a.remove(agencies.get(i));
		}
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		ArrayList<String> na = new ArrayList<String>();
		for (String string : new ArrayList<String>(agencies)) {
			if (!c_a.contains(string)) {
				na.add(string);

			}
		}
		PgisEventManager.dropConnection(connection);
		return na;
	}
	public HubsClusterList getClusterData(HashMap<String, KeyClusterHashMap> x, String[] dates, String[] days, final int dbindex, final double popRadius, final double pnrRadius, String username, final double key, final String popYear) throws SQLException{
		HubsClusterList output = new HubsClusterList();
    	int progress = 0;
    	setprogVal(key, 5);
		final HashMap<String, Integer> serviceMap = PgisEventManager.stopFrequency(null, dates, days, username, dbindex);
		
		setprogVal(key, 10);
    	int totalLoad = x.entrySet().size();
    	HashMap<String, KeyClusterHashMap> first = new HashMap<String, KeyClusterHashMap>();
    	HashMap<String, KeyClusterHashMap> second = new HashMap<String, KeyClusterHashMap>();
    	HashMap<String, KeyClusterHashMap> third = new HashMap<String, KeyClusterHashMap>();
    	HashMap<String, KeyClusterHashMap> forth = new HashMap<String, KeyClusterHashMap>();
    	int b = 0;
    	for (Entry<String, KeyClusterHashMap> e: x.entrySet()) {
    	  if (b==0)
    		  first.put(e.getKey(), e.getValue());
    	  else if(b==1)
    		  second.put(e.getKey(), e.getValue());
    	  else if(b==2)
    		  third.put(e.getKey(), e.getValue());
    	  else{
    		  forth.put(e.getKey(), e.getValue());
			  b=0;
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
    		public int threadProgress=0;
    		
		   
    		fillClusters(String name, HashMap<String, KeyClusterHashMap> set, HubsClusterList output){
    			threadName = name;
    			threadSet = set;
    			tOutput = output;
    		}
    		
			public void run() {
				String query;
				Connection connection = PgisEventManager.makeConnection(dbindex);
				try {
					Statement stmt = connection.createStatement();
					for (Entry<String, KeyClusterHashMap> entry : threadSet.entrySet()){
						threadProgress++;
						HubCluster response = new HubCluster();
						List<String> agencyids = new ArrayList<String>();
						List<String> stopids = new ArrayList<String>();
						int visits = 0;
						for(String instance : entry.getValue().values){
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
								+ "routesarray0 AS (SELECT routes.agencyid, routes.id, routes.shortname, routes.longname, gtfs_agencies.name AS agencyname "
								+ "	FROM routes INNER JOIN gtfs_agencies "
								+ "	ON routes.agencyid = gtfs_agencies.id "
								+ "	GROUP BY routes.agencyid, routes.id, routes.shortname, routes.longname, agencyname), "
								+ "routesarray  AS (SELECT COALESCE(array_agg(agencyid),'{N/A}') AS routesagencies, COALESCE(array_agg(agencyname),'{N/A}') AS routesagenciesnames, COALESCE(array_agg(id),'{N/A}') AS routesids, COALESCE(array_agg(shortname),'{N/A}') AS routesshortnames, COALESCE(array_agg(longname),'{N/A}') AS routeslongnames FROM routesarray0),  "
								+ "routescount AS (SELECT count(distinct agencyid||id) AS routescount FROM routes), "
								+ "counties AS (SELECT counties.countyid, counties.cname FROM census_counties AS counties INNER JOIN stops ON LEFT(stops.blockid,5) = counties.countyid), "
								+ "countiesarray AS (SELECT COALESCE(array_agg(distinct countyid),'{N/A}') AS countiesids, COALESCE(array_agg(distinct cname),'{N/A}') AS countiesnames FROM counties), "
								+ "countiescount AS (SELECT count(distinct counties.countyid) AS countiescount FROM counties), "
								+ "urbanarray AS (SELECT COALESCE(sum(distinct population"+popYear+")::text, 'N/A') AS urbanareaspop, COALESCE(array_agg(distinct stops.urbanid),'{N/A}') AS urbanids, COALESCE(array_agg(distinct uname),'{N/A}') AS urbannames FROM census_urbans INNER JOIN stops ON census_urbans.urbanid = stops.urbanid),"
								+ "regionsarray  AS (SELECT  COALESCE(array_agg(distinct ' Region '||regionid),'{N/A}') AS regionids FROM stops),"
								+ "pop0 AS (SELECT distinct census_blocks.blockid, population"+popYear+" as population FROM census_blocks INNER JOIN stops ON ST_Dwithin(census_blocks.location, stops.location, " + popRadius + ")), "
								+ "pop AS (SELECT COALESCE(sum(population),0) AS pop FROM pop0),"
								+ "pnr AS (SELECT pr.* FROM parknride AS pr INNER JOIN clustercoor ON ST_Dwithin(pr.geom,ST_transform(ST_setsrid(ST_MakePoint(clustercoor.lon, clustercoor.lat),4326), 2993)," + pnrRadius + ")), "
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
								+ " stopsarray.*, routesarray.*,pnrarray.*, placesarray.*"
								+ "	FROM stopsarray CROSS JOIN stopscount CROSS JOIN agenciesarray CROSS JOIN routesarray"
								+ "	CROSS JOIN routescount CROSS JOIN countiescount CROSS JOIN countiesarray CROSS JOIN pop"
								+ "	CROSS JOIN clustercoor CROSS JOIN urbanarray CROSS JOIN regionsarray CROSS JOIN agenciescount"
								+ "	CROSS JOIN pnrarray CROSS JOIN placesarray";
	//					System.out.println(query);	
						
						ResultSet rs = stmt.executeQuery(query);
							
						while(rs.next()){
							response.lat = rs.getString("lat");
							response.lon = rs.getString("lon");
							response.agenciescount = rs.getString("agenciescount");
							response.countiescount = rs.getString("countiescount");
							response.pop = rs.getString("pop");
							response.stopscount = rs.getString("stopscount");
							response.routescount = rs.getString("routescount");
							response.pnrcount = rs.getInt("pnrcount");
							response.placescount = rs.getInt("placescount");
							response.urbanareaspop = rs.getString("urbanareaspop");
							response.visits = visits + "";
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
							Integer[] tempPnrIDs = (Integer[]) rs.getArray("pnrids").getArray();
							response.pnrIDs =  Arrays.asList(tempPnrIDs);
							Double[] tempPnrLats = (Double[]) rs.getArray("pnrlats").getArray();
							response.pnrLats =  Arrays.asList(tempPnrLats);
							Double[] tempPnrLons = (Double[]) rs.getArray("pnrlons").getArray();
							response.pnrLons =  Arrays.asList(tempPnrLons);
							String[] tempPnrNames = (String[]) rs.getArray("pnrnames").getArray();
							response.pnrNames = Arrays.asList(tempPnrNames);
							String[] tempPnrCities = (String[]) rs.getArray("pnrnames").getArray();
							response.pnrCities = Arrays.asList(tempPnrCities);
							Integer[] tempPnrSpaces = (Integer[]) rs.getArray("pnrspaces").getArray();
							response.pnrSpaces = Arrays.asList(tempPnrSpaces);
							String[] tempPlacesIDs = (String[]) rs.getArray("placesids").getArray();
							response.placesIDs = Arrays.asList(tempPlacesIDs);
							String[] tempPlacesNames = (String[]) rs.getArray("placesnames").getArray();
							response.placesNames = Arrays.asList(tempPlacesNames);
							tOutput.Clusters.add(response);
						}
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
						
				PgisEventManager.dropConnection(connection);
				bool=false;
			}
			
			public void start (){
				if (t == null){
					t = new Thread (this, threadName);
					t.start ();
				}
		   }
    		
    	}
		
    	fillClusters fc1 = new fillClusters( "Thread-1", first, output);
        fc1.start();
        
        fillClusters fc2 = new fillClusters( "Thread-2", second, output);
        fc2.start();
        
        fillClusters fc3 = new fillClusters( "Thread-2", third, output);
        fc3.start();
        
        fillClusters fc4 = new fillClusters( "Thread-2", forth, output);
        fc4.start();
        
        while(fc1.bool || fc2.bool || fc3.bool || fc4.bool){
        	progress=fc1.threadProgress+fc2.threadProgress+fc3.threadProgress+fc4.threadProgress;
        	setprogVal(key, 10+((int) Math.round(progress*90/totalLoad)));
        	try {
    			Thread.sleep(500);
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		} 
        }
		       
        progVal.remove(key);
		return output;
	}
	
	private HashMap<String, KeyClusterHashMap> recurse(HashMap<String, KeyClusterHashMap> y, Entry<String, KeyClusterHashMap> entry, String stopID){
		if (y.containsKey(stopID)){
			HashSet<String> tmpStopsSet = y.get(stopID).values;
			HashSet<String> tmpAgencyIDs = y.get(stopID).keyAgencyIDs;
			y.get(entry.getKey()).values.addAll(tmpStopsSet);
			y.get(entry.getKey()).keyAgencyIDs.addAll(tmpAgencyIDs);
			y.remove(stopID);
			for (String x: tmpStopsSet){
				if(!entry.getKey().equals(x))
					y = recurse(y, entry, x);
			}
		}
		return y;
	}
	
	@GET
    @Path("/connectivityGraph")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
	public Object connectivityGraph(@QueryParam("x") Double x, @QueryParam("day") String date, @QueryParam("username") String session, @QueryParam("dbindex") Integer dbindex) throws SQLException{
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
		
		//Retrieving the list of agencies and putting the IDs into an array.
		HashMap<String, ConGraphAgency> agencies = SpatialEventManager.getAllAgencies(session, dbindex);
		
		ConGraphObjSet response = new ConGraphObjSet();
		Set<ConGraphObj> e = new HashSet<ConGraphObj>();
		for (Entry<String, ConGraphAgency> i : agencies.entrySet()){
			e = SpatialEventManager.getConGraphObj(i.getKey(), i.getValue().name, fulldate, day, session, x, stmt);
			response.set.addAll(e);
		}
		
		connection.close();
		return response;
	}
	
	@GET
    @Path("/agencyCentriods")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
    public Object getAgencyCentroids(@QueryParam("username") String username, @QueryParam("dbindex") Integer dbindex) throws SQLException{
		// Making connection to DB
		Connection connection = PgisEventManager.makeConnection(dbindex);
		Statement stmt = connection.createStatement();
		ConGraphAgencyGraphList response = new ConGraphAgencyGraphList();
		
		Map<String,ConGraphAgency> agencies = SpatialEventManager.getAllAgencies(username, dbindex);
		for (Entry<String,ConGraphAgency> e : agencies.entrySet() ){
			if (!e.getValue().centralized){
				ConGraphAgencyGraph i = SpatialEventManager.getAgencyCentroids(e.getKey(), stmt, 100);
				i.centralized = e.getValue().centralized;
				response.list.add(i);
			}else {
				ConGraphAgencyGraph i = SpatialEventManager.getAgencyCentroids(e.getKey(), stmt, 100);
				i.centralized = e.getValue().centralized;
				response.list.add(i);
			}
		}
		connection.close();
		return response;
	}
	
	@GET
    @Path("/agencyCentriods2")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
    public Object getAgencyCentroids2(@QueryParam("username") String username, @QueryParam("dbindex") Integer dbindex) throws SQLException{
		// Making connection to DB
		Connection connection = PgisEventManager.makeConnection(dbindex);
		Statement stmt = connection.createStatement();
		AgencyCentroidList response = new AgencyCentroidList();
		
		Map<String,ConGraphAgency> agencies = SpatialEventManager.getAllAgencies(username, dbindex);
		for (Entry<String,ConGraphAgency> e : agencies.entrySet() ){
			
				AgencyCentroid i = SpatialEventManager.getAgencyCentroid(e.getKey(), stmt);
//				if (i.lat!=0.0)
					response.list.add(i);
			}
		
		connection.close();
		return response;
	}
	
	@GET
    @Path("/allAgencies")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
    public Object getAllAgencies(@QueryParam("username") String username, @QueryParam("dbindex") Integer dbindex) throws SQLException{
		HashMap<String,ConGraphAgency> response = new HashMap<String, ConGraphAgency>();
		try{
			response = SpatialEventManager.getAllAgencies(username, dbindex);
		}catch(SQLException e){
			System.out.println(e);
		}
		return response;
	}
	
	/**
     * Get calendar range for a set of agencies
     */
    @GET
    @Path("/agenciesCalendarRange")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
    public Object agenciesCalendarRange(@QueryParam("agencies") String agency, @QueryParam("dbindex") Integer dbindex){
    	if (dbindex==null || dbindex<0 || dbindex>dbsize-1){
        	dbindex = default_dbindex;
        }
    	StartEndDatesList sedlist = new StartEndDatesList();
    	String[] agencies = agency.split(",");
    	StartEndDates seDates;
    	String defaultAgency;
    	FeedInfoExt feed;
    	AgencyExt ag;
    	try{
	    	for(String a:agencies){
	    		seDates = new StartEndDates();
	    		ag = GtfsHibernateReaderExampleMain.QueryAgencybyid(a, dbindex);
	    		defaultAgency = ag.getDefaultId();//to be fixed
	        	feed = GtfsHibernateReaderExampleMain.QueryFeedInfoByDefAgencyId(defaultAgency, dbindex).get(0);
	        	seDates.Startdate = feed.getStartDate().getAsString();
	        	seDates.Enddate = feed.getEndDate().getAsString();
	        	seDates.Agency = ag.getName();
	        	sedlist.SEDList.add(seDates);
	    	}
    	}catch(Exception e){
    		System.out.println(e);
    	}
		return sedlist;
    }
    
    /**
     * Get overall calendar range
     */
    @GET
    @Path("/calendarRange")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
    public Object calendarRange(@QueryParam("agency") String agency,@QueryParam("username") String username,@QueryParam("dbindex") Integer dbindex){
    	if (dbindex==null || dbindex<0 || dbindex>dbsize-1){
        	dbindex = default_dbindex;
        }
    	if (username==null)
    		username = "admin";
    	if (agency!=null){
	    	if (agency.equals("null")||agency.equals("")||agency.equals("undefined"))
	    		agency = null;
    	}
		StartEndDates response = PgisEventManager.getsedates(username, agency, dbindex);    	
		return response;
    }	
}
