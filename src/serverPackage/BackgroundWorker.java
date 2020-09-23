package serverPackage;

/*
 * written by Elias Geiger
 * 
 * This class encapsulates a thread which keeps executing tasks in 
 * the background in a periodic manner
 * 
 */

public class BackgroundWorker extends Thread {

	// Class members //
	private boolean stopOrder;
	
	// Constructor
	public BackgroundWorker() 
	{
		this.stopOrder = false;
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
