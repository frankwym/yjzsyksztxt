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
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.alibaba.fastjson.JSON;
import com.bean.ExcelFile;
import com.bean.MapFormat;
import com.bean.nodes;
import com.dbconn.JdbcUtils;

public class getCustomizeFormatDataList extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public getCustomizeFormatDataList() {
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
		
		String username=request.getParameter("userName");
		String returnString = getFormats(username);

		PrintWriter out = response.getWriter();
		out.println(returnString);
		out.flush();
		out.close();
	}

	// get the all formats
	public String getFormats(String username) {
		//get default formats
		String defaultFormat = "[{  text: \"默认制图模板\",  backColor: \"#2d5990\",  color: \"white\",  selectable: false,nodes:";
		String customizeFormat = "[{  text: \"自定义模板\",  backColor: \"#2d5990\",  color: \"white\",  selectable: false,tags: ['上传模板'],nodes:";
		
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		List<MapFormat> mapFormats = new ArrayList<>();
		String sql = "select * from WHU_MAP_FORMAT t where t.username=?";					
			try {
				mapFormats= qr.query(sql, new BeanListHandler<MapFormat>(
						MapFormat.class),"allusers");
				nodes[] nodes = new nodes[mapFormats.size()];
				for (int i = 0; i < nodes.length; i++) {
					nodes nodes3 = new nodes();
					nodes3.setText(mapFormats.get(i).getFORMATNAME());
					nodes3.setValue(mapFormats.get(i).getContent());
					nodes[i] = nodes3;
				}
				String end = "}]";
				defaultFormat=defaultFormat + JSON.toJSON(nodes) + end;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mapFormats = new ArrayList<>();
			sql = "select * from WHU_MAP_FORMAT t where t.username=?";					
				try {
					mapFormats= qr.query(sql, new BeanListHandler<MapFormat>(
							MapFormat.class),username);
					nodes[] nodes = new nodes[mapFormats.size()];
					for (int i = 0; i < nodes.length; i++) {
						nodes nodes3 = new nodes();
						nodes3.setText(mapFormats.get(i).getFORMATNAME());
						nodes3.setValue(mapFormats.get(i).getContent());
						String [] tag={"修改","删除"};
						nodes3.setTags(tag);
						nodes[i] = nodes3;
					}
					String end = "}]";
					customizeFormat=customizeFormat + JSON.toJSON(nodes) + end;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			return defaultFormat+"s-p-l-i-t"+customizeFormat;
		
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
