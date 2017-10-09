package com.bean;

import oracle.sql.BLOB;

public class regionBean {
private String name;
private BLOB extent;
private String x;
private String y;
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public BLOB getExtent() {
	return extent;
}
public void setExtent(BLOB extent) {
	this.extent = extent;
}
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
}
