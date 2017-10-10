package com.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kjoms.udcs.util.GetOpenService;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.dbconn.JdbcUtils;
import com.google.gson.Gson;
import com.util.POIExcelUtil;

public class UploadImage extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UploadImage() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter printWriter = response.getWriter();
		// 得到上传文件的保存目录，将上传的文件存放于WEB-INF目录下，不允许外界直接访问，保证上传文件的安全

		String filename = "";// 上传在临时目录下

		String userId = null;
		String mapName = null;

		String mapId = null;
		// 构建路径保存
		String MapId = null;
		String pathTemp = request.getServletContext().getRealPath("/")+"//resource//printMap//temporaryIMG//";
		String path = request.getServletContext().getRealPath("/")+"//resource//printMap//temporaryIMG//";
		// 消息提示
		String message = "";
		//获取当前时间
		Date date=new Date();
		  DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		  String time=format.format(date);
		try {
			// 使用Apache文件上传组件处理文件上传步骤：
			// 1、创建一个DiskFileItemFactory工厂
			DiskFileItemFactory factory = new DiskFileItemFactory();
			// 2、创建一个文件上传解析器
			ServletFileUpload upload = new ServletFileUpload(factory);
			// 解决上传文件名的中文乱码
			upload.setHeaderEncoding("UTF-8");
			// 3、判断提交上来的数据是否是上传表单的数据
			if (!ServletFileUpload.isMultipartContent(request)) {
				// 按照传统方式获取数据
				return;
			}
			// 4、使用ServletFileUpload解析器解析上传数据，解析结果返回的是一个List<FileItem>集合，每一个FileItem对应一个Form表单的输入项
			List<FileItem> list = upload.parseRequest(request);
			for (FileItem item : list) {
				// 如果fileitem中封装的是普通输入项的数据
				if (item.isFormField()) {
					String name = item.getFieldName();
					// 解决普通输入项的数据的中文乱码问题
					String value = item.getString("UTF-8");
					System.out.println(name + "--" + value);
					
					if ("mapName".equals(name)) {
						mapName = value;
					}
					if ("userId".equals(name)) {
						userId = value;
					}
					if (mapName != null && !"".equals(mapName)
							&& !"null".equals(mapName)&&userId != null && !"".equals(userId)
									&& !"null".equals(userId)) {
						
						
						path += userId+"_"+mapName+"_"+time;
						File file = new File(path);
						if (!file.exists()) {
							file.mkdirs();
						} else {
							file.mkdirs();
						}
						copyFile(pathTemp + filename, path);
						System.out.println(pathTemp + filename + "--" + path);
					}

					// value = new String(value.getBytes("iso8859-1"),"UTF-8");
				} else {// 如果fileitem中封装的是上传文件
						// 得到上传的文件名称，
					filename = System.currentTimeMillis() + item.getName();

					System.out.println(filename);
					if (filename == null || filename.trim().equals("")) {
						continue;
					}
					// 注意：不同的浏览器提交的文件名是不一样的，有些浏览器提交上来的文件名是带有路径的，如：
					// c:\a\b\1.txt，而有些只是单纯的文件名，如：1.txt
					// 处理获取到的上传文件的文件名的路径部分，只保留文件名部分
					// 获取item中的上传文件的输入流
					InputStream in = item.getInputStream();

					// 创建一个文件输出流
					FileOutputStream out = new FileOutputStream(pathTemp + "\\"
							+ filename);
					// 创建一个缓冲区
					byte buffer[] = new byte[1024];
					// 判断输入流中的数据是否已经读完的标识
					int len = 0;
					// 循环将输入流读入到缓冲区当中，(len=in.read(buffer))>0就表示in里面还有数据
					while ((len = in.read(buffer)) > 0) {
						// 使用FileOutputStream输出流将缓冲区的数据写入到指定的目录(savePath + "\\"
						// + filename)当中
						out.write(buffer, 0, len);
					}
					// 关闭输入流
					in.close();
					// 关闭输出流
					out.close();
					// 删除处理文件上传时生成的临时文件
					item.delete();
				}
			}
		} catch (Exception e) {
			message = "文件上传失败！";
			e.printStackTrace();
		}
		message = "./resource//printMap//temporaryIMG//"+userId+"_"+mapName+"_"+time+"//pic.png";
		message="{\"msg\":\""+message+"\"}";
		printWriter.write(message);
	}

	private static boolean copyFile(String srcPath, String destDir) {
		boolean flag = false;

		File srcFile = new File(srcPath);
		if (!srcFile.exists()) { // 源文件不存在
			System.out.println("源文件不存在");
			return false;
		}

		File destFile = new File(destDir + "//pic.png");

		try {
			FileInputStream fis = new FileInputStream(srcPath);
			FileOutputStream fos = new FileOutputStream(destFile);
			byte[] buf = new byte[1024];
			int c;
			while ((c = fis.read(buf)) != -1) {
				fos.write(buf, 0, c);
			}
			fis.close();
			fos.close();

			flag = true;
		} catch (IOException e) {
			//
		}

		if (flag) {
			System.out.println("复制文件成功!");
			srcFile.delete();
		}

		return flag;
	}

}