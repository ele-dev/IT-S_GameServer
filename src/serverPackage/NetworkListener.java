package serverPackage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class NetworkListener extends Thread {

	// class members //
	
	// server socket and the wel known service port
	ServerSocket serverSocket = null;
	private static int wellknownPort = 0;
	
	// Thread status indicator for outside surveillance
	private boolean stopOrder;
	
	// Methods // 
	
	// Constructor 
	public NetworkListener()
	{
		super();
		super.setName("ServerListenerThread");
		
		boolean status = true;
		
		// Obtain the configured server port from the global config
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
	
	// Thread function for listening in the background
	public void run()
	{
		// Print info message that server listener thread has just been launched
		Main.logger.printInfo("Server listener thread launched", true, 1);
		
		// begin to listen continously for connection requests
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
				Main.logger.printInfo("Accepted connection request from " + clientSocket.getRemoteSocketAddress().toString(), true, 0);
				
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
