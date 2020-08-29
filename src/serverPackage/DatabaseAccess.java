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
	PreparedStatement pst_VerifyLogin = null;
	PreparedStatement pst_SetOnlineStatus = null;
	PreparedStatement pst_AddGuestPlayer = null;
	PreparedStatement pst_RemoveGuestPlayer = null;
	PreparedStatement pst_GetPlayerAttribute = null;
	
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
			return;
		}
		
		// Ouput status message about clean close up
		Main.logger.printInfo("Database Module unloaded", true, 0);
	}
	
	// High level functions for frequently used operations // 

	// Function for handling the login of a registered player 
	public boolean loginPlayer(String username, String passwordHash) throws SQLException {
		
		// Execute the prepared statement and store the result
		this.pst_VerifyLogin.setString(1, username);
		this.pst_VerifyLogin.setString(2, passwordHash);
		ResultSet loginResult = this.pst_VerifyLogin.executeQuery();
		if(!loginResult.next()) {
			return false;
		}
		
		// if the login was successfull then mark the player as online
		this.pst_SetOnlineStatus.setString(1, "online");
		this.pst_SetOnlineStatus.setString(2, username);
		this.pst_SetOnlineStatus.executeUpdate();
		
		return true;
	}
	
	// Function for handling the login of an unregistered guest player
	public void loginGuest(String guestName) throws SQLException {
		
		// Insert the new guest player data into the table using prepared statement
		this.pst_AddGuestPlayer.setString(1, guestName);
		this.pst_AddGuestPlayer.executeUpdate();
	}
	
	public void logoutPlayer(String username) throws SQLException {
		// Execute a prepared statement to update the online status of the player
		this.pst_SetOnlineStatus.setString(1, "offline");
		this.pst_SetOnlineStatus.setString(2, username);
		this.pst_SetOnlineStatus.executeUpdate();
	}
	
	public void logoutGuest(String guestName) throws SQLException {
		// Execute a prepared statement to delete the guests entry in the table
		this.pst_RemoveGuestPlayer.setString(1, guestName);
		this.pst_RemoveGuestPlayer.executeUpdate();
	}
	
	// Functions for getting an attribute from a player with the give name
	public String getPlayerAttributeStr(String attr, String playername) throws SQLException {
		String value = "";
		this.pst_GetPlayerAttribute.setString(1, playername);
		ResultSet result = this.pst_GetPlayerAttribute.executeQuery();
		if(!result.next()) {
			return null;
		} 
		value = result.getString(attr);
		
		return value;
	}
	
	public int getPlayerAttributeInt(String attr, String playername) throws SQLException {
		int value = 0;
		this.pst_GetPlayerAttribute.setString(1, playername);
		ResultSet result = this.pst_GetPlayerAttribute.executeQuery();
		
		while(result.next()) {
			value = result.getInt(attr);
		}
		
		return value;
	}
	
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
		
		// statement to select all matches to the given login credentials 
		queryStr = "SELECT * FROM tbl_userAccounts WHERE playername LIKE ? AND password_hash LIKE ?";
		this.pst_VerifyLogin = this.dbCon.prepareStatement(queryStr);
		
		// statement to update a players online state
		queryStr = "UPDATE tbl_userAccounts SET status = ? WHERE playername LIKE ?";
		this.pst_SetOnlineStatus = this.dbCon.prepareStatement(queryStr);
		
		// statement for inserting new guest players into the user account table
		queryStr = "INSERT INTO tbl_userAccounts (playername, status) VALUES (?, 'online')";
		this.pst_AddGuestPlayer = this.dbCon.prepareStatement(queryStr);
		
		// statement for deleting a guest player who wants to logout from the user account table
		queryStr = "DELETE FROM tbl_userAccounts WHERE playername LIKE ?";
		this.pst_RemoveGuestPlayer = this.dbCon.prepareStatement(queryStr);
		
		// statement for selecting an attribute of a player given by playername
		queryStr = "SELECT * FROM tbl_userAccounts WHERE playername LIKE ?";
		this.pst_GetPlayerAttribute = this.dbCon.prepareStatement(queryStr);
		
		Main.logger.printInfo("All prepared SQL statements are compiled and ready", true, 1);
	}
}
