package com.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbutils.QueryRunner;

import com.dbconn.JdbcUtils;

/**
 * Servlet implementation class UpLoadFormat
 */
@WebServlet("/UpLoadFormat")
public class UpLoadFormat extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpLoadFormat() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		
		String userName=request.getParameter("userName").toString();
		String formatName=request.getParameter("formatName").toString();
		String districtColor=request.getParameter("districtColor").toString();
		String districtChart=request.getParameter("districtChart").toString();
		String streetColor=request.getParameter("streetColor").toString();
		String streetChart=request.getParameter("streetChart").toString();
		String districtClassNum=request.getParameter("districtClassNum").toString();
		String streetClassNum=request.getParameter("streetClassNum").toString();
		String isEditForm=request.getParameter("isEditForm").toString();
		
		String contentString=districtClassNum+","+districtColor+","+districtChart+","+streetClassNum+","+streetColor+","+streetChart;
		String result="";
		
		if("true".equals(isEditForm)){
			QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
			
			//占位符用着有问题 所以先改一下
			String sql = "update WHU_MAP_FORMAT t set t.content='"+contentString+"' ,t.time=sysdate where t.FORMATNAME='"+formatName+"' and t.username='"+userName+"' ";
			try {
				int isSuccess = qr.update(sql);
				if(isSuccess==1){
					result="修改成功！";
				}else {
					result="修改失败！";
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				result="修改失败："+e.getMessage();
			}
		}else{
			QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
			String sql = "insert into WHU_MAP_FORMAT (username, FORMATNAME, content,time) values ( ?, ?, ?, sysdate) ";
			
			try {
				int isSuccess = qr.update(sql, userName,  formatName,contentString);
				if(isSuccess==1){
					result="上传成功！";
				}else {
					result="上传失败！";
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				result="上传失败："+e.getMessage();
			}
		}
		
		PrintWriter out = response.getWriter();
		out.println(result);
		out.flush();
		out.close();
	}
	
	

}
