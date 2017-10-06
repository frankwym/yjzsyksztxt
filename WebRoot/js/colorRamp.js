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
}, {
	"value" : "10108",
	"url" : "images/chartIcon/010108.png"
}, {
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

//RGB切换16位色彩
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

/*16进制颜色转为RGB格式*/  

function HexToRGB(hex){
	 var sColor = hex.toLowerCase();  
	 var sColorChange = [];  
     for(var i=1; i<7; i+=2){  
         sColorChange.push(parseInt("0x"+sColor.slice(i,i+2)));    
     }
     sColorChange.push(1);
     return   sColorChange;
}
