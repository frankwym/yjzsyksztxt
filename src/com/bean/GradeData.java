package com.bean;

public class GradeData implements Comparable {

	private String code;
	private Double value;
	private String geometry;
	private String color;// 色彩值

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String getGeometry() {
		return geometry;
	}

	public void setGeometry(String geometry) {
		this.geometry = geometry;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	@Override
	public int compareTo(Object o) {
		GradeData sdto = (GradeData) o;
		Double otherAge = sdto.getValue();
		return this.value.compareTo(otherAge);
	}
}