// Copyright (C) 2015 Oregon State University - School of Mechanical,Industrial and Manufacturing Engineering 
//   This file is part of Transit Network Analysis Software Tool.
//
//    Transit Network Analysis Software Tool is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Transit Network Analysis Software Tool is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU  General Public License for more details.
//
//    You should have received a copy of the GNU  General Public License
//    along with Transit Network Analysis Software Tool.  If not, see <http://www.gnu.org/licenses/>.


package com.model.database.queries.objects;

import javax.xml.bind.annotation.adapters.XmlAdapter;

//import com.model.database.onebusaway.gtfs.hibernate.objects.ext.AgencyExt;
import org.onebusaway.gtfs.model.Agency;

public class AgencyAdapter extends XmlAdapter<AgencyType, Agency> {

    @Override
    public Agency unmarshal(AgencyType arg) throws Exception {
        if (arg == null) {
            return null;
        }
        Agency a = new Agency();
        a.setId(arg.id);
        a.setName(arg.name);
        a.setUrl(arg.url);
        a.setTimezone(arg.timezone);
        a.setLang(arg.lang);
        a.setPhone(arg.phone);
        a.setFareUrl(arg.fareUrl);
        return new Agency(a);
    }

    @Override
    public AgencyType marshal(Agency arg) throws Exception {
        if (arg == null) {
            return null;
        }
        return new AgencyType(arg);
    }

}
