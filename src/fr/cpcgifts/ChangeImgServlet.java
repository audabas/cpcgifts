package fr.cpcgifts;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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

			@SuppressWarnings("unchecked")
			Map<String, String[]> params = req.getParameterMap();
			
			String imgUrl = params.get("imgurl")[0];
			
			cpcuser.setAvatarUrl(imgUrl);
			
			resp.sendRedirect("user?userID=" + cpcuser.getKey().getId());
			
			log.info(cpcuser.getCpcNickname() + ":" + cpcuser.getKey().getId() + " changed avatar with " + imgUrl);
			
		} else {
			resp.sendRedirect("/");
		}
		
		pm.close();
		
	}
}
