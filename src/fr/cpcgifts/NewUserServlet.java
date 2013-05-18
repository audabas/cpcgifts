package fr.cpcgifts;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.servlet.http.*;
import javax.jdo.Query;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import fr.cpcgifts.model.CpcUser;
import fr.cpcgifts.persistance.CpcUserPersistance;
import fr.cpcgifts.persistance.PMF;

@SuppressWarnings("serial")
public class NewUserServlet extends HttpServlet { // /signin-serv
	
	private static final Logger log = Logger.getLogger(NewUserServlet.class.getName());
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		
		if (user != null) {
		
		resp.getWriter().print(CpcUserPersistance.getCpcUser(user.getUserId()) != null);
		
		} else {
			resp.getWriter().print(false);
		}
		
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		PersistenceManager pm = PMF.get().getPersistenceManager();

		if (user != null) {

			CpcUser cpcuser = new CpcUser(user, req.getParameter("idCPC"));
			
			log.info("New user registred : " + cpcuser.getCpcNickname());

			try {
				pm.makePersistent(cpcuser);
			} finally {
				pm.close();
			}
			
		}

		resp.sendRedirect("/");
		return;
	}
}