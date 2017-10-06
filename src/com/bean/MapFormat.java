package com.bean;

import java.sql.Timestamp;
import java.util.Date;

public class MapFormat {
	private String FORMATNAME;
	private String username;
	private java.sql.Date  time;
	private String content;
	public String getFORMATNAME() {
		return FORMATNAME;
	}
	public void setFORMATNAME(String FORMATNAME) {
		this.FORMATNAME = FORMATNAME;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(java.sql.Date  time) {
		this.time = time;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}


}
