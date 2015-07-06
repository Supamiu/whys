package com.whys.utils;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.whys.data.User;
import com.whys.database.Neo4j;

public class MailEngine {

	private final static Neo4j neo = Neo4j.getInstance();
	private static final String SMTP_AUTH_USER = "noreply@whys.fr";
    private static final String SMTP_AUTH_PWD  = "ba4n6m1j";
	
	public static void sendNoreplyMail(String to, String subject, String content) {
		Properties props = new Properties();
		
		props.put("mail.smtp.host", "localhost");
		props.put("mail.smtp.starttls.enable", "false");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "25");
		
		Authenticator auth = new SMTPAuthenticator();
		
		Session session = Session.getDefaultInstance(props,auth);
 
		try {
			final Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("noreply@whys.fr"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(to));
			message.setSubject(subject);
			content += "<br/><br/><br/><div align='center'>----------------</div><br/><br/>Ce mail est envoyé automatiquement, personne ne vous répondra si vous envoyez un message sur cette adresse. <br><br> Pour nous répondre, attachez ce message par email à contact@whys.fr";
			message.setContent(content, "text/html; charset=UTF-8");
			message.saveChanges();
			final Transport transport = session.getTransport("smtp");
			new Thread(new Runnable() {
			    public void run() {
			    	try {
						Transport.send(message);
						transport.close();
					} catch (MessagingException e) {
						e.printStackTrace();
					}
			    	
			    }
			}).start();
			
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	public static void sendMailFromUser(String to, String subject, String content, String replyTo) {
		Properties props = new Properties();
		
		props.put("mail.smtp.host", "localhost");
		props.put("mail.smtp.starttls.enable", "false");
		props.put("mail.smtp.auth", "false");
		props.put("mail.smtp.port", "25");
		
		Session session = Session.getDefaultInstance(props);
 
		try {
			final Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("noreply@whys.fr"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(to));
			message.setReplyTo(new javax.mail.Address[]
					{
					    new javax.mail.internet.InternetAddress(replyTo)
					});
			message.setSubject(subject);
			content += "<br/><br/><br/><div align='center'>----------------</div><br/><br/>Ce mail est envoyé automatiquement, personne ne vous répondra si vous envoyez un message sur cette adresse. <br><br> Pour nous répondre, attachez ce message par email à contact@whys.fr";
			message.setContent(content, "text/html; charset=UTF-8");
			message.saveChanges();
			final Transport transport = session.getTransport("smtp");
			new Thread(new Runnable() {
			    public void run() {
			    	try {
						Transport.send(message);
						transport.close();
					} catch (MessagingException e) {
						e.printStackTrace();
					}
			    	
			    }
			}).start();
			
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}	
	
	public static void SendNewsLetter(String subject, String content){
		for(User u : neo.getUsers()){
			sendNoreplyMail(u.getEmail(), subject, content);
		}
	}
	
	private static class SMTPAuthenticator extends javax.mail.Authenticator {
        public PasswordAuthentication getPasswordAuthentication() {
           String username = SMTP_AUTH_USER;
           String password = SMTP_AUTH_PWD;
           return new PasswordAuthentication(username, password);
        }
    }
}