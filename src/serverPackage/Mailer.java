package serverPackage;

/*
 * written by Elias Geiger
 * 
 * This class has contains a static method for sending a verification email to a new user
 * This mail contains a weblink redirects to the verifcation backend (PHP7).
 * When the user opens the link from his mailbox, his game account will become activated
 * 
 */

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Mailer {
	
	// config variables
	private static String smtpsPort = "465";
	private static final String webLink = "https://www.ele-server.de/online-game/confirm-registration.php?verification-key=";
	
	public static boolean sendVerificationMailTo(String receiver, String verificationKey) {
		
		MimeMessage message = null;
		Properties props = null;
		Session session = null;
		
		// Create the properties 
		props = new  Properties();
		// props.put("mail.debug", "true");
		props.put("mail.smtp.host", GameConfigs.smtpServer);
		props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", smtpsPort);
		
		// Get the session object
		session = Session.getDefaultInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(GameConfigs.noreplyMailAddress, GameConfigs.noreplyMailPassword);
			}
		});
		
		if(GameConfigs.logLevel.equals("detailed")) {
			session.setDebug(true);
		}
		
		// Define content of the mail
		String mailText = "Your account registration is almost complete.\n"
				+ "Just click the following Link to complete the registration of your account. \n"
				+ "Verification Link: " + webLink + verificationKey;
		
		// Compose the message
		try {
			message = new MimeMessage(session);
			message.setFrom(new InternetAddress(GameConfigs.noreplyMailAddress));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
			message.setSubject("Account Verification");
			message.setText(mailText);
		} catch(Exception e) {
			e.printStackTrace();
			System.err.println("Error while composing message");
			return false;
		}
		
		// Send the email
		try {
			Transport.send(message);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error while sending mail!");
			return false;
		}
		
		return true;
	}
	
}
