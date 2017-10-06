package com.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bean.ExcelFile;
import com.bean.GeoInfo;
import com.bean.MapFormat;
import com.bean.StatisticYearBook;
import com.bean.nodes;
import com.dbconn.JdbcUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class DBUtilsTest {
	// 测试DBUtils的测试编辑
	@Test
	public void testDBUtils() throws SQLException {
		 String fullFileName = "D:/district.json";
         
	        File file = new File(fullFileName);
	        Scanner scanner = null;
	        StringBuilder buffer = new StringBuilder();
	        try {
	            scanner = new Scanner(file, "utf-8");
	            while (scanner.hasNextLine()) {
	                buffer.append(scanner.nextLine());
	            }
	 
	        } catch (FileNotFoundException e) {
	            // TODO Auto-generated catch block  
	 
	        } finally {
	            if (scanner != null) {
	                scanner.close();
	            }
	        }

	        try {
	            JSONObject object = JSON.parseObject(buffer.toString());
	            JSONObject data = (JSONObject) object.get("features");
	            JSONArray jsonArray = data.getJSONArray("ThemeList");
	           
	        } catch (Exception e) {
	           
	        }
	       
	        /*for (int i = 0; i < result.get; i++) {  
	        	   
	        }  */
	       // List<Link> links= JSON.parseArray(result.toJSONString(),Link.class);
	        System.out.println(buffer.toString());
	}
	public String[] GeoCoordinate(String[] GeoCode,String GeoType){
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		List<GeoInfo> mapFormats = new ArrayList<>();
		String [] Coordinate = new String[GeoCode.length];
		String tableString="";
		String codeString="";
		if(GeoType=="district"){
			tableString="WHU_DISTRICT_DIC";
			codeString="districtcode";
		}else {
			tableString="WHU_STREET_DIC";
			codeString="streetcode";
		}
		for(int i=0;i<GeoCode.length;i++){
			String sql = "select * from "+tableString+" t where t."+codeString+"=?";					
			try {
				mapFormats= qr.query(sql, new BeanListHandler<GeoInfo>(
						GeoInfo.class),GeoCode[i]);
				String x=mapFormats.get(0).getX();
				String y=mapFormats.get(0).getY();
				Coordinate[i]=x+","+y+";";
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return Coordinate;
		
	}
	@Test
	public void testDBUtils1() throws SQLException {
	
		String str="{\"NsewLen\": [{\"sn_len\":\"36213.18087563\",\"ew_len\": \"65558.35980913\", \"fid\": \"1\"}], \"SphArea\": 943567604.8846052}";
		  JsonParser parse =new JsonParser();  //创建json解析器
		  JsonObject json=(JsonObject) parse.parse(str);  //创建jsonObject对象
          System.out.println("resultcode:"+json.get("NsewLen").getAsJsonArray());  //将json数据转为为int型的数据
		
	}
	
	@Test
	public void ss(){
		 String  MapId = null;
	        QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
	        String sql = "select t.rowid from WHU_CUSTOMMAP t where mapname=? and userid=?";
	       Object[] params={"333","default"};
	        try {
				MapId = ""+qr.query(sql,new ScalarHandler(), params);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        System.out.print(MapId);
	}
	
}
