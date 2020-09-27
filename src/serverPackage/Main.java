package serverPackage;

/*
 * written by Elias Geiger
 * 
 * This is the main class, with the application entry point of
 * Things are done on a high abstraction level.
 * This way the application structure is more comprehendable
 * 
 */

import java.util.Scanner;

public class Main {
	
	// The main parts of the application
	static NetworkListener listener = null;
	public static Logger logger = null;
	static BackgroundWorker worker = null;

	
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
		
		System.out.println("Type 'help' to list all available commands");
		
		// Handle command line input in the main frame
		Scanner scanner = new Scanner(System.in);
		String consoleInput = "";
		boolean running = true;
		
		do {
			System.out.print("> ");
			consoleInput = scanner.nextLine();
			running = ConsoleHandler.handleCommand(consoleInput);
			
		} while(running);		// exit command takes server offline
		
		// Call local shutdown method
		shutdownModules();
		scanner.close();
		
		System.out.println("Exit application");
		
		return;
	}
	
	// Intialization of the application modules
	private static void initModules() 
	{
		System.out.println("Loading modules ... ");
		
		// create a logger instance 
		logger = new Logger();
		
		// init and launch the network listener thread 
		listener = new NetworkListener();
		listener.start();
		
		// intit and launch the backgeound worker thread
		worker = new BackgroundWorker();
		worker.start();
	}
	
	// Shutdown of the application modules
	private static void shutdownModules()
	{
		// Finalize the background worker first
		if(worker != null) {
			// The tell the worker thread to complete his work
			worker.sendStopOrder();
			
			// Wait until the thread has finished
			while(worker.isAlive()) {}
		}
		
		// Finalize the network module next
		if(listener != null) {
			// Tell the network listener thread to complete his work
			listener.sendStopOrder();
			
			// Wait until the thread has finished and return
			logger.printInfo("Waiting for threads to finish ... ", true, 0);
			while(listener.isAlive()) {}
		}
		
		// Finalize the logger module
		if(logger != null) {
			logger.finalize();
		}
	}

}
