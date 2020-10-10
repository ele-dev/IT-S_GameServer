package serverPackage;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Mailer {
	
	// Mail configs
	private static final String senderMail = "noreply@ele-dev.de";
	private static final String host = "mail.ele-dev.de";
	private static final String password = "01022002";
	
	public static boolean sendConfirmationMail(String recipient) 
	{
		// Get the session object
		Properties props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.auth", "true");
		Session session = Session.getInstance(props,
				new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(senderMail, password);
			}
		});
		
		// Compose the message
		MimeMessage message = new MimeMessage(session);
		InternetAddress sender = null;
		try {
			 sender = new InternetAddress(senderMail);
		} catch (AddressException e) {
			e.printStackTrace();
			return false;
		}
		
		InternetAddress receiver = null;
		try {
			receiver = new InternetAddress(recipient);
		} catch (AddressException e) {
			e.printStackTrace();
			return false;
		}
		
		try {
			message.setFrom(sender);
		} catch (MessagingException e1) {
			e1.printStackTrace();
			return false;
		}
		
		try {
			message.addRecipient(Message.RecipientType.TO, receiver);
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		}
		
		// Set the subject
		try {
			message.setSubject("Account Confirmation");
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		}
		
		// set the content of the email
		try {
			message.setText("");
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		}
		
		// Send the email
		try {
			Transport.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		}
		
		System.out.println("Sent out e-mail to " + recipient);
		
		return false;
	}
}
