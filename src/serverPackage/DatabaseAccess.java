package serverPackage;

import java.sql.*;

public class DatabaseAccess {

	// Class members
	private static Connection dbCon = null;
	
	// Login configuration
	private static final String dbUrl = "jdbc:mariadb://localhost:3306/onlineGameDB";
	private static final String dbUser = "java-data";
	private static final String dbPassword = "";
	
	// Constructor
	public DatabaseAccess()
	{
		
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
