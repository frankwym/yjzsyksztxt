//要修改的地图名称序号
var editingMapIndex;

// 地图数据数组
var maps = [];

/* 页面加载完触发的函数 */
$(function() {
	buildMyMapPage();
	// 切换事件
	$('#switch').on('switch-change', function(e, data) {
		var $el = $(data.el), value = data.value;
		$(".main-container .row").empty();
		if (value) {
			buildMyMapPage();
		} else {
			getSharedMaps();
		}
	});
});
//构造我的地图页面
function buildMyMapPage(){
	 $.ajax({
         url: './manageMyMap',
         type: 'POST',
         data: {
        	 type:"getMyMapByName",
             userId: window.localStorage.getItem("username")
         },
         success: function (res) {
             if (res != "") {
            	 $(".main-container .row").empty();
            	 maps=[];
            	 resArr=res.split("mymaps-p-l");
            	 $.each(resArr, function(index, el) {
            		 if(resArr[index].trim()!=""){
            			 var mymap=eval("("+resArr[index]+")"); 
            			 maps.push(mymap);
            		 }
            		
            	 });
            	 

     		  var thumbnailMap,
     		    thumbnailTitle,
     		    thumbnailDescribe;
     		 
     		  console.log(maps);
     		 callBackPagination(maps);
             } else {
                 alert("数据为空！");
             }
             $(".thumbnail-map").unbind('click').click(function(){
            	 var mapName = $($(this).find("img")[0]).attr("alt");
            		//待修改
            		//window.localStorage.setItem("username", "default");
            		
            		var userId=window.localStorage.getItem("loginId");
            		window.location.href = "viewmap.html?mapName=" + mapName;
         		});
         },
         error: function (res) {
             alert("发生错误");	
         }
     });
}
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

//查看我的底图
function viewMyMap(index) {
	var mapName = maps[index].mapName;
	//待修改
	//window.localStorage.setItem("username", "default");
	
	var userId=window.localStorage.getItem("loginId");
	window.location.href = "viewmap.html?mapName=" + mapName;
}
//打印我的地图
function printMyMap(index){
	var mapName = maps[index].mapName;
	//待修改
	//window.localStorage.setItem("username", "default");
	
	var userId=window.localStorage.getItem("loginId");
	window.location.href = "mapPrint.html?mapName=" + mapName;
}
//修改我的地图
function editMyMap(){
	var editingMapName=maps[editingMapIndex].mapName;
	var editingMapInfor=$.extend(true, {}, maps[editingMapIndex]);
	
	var mapName = $("#mapName").val();
	var mapDescribe = $("#mapDescribe").val();
	editingMapInfor.mapName=mapName;
	editingMapInfor.mapDescribe=mapDescribe;
	$.ajax({
        url: './manageMyMap',
        type: 'POST',
        data: {
       	 type:"editMyMapByName",
            userId: window.localStorage.getItem("username"),
            oldMapName:editingMapName,
            newMapName:mapName,
            editingMapInfor:JSON.stringify(editingMapInfor)
        },
        success: function (res) {
        	alert(res);
        	$('#editMapModal').modal('hide');
        	$(".main-container .row").empty();
        	buildMyMapPage();
        },
        error: function (res) {
            alert("发生错误");	
        }
    });
}
//填充修改地图的模态框
function fillEditModal(index){
	editingMapIndex=index;
	 $("#mapName").val(maps[editingMapIndex].mapName);
	 $("#mapDescribe").val(maps[editingMapIndex].mapDescribe);
	$('#editMapModal').modal('show');
}
//删除我的地图
function deleteMyMap(index){
	$('#confirm-delete').modal("show");
	$('.btn-ok').unbind('click').click(function(){
		$.ajax({
	        url: './manageMyMap',
	        type: 'POST',
	        data: {
	       	 type:"deleteMyMapByName",
	            userId: window.localStorage.getItem("username"),
	            mapName:maps[index].mapName
	        },
	        success: function (res) {
	        	alert(res);
	        	$('#confirm-delete').modal("hide");
	        	$(".main-container .row").empty();
	        	buildMyMapPage();
	        },
	        error: function (res) {
	            alert("发生错误");	
	        }
	    });
	});
	
        
}
//分享我的地图
var ShareMapName="";
function showShareModal(index){
	ShareMapName=maps[index].mapName;
	$("#shareMapModal").modal("show");
}
function shareMyMap(){
	
	var mapName =ShareMapName;
	var resName=$("#resName").val();
	var mapKeyword = $("#mapKeyword").val();
	if (resName==""||mapName == "" || mapKeyword == "") {
		alert("输入不许为空");
		return
	}
	$.ajax({
        url: './registerCustomizeMap',
        type: 'POST',
        data: {
        	loginId:window.localStorage.getItem("loginId"),
            userId: window.localStorage.getItem("username"),
            mapName:mapName,
            resName:resName,
            mapKeyword:mapKeyword
        },
        success: function (res) {
        	alert(res);
        	$('#confirm-delete').modal("hide");
        	$(".main-container .row").empty();
        	buildMyMapPage();
        },
        error: function (res) {
            alert("发生错误");	
        }
    });
}


//这段代码不许要动  
function callBackPagination(maps) {  
	var totalCount= maps.length;
	var showCount=1;
	var limit=8;
    createTable(showCount, limit, totalCount,maps); //第一次调用(加载第一页数据)  
    $('#callBackPager').extendPagination({  //根据传入的参数生成分页控件  
        totalCount: totalCount,  
        showCount: showCount,  
        limit: limit,             
        callback: function (curr, limit, totalCount) {//当点击分页控件页码时会触发此回调函数  
            createTable(curr, limit, totalCount,maps);  
        }  
    });  
} 

//用的时候只需要写下面这段ajax代码就可以  
function createTable(currPage, limit, total,maps) {      
	//前端分页
	var start=(currPage-1)*limit; //起始的位置
	var end=(currPage)*limit; //起始的位置
	//当前页的maps
	var newmaps=maps.slice(start, end);
	
	 $(".main-container .row").empty();
	 $.each(newmaps, function(index, el) {
		 var GlobalIndex=Number(Number((currPage-1)*limit)+Number(index));
     			 var btnView = creatBtn("查看","fa-eye","viewMyMap('"+GlobalIndex+"')"),
      		    btnEdit = creatBtn("编辑","fa-pencil-square-o","fillEditModal('"+GlobalIndex+"')"),
      		    btnDelete = creatBtn("删除","fa-trash","deleteMyMap('"+GlobalIndex+"')"),
      		  btnShare = creatBtn("分享","fa-share-alt","showShareModal('"+GlobalIndex+"')"),
      		btnPrint= creatBtn("打印","fa-print","printMyMap('"+GlobalIndex+"')"),
      		    btnGroup = '<div class="btn-group btn-group-sm" role="group" aria-label="...">' + btnView + btnEdit + btnDelete+btnShare +btnPrint+ '</div>';
     		    thumbnailMap = '<div class="thumbnail-map"><img class="thumbnail-img" src="' + newmaps[index].mapImage + '" alt="' + newmaps[index].mapName + '"></div>',
     		      thumbnailTitle = '<h5 class="thumbnail-title ellipsis" title="' + newmaps[index].mapName + '">' + newmaps[index].mapName + '</h5>',
     		      thumbnailDescribe = '<p class="thumbnail-info ellipsis">' + newmaps[index].mapDescribe + '</p>';
     		    var thumbnailHtml = '<div class="col-xs-3 col-sm-3 col-md-3 thumbnail-container">' + thumbnailMap + thumbnailTitle + thumbnailDescribe + btnGroup + '</div>';

     		    $(".main-container .row").append(thumbnailHtml);
     		  });
	 $(".thumbnail-map").unbind('click').click(function(){
    	 var mapName = $($(this).find("img")[0]).attr("alt");
    		//待修改
    		//window.localStorage.setItem("username", "default");
    		
    		var userId=window.localStorage.getItem("loginId");
    		window.location.href = "viewmap.html?mapName=" + mapName;
 		});
} 

//创建btn函数
function creatBtn(btnname,classname,clickfunc) {
  return '<button type="button" class="btn btn-default" title="' + btnname + '" onclick="'+clickfunc+'" aria-label="Left Align"><i class="fa '+classname+'" aria-hidden="true"></i></button>'
}
//获取共享的地图
function getSharedMaps(){
	$.ajax({
        url: './GetWorkSpace',
        type: 'POST',
        data: {
        	loginId:window.localStorage.getItem("loginId"),
            row:8,
            page:1
        },
        success: function (res) {
        	alert(res);
        	$('#confirm-delete').modal("hide");
        	$(".main-container .row").empty();
        	buildMyMapPage();
        },
        error: function (res) {
            alert("发生错误");	
        }
    });
}
//构造共享的地图页面
function buildSharedMapPage(){
	 $.ajax({
	        url: './GetWorkSpace',
	        type: 'POST',
	        data: {
	        	loginId:window.localStorage.getItem("loginId"),
	            row:8,
	            page:1
	        },
         success: function (res) {
             if (res != "") {
            	 $(".main-container .row").empty();
            	 maps=[];
            	 resArr=eval("("+res+")");
            	 $.each(resArr.rows, function(index, el) {
            			 maps.push(resArr.rows[index]);
            		
            	 });
            	 

     		  var thumbnailMap,
     		    thumbnailTitle,
     		    thumbnailDescribe;
     		 
     		  console.log(maps);
     		 buildSharedPagination(maps);
             } else {
                 alert("数据为空！");
             }
             $(".thumbnail-map").unbind('click').click(function(){
            	 var mapId = $($(this).find("img")[0]).attr("alt");
            		//待修改
            		//window.localStorage.setItem("username", "default");
            		
            		var userId=window.localStorage.getItem("loginId");
            		window.location.href = "viewmap.html?mapId=" + mapId;
         		});
         },
         error: function (res) {
             alert("发生错误");	
         }
     });
}
//共享地图分页
function buildSharedPagination(maps) {  
	var totalCount= maps.length;
	var showCount=1;
	var limit=8;
	createSharedTable(showCount, limit, totalCount,maps); //第一次调用(加载第一页数据)  
    $('#callBackPager').extendPagination({  //根据传入的参数生成分页控件  
        totalCount: totalCount,  
        showCount: showCount,  
        limit: limit,             
        callback: function (curr, limit, totalCount) {//当点击分页控件页码时会触发此回调函数  
        	createSharedTable(curr, limit, totalCount,maps);  
        }  
    });  
} 

//构建共享地图分页  
function createSharedTable(currPage, limit, total,maps) {      
	//前端分页
	var start=(currPage-1)*limit; //起始的位置
	var end=(currPage)*limit; //起始的位置
	
	var newmaps=maps.slice(start, end);
	debugger
	console.log(newmaps)
	 $(".main-container .row").empty();
	 $.each(newmaps, function(index, el) {
     			 var btnView = creatBtn("查看","fa-eye","viewMyMap('"+index+"')"),
      		    btnEdit = creatBtn("编辑","fa-pencil-square-o","fillEditModal('"+index+"')"),
      		    btnDelete = creatBtn("删除","fa-trash","deleteMyMap('"+index+"')"),
      		  btnShare = creatBtn("分享","fa-share-alt","showShareModal('"+index+"')"),
      		    btnGroup = '<div class="btn-group btn-group-sm" role="group" aria-label="...">' + btnView + btnEdit + btnDelete+btnShare + '</div>';
     		    thumbnailMap = '<div class="thumbnail-map"><img class="thumbnail-img" src="images/mymap/map2.png" alt="' + maps[index].id + '"></div>',
     		      thumbnailTitle = '<h5 class="thumbnail-title ellipsis" title="' + maps[index].workName + '">' + maps[index].workName + '</h5>',
     		      thumbnailDescribe = '<p class="thumbnail-info ellipsis">' + maps[index].workName + '</p>';
     		    var thumbnailHtml = '<div class="col-xs-3 col-sm-3 col-md-3 thumbnail-container">' + thumbnailMap + thumbnailTitle + thumbnailDescribe + '</div>';

     		    $(".main-container .row").append(thumbnailHtml);
     		  });
} 
