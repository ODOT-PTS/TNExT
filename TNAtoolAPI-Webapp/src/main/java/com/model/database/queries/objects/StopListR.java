/* This program is free software: you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation, either version 3 of
 the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>. */

package com.model.database.queries.objects;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@XmlRootElement(name = "AgencySRList")
public class StopListR {
	
	@XmlAttribute
    @JsonSerialize
	public String AgencyName;
	
	@XmlAttribute
    @JsonSerialize
	public String AreaName;
	
	@XmlAttribute
    @JsonSerialize
	public String AreaType;
	
	@XmlAttribute
    @JsonSerialize
	public String metadata;
	
	@XmlElement(name = "StopListR")
    public Collection<StopR> StopR = new ArrayList<StopR>();

}
