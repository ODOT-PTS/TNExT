/**
 * Copyright (C) 2011 Brian Ferris <bdferris@onebusaway.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.model.database;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.FareAttribute;
import org.onebusaway.gtfs.model.FareRule;
import org.onebusaway.gtfs.model.FeedInfo;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.ServiceCalendar;
import org.onebusaway.gtfs.model.ServiceCalendarDate;
import org.onebusaway.gtfs.model.ShapePoint;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.model.calendar.ServiceDate;
import org.onebusaway.gtfs.serialization.GtfsReader;
import org.onebusaway.gtfs.services.GtfsMutableRelationalDao;
import org.onebusaway.gtfs.services.HibernateGtfsFactory;
import org.onebusaway.gtfs.services.calendar.CalendarService;

import com.model.database.*;

public class GtfsHibernateReaderExampleMain {

  private static final String KEY_CLASSPATH = "classpath:";

  private static final String KEY_FILE = "file:";
  
  public static HibernateGtfsFactory[] factory = new HibernateGtfsFactory[Databases.dbsize];
  static{
	  for (int k=0; k<Databases.dbsize; k++){
		  factory[k] = createHibernateGtfsFactory(Databases.ConfigPaths[k]);
	  }	    
  }		  

  public static Agency QueryAgencybyid(String id, int dbindex){
	  GtfsMutableRelationalDao dao = factory[dbindex].getDao();	  
	  Agency a = dao.getAgencyForId(id);
	  return a;
  }
  
  public static Stop QueryStopbyid(AgencyAndId id, int dbindex){
	  GtfsMutableRelationalDao dao = factory[dbindex].getDao();	  
	  return dao.getStopForId(id);
  }
  
  public static Route QueryRoutebyid(AgencyAndId route, int dbindex){
	  GtfsMutableRelationalDao dao = factory[dbindex].getDao();	  
	  return dao.getRouteForId(route);
  }
  
  public static List<Trip> QueryTripsforAgency(String agencyId, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = (HibernateGtfsRelationalDaoImplExt) factory[dbindex].getDao();	  
	  return dao.getTripsForAgency(agencyId);
  }
  
  public static Double getRouteMilesforAgency (String agencyId, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = (HibernateGtfsRelationalDaoImplExt) factory[dbindex].getDao();
	  List<Double> lengths = dao.getMaxTripLengthsForAgency(agencyId);
	  Double response = 0.0;
	  for (Double length: lengths){
		  response  += length;
	  }
	  return response;
  }
  public static List<String> QueryRouteIdsforStop(Stop stop, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = (HibernateGtfsRelationalDaoImplExt) factory[dbindex].getDao();	  
	  return dao.getRouteIdsForStop(stop);
  }
  
  public static List<Trip> QueryTripsforAgency_RouteSorted(String agencyId, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = (HibernateGtfsRelationalDaoImplExt) factory[dbindex].getDao();	  
	  return dao.getTripsForAgency_RouteSorted(agencyId);
  }
  
  public static List<ServiceCalendar> QueryCalsforRoute(Route route, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = (HibernateGtfsRelationalDaoImplExt) factory[dbindex].getDao();	  
	  return dao.getServiceCalendarsForRoute(route);
  }
  
  public static ServiceCalendar  QueryCalendarforTrip(Trip trip, int dbindex){
	  GtfsMutableRelationalDao dao = factory[dbindex].getDao();
	  return dao.getCalendarForServiceId(trip.getServiceId());
  }
  
  public static List<ServiceCalendarDate>  QueryCalendarDatesforTrip(Trip trip, int dbindex){
	  GtfsMutableRelationalDao dao = factory[dbindex].getDao();
	  return dao.getCalendarDatesForServiceId(trip.getServiceId());
  }
  
  //Alireza
  public static List<ServiceCalendar>  QueryCalendarforAgency(String agency, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = (HibernateGtfsRelationalDaoImplExt) factory[dbindex].getDao();
	  return dao.getCalendarForAgency(agency);
  }
  
  public static List<ServiceCalendarDate>  QueryCalendarDatesforAgency(String agency, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = (HibernateGtfsRelationalDaoImplExt) factory[dbindex].getDao();
	  return dao.getCalendarDatesForAgency(agency);
  }
  
  public static List<ShapePoint> Queryshapebytrip(AgencyAndId trip, int dbindex){
	  GtfsMutableRelationalDao dao = factory[dbindex].getDao();	  
	  AgencyAndId shapeid = dao.getTripForId(trip).getShapeId();	  
	  return dao.getShapePointsForShapeId(shapeid);
  }
  
  public static List<StopTime> Querystoptimebytrip(AgencyAndId trip, int dbindex){
	  GtfsMutableRelationalDao dao = factory[dbindex].getDao();	  
	  Trip trp = dao.getTripForId(trip);	  
	  return dao.getStopTimesForTrip(trp);
  }
  
  public static List<FareRule> QueryFareRuleByRoute(Route route, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = (HibernateGtfsRelationalDaoImplExt) factory[dbindex].getDao();	  	  
	  return dao.getFareRuleForRoute(route);
  }
  
  public static HashMap<String, Float> QueryFareData(String agencyId, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = (HibernateGtfsRelationalDaoImplExt) factory[dbindex].getDao();
	  return dao.getFareDataForAgency(agencyId);
  }
  
  public static HashMap<String, Float> QueryFareData(List<String> selectedAgencies, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = (HibernateGtfsRelationalDaoImplExt) factory[dbindex].getDao();
	  return dao.getFareDataForState(selectedAgencies);
  }
  
  public static Double QueryRouteMiles(int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = (HibernateGtfsRelationalDaoImplExt) factory[dbindex].getDao();
	  return dao.getRouteMiles();
  }
  
  public static Double QueryRouteMiles(List<String> selectedAgencies,int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = (HibernateGtfsRelationalDaoImplExt) factory[dbindex].getDao();
	  return dao.getRouteMiles(selectedAgencies);
  }
  
 public static Float QueryFareMedian(String agencyId,int farecount, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = (HibernateGtfsRelationalDaoImplExt) factory[dbindex].getDao();
	  return dao.getFareMedianForAgency(agencyId, farecount);
  }
  
  public static Float QueryFareMedian(List<String> selectedAgencies,int farecount, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = (HibernateGtfsRelationalDaoImplExt) factory[dbindex].getDao();
	  return dao.getFareMedianForState(selectedAgencies, farecount);
  }
  
  public static List<Float> QueryFarePriceByRoutes(List <String> routes, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = (HibernateGtfsRelationalDaoImplExt) factory[dbindex].getDao();	  	  
	  return dao.getFarePriceForRoutes(routes);
  }
  
  public static void updateTrip(Trip trip, int dbindex){
	HibernateGtfsRelationalDaoImplExt dao = (HibernateGtfsRelationalDaoImplExt) factory[dbindex].getDao();
	dao.updateTrip(trip);
  }
  
  public static Trip getTrip(AgencyAndId id, int dbindex){
	  GtfsMutableRelationalDao dao = factory[dbindex].getDao();
	  return dao.getTripForId(id);
  }
  
  public static Collection<FeedInfo> QueryAllFeedInfos (int dbindex){
	  GtfsMutableRelationalDao dao = factory[dbindex].getDao();	  
	  return dao.getAllFeedInfos();
  }
  
  public static Collection<Agency> QueryAllAgencies (List<String> selectedAgencies, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = (HibernateGtfsRelationalDaoImplExt) factory[dbindex].getDao();	  
	  return dao.getAllAgencies(selectedAgencies);
  }
  
  public static Collection<Agency> QuerySelectedAgencies (List<String> selectedAgencies, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = (HibernateGtfsRelationalDaoImplExt) factory[dbindex].getDao();	  
	  return dao.getSelectedAgencies(selectedAgencies);
  }
  
  public static Collection<Route> QueryAllRoutes (int dbindex){
	  GtfsMutableRelationalDao dao = factory[dbindex].getDao();	  
	  return dao.getAllRoutes();
  }
  
  public static Collection<Trip> QueryAllTrips (int dbindex){
	  GtfsMutableRelationalDao dao = factory[dbindex].getDao();	  
	  return dao.getAllTrips();
  }
  
  public static List<Route> QueryRoutesbyAgency (Agency agency, int dbindex){
	  GtfsMutableRelationalDao dao = factory[dbindex].getDao();	  
	  return dao.getRoutesForAgency(agency);
  }
  
  public static List<Trip> QueryTripsbyRoute (Route route, int dbindex){
	  GtfsMutableRelationalDao dao = factory[dbindex].getDao();	  
	  return dao.getTripsForRoute(route);
  }
  
  public static List<Stop> QueryStopsbyAgency (String id, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = (HibernateGtfsRelationalDaoImplExt) factory[dbindex].getDao();	  
	  return dao.getStopsForAgency(id);
  }
  public static List<Stop> QueryStopsbyRoute (AgencyAndId route, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = (HibernateGtfsRelationalDaoImplExt) factory[dbindex].getDao();	  
	  return dao.getStopsForRoute(route);
  }
  
  public static List<Stop> QueryStopsbyTrip (AgencyAndId trip, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = (HibernateGtfsRelationalDaoImplExt) factory[dbindex].getDao();	  
	  return dao.getStopsForTrip(trip);
  }
  
  public static List<Stop> QueryStopsbyTripCounty (AgencyAndId trip, String county, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = (HibernateGtfsRelationalDaoImplExt) factory[dbindex].getDao();	  
	  return dao.getStopsForTripCounty(trip,county);
  }
  
  public static List<Stop> QueryStopsbyTripRegion (AgencyAndId trip, String region, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = (HibernateGtfsRelationalDaoImplExt) factory[dbindex].getDao();	  
	  return dao.getStopsForTripRegion(trip,region);
  }
  
  public static List<Stop> QueryStopsbyTripTract (AgencyAndId trip, String tract, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = (HibernateGtfsRelationalDaoImplExt) factory[dbindex].getDao();	  
	  return dao.getStopsForTripTract(trip,tract);
  }
  
  public static List<Stop> QueryStopsbyTripPlace (AgencyAndId trip, String place, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = (HibernateGtfsRelationalDaoImplExt) factory[dbindex].getDao();	  
	  return dao.getStopsForTripPlace(trip,place);
  }
  
  public static List<Stop> QueryStopsbyTripUrban (AgencyAndId trip, String urban, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = (HibernateGtfsRelationalDaoImplExt) factory[dbindex].getDao();	  
	  return dao.getStopsForTripUrban(trip,urban);
  }
  
  public static List<Stop> QueryStopsbyTripUrbans (AgencyAndId trip, List<String> urbans, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = (HibernateGtfsRelationalDaoImplExt) factory[dbindex].getDao();	  
	  return dao.getStopsForTripUrbans(trip,urbans);
  }
  
  public static HashMap<String, Integer> QueryCounts (int dbindex, List<String> selectedAgencies){
	  HibernateGtfsRelationalDaoImplExt dao = (HibernateGtfsRelationalDaoImplExt) factory[dbindex].getDao();	  
	  return dao.getCounts(selectedAgencies);
  }
  
  public static List<Stop> QueryStopsbyTripCongdist (AgencyAndId trip, String congdist, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = (HibernateGtfsRelationalDaoImplExt) factory[dbindex].getDao();	  
	  return dao.getStopsForTripCongdist(trip,congdist);
  }
  
  public static Long QueryStopsCount(int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = (HibernateGtfsRelationalDaoImplExt) factory[dbindex].getDao();	  
	  return dao.getStopsCount();
  }
  
  public static Long QueryStopsCount( List<String> selectedAgencies, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = (HibernateGtfsRelationalDaoImplExt) factory[dbindex].getDao();	  
	  return dao.getStopsCount(selectedAgencies);
  }
  
  public static String QueryServiceHours (List<String> trips, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = (HibernateGtfsRelationalDaoImplExt) factory[dbindex].getDao();	  
	  return dao.getServiceHours(trips);
  }
  
  public static List<FeedInfo> QueryFeedInfoByDefAgencyId(String defaultAgency, int dbindex) {
	  HibernateGtfsRelationalDaoImplExt dao = (HibernateGtfsRelationalDaoImplExt) factory[dbindex].getDao();	  
	  return dao.getFeedInfoByDefAgencyId(defaultAgency);
  }
 
  private static ServiceDate min(ServiceDate a, ServiceDate b) {
    if (a == null)
      return b;
    if (b == null)
      return a;
    return a.compareTo(b) <= 0 ? a : b;
  }

  private static ServiceDate max(ServiceDate a, ServiceDate b) {
    if (a == null)
      return b;
    if (b == null)
      return a;
    return a.compareTo(b) <= 0 ? b : a;
  }

  private static HibernateGtfsFactory createHibernateGtfsFactory(String resource) {

    Configuration config = new Configuration();

    if (resource.startsWith(KEY_CLASSPATH)) {
      resource = resource.substring(KEY_CLASSPATH.length());
      config = config.configure(resource);
    } else if (resource.startsWith(KEY_FILE)) {
      resource = resource.substring(KEY_FILE.length());
      config = config.configure(new File(resource));
    } else {
      config = config.configure(new File(resource));
    }

    SessionFactory sessionFactory = config.buildSessionFactory();
    return new HibernateGtfsFactory(sessionFactory);
  }

}