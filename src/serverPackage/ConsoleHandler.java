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
										"list-players",
										"list-matches",
										"set-loglevel"};
	
	private static String[] descriptions = {"Shows this list of available commands",
											"Closes all active client connections and the application itself",
											"Shows list with information about currently connected clients",
											"Shows list of all running matches",
											"Changes the loglevel (basic, enhanced or detailed)"};
	
	// Constants
	private static final int tableColumnWidth = 20;
	
	// Main handler function for command line input
	public static boolean handleCommand(String cmd) 
	{
		// Skip any invalid input 
		if(cmd.length() <= 0) {
			return true;
		}
		
		// Separate possible arguments from the command itself (space as separator char)
		String[] arguments = cmd.split(" ");
		int argCount = arguments.length - 1;
		
		switch(arguments[0])
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
			
			// Command for listing all connected clients
			case "list-players":
			{
				System.out.println("List of online players: ");
				
				ClientConnection.printClientInfo();
				System.out.println();
				
				break;
			}
			
			case "list-matches":
			{
				System.out.println("Running matches: ");
				
				// ...
				System.out.println();
				
				break;
			}
			
			// Command for changing the application log level during runtime
			case "set-loglevel":
			{
				// Check for the required argument
				if(argCount != 1) {
					Main.logger.printWarning("Argument count does not match. Type 'set-loglevel --help' for more info", false, 0);
					break;
				}
				
				// Check if the admin wants help to the command usage
				if(arguments[1].equalsIgnoreCase("--help")) {
					System.out.println("Usage: set-loglevel [level]");
					System.out.println("Options for [level]: 'basic', 'enhanced', 'detailed'");
					System.out.println();
					break;
				}
				
				// Change the log level
				Main.logger.setLogLevel(arguments[1]);
				
				break;
			}
			
			default:
			{
				Main.logger.printWarning("Command does not exist. Type 'help' for more information", false, 0);
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
