package fr.cpcgifts.task;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.cpcgifts.utils.Constants;

@SuppressWarnings("serial")
public class SendMail extends HttpServlet {

	private static final Logger log = Logger.getLogger(SendMail.class.getName());
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		
		String from = Constants.DEFAULT_CONTACT_EMAIL;
		String fromPersonal = Constants.DEFAULT_CONTACT_PERSONAL;
		String to = req.getParameter("to");
		String toPersonal = req.getParameter("to_personal");
		String subject = req.getParameter("subject");
		String body = req.getParameter("body");
		
		try {
		    MimeMessage msg = new MimeMessage(session);
		    msg.setFrom(new InternetAddress(from, fromPersonal));
		    msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to, toPersonal));
		    msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(from, fromPersonal));
		    msg.setSubject(subject,"utf-8");
		    msg.setText(body,"utf-8", "html");
		    Transport.send(msg);

		} catch (AddressException e) {
		    log.severe("Send mail address exception : " + e.getMessage());
		} catch (MessagingException e) {
		    log.severe("Send mail messaging exception : " + e.getMessage());
		}
		
	}
	
}
