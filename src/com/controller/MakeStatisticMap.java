package com.controller;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sun.misc.BASE64Encoder;


import com.zj.chart.chartfactory.ChartFactory;
import com.zj.chart.chartfactory.ChartStyleFactory;
import com.zj.chart.chartfactory.IChart;
import com.zj.chart.chartstyle.ChartDataPara;
import com.zj.chart.chartstyle.ChartStyle;
import com.zj.chart.data.IndicatorData;
import com.zj.chart.data.ReadRegionData;
import com.zj.util.JAffine;
import com.zj.util.JUtil;

public class MakeStatisticMap extends HttpServlet {

	
	
	//测试
	
	/*
	 * http://localhost:8080/TJCH_DLGQ/makeStatisticMap?WC=0,0,60,60&DC=0,0,60,60&chartData={%20%22columns%22:%20[%20%22jiedaoCode%22,%20%22zhibao1%22,%20%22zhibiao2%22%20],%20%22datas%22:%20[%20[%20%221011%22,%20%221%22,%20%222.0%22%20],%20[%20%221011%22,%20%221%22,%20%222.0%22%20]%20]%20}&colors=32896;8405056;16776960&chartId=10201&widthString=60&heightString=60&isLabel=false
	 */
	/**
	 * Constructor of the object.
	 */
	public MakeStatisticMap() {
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
		request.setCharacterEncoding("UTF-8");
		//锟斤拷取锟斤拷锟斤拷
		String wcString=request.getParameter("WC");
		String dcString=request.getParameter("DC");
		String chartData=request.getParameter("chartData");
		//将没有数据的值换成0
		chartData=chartData.replaceAll("null", "0");
		//将没有数据的值换成0
		chartData.replaceAll("null", "0");
		String colorString=request.getParameter("colors");
		String chartId=request.getParameter("chartId");
		String widthString=request.getParameter("widthString");
		String heightString=request.getParameter("heightString");
		String isLabelString=request.getParameter("isLabel");
		String yearString ="2016";
	/*	colorString="16776960;12303665;16744448";
		chartData="1,2,3;个,个,个;4815,9288,10677,1,0,0,320583;4695,9727,11278,2,0,0,320581;3225,5633,6115,3,0,0,320508;2700,4513,4754,4,0,0,320505;4581,7995,8865,5,0,0,320501;1573,3061,3529,6,0,0,320585;5554,9649,10801,7,0,0,320582;4089,7089,7903,8,0,0,320584;4473,7509,8102,9,0,0,320506;2858,4894,5159,10,0,0,320507;";
*/
		//锟斤拷锟斤拷锟阶拷锟轿拷锟斤拷锟�
		Rectangle2D.Double WC = JUtil.StringToRect(wcString);//WC
		Rectangle2D.Double DC = JUtil.StringToRect(dcString);//DC
		
		//锟斤拷锟斤拷锟绞�
		ChartStyleFactory chartStyleFactory = new ChartStyleFactory();
		ChartStyle chartStyle = chartStyleFactory.createcChartStyle(chartId);
		
		int width = Integer.parseInt(widthString);
		int height = Integer.parseInt(heightString);
		
		/*width = (int) (1000000*width / WC.width);
		height = (int) (1600000*height / WC.height);*///width,height
		/*width = (int) (10*width / WC.width);
		height = (int) (16*height / WC.height);//width,height
*/		
		String dir = "assets/";
		String chartPath = dir + chartId + ".xml";
		chartStyle.Load(chartPath);
		
		chartStyle.setLabels(Boolean.parseBoolean(isLabelString));
		chartStyle.setLabelsFontSize(width/8);
		
		ChartDataPara chartDataPara = new ChartDataPara();
		chartDataPara.initial(chartData,yearString);//锟斤拷始锟斤拷专锟斤拷锟脚诧拷锟斤拷锟�
		
		
		String[] colors=colorString.split(";");
		int[] fieldColors=new int[colors.length];  //专锟斤拷锟脚碉拷锟斤拷色
		for(int i=0;i<fieldColors.length;i++)
		{
			fieldColors[i]=Integer.parseInt(colors[i]);
			//System.out.println(fieldColors[i]);
		}
		chartDataPara.setFieldColor(fieldColors);
		chartDataPara.setWidth(width);
		chartDataPara.setHeight(height);
		
		ReadRegionData regionData = new ReadRegionData(chartData);
		String[] regionCodes = regionData.getRegonCode();
		
		IndicatorData[] indicatorDatas = JUtil.getIndicatorData(chartData,chartDataPara);
		double[] maxValues = JUtil.maxValues(indicatorDatas);
		double[] minValues = JUtil.minValues(indicatorDatas);
		double[] averageValues = JUtil.averageValues(indicatorDatas);
		double[] scales = JUtil.scales(indicatorDatas, width); 
		
		chartDataPara.setScales(scales);
		
		
		String[] xStrings = regionData.getRegonX();
		String[] yStrings = regionData.getRegonY();
		
		JAffine affine = new JAffine(regionCodes, xStrings, yStrings, WC, DC);
		double[] X = affine.getX();
		double[] Y = affine.getY();
			
		/*BufferedImage bi = new BufferedImage((int)DC.getWidth(), (int)DC.getHeight()
				, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2D = bi.createGraphics();*/
		
		
		/*BufferedImage bi2 = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2d=bi2.createGraphics();*/
		/* String path=this.getServletContext().getRealPath("/");
		 delAllFile(path+"images\\xx");*/
		 //锟斤拷锟截碉拷锟侥硷拷锟斤拷锟�
		// String returnStr="";
		 String returnImgStr="";
		for (int i = 0; i < regionCodes.length; i++) {
			IndicatorData[] indicatorData = new IndicatorData[1];
			indicatorData[0] = indicatorDatas[i];
			//锟斤拷锟斤拷锟斤拷图锟斤拷锟�.4锟斤拷锟斤拷锟斤拷锟皆很好碉拷锟斤拷示注锟斤拷
			  
			BufferedImage bi = new BufferedImage((int)width, (int)height
					, BufferedImage.TYPE_INT_ARGB);
			//锟斤拷锟斤拷锟斤拷图锟斤拷锟�.4锟斤拷锟斤拷锟斤拷锟皆很好碉拷锟斤拷示注锟斤拷
			if(isLabelString=="true"){
				bi = new BufferedImage((int)(1.4*width), (int)(1.4*height)
						, BufferedImage.TYPE_INT_ARGB);
			}
			Graphics2D g2D = bi.createGraphics();
			ChartFactory chartFactory = new ChartFactory();
			IChart chart = chartFactory.createcChart(chartId);
			//1.4锟斤拷锟斤拷示图锟斤拷
			if(isLabelString=="true"){
				chart.drawChart(g2D, (int)(1.4*width)/2,(int)(1.4*height)/2, width, height, chartDataPara, chartStyle, maxValues, minValues, averageValues, indicatorData);
			}
			else{
				chart.drawChart(g2D, width/2,height/2, width, height, chartDataPara, chartStyle, maxValues, minValues, averageValues, indicatorData);
			}
			
			//String absolutPath="Statistic"+(int)(1+Math.random()*(1000000-1+1))+".png";
			//String legendPath=path+"UI\\createdSymbols\\"+absolutPath;
			//returnStr=returnStr+absolutPath+";";
			//ImageIO.write(bi, "png", new File(legendPath));
			ByteArrayOutputStream output = new ByteArrayOutputStream(); 
			try {
				/*String absolutPath="Statistic"+(int)(1+Math.random()*(1000000-1+1))+".png";
				String legendPath=path+"images\\xx\\"+absolutPath;
				returnStr=returnStr+absolutPath+";";
				ImageIO.write(bi, "png", new File(legendPath));*/
	         	ImageIO.write(bi, "png", output);
				BASE64Encoder encoder = new BASE64Encoder();
				String xt=encoder.encode(output.toByteArray());
				 returnImgStr=returnImgStr+xt+";";
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//chart.drawChart(g2D, 100, 100, 100, 100, chartDataPara, chartStyle, maxValues, minValues, averageValues, indicatorData);
		}	
		
		PrintWriter out = response.getWriter();
		out.println(returnImgStr);
		out.flush();
		out.close();

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
				delAllFile(path + "/" + tempList[i]);
				delFolder(path + "/" + tempList[i]);
				flag = true;
			}
		}
		return flag;
	}

	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath);
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
