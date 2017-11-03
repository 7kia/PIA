package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class DbConnection {
	public static final String DRIVER_CLASS = "com.nuodb.jdbc.Driver";
	
	public static Connection getConnection(String url, Properties info)
		throws ClassNotFoundException
	{
		Connection connection = null;
		try {
			Class.forName(DRIVER_CLASS);
			return DriverManager.getConnection(url, info);
			
		} catch (ClassNotFoundException | SQLException exception) {
			exception.printStackTrace();
		}
		return connection;
	}
}
