package com.webapp.api;

import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class HeatMap1 {


	
	@XmlAttribute
    @JsonSerialize
	public ArrayList<HeatMap3> a;

	@XmlAttribute
    @JsonSerialize
	public ArrayList<HeatMap3> b;
	
	@XmlAttribute
    @JsonSerialize
	public ArrayList<HeatMap3> c;
	
	@XmlAttribute
    @JsonSerialize
	public ArrayList<HeatMap3> d;
	
	@XmlAttribute
    @JsonSerialize
	public ArrayList<HeatMap3> e;
	
	@XmlAttribute
    @JsonSerialize
	public ArrayList<HeatMap3> f;
	
	@XmlAttribute
    @JsonSerialize
	public ArrayList<HeatMap3> g;
	
	@XmlAttribute
    @JsonSerialize
	public ArrayList<HeatMap3> h;
	

	@XmlAttribute
    @JsonSerialize
	public ArrayList<HeatMap3> i;

	@XmlAttribute
    @JsonSerialize
	public ArrayList<HeatMap3> j;
	
	@XmlAttribute
    @JsonSerialize
	int total;

}