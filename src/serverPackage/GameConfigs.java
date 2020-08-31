package serverPackage;

/*
 * written by Elias Geiger
 * 
 * This class is kind of a wrapper on top of the ConfigFile Class and is only used in static context
 * Inside the readConfigFile Function all config values that are expected to be present in the configuration file
 * are retrieved, parsed to other datatypes if necessary and finally stored inside public accessible variables
 * 
 * This additional class for config variables makes it even more comfortable to introduce additonal configuration variables
 * into the project because the ConfigFile Class can remain untouched and stays resusable for other projects while 
 * it requires only two lines of code more in this class to read in a new value
 * 
 * Last but not least the class offers the option to define default fallback values for the variable that will be used 
 * in case the given configuration file can not be found and read for any reason
 * 
 */

import java.io.FileNotFoundException;

public class GameConfigs {
	
	// hardcoded constant
	private static final String configFilePath = "config.txt";
	
	// relevant game configuration parameters default values
	public static int serverPort = 1000;
	public static String dbHost = "localhost";
	public static String dbUser = "dbUser";
	public static String dbPassword = "dbPass";
	public static String logFilename = "log.txt";
	public static String logLevel = "basic";       // basic | enhanced | detailed
	
	public static boolean readConfigFile() {
		boolean result = true;
		
		ConfigFile file = null;
		
		// Open the config file
		try {
			file = new ConfigFile(configFilePath);
		} catch (FileNotFoundException e) {
			result = false;
			return false;
		}
		// Get all the values we need from the config file
		try {
			// General paradigma for reading in config values
			// value = file.getValueByName("valName");
			
			// General values
			serverPort = Integer.parseInt(file.getValueByName("server-port"));
			
			// Database (MariaDB/MySQL) related configuration values
			dbHost = file.getValueByName("db-host");
			dbUser = file.getValueByName("db-user");
			dbPassword = file.getValueByName("db-password");
			
			// Logging related values
			logFilename = file.getValueByName("log-file");
			logLevel = file.getValueByName("log-level");

			// ... 
			
		} catch(Exception e) {
			System.out.println(e);
			System.err.println("Please check the configuration file syntax!");
			result = false;
		}
		
		return result;
	}
	
	// Static method for printing all the global configuration values to the server console
	public static void printConfig() {
		System.out.println("Configuration Values: ");
		System.out.println("General:");
		System.out.println("   Service Port: " + serverPort);
		System.out.println("Database: ");
		System.out.println("   Host: " + dbHost);
		System.out.println("   Username: " + dbUser);
		System.out.println("   Password: " + dbPassword);
		System.out.println("Logging: ");
		System.out.println("   logfile: " + logFilename);
		System.out.println("   log level: " + logLevel);
		// ...
		System.out.println();
	}
}
