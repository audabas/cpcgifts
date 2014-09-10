package fr.cpcgifts;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;

import fr.cpcgifts.model.Comment;
import fr.cpcgifts.model.CpcUser;
import fr.cpcgifts.model.Giveaway;
import fr.cpcgifts.persistance.CommentPersistance;
import fr.cpcgifts.persistance.CpcUserPersistance;
import fr.cpcgifts.persistance.GiveawayPersistance;
import fr.cpcgifts.utils.Constants;

@SuppressWarnings("serial")
public class AdminServlet extends HttpServlet {
	
	private static final Logger log = Logger.getLogger(AdminServlet.class.getName());
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		CpcUser cpcuser = null;
		if(user != null)
			cpcuser = CpcUserPersistance.getCpcUser(user.getUserId());
		if(cpcuser == null)
			resp.sendRedirect(userService.createLogoutURL("/"));

		if (user != null && cpcuser != null && userService.isUserAdmin()) {

			@SuppressWarnings("unchecked")
			Map<String, String[]> params = req.getParameterMap();
			
			String reqType = params.get("reqtype")[0];
			
			Giveaway ga = null;
			Key<Giveaway> gaKey = null;
			if (params.containsKey("gaid")) {
				String gaID = params.get("gaid")[0];
				gaKey = Key.create(Giveaway.class,Long.parseLong(gaID.trim()));
				try {
					ga = GiveawayPersistance.getGA(gaKey);
				} catch(Exception e) {}
			}
			
			CpcUser userToUpdate = null;
			Key<CpcUser> userToUpdateKey = null;
			if (params.containsKey("userid")) {
				String userID = params.get("userid")[0];
				userToUpdateKey = Key.create(CpcUser.class,Long.parseLong(userID.trim()));
				try {
					userToUpdate = CpcUserPersistance.getCpcUser(userToUpdateKey);
				} catch(Exception e) {}
			}
			
			Key<Comment> commentToUpdateKey = null;
			if (params.containsKey("commentid")) {
				String commentID = params.get("commentid")[0];
				commentToUpdateKey = Key.create(Comment.class,Long.parseLong(commentID.trim()));
			}
			
			if("reroll".equals(reqType)) { //reroll
				String winnerToRerollId = params.get("winnerToReroll")[0];
				
				ga.reroll(Key.create(CpcUser.class, Long.parseLong(winnerToRerollId)));
				log.info("[ADMIN] " + cpcuser + " rerolled the giveaway " + ga + "." + "\n"
						+ "New winners are : " + Arrays.deepToString(ga.getWinners().toArray()) + "."
						);
			} else if("closeGa".equals(reqType)) {
				log.info("[ADMIN] " + cpcuser + " closed the giveaway " + ga + ".");
				ga.setTitle(ga.getTitle() + " (Fermé par " + cpcuser.getCpcNickname() + ")");
				ga.setOpen(false);
			} else if("openGa".equals(reqType)) {
				log.info("[ADMIN] " + cpcuser + " reopened the giveaway " + ga + ".");
				ga.setOpen(true);
			} else if("deleteGiveaway".equals(reqType)) {
				log.info("[ADMIN] " + cpcuser + " deleted " + ga + ".");
				GiveawayPersistance.deleteGa(gaKey);
			} else if("addWinner".equals(reqType)) {
				log.info("[ADMIN] " + cpcuser + " added " + userToUpdate + " to winner list of " + ga + ".");
				if(userToUpdate != null)
					userToUpdate.addWon(gaKey);
				if(ga != null)
					ga.addWinner(userToUpdateKey);
			} else if ("removeWinner".equals(reqType)) {
				log.info("[ADMIN] " + cpcuser + " removed " + userToUpdate + " from winner list of " + ga + ".");
				if(userToUpdate != null)
					userToUpdate.removeWon(gaKey);
				if(ga != null)
					ga.removeWinner(userToUpdateKey);
			} else if("addEntry".equals(reqType)) {
				log.info("[ADMIN] " + cpcuser + " added " + userToUpdate + " in entries of " + ga + ".");
				if(userToUpdate != null)
					userToUpdate.addEntry(gaKey);
				if(ga != null)
					ga.addEntrant(userToUpdateKey);
			} else if("removeEntry".equals(reqType)) {
				log.info("[ADMIN] " + cpcuser + " removed " + userToUpdate + " from entries of " + ga + ".");
				if(userToUpdate != null)
					userToUpdate.removeEntry(gaKey);
				if(ga != null)
					ga.removeEntrant(userToUpdateKey);
			} else if("removeCreated".equals(reqType)) {
				log.info("[ADMIN] " + cpcuser + " removed giveaway " + gaKey + " from user " + userToUpdateKey);
				userToUpdate.removeGiveaway(gaKey);
			} else if("addCreated".equals(reqType)) {
				log.info("[ADMIN] " + cpcuser + " added giveaway " + gaKey + " to user " + userToUpdateKey);
				if(userToUpdate != null)
					userToUpdate.addGiveaway(gaKey);
				if(ga != null)
					ga.setAuthor(userToUpdateKey);
			} else if("removeComment".equals(reqType)) {
				log.info("[ADMIN] " + cpcuser + " removed " + commentToUpdateKey + " from comments of " + ga + ".");
				ga.removeComment(commentToUpdateKey);
				CommentPersistance.delete(commentToUpdateKey);
			} else if("banuser".equals(reqType)) {
				log.info("[ADMIN] " + cpcuser + " banned " + userToUpdate + ".");
				userToUpdate.setBanned(true);
			} else if("unbanuser".equals(reqType)) {
				log.info("[ADMIN] " + cpcuser + " unbanned " + userToUpdate + ".");
				userToUpdate.setBanned(false);
			} else if("userfusion".equals(reqType)) {
				String userToDeleteID = params.get("user2id")[0];
				Key<CpcUser> userToDeleteKey = Key.create(CpcUser.class,Long.parseLong(userToDeleteID.trim()));
				log.info("[ADMIN] " + cpcuser + " fusionned " + userToUpdate + " with " + userToDeleteKey + ".");
				CpcUserPersistance.cpcusersFusion(userToUpdateKey, userToDeleteKey);
			} else if("changeNbCopies".equals(reqType)) {
				log.info("[ADMIN] " + cpcuser + " changed " +  ga + " number of copies.");
				int newNbCopies = Integer.parseInt(params.get("nbcopies")[0]);
				if(ga != null)
					ga.setNbCopies(newNbCopies);
			} else if("changeNickname".equals(reqType)) {
				log.info("[ADMIN] " + cpcuser + " changed " +  userToUpdate + " nickname.");
				String newNickname = params.get("newnickname")[0];
				if(userToUpdate != null)
					userToUpdate.setCpcNickname(newNickname);
			} else if("changeGiveawayDate".equals(reqType)) {
				log.info("[ADMIN] " + cpcuser + " changed " +  ga + " end date.");
				String newDate = params.get("newDate")[0];
				SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.FRANCE);
				try {
					Date newGiveawayDate = sdf.parse(newDate);
					ga.setEndDate(newGiveawayDate);
				} catch (ParseException e) { }
			}
			
			
			if(ga != null) {
				GiveawayPersistance.updateOrCreate(ga);
				resp.sendRedirect("/giveaway?gaID=" + ga.getKey().getId());
			} 
			if(userToUpdate != null) {
				CpcUserPersistance.updateOrCreate(userToUpdate);
				resp.sendRedirect("/user?userID=" + userToUpdate.getKey().getId());
			}
			
		} else {

			resp.sendRedirect("/");
		}
		
	}
}