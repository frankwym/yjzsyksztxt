package com.manager.model;

public class Workspace {

    private String id;
    private String userId;//创建用户id
    private String workspaces;//工作集id
    private String workName;//工作集名称
    private String workDescribe;//工作集描述
    private String mapIds;//地图资源列表

    private String mapStatus;//地图状态

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWorkspaces() {
        return workspaces;
    }

    public void setWorkspaces(String workspaces) {
        this.workspaces = workspaces;
    }

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public String getWorkDescribe() {
        return workDescribe;
    }

    public void setWorkDescribe(String workDescribe) {
        this.workDescribe = workDescribe;
    }

    public String getMapIds() {
        return mapIds;
    }

    public void setMapIds(String mapIds) {
        this.mapIds = mapIds;
    }

    public String getMapStatus() {
        return mapStatus;
    }

    public void setMapStatus(String mapStatus) {
        this.mapStatus = mapStatus;
    }
}
