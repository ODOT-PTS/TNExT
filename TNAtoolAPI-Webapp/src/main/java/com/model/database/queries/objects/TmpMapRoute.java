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
