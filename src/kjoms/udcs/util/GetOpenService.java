package kjoms.udcs.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import kjoms.udcs.service.OpenService2;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.zj.util.JUtil;

/**
 * Created by msi on 2016/11/16.
 */
public class GetOpenService {

    //令牌
    public static String token = null;

    //账号
    public static String loginId = null;
    
    //登录名
    public static String username = null;
    
    //昵称
    public static String viewname = null;
    
    public static OpenService2 getOpenService() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
                "classpath:/remote-client.xml");
        OpenService2 service = (OpenService2) applicationContext
                .getBean("remoteService");
        return service;
    }

    public static String GetArcServerUrl() {
        String path = JUtil.GetWebInfPath() + "//classes//client.properties";
        Properties prop = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream(path);
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            prop.load(in);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String arcServer = prop.getProperty("arc-server");
        return arcServer;
    }

    public static String GetaddResourceUrl() {
        String path = JUtil.GetWebInfPath() + "//classes//client.properties";
        Properties prop = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream(path);
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            prop.load(in);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String addResourceUrl = prop.getProperty("addResourceUrl");
        return addResourceUrl;
    }

    public static String GetIsLoginUrl() {
        String path = JUtil.GetWebInfPath() + "//classes//client.properties";
        Properties prop = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream(path);
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            prop.load(in);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String isloginUrl = prop.getProperty("isloginUrl");
        return isloginUrl;
    }


    public static String GetImageSave() {
        String path = JUtil.GetWebInfPath() + "//classes//client.properties";
        Properties prop = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream(path);
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
			prop.load(in);
		} catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String imageSave = prop.getProperty("imageSave");
        return imageSave;
    }
}
