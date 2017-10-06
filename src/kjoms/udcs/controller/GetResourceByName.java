package kjoms.udcs.controller;

import com.google.gson.Gson;
import kjoms.udcs.bean.ResourceBean;
import kjoms.udcs.bean2.MapResource;
import kjoms.udcs.service.OpenService2;
import kjoms.udcs.util.GetOpenService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by msi on 2016/11/16.
 */
public class GetResourceByName extends HttpServlet {
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
        String name = request.getParameter("name");
        String result = "";
        List<ResourceBean> resourceBeens = new ArrayList<>();
        List<MapResource> mapResources = new ArrayList<>();
        OpenService2 openService = GetOpenService.getOpenService();
        if (GetOpenService.loginId != null) {
            resourceBeens = openService.getResourceByLoginIdAndName(GetOpenService.loginId, name);
            if (resourceBeens == null) {
                resourceBeens = new ArrayList<>();
            }
            //转换成我们自己的Bean
            for (int i = 0; i < resourceBeens.size(); i++) {
                ResourceBean resourceBean = resourceBeens.get(i);
                MapResource mapResource = new MapResource();
                switch (resourceBean.getResourceType()) {
                    case "Arcgis切片服务":
                        mapResource.setMapType("1");
                        break;
                    case "Arcgis动态服务":
                        mapResource.setMapType("2");
                        break;
                    //先针对arcgis 的服务来
//                    case "WMS服务":
//                        mapResource.setMapType("7");
//                        break;
//                    case "WMTS服务":
//                        mapResource.setMapType("8");
//                        break;
                }
                Random random = new Random();
                String ArcServerUrl = GetOpenService.GetArcServerUrl();
                mapResource.setMapName(resourceBean.getResourceName());
                mapResource.setMapUrl(ArcServerUrl + resourceBean.getResourceUrl());
                mapResource.setId(random.nextDouble() + "");
                if (mapResource.getMapType() != null) {
                    mapResources.add(mapResource);
                }
            }
        }
        try {
            Gson gson = new Gson();
            result = gson.toJson(mapResources);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PrintWriter out = response.getWriter();
        out.print(result);
        out.flush();
        out.close();
    }
}
