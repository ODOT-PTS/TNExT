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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement( name = "EmpData")
public class EmpData {
	
	@XmlAttribute
	@JsonSerialize
	public String id;	
	@XmlAttribute
	@JsonSerialize
	public String name;
	@XmlAttribute
	@JsonSerialize
	public int c000;
	@XmlAttribute
	@JsonSerialize
	public int ca01;
	@XmlAttribute
	@JsonSerialize
	public int ca02;
	@XmlAttribute
	@JsonSerialize
	public int ca03;	
	@XmlAttribute
	@JsonSerialize
	public int ce01;
	@XmlAttribute
	@JsonSerialize
	public int ce02;
	@XmlAttribute
	@JsonSerialize
	public int ce03;	
	@XmlAttribute
	@JsonSerialize
	public int cns01;
	@XmlAttribute
	@JsonSerialize
	public int cns02;	
	@XmlAttribute
	@JsonSerialize
	public int cns03;
	@XmlAttribute
	@JsonSerialize
	public int cns04;
	@XmlAttribute
	@JsonSerialize
	public int cns05;
	@XmlAttribute
	@JsonSerialize
	public int cns06;
	@XmlAttribute
	@JsonSerialize
	public int cns07;	
	@XmlAttribute
	@JsonSerialize
	public int cns08;
	@XmlAttribute
	@JsonSerialize
	public int cns09;
	@XmlAttribute
	@JsonSerialize
	public int cns10;
	@XmlAttribute
	@JsonSerialize
	public int cns11;
	@XmlAttribute
	@JsonSerialize
	public int cns12;	
	@XmlAttribute
	@JsonSerialize
	public int cns13;
	@XmlAttribute
	@JsonSerialize
	public int cns14;
	@XmlAttribute
	@JsonSerialize
	public int cns15;
	@XmlAttribute
	@JsonSerialize
	public int cns16;
	@XmlAttribute
	@JsonSerialize
	public int cns17;	
	@XmlAttribute
	@JsonSerialize
	public int cns18;
	@XmlAttribute
	@JsonSerialize
	public int cns19;
	@XmlAttribute
	@JsonSerialize
	public int cns20;	
	@XmlAttribute
	@JsonSerialize
	public int cr01;
	@XmlAttribute
	@JsonSerialize
	public int cr02;	
	@XmlAttribute
	@JsonSerialize
	public int cr03;
	@XmlAttribute
	@JsonSerialize
	public int cr04;
	@XmlAttribute
	@JsonSerialize
	public int cr05;
	@XmlAttribute
	@JsonSerialize
	public int cr07;	
	@XmlAttribute
	@JsonSerialize
	public int ct01;
	@XmlAttribute
	@JsonSerialize
	public int ct02;	
	@XmlAttribute
	@JsonSerialize
	public int cd01;
	@XmlAttribute
	@JsonSerialize
	public int cd02;	
	@XmlAttribute
	@JsonSerialize
	public int cd03;
	@XmlAttribute
	@JsonSerialize
	public int cd04;	
	@XmlAttribute
	@JsonSerialize
	public int cs01;
	@XmlAttribute
	@JsonSerialize
	public int cs02;	

	@XmlAttribute
	@JsonSerialize
	public long c000served;
	@XmlAttribute
	@JsonSerialize
	public long ca01served;
	@XmlAttribute
	@JsonSerialize
	public long ca02served;
	@XmlAttribute
	@JsonSerialize
	public long ca03served;	
	@XmlAttribute
	@JsonSerialize
	public long ce01served;
	@XmlAttribute
	@JsonSerialize
	public long ce02served;
	@XmlAttribute
	@JsonSerialize
	public long ce03served;	
	@XmlAttribute
	@JsonSerialize
	public long cns01served;
	@XmlAttribute
	@JsonSerialize
	public long cns02served;	
	@XmlAttribute
	@JsonSerialize
	public long cns03served;
	@XmlAttribute
	@JsonSerialize
	public long cns04served;
	@XmlAttribute
	@JsonSerialize
	public long cns05served;
	@XmlAttribute
	@JsonSerialize
	public long cns06served;
	@XmlAttribute
	@JsonSerialize
	public long cns07served;	
	@XmlAttribute
	@JsonSerialize
	public long cns08served;
	@XmlAttribute
	@JsonSerialize
	public long cns09served;
	@XmlAttribute
	@JsonSerialize
	public long cns10served;
	@XmlAttribute
	@JsonSerialize
	public long cns11served;
	@XmlAttribute
	@JsonSerialize
	public long cns12served;	
	@XmlAttribute
	@JsonSerialize
	public long cns13served;
	@XmlAttribute
	@JsonSerialize
	public long cns14served;
	@XmlAttribute
	@JsonSerialize
	public long cns15served;
	@XmlAttribute
	@JsonSerialize
	public long cns16served;
	@XmlAttribute
	@JsonSerialize
	public long cns17served;	
	@XmlAttribute
	@JsonSerialize
	public long cns18served;
	@XmlAttribute
	@JsonSerialize
	public long cns19served;
	@XmlAttribute
	@JsonSerialize
	public long cns20served;	
	@XmlAttribute
	@JsonSerialize
	public long cr01served;
	@XmlAttribute
	@JsonSerialize
	public long cr02served;	
	@XmlAttribute
	@JsonSerialize
	public long cr03served;
	@XmlAttribute
	@JsonSerialize
	public long cr04served;
	@XmlAttribute
	@JsonSerialize
	public long cr05served;
	@XmlAttribute
	@JsonSerialize
	public long cr07served;	
	@XmlAttribute
	@JsonSerialize
	public long ct01served;
	@XmlAttribute
	@JsonSerialize
	public long ct02served;	
	@XmlAttribute
	@JsonSerialize
	public long cd01served;
	@XmlAttribute
	@JsonSerialize
	public long cd02served;	
	@XmlAttribute
	@JsonSerialize
	public long cd03served;
	@XmlAttribute
	@JsonSerialize
	public long cd04served;	
	@XmlAttribute
	@JsonSerialize
	public long cs01served;
	@XmlAttribute
	@JsonSerialize
	public long cs02served;
	
	@XmlAttribute
	@JsonSerialize
	public long c000los;
	@XmlAttribute
	@JsonSerialize
	public long ca01los;
	@XmlAttribute
	@JsonSerialize
	public long ca02los;
	@XmlAttribute
	@JsonSerialize
	public long ca03los;	
	@XmlAttribute
	@JsonSerialize
	public long ce01los;
	@XmlAttribute
	@JsonSerialize
	public long ce02los;
	@XmlAttribute
	@JsonSerialize
	public long ce03los;	
	@XmlAttribute
	@JsonSerialize
	public long cns01los;
	@XmlAttribute
	@JsonSerialize
	public long cns02los;	
	@XmlAttribute
	@JsonSerialize
	public long cns03los;
	@XmlAttribute
	@JsonSerialize
	public long cns04los;
	@XmlAttribute
	@JsonSerialize
	public long cns05los;
	@XmlAttribute
	@JsonSerialize
	public long cns06los;
	@XmlAttribute
	@JsonSerialize
	public long cns07los;	
	@XmlAttribute
	@JsonSerialize
	public long cns08los;
	@XmlAttribute
	@JsonSerialize
	public long cns09los;
	@XmlAttribute
	@JsonSerialize
	public long cns10los;
	@XmlAttribute
	@JsonSerialize
	public long cns11los;
	@XmlAttribute
	@JsonSerialize
	public long cns12los;	
	@XmlAttribute
	@JsonSerialize
	public long cns13los;
	@XmlAttribute
	@JsonSerialize
	public long cns14los;
	@XmlAttribute
	@JsonSerialize
	public long cns15los;
	@XmlAttribute
	@JsonSerialize
	public long cns16los;
	@XmlAttribute
	@JsonSerialize
	public long cns17los;	
	@XmlAttribute
	@JsonSerialize
	public long cns18los;
	@XmlAttribute
	@JsonSerialize
	public long cns19los;
	@XmlAttribute
	@JsonSerialize
	public long cns20los;	
	@XmlAttribute
	@JsonSerialize
	public long cr01los;
	@XmlAttribute
	@JsonSerialize
	public long cr02los;	
	@XmlAttribute
	@JsonSerialize
	public long cr03los;
	@XmlAttribute
	@JsonSerialize
	public long cr04los;
	@XmlAttribute
	@JsonSerialize
	public long cr05los;
	@XmlAttribute
	@JsonSerialize
	public long cr07los;	
	@XmlAttribute
	@JsonSerialize
	public long ct01los;
	@XmlAttribute
	@JsonSerialize
	public long ct02los;	
	@XmlAttribute
	@JsonSerialize
	public long cd01los;
	@XmlAttribute
	@JsonSerialize
	public long cd02los;	
	@XmlAttribute
	@JsonSerialize
	public long cd03los;
	@XmlAttribute
	@JsonSerialize
	public long cd04los;	
	@XmlAttribute
	@JsonSerialize
	public long cs01los;
	@XmlAttribute
	@JsonSerialize
	public long cs02los;
	
	@XmlAttribute
	@JsonSerialize
	public int c000within;
	@XmlAttribute
	@JsonSerialize
	public int ca01within;
	@XmlAttribute
	@JsonSerialize
	public int ca02within;
	@XmlAttribute
	@JsonSerialize
	public int ca03within;	
	@XmlAttribute
	@JsonSerialize
	public int ce01within;
	@XmlAttribute
	@JsonSerialize
	public int ce02within;
	@XmlAttribute
	@JsonSerialize
	public int ce03within;	
	@XmlAttribute
	@JsonSerialize
	public int cns01within;
	@XmlAttribute
	@JsonSerialize
	public int cns02within;	
	@XmlAttribute
	@JsonSerialize
	public int cns03within;
	@XmlAttribute
	@JsonSerialize
	public int cns04within;
	@XmlAttribute
	@JsonSerialize
	public int cns05within;
	@XmlAttribute
	@JsonSerialize
	public int cns06within;
	@XmlAttribute
	@JsonSerialize
	public int cns07within;	
	@XmlAttribute
	@JsonSerialize
	public int cns08within;
	@XmlAttribute
	@JsonSerialize
	public int cns09within;
	@XmlAttribute
	@JsonSerialize
	public int cns10within;
	@XmlAttribute
	@JsonSerialize
	public int cns11within;
	@XmlAttribute
	@JsonSerialize
	public int cns12within;	
	@XmlAttribute
	@JsonSerialize
	public int cns13within;
	@XmlAttribute
	@JsonSerialize
	public int cns14within;
	@XmlAttribute
	@JsonSerialize
	public int cns15within;
	@XmlAttribute
	@JsonSerialize
	public int cns16within;
	@XmlAttribute
	@JsonSerialize
	public int cns17within;	
	@XmlAttribute
	@JsonSerialize
	public int cns18within;
	@XmlAttribute
	@JsonSerialize
	public int cns19within;
	@XmlAttribute
	@JsonSerialize
	public int cns20within;	
	@XmlAttribute
	@JsonSerialize
	public int cr01within;
	@XmlAttribute
	@JsonSerialize
	public int cr02within;	
	@XmlAttribute
	@JsonSerialize
	public int cr03within;
	@XmlAttribute
	@JsonSerialize
	public int cr04within;
	@XmlAttribute
	@JsonSerialize
	public int cr05within;
	@XmlAttribute
	@JsonSerialize
	public int cr07within;	
	@XmlAttribute
	@JsonSerialize
	public int ct01within;
	@XmlAttribute
	@JsonSerialize
	public int ct02within;	
	@XmlAttribute
	@JsonSerialize
	public int cd01within;
	@XmlAttribute
	@JsonSerialize
	public int cd02within;	
	@XmlAttribute
	@JsonSerialize
	public int cd03within;
	@XmlAttribute
	@JsonSerialize
	public int cd04within;	
	@XmlAttribute
	@JsonSerialize
	public int cs01within;
	@XmlAttribute
	@JsonSerialize
	public int cs02within;
	
}
