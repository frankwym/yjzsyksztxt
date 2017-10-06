package kjoms.udcs.bean2;

/**
 * Created by msi on 2016/11/18.
 */
public class MapResource {
    private String id;
    private String mapName;
    private String mapType;// 0-底图  1-瓦片  2-动态  3-要素  4-图形  5-其他shp  6-图表 7-wmts 8-wms 服务
    private String mapUrl;

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

    public String getMapType() {
        return mapType;
    }

    public void setMapType(String mapType) {
        this.mapType = mapType;
    }

    public String getMapUrl() {
        return mapUrl;
    }

    public void setMapUrl(String mapUrl) {
        this.mapUrl = mapUrl;
    }
}
