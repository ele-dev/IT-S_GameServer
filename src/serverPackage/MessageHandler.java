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
			case GenericMessage.MSG_JOIN_TEAM:
			{
				// A client can only join a team once, so the message must come from an unassigned player
				if(sender.getTeam() != 0) {
					break;
				}
				
				// Coerce the message into the right format
				MsgJoinTeam teamJoinMessage = (MsgJoinTeam) msg;
				
				// Assign the client to the desired team
				sender.assignTeam(teamJoinMessage.getTeam());
				
				break;
			}
		
			case GenericMessage.MSG_UPDATED_FIELD_STATE:
			{
				// Only handle message if the game hasn't finished yet
				if(GameState.isGameOver()) {
					break;
				}
				
				// Coerce the message into the right format
				MsgFieldState fieldMessage = (MsgFieldState) msg;
				
				Main.logger.printInfo("received updated field from player", true, 0);
				
				// Validate the correctness of the received field by comparing it to the last known field state
				// ...
				
				// Apply updated field and share changes with all clients
				GameState.updateField(fieldMessage.getField());
				ClientConnection.broadcastMessage(fieldMessage);
				Main.logger.printInfo("Applied updated field", true, 0);
				
				// Now check if the match is over or not (--> winner detection and full field detection combined)
				GameState.checkForGameOver();
				
				// Switch the acting team and inform all clients about it
				GameState.switchActingTeam();
				MsgSetTurn turingTeamMessage = new MsgSetTurn(GameState.getActingTeam());
				ClientConnection.broadcastMessage(turingTeamMessage);
				Main.logger.printInfo("Switch acting team --> It is " + GameState.getActingTeam() + " turn", true, 0);
				
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
