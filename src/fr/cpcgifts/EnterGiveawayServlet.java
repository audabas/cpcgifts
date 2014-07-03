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

import fr.cpcgifts.model.CpcUser;
import fr.cpcgifts.model.Giveaway;
import fr.cpcgifts.persistance.CpcUserPersistance;
import fr.cpcgifts.persistance.GiveawayPersistance;

@SuppressWarnings("serial")
public class EnterGiveawayServlet extends HttpServlet {
	
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(EnterGiveawayServlet.class.getName());
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
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
			
			String reqType = params.get("reqtype")[0];
			String gaID = params.get("gaid")[0];
			Giveaway ga = GiveawayPersistance.getGA(Key.create(Giveaway.class,Long.parseLong(gaID)));
			
			if(reqType.equals("enter")) {
				if(! ga.getEntrants().contains(cpcuser.getKey()) && !cpcuser.isBanned()) { // vérifie que l'utilisateur n'est pas déjà inscrit et qu'il n'est pas banni.
					ga.addEntrant(cpcuser.getKey());
					cpcuser.addEntry(ga.getKey());
				}
			} else {
				ga.removeEntrant(cpcuser.getKey());
				cpcuser.removeEntry(ga.getKey());
			}
			
			GiveawayPersistance.updateOrCreate(ga);
			CpcUserPersistance.updateOrCreate(cpcuser);
			
			resp.sendRedirect("/giveaway?gaID=" + ga.getKey().getId());
			
			
			
		} else {

			resp.sendRedirect("/");
		}
		
	}
}