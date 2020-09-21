package game;

import java.util.ArrayList;

/*
 * written by Elias Geiger
 * 
 * This class represents a running match.
 * An instance is created when two player have accepted to play against each other
 * It ends when one player wins. Leaving a running match is possible but automatically leads
 * to defeat because leaving before finishing is considered as giving up
 * 
 */

public class Match {
	
	// static class members //
	private static ArrayList<Match> matches = new ArrayList<>();
	
	// class members //
	private Player p1, p2;
	
	// Constructor
	public Match()
	{
		// initialize the class members 
		this.p1 = this.p2 = null;
		
		// Add this instance to the list of matches
		matches.add(this);
	}
	
	// Finalizer 
	@Override
	public void finalize() 
	{
		// Remove this instance from the list of matches
		matches.remove(this);
	}
}
