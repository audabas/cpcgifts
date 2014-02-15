package fr.cpcgifts.persistance;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;

import fr.cpcgifts.model.CpcUser;
import fr.cpcgifts.model.Giveaway;
import fr.cpcgifts.utils.DateTools;

/**
 * Utilitaire permettant de récupérer des entités de type giveaway depuis le datastore.
 * @author bastien
 *
 */
public class GAPersistance {

	/**
	 * Récupère un giveaway à partir de sa clé.
	 * @param key La clé du giveaway demandé.
	 * @return	Le giveaway correspondant.
	 * @throws JDOObjectNotFoundException si la clé ne correspond à aucune entité dans le datastore.
	 */
	public static Giveaway getGA(Key key) throws JDOObjectNotFoundException {
		Giveaway res = getGA(key, true);
		return res;
	}
	
	/**
	 * Récupère un giveaway à partir de sa clé.
	 * @param key La clé du giveaway demandé.
	 * @param detached Indique si l'objet doit être détachée du persistance manager ou non.
	 * @return	Le giveaway correspondant.
	 * @throws JDOObjectNotFoundException si la clé ne correspond à aucune entité dans le datastore.
	 */
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
	
	/**
	 * Récupère un giveaway depuis le cache s'il y est, le place dans le cache sinon.
	 * @param key La clé du giveaway à récupérer.
	 * @return Le giveaway demandé.
	 * @throws JDOObjectNotFoundException si la clé ne correspond à aucune entité dans le datastore.
	 */
	public static Giveaway getGAFromCache(Key key) throws JDOObjectNotFoundException {
		Giveaway res = null;
		
		try {
			Cache cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());

			res = (Giveaway) cache.get(key);

			if (res == null) { // s'il n'est pas dans le cache, on le met en cache
				res = getGA(key);
				cache.put(key, res);
			}
		} catch (CacheException e) {
			res = getGA(key);
		}
		
		return res;
	}
	
	/**
	 * Récupère un ensemble de giveaways depuis le cache. Les place dans le cache s'ils n'y sont pas déjà.
	 * @param keys
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<Key, Giveaway> getAllFromCache(Collection<Key> keys) {
		Map<Key, Giveaway> res = null;
		
		try {
			Cache cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());

			res = cache.getAll(keys);

			Collection<Key> notCachedKeys = CollectionUtils.subtract(keys, res.keySet());

			for (Key k : notCachedKeys) {
				Giveaway ga = getGAFromCache(k);
				res.put(k, ga);
			}

		} catch (CacheException e) {
			List<Giveaway> gas = getGAs(new ArrayList<>(keys));
			res = new HashedMap();
			
			for(Giveaway ga : gas) {
				res.put(ga.getKey(), ga);
			}
		}
		
		return res;
	}
	
	/**
	 * Récupère les giveaways triés par date de fin pour un utilisateur donné.
	 * @param user
	 * @param selection entries, won ou created
	 * @return
	 */
	public static Giveaway[] getUserGAsFromCache(CpcUser user, String selection) {
		Giveaway[] res = null;
		
		try {
			Cache cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());

			res = (Giveaway[]) cache.get(user.getKey().getId() + "-" + selection);
			
			if (res == null) {
				
				switch (selection) {
				case "created":
					res = DateTools.sortGiveawaysByEndDate(getAllFromCache(user.getGiveaways()));
					break;
				case "entries":
					res = DateTools.sortGiveawaysByEndDate(getAllFromCache(user.getEntries()));
					break;
				case "won":
					res = DateTools.sortGiveawaysByEndDate(getAllFromCache(user.getWon()));
					break;
				default:
					break;
				}
				
				cache.put(user.getKey().getId() + "-" + selection, res);
				
			}
			
		} catch (CacheException e) {
			switch (selection) {
			case "created":
				res = DateTools.sortGiveawaysByEndDate(getAllFromCache(user.getGiveaways()));
				break;
			case "entries":
				res = DateTools.sortGiveawaysByEndDate(getAllFromCache(user.getEntries()));
				break;
			case "won":
				res = DateTools.sortGiveawaysByEndDate(getAllFromCache(user.getWon()));
				break;
			default:
				break;
			}
		}
		
		return res;
	}

	/**
	 * Récupère une liste de giveaways depuis le datastore.
	 * @param keys
	 * @return 
	 */
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

	/**
	 * Récupère tout les giveaways du datastore.
	 * @return
	 */
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
	
	/**
	 * Récupère tous les concours enregistrés dans le datastore.
	 * @return
	 */
	public static List<Entity> getAllGAKeys() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		com.google.appengine.api.datastore.Query q = new com.google.appengine.api.datastore.Query(Giveaway.class.getSimpleName());
		q.setKeysOnly();
	
		PreparedQuery pq = datastore.prepare(q);
		
		return pq.asList(FetchOptions.Builder.withDefaults());
	}
	
	@SuppressWarnings("unchecked")
	public static List<Entity> getAllGAKeysFromCache() {
		List<Entity> res;
		
		Cache cache;
		
		try {
	        cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());
	                    
	        res = (List<Entity>) cache.get("allGAKeys");
			
			if(res == null) {
				res = getAllGAKeys();
				cache.put("allGAKeys", res);
			}
			
	    } catch (CacheException e) {
	    	res = getAllGAKeys();
	    }
		
		return res;
	}
	
	public static List<Giveaway> getOpenGAs() {
		return getOpenGAs(true);
	}

	/**
	 * Récupère la liste des giveaways encore ouverts.
	 * @param detached
	 * @return
	 */
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
	 * Récupère la liste des giveaways encore ouverts.
	 * @param detached
	 * @return
	 */
	public static List<Entity> getOpenGAKeys() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Filter opengasFilter = new FilterPredicate("open", FilterOperator.EQUAL, true);
		
		com.google.appengine.api.datastore.Query q = new com.google.appengine.api.datastore.Query(Giveaway.class.getSimpleName());
		q.setKeysOnly();
		q.setFilter(opengasFilter);
		q.addSort("endDate", SortDirection.ASCENDING);
	
		PreparedQuery pq = datastore.prepare(q);
		
		return pq.asList(FetchOptions.Builder.withDefaults());
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
	

	/**
	 * Récupère la liste des giveaways ouverts dont la date de fermeture est passée.
	 * @param detached
	 * @return
	 */
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
	
	/**
	 * Supprime un giveaway du datastore en retirant les liens vers celui-ci dans les profils de l'auteur et des participants.
	 * @param key
	 */
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

	/**
	 * Ferme le persistanceManager utilisé par cette classe.
	 */
	public static void closePm() {
		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();
		pm.close();
	}

}
