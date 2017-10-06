package com.zj.chart.data;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;
import com.zj.util.JUtil;


public class ReadThematicData {


	private String[] regonData;//地区编码
	private String[] data;//制图数据
	private String[] yearData;//年份
	private double max;//最大值
	private double min;//最小值
	private String regonName;
	private String dataName="";
	private String rowNum;
	private HashMap<String, String> dataMap;
	private double[] maxValues;


	private double[] minValues;
	
	public ReadThematicData(String chartData){
		
		String[] temp = chartData.split(";");//数据库 表的名称 以及要查询的指标的名称
		//String table = temp[0];//表的名称
		String fieldName =temp[0];//获取指标名称 
		/*for (int i = 1; i < temp.length; i++) {
			String cnnameAndUnit = null;
			cnnameAndUnit = JUtil.getCnNameAndUnit(temp[i]);
			String cnname = cnnameAndUnit.split(",")[0];
			//fieldName+=temp[i]+",";
			dataName +=cnname + ",";
		}*/
		dataName=fieldName;
		/*fieldName = fieldName.substring(0,fieldName.length()-1);//英文名
		dataName = dataName.substring(0,dataName.length()-1);//中文名
		String selectString = "REGION_NAME,REGION_CODE,YEAR," + fieldName;
	
		String sqlString = "SELECT " ;
		String sqlString1 = " FROM ";
		String sql = sqlString + selectString + sqlString1 +table;
		String sql1 = "SELECT COUNT(*) FROM "+table;	
		
//		ConnOrcl connOrcl = new ConnOrcl();
//		Statement stam = connOrcl.getStmt();
		JConnection jConnection = new JConnection();
		Connection connection = jConnection.getConnection();
		PreparedStatement pst = null;
		ResultSet resultSet = null;
		try {
			pst = connection.prepareStatement(sql1);
			resultSet = pst.executeQuery();
			while (resultSet.next()) {
				rowNum = resultSet.getString(1);
			System.out.println(rowNum);
			}
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
		String[] temp2=chartData.split(";");
		int zhibiaoNum=temp2[0].split(",").length;
		int row = temp2.length-2;
		rowNum=row+"";
		regonData = new String[row];
		data = new String[row];
		yearData = new String[1];
		yearData[0]="2016";
		dataMap = new HashMap<String, String>();
		regonName = "";
		
		for(int i=0;i<(temp2.length-2);i++)
		{
			regonData[i]=temp2[i+2].split(",")[zhibiaoNum];
			data[i]="";
			for(int j=0;j<zhibiaoNum;j++)
			{
				if(j==0)
				{
					data[i]=temp2[i+2].split(",")[j];
				}
				else
				{
					data[i]=data[i]+","+temp2[i+2].split(",")[j];
				}
				
			}
			/*data[i]=temp2[i+2].split(",")[0]+","+temp2[i+2].split(",")[1]+","+temp2[i+2].split(",")[2];*/
			dataMap.put(regonData[i], data[i]);
			if(i==0)
			{
				regonName=temp2[i+2].split(",")[zhibiaoNum+3];
			}
			else
			{
				regonName=","+regonName;
			}
			
			
		}
		
		//PreparedStatement pst2 = null;
/*
		int i = 0;
		try {
			pst2 = connection.prepareStatement(sql);
			resultSet = pst2.executeQuery();
			while (resultSet.next()) {
				regonName += resultSet.getString(1) + ",";
				regonData[i] = resultSet.getString(2);
				yearData[i] = resultSet.getString(3);
								
				String tempString = "";
				for (int j = 0; j < temp.length-1; j++) {	
					String tempData = resultSet.getString(j+4);
					if (tempData.length()==0) {
						tempData = "0";
						tempString+=tempData+",";
					}else {
						tempString+=tempData+",";
					}
//					tempString +=resultSet.getString(j+4)+",";
				}
				data[i] = tempString.substring(0, tempString.length()-1);//去掉末尾逗号
				
				dataMap.put(regonData[i], data[i]);
				i++;
			}*/
			max = 0;
			for (int k = 0; k < data.length; k++) {
				String[] tempStrings = data[k].split(",");
				for (int j = 0; j < tempStrings.length; j++) {
					double tempint = Double.parseDouble(tempStrings[j]);
					if (tempint > max) {
						max = tempint;
					}
				}
			}
			maxValues = new double[2];
			maxValues[0] = max;
			maxValues[1] = max;
			min = 0;
			min = Double.parseDouble(data[0].split(",")[0]);
			for (int k = 0; k < data.length; k++) {
				String[] tempStrings = data[k].split(",");
				for (int j = 0; j < tempStrings.length; j++) {
					double tempint = Double.parseDouble(tempStrings[j]);
					if (tempint < min) {
						min = tempint;
					}
				}
			}
			minValues = new double[2];
			minValues[0] = min;
			minValues[1] = min;
			
			/*pst2.close();
		} catch (Exception e) {
			System.out.println(i);
			e.printStackTrace();
		}
		
		jConnection.close();*/
	}
	public String[] getRegonData() {
		return regonData;
	}

	public void setRegonData(String[] regonData) {
		this.regonData = regonData;
	}

	public String[] getData() {
		return data;
	}

	public void setData(String[] data) {
		this.data = data;
	}

	public String[] getYearData() {
		return yearData;
	}

	
	public static String[] getUniqYears(String thematicData){
		
		ReadThematicData rtData = new ReadThematicData(thematicData);
	     TreeSet<String> treeSet = new TreeSet<String>();
	     String[] yeardatas = rtData.getYearData();
	     for (int i = 0; i < yeardatas.length; i++) {
			treeSet.add(yeardatas[i]);
		}
	     String[] yearStrings = new String[treeSet.size()];
	       for (int j = 0; j < yearStrings.length; j++) {
			yearStrings[j] = treeSet.pollFirst();
		} 
	       System.out.println(yearStrings[0]);
	       return yearStrings;
	      
	}
	public void setYearData(String[] yearData) {
		this.yearData = yearData;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public String getRegonName() {
		return regonName;
	}

	public void setRegonName(String regonName) {
		this.regonName = regonName;
	}

	public String getDataName() {
		return dataName;
	}

	public void setDataName(String dataName) {
		this.dataName = dataName;
	}

	public HashMap<String, String> getDataMap() {
		return dataMap;
	}

	public void setDataMap(HashMap<String, String> dataMap) {
		this.dataMap = dataMap;
	}
	public double[] getMaxValues() {
		return maxValues;
	}
	public void setMaxValues(double[] maxValues) {
		this.maxValues = maxValues;
	}
	public double[] getMinValues() {
		return minValues;
	}
	public void setMinValues(double[] minValues) {
		this.minValues = minValues;
	}
	
	public static void main(String[] args){
		/*String thematicData = "dz,RK_HSZ,RK_SW,RK_SZ,RK_ZY";
	
		String[] years = getUniqYears(thematicData);
		for (int i = 0; i < years.length; i++) {
			System.out.println(years[i]);
		}
		*/
	}
}
