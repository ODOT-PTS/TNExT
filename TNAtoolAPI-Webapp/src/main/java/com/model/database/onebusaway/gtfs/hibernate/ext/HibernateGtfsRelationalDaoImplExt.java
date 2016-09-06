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


package com.model.database.onebusaway.gtfs.hibernate.ext;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.hibernate.SessionFactory;
import org.onebusaway.gtfs.impl.HibernateGtfsRelationalDaoImpl;
//import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.AgencyAndId;
//import org.onebusaway.gtfs.model.FareRule;
//import org.onebusaway.gtfs.model.FeedInfo;
//import org.onebusaway.gtfs.model.Route;
//import org.onebusaway.gtfs.model.ServiceCalendar;
//import org.onebusaway.gtfs.model.ServiceCalendarDate;
//import org.onebusaway.gtfs.model.Stop;
//import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.services.GtfsMutableRelationalDao;

import com.model.database.onebusaway.gtfs.hibernate.objects.ext.*;

public class HibernateGtfsRelationalDaoImplExt extends
		HibernateGtfsRelationalDaoImpl /*implements GtfsMutableRelationalDao*/ {

	public HibernateGtfsRelationalDaoImplExt(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public Collection<AgencyExt> getSelectedAgencies(List<String> selectedAgencies) {
		return _ops.findByNamedQueryAndNamedParam("selectedAgenies", "sa",
				selectedAgencies);
	}

	public List<AgencyExt> getAllAgencies(List<String> selectedAgencies) {
		return _ops.findByNamedQueryAndNamedParam("allAgencies", "sa",
				selectedAgencies);
	}

	public List<TripExt> getTripsForAgency(String agencyId) {
		return _ops.findByNamedQueryAndNamedParam("tripsByAgency_uidsorted",
				"agencyId", agencyId);
	}

	public List<Double> getMaxTripLengthsForAgency(String agencyId) {
		return _ops.findByNamedQueryAndNamedParam("serviceMilesbyAgency",
				"agencyId", agencyId);
	}

	public List<TripExt> getTripsForAgency_RouteSorted(String agencyId) {
		return _ops.findByNamedQueryAndNamedParam("tripsByAgency_routesorted",
				"agencyId", agencyId);
	}

	public List<String> getRouteIdsForStop(StopExt stop) {
		return _ops.findByNamedQueryAndNamedParam("routeIdsForStop", "stop",
				stop);
	}

	public List<ServiceCalendarExt> getServiceCalendarsForRoute(RouteExt route) {
		return _ops.findByNamedQueryAndNamedParam("serviceCalendarsForRoute",
				"route", route);
	}

	public List<ServiceCalendarExt> getCalendarForAgency(String agency) {
		List<ServiceCalendarExt> calendars = _ops.findByNamedQueryAndNamedParam(
				"calendarForAgency", "agency", agency);
		switch (calendars.size()) {
		case 0:
			return null;
		default:
			return calendars;
		}
	}

	public List<ServiceCalendarDateExt> getCalendarDatesForAgency(String agency) {
		return _ops.findByNamedQueryAndNamedParam("calendarDatesForAgency",
				"agency", agency);
	}

	public List<FareRuleExt> getFareRuleForRoute(RouteExt route) {
		return _ops.findByNamedQueryAndNamedParam("fareRuleForRoute", "route",
				route);
	}

	public HashMap<String, Float> getFareDataForAgency(String agencyId) {
		HashMap<String, Float> response = new HashMap<String, Float>();
		List results = _ops.findByNamedQueryAndNamedParam("fareDataForAgency",
				"agency", agencyId);
		Object[] data = (Object[]) results.get(0);
		Object test = new Object();
		response.put("avg",
				(float) (Math.round(((Double) data[0]) * 100) / 100.0));
		response.put("min",
				(float) (Math.round(((Float) data[1]) * 100) / 100.0));
		response.put("max",
				(float) (Math.round(((Float) data[2]) * 100) / 100.0));
		response.put("count", (float) ((Long) data[3]));
		return response;
	}

	public HashMap<String, Float> getFareDataForState(
			List<String> selectedAgencies) {
		HashMap<String, Float> response = new HashMap<String, Float>();
		List results = _ops.findByNamedQueryAndNamedParam("fareDataForState",
				"sa", selectedAgencies);

		Object[] data = (Object[]) results.get(0);
		response.put("avg",
				(float) (Math.round(((Double) data[0]) * 100) / 100.0));
		response.put("min",
				(float) (Math.round(((Float) data[1]) * 100) / 100.0));
		response.put("max",
				(float) (Math.round(((Float) data[2]) * 100) / 100.0));
		response.put("count", (float) ((Long) data[3]));
		return response;
	}

	public Double getRouteMiles() {
		Double response = (Double) _ops.findByNamedQuery("RouteMilesForState")
				.get(0);
		response = Math.round(response * 100.00) / 100.00;
		return response;
	}

	public Double getRouteMiles(List<String> selectedAgencies) {
		Double response = (Double) _ops.findByNamedQueryAndNamedParam(
				"RouteMilesForState", "sa", selectedAgencies).get(0);
		response = Math.round(response * 100.00) / 100.00;
		return response;
	}

	public Float getFareMedianForAgency(String agency, int farecount) {
		/*if (farecount % 2 == 0) {
			List<Float> results = ((HibernateOperationsImplExt) _ops).findByNamedQueryAndNamedParamLimited(
					"fareMedianForAgency", "agency", agency, 2,
					(farecount / 2) - 1);
			return (float) (Math
					.round((results.get(0) + results.get(1)) * 50.0) / 100.0);
		} else {
			List<Float> results = ((HibernateOperationsImplExt) _ops).findByNamedQueryAndNamedParamLimited(
							"fareMedianForAgency", "agency", agency, 1,
							(farecount / 2));
			return results.get(0);
		}*/
		return (float) 0;
	}

	public Float getFareMedianForState(List<String> selectedAgencies,
			int farecount) {
		if (farecount % 2 == 0) {
			List<Float> results = ((HibernateOperationsImplExt) _ops).findByNamedQueryAndNamedParamLimited(
					"fareMedianForState", "sa", selectedAgencies, 2,
					(farecount / 2) - 1);
			return (float) (Math
					.round((results.get(0) + results.get(1)) * 50.0) / 100.0);
		} else {
			List<Float> results = ((HibernateOperationsImplExt) _ops).findByNamedQueryAndNamedParamLimited(
					"fareMedianForState", "sa", selectedAgencies, 1,
					(farecount / 2));
			return results.get(0);
		}
	}

	public List<Float> getFarePriceForRoutes(List<String> routes) {
		return _ops.findByNamedQueryAndNamedParam("farePriceForRoutes",
				"routes", routes);
	}

	public void updateTrip(TripExt trip) { 
		/*String[] names = { "length", "estlength", "shape", "tripid" };
		Object[] values = { trip.getLength(), trip.getEstlength(),
				trip.getEpshape(), trip.getId() };
		_ops.updateByNamedQueryAndNamedParams("updatedTripShpaeLength", names,
				values);*/
	}//to be fixed

	public List<StopExt> getStopsForRoute(AgencyAndId route) {
		String[] names = { "agency", "route" };
		Object[] values = { route.getAgencyId(), route.getId() };
		return _ops.findByNamedQueryAndNamedParams("stopsForRoute", names,
				values);
	}

	public List<StopExt> getStopsForAgency(String agency) {
		return _ops.findByNamedQueryAndNamedParam("stopsForAgency", "agency",
				agency);
	}

	public List<StopExt> getStopsForTrip(AgencyAndId trip) {
		String[] names = { "agency", "trip" };
		Object[] values = { trip.getAgencyId(), trip.getId() };
		return _ops.findByNamedQueryAndNamedParams("stopsForTrip", names,
				values);
	}

	public List<StopExt> getStopsForTripRegion(AgencyAndId trip, String region) {
		String[] names = { "agency", "trip", "region" };
		Object[] values = { trip.getAgencyId(), trip.getId(), region };
		return _ops.findByNamedQueryAndNamedParams("stopsForTripRegion", names,
				values);
	}

	public List<StopExt> getStopsForTripUrban(AgencyAndId trip, String urban) {
		String[] names = { "agency", "trip", "urban" };
		Object[] values = { trip.getAgencyId(), trip.getId(), urban };
		return _ops.findByNamedQueryAndNamedParams("stopsForTripUrban", names,
				values);
	}

	public List<StopExt> getStopsForTripCongdist(AgencyAndId trip, String congdist) {
		String[] names = { "agency", "trip", "congdist" };
		Object[] values = { trip.getAgencyId(), trip.getId(), congdist };
		return _ops.findByNamedQueryAndNamedParams("stopsForTripCongdist",
				names, values);
	}

	public Long getStopsCount() {
		List<Long> results = _ops.findByNamedQuery("allStopsCount");
		return results.get(0);
	}

	public Long getStopsCount(List<String> selectedAgencies) {
		List<Long> results = _ops.findByNamedQueryAndNamedParam(
				"allStopsCount", "sa", selectedAgencies);
		return results.get(0);
	}

	public List<FeedInfoExt> getFeedInfoByDefAgencyId(String defaultAgency) {
		return _ops.findByNamedQueryAndNamedParam("feedInfoByDefAgency",
				"defaultAgencyId", defaultAgency);
	}
	
	public String getServiceHours(List<String> trips) {
	    return _ops.findByNamedQueryAndNamedParam("serviceHoursbyTrip",
	            "trips", trips).get(0).toString();
	  }
	
	public HashMap<String, Integer> getCounts(List<String> selectedAgencies) {
		  HashMap<String, Integer> response = new HashMap<String, Integer>();
		  List results = _ops.findByNamedQueryAndNamedParam("counts", "sa", selectedAgencies);
		  Object[] counts = (Object[]) results.get(0);
		  response.put("agency", ((Long)counts[0]).intValue());
		  response.put("stop", ((Long)counts[1]).intValue());
		  response.put("route", ((Long)counts[2]).intValue());
	    return response;
	  }
	
	public List<StopExt> getStopsForTripTract(AgencyAndId trip, String tract) {	  
		  String[] names = {"agency", "trip", "tract"};
		  Object[] values = {trip.getAgencyId(), trip.getId(), tract};
		  return _ops.findByNamedQueryAndNamedParams("stopsForTripTract", names,
			        values);
	  }
	  
	  public List<StopExt> getStopsForTripPlace(AgencyAndId trip, String place) {	  
		  String[] names = {"agency", "trip", "place"};
		  Object[] values = {trip.getAgencyId(), trip.getId(), place};
		  return _ops.findByNamedQueryAndNamedParams("stopsForTripPlace", names,
			        values);
	  }
	  
	  public List<StopExt> getStopsForTripUrbans(AgencyAndId trip, List<String> urbans) {	  
		  String[] names = {"agency", "trip", "urbans"};
		  Object[] values = {trip.getAgencyId(), trip.getId(), urbans};
		  return _ops.findByNamedQueryAndNamedParams("stopsForTripUrbans", names,
			        values);
	  }
	  
	  public List<StopExt> getStopsForTripCounty(AgencyAndId trip, String county) {	  
		  String[] names = {"agency", "trip", "county"};
		  Object[] values = {trip.getAgencyId(), trip.getId(), county};
		  return _ops.findByNamedQueryAndNamedParams("stopsForTripCounty", names,
			        values);
	  }
	  
	  
	  
	  public AgencyExt getAgencyForIdExt(String id) {
		  return (AgencyExt) _ops.get(AgencyExt.class, id);
	  }
		
	  public TripExt getTripForIdExt(AgencyAndId id) {
		  return (TripExt) _ops.get(TripExt.class, id);
	  }
		
	  public RouteExt getRouteForIdExt(AgencyAndId id) {
		  return (RouteExt) _ops.get(RouteExt.class, id);
	  }
		
	  public StopExt getStopForIdExt(AgencyAndId id) {
		  return (StopExt) _ops.get(StopExt.class, id);
	  }
	
	  public Collection<FeedInfoExt> getAllFeedInfosExt() {
		  return _ops.find("FROM FeedInfo");
	  }
	
	  public Collection<RouteExt> getAllRoutesExt() {
		  return _ops.find("FROM Route route");
	  }
	
	  public Collection<TripExt> getAllTripsExt() {
		  return _ops.find("FROM Trip ORDER BY id.agencyId");
	  }
	  
}
