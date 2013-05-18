package fr.cpcgifts;

import java.io.IOException;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.servlet.http.*;
import javax.jdo.Query;
import javax.mail.Session;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import fr.cpcgifts.model.CpcUser;
import fr.cpcgifts.model.Giveaway;
import fr.cpcgifts.persistance.CpcUserPersistance;
import fr.cpcgifts.persistance.PMF;

@SuppressWarnings("serial")
public class GiveawayServlet extends HttpServlet {
	
	private static final Logger log = Logger.getLogger(GiveawayServlet.class.getName());
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		HttpSession session = req.getSession();
		CpcUser cpcuser = (CpcUser) session.getAttribute("cpcuser");
		cpcuser = pm.getObjectById(CpcUser.class, cpcuser.getKey());

		if (user != null && cpcuser != null) {

			Map params = req.getParameterMap();
			
			String imgUrl = ((String[]) params.get("imgUrl"))[0];
			String gameName = ((String[]) params.get("gameName"))[0];
			String gameDescription = ((String[]) params.get("gameDescription"))[0];
			String endDateStr = ((String[]) params.get("endDateParsed"))[0];
			String[] endDateSplittedStr = endDateStr.split("@");
			String[] splittedDate = endDateSplittedStr[0].split("-");
			String[] splittedTime = endDateSplittedStr[1].split("-");
			
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, Integer.parseInt(splittedDate[0]));
			c.set(Calendar.MONTH, Integer.parseInt(splittedDate[1])-1);
			c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(splittedDate[2]));
			c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splittedTime[0]));
			c.set(Calendar.MINUTE, Integer.parseInt(splittedTime[1]));
			c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) + (Integer.parseInt(endDateSplittedStr[2])/10));
			
			
			Giveaway ga = new Giveaway(cpcuser.getKey(), gameName, gameDescription, imgUrl, c.getTime());
			
			log.info("Created ga : " + ga.getTitle() + " by " + cpcuser.getCpcNickname());
			
			try {
				pm.makePersistent(ga);
				
				cpcuser.addGiveaway(ga.getKey());				
				pm.makePersistent(cpcuser);
				
			} finally {
				pm.close();
			}
			
			resp.sendRedirect("/giveaway?gaID=" + ga.getKey().getId());
			
		} else {

			resp.sendRedirect("/");
			return;
		}
	}
}