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

//编辑饼状图
var editChart;
/* 页面加载完触发的函数 */
$(function() {
	 //checkLogin();
/*	var a=$($(".main-section")[0]).css("height");
	var b=a.substring(0,a.length-2)-100;
	$("#mapContainer").css("height",b+"px");
	var c=$("#mapContainer").css("marginLeft");*/
	//$(".scroll-content").mCustomScrollbar();
	
	buildInterfaceOfPrintMat();
	buildInterfaceOfParameters();
	GetViewMapInfo();
	//prntMap();
	initPrint();
	var slider = new Slider('#textFontSize', {
		formatter: function(value) {
			return value;
		}
	});
	var scalebox=($('#myMap').children('div'))[6];
	var mubanbox=$('#mCSB_2_container').find('div');
	$(mubanbox[0]).click(function(){
		$(scalebox).css('left','25px');
	});
	$(mubanbox[1]).click(function(){
		$(scalebox).css('left','25px');
	});
	$(mubanbox[2]).click(function(){
		$(scalebox).css('left','285px');
	});
	$(mubanbox[3]).click(function(){
		$(scalebox).css('left','285px');
	});
	$(mubanbox[4]).click(function(){
		$(scalebox).css('right','25px');
	});
	$(mubanbox[5]).click(function(){
		$(scalebox).css('left','285px');
	});
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

// 从url中获取参数
function getURLParamsString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return decodeURI(r[2]);
    return null;
}

//获取URL的参数
function GetViewMapInfo() { 
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
		$("#statisticFashioning").unbind('click').click(function(){reMakeClasspanel();});
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
				
        				var lods=[
				{"level":0,"resolution" :0.0027465820824835504,"scale":1154287.49441732},
				{"level":1,"resolution":0.0013732910412417797,"scale":577143.747208662},
				{"level":2,"resolution":6.866455206208899E-4,"scale":288571.873604331},
				{"level":3,"resolution":3.433227603104438E-4,"scale":144285.936802165},
				{"level":4,"resolution":1.716613801552224E-4,"scale":72142.9684010827},
				{"level":5,"resolution":8.583069007761132E-5,"scale":36071.4842005414}
				];
				drawLayer=new GraphicsLayer();
				mapContainer = new Map("myMap",{logo:false,lods:lods,slider:false});
        				statisticLayer = new GraphicsLayer();
        				classLayer=new GraphicsLayer();
        				baseMapServiceLayer = new ArcGISTiledMapServiceLayer(CutomizeMapRecords.baseMapUrl,{
        					visible: false
        			    });
        				mapContainer.addLayer(baseMapServiceLayer);
        				mapContainer.addLayer(classLayer);	
        				mapContainer.addLayer(statisticLayer);	
						mapContainer.addLayer(drawLayer);	
						drawGraphics();
						mapContainer.on("load",function(){
							mapContainer.centerAndZoom({"x": 117.32, "y": 39.27, "spatialReference":mapContainer.spatialReference },0);
						});
        				
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
					mapContainer.centerAndZoom({"x": 117.32, "y": 39.27, "spatialReference":mapContainer.spatialReference },0);
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
        				showMyMap();
        			});
        	
        },
        error: function (res) {
            alert("发生错误");	
        }
    });
	} 
//显示我的地图
function showMyMap() {
	if(CutomizeMapRecords.gradeColors==""){//如果还没有选择分级颜色，则为第一次出现精加工界面 
		
		
	}
	
	//统计年鉴数据
	if(CutomizeMapRecords.dataType=="TrafficDatatree"){
		// get data of thematic map
		debugger
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
							
			        
						
							//mapContainer.centerAndZoom({"x": 117.18078975541013, "y": 39.23016133153902, "spatialReference":mapContainer.spatialReference },1);
							
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
				
					//mapContainer.centerAndZoom({"x": 117.18078975541013, "y": 39.23016133153902, "spatialReference":mapContainer.spatialReference },1);
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

//生成统计专题图
function makeStatisticMap(ChartData){	
	CutomizeMapRecords.statisticalData=ChartData;
	var defer1 = $.Deferred();
	statisticLayer.clear();

	var mapId=getURLParamsString('mapId'); 
	var mapName = getURLParamsString('mapName'); 
	var userId = window.localStorage.getItem("username");
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
			isLabel : "false",
			userId:userId,
			mapName:mapName
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
										+ CityCodeToCn(statisticalIndex[s])
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
						debugger
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
	
	var mapId=getURLParamsString('mapId'); 
	var mapName = getURLParamsString('mapName'); 
	var userId = window.localStorage.getItem("username");
	$.ajax({
		url:"./MakeClassMapLegend",
		type:"post",
		data:{
			lengendStr:lengendStr,
			userId:userId,
			mapName:mapName
			},
		success:function(res){
			$("#class_legend").attr('src',"data:image/png;base64,"+res); 
			//$("#statistic_legend").attr('src',"data:image/png;base64,"+"254,250,252,145.00~265.67@253,235,242,265.67~386.33@231,101,155,386.33~507.00");
		}
	});
	
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
										//gra.setAttributes(attr);
										classLayer.add(gra);

									});

						}

						defer2.resolve("success1");});
	return defer2.promise();
}
//构建打印模板界面
function buildInterfaceOfPrintMat() {
	$("#data-content").empty();
	//$("#data-content").append("<div id='mapPanel1Scroll' class=\"scroll-content\" ></div>");
	$("#data-content").append("<div id='mapPanel1' class=\"panel\" style=\"height:400px;\"></div>");
	$("#mapPanel1")
			.append(
					'<div class="panel-heading" style="padding:0px" role="tab" id="mapPanel1Head"></div>');
	$("#mapPanel1Head")
			.append(
					"<div id=\"mapPanel1HeadTitle\" style=\"margin-bottom:0px\"  class=\"panel\"></div>");
	$("#mapPanel1HeadTitle").append('<h4 class="panel-title"></h4>');
	$("#mapPanel1HeadTitle")
			.children("h4")
			.append(
					'<button class="map-div-title"  data-toggle="collapse" data-parent="#accordion1" href="#mapPanel1Collapse" aria-expanded="true" aria-controls="mapPanel1Collapse">打印模板</button>');
	$("#mapPanel1")
			.append(
					'<div id="mapPanel1Collapse" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="mapPanel1Head"></div>');
	$("#mapPanel1Collapse").append('<div class="panel-body scroll-content" style="height:360px;"></div>');

	var mapThumbnailData = [
			["images/printFormat/mxd1.png","模板1",
					"http://server.arcgisonline.com/arcgis/rest/services/ESRI_Imagery_World_2D/MapServer" ],
			["images/printFormat/mxd2.png","模板2",
				"http://server.arcgisonline.com/arcgis/rest/services/ESRI_Imagery_World_2D/MapServer" ],
			["images/printFormat/mxd3.png","模板3",
				"http://server.arcgisonline.com/arcgis/rest/services/ESRI_Imagery_World_2D/MapServer"  ],
            ["images/printFormat/mxd4.png","模板4",
				"http://server.arcgisonline.com/arcgis/rest/services/ESRI_Imagery_World_2D/MapServer" ],
				["images/printFormat/mxd5.png","模板5",
					"http://server.arcgisonline.com/arcgis/rest/services/ESRI_Imagery_World_2D/MapServer" ],
					["images/printFormat/mxd6.png","模板6",
						"http://server.arcgisonline.com/arcgis/rest/services/ESRI_Imagery_World_2D/MapServer" ]

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
			$("#mapPanel1Collapse").children(".panel-body").append(
					mapThumbnailElement);
		} else {
			var mapThumbnailElement = '<div class="map-thumbnail"><i class="map-checked-icon map-checked-icon fa fa-check-circle" aria-hidden="true"></i><img src="'
					+ mapThumbnailData[i][0]
					+ '" alt="'
					+ mapThumbnailData[i][2]
					+ '"><p>'
					+ mapThumbnailData[i][1]
					+ '</p></div>';
			$("#mapPanel1Collapse").children(".panel-body").append(
					mapThumbnailElement);
		}

	}
	/*$("#mapPanel1Collapse").children(".panel-body").append(
	"<div style='background:red;width:410px;height:300px'></div>");*/
	$(".scroll-content").mCustomScrollbar();
	$(".map-thumbnail").click(
					function() {
						// when select the baseMap, show the check symbol;
						$(".map-checked-icon").hide();
						$($(this).children(".map-checked-icon")[0]).show();
						var fomatName = $($(this).children("p")[0]).html();
						switch (fomatName){
						case "模板1":
							PrintTemplateMxd="mxd1";
							$("#map-print-legend").attr("class","map-print-legend");
							$("#map-print-chart").attr("class","map-print-chart");
							$("#map-print-text").attr("class","map-print-text");
							$("#arrow").attr("class","arrow");
							break;
						case "模板2":
							PrintTemplateMxd="mxd2";
							$("#map-print-legend").attr("class","map-print-legend2");
							$("#map-print-chart").attr("class","map-print-chart2");
							$("#map-print-text").attr("class","map-print-text2");
							$("#arrow").attr("class","arrow2");
							break;
						case "模板3":
							PrintTemplateMxd="mxd3";
							$("#map-print-legend").attr("class","map-print-legend3");
							$("#map-print-chart").attr("class","map-print-chart3");
							$("#map-print-text").attr("class","map-print-text3");
							$("#arrow").attr("class","arrow3");
							break;
						case "模板4":
							PrintTemplateMxd="mxd4";
							$("#map-print-legend").attr("class","map-print-legend4");
							$("#map-print-chart").attr("class","map-print-chart4");
							$("#map-print-text").attr("class","map-print-text4");
							$("#arrow").attr("class","arrow4");
							break;
						case "模板5":
							PrintTemplateMxd="mxd5";
							$("#map-print-legend").attr("class","map-print-legend5");
							$("#map-print-chart").attr("class","map-print-chart5");
							$("#map-print-text").attr("class","map-print-text5");
							$("#arrow").attr("class","arrow5");
							break;
						case "模板6":
							PrintTemplateMxd="mxd6";
							$("#map-print-legend").attr("class","map-print-legend6");
							$("#map-print-chart").attr("class","map-print-chart6");
							$("#map-print-text").attr("class","map-print-text6");
							$("#arrow").attr("class","arrow6");
							break;
						}
						/*$("#titleName").val("");
						$("#textDetail").val("");
						$("#file-Portrait").val("");*/
					});
	 $($('.map-thumbnail')[3]).trigger("click");
	$(".map-thumbnail p").hover(
			function() {
				$(this).css("color","red");
			},function() {
				$(this).css("color","black");
			});
	$(".map-thumbnail p").click(
			function() {
				var imgSrc=$($(this).prev()).attr('src');
				$("#mapPrintImg").attr("src",imgSrc);
				$("#showFomatModal").modal("show");
			});
}//构建打印模板界面
//构建打印参数
function buildInterfaceOfParameters() {	
	$("#data-content").append("<div id='mapPanel2' class=\"panel\" style=\"height:200px;\"></div>");
	$("#mapPanel2")
	.append(
			'<div class="panel-heading" style="padding:0px" role="tab" id="mapPanel2Head"></div>');
	$("#mapPanel2Head")
	.append(
			"<div id=\"mapPanel2HeadTitle\" style=\"margin-bottom:0px\"  class=\"panel\"></div>");
	$("#mapPanel2HeadTitle").append('<h4 class="panel-title"></h4>');
	
	$("#mapPanel2HeadTitle")
	.children("h4")
	.append(
			'<button class="map-div-title"  data-toggle="collapse" data-parent="#accordion2" href="#mapPanel2Collapse" aria-expanded="true" aria-controls="mapPanel2Collapse">打印参数</button>');
$("#mapPanel2")
	.append(
			'<div id="mapPanel2Collapse" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="mapPanel2Head"></div>');
$("#mapPanel2Collapse").append('<div id="panel2" class="panel-body"></div>');
var element='<div id="titleGroup" class="form-group"><label for="titleName" class="col-sm-12 control-label" style="margin-top:10px;margin-bottom:5px">地图标题：</label>'+
'<div class="col-sm-12"><input  class="form-control" id="titleName" placeholder="请输入地图标题" style="margin-top:5px;margin-bottom:5px"></div></div>'+
'<div id="textGroup" class="form-group"><label for="textDetail" class="col-sm-12 control-label" style="margin-top:5px;margin-bottom:5px">文字说明：</label>'+
'<div class="col-sm-12"><textarea  class="form-control" id="textDetail" placeholder="请输入文字说明，170字以内" style="margin-top:5px;margin-bottom:5px;height:130px;max-width:100%"></textarea></div></div>'+
'<div id="textFont" class="form-group"><label for="textFont" class="col-sm-6 control-label" style="margin-top:5px;margin-bottom:5px;padding-right: 0px;width: 85px;">文字字体：</label>'+
'<div class="col-sm-6" style="padding-left: 15px;clear: both;"><select id="fontValue" class="selectpicker" style="margin-top:5px;margin-bottom:5px;display:block"><option value ="SimSun">宋体</option> <option value ="SimHei">黑体</option value ="Microsoft YaHei"><option>微软雅黑</option></select></div></div>'+
'<div id="textFontSizeDiv" class="form-group"><label for="textFontSizeLabel" class="col-sm-12 control-label" style="margin-top:10px;margin-bottom:7px">文字大小：</label>'+
'<div class="col-sm-6" style="margin-bottom: 10px;"><input id="textFontSize" data-slider-id="ex3Slider" type="text" data-slider-min="14" data-slider-max="40" data-slider-step="2" data-slider-value="24" style="margin-left: 5px;margin-top:5px;margin-bottom:5px;height: 30px;width:220px;"/></div></div>'+
'<div id="textFontColorDiv" class="form-group"><label for="textFontColorLabel" class="col-sm-12 control-label" style="margin-top:5px;margin-bottom:7px">文字颜色：</label>'+
'<div class="col-sm-6" style="margin-bottom: 10px;"><input id="textFontColor" type="hidden" class="demo minicolors-input" value="#000000" style="margin-left: 5px;margin-top:5px;margin-bottom:5px;height: 30px;width: 100px;" size="7"/></div></div>'+
//出图格式
/*'<div id="textbox" class="form-group"><label for="textbox" class="col-sm-12 control-label" style="margin-top:15px;margin-bottom:7px">文本框颜色：</label>'+
'<div class="col-sm-6" style="margin-bottom: 10px;"><input id="textboxColor" type="hidden" class="demo minicolors-input" value="#EBF2E0" style="margin-left: 5px;margin-top:5px;margin-bottom:5px;height: 30px;width: 100px;" size="7"/></div></div>'+*/

'<div id="chartGroup" class="form-group"><label for="chartDetail" class="col-sm-12 control-label" style="margin-top:10px;margin-bottom:5px;">上传图表：</label>'+
'<div class="col-sm-12"><input id="file-Portrait" type="file" style="margin-top:5px;margin-bottom:5px;"></div></div>'+
'<div id="mapstyle" class="form-group"><label for="mapstyle" class="col-sm-12 control-label" style="margin-top:15px;margin-bottom:7px">输出格式：</label>'+
'<div class="col-sm-6" style="padding-left: 15px;clear: both;"><select id="mapstyleValue" class="selectpicker" style="margin-top:5px;margin-bottom:5px;display:block"><option value ="png">PNG格式</option> <option value ="pdf">PDF格式</option><option value ="jpg">JPEG格式</option><option  value ="bmp">BMP格式</option></select></div></div>'+

'<div style="padding-left: 10px;padding-right: 10px;"><button id="printBtn" class="col-sm-12 btn btn-success" style="margin-top:20px;margin-bottom: 20px;">打印</button></div></div>';

$("#mapPanel2Collapse").children(".panel-body").append(
		element);
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
	// 设置颜色对话框长度
	var colorP = $(".minicolors-swatch-color");
	for ( var i = 0; i < colorP.length; i++) {
		var item = colorP[i];
		item.style.width = "220px";	
		item.style.borderRadius="4px";
		item.style.height = "30px";	
		
	}
});
initFileInput("file-Portrait", "./UploadImage"); 
	//$(".scroll-content").mCustomScrollbar();
$("#textDetail").bind('input propertychange', function() {
	$("#print-text").html($("#textDetail").val());
});
$("#textFontSize").bind('change', function() {
	$("#print-text").css("font-size",$("#textFontSize").val()+"px");
});
$("#textFontColor").bind('change', function() {
	$("#print-text").css("color",$("#textFontColor").val());
});
$("#titleName").bind('input propertychange', function() {
	$("#print-title").html($("#titleName").val());
});
$("#textFont").bind('change', function() {
	$("#print-text").css("font-family",$("#textFont option:selected").val());
});

/*$("#textboxColor").bind('change', function() {
	$("#map-print-text").css("background-color",$("#textboxColor").val());
});*/
}

//用于打印的模板MXD
var PrintTemplateMxd;
function initPrint(){
	
	$("#printBtn").click(function() {
		var mapTitle=$("#titleName").val();
        var mapText=$("#textDetail").val();
        var outType=$("#mapstyleValue").val();
        
        if(mapTitle==""||mapText==""){
        	alert("请完善信息");
        	return;
        }
		progressCtr("show");
		
        //用换行符替换，否则会出现换两行现象
		 mapText=mapText.replace(/\r\n/g, "\n");
	        var textFont=$("#textFont option:selected").val();
	        var textSize=$("#textFontSize").val();
	        var textColor=$("#textFontColor").val();
	        var legendPath="";
	        var chartPath="";
	        textColor='red="'+parseInt(textColor.substr(1, 2),16)+'" green="'+parseInt(textColor.substr(3, 2),16)+'" blue="'+parseInt(textColor.substr(5, 2),16)+'"';
			
		//合并分级图例和专题图例
		var mapId=getURLParamsString('mapId'); 
		var mapName = getURLParamsString('mapName'); 
		var userId = window.localStorage.getItem("username");
		$.ajax({
			url:"./PrintServer2",
			type:"post",
			data:{
				mapId:mapId,
				userId:userId,
				mapName:mapName
				},
			success:function(res){
				legendPath=eval("("+res+")").msg;
				var isChart=legendPath.substring(legendPath.length-1, legendPath.length);
				legendPath=legendPath.substring(0,legendPath.length-2);
				if(isChart=="1")
				chartPath=legendPath.substring(0,legendPath.indexOf("legend"))+"pic.png";

				esriConfig.defaults.io.proxyUrl = "http://localhost:8080/TJCH_DLGQ/proxy.jsp";
				require([
				          "esri/tasks/PrintTemplate", "esri/tasks/PrintParameters","esri/tasks/PrintTask"
				       ], function(PrintTemplate, PrintParameters,PrintTask ) {
					
			       var printTask1 = new PrintTask('http://localhost:6080/arcgis/rest/services/mapPrint/GPServer/tool2?text_color='+textColor+'&text_font='+textFont+'&legend_path='+legendPath+'&chart_path='+chartPath+'&mxd_name='+PrintTemplateMxd+'&text_text='+mapText+'&text_size='+textSize+"&outputtype="+outType, {async: true});
			       //var printTask1 = new PrintTask('http://localhost:6080/arcgis/rest/services/15/GPServer/tool2?legend_path=AAAR7nAAFAAAADGAAC/legend_grade.png&chart_path=AAAR7nAAFAAAADGAAC/legend_sta.png&mxd_name=mxd2', {async: true});
			        var params = new PrintParameters();
				        params.map =  mapContainer;
				        
				        var template = new PrintTemplate();
				       /*template.exportOptions = {
				          width: 500,
				          height: 400,
				         dpi: 96
				        };*/	     
				        template.preserveScale = false;
				        /*template.layoutOptions={"titleText":"地铁图"MapTitle,"customTextElements":[
					                                                     {"text2": "Eastfdaf",
					                                                     "color":"red"},
					                                                     {"areatext": "Wefdsafst","color":"red","background":"white"},
					                                                     {"subtitle": "Check out this great map!"},
					                                                     {"disclaimer": "Don't sue me if you get lost using this map."}
					                                                 ]};*/
				        template.layoutOptions={"titleText":mapTitle,"customTextElements":[ {"MapTitle": mapTitle}]};
				        params.template = template;
				       
				        var printResult=function(res){
				        	progressCtr("hide");
				        
				        	console.log(res);
				        	window.open(res.url); };
				        var printError=function(res){
				        	progressCtr("hide");
				        	console.log(res);
				        };
				        printTask1.execute(params, printResult,printError);
				});
				//$("#statistic_legend").attr('src',"data:image/png;base64,"+"254,250,252,145.00~265.67@253,235,242,265.67~386.33@231,101,155,386.33~507.00");
			}
		});
		
		
		
		
		
		/*var printmap = printMap(mapContainer, "printBtn");
		var mapId=getURLParamsString('mapId'); 
		var mapName = getURLParamsString('mapName'); 
		var userId = window.localStorage.getItem("username");
		printmap.printMapThumbtnail(function(event) {
			console.log(event);
	
			var iframe=$("<iframe>");
			iframe.attr("style","display:none");
			iframe.attr("name","printIframe");
			iframe.load(function(){
				progressCtr("hide");;
				});
			$("body").append(iframe);//将表单放置在web中
			var form=$("<form>");//定义一个form表单
			form.attr("style","display:none");
			form.attr("target","printIframe");
			form.attr("method","post");
			form.attr("action","./PrintServer");
			var input1=$("<input>");
			input1.attr("type","hidden");
			input1.attr("name","url");
			input1.attr("value" ,event.url);
			var input2=$("<input>");
			input2.attr("type","hidden");
			input2.attr("name","mapName");
			input2.attr("value" ,mapName);
			var input3=$("<input>");
			input3.attr("type","hidden");
			input3.attr("name","userId");
			input3.attr("value" ,userId);
			var input4=$("<input>");
			input4.attr("type","hidden");
			input4.attr("name","mapId");
			input4.attr("value" ,mapId);
			var input5=$("<input>");
			input5.attr("type","hidden");
			input5.attr("name","dpi");
			input5.attr("value" ,3);
			var input6=$("<input>");
			input6.attr("type","hidden");
			input6.attr("name","text");
			input6.attr("value" ,$("textarea").val());
			var input7=$("<input>");
			input7.attr("type","hidden");
			input7.attr("name","textFont");
			input7.attr("value" ,$("#textFont option:selected").val());
			var input8=$("<input>");
			input8.attr("type","hidden");
			input8.attr("name","textBackGroundColor");
			input8.attr("value" ,$("#backGroundColor").val());
			var input9=$("<input>");
			input9.attr("type","hidden");
			input9.attr("name","textFontColor");
			input9.attr("value" ,$("#textFontColor").val());
			
			$("body").append(form);//将表单放置在web中
			form.append(input1);
			form.append(input2);form.append(input3);form.append(input4);form.append(input5);form.append(input6);form.append(input7);form.append(input8);form.append(input9);
			form.submit();//表单提交 
		});*/
	});
}
function initFileInput(ctrlName, uploadUrl) {
	var control = $('#' + ctrlName);

	// 初始化上传控件的样式
	control.fileinput({
		language : 'zh', // 设置语言
		uploadUrl : uploadUrl, // 上传的地址
		allowedFileExtensions : [ 'jpg', 'png' ],// 接收的文件后缀
		showUpload : false, // 是否显示上传按钮
		showCaption : true,// 是否显示标题
		browseClass : "btn btn-primary", // 按钮样式
		dropZoneEnabled : false,// 是否显示拖拽区
		previewFileIcon : "",
		// maxFileSize: 0,//单位为kb，如果为0表示不限制文件大小
		// minFileCount: 0,
		maxFileCount : 1, // 表示允许同时上传的最大文件个数
		enctype : 'multipart/form-data',
		validateInitialCount : true,
		previewFileIcon : "<i class='glyphicon glyphicon-king'></i>",
		msgFilesTooMany : "选择上传的文件数量({n}) 超过允许的最大数值{m}！",
		uploadExtraData : function(previewId, index) { //额外参数的关键点
			var obj = {};
			var mapId=getURLParamsString('mapId'); 
			var mapName = getURLParamsString('mapName'); 
			var userId = window.localStorage.getItem("username");
			obj.mapId =mapId;
			obj.mapName =mapName;
			obj.userId =userId;
			return obj;
		}
	}).on("filebatchselected", function(event, files) {
		$(this).fileinput("upload");
		// 隐藏预览效果
		$(".fileinput-remove-button").hide()
		$(".file-preview").hide()
	}).on("fileuploaded", function(event, data) {
		debugger
		if (data.response) {
			$("#map-print-chart").attr("src",data.response.msg);
		}
	});
}

function getPath(obj)    
{    
  if(obj)    
    {    
   
    if (window.navigator.userAgent.indexOf("MSIE")>=1)    
      {    
        obj.select();    
   
      return document.selection.createRange().text;    
      }    
   
    else if(window.navigator.userAgent.indexOf("Firefox")>=1)    
      {    
      if(obj.files)    
        {    
   
        return obj.files.item(0).getAsDataURL();    
        }    
      return obj.value;    
      }    
    return obj.value;    
    }    
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
