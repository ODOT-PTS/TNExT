/**
 * Copyright (C) 2011 Brian Ferris <bdferris@onebusaway.org>
 * Copyright (C) 2012 Google, Inc.
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

 * 2015
 * Modified by Oregon State University - School of Mechanical,Industrial and Manufacturing Engineering 

*/
package com.model.database.onebusaway.gtfs.hibernate.objects.ext;

import org.onebusaway.csv_entities.schema.annotations.CsvField;
import org.onebusaway.csv_entities.schema.annotations.CsvFields;
import org.onebusaway.gtfs.model.IdentityBean;
import org.onebusaway.gtfs.serialization.mappings.EntityFieldMappingFactory;

@CsvFields(filename = "transfers.txt", required = false)
public final class TransferExt extends IdentityBean<Integer> {

  private static final long serialVersionUID = 1L;

  private static final int MISSING_VALUE = -999;

  @CsvField(ignore = true)
  private int id;

  @CsvField(name = "from_stop_id", mapping = EntityFieldMappingFactory.class)
  private StopExt fromStop;

  @CsvField(name = "from_route_id", mapping = EntityFieldMappingFactory.class, optional = true)
  private RouteExt fromRoute;

  @CsvField(name = "from_trip_id", mapping = EntityFieldMappingFactory.class, optional = true)
  private TripExt fromTrip;

  @CsvField(name = "to_stop_id", mapping = EntityFieldMappingFactory.class)
  private StopExt toStop;

  @CsvField(name = "to_route_id", mapping = EntityFieldMappingFactory.class, optional = true)
  private RouteExt toRoute;

  @CsvField(name = "to_trip_id", mapping = EntityFieldMappingFactory.class, optional = true)
  private TripExt toTrip;

  private int transferType;
  
  @CsvField(name = "from_stop_id", optional = true, mapping = DefaultAgencyIdTranslator.class)
  private String defaultId;

  @CsvField(optional = true)
  private int minTransferTime = MISSING_VALUE;

  public TransferExt() {

  }

  public TransferExt(TransferExt obj) {
    this.id = obj.id;
    this.fromStop = obj.fromStop;
    this.fromRoute = obj.fromRoute;
    this.fromTrip = obj.fromTrip;
    this.toStop = obj.toStop;
    this.toRoute = obj.toRoute;
    this.toTrip = obj.toTrip;
    this.transferType = obj.transferType;
    this.minTransferTime = obj.minTransferTime;
    this.defaultId = obj.defaultId;
  }

  @Override
  public Integer getId() {
    return id;
  }

  @Override
  public void setId(Integer id) {
    this.id = id;
  }

  public StopExt getFromStop() {
    return fromStop;
  }

  public void setFromStop(StopExt fromStop) {
    this.fromStop = fromStop;
  }

  public RouteExt getFromRoute() {
    return fromRoute;
  }

  public void setFromRoute(RouteExt fromRoute) {
    this.fromRoute = fromRoute;
  }

  public TripExt getFromTrip() {
    return fromTrip;
  }

  public void setFromTrip(TripExt fromTrip) {
    this.fromTrip = fromTrip;
  }

  public StopExt getToStop() {
    return toStop;
  }

  public void setToStop(StopExt toStop) {
    this.toStop = toStop;
  }

  public RouteExt getToRoute() {
    return toRoute;
  }

  public void setToRoute(RouteExt toRoute) {
    this.toRoute = toRoute;
  }

  public TripExt getToTrip() {
    return toTrip;
  }

  public void setToTrip(TripExt toTrip) {
    this.toTrip = toTrip;
  }

  public int getTransferType() {
    return transferType;
  }

  public void setTransferType(int transferType) {
    this.transferType = transferType;
  }

  public boolean isMinTransferTimeSet() {
    return minTransferTime != MISSING_VALUE;
  }

  public int getMinTransferTime() {
    return minTransferTime;
  }

  public void setMinTransferTime(int minTransferTime) {
    this.minTransferTime = minTransferTime;
  }
  
  public String getDefaultId() {
    return defaultId;
  }

  public void setDefaultId(String defaultId) {
    this.defaultId = defaultId;
  }

  public void clearMinTransferTime() {
    this.minTransferTime = MISSING_VALUE;
  }

  public String toString() {
    return "<Transfer " + getId() + ">";
  }
}
