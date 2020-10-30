package game;

/*
 * written by Elias Geiger
 * 
 * This class represents a running match.
 * An instance is created when two player have accepted to play against each other
 * It ends when one player wins. Leaving a running match is possible but automatically leads
 * to defeat because leaving before finishing is considered as giving up
 * 
 */

import java.util.ArrayList;

import networking.GenericMessage;
import networking.MsgMatchInfo;
import networking.SignalMessage;

public class Match {
	
	// static class members //
	private static ArrayList<Match> matches = new ArrayList<>();
	
	// class members //
	private Player p1, p2;
	private String currentlyActingPlayer;
	
	// Default Constructor
	public Match()
	{
		// initialize the class members 
		this.currentlyActingPlayer = "";
		this.p1 = this.p2 = null;
		
		// Add this instance to the list of matches
		matches.add(this);
	}
	
	// Advanced constructor takes two players
	public Match(Player player1, Player player2)
	{
		// Call the default constructor
		this();
		
		// store this players in the class
		this.p1 = player1;
		this.p2 = player2;
	}
	
	// Finalizer 
	@Override
	public void finalize() 
	{
		// Remove this instance from the list of running matches
		// matches.remove(this);
		
		// ...
		
		// Remove the players from the match
		this.p1 = this.p2 = null;
	}
	
	// match start function
	public void beginMatch() 
	{
		SignalMessage foundMatchMsg = new SignalMessage(GenericMessage.MSG_FOUND_MATCH);
		this.p1.sendMessage(foundMatchMsg);
		this.p1.joinMatch(this);
		this.p2.sendMessage(foundMatchMsg);
		this.p2.joinMatch(this);
		
		// Send initial info messages to the two players
		MsgMatchInfo matchInfo1 = new MsgMatchInfo(this.p2.getName());
		MsgMatchInfo matchInfo2 = new MsgMatchInfo(this.p1.getName());
		this.p1.sendMessage(matchInfo1);
		this.p2.sendMessage(matchInfo2);
		
		// Let player1 do his first move
		SignalMessage yourTurnMsg = new SignalMessage(GenericMessage.MSG_BEGIN_TURN);
		this.p1.sendMessage(yourTurnMsg);
		System.out.println("--> " + this.p1.getName() + " is allowed to act now");
	}
	
	// This function takes a player instance as argument 
	// If the player is the one that is currently allowed make moves
	// then switch turns and let the other player react
	public boolean switchTurn(Player finishedTurn) 
	{
		if(!finishedTurn.getName().equals(this.currentlyActingPlayer)) {
			// Player isn't allowed 
			return false;
		}
		
		// Switch turns and inform players about it
		SignalMessage yourTurnMsg = new SignalMessage(GenericMessage.MSG_BEGIN_TURN);
		if(this.p1.getName().equals(this.currentlyActingPlayer)) {
			this.currentlyActingPlayer = this.p2.getName();
			this.p2.sendMessage(yourTurnMsg);
			System.out.println("--> " + this.p2.getName() + " is allowed to act now");
		} else {
			this.currentlyActingPlayer = this.p1.getName();
			this.p1.sendMessage(yourTurnMsg);
			System.out.println("--> " + this.p1.getName() + " is allowed to act now");
		}
		
		return true;
	}
	
	public void leaveMatch(Player player) 
	{
		// Create an enemy surrender signal message
		SignalMessage surrender = new SignalMessage(GenericMessage.MSG_ENEMY_SURRENDER);
		
		// Determine which player wants to leave the match
		if(player.equals(this.p1)) {
			// Send the enemy surrender message to player 2
			this.p2.sendMessage(surrender);
			
		} else if(player.equals(this.p2)) {
			// Send the enemy surrender message to player 1
			this.p1.sendMessage(surrender);
			
		} else {
			// If the player doesn't match with one these players then continue the match
			return;
		}
		
		// Remove this unfinished match from the list
		matches.remove(this);
	}
	
	// static method that prints the running Matches formatted to the console
	public static void printMatches()
	{
		for(int i = 0; i < matches.size(); i++)
		{
			// skip invalid matches
			if(matches.get(i) == null) {
				continue;
			}
			
			System.out.print("   Match " + (i+1) + ": ");
			if(matches.get(i).p1 == null && matches.get(i).p2 == null) {
				System.out.println(" <empty>");
			} else {
				System.out.println(matches.get(i).p1.getName() + " vs " + matches.get(i).p2.getName());
			}
		}
	}
	
	// static method for ending all matches immediately
	public static void stopAllMatches() 
	{
		// Stop all running matches 
		for(Match m: matches) 
		{
			if(m != null) 
			{
				m.finalize();
			}
		}
		
		// Empty the list
		matches.clear();
	}
}
