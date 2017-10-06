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
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.dbconn.JdbcUtils;

/**
 * Servlet implementation class DeleteCustomizeInfo
 */
@WebServlet("/DeleteCustomizeInfo")
public class DeleteCustomizeInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteCustomizeInfo() {
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
		
		String type=request.getParameter("type").toString();
		String userName=request.getParameter("userName").toString();
		String resultString="删除成功";
		
		if(type.equals("UpdataDatatree")){
			String dataName=request.getParameter("dataName").toString();
			QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
			String sql = "delete from WHU_UPLOAD_DATA t where t.filename=? and t.username=?";
			
			try {	
				int i=qr.update(sql,dataName,userName);
				if(i==0){
					resultString="删除失败";
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				resultString="删除失败："+e.getMessage();
			}
		}else if(type.equals("updataFormatTree")){
			String formatName=request.getParameter("formatName").toString();
			QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
			String sql = "delete from WHU_MAP_FORMAT f where f.formatname='"+formatName+"' and f.username='"+userName+"'";
			
			try {	
				int i=qr.update(sql);
				if(i==0){
					resultString="删除失败";
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				resultString="删除失败："+e.getMessage();
			}
		}else if(type.equals("myMaps")){
			
		}
		
		PrintWriter out = response.getWriter();
		out.println(resultString);
		out.flush();
		out.close();
	}

}
