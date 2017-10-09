package com.bean;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;

import Decoder.BASE64Encoder;

public class statisticData {
	private String name;
	private String value;
	public String getX() {
		return x;
	}
	public void setX(String x) {
		this.x = x;
	}
	public String getY() {
		return y;
	}
	public void setY(String y) {
		this.y = y;
	}
	private String x;
	private String y;
	private String regionname;
	private Blob extent;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value.trim();
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getRegionname() {
		return regionname;
	}
	public void setRegionname(String regionname) {
		this.regionname = regionname;
	}
	public Blob getExtent() {
		return extent;
	}
	public void setExtent(Blob extent) {
		this.extent = extent;
	}
	public String returnJson(){
		String returnString="{'name':'"+getName()+"','value':'"+getValue()+"','regionname':'"+getRegionname()+"'，'extent':'"+getExtent()+"'}";
		//String returnString=getName()+"',"+getValue()+"','regionname':'"+getRegionname()+"'，'extent':'"+getExtent()+"'}";
		
		return returnString;
	}
	
}