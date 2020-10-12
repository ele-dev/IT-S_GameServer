package serverPackage;

/*
 * written by Elias Geiger
 * 
 * This class is the interface to the mariaDB/MySQL Database backend
 * which is neccessary to provide persistency for important game data
 * 
 * The class attempts to connect to the database in the constructor and executes a test query 
 * to make sure the connection is working properly. The finalize method is responsible for the closeup
 * before the application itself closes. The Majority of the required SQL Queries for are used very
 * frequently with only slight changes in the paramaters. Thats why Prepared statements are used.
 * They can be prepared/precompiled during application initialization with a few placeholders for parameters passing
 * and later on they can be executed faster compared to the normal statements
 * 
 * This class contains a lot of functions that are designed for specific use cases e.g. login validation
 * This way the database/SQL source code stays widely in this class and the functions can be called easily from 
 * wherever they are needed
 * 
 */

import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

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
	PreparedStatement pst_CleanTable = null;
	
	// Account control statements
	PreparedStatement pst_AddAccount = null;
	PreparedStatement pst_AddAccountControl = null;
	PreparedStatement pst_GetVerificationKeys = null;
	PreparedStatement pst_PurgeOldAccounts = null;
	
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
		
		// Clean the table from remaining guest player entries that shouldn't be persistent
		try {
			this.pst_CleanTable.executeUpdate();
		} catch (SQLException e1) {
			Main.logger.printWarning("Failed to run database cleanup query!", false, 1);
		}
		
		// Close the local database connection and everything related to it (statements, resultSets, etc)
		try {
			if(this.testQuery != null) {
				this.testQuery.close();
			}
			
			if(this.pst_AddGuestPlayer != null) {
				this.pst_AddGuestPlayer.close();
			}
			
			if(this.pst_GetPlayerAttribute != null) {
				this.pst_GetPlayerAttribute.close();
			}
			
			if(this.pst_RemoveGuestPlayer != null) {
				this.pst_RemoveGuestPlayer.close();
			}
			
			if(this.pst_SetOnlineStatus != null) {
				this.pst_SetOnlineStatus.close();
			}
			
			if(this.pst_VerifyLogin != null) {
				this.pst_VerifyLogin.close();
			}
			
			if(this.pst_CleanTable != null) {
				this.pst_CleanTable.close();
			}
			
			if(this.pst_AddAccount != null) {
				this.pst_AddAccount.close();
			}
			
			if(this.pst_AddAccountControl != null) {
				this.pst_AddAccountControl.close();
			}
			
			if(this.pst_GetVerificationKeys != null) {
				this.pst_GetVerificationKeys.close();
			}
			
			if(this.pst_PurgeOldAccounts != null) {
				this.pst_PurgeOldAccounts.close();
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

	// Functions for handling the login/logout of guest normal registered players 
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
	
	public void registerNewAccount(String user, String email, String pwdHash, String verificationKey) throws SQLException {
		
		// First create the account entry in tbl_userAccounts
		this.pst_AddAccount.setString(1, user);
		this.pst_AddAccount.setString(2, email);
		this.pst_AddAccount.setString(3, pwdHash);
		this.pst_AddAccount.executeUpdate();
		
		// Second create the corresponding control entry in tbl_accountControl
		this.pst_AddAccountControl.setString(1, verificationKey);
		this.pst_AddAccountControl.setString(2, user);
		this.pst_AddAccountControl.executeUpdate();
	}
	
	// Functions for getting an attribute from a player with the given name
	public String getPlayerAttributeStr(String attr, String playername) throws SQLException {
		String value = "";
		this.pst_GetPlayerAttribute.setString(1, playername);
		ResultSet result = this.pst_GetPlayerAttribute.executeQuery();
		
		while(result.next()) {
			value = result.getString(attr);
		}
		
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
	
	// Method that checks if a given username is free for use or already taken
	public boolean isNameAvailable(String playername) throws SQLException {
		boolean isAvailable = true;
		
		// Run the query and store the result
		this.pst_GetPlayerAttribute.setString(1, playername);
		ResultSet result = this.pst_GetPlayerAttribute.executeQuery();
		
		// Now count the entries in the result set (1 or more means taken)
		if(result.next() == true) {
			isAvailable = false;
		}
		
		return isAvailable;
	}
	
	// Function that returns a new unique verification key for a newly registered account
	public String getUniqueKey() throws SQLException {
		String uniqueKey = "";
		ArrayList<String> reservedKeys = new ArrayList<String>();
		
		// Pull the reserved keys from the database and create a list
		ResultSet result = this.pst_GetVerificationKeys.executeQuery();
		while(result.next()) {
			reservedKeys.add(result.getString("activationKey"));
		}
		
		// Generate a new keys until a unique one comes up
		String allowedChars = "0123456789abcdefghijklmnopqrstuvwxyz";
		Random random = new Random();
		do {
			uniqueKey = "";
			StringBuffer buffer = new StringBuffer();
			for(int i = 0; i < 10; i++) 
			{
				int value = random.nextInt(allowedChars.length());
				buffer.append(allowedChars.charAt(value));
			}
			
			uniqueKey = buffer.toString();
		} while(reservedKeys.contains(uniqueKey));
		
		return uniqueKey;
	}
	
	// Function that purges the database tables from old unconfirmed accounts
	public void purgeOldAccounts(int daysOfLife) throws SQLException {
		
		// Set the maximum tolerated lifetime for these accounts in days
		this.pst_PurgeOldAccounts.setInt(1, daysOfLife);
		this.pst_PurgeOldAccounts.executeUpdate();
	}
	
	// Helper functions // 
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
	
	// Method precompiles all neccessary prepared statements for faster execution during runtime
	private void precompileStatements() throws SQLException {
		// test statement
		String queryStr = "SELECT * FROM tbl_userAccounts";
		this.testQuery = this.dbCon.prepareStatement(queryStr);
		
		// statement to select all matches to the given login credentials 
		queryStr = "SELECT * FROM tbl_userAccounts, tbl_accountControl "
				+ "WHERE tbl_userAccounts.playername = tbl_accountControl.playername "
				+ "AND tbl_userAccounts.playername LIKE ? AND password_hash LIKE ? AND activationStatus LIKE 'complete'";
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
		
		// statement for deleting remaining table entries of players that aren't connected
		queryStr = "DELETE FROM tbl_userAccounts WHERE playername LIKE 'guest%'";
		this.pst_CleanTable = this.dbCon.prepareStatement(queryStr);
		
		// statement for inserting new user account in the table 
		queryStr = "INSERT INTO tbl_userAccounts (playername, email, password_hash) VALUES (?, ?, ?)";
		this.pst_AddAccount = this.dbCon.prepareStatement(queryStr);
		
		// statement for inserting new account control entry into the control table
		queryStr = "INSERT INTO tbl_accountControl (activationKey, playername) VALUES (?, ?)";
		this.pst_AddAccountControl = this.dbCon.prepareStatement(queryStr);
		
		// statement for pulling a list of all reserved verification keys from the tbl_accountControl
		queryStr = "SELECT * FROM tbl_accountControl";
		this.pst_GetVerificationKeys = this.dbCon.prepareStatement(queryStr);
		
		// statement that deletes all unconfirmed accounts after a certain period of time from both tables (accountControl & userAccounts)
		queryStr = "DELETE tbl_accountControl, tbl_userAccounts FROM tbl_accountControl "
				+ "INNER JOIN tbl_userAccounts ON tbl_userAccounts.playername = tbl_accountControl.playername "
				+ "WHERE tbl_accountControl.activationStatus LIKE 'incomplete' "
				+ "AND tbl_accountControl.creationTime < now() - interval ? DAY";
		this.pst_PurgeOldAccounts = this.dbCon.prepareStatement(queryStr);
		
		Main.logger.printInfo("All prepared SQL statements are compiled and ready", true, 1);
	}
}
