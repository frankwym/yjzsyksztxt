package com.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.alibaba.fastjson.JSON;
import com.bean.ChartData;
import com.bean.ExcelFile;
import com.bean.GeoInfo;
import com.bean.GradeData;
import com.bean.MapFormat;
import com.dbconn.JdbcUtils;
import com.google.gson.Gson;

public class MakeGradeParam extends HttpServlet {

	// 测试
	String codeType = "codeType=";

	public MakeGradeParam() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.codeType = "codeType=";

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		// 获取参数
		String classificationIndex = request
				.getParameter("classificationIndex");
		String dataName = request.getParameter("dataName");
		String dataType = request.getParameter("dataType");
		String userName = request.getParameter("userName");

		String returnString = "";
		returnString += this.codeType + "&gradeArr="
				+ getCustomizeData(dataName, userName, classificationIndex);
		PrintWriter out = response.getWriter();
		out.println(returnString);
		System.out.println(returnString);
		out.flush();
		out.close();
	}

	// 根据所选的colIndex 构造chartData 用户上传数据
	private String getCustomizeData(String dataName, String userName,
			String classificationIndex) {
		String resultString = "";
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		List<ExcelFile> excelFiles = new ArrayList<>();
		String sql = "select * from whu_upload_data t where t.filename=? and t.userName=?";
		try {
			excelFiles = qr.query(sql, new BeanListHandler<ExcelFile>(
					ExcelFile.class), dataName, userName);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (excelFiles.size() == 0) {
			return "";
		}
		ExcelFile excelFile = excelFiles.get(0);
		Gson gson = new Gson();
		ChartData chardata = gson
				.fromJson(excelFile.getData(), ChartData.class);
		String codeType = "street";
		if ("quCode".equals(chardata.getColumns()[0])) {
			this.codeType += "street";
		} else {
			this.codeType += "distinct";
		}
		double[] newData = new double[chardata.getDatas().length];

		// 根据所选指标获取data

		int index = 0;// 指标classificationIndex的索引
		for (int i = 0; i < chardata.getColumns().length; i++) {
			String colString = chardata.getColumns()[i];
			if (colString.equals(classificationIndex)) {
				index = i;
				break;
			}
		}

		for (int i = 0; i < chardata.getDatas().length; i++) {
			String data = chardata.getDatas()[i][index];
			newData[i] = Double.parseDouble(data);
		}

		for (int i = 1; i < newData.length; i++) {
			double temp = newData[i]; // remove marked item
			int in = i; // start shifts at out
			while (in > 0 && newData[in - 1] >= temp) // until one is smaller,
			{
				newData[in] = newData[in - 1]; // shift item to right
				--in; // go left one position
			}
			newData[in] = temp;
		}
		return JSON.toJSONString(newData);
	}

	private List<GradeData> getGradeDatas(String dataName, String userName,
			String classificationIndex) throws Exception {
		List<GradeData> gradeDatas = new ArrayList<>();
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		List<ExcelFile> excelFiles = new ArrayList<>();
		String sql = "select * from whu_upload_data t where t.filename=? and t.userName=?";
		try {
			excelFiles = qr.query(sql, new BeanListHandler<ExcelFile>(
					ExcelFile.class), dataName, userName);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (excelFiles.size() == 0) {
			return gradeDatas;
		}
		ExcelFile excelFile = excelFiles.get(0);
		Gson gson = new Gson();
		ChartData chardata = gson
				.fromJson(excelFile.getData(), ChartData.class);
		String codeType = "street";
		if ("quCode".equals(chardata.getColumns()[0])) {
			this.codeType += "street";
		} else {
			this.codeType += "distinct";
		}

		// 根据所选指标获取data

		int index = 0;// 指标classificationIndex的索引
		for (int i = 0; i < chardata.getColumns().length; i++) {
			String colString = chardata.getColumns()[i];
			if (colString.equals(classificationIndex)) {
				index = i;
				break;
			}
		}

		for (int i = 0; i < chardata.getDatas().length; i++) {
			GradeData gradeData = new GradeData();
			String data = chardata.getDatas()[i][index];
			String code=chardata.getDatas()[i][0];
			gradeData.setCode(code);
			gradeData.setValue(Double.parseDouble(data));
			gradeData.setGeometry(getGeo(code, this.codeType));
			gradeDatas.add(gradeData);
		}
		Collections.sort(gradeDatas);
		return gradeDatas;
	}

	public String getGeo(String GeoCode, String GeoType) {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		List<GeoInfo> mapFormats = new ArrayList<>();
		String geo = "";
		String tableString = "";
		String codeString = "";
		if (GeoType.contains("district")) {
			tableString = "WHU_DISTRICT_DIC";
			codeString = "districtcode";
		} else {
			tableString = "WHU_STREET_DIC";
			codeString = "streetcode";
		}

		String sql = "select * from " + tableString + " t where t."
				+ codeString + "=?";
		try {
			mapFormats = qr.query(sql, new BeanListHandler<GeoInfo>(
					GeoInfo.class), GeoCode);
			geo = mapFormats.get(0).getGeo();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return geo;
	}
	
	public void init() throws ServletException {
		
	}
}
