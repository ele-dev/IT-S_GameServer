package serverPackage;

import java.sql.*;

public class DatabaseAccess {

	// Class members
	private Connection dbCon = null;
	
	// Default Database connection configuration
	private String dbUrl = "jdbc:mariadb://localhost:3306/onlineGameDB";
	private String dbUser = "java-data";
	private String dbPassword = "";
	
	// Prepared statements 
	PreparedStatement testQuery = null;
	
	
	// Constructor
	public DatabaseAccess()
	{
		// First obtain the global configuration values
		dbUser = GameConfigs.dbUser;
		dbPassword = GameConfigs.dbPassword;
		dbUrl = "jdbc:mariadb://" + GameConfigs.dbHost + ":3306/onlineGameDB";
		
		// Now try to open the connection
		try {
			this.dbCon = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword);
		} catch(SQLException e) {
			Main.logger.printError("Failed to get connection to database!", true, 0);
			this.dbCon = null;
			return;
		}
		
		// If connected then try to precompile the prepared statements
		try {
			this.precompileStatements();
		} catch (SQLException e) {
			Main.logger.printError("Failed to compile prepared statements", true, 0);
			this.dbCon = null;
			return;
		}
	}
	
	// Finalizer 
	@Override
	public void finalize() {
		
		// Close the local database connection and everything related to it (statements, resultSets, etc)
		try {
			if(this.testQuery != null) {
				this.testQuery.close();
			}
			
			if(this.dbCon != null) {
				dbCon.close();
			}
		} catch (SQLException e) {
			Main.logger.printWarning("Problems during database connection cleanup!", true, 0);
		}
	}
	
	// High level function for specific tasks
	public boolean login(String username, String passwordHash) throws SQLException{
		
		// Construct SQL query and statement for user login validation
		String sqlQuery = "SELECT * FROM tbl_userAccounts WHERE playername LIKE '" + username + "' AND"
				+ " password_hash LIKE '" + passwordHash + "'";
		Statement loginStatement = this.dbCon.createStatement();
		
		// Execute the query and store the result
		ResultSet loginResult = loginStatement.executeQuery(sqlQuery);
		if(loginResult.next() == false) {
			return false;
		}
		
		return true;
	}
	
	// .... // 
	
	// Helper functions 
	public boolean testConnection() {
		if(this.dbCon == null) {
			return false;
		}
		
		// Execute the test query to see if the connection is working properly
		try {
			this.testQuery.execute();
		} catch (SQLException e) {
			return false;
		}
		
		return true;
	}
	
	private void precompileStatements() throws SQLException {
		// test statement
		String queryStr = "SELECT * FROM tbl_userAccounts";
		this.testQuery = this.dbCon.prepareStatement(queryStr);
		
		// ... 
	}
}
