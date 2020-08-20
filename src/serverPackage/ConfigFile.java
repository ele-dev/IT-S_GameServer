package serverPackage;

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
			
			// Remove all whitespaces (including tabs) between characters in this line
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
			// Extract the value by seperating name and value from each other
			String[] tmpArray = x.split(":");
			if(tmpArray[0].equals(name)) {
				value = tmpArray[1];
				return value;
			}
		}
		
		// Print message if the config value wasnt found 
		System.out.println("Unable to find config value named " + name + ". Please check " + this.configFile.getName());
		System.out.println("  --> using default config value");
		
		return value;
	}
}
