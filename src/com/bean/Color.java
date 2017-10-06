package com.bean;

import java.util.Arrays;
import java.util.Collections;

public class Color {
	// color ribbon
	static int[][] color1001 = { { 255, 254, 227 }, { 255, 253, 201 },
			{ 255, 251, 177 }, { 255, 250, 137 }, { 252, 228, 116 },
			{ 247, 197, 95 }, { 241, 165, 75 }, { 233, 129, 70 },
			{ 228, 102, 66 }, { 201, 81, 61 } };
	static int[][] color1002 = { { 229, 243, 252 }, { 196, 229, 250 },
			{ 164, 215, 246 }, { 127, 200, 242 }, { 86, 187, 237 },
			{ 19, 174, 233 }, { 0, 161, 227 }, { 0, 145, 211 },
			{ 0, 131, 186 }, { 0, 112, 155 } };
	static int[][] color1003 = { { 253, 235, 242 }, { 250, 210, 228 },
			{ 246, 186, 211 }, { 242, 161, 195 }, { 237, 136, 178 },
			{ 233, 111, 161 }, { 228, 79, 143 }, { 212, 46, 125 },
			{ 188, 47, 115 }, { 158, 43, 101 } };
	static int[][] color1004 = { { 255, 254, 227 }, { 255, 253, 201 },
			{ 255, 251, 177 }, { 255, 250, 137 }, { 228, 237, 121 },
			{ 186, 219, 107 }, { 143, 201, 93 }, { 88, 181, 97 },
			{ 18, 169, 98 }, { 0, 147, 91 } };
	static int[][] color1005 = { { 255, 254, 236 }, { 255, 253, 213 },
			{ 255, 252, 189 }, { 255, 251, 164 }, { 255, 250, 137 },
			{ 255, 248, 106 }, { 255, 247, 51 }, { 241, 232, 0 },
			{ 209, 202, 0 }, { 171, 167, 26 } };
	static int[][] color1006 = { { 115, 147, 162 }, { 123, 159, 177 },
			{ 148, 175, 187 }, { 177, 197, 198 }, { 201, 213, 203 },
			{ 227, 232, 205 }, { 241, 235, 198 }, { 242, 228, 186 },
			{ 237, 210, 169 }, { 246, 202, 156 } };
	static int[][] color1007 = { { 239, 204, 166 }, { 239, 214, 174 },
			{ 238, 222, 182 }, { 237, 229, 191 }, { 234, 232, 190 },
			{ 244, 242, 205 }, { 232, 236, 203 }, { 221, 228, 186 },
			{ 202, 214, 164 }, { 187, 202, 151 } };
	static int[][] color1008 = { { 255, 254, 227 }, { 248, 241, 208 },
			{ 242, 230, 187 }, { 237, 210, 167 }, { 246, 202, 156 },
			{ 232, 175, 126 }, { 228, 153, 107 }, { 208, 133, 88 },
			{ 183, 113, 73 }, { 188, 202, 151 } };
	static int[][] color1009 = { { 252, 225, 237 }, { 248, 199, 220 },
			{ 244, 174, 203 }, { 237, 136, 178 }, { 215, 117, 166 },
			{ 185, 97, 153 }, { 155, 77, 142 }, { 128, 80, 145 },
			{ 110, 83, 148 }, { 189, 202, 151 } };
	static int[][] color1010 = { { 244, 250, 249 }, { 237, 246, 241 },
			{ 217, 237, 234 }, { 200, 229, 222 }, { 183, 221, 206 },
			{ 149, 207, 199 }, { 116, 195, 187 }, { 105, 173, 166 },
			{ 79, 150, 144 }, { 190, 202, 151 } };

	// 产生颜色数组
	public static int[][] generateColor(String gradeNumberSelect,
			String _colorSetSelect, String _colorOrderSelect) {
		int[][] _colorScheme = new int[Integer.parseInt(gradeNumberSelect)][];// 所选颜色的完整的颜色数组
		int[][] _colorSchemeFull = new int[10][]; // 最后返回的颜色数组

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
			Collections.reverse(Arrays.asList(_colorScheme));
		}
		return _colorScheme;
	}
}
