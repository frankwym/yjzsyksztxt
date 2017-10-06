package com.bean;

import java.sql.Timestamp;
import java.util.Date;

public class ExcelFile {

	private String filename;
	private String username;
	private java.sql.Date datetime;
	private String data;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(java.sql.Date datetime) {
		this.datetime = datetime;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
