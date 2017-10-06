package kjoms.udcs.controller;

import com.google.gson.Gson;
import com.manager.model.Workspace;
import kjoms.udcs.bean.ZTZTBean;
import kjoms.udcs.service.OpenService2;
import kjoms.udcs.util.GetOpenService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by msi on 2016/11/25 0025.
 */
public class GetWorkSpace extends HttpServlet {
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
        Gson gson = new Gson();
        String loginId=request.getParameter("loginId");
        int row = Integer.parseInt(request.getParameter("row"));
        int page = Integer.parseInt(request.getParameter("page"));

        OpenService2 openService = GetOpenService.getOpenService();
        List<ZTZTBean> ztztBeanList = openService.getAllZTZTByPage(loginId, row, page);
        ArrayList<Object> arrayList = new ArrayList<>();
        for (int i = 0; i < ztztBeanList.size(); i++) {
            ZTZTBean ztztBean = ztztBeanList.get(i);
            Map<String, Object> resourceitem = new HashMap<String, Object>();
            resourceitem.put("id", ztztBean.getZTZT_ID());
            resourceitem.put("workspaces", ztztBean.getZTZT_ID());
            resourceitem.put("workName", ztztBean.getName());
            resourceitem.put("workDescribe", ztztBean.getName());
            resourceitem.put("mapIds", "/map/" + ztztBean.getZTZT_ID() + ".png");
            arrayList.add(resourceitem);
        }
        Map<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("total", openService.getCountOfZTZT(loginId));
        hashMap.put("rows", arrayList);
        result = gson.toJson(hashMap);
        PrintWriter out = response.getWriter();
        out.print(result);
        out.flush();
        out.close();
    }
}
