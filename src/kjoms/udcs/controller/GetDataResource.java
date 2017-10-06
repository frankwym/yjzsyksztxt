package kjoms.udcs.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kjoms.udcs.bean.ResourceBean;
import kjoms.udcs.bean2.DataResource;
import kjoms.udcs.bean2.MapResource;
import kjoms.udcs.service.OpenService2;
import kjoms.udcs.util.GetOpenService;

import com.bean.nodes;
import com.google.gson.Gson;

public class GetDataResource extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doResponse(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doResponse(req, resp);
	}

	private void doResponse(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		// 设置全域
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setCharacterEncoding("utf-8");
		// 解析参数
		// 用户输入的名称
		String name = request.getParameter("name");
		String result = "";
		List<ResourceBean> resourceBeens = new ArrayList<>();
		List<DataResource> dataResources = new ArrayList<>();
		OpenService2 openService = GetOpenService.getOpenService();
		if (GetOpenService.loginId != null) {// 用户已经登录系统了 isLogin后再查询
			resourceBeens = openService.getResourceByLoginIdAndName(
					GetOpenService.loginId, name);
			if (resourceBeens == null) {
				resourceBeens = new ArrayList<>();
			}
			// 转换成我们自己的Bean
			for (int i = 0; i < resourceBeens.size(); i++) {
				ResourceBean resourceBean = resourceBeens.get(i);
				DataResource dataResource = new DataResource();
				dataResource.setName(resourceBean.getResourceName());
				dataResource.setId(resourceBean.getResourceId());
				dataResource.setUrl(resourceBean.getResourceUrl());
				if ("数据集服务".equals(resourceBean.getResourceType())) {// 需要注意一下
					dataResources.add(dataResource);
				}
			}
		}

		// 解析datasource name后转化为二级结点

		HashMap<String, List<nodes>> hashMap = new HashMap<>();// 转化为二级结点 去重处理
		for (DataResource tem : dataResources) {
			String[] nodeString = tem.getName().split("_");// 按照下划线分割
			if (nodeString.length < 2) {
				continue;
			}
			nodes node2 = new nodes();
			node2.setText(nodeString[1]);
			node2.setId(tem.getId());
			node2.setValue(tem.getUrl());
			String key = nodeString[0];
			if (hashMap.keySet().contains(key)) {
				hashMap.get(key).add(node2);
			} else {
				List<nodes> nodeList = new ArrayList<>();
				nodeList.add(node2);
				hashMap.put(key, nodeList);
			}
		}

		for (String key : hashMap.keySet()) {
			nodes node1 = new nodes();
			node1.setText(key);//
			
			nodes node11 = new nodes();
			node11.setText("指标集");//指标级
			List<nodes> node2 = hashMap.get(key);
			node11.setNodes((nodes[]) node2.toArray(new nodes[0]));//list转数组
			
			nodes node12 = new nodes();
			node11.setText("类别集");//指标级
			node1.setNodes(new nodes[]{node11,node12});
		}
		PrintWriter out = response.getWriter();
		out.print(result);
		out.flush();
		out.close();
	}

}
