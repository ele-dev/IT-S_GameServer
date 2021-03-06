package serverPackage;

/*
 * written by Elias Geiger
 * 
 * This class is the endpoint of one client connected to the server
 * The game server works with TCP/IP connections and has therefore 
 * one Socket and one separate Thread per client connection to handle.
 * It means every time the server socket accepts a connection request, a new client handler
 * thread is launched.
 * 
 * These handler threads do nothing more that waiting for messages and handling them
 * as soon as they arrive. There are two possible cases in which a handler thread stop its execution
 * First: The client sends a logout message and goes offline 
 * Second: A server admin closes the game server application and all active ClientConnections and their threads are terminated
 * 
 * The class uses a static ArrayList to keep track of all the existing class instances during execution  
 * 
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import networking.*;
import game.Player;

public class ClientConnection extends Thread {
	
	// --- static class members --- // 
	private static ArrayList<ClientConnection> clientList = new ArrayList<>();
	private static int threadCounter = 0;
	
	// --- non-static members --- //
	private Socket clientSocket = null;
	private ObjectOutputStream objOut = null;
	private ObjectInputStream objIn = null;
	
	// Thread status indicator
	private boolean stopOrder;
	
	// Login status of the client
	private boolean loggedIn;
	public Player playerInstance;
	
	// Constructor
	public ClientConnection(Socket socket)
	{
		super();
		
		// init values
		super.setName("ClientHandlerThread-" + threadCounter);
		threadCounter++;
		this.clientSocket = socket;
		this.stopOrder = false;
		this.loggedIn = false;
		this.playerInstance = null;
		
		// Set the timeout for the client socket
		try {
			this.clientSocket.setSoTimeout(3000);
		} catch (SocketException e1) {
			Main.logger.printWarning("Failed to set timeout for client socket", true, 1);
			e1.printStackTrace();
		}
		
		// create and prepare the i/o streams for data serialization
		boolean status = true;
		try {
			this.objOut = new ObjectOutputStream(this.clientSocket.getOutputStream());
			this.objOut.flush();
			this.objIn = new ObjectInputStream(this.clientSocket.getInputStream());
		} catch (Exception e) {
			Main.logger.printError("Could not create object data streams!", true, 0);
			Main.logger.printError(e.getStackTrace().toString(), true, 2);
			status = false;
		} 
		
		if(status) {
			Main.logger.printInfo("Object I/O streams created", true, 1);
		}
		
		// add this instance to the client list 
		clientList.add(this);
	}
	
	// Finalizer that is called before garbage collection
	@Override
	public void finalize()
	{
		// Remove this instance from the client list first to avoid
		// problems caused by server broadcasts
		clientList.remove(this);
		
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
		
		// Set the player to offline in the database too
		if(this.loggedIn && this.playerInstance != null) {
			this.playerInstance.logout();
			this.loggedIn = false;
		}
	}
	
	// Thread function that runs simultaneous
	@Override
	public void run()
	{
		Main.logger.printInfo(Thread.currentThread().getName() + " running", true, 1);
		
		while(!this.stopOrder)
		{
			// Initialize an empty object for storing a message of any kind
			GenericMessage recvBuffer = null;
		
			// Check the input stream for incoming network messages
			try {
				recvBuffer = (GenericMessage) this.objIn.readObject();
			} catch (ClassNotFoundException e) {
				Main.logger.printWarning(e.getMessage(), true, 2);
				Main.logger.printWarning("Class Not Found Exception thrown", false, 1);
				Main.logger.printWarning("Failed to parse incoming message", true, 0);
				continue;
			} catch(SocketTimeoutException e1) {
				Main.logger.printInfo("Client Socket Timeout exception thrown while reading", false, 2);
				continue;
			} catch (StreamCorruptedException e2) {
				Main.logger.printWarning("Stream corrupted exception thrown while reading", true, 0);
				break;
			} catch(IOException e3) {
				Main.logger.printWarning("IOException thrown while reading", true, 2);
				break;
			} catch (Exception e4) {
				Main.logger.printWarning("Unhandled Exception thrown while parsing incoming message!", true, 2);
				break;
			}
			
			// Now handle and process the received message
			MessageHandler.handleMessage(recvBuffer, this);
		}
		
		// Finalize this instance
		this.finalize();
		
		Main.logger.printInfo("Client has closed the connection", true, 0);
		
		Main.logger.printInfo("Client handler thread finished", true, 1);
	}
	
	// Method for sending messages to this individual client
	public void sendMessageToClient(GenericMessage networkMessage) 
	{
		// Make sure the connection to the client is alive before attempt to send something
		if(this.clientSocket.isClosed()) {
			return;
		}
		
		// Write the serializable object to the stream pipe
		try {
			this.objOut.writeObject(networkMessage);
		} catch (IOException e) {
			Main.logger.printWarning("Failed to send message to client (Serialization Error)", true, 0);
		}
		
		return;
	}
	
	// Method for sending broadcast messages to all connected clients
	public static void broadcastMessage(GenericMessage networkMessage, boolean loggedInOnly)
	{	
		// Loop through the list and send the message to everyone
		for(int i = 0; i < clientList.size(); i++)
		{
			if(loggedInOnly) 
			{
				if(clientList.get(i) != null && clientList.get(i).isLoggedIn()) {
					clientList.get(i).sendMessageToClient(networkMessage);
				}
			}
			else 
			{
				if(clientList.get(i) != null) {
					clientList.get(i).sendMessageToClient(networkMessage);
				}
			}
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
	
	// static helper method that prints a list with info about all connected clients
	public static void printClientInfo() 
	{
		for(ClientConnection cc: clientList)
		{
			// Avoid invalid entries from the list
			if(cc != null && cc.isAlive())
			{
				String ipStr = cc.clientSocket.getRemoteSocketAddress().toString();
				ipStr = ipStr.replace('/', ' ');
				String playerStr = cc.playerInstance == null ? " <not logged in>" : cc.playerInstance.getName();
				System.out.println("   Playername: " + playerStr + "   Remote Endpoint:" + ipStr);
			}
		}
	}
	
	// static method for retrieving the current online player count (->logged in players only)
	public static int getOnlinePlayerCount() 
	{
		int count = 0;
		
		// Go through the global client list, only count authentificated clients
		for(ClientConnection cc: clientList) 
		{
			if(cc != null && cc.isLoggedIn() && !cc.clientSocket.isClosed()) {
				count++;
			}
		}
		
		return count;
	}
	
	// Getters
	public boolean isLoggedIn() 
	{
		return this.loggedIn;
	}
	
	// Setters
	public void setLoginStatus(boolean status) 
	{
		this.loggedIn = status;
	}
}
