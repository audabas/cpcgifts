package fr.cpcgifts.persistance;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;

import fr.cpcgifts.model.CpcUser;
import fr.cpcgifts.model.Giveaway;

public class GAPersistance {

	public static Giveaway getGA(Key key) {
		Giveaway res = null;

		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();

		try {
			res = pm.getObjectById(Giveaway.class, key);
			res = pm.detachCopy(res);
		} finally {
			pm.close();
		}

		return res;
	}

	public static List<Giveaway> getGAs(List<Key> keys) {
		List<Giveaway> res = new ArrayList<>();

		if (keys.size() == 0)
			return res;

		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();

		try {

			for (Key k : keys) {

				Giveaway ga = pm.getObjectById(Giveaway.class, k);

				res.add(pm.detachCopy(ga));

			}
		} finally {
			pm.close();
		}

		return res;
	}

	public static List<Giveaway> getAllGA() {
		List<Giveaway> res;

		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();

		Query query = pm.newQuery(Giveaway.class);
		query.setOrdering("endDate desc");

		try {
			res = (List<Giveaway>) query.execute();
			res = (List<Giveaway>) pm.detachCopyAll(res);
		} finally {
			query.closeAll();
			pm.close();
		}

		return res;
	}
	
	public static List<Giveaway> getOpenGAs() {
		return getOpenGAs(true);
	}

	public static List<Giveaway> getOpenGAs(boolean detached) {
		List<Giveaway> res;

		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();

		Query query = pm.newQuery(Giveaway.class);
		query.setFilter("open == openParam");
		query.setOrdering("endDate asc");
		query.declareParameters("boolean openParam");

		try {
			res = (List<Giveaway>) query.execute(true);
			if (detached)
				res = (List<Giveaway>) pm.detachCopyAll(res);
		} finally {
			query.closeAll();
			if (detached)
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
