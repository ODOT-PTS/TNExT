package com.model.database.queries.util;

import java.util.Collection;
import java.util.Iterator;

public class StringUtils {
	public static String join(Collection s, String delimiter) {
	    StringBuffer buffer = new StringBuffer();
	    Iterator iter = s.iterator();
	    while (iter.hasNext()) {
	        buffer.append(iter.next());
	        if (iter.hasNext()) {
	            buffer.append(delimiter);
	        }
	    }
	    return buffer.toString();
	}
	
	public static String roundjoin(Collection s, String delimiter) {
	    StringBuffer buffer = new StringBuffer();
	    Iterator iter = s.iterator();
	    while (iter.hasNext()) {
	        buffer.append(String.valueOf(Math.round(328.084*((Double)iter.next()))/100.00));
	        if (iter.hasNext()) {
	            buffer.append(delimiter);
	        }
	    }
	    return buffer.toString();
	}
	public static String timefromint(int time){
		String ampm = "AM";
		int day = 0;
		int hour = time/3600;
		int minute = (time - (hour * 3600))/60;
		while (hour>=24){
			hour=hour-24;
			day++;
		}		
		if (hour >=12){
			hour = (hour>12)? hour-12:hour;
			ampm = "PM";
		}
		hour = (hour==0)? 12:hour;
		String dday = (day>0)? "+"+String.valueOf(day)+":":"";
		String Hhour = (String.valueOf(hour).length()==1)? "0"+String.valueOf(hour):String.valueOf(hour);
		String Mminute = (String.valueOf(minute).length()==1)? "0"+String.valueOf(minute):String.valueOf(minute);
		String response = dday+Hhour+":"+Mminute+ampm;
		return response;
	}

}
