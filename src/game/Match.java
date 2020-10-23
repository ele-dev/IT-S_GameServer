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
		// Remove the players from the match
		this.p1 = this.p2 = null;
		
		// Remove this instance from the list of matches
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
			
			System.out.print("   Match " + i + ": ");
			System.out.println(matches.get(i).p1.getName() + " vs " + matches.get(i).p2.getName());
		}
	}
}
