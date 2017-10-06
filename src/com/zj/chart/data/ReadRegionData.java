package com.zj.chart.data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ReadRegionData {

	private String[] regonCode;
	private String[] regonX;
	private String[] regonY;
	private String rowNum;

	public String getRowNum() {
		return rowNum;
	}

	public void setRowNum(String rowNum) {
		this.rowNum = rowNum;
	}

	public  ReadRegionData(String chartData) {
		/*String sql = "SELECT REGION_CODE,REGION_X,REGION_Y FROM REGION ";
		String sql1 = "SELECT COUNT(REGION_CODE) FROM REGION";
//		ConnOrcl connOrcl = new ConnOrcl();
//		Statement statement = connOrcl.getStmt();
		
		JConnection jConnection = new JConnection();
		Connection connection = jConnection.getConnection();
		PreparedStatement pst = null;
		PreparedStatement pst2 =null;
		ResultSet resultSet = null;

		try {
			pst = connection.prepareStatement(sql1);
			resultSet = pst.executeQuery();
			while (resultSet.next()) {
				rowNum = resultSet.getString(1);
			}
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		int row = Integer.parseInt(rowNum);	
		regonCode = new String[row];
		regonX = new String[row];
		regonY = new String[row];
		int i =0;
		try {
			pst2 = connection.prepareStatement(sql);
			resultSet = pst2.executeQuery();
			while (resultSet.next()) {
				regonCode[i] = resultSet.getString(1);
				regonX[i] = resultSet.getString(2);
				regonY[i] = resultSet.getString(3);
				i++;
			}
			pst2.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		jConnection.close();*/
		
		String[] temp=chartData.split(";");
		int zhibiaoNum=temp[0].split(",").length;
		int regonNum=(temp.length-2);
		rowNum=regonNum+"";
		regonCode=new String[regonNum];
		regonX=new String[regonNum];
		regonY=new String[regonNum];
		for(int i=0;i<(temp.length-2);i++)
		{
			regonCode[i]=temp[i+2].split(",")[zhibiaoNum];
			regonX[i] = temp[i+2].split(",")[zhibiaoNum+1];
			regonY[i] =temp[i+2].split(",")[zhibiaoNum+2];
		}
	}

	public String[] getRegonCode() {
		return regonCode;
	}

	public void setRegonCode(String[] regonCode) {
		this.regonCode = regonCode;
	}
	public String[] getRegonX() {
		return regonX;
	}
	public void setRegonX(String[] regonX) {
		this.regonX = regonX;
	}
	public String[] getRegonY() {
		return regonY;
	}
	public void setRegonY(String[] regonY) {
		this.regonY = regonY;
	}

	//test
	public static void main(String[] args){
		/*ReadRegionData rrd = new ReadRegionData();
		String[] regioncodeStrings = rrd.getRegonCode();
		System.out.println(regioncodeStrings[2]);*/
		
	}
}
