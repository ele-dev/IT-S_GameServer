package serverPackage;

/*
 * written by Elias Geiger
 * 
 * This class was takes care of the logging and functionality and also provides useful methods 
 * for formatted, colored console output  
 * 
 * There are 3 different console output channels: INFO (white), WARNING (yellow), ERROR (red) 
 * this way it is possible to highlight messages and classify them. The three different Methods are 
 * used in all source file when console output is printed out. Additionally the it can be specified through
 * a boolean flag parameter if a console message should be written to the log file or not
 * 
 * Another handy feature is the conditional output implemented using a logLevel that can be 0, 1 or 2
 * It's mainly used for debugging. If you need to know the value of a variable at a specific point of time for example 
 * then it is useful to increase the log-level for the debug message, then it is only shown if the log level during execution
 * is high enough. The initial log-level is also defined in the config.txt by the way
 * 
 * Note: The color codes for colored console messages aren't working on windows and aren't cross-platform in general
 * They only work on Linux platforms and since the server application is supposed to run on Linux
 * anyway I decided to keep this simple implementation coloring the console text
 * 
 */

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.Date;

public class Logger {
	
	// static class members //
	
	// the single instance
	private static Logger logger = new Logger();
	
	// Console color codes
    // Reset
    public static final String RESET = "\033[0m";  // Text Reset

    // Regular Colors
    public static final String BLACK = "\033[0;30m";   // BLACK
    public static final String RED = "\033[0;31m";     // RED
    public static final String GREEN = "\033[0;32m";   // GREEN
    public static final String YELLOW = "\033[0;33m";  // YELLOW
    public static final String BLUE = "\033[0;34m";    // BLUE
    public static final String PURPLE = "\033[0;35m";  // PURPLE
    public static final String CYAN = "\033[0;36m";    // CYAN
    public static final String WHITE = "\033[0;37m";   // WHITE

    // Bold
    public static final String BLACK_BOLD = "\033[1;30m";  // BLACK
    public static final String RED_BOLD = "\033[1;31m";    // RED
    public static final String GREEN_BOLD = "\033[1;32m";  // GREEN
    public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
    public static final String BLUE_BOLD = "\033[1;34m";   // BLUE
    public static final String PURPLE_BOLD = "\033[1;35m"; // PURPLE
    public static final String CYAN_BOLD = "\033[1;36m";   // CYAN
    public static final String WHITE_BOLD = "\033[1;37m";  // WHITE
    
    // non static class members //
    private FileWriter fileWriter;
    private int logLevel;		// basic = 0 | enhanced = 1 | detailed = 2
    
    // Constructor
    private Logger()
    {
    	// Create or reopen the log file in append mode
    	try {
			this.fileWriter = new FileWriter(GameConfigs.logFilename, true);
		} catch (IOException e) {
			System.err.println("Failed to open/create the logfile!");
		}
    	
    	// create a full time stamp including date 
    	Date date = new Date();
    	long time = date.getTime();
    	Timestamp ts = new Timestamp(time);
    	
    	// Enter a separation line to mark the application launch in the log file
    	try {
    		fileWriter.write("\n\n");
			fileWriter.write("---------------------- Application launch at " + ts + " ----------------------\n\n");
		} catch (IOException e) {
			System.err.println("Failed to write to the log file!");
		}
    	
    	// Set the initial log level
    	this.setLogLevel(GameConfigs.logLevel);
    }
    
    // Static method for retrieving the instance
    public static Logger getLoggerInstance() 
    {
    	return logger;
    }
    
    // Finalizer
    @Override 
    public void finalize() 
    {
    	// close the file writer before garbage collection
    	try {
			this.fileWriter.close();
			System.out.println("Closed file writer");
		} catch (IOException e) {
			System.err.println("Failed to close file writer");
		}
    	
    	// Make sure the console text color is reseted before the application closes
    	System.out.print(RESET);
    }
    
    // Method to change the current log level
    public void setLogLevel(String level)
    {
    	switch(level)
    	{
			case "basic":
			{
				this.logLevel = 0;
				break;
			}
			
			case "enhanced":
			{
				this.logLevel = 1;
				break;
			}
			
			case "detailed":
			{
				this.logLevel = 2;
				break;
			}
			
    		default:
    		{
    			// Set the default log level to 0 (basic)
    			this.logLevel = 0;
    			break;
    		}
    	}
    }
    
    // Different methods for formatted console output and file logging
    public void printInfo(String output, boolean loggingOn, int level) 
    {
    	// Only write if the log level is high enough
    	if(level > logLevel) 
    		return;
    	
    	// First get a formatted time stamp
    	String tStamp = getTimestamp();
    	
    	// Then print the output to console in the right color
    	System.out.print(tStamp);
    	System.out.print(WHITE + "[Info]: ");
    	System.out.println(output);
    	
    	// Optionally write to the log file too
    	if(loggingOn) {
    		try {
    			this.fileWriter.write(tStamp + "[Info]: " + output + "\n");
    		} catch(Exception e) {}
    	}
    }
    
    public void printError(String output, boolean loggingOn, int level) 
    {
    	// Only write if the log level is high enough
    	if(level > logLevel) 
    		return;
    	
    	// First get a formatted time stamp
    	String tStamp = getTimestamp();
    	
    	// Then print the output to console in the right color
    	System.out.print(tStamp);
    	System.out.print(RED + "[Error]: ");
    	System.out.println(output);
    	System.out.print(WHITE);
    	
    	// Optionally write to the log file too
    	if(loggingOn) {
    		try {
    			this.fileWriter.write(tStamp + "[Error]: " + output + "\n");
    		} catch(Exception e) {}
    	}
    }
    
    public void printWarning(String output, boolean loggingOn, int level) 
    {
    	// Only write if the log level is high enough
    	if(level > logLevel) 
    		return;
    	
    	// First get a formatted time stamp
    	String tStamp = getTimestamp();
    	
    	// Then print the output to console in the right color
    	System.out.print(tStamp);
    	System.out.print(YELLOW + "[Warning]: ");
    	System.out.println(output);
    	System.out.print(WHITE);
    	
    	// Optionally write to the log file too
    	if(loggingOn) {
    		try {
    			this.fileWriter.write(tStamp + "[Warning]: " + output + "\n");
    		} catch(Exception e) {}
    	}
    }
    
    // Private function that returns a formatted time stamp for console output
    // Desired time stamp format 	[hh:mm:ss]
    private static String getTimestamp()
    {
    	LocalTime now = LocalTime.now();
    	
    	String hour = now.getHour() < 10 ? "0" : "";
    	hour += now.getHour();
    	String minute = now.getMinute() < 10 ? "0" : "";
    	minute += now.getMinute();
    	String second = now.getSecond() < 10 ? "0" : "";
    	second += now.getSecond();
    	String timestamp = "[" + hour + ":" + minute + ":" + second + "] ";
    	
    	return timestamp;
    }
}
