package com.bean;

import com.alibaba.fastjson.JSON;

public class nodes {
	String text;
	String value;// 给子节点设置url
	String id;// 给子节点设置id 数据服务需要
	String[] tags;
	nodes[] nodes;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	public nodes[] getNodes() {
		return nodes;
	}

	public void setNodes(nodes[] nodes) {
		this.nodes = nodes;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public static void main(String[] args) {
		nodes nodes = new nodes();
		nodes[] nodes2 = new nodes[10];
		for (int i = 0; i < nodes2.length; i++) {
			nodes nodes3 = new nodes();
			nodes3.setText("第" + i + "个结点");
			nodes2[i] = nodes3;
		}
		nodes.setText("第一级别的结点");
		nodes.setNodes(nodes2);
		System.out.println(JSON.toJSON(nodes));
	}
}
