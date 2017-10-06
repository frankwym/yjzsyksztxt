package com.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.PaneInformation;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import com.alibaba.fastjson.JSON;

/**
 * 主要提供对Excel的各种处理,侧重点是取数据
 * 
 * @author 李元元
 * 
 */
public class POIExcelUtil {

	static SimpleDateFormat sFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	static short[] yyyyMMdd = { 14, 31, 57, 58, 179, 184, 185, 186, 187, 188 };
	static short[] HHmmss = { 20, 32, 190, 191, 192 };
	static List<short[]> yyyyMMddList = Arrays.asList(yyyyMMdd);
	static List<short[]> hhMMssList = Arrays.asList(HHmmss);

	/**
	 * 根据路径,获取WorkBook对象
	 * 
	 * @param filePath
	 *            文件路径
	 * @return workbook
	 * @throws Exception
	 */
	public static Workbook getExcelWorkbook(String filePath) throws Exception {
		Workbook workbook = null;
		File file = new File(filePath);
		if (file.exists()) {
			workbook = getWorkBookByStream(new FileInputStream(file));
		}
		return workbook;
	}

	/**
	 * 根据输入流ins获取WorkBook对象
	 * 
	 * @param ins
	 *            输入流
	 * @return workbook
	 * @throws Exception
	 */
	public static Workbook getWorkBookByStream(InputStream ins)
			throws Exception {
		return WorkbookFactory.create(ins);
	}

	/**
	 * 根据Workbook,sheetIndex获取sheet对象
	 * 
	 * @param book
	 *            WorkBook对象
	 * @param number
	 *            sheetIndex ,从1开始
	 * @return sheet
	 * @throws Exception
	 */
	public static Sheet getSheetByNum(Workbook book, int number)
			throws Exception {
		return book.getSheetAt(number - 1);
	}

	/**
	 * 根据 Workbook对象返回该Workbook对象中所有sheet的Map数组.
	 * 
	 * @param book
	 * @return Map<sheetIndex , sheetName>
	 * @throws Exception
	 */
	public static Map<Integer, String> getSheetNameByBook(Workbook book)
			throws Exception {
		Map<Integer, String> map = new LinkedHashMap<Integer, String>();
		int sheetNum = book.getNumberOfSheets();
		for (int indexSheet = 1; indexSheet <= sheetNum; indexSheet++) {
			Sheet sheet = getSheetByNum(book, indexSheet);
			map.put(indexSheet, sheet.getSheetName());
		}
		return map;
	}

	/**
	 * 获取workbook数据Map集合
	 * 
	 * @param book
	 * @return Map<Integer, List<List<String>>> @ throws Exception
	 */
	public static Map<Integer, List<List<String>>> getWorkbookDatas(
			Workbook book) throws Exception {
		Map<Integer, List<List<String>>> bookdatas = new HashMap<Integer, List<List<String>>>();
		int sheetNum = book.getNumberOfSheets();
		for (int indexSheet = 1; indexSheet <= sheetNum; indexSheet++) {
			Sheet sheet = getSheetByNum(book, indexSheet);
			bookdatas.put(indexSheet, getSheetDataList(sheet));
		}
		return bookdatas;
	}

	/**
	 * 获取sheet中的数据
	 * 
	 * @param sheet
	 * @return List<List<String>> @ throws Exception
	 */
	public static List<List<String>> getSheetDataList(Sheet sheet)
			throws Exception {
		List<List<String>> sheetdatas = new ArrayList<List<String>>();
		// 需要先合并单元格数据
		mergedRegion(sheet);
		int lastRowNum = getRowNum(sheet);
		int lastCellNum = getColumnNum(sheet);
		for (int i = 0; i < lastRowNum; i++) {
			Row row = sheet.getRow(i);
			sheetdatas.add(getRowDataList(sheet, row, lastCellNum));
		}
		return sheetdatas;
	}

	/**
	 * 获取的数据对象是符合easyui格式的标准JSON对象数据集[{A:x,B:xx,C:xxx},{A:x,B:xx,C:xxx}]
	 * 
	 * @param sheet
	 * @return
	 */
	public static List<Map<String, String>> getSheetDataMap(Sheet sheet) {
		List<Map<String, String>> sheetdatas = new ArrayList<Map<String, String>>();
		int lastRowNum = getRowNum(sheet);
		Row row;
		for (int i = 0; i < lastRowNum; i++) {
			row = sheet.getRow(i);
			Map<String, String> map = getRowDataMap(sheet, row);
			if (!map.isEmpty()) {
				sheetdatas.add(map);
			}
		}
		return sheetdatas;
	}

	/**
	 * 获取的数据对象是符合dHtml格式的非标准JSON对象数据集[{id:1 , data:[x,xx,xxx]},{id:2
	 * ,data:[x,xx,xxx]}]
	 * 
	 * @param sheet
	 * @return
	 */
	public static List<Map<String, Object>> getSheetDataMapAndId(Sheet sheet)
			throws Exception {
		List<Map<String, Object>> sheetdatas = new ArrayList<Map<String, Object>>();
		List<List<String>> sheetLists = getSheetDataList(sheet);
		for (int i = 0; i < sheetLists.size(); i++) {
			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("id", i);
			dataMap.put("data", sheetLists.get(i));
			sheetdatas.add(dataMap);
		}
		return sheetdatas;
	}

	/**
	 * 读取一行的数据,返回的是数据集合List,[x,xx,xxx]
	 * 
	 * @param row
	 */
	public static List<String> getRowDataList(Sheet sheet, Row row,
			int lastCellNum) {
		List<String> rowdatas = new ArrayList<String>();
		if (row == null) {
			for (int i = 0; i < lastCellNum; i++) {
				rowdatas.add("");
			}
		} else {
			for (int i = 0; i < lastCellNum; i++) {
				rowdatas.add(getCellData(row.getCell(i)));
			}
		}
		return rowdatas;
	}

	/**
	 * 获取一行的数据集合,体现的是Map<String , String>{A:x,B:xx,C:xxx}
	 * 
	 * @param row
	 * @return
	 */
	public static Map<String, String> getRowDataMap(Sheet sheet, Row row) {
		Map<String, String> rowdatas = new LinkedHashMap<String, String>();
		String cellVaue;
		int columnNum = 0;
		int lastCellNum = getColumnNum(sheet);
		for (int j = 0; j < lastCellNum; j++) {
			cellVaue = getCellData(row.getCell(j));
			rowdatas.put(getCharByNum(columnNum) + "", cellVaue);
			columnNum = columnNum + 1;
		}
		return rowdatas;
	}

	/**
	 * 获取指定Sheet中指定一列的数据.
	 * 
	 * @param sheet
	 *            指定的Sheet
	 * @param colIndex
	 *            指定的列
	 * @return
	 * @throws Exception
	 */
	public static List<String> getColumnDataList(Sheet sheet, int colIndex)
			throws Exception {
		List<String> coldatas = new ArrayList<String>();
		int lastRowNum = getRowNum(sheet);
		for (int i = 0; i < lastRowNum; i++) {
			coldatas.add(getSheetCellValueWithRowIndexAndColIndex(sheet, i,
					colIndex));
		}
		return coldatas;
	}

	/**
	 * 返回指定sheet页的最大行数,例如:25,则表示下标从0...24
	 * 
	 * @param book
	 * @param sheetIndex
	 * @return
	 */
	public static int getRowNum(Sheet sheet) {
		return sheet.getLastRowNum() + 1;
	}

	/**
	 * 返回指定sheet页的最大列数,例如:25,则表示下标从0...24
	 * 
	 * @param book
	 * @param sheetIndex
	 * @return 列数
	 */
	public static int getColumnNum(Sheet sheet) {
		int maxclNum = 0;
		int lastRowNum = getRowNum(sheet);
		for (int i = 0; i < lastRowNum; i++) {
			if (sheet.getRow(i) != null) {
				int tempNum = sheet.getRow(i).getLastCellNum();
				if (tempNum > maxclNum) {
					maxclNum = tempNum;
				}
			}
		}
		return maxclNum;
	}

	/**
	 * 获取单元格的名称 按照excel常见的名称 例如A1
	 * 
	 * @param int rowInt 行数 从0开始
	 * @param int columnInt 列数 从0开始
	 * @return String
	 */
	public static String getCellName(int rowInt, int columnInt) {
		CellReference cellReference = new CellReference(rowInt, columnInt);
		return cellReference.formatAsString();
	}

	/**
	 * 获取指定单元格的行坐标
	 * 
	 * @param cellName
	 *            例如A1
	 * @return 2
	 */
	public static int getCellRowIndex(String cellName) {
		CellReference cellReference = new CellReference(cellName);
		return cellReference.getRow();
	}

	/**
	 * 获取指定单元格的列坐标
	 * 
	 * @param cellName
	 *            例如A1
	 * @return 0
	 */
	public static int getCellColIndex(String cellName) {
		CellReference cellReference = new CellReference(cellName);
		return cellReference.getCol();
	}

	/**
	 * 获取指定sheet中指定rowNum和cellNum的值
	 * 
	 * @param sheet
	 * @param rowNum
	 * @param cellNum
	 * @return
	 * @throws Exception
	 */
	public static String getSheetCellValueWithRowIndexAndColIndex(Sheet sheet,
			int rowNum, int cellNum) throws Exception {
		Row row = sheet.getRow(rowNum);
		Cell cell = row.getCell(cellNum);
		return getCellData(cell);
	}

	/**
	 * 获取给定SHEET中指定单元格的值
	 * 
	 * @param sheet
	 *            指定SHEET
	 * @param cellName
	 *            格式为：A1,B3
	 * @return
	 */
	public static String getSheetCellValueWithCellName(Sheet sheet,
			String cellName) {
		CellReference cellReference = new CellReference(cellName);
		Row row = sheet.getRow(cellReference.getRow());
		Cell cell = row.getCell(cellReference.getCol());
		return getCellData(cell);
	}

	/**
	 * 获得cell单元格的TypeNumber,范围是0~5
	 * 
	 * @param cell
	 * @return
	 */
	public static int getColumnTypeNumber(Cell cell) {
		if (cell != null) {
			int type = cell.getCellType();
			return type;
		}
		return -1;
	}

	/**
	 * 获取指定Sheet页 所有合并单元格数据信息
	 * 
	 * @param sheet
	 * @return List<Map<String, String>>
	 */
	public static List<Map<String, String>> getSheetRegion(Sheet sheet) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		// 合并的单元格数量
		int merged = sheet.getNumMergedRegions();
		// 预读合并的单元格
		for (int i = 0; i < merged; i++) {
			CellRangeAddress range = sheet.getMergedRegion(i);
			Map<String, String> map = new LinkedHashMap<String, String>();
			int colstart = range.getFirstColumn();
			int colend = range.getLastColumn();
			int rowstart = range.getFirstRow();
			int rowend = range.getLastRow();
			map.put("colstart", colstart + "");
			map.put("colend", colend + "");
			map.put("rowstart", rowstart + "");
			map.put("rowend", rowend + "");
			map.put("field", getCharByNum(colstart));
			map.put("colspan", (colend - colstart + 1) + "");
			map.put("rowspan", (rowend - rowstart + 1) + "");
			map.put("index", rowstart + "");
			list.add(map);
		}
		return list;
	}

	/**
	 * 获取sheet中指定column的列宽度,这里的宽度是近似宽度,不是很精确
	 * 
	 * @param sheet
	 * @param cloumI
	 * @return
	 */
	public static int getColumnWidth(Sheet sheet, int cloumI) {
		return new BigDecimal(sheet.getColumnWidth(cloumI) * 37 / 1200)
				.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
	}

	/**
	 * 获取sheet中指定column的列宽度集合,这里的宽度是近似宽度,不是很精确
	 * 
	 * @param sheet
	 * @return
	 */
	public static List<Integer> getColumnWidths(Sheet sheet) {
		List<Integer> columnWidths = new ArrayList<Integer>();
		int lastCellNum = getColumnNum(sheet);
		for (int i = 0; i < lastCellNum; i++) {
			columnWidths
					.add(new BigDecimal(sheet.getColumnWidth(i) * 37 / 1200)
							.setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
		}
		return columnWidths;
	}

	/**
	 * 获取一个Sheet的冻结信息,包括冻结列和冻结行
	 * 
	 * @param sheet
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Short> getSheetFrazenColAndRow(Sheet sheet)
			throws Exception {
		Map<String, Short> frazenMap = new HashMap<String, Short>();
		PaneInformation paneInformation = sheet.getPaneInformation();
		if (paneInformation != null) {
			// 有多少列是冻结的
			frazenMap.put("freezeCol",
					paneInformation.getVerticalSplitLeftColumn());
			// 有多少行是冻结
			frazenMap.put("freezeRow",
					paneInformation.getHorizontalSplitTopRow());
		}
		return frazenMap;
	}

	/**
	 * 获取单元中值(字符串类型)
	 * 
	 * @param cell
	 * @return
	 */
	public static String getCellData(Cell cell) {
		String cellValue = "";
		if (cell != null) {
			try {
				switch (cell.getCellType()) {
				case Cell.CELL_TYPE_BLANK:// 空白
					cellValue = "";
					break;
				case Cell.CELL_TYPE_NUMERIC: // 数值型 0----日期类型也是数值型的一种
					if (DateUtil.isCellDateFormatted(cell)) {
						short format = cell.getCellStyle().getDataFormat();

						if (yyyyMMddList.contains(format)) {
							sFormat = new SimpleDateFormat("yyyy-MM-dd");
						} else if (hhMMssList.contains(format)) {
							sFormat = new SimpleDateFormat("HH:mm:ss");
						}
						Date date = cell.getDateCellValue();
						cellValue = sFormat.format(date);
					} else {
						Double numberDate = new BigDecimal(
								cell.getNumericCellValue()).setScale(4,
								BigDecimal.ROUND_HALF_UP).doubleValue();
						cellValue = numberDate + "";
					}
					break;
				case Cell.CELL_TYPE_STRING: // 字符串型 1
					cellValue = replaceBlank(cell.getStringCellValue());
					break;
				case Cell.CELL_TYPE_FORMULA: // 公式型 2
					cell.setCellType(Cell.CELL_TYPE_STRING);
					cellValue = replaceBlank(cell.getStringCellValue());
					break;
				case Cell.CELL_TYPE_BOOLEAN: // 布尔型 4
					cellValue = String.valueOf(cell.getBooleanCellValue());
					break;
				case Cell.CELL_TYPE_ERROR: // 错误 5
					cellValue = "!#REF!";
					break;
				}
			} catch (Exception e) {
				System.out.println("读取Excel单元格数据出错：" + e.getMessage());
				return cellValue;
			}
		}
		return cellValue;
	}

	public static String replaceBlank(String source) {
		String dest = "";
		if (source != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(source);
			dest = m.replaceAll("");
		}
		return dest;
	}

	/**
	 * 给SHEET某一个单元格赋值
	 * 
	 * @param sheet
	 *            指定单元格
	 * @param rowNum
	 *            行号
	 * @param cellNum
	 *            列号
	 * @param value
	 *            值
	 */
	public static void setCellValue(Sheet sheet, int rowNum, int cellNum,
			String value) {
		Row row = sheet.getRow(rowNum);
		Cell cell = row.getCell(cellNum);
		if (cell == null) {
			row.createCell(cellNum).setCellValue(value);
		} else {
			cell.setCellValue(value);
		}
	}

	public static void mergedRegion(Sheet sheet) throws Exception {
		// 合并的单元格数量
		int merged = sheet.getNumMergedRegions();
		// 预读合并的单元格
		for (int i = 0; i < merged; i++) {
			CellRangeAddress range = sheet.getMergedRegion(i);
			int y0 = range.getFirstRow();
			int x0 = range.getFirstColumn();
			int y1 = range.getLastRow();
			int x1 = range.getLastColumn();

			String value = getSheetCellValueWithRowIndexAndColIndex(sheet, y0,
					x0);

			for (int m = y0; m <= y1; m++) {
				for (int n = x0; n <= x1; n++) {
					setCellValue(sheet, m, n, value);
				}
			}
		}
	}

	/**
	 * 生成表头名称,A,B,C,D...
	 * 
	 * @param number
	 * @return
	 */
	public static String getCharByNum(int number) {
		int index = number / 26 - 1;
		if (index < 0) {
			return (char) (65 + number % 26) + "";
		} else if (index >= 0) {
			return (char) (65 + index) + "" + (char) (65 + number % 26) + "";
		}
		return "@";
	}

	/**
	 * 补全String字符串,
	 * 
	 * @param str
	 *            字符窜
	 * @param len
	 *            长度
	 * @param pre
	 *            补全字符
	 * @return 补全之后的字符串
	 */
	public static String preFillString(String str, int len, char pre) {
		int length = len - str.length();
		for (int i = 0; i < length; i++) {
			str = pre + str;
		}
		return str;
	}

	/**
	 * 获取颜色的HTML表示方式,
	 * 
	 * @param str
	 *            getHexString()
	 * @return
	 */
	public static String getColorByHex(String str) {
		String[] hexString = str.split(":");
		String colorRGB = "";
		for (int i = 0; i < hexString.length; i++) {
			hexString[i] = preFillString(hexString[i], 4, '0');
			colorRGB += hexString[i].substring(0, 2);
		}
		if ("000000".equals(colorRGB)) {
			colorRGB = "";
		}
		return colorRGB;
	}

	/**
	 * 获取颜色
	 * 
	 * @param shortColor
	 * @return
	 */
	public static String getColorByShortColor(short shortColor) {
		String returnColor = "";
		for (IndexedColors color : IndexedColors.values()) {
			if (shortColor == color.getIndex()) {
				returnColor = color.toString();
			}
		}
		if ("AUTOMATIC".equals(returnColor)) {
			returnColor = "";
		}
		return returnColor;
	}

	/**
	 * 获取Sheet中所有单元格样式合集
	 * 
	 * @param sheet
	 * @return
	 * @throws Exception
	 */
	public static List<Map<String, Object>> getSheetCellStyleMaps(Sheet sheet)
			throws Exception {
		List<Map<String, Object>> sheetCellStyles = new ArrayList<Map<String, Object>>();
		int lastRowNum = getRowNum(sheet);
		Row row;
		for (int i = 0; i < lastRowNum; i++) {
			row = sheet.getRow(i);
			if (row == null) {
				continue;
			}
			int columnNumMax = getColumnNum(sheet);
			for (int j = 0; j < columnNumMax; j++) {
				Cell cell = row.getCell(j);
				if (cell == null) {
					continue;
				}
				Map<String, Object> cellMap = getCellStyleMap(sheet, cell);
				cellMap.put("y", i);
				cellMap.put("x", j);
				sheetCellStyles.add(cellMap);
			}
		}
		return sheetCellStyles;
	}

	/**
	 * 获取Sheet中,某一个Cell的样式,Cell的背景颜色单独去取,借助于HSSFSheet和XSSFSheet
	 * 
	 * @param sheet
	 * @param cell
	 * @return
	 */
	public static Map<String, Object> getCellStyleMap(Sheet sheet, Cell cell) {
		Map<String, Object> cellStyleMap = new HashMap<String, Object>();

		Short alignShort = cell.getCellStyle().getAlignment();
		String alignment = "c";
		if (alignShort == 1) {
			alignment = "l";
		} else if (alignShort == 3) {
			alignment = "r";
		}

		CellStyle cellStyle = cell.getCellStyle();
		Workbook workbook = sheet.getWorkbook();
		Font font = workbook.getFontAt(cellStyle.getFontIndex());
		cellStyleMap.put("fontColor", getColorByShortColor(font.getColor()));
		cellStyleMap.put("fontBold", font.getBoldweight());
		cellStyleMap.put("fontSize", font.getFontHeightInPoints());
		cellStyleMap.put("alignment", alignment);
		try {
			HSSFCellStyle hSSFCellStyle = (HSSFCellStyle) cell.getCellStyle();
			cellStyleMap.put("cellColor", getColorByHex(hSSFCellStyle
					.getFillForegroundColorColor().getHexString()));
		} catch (Exception e) {
			XSSFCellStyle xSSFCellStyle = (XSSFCellStyle) cell.getCellStyle();
			String xssfCellColor = "";
			if (xSSFCellStyle.getFillBackgroundColorColor() != null) {
				xssfCellColor = xSSFCellStyle.getFillForegroundColorColor()
						.getARGBHex().substring(2);
			}
			xssfCellColor = "000000".equals(xssfCellColor) ? "" : xssfCellColor;
			cellStyleMap.put("cellColor", xssfCellColor);
		}
		return cellStyleMap;
	}

	public static void main(String[] args) throws Exception {
		Workbook workbook = getExcelWorkbook("D:/test.xls");
		Sheet sheet = getSheetByNum(workbook, 1);
		List<Map<String, Object>> colAndDataList = getSheetDataMapAndId(sheet);
		List<Object> columns = new ArrayList<Object>();
		List<Object> datas = new ArrayList<Object>();
		for (int i = 0; i < colAndDataList.size(); i++) {
			Map<String, Object> hashMap = colAndDataList.get(i);
			String idString = String.valueOf(hashMap.get("id"));
			if ("0".equals(idString)) {
				String dataString = hashMap.get("data").toString();
				dataString = dataString.substring(1, dataString.length() - 1);
				String[] data = dataString.split(",");
				for (String val : data) {
					Map<String, Object> column = new HashMap<String, Object>();
					if (val.equals("")) {
						val = " ";
					}
					column.put("field", val);
					column.put("title", val);
					columns.add(column);
				}
			} else {
				String dataString = hashMap.get("data").toString();
				dataString = dataString.substring(1, dataString.length() - 1);
				String[] data = dataString.split(",");
				Map<String, Object> temp = new HashMap<String, Object>();
				for (int j = 0; j < data.length; j++) {
					String val = data[j];
					if (val.equals("")) {
						val = "0";
					}
					Map<String, Object> colString = (Map<String, Object>) columns.get(j);
					temp.put((String) colString.get("title"), val);
				}
				datas.add(temp);
			}
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("datas", datas);
		result.put("columns", columns);
		result.put("success", true);
		System.out.println(result);
	}
}
