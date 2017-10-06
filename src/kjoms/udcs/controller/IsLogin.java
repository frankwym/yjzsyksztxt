package kjoms.udcs.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kjoms.udcs.service.OpenService2;
import kjoms.udcs.util.GetOpenService;

import com.google.gson.Gson;

/**
 * Created by msi on 2016/11/18.
 */
public class IsLogin extends HttpServlet {
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

		String token = request.getParameter("token");

		// 用户输入的名称
		String result = "";
		Gson gson = new Gson();
		OpenService2 openService = GetOpenService.getOpenService();
		String loginId = openService.getLoginIdByToken(token);
		// String s = HttpRequest2.sendGet(GetOpenService.GetIsLoginUrl(),
		// "token=" + token);
		// System.out.print(s);
		if (openService.isLogin(token, loginId)) {
			GetOpenService.token = token;
			GetOpenService.loginId = loginId.split(",")[0];
			GetOpenService.username = loginId.split(",")[1];
			GetOpenService.viewname = loginId.split(",")[2];
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("token", token);
			item.put("loginId", loginId);
			result = gson.toJson(item);
		} else {
			GetOpenService.token = null;
			GetOpenService.loginId = null;
		}
		PrintWriter out = response.getWriter();
		out.print(result);
		out.flush();
		out.close();
	}
}