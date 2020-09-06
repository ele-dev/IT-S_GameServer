package serverPackage;

/*
 * written by Elias Geiger
 * 
 * This Class is reponsible for all the message handling and is only used
 * in a static context. The handleMessage() Function is called from the clientHandler threads
 * everytime a new message is received
 * 
 */

import java.sql.SQLException;

import game.Player;
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
			case GenericMessage.MSG_LOGIN:
			{
				Main.logger.printInfo("Received login message", false, 2);
				
				// Parse the generic message in a more specific format
				MsgLogin loginMsg = (MsgLogin) msg;
				
				// Important variables for login procedure
				boolean status = true;
				String guestPlayerName = "";
				
				// Check if it's a guest or an account player who wants to login
				// And handle the two cases differently
				if(loginMsg.isGuest()) 
				{
					// Generate a new unique guest player name 
					guestPlayerName = "guest-" + Player.nextGuestId;
					Player.nextGuestId++;
					
					// Also update the database with new player information
					try {
						Main.database.loginGuest(guestPlayerName);
					} catch(SQLException e) {
						status = false;
					}
					
					// Respond with a login status message that contains the assigned guest player name additionally
					MsgLoginStatus response = new MsgLoginStatus(status, guestPlayerName);
					sender.sendMessageToClient(response);
					
					if(status) {
						sender.setLoginStatus(true);
						sender.playerInstance = new Player(guestPlayerName);
						Main.logger.printInfo("Guest login successfull", true, 0);
					} else {
						Main.logger.printInfo("Guest login failed", true, 0);
					}
				}
				else 
				{
					// intialize the status as false for this case
					status = false;
					
					// Validate the login request by checking the credentials in the database
					try {
						status = Main.database.loginPlayer(loginMsg.getUsername(), loginMsg.getPasswordHash());
					} catch(SQLException e) {}
					
					// Respond with a login status message 
					MsgLoginStatus response = new MsgLoginStatus(status);
					sender.sendMessageToClient(response);
					
					// Print the result status of the login to the console
					if(status) {
						sender.setLoginStatus(true);
						sender.playerInstance = new Player(loginMsg.getUsername());
						Main.logger.printInfo("Player authentification successfull", true, 0);
					} else {
						Main.logger.printInfo("Player authentification failed!", true, 0);
						break;
					}
					
					// After successfull authentification send a player stats message to the client
					int playedMatches = 0;
					int accountBalance = 0;
					try {
						playedMatches = Main.database.getPlayerAttributeInt("playedMatches", loginMsg.getUsername());
						accountBalance = Main.database.getPlayerAttributeInt("accountBalance", loginMsg.getUsername());
					} catch (SQLException e) {
						Main.logger.printWarning("Exception thrown during SQL Query", true, 1);
					}
					
					// Create and send the message
					MsgAccountStats statsMsg = new MsgAccountStats(playedMatches, accountBalance);
					sender.sendMessageToClient(statsMsg);
					Main.logger.printInfo("Sent Account Stats to the newly logged in player", true, 1);
				}
				
				break;
			}
			
			case GenericMessage.MSG_LOGOUT:
			{
				// Ignore messages from unauthentificated clients
				if(!sender.isLoggedIn()) {
					Main.logger.printWarning("Received message from unauthentificated client!", true, 1);
					break;
				}
				
				sender.playerInstance.logout();
				
				Main.logger.printInfo("Received logout message from " + sender.getName(), false, 0);
				sender.setLoginStatus(false);
				
				break;
			}
			
			case GenericMessage.MSG_KEEP_ALIVE:
			{
				// Ignore messages from unauthentificated clients
				if(!sender.isLoggedIn()) {
					Main.logger.printWarning("Received message from unauthentificated client!", true, 1);
					break;
				}
				
				Main.logger.printInfo("Received keep alive message", false, 2);
				
				break;
			}
			
			case GenericMessage.MSG_JOIN_QUICKMATCH:
			{
				// Ignore messages from unauthentificated clients
				if(!sender.isLoggedIn()) {
					Main.logger.printWarning("Received message from unauthentificated client!", true, 1);
					break;
				}
				
				// First update the players state 
				sender.playerInstance.setState("searching");
				Main.logger.printInfo(sender.getName() + " wants to play a quick match --> searching second player", true, 0);
				
				// Look in the quick match waiting queue for an enemy
				Player potentialEnemy = Player.getWaitingPlayer();
				
				// If there is no other player currently searching an enemy, put the request sender on the queue
				if(potentialEnemy == null) {
					Player.putPlayerOnWaitingSlot(sender.playerInstance);
					Main.logger.printInfo("Put " + sender.getName() + " on the waiting slot", true, 1);
				} else {
					// Update the players states
					sender.playerInstance.setState("playing");
					potentialEnemy.setState("playing");
					
					// Create a new Match with the two players and inform them 
					// ...
					Main.logger.printInfo("Starting new match: " + sender.getName() + " vs " + potentialEnemy.getName(), true, 0);
				}
				
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
