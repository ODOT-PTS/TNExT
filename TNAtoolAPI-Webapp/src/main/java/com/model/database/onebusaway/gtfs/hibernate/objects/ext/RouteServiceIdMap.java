/**
 * Copyright (C) 2011 Brian Ferris <bdferris@onebusaway.org>
 * Copyright (C) 2011 Google, Inc.
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
package com.model.database.onebusaway.gtfs.hibernate.objects.ext;

import org.onebusaway.csv_entities.schema.annotations.CsvField;
import org.onebusaway.gtfs.model.IdentityBean;

//@CsvFields(filename = "agency.txt", prefix = "agency_")
public final class RouteServiceIdMap extends IdentityBean<Integer> {

  private static final long serialVersionUID = 1L;

  //@CsvField(optional = true, mapping = AgencyIdTranslationFieldMappingFactory.class)
  @CsvField(ignore = true)
  private int id;

  private String serviceId;
  
  private String agencyId_def;
  
  private ServiceCalendarExt calendar;
  
  private RouteExt route;
  
  public void setCalendar(ServiceCalendarExt calendar) {
      this.calendar = calendar;
  }
  
  public void setRoute(RouteExt route) {
      this.route = route;
  }

  public RouteServiceIdMap() {

  }

  public RouteServiceIdMap(RouteServiceIdMap a) {
    this.id = a.id;    
    this.serviceId = a.serviceId;    
    this.agencyId_def = a.agencyId_def;
    this.route = a.route;
    this.calendar = a.calendar;
  }

  public Integer getId() {
    return id;
  }
  
  public String getAgencyId_def(){
	  return agencyId_def;
  }
  
   public ServiceCalendarExt getCalendar(){
	  return calendar;
  }
    
  public RouteExt getRoute(){
	  return route;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public void setAgencyId_def(String agencyId_def) {
	    this.agencyId_def = agencyId_def;
	  }
  
  public String getServiceId() {
    return serviceId;
  }

  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
  }
  
  public String toString() {
    return "<ServiceId " + this.serviceId + "Is used by "+ this.route.getId().getId()+ ">";
  }
}
