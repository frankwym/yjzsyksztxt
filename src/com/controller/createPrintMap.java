package com.controller;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class createPrintMap extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public createPrintMap() {
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
		response.setContentType("text/plain;charset=" + "utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		String path=this.getServletContext().getRealPath("/");
		String sourceFilePath = path+"images\\11.jpg";
        String waterFilePath = path+"images\\22.jpg";
        String saveFilePath = path+"images\\44.jpg";
       String statisticlegendPath=path+"images\\33.jpg";
       String classLegendPath=path+"images\\331.jpg";
       String compassPath=path+"images\\compass.jpg";
       String textPath=path+"images\\text.jpg";
       String chartPath=path+"images\\chart.jpg";
        // 构建叠加层
        BufferedImage buffImg = watermark(new File(sourceFilePath), new File(waterFilePath), 150, 150, 1.0f,1);
        // 输出水印图片
        generateWaterFile(buffImg, saveFilePath);
        // 构建叠加层
        BufferedImage buffImg2 = watermark(new File(saveFilePath), new File(statisticlegendPath), 3110, 3000, 1.0f,4);
        // 输出统计图例图片
        generateWaterFile(buffImg2, saveFilePath);
        // 构建叠加层
        BufferedImage buffImg3 = watermark(new File(saveFilePath), new File(classLegendPath), 3110, 3670, 1.0f,3);
        // 输出统计图例图片
        generateWaterFile(buffImg3, saveFilePath);
        // 构建叠加层
        BufferedImage buffImg4 = watermark(new File(saveFilePath), new File(compassPath), 200, 200, 1.0f,2);
        // 输出指北针图片
        generateWaterFile(buffImg4, saveFilePath);
        // 构建叠加层
        BufferedImage buffImg5= watermark(new File(saveFilePath), new File(textPath), 200, 600, 1.0f,2);
        // 输出指北针图片
        generateWaterFile(buffImg5, saveFilePath);
        // 构建叠加层
        BufferedImage buffImg6 = watermark(new File(saveFilePath), new File(chartPath), 2700, 1000, 1.0f,2);
        // 输出指北针图片
        generateWaterFile(buffImg6, saveFilePath);
	}
	 public static BufferedImage watermark(File file, File waterFile, int x, int y, float alpha,int time) throws IOException {
	        // 获取底图
	        BufferedImage buffImg = ImageIO.read(file);
	        // 获取层图
	        BufferedImage waterImg = ImageIO.read(waterFile);	 	        
	        // 创建Graphics2D对象，用在底图对象上绘图
	        Graphics2D g2d = buffImg.createGraphics();
	        int waterImgWidth = waterImg.getWidth();// 获取层图的宽度
	        int waterImgHeight = waterImg.getHeight();// 获取层图的高度
	        // 在图形和图像中实现混合和透明效果
	        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
	        //Image newImage=waterImg.getScaledInstance(waterImgWidth*time, waterImgHeight*time,  Image.SCALE_SMOOTH );
	        //BufferedImage newwaterImg=toBufferedImage(newImage);
	        // 绘制
	        g2d.drawImage(waterImg, x, y, waterImgWidth*time, waterImgHeight*time, null);
	        
	        
	        
	        g2d.dispose();// 释放图形上下文使用的系统资源
	        return buffImg;
	    }
	 public static BufferedImage toBufferedImage(Image img)
	 {
	     if (img instanceof BufferedImage)
	     {
	         return (BufferedImage) img;
	     }

	     // Create a buffered image with transparency
	     BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

	     // Draw the image on to the buffered image
	     Graphics2D bGr = bimage.createGraphics();
	     bGr.drawImage(img, 0, 0, null);
	     bGr.dispose();

	     // Return the buffered image
	     return bimage;
	 }

	    /**
	     * 输出水印图片
	     * 
	     * @param buffImg
	     *            图像加水印之后的BufferedImage对象
	     * @param savePath
	     *            图像加水印之后的保存路径
	     */
	    private void generateWaterFile(BufferedImage buffImg, String savePath) {
	        int temp = savePath.lastIndexOf(".") + 1;
	        try {
	            ImageIO.write(buffImg, savePath.substring(temp), new File(savePath));
	        } catch (IOException e1) {
	            e1.printStackTrace();
	        }
	    }
}
