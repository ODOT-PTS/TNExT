// Copyright (C) 2015 Oregon State University - School of Mechanical,Industrial and Manufacturing Engineering 
//   This file is part of Transit Network Explorer Tool.
//
//    Transit Network Explorer Tool is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Transit Network Explorer Tool is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU  General Public License for more details.
//
//    You should have received a copy of the GNU  General Public License
//    along with Transit Network Explorer Tool.  If not, see <http://www.gnu.org/licenses/>.

package com.model.database.queries.objects;

import java.util.ArrayList;
import java.util.List;

public class TmpMapRoute {
	private int frequency;
	private String shape;
	private String shape0;
	private String shape1;
	private double length;
	private double length0;
	private double length1;
	private List<MapStop> mss;
	
	public TmpMapRoute(){
		this.frequency = 0;
		this.shape = "";
		this.shape0 = "";
		this.shape1 = "";
		this.length = 0;
		this.length0 = 0;
		this.length1 = 0;
		this.mss = new ArrayList<MapStop>();
	}
	
	public void setFrequency(int f){
		this.frequency = f;
	}
	
	public void incrementFrequency(){
		this.frequency ++;
	}
	
	public int getFrequency(){
		return this.frequency;
	}
	
	public void setShape(String s){
		this.shape = s;
	}
	
	public void setShape0(String s){
		this.shape0 = s;
	}
	
	public void setShape1(String s){
		this.shape1 = s;
	}
	
	public String getShape(){
		return this.shape;
	}
	
	public String getShape0(){
		return this.shape0;
	}
	
	public String getShape1(){
		return this.shape1;
	}
	
	public void setLength(double l){
		this.length = l;
	}
	
	public void setLength0(double l){
		this.length0 = l;
	}
	
	public void setLength1(double l){
		this.length1 = l;
	}
	
	public double getLength(){
		return this.length;
	}
	
	public double getLength0(){
		return this.length0;
	}
	
	public double getLength1(){
		return this.length1;
	}
	
	public void addStop(MapStop ms){
		this.mss.add(ms);
	}
	
	public List<MapStop> getMss(){
		return this.mss;
	}
	
}
