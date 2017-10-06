

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import sun.misc.BASE64Encoder;

/**
 * html2canvas 使用跨域图片的代理类， 将图片下载到本地供html2canvas使用
 * 
 * @author zhangss 2016-5-27 08:41:31
 * */
@Controller
@RequestMapping(value = "proxy")
public class ProxyAction {
	@RequestMapping(value="/proxy", method = RequestMethod.GET)
	public  void  getJwd(HttpServletRequest request,HttpServletResponse response){
		String url = request.getParameter("url");
		String callback = request.getParameter("callback");
		if(url != "" && callback != ""){
			try {
				URL urlInfo = new URL(url);
				if(urlInfo.getProtocol().equals("http") || urlInfo.getProtocol().equals("https")){
					HttpURLConnection conn = (HttpURLConnection) urlInfo.openConnection(); 
					String contentType = conn.getContentType();
					if(contentType.equals("image/png") || contentType.equals("image/jpg") || contentType.equals("image/jpeg") || contentType.equals("image/gif") || contentType.equals("text/html") || contentType.equals("application/xhtml")){
						if(request.getParameter("xhr2") != null){
							response.setHeader("Access-Control-Allow-Origin", "*");
							response.setContentType(contentType);
							DataInputStream input = new DataInputStream(conn.getInputStream()); 
							BufferedOutputStream bout = new BufferedOutputStream(response.getOutputStream());
							try {
						      byte b[] = new byte[1024];
						      int len = input.read(b);
						      while (len > 0) {
						        bout.write(b, 0, len);
						        len = input.read(b);
						      }
						    } catch (Exception e) {
						      e.printStackTrace();
						    } finally {
						      bout.close();
						      input.close();
						    }
						}else{
							response.setContentType("application/javascript");
							if(contentType.equals("text/html") || contentType.equals("application/xhtml")){
							}else{
								// 获取数据流生成byte字节
								DataInputStream input = new DataInputStream(conn.getInputStream());
								input.toString();
								byte[] buffer = new byte[1024 * 8]; 
								
								ByteArrayOutputStream bos = new ByteArrayOutputStream(1000); 
								byte[] b = new byte[1024 * 8]; 
								int n;  
					            while ((n = input.read(b)) != -1) {  
					                bos.write(b, 0, n);  
					            }  
					            input.close();  
					            bos.close(); 
					            buffer = bos.toByteArray();  
					            // 将byte转成base64
								BASE64Encoder encode = new BASE64Encoder(); 
								String base64data = encode.encode(buffer);
								base64data = base64data.replaceAll("\r\n","");
								response.getWriter().write(callback+"('"+ "data:" + contentType + ";base64," + base64data +"')");
							}
						}

 
					}
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	}
}
