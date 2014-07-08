package fr.cpcgifts;

import java.io.IOException;
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

@SuppressWarnings("serial")
public class EditGiveawayServlet extends HttpServlet {
	
	private static final Logger log = Logger.getLogger(EditGiveawayServlet.class.getName());
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		CpcUser cpcuser = null;
		if(user != null)
			cpcuser = CpcUserPersistance.getCpcUser(user.getUserId());
		if(cpcuser == null)
			resp.sendRedirect(userService.createLogoutURL("/"));

		if (user != null && cpcuser != null) {

			@SuppressWarnings("unchecked")
			Map<String, String[]> params = req.getParameterMap();
			
			String gaID = params.get("gaid")[0];
			Giveaway ga = GiveawayPersistance.getGA(Key.create(Giveaway.class,Long.parseLong(gaID)));

			String reqType = params.get("req")[0];

			if("changeimg".equals(reqType)) {
				String imgUrl = params.get("imgurl")[0];

				if(cpcuser.getKey().equals(ga.getAuthor()) || userService.isUserAdmin()) {
					ga.setImgUrl(imgUrl);
					log.info(cpcuser.getCpcNickname() + ":" + cpcuser.getKey().getId() + " changed giveaway " + ga.getTitle() + "[" + ga.getKey().getId() +  "] image by " + imgUrl);
				}

			} else if("changedescription".equals(reqType)) {
				String newDesc = params.get("desc")[0];

				if(cpcuser.getKey().equals(ga.getAuthor()) || userService.isUserAdmin()) {
					ga.setDescription(newDesc);
					log.info(cpcuser.getCpcNickname() + ":" + cpcuser.getKey().getId() + " changed giveaway " + ga.getTitle() + "[" + ga.getKey().getId() +  "] description by " + newDesc);
				}
			} else if("comment".equals(reqType)) {
				String commentText = params.get("comment")[0];
				
				Comment comment = new Comment(cpcuser.getKey(), ga.getKey(), commentText);
				
				CommentPersistance.updateOrCreate(comment);
				
				ga.addComment(comment.getKey());
				
				log.info(cpcuser + " posted new comment : " + comment);
			} else if("changetitle".equals(reqType)) {
				String newTitle = params.get("title")[0];
				
				if(userService.isUserAdmin()) {
					log.info(cpcuser + " changed giveway title " + ga + " by " + newTitle);
					ga.setTitle(newTitle);
				}
			} else if("deletecomment".equals(reqType)) {
				String commentId = params.get("comment")[0];
				Key<Comment> commentKey = Key.create(Comment.class, Long.parseLong(commentId));
				Comment c = CommentPersistance.getComment(commentKey);
				
				if(c.getAuthor().equals(cpcuser.getKey()) || userService.isUserAdmin()) {
					log.info(cpcuser + " deleted comment " + c + " from giveaway " + ga + ".");
					
					ga.removeComment(commentKey);
					CommentPersistance.delete(commentKey);
				}
				
			} else if("changerules".equals(reqType)) {
				String newRules = params.get("rules")[0];
				
				if(userService.isUserAdmin()) {
					log.info(cpcuser + " changed giveway rules " + ga + " by " + newRules);
					ga.setRules(newRules);
				}
			} else if("closeGa".equals(reqType)) {				
				if(cpcuser.getKey().equals(ga.getAuthor()) || userService.isUserAdmin()) {
					log.info(cpcuser + " closed giveaway " + ga);
					
					ga.setTitle(ga.getTitle() + " (Fermé par " + cpcuser.getCpcNickname() + ")");
					ga.setOpen(false);
				}
			}
			
			resp.sendRedirect("/giveaway?gaID=" + ga.getKey().getId());
			
			GiveawayPersistance.updateOrCreate(ga);
			
		} else {
			resp.sendRedirect("/");
		}
		
	}
}
