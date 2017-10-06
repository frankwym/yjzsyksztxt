package com.zj.chart.chartstyle;

import com.zj.util.JUtil;

/**
 * 制图数据参数类
 * @author QTQ
 *
 */
public class ChartDataPara {
	/**
	 * 自变量数组  eg:2011，2012
	 */
	private String[] domainAxis;

	/**
	 * 自变量单位  eg：年
	 */
	private String domainAxisUnit;
	
	/**
	 * 指标对应的颜色数组  
	 */
	private int[] fieldColor;
	/**
	 * 指标名称数组
	 */
	private String[] fieldName;
	/**
	 * 组合符号分组
	 */
	//private byte[] fieldGroup;
	/**
	 * 指标单位
	 */
	private String[] fieldUnits;

	/**
	 * 单个区域绘图宽度
	 */
	private int width;
	/**
	 * 单个区域绘图高度
	 */
	private int height;
	/**
	 * 一个像素表示多大的值
	 */
	private double[] scales;
	
	/**
	 * 默认绘制符号的ID
	 */
	public void initial(String chartData,String yearString){
		String[] temp = chartData.split(";");
		/*String popNum=temp[0];
		String totalPro=temp[1];
		String GDP=temp[2];*/
		int zhibiaoNum=temp[0].split(",").length;
		String[] zhib=new String[zhibiaoNum];
		String[] danw=new String[zhibiaoNum];
		for(int i=0;i<zhibiaoNum;i++)
		{
			zhib[i]=temp[0].split(",")[i];
			danw[i]=temp[1].split(",")[i];
		}
		
		this.setFieldName(zhib);
		this.setFieldUnits(danw);

		
 //		String table = temp[0];//表名
		/*String[] fn = new String[temp.length-1];
		String[] fu = new String[temp.length-1];
		for (int i = 0; i < temp.length-1; i++) {
			String cnNameAndUnit = null;
			cnNameAndUnit = JUtil.getCnNameAndUnit(temp[i+1]);
			fn[i] = cnNameAndUnit.split(",")[0];  
			fu[i] = cnNameAndUnit.split(",")[1];
		}*/
		String[] years = new String[1];
		years[0]=yearString;
		/*this.setFieldName(fn); //指标名
		this.setFieldUnits(fu);//单位
*/		this.setDomainAxis(years);
		this.setDomainAxisUnit("年");
	}
	
	
	public String[] getDomainAxis() {
		return domainAxis;
	}

	public void setDomainAxis(String[] domainAxis) {
		this.domainAxis = domainAxis;
	}

	public String getDomainAxisUnit() {
		return domainAxisUnit;
	}

	public void setDomainAxisUnit(String domainAxisUnit) {
		this.domainAxisUnit = domainAxisUnit;
	}

	public int[] getFieldColor() {
		return fieldColor;
	}

	public void setFieldColor(int[] fieldColor) {
		this.fieldColor = fieldColor;
	}

	public String[] getFieldName() {
		return fieldName;
	}

	public void setFieldName(String[] fieldName) {
		this.fieldName = fieldName;
	}

//	public byte[] getFieldGroup() {
//		return fieldGroup;
//	}
//
//	public void setFieldGroup(byte[] fieldGroup) {
//		this.fieldGroup = fieldGroup;
//	}

	public String[] getFieldUnits() {
		return fieldUnits;
	}

	public void setFieldUnits(String[] fieldUnits) {
		this.fieldUnits = fieldUnits;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public double[] getScales() {
		return scales;
	}

	public void setScales(double[] scales) {
		this.scales = scales;
	}

}

