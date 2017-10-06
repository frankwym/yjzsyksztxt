import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

public class MakeTextImage {

	String text = "";

	public MakeTextImage() {

	}

	// 最佳170个字
	public static void main(String[] args) throws IOException {

		String str4 = "天津，简称津，中华人民共和国直辖市、国家中心城市、超大城市、[1-2]  环渤海地区经济中心、首批沿海开放城市，全国先进制造研发基地、北方国际航运核心区、金融创新运营示范区、改革开放先行区天津，简称津，中华人民共和国直辖市、国家中心城市、超大城市、[1-2]  环渤海地区经济中心、首批沿海开放城市，全国先进制造研发基地、北方国际航运核心区";
		makeTxtWithPng(str4,"d://txt.png","d://text.png");
		makeTxtWithin(str4,"d://text2.png");
	}

	/*
	 * 图片裁剪通用接口
	 */

	public static void cutImage(InputStream in, String dest, int x, int y,
			int w, int h) throws IOException {
		Iterator iterator = ImageIO.getImageReadersByFormatName("png");
		ImageReader reader = (ImageReader) iterator.next();
		ImageInputStream iis = ImageIO.createImageInputStream(in);
		reader.setInput(iis, true);
		ImageReadParam param = reader.getDefaultReadParam();
		Rectangle rect = new Rectangle(x, y, w, h);
		param.setSourceRegion(rect);
		BufferedImage bi = reader.read(0, param);
		ImageIO.write(bi, "png", new File(dest));

	}

	public static void makeTxtWithPng(String str4, String backImagePath,String outputPath)
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
		try {
			ImageIO.write(bimage, "png", new File(outputPath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ByteArrayOutputStream bs = new ByteArrayOutputStream();
		// ImageOutputStream imOut = ImageIO.createImageOutputStream(bs);
		// ImageIO.write(bimage, "png", imOut);
		// InputStream is = new ByteArrayInputStream(bs.toByteArray());
		// cutImage(is, "D://text.png", 0, 0, bimage.getWidth(),
		// (int) (fontHeigth1 * tempStrs.length + 10));
	}

	public static void makeTxtWithin(String str4,String outputPath) throws IOException {
		// 开始写txt的坐标
		int start_x = 0;
		int start_y = 0;

		// 右边距换行处理
		int margin_r = 0;
		// 首行添加两个空格
		str4 = "  " + str4;
		// Create a buffered image with transparency
		// 自定义大小图片

		BufferedImage bimage = new BufferedImage(200, 800,
				BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image Graphics2D graphics =
		Graphics2D graphics = bimage.createGraphics();

		// 生成白色的底色
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, bimage.getWidth(), bimage.getHeight());

		// 选择底图设置
		// BufferedImage bimage = ImageIO.read(new File(backImagePath));
		// Graphics2D graphics = bimage.createGraphics();

		// 设置打印字体（字体名称、样式和点大小）（字体名称可以是物理或者逻辑名称）
		// Java平台所定义的五种字体系列：Serif、SansSerif、Monospaced、Dialog 和 DialogInput
		Font font = new Font("宋体", Font.PLAIN, 20);
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
		cutImage(is, outputPath, 0, 0, bimage.getWidth(),
				(int) (fontHeigth1 * tempStrs.length + 10));
	}
}
