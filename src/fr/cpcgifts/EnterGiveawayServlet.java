package fr.cpcgifts;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import fr.cpcgifts.model.CpcUser;
import fr.cpcgifts.model.Giveaway;
import fr.cpcgifts.persistance.PMF;

@SuppressWarnings("serial")
public class EnterGiveawayServlet extends HttpServlet {
	
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(EnterGiveawayServlet.class.getName());
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		HttpSession session = req.getSession();
		CpcUser cpcuser = (CpcUser) session.getAttribute("cpcuser");
		cpcuser = pm.getObjectById(CpcUser.class, cpcuser.getKey());

		if (user != null && cpcuser != null) {

			@SuppressWarnings("unchecked")
			Map<String, String[]> params = req.getParameterMap();
			
			String reqType = params.get("reqtype")[0];
			String gaID = params.get("gaid")[0];
			Giveaway ga = pm.getObjectById(Giveaway.class, KeyFactory.createKey(Giveaway.class.getSimpleName(),Long.parseLong(gaID)));
			
			if(reqType.equals("enter")) {
				if(! ga.getEntrants().contains(cpcuser.getKey())) { // si l'utilisateur est déjà inscrit.
					ga.addEntrant(cpcuser.getKey());
					cpcuser.addEntry(ga.getKey());
				}
			} else {
				ga.removeEntrant(cpcuser.getKey());
				cpcuser.removeEntry(ga.getKey());
			}
			
			
			
			resp.sendRedirect("/giveaway?gaID=" + ga.getKey().getId());
			
		} else {

			resp.sendRedirect("/");
		}
		
		pm.close();
	}
}