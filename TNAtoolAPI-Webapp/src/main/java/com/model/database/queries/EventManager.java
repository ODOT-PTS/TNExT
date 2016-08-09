package com.model.database.queries;

import org.hibernate.Session;
import org.hibernate.Query;
import org.hibernate.type.Type;
import org.hibernatespatial.GeometryUserType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.model.database.queries.util.Hutil;
import com.model.database.queries.util.Types;
import com.model.database.queries.objects.*;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;



public class EventManager {		
private	static Session[] session = new Session[Hutil.getSessionFactory().length];
static{
	for (int scnt=0; scnt< session.length; scnt++){
		session[scnt] = Hutil.getSessionFactory()[scnt].openSession();
	}
};


/**
 * returns trip data and shape
 *//*
	public static Geotrip getTripData(AgencyAndId id) throws FactoryException, TransformException {			
		session.beginTransaction();
		Query q = session.getNamedQuery("SHAPE_BY_TRIP");		
		q.setParameter("id", id);
		@SuppressWarnings("unchecked")
		List<Geotrip> results = (List<Geotrip>) q.list();
        Hutil.getSessionFactory().close();
        return results.get(0);
    }*/

/**
 * returns ParknRides within a circle
 */
	/*public static List<ParknRide> getPnRs(double radius, double lat, double lon, int sessionindex, int dbindex) throws FactoryException, TransformException {
		CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
		CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:2993");
		MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();			
		Point point = geometryFactory.createPoint(new Coordinate(lat, lon));
		Geometry targetGeometry = JTS.transform( point, transform);
		point = targetGeometry.getCentroid();
		point.setSRID(2993);	
		session[sessionindex].beginTransaction();
		
		Query q = session[sessionindex].getNamedQuery("PARKNRIDE_WITHIN_CIRCLE");
		Type geomType = GeometryUserType.TYPE;
		q.setParameter("point", point, geomType);
		q.setParameter("radius", radius);
		@SuppressWarnings("unchecked")
		List<ParknRide> results = (List<ParknRide>) q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return results;
    }*/
	
/**
 * returns ParknRides within a rectangle
 */	
	/*public static List<ParknRide> getPnRs(double[] lat, double[] lon, int sessionindex) throws FactoryException, TransformException {			

		CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
		CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:2993");
		MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		Coordinate[] coords = new Coordinate[lat.length+1];
		for(int i=0;i<lat.length;i++){
			coords[i]= new Coordinate(lat[i], lon[i]);
		}
		coords[coords.length-1]= new Coordinate(lat[0], lon[0]);
		LinearRing ring = geometryFactory.createLinearRing( coords );
		LinearRing holes[] = null; 
		Polygon polygon = geometryFactory.createPolygon(ring, holes );
		//Point point = geometryFactory.createPoint(new Coordinate(lat, lon));
		Geometry targetGeometry = JTS.transform( polygon, transform);
		//point = geometryFactory.createPoint(targetGeometry.getCoordinate());
		//point = targetGeometry.getCentroid();
		targetGeometry.setSRID(2993);	
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("PARKNRIDE_WITHIN_RECTANGLE");
		Type geomType = GeometryUserType.TYPE;
		q.setParameter("polygon", targetGeometry, geomType);
		//q.setParameter("radius", d);
		@SuppressWarnings("unchecked")
		List<ParknRide> results = (List<ParknRide>) q.list();
        Hutil.getSessionFactory()[sessionindex].close();
//        List<ParknRide> results=new ArrayList<ParknRide>();
        
        return results;
    }*/	

/**
 * returns population centroids
 */
	public static List<Census> getcentroids(double d, double lat, double lon, int sessionindex) throws FactoryException, TransformException {
		CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
		CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:2993");
		MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();			
		Point point = geometryFactory.createPoint(new Coordinate(lat, lon));
		Geometry targetGeometry = JTS.transform( point, transform);
		//point = geometryFactory.createPoint(targetGeometry.getCoordinate());
		point = targetGeometry.getCentroid();
		point.setSRID(2993);	
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("CENSUS_BY_COORDINATES");
		Type geomType = GeometryUserType.TYPE;
		q.setParameter("point", point, geomType);
		q.setParameter("radius", d);
		@SuppressWarnings("unchecked")
		List<Census> results = (List<Census>) q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return results;
    }
	
/**
 * returns centroids within a rectangle
 */
	public static List<Census> getcentroidswithinrectangle(double[] lat, double[] lon, int sessionindex) throws FactoryException, TransformException {			
		CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
		CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:2993");
		MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		Coordinate[] coords = new Coordinate[lat.length+1];
		for(int i=0;i<lat.length;i++){
			coords[i]= new Coordinate(lat[i], lon[i]);
		}
		coords[coords.length-1]= new Coordinate(lat[0], lon[0]);
		LinearRing ring = geometryFactory.createLinearRing( coords );
		LinearRing holes[] = null; 
		Polygon polygon = geometryFactory.createPolygon(ring, holes );
		//Point point = geometryFactory.createPoint(new Coordinate(lat, lon));
		Geometry targetGeometry = JTS.transform( polygon, transform);
		//point = geometryFactory.createPoint(targetGeometry.getCoordinate());
		//point = targetGeometry.getCentroid();
		targetGeometry.setSRID(2993);	
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("CENSUS_WITHIN_RECTANGLE");
		Type geomType = GeometryUserType.TYPE;
		q.setParameter("polygon", targetGeometry, geomType);
		//q.setParameter("radius", d);
		@SuppressWarnings("unchecked")
		List<Census> results = (List<Census>) q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return results;
    }	
	
/**
 * returns stops within a circle
 */
	public static List<GeoStop> getstopswithincircle(double d, double lat, double lon, int sessionindex) throws FactoryException, TransformException {			
		CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
		CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:2993");
		MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();			
		Point point = geometryFactory.createPoint(new Coordinate(lat, lon));
		Geometry targetGeometry = JTS.transform( point, transform);
		//point = geometryFactory.createPoint(targetGeometry.getCoordinate());
		point = targetGeometry.getCentroid();
		point.setSRID(2993);	
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("STOP_BY_COORDINATES");
		Type geomType = GeometryUserType.TYPE;
		q.setParameter("point", point, geomType);
		q.setParameter("radius", d);
		
		@SuppressWarnings("unchecked")
		List<GeoStop> results = (List<GeoStop>) q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return results;
    }
	
	/**
	 * returns stops within a circle for selected list of agencies
	 */
		public static List<GeoStop> getstopswithincircle2(double d, double lat, double lon, int sessionindex, List<String> agencyList) throws FactoryException, TransformException {			
			CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
			CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:2993");
			MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
			GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();			
			Point point = geometryFactory.createPoint(new Coordinate(lat, lon));
			Geometry targetGeometry = JTS.transform( point, transform);
			//point = geometryFactory.createPoint(targetGeometry.getCoordinate());
			point = targetGeometry.getCentroid();
			point.setSRID(2993);	
			session[sessionindex].beginTransaction();
			Query q = session[sessionindex].getNamedQuery("STOP_BY_COORDINATES_SEL_AGENCIES");
			Type geomType = GeometryUserType.TYPE;
			q.setParameter("point", point, geomType);
			q.setParameter("radius", d);
			q.setParameterList("sa", agencyList);
			@SuppressWarnings("unchecked")
			List<GeoStop> results = (List<GeoStop>) q.list();
	        Hutil.getSessionFactory()[sessionindex].close();
	        return results;
	    }
	
	/**
	 * returns stops within a rectangle
	 */
		public static List<GeoStop> getstopswithinrectangle(double[] lat, double[] lon, int sessionindex) throws FactoryException, TransformException {			
			CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
			CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:2993");
			MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
			GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
			Coordinate[] coords = new Coordinate[lat.length+1];
			for(int i=0;i<lat.length;i++){
				coords[i]= new Coordinate(lat[i], lon[i]);
			}
			coords[coords.length-1]= new Coordinate(lat[0], lon[0]);
			LinearRing ring = geometryFactory.createLinearRing( coords );
			LinearRing holes[] = null; 
			Polygon polygon = geometryFactory.createPolygon(ring, holes );
			//Point point = geometryFactory.createPoint(new Coordinate(lat, lon));
			Geometry targetGeometry = JTS.transform( polygon, transform);
			//point = geometryFactory.createPoint(targetGeometry.getCoordinate());
			//point = targetGeometry.getCentroid();
			targetGeometry.setSRID(2993);	
			session[sessionindex].beginTransaction();
			Query q = session[sessionindex].getNamedQuery("STOP_WITHIN_RECTANGLE");
			Type geomType = GeometryUserType.TYPE;
			q.setParameter("polygon", targetGeometry, geomType);
			//q.setParameter("radius", d);
			@SuppressWarnings("unchecked")
			List<GeoStop> results = (List<GeoStop>) q.list();
	        Hutil.getSessionFactory()[sessionindex].close();
	        return results;
	    }	
	
/**
 * returns route for a given stop
 */
	public static List<GeoStopRouteMap> getroutebystop(String id, String agency, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("ROUTE_BY_STOP");
		q.setParameter("id", id).setParameter("agency", agency);
		@SuppressWarnings("unchecked")
		List<GeoStopRouteMap> result = (List<GeoStopRouteMap>) q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	}

/**
 * returns list of stop_route_map
 */
	public static List<GeoStopRouteMap> getstoproutemaps(int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("All_STOP_ROUTE_MAPS");
		@SuppressWarnings("unchecked")
		List<GeoStopRouteMap> results = q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return results;
    }
/**
 * returns number of all geo areas in the DB: keys are : county, tract, place, urban, congdist, region, pop, landarea, urbanpop, ruralpop
 */
	public static HashMap<String, Long> getGeoCounts(int sessionindex, String popYear) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		//Query q = session[sessionindex].getNamedQuery("GEO_COUNTS");
		//q.setParameter("popYear","population"+popYear);
		//@SuppressWarnings("unchecked")
		String hql = "select (select count(countyId) from County) as county, (select count(tractId) from Tract) as tract, "
				+ "(select count(placeId) from Place) as place, (select count(urbanId) from Urban) as urban, "
				+ "(select count(congdistId) from CongDist) as congdist, (select count(distinct regionId) from County) as region, "
				+ "sum(population"+popYear+"), sum(landarea), (select sum(population"+popYear+") from Census where poptype='U'), "
				+ "(select sum(population"+popYear+") from Census where poptype='R') from County";
		Query q = session[sessionindex].createQuery(hql);
		List results = q.list();
		Object[] counts = (Object[]) results.get(0);
		HashMap<String, Long> response = new HashMap<String, Long>();
		response.put("county", (Long)counts[0]);
		response.put("tract", (Long)counts[1]);
		response.put("place", (Long)counts[2]);
		response.put("urban", (Long)counts[3]);
		response.put("congdist", (Long)counts[4]);
		response.put("region", (Long)counts[5]);
		response.put("pop", (Long)counts[6]);
		response.put("landarea", (Long)counts[7]);
		response.put("urbanpop", (Long)counts[8]);
		response.put("ruralpop", (Long)counts[9]);
        Hutil.getSessionFactory()[sessionindex].close();
        return response;
    }	
	
/**
 * returns list of counties
 */
	public static List<County> getcounties(int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("All_COUNTIES");
		@SuppressWarnings("unchecked")
		List<County> results = q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return results;
    }
	
/**
 * returns list of tracts
 */
	public static List<Tract> gettracts(int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("All_TRACTS");
		@SuppressWarnings("unchecked")
		List<Tract> results = q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return results;
    }
	
/**
 * returns list of census places
 */
	public static List<Place> getplaces(int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("All_PLACES");
		@SuppressWarnings("unchecked")
		List<Place> results = (List<Place>) q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return results;
    }
	
/**
 * returns list of urban areas
 */
	public static List<Urban> geturban(int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("All_URBANS");
		@SuppressWarnings("unchecked")
		List<Urban> results = (List<Urban>) q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return results;
    }
	
/**
 * returns list of urban areas with population greater than pop
 */
	public static List<Urban> geturbansbypop(int pop, int sessionindex, String popYear) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		//Query q = session[sessionindex].getNamedQuery("URBANS_BYPOP");
		//q.setParameter("pop", (long)pop);
		//@SuppressWarnings("unchecked")
		String hql = "from Urban where population"+popYear+" >= "+pop;
		Query q = session[sessionindex].createQuery(hql);
		List<Urban> results = (List<Urban>) q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return results;
    }

/**
 * returns list of urban areas
 */
	public static List<CongDist> getcongdist(int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("All_CONGDISTS");
		@SuppressWarnings("unchecked")
		List<CongDist> results = (List<CongDist>) q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return results;
    }
	
/**
 * returns number of tracts for a given county
 */
	public static long gettractscountbycounty(String countyId, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("TRACTSNO_BY_COUNTY");
		q.setParameter("id", countyId);
		//@SuppressWarnings("unchecked")
		long result = (Long) q.list().get(0);
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }

/**
 * returns number of census blocks for a given county
 */
	public static long getblockscountbytract(String tractId, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("BLOCKSNO_BY_TRACT");
		q.setParameter("id", tractId);
		//@SuppressWarnings("unchecked")
		long result = (Long) q.list().get(0);
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }
	
/**
 * returns number of stops for a given county
 */
	public static long getstopscountbycounty(String countyId, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("STOPS_BY_COUNTY");
		q.setParameter("id", countyId);
		//@SuppressWarnings("unchecked")
		long result = (Long) q.list().get(0);
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }
	
	public static long getstopscountbycounty(String countyId, List<String> selectedAgencies, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("STOPS_BY_COUNTY_SEL_AGENCIES");
		q.setParameter("id", countyId);
		q.setParameterList("sa", selectedAgencies);
		//@SuppressWarnings("unchecked")
		long result = (Long) q.list().get(0);
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }
	
/**
 * returns list of stops for a given county
 */
	public static List<GeoStop> getstopsbycounty(String countyId, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("STOPSL_BY_COUNTY");
		q.setParameter("id", countyId);
		@SuppressWarnings("unchecked")
		List<GeoStop> result = (List<GeoStop>) q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }
/**
 * returns list of stops for a given county
 */
	public static List<GeoStop> getstopsbyregion(String regionId, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("STOPSL_BY_REGION");
		q.setParameter("id", regionId);
		@SuppressWarnings("unchecked")
		List<GeoStop> result = (List<GeoStop>) q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }
/**
 * returns list of trips for a given county
 */
	public static List<CountyTripMap> gettripsbycounty(String countyId, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("TRIPS_BY_COUNTY");
		q.setParameter("id", countyId);
		@SuppressWarnings("unchecked")
		List<CountyTripMap> result = (List<CountyTripMap>) q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }
/**
 * returns list of trips for a given odot transit region
 */
	public static List<CountyTripMap> gettripsbyregion(String regionId, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("TRIPS_BY_REGION");
		q.setParameter("id", regionId);
		@SuppressWarnings("unchecked")
		List<CountyTripMap> result = (List<CountyTripMap>) q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }
	
/**
 * returns list of trips for a given census tracts
 */
	public static List<TractTripMap> gettripsbytract(String tractId, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("TRIPS_BY_TRACT");
		q.setParameter("id", tractId);
		@SuppressWarnings("unchecked")
		List<TractTripMap> result = (List<TractTripMap>) q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }
/**
 * returns list of trips for a given census places
 */
	public static List<PlaceTripMap> gettripsbyplace(String placeId, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("TRIPS_BY_PLACE");
		q.setParameter("id", placeId);
		@SuppressWarnings("unchecked")
		List<PlaceTripMap> result = (List<PlaceTripMap>) q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }

/**
 * returns list of trips for a given urban area
 */
	public static List<UrbanTripMap> gettripsbyurban(String urbanId, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("TRIPS_BY_URBAN");
		q.setParameter("id", urbanId);
		@SuppressWarnings("unchecked")
		List<UrbanTripMap> result = (List<UrbanTripMap>) q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }
	
/**
 * returns list of trips for urban areas with population greater than pop 
 */
	public static List<UrbanTripMap> gettripsbyurbanpop(int pop, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("TRIPS_BY_URBANPOP");
		q.setParameter("pop", (long)pop);
		@SuppressWarnings("unchecked")
		List<UrbanTripMap> result = (List<UrbanTripMap>) q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }	
/**
 * returns list of trips for a given congressional district
 */
	public static List<CongdistTripMap> gettripsbycongdist(String congdistId, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("TRIPS_BY_CONGDIST");
		q.setParameter("id", congdistId);
		@SuppressWarnings("unchecked")
		List<CongdistTripMap> result = (List<CongdistTripMap>) q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }
/**
 * returns list of all connected counties for a given county
 */
	public static List<String> getconnectedcounties(String countyId, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("CONNECTED_COUNTIES");
		q.setParameter("id", countyId);
		@SuppressWarnings("unchecked")
		List<String> result = (List<String>) q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }
	
/**
 * returns list of all connected Tracts for a given census tract
 */
	public static List<String> getconnectedtracts(String tractId, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("CONNECTED_TRACTS");
		q.setParameter("id", tractId);
		@SuppressWarnings("unchecked")
		List<String> result = (List<String>) q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }
	
/**
 * returns list of all connected places for a given census place
 */
	public static List<String> getconnectedplaces(String placeId, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("CONNECTED_PLACES");
		q.setParameter("id", placeId);
		@SuppressWarnings("unchecked")
		List<String> result = (List<String>) q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }
	
/**
 * returns list of all connected urban areas for a given urban area
 */
	public static List<String> getconnectedurbans(String urbanId, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("CONNECTED_URBANS");
		q.setParameter("id", urbanId);
		@SuppressWarnings("unchecked")
		List<String> result = (List<String>) q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }
	
/**
 * returns list of all connected urban areas for a set of urban areas with population larger than pop
 */
	public static List<String> getconnectedurbansbypop(int pop, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("CONNECTED_URBANS_BYPOP");
		q.setParameter("pop", (long)pop);
		@SuppressWarnings("unchecked")
		List<String> result = (List<String>) q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }	
	
/**
 * returns list of all connected congressional districts for a given congressional district
 */
	public static List<String> getconnectedcongdists(String congdistId, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("CONNECTED_CONGDISTS");
		q.setParameter("id", congdistId);
		@SuppressWarnings("unchecked")
		List<String> result = (List<String>) q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }
	
/**
 * returns list of all connected ODOT Transit Regions for a given region
 */
	public static List<String> getconnectedregions(String regionId, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("CONNECTED_REGIONS");
		q.setParameter("id", regionId);
		@SuppressWarnings("unchecked")
		List<String> result = (List<String>) q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }
/**
 * returns number of stops for a given ODOT Region
 */
	public static long getstopscountbyregion(String regionId, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("STOPS_BY_REGION");
		q.setParameter("id", regionId);
		//@SuppressWarnings("unchecked")
		long result = (Long) q.list().get(0);
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }
	
	public static long getstopscountbyregion(String regionId, List<String> selectedAgencies, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("STOPS_BY_REGION_SEL_AGENCIES");
		q.setParameter("id", regionId);
		q.setParameterList("sa", selectedAgencies);
		//@SuppressWarnings("unchecked")
		long result = (Long) q.list().get(0);
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }
/**
 * returns number of stops for a given census tract
 */
	public static long getstopscountbytract(String tractId, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("STOPS_BY_TRACT");
		q.setParameter("id", tractId);
		//@SuppressWarnings("unchecked")
		long result = (Long) q.list().get(0);
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }
	
	public static long getstopscountbytract(String tractId, List<String> selectedAgencies, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("STOPS_BY_TRACT_SEL_AGENCIES");
		q.setParameter("id", tractId);
		q.setParameterList("sa", selectedAgencies);
		//@SuppressWarnings("unchecked")
		long result = (Long) q.list().get(0);
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }
/**
 * returns list of stops for a given census tract
 */
	public static List<GeoStop> getstopsbytract(String tractId, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("STOPSL_BY_TRACT");
		q.setParameter("id", tractId);
		@SuppressWarnings("unchecked")
		List<GeoStop> result = (List<GeoStop>) q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }
		
/**
 * returns list of stops for a given census place
 */
	public static List<GeoStop> getstopsbyplace(String placeId, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("STOPSL_BY_PLACE");
		q.setParameter("id", placeId);
		@SuppressWarnings("unchecked")
		List<GeoStop> result = (List<GeoStop>) q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }

/**
 * returns list of stops for a given urban area
 */
	public static List<GeoStop> getstopsbyurban(String urbanId, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("STOPSL_BY_URBAN");
		q.setParameter("id", urbanId);
		@SuppressWarnings("unchecked")
		List<GeoStop> result = (List<GeoStop>) q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }
/**
 * returns list of stops for a given urban area
 */
	public static List<GeoStop> getstopsbyurbanpop(int pop, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("STOPSL_BY_URBANPOP");
		q.setParameter("pop", (long)pop);
		@SuppressWarnings("unchecked")
		List<GeoStop> result = (List<GeoStop>) q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }
	
/**
 * returns list of stops for a given congressional district
 */
	public static List<GeoStop> getstopsbycongdist(String congdistId, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("STOPSL_BY_CONGDIST");
		q.setParameter("id", congdistId);
		@SuppressWarnings("unchecked")
		List<GeoStop> result = (List<GeoStop>) q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }
/**
 * returns number of stops for a given census place
 */
	public static long getstopscountbyplace(String placeId, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("STOPS_BY_PLACE");
		q.setParameter("id", placeId);
		//@SuppressWarnings("unchecked")
		long result = (Long) q.list().get(0);
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }
	
	public static long getstopscountbyplace(String placeId, List<String> selectedAgencies, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("STOPS_BY_PLACE_SEL_AGENCIES");
		q.setParameter("id", placeId);
		q.setParameterList("sa", selectedAgencies);
		//@SuppressWarnings("unchecked")
		long result = (Long) q.list().get(0);
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }
	
/**
 * returns number of stops for a given urban area
 */
	public static long getstopscountbyurban(String urbanId, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("STOPS_BY_URBAN");
		q.setParameter("id", urbanId);
		//@SuppressWarnings("unchecked")
		long result = (Long) q.list().get(0);
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }
	
	public static long getstopscountbyurban(String urbanId, List<String> selectedAgencies, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("STOPS_BY_URBAN_SEL_AGENCIES");
		q.setParameter("id", urbanId);
		q.setParameterList("sa", selectedAgencies);
		//@SuppressWarnings("unchecked")
		long result = (Long) q.list().get(0);
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }
	
/**
 * returns number of stops for a given congressional district
 */
	public static long getstopscountbycongdist(String congdistId, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("STOPS_BY_CONGDIST");
		q.setParameter("id", congdistId);
		//@SuppressWarnings("unchecked")
		long result = (Long) q.list().get(0);
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }
	
	public static long getstopscountbycongdist(String congdistId, List<String> selectedAgencies, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("STOPS_BY_CONGDIST_SEL_AGENCIES");
		q.setParameter("id", congdistId);
		q.setParameterList("sa", selectedAgencies);
		//@SuppressWarnings("unchecked")
		long result = (Long) q.list().get(0);
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }
/**
 * returns list of routes for a given ODOT region
 */
	public static int getroutescountsbyregion(String regionId, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("ROUTES_BY_REGION");
		q.setParameter("id", regionId);
		@SuppressWarnings("unchecked")
		List<GeoStopRouteMap> result = q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result.size();
	    }
	
	public static int getroutescountsbyregion(String regionId, List<String> selectedAgencies, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("ROUTES_BY_REGION_SEL_AGENCIES");
		q.setParameter("id", regionId);
		q.setParameterList("sa", selectedAgencies);
		@SuppressWarnings("unchecked")
		List<GeoStopRouteMap> result = q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result.size();
	    }
/**
 * returns list of routes for a given county
 *//*
	public static int getroutescountsbycounty(String countyId, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("ROUTES_BY_COUNTY");
		q.setParameter("id", countyId);
		@SuppressWarnings("unchecked")
		List<GeoStopRouteMap> result = q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result.size();
	    }*/
/**
 * returns count of routes for a given county
 */	
	public static int getroutescountsbycounty(String countyId, List<String> selectedAgencies, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("ROUTES_BY_COUNTY_SEL_AGENCIES");
		q.setParameter("id", countyId);
		q.setParameterList("sa", selectedAgencies);
		@SuppressWarnings("unchecked")
		List<GeoStopRouteMap> result = q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result.size();
	    }
/**
 * returns count of routes for a given census place
 */
	public static int getroutescountsbyplace(String placeId, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("ROUTES_BY_PLACE");
		q.setParameter("id", placeId);
		@SuppressWarnings("unchecked")
		List<GeoStopRouteMap> result = q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result.size();
	    }
	
/**
 * returns count of agencies for a given geographic area
 */
	@SuppressWarnings("unchecked")
	public static int getAgencyCountByArea(String areaId, int type, List<String> selectedAgencies, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q;
		List<String> results = new ArrayList<String>();
		
		switch (type) {
		case 0: //counties	
			//System.out.println("The SA size is: "+selectedAgencies.size());
			//System.out.println("SA list: "+selectedAgencies.size());
			q = session[sessionindex].getNamedQuery("AGENCIES_BY_COUNTY_SEL_AGENCIES");
			q.setParameter("id",areaId);	
			q.setParameterList("sa", selectedAgencies);
	        results = q.list();	
			System.out.println("The result size for county "+areaId+" is :"+results.size());
			//System.out.println("Query result is: "+results.get(0));

			break;						
		case 1:	//census tract
			q = session[sessionindex].getNamedQuery("AGENCIES_BY_TRACT_SEL_AGENCIES");
			q.setParameter("id",areaId);	
			q.setParameterList("sa", selectedAgencies);
	        results = q.list();			
			break;
		case 2:	//census place
			q = session[sessionindex].getNamedQuery("AGENCIES_BY_PLACE_SEL_AGENCIES");
			q.setParameter("id",areaId);	
			q.setParameterList("sa", selectedAgencies);
	        results = q.list();
			break;
		case 3:	//urban area
			q = session[sessionindex].getNamedQuery("AGENCIES_BY_URBAN_SEL_AGENCIES");
			q.setParameter("id",areaId);	
			q.setParameterList("sa", selectedAgencies);
	        results = q.list();
			break;
		case 4:	//ODOT region
			q = session[sessionindex].getNamedQuery("AGENCIES_BY_REGION_SEL_AGENCIES");
			q.setParameter("id",areaId);	
			q.setParameterList("sa", selectedAgencies);
	        results = q.list();
			break;
		case 5:	//Congressional District
			q = session[sessionindex].getNamedQuery("AGENCIES_BY_CONGDIST_SEL_AGENCIES");
			q.setParameter("id",areaId);	
			q.setParameterList("sa", selectedAgencies);
		    results = q.list();
			break;
		}		
        Hutil.getSessionFactory()[sessionindex].close();
        return results.size();
	    }
	
	public static int getroutescountsbyplace(String placeId, List<String> selectedAgencies, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("ROUTES_BY_PLACE_SEL_AGENCIES");
		q.setParameter("id", placeId);
		q.setParameterList("sa", selectedAgencies);
		@SuppressWarnings("unchecked")
		List<GeoStopRouteMap> result = q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result.size();
	    }
/**
 * returns list of routes for a given census tract
 */
	public static int getroutescountsbytract(String tractId, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("ROUTES_BY_TRACT");
		q.setParameter("id", tractId);
		@SuppressWarnings("unchecked")
		List<GeoStopRouteMap> result = q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result.size();
	    }
	
	public static int getroutescountsbytract(String tractId, List<String> selectedAgencies, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("ROUTES_BY_TRACT_SEL_AGENCIES");
		q.setParameter("id", tractId);
		q.setParameterList("sa", selectedAgencies);
		@SuppressWarnings("unchecked")
		List<GeoStopRouteMap> result = q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result.size();
	    }
/**
 * returns list of routes for a given urban area
 */
	public static int getroutescountbyurban(String urbanId, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("ROUTES_BY_URBAN");
		q.setParameter("id", urbanId);
		@SuppressWarnings("unchecked")
		List<GeoStopRouteMap> result = q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result.size();
	    }	
	
	public static int getroutescountbyurban(String urbanId, List<String> selectedAgencies, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("ROUTES_BY_URBAN_SEL_AGENCIES");
		q.setParameter("id", urbanId);
		q.setParameterList("sa", selectedAgencies);
		@SuppressWarnings("unchecked")
		List<GeoStopRouteMap> result = q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result.size();
	    }
	
/**
* returns list of routes for a given urban area for Aggregated urban area report
*/
	public static List<GeoStopRouteMap> getroutesbyurban(String urbanId, int sessionindex) throws FactoryException, TransformException {
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("ROUTES_BY_AURBAN");
		q.setParameter("id", urbanId);
		@SuppressWarnings("unchecked")
		List<GeoStopRouteMap> result = q.list();
		Hutil.getSessionFactory()[sessionindex].close();
		return result;
	}
	
	public static List<GeoStopRouteMap> getroutesbyurban(String urbanId, List<String> selectedAgencies, int sessionindex) throws FactoryException, TransformException {
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("ROUTES_BY_AURBAN_SEL_AGENCIES");
		q.setParameter("id", urbanId);
		q.setParameterList("sa", selectedAgencies);
		@SuppressWarnings("unchecked")
		List<GeoStopRouteMap> result = q.list();
		Hutil.getSessionFactory()[sessionindex].close();
		return result;
	}
	
/**
 * returns list of routes for a given congressional district
 */
	public static int getroutescountbycongdist(String congdistId, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("ROUTES_BY_CONGDIST");
		q.setParameter("id", congdistId);
		@SuppressWarnings("unchecked")
		List<GeoStopRouteMap> result = q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result.size();
	    }
	
	public static int getroutescountbycongdist(String congdistId, List<String> selectedAgencies, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("ROUTES_BY_CONGDIST_SEL_AGENCIES");
		q.setParameter("id", congdistId);
		q.setParameterList("sa", selectedAgencies);
		@SuppressWarnings("unchecked")
		List<GeoStopRouteMap> result = q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result.size();
	    }
	
/**
 * returns list of tracts for a given county
 */
	public static List<Tract> gettractsbycounty(String countyId, int sessionindex) throws FactoryException, TransformException {			
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("TRACTS_BY_COUNTY");
		q.setParameter("id", countyId);
		@SuppressWarnings("unchecked")
		List<Tract> result = q.list();
        Hutil.getSessionFactory()[sessionindex].close();
        return result;
	    }
/**
 * returns population within the d distance of a point
 */
	public static long getpop(double d, double lat, double lon, int sessionindex) throws FactoryException, TransformException {			
		CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
		CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:2993");
		MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();			
		Point point = geometryFactory.createPoint(new Coordinate(lat, lon));
		Geometry targetGeometry = JTS.transform( point, transform);
		//point = geometryFactory.createPoint(targetGeometry.getCoordinate());
		point = targetGeometry.getCentroid();
		point.setSRID(2993);	
		session[sessionindex].beginTransaction();
		Query q = session[sessionindex].getNamedQuery("POP_BY_COORDINATES");
		Type geomType = GeometryUserType.TYPE;
		q.setParameter("point", point, geomType);
		q.setParameter("radius", d);
		//@SuppressWarnings("unchecked")
		List results = q.list();
		long pop = 0;
		if (results.get(0)!=null){ 
		pop = (Long) results.get(0);
		}
        Hutil.getSessionFactory()[sessionindex].close();
        return pop;
    }
	
	/**
	 * returns census block internal points within the d distance of a point
	 */
	public static List<Long> getpopbatch(double d, List <Coordinate> points, int sessionindex) throws FactoryException, TransformException {			
		List<Long> response = new ArrayList<Long> ();
		CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
		CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:2993");
		MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		session[sessionindex].beginTransaction();
		Type geomType = GeometryUserType.TYPE;
		Query q = session[sessionindex].getNamedQuery("POP_BY_COORDINATES");
		q.setParameter("radius", d);
		for (Coordinate point: points){
			Point p = geometryFactory.createPoint(point);
			Geometry targetGeometry = JTS.transform( p, transform);
			p = targetGeometry.getCentroid();
			p.setSRID(2993);		
			q.setParameter("point", p, geomType);
			List results = q.list();
			long pop = 0;
			if (results.get(0)!=null){ 
			pop = (Long) results.get(0);
			}
			response.add(pop);
		}		
        Hutil.getSessionFactory()[sessionindex].close();
        return response;
    }
	/**
	 * returns unduplicated population within the d distance of a list of points
	 */
	
	public static List<Census> getundupcentbatch(double d, List <Coordinate> points, int sessionindex) throws FactoryException, TransformException {		
		CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
		CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:2993");
		MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		session[sessionindex].beginTransaction();
		Type geomType = GeometryUserType.TYPE;		
		StringBuffer queryBuf = new StringBuffer("from Census");
		//boolean firstClause = true;
		int i = 0;		
		//List<Point> qpoints = new ArrayList<Point>();
		Point[] plist = new Point[points.size()];
		for (Coordinate point: points){
			//queryBuf.append(firstClause ? " where " : " or ");
			Point p = geometryFactory.createPoint(point);			
			Geometry targetGeometry = JTS.transform( p, transform);
			p = targetGeometry.getCentroid();
			p.setSRID(2993);
			plist[i]=p;
			//queryBuf.append("(distance(:point"+String.valueOf(i)+",location)<:radius)");			
			//firstClause = false;
			i++;
		}
		MultiPoint allpoints = geometryFactory.createMultiPoint(plist);
		allpoints.setSRID(2993);		
		System.out.println("no of points: "+plist.length);
		queryBuf.append(" where distance(:allpoints, location)<:radius ");
		queryBuf.append("group by blockId");
		String hqlQuery = queryBuf.toString();
		Query query = session[sessionindex].createQuery(hqlQuery);
		query.setParameter("radius",d);
		query.setParameter("allpoints",allpoints,geomType);
		System.out.println(hqlQuery);		
		//i=1;
		/*for (Point p :qpoints){
			query.setParameter("point"+String.valueOf(i),p,geomType);			
			i++;
		}*/
		@SuppressWarnings("unchecked")
		List<Census> results = (List<Census>) query.list();		
        Hutil.getSessionFactory()[sessionindex].close();
        //List results = query.list();
		//long pop = 0;
		//if (results.get(0)!=null){ 
		//pop = (Long) results.get(0);
		//}
        return results;		
    }
	public static long getunduppopbatch(double d, List <Coordinate> points, int sessionindex) throws FactoryException, TransformException {		
		CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
		CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:2993");
		MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		session[sessionindex].beginTransaction();
		Type geomType = GeometryUserType.TYPE;		
		//StringBuffer queryBuf = new StringBuffer("select sum(population) from Census where id in (select distinct id from Census");
		//boolean firstClause = true;
		int i = 0;		
		//List<Point> qpoints = new ArrayList<Point>();
		Point[] plist = new Point[points.size()];
		for (Coordinate point: points){
			//queryBuf.append(firstClause ? " where " : " or ");
			Point p = geometryFactory.createPoint(point);
			Geometry targetGeometry = JTS.transform( p, transform);
			p = targetGeometry.getCentroid();
			p.setSRID(2993);
			plist[i]=p;
			//qpoints.add(p);
			//queryBuf.append("(distance(:point"+String.valueOf(i)+",location)<:radius)");			
			//firstClause = false;
			i++;
		}
		MultiPoint allpoints = geometryFactory.createMultiPoint(plist);
		allpoints.setSRID(2993);
		//queryBuf.append(" where dwithin(location, :allpoints, :radius) = true ) ");
		System.out.println("no of points: "+plist.length);
		//queryBuf.append(") ");
		Query q = session[sessionindex].getNamedQuery("POP_UNDUP_BATCH");
		//String hqlQuery = queryBuf.toString();
		//Query query = session.createQuery(hqlQuery);				
		//i=1;
		q.setParameter("radius",d);
		q.setParameter("allpoints",allpoints,geomType);
		System.out.println(q.toString());
		/*for (Point p :qpoints){
			query.setParameter("point"+String.valueOf(i),p,geomType);			
			i++;
		}*/
		//@SuppressWarnings("unchecked")
		//List<Census> results = (List<Census>) query.list();		
        
        List results = q.list();
		long pop = 0;
		if (results.size()>0 && results.get(0)!=null){ 
		pop = (Long) results.get(0);
		//pop = (Integer) results.get(0);
		}
		Hutil.getSessionFactory()[sessionindex].close();
		System.out.println("Query returned: "+pop);
        return pop;		
    }
	
	public static long getcountyunduppopbatch(double d, String countyId, List <Coordinate> points, int sessionindex) throws FactoryException, TransformException {		
		CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
		CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:2993");
		MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		session[sessionindex].beginTransaction();
		Type geomType = GeometryUserType.TYPE;	
		int i = 0;		
		Point[] plist = new Point[points.size()];
		for (Coordinate point: points){			
			Point p = geometryFactory.createPoint(point);
			Geometry targetGeometry = JTS.transform( p, transform);
			p = targetGeometry.getCentroid();
			p.setSRID(2993);
			plist[i]=p;			
			i++;
		}
		MultiPoint allpoints = geometryFactory.createMultiPoint(plist);
		allpoints.setSRID(2993);		
		System.out.println("no of points: "+plist.length);		
		Query q = session[sessionindex].getNamedQuery("COUNTY_POP_UNDUP_BATCH");		
		q.setParameter("id",countyId);
		q.setParameter("radius",d);
		q.setParameter("allpoints",allpoints,geomType);
		System.out.println(q.toString());		
        List results = q.list();
		long pop = 0;
		if (results.size()>0 && results.get(0)!=null){ 
		pop = (Long) results.get(0);		
		}
		Hutil.getSessionFactory()[sessionindex].close();
		return pop;		
    }
	
	public static long getregionunduppopbatch(double d, String regionId, List <Coordinate> points, int sessionindex) throws FactoryException, TransformException {		
		CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
		CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:2993");
		MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		session[sessionindex].beginTransaction();
		Type geomType = GeometryUserType.TYPE;	
		int i = 0;		
		Point[] plist = new Point[points.size()];
		for (Coordinate point: points){			
			Point p = geometryFactory.createPoint(point);
			Geometry targetGeometry = JTS.transform( p, transform);
			p = targetGeometry.getCentroid();
			p.setSRID(2993);
			plist[i]=p;			
			i++;
		}
		MultiPoint allpoints = geometryFactory.createMultiPoint(plist);
		allpoints.setSRID(2993);		
		System.out.println("no of points: "+plist.length);		
		Query q = session[sessionindex].getNamedQuery("REGION_POP_UNDUP_BATCH");		
		q.setParameter("id",regionId);
		q.setParameter("radius",d);
		q.setParameter("allpoints",allpoints,geomType);
		System.out.println(q.toString());		
        List results = q.list();
		long pop = 0;
		if (results.size()>0 && results.get(0)!=null){ 
		pop = (Long) results.get(0);		
		}
		Hutil.getSessionFactory()[sessionindex].close();
		return pop;		
    }
	
	public static long gettractunduppopbatch(double d, String tractId, List <Coordinate> points, int sessionindex) throws FactoryException, TransformException {		
		CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
		CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:2993");
		MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		session[sessionindex].beginTransaction();
		Type geomType = GeometryUserType.TYPE;	
		int i = 0;		
		Point[] plist = new Point[points.size()];
		for (Coordinate point: points){			
			Point p = geometryFactory.createPoint(point);
			Geometry targetGeometry = JTS.transform( p, transform);
			p = targetGeometry.getCentroid();
			p.setSRID(2993);
			plist[i]=p;			
			i++;
		}
		MultiPoint allpoints = geometryFactory.createMultiPoint(plist);
		allpoints.setSRID(2993);		
		System.out.println("no of points: "+plist.length);		
		Query q = session[sessionindex].getNamedQuery("TRACT_POP_UNDUP_BATCH");		
		q.setParameter("id",tractId);
		q.setParameter("radius",d);
		q.setParameter("allpoints",allpoints,geomType);
		System.out.println(q.toString());		
        List results = q.list();
		long pop = 0;
		if (results.size()>0 && results.get(0)!=null){ 
		pop = (Long) results.get(0);		
		}
		Hutil.getSessionFactory()[sessionindex].close();
		return pop;		
    }
	public static long getplaceunduppopbatch(double d, String placeId, List <Coordinate> points, int sessionindex) throws FactoryException, TransformException {		
		CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
		CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:2993");
		MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		session[sessionindex].beginTransaction();
		Type geomType = GeometryUserType.TYPE;	
		int i = 0;		
		Point[] plist = new Point[points.size()];
		for (Coordinate point: points){			
			Point p = geometryFactory.createPoint(point);
			Geometry targetGeometry = JTS.transform( p, transform);
			p = targetGeometry.getCentroid();
			p.setSRID(2993);
			plist[i]=p;			
			i++;
		}
		MultiPoint allpoints = geometryFactory.createMultiPoint(plist);
		allpoints.setSRID(2993);		
		System.out.println("no of points: "+plist.length);		
		Query q = session[sessionindex].getNamedQuery("PLACE_POP_UNDUP_BATCH");		
		q.setParameter("id",placeId);
		q.setParameter("radius",d);
		q.setParameter("allpoints",allpoints,geomType);
		System.out.println(q.toString());		
        List results = q.list();
		long pop = 0;
		if (results.size()>0 && results.get(0)!=null){ 
		pop = (Long) results.get(0);		
		}
		Hutil.getSessionFactory()[sessionindex].close();
		return pop;		
    }
	
	public static long geturbanunduppopbatch(double d, String urbanId, List <Coordinate> points, int sessionindex) throws FactoryException, TransformException {		
		CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
		CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:2993");
		MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		session[sessionindex].beginTransaction();
		Type geomType = GeometryUserType.TYPE;	
		int i = 0;		
		Point[] plist = new Point[points.size()];
		for (Coordinate point: points){			
			Point p = geometryFactory.createPoint(point);
			Geometry targetGeometry = JTS.transform( p, transform);
			p = targetGeometry.getCentroid();
			p.setSRID(2993);
			plist[i]=p;			
			i++;
		}
		MultiPoint allpoints = geometryFactory.createMultiPoint(plist);
		allpoints.setSRID(2993);		
		System.out.println("no of points: "+plist.length);		
		Query q = session[sessionindex].getNamedQuery("URBAN_POP_UNDUP_BATCH");		
		q.setParameter("id",urbanId);
		q.setParameter("radius",d);
		q.setParameter("allpoints",allpoints,geomType);
		System.out.println(q.toString());		
        List results = q.list();
		long pop = 0;
		if (results.size()>0 && results.get(0)!=null){ 
		pop = (Long) results.get(0);		
		}
		Hutil.getSessionFactory()[sessionindex].close();
		return pop;		
    }
	public static long geturbanunduppopbatchbypop(double d, int upop, List <Coordinate> points, int sessionindex) throws FactoryException, TransformException {		
		CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
		CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:2993");
		MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		session[sessionindex].beginTransaction();
		Type geomType = GeometryUserType.TYPE;	
		int i = 0;		
		Point[] plist = new Point[points.size()];
		for (Coordinate point: points){			
			Point p = geometryFactory.createPoint(point);
			Geometry targetGeometry = JTS.transform( p, transform);
			p = targetGeometry.getCentroid();
			p.setSRID(2993);
			plist[i]=p;			
			i++;
		}
		MultiPoint allpoints = geometryFactory.createMultiPoint(plist);
		allpoints.setSRID(2993);		
		System.out.println("no of points: "+plist.length);		
		Query q = session[sessionindex].getNamedQuery("URBAN_POP_UNDUP_BATCH_BYPOP");		
		q.setParameter("pop",(long)upop);
		q.setParameter("radius",d);
		q.setParameter("allpoints",allpoints,geomType);
		System.out.println(q.toString());		
        List results = q.list();
		long pop = 0;
		if (results.size()>0 && results.get(0)!=null){ 
		pop = (Long) results.get(0);		
		}
		Hutil.getSessionFactory()[sessionindex].close();
		return pop;		
    }
	
	public static long getcongdistunduppopbatch(double d, String congdistId, List <Coordinate> points, int sessionindex) throws FactoryException, TransformException {		
		CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
		CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:2993");
		MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		session[sessionindex].beginTransaction();
		Type geomType = GeometryUserType.TYPE;	
		int i = 0;		
		Point[] plist = new Point[points.size()];
		for (Coordinate point: points){			
			Point p = geometryFactory.createPoint(point);
			Geometry targetGeometry = JTS.transform( p, transform);
			p = targetGeometry.getCentroid();
			p.setSRID(2993);
			plist[i]=p;			
			i++;
		}
		MultiPoint allpoints = geometryFactory.createMultiPoint(plist);
		allpoints.setSRID(2993);		
		System.out.println("no of points: "+plist.length);		
		Query q = session[sessionindex].getNamedQuery("CONGDIST_POP_UNDUP_BATCH");		
		q.setParameter("id",congdistId);
		q.setParameter("radius",d);
		q.setParameter("allpoints",allpoints,geomType);
		System.out.println(q.toString());		
        List results = q.list();
		long pop = 0;
		if (results.size()>0 && results.get(0)!=null){ 
		pop = (Long) results.get(0);		
		}
		Hutil.getSessionFactory()[sessionindex].close();
		return pop;		
    }
	
	public static GeoArea QueryGeoAreabyId(String id, int type, int sessionindex, int popYear){
		Query q;
		List results;
		GeoArea response = new GeoArea();
		response.setType(type);
		response.setTypeName(Types.getAreaName(type));
		switch (type) {
		case 0: //counties
			q = session[sessionindex].getNamedQuery("COUNTY_BY_ID");
			q.setParameter("id",id);		
	        results = q.list();
			County cn = new County();
			if (results.size()>0 && results.get(0)!=null){ 
			cn = (County) results.get(0);
			response.setId(cn.getCountyId());
			response.setName(cn.getName());
			response.setLandarea(cn.getLandarea());
			response.setWaterarea(cn.getWaterarea());
			response.setPopulation(cn.getPopulation(popYear));
			}
			break;						
		case 1:	//census tract
			q = session[sessionindex].getNamedQuery("TRACT_BY_ID");		
			q.setParameter("id",id);		
	        results = q.list();
			Tract ct = new Tract();
			if (results.size()>0 && results.get(0)!=null){ 
			ct = (Tract) results.get(0);
			response.setId(ct.getTractId());
			response.setName(ct.getLongname());
			response.setLandarea(ct.getLandarea());
			response.setWaterarea(ct.getWaterarea());
			response.setPopulation(ct.getPopulation(popYear));
			}			
			break;
		case 2:	//census place
			q = session[sessionindex].getNamedQuery("PLACE_BY_ID");		
			q.setParameter("id",id);		
	        results = q.list();
			Place pl = new Place();
			if (results.size()>0 && results.get(0)!=null){ 
			pl = (Place) results.get(0);
			response.setId(pl.getPlaceId());
			response.setName(pl.getName());
			response.setLandarea(pl.getLandarea());
			response.setWaterarea(pl.getWaterarea());
			response.setPopulation(pl.getPopulation(popYear));
			}
			break;
		case 3:	//urban area
			q = session[sessionindex].getNamedQuery("URBAN_BY_ID");		
			q.setParameter("id",id);		
	        results = q.list();
			Urban ur = new Urban();
			if (results.size()>0 && results.get(0)!=null){ 
			ur = (Urban) results.get(0);
			response.setId(ur.getUrbanId());
			response.setName(ur.getName());
			response.setLandarea(ur.getLandarea());
			response.setWaterarea(ur.getWaterarea());
			response.setPopulation(ur.getPopulation(popYear));
			}
			break;
		case 4:	//ODOT region
			q = session[sessionindex].getNamedQuery("REGION_BY_ID");		
			q.setParameter("id",id);		
	        results = q.list();
			List<County> cns = new ArrayList<County>();
			if (results.size()>0 && results.get(0)!=null){ 
			cns = (List<County>) results;
			//pop = (Integer) results.get(0);
			long LandArea = 0;
			long WaterArea = 0;
			long Population = 0;
			for (County inst:cns){
				LandArea+=inst.getLandarea();
				WaterArea+=inst.getWaterarea();
				Population+=inst.getPopulation(popYear);
			}
			response.setId(cns.get(0).getRegionId());			
			response.setName("ODOT Transit "+cns.get(0).getRegionName());
			response.setLandarea(LandArea);
			response.setWaterarea(WaterArea);
			response.setPopulation(Population);
			}
			break;
		case 5:	//Congressional District
			q = session[sessionindex].getNamedQuery("CONGDIST_BY_ID");		
			q.setParameter("id",id);		
	        results = q.list();
			CongDist cd = new CongDist();
			if (results.size()>0 && results.get(0)!=null){ 
			cd = (CongDist) results.get(0);
			response.setId(cd.getCongdistId());
			response.setName(cd.getName());
			response.setLandarea(cd.getLandarea());
			response.setWaterarea(cd.getWaterarea());
			response.setPopulation(cd.getPopulation(popYear));
			}
			break;
		}		
		Hutil.getSessionFactory()[sessionindex].close();
		return response;
	}
	
	public static County QueryCountybyId(String id, int sessionindex){
		Query q = session[sessionindex].getNamedQuery("COUNTY_BY_ID");		
		q.setParameter("id",id);		
        List results = q.list();
		County ct = new County();
		if (results.size()>0 && results.get(0)!=null){ 
		ct = (County) results.get(0);
		//pop = (Integer) results.get(0);
		}
		Hutil.getSessionFactory()[sessionindex].close();
		return ct;
	}
	
	public static List<County> QueryOdotregionsbyId(String id, int sessionindex){
		Query q = session[sessionindex].getNamedQuery("REGION_BY_ID");		
		q.setParameter("id",id);		
        List results = q.list();
		List<County> ct = new ArrayList<County>();
		if (results.size()>0 && results.get(0)!=null){ 
		ct = (List<County>) results;
		//pop = (Integer) results.get(0);
		}
		Hutil.getSessionFactory()[sessionindex].close();
		return ct;
	}
	
	public static Tract QueryTractbyId(String id, int sessionindex){
		Query q = session[sessionindex].getNamedQuery("TRACT_BY_ID");		
		q.setParameter("id",id);		
        List results = q.list();
		Tract ct = new Tract();
		if (results.size()>0 && results.get(0)!=null){ 
		ct = (Tract) results.get(0);
		//pop = (Integer) results.get(0);
		}
		Hutil.getSessionFactory()[sessionindex].close();
		return ct;
	}
	
	public static Place QueryPlacebyId(String id, int sessionindex){
		Query q = session[sessionindex].getNamedQuery("PLACE_BY_ID");		
		q.setParameter("id",id);		
        List results = q.list();
		Place ct = new Place();
		if (results.size()>0 && results.get(0)!=null){ 
		ct = (Place) results.get(0);
		//pop = (Integer) results.get(0);
		}
		Hutil.getSessionFactory()[sessionindex].close();
		return ct;
	}
	
	public static Urban QueryUrbanbyId(String id, int sessionindex){
		Query q = session[sessionindex].getNamedQuery("URBAN_BY_ID");		
		q.setParameter("id",id);		
        List results = q.list();
		Urban ct = new Urban();
		if (results.size()>0 && results.get(0)!=null){ 
		ct = (Urban) results.get(0);
		//pop = (Integer) results.get(0);
		}
		Hutil.getSessionFactory()[sessionindex].close();
		return ct;
	}
	
	public static CongDist QueryCongdistbyId(String id, int sessionindex){
		Query q = session[sessionindex].getNamedQuery("CONGDIST_BY_ID");		
		q.setParameter("id",id);		
        List results = q.list();
		CongDist ct = new CongDist();
		if (results.size()>0 && results.get(0)!=null){ 
		ct = (CongDist) results.get(0);
		//pop = (Integer) results.get(0);
		}
		Hutil.getSessionFactory()[sessionindex].close();
		return ct;
	}
/*
    private void createAndStoreEvent(String id, String pop, String lat, String lon) {

        //First interpret the WKT string to a point
        WKTReader fromText = new WKTReader();
        Geometry geom = null;
        try {
            geom = fromText.read(wktPoint);
        } catch (ParseException e) {
            throw new RuntimeException("Not a WKT string:" + wktPoint);
        }
        if (!geom.getGeometryType().equals("Point")) {
            throw new RuntimeException("Geometry must be a point. Got a " + geom.getGeometryType());
        }

        Session session = Hutil.getSessionFactory().getCurrentSession();

        session.beginTransaction();

        Census theEvent = new Census();
        theEvent.setTitle(title);
        theEvent.setDate(theDate);
        theEvent.setLocation((Point) geom);
        session.save(theEvent);

        session.getTransaction().commit();
    }
*/
    /**
    * Utility method to assemble all arguments save the first into a String
    */
	/*
    private static String assemble(String[] args){
            StringBuilder builder = new StringBuilder();
            for(int i = 1; i<args.length;i++){
                    builder.append(args[i]).append(" ");
            }
            return builder.toString();
    }
    private List find(String wktFilter){
        WKTReader fromText = new WKTReader();
        Geometry filter = null;
        try{
                filter = fromText.read(wktFilter);
        } catch(ParseException e){
                throw new RuntimeException("Not a WKT String:" + wktFilter);
        }
        Session session = Hutil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        System.out.println("Filter is : " + filter);
        Criteria testCriteria = session.createCriteria(Census.class);
        testCriteria.add(SpatialRestrictions.within("location", filter));
        List results = testCriteria.list();
        session.getTransaction().commit();
        return results;
    }*/
}
