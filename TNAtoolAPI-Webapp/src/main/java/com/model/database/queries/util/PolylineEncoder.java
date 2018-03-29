/**
 * Reimplementation of Mark McClures Javascript PolylineEncoder
 * All the mathematical logic is more or less copied by McClure
 *  
 * @author Mark Rambow
 * @e-mail markrambow[at]gmail[dot]com
 * @version 0.1
 
 * 2015
 * Modified by Oregon State University - School of Mechanical,Industrial and Manufacturing Engineering 

 */

package com.model.database.queries.util;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.onebusaway.gtfs.model.ShapePoint;


public class PolylineEncoder {



	private double verySmall = 0.00001;

	//private boolean forceEndpoints = true;

	//private double[] zoomLevelBreaks;

	private HashMap<String, Double> bounds;

	public static String createEncodings(List <ShapePoint> points, int step) {

		StringBuffer encodedPoints = new StringBuffer();		
		int plat = 0;
		int plng = 0;
		int counter = 0;
		int listSize = points.size();		

		for (int i = 0; i < listSize; i += step) {
			counter++;			
			ShapePoint sp = points.get(i);
			int late5 = floor1e5(sp.getLat());
			int lnge5 = floor1e5(sp.getLon());

			int dlat = late5 - plat;
			int dlng = lnge5 - plng;

			plat = late5;
			plng = lnge5;

			encodedPoints.append(encodeSignedNumber(dlat)).append(
					encodeSignedNumber(dlng));			

		}

		// logger.debug("listSize: " + listSize + " step: " + step + " counter: " + counter);		
		return encodedPoints.toString();
	}
	
	/**
	 * Douglas-Peucker algorithm, adapted for encoding
	 * 
	 * @return HashMap [EncodedPoints;EncodedLevels]
	 * 
	 */
	
	public String dpEncode( List<ShapePoint> points) {
		int i, maxLoc = 0;
		Stack<int[]> stack = new Stack<int[]>();
		double[] dists = new double[points.size()];
		
		double maxDist, absMaxDist = 0.0, temp = 0.0;
		int[] current;
		String encodedPoints; //encodedLevels;

		if (points.size() > 2) {
			int[] stackVal = new int[] { 0, (points.size() - 1) };
			stack.push(stackVal);

			while (stack.size() > 0) {
				current = stack.pop();
				maxDist = 0;

				for (i = current[0] + 1; i < current[1]; i++) {
					temp = this.distance(points.get(i), points.get(current[0]), points.get(current[1]));
					if (temp > maxDist) {
						maxDist = temp;
						maxLoc = i;
						if (maxDist > absMaxDist) {
							absMaxDist = maxDist;
						}
					}
				}
				if (maxDist > this.verySmall) {
					dists[maxLoc] = maxDist;
					int[] stackValCurMax = { current[0], maxLoc };
					stack.push(stackValCurMax);
					int[] stackValMaxCur = { maxLoc, current[1] };
					stack.push(stackValMaxCur);
				}
			}
		}

		// logger.debug("createEncodings(" + track.getTrackpoints().size()
		// + "," + dists.length + ")");
		encodedPoints = createEncodings(points, dists);
		// logger.debug("encodedPoints \t\t: " + encodedPoints);
		// encodedPoints.replace("\\","\\\\");
		encodedPoints = replace(encodedPoints, "\\", "\\\\");
		//logger.debug("encodedPoints slashy?\t\t: " + encodedPoints);

		//encodedLevels = encodeLevels(track.getTrackpoints(), dists, absMaxDist);
		//logger.debug("encodedLevels: " + encodedLevels);

		//HashMap<String, String> hm = new HashMap<String, String>();
		//hm.put("encodedPoints", encodedPoints);
		//hm.put("encodedLevels", encodedLevels);
		return encodedPoints;

	}
	
	/**
	 * distance(p0, p1, p2) computes the distance between the point p0 and the
	 * segment [p1,p2]. This could probably be replaced with something that is a
	 * bit more numerically stable.
	 * 
	 * @param p0
	 * @param p1
	 * @param p2
	 * @return
	 */
	
	public double distance(ShapePoint p0, ShapePoint p1, ShapePoint p2) {
		double u, out = 0.0;

		if (p1.getLat() == p2.getLat()
				&& p1.getLon() == p2.getLon()) {
			out = Math.sqrt(Math.pow(p2.getLat() - p0.getLat(), 2)
					+ Math.pow(p2.getLon() - p0.getLon(), 2));
		} else {
			u = ((p0.getLat() - p1.getLat())
					* (p2.getLat() - p1.getLat()) + (p0
					.getLon() - p1.getLon())
					* (p2.getLon() - p1.getLon()))
					/ (Math.pow(p2.getLat() - p1.getLat(), 2) + Math
							.pow(p2.getLon() - p1.getLon(), 2));

			if (u <= 0) {
				out = Math.sqrt(Math.pow(p0.getLat() - p1.getLat(),
						2)
						+ Math.pow(p0.getLon() - p1.getLon(), 2));
			}
			if (u >= 1) {
				out = Math.sqrt(Math.pow(p0.getLat() - p2.getLat(),
						2)
						+ Math.pow(p0.getLon() - p2.getLon(), 2));
			}
			if (0 < u && u < 1) {
				out = Math.sqrt(Math.pow(p0.getLat() - p1.getLat()
						- u * (p2.getLat() - p1.getLat()), 2)
						+ Math.pow(p0.getLon() - p1.getLon() - u
								* (p2.getLon() - p1.getLon()), 2));
			}
		}
		return out;
	}
	
	
	
	private String createEncodings(List<ShapePoint> points, double[] dists) {
		StringBuffer encodedPoints = new StringBuffer();

		double maxlat = 0, minlat = 0, maxlon = 0, minlon = 0;

		int plat = 0;
		int plng = 0;
		
		for (int i = 0; i < points.size(); i++) {

			// determin bounds (max/min lat/lon)
			if (i == 0) {
				maxlat = minlat = points.get(i).getLat();
				maxlon = minlon = points.get(i).getLon();
			} else {
				if (points.get(i).getLat() > maxlat) {
					maxlat = points.get(i).getLat();
				} else if (points.get(i).getLat() < minlat) {
					minlat = points.get(i).getLat();
				} else if (points.get(i).getLon() > maxlon) {
					maxlon = points.get(i).getLon();
				} else if (points.get(i).getLon() < minlon) {
					minlon = points.get(i).getLon();
				}
			}

			if (dists[i] != 0 || i == 0 || i == points.size() - 1) {
				ShapePoint point = points.get(i);

				int late5 = floor1e5(point.getLat());
				int lnge5 = floor1e5(point.getLon());

				int dlat = late5 - plat;
				int dlng = lnge5 - plng;

				plat = late5;
				plng = lnge5;

				encodedPoints.append(encodeSignedNumber(dlat));
				encodedPoints.append(encodeSignedNumber(dlng));

			}
		}

		HashMap<String, Double> bounds = new HashMap<String, Double>();
		bounds.put("maxlat", new Double(maxlat));
		bounds.put("minlat", new Double(minlat));
		bounds.put("maxlon", new Double(maxlon));
		bounds.put("minlon", new Double(minlon));

		this.setBounds(bounds); 
		return encodedPoints.toString();
	}
	
	private static int floor1e5(double coordinate) {
		return (int) Math.floor(coordinate * 1e5);
	}
	
	private static String encodeSignedNumber(int num) {
		int sgn_num = num << 1;
		if (num < 0) {
			sgn_num = ~(sgn_num);
		}
		return (encodeNumber(sgn_num));
	}
	
	private static String encodeNumber(int num) {

		StringBuffer encodeString = new StringBuffer();

		while (num >= 0x20) {
			int nextValue = (0x20 | (num & 0x1f)) + 63;
			encodeString.append((char) (nextValue));
			num >>= 5;
		}

		num += 63;
		encodeString.append((char) (num));

		return encodeString.toString();
	}
	
	public String replace(String s, String one, String another) {
		// In a string replace one substring with another
		if (s.equals(""))
			return "";
		String res = "";
		int i = s.indexOf(one, 0);
		int lastpos = 0;
		while (i != -1) {
			res += s.substring(lastpos, i) + another;
			lastpos = i + one.length();
			i = s.indexOf(one, lastpos);
		}
		res += s.substring(lastpos); // the rest
		return res;
	}
	
	private void setBounds(HashMap<String, Double> bounds) {
		this.bounds = bounds;
	}
}
