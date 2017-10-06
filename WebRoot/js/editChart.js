//编辑饼状图 isEdit
function edit(map, isEdit) {
	var editProps = {
		map : null,
		isEdit : 0,
		tool : 0,
		chartLayer : null,
		editToolbar : null,
		initEdit : function(map, isEdit) {
			debugger
			this.map = map;
			this.isEdit = isEdit;

			// 固定的图层进行编写
			this.chartLayer = map.getLayer("graphicsLayer2");

		},

		startEdit : function() {
			if (this.isEdit == 1) {
				this.editToolbar = new esri.toolbars.Edit(this.map);
				// 开启移动
				this.tool = this.isEdit | esri.toolbars.Edit.MOVE;
				var options = {
					allowAddVertices : false,
					allowDeleteVertices : false,
					uniformScaling : false
				};
				debugger
				var That = this;
				this.chartLayer.on("click", function(evt) {
					debugger
					// 取消InfoWindow的显示
					dojo.stopEvent(evt);
					That.editToolbar.activate(That.tool, evt.graphic, options);
					//保存每一次移动的状态
					CutomizeMapRecords.editChartData=That.saveGraphics();
				});
			}
		},

		stopEdit : function() {
			this.editToolbar.deactivate();
		},

		saveGraphics : function() {
			var graphicsArray = [];
			for ( var i in this.chartLayer.graphics) {
				var graphic = this.chartLayer.graphics[i];
				var code = graphic.attributes["区域"];
				var x = graphic.geometry.x;
				var y = graphic.geometry.y;
				graphicsArray.push({
					"code" : code,
					"x" : x,
					"y" : y
				});
			}
			console.log(graphicsArray);
			return graphicsArray;

		},

		recoverGraphics : function() {
			debugger
			if (CutomizeMapRecords.editChartData == undefined
					|| CutomizeMapRecords.editChartData == null) {
				return;
			}
			var graphicsArray = CutomizeMapRecords.editChartData;
			for ( var i in this.chartLayer.graphics) {
				var graphic = this.chartLayer.graphics[i];
				var realGraphic = this.findByCode(graphicsArray,
						graphic.attributes["区域"]);
				graphic.geometry.x = realGraphic.x;
				graphic.geometry.y = realGraphic.y;
			}
			// 刷新显示
			this.chartLayer.redraw();
		},

		findByCode : function(graphicsArray, code) {
			for ( var i in graphicsArray) {
				var graphic = graphicsArray[i];
				if (graphic.code == code) {
					return graphic;
				}
			}
			return null;
		}
	}
	editProps.initEdit(map, isEdit);
	return editProps;

}