package game;

/*
 * written by Elias Geiger
 * 
 * This class represent an online player
 * Players can either be guests or registered players but all have a few common properties
 * the name, state (homescreen, searching or playing), account balance (game money)
 * 
 * The class is mainly used to track player activities but also includes static variables 
 * and functions for the waiting queue and matchmaking in general
 * 
 */

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
	
	// If there is a waiting player on the waiting slot then return him and free the slot
	// Otherwise the function returns null to signal an empty waiting slot
	public static Player getWaitingPlayer()
	{
		Player enemy = null;
		
		if(quickMatchWaitingSlot != null) {
			enemy = quickMatchWaitingSlot;
			quickMatchWaitingSlot = null;
		}
		
		return enemy;
	}
	
	// This method functions checks the equality of the waiting player and the passed instance
	// Only if it is the same player then empty the slot and return true otherwise return false
	public static boolean abortWaiting(Player player)
	{
		if(player.equals(quickMatchWaitingSlot)) {
			// Empty the waiting slot to abort the players match search
			quickMatchWaitingSlot = null;
			return true;
		}
		
		return false;
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
