package fr.cpcgifts;

import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.*;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import fr.cpcgifts.model.CpcUser;
import fr.cpcgifts.persistance.PMF;

@SuppressWarnings("serial")
public class ChangeImgServlet extends HttpServlet {
	
	private static final Logger log = Logger.getLogger(ChangeImgServlet.class.getName());
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		HttpSession session = req.getSession();
		CpcUser cpcuser = (CpcUser) session.getAttribute("cpcuser");
		cpcuser = pm.getObjectById(CpcUser.class, cpcuser.getKey());

		if (user != null && cpcuser != null) {

			Map params = req.getParameterMap();
			
			String imgUrl = ((String[]) params.get("imgurl"))[0];
			
			cpcuser.setAvatarUrl(imgUrl);
			
			resp.sendRedirect("user?userID=" + cpcuser.getKey().getId());
			
			log.info(cpcuser.getCpcNickname() + ":" + cpcuser.getKey().getId() + " changed avatar with " + imgUrl);
			
		} else {
			resp.sendRedirect("/");
		}
		
		pm.close();
		
	}
}
