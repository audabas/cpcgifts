package fr.cpcgifts;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheManager;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import fr.cpcgifts.model.CpcUser;
import fr.cpcgifts.model.Giveaway;
import fr.cpcgifts.persistance.CpcUserPersistance;
import fr.cpcgifts.persistance.PMF;

@SuppressWarnings("serial")
public class AdminServlet extends HttpServlet {
	
	private static final Logger log = Logger.getLogger(AdminServlet.class.getName());
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		HttpSession session = req.getSession();
		CpcUser cpcuser = (CpcUser) session.getAttribute("cpcuser");
		cpcuser = pm.getObjectById(CpcUser.class, cpcuser.getKey());

		if (user != null && cpcuser != null && userService.isUserAdmin()) {

			@SuppressWarnings("unchecked")
			Map<String, String[]> params = req.getParameterMap();
			
			String reqType = params.get("reqtype")[0];
			String gaID = params.get("gaid")[0];
			Giveaway ga = pm.getObjectById(Giveaway.class, KeyFactory.createKey(Giveaway.class.getSimpleName(),Long.parseLong(gaID)));
			
			if(reqType.equals("reroll")) { //reroll
				String winnerToRerollId = params.get("winnerToReroll")[0];
				
				ga.reroll(KeyFactory.createKey(CpcUser.class.getSimpleName(), Long.parseLong(winnerToRerollId)));
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
			} else if("addWinner".equals(reqType)) {
				Long userId = Long.parseLong(params.get("user")[0]);
				log.info("[ADMIN] " + cpcuser + " added " + userId + " to winner list of " + ga + ".");
				Key userKey = KeyFactory.createKey(CpcUser.class.getSimpleName(), userId);
				CpcUser winner = CpcUserPersistance.getCpcUserUndetached(userKey);
				winner.addWon(ga.getKey());
				ga.addWinner(userKey);
			} else if ("removeWinner".equals(reqType)) {
				Long userId = Long.parseLong(params.get("user")[0]);
				Key userKey = KeyFactory.createKey(CpcUser.class.getSimpleName(), userId);
				log.info("[ADMIN] " + cpcuser + " removed " + userId + " from winner list of " + ga + ".");
				CpcUser winner = CpcUserPersistance.getCpcUserUndetached(userKey);
				winner.removeWon(ga.getKey());
				ga.removeWinner(userKey);
			}
			
			resp.sendRedirect("/giveaway?gaID=" + ga.getKey().getId());
			
			try {
	            Cache cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());
	            
	            cache.remove(cpcuser.getKey());
	            cache.remove(ga.getKey());
				
	        } catch (CacheException e) {
	        	//rien
	        }
			
		} else {

			resp.sendRedirect("/");
		}
		
		pm.close();
	}
}