package game;

import java.sql.SQLException;

import serverPackage.Main;

public class Player {
	
	// static class members //
	public static int nextGuestId = 0;
	
	// Class members 
	private String name;
	private int accountBalance;
	
	// Default constructor for creating empty Player
	public Player()
	{
		this.name = "";
		this.accountBalance = 0;
	}
	
	// Additional constructor for creating player with name
	public Player(String name) 
	{
		this();
		this.name = name;
	}
	
	// Method that simplyfies player logout no matter if guest or registered
	public void logout() 
	{
		// Get the playername of the sender of this message
		boolean isGuest = this.name.contains("guest");
		
		// Handle the logout message differently for guest and accounts
		if(isGuest) 
		{
			// Logout the guest player
			try {
				Main.database.logoutGuest(this.name);
			} catch(SQLException e) {}
		}
		else
		{
			// Logout the registered player
			try {
				Main.database.logoutPlayer(this.name);
			} catch (SQLException e) {}
		}
	}
	
	// Getters //
	public String getName() 
	{
		return this.name;
	}
	
	public int getAccountBalance() 
	{
		return this.accountBalance;
	}
}
