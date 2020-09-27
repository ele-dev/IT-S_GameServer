package serverPackage;

/*
 * written by Elias Geiger
 * 
 * This class is only used in a static context and 
 * servers as a handler for any text command input on the server console
 * 
 */

public class ConsoleHandler {
	
	// static class members
	private static String[] commands = {"help", 
										"exit",
										"list players"};
	
	private static String[] descriptions = {"Shows this list of available commands",
											"Closes all active client connections and the application itself",
											"Shows list with information about currently connected clients"};
	
	// Constants
	private static final int tableColumnWidth = 20;
	
	// Main handler function for command line input
	public static boolean handleCommand(String cmd) 
	{
		// Skip any invalid input 
		if(cmd.length() <= 0) {
			return true;
		}
		
		switch(cmd)
		{
			// Command to list all existing commands 
			case "help": 
			{
				System.out.println("List of available commands: ");
				printCommandHelpPage();
				break;
			}
			
			// Command for closing down the server application 
			case "exit":
			{
				// return false as a sign for the calling method to close the application
				return false;
			}
			
			case "list players":
			{
				System.out.println("List of online players: ");
				
				ClientConnection.printClientInfo();
				System.out.println();
				
				break;
			}
			
			default:
			{
				Main.logger.printWarning("Command does not exist. Type help for more information", false, 0);
				break;
			}
		}
		
		return true;
	}
	
	// Helper function for printing the help page for commands to the console
	private static void printCommandHelpPage() {
		System.out.println();
		
		for(int i = 0; i < commands.length; i++)
		{
			System.out.print("   " + commands[i]);
			// Insert spaces depending on the length (characters) of the command
			for(int k = tableColumnWidth - commands[i].length(); k >= 1; k--) 
			{
				System.out.print(" ");
			}
			System.out.println(descriptions[i]);
		}
		System.out.println();
	}
}
