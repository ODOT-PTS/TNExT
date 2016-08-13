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
package com.model.database.onebusaway.gtfs.hibernate.ext;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
//import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.impl.HibernateGtfsRelationalDaoImpl;
import org.onebusaway.gtfs.model.*;
//import org.onebusaway.gtfs.model.FareAttribute;
//import org.onebusaway.gtfs.model.FareRule;
//import org.onebusaway.gtfs.model.FeedInfo;
//import org.onebusaway.gtfs.model.Route;
//import org.onebusaway.gtfs.model.ServiceCalendar;
//import org.onebusaway.gtfs.model.ServiceCalendarDate;
//import org.onebusaway.gtfs.model.ShapePoint;
//import org.onebusaway.gtfs.model.Stop;
//import org.onebusaway.gtfs.model.StopTime;
//import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.model.calendar.ServiceDate;
import org.onebusaway.gtfs.serialization.GtfsReader;
import org.onebusaway.gtfs.services.GtfsMutableRelationalDao;
import org.onebusaway.gtfs.services.HibernateGtfsFactory;
import org.onebusaway.gtfs.services.calendar.CalendarService;

import com.model.database.Databases;
import com.model.database.onebusaway.gtfs.hibernate.ext.HibernateGtfsRelationalDaoImplExt;
import com.model.database.onebusaway.gtfs.hibernate.objects.ext.*;


public class GtfsHibernateReaderExampleMain {

  private static final String KEY_CLASSPATH = "classpath:";

  private static final String KEY_FILE = "file:";
  
  private GtfsMutableRelationalDao dao;
  
  public static HibernateGtfsFactory[] factory = new HibernateGtfsFactory[Databases.dbsize];
  
  public static SessionFactory[] sessions = new SessionFactory[Databases.dbsize];
  
  static{
	  for (int k=0; k<Databases.dbsize; k++){
		  factory[k] = createHibernateGtfsFactory(Databases.ConfigPaths[k],k);
	  }	    
  }		  
  
  public static AgencyExt QueryAgencybyid(String id, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);	  
	  AgencyExt a = dao.getAgencyForIdExt(id);
	  return a;
  }
  
  public static StopExt QueryStopbyid(AgencyAndId id, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);	  
	  return dao.getStopForIdExt(id);
  }
  
  public static RouteExt QueryRoutebyid(AgencyAndId route, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);	  
	  return dao.getRouteForIdExt(route);
  }
  
  public static List<TripExt> QueryTripsforAgency(String agencyId, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);	  
	  return dao.getTripsForAgency(agencyId);
  }
  
  public static Double getRouteMilesforAgency (String agencyId, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);
	  List<Double> lengths = dao.getMaxTripLengthsForAgency(agencyId);
	  Double response = 0.0;
	  for (Double length: lengths){
		  response  += length;
	  }
	  return response;
  }
    
  /*public static List<ServiceCalendarExt> QueryCalsforRoute(RouteExt route, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);	  
	  return dao.getServiceCalendarsForRoute(route);
  }*/
  
  /*public static ServiceCalendar  QueryCalendarforTrip(Trip trip, int dbindex){
	  GtfsMutableRelationalDao dao = factory[dbindex].getDao();
	  return dao.getCalendarForServiceId(trip.getServiceId());
  }*/
  
 /* public static List<ServiceCalendarDate>  QueryCalendarDatesforTrip(Trip trip, int dbindex){
	  GtfsMutableRelationalDao dao = factory[dbindex].getDao();
	  return dao.getCalendarDatesForServiceId(trip.getServiceId());
  }*/
  
  //Alireza
  public static List<ServiceCalendarExt>  QueryCalendarforAgency(String agency, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);
	  return dao.getCalendarForAgency(agency);
  }
  
  public static List<ServiceCalendarDateExt>  QueryCalendarDatesforAgency(String agency, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);
	  return dao.getCalendarDatesForAgency(agency);
  }
  
  /*public static List<ShapePointExt> Queryshapebytrip(AgencyAndId trip, int dbindex){
	  GtfsMutableRelationalDao dao = factory[dbindex].getDao();	  
	  AgencyAndId shapeid = dao.getTripForId(trip).getShapeId();	  
	  return dao.getShapePointsForShapeId(shapeid);
  }*/
  
  public static List<FareRuleExt> QueryFareRuleByRoute(RouteExt route, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);	  	  
	  return dao.getFareRuleForRoute(route);
  }
  
  public static HashMap<String, Float> QueryFareData(String agencyId, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);
	  return dao.getFareDataForAgency(agencyId);
  }
  
  public static HashMap<String, Float> QueryFareData(List<String> selectedAgencies, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);
	  return dao.getFareDataForState(selectedAgencies);
  }
  
  public static Double QueryRouteMiles(int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);
	  return dao.getRouteMiles();
  }
  
  public static Double QueryRouteMiles(List<String> selectedAgencies,int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);
	  return dao.getRouteMiles(selectedAgencies);
  }
  
 public static Float QueryFareMedian(String agencyId,int farecount, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);
	  return dao.getFareMedianForAgency(agencyId, farecount);
  }
  
  public static Float QueryFareMedian(List<String> selectedAgencies,int farecount, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);
	  return dao.getFareMedianForState(selectedAgencies, farecount);
  }
  
  public static List<Float> QueryFarePriceByRoutes(List <String> routes, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);	  	  
	  return dao.getFarePriceForRoutes(routes);
  }
  
  public static void updateTrip(TripExt trip, int dbindex){
	HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);
	dao.updateTrip(trip);
  }
  
  public static TripExt getTrip(AgencyAndId id, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);	 
	  return dao.getTripForIdExt(id);
  }
  
  public static Collection<FeedInfoExt> QueryAllFeedInfos (int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);	  
	  return dao.getAllFeedInfosExt();
  }
  
  public static Collection<AgencyExt> QueryAllAgencies (List<String> selectedAgencies, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);	  
	  return dao.getAllAgencies(selectedAgencies);
  }
  
  public static Collection<AgencyExt> QuerySelectedAgencies (List<String> selectedAgencies, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);	  
	  return dao.getSelectedAgencies(selectedAgencies);
  }
  
  public static Collection<RouteExt> QueryAllRoutes (int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);	  
	  return dao.getAllRoutesExt();
  }
  
  public static Collection<TripExt> QueryAllTrips (int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);	  
	  return dao.getAllTripsExt();
  }
  
  /*public static List<Route> QueryRoutesbyAgency (Agency agency, int dbindex){
	  GtfsMutableRelationalDao dao = factory[dbindex].getDao();	  
	  return dao.getRoutesForAgency(agency);
  }*/
  
  public static List<Trip> QueryTripsbyRoute (Route route, int dbindex){
	  GtfsMutableRelationalDao dao = factory[dbindex].getDao();	  
	  return dao.getTripsForRoute(route);
  }
  
  public static List<StopExt> QueryStopsbyAgency (String id, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);	  
	  return dao.getStopsForAgency(id);
  }
  /*public static List<Stop> QueryStopsbyRoute (AgencyAndId route, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);	  
	  return dao.getStopsForRoute(route);
  }*/
  
  public static List<StopExt> QueryStopsbyTrip (AgencyAndId trip, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);	  
	  return dao.getStopsForTrip(trip);
  }
  
  public static List<StopExt> QueryStopsbyTripCounty (AgencyAndId trip, String county, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);	  
	  return dao.getStopsForTripCounty(trip,county);
  }
  
  public static List<StopExt> QueryStopsbyTripRegion (AgencyAndId trip, String region, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);	  
	  return dao.getStopsForTripRegion(trip,region);
  }
  
  public static List<StopExt> QueryStopsbyTripTract (AgencyAndId trip, String tract, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);	  
	  return dao.getStopsForTripTract(trip,tract);
  }
  
  public static List<StopExt> QueryStopsbyTripPlace (AgencyAndId trip, String place, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);	  
	  return dao.getStopsForTripPlace(trip,place);
  }
  
  public static List<StopExt> QueryStopsbyTripUrban (AgencyAndId trip, String urban, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);	  
	  return dao.getStopsForTripUrban(trip,urban);
  }
  
  public static List<StopExt> QueryStopsbyTripUrbans (AgencyAndId trip, List<String> urbans, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);	  
	  return dao.getStopsForTripUrbans(trip,urbans);
  }
  
  public static HashMap<String, Integer> QueryCounts (int dbindex, List<String> selectedAgencies){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);	  
	  return dao.getCounts(selectedAgencies);
  }
  
  public static List<StopExt> QueryStopsbyTripCongdist (AgencyAndId trip, String congdist, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);	  
	  return dao.getStopsForTripCongdist(trip,congdist);
  }
  
  public static Long QueryStopsCount(int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);	  
	  return dao.getStopsCount();
  }
  
  public static Long QueryStopsCount( List<String> selectedAgencies, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);	  
	  return dao.getStopsCount(selectedAgencies);
  }
  
  public static String QueryServiceHours (List<String> trips, int dbindex){
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);	  
	  return dao.getServiceHours(trips);
  }
  
  public static List<FeedInfoExt> QueryFeedInfoByDefAgencyId(String defaultAgency, int dbindex) {
	  HibernateGtfsRelationalDaoImplExt dao = new HibernateGtfsRelationalDaoImplExt(sessions[dbindex]);	  
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

  private static HibernateGtfsFactory createHibernateGtfsFactory(String resource, int k) {

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
    sessions[k] = sessionFactory;
    return new HibernateGtfsFactory(sessionFactory);
  }

}