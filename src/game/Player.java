package game;

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
