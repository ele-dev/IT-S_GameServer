package serverPackage;

/*
 * written by Elias Geiger
 * 
 * This class encapsulates a thread which keeps executing tasks in 
 * the background in a periodic manner
 * 
 */

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;

public class BackgroundWorker extends Thread {
	
	// single instance 
	private static BackgroundWorker backgroundWorker = new BackgroundWorker();

	// Class members //
	private boolean stopOrder;
	private Instant lastSQLKeepAlive;
	private Instant lastAccountPurge;
	private Instant lastGameStatsSync;
	
	// Intervals
	private static final int sqlKeepAliveIntv = 30;		// every 30 seconds
	private static final int accountPurgeIntv = 50;		// every 50 seconds
	private static final int gameStatsSyncIntv = 10;	// every 10 seconds
	
	// Constructor
	private BackgroundWorker() 
	{
		this.stopOrder = false;
		this.lastSQLKeepAlive = Instant.now();
		this.lastAccountPurge = Instant.now();
		this.lastGameStatsSync = Instant.now();
	}
	
	// Static method to retrieve the instance
	public static BackgroundWorker getBackgroundWorkerInstance()
	{
		return backgroundWorker;
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
	@Override
	public void run() 
	{
		// Run until a stop order from the main thread arrives
		while(!stopOrder)
		{
			// Execute the test SQL statement to keep the database connection alive
			Instant now = Instant.now();
			Duration duration = Duration.between(this.lastSQLKeepAlive, now);
			if(duration.getSeconds() > sqlKeepAliveIntv) {
				boolean status = Main.database.testConnection();
				if(status) {
					this.lastSQLKeepAlive = now;
				} else {
					// ...
				}
			}
			
			// Check if there are accounts in the database exist over long time but have never been verified
			now = Instant.now();
			duration = Duration.between(this.lastAccountPurge, now);
			if(duration.getSeconds() > accountPurgeIntv) {
				
				// Delete all accounts that haven't been verified for a 7 days or more
				try {
					Main.database.purgeOldAccounts(7);
				} catch (SQLException e) {
					// e.printStackTrace();
					Main.logger.printWarning("Error during periodic account purge", true, 1);
				}
				this.lastAccountPurge = now;
			}
			
			// Send current game statistics and account info to every online player that isn't in-game
			now = Instant.now();
			duration = Duration.between(this.lastGameStatsSync, now);
			if(duration.getSeconds() > gameStatsSyncIntv) {
				
				// Send the message to the player clients
				// ...
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
