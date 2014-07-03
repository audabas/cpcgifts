package fr.cpcgifts;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import fr.cpcgifts.model.CpcUser;
import fr.cpcgifts.persistance.CpcUserPersistance;

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

		if (user != null) {

			CpcUser cpcuser = new CpcUser(user, req.getParameter("idCPC"));
			
			CpcUser existingUser = CpcUserPersistance.getCpcUserByCpcProfileId(cpcuser.getCpcProfileId());
			
			if(existingUser != null) {
			
				log.info("User change email : " + cpcuser + " : " + user.getEmail());
				
				existingUser.setGuser(user);
				
				CpcUserPersistance.updateOrCreate(existingUser);
				
			} else {
			
				log.info("New user registred : " + cpcuser.getCpcNickname());
	
				CpcUserPersistance.updateOrCreate(cpcuser);
			
			}
		}

		resp.sendRedirect("/");
		return;
	}
}