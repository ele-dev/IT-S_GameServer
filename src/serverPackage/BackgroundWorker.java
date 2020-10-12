package serverPackage;

/*
 * written by Elias Geiger
 * 
 * This class encapsulates a thread which keeps executing tasks in 
 * the background in a periodic manner
 * 
 */

import java.time.Duration;
import java.time.Instant;

public class BackgroundWorker extends Thread {

	// Class members //
	private boolean stopOrder;
	private Instant lastSQLKeepAlive;
	private Instant lastAccountPurge;
	
	// Intervalls
	private static final int sqlKeepAliveIntv = 30;		// every 30 seconds
	private static final int accountPurgeIntv = 50;		// every 50 seconds
	
	// Constructor
	public BackgroundWorker() 
	{
		this.stopOrder = false;
		this.lastSQLKeepAlive = Instant.now();
		this.lastAccountPurge = Instant.now();
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
			Duration duration = Duration.between(this.lastSQLKeepAlive, now);
			if(duration.getSeconds() > sqlKeepAliveIntv) {
				boolean status = Main.database.testConnection();
				this.lastSQLKeepAlive = now;
			}
			
			// Check if there are accounts in the database exist over long time but have never been verified
			now = Instant.now();
			duration = Duration.between(this.lastAccountPurge, now);
			if(duration.getSeconds() > accountPurgeIntv) {
				// ...
				
				this.lastAccountPurge = now;
			}
			
			// Do background tasks
			// ...
			
			// A short break for the thread to prevent undesired high CPU consumption
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				continue;
			}
		}
		
		this.finalize();
	}
}
