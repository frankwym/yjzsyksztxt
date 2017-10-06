dojo.require("esri.dijit.Print");
function printMap(map, dom) {
	esriConfig.defaults.io.proxyUrl = "http://localhost:8080/TJCH_DLGQ/proxy.jsp";
	var printM = {
		map : null,//
		printDom : null,//
		printer : null,//
		// printUrl:"http://sampleserver6.arcgisonline.com/arcgis/rest/services/Utilities/PrintingTools/GPServer/Export%20Web%20Map%20Task",

		// printUrl:"http://115.28.75.54:6080/arcgis/rest/services/Utilities/PrintingTools/GPServer/Export
		// Web Map Task",//
		//printUrl : "http://10.5.211.222:6080/arcgis/rest/services/Utilities/PrintingTools/GPServer/Export%20Web%20Map%20Task",
		printUrl : "http://10.5.211.222:6080/arcgis/rest/services/Utilities/PrintingTools/GPServer/Export%20Web%20Map%20Task",
		printComplete : null,
		printError : null,
		template :{
			label : "Map",
			format : "PNG8",
			//layout: "MAP_ONLY",
			layout : "A4_wym1",
			//outScale : this.map.getScale(),
			outScale : 577143.747208662,
			preserveScale : true,
			exportOptions : {
				//width : this.map.width,
				//height : this.map.height,
				width : 3500,
				height : 4200,
				dpi : 200
			},
			layoutOptions : {
				scalebarUnit : "Kilometers",
				titleText : $("#titleName").val(),
				authorText : "MAP",
				copyrightText : "MAP",
			}
		},//
		count : 0,
		initPrint : function(map, dom) {
			this.map = map;
			this.printDom = dom;
			this.printer = new esri.dijit.Print({
				map : this.map,
				url : this.printUrl
			}, dom);
			this.printer.hide();
			this.printer.startup();
			dojo.connect(this.printer, "onPrintStart", this, function(event) {
			});
			dojo.connect(this.printer, "onPrintComplete", this,
					function(event) {
						if (this.printComplete != null) {
							this.printComplete(event);
							this.printComplete = null;
						} else {
							window.open(event.url);
						}

					});
			dojo.connect(this.printer, "onError", this, function(event) {

				//重复两次，两次都错误的情况下就跳出
				if (this.count < 2) {
					this.count++;
					this.printer.printMap(this.template);
				} else {
					alert(event.message);
					this.count == 0;
					progressCtr("hide");
				}
			});
		},
		printMapThumbtnail : function(successFun) {
			this.printComplete=successFun;
			this.printer.printMap(this.template);
		},
		printMapDefault : function(dp, format) {

			var template = {
				label : "Map",
				format : "PDF",
				layout : "MAP_ONLY",
				outScale : this.map.getScale(),
				preserveScale : true,
				exportOptions : {
					width : this.map.width,
					height : this.map.height,
					dpi : 96
				},
				layoutOptions : {
					scalebarUnit : "Kilometers",
					titleText : "MAP",
					authorText : "MAP",
					copyrightText : "MAP",
				}
			};
			mapobj.print.printTemplate(template);
		},
		printMapLayout : function(layout, scale) {

			this.template = {
				label : "Map",
				format : "PDF",
				layout : layout == null || layout == "" ? "MAP_ONLY" : layout,
				outScale : scale == null || scale == "" ? this.map.getScale()
						: scale,
				preserveScale : true,
				exportOptions : {
					width : this.map.width * 2,
					height : this.map.height * 2,
					dpi : 96

				},
				layoutOptions : {
					scalebarUnit : "Kilometers",
					titleText : "MAP",
					authorText : "MAP",
					copyrightText : "MAP",
				}
			};
			this.printer.printMap(this.template);
		},
		printTemplate : function(t) {
			this.template = t;
			this.printer.printMap(t);
		},
		printMapDefault : function() {
			this.template = {
				label : this.label == null ? "Map" : this.label,
				format : this.format == null ? "PDF" : this.format,
				layout : this.layout == null ? "A4 landscape" : this.layout,
				outScale : this.outScale == null ? this.map.getScale()
						: this.outScale,
				preserveScale : this.preserveScale == null ? true
						: this.preserveScale,
				exportOptions : {
					width : this.exportOptions.width == null ? this.map.width
							: this.exportOptions.width,
					height : this.exportOptions.height == null ? this.map.height
							: this.exportOptions.height,
					dpi : this.exportOptions.dpi == null ? 96
							: this.exportOptions.dpi
				},
				layoutOptions : {
					legendLayers : this.legendLayers,
					scalebarUnit : this.layoutOptions.scalebarUnit == null ? "Kilometers"
							: this.layoutOptions.scalebarUnit,
					titleText : this.layoutOptions.titleText == null ? ""
							: this.layoutOptions.titleText,
					authorText : this.layoutOptions.authorText == null ? ""
							: this.layoutOptions.authorText,
					copyrightText : this.layoutOptions.copyrightText == null ? ""
							: this.layoutOptions.copyrightText
				}
			};
			// this.printer.startup();//初始化div控件的文字，并调出默认的打印样式png,两个方到一起就不会打印两次了
			this.printer.printMap(this.template);
			// this.printer.hide();
		}

	};
	printM.initPrint(map, dom);
	return printM;

}

function convertImgToBase64(url, callback, outputFormat) {
	var canvas = document.createElement('CANVAS');
	var ctx = canvas.getContext('2d');
	var img = new Image;
	img.crossOrigin = 'Anonymous';
	img.onload = function() {
		canvas.height = img.height;
		canvas.width = img.width;
		ctx.drawImage(img, 0, 0);
		var dataURL = canvas.toDataURL(outputFormat || 'image/png');
		callback.call(this, dataURL);
		// Clean up
		canvas = null;
	};
	img.src = url;
}