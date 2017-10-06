package com.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.bean.StatisticYearBook;
import com.dbconn.JdbcUtils;
import com.google.gson.Gson;

public class GetStatisticYearBookData extends HttpServlet {

	public GetStatisticYearBookData() {
		super();
	}

	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
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
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select *  from WUH_STATISTICYEARBOOK ";
		List<StatisticYearBook> mapFormats = new ArrayList<>();
		try {
			mapFormats = qr.query(sql, new BeanListHandler<StatisticYearBook>(
					StatisticYearBook.class));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PrintWriter out = response.getWriter();
		out.println(new Gson().toJson(mapFormats));
		out.flush();
		out.close();
	}
}
