package com.controller;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kjoms.udcs.bean.ResourceBean;
import kjoms.udcs.bean2.DataResource;
import kjoms.udcs.service.OpenService2;
import kjoms.udcs.util.GetOpenService;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bean.CityData;
import com.bean.ExcelFile;
import com.bean.nodes;
import com.dbconn.JdbcUtils;
import com.google.gson.Gson;

public class getCustomizeMapDataList extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public getCustomizeMapDataList() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);

	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain;charset=" + "utf-8");  
		response.setCharacterEncoding("utf-8"); 
		response.setContentType("text/html");
		
		String returnString = getTrafficData() + "s-p-l-i-t"
				+ getFireData() + "s-p-l-i-t" + getEarthquakeData()+ "s-p-l-i-t" + getUploadData(request)+ "s-p-l-i-t" + getFloodData();
		PrintWriter out = response.getWriter();
		out.println(returnString);
		out.flush();
		out.close();
	}

	// 交通事故数据
	public String getTrafficData() {
		String path=this.getServletContext().getRealPath("/")+"resource/StatisticYearbook.json";
		 File file = new File(path);  
	        BufferedReader reader = null;  
	        String laststr = "";  
	        try {  
	            reader = new BufferedReader(new InputStreamReader(new FileInputStream(path),"UTF-8"));   
	            String tempString = null;  
	            while ((tempString = reader.readLine()) != null) {  
	                laststr = laststr + tempString;  
	            }  
	            reader.close();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        } finally {  
	            if (reader != null) {  
	                try {  
	                    reader.close();  
	                } catch (IOException e1) {  
	                }  
	            }  
	        }  
	        return laststr; 
	}

	// 水灾数据
		public String getFloodData() {
			
			// 获取自定义数据列表
			String format = "[{  text: \"水灾数据\",  backColor: \"#2d5990\",  color: \"white\", selectable: false, nodes:[]";
			String end = "}]";
			return format +  end;
		}
		
	// 火灾数据
	public String getFireData() {
		
		// 获取自定义数据列表
		String format = "[{  text: \"火灾数据\",  backColor: \"#2d5990\",  color: \"white\", selectable: false, nodes:[]";
		String end = "}]";
		return format +  end;
	}
	//地震数据
	public String getEarthquakeData(){
		
		String format = "[ {text : \"地震数据\",backColor : \"#2d5990\",color : \"white\",selectable : false,	tags: ['<li id=\"cityData_plus\" class=\"fa fa-plus\"></li>','<li id=\"cityData_refresh\" class=\"fa fa-refresh\"></li>'],nodes:[]";
		
		String end = "}]";
		return format + end;
		
	}
	// get the UploadData for CustomizeMap
	public String getUploadData(HttpServletRequest request) {
		// 获取自定义数据列表
		String usernameString=request.getParameter("username").toString();
		String format = "[ {text : \"自定义上传数据\",backColor : \"#2d5990\",color : \"white\",selectable : false,	tags: ['上传数据'],nodes :";

		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		List<ExcelFile> excelFiles = new ArrayList<>();
		String sql = "select filename,username,datetime from whu_upload_data where username=?";
		try {
			excelFiles = qr.query(sql, new BeanListHandler<ExcelFile>(
					ExcelFile.class),usernameString);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		nodes[] nodes = new nodes[excelFiles.size()];
		for (int i = 0; i < excelFiles.size(); i++) {
			nodes nodes3 = new nodes();
			nodes3.setText(excelFiles.get(i).getFilename());
			String[] tag = { "删除" };
			nodes3.setTags(tag);
			nodes[i] = nodes3;
		}
		String end = "}]";
		return format + JSON.toJSON(nodes) + end;
	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
