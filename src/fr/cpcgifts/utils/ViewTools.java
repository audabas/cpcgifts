package fr.cpcgifts.utils;

import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;

import fr.cpcgifts.model.Comment;
import fr.cpcgifts.model.CpcUser;
import fr.cpcgifts.model.Giveaway;
import fr.cpcgifts.persistance.CpcUserPersistance;

public class ViewTools {

	private static final Logger log = Logger.getLogger(ViewTools.class.getName());
	
	public static String userView(CpcUser u) {

		String res = "<div class=\"row\">"
				+ "<div class=\"span1\"><a href=\"/user?userID="
				+ u.getKey().getId()
				+ "\"><img alt=\"Avatar\" class=\"img-rounded img-small-avatar lazy\" src=\"img/avatar.jpg\" data-original=\""
				+ u.getAvatarUrl() + "\" /></a></div>"
				+ "<div class=\"span3\"><a  href=\"/user?userID="
				+ u.getKey().getId() + "\">" + u.getCpcNickname()
				+ "</a></div>" + "</div>";

		return res;
	}

	public static String gaView(Giveaway ga) {
		CpcUser auth = null;
		
		try {
			auth = CpcUserPersistance.getUserFromCache(ga.getAuthor());
		} catch (JDOObjectNotFoundException e) {
			log.severe("ViewTools : author not found : " + ga.getAuthor().getId() + " on giveaway " + ga.getKey().getId());
			throw e;
		}

		StringBuilder res = new StringBuilder(); 
				res.append("<div class=\"row-fluid\">" + "<div class=\"span2\">"
				+ "<a href=\"/giveaway?gaID="
				+ ga.getKey().getId()
				+ "\" ><img alt=\"Game Image\" class=\"img-rounded img-small-ga lazy\" src=\"img/game.png\" data-original=\""
				+ ga.getImgUrl()
				+ "\"></a>"
				+ "</div>"
				+ "<div class=\"span8 offset1\">"
				+ "<div class=\"row\">"
				+ "<h2 class='span8'><a href=\"/giveaway?gaID="
				+ ga.getKey().getId()
				+ "\" >"
				+ ga.getTitle());
				
		if(ga.getNbCopies() > 1) {
			res.append(" <span class=\"gray \">(" + ga.getNbCopies() + " copies)</span>");
		}
		
		res.append("</a></h2>"
				+ "<div class=\"offset7\">\n"
				+ "<div class=\"media\">\n"
				+ "<a class=\"pull-left\" "
				+ "href=\"/user?userID="
				+ auth.getKey().getId()
				+ "\"> <img alt=\"Avatar Image\" "
				+ "class=\"media-object img-small-avatar lazy\" src=\"img/avatar.jpg\" "
				+ "data-original=\""
				+ auth.getAvatarUrl()
				+ "\">"
				+ "</a>\n"
				+ "<div class=\"media-body\">\n"
				+ "<h4 class=\"media-heading\">"
				+ "<a href=\"/user?userID="
				+ auth.getKey().getId()
				+ "\">"
				+ auth.getCpcNickname()
				+ "</a>"
				+ "</h4>\n"
				+ "</div>\n"
				+ "</div>\n </div>\n </div>\n");

		res.append("<div class=\"row\">\n");
		
		res.append("<img alt=\"Clock\" class=\"img-small-icon\" src=\"img/clock.png\" /> \n");
		
		if(ga.isOpen()) {
			res.append(" Ouvert encore ");
		} else {
			res.append(" Fermé depuis ");
		}
		
		res.append(DateTools.dateDifference(ga.getEndDate()));

		res.append("</div>\n" + "</div>\n" + "</div>\n");

		return res.toString();
	}

	public static String gaCarouselView(Giveaway ga, boolean active) {

		String res = "<div class=\"item";

		if (active) {
			res += " active";
		}

		res += "\">";

		res += "<a href=\"/giveaway?gaID=" + ga.getKey().getId() + "\" >"
				+ "<img alt=\"Steam game image\" class=\"img-steam-game\" src=\"" + ga.getImgUrl()
				+ "\" alt=\"\">" + "</a>" + "<div class=\"carousel-caption\">"
				+ "<h4>" + ga.getTitle() + "</h4>" + "<p>";
		if(ga.getDescription().length() < 150) {
			res += ga.getDescription();
		} else {
			res += ga.getDescription().substring(0, 150) + " ...";
		}
		res += "</p>" + "</div>" + "</div>";

		return res;
	}

	public static String commentView(Comment c) {
		CpcUser u = null;
		
		try {
			u = CpcUserPersistance.getUserFromCache(c.getAuthor());
		} catch (JDOObjectNotFoundException e) {
			log.severe("ViewTools : author not found : " + c.getAuthor().getId() + " on comment " + c.getKey().getId() + " from giveaway " + c.getGiveaway().getId());
			throw e;
		}
		

		String res = "<div class='media'>";
		res += "<a class='pull-left' href='/user?userID=" + u.getKey().getId()
				+ "'>" + "<img alt=\"Avatar\" class='media-object img-small-avatar' src='"
				+ u.getAvatarUrl() + "'>" + "</a>";
		res += "<div class='media-body'>";
		res += "<h6 class='media-heading'>" + "<a href='/user?userID="
				+ u.getKey().getId() + "'>" + u.getCpcNickname() + "</a>"
				+ " <small>(il y a " + DateTools.dateDifference(c.getCommentDate()) + ")</small></h6>";
		res += "<textarea class='hidden' id='comment-" + c.getKey().getId() + "'>" + c.getCommentText() + "</textarea>";
		res += "<p class='' id='comment-" + c.getKey().getId() + "-display'></p>\n";
		res += "</div>"; // /media-body
		res += "</div>"; // /media
		return res;
	}

}
