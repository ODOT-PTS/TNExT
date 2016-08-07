package com.model.database;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.onebusaway.gtfs.impl.HibernateGtfsRelationalDaoImpl;
import com.model.database.HibernateGtfsRelationalDaoImplExt;
import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.FareRule;
import org.onebusaway.gtfs.model.FeedInfo;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.ServiceCalendar;
import org.onebusaway.gtfs.model.ServiceCalendarDate;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.services.GtfsMutableRelationalDao;

public class HibernateGtfsRelationalDaoImplExt extends
		HibernateGtfsRelationalDaoImpl implements GtfsMutableRelationalDao {

	public Collection<Agency> getSelectedAgencies(List<String> selectedAgencies) {
		return _ops.findByNamedQueryAndNamedParam("selectedAgenies", "sa",
				selectedAgencies);
	}

	public List<Agency> getAllAgencies(List<String> selectedAgencies) {
		return _ops.findByNamedQueryAndNamedParam("allAgencies", "sa",
				selectedAgencies);
	}

	public List<Trip> getTripsForAgency(String agencyId) {
		return _ops.findByNamedQueryAndNamedParam("tripsByAgency_uidsorted",
				"agencyId", agencyId);
	}

	public List<Double> getMaxTripLengthsForAgency(String agencyId) {
		return _ops.findByNamedQueryAndNamedParam("serviceMilesbyAgency",
				"agencyId", agencyId);
	}

	public List<Trip> getTripsForAgency_RouteSorted(String agencyId) {
		return _ops.findByNamedQueryAndNamedParam("tripsByAgency_routesorted",
				"agencyId", agencyId);
	}

	public List<String> getRouteIdsForStop(Stop stop) {
		return _ops.findByNamedQueryAndNamedParam("routeIdsForStop", "stop",
				stop);
	}

	public List<ServiceCalendar> getServiceCalendarsForRoute(Route route) {
		return _ops.findByNamedQueryAndNamedParam("serviceCalendarsForRoute",
				"route", route);
	}

	public List<ServiceCalendar> getCalendarForAgency(String agency) {
		List<ServiceCalendar> calendars = _ops.findByNamedQueryAndNamedParam(
				"calendarForAgency", "agency", agency);
		switch (calendars.size()) {
		case 0:
			return null;
		default:
			return calendars;
		}
	}

	public List<ServiceCalendarDate> getCalendarDatesForAgency(String agency) {
		return _ops.findByNamedQueryAndNamedParam("calendarDatesForAgency",
				"agency", agency);
	}

	public List<FareRule> getFareRuleForRoute(Route route) {
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
		if (farecount % 2 == 0) {
			List<Float> results = _ops.findByNamedQueryAndNamedParamLimited(
					"fareMedianForAgency", "agency", agency, 2,
					(farecount / 2) - 1);
			return (float) (Math
					.round((results.get(0) + results.get(1)) * 50.0) / 100.0);
		} else {
			List<Float> results = _ops
					.findByNamedQueryAndNamedParamLimited(
							"fareMedianForAgency", "agency", agency, 1,
							(farecount / 2));
			return results.get(0);
		}
	}

	public Float getFareMedianForState(List<String> selectedAgencies,
			int farecount) {
		if (farecount % 2 == 0) {
			List<Float> results = _ops.findByNamedQueryAndNamedParamLimited(
					"fareMedianForState", "sa", selectedAgencies, 2,
					(farecount / 2) - 1);
			return (float) (Math
					.round((results.get(0) + results.get(1)) * 50.0) / 100.0);
		} else {
			List<Float> results = _ops.findByNamedQueryAndNamedParamLimited(
					"fareMedianForState", "sa", selectedAgencies, 1,
					(farecount / 2));
			return results.get(0);
		}
	}

	public List<Float> getFarePriceForRoutes(List<String> routes) {
		return _ops.findByNamedQueryAndNamedParam("farePriceForRoutes",
				"routes", routes);
	}

	public void updateTrip(Trip trip) {
		String[] names = { "length", "estlength", "shape", "tripid" };
		Object[] values = { trip.getLength(), trip.getEstlength(),
				trip.getEpshape(), trip.getId() };
		_ops.updateByNamedQueryAndNamedParams("updatedTripShpaeLength", names,
				values);
	}

	public List<Stop> getStopsForRoute(AgencyAndId route) {
		String[] names = { "agency", "route" };
		Object[] values = { route.getAgencyId(), route.getId() };
		return _ops.findByNamedQueryAndNamedParams("stopsForRoute", names,
				values);
	}

	public List<Stop> getStopsForAgency(String agency) {
		return _ops.findByNamedQueryAndNamedParam("stopsForAgency", "agency",
				agency);
	}

	public List<Stop> getStopsForTrip(AgencyAndId trip) {
		String[] names = { "agency", "trip" };
		Object[] values = { trip.getAgencyId(), trip.getId() };
		return _ops.findByNamedQueryAndNamedParams("stopsForTrip", names,
				values);
	}

	public List<Stop> getStopsForTripRegion(AgencyAndId trip, String region) {
		String[] names = { "agency", "trip", "region" };
		Object[] values = { trip.getAgencyId(), trip.getId(), region };
		return _ops.findByNamedQueryAndNamedParams("stopsForTripRegion", names,
				values);
	}

	public List<Stop> getStopsForTripUrban(AgencyAndId trip, String urban) {
		String[] names = { "agency", "trip", "urban" };
		Object[] values = { trip.getAgencyId(), trip.getId(), urban };
		return _ops.findByNamedQueryAndNamedParams("stopsForTripUrban", names,
				values);
	}

	public List<Stop> getStopsForTripCongdist(AgencyAndId trip, String congdist) {
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

	public List<FeedInfo> getFeedInfoByDefAgencyId(String defaultAgency) {
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
	
	public List<Stop> getStopsForTripTract(AgencyAndId trip, String tract) {	  
		  String[] names = {"agency", "trip", "tract"};
		  Object[] values = {trip.getAgencyId(), trip.getId(), tract};
		  return _ops.findByNamedQueryAndNamedParams("stopsForTripTract", names,
			        values);
	  }
	  
	  public List<Stop> getStopsForTripPlace(AgencyAndId trip, String place) {	  
		  String[] names = {"agency", "trip", "place"};
		  Object[] values = {trip.getAgencyId(), trip.getId(), place};
		  return _ops.findByNamedQueryAndNamedParams("stopsForTripPlace", names,
			        values);
	  }
	  
	  public List<Stop> getStopsForTripUrbans(AgencyAndId trip, List<String> urbans) {	  
		  String[] names = {"agency", "trip", "urbans"};
		  Object[] values = {trip.getAgencyId(), trip.getId(), urbans};
		  return _ops.findByNamedQueryAndNamedParams("stopsForTripUrbans", names,
			        values);
	  }
	  
	  public List<Stop> getStopsForTripCounty(AgencyAndId trip, String county) {	  
		  String[] names = {"agency", "trip", "county"};
		  Object[] values = {trip.getAgencyId(), trip.getId(), county};
		  return _ops.findByNamedQueryAndNamedParams("stopsForTripCounty", names,
			        values);
	  }
	  
}
