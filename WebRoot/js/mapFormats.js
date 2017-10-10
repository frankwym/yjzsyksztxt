//还原制图界面
function rebuildMappingInterface(){
	try{
		$("#map-print-title").remove();
		$($(".map-print-top")[0]).remove();
		$($(".map-print-bottom")[0]).remove();
		$("#map-print-legend").remove();
		$("#class_legend").remove();
		$("#statistic_legend").remove();
		$("#arrow").remove();
		$("#map-print-chart").remove();
		$("#map-print-text").remove();
		$("#myMap").removeClass("map-print-Container");
		$("#myMap").addClass("map-area");
		$("#myMap").css("height","auto");
	}catch(e){
		alert(e.message);
	}
	
}
//构建打印模板界面
function buildInterfaceOfPrintMat() {
	$("#myMap").removeClass("map-area");
	$("#myMap").addClass("map-print-Container");
	$("#myMap").css("height","auto");
	var mapInnerHtml='<div id="map-print-title" class="map-print-title"><span id="print-title" style="font-size:20px;margin-left:15px"></span></div>'+
	'<div class="map-print-top"><img src="./images/bottomgb.PNG" style="width:100%;height:100%"/></div>'+
	'<div class="map-print-bottom" ><img src="./images/titlebg.PNG" style="width:100%;height:100%"/></div>'+
	'<div id="map-print-legend" class="map-print-legend">'+
	'<img id="class_legend" src="">'+
	'<img id="statistic_legend" src=""></div>'+
	'<img id="arrow" class="arrow" src="./images/arrow.png"></img>'+
	'<img id="map-print-chart" class="map-print-chart"></img>'+
	'<div id="map-print-text" class="map-print-text"><span id="print-text" style="font-family:simsun;font-size:24px"></span></div></div>';
	$("#myMap").append(mapInnerHtml);
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
$("#fontValue").selectpicker("refresh");
/*$("#textboxColor").bind('change', function() {
	$("#map-print-text").css("background-color",$("#textboxColor").val());
});*/
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
			var mapName = CutomizeMapRecords.mapName; 
			var userId = window.localStorage.getItem("username");
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
//从url中获取参数
function getURLParamsString(name) {
 var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
 var r = window.location.search.substr(1).match(reg);
 if (r != null) return decodeURI(r[2]);
 return null;
}