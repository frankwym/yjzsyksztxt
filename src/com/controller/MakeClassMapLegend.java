package com.controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kjoms.udcs.util.GetOpenService;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.dbconn.JdbcUtils;

import oracle.net.aso.b;

import sun.misc.BASE64Encoder;

public class MakeClassMapLegend extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public MakeClassMapLegend() {
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

		response.setContentType("text/html");
		response.setHeader("Cache-Control", "no-cache");
		// ��ȡ���ݹ�����SQL
		String initaldata = request.getParameter("lengendStr").toString();
		String userId = request.getParameter("userId");
		String mapName = request.getParameter("mapName");

		String mapId = request.getParameter("mapId");

		String path = request.getServletContext().getRealPath("/")+"//resource//printMap//";
		// 构建路径保存
		String MapId = null;
		if (mapName != null && !"".equals(mapName)) {
			QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
			String sql = "select t.rowid from WHU_CUSTOMMAP t where mapname=? and userid=?";
			Object[] params = { mapName, userId };
			try {
				MapId = "" + qr.query(sql, new ScalarHandler(), params);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (mapId != null && !"".equals(mapId)) {
			MapId = mapId;
		}

		String[] item = initaldata.split("@");

		int width = 200;

		int height = 40 * item.length;

		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

		Graphics2D g2d = image.createGraphics();

		image = g2d.getDeviceConfiguration().createCompatibleImage(width,
				height + 40, Transparency.TRANSLUCENT);

		g2d.dispose();

		g2d = image.createGraphics();
		Font font = new Font("宋体", Font.PLAIN, 20);
		g2d.setFont(font);
		g2d.setColor(Color.black);
		g2d.drawString("分级图图例", 50, 20);
		font = new Font("", Font.PLAIN, 14);
		g2d.setFont(font);
		for (int i = 0; i < item.length; i++) {
			String[] record = item[i].split(",");
			g2d.setColor(new Color(Integer.parseInt(record[0]), Integer
					.parseInt(record[1]), Integer.parseInt(record[2])));
			g2d.fillRoundRect(0, i * 30 + 30, 35, 20, 0, 0);
			g2d.setColor(Color.black);
			String[] breaksString = record[3].split("~");
			BigDecimal bigDecimal = new BigDecimal(breaksString[0].trim());
			BigDecimal divisor = BigDecimal.ONE;
			MathContext mc = new MathContext(9);
			String string = bigDecimal.divide(divisor, mc).toString();
			bigDecimal = new BigDecimal(breaksString[1].trim());
			divisor = BigDecimal.ONE;
			mc = new MathContext(9);
			string = string + "~" + bigDecimal.divide(divisor, mc).toString();
			g2d.drawString(string, 60, 15 + i * 30 + 30);
		}

		g2d.dispose();

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		String returnImgStr = "";
		if (MapId != null && !"".equals(MapId)) {
			path += MapId;
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			} else {
				file.mkdirs();
			}
			ImageIO.write(image, "png", new File(path+"//legend_grade.png"));
		}

		
		try {
			ImageIO.write(image, "png", output);
			BASE64Encoder encoder = new BASE64Encoder();
			String xt = encoder.encode(output.toByteArray());
			returnImgStr = xt;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PrintWriter out = response.getWriter();
		out.println(returnImgStr);
		out.flush();
		out.close();

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
