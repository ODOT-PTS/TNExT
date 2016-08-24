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
import org.onebusaway.gtfs.model.AgencyAndId;

public class AgencyAndIdAdapter extends XmlAdapter<AgencyAndIdType, AgencyAndId> {

    @Override
    public AgencyAndId unmarshal(AgencyAndIdType arg) throws Exception {
        if (arg == null) {
            return null;
        }
        return new AgencyAndId(arg.agency, arg.id);
    }

    @Override
    public AgencyAndIdType marshal(AgencyAndId arg) throws Exception {
        if (arg == null) {
            return null;
        }
        return new AgencyAndIdType(arg.getAgencyId(), arg.getId());
    }

}
