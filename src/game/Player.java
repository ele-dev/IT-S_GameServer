package game;

public class Player {
	
	// static class members //
	public static int nextGuestId = 0;
	
	// Class members 
	private String name;
	private int accountBalance;
	
	// Constructor
	public Player()
	{
		this.name = "";
		this.accountBalance = 0;
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
	
	public int getAccountBalance() 
	{
		return this.accountBalance;
	}
}
