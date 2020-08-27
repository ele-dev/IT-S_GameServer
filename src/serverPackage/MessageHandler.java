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
				
				boolean status = false;
				String guestPlayerName = "";
				
				// Check if it's a guest or an account player who wants to login
				// And handle the two cases differently
				if(loginMsg.isGuest()) 
				{
					// Generate a new unique guest player name 
					guestPlayerName = "guest-01";
					
					// Also update the database with new player information
					try {
						Main.database.loginGuest(guestPlayerName);
					} catch(SQLException e) {
						status = false;
					}
					
					// Respond with a login status message that contains the assigned guest player name additionally
					MsgLoginStatus response = new MsgLoginStatus(status, guestPlayerName);
					sender.sendMessageToClient(response);
				}
				else 
				{
					// Validate the login request by checking the credentials in the database
					try {
						status = Main.database.loginPlayer(loginMsg.getUsername(), loginMsg.getPasswordHash());
					} catch(SQLException e) {}
					
					// Respond with a login status message 
					MsgLoginStatus response = new MsgLoginStatus(status);
					sender.sendMessageToClient(response);
				}
				
				
				// Also print the result of the login attempt to the server console
				if(status) {
					sender.setLoginStatus(true);
					if(loginMsg.isGuest()) {
						sender.playerInstance = new Player(guestPlayerName);
					} else {
						sender.playerInstance = new Player(loginMsg.getUsername());
					}
					Main.logger.printInfo("Client authentification successfull", true, 0);
				} else {
					Main.logger.printInfo("Client authentification failed!", true, 0);
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
						Main.database.loginGuest(playerName);
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
