/*全局变量声明*/
var LOGIN_URL="http://10.2.101.39/hgp/ztzt/login";
//图片点击事件
function setIamgeRes(e) {
	console.log(e.dataset.image)
	window.localStorage.setItem("image", e.dataset.image);
}


$(function() {
	//checkLogin();
	
	//外网测试更改
	window.localStorage.setItem("token", "11225566"); 
    window.localStorage.setItem("loginId", "default");
    window.localStorage.setItem("username", "default");
    window.localStorage.setItem("viewname", "default");
	$(".scroll-content").mCustomScrollbar();
	$.getJSON("resource/image.json", function(typeJson) {
		buildInterfaceOfImageTheme(typeJson)
	});
	
})

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
function buildInterfaceOfImageTheme(typeJson) {
	$("#data-content").empty();
	$("#data-content").append("<div id=\"Images\" ></div>");
	var nodes = [ {
		text : "主目录",
		color : "black",
		nodes : typeJson
	} ];
	
	//更新初始化界面
	$(".main-container").empty();
	$(".main-container").append('<div class="row"></div>');
	for ( var i = 0; i < typeJson.length; i++) {
		for ( var j = 0; j < typeJson[i].nodes.length; j++) {
			var tempTheme = typeJson[i].nodes[j]
			var html = makeHtml(tempTheme)
			$(".row").append(html);
		}
	}
	
	$('#Images').treeview({
		data : nodes,
		onNodeSelected : function(event, data) {
			$(".main-container").empty();

			// 如果点击是主目录 nodeId为0
			if (data.nodeId == 0) {
				$(".main-container").append('<div class="row"></div>');
				for ( var i = 0; i < data.nodes.length; i++) {
					for ( var j = 0; j < data.nodes[i].nodes.length; j++) {
						var tempTheme = data.nodes[i].nodes[j]
						var html = makeHtml(tempTheme)
						$(".row").append(html);
					}
				}
				
			}
			// 如果点击是主题 parentId为0
			else if (data.parentId == 0 && data.nodeId != 0) {
				$(".main-container").append('<div class="row"></div>');
				for ( var i = 0; i < data.nodes.length; i++) {
					var tempTheme = data.nodes[i]
					var html = makeHtml(tempTheme)
					$(".row").append(html);
				}
			}
			// 如果点击是图片则直接浏览地
			else{
				//document.getElementById("container").innerHTML = '<div class="ViewContainer"></div> ';
				document.getElementById("container").innerHTML = '<div class="ViewContainer"><img id="img"  src="'+data.val+'" alt=""></div>';
				var viewer = new Viewer(document.getElementById('img'), { 'navbar': false, "inline": true, 'toolbar': false, 'title': false, "rotatable": false, "scalable": false, "button": false });
                //$('#mainMap').viewer({ 'navbar': false, "inline": true, 'toolbar': false, 'title': false, "rotatable": false, "scalable": false, "button": false });
			}
		
			$(".thumbnail-container").unbind('click').click(function(){
				//var d=this;
				var img=$(this).find("img")[0];
				var src=$(img).attr("src").toString();
				var thisSrc=src.replace("Thumbnail","");
				$(".main-container").empty();
				document.getElementById("container").innerHTML = '<div class="ViewContainer"><img id="img"  src="'+thisSrc+'" alt=""></div>';
				var viewer = new Viewer(document.getElementById('img'), { 'navbar': false, "inline": true, 'toolbar': false, 'title': false, "rotatable": false, "scalable": false, "button": false });

				this;
				});
		}
	})
	$(".thumbnail-container").unbind('click').click(function(){
		//var d=this;
		var img=$(this).find("img")[0];
		var src=$(img).attr("src").toString();
		var thisSrc=src.replace("Thumbnail","");
		$(".main-container").empty();
		document.getElementById("container").innerHTML = '<div class="ViewContainer"><img id="img"  src="'+thisSrc+'" alt=""></div>';
		var viewer = new Viewer(document.getElementById('img'), { 'navbar': false, "inline": true, 'toolbar': false, 'title': false, "rotatable": false, "scalable": false, "button": false });

		});
}

// 根据结点信息生成html
function makeHtml(tempTheme) {
	if(tempTheme.thumbnail==""||tempTheme.thumbnail==undefined){
		return;
	}
	var html = '<div class="col-xs-3 col-sm-3 col-md-3 thumbnail-container"">'
			+ '<div class="thumbnail-map">'
			+ '<img class="thumbnail-img" src="' + tempTheme.thumbnail + '"'
			+ 'data-image="' + tempTheme.val + '"onclick="setIamgeRes(this)"'
			+ 'alt="' + tempTheme.text + '"/>' + '</div>'
			+ '<h5 class="thumbnail-title ellipsis"' + 'title="'
			+ tempTheme.text + '">' + tempTheme.text + '</h5>'
			+ '<p class="thumbnail-info ellipsis">' + tempTheme.describe
			+ '</p>' + '</div>';
	return html;
}
