package serverPackage;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.Date;

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
    private FileWriter fileWriter;
    
    // Constructor
    public Logger()
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
    	
    	// Enter a seperation line to mark the application launch in the logfile
    	try {
    		fileWriter.write("\n\n");
			fileWriter.write("---------------------- Application launch at " + ts + " ----------------------\n\n");
		} catch (IOException e) {
			System.err.println("Failed to write to the log file!");
		}
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
    	
    	// Make sure the console text color is resetted before the application closes
    	System.out.print(RESET);
    }
    
    // Different methods for formatted console output and file logging
    public void printInfo(String output, boolean loggingOn) 
    {
    	// First get a formatted timestamp
    	String tStamp = getTimestamp();
    	
    	// Then print the output to console in the right color
    	System.out.print(tStamp);
    	System.out.print(WHITE + "[Info]: ");
    	System.out.println(output);
    	
    	// Optionally write to the logfile too
    	if(loggingOn) {
    		try {
    			this.fileWriter.write(tStamp + "[Info]: " + output + "\n");
    		} catch(Exception e) {}
    	}
    }
    
    public void printError(String output, boolean loggingOn) 
    {
    	// First get a formatted timestamp
    	String tStamp = getTimestamp();
    	
    	// Then print the output to console in the right color
    	System.out.print(tStamp);
    	System.out.print(RED + "[Error]: ");
    	System.out.println(output);
    	System.out.print(WHITE);
    	
    	// Optionally write to the logfile too
    	if(loggingOn) {
    		try {
    			this.fileWriter.write(tStamp + "[Error]: " + output + "\n");
    		} catch(Exception e) {}
    	}
    }
    
    public void printWarning(String output, boolean loggingOn) 
    {
    	// First get a formatted timestamp
    	String tStamp = getTimestamp();
    	
    	// Then print the output to console in the right color
    	System.out.print(tStamp);
    	System.out.print(YELLOW + "[Warning]: ");
    	System.out.println(output);
    	System.out.print(WHITE);
    	
    	// Optionally write to the logfile too
    	if(loggingOn) {
    		try {
    			this.fileWriter.write(tStamp + "[Warning]: " + output + "\n");
    		} catch(Exception e) {}
    	}
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
