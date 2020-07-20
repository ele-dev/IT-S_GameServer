package serverPackage;

import java.sql.*;

public class DatabaseAccess {

	// Class members
	private static Connection dbCon = null;
	
	// Default Database connection configuration
	private static String dbUrl = "jdbc:mariadb://localhost:3306/onlineGameDB";
	private static String dbUser = "java-data";
	private static String dbPassword = "";
	
	// Constructor
	public DatabaseAccess()
	{
		// First obtain the global configuration values
		dbUser = GameConfigs.dbUser;
		dbPassword = GameConfigs.dbPassword;
		dbUrl = "jdbc:mariadb://" + GameConfigs.dbHost + ":3306/onlineGameDB";
	}
	
	// High level function for specific tasks
	// ....
	
	// Helper functions 
	private Connection getConnection() {
		// ...
		
		return dbCon;
	}
	
	public void closeConnection() {
		try {
			dbCon.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
