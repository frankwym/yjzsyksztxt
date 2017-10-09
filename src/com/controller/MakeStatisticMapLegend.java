package com.controller;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kjoms.udcs.util.GetOpenService;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import sun.misc.BASE64Encoder;

import com.dbconn.JdbcUtils;
import com.zj.chart.chartfactory.ChartFactory;
import com.zj.chart.chartfactory.ChartStyleFactory;
import com.zj.chart.chartfactory.IChart;
import com.zj.chart.chartstyle.ChartDataPara;
import com.zj.chart.chartstyle.ChartStyle;
import com.zj.chart.data.IndicatorData;
import com.zj.chart.data.ReadRegionData;
import com.zj.util.JAffine;
import com.zj.util.JUtil;

public class MakeStatisticMapLegend extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public MakeStatisticMapLegend() {
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
		
		//response.setContentType("application/octet-stream");
		//response.setHeader("Content-type", "image/jpeg");
		//response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("UTF-8");
		//request.setCharacterEncoding("gbk");

		//获取参数
		String wcString=request.getParameter("WC");
		String dcString=request.getParameter("DC");
		String themeData=request.getParameter("chartData");
		
		//将没有数据的值换成0
		themeData=themeData.replaceAll("null", "0");
		//中文转码
		//String chartData=new String(themeData.getBytes("ISO-8859-1"),("UTF-8"));
		String colorString=request.getParameter("colors");
		String chartId=request.getParameter("chartId");
		String widthString=request.getParameter("widthString");
		String heightString=request.getParameter("heightString");
		String yearString ="2016";
		//servlet test:
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
		
		Rectangle2D.Double WC = JUtil.StringToRect(wcString);//WC
		Rectangle2D.Double DC = JUtil.StringToRect(dcString);//DC

		ChartStyleFactory chartStyleFactory = new ChartStyleFactory();
		ChartStyle chartStyle = chartStyleFactory.createcChartStyle(chartId);//符号样式

		String dir = "assets/";
		String chartPath = dir + chartId + ".xml";
		chartStyle.Load(chartPath);

		ChartDataPara chartDataPara = new ChartDataPara();
		chartDataPara.initial(themeData,yearString);//初始化专题符号层参数

		String[] colors = colorString.split(";");
		int[] fieldColors = new int[colors.length]; //专题符号颜色
		for (int i = 0; i < fieldColors.length; i++) {
			fieldColors[i] = Integer.parseInt(colors[i]);
		}

		chartDataPara.setFieldColor(fieldColors);
		int width = Integer.parseInt(widthString);
		int height = Integer.parseInt(heightString);

		/*width = (int) (1000000*width / WC.width);
		height = (int) (1600000*height / WC.height);//width,height
*/		chartDataPara.setWidth(width);
		chartDataPara.setHeight(height);

		IndicatorData[] indicatorDatas = JUtil.getIndicatorData(themeData,chartDataPara);
		double[] maxValues = JUtil.maxValues(indicatorDatas);
		double[] minValues = JUtil.minValues(indicatorDatas);
		double[] averageValues = JUtil.averageValues(indicatorDatas);
		double[] scales = JUtil.scales(indicatorDatas, width); 

		chartDataPara.setScales(scales);

		ReadRegionData regionData = new ReadRegionData(themeData);
		String[] regionCodes = regionData.getRegonCode();
		String[] xStrings = regionData.getRegonX();
		String[] yStrings = regionData.getRegonY();

		JAffine affine = new JAffine(regionCodes, xStrings, yStrings, WC, DC);
		double[] X = affine.getX();
		double[] Y = affine.getY();

		ChartFactory chartFactory = new ChartFactory();
		IChart chart = chartFactory.createcChart(chartId);
		IndicatorData[] indicatorData = new IndicatorData[1];
		indicatorData[0] = indicatorDatas[0];
//		BufferedImage bi=chart.drawLegend(100, 200, chartDataPara, chartStyle, maxValues, minValues, averageValues, indicatorData);
		BufferedImage bi=chart.drawLegend(width, height, chartDataPara, chartStyle, maxValues, minValues, averageValues, indicatorData);
//		chart.drawChart(graphics2d, 100, 100, 100, 100, chartDataPara, chartStyle, maxValues, minValues, averageValues, indicatorData);

		
		// String path=this.getServletContext().getRealPath("/");
		// delAllFile(path+"UI\\createdSymbols");
		// String
		// absolutPath="Statistic"+(int)(1+Math.random()*(1000000-1+1))+".png";
		// String legendPath=path+"UI\\createdSymbols\\"+absolutPath;
		// String returnStr=absolutPath;
		// ImageIO.write(bi, "png", new File(legendPath));
		// PrintWriter out = response.getWriter();
		// out.println(returnStr);
		// out.flush();
		// out.close();
		/*ServletOutputStream sos = response.getOutputStream();		
		ImageIO.write(bi, "PNG", sos);
		sos.close();*/
			
			String returnImgStr="";
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			if (MapId != null && !"".equals(MapId)) {
				path += MapId;
				File file = new File(path);
				if (!file.exists()) {
					file.mkdirs();
				} else {
					file.mkdirs();
				}
				ImageIO.write(bi, "png", new File(path+"//legend_sta.png"));
			}

			try {

				ImageIO.write(bi, "png", output);
				BASE64Encoder encoder = new BASE64Encoder();
				String xt = encoder.encode(output.toByteArray());
				returnImgStr = returnImgStr + xt;
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

		doGet(request,response);
	}
	 public static boolean delAllFile(String path) {
	       boolean flag = false;
	       File file = new File(path);
	       if (!file.exists()) {
	         return flag;
	       }
	       if (!file.isDirectory()) {
	         return flag;
	       }
	       String[] tempList = file.list();
	       File temp = null;
	       for (int i = 0; i < tempList.length; i++) {
	          if (path.endsWith(File.separator)) {
	             temp = new File(path + tempList[i]);
	          } else {
	              temp = new File(path + File.separator + tempList[i]);
	          }
	          if (temp.isFile()) {
	             temp.delete();
	          }
	          if (temp.isDirectory()) {
	             delAllFile(path + "/" + tempList[i]);//
	             delFolder(path + "/" + tempList[i]);//
	             flag = true;
	          }
	       }
	       return flag;
	     }


	      public static void delFolder(String folderPath) {
	      try {
	         delAllFile(folderPath); //
	         String filePath = folderPath;
	         filePath = filePath.toString();
	         java.io.File myFilePath = new java.io.File(filePath);
	         myFilePath.delete(); //
	      } catch (Exception e) {
	        e.printStackTrace(); 
	      }
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
