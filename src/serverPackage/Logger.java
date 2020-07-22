package serverPackage;

import java.io.File;
import java.time.LocalTime;

public class Logger {
	
	// static class members //
	
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
    private File logFile;
    
    // Constructor
    public Logger()
    {
    	// create and open a log file
    	// this.logFile = new File(GameConfigs.logFilename);
    }
    
    // Different methods for formatted console output and file logging
    public void print(String output) 
    {
    	System.out.println(getTimestamp() + output);
    }
    
    // Private function that returns a formatted timestamp for console output
    // Desired timestamp format 	[hh:mm:ss]
    private static String getTimestamp()
    {
    	LocalTime now = LocalTime.now();
    	
    	String timestamp = "[" + now.getHour() + ":" + now.getMinute() + ":" + now.getSecond() + "] ";
    	
    	return timestamp;
    }
}
