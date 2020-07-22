package serverPackage;

import java.util.Scanner;

public class Main {
	
	// The main parts of the application
	static NetworkListener listener = null;
	static Logger logger = null;

	
	// --- Application Entry point --- //
	public static void main(String[] args) {
		System.out.println("Online Game Server Application");
		
		// First read the configuration file to obtain the basic parameters 
		boolean result = GameConfigs.readConfigFile();
		if(!result) {
			System.err.println("[Error]: Could not read the configuration file!");
			System.out.println("  --> Using default paramaters");
		} else {
			System.out.println("Configuration was loaded from file");
		}
		
		// Print out the config values that will be used
		GameConfigs.printConfig();
		
		// Call local init method 
		initModules();
		
		// dummy output
		logger.print("This is a logger output");
		
		// Handle command line input in the main frame
		Scanner scanner = new Scanner(System.in);
		String consoleInput = "";
		
		do {
			System.out.print("~ ");
			consoleInput = scanner.nextLine();
			
		} while(!consoleInput.equals("exit"));		// exit command takes server offline
		
		// Call local shutdown method
		shutdownModules();
		scanner.close();
		
		System.out.println("Exit application");
		
		return;
	}
	
	private static void initModules() 
	{
		System.out.print("Loading modules ... ");
		
		// create a logger instance 
		logger = new Logger();
		
		// init and launch the network listener thread 
		listener = new NetworkListener();
		listener.start();
		
		System.out.println("Done");
	}
	
	private static void shutdownModules()
	{
		// Tell the network listener thread to complete his work
		listener.sendStopOrder();
		
		// Wait until the thread has finished and return
		System.out.print("Waiting for threads to finish ... ");
		while(listener.isAlive()) {}
		System.out.println("Done");
	}

}
