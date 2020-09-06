package game;

import java.sql.SQLException;

import serverPackage.Main;

public class Player {
	
	// static class members //
	public static int nextGuestId = 0;
	private static Player quickMatchWaitingSlot = null;
	
	// Class members 
	private String name;
	private int accountBalance;
	private String currentState;		// states: homescreen | searching | playing
	
	// Default constructor for creating empty Player
	public Player()
	{
		this.name = "";
		this.accountBalance = 0;
		this.currentState = "homescreen";
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
	
	public String getState() 
	{
		return this.currentState;
	}
	
	public static Player getWaitingPlayer()
	{
		Player enemy = null;
		
		// If there is a waiting player then return him and free the waiting slot
		// Otherwise the function returns null to signal an empty waiting slot
		if(quickMatchWaitingSlot != null) {
			enemy = quickMatchWaitingSlot;
			quickMatchWaitingSlot = null;
		}
		
		return enemy;
	}
	
	// Setters //
	
	public void setState(String state) 
	{
		this.currentState = state;
	}
	
	public static void putPlayerOnWaitingSlot(Player player)
	{
		quickMatchWaitingSlot = player;
	}
}
