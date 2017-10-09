/*全局变量声明*/
var SAVE_INDEX=0;// 记录定制地图保存位置，0代表新建时与数据库查重，1代表粗加工保存，2代表精加工保存
// global variables,record all variables in customizeMap
var CutomizeMapRecords = {
	userName : "default",// 用户名
	mapName:"",// 地图名称
	mapImage:"",// /地图图片的地址
	mapDescribe:"",// 地图描述
	mapScopeStr:"",
	disasterType:"",
	mapType:"",
	mapUnitName:"",
	mapUnitTel:"",
	dataType : "",// 数据类型
	dataNodeId : "",// 选择的nodeId
	dataName : "",// 数据名称
	resourceURL:"",// 资源链接
	geoUnit : "district",// 制图单位 “市区”、“街道”
	classificationIndex : "",// 分级字段
	statisticalIndex : "",// 统计字段
	baseMapName : "暗黑系列1",
	baseMapUrl : "http://server.arcgisonline.com/arcgis/rest/services/ESRI_Imagery_World_2D/MapServer",
	mapFormatType : "defaultFormatTree",
	mapFormatContent:"4,1003,10203,4,1003,10203",
	mapFormatName : "红色色系火炬型模板",
	mapFormatNodeId : 1,
	gradeColors:"",// 分级色带值 只有精加工才有，
	colorId:"",
	colorOrder:"",
	lineColor:{r:194,g:194,b:194,a:1},
	lineWidth:1,
	statisticalData:"",
	polish_StatisticChartId:"",
	polish_StatisticColors:"",
	polish_StatisticWidth:"",
	thematicMapArea:"null"
};

// global variables,represent the map container
var mapContainer;
// 侧边栏是否收缩
var sidebarHide = false;
// 当前所在步骤数
var currentStep = 1;
//当前绘制图形是否为地图范围
isDrawMapScope=false;
// 地图图层
var baseMapServiceLayer;
// 统计图图层
var statisticLayer;
// 分级图图层
var classLayer;
//标绘图层
var drawLayer;
//地图引擎对象 
var geoEngine;
//扩展工具1
var Extension={DrawEx:null};
//扩展工具2
var plotDraw={DrawExt:null};
//缩放工具
var navToolBar;

//编辑饼状图
var editChart;

var dataLayer;

//编辑模板控制
var isEditForm;
/* 页面加载完触发的函数 */
$(function() {
	 // checkLogin();
	CutomizeMapRecords.userName= window.localStorage.getItem("username");
	$(".scroll-content").mCustomScrollbar();
	buildInterfaceOfSelectType();
	$("#newMapModal").modal("show");
	require(
			[ "mapDefinition/TDTLayer","esri/map", "esri/layers/ArcGISDynamicMapServiceLayer", "esri/dijit/Scalebar",
					"esri/layers/GraphicsLayer" ,"esri/dijit/Print","esri/dijit/editing/TemplatePicker","esri/toolbars/navigation","esri/toolbars/draw","esri/geometry/geometryEngine","drawExtension/Extension/DrawEx","drawExtension/plotDraw/DrawExt","esri/graphic"],
			function(TDTLayer,Map, ArcGISDynamicMapServiceLayer,Scalebar, GraphicsLayer,Print,TemplatePicker,Navigation,Draw,geometryEngine,DrawEx,DrawExt,Graphic) {
				esriConfig.defaults.io.proxyUrl = "http://localhost:8080/TJCH_DLGQ/proxy.jsp";
	            console.log(esriConfig.defaults.io.proxyUrl);
	            esriConfig.defaults.io.alwaysUseProxy = false;
				var lods=[
					 { "level": 2, "resolution": 0.3515625, "scale": 147748796.52937502 },  
                     { "level": 3, "resolution": 0.17578125, "scale": 73874398.264687508 },  
                     { "level": 4, "resolution": 0.087890625, "scale": 36937199.132343754 },  
                     { "level": 5, "resolution": 0.0439453125, "scale": 18468599.566171877 },  
                     { "level": 6, "resolution": 0.02197265625, "scale": 9234299.7830859385 },  
                     { "level": 7, "resolution": 0.010986328125, "scale": 4617149.8915429693 },  
                     { "level": 8, "resolution": 0.0054931640625, "scale": 2308574.9457714846 },  
                     { "level": 9, "resolution": 0.00274658203125, "scale": 1154287.4728857423 },  
                     { "level": 10, "resolution": 0.001373291015625, "scale": 577143.73644287116 },  
                     { "level": 11, "resolution": 0.0006866455078125, "scale": 288571.86822143558 },  
                     { "level": 12, "resolution": 0.00034332275390625, "scale": 144285.93411071779 },  
                     { "level": 13, "resolution": 0.000171661376953125, "scale": 72142.967055358895 },  
                     { "level": 14, "resolution": 8.58306884765625e-005, "scale": 36071.483527679447 },  
                     { "level": 15, "resolution": 4.291534423828125e-005, "scale": 18035.741763839724 },  
                     { "level": 16, "resolution": 2.1457672119140625e-005, "scale": 9017.8708819198619 },  
                     { "level": 17, "resolution": 1.0728836059570313e-005, "scale": 4508.9354409599309 },  
                     { "level": 18, "resolution": 5.3644180297851563e-006, "scale": 2254.4677204799655 }  
				];
				mapContainer = new Map("myMap",{logo:false});
				mapContainer.on("mouse-move",function(e){
					console.log(e.mapPoint.x+"____"+e.mapPoint.y);
				});
				statisticLayer = new GraphicsLayer();
				classLayer=new GraphicsLayer();
				drawLayer=new GraphicsLayer();
				baseMapServiceLayer = new TDTLayer(
						"vec");
				
				/* dataLayer= new ArcGISDynamicMapServiceLayer(
					"http://localhost:6080/arcgis/rest/services/底图配图/MapServer");*/
				mapContainer.addLayer(baseMapServiceLayer);
				mapContainer.removeLayer(baseMapServiceLayer);
				baseMapServiceLayer = new ArcGISDynamicMapServiceLayer(
				"http://localhost:6080/arcgis/rest/services/底图配图111/MapServer");
				mapContainer.addLayer(baseMapServiceLayer);
				mapContainer.addLayer(classLayer);	
				mapContainer.addLayer(statisticLayer);	
				mapContainer.addLayer(drawLayer);	
				//mapContainer.addLayer(dataLayer);	
				mapContainer.centerAndZoom({"x": 117.333103, "y": 39.294706, "spatialReference":mapContainer.spatialReference },5);
				
				//邦定缩放工具
				navToolBar=new Navigation(mapContainer);
				$("#zoom-in").click(function(){
					navToolBar.activate(Navigation.ZOOM_IN);
				});
				$("#zoom-out").click(function(){
					navToolBar.activate(Navigation.ZOOM_OUT);
				});
				$("#pan").click(function(){
					navToolBar.activate(Navigation.PAN);
				});
				$("#overall").click(function(){
					mapContainer.centerAndZoom({"x": 117.333103, "y": 39.294706, "spatialReference":mapContainer.spatialReference },5);
				});
				var scalebar = new Scalebar({
			          map: mapContainer,
			          // "dual" displays both miles and kilometers
			          // "english" is the default, which displays miles
			          // use "metric" for kilometers
			          scalebarUnit: "dual"
			        });
			    // 扩展工具1
				Extension.DrawEx=DrawEx;
				// 扩展工具2
				plotDraw.DrawExt=DrawExt;
				// 涂鸦工具初始化
				drawing = drawingTool(mapContainer,drawLayer,"tuyapane");
				drawing.initEdit();
				// 量算工具初始化
				geoEngine = geometryEngine;
				measure = measureSimpleTool(mapContainer);
				measure.measureTool(drawing.tool);
				//隐藏标绘栏
				$('#tuyapane').css({"display":"none"});
				//添加其它选择栏
				$('#tuyapane').append(htmltuya1);
				$('#tuyapane').append(htmltuya2);
				$('#graphicTemplateDiv').css({"width": 228,
				    "overflow": "auto",
				    "border":" 1px solid #448aca"
				});
				//不透明度滑动条
				var slider = new Slider('#ex1', {
					formatter: function(value) {
						return value;
					}
				});
				var slider = new Slider('#ex2', {
					formatter: function(value) {
						return value;
					}
				});
				$('#ex1Slider').mouseover(function(){
					$('#tuyapane').draggable("disable");
				});
				$('#ex1Slider').mouseout(function(){
					$('#tuyapane').draggable("enable");
				});
				$('#ex2Slider').mouseover(function(){
					$('#tuyapane').draggable("disable");
				});
				$('#ex2Slider').mouseout(function(){
					$('#tuyapane').draggable("enable");
				});
			});
});

/* 判断是否登录 */
function checkLogin(){
	   // 从url中获取参数
    function getURLParamsString(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) return unescape(r[2]);
        return null;
    }

    function isLogin(token) {
        $.ajax({
            url: './IsLogin',
            type: 'POST',
            data: {
                token: token
            },
            success: function (res) {
                if (res != "") {
                    res=eval("("+res+")")
                    window.localStorage.setItem("token", res.token); 
                    window.localStorage.setItem("loginId", res.loginId.split(",")[0]);
                    window.localStorage.setItem("username", res.loginId.split(",")[1]);
                    window.localStorage.setItem("viewname", res.loginId.split(",")[2]);
                } else {
                     window.location.href = LOGIN_URL;
                }
            },
            error: function (res) {
                window.location.href = LOGIN_URL;
                alert("发生错误");	
            }
        });
    }

    var token = getURLParamsString("token");
    var username = getURLParamsString("username");

    if (token != null) {
        isLogin(token);
    } else {
        window.location.href = LOGIN_URL;
    }

}
/* 侧边栏收缩动画 */
$(".toggle-btn").click(function() {
	if (!sidebarHide) {
		sidebarHide = true;
		$(".sidebar").addClass('sidebar-hide');
		$(".main-section").css('left', '0');
	} else {
		sidebarHide = false;
		$(".sidebar").removeClass('sidebar-hide');
		$(".main-section").css('left', '290px');
	}
});

/* 步骤条切换函数 */
function stepShift() {
	$(".steps div").removeClass('step-active').removeClass('step-visited');
	switch (currentStep) {
		case 1:
			$(".steps div:eq(0)").addClass('step-active');
			buildInterfaceOfSelectType();
			break;
		case 2:
			$(".steps div:eq(1)").addClass('step-active');
			buildInterfaceOfSelectMap();
			
			
			break;
		case 3:
			$(".steps div:eq(2)").addClass('step-active');
			buildInterfaceOfSelectData();
			
			break;
		case 4:
			$(".steps div:eq(3)").addClass('step-active');
			buildInterfaceOfSelectFormat();
		
			break;
		case 5:
			$(".steps div:eq(4)").addClass('step-active');
			
			break;
		case 6:
			$(".steps div:eq(5)").addClass('step-active');
			progressCtr("show");
			makeThematicMap();
		}
	for ( var i = 0; i < currentStep - 1; i++) {
		$(".steps div:eq(" + i + ")").addClass('step-visited');
	}
}
$("#pre-step").click(function() {

	if (currentStep > 1 && currentStep < 7) {
		currentStep--;
		stepShift();
	}
});
$("#next-step").click(function() {	
	if (currentStep > 0 && currentStep < 6) {
		currentStep++;
		stepShift();
	}
});

/* 动态添加色带选项 */
// 色带图片json
var colorImages = [ {
	"value" : "1001",
	"url" : "images/10/1.jpg",
	"name" : "黄红色系"
}, {
	"value" : "1002",
	"url" : "images/10/2.jpg",
	"name" : "蓝色色系"
}, {
	"value" : "1003",
	"url" : "images/10/3.jpg",
	"name" : "红色色系"
}, {
	"value" : "1004",
	"url" : "images/10/4.jpg",
	"name" : "黄绿色系"
}, {
	"value" : "1005",
	"url" : "images/10/5.jpg",
	"name" : "黄棕色系"
}, {
	"value" : "1006",
	"url" : "images/10/6.jpg",
	"name" : "棕蓝色系"
}, {
	"value" : "1007",
	"url" : "images/10/7.jpg",
	"name" : "青黄色系"
}, {
	"value" : "1008",
	"url" : "images/10/8.jpg",
	"name" : "棕青色系"
}, {
	"value" : "1009",
	"url" : "images/10/9.jpg",
	"name" : "紫红色系"
}, {
	"value" : "1010",
	"url" : "images/10/10.jpg",
	"name" : "青色色系"
} ];
// 创建色带li元素
for ( var i = 0; i < colorImages.length; i++) {
	var colorOption = "<li ><img src=" + colorImages[i].url + " alt="
			+ colorImages[i].value + "><span>" + colorImages[i].name
			+ "</span></li>";
	$(".color-tab-content ul").append(colorOption);
}

/* 动态添加图表符号选项 */
var chartImages = [ {
	"value" : "10101",
	"url" : "images/chartIcon/010101.png"
}, {
	"value" : "10102",
	"url" : "images/chartIcon/010102.png"
}, {
	"value" : "10103",
	"url" : "images/chartIcon/010103.png"
}, {
	"value" : "10104",
	"url" : "images/chartIcon/010104.png"
}, {
	"value" : "10105",
	"url" : "images/chartIcon/010105.png"
}, {
	"value" : "10106",
	"url" : "images/chartIcon/010106.png"
}, {
	"value" : "10107",
	"url" : "images/chartIcon/010107.png"
},{
	"value" : "10109",
	"url" : "images/chartIcon/010109.png"
}, {
	"value" : "10110",
	"url" : "images/chartIcon/010110.png"
}, {
	"value" : "10111",
	"url" : "images/chartIcon/010111.png"
}, {
	"value" : "10201",
	"url" : "images/chartIcon/010201.png"
}, {
	"value" : "10202",
	"url" : "images/chartIcon/010202.png"
}, {
	"value" : "10203",
	"url" : "images/chartIcon/010203.png"
}, {
	"value" : "10204",
	"url" : "images/chartIcon/010204.png"
}, {
	"value" : "20101",
	"url" : "images/chartIcon/020101.png"
}, {
	"value" : "20102",
	"url" : "images/chartIcon/020102.png"
}, {
	"value" : "20201",
	"url" : "images/chartIcon/020201.png"
}, {
	"value" : "20202",
	"url" : "images/chartIcon/020202.png"
}, {
	"value" : "20203",
	"url" : "images/chartIcon/020203.png"
} ];
// 创建图表li元素
for ( var i = 0; i < chartImages.length; i++) {
	var chartOption = "<li><img src=" + chartImages[i].url + " alt="
			+ chartImages[i].value + "></span></li>";
	$(".chart-tab-content ul").append(chartOption);
}

// 给每个li元素绑定事件
$(".option-container li").click(
		function(e) {
			var currentImageUrl = $(this).find("img").attr('src');
			var currentImageVal = $(this).find("img").attr('alt');
			$(this).parents(".option-container").siblings().find(
					".selected-container").attr('src', currentImageUrl);
			$(this).parents(".option-container").siblings().find(
					".selected-container").attr('alt', currentImageVal);
			$(".option-container").find("img").removeClass("selected-image");
			$(this).find("img").addClass("selected-image");
		});

//构建灾害类型选择页面
function buildInterfaceOfSelectType() {
	
	$("#data-content").empty();
	//从json中填充灾害种类
	$("#data-content").append("<div id=\"disasterType\" style=\"margin:0 20px\" ></div>");
	$("#disasterType").append("<label >灾害种类：</label>");
	$("#disasterType").append("<div class=\"col-sm-12\"><select id=\"disasterTypeSelection\"  class=\"selectpicker show-menu-arrow form-control\" multiple data-max-options=\"1\"></select></div>");
	$("#data-content").append("<br/><br/><div id=\"mapType\" style=\"margin:20px\" ></div>");
	$("#mapType").append("<label >图件种类：</label>");
	$("#mapType").append("<div class=\"col-sm-12\"><select id=\"mapTypeSelection\" class=\"selectpicker show-menu-arrow form-control\" multiple data-max-options=\"1\"></select></div>");
	$("#data-content").append("<div id=\"mapScopeType\" style=\"margin:0 20px\" ></div>");
	$("#mapScopeType").append("<br/><br/><label >制图范围：</label>");
	$("#mapScopeType").append("<div style=\"float:right\" class=\"col-sm-7\"><select id=\"mapScopeSelection\" class=\"selectpicker show-menu-arrow form-control\" multiple data-max-options=\"1\"></select></div>");
	$("#mapScopeType").append("<br/><br/><textarea id=\"mapScopeStr\" style=\"width:100%;height:100px\"></textarea>");
	/*$("#mapScopeType").append("<br/><button id=\"strToMapScope\" style=\"float:right;height:30px\" class=\"btn\">确定</button>");*/
	$("#data-content").append("<div id=\"mapMatedata\" style=\"margin:0 20px\" ></div>");
	$("#mapMatedata").append("<br/><label >单位名称：</label>");
	$("#mapMatedata").append("<br/><textarea id=\"mapUnitName\" style=\"width:100%;height:35px\"></textarea>");
	$("#mapMatedata").append("<br/><br/><label >联系方式：</label>");
	$("#mapMatedata").append("<br/><textarea id=\"mapUnitTel\" style=\"width:100%;height:35px\"></textarea>");
	$("#data-content").append("<br/><div style=\"margin:0 20px\" ><button id=\"submitDisasterTpye\" style=\"width:100%\"  class=\"btn btn-success\">确定</button></div>");
	//当灾害选择框变化时，图种选择框对应变化
	$("#disasterTypeSelection").on('change',function(){
		var selectText = $(this).find('option:selected').text();
		$.getJSON("resource/disasterType.json",
				function(data) {
			$("#mapTypeSelection").empty();
			var diaster=data;
			for(var i=0;i<diaster.length;i++){
				if(diaster[i].text==selectText){
					for(var j=0;j<diaster[i].nodes.length;j++){
						if(j==0){
							$("#mapTypeSelection").append(
									"<option selected>" +diaster[i].nodes[j].text+ "</option>");
						}else{
							$("#mapTypeSelection").append(
									"<option>" +diaster[i].nodes[j].text+ "</option>");
						}
						
					}
					$("#mapTypeSelection").selectpicker("refresh");}
			}
			
		});
	});
//填充灾害种类下拉菜单 
	$.getJSON("resource/disasterType.json",
			function(data) {
		$("#disasterTypeSelection").empty();
		var diaster=data;
		for(var i=0;i<diaster.length;i++){
			if(i==0){
				$("#disasterTypeSelection").append(
						"<option selected>" +diaster[i].text+ "</option>");
			}else{
				$("#disasterTypeSelection").append(
						"<option>" +diaster[i].text+ "</option>");
			}
			
		}
		$("#disasterTypeSelection").selectpicker("refresh");
		//如果灾害种类已经选择过，还原设置
		if(CutomizeMapRecords.disasterType!=""){
			$("#disasterTypeSelection").find("option").each(function(){  
	            $(this).removeAttr("selected");  
	        });  
	        $("#disasterTypeSelection").find("option").each(function(){  
	            if(CutomizeMapRecords.disasterType == $(this).val()){  
	               $(this).attr("selected","selected");  
	               return false;  
	            }  
	        });  
		}
		$("#disasterTypeSelection").change();
		$("#mapScopeStr").val(CutomizeMapRecords.mapScopeStr);
		$("#mapUnitName").val(CutomizeMapRecords.mapUnitName);
		$("#mapUnitTel").val(CutomizeMapRecords.mapUnitTel);
	});
	
//填充制图范围种类下拉菜单 	
	var mapScope=["手动输入","矩形选择","圆形选择","多边形选择"];
	for(var i=0;i<mapScope.length;i++){
		if(i==0){
			$("#mapScopeSelection").append(
					"<option selected>" +mapScope[i]+ "</option>");
		}else{
			$("#mapScopeSelection").append(
					"<option>" +mapScope[i]+ "</option>");
		}
		
	}
	$("#mapScopeSelection").selectpicker("refresh");
	$("#mapScopeSelection").on('change',function(){
		isDrawMapScope=true;
		$("#mapScopeStr").val("");
		var selectText = $(this).find('option:selected').text();
		if(selectText=="手动输入"){
			$("#mapScopeStr").attr("disabled",false);			
		}
		if(selectText=="矩形选择"){
			$("#mapScopeStr").attr("disabled","disabled");
			drawing.toolbar.activate("rectangle");
		}
		if(selectText=="圆形选择"){
			$("#mapScopeStr").attr("disabled","disabled");
			drawing.toolbar.activate("circle");
		}
		if(selectText=="多边形选择"){
			$("#mapScopeStr").attr("disabled","disabled");
			drawing.toolbar.activate("freehandpolygon");
		}
	});
	$("#submitDisasterTpye").on("click",function(){
		require(["esri/geometry/jsonUtils"], function(geometryJsonUtils) { 
		try{
			
				var mapScopeStr=$("#mapScopeStr").val();
				var mapUnitName=$("#mapUnitName").val();
				var  mapUnitTel=$("#mapUnitTel").val();
				var disasterType=$("#disasterTypeSelection option:selected").val();
				var mapType=$("#mapTypeSelection option:selected").val();
				if(mapScopeStr==""){
					alert("请补充完整信息");
					return;
				}
				
				var mapScope=geometryJsonUtils.fromJson(JSON.parse(mapScopeStr));
				mapContainer.setExtent(mapScope.getExtent());
				CutomizeMapRecords.mapScopeStr=mapScopeStr;
				CutomizeMapRecords.mapUnitName=mapUnitName;
				CutomizeMapRecords.mapUnitTel=mapUnitTel;
				CutomizeMapRecords.disasterType=disasterType;
				CutomizeMapRecords.mapType=mapType;
			
		}catch(e){
			alert(e.message);
		}
		$("#next-step").click();
		});
	}); 
}

function buildInterfaceOfSelectData() {
	$("#data-content").empty();
	$("#data-content").empty();
	$("#data-content").append("<ul id=\"mayTab\" class=\"nav nav-tabs\"><li class=\"active\"><a href=\"#statisticDatasTab\" data-toggle=\"tab\">统计型数据"+
			 "</a></li><li><a href=\"#distributedDatasTab\" data-toggle=\"tab\">分布型数据</a></li></ul>");
	$("#data-content").append('<div class="tab-content">'+
			'<div class="tab-pane fade in active" id="statisticDatasTab"></div>'+
			'<div class="tab-pane fade" id="distributedDatasTab"></div></div>');
	$("#statisticDatasTab").append("<div id='mapPanel1' class=\"panel\"></div>");
	$("#mapPanel1").append('<div id="statisticDatasTree" class="panel-body"></div>');
	$("#distributedDatasTab").append("<div id='mapPanel2' class=\"panel\"></div>");
	$("#mapPanel2").append('<div id="distributedDatasTree" class="panel-body"></div>');
	$('#distributedDatasTree').treeview(
			{
				data : [{"text":"地震数据","value":"http://localhost:6080/arcgis/rest/services/地震数据/MapServer"},{"text":"洪涝数据","value":"http://localhost:6080/arcgis/rest/services/%E6%B4%AA%E6%B6%9D%E6%95%B0%E6%8D%AE/MapServer"}],
				onNodeSelected : function(event, data) {
					require([
						  "esri/layers/ArcGISDynamicMapServiceLayer"
						], function(ArcGISDynamicMapServiceLayer ) {
						  var layer = new ArcGISDynamicMapServiceLayer(
						   data.value ,{
						      useMapImage: true
						    }
						  );
						  try{
							  mapContainer.removeLayer(dataLayer);	
						  }catch(e){
							  
						  }
						  
						  dataLayer=layer;
						  mapContainer.addLayer(dataLayer);	
						});
				}});
	$.ajax({
		type : "post",
		url : "./getStatisticDataList",
		data:{disasterType:CutomizeMapRecords.disasterType,mapType:CutomizeMapRecords.mapType},
		success : function(res) {
			var dataList=res.trim();
			$('#statisticDatasTree').treeview(
					{
						data : eval("(" + dataList + ")"),
						onNodeSelected : function(event, data) {
							CutomizeMapRecords.dataType="UpdataDatatree";
							var selectedVal=data.value;
							var allData=$('#statisticDatasTree').treeview('getEnabled', data.nodeId);
							$("#statisticalTypeDiv").css("display","none");
							$("#mapUnit").css("display","none");
							$("#classificationIndex").empty();
							$("#statisticalIndex").empty();
							$("#thematicMapArea").empty();
							for ( var i = 0; i < allData.length; i++) {
								$("#classificationIndex").append(
										"<option>" + allData[i].text + "</option>");
								$("#statisticalIndex").append(
										"<option>" + allData[i].text + "</option>");
							}
							$.ajax({
								type : "post",
								url : "./getRegionCode",
								data:{Type:"REGION_DISTRIC"	},
								success : function(res) {
									var thisAreaJson=res.trim().split(",");
									for(var i=0;i<thisAreaJson.length;i++){
										$("#thematicMapArea").append(
												"<option selected=\"selected\">" + thisAreaJson[i] + "</option>");
									}
									$("#thematicMapArea").selectpicker("refresh");
								}});
							
							$("#classificationIndex").selectpicker("refresh");
							$("#statisticalIndex").selectpicker("refresh");
							
							$("#MapDataIndexModal").modal("show");
						}
						});
		}});
}
/* build the interface of selectData */
function buildInterfaceOfSelectData1() {
	$("#data-content").empty();
	$("#data-content").append("<div id=\"TrafficDatatree\" ></div>");
	$("#data-content").append("<div id=\"FloodDatatree\" ></div>");
	
	$("#data-content").append("<div id=\"EarthquakeDatatree\" ></div>");
	$("#data-content").append("<div id=\"FireDatatree\" ></div>");
	$("#data-content").append("<div id=\"UpdataDatatree\" ></div>");
	$.ajax({
		type : "post",
		url : "./getCustomizeMapDataList",
		data:{loginId: window.localStorage.getItem("loginId"),username:window.localStorage.getItem("username")},
		success : function(res) {
			// build dataTree
			var treeArr = res.split("s-p-l-i-t");
			//地震数据
			$('#EarthquakeDatatree').treeview(
					{
						data : eval("(" + treeArr[2] + ")"),
						onNodeSelected : function(event, data) {
							var selected = $('#FloodDatatree').treeview(
									'getSelected');
							if (selected.length > 0)
								$('#FloodDatatree').treeview(
										'unselectNode', [ selected[0].nodeId, {
											silent : true
										} ]);
							selected = $('#UpdataDatatree').treeview(
									'getSelected');
							if (selected.length > 0)
								$('#UpdataDatatree').treeview('unselectNode',
										[ selected[0].nodeId, {
											silent : true
										} ]);
							selected = $('#TrafficDatatree').treeview(
							'getSelected');
					if (selected.length > 0)
						$('#TrafficDatatree').treeview('unselectNode',
								[ selected[0].nodeId, {
									silent : true
								} ]);
							buildMapDataIndexModal("EarthquakeDatatree",data);
						}
					});
			//火灾数据
			$('#FireDatatree').treeview(
					{
						data : eval("(" + treeArr[1] + ")"),
						onNodeSelected : function(event, data) {
							var selected = $('#FloodDatatree').treeview(
									'getSelected');
							if (selected.length > 0)
								$('#FloodDatatree').treeview(
										'unselectNode', [ selected[0].nodeId, {
											silent : true
										} ]);
							selected = $('#UpdataDatatree').treeview(
									'getSelected');
							if (selected.length > 0)
								$('#UpdataDatatree').treeview('unselectNode',
										[ selected[0].nodeId, {
											silent : true
										} ]);
							selected = $('#TrafficDatatree').treeview(
							'getSelected');
					if (selected.length > 0)
						$('#TrafficDatatree').treeview('unselectNode',
								[ selected[0].nodeId, {
									silent : true
								} ]);
							buildMapDataIndexModal("FireDatatree",data);
						}
					});
			//交通事故数据
			$('#TrafficDatatree').treeview(
					{
						data : eval("(" + treeArr[0] + ")"),
						showTags : true,
						onNodeSelected : function(event, data) {
							var selected = $('#FloodDatatree').treeview(
									'getSelected');
							if (selected.length > 0)
								$('#FloodDatatree').treeview(
										'unselectNode', [ selected[0].nodeId, {
											silent : true
										} ]);
							selected = $('#UpdataDatatree').treeview(
									'getSelected');
							if (selected.length > 0)
								$('#UpdataDatatree').treeview('unselectNode',
										[ selected[0].nodeId, {
											silent : true
										} ]);
							selected = $('#FireDatatree').treeview(
							'getSelected');
					if (selected.length > 0)
						$('#FireDatatree').treeview('unselectNode',
								[ selected[0].nodeId, {
									silent : true
								} ]);
							buildMapDataIndexModal("TrafficDatatree",data);
						}
					});
			
		
			//水灾数据
			$('#FloodDatatree').treeview(
					{
						data :eval("(" + treeArr[4] + ")"),
						onNodeSelected : function(event, data) {
							var selected = $('#FireDatatree').treeview(
									'getSelected');
							if (selected.length > 0)
								$('#FireDatatree').treeview('unselectNode',
										[ selected[0].nodeId, {
											silent : true
										} ]);
							selected = $('#UpdataDatatree').treeview(
									'getSelected');
							if (selected.length > 0)
								$('#UpdataDatatree').treeview('unselectNode',
										[ selected[0].nodeId, {
											silent : true
										} ]);
							selected = $('#TrafficDatatree').treeview(
							'getSelected');
					if (selected.length > 0)
						$('#TrafficDatatree').treeview('unselectNode',
								[ selected[0].nodeId, {
									silent : true
								} ]);
							
							buildMapDataIndexModal("FloodDatatree",
									data);
						}
					})
			//用户上传数据
			$('#UpdataDatatree').treeview(
					{
						data : eval("(" + treeArr[3] + ")"),
						onNodeSelected : function(event, data) {
							var selected = $('#FireDatatree').treeview(
									'getSelected');
							if (selected.length > 0)
								$('#FireDatatree').treeview('unselectNode',
										[ selected[0].nodeId, {
											silent : true
										} ]);
							selected = $('#FloodDatatree').treeview(
									'getSelected');
							if (selected.length > 0)
								$('#FloodDatatree').treeview(
										'unselectNode', [ selected[0].nodeId, {
											silent : true
										} ]);
							selected = $('#TrafficDatatree').treeview(
							'getSelected');
					if (selected.length > 0)
						$('#TrafficDatatree').treeview('unselectNode',
								[ selected[0].nodeId, {
									silent : true
								} ]);
							buildMapDataIndexModal("UpdataDatatree", data);
						},
						showTags : true
					});
			// if the parameters of SelectData have been selected, show the
			// selected node
			if (CutomizeMapRecords.dataName != ""
					&& CutomizeMapRecords.dataType != ""
					&& CutomizeMapRecords.dataNodeId != "") {
				$('#' + CutomizeMapRecords.dataType).treeview('selectNode',
						[ CutomizeMapRecords.dataNodeId, {
							silent : true
						} ]);
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert(errorThrown);
		}
	});
}
/* chose classification index and statistical index of the mapData */
function buildMapDataIndexModal(dataType, data) {
	// record the parameter of CutomizeMapRecords
	var dataName=data.text;
	var dataNodeId=data.nodeId;
	CutomizeMapRecords.dataName = data.text;
	CutomizeMapRecords.dataType = dataType;
	CutomizeMapRecords.dataNodeId =data.nodeId;
	// var datasd=$('#'+dataType).treeview("data");
	// get items of the data, and built buildMapDataIndexModalModal
	if(dataType=="TrafficDatatree"){
		var types;
		var indexs;
		var Node = $('#TrafficDatatree').treeview('getNode', dataNodeId);
		if(Node.parentId==0){
			types=Node.value;
			if(Node.nodes==undefined){
				indexs=[Node];
			}
			else{
				indexs=Node.nodes;
			}
			
		}
		else{
			if(Node.nodes==undefined){
				indexs=[Node];
			}
			else{
				indexs=Node.nodes;
			}
			for(var i=0;i<6;i++){
				var parentNode= $('#TrafficDatatree').treeview('getParent', Node);
				if(parentNode.parentId=="0"){
					types=parentNode.value;
					break;
				}
				Node=parentNode;
			}
		}
		
		types=eval("("+types+")");
		indexArr = indexs;
		$("#statisticalTypeDiv").css("display","none");
		$("#mapUnit").css("display","none");
		$("#classificationIndex").empty();
		$("#statisticalIndex").empty();
		$("#thematicMapArea").empty();
		for ( var i = 0; i < indexArr.length; i++) {
			$("#classificationIndex").append(
					"<option>" + indexArr[i].text + "</option>");
			$("#statisticalIndex").append(
					"<option>" + indexArr[i].text + "</option>");
		}
		$.getJSON("resource/district.json",
				function(areaJson) {
			var thisAreaJson=areaJson.features;
			for(var i=0;i<thisAreaJson.length;i++){
				$("#thematicMapArea").append(
						"<option selected=\"selected\">" + thisAreaJson[i].attributes.NAME + "</option>");
			}
			$("#thematicMapArea").selectpicker("refresh");
		});
		$("#classificationIndex").selectpicker("refresh");
		$("#statisticalIndex").selectpicker("refresh");
		
		$("#MapDataIndexModal").modal("show");
	}

	if(dataType=="UpdataDatatree"){
		var indexArr;
		var statisticArea;
		$.ajax({
			type : "post",
			url : "./getCustomizeMapDataIndex",
			data : {
				dataType : dataType,
				dataName : dataName,
				username:window.localStorage.getItem("username")
			},
			success : function(res) {
				indexArr = res.split("s-p-l")[0].split(",");
				statisticArea=res.split("s-p-l")[1].split("#")[0];
				$("#mapUnit").css("display","none");
				$("#statisticalTypeDiv").css("display","none");
				$("#classificationIndex").empty();
				$("#statisticalIndex").empty();
				$("#thematicMapArea").empty();
				for ( var i = 0; i < indexArr.length; i++) {
					$("#classificationIndex").append(
							"<option>" + indexArr[i] + "</option>");
					$("#statisticalIndex").append(
							"<option>" + indexArr[i] + "</option>");
				}
				CutomizeMapRecords.geoUnit=statisticArea;
				$.getJSON("resource/"+CutomizeMapRecords.geoUnit+".json",
						function(areaJson) {
					var thisAreaJson=areaJson.features;
					for(var i=0;i<thisAreaJson.length;i++){
						$("#thematicMapArea").append(
								"<option selected=\"selected\">" + thisAreaJson[i].attributes.NAME + "</option>");
					}
					$("#thematicMapArea").selectpicker("refresh");
				});
				$("#classificationIndex").selectpicker("refresh");
				$("#statisticalIndex").selectpicker("refresh");
				$("#thematicMapArea").selectpicker("refresh");
				$("#MapDataIndexModal").modal("show");
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert(errorThrown);
			}
		});
	}

}
/* build the interface of selectMap */
function buildInterfaceOfSelectMap() {
	if (CutomizeMapRecords.mapName == "") {
	$("#pre-step").click();
	alert("请输入地图名称");
	$("#newMapModal").modal("show");
	return;
}
	
	$("#data-content").empty();
	$("#data-content").append("<ul id=\"mayTab\" class=\"nav nav-tabs\"><li class=\"active\"><a href=\"#homeMapsTab\" data-toggle=\"tab\">内置地图"+
			 "</a></li><li><a href=\"#otherMapsTab\" data-toggle=\"tab\">外接地图</a></li></ul>");
	$("#data-content").append('<div class="tab-content">'+
			'<div class="tab-pane fade in active" id="homeMapsTab"></div>'+
			'<div class="tab-pane fade" id="otherMapsTab"></div></div>');
	$("#homeMapsTab").append("<div id='mapPanel1' class=\"panel\"></div>");
	$("#otherMapsTab").append("<div id='mapPanel2' class=\"panel\"></div>");
	$("#mapPanel1").append('<div class="panel-body"></div>');
	$("#mapPanel2").append('<div class="panel-body"></div>');
var otherMapThumbnailData=[
	[
		"images/mapThumbnail/reliefShading_danya.JPG",
		"天地图政区图",
		"vec" ],
[
		"images/mapThumbnail/dom.JPG",
		"天地图影像图",
		"img" ]
];
for (i = 0; i < otherMapThumbnailData.length; i++) {
	// if the parameters of SelectMap have been selected, show the item
	if (otherMapThumbnailData[i][1] == CutomizeMapRecords.baseMapName) {
		var otherMapThumbnailElement = '<div class="map-thumbnail"><i class="map-checked-icon fa fa-check-circle" aria-hidden="true" style="display:block"></i><img src="'
				+ otherMapThumbnailData[i][0]
				+ '" alt="'
				+ otherMapThumbnailData[i][2]
				+ '"><p>'
				+ otherMapThumbnailData[i][1]
				+ '</p></div>';
		$("#mapPanel1").children(".panel-body").append(
				otherMapThumbnailElement);
	} else {
		var otherMapThumbnailElement = '<div class="map-thumbnail"><i class="map-checked-icon map-checked-icon fa fa-check-circle" aria-hidden="true"></i><img src="'
				+ otherMapThumbnailData[i][0]
				+ '" alt="'
				+ otherMapThumbnailData[i][2]
				+ '"><p>'
				+ otherMapThumbnailData[i][1]
				+ '</p></div>';
		$("#mapPanel2").children(".panel-body").append(
				otherMapThumbnailElement);
	}

}
	var mapThumbnailData = [
	    					
	    	            			[
	    	            					"images/mapThumbnail/fzzqb.png",
	    	            					"区划图",
	    	            					"http://localhost:6080/arcgis/rest/services/底图配图111/MapServer"  ]
	    	            			
	    	            			

	    	            	];
	for (i = 0; i < mapThumbnailData.length; i++) {
		// if the parameters of SelectMap have been selected, show the item
		if (mapThumbnailData[i][1] == CutomizeMapRecords.baseMapName) {
			var mapThumbnailElement = '<div class="map-thumbnail"><i class="map-checked-icon fa fa-check-circle" aria-hidden="true" style="display:block"></i><img src="'
					+ mapThumbnailData[i][0]
					+ '" alt="'
					+ mapThumbnailData[i][2]
					+ '"><p>'
					+ mapThumbnailData[i][1]
					+ '</p></div>';
			$("#mapPanel1").children(".panel-body").append(
					mapThumbnailElement);
		} 
		else {
			var mapThumbnailElement = '<div class="map-thumbnail"><i class="map-checked-icon map-checked-icon fa fa-check-circle" aria-hidden="true"></i><img src="'
					+ mapThumbnailData[i][0]
					+ '" alt="'
					+ mapThumbnailData[i][2]
					+ '"><p>'
					+ mapThumbnailData[i][1]
					+ '</p></div>';
			$("#mapPanel1").children(".panel-body").append(
					mapThumbnailElement);
		}

	}
	$(".map-thumbnail")
			.click(
					function() {
						// when select the baseMap, show the check symbol;
						$(".map-checked-icon").hide();
						$($(this).children(".map-checked-icon")[0]).show();

						var mapUrl = $(this).children("img")[0].alt;
						var mapName = $($(this).children("p")[0]).html();
						// record the parameter of CutomizeMapRecords
						CutomizeMapRecords.baseMapName = mapName;
						CutomizeMapRecords.baseMapUrl = mapUrl;
						// shift the baseMap
						require(
								[ "esri/map",
										"esri/layers/ArcGISDynamicMapServiceLayer","mapDefinition/TDTLayer",  "mapDefinition/GDLayer",  
								        "mapDefinition/TDTAnnoLayer" ],
								function(Map, ArcGISDynamicMapServiceLayer,TDTLayer,GDLayer,TDTAnnoLayer) {
									mapContainer.removeLayer(baseMapServiceLayer);
									if(mapName.indexOf("天地图")!=-1){
										baseMapServiceLayer = new TDTLayer(
												mapUrl);
										mapContainer.centerAndZoom({"x": 117.333103, "y": -39.294706, "spatialReference":mapContainer.spatialReference },5);
									}
									else if(mapName.indexOf("高德")!=-1){
										baseMapServiceLayer = new GDLayer();
									}else{
										baseMapServiceLayer = new ArcGISDynamicMapServiceLayer(
												mapUrl);
										mapContainer.centerAndZoom({"x": 117.333103, "y": 39.294706, "spatialReference":mapContainer.spatialReference },5);
									}
									
									mapContainer.addLayer(baseMapServiceLayer);
									
								});
					});
}
/* build the interface of selectFormat */
function buildInterfaceOfSelectFormat() {
	$("#data-content").empty();
	$
			.ajax({
				url : "./getCustomizeFormatDataList",
				data : {
					userName : CutomizeMapRecords.userName
				},
				success : function(res) {
					var formatTreeData = res.split("s-p-l-i-t");
					$("#data-content").append(
							"<div id=\"defaultFormatTree\" ></div>");
					$("#data-content").append(
							"<div id=\"updataFormatTree\" ></div>");
					$('#defaultFormatTree')
							.treeview(
									{
										data : eval("(" + formatTreeData[0]
												+ ")"),
										onNodeSelected : function(event, data) {
											var selected = $(
													'#updataFormatTree')
													.treeview('getSelected');
											if (selected.length > 0)
												$('#updataFormatTree')
														.treeview(
																'unselectNode',
																[
																		selected[0].nodeId,
																		{
																			silent : true
																		} ]);
											CutomizeMapRecords.mapFormatName = data.text;
											CutomizeMapRecords.mapFormatContent = data.value;
											CutomizeMapRecords.mapFormatType = "defaultFormatTree";
											CutomizeMapRecords.mapFormatNodeId = data.nodeId;

										}
									});
					var test = '[{  text: "自定义模板",  backColor: "#f8f8f8",  selectable: false,tags: [\'自定义模板\'],nodes:[{"tags": [\'自定义模板\'],"text":"黄棕色系旗型模板"},{"tags": [\'自定义模板\'],"text":"黄红色系圆环模板"},{"tags": [\'自定义模板\'],"text":"蓝色色系圆柱模板"}]}]';
					$('#updataFormatTree')
							.treeview(
									{
										data : eval("(" + formatTreeData[1]
												+ ")"),
										showTags : true,
										onNodeSelected : function(event, data) {
											var selected = $(
													'#defaultFormatTree')
													.treeview('getSelected');
											if (selected.length > 0)
												$('#defaultFormatTree')
														.treeview(
																'unselectNode',
																[
																		selected[0].nodeId,
																		{
																			silent : true
																		} ]);
											CutomizeMapRecords.mapFormatName = data.text;
											CutomizeMapRecords.mapFormatContent = data.value;
											CutomizeMapRecords.mapFormatType = "updataFormatTree";
											CutomizeMapRecords.mapFormatNodeId = data.nodeId;

										}
									});
					// if the parameters of SelectFormat have been selected,
					// show the
					// selected node
					if (CutomizeMapRecords.mapFormatName != ""
							&& CutomizeMapRecords.mapFormatType != ""
							&& CutomizeMapRecords.mapFormatNodeId != "") {
						$('#' + CutomizeMapRecords.mapFormatType).treeview(
								'selectNode',
								[ CutomizeMapRecords.mapFormatNodeId, {
									silent : true
								} ]);
						if(isEditForm==true&&CutomizeMapRecords.mapFormatType == "updataFormatTree" ){
							var selected = $(
							'#updataFormatTree')
							.treeview('getSelected');
							CutomizeMapRecords.mapFormatName = selected[0].text;
							CutomizeMapRecords.mapFormatContent = selected[0].value;
							
							CutomizeMapRecords.mapFormatNodeId = selected[0].nodeId;
						}
					}
				}
			});
}

/* submit all records in Selecting mapData, and turn to selecting baseMap */
function submitMapDataIndexs() {
	var classificationIndex = $("#classificationIndex option:selected").val();
	if(classificationIndex==undefined){
		classificationIndex="";
	}
	var statisticalUrl= $("#statisticalType option:selected").val();
	var statisticalIndex = [];
	var thematicMapArea=[];
	var geoUnit ="district";
	if(CutomizeMapRecords.dataType=='FloodDatatree'){
		geoUnit=$("#geoUnit option:selected").val();
	}
	
	$("#statisticalIndex :selected").each(function() {
		statisticalIndex.push($(this).val());
	});
	statisticalIndex = statisticalIndex.toString();
	$("#thematicMapArea :selected").each(function() {
		thematicMapArea.push($(this).val());
	});
	thematicMapArea=thematicMapArea.toString();
	//如果是自定义区域，为地理单元是特殊的
	if(thematicMapArea.indexOf(".")>0){
		geoUnit="special";
	}
	var statisticalTypeDiv= $("#statisticalTypeDiv").css("display");
	if (geoUnit == "") {
		alert("请选择制图单元");
		return;
	}
	if(statisticalUrl==undefined&& statisticalTypeDiv=="block"){
		alert("指标类型至少选择一种");
		return;
	}
	if (statisticalIndex == "" && classificationIndex == "") {
		alert("分级指标和统计指标至少选择一种");
		return;
	}
	if (thematicMapArea == "" ) {
		alert("请选择制图范围");
		return;
	}
	CutomizeMapRecords.classificationIndex = classificationIndex;
	CutomizeMapRecords.statisticalIndex = statisticalIndex;
	CutomizeMapRecords.thematicMapArea = thematicMapArea;
	
	if(CutomizeMapRecords.dataType=="FloodDatatree"||CutomizeMapRecords.dataType=="TrafficDatatree")
	CutomizeMapRecords.geoUnit = geoUnit;
	CutomizeMapRecords.geoUnit = CutomizeMapRecords.geoUnit.trim();
	//CutomizeMapRecords.resourceURL=statisticalUrl;
	$("#MapDataIndexModal").modal("hide");
	$("#next-step").click();
}
// new a format
function newFormat() {
	var formatName = $("#formatName").val();
	var districtColor = $("#districtColor").attr("alt");
	var districtChart = $("#districtChart").attr("alt");
	var streetColor = $("#streetColor").attr("alt");
	var streetChart = $("#streetChart").attr("alt");
	var streetClassNum = $("#streetClassNum option:selected").text();
	var districtClassNum = $("#districtClassNum option:selected").text();
	
	debugger
	if (formatName == "" || districtColor == "" || districtChart == ""
			|| streetColor == "" || streetChart == "") {
		alert("请完善信息");
		return;
	}
	$.ajax({
		url : "./UpLoadFormat",
		data : {
			userName : CutomizeMapRecords.userName,
			formatName : formatName,
			districtColor : districtColor,
			districtChart : districtChart,
			streetColor : streetColor,
			streetChart : streetChart,
			streetClassNum : streetClassNum,
			districtClassNum : districtClassNum,
			isEditForm:isEditForm
		},
		type : "post",
		success : function(res) {
			alert(res);
			buildInterfaceOfSelectFormat();
			$("#newFormatModal").modal("hide");
		}
	});
}
//存储制图单元的名称和坐标信息
var coordinate;
//用于专题制图的数据
var thematicData;
// 获取制图参数，并制图
function makeThematicMap() {
	classLayer.clear();
	statisticLayer.clear();
	if(CutomizeMapRecords.gradeColors==""){// 如果还没有选择分级颜色，则为第一次出现精加工界面
		$("#data-content").empty();	
		$("#data-content").append(
		"<button id=\"statisticAlone\" type=\"button\" class=\"btn btn-warning\" onclick=\"statisticAlone();\">独立统计</button>");
		$("#data-content").append(
		"<br/>");	
		$("#data-content").append(
		"<button id=\"statisticFashioning\" type=\"button\" class=\"btn btn-primary\" onclick=\"makeClassPanel()\">地图精加工</button>");
		$("#data-content").append(
		"<br/>");
		$("#data-content").append(
		"<button id=\"mapSave\" type=\"button\" class=\"btn btn-success\" onclick=\"saveCustomizeMap();\">保存</button>");
		
	}
	
	
	
	//统计年鉴数据
	if(CutomizeMapRecords.dataType=="TrafficDatatree"){
		// get data of thematic map
		$.getJSON("resource/StatisticYearbook.json",function(nodes){
			nodes=nodes[0].nodes;
			nodes=_.filter(nodes,function(temp){return  temp["text"]==CutomizeMapRecords.dataName;})[0].nodes;
			$.getJSON("resource/district.json",
					function(areaJson) {
				areaJson=areaJson.features;
				$.getJSON("./GetStatisticYearBookData",
						function(data) {
					
					//过滤年份数据
					data= _.filter(data, function(temp){ return temp["YEAR"] == CutomizeMapRecords.dataName; });
					thematicData=data;
					// 构造统计专题数据中每条记录的坐标数组
					 coordinate=[];
					for(var j=0;j<data.length;j++){	
						var item=[];
						for(var i=0;i<areaJson.length;i++){
							if(data[j]["GEOCODE"]==areaJson[i]["attributes"]["PAC"]){
								item["x"]=areaJson[i]["attributes"]["x"];
								item["y"]=areaJson[i]["attributes"]["y"];
								item["name"]=areaJson[i]["attributes"]["NAME"];
								item["PAC"]=areaJson[i]["attributes"]["PAC"];
							}
							
						}
						coordinate.push(item);
					}
					// 构造分级数据
					 classData=[];
					if(CutomizeMapRecords.classificationIndex!=""&&CutomizeMapRecords.classificationIndex!=undefined){
						//找到对应的英文字段名
						var classificationIndex=_.filter(nodes,function(temp){return  temp["text"]==CutomizeMapRecords.classificationIndex;})[0].value
						if(classificationIndex!="")
						for(var i=0;i<data.length;i++){
							var item="";
							var value=data[i][classificationIndex];
							if(value==undefined){
								value=0;
							}
							if(data[i]["GEOCODE"]=="120000000000"){
								continue;
							}
							classData.push({
								"code":data[i]["GEOCODE"],
								"value":value
								});
						}
					}
					
					$.ajax({
						url : "./makeChartParam",
						data : {
							classificationIndex : CutomizeMapRecords.classificationIndex,
							statisticalIndex : CutomizeMapRecords.statisticalIndex,
							dataName : CutomizeMapRecords.dataName,
							dataType : CutomizeMapRecords.dataType,
							userName : CutomizeMapRecords.userName,
							mapFormatName : CutomizeMapRecords.mapFormatName,
							mapFormatContent : CutomizeMapRecords.mapFormatContent,
							gradeColors:CutomizeMapRecords.gradeColors,
							classData:JSON.stringify(classData)
						},
						type : "post",
						success : function(res) {
							ChartData = res.split("&");
							CutomizeMapRecords.statisticalData=ChartData;
							if(CutomizeMapRecords.gradeColors!=""){	
								makeClass(ChartData, "district");
								//return;
							}
							ChartData[9]=coordinate;
							// 通过变换统计专题数据生成符合makeStatisticMap的参数，并执行制作统计专题图
							function transforMakeStatisticMap(){
								var statisticalIndex=CutomizeMapRecords.statisticalIndex.split(",");
								
								var statisticalIndexEn=[];
								//中英文转化
								for(var i=0;i<statisticalIndex.length;i++){
									var  temp=_.filter(nodes,function(temp){return  temp["text"]==statisticalIndex[i];})[0].value
									statisticalIndexEn.push(temp);
								}
								
								var unit="";
								var num=""
								for(var j=0;j<statisticalIndex.length;j++){
									num=num+","+statisticalIndex[j];
									unit=unit+","+"个";
								}
								var  values="";
								for(var i=0;i<data.length;i++){
									var item="";
									for(var j=0;j<statisticalIndex.length;j++){
										var index=statisticalIndexEn[j];
										var value=data[i][index];
										if(value==undefined){
											value=0;
										}
										if(j==0){
											item=value;
										}
										else{
											item=item+","+value;
										}
										
									}
									values=values+";"+item+","+(i+1)+",0,0,"+(i+1);
								}								
								var chartdata=num.substring(1)+";"+unit.substring(1)+values;
								ChartData[3]=chartdata;
								$.when(makeStatisticMap(ChartData)).done(function(data){
							           progressCtr("hide");
							        });
							}
							// if there is classificationIndex
							if (CutomizeMapRecords.classificationIndex != "") {
								// geoArr = eval("(" + ChartData[10] + ")");
								 $.when(makeClass(ChartData, "district")).done(function(data){
									// 为了让统计专题图的图层在分级专题图图层之上
									// if there is statisticalIndex
									if (CutomizeMapRecords.statisticalIndex != "") {
										transforMakeStatisticMap();
										return;
									}else{
										progressCtr("hide");
									}
						        });
							}
							else if (CutomizeMapRecords.statisticalIndex != "") {
								transforMakeStatisticMap();
							}
							
			        
						
							mapContainer.centerAndZoom({"x": 117.333103, "y": 39.294706, "spatialReference":mapContainer.spatialReference },0);
							
							}});
					
				});
			})
		});
	}
	if(CutomizeMapRecords.dataType=="UpdataDatatree"){
		// get data of thematic map
		var ChartData = "";
		$.ajax({
			url : "./makeChartParam",
			// async : false,
			data : {
				classificationIndex : CutomizeMapRecords.classificationIndex,
				statisticalIndex : CutomizeMapRecords.statisticalIndex,
				
				dataType : CutomizeMapRecords.dataType,
				
				mapFormatName : CutomizeMapRecords.mapFormatName,
				mapFormatContent : CutomizeMapRecords.mapFormatContent,
				gradeColors:CutomizeMapRecords.gradeColors,
				regionNames:CutomizeMapRecords.thematicMapArea,
				disastertype:CutomizeMapRecords.disasterType,
				maptype:CutomizeMapRecords.mapType
			},
			type : "post",
			success : function(res) {
				ChartData = res.trim().split("&");
				CutomizeMapRecords.statisticalData=ChartData;
				/*if(CutomizeMapRecords.gradeColors!=""){
					makeClass(ChartData, ChartData[2])
					return;
				}*/
				 ChartData[9]=eval("(" + ChartData[9] + ")");
				 thematicData=ChartData;
				 coordinate=ChartData[9];
				 function transforMakeStatisticMap(){
						
						$.when(makeStatisticMap(ChartData)).done(function(data){
					           progressCtr("hide");
					        });
					}
					// if there is classificationIndex
					if (CutomizeMapRecords.classificationIndex != "") {
						// geoArr = eval("(" + ChartData[10] + ")");
						 $.when(makeClass(ChartData,ChartData[2])).done(function(data){
							// 为了让统计专题图的图层在分级专题图图层之上
							// if there is statisticalIndex
							if (CutomizeMapRecords.statisticalIndex != "") {
								transforMakeStatisticMap();
								return;
							}else{
								progressCtr("hide");
							}
				        });
					}
					else if (CutomizeMapRecords.statisticalIndex != "") {
						transforMakeStatisticMap();
					}
				
				mapContainer.centerAndZoom({"x": 117.333103, "y": 39.294706, "spatialReference":mapContainer.spatialReference },0);
			}
		});	
	}
	
}


//显示独立统计图表
function statisticAlone(){
	//构造选择专题指标下拉框
	 $("#themeIndex").empty();
	if(CutomizeMapRecords.statisticalIndex!=""){
		var child = "<option>统计专题指标</option>";
	      $("#themeIndex").append(child);
	}
	if(CutomizeMapRecords.classificationIndex!=""){
		var child = "<option>分级专题指标</option>";
	      $("#themeIndex").append(child);
	}
	$("#echartsModal").modal("show");
	$("#statisticAloneBtn").click(tagStatisticAlone);

	//触发独立统计事件
	function tagStatisticAlone(){
		//选择分级还是统计指标
		var index= $("#themeIndex option:selected")[0].text;
		//横坐标数据
		var xAxis=[];
		//指标
		var legend=[];
		//画图数据
		var data=[];
		//分级专题指标
		var classificationIndex;
		//统计专题指标
		var statisticalIndex;
		if(index=="统计专题指标"){

				var statisticalIndex=CutomizeMapRecords.statisticalIndex.split(",");
				legend=[];
				legend=statisticalIndex;
				$.each(legend,function(i){
					legend[i]=CityCodeToCn(legend[i]);
				});
				//构造图表中的各个参数
				//构造指标
				xAxis=[];
				for(var i=0;i<statisticLayer.graphics.length;i++){
					xAxis.push(statisticLayer.graphics[i].attributes["区域"]);
				}
				//构造数据
				data=[];

				for(var j=0;j<statisticalIndex.length;j++){
					var temp={"name":CityCodeToCn( statisticalIndex[j]),"type":'bar',"data":[]};
					for(var i=0;i<statisticLayer.graphics.length;i++){
						temp.data.push(statisticLayer.graphics[i].attributes[CityCodeToCn(statisticalIndex[j])]);
					}
					
					data.push(temp);	
				}
			
			
		}
		else if(index=="分级专题指标"){
			
				legend=[];
				legend.push(CityCodeToCn(CutomizeMapRecords.classificationIndex));
				//构造图表中的各个参数
				//构造指标
				xAxis=[];
				
				for(var i=0;i<classLayer.graphics.length;i++){
					xAxis.push(classLayer.graphics[i].attributes.areaName);
				}
				//构造数据
				data=[];
					var temp={"name":CityCodeToCn(CutomizeMapRecords.classificationIndex),"type":'bar',"data":[]};
					
					for(var i=0;i<classLayer.graphics.length;i++){
	
					   temp.data.push(classLayer.graphics[i].attributes.value);
						
					}
					
					
					data.push(temp);
					}
			
			
		
		//绑定插件
        var myChart = echarts.init(document.getElementById('echarts-container'));
        myChart.setOption({
            tooltip : {
                trigger: 'axis'
            },
            legend: {
                data:legend
            },
            toolbox: {
                show : true,
                feature : {
                    mark : {show: true},
                    dataView : {show: false, readOnly: false},
                    magicType : {show: true, type: ['line', 'bar','pie']},
                    restore : {show: false},
                    saveAsImage : {show: true}
                }
            },
            calculable : true,
            xAxis : [
                {
                    type : 'category',
                    data : xAxis
                }
            ],
            yAxis : [
                {
                    type : 'value',
                    splitArea : {show : true}
                }
            ],
            series : data
        });
        $.ajaxSettings.async = true;
	}

  
}

// color ribbon
var color1001 = [ [ 255, 254, 227 ], [ 255, 253, 201 ], [ 255, 251, 177 ],
		[ 255, 250, 137 ], [ 252, 228, 116 ], [ 247, 197, 95 ],
		[ 241, 165, 75 ], [ 233, 129, 70 ], [ 228, 102, 66 ], [ 201, 81, 61 ] ];
var color1002 = [ [ 229, 243, 252 ], [ 196, 229, 250 ], [ 164, 215, 246 ],
		[ 127, 200, 242 ], [ 86, 187, 237 ], [ 19, 174, 233 ], [ 0, 161, 227 ],
		[ 0, 145, 211 ], [ 0, 131, 186 ], [ 0, 112, 155 ] ];
var color1003 = [ [ 253, 235, 242 ], [ 250, 210, 228 ], [ 246, 186, 211 ],
		[ 242, 161, 195 ], [ 237, 136, 178 ], [ 233, 111, 161 ],
		[ 228, 79, 143 ], [ 212, 46, 125 ], [ 188, 47, 115 ], [ 158, 43, 101 ] ];
var color1004 = [ [ 255, 254, 227 ], [ 255, 253, 201 ], [ 255, 251, 177 ],
		[ 255, 250, 137 ], [ 228, 237, 121 ], [ 186, 219, 107 ],
		[ 143, 201, 93 ], [ 88, 181, 97 ], [ 18, 169, 98 ], [ 0, 147, 91 ] ];
var color1005 = [ [ 255, 254, 236 ], [ 255, 253, 213 ], [ 255, 252, 189 ],
		[ 255, 251, 164 ], [ 255, 250, 137 ], [ 255, 248, 106 ],
		[ 255, 247, 51 ], [ 241, 232, 0 ], [ 209, 202, 0 ], [ 171, 167, 26 ] ];
var color1006 = [ [ 115, 147, 162 ], [ 123, 159, 177 ], [ 148, 175, 187 ],
		[ 177, 197, 198 ], [ 201, 213, 203 ], [ 227, 232, 205 ],
		[ 241, 235, 198 ], [ 242, 228, 186 ], [ 237, 210, 169 ],
		[ 246, 202, 156 ] ];
var color1007 = [ [ 239, 204, 166 ], [ 239, 214, 174 ], [ 238, 222, 182 ],
		[ 237, 229, 191 ], [ 234, 232, 190 ], [ 244, 242, 205 ],
		[ 232, 236, 203 ], [ 221, 228, 186 ], [ 202, 214, 164 ],
		[ 187, 202, 151 ] ];
var color1008 = [ [ 255, 254, 227 ], [ 248, 241, 208 ], [ 242, 230, 187 ],
		[ 237, 210, 167 ], [ 246, 202, 156 ], [ 232, 175, 126 ],
		[ 228, 153, 107 ], [ 208, 133, 88 ], [ 183, 113, 73 ],
		[ 188, 202, 151 ] ];
var color1009 = [ [ 252, 225, 237 ], [ 248, 199, 220 ], [ 244, 174, 203 ],
		[ 237, 136, 178 ], [ 215, 117, 166 ], [ 185, 97, 153 ],
		[ 155, 77, 142 ], [ 128, 80, 145 ], [ 110, 83, 148 ], [ 189, 202, 151 ] ];
var color1010 = [ [ 244, 250, 249 ], [ 237, 246, 241 ], [ 217, 237, 234 ],
		[ 200, 229, 222 ], [ 183, 221, 206 ], [ 149, 207, 199 ],
		[ 116, 195, 187 ], [ 105, 173, 166 ], [ 79, 150, 144 ],
		[ 190, 202, 151 ] ];
// 产生颜色数组
function generateColor(gradeNumberSelect, _colorSetSelect, _colorOrderSelect) {
	var _colorScheme = [];// 所选颜色的完整的颜色数组
	var _colorSchemeFull = []; // 最后返回的颜色数组
	_colorSetSelect=_colorSetSelect.trim();
	switch (_colorSetSelect) {
	case "1001":
		_colorSchemeFull = color1001;
		break;
	case "1002":
		_colorSchemeFull = color1002;
		break;
	case "1003":
		_colorSchemeFull = color1003;
		break;
	case "1004":
		_colorSchemeFull = color1004;
		break;
	case "1005":
		_colorSchemeFull = color1005;
		break;
	case "1006":
		_colorSchemeFull = color1006;
		break;
	case "1007":
		_colorSchemeFull = color1007;
		break;
	case "1008":
		_colorSchemeFull = color1008;
		break;
	case "1009":
		_colorSchemeFull = color1009;
		break;
	case "1010":
		_colorSchemeFull = color1010;
		break;
	}

	switch (gradeNumberSelect) {
	case "3":
		_colorScheme[0] = _colorSchemeFull[0];
		_colorScheme[1] = _colorSchemeFull[4];
		_colorScheme[2] = _colorSchemeFull[8];
		break;
	case "4":
		_colorScheme[0] = _colorSchemeFull[0];
		_colorScheme[1] = _colorSchemeFull[3];
		_colorScheme[2] = _colorSchemeFull[6];
		_colorScheme[3] = _colorSchemeFull[9];
		break;
	case "5":
		_colorScheme[0] = _colorSchemeFull[0];
		_colorScheme[1] = _colorSchemeFull[2];
		_colorScheme[2] = _colorSchemeFull[4];
		_colorScheme[3] = _colorSchemeFull[6];
		_colorScheme[4] = _colorSchemeFull[8];
		break;
	case "6":
		_colorScheme[0] = _colorSchemeFull[0];
		_colorScheme[1] = _colorSchemeFull[2];
		_colorScheme[2] = _colorSchemeFull[4];
		_colorScheme[3] = _colorSchemeFull[6];
		_colorScheme[4] = _colorSchemeFull[8];
		_colorScheme[5] = _colorSchemeFull[9];
		break;
	case "7":
		_colorScheme[0] = _colorSchemeFull[0];
		_colorScheme[1] = _colorSchemeFull[2];
		_colorScheme[2] = _colorSchemeFull[3];
		_colorScheme[3] = _colorSchemeFull[5];
		_colorScheme[4] = _colorSchemeFull[6];
		_colorScheme[5] = _colorSchemeFull[8];
		_colorScheme[6] = _colorSchemeFull[9];
		break;
	case "8":
		_colorScheme[0] = _colorSchemeFull[0];
		_colorScheme[1] = _colorSchemeFull[1];
		_colorScheme[2] = _colorSchemeFull[3];
		_colorScheme[3] = _colorSchemeFull[4];
		_colorScheme[4] = _colorSchemeFull[5];
		_colorScheme[5] = _colorSchemeFull[7];
		_colorScheme[6] = _colorSchemeFull[8];
		_colorScheme[7] = _colorSchemeFull[9];
		break;
	case "9":
		_colorScheme[0] = _colorSchemeFull[0];
		_colorScheme[1] = _colorSchemeFull[1];
		_colorScheme[2] = _colorSchemeFull[3];
		_colorScheme[3] = _colorSchemeFull[4];
		_colorScheme[4] = _colorSchemeFull[5];
		_colorScheme[5] = _colorSchemeFull[6];
		_colorScheme[6] = _colorSchemeFull[7];
		_colorScheme[7] = _colorSchemeFull[8];
		_colorScheme[8] = _colorSchemeFull[9];
		break;
	case "10":
		_colorScheme[0] = _colorSchemeFull[0];
		_colorScheme[1] = _colorSchemeFull[1];
		_colorScheme[2] = _colorSchemeFull[2];
		_colorScheme[3] = _colorSchemeFull[3];
		_colorScheme[4] = _colorSchemeFull[4];
		_colorScheme[5] = _colorSchemeFull[5];
		_colorScheme[6] = _colorSchemeFull[6];
		_colorScheme[7] = _colorSchemeFull[7];
		_colorScheme[8] = _colorSchemeFull[8];
		_colorScheme[9] = _colorSchemeFull[9];
		break;
	}
	if (_colorOrderSelect == "1") { // 选择逆序时将最终颜色数组倒置
		_colorScheme.reverse();
	}
	return _colorScheme;
}

//将市情数据的指标转换为中文
function CityCodeToCn(code){
	var cnVal="";
	switch (code) {
	case "SphArea":
		cnVal="面积";
		break;
	case "SurArea":
		cnVal="表面积";
		break;
	case "NsewLen-ew_len":
		cnVal="东西长度";
		break;
	case "NsewLen-sn_len":
		cnVal="南北长度";
		break;
	case "Accounted":
		cnVal="占比";
		break;
	case "Distance":
		cnVal="距离";
		break;
	case "SurDistance":
		cnVal="表面距离";
		break;
	case "Number":
		cnVal="个数";
		break;
	case "Density":
		cnVal="密度";
		break;
	case "ELEA-Max":
		cnVal="最大高程";
	case "ELEA-Min":
		cnVal="最小高程";
	case "ELEA-ElevationAverage":
		cnVal="平均高程";
		break;
	default:
		cnVal=code;
		break;
}
return cnVal;
}
// 生成统计专题图
function makeStatisticMap(ChartData){	
	CutomizeMapRecords.statisticalData=ChartData;
	var defer1 = $.Deferred();
	statisticLayer.clear();
	$.ajax({
		type : "post",
		url : "./MakeStatisticMapLegend",
		data : {
			WC : ChartData[0],
			DC : ChartData[1],
			chartData : ChartData[3],
			colors : ChartData[5],
			chartId : ChartData[4],
			widthString : ChartData[6],
			heightString : ChartData[7],
			isLabel : "false"
		},
		// async : false,
		type : "post",
		success : function(res) {
			$("#statistic_legend").attr('src',"data:image/png;base64,"+res);
		}
		});
	$.ajax({
		type : "post",
		url : "./MakeStatisticMap",
		data : {
			WC : ChartData[0],
			DC : ChartData[1],
			chartData : ChartData[3],
			colors : ChartData[5],
			chartId : ChartData[4],
			widthString : ChartData[6],
			heightString : ChartData[7],
			isLabel : "false"
		},
		// async : false,
		type : "post",
		success : function(res) {
			 coordinateArr = ChartData[9] ;
			statisticCharts = res.split(";");
			require(
					[ "esri/geometry/Polygon",
							"esri/symbols/PictureMarkerSymbol",
							"esri/symbols/SimpleLineSymbol",
							"esri/Color", "esri/graphic" ],
					function(Polygon, PictureMarkerSymbol,
							SimpleLineSymbol, Color, Graphic) {
						for ( var i = 0; i < statisticCharts.length - 1; i++) {
							if(coordinateArr[i]["x"]==undefined){
								continue;
							}
							var pfs = new PictureMarkerSymbol(
									'data:image/png;base64,'
											+ statisticCharts[i],
											Number(ChartData[6]), Number(ChartData[6])	);
							var attr = {};
							var statisticalIndex = CutomizeMapRecords.statisticalIndex
									.split(",");
							// 构造infoTemplate的内容
							var content = "区域:${区域}<br/>";
							for ( var s = 0; s < statisticalIndex.length; s++) {
								content = content
										+ CityCodeToCn(statisticalIndex[s])
										+ ": ${"
										+ CityCodeToCn(statisticalIndex[s])
										+ "} <br/>";
								var item = ChartData[3].split(";")[i + 2];
								attr[CityCodeToCn(statisticalIndex[s])] = item
										.split(",")[s];
							}
							attr["区域"] = coordinateArr[i]["name"];
							/*//筛选出选择的制图区域
							if(CutomizeMapRecords.thematicMapArea!=undefined && CutomizeMapRecords.thematicMapArea!="null")
							if(CutomizeMapRecords.dataType!="TrafficDatatree"&& CutomizeMapRecords.thematicMapArea.indexOf(attr["区域"])<0){
								continue;
							}*/
							// 构造对应属性
							var myPoint = {
								"geometry" : {
									"x" : coordinateArr[i]["x"],
									"y" : coordinateArr[i]["y"],
									"spatialReference" : mapContainer.spatialReference
								},
								"attributes" : attr,
								"infoTemplate" : {
									"title" : "详细信息",
									"content" : content
								}
							};
							var gra = new Graphic(myPoint);
							gra.setSymbol(pfs);

							statisticLayer.add(gra);

						}
						//恢复地图坐标位置
						debugger
						editChart=edit(mapContainer,1);
						editChart.recoverGraphics();
						});
			
			defer1.resolve("success2");
		}});
	return defer1.promise();
}
// 生成分级专题图
function makeClass(data, type) {
	CutomizeMapRecords.colorId=data[12];
	var defer2 = $.Deferred();
	classLayer.clear();
	arr = eval("(" + data[10] + ")");
	lengendStr=data[11];
	var url = "";
	if (type == "district") {
		url = "resource/district.json";
	} else {
		url = "resource/street.json";
	}
	$.ajax({
		url:"./MakeClassMapLegend",
		type:"post",
		data:{lengendStr:lengendStr},
		success:function(res){
			$("#class_legend").attr('src',"data:image/png;base64,"+res); 
			// $("#statistic_legend").attr('src',"data:image/png;base64,"+"254,250,252,145.00~265.67@253,235,242,265.67~386.33@231,101,155,386.33~507.00");
		}
	});
	console.log(data[13])
	var classAreaGeo=eval("("+data[13]+")");
	
		require(
				[ "esri/geometry/Polygon",
						"esri/symbols/PictureMarkerSymbol",
						"esri/symbols/SimpleLineSymbol",
						"esri/Color", "esri/graphic",
						"esri/symbols/SimpleFillSymbol" ],
				function(Polygon, PictureMarkerSymbol,
						SimpleLineSymbol, Color, Graphic,
						SimpleFillSymbol) {
					for ( var i = 0; i < classAreaGeo.length; i++) {
						
						var geometry =classAreaGeo[i];
						var areaName=arr[i].code;
					var attr = {};
					var rgb = arr[i].color.split(",");
					var value=arr[i].value;
					attr.value=value;
					attr.areaName=areaName;
					var linecolor=[CutomizeMapRecords.lineColor["r"],CutomizeMapRecords.lineColor["g"],CutomizeMapRecords.lineColor["b"],CutomizeMapRecords.lineColor["a"]];
					var sfs = new SimpleFillSymbol(
							SimpleLineSymbol.STYLE_SOLID,
							new SimpleLineSymbol(
									SimpleLineSymbol.STYLE_SOLID,
									new Color(
											linecolor),
								CutomizeMapRecords.lineWidth),
							new Color([ parseInt(rgb[0]),
									parseInt(rgb[1]),
									parseInt(rgb[2]), 0.5 ]));

					// 构造对应属性
					var myPoint = {
						"geometry" : {
							"rings" : geometry.rings,
							"spatialReference" : mapContainer.spatialReference
						},
						"symbol" : sfs,
						"attributes" : attr,
						"infoTemplate" : {
							"title" : "详细信息",
							"content" :  "区域:${areaName}<br/>"+CityCodeToCn(CutomizeMapRecords.classificationIndex)+":${value}"
						}
					};
					var gra = new Graphic(myPoint);
					gra.setSymbol(sfs);
					classLayer.add(gra);
				}defer2.resolve("success1");
				});


	
	
	// $.ajaxSettings.async = false;
		return defer2.promise();
}
// 控制进度提示页面
function progressCtr(type) {
	// 如果是显示进度页面
	if (type == "show") {
		$("#progressloader").css('display','block'); 
		$('.progressloader')
				.loader(
						'show',
						'<img style="height:50px; width:50px;" src="./js/plugins/fakeLoader/img/loader1.gif">');

	}
	// 如果是关闭进度页面
	if (type == "hide") {
		$('.progressloader').loader('hide');
		$("#progressloader").css('display','none'); 

	}
}
// 获取国情数据
function getStatisticData(){
	var token=window.localStorage.getItem("token");
	var username=window.localStorage.getItem("username");
	var url=RESOURCE_URL+CutomizeMapRecords.resourceURL+"?hp_t="+token+"&currentLoginName="+username;
	$.ajax({
		type : "post",
		url : "./ProxyGetViewData",
		type : "post",
		data:{url:url},
		success : function(res) {
		}
	});
}

// 分级精加工界面
function makeClassPanel(){
	//开始编辑
	if(editChart!=undefined)
	editChart.startEdit();
	
	$(".steps-container").css("display","none");
	$("#data-content").empty();	
	
	var html1 =$("#city-color").clone(true);
	$("#data-content").append(html1);
	var ChartData=CutomizeMapRecords.statisticalData;
	var colorId=CutomizeMapRecords.colorId;
	var picid=parseInt(colorId)%10;
	if(0==picid){
		picid=10;
	}
	
	// 初始化选中的值
	$("#districtColor").attr("src","images/10/"+picid+".jpg"); 
	$("#districtColor").attr("alt",colorId); 
	if(ChartData[11]!=undefined){
		$("#districtClassNum").val(ChartData[11].split("@").length); 
	}else{
		$("#districtClassNum").val(3); 
	}
	
	// 调整模版
	var html3= '<label id ="Thematic_Class_colorOrder_label" style="font-weight: normal;margin-top:15px;    margin-left: 15px;">色板顺序:</label>'
	+ '<select id="Thematic_Class_colorOrder"'
	+ 'style="margin-left: 5px; height: 30px;width: 180px;">'
	+ '	<option value="0">正序</option>'
	+ '	<option value="1">逆序</option>'
	+ '</select> <br>';
	$("#data-content").append(html3);
	
	var html2='<div class="custom" id="customBreakDiv" style="display: block;"></div>';
	$("#data-content").append(html2);
	
	var html4=
	'<label style="font-weight: normal;margin-top:-15px;    margin-left: 15px;">边界颜色:</label>'
	+ '<input id="Thematic_Class_BorderC" type="hidden" class="demo" value="#c2c2c2" style="margin-left: 5px; height: 30px;width: 80px;"/><br/>'
	+ '<label style="font-weight: normal;margin-top: 15px;float:left;margin-left:15px;">边界宽度:</label>'
	+ '<label id="Thematic_Class_BorderH" style="font-weight: normal;margin-top: 15px;margin-left: 15px;">1</label><br>'
	+ '<input id="Thematic_Class_BorderHV" type="range" min="1" max="10" value="1" style="height: 30px;width:100%;"onchange="$(\'#Thematic_Class_BorderH\').html(this.value)"/><br/>'
	+ '<button id="Thematic_Class_make"  class="btn btn-primary" style="margin-left: 10px;width:40%;"'
	+ '	onclick="getClassColor();">确定</button>'
	+ '<button id="NextToStatistic" class="btn btn-primary" style="width:40%;margin-left:40px"'
	+ '	onclick="makeStatisticPanel();">下一步</button>'
	+ '<button id="saveMap"  class="btn btn-primary" style="display:none;width:80%;margin-left:20px;margin-top:25px"'
	+ '	onclick="saveCustomizeMap();">保存</button>';
	$("#data-content").append(html4);
	
	// 切换分级数目

	makeCustom();
	refreshColor();
	
	$("#districtClassNum").change(function(){
		makeCustom();
	});
	
	// li 切换了颜色
	$(".option-container li").click(
			function(e) {
				var currentImageUrl = $(this).find("img").attr('src');
				var currentImageVal = $(this).find("img").attr('alt');
				$(this).parents(".option-container").siblings().find(
						".selected-container").attr('src', currentImageUrl);
				$(this).parents(".option-container").siblings().find(
						".selected-container").attr('alt', currentImageVal);
				$(".option-container").find("img").removeClass("selected-image");
				$(this).find("img").addClass("selected-image");
				makeCustom();
			});
	
	// 切换了顺序
	$("#Thematic_Class_colorOrder").change(function(){
		makeCustom();
	});
	if(CutomizeMapRecords.classificationIndex==""){
		$("#Thematic_Class_make").attr("disabled","true");
	}
	if(CutomizeMapRecords.statisticalIndex==""){
		$("#NextToStatistic").attr("disabled","true");
		$("#saveMap").css("display","block");
	}
	//设置线色值
	var colorRGB=""+CutomizeMapRecords.lineColor.r+","+CutomizeMapRecords.lineColor.g+","+CutomizeMapRecords.lineColor.b;
	$("#Thematic_Class_BorderC").minicolors("value",RGBToHex(colorRGB));
}

// 刷新color 样式
function refreshColor(){
	// 初始化颜色对话框
	$('.demo').each(function() {
		$(this).minicolors({
			control : $(this).attr('data-control') || 'hue',
			defaultValue : $(this).attr('data-defaultValue') || '',
			inline : $(this).attr('data-inline') === 'true',
			letterCase : $(this).attr('data-letterCase') || 'lowercase',
			opacity : $(this).attr('data-opacity'),
			position : $(this).attr('data-position') || 'bottom left',
			change : function(hex, opacity) {
				if (!hex)
					return;
				if (opacity)
					hex += ', ' + opacity;
				try {
					
				} catch (e) {
				}
			},
			theme : 'default'
		});
	});
	
	// 设置颜色对话框长度
	var colorP = $(".minicolors-swatch-color");
	for ( var i = 0; i < colorP.length; i++) {
		var item = colorP[i];
		item.style.width = "140px";	
	}
}
// 自定义设色面板填充数据
function makeCustom(){
	$("#customBreakDiv").empty();
	//只是界面调整
	var p1=$("#districtClassNum").children('option:selected').val();
	var classNum=parseInt(p1);
	var colorId=$("#districtColor")[0].alt
	var isasc=$("#Thematic_Class_colorOrder").children('option:selected').val();
	
	var colors=generateColor(classNum+'', colorId, isasc);
	var text='';
	for(var i=1;i<classNum+1;i++){
		var color16=RGBToHex(colors[i-1][0]+','+colors[i-1][1]+','+colors[i-1][2]);
		var value='10';
		if(i<10){
			value='0'+i;
		}
		 text+='<p class="color-level"><span>第'+value+'级</span> <span>---</span><input type="hidden" value='+color16+' class="demo"></p>';
	}
	$("#customBreakDiv").append(text);
	refreshColor();
}

//确定了色带后需要重新恢复界面
function reMakeClasspanel(){
	makeClassPanel();
	//恢复colorId
	var colorId=CutomizeMapRecords.colorId;
	if(""!=colorId){
		var picid=parseInt(colorId)%10;
		if(0==picid){
			picid=10;
		}
		
		// 初始化选中的值
		$("#districtColor").attr("src","images/10/"+picid+".jpg"); 
		$("#districtColor").attr("alt",colorId); 
	}
	
	//恢复正逆序
	if(""!=CutomizeMapRecords.colorOrder){
		$("#Thematic_Class_colorOrder").val(CutomizeMapRecords.colorOrder);
	}
	
	//恢复线宽
	$("#Thematic_Class_BorderH").html(CutomizeMapRecords.lineWidth);
	$("#Thematic_Class_BorderHV").val(CutomizeMapRecords.lineWidth);
	
	//恢复线色值
	var colorRGB=""+CutomizeMapRecords.lineColor.r+","+CutomizeMapRecords.lineColor.g+","+CutomizeMapRecords.lineColor.b;
	$("#Thematic_Class_BorderC").minicolors("value",RGBToHex(colorRGB));
	var text='';
	if(""!=CutomizeMapRecords.gradeColors){
		// 确定了色带 返回界面色带恢复
		$("#customBreakDiv").empty();
		var color16s=CutomizeMapRecords.gradeColors.split(",");
		$("#districtClassNum").val(color16s.length);//恢复分级数目
		for(var i=0;i<color16s.length;i++){
			var value='10';
			if(i<9){
				value='0'+(i+1);
			}
			var color16=color16s[i];
			 text+='<p class="color-level"><span>第'+value+'级</span> <span>---</span><input type="hidden" value='+color16+' class="demo"></p>';
		}
		$("#customBreakDiv").append(text);
		refreshColor();
	}
}
// 精加工
function getClassColor(){
	CutomizeMapRecords.colorId=$("#districtColor")[0].alt;//记录下来
	CutomizeMapRecords.colorOrder=$("#Thematic_Class_colorOrder").children('option:selected').val();
	var gradeColors='';
	var classColors=$(".color-level input")
	for(var i=0;i<classColors.length;i++){
		if(''==gradeColors){
			gradeColors=classColors[i].value
		}else{
			gradeColors+=','+classColors[i].value
		}
	}
	CutomizeMapRecords.gradeColors=gradeColors;
	
	var borderColor=$("#Thematic_Class_BorderC")[0].value;
	borderColor=HexToRGB(borderColor);
	var colorJson={r:borderColor[0],g:borderColor[1],b:borderColor[2],a:borderColor[3]}
	CutomizeMapRecords.lineColor=colorJson;
	
	CutomizeMapRecords.lineWidth=$("#Thematic_Class_BorderHV")[0].value;
	makeThematicMap();
}
// RGB切换16位色彩
function RGBToHex(rgb){ 
	   var regexp = /[0-9]{0,3}/g;  
	   var re = rgb.match(regexp);// 利用正则表达式去掉多余的部分，将rgb中的数字提取
	   var hexColor = "#"; var hex = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'];  
	   for (var i = 0; i < re.length; i++) {
	        var r = null, c = re[i], l = c; 
	        var hexAr = [];
	        while (c > 16){  
	              r = c % 16;  
	              c = (c / 16) >> 0; 
	              hexAr.push(hex[r]);  
	         } hexAr.push(hex[c]);
	         if(l < 16&&l != ""){        
	             hexAr.push(0)
	         }
	       hexColor += hexAr.reverse().join(''); 
	    }  
	   // alert(hexColor)
	   return hexColor;  
	}

/* 16进制颜色转为RGB格式 */  

function HexToRGB(hex){
	 var sColor = hex.toLowerCase();  
	 var sColorChange = [];  
     for(var i=1; i<7; i+=2){  
         sColorChange.push(parseInt("0x"+sColor.slice(i,i+2)));    
     }
     sColorChange.push(1);
     return   sColorChange;
}

// 统计精加工界面
function makeStatisticPanel(){
	$("#data-content").empty();	
	var ChartData=CutomizeMapRecords.statisticalData;
	var chartId=ChartData[4];
	if(chartId==undefined){
		chartId="10101"
	}
	$("#districtChart").attr("src","images/chartIcon/0"+""+chartId+".png"); 
	$("#districtChart").attr("alt",chartId);
	if(ChartData[5]==undefined){
		colors=[];
	}else{
		var colors=ChartData[5].split(";");
	}
	// $(".option-container")[0].clone(true).appendTo($("#data-content"));
	$("#data-content").append( $($('.collapse-label')[2]).clone(true));
	$("#data-content").append( $($('.option-container')[1]).clone(true));
	var statisticIndex=CutomizeMapRecords.statisticalIndex.split(",");
	var elementColors="<div class=\"custom\"";
	for(var i=0;i<statisticIndex.length;i++){
		var color=colors[i];
		var colorstr=Number(color).toString(16) ;
		if(colorstr.length==4){
			colorstr="00"+colorstr;
		}
		elementColors=elementColors+"<p><span>"+statisticIndex[i]+"---</span>"+'<input  type="hidden" class="demo" value="#'+colorstr+'" style="margin-left: 5px; height: 30px;width: 80px;"/>'+"</p>"
		
	}
	var chartWidth="60";
	if(CutomizeMapRecords.polish_StatisticWidth!=""){
		chartWidth=CutomizeMapRecords.polish_StatisticWidth;
	}
	elementColors=elementColors+"</div>";
	$("#data-content").append( elementColors);
	var elementOther='<label style="font-weight: normal;margin-top: 20px;float:left;margin-left: 7px;">符号大小:</label>'
	+ '<label id="statisticBorder" style="font-weight: normal;margin-top: 20px;">'+chartWidth+'</label><br>'
	+ '<input  type="range" min="20" max="100" value="'+chartWidth+'" style="height: 30px;width:100%;"onchange="$(\'#statisticBorder\').html(this.value)"/><br/>'
	+ '<button id="Thematic_Statistic_make"  class="btn btn-primary" style="width:40%;"'
	+ '	onclick="reMakeStatisticMap();">确定</button>'
	+ '<button class="btn btn-primary" style="width:40%;margin-left:40px"'
	+ '	onclick="reMakeClasspanel();">上一步</button>'
	+ '<button class="btn btn-primary" style="width:80%;margin-left:20px;margin-top:25px"'
	+ '	onclick="saveCustomizeMap();">保存</button>';
	$("#data-content").append( elementOther);
	// 初始化颜色对话框
	$('.demo').each(function() {
		$(this).minicolors({
			control : $(this).attr('data-control') || 'hue',
			defaultValue : $(this).attr('data-defaultValue') || '',
			inline : $(this).attr('data-inline') === 'true',
			letterCase : $(this).attr('data-letterCase') || 'lowercase',
			opacity : $(this).attr('data-opacity'),
			position : $(this).attr('data-position') || 'bottom left',
			change : function(hex, opacity) {
				if (!hex)
					return;
				if (opacity)
					hex += ', ' + opacity;
				try {
					console.log(hex);
				} catch (e) {
				}
			},
			theme : 'default'
		});
	});
	// 设置颜色对话框长度
	var colorP = $(".minicolors-swatch-color");
	for ( var i = 0; i < colorP.length; i++) {
		var item = colorP[i];
		item.style.width = "140px";	
	}
	if(CutomizeMapRecords.statisticalIndex==""){
		$("#Thematic_Statistic_make").attr("disabled","true");
	}
}


// 重新绘制统计专题图
function reMakeStatisticMap(){
	var ChartData=CutomizeMapRecords.statisticalData;
	var colorP = $(".demo");
	var colors="";
	for ( var i = 0; i < colorP.length; i++) {
		var item = colorP[i].value.substring(1);
		item=parseInt(item,16);
		colors=colors+item+";";
	}
	colors=colors.substring(0,colors.length-1);
	var chartExt=$('#statisticBorder').text();
	// 将变化的参数保存到全局变量
	CutomizeMapRecords.polish_StatisticChartId=$("#districtChart").attr("alt");
	CutomizeMapRecords.polish_StatisticColors=colors,
	CutomizeMapRecords.polish_StatisticWidth=chartExt;
	// 更改参数，制作精加工地图
	ChartData[5]=colors;
	ChartData[4]=$("#districtChart").attr("alt"); 
	
	ChartData[0]="0,0,"+chartExt+","+chartExt;
	ChartData[1]="0,0,"+chartExt+","+chartExt;
	ChartData[6]=chartExt;
	ChartData[7]=chartExt;
	makeStatisticMap(ChartData);
}

// 保存定制的专题图
function saveCustomizeMap(){
    saveGraphics();
	var MyRecord = $.extend(true, {}, CutomizeMapRecords);
	MyRecord.statisticalData="";
	MyRecord.mapImage="images/mymap/map2.png";
	$.ajax({
        url: './SaveUserMap',
        type: 'POST',
        data: {
        	mapName: MyRecord.mapName.trim(),
        	userId:MyRecord.userName,
        	cutomizemaprecords:JSON.stringify(MyRecord) ,
        	isSave:SAVE_INDEX
        },
        success: function (res) {
        		 alert(res);   
        		 SAVE_INDEX++;
        },
        error: function (res) {
            alert("错误:"+res);	
        }
    });
}

//构造图层控制器
function buildLayersCtrl(root){
	var LayerCtrl=$(root).parent().append("<div id='layersCtrl' class='layerCtrlDiv'>"+
			"<p>图层控制"+
			"<button onclick=\"$(this).parent().parent().remove();$('#layersBtn').prop('disabled', false);\"></button>"+
			"</p>"+
			"</div>");
	var baseMapCtrl="<input id='baseMapCtrl'  type='checkbox' onchange='LayersCtrl(this)'> "+
	" <label for='baseMapCtrl'>底图图层</label><br/>";
	var classMapCtrl="<input id='classmapCtrl'  type='checkbox' onchange='LayersCtrl(this)'> "+
	" <label for='classmapCtrl'>分级图层</label><br/>";
	var statisticMapCtrl="<input id='statisticMapCtrl'  type='checkbox' onchange='LayersCtrl(this)'> "+
	" <label for='statisticMapCtrl'>统计图层</label><br/>";
	$("#layersCtrl").append(baseMapCtrl);
	$("#layersCtrl").append(classMapCtrl);
	$("#layersCtrl").append(statisticMapCtrl);
	//判断目前地图服务是否显示
	if(baseMapServiceLayer.visible==true){
		$("#baseMapCtrl").attr("checked","true"); 
	}
	if(classLayer.visible==true){
		$("#classmapCtrl").attr("checked","true"); 
	}
	if(statisticLayer.visible==true){
		$("#statisticMapCtrl").attr("checked","true"); 
	}
	$(root).prop('disabled', true);
	
}
//控制图层是否显示
function LayersCtrl(e){
	if(e.id=="baseMapCtrl"){
		if($("#baseMapCtrl").is(':checked')){
			baseMapServiceLayer.setVisibility(true);
		}
		else{
			baseMapServiceLayer.setVisibility(false);
		}
	}
	if(e.id=="classmapCtrl"){
		if($("#classmapCtrl").is(':checked')){
			classLayer.setVisibility(true);
		}
		else{
			classLayer.setVisibility(false);
		}
	}
	if(e.id=="statisticMapCtrl"){
		if($("#statisticMapCtrl").is(':checked')){
			statisticLayer.setVisibility(true);
		}
		else{
			statisticLayer.setVisibility(false);
		}
	}
}
