package com.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.bean.UserMap;
import com.dbconn.JdbcUtils;

public class SaveUserMap extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter printWriter = response.getWriter();
		String mapName = request.getParameter("mapName");
		String userId = request.getParameter("userId");
		String cutomizemaprecords = request.getParameter("cutomizemaprecords");
		String isSave = request.getParameter("isSave");// 0与1 0代表判断是否为新建 ，1代表保存
		if (mapName == null || userId == null) {
			printWriter.println("请输入地图名");
			return;
		}
		if ("0".equals(isSave) && isSave != null) {
			printWriter.write(checkMapExit(mapName, userId));
		} else if("1".equals(isSave) && isSave != null) {
			int code = saveUserMap(mapName, userId, cutomizemaprecords);
			if (code == -1) {
				printWriter.println("上传数据失败");
			} else {
				printWriter.println("上传数据成功");
			}
		}else  {
			int code = reSaveUserMAP(mapName, userId, cutomizemaprecords);
			if (code == -1) {
				printWriter.println("上传数据失败");
			} else {
				printWriter.println("上传数据成功");
			}
		}
		printWriter.flush();
		printWriter.close();
	}

	public String checkMapExit(String mapName, String userId) {
		List<UserMap> userMaps = new ArrayList<>();
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from WHU_CUSTOMMAP  t where t.mapname=? and t.userid=?";
		try {
			userMaps = qr.query(sql,
					new BeanListHandler<UserMap>(UserMap.class), mapName,
					userId);
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (userMaps.size() > 0) {
			return "地图名字已经存在，请重新输入";
		} else {
			return "";
		}
	}

	public int saveUserMap(String mapName, String userId,
			String cutomizemaprecords) {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		
		String sql = "insert into WHU_CUSTOMMAP (mapname, userid, cutomizemaprecords, createtime) values (  ?, ?,?, ? ) ";
		int code = -1;
		try {
			Date time= new java.sql.Date(new java.util.Date().getTime());
			code = qr.update(sql, mapName, userId, cutomizemaprecords,time);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return code;
	}
	
	public int reSaveUserMAP(String mapName, String userId,
			String cutomizemaprecords) {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "update WHU_CUSTOMMAP set cutomizemaprecords=?, time=?  where mapname=? and userid=? ";
		int code = -1;
		try {
			Date time= new java.sql.Date(new java.util.Date().getTime());
			code = qr.update(sql,cutomizemaprecords,time,mapName,userId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return code;
	}
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}
}

