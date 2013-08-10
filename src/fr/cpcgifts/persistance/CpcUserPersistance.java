package fr.cpcgifts.persistance;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;

import fr.cpcgifts.model.CpcUser;

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
	
	public static CpcUser getCpcUserUndetached(Key key) {
		CpcUser res = null;
		
		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();

		try {
			res = pm.getObjectById(CpcUser.class, key);
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

	public static void closePm() {
		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();
		pm.close();
	}

}
