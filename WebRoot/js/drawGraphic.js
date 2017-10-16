var  geometry_ServiceUrl= "http://115.28.75.54:6080/arcgis/rest/services/Utilities/Geometry/GeometryServer";//arcgis测量服务url
var WKID=4490;//坐标系
//地图涂鸦功能

dojo.require("esri.toolbars.draw");
dojo.require("esri.toolbars.edit");
dojo.require("esri.dijit.editing.TemplatePicker");
dojo.require("esri.tasks.LengthsParameters");
dojo.require("esri.tasks.AreasAndLengthsParameters");

var drawing;
var measure;

var div=$('#tuyapane');
var htmltuya='<div id="tuyapane" style="box-sizing: border-box;padding: 10px;border: 2px solid #448aca;border-radius: 6px;width:250px;height:646px;visibility:hidden;position:absolute;right:2%;top:10%;z-index:999;background:white;"></div>';      
$(".main-section").append(htmltuya);
$('#tuyapane').draggable();

var htmlElse ='<div style="height:100%;width:50%;overflow: auto;padding:0px;margin:0px;position:relative;float:left">'
	+'<h5 id="ant-col-label-1" style="font-weight:bold;margin-top:10px;margin-bottom:5px;">填充颜色:</h5>'
	+'<input type="color" id="ant-col-input-1" Value="#448aca" onchange="isUpdate()" style="border-radius: 4px;display:inline-block;cursor:text;padding: 0px; border: none; width:96%;background-color: rgb(192, 192, 192);">'+'</div>'
	+'<div style="height:100%;width:50%;padding:0px;margin:0px;position:relative;float:left">'
	+'<h5 id="ant-col-label-2" style="font-weight:bold;margin-top:10px; margin-bottom:5px;">边界颜色:</h5>'
	+'<input type="color" id="ant-col-input-2" Value="#cccccc" onchange="isUpdate()" style="border-radius: 4px;display:inline-block;cursor:text;padding: 0px; border: none; width:96%;background-color: rgb(192, 192, 192);">'
	+'</div>';
//var htmlPoint =;
var htmltuya1='<div id="ant-col" style="box-sizing: border-box;padding: 3px;border-radius: 6px;width:228px;box-sizing: border-box;margin-top: 7px;margin-bottom: 0px;height: 168px;overflow:auto">'
+htmlElse+'</div>';
var htmltuya2='<div id="fillopa-borderwidth" style="box-sizing: border-box;padding: 3px;border-radius: 6px;width:228px;box-sizing: border-box;margin-top: 10px;margin-bottom: 10px;height: 133px">'
	+'<div style="height:50%;width:100%;padding:0px;margin:0px;position:relative;float:left">'
	+'<h5 id="fillopa" style="font-weight:bold;margin-top:5px; margin-bottom:5px;">不透明度:</h5>'
+'<input id="ex1" onchange="isUpdate()" data-slider-id="ex1Slider" type="text" data-slider-min="0" data-slider-max="100" data-slider-step="1" data-slider-value="100"/>'+'</div>'
+'<div style="height:50%;width:100%;padding:0px;margin:0px;position:relative;float:left">'+'<h5 id="borderwid" style="font-weight:bold;margin-top:5px; margin-bottom:5px;">边界宽度:</h5>'
+'<input id="ex2" onchange="isUpdate()" data-slider-id="ex2Slider" type="text" data-slider-min="0" data-slider-max="10" data-slider-step="1" data-slider-value="1"/>'+'</div>'
+'<button class="btn btn-danger" style="heigth:30px;width:100%" onclick="drawing.clearDrawing()();">清除所有标绘</button></div>';

     /*添加图标*/
/*$('.steps-container').css({"width":1200});
var htmltuya3='<div style="box-sizing:border-box;color:rgb(136, 136, 136);display:block;float:left;width:30px;height:30px;line-height:23px;font-size:18px;margin-left: 10px;text-align:center;margin-top: 3px;">'
	+'<i class="fa fa-anchor"  id="drawGraphic" title="地图标绘" ></i>'+'</div>';
$(".steps-container").append(htmltuya3);*/
$("#drawGraphic").click(function(){
	$('#tuyapane').css('visibility','');
	$('#tuyapane').toggle();
});
/*var htmltuya4='<div style="box-sizing:border-box;color:rgb(136, 136, 136);display:block;float:left;width:20px;height:30px;line-height:30px;margin-left: 10px;text-align:center;margin-top: 3px;">'
	+'<input type="image" src="images/UI/clearDrawing.png" id="clearDrawing" title="清除绘制" style="width:100%;outline:none">'+'</div>';
$(".steps-container").append(htmltuya4);
$("#clearDrawing").click(function(){
    drawing.clearDrawing();
});
var htmltuya5='<div style="box-sizing:border-box;color:rgb(136, 136, 136);display:block;float:left;width:20px;height:30px;line-height:30px;margin-left: 10px;text-align:center;margin-top: 3px;">'
	+'<input type="image" src="images/UI/revokeDrawing.png" id="revokeDrawing" title="撤销" style="width:100%;outline:none">'+'</div>';
$(".steps-container").append(htmltuya5);
$("#revokeDrawing").click(function(){
    drawing.revokeDrawing();
});*/

     /*更新样式*/
function isUpdate(){
	var fillopacity =$('#ex1').val();
	var borderwidth=$('#ex2').val();
	var fillcolor =$('#ant-col-input-1').val();
	var bordercolor=$('#ant-col-input-2').val();
drawing.updateSymbol(fillcolor,fillopacity,bordercolor,borderwidth);
}

dojo.require("esri.map", 
        "esri.toolbars.draw",
        "esri.toolbars.edit",
        "esri.graphic",
        "esri.config",
        
        "esri.layers.FeatureLayer",

        "esri.symbols.SimpleMarkerSymbol",
        "esri.symbols.PictureMarkerSymbol",
        "esri.symbols.SimpleLineSymbol",
        "esri.symbols.SimpleFillSymbol",

        "esri.dijit.editing.TemplatePicker",

        "esri/symbols/PictureMarkerSymbol",
        
        "dojo._base.array", 
        "dojo._base.event", 
        "dojo._base.lang",
        "dojo.parser", 
        "dijit.registry",

        "dijit.layout.BorderContainer", "dijit.layout.ContentPane", 
        "dijit.form.Button", "dojo.domReady!");
//$("body").on("load",drawingTool(mapContainer,statisticLayer,div));
function saveGraphics(){
var graphics=drawLayer.graphics;
var graphicLayer=[];
for(var i=0;i<graphics.length;i++){
graphicLayer.push({geometry:graphics[i].geometry,symbol:graphics[i].symbol.toJson(),attributes:graphics[i].attributes,infoTemplate:graphics[i].infoTemplate});
}

var geometryStr=JSON.stringify(graphicLayer);
CutomizeMapRecords.graphics=geometryStr;
}

function drawGraphics(){
 var graphicLayer=eval(CutomizeMapRecords.graphics);
 //var templayer=new esri.layers.GraphicsLayer();
 if(graphicLayer==undefined||graphicLayer==null){
	 return;
 }
 for(var i=0;i<graphicLayer.length;i++){
  var tempGraphic=new esri.Graphic(graphicLayer[i]);
  tempGraphic.symbol.color.a=graphicLayer[i].symbol.color[3];
  //tempGraphic.setSymbol(graphicLayer[i].symbol);
  drawLayer.add(tempGraphic);
 }
drawLayer.refresh();
 //mapContainer.addLayer(templayer);
}
function drawingTool(map,layer,div){
	var drawing = {
		map:null,//地图对象
		toolbar:null,//draw绘画对象
		exToolbar:null,//扩展draw绘画对象1
		exToolbar1:null,//扩展draw绘画对象2
		pointSymbol:null,//点的样式
		polylineSymbol:null,//线的样式
		polygonSymbol:null,//面的样式
		textSymbol:null,//文字的样式
		drawingLayer:null,//绘制显示图层
		isDrawText:false,//是否绘制文字
		drawText:"显示文字",//绘制文字内容
		drawTextFun:null,//自定义绘制文字方法，为空时执行默认方法
		editable:false,//是否可编辑
		editToolbar:null,//draw编辑工具
		templatePicker:null,//arcgis 对象
		editDiv:null,//div容器
		graphic:null,//
		isDrawOnMapOnece:false,
		isDrawing:false,
		successFun:null,
		pictureSymbol:null,
		sy1:null,//显示样式
		sy2:null,//
		sy3:null,//
		sy4:null,//
		sy5:null,//
		sy6:null,//
		sy7:null,//
		sy8:null,//
		sy9:null,//
		sy10:null,//
		sy11:null,//
		fillcolor:"#448aca",//填充颜色
		fillopacity:128,//透明度
		bordercolor:"#c0c0c0",//边界颜色
		borderwidth:1,//边界宽度
		/** 停止绘制 */
		stopDrawing:function(){
			if(this.toolbar){
				this.toolbar.deactivate();
			}
			if(this.templatePicker){
				this.templatePicker.clearSelection();
			}
		},
		/** 更新样式 */
		updateSymbol:function(fillcolor,fillopacity,bordercolor,borderwidth){
			if(fillcolor)this.fillcolor=fillcolor;
			if(fillopacity)this.fillopacity=fillopacity*255/100;
			if(bordercolor)this.bordercolor=bordercolor;
			if(borderwidth)this.borderwidth=borderwidth;
			//this.sy1 = new esri.symbol.SimpleMarkerSymbol({"color": this.color16tocolor10(this.fillcolor,this.fillopacity),"size":5,
			//	"outline": {"color": this.color16tocolor10(this.bordercolor,this.fillopacity),"width": this.borderwidth}});
			this.sy1 = new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"http://static.arcgis.com/images/Symbols/Basic/esriCartographyMarker_81_Blue.png","imageData":"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","contentType":"image/png","width":24,"height":24});
			this.sy2 = new esri.symbol.SimpleLineSymbol({"color": this.color16tocolor10(this.bordercolor,this.fillopacity),"width": this.borderwidth});
			this.sy3 = new esri.symbol.SimpleMarkerSymbol({"color": this.color16tocolor10(this.fillcolor,0),
				"outline": {"color": this.color16tocolor10(this.bordercolor,this.fillopacity),"width": this.borderwidth}});
			this.sy3.setPath('M -10,14 L 2,-10 L 14,14 E');
			this.sy4 = new esri.symbol.SimpleFillSymbol(esri.symbol.SimpleFillSymbol.STYLE_SOLID,
					new esri.symbol.SimpleLineSymbol({"color": this.color16tocolor10(this.bordercolor,this.fillopacity),"width": this.borderwidth}),
					new esri.Color(this.color16tocolor10(this.fillcolor,this.fillopacity/255)));
			this.sy5 = new esri.symbol.SimpleMarkerSymbol({"color": this.color16tocolor10(this.fillcolor,this.fillopacity),
				"outline": {"color": this.color16tocolor10(this.bordercolor,this.fillopacity),"width": this.borderwidth}});
			this.sy5.setPath('M -10,10 L -10,-10 L 10,-10 L10,10 L-10,10 E');
			this.sy6 = new esri.symbol.SimpleMarkerSymbol({"color": this.color16tocolor10(this.fillcolor,0),
				"outline": {"color": this.color16tocolor10(this.bordercolor,this.fillopacity),"width": this.borderwidth}});
			this.sy6.setPath("m -11, -7c-1.5,-3.75 7.25,-9.25 12.5,-7c5.25,2.25 6.75,9.75 3.75,12.75c-3,3 -3.25,2.5 -9.75,5.25c-6.5,2.75 -7.25,14.25 2,15.25c9.25,1 11.75,-4 13.25,-6.75c1.5,-2.75 3.5,-11.75 12,-6.5");
			this.sy7 = new esri.symbol.SimpleMarkerSymbol({"color": this.color16tocolor10(this.fillcolor,this.fillopacity),
				"outline": {"color": this.color16tocolor10(this.bordercolor,this.fillopacity),"width": this.borderwidth}});
			this.sy7.setPath('M 10,-13 c3.1,0.16667 4.42564,2.09743 2.76923,3.69231c-2.61025,2.87179 -5.61025,5.6718 -6.14358,6.20513c-0.66667,0.93333'+
		    		' -0.46667,1.2 -0.53333,1.93333c-0.00001,0.86666 0.6,1.66667 1.13334,2c1.03077,0.38462 2.8,0.93333 3.38974,1.70769c0.47693,0.42564'+
		    		' 0.87693,0.75897 1.41026,1.75897c0.13333,1.06667 -0.46667,2.86667 -1.8,3.8c-0.73333,0.73333 -3.86667,2.66666 -4.86667,3.13333c-0.93333,0.8'+
		    		' -7.4,3.2 -7.6,3.06667c-1.06667,0.46667 -4.73333,1.13334 -5.2,1.26667c-1.6,0.33334 -4.6,0.4 -6.25128,0.05128c-1.41539,-0.18462 '+
		    		'-2.34872,-2.31796 -1.41539,-4.45129c0.93333,-1.73333 1.86667,-3.13333 2.64615,-3.85641c1.28718,-1.47692'+
		    		' 2.57437,-2.68204 3.88718,-3.54359c0.88718,-1.13845 1.8,-1.33333 2.26666,-2.45641c0.33334,-0.74359 0.37949,-1.7641'+
		    		' 0.06667,-2.87692c-0.66666,-1.46666 -1.66666,-1.86666 -2.98975,-2.2c-1.27692,-0.26666 -2.12307,-0.64102 -3.27692,-1.46666c-0.66667,-1.00001'+
		    		'-1.01538,-3.01539 0.73333,-4.06667c1.73333,-1.2 3.6,-1.93333 4.93333,-2.2c1.33333,-0.46667 4.84104,-1.09743 5.84103,-1.23076c1.60001,-0.46667'+
		    		' 6.02564,-0.50257 7.29231,-0.56924z');
			this.sy8 =new esri.symbol.SimpleMarkerSymbol({"color": this.color16tocolor10(this.fillcolor,this.fillopacity),
				"outline": {"color": this.color16tocolor10(this.bordercolor,this.fillopacity),"width": this.borderwidth}});
			this.sy8.setPath("M 10,1 L 3,8 L 3,5 L -15,5 L -15,-2 L 3,-2 L 3,-5 L 10,1 E");
			this.sy9 =new esri.symbol.SimpleMarkerSymbol({"color": this.color16tocolor10(this.fillcolor,this.fillopacity),
				"outline": {"color": this.color16tocolor10(this.bordercolor,this.fillopacity),"width": this.borderwidth}});
			this.sy9.setPath("M -10,14 L 2,-10 L 14,14 L -10,14 E");
			this.sy10 =new esri.symbol.SimpleMarkerSymbol({"color": this.color16tocolor10(this.fillcolor,this.fillopacity),
				"outline": {"color": this.color16tocolor10(this.bordercolor,this.fillopacity),"width": this.borderwidth}});
			this.sy11 =new esri.symbol.SimpleMarkerSymbol({"color": this.color16tocolor10(this.fillcolor,this.fillopacity),
				"outline": {"color": this.color16tocolor10(this.bordercolor,this.fillopacity),"width": this.borderwidth}});
			this.sy11.setPath("M-10,15 Q2,0 14,15 M14,15 Q2,30 -10,15");  
			this.sy12 =new esri.symbol.SimpleMarkerSymbol({"color": this.color16tocolor10(this.fillcolor,0),
				"outline": {"color": this.color16tocolor10(this.fillcolor,this.fillopacity),"width": 2}});
			this.sy12.setPath("M -10,14 L -2,-10 L 6,14 M-8,7 L4,7 E");
			this.sy13 =new esri.symbol.SimpleMarkerSymbol({"color": this.color16tocolor10(this.fillcolor,this.fillopacity),
				"outline": {"color": this.color16tocolor10(this.bordercolor,this.fillopacity),"width": this.borderwidth}});
			this.sy13.setPath("M 10,1 L 3,8 L 3,5 L -15,5 L -15,-2 L 3,-2 L 3,-5 L 10,1 E");
			if(this.templatePicker){
				this.templatePicker.items = [
                      { label: "点", symbol: this.sy1,name:"点", description: "描述..." ,cartNum:"1"},
//				       { label: "多点", symbol: new esri.symbol.SimpleMarkerSymbol(esri.symbol.SimpleMarkerSymbol.STYLE_DIAMOND),name:"点", description: "description 2" ,cartNum:"2"},
                      { label: "画线", symbol: this.sy2,name:"线", description: "描述..." ,cartNum:"3"},
                      { label: "折线", symbol: this.sy3,name:"线", description: "描述..." ,cartNum:"4"},
                      { label: "多边形", symbol: this.sy4,name:"面", description: "描述..." ,cartNum:"5"},
                      { label: "任意线", symbol: this.sy6,name:"线", description: "描述..." ,cartNum:"6"},
                      { label: "任意面", symbol: this.sy7, name:"面",description: "描述..." ,cartNum:"7"},
                      { label: "箭头", symbol: this.sy8,name:"箭头", description: "描述..." ,cartNum:"8"},
                      { label: "三角形", symbol: this.sy9, name:"三角形",description: "描述..." ,cartNum:"9"},
                      { label: "画圆", symbol: this.sy10,name:"圆形", description: "描述..." ,cartNum:"10"},
                      { label: "椭圆", symbol: this.sy11,name:"椭圆", description: "描述..." ,cartNum:"11"},
                      { label: "文字", symbol: this.sy12,name:"显示文字", description: "描述..." ,cartNum:"12"},
                      { label: "矩形", symbol: this.sy5,name:"矩形", description: "描述..." ,cartNum:"13"},
                      { label: "集结地", symbol: this.sy7,name:"集结地", description: "描述..." ,cartNum:"14"},
                      { label: "弧线", symbol: this.sy6,name:"弧线", description: "描述..." ,cartNum:"15"},
                      { label: "曲线", symbol: this.sy6,name:"曲线", description: "描述..." ,cartNum:"16"},
                      { label: "燕尾箭头", symbol: this.sy13,name:"燕尾箭头", description: "描述..." ,cartNum:"17"}
                    ];
				this.templatePicker.update();
			}
		},
		/** 初始化绘制工具 */
		initDrawing:function(map,layer,div){
			this.map = map;
			if(this.toolbar==null)
				this.toolbar = new esri.toolbars.Draw(map);
			if(this.exToolbar == null)
				this.exToolbar = new Extension.DrawEx(map);
			if(this.exToolbar1 == null)
				this.exToolbar1 = new plotDraw.DrawExt(map);
			if(this.editToolbar==null)
				this.editToolbar = new esri.toolbars.Edit(map);
			if(layer==null){
				this.drawingLayer = this.map.graphics;
			}else{
				this.drawingLayer = layer;
			}
			this.editDiv = div;
			dojo.connect(this.toolbar, "onDrawEnd",this, this.addToMap);
			dojo.connect(this.exToolbar, "onDrawEnd",this, this.addToMap);
			dojo.connect(this.exToolbar1, "onDrawEnd",this, this.addToMap);
			dojo.connect(this.map,"onClick",this,this.drawingText);
			
			dojo.connect(layer,"onClick",this,this.removeDrawing);
			dojo.connect(layer,"onDblClick",this,this.editGraphic);
		},
		/** 设置绘制所在的图层 */
		addLayer:function(layer){
			this.drawingLayer = layer;
			this.bindEvent(layer);
		},
		/** 为绘制图层绑定事件 */
		bindEvent:function(layer){
			dojo.connect(layer,"onClick",this,this.removeDrawing);
			dojo.connect(layer,"onDblClick",this,this.editGraphic);
		},
		bindClickEvent:function(layer){
			dojo.connect(layer,"onClick",this,this.removeDrawing);
		},
		/** 初始化编辑 */
		initEdit:function(){
			var my = document.getElementById(this.editDiv);
			if (my != null){
   	    	var tDiv = document.createElement('div');
   		    tDiv.setAttribute("id", "graphicTemplateDiv");
   		    my.appendChild(tDiv);
   		    this.updateSymbol(this.fillcolor,this.fillopacity,this.bordercolor,this.borderwidth);
   		    this.templatePicker = new esri.dijit.editing.TemplatePicker({
   	    		items:[
                          { label: "点", symbol: this.sy1,name:"点", description: "描述..." ,cartNum:"1"},
//                          { label: "多点", symbol: new esri.symbol.SimpleMarkerSymbol(esri.symbol.SimpleMarkerSymbol.STYLE_DIAMOND),name:"点", description: "description 2" ,cartNum:"2"},
                          { label: "画线", symbol: this.sy2,name:"线", description: "描述..." ,cartNum:"3"},
                          { label: "折线", symbol: this.sy3,name:"线", description: "描述..." ,cartNum:"4"},
                          { label: "多边形", symbol: this.sy4,name:"面", description: "描述..." ,cartNum:"5"},
                          { label: "任意线", symbol: this.sy6,name:"线", description: "描述..." ,cartNum:"6"},
                          { label: "任意面", symbol: this.sy7, name:"面",description: "描述..." ,cartNum:"7"},
                          { label: "箭头", symbol: this.sy8,name:"箭头", description: "描述..." ,cartNum:"8"},
                          { label: "三角形", symbol: this.sy9, name:"三角形",description: "描述..." ,cartNum:"9"},
                          { label: "画圆", symbol: this.sy10,name:"圆形", description: "描述..." ,cartNum:"10"},
                          { label: "椭圆", symbol: this.sy11,name:"椭圆", description: "描述..." ,cartNum:"11"},
                          { label: "文字", symbol: this.sy12,name:"显示文字", description: "描述..." ,cartNum:"12"},
                          { label: "矩形", symbol: this.sy5,name:"矩形", description: "描述..." ,cartNum:"13"},
                          { label: "集结地", symbol: this.sy7,name:"集结地", description: "描述..." ,cartNum:"14"},
                          { label: "弧线", symbol: this.sy6,name:"弧线", description: "描述..." ,cartNum:"15"},
                          { label: "曲线", symbol: this.sy6,name:"曲线", description: "描述..." ,cartNum:"16"},
                          { label: "燕尾箭头", symbol: this.sy13,name:"燕尾箭头", description: "描述..." ,cartNum:"17"}
                        ],
	    	        grouping: true,
	    	        rows: "auto",
	    	        columns: 4
	    	    }, "graphicTemplateDiv");
	    	    this.templatePicker.startup();
	    	    dojo.connect(this.templatePicker,"onSelectionChange",this,function() {
	    	    	
	    	    	var selectedTemplate;
       			this.toolbar.deactivate();
       			this.exToolbar.deactivate();
       			this.exToolbar1.deactivate();
	                if( this.templatePicker.getSelected() ) {
	                  selectedTemplate = this.templatePicker.getSelected();
	                  switch (selectedTemplate.item.cartNum) {
		                //case "1":this.drawingPoint();break;
		                //case "1":alert("I am an alert box!!");
		                case "1":this.drawingPoint();break;
		        		case "2":this.drawingMulti_Point();break;
		        		case "3":this.drawingLine();break;
		        		case "4":this.drawingPolyline();break;
		        		case "5":this.drawingPolygon();break;
		        		case "6":this.drawingFreehand_Polyline();break;
		        		case "7":this.drawingFreehand_Polygon();break;
		        		case "8":this.drawingArrow();break;
		        		case "9":this.drawingTriangle();break;
		        		case "10":this.drawingCircle();break;
		        		case "11":this.drawingEllipse();break;
		        		case "12":this.isDrawText=true;break;
		        		case "13":this.drawingRectangle();break;
		        		case "14":this.drawingBezierPolygon();break;
		        		case "15":this.drawingCurve();break;
		        		case "16":this.drawingBezierCurve();break;
		        		case "17":this.drawingTailedsquadcombat();break;
		        		default:break;
		              }
	                }
	                
	            });
   	    }
		},
		/** 添加文字 */
		drawingText:function(evt){
			if(this.isDrawText){
				if(this.drawTextFun==null){
					var symbol = new esri.symbol.TextSymbol({"color": this.color16tocolor10(this.fillcolor,this.fillopacity)}).setFont(new esri.symbol.Font("20pt").setFamily("SimSun"));
					symbol.setText(this.drawText);
					var attrs = null;
					if( this.templatePicker.getSelected() ) {
		            	selectedTemplate = this.templatePicker.getSelected();
		  				attrs = {
		  						name:selectedTemplate.item.name,
		  						description:selectedTemplate.item.description
		  				};
		            }
					var gr = new esri.Graphic(evt.mapPoint, symbol, attrs);
					this.graphic = this.drawingLayer.add(gr);
					var content = "<table>";
	                content += "<tr><td style='width:40px;'>名称 :</td><td colspan='2'><Input type='text' value='"+this.graphic.attributes.name+"' onchange=\"updateGraphic('name',this.value)\" /></td></tr>";
	                content += "<tr><td>描述 : </td><td colspan='2'><textarea rows='3' style='width:200px;' onkeyup=\"updateGraphic('description',this.value)\" />"+
						this.graphic.attributes.description+"</textarea></td></tr>";
	                content += "</table>";
		        	this.map.infoWindow.setTitle("标注");
//		        	this.map.infoWindow.setContent(content);
		        	var features = [];
	            	var buildingFootprintTemplate = new esri.InfoTemplate("",content);
	            	var feature = new esri.Graphic(this.graphic.toJson());
       			feature.setInfoTemplate(buildingFootprintTemplate);
       			features.push(feature);
	            	this.map.infoWindow.setFeatures(features);
		        	
		            this.map.infoWindow.show(evt.mapPoint);
					this.templatePicker.clearSelection();
				}else{
					this.drawTextFun(evt);
				}

				this.isDrawText = false;
			}
			
			this.editToolbar.deactivate();
		},
		/** 要素编辑 */
		editGraphic:function(evt){
//			if(!this.editable){
//				return;
//			}
			dojo.stopEvent(evt);
			var tool = 0;
           tool = tool | esri.toolbars.Edit.MOVE; 
           tool = tool | esri.toolbars.Edit.EDIT_VERTICES; 
           tool = tool | esri.toolbars.Edit.SCALE; 
           tool = tool | esri.toolbars.Edit.ROTATE; 
	          
           // enable text editing if a graphic uses a text symbol
           if ( evt.graphic.symbol.declaredClass === "esri.symbol.TextSymbol" ) {
           	tool = tool | esri.toolbars.Edit.EDIT_TEXT;
           }
           //specify toolbar options        
           var options = {
           	allowAddVertices: true,
           	allowDeleteVertices: true,
           	uniformScaling: false
           };
			this.toolbar.deactivate();
			this.exToolbar.deactivate();
			this.exToolbar1.deactivate();
	        this.editToolbar.activate(tool, evt.graphic, options);
		},
		/** 将绘制的要素添加到地图中 */
		addToMap:function(geometry){
			this.isDrawing = false;
			var symbol;
			switch (geometry.type) {
				case "point":
					if(this.isDrawOnMapOnece && this.isDrawOnMapOnece!=2){
						symbol = new esri.symbol.PictureMarkerSymbol();
					}else{
						symbol =this.pictureSymbol;
					}
					break;
				case "multipoint":
					if(this.isDrawOnMapOnece && this.isDrawOnMapOnece!=2){
						symbol = new esri.symbol.SimpleMarkerSymbol();
					}else{
						symbol = new esri.symbol.SimpleMarkerSymbol({"color": this.color16tocolor10(this.fillcolor,this.fillopacity),
							"outline": {"color": this.color16tocolor10(this.bordercolor,100),"width": this.borderwidth}});
					}
				break;
				case "line":
				case "polyline":
					if(this.isDrawOnMapOnece && this.isDrawOnMapOnece!=2){
						symbol = new esri.symbol.SimpleLineSymbol();
					}else{
						symbol = new esri.symbol.SimpleLineSymbol({"color": this.color16tocolor10(this.bordercolor,this.fillopacity),"width": this.borderwidth});
					}
				break;
				default:
					if(this.isDrawOnMapOnece && this.isDrawOnMapOnece!=2){
						symbol = new esri.symbol.SimpleFillSymbol();
					}else{
						symbol = new esri.symbol.SimpleFillSymbol(esri.symbol.SimpleFillSymbol.STYLE_SOLID,
									new esri.symbol.SimpleLineSymbol({"color": this.color16tocolor10(this.bordercolor,100),"width": this.borderwidth}),
									new esri.Color(this.color16tocolor10(this.fillcolor,this.fillopacity/255)));
					}
				break;
			}
			
			//是否只画一次
			if(this.isDrawOnMapOnece===true){
				this.toolbar.deactivate();
				this.exToolbar.deactivate();
				this.exToolbar1.deactivate();
				var g = new esri.Graphic(geometry, symbol);
				this.isDrawOnMapOnece = false;
				if(this.successFun)this.successFun(g);
				this.successFun = null;
				return;
			}else if(this.isDrawOnMapOnece=="1"){
				this.map.graphics.clear();
				var g = new esri.Graphic(geometry, symbol);
				if(this.successFun)this.successFun(g);
//				this.successFun = null;
				return;
			}
			if(this.drawingLayer==null){
				message.warn("未选择绘制图层!");
				return;
			}
			var attrs = null;
			var selectedTemplate;
           if( this.templatePicker && this.templatePicker.getSelected() ) {
           	selectedTemplate = this.templatePicker.getSelected();
 				attrs = {
 						name:selectedTemplate.item.name,
 						description:selectedTemplate.item.description
 				};
           }else{
           	attrs = {
 						name:"标注",
 						description:"描述..."
 				};
           }
           
           //如果是画制图范围，则不用graphic
           if(isDrawMapScope==true){
        	   mapContainer.setExtent(geometry.getExtent());
				$("#mapScopeStr").val(JSON.stringify(geometry.toJson()));
				isDrawMapScope=false;
				drawing.toolbar.deactivate();
				return;
			}
           var infoTemplate = new esri.InfoTemplate("Details", "${*}");
			var gr = new esri.Graphic(geometry, symbol,attrs);
			this.graphic = this.drawingLayer.add(gr);
			
			
			
			var content = "<table>"
       	content += "<tr><td style='width:40px;'>名称 :</td><td colspan='2'><Input type='text' value='"+this.graphic.attributes.name+"' onchange=\"updateGraphic('name',this.value)\" /></td></tr>";
//			content += "<tr><td>描述 :</td><td colspan='2'><Input type='textarea' value='"+this.graphic.attributes.description+"' onchange=\"updateGraphic('description',this.value)\" /></td></tr>";
			content += "<tr><td>描述 : </td><td colspan='2'><textarea rows='3' style='width:200px;' onkeyup=\"updateGraphic('description',this.value)\" />"+
				this.graphic.attributes.description+"</textarea></td></tr>";
			if(this.graphic.geometry.type=="polygon"){
           	content += "<tr><td colspan='2'><input type='button' value='量算' onclick='measureGraphic()' /></td>" +
//           			"<td><input type='button' value='1km缓冲' onclick='bufferGraphic(1)' /></td>" +
           		"<td><input type='button' value='删除' onclick='deleteGraphic()' /></td>" +
   				"</tr></table>";
       	}else if(this.graphic.geometry.type=="polyline"){
       		content += "<td><input type='button' value='删除' onclick='deleteGraphic()' /></td>";
           	content += "<tr><td colspan='2'><input type='button' value='量算' onclick='measureGraphic()' /></td></tr></table>";
       	}else{
       		content += "<td><input type='button' value='删除' onclick='deleteGraphic()' /></td>";
       		content += "</table>";
       	}
       	this.map.infoWindow.setTitle("标注");
       	var features = [];
       	var buildingFootprintTemplate = new esri.InfoTemplate("",content);
       	var feature = new esri.Graphic(this.graphic.toJson());
			feature.setInfoTemplate(buildingFootprintTemplate);
			features.push(feature);
       	this.map.infoWindow.setFeatures(features);
       	
           this.map.infoWindow.show(getPoint(geometry));
           if(this.isDrawOnMapOnece==2){
        	   this.exToolbar1.deactivate;
        	   this.templatePicker.clearSelection();
           }
		},
		closeDrawing:function(){
			this.toolbar.deactivate();
			this.exToolbar.deactivate();
			this.exToolbar1.deactivate();
			this.isDrawOnMapOnece = false;
		},
		/** 清除绘制图形 */
		clearDrawing:function(){
			if(this.drawingLayer!=null){
				this.drawingLayer.clear();
			}
		},
		/** 撤销 */
		revokeDrawing:function(){
			this.editToolbar.deactivate();
			if(this.drawingLayer!=null && this.drawingLayer.graphics.length>0){
				this.drawingLayer.remove(this.drawingLayer.graphics[this.drawingLayer.graphics.length-1]);
			}
		},
		/** 点击弹出要素信息，ctrl+点击 删除要素 */
		removeDrawing:function(evt){
			if(this.isDrawing){
				return;
			}
			dojo.stopEvent(evt);
			this.editToolbar.deactivate();
			this.graphic = evt.graphic;
			if(this.drawingLayer!=null){
	            if (evt.ctrlKey === true || evt.metaKey === true) {  //delete feature if ctrl key is depressed
					this.drawingLayer.remove(evt.graphic);
	            }else{
	            	
	            	var content = "<table>"
	            	for(var key in this.graphic.attributes){
	            	
	            		if(key=="name"){
	            			content += "<tr><td style='width:40px;'>名称 :</td><td colspan='2'><Input type='text' value='"+this.graphic.attributes[key]+
	            			"' onchange=\"updateGraphic('"+key+"',this.value)\" /></td></tr>";
	            		}else if(key=="description"){
	            			content += "<tr><td>描述 : </td><td colspan='2'><textarea rows='3' style='width:200px;' onkeyup=\"updateGraphic('description',this.value)\" />"+
	            				this.graphic.attributes.description+"</textarea></td></tr>";
//	            			content += "<tr><td>描述 :</td><td colspan='2'><Input type='text' value='"+this.graphic.attributes[key]+
//	            			"' onchange=\"updateGraphic('"+key+"',this.value)\" /></td></tr>";
	            		}else{
	            			content += "<tr><td>"+key+" :</td><td colspan='2'><Input type='text' value='"+this.graphic.attributes[key]+
	            			"' onchange=\"updateGraphic('"+key+"',this.value)\" /></td></tr>";
	            		}
	            		
	            	}
	            	if(evt.graphic.geometry.type=="polygon"){
		            	content += "<tr><td colspan='2'><input type='button' value='量算' onclick='measureGraphic()' /></td>" +
	//            			"<td><input type='button' value='1km缓冲' onclick='bufferGraphic(1)' /></td>" +
	            			"<td><input type='button' value='删除' onclick='deleteGraphic()' /></td>" +
	        				"</tr></table>";
	            	}else if(this.graphic.geometry.type=="polyline"){
	            		content += "<td><input type='button' value='删除' onclick='deleteGraphic()' /></td>";
		            	content += "<tr><td colspan='2'><input type='button' value='量算' onclick='measureGraphic()' /></td></tr></table>";
	            	}else{
	            		content += "<td><input type='button' value='删除' onclick='deleteGraphic()' /></td>";
	            		content += "</table>";
	            	}

	            	this.map.infoWindow.setTitle("标注");
//	            	this.map.infoWindow.setContent(content);
	            	var features = [];
	            	var buildingFootprintTemplate = new esri.InfoTemplate("",content);
	            	var feature = new esri.Graphic(this.graphic.toJson());
       			feature.setInfoTemplate(buildingFootprintTemplate);
       			features.push(feature);
	            	this.map.infoWindow.setFeatures(features);
	                this.map.infoWindow.show(evt.mapPoint);
	            }
				
			}
		},
		drawingTool:function(tool,drawOnece) {
			this.isDrawOnMapOnece = drawOnece;
			var tool = tool.toUpperCase().replace(/ /g, "_");
	        this.toolbar.activate(esri.toolbars.Draw[tool]);
			this.drawGraphic();
		},
		/*drawingPoint:function(drawOnece,fun){
			
			this.isDrawOnMapOnece = drawOnece;
			this.successFun = fun;
			this.toolbar.activate(esri.toolbars.Draw.POINT);
			this.drawGraphic();
		},*/
		drawingPoint:function(drawOnece,fun){
						
			this.isDrawOnMapOnece = drawOnece;
			this.successFun = fun;
			$('#ant-col').empty();
//			if($("#ant-col").children().attr('id')=="pointTemplateDiv"){
//				templatePickerPoint.destroy();
//			}
			try{
				templatePickerPoint.destroy();
			}
			catch(e){}
			var nowDiv=document.getElementById("ant-col");
//			if($("#ant-col").children().attr('id')!="pointTemplateDiv"){}
			var pointDiv = document.createElement('div');
   		    pointDiv.setAttribute("id", "pointTemplateDiv");
   		    document.getElementById("ant-col").appendChild(pointDiv);
			templatePickerPoint = new esri.dijit.editing.TemplatePicker({
                items:[
                    { label: "安全建筑", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/anquanjianzhu.png","contentType":"image/png","width":24,"height":24}),name:"点", description: "描述..." ,cartNum:"1"},
//                  { label: "多点", symbol: new esri.symbol.SimpleMarkerSymbol(esri.symbol.SimpleMarkerSymbol.STYLE_DIAMOND),name:"点", description: "description 2" ,cartNum:"2"},
                    { label: "不安全建筑", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/buanquanjianzhu.png","contentType":"image/png","width":24,"height":24}),name:"线", description: "描述..." ,cartNum:"3"},
                    { label: "倒塌", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/daota.png","contentType":"image/png","width":24,"height":24}),name:"线", description: "描述..." ,cartNum:"3"},
                    { label: "黄闪灯", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/huangshandeng.png","contentType":"image/png","width":24,"height":24}),name:"线", description: "描述..." ,cartNum:"3"},
                    { label: "化学火灾", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/huaxuehuozai.png","contentType":"image/png","width":24,"height":24}),name:"线", description: "描述..." ,cartNum:"3"},
                    { label: "滑坡", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/huapo.png","contentType":"image/png","width":24,"height":24}),name:"线", description: "描述..." ,cartNum:"3"},
                    { label: "火源", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/huoyuan.png","contentType":"image/png","width":24,"height":24}),name:"线", description: "描述..." ,cartNum:"3"},
                    { label: "交通事故", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/jiaotongshigu.png","contentType":"image/png","width":24,"height":24}),name:"线", description: "描述..." ,cartNum:"3"},
                    { label: "警示", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/jingshi.png","contentType":"image/png","width":24,"height":24}),name:"线", description: "描述..." ,cartNum:"3"},
                    { label: "禁区", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/jinqu.png","contentType":"image/png","width":24,"height":24}),name:"线", description: "描述..." ,cartNum:"3"},
                    { label: "紧急救护站", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/jinjijiuhuzhan.png","contentType":"image/png","width":24,"height":24}),name:"线", description: "描述..." ,cartNum:"3"},
                    { label: "救护车", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/jiuhuche.png","contentType":"image/png","width":24,"height":24}),name:"线", description: "描述..." ,cartNum:"3"},
                    { label: "救护中心", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/jiuhuzhongxin.png","contentType":"image/png","width":24,"height":24}),name:"线", description: "描述..." ,cartNum:"3"},
                    { label: "救援队", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/jiuyuandui.png","contentType":"image/png","width":24,"height":24}),name:"线", description: "描述..." ,cartNum:"3"},
                    { label: "口门", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/koumen.png","contentType":"image/png","width":24,"height":24}),name:"线", description: "描述..." ,cartNum:"3"},
                    { label: "路灯", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/ludeng.png","contentType":"image/png","width":24,"height":24}),name:"线", description: "描述..." ,cartNum:"3"},
                    { label: "灭火器", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/miehuoqi.png","contentType":"image/png","width":24,"height":24}),name:"线", description: "描述..." ,cartNum:"3"},
                    { label: "灭火栓", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/miehuoshuan.png","contentType":"image/png","width":24,"height":24}),name:"线", description: "描述..." ,cartNum:"3"},
                    { label: "泥石流", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/nishiliu.png","contentType":"image/png","width":24,"height":24}),name:"线", description: "描述..." ,cartNum:"3"},
                    { label: "人行横道", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/renxinghengdao.png","contentType":"image/png","width":24,"height":24}),name:"线", description: "描述..." ,cartNum:"3"},
                    { label: "物资供应处", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/wuzigongyingchu.png","contentType":"image/png","width":24,"height":24}),name:"线", description: "描述..." ,cartNum:"3"},
                    { label: "消防车", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/xiaofangche.png","contentType":"image/png","width":24,"height":24}),name:"线", description: "描述..." ,cartNum:"3"},
                    { label: "消防栓", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/xiaofangshuan.png","contentType":"image/png","width":24,"height":24}),name:"线", description: "描述..." ,cartNum:"3"},
                    { label: "消防通道", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/xiaofangtongdao.png","contentType":"image/png","width":24,"height":24}),name:"线", description: "描述..." ,cartNum:"3"},
                    { label: "消防云梯", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/xiaofangyunti.png","contentType":"image/png","width":24,"height":24}),name:"线", description: "描述..." ,cartNum:"3"},
                    { label: "沿途危险点", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/yantuweixiandian.png","contentType":"image/png","width":24,"height":24}),name:"线", description: "描述..." ,cartNum:"3"},
                    { label: "次生灾害源", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/cishengzaihaiyuan.png","contentType":"image/png","width":24,"height":24}),name:"线", description: "描述..." ,cartNum:"4"},
                    { label: "宏观震中", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/hongguanzhenzhong.png","contentType":"image/png","width":24,"height":24}),name:"面", description: "描述..." ,cartNum:"5"},
                    { label: "物资供应处", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/wuzigongyingchu.png","contentType":"image/png","width":24,"height":24}),name:"线", description: "描述..." ,cartNum:"6"},
                    { label: "医疗救护站", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/yiliaojiuhuzhan.png","contentType":"image/png","width":24,"height":24}), name:"面",description: "描述..." ,cartNum:"7"},
                    { label: "应急避难所", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/yingjibinansuo.png","contentType":"image/png","width":24,"height":24}),name:"箭头", description: "描述..." ,cartNum:"8"},
                    { label: "应急避出口", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/yingjichukou.png","contentType":"image/png","width":24,"height":24}),name:"箭头", description: "描述..." ,cartNum:"8"},
                    { label: "应急棚", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/yingjipeng.png","contentType":"image/png","width":24,"height":24}), name:"三角形",description: "描述..." ,cartNum:"9"},
                    { label: "应急停车场", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/yingjitingchechang.png","contentType":"image/png","width":24,"height":24}),name:"圆形", description: "描述..." ,cartNum:"10"},
                    { label: "应急停机坪", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/yingjitingjiping.png","contentType":"image/png","width":24,"height":24}),name:"椭圆", description: "描述..." ,cartNum:"11"},
                    { label: "应急指挥", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/yingjizhihui.png","contentType":"image/png","width":24,"height":24}),name:"显示文字", description: "描述..." ,cartNum:"12"},
                    { label: "原地安置点", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/yuandianzhidian.png","contentType":"image/png","width":24,"height":24}),name:"矩形", description: "描述..." ,cartNum:"13"},
                    { label: "闸", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/zha.png","contentType":"image/png","width":24,"height":24}),name:"集结地", description: "描述..." ,cartNum:"14"},
                    { label: "指路标志", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/zhilubiaozhi.png","contentType":"image/png","width":24,"height":24}),name:"弧线", description: "描述..." ,cartNum:"15"},
                    { label: "指示标识", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/zhishibiaoshi.png","contentType":"image/png","width":24,"height":24}),name:"曲线", description: "描述..." ,cartNum:"16"},
                    { label: "指示灯", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/zhishideng.png","contentType":"image/png","width":24,"height":24}),name:"燕尾箭头", description: "描述..." ,cartNum:"17"},
                    { label: "转移安置点", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/zhuanyianzhidian.png","contentType":"image/png","width":24,"height":24}),name:"燕尾箭头", description: "描述..." ,cartNum:"17"},
                    { label: "转移单元", symbol: new esri.symbol.PictureMarkerSymbol({"angle":0,"xoffset":0,"yoffset":0,"type":"esriPMS","url":"./images/disasterIMG/zhuanyidanyuan.png","contentType":"image/png","width":24,"height":24}),name:"燕尾箭头", description: "描述..." ,cartNum:"17"}
                    ],
                    grouping: true,
                    rows:"auto",
                    columns:3
					},"pointTemplateDiv");                  
              		templatePickerPoint.startup();       
              		dojo.connect(templatePickerPoint,"onSelectionChange",this,function() {
              			if(templatePickerPoint.getSelected()){
              				var selectedPoint=templatePickerPoint.getSelected();
              				this.pictureSymbol=selectedPoint.item.symbol;
              			}
              			
              		});
			this.toolbar.activate(esri.toolbars.Draw.POINT);
			this.drawGraphic();
		},
		drawingMulti_Point:function(drawOnece,fun){
			$('#ant-col').empty();
			try{
							templatePickerPoint.destroy();
						}
						catch(e){}
						var nowDiv=document.getElementById("ant-col");
			$('#ant-col').append(htmlElse);
			this.isDrawOnMapOnece = drawOnece;
			this.successFun = fun;
			this.toolbar.activate(esri.toolbars.Draw.MULTI_POINT);
			this.drawGraphic();
		},
		drawingLine:function(drawOnece,fun){
			$('#ant-col').empty();
			try{
							templatePickerPoint.destroy();
						}
						catch(e){}
						var nowDiv=document.getElementById("ant-col");
			$('#ant-col').append(htmlElse);
			this.isDrawOnMapOnece = drawOnece;
			this.successFun = fun;
			this.toolbar.activate(esri.toolbars.Draw.LINE);
			this.drawGraphic();
		},
		drawingPolyline:function(drawOnece,fun){
			$('#ant-col').empty();
			try{
							templatePickerPoint.destroy();
						}
						catch(e){}
						var nowDiv=document.getElementById("ant-col");
			$('#ant-col').append(htmlElse);
			this.isDrawOnMapOnece = drawOnece;
			this.successFun = fun;
			this.toolbar.activate(esri.toolbars.Draw.POLYLINE);
			this.drawGraphic();
		},
		drawingPolygon:function(drawOnece,fun){
			$('#ant-col').empty();
			try{
							templatePickerPoint.destroy();
						}
						catch(e){}
						var nowDiv=document.getElementById("ant-col");
			$('#ant-col').append(htmlElse);
			this.isDrawOnMapOnece = drawOnece;
			this.successFun = fun;
			this.toolbar.activate(esri.toolbars.Draw.POLYGON);
			this.drawGraphic();
		},
		drawingFreehand_Polyline:function(drawOnece,fun){
			$('#ant-col').empty();
			try{
							templatePickerPoint.destroy();
						}
						catch(e){}
						var nowDiv=document.getElementById("ant-col");
			$('#ant-col').append(htmlElse);
			this.isDrawOnMapOnece = drawOnece;
			this.successFun = fun;
			this.toolbar.activate(esri.toolbars.Draw.FREEHAND_POLYLINE);
			this.drawGraphic();
		},
		drawingFreehand_Polygon:function(drawOnece,fun){
			$('#ant-col').empty();
			try{
							templatePickerPoint.destroy();
						}
						catch(e){}
						var nowDiv=document.getElementById("ant-col");
			$('#ant-col').append(htmlElse);
			this.isDrawOnMapOnece = drawOnece;
			this.successFun = fun;
			this.toolbar.activate(esri.toolbars.Draw.FREEHAND_POLYGON);
			this.drawGraphic();
		},
		drawingArrow:function(drawOnece,fun){
			$('#ant-col').empty();
			try{
							templatePickerPoint.destroy();
						}
						catch(e){}
						var nowDiv=document.getElementById("ant-col");
			$('#ant-col').append(htmlElse);
			this.isDrawOnMapOnece = drawOnece;
			this.successFun = fun;
			this.toolbar.activate(esri.toolbars.Draw.ARROW);
			this.drawGraphic();
		},
		drawingTriangle:function(drawOnece,fun){
			$('#ant-col').empty();
			try{
							templatePickerPoint.destroy();
						}
						catch(e){}
						var nowDiv=document.getElementById("ant-col");
			$('#ant-col').append(htmlElse);
			this.isDrawOnMapOnece = drawOnece;
			this.successFun = fun;
			this.toolbar.activate(esri.toolbars.Draw.TRIANGLE);
			this.drawGraphic();
		},
		drawingCircle:function(drawOnece,fun){
			$('#ant-col').empty();
			try{
							templatePickerPoint.destroy();
						}
						catch(e){}
						var nowDiv=document.getElementById("ant-col");
			$('#ant-col').append(htmlElse);
			this.isDrawOnMapOnece = drawOnece;
			this.successFun = fun;
			this.toolbar.activate(esri.toolbars.Draw.CIRCLE);
			this.drawGraphic();
		},
		drawingEllipse:function(drawOnece,fun){
			$('#ant-col').empty();
			try{
							templatePickerPoint.destroy();
						}
						catch(e){}
						var nowDiv=document.getElementById("ant-col");
			$('#ant-col').append(htmlElse);
			this.isDrawOnMapOnece = drawOnece;
			this.successFun = fun;
			this.toolbar.activate(esri.toolbars.Draw.ELLIPSE);
			this.drawGraphic();
		},
		drawingRectangle:function(drawOnece,fun){
			$('#ant-col').empty();
			try{
							templatePickerPoint.destroy();
						}
						catch(e){}
						var nowDiv=document.getElementById("ant-col");
			$('#ant-col').append(htmlElse);
			this.isDrawOnMapOnece = drawOnece;
			this.successFun = fun;
			this.toolbar.activate(esri.toolbars.Draw.RECTANGLE);
			this.drawGraphic();
		},
		drawingExtent:function(drawOnece,fun){
			$('#ant-col').empty();
			try{
							templatePickerPoint.destroy();
						}
						catch(e){}
						var nowDiv=document.getElementById("ant-col");
			$('#ant-col').append(htmlElse);
			this.isDrawOnMapOnece = drawOnece;
			this.successFun = fun;
			this.toolbar.activate(esri.toolbars.Draw.EXTENT);
			this.drawGraphic();
		},
		drawingBezierPolygon:function(drawOnece,fun){
			$('#ant-col').empty();
			try{
							templatePickerPoint.destroy();
						}
						catch(e){}
						var nowDiv=document.getElementById("ant-col");
			$('#ant-col').append(htmlElse);
			this.isDrawOnMapOnece = drawOnece;
			this.successFun = fun;
			this.exToolbar.activate(Extension.DrawEx.BEZIER_POLYGON);
			this.drawGraphic();
		},
		drawingCurve:function(drawOnece,fun){
			$('#ant-col').empty();
			try{
							templatePickerPoint.destroy();
						}
						catch(e){}
						var nowDiv=document.getElementById("ant-col");
			$('#ant-col').append(htmlElse);
			this.isDrawOnMapOnece = drawOnece;
			this.successFun = fun;
			this.exToolbar.activate(Extension.DrawEx.CURVE);
			this.drawGraphic();
		},
		drawingBezierCurve:function(drawOnece,fun){
			$('#ant-col').empty();
			try{
							templatePickerPoint.destroy();
						}
						catch(e){}
						var nowDiv=document.getElementById("ant-col");
			$('#ant-col').append(htmlElse);
			this.isDrawOnMapOnece = drawOnece;
			this.successFun = fun;
			this.exToolbar.activate(Extension.DrawEx.BEZIER_CURVE);
			this.drawGraphic();
		},
		drawingTailedsquadcombat:function(drawOnece,fun){
			$('#ant-col').empty();
			try{
							templatePickerPoint.destroy();
						}
						catch(e){}
						var nowDiv=document.getElementById("ant-col");
			$('#ant-col').append(htmlElse);
			this.isDrawOnMapOnece = 2;
			this.successFun = fun;
			this.exToolbar1.activate("tailedsquadcombat");
			this.drawGraphic();
		},
		exportGraphics:function(){
			var gs = this.drawingLayer.graphics;
			var res = new Array();
			for(var i=0;i<gs.length;i++){
				res.push(gs[i].toJson());
			}
			return JSON.stringify(res);
		},
		drawGraphic:function(){
			this.isDrawing = true;
		},
		color16tocolor10:function(color16,opacity){
			var r = parseInt(color16.substr(1,2),16);
			var g = parseInt(color16.substr(3,2),16);
			var b = parseInt(color16.substr(5,2),16);
			return [r,g,b,opacity];
		}
		
	};
	
	drawing.initDrawing(map,layer,div);
	return drawing;
}
//删除图形
function deleteGraphic(){
	drawing.graphic.getLayer().remove(drawing.graphic);
	drawing.graphic = null;
	drawing.map.infoWindow.hide();
}
//地图量算
function measureGraphic(){
	measure.doMeasureWithGraphic(drawing.graphic.geometry);
}

function updateGraphic(name,attr){
	var a = drawing.graphic.attributes;
	a[name] = attr;
	if(drawing.graphic.symbol.type=="textsymbol"){
		drawing.graphic.symbol.setText(a["name"]);
		drawing.graphic.setAttributes(a);
		drawing.drawingLayer.setVisibility(false);
		drawing.drawingLayer.setVisibility(true);
	}else{
		drawing.graphic.setAttributes(a);
	}
}
//转变颜色
function color16tocolor10(color16,opacity){
	var r = parseInt(color16.substr(1,2),16);
	var g = parseInt(color16.substr(3,2),16);
	var b = parseInt(color16.substr(5,2),16);
	return [r,g,b,opacity];
}

//获取geometry的中心点
function getPoint(g){
	var point = null;
	if(g.type=="polygon"){
		if(g.getExtent().getCenter()){
			point = g.getExtent().getCenter();
			return point;
		}else{
			point = g.rings[0][0];
		}
	}else if(g.type=="polyline" || g.type=="line"){
		if(g.getExtent().getCenter()){
			point = g.getExtent().getCenter();
			return point;
		}else{
			point = g.paths[0][0];
		}
	}else{
		point = g;;
		return point;
	}
	var p = esri.geometry.Point(point[0],point[1],mapobj.map.spatialReference);
	return p;
}

//地图量测工具
function measureSimpleTool(map){
	var measure = {
			map:null,//地图对象
			toolbar:null,//draw绘画对象
			polylineSymbol:null,//线的样式
			polygonSymbol:null,//面的样式
			measureLayer:null,//测量结果显示图层
			measureGeo:null,//测量要素
			showDom:null,//测量结果出来函数
			isOneceMeasure:true,//是否单次测量
			isDrawOnly:false,
			initMeasure:function(map){
				this.map = map;
				if(this.toolbar==null)
					this.toolbar = new esri.toolbars.Draw(map);
				dojo.connect(this.toolbar, "onDrawEnd",this, this.doMeasure);
				this.polylineSymbol = new esri.symbol.SimpleLineSymbol(
						esri.symbol.SimpleLineSymbol.STYLE_SOLID,
						new dojo.Color([ 0, 255, 0 ]), 2);
				this.polygonSymbol = new esri.symbol.SimpleFillSymbol(
						esri.symbol.SimpleFillSymbol.STYLE_SOLID,
						new esri.symbol.SimpleLineSymbol(
								esri.symbol.SimpleLineSymbol.STYLE_SOLID,
								new dojo.Color([ 255, 0, 0 ]), 2),
						new dojo.Color([ 125, 0, 0, 0.25 ]));
				this.measureLayer = this.map.graphics;
			},
			measureTool : function(tool) {
				switch (tool) {
					case "length": {//距离 
						this.toolbar.activate(esri.toolbars.Draw.POLYLINE);
						break;
					}
					case "area": {//面积  
						this.toolbar.activate(esri.toolbars.Draw.POLYGON);
						break;
					}
					case "clear": {//清除  
						this.toolbar.deactivate();
						this.map.graphics.clear();
						this.map.infoWindow.hide();
						break;
					}
					case "rectangle":
						isDrawOnly = true;
						this.toolbar.activate(esri.toolbars.Draw.RECTANGLE);
						break;
					case "circle":
						isDrawOnly = true;
						this.toolbar.activate(esri.toolbars.Draw.CIRCLE);
						break;
					case "polygon":
						isDrawOnly = true;
						this.toolbar.activate(esri.toolbars.Draw.POLYGON);
						break;
				}
			},
			doMeasure : function(geometry) {
				if(this.isOneceMeasure){
					this.toolbar.deactivate();
				}
				this.measureGeo = geometry;
				var graphic;
				switch (geometry.type) {
				case "polyline":
					graphic = new esri.Graphic(geometry, this.polylineSymbol);		
					break;
				case "polygon":
					graphic = new esri.Graphic(geometry, this.polygonSymbol);	
					break;
				}
				this.map.graphics.clear();
				this.map.graphics.add(graphic);
				if(this.isDrawOnly){
					this.isDrawOnly = false;
					return;
				}
				this.measureGeometry([geometry]);
			},
			doMeasureWithGraphic : function(geometry,showFun) {
				this.showDom = showFun;
				if(this.isOneceMeasure){
					this.toolbar.deactivate();
				}
				this.measureGeo = geometry;
				this.measureGeometry([geometry]);
			},
			measureGeometry : function(geometrys) {
				var length = 0;
				var area = 0;
				if (geometrys[0].type == "polyline") {
					this.measureServices(geometrys,true,this.showResult)
					//length =  geoEngine.geodesicLength(geometrys[0], 4326);
				}else if (geometrys[0].type == "polygon") {
					//length =  geoEngine.geodesicLength(geometrys[0], 4326);
			       // area = geoEngine.geodesicArea(geometrys[0], 4326);
					
					this.measureServices(geometrys,false,this.showResult,this.showResult)
			    };
			    
			   
			    
			},
			showResult:function(That,length,area){
				 if(That.showDom==null){
						var CurPos = That.measureGeo.getExtent().getCenter();
						That.map.infoWindow.hide();
						That.map.infoWindow.setTitle("测量");
//						this.map.infoWindow.setContent("长度:"+length.toFixed(4)+" 米  <br />面积:"+area.toFixed(4)+" 平方米");
						var features = [];
			        	var buildingFootprintTemplate = new esri.InfoTemplate("","长度:"+length.toFixed(4)+" 千米  <br />面积:"+area.toFixed(4)+" 平方千米");
			        	var feature = new esri.Graphic(That.measureGeo.toJson());
						feature.setInfoTemplate(buildingFootprintTemplate);
						features.push(feature);
			        	drawing.map.infoWindow.setFeatures(features);
			        	That.map.infoWindow.show(CurPos);
					}else{
						That.showDom([length,area],"长度:"+length.toFixed(4)+" 千米;  <br />面积:"+area.toFixed(4)+" 平方千米");
					}
			},
			measureServices:function(geometrys,isLine,outputDistance,outputAreaAndLength){
				var  geometryServiceUrl= geometry_ServiceUrl;//arcgis测量服务url
			    var geometryService = new esri.tasks.GeometryService(geometryServiceUrl);
			    var That=this;
	            var lengthParams = new esri.tasks.LengthsParameters();
	            lengthParams.lengthUnit = esri.tasks.GeometryService.UNIT_KILOMETER;
	            lengthParams.geodesic = true;
	            lengthParams.outSR = new esri.SpatialReference(WKID);
	            if(isLine){
	            	lengthParams.polylines = geometrys;
	  	            geometryService.lengths(lengthParams, function(result){
	            	  // do something with the results here
	  	            	var length=0;
	  	            for (var i = 0; i < result.lengths.length; i++) {
	  	            		length += parseFloat(result.lengths[i]);
	  	              	}
	  	          outputDistance(That,length,0);
	            		  console.log(measurements)
	  	            });
	            }
	           WKID=mapContainer.spatialReference.wkid;
	            var areasAndLengthParams = new esri.tasks.AreasAndLengthsParameters();
	            areasAndLengthParams.lengthUnit = esri.tasks.GeometryService.UNIT_KILOMETER;
	            areasAndLengthParams.areaUnit = esri.tasks.GeometryService.UNIT_SQUARE_KILOMETERS;
	            areasAndLengthParams.calculationType = "geodesic";
	            areasAndLengthParams.outSR = new esri.SpatialReference({wkid: WKID});
	           
	            geometryService.simplify(geometrys, function(simplifiedGeometries) {
	            	  areasAndLengthParams.polygons = simplifiedGeometries;
	            	  geometryService.areasAndLengths(areasAndLengthParams, function(result){
	            			var length=0;
	    	  	            for (var i = 0; i < result.lengths.length; i++) {
	    	  	            		length += parseFloat(result.lengths[i]);
	    	  	              	}
	    	  	          var area=0;
	    	  	            for (var i = 0; i < result.areas.length; i++) {
	    	  	            	area += parseFloat(result.areas[i]);
	    	  	              }
	    	  	          outputAreaAndLength(That,length,area);
	            	    });
	            	});
			}
				
		};
		measure.initMeasure(map);
		return measure;
}
