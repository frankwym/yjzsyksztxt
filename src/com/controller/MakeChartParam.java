package com.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.sql.Blob;
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
import com.bean.regionBean;
import com.bean.statisticData;
import com.dbconn.JdbcUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zj.bglayer.ModelPrim;

import Decoder.BASE64Encoder;

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
	String classRegionGeo = "";
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

		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain;charset=" + "utf-8");  
		response.setCharacterEncoding("utf-8"); 
		response.setContentType("text/html");
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
		this.classRegionGeo = "";
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
			String resString=getCustomizeData(request);
			this.chartData = resString;
			getRegionInfo(request);
			getFormatParam(mapFormatContent);
			if(classificationIndex!=""){
				try {
					gradeDatas = getGradeDatas(request);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
		}
		this.widthString += "60";
		this.heightString += "60";
		this.isLabel += "false";
	
		String returnString = "0,0,60,60&0,0,60,60&";
		returnString = returnString + this.codeType + "&" + this.chartData
				+ "&" + this.chartId + "&" + this.statisticColors + "&"
				+ this.heightString + "&" + this.widthString + "&"
				+ this.isLabel + "&" + this.xyString + "&"
				+ JSON.toJSONString(gradeDatas)+"&"+getGradeLegendString(gradeDatas,gradeColors)+"&"+this.classColors.trim()+"&"+this.classRegionGeo;
		PrintWriter out = response.getWriter();
		out.println(returnString);
		System.out.println(returnString);
		out.flush();
		out.close();
	}
	private void getRegionInfo(HttpServletRequest request) throws IOException {
		String statisticalIndex=request.getParameter("statisticalIndex").toString();
		String classifictionIndex=request.getParameter("classificationIndex").toString();
		String[] regionNames=request.getParameter("regionNames").toString().split(",");
		
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		List<regionBean> classDatas = new ArrayList<>();
		String geoStr="";
		String xyStr="";
		String sql = "select * from  region_distric  where name=?";
		for (int j = 0; j < regionNames.length; j++) {
		try {
				classDatas = qr.query(sql, new BeanListHandler<regionBean>(
						regionBean.class),regionNames[j]);
			
				if (classDatas.size() != 0) { 
					if(j==0){
						if(statisticalIndex!="")
						xyStr="{\"x\":"+classDatas.get(0).getX() +",\"y\":"+classDatas.get(0).getY()+",\"name\":\""+regionNames[j]+"\",\"code\":\""+regionNames[j]+"\"}";
						if(classifictionIndex!="")
						geoStr=blobToString(classDatas.get(0).getExtent());
					}

					else {
						if(statisticalIndex!="")
						xyStr=xyStr+","+"{\"x\":"+classDatas.get(0).getX() +",\"y\":"+classDatas.get(0).getY()+",\"name\":\""+regionNames[j]+"\",\"code\":\""+regionNames[j]+"\"}";
						if(classifictionIndex!="")
						geoStr=geoStr+"s-p-l"+blobToString(classDatas.get(0).getExtent());
					}
				}
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		this.xyString="["+xyStr+"]";
		this.classRegionGeo=geoStr;
	}
	// 根据所选的colIndex 构造chartData 用户上传数据
	private String getCustomizeData(HttpServletRequest request) throws IOException {
		String[] statisticalIndex=request.getParameter("statisticalIndex").toString().split(",");
		String[] regionNames=request.getParameter("regionNames").toString().split(",");
		String disastertype=request.getParameter("disastertype");
		String maptype=request.getParameter("maptype");
		/*String[] statisticalIndex=new String[] {"2014年崩塌次数","2014年滑坡次数"};
		String[] regionNames=new String[] {"山西省","四川省","浙江省"};
		String disastertype="地质灾害";
		String maptype="地质灾害范围图";*/
		String dataString = "";
		//String xyString="";
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		List<statisticData> statisticDatas = new ArrayList<>();
		String sql = "select t.name,t.value,t.regionname,c.extent,c.x,c.y from whu_statisticdata t, region_distric c where t.name=? and t.disastertype=? and t.maptype=? and t.regionname=? and t.regionname=c.name";
		try {
			
				for (int j = 0; j < regionNames.length; j++) {
					for(int i=0;i<statisticalIndex.length;i++){
					statisticDatas = qr.query(sql, new BeanListHandler<statisticData>(
							statisticData.class), statisticalIndex[i], disastertype,maptype,regionNames[j]);
					if (statisticDatas.size() != 0) {
						if(i==0 &&j==0){
							dataString=statisticDatas.get(0).getValue();
						
						}
						else if (i==statisticalIndex.length-1) {
							dataString=dataString+","+statisticDatas.get(0).getValue()+";";
							//xyString=xyString+","+"{\"x\":"+statisticDatas.get(0).getX() +",\"y\":"+statisticDatas.get(0).getY()+",\"name\":\""+regionNames[j]+"\",\"code\":\""+regionNames[j]+"\"}";
							//regionString=regionString+blobToString(statisticDatas.get(0).getExtent())+"s-p-l";
						}
						else if (i==0 &&j!=0){
							dataString=dataString+statisticDatas.get(0).getValue();
							
						}
						
						else {
							dataString=dataString+","+statisticDatas.get(0).getValue();
							
						}
					}
					else{
						if(i==0 &&j==0){
							dataString="null";
						
						}
						else if (i==statisticalIndex.length-1) {
							dataString=dataString+","+"null"+";";
							//xyString=xyString+","+"{\"x\":"+statisticDatas.get(0).getX() +",\"y\":"+statisticDatas.get(0).getY()+",\"name\":\""+regionNames[j]+"\",\"code\":\""+regionNames[j]+"\"}";
							//regionString=regionString+blobToString(statisticDatas.get(0).getExtent())+"s-p-l";
						}
						else if (i==0 &&j!=0){
							dataString=dataString+"null";
							
						}
						
						else {
							dataString=dataString+","+"null";;
							
						}
					}
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		}
		//xyString="["+xyString.substring(1)+"]";
		
		String data1 = "", data2 = "", data3 = "", colors = "";
		String[] colorArr = { "#ff0000", "#00ff00", "#8080ff", "#ff8000",
				"#008080", "#804040", "#ffff00", "#ff00ff", "#00ffff",
				"#0088ff" };
		for (int i = 0; i < statisticalIndex.length; i++) {
			//String color = colorArr[(int) Math.round(Math.random() * 9)];
			String color = colorArr[i];
			String subColor = color.substring(1);
			int colorInt = Integer.parseInt(subColor, 16);
			if (i == 0) {
				colors = String.valueOf(colorInt);
				data1 = statisticalIndex[i];
				;
				data2 = "个";
			} else {
				colors = colors + ";" + String.valueOf(colorInt);
				data1 = data1 + "," + statisticalIndex[i];
				data2 = data2 + "," + "个";
			}
		}
		this.statisticColors = colors;
		String[] dataStringArr=dataString.split(";");
		for (int i = 0; i < dataStringArr.length; i++) {
			String data = "";
			

			data = dataStringArr[i] + "," + (i + 1) + "," + 0 + "," + 0 + "," + (i + 1);
			if (i == 0) {
				data3 = data;
			} else {
				data3 = data3 + ";" + data;
			}
		}

		// return JSON.toJSONString(newChartData);
		//return data1 + ";" + data2 + ";" + data3+"s-p-l"+xyString;
		return data1 + ";" + data2 + ";" + data3;
	}
	 public  String blobToString(Blob blob) throws SQLException, IOException
	    {
		  String result = "";  
	      try {  
	    	  result=  new String(blob.getBytes((long)1, (int)blob.length()));  
	      } catch (SQLException e) {  	          
	    	  e.printStackTrace();  
         }
	      return result;
	    }
	
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
	private List<GradeData> getGradeDatas(HttpServletRequest  request) throws Exception {
		String classIndex=request.getParameter("classificationIndex").toString();
		String[] regionNames=request.getParameter("regionNames").toString().split(",");
		String disastertype=request.getParameter("disastertype");
		String maptype=request.getParameter("maptype");
		String gradeColors=request.getParameter("gradeColors");
		List<GradeData> gradeDatas = new ArrayList<>();
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		List<statisticData> classDatas = new ArrayList<>();
		String dataString="";
		//String xyStr="";
		String sql = "select t.name,t.value,t.regionname,c.extent,c.x,c.y from whu_statisticdata t, region_distric c where t.name=? and t.disastertype=? and t.maptype=? and t.regionname=? and t.regionname=c.name";
		for (int j = 0; j < regionNames.length; j++) {
		try {
				classDatas = qr.query(sql, new BeanListHandler<statisticData>(
						statisticData.class),classIndex, disastertype,maptype,regionNames[j]);
			
				if (classDatas.size() != 0) {
					if(j==0){
						dataString=classDatas.get(0).getValue();
						//xyStr=blobToString(classDatas.get(0).getExtent());
					}

					else {
						dataString=dataString+","+classDatas.get(0).getValue();
						//xyStr=xyStr+","+blobToString(classDatas.get(0).getExtent());
					}
				}
				else{
					if(j==0){
						dataString="null";
						//xyStr="";
					}
					else {
						dataString=dataString+","+"null";
						//xyStr=""+","+"";
					}
				}
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}

		
		
		//this.classRegionGeo="["+xyStr+"]";

		double[] values = new double[regionNames.length];
		for (int i = 0; i < regionNames.length; i++) {
			GradeData gradeData = new GradeData();
			String data = dataString.split(",")[i];
			String code =regionNames[i];
			gradeData.setCode(code);
			if (data.equals("null")) {
				data="0";
			}
			values[i] = Double.parseDouble(data);
			
			gradeData.setValue(Double.parseDouble(data));
			gradeData.setGeometry(this.classRegionGeo.split("s-p-l")[i]);
			gradeDatas.add(gradeData);
		}
		Collections.sort(gradeDatas);
		String newGeoStr="";
		for (int i = 0; i < gradeDatas.size(); i++) {
			newGeoStr=newGeoStr+","+gradeDatas.get(i).getGeometry();
		}
		this.classRegionGeo="["+newGeoStr.substring(1)+"]";
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
