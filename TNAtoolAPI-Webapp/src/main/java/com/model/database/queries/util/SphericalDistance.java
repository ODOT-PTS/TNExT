package com.model.database.queries.util;

import java.util.List;

import org.onebusaway.gtfs.model.ShapePoint;

public class SphericalDistance {
	
	public static double sLength (List<ShapePoint> points){
		double SL = 0;
		for (int i =0; i < points.size()-1; i++ ){
			SL+=sDistance(points.get(i).getLat(),points.get(i).getLon(), points.get(i+1).getLat(),points.get(i+1).getLon());
		}
		return SL;
	}
	
    public static double sDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        if (dist>1) dist =1;
        if (dist<-1) dist = -1;
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;      
        return (dist);
      }

      /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
      /*::  This function converts decimal degrees to radians             :*/
      /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
      private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
      }

      /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
      /*::  This function converts radians to decimal degrees             :*/
      /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
      private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
      }
}
