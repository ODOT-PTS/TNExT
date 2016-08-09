/* This program is free software: you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation, either version 3 of
 the License, or (props, at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>. */

package com.model.database.queries.objects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name = "CountyR")
public class GeoR {
	
	
	@XmlAttribute
    @JsonSerialize
	public String Name;
	
	@XmlAttribute
    @JsonSerialize
    public String id;
	
	@XmlAttribute
    @JsonSerialize
    public String population;
	
	@XmlAttribute
    @JsonSerialize
    public String urbanpop;
	
	@XmlAttribute
    @JsonSerialize
    public String ruralpop;
	
	@XmlAttribute
    @JsonSerialize
    public String landArea;
	
	@XmlAttribute
    @JsonSerialize
    public String waterArea;
	
	@XmlAttribute
    @JsonSerialize
    public String ODOTRegion;
	
	@XmlAttribute
    @JsonSerialize
    public String ODOTRegionName;
	
	@XmlAttribute
    @JsonSerialize
    public String AgenciesCount;	
	
	@XmlAttribute
    @JsonSerialize
    public String StopsCount;
	
	@XmlAttribute
    @JsonSerialize
    public String RoutesCount;
	
	@XmlAttribute
    @JsonSerialize
    public String UrbansCount;
	
	@XmlAttribute
    @JsonSerialize
    public String CountiesCount;
		
	@XmlAttribute
    @JsonSerialize
    public String TractsCount;
	
	@XmlAttribute
    @JsonSerialize
    public String PlacesCount;
	
	@XmlAttribute
    @JsonSerialize
    public String RegionsCount;
	
	@XmlAttribute
    @JsonSerialize
    public String CongDistsCount;
	
	@XmlAttribute
    @JsonSerialize
    public String BlocksCount;
	
	@XmlAttribute
    @JsonSerialize
    public String AverageFare;
	
	@XmlAttribute
    @JsonSerialize
    public String MedianFare;
       	
}
