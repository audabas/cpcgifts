package fr.cpcgifts.utils;

import java.util.Collection;
import java.util.logging.Logger;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

import fr.cpcgifts.model.CpcUser;
import fr.cpcgifts.model.Giveaway;
import fr.cpcgifts.persistance.CpcUserPersistance;

public class SendEmailUtils {
	
	private static final Logger log = Logger.getLogger(SendEmailUtils.class.getName());
	
	public static void sendGiveawayFinishedEmail(Giveaway ga) {
		
		CpcUser author = CpcUserPersistance.getUserFromCache(ga.getAuthor());
		
		if(!author.isAcceptEmails()) {
			// si l'utilisateur n'accepte pas l'envoi d'email, on s'arrête ici.
			return;
		}
		
		Collection<CpcUser> winners = CpcUserPersistance.getAllFromCache(ga.getWinners()).values();
		
		String to = author.getGuser().getEmail();
		String toPersonal = author.getCpcNickname();
		String subject = "Concours « " + ga.getTitle() + " » terminé.";
		
		StringBuilder body = new StringBuilder(); 
				body.append("<body>\n" +
				"<p>\n" +
				"Bonjour " + author.getCpcNickname() + ",<br />\n" +
				"<br />\n" +
				"votre concours <a href='http://cpcgifts.appspot.com/giveaway?gaID=" + ga.getKey().getId() + "'>" + ga.getTitle() + "</a> vient de se terminer.<br />\n");
		
		if(winners.size() == 0) {
			body.append("Aucune personne n'a participé à ce concours.<br /><br />\n" +
					"Vous pouvez, si vous le souhaitez, remettre le lot en jeu en créant un nouveau concours.<br />\n" +
					"La page concernant ce concours sera supprimée automatiquement dans quelques semaines.</p>");
		} else if(winners.size() == 1) {
			CpcUser winner = (CpcUser) winners.toArray()[0];
			body.append("Le gagnant qui a été tiré au sort est : " +
					"<a href='http://cpcgifts.appspot.com/user?userID=" + winner.getKey().getId() + "'>" + winner.getCpcNickname() + "</a>.\n" +
					"</p>\n" );
			body.append("<p>\n" +
					"S'il ne respecte pas une des conditions indiquées sur " +
					"<a href='http://cpcgifts.appspot.com/giveaway?gaID=" + ga.getKey().getId() + "'>la page du concours</a>, " +
					"vous pouvez demander la désignation d'un nouveau gagnant en utilisant le formulaire présent sur la page du concours.\n" +
					"</p>\n");
		} else {
			body.append("Les gagnants qui ont été tirés au sort sont : \n");
			
			body.append("<ul>\n");
			for(CpcUser winner : winners) {
				body.append("<li><a href='http://cpcgifts.appspot.com/user?userID=" + winner.getKey().getId() + "'>" + winner.getCpcNickname() + "</a></li>\n");
			}
			body.append("</ul>\n");
			
			body.append("<p>\n" +
					"Si l'un d'eux ne respecte pas une des conditions indiquées sur " +
					"<a href='http://cpcgifts.appspot.com/giveaway?gaID=" + ga.getKey().getId() + "'>la page du concours</a>, " +
					"vous pouvez demander la désignation d'un nouveau gagnant en utilisant le formulaire présent sur la page du concours.\n" +
					"</p>\n");
		}
		
		body.append("<p>\n" +
				"Cordialement,<br />\n" +
				"L'équipe cpcgifts.appspot.com\n" +
				"</p>\n");
		
		body.append("</p>\n" +
				"<p>***************</p>\n" +
				"<p>\n" +
				"Si vous ne souhaitez plus recevoir d'emails en provenance de CPC Gifts, rendez sur la page de " +
				"<a href='http://cpcgifts.appspot.com/user?userID=" + author.getKey().getId() + "'>votre profil</a> " +
				"pour désactiver l'envoi d'emails." +
				"</p>\n");
		
		body.append("</body>");
		
		Queue queue = QueueFactory.getQueue("mail-queue");
		queue.add(TaskOptions.Builder.withUrl("/task/sendmail").param("to",	to).param("to_personal", toPersonal).param("subject", subject).param("body", body.toString()));
		
		log.info("Mail sent to : " + toPersonal + " [" + to + "] : " + subject + "\n*****\n" + body);
	}

}
