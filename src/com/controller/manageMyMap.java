package com.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kjoms.udcs.util.GetOpenService;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.bean.UserMap;
import com.dbconn.JdbcUtils;

public class manageMyMap extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public manageMyMap() {
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

		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter printWriter = response.getWriter();
		String userId = request.getParameter("userId");
		String type = request.getParameter("type");
		String returnString = "";
		if (type.equals("getMyMapByName")) {
			List<UserMap> userMaps = new ArrayList<>();
			QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
			String sql = "select * from WHU_CUSTOMMAP  t where t.userid=?";
			try {
				userMaps = qr.query(sql, new BeanListHandler<UserMap>(
						UserMap.class), userId);
				for (int i = 0; i < userMaps.size(); i++) {
					returnString = returnString
							+ userMaps.get(i).getCutomizeMapRecords()
							+ "mymaps-p-l";
				}

			} catch (Exception e) {
				// TODO: handle exception
				returnString = "错误:" + e.getMessage();
			}
		} else if (type.equals("deleteMyMapByName")) {
			String mapName = request.getParameter("mapName");
			QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
			String sql = "delete from WHU_CUSTOMMAP  where userid=? and mapname=?";
			int code = -1;
			try {
				code = qr.update(sql, userId, mapName);
				if (code == -1) {
					returnString = "删除失败";
				} else {
					returnString = "删除成功";
				}

			} catch (Exception e) {
				// TODO: handle exception
				returnString = "错误:" + e.getMessage();
			}
		} else if (type.equals("editMyMapByName")) {
			String oldMapName = request.getParameter("oldMapName");
			String newMapName = request.getParameter("newMapName");
			String editingMapInfor = request.getParameter("editingMapInfor");
			QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
			String sql = "update WHU_CUSTOMMAP set mapname=?,cutomizemaprecords=?, time=sysdate  where mapname=? and userid=?";
			int code = -1;
			try {
				code = qr.update(sql, newMapName, editingMapInfor, oldMapName,
						userId);
				if (code == -1) {
					returnString = "修改失败";
				} else {
					returnString = "修改成功";
				}

			} catch (Exception e) {
				// TODO: handle exception
				returnString = "错误:" + e.getMessage();
			}
		} else if (type.equals("getViewMapByName")) {
			String mapName = request.getParameter("mapName");
			List<UserMap> userMaps = new ArrayList<>();
			QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
			String sql = "select * from WHU_CUSTOMMAP  t where t.userid=? and mapname=?";
			try {
				userMaps = qr.query(sql, new BeanListHandler<UserMap>(
						UserMap.class), userId, mapName);
				for (int i = 0; i < userMaps.size(); i++) {
					returnString = userMaps.get(i).getCutomizeMapRecords();
				}

			} catch (Exception e) {
				// TODO: handle exception
				returnString = "错误:" + e.getMessage();
			}
			
			//在查看地图的时候就创建文件夹 为打印做准备
			String MapId = null;
			sql = "select t.rowid from WHU_CUSTOMMAP t where mapname=? and userid=?";
			Object[] params = { mapName, userId };
			try {
				MapId = "" + qr.query(sql, new ScalarHandler(), params);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String path=request.getServletContext().getRealPath("/")+"//resource//printMap//"+MapId;
			File file=new File(path);
			if(!file.exists()){
				file.mkdirs();
			}else{
				//删除上传的图片
				File file2=new File(path+"//pic.png");
				if(file2.exists()){
					file2.delete();
				}
			}
		} else if (type.equals("getViewMapById")) {
			String mapId = request.getParameter("mapId");
			//建立文件夹
			String path=GetOpenService.GetImageSave()+mapId;
			File file=new File(path);
			if(!file.exists()){
				file.mkdirs();
			}else{
				//清空后再建立
				file.delete();
				file.mkdirs();
			}
			List<UserMap> userMaps = new ArrayList<>();
			QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
			String sql = "select * from WHU_CUSTOMMAP  t where t.rowid=? ";
			try {
				userMaps = qr.query(sql, new BeanListHandler<UserMap>(
						UserMap.class), mapId);
				for (int i = 0; i < userMaps.size(); i++) {
					returnString = userMaps.get(i).getCutomizeMapRecords();
				}

			} catch (Exception e) {
				// TODO: handle exception
				returnString = "错误:" + e.getMessage();
			}
		}
		printWriter.println(returnString);
		printWriter.flush();
		printWriter.close();
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

		this.doGet(request, response);
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
