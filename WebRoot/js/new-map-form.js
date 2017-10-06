var url="http://localhost:8080/TJCH_DLGQ/"
var currentMap = {};// 当前地图的属性
	currentMap.checkCount=0;

		// 新建地图，输入地图名和地图描述

		$(function () {
		    // 0.初始化fileinput
		    var oFileInput = new FileInput();
		    oFileInput.Init("excel_file", "./uploadExcelServlet");
		});
		
		function newMap() {
			var mapName = $("#mapName").val();
			var mapDescribe = $("#mapDescribe").val();
			if (mapName == "" || mapDescribe == "") {
				alert("输入不许为空");
				return
			}
			currentMap.mapName=mapName;
			currentMap.mapDescribe=mapDescribe;
			
			CutomizeMapRecords.mapName=mapName;
			CutomizeMapRecords.mapDescribe=mapDescribe;
			 $.ajax({
		            url: './SaveUserMap',
		            type: 'POST',
		            data: {
		            	mapName: mapName,
		            	userId:"default",
		            	isSave:SAVE_INDEX
		            },
		            success: function (res) {
		            	if(res!=""){
		            		 alert(res);
		            		 return;
		            	}
		            	$("#thisMapName").html(currentMap.mapName);
		    			$("#newMapModal").modal("hide");
		    			SAVE_INDEX++;
		            },
		            error: function (res) {
		                alert("错误:"+res);	
		            }
		        });
			
			
			
		}
		
			// 初始化fileinput
			var FileInput = function () {
				var oFile = new Object();

		    // 初始化fileinput控件（第一次初始化）
		    oFile.Init = function(ctrlName, uploadUrl) {
		    	var control = $('#' + ctrlName);

		    // 初始化上传控件的样式
		    control.fileinput({
		        language: 'zh', // 设置语言
		        uploadUrl: uploadUrl, // 上传的地址
		        allowedFileExtensions: ['xls', 'xlsx'],// 接收的文件后缀
		        showUpload: false, // 是否显示上传按钮
		        showCaption: true,// 是否显示标题
		        browseClass: "btn btn-primary", // 按钮样式
		        dropZoneEnabled: false,// 是否显示拖拽区域
		        minImageWidth: 0, // 图片的最小宽度
		        minImageHeight: 0,// 图片的最小高度
		        maxImageWidth: 0,// 图片的最大宽度
		        maxImageHeight: 0,// 图片的最大高度
		        previewFileIcon: "",
		        // maxFileSize: 0,//单位为kb，如果为0表示不限制文件大小
		        // minFileCount: 0,
		        maxFileCount: 1, // 表示允许同时上传的最大文件个数
		        enctype: 'multipart/form-data',
		        validateInitialCount:true,
		        previewFileIcon: "<i class='glyphicon glyphicon-king'></i>",
		        msgFilesTooMany: "选择上传的文件数量({n}) 超过允许的最大数值{m}！",
		    }).on("filebatchselected", function(event, files) {
		    	$(this).fileinput("upload");
            // 隐藏预览效果
            $(".fileinput-remove-button").hide()
            $(".file-preview").hide()
        })
		    .on("fileuploaded", function(event, data) {
		    	if(data.response)
		    	{
		    		currentMap.columns=data.response.columns;
		    		currentMap.data=data.response.datas;
		    		currentMap.filename=data.response.filename;
		    		$("#table").bootstrapTable('destroy').bootstrapTable({   pagination : true,
		    			pageList : [2, 4],
		    			pageSize : 2,
		    			pageNumber : 1,
		    			search : false,
		    			showColumns : false,
		    			columns:data.response.columns,
		    			data: data.response.datas})
		    		$("select.form-control").html("<option>无</option>");
		    		$("#checkboxs").html("");
		    		for(var i=0;i< data.response.columns.length;i++ ){
		    			var col=data.response.columns[i].title
		    			var option="<option value="+col+">"+col+"</option>"
		    			$("select.form-control").append(option)
		    			var label="<label class='checkbox-inline'> <input type='checkbox' value='"+col+"'>"+col+"</label>";
		    			$("#checkboxs").append(label)
		    		}
		    		currentMap.checkCount=0;
		    		
		    		// 点击数据
		    		$("input[type='checkbox']").click(function(){
		    			currentMap.checkCount=$("input[type='checkbox']:checked").length;
		    			if(currentMap.checkCount>10){
		    				alert("指标数过多")
		    				return false
		    			}
		    		})
		    		
		    		
		    	}
		    });	
		}
		return oFile;
	};


	// 切换事件 上传数据 切换制图单元
	$(function(){
		$('input:radio[name="uploadDataRadio"]').change(function(e){
			var val=e.target.value
			if("qu"==val){
				$("#quSelect").css("display","block")
				$("#jiedaoSelect").css("display","none")
			}else{
				$("#quSelect").css("display","none")
				$("#jiedaoSelect").css("display","block")
			}
			console.log(e.target.value);
		})
	})

	
	$("#submitData").click(function(){
		    		// 获取数据
		    		// var timeData=$("select").find("option:selected")[0].value
		    		var quCode=$("#districtSelect").find("option:selected")[0].value// 区级别
		    		var jiedaoCode=$("#streetSelect").find("option:selected")[0].value// 街道级别
		    		
		    		// 添加radio的限制
		    		var radioVal=$('input:radio:checked').val();
		    		if("qu"==radioVal){
		    			jiedaoCode="无"
		    		}
		    		
		    		if("jiedao"==radioVal){
		    			quCode="无"
		    		}
		    		
		    		if(jiedaoCode==quCode){
						alert("请检查空间指标字段")
						return false
					}
		    		
		    		
		    		/*
					 * if(timeData==quCode){ alert("请检查时间和区域编码字段") return false }
					 */
					 var checkData=$("#upLoadExcel input[type='checkbox']:checked")
					 var indexData=[];
					 for(var i=0;i<checkData.length;i++){
					 	var temp= $.trim(checkData[i].value);
						// if(temp==timeData||temp==quCode){
							if(temp==quCode||jiedaoCode==temp){
								alert("请检查指标字段")
								return false
							}
							indexData.push(temp)
						}
					 
					 // 检查是否选择了指标
					 
					 if(indexData.length==0){
						 alert("至少选择一个指标")
						 return false
					 }
					// 提取数据
					debugger
					var selectData=[];
					for(var i=0;i<currentMap.data.length;i++){
						var tem=[]
						// 符合quCode的值,放在第一列
						if("无"!=quCode){
							tem.push(currentMap.data[i][quCode])
						}
						// 符合jiedaoCode的值,放在二列
						if("无"!=jiedaoCode){
							tem.push(currentMap.data[i][jiedaoCode])
						}
						
						
						// 符合指标的值
						for(var j=0;j<indexData.length;j++){
							tem.push(currentMap.data[i][indexData[j]])
						}
						selectData.push(tem)
					}
					
					// 在最前面添加一个quCode
					
					if("无"!=jiedaoCode){
						indexData=["jiedaoCode"].concat(indexData)
					}
					
					if("无"!=quCode){
						indexData=["quCode"].concat(indexData)
					}
					
					
					console.log(indexData)
					console.log(selectData)
					console.log(currentMap.filename)
					
					var data ={columns:indexData,datas:selectData}
					console.log(data)
					$.ajax({
						type: "POST",
						url: url+"saveExcel2Clob",
						data: {username:CutomizeMapRecords.userName,filename:currentMap.filename, data:JSON.stringify(data)},
						dataType: "json",
						success: function(data){
							alert(data.msg)
							
							// 刷新目录树
							buildInterfaceOfSelectData();
						}
					});
				})