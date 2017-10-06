package com.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.alibaba.fastjson.JSON;
import com.bean.MapFormat;
import com.bean.nodes;
import com.dbconn.JdbcUtils;

/**
 * Servlet implementation class getStatisticDataList
 */
@WebServlet("/getStatisticDataList")
public class getStatisticDataList extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public getStatisticDataList() {
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
		//获取灾害种类和制图种类
		String disasterType=request.getParameter("disasterType");
		String mapType=request.getParameter("mapType");
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		 List<Object[]> dataList=  new ArrayList<>();
		 String returnString="";
		String sql = "select name from WHU_STATISTICDATA   where disastertype=? and maptype=?  group by name";					
			try {
				
				dataList= qr.query(sql, new ArrayListHandler(),disasterType,mapType);
			
				for (int i = 0; i < dataList.size(); i++) {
					if(i==0){
						returnString="{'text':'"+dataList.get(i)[0].toString()+"','value':'"+dataList.get(i)[0].toString()+"'}";
					}
					else {
						returnString=returnString+","+"{'text':'"+dataList.get(i)[0].toString()+"','value':'"+dataList.get(i)[0].toString()+"'}";
					}
					
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			returnString="["+returnString+"]";
			PrintWriter out = response.getWriter();
			out.println(returnString);
			out.flush();
			out.close();
	}

}
