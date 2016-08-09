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
package com.model.database.queries.objects;

import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.IdentityBean;

import com.vividsolutions.jts.geom.LineString;

public class Geotrip extends IdentityBean<AgencyAndId> {

  private static final long serialVersionUID = 1L;

  private AgencyAndId id;
  private AgencyAndId routeId;
  private AgencyAndId serviceId;
  private String tripShortName;
  private String epshape;
  private double length;
  private double estlength;
  private String tripHeadsign;
  private String routeShortName;
  private String directionId;
  private String blockId;
  private AgencyAndId shapeId;
  private int wheelchairAccessible;
  private int stopscount;
  private int bikesAllowed;
  private LineString shape;
  private String uid;

  public Geotrip() {

  }

  public Geotrip(Geotrip obj) {
    this.id = obj.id;
    this.routeId = obj.routeId;
    this.serviceId = obj.serviceId;
    this.tripShortName = obj.tripShortName;
    this.tripHeadsign = obj.tripHeadsign;
    this.routeShortName = obj.routeShortName;
    this.directionId = obj.directionId;
    this.stopscount = obj.stopscount;
    this.blockId = obj.blockId;
    this.shapeId = obj.shapeId;
    this.wheelchairAccessible = obj.wheelchairAccessible;
    this.bikesAllowed = obj.bikesAllowed;
    this.epshape = obj.epshape;
    this.length = obj.length;
    this.estlength = obj.estlength;
    this.uid = obj.uid;
  }

  public AgencyAndId getId() {
    return id;
  }

  public void setId(AgencyAndId id) {
    this.id = id;
  }

  public AgencyAndId getRouteId() {
    return routeId;
  }
  
  public void setRouteId(AgencyAndId routeId) {
    this.routeId = routeId;
  }

  public AgencyAndId getServiceId() {
    return serviceId;
  }

  public void setServiceId(AgencyAndId serviceId) {
    this.serviceId = serviceId;
  }

  public String getTripShortName() {
    return tripShortName;
  }
  
  public int getStopscount(){
	  return stopscount;
  }

  public String getEpshape(){
	  return epshape;
  }
  
  public Double getLength(){
	  return length;
  }
  
  public Double getEstlength(){
	  return estlength;
  }
  
  public void setEpshape(String epshape){
	  this.epshape = epshape;
  }
  
  public void setLength(double length){
	  this.length = length;
  }
  
 public void setEstlength(double estlength){
	 this.estlength = estlength;
 }
 
  public void setTripShortName(String tripShortName) {
    this.tripShortName = tripShortName;
  }
  
  public void setStopscount(int stopscount) {
	    this.stopscount = stopscount;
	  }

  public String getTripHeadsign() {
    return tripHeadsign;
  }

  public void setTripHeadsign(String tripHeadsign) {
    this.tripHeadsign = tripHeadsign;
  }

  public String getRouteShortName() {
    return routeShortName;
  }

  public void setRouteShortName(String routeShortName) {
    this.routeShortName = routeShortName;
  }

  public String getDirectionId() {
    return directionId;
  }

  public void setDirectionId(String directionId) {
    this.directionId = directionId;
  }

  public String getBlockId() {
    return blockId;
  }

  public void setBlockId(String blockId) {
    this.blockId = blockId;
  }

  public AgencyAndId getShapeId() {
    return shapeId;
  }

  public void setShapeId(AgencyAndId shapeId) {
    this.shapeId = shapeId;
  }

  public void setWheelchairAccessible(int wheelchairAccessible) {
    this.wheelchairAccessible = wheelchairAccessible;
  }

  public int getWheelchairAccessible() {
    return wheelchairAccessible;
  }
  
  /**
   * @return 0 = unknown / unspecified, 1 = bikes allowed, 2 = bikes NOT allowed
   */
  public int getBikesAllowed() {
    return bikesAllowed;
  }

  /**
   * @param bikesAllowed 0 = unknown / unspecified, 1 = bikes allowed, 2 = bikes
   *          NOT allowed
   */
  public void setBikesAllowed(int bikesAllowed) {
    this.bikesAllowed = bikesAllowed;
  }

  public LineString getShape() {
	return shape;
}

public void setShape(LineString shape) {
	this.shape = shape;
}

public String getUid(){
	return uid;
}

public void setUid(String uid){
	this.uid = uid;
}

public String toString() {
    return "<Trip " + getId() + ">";
  }
}