package fr.cpcgifts;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import fr.cpcgifts.persistance.CpcUserPersistance;

@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
      UserService userService = UserServiceFactory.getUserService();
      User user = userService.getCurrentUser();
      
      if (user != null) {
    	  
    	  
    	  if(CpcUserPersistance.getCpcUser(user.getUserId()) == null) { // si c'est sa première connexion
    		  resp.sendRedirect("/login.jsp");
    		  return;
    	  }
      }
      
      // non connecté ou déjà utilisateur
      resp.sendRedirect("/");
      return;
  }
}