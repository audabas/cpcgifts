package fr.cpcgifts;

import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import fr.cpcgifts.model.CpcUser;
import fr.cpcgifts.model.Giveaway;
import fr.cpcgifts.persistance.CpcUserPersistance;
import fr.cpcgifts.persistance.GiveawayPersistance;

@SuppressWarnings("serial")
public class CreateGiveawayServlet extends HttpServlet {
	
	private static final Logger log = Logger.getLogger(CreateGiveawayServlet.class.getName());
	
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
			
			String imgUrl = params.get("imgUrl")[0];
			String gameName = params.get("gameName")[0];
			String gameDescription = params.get("gameDescription")[0];
			String customRules = params.get("customRules")[0];
			String endDateStr = params.get("endDateParsed")[0];
			String[] endDateSplittedStr = endDateStr.split("@");
			String[] splittedDate = endDateSplittedStr[0].split("-");
			String[] splittedTime = endDateSplittedStr[1].split("-");
			boolean isPrivate = (params.get("visibility") != null && "private".equals(params.get("visibility")[0]));
			
			int nbCopies = 1;
			try {
				nbCopies = Integer.parseInt(params.get("nbCopies")[0]);
			} catch (NumberFormatException e) {
				nbCopies = 1;
			}
			if(nbCopies < 1) {
				nbCopies = 1;
			}
			
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, Integer.parseInt(splittedDate[0]));
			c.set(Calendar.MONTH, Integer.parseInt(splittedDate[1])-1);
			c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(splittedDate[2]));
			c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splittedTime[0]) - (Integer.parseInt(endDateSplittedStr[2])/100));
			c.set(Calendar.MINUTE, Integer.parseInt(splittedTime[1]));
			
			
			Giveaway ga = new Giveaway(cpcuser.getKey(), gameName, gameDescription, customRules, imgUrl, c.getTime(), nbCopies);
			
			ga.setPrivate(isPrivate);
			
			GiveawayPersistance.updateOrCreate(ga);
			
			cpcuser.addGiveaway(ga.getKey());
			
			CpcUserPersistance.updateOrCreate(cpcuser);
			
			log.info("Created ga : " + ga.getTitle() + " by " + cpcuser.getCpcNickname());
			
			resp.sendRedirect("/giveaway?gaID=" + ga.getKey().getId());
			
		} else {

			resp.sendRedirect("/");
			return;
		}
	}
}