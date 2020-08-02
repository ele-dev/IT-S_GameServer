package serverPackage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import networking.*;

public class ClientConnection extends Thread {
	
	// --- static class members --- // 
	private static ArrayList<ClientConnection> clientList = new ArrayList<>();
	
	// --- non-static members --- //
	Socket clientSocket = null;
	ObjectOutputStream objOut = null;
	ObjectInputStream objIn = null;
	
	// Thread status indicator
	private boolean stopOrder;
	
	// Constructor
	public ClientConnection(Socket socket)
	{
		// init values
		super("ClientHandlerThread");
		this.clientSocket = socket;
		this.stopOrder = false;
		
		// create and prepare the i/o streams for data serialization
		boolean status = true;
		try {
			this.objOut = new ObjectOutputStream(this.clientSocket.getOutputStream());
			this.objOut.flush();
			this.objIn = new ObjectInputStream(this.clientSocket.getInputStream());
		} catch (Exception e) {
			Main.logger.printError("Could not create object data streams!", true, 0);
			Main.logger.printError(e.getStackTrace().toString(), true, 2);
			// e.printStackTrace();
			status = false;
		} 
		
		if(status)
			Main.logger.printInfo("Object I/O streams created", true, 1);
		
		// add this instance to the client list 
		clientList.add(this);
	}
	
	// Finalizer that is called before garbage collection
	@Override
	public void finalize()
	{
		// close the i/o streams
		try {
			if(this.objIn != null) {
				this.objIn.close();
			}
			if(this.objOut != null) {
				this.objOut.close();
			}
		} catch (IOException e) { 
			Main.logger.printWarning("Could not close the object data streams properly", true, 1);
		}
		
		// close the associated client socket
		try {
			if(this.clientSocket != null && this.clientSocket.isConnected()) {
				this.clientSocket.close();
			}
		} catch (IOException e) {
			Main.logger.printWarning("Could not close client socket properly", true, 1);
		}
	}
	
	// Thread function that runs simultanenious
	@Override
	public void run()
	{
		Main.logger.printInfo("Client handler thread running", true, 1);
		
		while(!this.stopOrder)
		{
			// Check if the connection is still alive
			if(!this.clientSocket.isConnected() || this.clientSocket.isClosed() ||
					this.clientSocket.isInputShutdown() || this.clientSocket.isOutputShutdown()) 
			{
				break;
			}
			
			// Check the input stream for incoming network messages
			GenericMessage recvBuffer = null;
			
			try {
				recvBuffer = (GenericMessage) this.objIn.readObject();
			} catch (ClassNotFoundException e) {
				Main.logger.printWarning("Failed to parse incoming message", true, 0);
				continue;
			} catch (Exception e) {
				// Main.logger.printWarning("Unhandled Exception thrown while parsing incoming message!", true, 2);
				continue;
			}
			
			// Now handle and process the received message
			// ...
		}
		
		// Finalize this instance
		this.finalize();
		
		Main.logger.printInfo("Client has closed the connection", true, 0);
		
		Main.logger.printInfo("Client handler thread finished", true, 1);
	}
	
	// Method for sending messages to this individual client
	public void sendMessageToClient(GenericMessage networkMessage) 
	{
		// Write the serializable object to the stream pipe
		try {
			this.objOut.writeObject(networkMessage);
		} catch (IOException e) {
			Main.logger.printWarning("Failed to send message to client (Serialization Error)", true, 0);
		}
		
		return;
	}
	
	// Method for sending broadcast messages to all conected clients
	public static void broadcastMessage(GenericMessage networkMessage)
	{
		// Loop through the list and send the message to everyone
		for(int i = 0; i < clientList.size(); i++)
		{
			clientList.get(i).sendMessageToClient(networkMessage);
		}
	}
	
	// Public method that waits until all running client threads have finished their work
	public static void closeHandlerThreads() 
	{
		Main.logger.printInfo("Closing client handler threads ... ", true, 1);
		
		// Loop through the client list and close all threads
		for(ClientConnection cc: clientList)
		{
			// Instruct the handler thread to close and wait until it's done
			if(cc != null && cc.isAlive()) 
			{
				cc.stopOrder = true;
				while(cc.isAlive()) {}
			}
		}
		
		// Empty the whole client list
		clientList.clear();
	}
}
