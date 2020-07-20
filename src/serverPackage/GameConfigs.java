package serverPackage;

import java.io.FileNotFoundException;

public class GameConfigs {
	
	// relevant game configuration parameters default values
	public static int serverPort = 1000;
	
	public static boolean readConfigFile() {
		boolean result = true;
		
		ConfigFile file = null;
		
		// Open the config file
		try {
			file = new ConfigFile("config.txt");
		} catch (FileNotFoundException e) {
			result = false;
			return false;
		}
		// Get all the values we need from the config file
		try {
			// value = file.getValueByName("valName");
			serverPort = Integer.parseInt(file.getValueByName("server-port"));
			
		} catch(Exception e) {
			System.out.println(e);
			System.err.println("Please check the configuration file syntax!");
			result = false;
		}
		
		return result;
	}
	
	public static void printConfig() {
		System.out.println("Configuration Values: ");
		System.out.println("Service Port: " + serverPort);
		// ...
		System.out.println();
	}
}
