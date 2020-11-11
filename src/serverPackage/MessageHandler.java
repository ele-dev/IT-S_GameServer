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

import game.Match;
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
				// Ignore messages from clients that are already logged in
				if(sender.isLoggedIn()) {
					Main.logger.printWarning("Client is already logged in", true, 1);
					break;
				}
				
				Main.logger.printInfo("Received login message", false, 2);
				
				// Parse the generic message in a more specific format
				MsgLogin loginMsg = (MsgLogin) msg;
				
				// Important variables for login procedure
				boolean loginStatus = true;
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
						loginStatus = false;
					}
					
					// Respond with a login status message that contains the assigned guest player name additionally
					MsgLoginStatus response = new MsgLoginStatus(loginStatus, guestPlayerName);
					sender.sendMessageToClient(response);
					
					if(loginStatus) {
						sender.setLoginStatus(true);
						sender.playerInstance = new Player(guestPlayerName, sender);
						// sender.setName(guestPlayerName);
						Main.logger.printInfo("Guest login successfull", true, 0);
					} else {
						Main.logger.printInfo("Guest login failed", true, 0);
					}
				}
				else 
				{
					// intialize the status as false for this case
					loginStatus = false;
					boolean verifiedStatus = false;
					
					// Validate the login request by checking the credentials in the database
					try {
						loginStatus = Main.database.loginPlayer(loginMsg.getUsername(), loginMsg.getPasswordHash());
						verifiedStatus = Main.database.isAccountVerified(loginMsg.getUsername());
					} catch(SQLException e) {}
					
					// Respond with a login status message 
					MsgLoginStatus response = new MsgLoginStatus(loginStatus, verifiedStatus);
					sender.sendMessageToClient(response);
					
					// Print the result status of the login to the console
					if(loginStatus) {
						sender.setLoginStatus(true);
						sender.playerInstance = new Player(loginMsg.getUsername(), sender);
						Main.logger.printInfo("Player authentification successfull", true, 0);
					} else {
						Main.logger.printInfo("Player authentification failed!", true, 0);
						break;
					}
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
				
				// call the logout routine for the sender of this message
				sender.playerInstance.logout();
				
				// Delete the player instance of this client to prepare for secondary login
				String nameOfPlayer = sender.playerInstance.getName();
				sender.setLoginStatus(false);
				sender.playerInstance = null;
				
				// Send broadcast message MSG_GAME_DATA to the remaining players
				MsgGameData gameDataMsg = new MsgGameData(ClientConnection.getOnlinePlayerCount());
				ClientConnection.broadcastMessage(gameDataMsg, true);
				
				Main.logger.printInfo("Received logout message from " + nameOfPlayer, false, 0);
				sender.setLoginStatus(false);
				
				break;
			}
			
			case GenericMessage.MSG_REGISTER:
			{
				// Ignore messages from client that are currenly logged in (or guests)
				if(sender.isLoggedIn()) {
					Main.logger.printWarning("Received register message from client that is logged in at the moment", true, 1);
					break;
				}
				
				Main.logger.printInfo("Received an account registration request", true, 0);
				
				// Coerce the message into the right format
				MsgRegister registerMsg = (MsgRegister)msg;
				
				// Validate payload of the register request
				String statusDescription = "success";
				boolean status = true;
				
				// Check if the email address is free or already used by another account
				boolean mailUsed = false;
				try {
					mailUsed = Main.database.isEmailUsedAlready(registerMsg.getEmail());
					if(mailUsed) {
						status = false;
						statusDescription = "This email address is already used by another account";
						Main.logger.printWarning("Registration failed: " + registerMsg.getEmail() + " is already used by another account!", true, 0);
					}
				} catch (SQLException e2) {
					status = false;
					statusDescription = "error: database problems";
					Main.logger.printError("Registration process failed because of database problems!", true, 0);
				}
				
				// check if username is valid and free to use (not taken)
				String playername = registerMsg.getUsername();
				if(playername.contains("guest")) {
					status = false;
					statusDescription = "username can not contain 'guest'";
					Main.logger.printWarning("Playername '" + playername + "' is not allowed!", true, 0);
				}
				try {
					if(!Main.database.isNameAvailable(playername)) {
						status = false;
						statusDescription = "Sorry name already taken. Try another one";
						Main.logger.printWarning("The playername '" + playername + "' was already taken!", true, 0);
					}
				} catch (SQLException e) {
					status = false;
					statusDescription = "error: database problems";
					Main.logger.printError("Registration process failed because of database problems!", true, 0);
				}
				
				if(status) 
				{
					// Generate random but unique verification code for new account
					boolean getKey = true;
					String verifyKey = "";
					try {
						verifyKey = Main.database.getUniqueKey();
					} catch (SQLException e1) {
						getKey = false;
						status = false;
						statusDescription = "error: database problems";
						Main.logger.printError("Registration process failed because of database problems!", true, 0);
					}
					
					if(getKey)
					{
						// Create database table entries for new user account 
						String passwordHash = registerMsg.getPasswordHash();
						String email = registerMsg.getEmail();
						try {
							Main.database.registerNewAccount(playername, email, passwordHash, verifyKey);
						} catch (SQLException e) {
							e.printStackTrace();
							status = false;
							statusDescription = "error: database problems";
							Main.logger.printError("Registration process failed because of database problems!", true, 0);
						}
						
						// send mail with link Weblink to verification backend
						boolean result = Mailer.sendVerificationMailTo(registerMsg.getEmail(), verifyKey);
						if(!result) {
							status = false;
							statusDescription = "Type in a real email address!";
							Main.logger.printError("Registration process failed because of mail server issues!", true, 1);
						} else {
							Main.logger.printInfo("  --> New account '" + playername + "' has been registered successfully", true, 0);
						}
					}
				}
				
				// Respond with status message that contains success status and description
				MsgRegisterStatus response = new MsgRegisterStatus(status, statusDescription);
				sender.sendMessageToClient(response);
				
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
				Main.logger.printInfo(sender.playerInstance.getName() + " wants to play a quick match --> searching second player", true, 0);
				
				// Look in the quick match waiting queue for an enemy
				Player potentialEnemy = Player.getWaitingPlayer();
				
				// If there is no other player currently searching an enemy, put the request sender on the queue
				if(potentialEnemy == null) {
					Player.putPlayerOnWaitingSlot(sender.playerInstance);
					Main.logger.printInfo("Put " + sender.playerInstance.getName() + " on the waiting slot", true, 1);
				} else {
					// Update the players states
					sender.playerInstance.setState("playing");
					potentialEnemy.setState("playing");
					
					// Create a new Match with the two players and start it
					Match match = new Match(potentialEnemy, sender.playerInstance);
					match.beginMatch();
					
					Main.logger.printInfo("Starting new match: " + sender.playerInstance.getName() + " vs " + potentialEnemy.getName(), true, 0);
				}
				
				break;
			}
			
			case GenericMessage.MSG_ABORT_MATCH_SEARCH:
			{
				// Ignore messages from unauthentificated clients
				if(!sender.isLoggedIn() || sender.playerInstance == null) {
					Main.logger.printWarning("Received message from unauthentificated client!", true, 1);
					break;
				}
				
				// Call the function for aborting a players quick match search and store the result
				boolean isAborted = Player.abortWaiting(sender.playerInstance);
				
				// The result tells us if the abortion actually happended or if the request was invalid
				if(isAborted) {
					// Update the player state and output the message to the console
					sender.playerInstance.setState("homescreen");
					Main.logger.printInfo(sender.playerInstance.getName() + " aborted match search", true, 0);
				} else {
					// Output message about useless/misplaced match search abortion message
					Main.logger.printWarning("Invalid match search abortion request", true, 1);
				}
				
				break;
			}
			
			case GenericMessage.MSG_LEAVE_MATCH:
			{
				// Ignore messages from unauthentificated clients or players that aren't ingame
				if(!sender.isLoggedIn() || sender.playerInstance == null || !sender.playerInstance.getState().equals("playing")) {
					Main.logger.printWarning("Received invalid leave match message!", true, 1);
					break;
				}
				
				// Remove the player from his current match (surrender to the enemy)
				sender.playerInstance.leaveMatch();
				
				// Update the players state
				sender.playerInstance.setState("homescreen");
				
				break;
			}
		
			case GenericMessage.MSG_END_TURN:
			{
				// Ignore messages from clients that arent ingame at the moment
				if(!sender.isLoggedIn() || !sender.playerInstance.getState().equals("playing")) {
					Main.logger.printWarning("Received invalid end turn message from client!", true, 1);
					break;
				}
				
				// Attempt to switch the turns and store success result
				boolean result = sender.playerInstance.getMatch().switchTurn(sender.playerInstance);
				if(!result) {
					Main.logger.printWarning("The request for switching turns was invalid!", true, 1);
				}
				
				break;
			}
			
			case GenericMessage.MSG_MAKE_MOVE:
			{
				// Ignore messages from clients that aren't ingame or even logged in or arent allowed to act
				if(!sender.isLoggedIn() || !sender.playerInstance.getState().equals("playing")) {
					Main.logger.printWarning("Received invalid make move message from client!", true, 1);
					break;
				}
				
				// Coerce the message into the right format
				MsgMakeMove moveMsg = (MsgMakeMove) msg;
				
				// Forward move message to enemy client if all attributes are given
				if(moveMsg.getMovingPlayerPos() != null && moveMsg.getTargetField() != null) {
					// Send the message to the other client
					Player enemy = sender.playerInstance.getMatch().getEnemyOf(sender.playerInstance);
					if(enemy == null) {
						Main.logger.printError("Failed to forward move message from " + sender.playerInstance.getName() + " to his enemy!", true, 0);
						break;
					}
					
					enemy.sendMessage(moveMsg);
					Main.logger.printInfo("Forwarded move message from " + sender.playerInstance.getName() + " to " + enemy.getName(), true, 0);
 				}
				
				break;
			}
			
			case GenericMessage.MSG_ATTACK:
			{
				// Ignore messages from clients that aren't ingame or even logged in or arent allowed to act
				if(!sender.isLoggedIn() || !sender.playerInstance.getState().equals("playing")) {
					Main.logger.printWarning("Received invalid make move message from client!", true, 1);
					break;
				}
				
				// Coerce the message into the right format
				MsgAttack attackMsg = (MsgAttack) msg;
				
				// Forward attack message to the enemy client if all the attributes are given 
				if(attackMsg.getAttackerPiece() != null && attackMsg.getVicitimPos() != null) {
					// Send the message to the other client
					Player enemy = sender.playerInstance.getMatch().getEnemyOf(sender.playerInstance);
					if(enemy == null) {
						Main.logger.printError("Failed to forward attack message from " + sender.playerInstance.getName() + " to his enemy!", true, 0);
						break;
					}
					
					enemy.sendMessage(attackMsg);
					Main.logger.printInfo("Forwarded attack message from " + sender.playerInstance.getName() + " to " + enemy.getName(), true, 0);
				}
				
				break;
			}
			
			case GenericMessage.MSG_SPAWN_GAMEPIECE:
			{
				// Ignore messages from clients that aren't ingame at the moment
				if(!sender.isLoggedIn() || !sender.playerInstance.getState().equals("playing")) {
					Main.logger.printWarning("Received invalid spawn gamepiece message from client!", true, 1);
					break;
				}
				
				// Coerce the message into the right format
				MsgSpawnGamepiece spawnMsg = (MsgSpawnGamepiece) msg;
				
				// If all the attributes are given and valid then forward the message to the enemy client
				if(spawnMsg.getFieldCoordinates() != null && spawnMsg.getGamePieceClass() != null
						&& spawnMsg.getTeamColor() != null) {
					// Send the message to the other client
					Player enemy = sender.playerInstance.getMatch().getEnemyOf(sender.playerInstance);
					if(enemy == null) {
						Main.logger.printError("Failed to forward the message", true, 1);
						break;
					}
					
					enemy.sendMessage(spawnMsg);
					Main.logger.printInfo("Forwarded spawn gamepiece message from " + sender.playerInstance.getName() + " to " + enemy.getName(), true, 0);
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
