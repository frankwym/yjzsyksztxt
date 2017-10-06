package com.bean;

import com.google.gson.Gson;

public class ChartData {
	private String[] columns;
	private String[][] datas;

	public String[] getColumns() {
		return columns;
	}

	public void setColumns(String[] columns) {
		this.columns = columns;
	}

	public String[][] getDatas() {
		return datas;
	}

	public void setDatas(String[][] datas) {
		this.datas = datas;
	}

	public static void main(String[] args) {
		String dataString="{\"columns\":[\"jiedaoCode\",\"quCode\",\"zhibao1\",\"zhibiao2\"],\"datas\":[[\"1011\",\"1101\",\"1\",\"2.0\"],[\"1011\",\"1101\",\"1\",\"2.0\"]]}";
		Gson gson=new Gson();
		ChartData chartData=gson.fromJson(dataString, ChartData.class);
		System.out.println(chartData.datas.length);
	}
}
