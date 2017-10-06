package kjoms.udcs.controller;

import kjoms.udcs.bean.ZTZTBean;
import kjoms.udcs.service.OpenService2;
import kjoms.udcs.util.GetOpenService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by msi on 2016/11/18.
 */
public class LoginTest extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doResponse(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doResponse(req, resp);
    }

    private void doResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //设置全域
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setCharacterEncoding("utf-8");
        //解析参数
        //用户输入的名称
        String result = "";
        OpenService2 openService = GetOpenService.getOpenService();
//        result = openService.login("0", "0");
        List<ZTZTBean> ztztBeanList = openService.getAllZTZT("0");
        System.out.print(ztztBeanList.size());
        GetOpenService.token = result;
        GetOpenService.loginId = openService.getLoginIdByToken(result);
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
        PrintWriter out = response.getWriter();
        out.print(result);
        out.flush();
        out.close();
    }
}
