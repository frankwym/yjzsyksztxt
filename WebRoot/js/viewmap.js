/*全局变量声明*/
var LOGIN_URL="http://10.2.101.39/hgp/ztzt/login";
var RESOURCE_URL="http://10.2.101.39/hgp";
var SAVE_INDEX=2;//记录定制地图保存位置，0代表新建时与数据库查重，1代表粗加工保存，2代表精加工保存
//global variables,record all variables in customizeMap
var CutomizeMapRecords = {};

//地图图层
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
/* 页面加载完触发的函数 */
var editChart;
$(function() {
	 //checkLogin();
	
	$(".scroll-content").mCustomScrollbar();
	
	$("#data-content").empty();	
	$("#data-content").append(
	"<button id=\"mapSave\" type=\"button\" class=\"btn btn-success\"  onclick=\"window.location.href = 'mymap.html'\">返回</button>");
	$("#data-content").append(
	"<br/>");
	$("#data-content").append(
	"<button id=\"statisticFashioning\" type=\"button\" class=\"btn btn-primary\" >进入编辑</button>");
	GetViewMapInfo();
	
	
});
//控制进度提示页面
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

//获取URL的参数
function GetViewMapInfo() { 
//	var url = location.search; //获取url中"?"符后的字串 
//	var theRequest = new Object(); 
//	if (url.indexOf("?") != -1) { 
//	var str = url.substr(1); 
//	strs = str.split("&"); 
//	for(var i = 0; i < strs.length; i ++) { 
//	theRequest[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]); 
//	} 
//	} 
//	var Request = new Object(); 
//	Request = theRequest ;
	// 从url中获取参数
    function getURLParamsString(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) return decodeURI(r[2]);
        return null;
    }

	var mapName,userId,mapId; 
	mapId=getURLParamsString('mapId'); 
	mapName = getURLParamsString('mapName'); 
	userId = window.localStorage.getItem("username");
	var dataParams=null;
	if(mapId!=null){
		SAVE_INDEX=1;
		$("#mapSave").css("display","none");
		$("#statisticFashioning").unbind('click').click(function(){
			alert("编辑前请重新命名地图名称");
			$("#newMapModal").modal("show");
		});
		dataParams={
		       	 type:"getViewMapById",
		       	mapId:mapId        
		        };
	}else{
		$("#statisticFashioning").unbind('click').click(function(){
			reMakeClasspanel();
			//开始编辑
			editChart.startEdit();
		});
		dataParams={
		       	 type:"getViewMapByName",
		            userId: userId,
		            mapName:mapName        
		        };
	}
	$.ajax({
        url: './manageMyMap',
        type: 'POST',
        data: dataParams,
        success: function (res) {
        	CutomizeMapRecords=eval("("+res+")");
        	$("#thisMapName").html(CutomizeMapRecords.mapName);
        	require(
			[ "esri/map", "esri/layers/ArcGISTiledMapServiceLayer", "esri/dijit/Scalebar",
					"esri/layers/GraphicsLayer" ,"esri/dijit/Print","esri/dijit/editing/TemplatePicker","esri/toolbars/navigation","esri/toolbars/draw","esri/geometry/geometryEngine","drawExtension/Extension/DrawEx","drawExtension/plotDraw/DrawExt"],
			function(Map, ArcGISTiledMapServiceLayer,Scalebar, GraphicsLayer,Print,TemplatePicker,Navigation,Draw,geometryEngine,DrawEx,DrawExt) {
				drawLayer=new GraphicsLayer();
        				var lods=[
				{"level":0,"resolution" :0.0027465820824835504,"scale":1154287.49441732},
				{"level":1,"resolution":0.0013732910412417797,"scale":577143.747208662},
				{"level":2,"resolution":6.866455206208899E-4,"scale":288571.873604331},
				{"level":3,"resolution":3.433227603104438E-4,"scale":144285.936802165},
				{"level":4,"resolution":1.716613801552224E-4,"scale":72142.9684010827},
				{"level":5,"resolution":8.583069007761132E-5,"scale":36071.4842005414}
				];
				mapContainer = new Map("myMap",{logo:false,lods:lods});
        				statisticLayer = new GraphicsLayer();
        				classLayer=new GraphicsLayer();
        				baseMapServiceLayer = new ArcGISTiledMapServiceLayer(CutomizeMapRecords.baseMapUrl);
        				mapContainer.addLayer(baseMapServiceLayer);
        				mapContainer.addLayer(classLayer);	
        				mapContainer.addLayer(statisticLayer);	
						mapContainer.addLayer(drawLayer);
					    drawGraphics();
        				mapContainer.centerAndZoom({"x": 117.333103, "y": 39.294706, "spatialReference":mapContainer.spatialReference },0);
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
					mapContainer.centerAndZoom({"x": 117.353103, "y": 39.354706, "spatialReference":mapContainer.spatialReference },0);
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
				showMyMap();
        			});
        	
        },
        error: function (res) {
            alert("发生错误");	
        }
    });
	} 
//新建地图名称，用于二次编辑地图资源
function newMap1() {
	var mapName = $("#mapName").val();
	var mapDescribe = $("#mapDescribe").val();
	if (mapName == "" || mapDescribe == "") {
		alert("输入不许为空");
		return
	}
	
	
	 $.ajax({
            url: './SaveUserMap',
            type: 'POST',
            data: {
            	mapName: mapName,
            	userId:"default",
            	isSave:0
            },
            success: function (res) {
            	if(res!=""){
            		 alert(res);
            		 return;
            	}
            	CutomizeMapRecords.mapName=mapName;
            	CutomizeMapRecords.mapDescribe=mapDescribe;
            	$("#thisMapName").html(CutomizeMapRecords.mapName);
            	$("#newMapModal").modal("hide");
            	reMakeClasspanel();
            },
            error: function (res) {
                alert("错误:"+res);	
            }
        });
	
	
	
}
//显示我的地图
function showMyMap() {
	if(CutomizeMapRecords.gradeColors==""){//如果还没有选择分级颜色，则为第一次出现精加工界面 
		
		
	}
	
	
	//交通事故数据
	if(CutomizeMapRecords.dataType=="TrafficDatatree"){
		// get data of thematic map
		$.getJSON("resource/StatisticYearbook.json",function(nodes){
			nodes=nodes[0].nodes
			nodes=_.filter(nodes,function(temp){return  temp["text"]==CutomizeMapRecords.dataName;})[0].nodes
			$.getJSON("resource/district.json",
					function(areaJson) {
				areaJson=areaJson.features;
				$.getJSON("./GetStatisticYearBookData",
						function(data) {
					
					//过滤年份数据
					data= _.filter(data, function(temp){ return temp["YEAR"] == CutomizeMapRecords.dataName; });
					// 构造统计专题数据中每条记录的坐标数组
					var coordinate=[];
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
					var classData=[];
					
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
					
					debugger
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
							/*if(CutomizeMapRecords.gradeColors!=""){	
								makeClass(ChartData, "district");
								return;
							}*/
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
								//如果经过精加工处理
								if(CutomizeMapRecords.polish_StatisticColors!=""||CutomizeMapRecords.polish_StatisticChartId!=""||CutomizeMapRecords.polish_StatisticWidth!=""){
									ChartData[5]=CutomizeMapRecords.polish_StatisticColors;
									ChartData[4]=CutomizeMapRecords.polish_StatisticChartId; 
									var chartExt=CutomizeMapRecords.polish_StatisticWidth;
									ChartData[0]="0,0,"+chartExt+","+chartExt;
									ChartData[1]="0,0,"+chartExt+","+chartExt;
									ChartData[6]=chartExt;
									ChartData[7]=chartExt;
								}
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
			});
		});
	}
	
	if(CutomizeMapRecords.dataType=="UpdataDatatree"){
		// get data of thematic map
		var ChartData = "";
		$.ajax({
			url : "./makeChartParam",
			//async : false,
			data : {
				classificationIndex : CutomizeMapRecords.classificationIndex,
				statisticalIndex : CutomizeMapRecords.statisticalIndex,
				dataName : CutomizeMapRecords.dataName,
				dataType : CutomizeMapRecords.dataType,
				userName : CutomizeMapRecords.userName,
				mapFormatName : CutomizeMapRecords.mapFormatName,
				mapFormatContent : CutomizeMapRecords.mapFormatContent,
				gradeColors:CutomizeMapRecords.gradeColors
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
				//通过变换统计专题数据生成符合makeStatisticMap的参数，并执行制作统计专题图
					function transforMakeStatisticMap(){
						
						//如果经过精加工处理
						if(CutomizeMapRecords.polish_StatisticColors!=""||CutomizeMapRecords.polish_StatisticChartId!=""||CutomizeMapRecords.polish_StatisticWidth!=""){
							ChartData[5]=CutomizeMapRecords.polish_StatisticColors;
							ChartData[4]=CutomizeMapRecords.polish_StatisticChartId; 
							var chartExt=CutomizeMapRecords.polish_StatisticWidth;
							ChartData[0]="0,0,"+chartExt+","+chartExt;
							ChartData[1]="0,0,"+chartExt+","+chartExt;
							ChartData[6]=chartExt;
							ChartData[7]=chartExt;
						}
						
						$.when(makeStatisticMap(ChartData)).done(function(data){
					           progressCtr("hide");
					        });
					}
					// if there is classificationIndex
					if (CutomizeMapRecords.classificationIndex != "") {
						//geoArr = eval("(" + ChartData[10] + ")");
						 $.when(makeClass(ChartData,ChartData[2])).done(function(data){
							//为了让统计专题图的图层在分级专题图图层之上
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
		//async : false,
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
		//async : false,
		type : "post",
		success : function(res) {
			var coordinateArr = ChartData[9] ;
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
										+CityCodeToCn(statisticalIndex[s])
										+ "} <br/>";
								var item = ChartData[3].split(";")[i + 2];
								attr[CityCodeToCn(statisticalIndex[s])] = item
										.split(",")[s];
							}
							attr["区域"] = coordinateArr[i]["name"];
							//筛选出选择的制图区域
							if(CutomizeMapRecords.thematicMapArea!=undefined && CutomizeMapRecords.thematicMapArea!="null")
								if(CutomizeMapRecords.dataType!="CityDatatree"&& CutomizeMapRecords.thematicMapArea.indexOf(attr["区域"])<0){
									continue;
								}
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
						editChart=edit(mapContainer,1);
						editChart.recoverGraphics();
						});
			
			defer1.resolve("success2");
		}});
	return defer1.promise();
}


//生成分级专题图
function makeClass(data, type) {
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
			//$("#statistic_legend").attr('src',"data:image/png;base64,"+"254,250,252,145.00~265.67@253,235,242,265.67~386.33@231,101,155,386.33~507.00");
		}
	});
	//市情数据中的自定义数据
		if(type=="special"){
		
				require(
						[ "esri/geometry/Polygon",
								"esri/symbols/PictureMarkerSymbol",
								"esri/symbols/SimpleLineSymbol",
								"esri/Color", "esri/graphic",
								"esri/symbols/SimpleFillSymbol" ],
						function(Polygon, PictureMarkerSymbol,
								SimpleLineSymbol, Color, Graphic,
								SimpleFillSymbol) {
							for(var index=0;index<arr.length;index++){
							var attr = {};
							var rgb = arr[index].color.split(",");
							var value=arr[index].value;
							attr.value=value;
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
									"rings" : [[[116.8799169563902,39.55400452050826],[117.10209745245707,39.169176612869585], [116.65773646032333, 39.169176612869585],[116.8799169563902,39.55400452050826]]],
									"spatialReference" : mapContainer.spatialReference
								},
								"symbol" : sfs,
								"attributes" : attr,
								"infoTemplate" : {
									"title" : "详细信息",
									"content" : CityCodeToCn( CutomizeMapRecords.classificationIndex)+":${value}"
								}
							};
							var gra = new Graphic(myPoint);
							gra.setSymbol(sfs);
							classLayer.add(gra);
						}
						});
				defer2.resolve("success1");
				return defer2.promise();
		}

	
	//$.ajaxSettings.async = false;
	$.getJSON(
					url,
					function(data) {
						for ( var i = 0; i < data.features.length; i++) {
							var name = data.features[i].attributes.NAME;
							//筛选出选择的制图区域
							if(CutomizeMapRecords.thematicMapArea!=undefined && CutomizeMapRecords.thematicMapArea!="null")
							if(CutomizeMapRecords.thematicMapArea.indexOf(name)<0){
								continue;
							}
							var pac = data.features[i].attributes.PAC;
							if(CutomizeMapRecords.dataType=="CityDatatree"){
								pac = data.features[i].attributes.NAME;
							}
							var index = -1;
							for ( var j = 0; j < arr.length; j++) {
								var a = arr[j].code + "";
								var b = pac + "";
								if (a == b) {
									index = j;
									break;
								}
							}
							var geometry = data.features[i].geometry;
							var areaName=data.features[i].attributes.NAME;
							if(index==-1){
								continue;
							}
							require(
									[ "esri/geometry/Polygon",
											"esri/symbols/PictureMarkerSymbol",
											"esri/symbols/SimpleLineSymbol",
											"esri/Color", "esri/graphic",
											"esri/symbols/SimpleFillSymbol" ],
									function(Polygon, PictureMarkerSymbol,
											SimpleLineSymbol, Color, Graphic,
											SimpleFillSymbol) {
										
										var attr = {};
										var rgb = arr[index].color.split(",")
										var value=arr[index].value
										attr.value=value;
										attr.areaName=areaName;
										CutomizeMapRecords.lineColor=eval(CutomizeMapRecords.lineColor);
										var linecolor=[CutomizeMapRecords.lineColor["r"],CutomizeMapRecords.lineColor["g"],CutomizeMapRecords.lineColor["b"],CutomizeMapRecords.lineColor["a"]];
										var sfs = new SimpleFillSymbol(
												SimpleLineSymbol.STYLE_SOLID,
												new SimpleLineSymbol(
														SimpleLineSymbol.STYLE_SOLID,
														new Color(linecolor),
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

									});

						}

						defer2.resolve("success1");});
	return defer2.promise();
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
	$("#Thematic_Class_BorderC")
	var colorRGB=""+CutomizeMapRecords.lineColor.r+","+CutomizeMapRecords.lineColor.g+","+CutomizeMapRecords.lineColor.b;

	$("#Thematic_Class_BorderC").minicolors("value",RGBToHex(colorRGB));
	var text='';
	if(""!=CutomizeMapRecords.gradeColors){
		// 确定了色带 返回界面色带恢复
		$("#customBreakDiv").empty();
		var color16s=CutomizeMapRecords.gradeColors.split(",")
		$("#districtClassNum").val(color16s.length)//恢复分级数目
		for(var i=0;i<color16s.length;i++){
			var value='10';
			if(i<9){
				value='0'+(i+1);
			}
			var color16=color16s[i]
			 text+='<p class="color-level"><span>第'+value+'级</span> <span>---</span><input type="hidden" value='+color16+' class="demo"></p>';
		}
		$("#customBreakDiv").append(text);
		refreshColor();
	}
	
}

//精加工
function getClassColor(){
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
	showMyMap();
}
// 分级精加工界面
function makeClassPanel(){
	$(".steps-container").css("display","none");
	$("#data-content").empty();	
	
	var html1 =$("#city-color").clone(true);
	$("#data-content").append(html1);
	var ChartData=CutomizeMapRecords.statisticalData;
	var colorId=1005;
	if(ChartData!="")
	if(ChartData[12]!=undefined){
		colorId=ChartData[12].match("[0-9]*");//提取数据部分
	}
	
	var picid=parseInt(colorId)%10;
	if(0==picid){
		picid=10;
	}
	
	//初始化选中的值
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
	+ '	<option value="1">逆序</option>				'
	+ '</select> <br>';
	$("#data-content").append(html3);
	
	var html2='<div class="custom" id="customBreakDiv" style="display: block;"></div>';
	$("#data-content").append(html2)
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
	//设置线色值
	var colorRGB=""+CutomizeMapRecords.lineColor.r+","+CutomizeMapRecords.lineColor.g+","+CutomizeMapRecords.lineColor.b;
	$("#Thematic_Class_BorderC").minicolors("value",RGBToHex(colorRGB));
	if(CutomizeMapRecords.classificationIndex==""){
		$("#Thematic_Class_make").attr("disabled","true");
	}
}

//刷新color 样式
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


//统计精加工界面
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

	//$(".option-container")[0].clone(true).appendTo($("#data-content"));
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
	elementColors=elementColors+"</div>";
	var chartWidth="60";
	if(CutomizeMapRecords.polish_StatisticWidth!=""){
		chartWidth=CutomizeMapRecords.polish_StatisticWidth;
	}
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

//重新绘制统计专题图
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
	//将变化的参数保存到全局变量
	CutomizeMapRecords.polish_StatisticChartId=$("#districtChart").attr("alt");
	CutomizeMapRecords.polish_StatisticColors=colors,
	CutomizeMapRecords.polish_StatisticWidth=chartExt;
	//更改参数，制作精加工地图
	ChartData[5]=colors;
	ChartData[4]=$("#districtChart").attr("alt"); 
	
	ChartData[0]="0,0,"+chartExt+","+chartExt;
	ChartData[1]="0,0,"+chartExt+","+chartExt;
	ChartData[6]=chartExt;
	ChartData[7]=chartExt;
	makeStatisticMap(ChartData);
}

//保存定制的专题图
function saveCustomizeMap(){
	saveGraphics();
	
	//保存专题图的饼的位置
	var editChartData=editChart.saveGraphics();
	CutomizeMapRecords.editChartData=editChartData;
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

				statisticalIndex=CutomizeMapRecords.statisticalIndex.split(",");
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
