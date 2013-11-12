package fr.cpcgifts.persistance;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;

import fr.cpcgifts.model.CpcUser;
import fr.cpcgifts.model.Giveaway;

public class CpcUserPersistance {

	public static CpcUser getCpcUser(String id) {
		CpcUser res = null;
		
		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();

		Query query = pm.newQuery(CpcUser.class);
		query.setFilter("id == idParam");
		query.declareParameters("String idParam");

		try {
			@SuppressWarnings("unchecked")
			List<CpcUser> results = (List<CpcUser>) query.execute(id);
			if (!results.isEmpty()) {
				for (CpcUser u : results) {
					res = u;
					res = pm.detachCopy(res);
					break;
				}
			}
		} finally {
			query.closeAll();
			pm.close();
		}

		return res;
	}
	
	public static CpcUser getCpcUserByKey(Key key) {
		CpcUser res = null;
		
		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();

		try {
			res = pm.getObjectById(CpcUser.class, key);
			res = pm.detachCopy(res);
		} finally {
			pm.close();
		}

		return res;
	}
	
	public static CpcUser getCpcUserByCpcProfileId(String cpcProfileId, boolean detached) {
		CpcUser res = null;
		
		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();
		
		Query query = pm.newQuery(CpcUser.class);
		query.setFilter("cpcProfileId == idParam");
		query.declareParameters("String idParam");
		
		try {
			@SuppressWarnings("unchecked")
			List<CpcUser> results = (List<CpcUser>) query.execute(cpcProfileId);
			if (!results.isEmpty()) {
					res = results.get(0);
					if(detached)
						res = pm.detachCopy(res);
			}
		} finally {
			query.closeAll();
			
			if(detached)
				pm.close();
		}
		
		return res;
	}
	
	public static List<CpcUser> getCpcUsers(List<Key> keys, boolean detached) {
		List<CpcUser> res = new ArrayList<CpcUser>();

		if (keys.size() == 0)
			return res;

		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();

		try {

			for (Key k : keys) {

				CpcUser u = pm.getObjectById(CpcUser.class,k);

				if(detached) {
					res.add(pm.detachCopy(u));
				} else {
					res.add(u);
				}

			}
		} finally {
			if(detached)
				pm.close();
		}

		return res;
	}
	
	public static CpcUser getCpcUserUndetached(Key key) throws JDOObjectNotFoundException {
		CpcUser res = null;
		
		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();

		try {
			res = pm.getObjectById(CpcUser.class, key);
		} catch(JDOObjectNotFoundException e) {
			throw e;
		} finally {
		}

		return res;
	}
	
	@SuppressWarnings("unchecked")
	public static List<CpcUser> getAllUsers() {
		List<CpcUser> res;
		
		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();
		
		Query query = pm.newQuery(CpcUser.class);
		query.setOrdering("cpcNickname asc");
		
		try {
			res = (List<CpcUser>) query.execute();
			res = (List<CpcUser>) pm.detachCopyAll(res);
		} finally {
			query.closeAll();
			pm.close();
		}
		
		return res;
	}
	
	/**
	 * Fusionne les giveaways de deux profils en un seul. 
	 */
	public static void cpcusersFusion(Key profileToKeep, Key profileToDelete) {
		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();
		
		CpcUser userToKeep = pm.getObjectById(CpcUser.class,profileToKeep);
		CpcUser userToDelete = pm.getObjectById(CpcUser.class, profileToDelete);
		
		userToKeep.addGiveaways(userToDelete.getGiveaways());
		for(Key k : userToDelete.getGiveaways()) {
			Giveaway ga = pm.getObjectById(Giveaway.class, k);
			ga.setAuthor(profileToKeep);
		}
		
		userToKeep.addEntries(userToDelete.getEntries());
		for(Key k : userToDelete.getEntries()) {
			Giveaway ga = pm.getObjectById(Giveaway.class, k);
			ga.removeEntrant(profileToDelete);
			ga.addEntrant(profileToKeep);
		}
		
		userToKeep.addWon(userToDelete.getWon());
		for(Key k : userToDelete.getWon()) {
			Giveaway ga = pm.getObjectById(Giveaway.class, k);
			ga.removeWinner(profileToDelete);
			ga.addWinner(profileToKeep);
		}
		
		pm.makePersistent(userToKeep);
		pm.deletePersistent(userToDelete);
		
		pm.close();
	}

	public static void closePm() {
		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();
		pm.close();
	}

}
