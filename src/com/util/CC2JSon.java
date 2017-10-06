package com.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.bean.nodes;
import com.google.gson.Gson;
import com.sun.org.apache.bcel.internal.generic.NEW;

public class CC2JSon {

	static class CCBean {
		private String cc;
		private String cn;
		private String theme;

		public CCBean() {

		}

		public String getCc() {
			return cc;
		}

		public void setCc(String cc) {
			this.cc = cc;
		}

		public String getCn() {
			return cn;
		}

		public void setCn(String cn) {
			this.cn = cn;
		}

		public String getTheme() {
			return theme;
		}

		public void setTheme(String theme) {
			this.theme = theme;
		}

	}

	public static void readTxtFile(String filePath) {
		List<CCBean> ccList = new ArrayList<>();
		// 提取主题
		Set<String> set = new HashSet<String>();
		try {
			String encoding = "utf-8";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					CCBean ccBean = new CCBean();
					String[] line = lineTxt.split("	");

					ccBean.setCc(line[0]);
					ccBean.setCn(line[1]);
					if (line.length > 2) {
						ccBean.setTheme(line[2]);
						set.add(line[2]);
					} else {
						set.add("无主题");
					}
					ccList.add(ccBean);
				}
				read.close();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		list2node(ccList, set);
	}

	public static void list2node(List<CCBean> ccList, Set<String> themeSet) {
		nodes[] nodes = new nodes[themeSet.size()];
		int allindex = 0;
		for (Iterator it = themeSet.iterator(); it.hasNext();) {
			nodes nodes1 = new nodes();
			String theme = it.next().toString();
			nodes1.setText(theme);

			 String ccType = ccList.get(0).getCc().substring(0, 2);// 同一个一级类
			 String ccType1 = ccList.get(0).getCc().substring(0, 3);// 同一个二级类
//			String ccType = "";// 同一个一级类
//			String ccType1 = "";// 同一个二级类
			nodes[] nodescc1 = new nodes[10];
			nodes[] nodescc2 = new nodes[10];
			nodes[] nodescc3 = new nodes[20];
			int cc1 = 0;
			int cc2 = 0;
			int cc3 = 0;
			for (CCBean ccBean : ccList) {
				if (!theme.equals(ccBean.getTheme())) {
					continue;
				}
				String cc = ccBean.getCc();

				if (cc.endsWith("0")) {
					if (cc.endsWith("00")) {// 一级
						nodes temp1 = new nodes();
						temp1.setText(ccBean.getCn());
						nodescc1[cc1++] = temp1;
					} else {// 二级
						nodes temp2 = new nodes();
						temp2.setText(ccBean.getCn());
						nodescc2[cc2] = temp2;

					}
				} else {// 三级
					nodes temp3 = new nodes();
					temp3.setText(ccBean.getCn());
					nodescc3[cc3++] = temp3;
				}
				// System.out.println(ccBean.getCn());
				if (cc.substring(0, 2).equals(ccType)) {// 假如在一个cc大类中
					if (cc.substring(0, 3).equals(ccType1)) {
						
					} else {
						nodescc2[cc2].setNodes(nodescc3);
						cc2++;
						nodescc3 = new nodes[10];
						cc3 = 0;
						ccType1 = cc.substring(0, 3);
					}
				} else {
					// 总结分级
					nodescc1[cc1-2].setNodes(nodescc2);
					
					nodescc2 = new nodes[10];
					cc2 = 0;
					ccType = cc.substring(0, 2);
				}
			}
			nodes1.setNodes(nodescc1);
			nodes[allindex++] = nodes1;
		}
		System.out.println(new Gson().toJson(nodes));
	}

	public static void main(String[] args) {
		String filePath = "d:\\cc.txt";
		readTxtFile(filePath);

	}
}
