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
		// Obtain the configured server port from the global config
		wellknownPort = GameConfigs.serverPort;
		
		// Reset the stop order flag at first
		this.stopOrder = false;
		
		// create the server socket and bind it to the service port
		try {
			this.serverSocket = new ServerSocket(wellknownPort);
		} catch (IOException e) {
			Main.logger.printError("Could not create and bind the server socket!", true);
		}
	}
	
	// Thread function for listening in the background
	public void run()
	{
		// begin to listen continously for connection requests
		this.listen();
		
		// Notify all client handler threads to finish their work
		ClientConnection.closeHandlerThreads();
		
		// close the server socket at last
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			Main.logger.printError("Could not close the server socket properly!", true);
		}
		
		return;
	}
	
	// Method for accepting incoming connection requests from clients
	private void listen()
	{
		try {
			this.serverSocket.setSoTimeout(4000);
		}  catch(SocketException e) {
			Main.logger.printWarning("Could not set server socket timeout!", true);
			return;
		}
			
		// Listen until a stop order arrives from the outside
		do {
			
			try {
				// Wait for the next connection request and handle it
				Socket clientSocket = this.serverSocket.accept();
				
				// create a new client instance and pass the client socket
				ClientConnection cc = new ClientConnection(clientSocket);
				
				// Launch a separate thread that will handle this client from now on
				cc.start();
			
				// Catch various exceptions that might be thrown
			} catch (SocketTimeoutException e) {
				// e.printStackTrace();
			} catch (IOException e) {
				Main.logger.printWarning("IO Exception thrown while listening", true);
			}
			
		} while(this.stopOrder == false);
			
		return;
	}
	
	// Method to stop this thread from the outside
	public void sendStopOrder() {
		this.stopOrder = true;
	}
}
