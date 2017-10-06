package com.bean;

import java.util.Date;

public class UserMap {

	private String mapName;
	private String userId;
	private String cutomizeMapRecords;
	private Date date;

	public String getMapName() {
		return mapName;
	}
	public void setMapName(String mapName) {
		this.mapName = mapName;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getCutomizeMapRecords() {
		return cutomizeMapRecords;
	}
	public void setCutomizeMapRecords(String cutomizeMapRecords) {
		this.cutomizeMapRecords = cutomizeMapRecords;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
