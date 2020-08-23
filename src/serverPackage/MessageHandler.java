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
		
		switch(type)
		{
			case GenericMessage.MSG_LOGIN:
			{
				// Parse the generic message in a more specific format
				MsgLogin loginMsg = (MsgLogin) msg;
				// ...
				
				// Answer with a login status message
				MsgLoginStatus response = new MsgLoginStatus(true);
				sender.sendMessageToClient(response);
				
				break;
			}
			
			// ...
		
			default:
			{
				Main.logger.printWarning("Message of unknown type", true, 0);
				break;
			}
		}
	}

}
