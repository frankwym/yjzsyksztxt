package com.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javassist.compiler.ast.Variable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.bean.ChartData;
import com.bean.CityData;
import com.bean.ExcelFile;
import com.bean.nodes;
import com.dbconn.JdbcUtils;
import com.google.gson.Gson;

public class getCustomizeMapDataIndex extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public getCustomizeMapDataIndex() {
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
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
this.doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		String dataName=request.getParameter("dataName").toString();
		String dataType=request.getParameter("dataType").toString();
		String username=request.getParameter("username").toString();
		String returnString="[]";
		if(dataType.equals("UpdataDatatree")){
			returnString=getFileIndex(dataName,username);
		}
		if(dataType.equals("CityDatatree")){
			returnString=getCityDataIndex(dataName,username);
		}
		PrintWriter out = response.getWriter();
		out.println(returnString);
		out.flush();
		out.close();
	/*	String namesString=request.getParameter("tt");
		String geometriesString=request.getParameter("tt");
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		
			String sql = "UPDATE  WHU_DISTRICT_DIC set geometry='"+geometriesString+"' where districtname='"+namesString+"' ";
			String result="";
			try {
				int isSuccess = qr.update(sql);
				if(isSuccess==1){
					result="上传成功！";
				}else {
					result="上传失败！";
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				result="上传失败："+e.getMessage();
			}*/

	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}
	public String getCityDataIndex(String taskname,String username){
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		List<CityData> CityDatas = new ArrayList<>();
		String sql = "select * from OPERATIONRECORD where taskname=? and username=? and (state='计算成功' or state='计算完成')";
		try {
			CityDatas = qr.query(sql, new BeanListHandler<CityData>(
					CityData.class),taskname,username);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String indexString="";
		String areaString="";
		//获取统计指标
		for(int i=0;i<CityDatas.size();i++){
			//获取市情数据的指标
			 indexString=CityDatas.get(i).getINDICATORS();
			 int iw=indexString.lastIndexOf("#");
			 int ie=indexString.length();
			//如果指标最后有#,则删除
			if(indexString.lastIndexOf("#")==(indexString.length()-1)){
				indexString=indexString.substring(0, indexString.length()-1);
						
			}
			indexString.replaceAll("#", ",");
		}
		//获取统计区域
		for(int i=0;i<CityDatas.size();i++){
			//获取市情数据的指标
			areaString+=CityDatas.get(i).getSTATISTICALAREA()+"#";
			
		}
		return indexString+"s-p-l"+areaString;
	}
	public String getFileIndex(String filename,String username){
		String resultString="";
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		List<ExcelFile> excelFiles = new ArrayList<>();
		String sql = "select * from WHU_upload_data t where t.filename=? and username=?";
		try {
			excelFiles = qr.query(sql, new BeanListHandler<ExcelFile>(
					ExcelFile.class),filename,username);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String data="";
		if(excelFiles.size()>0){
			data=excelFiles.get(0).getData();
		}
		Gson gson = new Gson();
		ChartData chardata = gson.fromJson(data, ChartData.class);
		String areaType="";
		for(int i=0;i<chardata.getColumns().length;i++){
			String columnName=chardata.getColumns()[i];
			if("quCode".equals(columnName)){
				areaType="district";
				continue;
			}
			if("jiedaoCode".equals(columnName)){
				areaType="street";
				continue;
			}
			if("".equals(resultString)){
				resultString=columnName;
			}else{
				resultString+=","+columnName;
			}
			
		}
		resultString=resultString+"s-p-l"+areaType;
		return resultString;
	}
}
