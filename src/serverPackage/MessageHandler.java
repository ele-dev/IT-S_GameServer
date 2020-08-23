package serverPackage;

import networking.GenericMessage;
import networking.MsgLogin;
import networking.MsgLoginStatus;

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
				
				// Validate the login request of the client
				boolean status = true;
				// ...
				
				// Respond with a login status message
				MsgLoginStatus response = new MsgLoginStatus(status);
				sender.sendMessageToClient(response);
				
				// Also print the result of the login attempt to the server console
				if(status) {
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
				
				Main.logger.printInfo("Received logout message", false, 2);
				
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
