/*
 *  This class in used to represent data for the connected stops
 *  in the Connected Agencies On-map Report
 *  
 */

package com.model.database.queries.objects;

import java.util.List;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CAStopsList")
public class CAStopsList {

	@XmlElement(name = "stopsList")
	public List<CAStop> stopsList = new ArrayList<CAStop>();
}
