package serverPackage;

/*
 * written by Elias Geiger
 * 
 * The Network Listener Class contains the ServerSocket which is responsible
 * for listening for connection requests from clients and accepting them
 * All of this runs in the ServerListenerThread parallel and independent to the main thread
 * where the console input from server admins is handled.
 * 
 * From this thread, the client handler threads are created and launched
 * 
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class NetworkListener extends Thread {

	// single instance
	private static NetworkListener networkListener = new NetworkListener();
	
	// server socket and the well known service port
	ServerSocket serverSocket = null;
	private static int wellknownPort = 0;
	
	// Thread status indicator for outside surveillance
	private boolean stopOrder; 
	
	// Constructor 
	private NetworkListener()
	{
		super();
		super.setName("ServerListenerThread");
		
		boolean status = true;
		
		// Obtain the configured server port from the global configuration
		wellknownPort = GameConfigs.serverPort;
		
		// Reset the stop order flag at first
		this.stopOrder = false;
		
		// create the server socket and bind it to the service port
		try {
			this.serverSocket = new ServerSocket(wellknownPort);
		} catch (IOException e) {
			Main.logger.printError("Could not create and bind the server socket!", true, 0);
			status = false;
		}
		
		if(status)
			Main.logger.printInfo("Created Server Socket and bound to Port " + wellknownPort, true, 0);
	}
	
	// Public static function for retrieving the instance
	public static NetworkListener getNetworkListenerInstance() 
	{
		return networkListener;
	}
	
	// Thread function for listening in the background
	public void run()
	{
		// Print info message that server listener thread has just been launched
		Main.logger.printInfo("Server listener thread launched", true, 1);
		
		// begin to listen continuously for connection requests
		this.listen();
		
		// Notify all client handler threads to finish their work
		ClientConnection.closeHandlerThreads();
		
		// close the server socket at last
		boolean status = true;
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			Main.logger.printError("Could not close the server socket properly!", true, 1);
			status = false;
		}
		
		if(status)
			Main.logger.printInfo("Closed server socket", true, 0);
		
		// Print info that server listener thread is closed now
		Main.logger.printInfo("Server listener thread closed", true, 1);
		
		return;
	}
	
	// Method for accepting incoming connection requests from clients
	private void listen()
	{
		// Set the server socket timeout to 4 seconds (blocking mode)
		try {
			this.serverSocket.setSoTimeout(4000);
		}  catch(SocketException e) {
			Main.logger.printWarning("Could not set server socket timeout!", true, 1);
			return;
		}
		
		// Print little status message
		Main.logger.printInfo("Server ready. Listening for connection requests ... ", true, 0);
			
		// Listen until a stop order arrives from the outside
		do {
			
			try {
				// Wait for the next connection request and handle it
				Socket clientSocket = this.serverSocket.accept();
				
				// Print info message that a new client has established a connection
				String ipStr = clientSocket.getRemoteSocketAddress().toString();
				ipStr = ipStr.replace('/', ' ');
				Main.logger.printInfo("Accepted connection request from" + ipStr, true, 0);
				
				// create a new client instance and pass the client socket
				ClientConnection cc = new ClientConnection(clientSocket);
				
				Main.logger.printInfo("New client connection instance created", true, 2);
				
				// Launch a separate thread that will handle this client from now on
				cc.start();
				
				Main.logger.printInfo("New client handler thread launched", true, 1);
			
				// Catch various exceptions that might be thrown
			} catch (SocketTimeoutException e1) {
				Main.logger.printInfo("Server Socket Timeout exception thrown while listening", false, 2);
			} catch (IOException e) {
				Main.logger.printWarning("IO Exception thrown while listening", true, 2);
			} catch (Exception e) {
				Main.logger.printWarning("Unknown Exception thrown while listening/accepting!", true, 2);
				e.printStackTrace();
			}
			
		} while(this.stopOrder == false);
			
		return;
	}
	
	// Method to stop this thread from the outside
	public void sendStopOrder() {
		this.stopOrder = true;
	}
}
