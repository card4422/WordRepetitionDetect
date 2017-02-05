package DBAccess;
import java.sql.*;
import java.util.Vector;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;

public class DBAccess {
    /*
	private String drv = "com.mysql.jdbc.Driver";
	private String url = "jdbc:mysql://localhost:3306/zhuzheng?useUnicode=true&characterEncoding=utf-8&useSSL=false";
	private String usr = "root";
	private String pwd = "123456";*/
	private Connection conn = null;
	private Statement stm = null;
	private ResultSet rs = null;


	private static DataSource ds;//定义一个连接池对象
	static{
		try {
			Properties pro = new Properties();
			pro.load(DBAccess.class.getClassLoader().getResourceAsStream("dbcpconfig.properties"));
			ds = BasicDataSourceFactory.createDataSource(pro);//得到一个连接池对象
		} catch (Exception e) {
			throw new ExceptionInInitializerError("初始化连接错误，请检查配置文件！");
		}
	}
	//从池中获取一个连接
	public static Connection getConnection() throws SQLException{
		return ds.getConnection();
	}

	public boolean createConn(){
		boolean b = false;
		try{
			conn = getConnection();
			b = true;
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		return b;
	}
	
	public boolean update(String sql){
		boolean b = false;
		try{
			stm = conn.createStatement();
			stm.execute(sql);
			b = true;
		}catch(Exception e){
            e.printStackTrace();
        }
		return b;
	}
	
	public void query(String sql){
		try{
			stm = conn.createStatement();
			rs = stm.executeQuery(sql);
		}catch(Exception e){
            e.printStackTrace();
        }
	}
	
	public boolean next(){
		boolean b = false;
		try{
			if(rs.next())
				b = true;
		}catch(Exception e){
            e.printStackTrace();
        }
		return b;
	}
	
	
	public String getValue(String field){
		String value = null;
		try{
			if(rs!=null)
				value = rs.getString(field);
		}catch(Exception e){
            e.printStackTrace();
        }
		return value;
	}

	public void closeRs(){
		try{
			if(rs!=null)
				rs.close();
		}catch(SQLException e){
            e.printStackTrace();
        }
	}

	public void closeStm(){
		try{
			if(stm!=null)
				stm.close();
		}catch(SQLException e){
            e.printStackTrace();
        }
	}

	public void closeConn(){
		try{
			if(conn!=null)
				conn.close();
		}catch(SQLException e){
            e.printStackTrace();
        }
	}
	
}
