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
					Main.logger.printWarning("No team switching possible!", true, 0);
					break;
				}
				
				// Coerce the message into the right format
				MsgJoinTeam teamJoinMessage = (MsgJoinTeam) msg;
				
				// Assign the client to the desired team
				sender.assignTeam(teamJoinMessage.getTeam());
				Main.logger.printInfo("Client joined team " + teamJoinMessage.getTeam(), true, 0);
				
				break;
			}
		
			case GenericMessage.MSG_UPDATED_FIELD_STATE:
			{
				// Only handle message if the game hasn't finished yet
				if(GameState.isGameOver()) {
					break;
				}
				
				// Don't handle messages from the team that isn't allowed to make a move at the moment
				if(sender.getTeam() != GameState.getActingTeam()) {
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
				MsgSetTurn turingTeamMessage = new MsgSetTurn(GameState.getActingTeamStr());
				ClientConnection.broadcastMessage(turingTeamMessage);
				Main.logger.printInfo("Switch acting team --> It is " + GameState.getActingTeamStr() + " turn", true, 0);
				
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
