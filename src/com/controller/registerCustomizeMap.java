package com.controller;

import java.io.IOException;
import java.io.PrintWriter;

import com.dbconn.JdbcUtils;
import com.manager.model.MapResource;
import com.manager.model.Workspace;
import kjoms.udcs.service.OpenService;
import kjoms.udcs.service.OpenService2;
import kjoms.udcs.util.GetOpenService;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class registerCustomizeMap extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public registerCustomizeMap() {
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
		String loginId=request.getParameter("loginId");
		String mapNameString=request.getParameter("mapName");
		String mapKeyword=request.getParameter("mapKeyword");
		String resName=request.getParameter("resName");
		String userNameString=request.getParameter("userId");
		String mapIdString="";
		String registerRes="";
		try {
			mapIdString = getMapId(mapNameString,userNameString);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			registerRes="注册失败";
		}
		if(registerRes!="注册失败")
		try {
			 registerRes=saveWorkSpace(loginId,resName,mapKeyword, mapIdString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			registerRes=e.getMessage();
		}
		PrintWriter out = response.getWriter();
		out.println(registerRes);
		out.flush();
		out.close();
	}

	 //保存工作集
    public String saveWorkSpace(String loginId,String resName,String mapKeyword,String mapid) throws Exception {
        String error = "";
        if (loginId.equals("") || loginId == null) {
            return error = "用户失效！";
        }
        //将资源注册到南京平台中
		OpenService2 openService2 = GetOpenService.getOpenService();
		List<String> keyWords = new ArrayList<>();
		keyWords.add(mapKeyword);
		int code = openService2.addResource(loginId, resName, resName, keyWords, null, null, null, null, null, null, "0",  GetOpenService.GetaddResourceUrl() + mapid);
		switch (code) {
		    case -1:
		        error = "注册失败";
		        break;
		    case 0:
		        //事务开始
		    	error = "注册成功";
		        break;
		    case 1:
		        error = "资源名称已存在";
		        break;
		    case 2:
		        error = "用户id为空";
		        break;
		    case 3:
		        error = "资源名称为空";
		        break;
		    case 4:
		        error = "摘要为空";
		        break;
		    case 5:
		        error = "关键字列表为空";
		        break;
		    case 6:
		        error = "url为空";
		        break;
		}
		//事务提交
        return error;
    }


    //根据地图资源ID
    public String  getMapId(String mapname,String userNameString) throws Exception {
        String  MapId = null;
        QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
        String sql = "select t.rowid from WHU_CUSTOMMAP t where mapname=? and userid=?";
       Object[] params={mapname,userNameString};
        MapId =""+ qr.query(sql,new ScalarHandler(), params);
        return MapId;
    }

    //分页查询用户制作的图
    public List<Workspace> getWorkSpaceByUserName(String userName, int rows, int page) throws Exception {
        List<Workspace> workspaces = null;
        QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());

        String sql = "SELECT * FROM \n" +
                "(\n" +
                "SELECT A.*, ROWNUM RN \n" +
                "FROM (SELECT * FROM workspaces  where userId=?) A \n" +
                ")\n" +
                "WHERE RN BETWEEN ? AND ?";
        if (page > 0) {
            workspaces = qr.query(sql, new BeanListHandler<Workspace>(Workspace.class), userName, (page - 1) * rows, page * rows);

        } else {
            workspaces = new ArrayList<>();
        }
        return workspaces;
    }

    //获取总的用户制作的图的数据量
    public Long getCount(String userName) throws Exception {
        QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
        String sql = "select count(*) from workspaces where userId=? ";
        Object total = qr.query(sql, new ScalarHandler<Object>(), userName);
        return Long.parseLong("" + total);
    }


	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
