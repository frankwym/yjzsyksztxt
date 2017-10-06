package com.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jj2000.j2k.util.StringFormatException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.alibaba.fastjson.JSON;
import com.bean.ChartData;
import com.bean.Color;
import com.bean.ExcelFile;
import com.bean.GeoInfo;
import com.bean.GradeData;
import com.bean.MapFormat;
import com.dbconn.JdbcUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zj.bglayer.ModelPrim;

public class MakeChartParam extends HttpServlet {

	// 返回参数
	String codeType = "";
	String chartData = "";
	String statisticColors = "";
	String classColors = "";
	String chartId = "";
	String widthString = "";
	String heightString = "";
	String isLabel = "";
	String xyString = "";// xy坐标是
	String classNum = "";

	// 测试

	public MakeChartParam() {
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

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		// 返回参数
		this.codeType = "district";
		this.chartData = "";
		this.statisticColors = "";
		this.classColors = "";
		this.chartId = "";
		this.widthString = "";
		this.heightString = "";
		this.isLabel = "";
		this.xyString = "";// xy坐标是
		this.classNum = "";
		// 获取参数
		String classificationIndex = request
				.getParameter("classificationIndex");
		String statisticalIndex = request.getParameter("statisticalIndex");
		String dataName = request.getParameter("dataName");
		String dataType = request.getParameter("dataType");
		String mapFormatName = request.getParameter("mapFormatName");
		String mapFormatContent = request.getParameter("mapFormatContent");
		String userName = request.getParameter("userName");
		String classData=request.getParameter("classData");//国情数据上传分级
		String gradeColors=request.getParameter("gradeColors");
		// 获取分级的信息
		List<GradeData> gradeDatas = new ArrayList<>();
		// 设置模版样式 直接设置在字段里
		getFormatParam(mapFormatContent);
		if (dataType.equals("UpdataDatatree")) {// 如过是自定义数据
			this.chartData = getCustomizeData(dataName, userName,
					statisticalIndex);
			this.xyString = this.xyString
					+ getCustomizeAreaCode(dataName, userName);
			getFormatParam(mapFormatContent);
			if(classificationIndex!=""){
				try {
					gradeDatas = getGradeDatas(dataName, userName, classificationIndex,gradeColors);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
		} else if(dataType.equals("TrafficDatatree")||dataType.equals("FireDatatree")||dataType.equals("FloodDatatree")||dataType.equals("EarthquakeDatatree")){// 如果是其他数据
			String[] colorArr = { "#ff0000", "#00ff00", "#8080ff", "#ff8000",
					"#008080", "#804040", "#ffff00", "#ff00ff", "#00ffff",
					"#0088ff" };
			String colors = "";
			String[] statisticalIndexs = statisticalIndex.split(",");// 假设就是前台选取的指标
			for (int i = 0; i < statisticalIndexs.length; i++) {
				//String color = colorArr[(int) Math.round(Math.random() * 9)];
				String color = colorArr[i];
				String subColor = color.substring(1);
				int colorInt = Integer.parseInt(subColor, 16);
				if (i == 0) {
					colors = String.valueOf(colorInt);
					
				} else {
					colors = colors + ";" + String.valueOf(colorInt);
					
				}
			}
			this.statisticColors = colors;
			if(!classData.equals("[]")){
				List<GradeData> tempGradeDatas=new Gson().fromJson(classData,  new TypeToken<List<GradeData>>(){}.getType());
				try {
					gradeDatas=getGradeDatas(tempGradeDatas,gradeColors);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	
					
					this.widthString += "60";
					this.heightString += "60";
					this.isLabel += "false";
		// System.out.println(getCustomizeAreaCode(dataName, userName));
		String returnString = "0,0,60,60&0,0,60,60&";
		returnString = returnString + this.codeType + "&" + this.chartData
				+ "&" + this.chartId + "&" + this.statisticColors + "&"
				+ this.heightString + "&" + this.widthString + "&"
				+ this.isLabel + "&" + this.xyString + "&"
				+ JSON.toJSONString(gradeDatas)+"&"+getGradeLegendString(gradeDatas,gradeColors)+"&"+this.classColors.trim();
		PrintWriter out = response.getWriter();
		out.println(returnString);
		System.out.println(returnString);
		out.flush();
		out.close();
	}

	// 根据所选的colIndex 构造chartData 用户上传数据
	private String getCustomizeData(String dataName, String userName,
			String statisticalIndex) {
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
		String[] statisticalIndexs = statisticalIndex.split(",");// 假设就是前台选取的指标
		Gson gson = new Gson();
		ChartData chardata = gson
				.fromJson(excelFile.getData(), ChartData.class);

		String[][] newData = new String[chardata.getDatas().length][statisticalIndexs.length];

		// 根据所选指标获取data
		String dataString = "";
		for (int j = 0; j < statisticalIndexs.length; j++) {
			String itemString = "";
			int index = 0;// 指标j的索引
			for (int i = 0; i < chardata.getColumns().length; i++) {
				String colString = chardata.getColumns()[i];
				if (colString.equals(statisticalIndexs[j])) {
					index = i;
					break;
				}
			}

			for (int i = 0; i < chardata.getDatas().length; i++) {
				String data = chardata.getDatas()[i][index];
				newData[i][j] = data;
				if (i == 0) {
					itemString = data;
				} else {
					itemString = itemString + "," + data;
				}
			}
			if (j == 0) {
				dataString = itemString;
			} else {
				dataString = dataString + ";" + itemString;
			}
		}

		ChartData newChartData = new ChartData();
		newChartData.setColumns(statisticalIndexs);
		newChartData.setDatas(newData);

		String data1 = "", data2 = "", data3 = "", colors = "";
		String[] colorArr = { "#ff0000", "#00ff00", "#8080ff", "#ff8000",
				"#008080", "#804040", "#ffff00", "#ff00ff", "#00ffff",
				"#0088ff" };
		for (int i = 0; i < statisticalIndexs.length; i++) {
			//String color = colorArr[(int) Math.round(Math.random() * 9)];
			String color = colorArr[i];
			String subColor = color.substring(1);
			int colorInt = Integer.parseInt(subColor, 16);
			if (i == 0) {
				colors = String.valueOf(colorInt);
				data1 = statisticalIndexs[i];
				;
				data2 = "个";
			} else {
				colors = colors + ";" + String.valueOf(colorInt);
				data1 = data1 + "," + statisticalIndexs[i];
				data2 = data2 + "," + "个";
			}
		}
		this.statisticColors = colors;
		for (int i = 0; i < newData.length; i++) {
			String data = "";
			for (int j = 0; j < statisticalIndexs.length; j++) {

				if (j == 0) {
					data = newData[i][j];
				} else {
					data = data + "," + newData[i][j];
				}

			}
			data = data + "," + (i + 1) + "," + 0 + "," + 0 + "," + (i + 1);
			if (i == 0) {
				data3 = data;
			} else {
				data3 = data3 + ";" + data;
			}
		}

		// return JSON.toJSONString(newChartData);
		return data1 + ";" + data2 + ";" + data3;
	}

	public String GeoCoordinate(String[] GeoCode, String GeoType) {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		List<GeoInfo> mapFormats = new ArrayList<>();
		String Coordinate ="";
		String tableString = "";
		String codeString = "";
		if (GeoType == "district") {
			tableString = "WHU_DISTRICT_DIC";
			codeString = "districtcode";
		} else {
			tableString = "WHU_STREET_DIC";
			codeString = "streetcode";
		}
		for (int i = 0; i < GeoCode.length; i++) {
			String sql = "select * from " + tableString + " t where t."
					+ codeString + "=?";
			try {
				mapFormats = qr.query(sql, new BeanListHandler<GeoInfo>(
						GeoInfo.class), GeoCode[i]);
				String x = mapFormats.get(0).getX();
				String y = mapFormats.get(0).getY();
				String name = "";
				if (GeoType == "district") {
					name = mapFormats.get(0).getDISTRICTNAME();
				} else {
					name = mapFormats.get(0).getSTREETNAME();
				}
				Coordinate =Coordinate+","+ "{\"x\":"+x +",\"y\":"+y+",\"name\":\""+name+"\",\"code\":\""+GeoCode[i]+"\"}";
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return Coordinate;

	}

	// 根据所选的colIndex 构造地区编码 用户上传数据
	private String getCustomizeAreaCode(String dataName, String userName) {
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

		String reString = "";
		String codeType = "";
		HashMap<String, Object> hashMap = new HashMap<>();
		ExcelFile excelFile = excelFiles.get(0);
		Gson gson = new Gson();
		ChartData chardata = gson
				.fromJson(excelFile.getData(), ChartData.class);
		int index = 0;// 指标j的索引
		for (int i = 0; i < chardata.getColumns().length; i++) {
			String colString = chardata.getColumns()[i];
			if (colString.equals("quCode")) {
				index = i;
				codeType = "district";
				break;
			}
			if (colString.equals("jiedaoCode")) {
				index = i;
				codeType = "street";
				break;
			}
		}

		String[] codeArr = new String[chardata.getDatas().length];
		for (int i = 0; i < chardata.getDatas().length; i++) {
			String data = chardata.getDatas()[i][index];
			codeArr[i] = data;
		}
		String xyString = GeoCoordinate(codeArr, codeType);
		this.codeType = codeType;
		// hashMap.put("codeType", codeType);
		// hashMap.put("codeAtrr", codeArr);

		//hashMap.put("xyString", xyString);
		String xyString2="["+xyString.substring(1)+"]";
		//reString = JSON.toJSONString(hashMap);
		return xyString2;
	}

	//
	private void getFormatParam(String mapFormatContent) {
		
		String[] mapFormat=mapFormatContent.split(",");
		if (this.codeType.contains("district")) {
			this.classNum = mapFormat[0];
			this.classColors= mapFormat[1];
			this.chartId = mapFormat[2];
		} else {
			this.classNum = mapFormat[3];
			this.classColors = mapFormat[4];
			this.chartId = mapFormat[5];
		}
	}

	// 获取分级数据
	private List<GradeData> getGradeDatas(String dataName, String userName,
			String classificationIndex,String gradeColors) throws Exception {
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
		// 根据所选指标获取data

		int index = 0;// 指标classificationIndex的索引
		for (int i = 0; i < chardata.getColumns().length; i++) {
			String colString = chardata.getColumns()[i];
			if (colString.equals(classificationIndex)) {
				index = i;
				break;
			}
		}

		double[] values = new double[chardata.getDatas().length];
		for (int i = 0; i < chardata.getDatas().length; i++) {
			GradeData gradeData = new GradeData();
			String data = chardata.getDatas()[i][index];
			String code = chardata.getDatas()[i][0];
			gradeData.setCode(code);
			values[i] = Double.parseDouble(data);
			gradeData.setValue(Double.parseDouble(data));
			gradeData.setGeometry(getGeo(code, this.codeType));
			gradeDatas.add(gradeData);
		}
		Collections.sort(gradeDatas);
		//假如是精加工，则有颜色值
		
		if(!"".equals(gradeColors)){
			System.out.println(gradeColors);
			int breakNum=gradeColors.split(",").length;
			//分级
			double[] breaks = ModelPrim.modelD2(
					gradeDatas.get(gradeDatas.size() - 1).getValue(), gradeDatas
							.get(0).getValue(),breakNum);
			//对应的颜色
			int[][] colors=new int[breakNum][3];
			String[] gradeColor16=gradeColors.split(",");
			for(int i=0;i<breakNum;i++){
				String str=gradeColor16[i];
				 String str2 = str.substring(1,3);  
	             String str3 = str.substring(3,5);  
	             String str4 = str.substring(5,7);  
	             int red = Integer.parseInt(str2,16);  
	             int green = Integer.parseInt(str3,16);  
	             int blue = Integer.parseInt(str4,16);  
	             colors[i][0]=red;
	             colors[i][1]=green;
	             colors[i][2]=blue;
			}
			int gradeIndex = 0;// 计算数组的索引
			for (GradeData grade : gradeDatas) {
				for (int i = 0; i < breaks.length - 1; i++) {
					if (grade.getValue() > breaks[i]
							&& grade.getValue() == breaks[i+1]) {
						gradeIndex = i;
						break;
					}
					if (grade.getValue() >= breaks[i]
							&& grade.getValue() < breaks[i+1]) {
						gradeIndex = i;
						break;
					}
				}
				grade.setColor(colors[gradeIndex][0] + "," + colors[gradeIndex][1]
						+ "," + colors[gradeIndex][2]);
			}
			return gradeDatas;
		}
		
		// 获取颜色
		double[] breaks = ModelPrim.modelD2(
				gradeDatas.get(gradeDatas.size() - 1).getValue(), gradeDatas
						.get(0).getValue(), Integer.parseInt(this.classNum));
		int[][] colors = Color.generateColor(this.classNum, this.classColors,
				"0");
		int gradeIndex = 0;// 计算数组的索引
		for (GradeData grade : gradeDatas) {
			for (int i = 0; i < breaks.length - 1; i++) {
				if (grade.getValue() > breaks[i]
						&& grade.getValue() == breaks[i+1]) {
					gradeIndex = i;
					break;
				}
				if (grade.getValue() >= breaks[i]
						&& grade.getValue() < breaks[i+1]) {
					gradeIndex = i;
					break;
				}
			}
			grade.setColor(colors[gradeIndex][0] + "," + colors[gradeIndex][1]
					+ "," + colors[gradeIndex][2]);
		}
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

	
	// 获取分级数据
	private List<GradeData> getGradeDatas(List<GradeData> gradeDatas,String gradeColors) throws Exception {
			
			Collections.sort(gradeDatas);
			

			//假如是精加工，则有颜色值
			
			if(!"".equals(gradeColors)){
				int breakNum=gradeColors.split(",").length;
				//分级
				double[] breaks = ModelPrim.modelD2(
						gradeDatas.get(gradeDatas.size() - 1).getValue(), gradeDatas
								.get(0).getValue(),breakNum);
				//对应的颜色
				int[][] colors=new int[breakNum][3];
				String[] gradeColor16=gradeColors.split(",");
				for(int index=0;index<breakNum;index++){
					String str=gradeColor16[index];
					 String str2 = str.substring(1,3);  
		             String str3 = str.substring(3,5);  
		             String str4 = str.substring(5,7);  
		             int red = Integer.parseInt(str2,16);  
		             int green = Integer.parseInt(str3,16);  
		             int blue = Integer.parseInt(str4,16);  
		             colors[index][0]=red;
		             colors[index][1]=green;
		             colors[index][2]=blue;
				}
				int gradeIndex = 0;// 计算数组的索引
				for (GradeData grade : gradeDatas) {
					for (int i = 0; i < breaks.length - 1; i++) {
						if (grade.getValue() > breaks[i]
								&& grade.getValue() == breaks[i+1]) {
							gradeIndex = i;
							break;
						}
						if (grade.getValue() >= breaks[i]
								&& grade.getValue() < breaks[i+1]) {
							gradeIndex = i;
							break;
						}
					}
					grade.setColor(colors[gradeIndex][0] + "," + colors[gradeIndex][1]
							+ "," + colors[gradeIndex][2]);
				}
				return gradeDatas;
			}
			
			// 获取颜色
			double[] breaks = ModelPrim.modelD2(
					gradeDatas.get(gradeDatas.size() - 1).getValue(), gradeDatas
							.get(0).getValue(), Integer.parseInt(this.classNum));
			int[][] colors = Color.generateColor(this.classNum, this.classColors,
					"0");
			int gradeIndex = 0;// 计算数组的索引
			for (GradeData grade : gradeDatas) {
				for (int i = 0; i < breaks.length - 1; i++) {
					if (grade.getValue() >= breaks[i]
							&& grade.getValue() == breaks[i+1]) {
						gradeIndex = i;
						break;
					}
					if (grade.getValue() >= breaks[i]
							&& grade.getValue() < breaks[i+1]) {
						gradeIndex = i;
						break;
					}
				}
				grade.setColor(colors[gradeIndex][0] + "," + colors[gradeIndex][1]
						+ "," + colors[gradeIndex][2]);
			}
			return gradeDatas;
		}

	private String getGradeLegendString(List<GradeData> gradeDatas,String gradeColors){
		String resultString="";
		Collections.sort(gradeDatas);
		if(gradeDatas.size()<1){
			return resultString;
		}
		
		//假如是精加工，则有颜色值
		
		if(!"".equals(gradeColors)){
			int breakNum=gradeColors.split(",").length;
			//分级
			double[] breaks = ModelPrim.modelD2(
					gradeDatas.get(gradeDatas.size() - 1).getValue(), gradeDatas
							.get(0).getValue(),breakNum);
			//对应的颜色
			int[][] colors=new int[breakNum][3];
			String[] gradeColor16=gradeColors.split(",");
			for(int index=0;index<breakNum;index++){
				String str=gradeColor16[index];
				 String str2 = str.substring(1,3);  
	             String str3 = str.substring(3,5);  
	             String str4 = str.substring(5,7);  
	             int red = Integer.parseInt(str2,16);  
	             int green = Integer.parseInt(str3,16);  
	             int blue = Integer.parseInt(str4,16);  
	             colors[index][0]=red;
	             colors[index][1]=green;
	             colors[index][2]=blue;
			}
			for(int i=0;i<breaks.length-1;i++){
				if("".equals(resultString)){
					resultString=colors[i][0]+","+colors[i][1]+","+colors[i][2]+","+breaks[i]+"~"+breaks[i+1];
				}else {
					resultString=resultString+"@"+colors[i][0]+","+colors[i][1]+","+colors[i][2]+","+breaks[i]+"~"+breaks[i+1];
				}
			}
			return resultString;
		}
		
		//否则就按照之前的来
		// 获取颜色
		double[] breaks = ModelPrim.modelD2(
				gradeDatas.get(gradeDatas.size() - 1).getValue(), gradeDatas
						.get(0).getValue(), Integer.parseInt(this.classNum));
		
		int[][] colors = Color.generateColor(this.classNum, this.classColors,
				"0");
		for(int i=0;i<breaks.length-1;i++){
			if("".equals(resultString)){
				resultString=colors[i][0]+","+colors[i][1]+","+colors[i][2]+","+breaks[i]+"~"+breaks[i+1];
			}else {
				resultString=resultString+"@"+colors[i][0]+","+colors[i][1]+","+colors[i][2]+","+breaks[i]+"~"+breaks[i+1];
			}
		}
		return resultString;
	}
	public void init() throws ServletException {

	}
}
