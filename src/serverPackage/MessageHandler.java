package serverPackage;

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
				
				// Important variable or login procedure
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
					String matchesStr = "0";
					String balanceStr = "0";
					try {
						matchesStr = Main.database.getPlayerAttribute("playedMatches", loginMsg.getUsername());
						balanceStr = Main.database.getPlayerAttribute("accountBalance", loginMsg.getUsername());
					} catch (SQLException e) {
						Main.logger.printWarning("Exception thrown during SQL Query", true, 2);
					}
					int playedMatches = Integer.parseInt(matchesStr);
					int accoutBalance = Integer.parseInt(balanceStr);
					
					MsgAccountStats statsMsg = new MsgAccountStats(playedMatches, accoutBalance);
					sender.sendMessageToClient(statsMsg);
					Main.logger.printInfo("Sent Account Stats to the new logged in player", true, 1);
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
				
				// Get the playername of the sender of this message
				String playerName = sender.playerInstance.getName();
				boolean isGuest = playerName.contains("guest");
				
				// Handle the logout message differently for guest and accounts
				if(isGuest) 
				{
					// Logout the guest player
					try {
						Main.database.logoutGuest(playerName);
					} catch(SQLException e) {}
				}
				else
				{
					// Logout the registered player
					try {
						Main.database.logoutPlayer(playerName);
					} catch (SQLException e) {}
				}
				
				Main.logger.printInfo("Received logout message from " + playerName, false, 0);
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
				
				Main.logger.printInfo("Received logout keep alive message", false, 2);
				
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
