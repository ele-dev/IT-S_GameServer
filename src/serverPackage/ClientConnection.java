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
	
	// Constructor
	public ClientConnection(Socket socket)
	{
		// init values
		super("ClientHandlerThread");
		this.clientSocket = socket;
		
		// create and prepare the i/o streams for data conversion
		try {
			this.objOut = new ObjectOutputStream(this.clientSocket.getOutputStream());
			this.objIn = new ObjectInputStream(this.clientSocket.getInputStream());
		} catch (IOException e) {
			System.err.println("[Error]: Exception thrown while creating data streams!");
		}
		
		// add this instance to the client list 
		clientList.add(this);
	}
	
	// Finalizer
	@Override
	public void finalize()
	{
		// close the i/o streams
		try {
			this.objIn.close();
			this.objOut.close();
		} catch (IOException e) {
			System.err.println("Exception thrown while closing Object I/O streams!");
		}
		
		// close the associated client socket
		try {
			this.clientSocket.close();
		} catch (IOException e) {
			System.err.println("Exception thrown while closing client socket!");
		}
	}
	
	// Thread function that runs simultanious
	public void run()
	{
		while(true)
		{
			// Check the input stream for incoming network messages
			GenericMessage recvBuffer = null;
			
			try {
				recvBuffer = (GenericMessage)this.objIn.readObject();
			} catch (ClassNotFoundException e) {
				System.err.println("[Error]: Failed to parse incoming message!");
				continue;
			} catch (Exception e) {
				System.err.println("[Error]: Exception while reading parsing message!");
				continue;
			}
			
			// Now handle and process the received message
			// ...
		}
		
	}
	
	// Method for sending messages to this individual client
	public void sendMessageToClient(GenericMessage networkMessage) 
	{
		// Write the serializable object to the stream pipe
		try {
			this.objOut.writeObject(networkMessage);
		} catch (IOException e) {
			e.printStackTrace();
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
}
