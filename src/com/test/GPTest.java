package com.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.util.HttpRequest;


//测试java请求GP服务
public class GPTest {

	static String gpServer = "http://192.168.0.160:6080/arcgis/rest/services/SuZhou/yl_dissolve_intersect/GPServer/ylModel";

	public static void main(String[] args) {
		GPTest gpTest=new GPTest();
		String url = gpServer + "/submitJob";
		String params = "Break_Values=50&bf1=12&bf2:=50&f=json";
		HttpRequest httpRequest = new HttpRequest();
		String result = httpRequest.sendGet(url, params);
		String jobId=gpTest.parseJob(result)[0];
		gpTest.submitJob(jobId);
	}

	

	//查询任务状态
	public void submitJob(String jobId) {
		String url = gpServer + "/jobs/" + jobId;
		String params = "f=json";
		HttpRequest httpRequest = new HttpRequest();
		String result = httpRequest.sendGet(url, params);
		String[] resStrings = parseJob(result);
		if ("esriJobSucceeded".equals(resStrings[1])) {
			System.out.println(showResult(jobId));
		} else {
			try {
				//减少请求次数
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			submitJob(jobId);
		}
	}

	//展示结果
	public String showResult(String jobId) {
		String url = gpServer + "/jobs/" + jobId
				+ "/results/ServiceAreas_Intersect";
		String params = "f=json";
		HttpRequest httpRequest = new HttpRequest();
		String result = httpRequest.sendGet(url, params);
		return result;
	}
	
	//解析任务状态json
	public String[] parseJob(String job) {
		// 将JsonObject数据转换为Json
		// {"jobId":"jd8e80967af4244f88cd2acc025c08d56","jobStatus":"esriJobSubmitted"}
		JSONObject object = JSON.parseObject(job);
		String jobId = object.get("jobId").toString();
		String jobStatus = object.get("jobStatus").toString();
		return new String[] { jobId, jobStatus };
	}
}