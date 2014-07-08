package fr.cpcgifts.persistance;

import static fr.cpcgifts.persistance.OfyService.ofy;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;


import com.googlecode.objectify.Key;

import fr.cpcgifts.model.AdminRequest;
import fr.cpcgifts.model.AdminRequest.State;

/**
 * Utilitaire permettant de récupérer des entités de type AdminRequest depuis le datastore.
 * @author bastien
 */
public class AdminRequestPersistance {
	
	public static void updateOrCreate(AdminRequest request) {
		ofy().save().entity(request);
	}
	
	public static void delete(Key<AdminRequest> key) {
		ofy().delete().key(key);
	}

	/**
	 * Récupère une requête à l'aide de sa clé.
	 * @param key
	 * @return
	 */
	public static AdminRequest getRequest(Key<AdminRequest> key) {
		AdminRequest res = null;

		res = ofy().load().key(key).safe();

		return res;
	}
	
	/**
	 * Récupère un ensemble de requêtes.
	 * @param keys
	 * @return
	 */
	public static Map<Key<AdminRequest>, AdminRequest> getAll(Collection<Key<AdminRequest>> keys) {
		Map<Key<AdminRequest>, AdminRequest> res = null;
		
		res = ofy().load().keys(keys);
		
		return res;
	}
	
	
	/**
	 * Récupère la liste des requêtes encore ouvertes.
	 * @param detached
	 * @return
	 */
	public static List<AdminRequest> getAllRequests() {
		List<AdminRequest> res;
		
		res = ofy().load().type(AdminRequest.class).order("-requestDate").list();
		
		return res;
		
	}

	/**
	 * Récupère la liste des requêtes ouvertes.
	 */
	public static int getOpenAdminRequestsCount() {
				
		return ofy().load().type(AdminRequest.class).filter("state", State.Open).count();
	}
	
	/**
	 * Récupère la liste des requêtes fermées depuis plus de 2 semaines.
	 * @param detached
	 * @return
	 */
	public static List<AdminRequest> getRequestsToDelete() {
		List<AdminRequest> res;

		Calendar c = Calendar.getInstance();
		c.add(Calendar.WEEK_OF_YEAR, -2);
		
		List<State> closedStates = new ArrayList<State>();
		closedStates.add(State.Processed);
		closedStates.add(State.Denied);
		
		res = ofy().load().type(AdminRequest.class).filter("state IN", closedStates).filter("requestDate <", c.getTime()).list();
		
		return res;

	}
	
}
