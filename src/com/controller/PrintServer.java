package com.controller;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.dbconn.JdbcUtils;

import kjoms.udcs.util.GetOpenService;

public class PrintServer extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		String mapUrl = request.getParameter("url");
		String userId = request.getParameter("userId");
		String mapName = request.getParameter("mapName");

		String mapId = request.getParameter("mapId");
		String text = request.getParameter("text");
		String textFont = request.getParameter("textFont");
		String textBackGroundColor = request
				.getParameter("textBackGroundColor");
		String textFontColor = request.getParameter("textFontColor");
		textFont = !"".equals(textFont) && null != textFont ? textFont : "微软雅黑";
		textBackGroundColor = !"".equals(textBackGroundColor)
				&& null != textBackGroundColor ? textBackGroundColor
				: "#ffffff";
		textFontColor = !"".equals(textFontColor)
				&& null != textFontColor ? textFontColor
				: "#000000";
		BufferedImage textImage = null;
		String backImagePath = this.getServletContext().getRealPath("/")
				+ "images/txt.png";
		if (!"".equals(text) && text != null) {
			textImage = makeTxtWithin(text, backImagePath, textFont,
					textBackGroundColor,textFontColor);
		}

		Double dpi = Double.valueOf(request.getParameter("dpi")) * 0.75;// 相比100dpi的倍数,乘以0.7让尺寸更合适

		int image_x = 9;
		int image_y = 967;
		int image_w = 9;// 以高为基准还原宽
		int image_h = 500;

		int lengend_x = 805;// 记得减去高宽才算对
		int lengend_y = 964;
		int lengend_w = 500;
		int lengend_h = 805;// 高度按比例还原

		int text_x = 150;
		int text_y = 800;
		int text_w = 420;
		int text_h = 0;

		URL url;
		String path = request.getServletContext().getRealPath("/")
				+ "//resource//printMap//";
		;
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
		/*
		 * if (MapId != null && !"".equals(MapId)) { path += MapId; }
		 */

		if (mapUrl == null || "".equals(mapUrl)) {
			return;
		}
		try {
			url = new URL(mapUrl);
			URLConnection con = url.openConnection();
			// 输入流
			InputStream is = null;
			is = con.getInputStream();
			BufferedImage bi = new BufferedImage(400, 400,
					BufferedImage.TYPE_INT_RGB);
			bi = ImageIO.read(is);
			final Graphics2D finalg2d = (Graphics2D) bi.createGraphics();
			// 图片地址
			String pic_path = path + MapId + "//pic.png";
			System.out.println(pic_path);
			File file = new File(pic_path);
			// 如果图表文件存在
			if (file.exists()) {
				BufferedImage frameBuffer = null;
				try {
					frameBuffer = ImageIO.read(file);
				} catch (Exception e) {
					System.out.println("绘制图片失败：" + e.toString());
				}

				// 宽高比
				double wbh_pic = frameBuffer.getWidth() * 1.0
						/ frameBuffer.getHeight();

				// 计算长宽比例后再开始绘制//乘以0.9让尺寸更合适
				finalg2d.drawImage(frameBuffer, (int) (50),
						(int) (bi.getHeight() * 0.90 - 450),
						(int) (image_h * wbh_pic), (int) (image_h), null);
			}

			// 设置图例位置
			String lengend_grade = path + MapId + "//legend_grade.png";
			File lengend_grade_file = new File(lengend_grade);
			System.out.println(lengend_grade);
			BufferedImage lengend_grade_buffer = null;
			try {
				lengend_grade_buffer = ImageIO.read(lengend_grade_file);
			} catch (Exception e) {

			}

			String lengend_sta = path + MapId + "//legend_sta.png";

			File lengend_sta_file = new File(lengend_sta);
			System.out.println(lengend_sta);
			BufferedImage lengend_sta_buffer = null;
			try {
				lengend_sta_buffer = ImageIO.read(lengend_sta_file);
			} catch (Exception e) {

			}

			// 合并图例
			try {
				BufferedImage legendImage = mergeImage(lengend_grade_buffer,
						lengend_sta_buffer, false);
				// 计算长宽比例后再开始绘制
				// 宽高比
				double wbh_legend = legendImage.getWidth() * 1.0
						/ legendImage.getHeight();
				/*
				 * finalg2d.drawImage( legendImage, (int) (lengend_x * dpi -
				 * (lengend_w * dpi)), (int) (lengend_y * dpi - (int)
				 * ((lengend_w * dpi) / wbh_legend)), (int) (lengend_w * dpi),
				 * (int) ((lengend_w * dpi) / wbh_legend), null);
				 */
				finalg2d.drawImage(
						// 乘以0.956让尺寸更合适
						legendImage, (int) (bi.getWidth() - lengend_w - 30),
						(int) (bi.getHeight() * 0.956 - (lengend_w)
								/ wbh_legend), (int) (lengend_w),
						(int) ((lengend_w) / wbh_legend), null);
			} catch (Exception e) {
				// TODO: handle exception
			}

			// 绘制文本
			if (textImage != null) {
				Double textr = textImage.getHeight()
						/ (textImage.getWidth() * 1.0);
				finalg2d.drawImage(
						// 乘以0.956让尺寸更合适
						textImage, (int) (text_x), (int) (text_y),
						(int) (text_w), (int) (textr * text_w), null);
			}
			response.setHeader("content-disposition",
					"attachment;fileName=tt.png");
			response.setContentType("image/png"); // 设置返回的文件类型
			OutputStream os = response.getOutputStream();
			ImageIO.write(bi, "png", os);

			os.flush();
			os.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}

	public static BufferedImage mergeImage(BufferedImage img1,
			BufferedImage img2, boolean isHorizontal) throws IOException {
		int w1 = img1.getWidth();
		int h1 = img1.getHeight();
		int w2 = img2.getWidth();
		int h2 = img2.getHeight();

		// 生成新图片
		BufferedImage DestImage = null;
		if (isHorizontal) { // 水平方向合并
			DestImage = new BufferedImage(w1 + w2, h1,
					BufferedImage.TYPE_INT_RGB);
			Graphics graphics = DestImage.getGraphics();
			// 填充背景色
			graphics.setColor(Color.WHITE);
			graphics.fillRect(0, 0, w1 + w2, h1);
			graphics.drawImage(img1, 0, 0, w1, h1, null);
			graphics.drawImage(img2, w1, 0, w2, h2, null);
		} else { // 垂直方向合并
			DestImage = new BufferedImage(w1, h1 + h2,
					BufferedImage.TYPE_INT_RGB);
			Graphics graphics = DestImage.getGraphics();
			// 填充背景色
			graphics.setColor(Color.WHITE);
			graphics.fillRect(0, 0, w1, h1 + h2);
			graphics.drawImage(img1, 0, 0, w1, h1, null);
			graphics.drawImage(img2, 0, h1, w2, h2, null);
		}
		ImageIO.write(DestImage, "png", new File("D:\\testtile.png"));
		return DestImage;
	}

	public static void main(String[] args) throws IOException {
		String lengend_grade = "D:\\" + "default" + "\\" + "legend_grade.png";
		File lengend_grade_file = new File(lengend_grade);
		BufferedImage lengend_grade_buffer = null;
		try {
			lengend_grade_buffer = ImageIO.read(lengend_grade_file);
		} catch (Exception e) {
			System.out.println("绘制图片失败：" + e.toString());
		}

		String lengend_sta = "D:\\" + "default" + "\\" + "legend_sta.png";

		File lengend_sta_file = new File(lengend_sta);
		BufferedImage lengend_sta_buffer = null;
		try {
			lengend_sta_buffer = ImageIO.read(lengend_sta_file);
		} catch (Exception e) {
			System.out.println("绘制图片失败：" + e.toString());
		}

		// 调用mergeImage方法获得合并后的图像
		BufferedImage destImg = null;
		// 调用mergeImage方法获得合并后的图像

		try {
			destImg = mergeImage(lengend_grade_buffer, lengend_sta_buffer,
					false);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 保存图像
		ImageIO.write(destImg, "png", new File("D:\\testtileMap.png"));
		System.out.println("水平合并完毕!");

	}

	public static BufferedImage cutImage(InputStream in, int x, int y, int w,
			int h) throws IOException {
		Iterator iterator = ImageIO.getImageReadersByFormatName("png");
		ImageReader reader = (ImageReader) iterator.next();
		ImageInputStream iis = ImageIO.createImageInputStream(in);
		reader.setInput(iis, true);
		ImageReadParam param = reader.getDefaultReadParam();
		Rectangle rect = new Rectangle(x, y, w, h);
		param.setSourceRegion(rect);
		BufferedImage bi = reader.read(0, param);
		return bi;

	}

	public static BufferedImage makeTxtWithPng(String str4, String backImagePath)
			throws IOException {
		// 开始写txt的坐标
		int start_x = 10;
		int start_y = 10;

		// 右边距换行处理
		int margin_r = 20;
		// 首行添加两个空格
		str4 = "  " + str4;
		// Create a buffered image with transparency
		// 自定义大小图片
		/*
		 * BufferedImage bimage = new BufferedImage(400, 400,
		 * BufferedImage.TYPE_INT_ARGB);
		 * 
		 * // Draw the image on to the buffered image Graphics2D graphics =
		 * bimage.createGraphics();
		 * 
		 * // 生成白色的底色 graphics.setColor(Color.WHITE); graphics.fillRect(0, 0,
		 * bimage.getWidth(), bimage.getHeight());
		 */

		// 选择底图设置
		BufferedImage bimage = ImageIO.read(new File(backImagePath));
		Graphics2D graphics = bimage.createGraphics();

		// 设置打印字体（字体名称、样式和点大小）（字体名称可以是物理或者逻辑名称）
		// Java平台所定义的五种字体系列：Serif、SansSerif、Monospaced、Dialog 和 DialogInput
		Font font = new Font("微软雅黑", Font.PLAIN, 20);
		graphics.setFont(font);// 设置字体
		// BasicStroke bs_3=new BasicStroke(0.5f);
		float[] dash1 = { 2.0f };
		// 设置打印线的属性。
		// 1.线宽 2、3、不知道，4、空白的宽度，5、虚线的宽度，6、偏移量
		graphics.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 2.0f, dash1, 0.0f));
		// g2.setStroke(bs_3);//设置线宽
		float fontHeigth1 = font.getSize2D();
		// -1- 用Graphics2D直接输出
		// 首字符的基线(右下部)位于用户空间中的 (x, y) 位置处

		// g2.drawLine(10,10,200,300);
		graphics.setColor(Color.black);

		// 字体自动换行

		FontMetrics metrics = graphics.getFontMetrics();
		int StrPixelWidth = metrics.stringWidth(str4); // 字符串长度（像素） str要打印的字符串
		System.out.println(StrPixelWidth);
		int lineSize = (int) Math.ceil(StrPixelWidth * 1.0
				/ (bimage.getWidth() - margin_r));// 要多少行
		System.out.println(lineSize);
		String tempStrs[] = new String[lineSize];// 存储换行之后每一行的字符串
		// FontMetrics metrics = new FontMetrics(font) {};
		// Rectangle2D bounds = metrics.getStringBounds(str, null);
		// int widthInPixels = (int) bounds.getWidth();
		// System.out.println(StrPixelWidth+"---:");
		if (bimage.getWidth() < StrPixelWidth - margin_r) {// 页面宽度（width）小于
															// 字符串长度
			int lineNum = (int) Math.ceil((StrPixelWidth) * 1.0 / lineSize);// 算出行数
			StringBuilder sb = new StringBuilder();// 存储每一行的字符串
			int j = 0;
			int tempStart = 0;

			for (int i1 = 0; i1 < str4.length(); i1++) {
				char ch = str4.charAt(i1);
				sb.append(ch);
				Rectangle2D bounds2 = metrics.getStringBounds(sb.toString(),
						null);
				int tempStrPi1exlWi1dth = (int) bounds2.getWidth();
				if (tempStrPi1exlWi1dth + margin_r > (bimage.getWidth())) {
					tempStrs[j++] = str4.substring(tempStart, i1);
					tempStart = i1;
					sb.delete(0, sb.length());
					sb.append(ch);
				}
				if (i1 == str4.length() - 1) {// 最后一行
					tempStrs[j] = str4.substring(tempStart);
				}
			}

			// 绘制第一行 第一个字大些加粗

		} else {
			tempStrs[0] = str4;
		}
		for (int i = 0; i < tempStrs.length; i++) {
			graphics.drawString(tempStrs[i], (int) start_x, (int) start_y
					+ fontHeigth1 * (i + 1));
		}
		return bimage;

	}

	public static BufferedImage makeTxtWithin(String str4, String outputPath,
			String textFont, String textBackGroundColor, String textFontColor)
			throws IOException {
		// 开始写txt的坐标
		int start_x = 0;
		int start_y = 0;

		// 右边距换行处理
		int margin_r = 0;
		// 首行添加两个空格
		str4 = "  " + str4;
		// Create a buffered image with transparency
		// 自定义大小图片

		BufferedImage bimage = new BufferedImage(420, 10000,
				BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image Graphics2D graphics =
		Graphics2D graphics = bimage.createGraphics();

		// 生成白色的底色
		textBackGroundColor = textBackGroundColor.substring(1);
		Color color = new Color(Integer.parseInt(textBackGroundColor, 16));

		graphics.setColor(color);
		graphics.fillRect(0, 0, bimage.getWidth(), bimage.getHeight());

		// 选择底图设置
		// BufferedImage bimage = ImageIO.read(new File(backImagePath));
		// Graphics2D graphics = bimage.createGraphics();

		// 设置打印字体（字体名称、样式和点大小）（字体名称可以是物理或者逻辑名称）
		// Java平台所定义的五种字体系列：Serif、SansSerif、Monospaced、Dialog 和 DialogInput
		Font font = new Font(textFont, Font.BOLD, 30);
		graphics.setFont(font);// 设置字体
		// BasicStroke bs_3=new BasicStroke(0.5f);
		float[] dash1 = { 2.0f };
		// 设置打印线的属性。
		// 1.线宽 2、3、不知道，4、空白的宽度，5、虚线的宽度，6、偏移量
		graphics.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 2.0f, dash1, 0.0f));
		// g2.setStroke(bs_3);//设置线宽
		float fontHeigth1 = font.getSize2D();
		// -1- 用Graphics2D直接输出
		// 首字符的基线(右下部)位于用户空间中的 (x, y) 位置处

		// g2.drawLine(10,10,200,300);
		textFontColor = textFontColor.substring(1);
		Color color2 = new Color(Integer.parseInt(textFontColor, 16));
		graphics.setColor(color2);

		// 字体自动换行

		FontMetrics metrics = graphics.getFontMetrics();
		int StrPixelWidth = metrics.stringWidth(str4); // 字符串长度（像素） str要打印的字符串
		System.out.println(StrPixelWidth);
		int lineSize = (int) Math.ceil(StrPixelWidth * 1.0
				/ (bimage.getWidth() - margin_r));// 要多少行
		System.out.println(lineSize);
		String tempStrs[] = new String[lineSize];// 存储换行之后每一行的字符串
		// FontMetrics metrics = new FontMetrics(font) {};
		// Rectangle2D bounds = metrics.getStringBounds(str, null);
		// int widthInPixels = (int) bounds.getWidth();
		// System.out.println(StrPixelWidth+"---:");
		if (bimage.getWidth() < StrPixelWidth - margin_r) {// 页面宽度（width）小于
															// 字符串长度
			int lineNum = (int) Math.ceil((StrPixelWidth) * 1.0 / lineSize);// 算出行数
			StringBuilder sb = new StringBuilder();// 存储每一行的字符串
			int j = 0;
			int tempStart = 0;

			for (int i1 = 0; i1 < str4.length(); i1++) {
				char ch = str4.charAt(i1);
				sb.append(ch);
				Rectangle2D bounds2 = metrics.getStringBounds(sb.toString(),
						null);
				int tempStrPi1exlWi1dth = (int) bounds2.getWidth();
				if (tempStrPi1exlWi1dth + margin_r > (bimage.getWidth())) {
					tempStrs[j++] = str4.substring(tempStart, i1);
					tempStart = i1;
					sb.delete(0, sb.length());
					sb.append(ch);
				}
				if (i1 == str4.length() - 1) {// 最后一行
					int tem = j - 1;
					tempStrs[tem] = str4.substring(tempStart);
				}
			}

			// 绘制第一行 第一个字大些加粗

		} else {
			tempStrs[0] = str4;
		}
		for (int i = 0; i < tempStrs.length; i++) {
			if (tempStrs[i] == null) {
				continue;
			}
			graphics.drawString(tempStrs[i], (int) start_x, (int) start_y
					+ fontHeigth1 * (i + 1));
		}
		// try {
		// ImageIO.write(bimage, "png", new File("D://text.png"));
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		ImageOutputStream imOut = ImageIO.createImageOutputStream(bs);
		ImageIO.write(bimage, "png", imOut);
		InputStream is = new ByteArrayInputStream(bs.toByteArray());

		// 裁剪至合适大小
		return cutImage(is, 0, 0, bimage.getWidth(), (int) (fontHeigth1
				* tempStrs.length + 10));
	}

}
