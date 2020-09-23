package game;

/*
 * written by Elias Geiger
 * 
 * This class represent an online player
 */


public class Player {
	
	// static class members //
	
	// Class members // 
	private String name;
	
	// Default constructor for creating empty Player
	public Player()
	{
		this.name = "";
	}
	
	// Additional constructor for creating player with name
	public Player(String name) 
	{
		this();
		this.name = name;
	}
	
	// Getters //
	public String getName() 
	{
		return this.name;
	}
	
	// Setters //
	// ...
}
