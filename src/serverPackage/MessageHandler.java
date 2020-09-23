package serverPackage;

import game.GameState;

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
			case GenericMessage.MSG_UPDATED_FIELD_STATE:
			{
				// Coerce the message into the right format
				MsgFieldState fieldMessage = (MsgFieldState) msg;
				
				// Validate the correctness of the received field by comparing it to the last known field state
				// ...
				
				// Apply updated field and share changes with all clients
				GameState.updateField(fieldMessage.getField());
				ClientConnection.broadcastMessage(fieldMessage);
				
				// Switch the acting team
				GameState.switchActingTeam();
				
				break;
			}
		
			default:
			{
				Main.logger.printWarning("Message of unknown type", true, 0);
				break;
			}
		}
	}

}
