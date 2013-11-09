package fr.cpcgifts.persistance;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheManager;

import com.google.appengine.api.datastore.Key;

import fr.cpcgifts.model.CpcUser;
import fr.cpcgifts.model.Giveaway;

public class GAPersistance {

	public static Giveaway getGA(Key key) throws JDOObjectNotFoundException {
		Giveaway res = getGA(key, true);
		return res;
	}
	
	public static Giveaway getGA(Key key, boolean detached) throws JDOObjectNotFoundException {
		Giveaway res = null;

		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();

		try {
			res = pm.getObjectById(Giveaway.class, key);
			if(detached)
				res = pm.detachCopy(res);
		} catch(JDOObjectNotFoundException e) {
			throw e;
		} finally {
			if(detached)
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
		c.add(Calendar.WEEK_OF_YEAR, -2);

		Query query = pm.newQuery(Giveaway.class);
		query.setFilter("open == openParam && endDate < closeDate && nbWinners == 0");
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
	
	public static void deleteGa(Key key) {
		Cache cache;

		try {
			cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());
		} catch (CacheException e1) {
			return;
		}


		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();

		Giveaway ga = pm.getObjectById(Giveaway.class, key);

		CpcUser author = pm.getObjectById(CpcUser.class, ga.getAuthor());

		author.removeGiveaway(ga.getKey());

		Set<Key> entrantsKeys = ga.getEntrants();

		for(Key k : entrantsKeys) {
			try {
				CpcUser cpcuser = pm.getObjectById(CpcUser.class, k);
				cpcuser.removeEntry(ga.getKey());
				pm.makePersistent(cpcuser);
				cache.remove(k);
			} catch(javax.jdo.JDOObjectNotFoundException e) {
				// rien à faire, l'utilisateur à déjà été supprimé
			}

		}
		
		Set<Key> winnersKeys = ga.getWinners();
		
		for(Key k : winnersKeys) {
			try {
				CpcUser cpcuser = pm.getObjectById(CpcUser.class, k);
				cpcuser.removeWon(ga.getKey());
				pm.makePersistent(cpcuser);
				cache.remove(k);
			} catch(javax.jdo.JDOObjectNotFoundException e) {
				// rien à faire, l'utilisateur à déjà été supprimé
			}
			
		}

		pm.makePersistent(author);
		pm.deletePersistent(ga);
		
		cache.remove(author.getKey());
		cache.remove(ga.getKey());
	}

	public static void closePm() {
		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();
		pm.close();
	}

}
