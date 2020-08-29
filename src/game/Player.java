package game;

public class Player {
	
	// static class members //
	public static int nextGuestId = 0;
	
	// Class members 
	private String name;
	
	// Constructor
	public Player()
	{
		this.name = "";
	}
	
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
}
