package serverPackage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    private FileWriter fileWriter;
    
    // Constructor
    public Logger()
    {
    	// create and open a log file
    	this.logFile = new File(GameConfigs.logFilename);
    	
    	// Create a file write for the open file
    	try {
			this.fileWriter = new FileWriter(this.logFile);
		} catch (IOException e) {
			
		}
    	
    	
    }
    
    // Finalizer
    @Override 
    public void finalize() 
    {
    	// close the file writer before garbage collection
    	try {
			this.fileWriter.close();
		} catch (IOException e) {
		}
    	
    	// Make sure the console text color is resetted before the application closes
    	System.out.print(RESET);
    }
    
    // Different methods for formatted console output and file logging
    public void printInfo(String output) 
    {
    	System.out.print(getTimestamp());
    	System.out.print(WHITE + "[Info]: ");
    	System.out.println(output);
    }
    
    public void printError(String output) 
    {
    	System.out.print(getTimestamp());
    	System.out.print(RED + "[Error]: ");
    	System.out.println(output);
    	System.out.print(WHITE);
    }
    
    public void printWarning(String output) 
    {
    	System.out.print(getTimestamp());
    	System.out.print(YELLOW + "[Warning]: ");
    	System.out.println(output);
    	System.out.print(WHITE);
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
