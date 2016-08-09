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

@XmlRootElement(name = "StopR")
public class StopR {
	
	@XmlAttribute
    @JsonSerialize
	public String AgencyId;
	
	@XmlAttribute
    @JsonSerialize
	public String AgencyName;	
	
	@XmlAttribute
    @JsonSerialize
	public String StopName;
		
	@XmlAttribute
    @JsonSerialize
	public String CountyName;
	
	@XmlAttribute
    @JsonSerialize
	public String UrbanName;
	
	@XmlAttribute
    @JsonSerialize
	public String CongDistName;
	
	@XmlAttribute
    @JsonSerialize
	public String RegionName;
	
	@XmlAttribute
    @JsonSerialize
	public String PlaceName;
	
	@XmlAttribute
    @JsonSerialize
    public String StopId;
	
	@XmlAttribute
    @JsonSerialize
    public String URL;
	
	@XmlAttribute
    @JsonSerialize
    public String Routes;
	
	@XmlAttribute
    @JsonSerialize
    public String RPopWithinX;
	
	@XmlAttribute
    @JsonSerialize
    public String UPopWithinX;
	
	@XmlAttribute
    @JsonSerialize
    public String PopWithinX;
	
	@XmlAttribute
    @JsonSerialize
    public String OverFiftyPop;
	
	@XmlAttribute
    @JsonSerialize
    public String visits;
	
	@XmlAttribute
    @JsonSerialize
    public String lat;
	
	@XmlAttribute
    @JsonSerialize
    public String lon;
}
