package com.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.dbconn.JdbcUtils;

public class SaveExcel2Clob extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public SaveExcel2Clob() {
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
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String username = request.getParameter("username");
		String filename = request.getParameter("filename");
		String data = request.getParameter("data");
		int code = 0;
		try {
			code = save(username, filename, data);
		} catch (SQLException e) {
			code = -1;
			// TODO Auto-generated catch block
			System.out.println();
			e.printStackTrace();
			
			if(e.getMessage().indexOf("ORA-00001")>-1){//文件名重复
				code = -2;
			}
		}
		if (code == -1) {
			out.println("{\"msg\":\"上传数据失败\"}");
		} else if(code==-2){
			out.println("{\"msg\":\"文件名重复，请重新命名\"}");
		}else{
			out.println("{\"msg\":\"上传数据成功\"}");
		}

		out.flush();
		out.close();
	}

	// get the StatisticData for CustomizeMap
	public String getStatisticData() {
		return "";
	}

	// get the NationalData for CustomizeMap
	public String getNationalData() {
		return "";
	}

	// get the UploadData for CustomizeMap
	public int save(String username, String filename, String data)
			throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "insert into WHU_UPLOAD_DATA (username, filename, data, datetime) values ( ?, ?, ?, sysdate) ";
		int code = qr.update(sql, username, filename, data);
		return code;
	}

}
