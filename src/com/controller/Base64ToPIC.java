package com.controller;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import sun.misc.BASE64Decoder;
/**
 * Servlet implementation class Base64ToPIC
 */
@WebServlet("/Base64ToPIC")
public class Base64ToPIC extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Base64ToPIC() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		StringBuilder sb = new StringBuilder();
		try(BufferedReader reader = request.getReader();) {
		char[] buff = new char[1024];
		int len;
		 while((len = reader.read(buff)) != -1) {
		    sb.append(buff,0, len);
		    }
		}catch (IOException e) {
		     e.printStackTrace();
		}
		//String xx=request.getParameter("");
		String imgStr=sb.toString().substring(sb.toString().indexOf("base64,")+7);
		//JSONObject jobject = JSONObject.fromObject(sb.toString());
		//String requestTag = jobject.getString("requestTag");
		//对字节数组字符串进行Base64解码并生成图片  
		System.out.println(imgStr);
        if (imgStr == null) //图像数据为空  
            return ; 
        BASE64Decoder d = new BASE64Decoder();  
        byte[] bs = d.decodeBuffer(imgStr);  
        FileOutputStream os = new FileOutputStream("d://2221.png");  
        os.write(bs);  
        os.close();  
        /*BASE64Decoder decoder = new BASE64Decoder();  
        try   
        {  
            //Base64解码  	
            byte[] b = decoder.decodeBuffer(imgStr);  
            for(int i=0;i<b.length;++i)  
            {  
                if(b[i]<0)  
                {//调整异常数据  
                    b[i]+=256;  
                }  
            }  
            //生成jpeg图片  
            String imgFilePath = "d://222.png";//新生成的图片  
            OutputStream out = new FileOutputStream(imgFilePath);      
            out.write(b);  
            out.flush();  
            out.close();  
           
        }   
        catch (Exception e)   
        {  
            e.printStackTrace();
        }  */
	}

}
