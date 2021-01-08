package serverPackage;

/*
 * writte by Elias Geiger
 * 
 * This class simplifies the usage of external config files in a simple key:value format
 * When instantiated, it reads in the given textfile, separates the lines from each other
 * and removes all whitespace and comments (// and following text is ignored)
 * The : is used as separator between key and value in a text line
 * 
 * It's a big advantage over hardcoding config values because this way it is possible to 
 * quickly change environment variables without the need to rebuilt/recompile and export the whole application 
 * 
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ConfigFile {
	
	private File configFile = null;
	private Scanner scanner = null;
	private ArrayList<String> lines = null;
	
	public ConfigFile(String fullpath) throws FileNotFoundException 
	{
		lines = new ArrayList<String>();
		configFile = new File(fullpath);
		scanner = new Scanner(this.configFile);
		readLines();
	}
	
	// Private functions //
	
	private void readLines() {
		// read in the textfile content line by line
		while(this.scanner.hasNextLine()) 
		{
			// Read in the complete next line
			String line = this.scanner.nextLine();
			
			// Remove all whitespace (including tabs) between characters in this line
			line = line.replaceAll("\\s+", "");
			
			// Remove optional comments following after the comment marker (//) from the line
			String[] temp = line.split("//");
			line = temp[0];
			
			// Add the reduced line to the list
			this.lines.add(line);
		}
	}
	
	// Public functions //
	public String getValueByName(String name) {
		String value = "not found";
		
		// Go through the lines and to find the corresponding entry
		for(String x: this.lines)
		{
			// Extract the value by separating name and value from each other
			String[] tmpArray = x.split(":");
			if(tmpArray[0].equals(name)) {
				value = tmpArray[1];
				return value;
			}
		}
		
		// Print message if the config value wasn't found 
		System.out.println("Unable to find config value named " + name + ". Please check " + this.configFile.getName());
		System.out.println("  --> using default config value");
		
		return value;
	}
}
