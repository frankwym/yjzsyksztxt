package com.manager.model;



public class Role {
	 private Integer id;
	 private String name;
	 private String description;
	 private String privilegeIds;
	 private Integer sort;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	public String getPrivilegeIds() {
		return privilegeIds;
	}
	public void setPrivilegeIds(String privilegeIds) {
		this.privilegeIds = privilegeIds;
	}
}
