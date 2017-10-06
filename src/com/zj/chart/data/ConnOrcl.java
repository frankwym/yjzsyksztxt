package com.zj.chart.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import com.zj.util.JUtil;
/**
 * 使用Access数据库连接
 * @author 501
 *
 */
public class ConnOrcl {
	private Connection conn = null;
	private Statement stmt = null;

	public ConnOrcl(){
		String path = JUtil.GetWebInfPath() + "table/XIAN.mdb";
		path = path.substring(1); 
//		String url = "jdbc:odbc:driver={Microsoft Access Driver (*.mdb)};DBQ=" + path;
		
		try {
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
			conn=DriverManager.getConnection("jdbc:ucanaccess://"+path);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		try{
////			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
//			try {
//				conn = DriverManager.getConnection(url);
//			} 
//			catch (SQLException e) {
//				url = "jdbc:odbc:driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ=" + path;
//				try {
//					conn = DriverManager.getConnection(url);
//				} catch (SQLException e1) {
//					e1.printStackTrace();
//				}
//			}
//			stmt = conn.createStatement();
//		}
//		catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}

	}

	public Statement getStmt() {
		if (stmt==null) {
			try {
				return conn.createStatement();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return stmt;
	}

	public void close() {
		try {
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public PreparedStatement getPreparedStatement(String sql) throws SQLException {
		return this.conn.prepareStatement(sql);
	}
	
	public Connection getConnection() {
		
		return conn;
		
	}
	
	public static void main(String[] args)
	{
		ConnOrcl co = new ConnOrcl();
		Statement stmt = co.getStmt();
		
//		String sql = "SELECT Count(*) AS RTab FROM MSysObjects WHERE MSysObjects.Name like 'TZ00000001'";
//		ResultSet rs = 
		try {
			stmt.executeQuery("SELECT * FROM regionanalysis");
			System.out.println("exist");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("not exist");
		}
//		while(rs.next())
//			System.out.println(rs.getInt(1));
	}
}
