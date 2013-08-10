package fr.cpcgifts.persistance;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;

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
		List<Giveaway> res = new ArrayList<Giveaway>();

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

	@SuppressWarnings("unchecked")
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

	@SuppressWarnings("unchecked")
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
	
	/**
	 * Récupère les concours terminés sans participants inscrits.
	 * @param detached
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Giveaway> getClosedEmptyGAs(boolean detached) {
		List<Giveaway> res;

		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -1);

		Query query = pm.newQuery(Giveaway.class);
		query.setFilter("open == openParam && endDate < closeDate && winner == null");
		query.declareParameters("boolean openParam, " + Date.class.getName() +  " closeDate");

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("openParam", false);
		parameters.put("closeDate", c.getTime());
		
		try {
			res = (List<Giveaway>) query.executeWithMap(parameters);
			if (detached)
				res = (List<Giveaway>) pm.detachCopyAll(res);
		} finally {
			query.closeAll();
			if (detached)
				pm.close();
		}

		return res;
	}
	

	@SuppressWarnings("unchecked")
	public static List<Giveaway> getOpenGAsToClose(boolean detached) {
		List<Giveaway> res;

		Calendar c = Calendar.getInstance();
		
		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();

		Query query = pm.newQuery(Giveaway.class);
		query.setFilter("open == openParam && endDate < currentDate");
		query.declareParameters("boolean openParam, " + Date.class.getName() +  " currentDate");
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("openParam", true);
		parameters.put("currentDate", c.getTime());

		try {
			res = (List<Giveaway>) query.executeWithMap(parameters);
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
