package serverPackage;

/*
 * written by Elias Geiger
 * 
 * This Class is reponsible for all the message handling and is only used
 * in a static context. The handleMessage() Function is called from the clientHandler threads
 * everytime a new message is received
 * 
 */

import networking.*;

public class MessageHandler {

	public static void handleMessage(GenericMessage msg, ClientConnection sender) 
	{
		if(sender == null) {
			return;
		}
		
		// First read the type of the message
		int type = msg.getMessageID();
		
		// Distinguish between all different types of messages
		switch(type)
		{
			default:
			{
				Main.logger.printWarning("Message of unknown type", true, 0);
				break;
			}
		}
	}

}
