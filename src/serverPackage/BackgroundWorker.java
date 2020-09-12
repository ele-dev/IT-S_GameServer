package serverPackage;

import java.time.Duration;
import java.time.Instant;

public class BackgroundWorker extends Thread {

	// Class members //
	private boolean stopOrder;
	private Instant lastSQLKeepAlive;
	
	// Constructor
	public BackgroundWorker() 
	{
		this.stopOrder = false;
		this.lastSQLKeepAlive = Instant.now();
	}
	
	@Override
	public void finalize()
	{
		// ...
	}
	
	public void sendStopOrder()
	{
		this.stopOrder = true;
	}
	
	// Thread function that runs background tasks periodically
	@SuppressWarnings("unused")
	@Override
	public void run() 
	{
		// Run until a stop order from the main thread arrives
		while(!stopOrder)
		{
			// Execute the test sql statement to keep the database connection alive
			Instant now = Instant.now();
			Duration duration = Duration.between(lastSQLKeepAlive, now);
			if(duration.getSeconds() > 30) {
				boolean status = Main.database.testConnection();
				lastSQLKeepAlive = now;
			}
		}
		
		this.finalize();
	}
}
