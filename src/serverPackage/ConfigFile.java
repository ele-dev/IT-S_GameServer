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
			
			// Remove all spaces between characters in the line
			// ... 
			
			// Remove optional comments after // from the line
			// ...
			
			// Add the reduced line to the list
			this.lines.add(line);
		}
	}
	
	// Public functions //
	public String getValueByName(String name) {
		String value = "";
		
		for(String x: this.lines)
		{
			String[] tmpArray = x.split(":");
			if(tmpArray[0].equals(name)) {
				value = tmpArray[1];
				if(value.charAt(0) == ' ') {
					value.replace(" ", "");
					return value;
				}
			}
		}
		
		return value;
	}
}
