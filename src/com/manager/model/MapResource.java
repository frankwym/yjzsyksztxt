package com.manager.model;

/**
 * Created by msi on 2016/9/28.
 */
public class MapResource {

    private String id;
    private String mapName;
    private String catalogId;//所属的目录
    private String restMapUrl;//ArcGIS 服务的URL
    private String themeData;//存的jsonData
    private String themeRender;//存的地图渲染信息
    private String mapType;//地图的类型
    private String mapDescribe;//地图描述

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    public String getRestMapUrl() {
        return restMapUrl;
    }

    public void setRestMapUrl(String restMapUrl) {
        this.restMapUrl = restMapUrl;
    }

    public String getThemeData() {
        return themeData;
    }

    public void setThemeData(String themeData) {
        this.themeData = themeData;
    }

    public String getThemeRender() {
        return themeRender;
    }

    public void setThemeRender(String themeRender) {
        this.themeRender = themeRender;
    }

    public String getMapType() {
        return mapType;
    }

    public void setMapType(String mapType) {
        this.mapType = mapType;
    }

    public String getMapDescribe() {
        return mapDescribe;
    }

    public void setMapDescribe(String mapDescribe) {
        this.mapDescribe = mapDescribe;
    }
}
