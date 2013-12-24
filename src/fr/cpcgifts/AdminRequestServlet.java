package fr.cpcgifts;

import java.io.IOException;
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

import fr.cpcgifts.model.AdminRequest;
import fr.cpcgifts.model.Comment;
import fr.cpcgifts.model.CpcUser;
import fr.cpcgifts.model.Giveaway;
import fr.cpcgifts.persistance.AdminRequestPersistance;
import fr.cpcgifts.persistance.CpcUserPersistance;
import fr.cpcgifts.persistance.PMF;

@SuppressWarnings("serial")
public class AdminRequestServlet extends HttpServlet {
	
	private static final Logger log = Logger.getLogger(AdminRequestServlet.class.getName());
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
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
			
			String reqType = params.get("req")[0];
			
			AdminRequest requestToUpdate = null;
			if(!"create".equals(reqType)) {
				String requestId = params.get("reqid")[0];
				requestToUpdate = pm.getObjectById(AdminRequest.class, KeyFactory.createKey(AdminRequest.class.getSimpleName(),Long.parseLong(requestId)));
			}
			
			if("create".equals(reqType)) {
				String typeString = params.get("type")[0];
				Long attachmentId = Long.parseLong(params.get("attachmentid")[0]);
				String text = params.get("text")[0];
				
				AdminRequest.Type type = AdminRequest.Type.valueOf(typeString);
				
				Key attachment = null;
				
				StringBuilder textStr = new StringBuilder();
				
				switch (type) {
					case Reroll:
						String userId = params.get("userid")[0];
						Key userToRerollKey = KeyFactory.createKey(CpcUser.class.getSimpleName(), Long.parseLong(userId));
						CpcUser userToReroll = CpcUserPersistance.getUserFromCache(userToRerollKey);
						textStr.append("Utilisateur à reroll : ["+ userToReroll.getCpcNickname() + "](/user?userID=" + userId + ")  \n");
						textStr.append("Raison du reroll :  \n");
						textStr.append(text);
						
						attachment = KeyFactory.createKey(Giveaway.class.getSimpleName(), attachmentId);
						
						break;
					case ReportPost:
						attachment = KeyFactory.createKey(Comment.class.getSimpleName(), attachmentId);
						
						textStr.append("Raison du signalement :  \n");
						textStr.append(text);
						
						break;
					case ReportUser:
						attachment = KeyFactory.createKey(CpcUser.class.getSimpleName(), attachmentId);
						
						textStr.append("Raison du signalement :  \n");
						textStr.append(text);
						
						break;
					case RulesModification:
						attachment = KeyFactory.createKey(Giveaway.class.getSimpleName(), attachmentId);
						
						textStr.append("Modifications à effectuer :  \n");
						textStr.append(text);
						
						break;
					case TitleModification:
						attachment = KeyFactory.createKey(Giveaway.class.getSimpleName(), attachmentId);
						
						textStr.append("Modifications à effectuer :  \n");
						textStr.append(text);
						
						break;
					default:
						break;
				}
				
				AdminRequest adminRequest = new AdminRequest(cpcuser.getKey(), type, attachment, textStr.toString());
				
				AdminRequestPersistance.makePersistent(adminRequest);
				
				log.info("User " + cpcuser + " submited new admin request " + adminRequest);
			} else if("process".equals(reqType)) {
				requestToUpdate.setState(AdminRequest.State.Processed);
				requestToUpdate.setConsideredBy(cpcuser.getKey());
			} else if("deny".equals(reqType)) {
				requestToUpdate.setState(AdminRequest.State.Denied);
				requestToUpdate.setConsideredBy(cpcuser.getKey());
			}

			
			try {
	            if(requestToUpdate != null) {				
	            	Cache cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());
	            	
	            	cache.remove(requestToUpdate.getKey());
	            	cache.remove("allRequests");
	            }
	        } catch (CacheException e) {
	        	//rien
	        }
			
		}
		
		pm.close();
		
	}
}
