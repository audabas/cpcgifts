package fr.cpcgifts.persistance;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;

import fr.cpcgifts.model.AdminRequest;

/**
 * Utilitaire permettant de récupérer des entités de type AdminRequest depuis le datastore.
 * @author bastien
 *
 */
public class AdminRequestPersistance {
	
	public static void makePersistent(AdminRequest request) {
		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();
		
		try {
			pm.makePersistent(request);
			
			Cache cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());
			cache.remove("allRequests");
		} catch (CacheException e) {
		} finally {
			pm.close();
		}
	}
	
	public static void deleteAdminRequest(Key key) {
		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();
		Cache cache;
		
			try {
				
				AdminRequest ar = pm.getObjectById(AdminRequest.class, key);
				pm.deletePersistent(ar);
				
				cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());
				cache.remove("allRequests");
			} catch (CacheException e) {
			}
	}

	/**
	 * Récupère une requête à l'aide de sa clé.
	 * @param key
	 * @return
	 */
	public static AdminRequest getRequest(Key key) {
		AdminRequest res = null;

		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();

		try {
			res = pm.getObjectById(AdminRequest.class, key);
			res = pm.detachCopy(res);
		} finally {
			pm.close();
		}

		return res;
	}
	
	/**
	 * Récupère une requête depuis le cache s'il y est, le place dans le cache sinon.
	 * @param key 
	 * @return 
	 * @throws JDOObjectNotFoundException si la clé ne correspond à aucune entité dans le datastore.
	 */
	public static AdminRequest getRequestFromCache(Key key) throws JDOObjectNotFoundException {
		AdminRequest res = null;
		
		try {
			Cache cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());

			res = (AdminRequest) cache.get(key);

			if (res == null) { // s'il n'est pas dans le cache, on le met en cache
				res = getRequest(key);
				cache.put(key, res);
			}
		} catch (CacheException e) {
			res = getRequest(key);
		}
		
		return res;
	}
	
	/**
	 * Récupère un ensemble de commentaires depuis le cache. Les place dans le cache s'ils n'y sont pas déjà.
	 * @param keys
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<Key, AdminRequest> getAllFromCache(Collection<Key> keys) {
		Map<Key, AdminRequest> res = null;
		
		try {
			Cache cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());

			res = cache.getAll(keys);

			Collection<Key> notCachedKeys = CollectionUtils.subtract(keys, res.keySet());

			for (Key k : notCachedKeys) {
				AdminRequest c = getRequestFromCache(k);
				res.put(k, c);
			}

		} catch (CacheException e) {
			List<AdminRequest> comments = getRequests(new ArrayList<>(keys));
			res = new HashedMap();
			
			for(AdminRequest c : comments) {
				res.put(c.getKey(), c);
			}
		}
		
		return res;
	}
	
	
	/**
	 * Récupère une liste de requêtes à partir de leur clés.
	 * @param keys
	 * @return
	 */
	public static List<AdminRequest> getRequests(List<Key> keys) {
		List<AdminRequest> res = new ArrayList<AdminRequest>();

		if (keys.size() == 0)
			return res;

		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();

		try {

			for (Key k : keys) {

				AdminRequest c = pm.getObjectById(AdminRequest.class,k);

				res.add(pm.detachCopy(c));

			}
		} finally {
			pm.close();
		}

		return res;
	}

	/**
	 * Récupère la liste des requêtes encore ouvertes.
	 * @param detached
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<AdminRequest> getAllRequestsFromCache() {
		List<AdminRequest> res;
		
		Cache cache;
		
		Map<String, Object> props = new HashMap<String, Object>();
	    props.put(GCacheFactory.EXPIRATION_DELTA, 600); // on garde la liste en cache 10 minutes
		
		try {
	        cache = CacheManager.getInstance().getCacheFactory().createCache(props);
	                    
	        res = (List<AdminRequest>) cache.get("allRequests");
			
			if(res == null) {
				res = getAllRequests();
				cache.put("allRequests", res);
			}
			
	    } catch (CacheException e) {
	    	res = AdminRequestPersistance.getAllRequests();
	    }
		
		return res;
		
	}

	/**
	 * Récupère la liste des requêtes encore ouvertes.
	 * @param detached
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<AdminRequest> getAllRequests() {
		List<AdminRequest> res;

		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();

		Query query = pm.newQuery(AdminRequest.class);
		query.setOrdering("requestDate desc");

		try {
			res = (List<AdminRequest>) query.execute();
			res = (List<AdminRequest>) pm.detachCopyAll(res);
		} finally {
			query.closeAll();
			pm.close();
		}

		return res;

	}
	
	/**
	 * Récupère la liste des requêtes encore ouvertes.
	 * @param detached
	 * @return
	 */
	public static List<Entity> getAllRequestsKeys() {

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
		com.google.appengine.api.datastore.Query q = new com.google.appengine.api.datastore.Query(AdminRequest.class.getSimpleName());
		q.addSort("requestDate", SortDirection.DESCENDING);
		q.setKeysOnly();
	
		PreparedQuery pq = datastore.prepare(q);
		
		return pq.asList(FetchOptions.Builder.withDefaults());

	}
	
	/**
	 * Récupère la liste des requêtes encore ouvertes.
	 * @param detached
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Entity> getAllRequestsKeysFromCache() {
		List<Entity> res;
		
		Cache cache;
		
		Map<String, Object> props = new HashMap<String, Object>();
	    props.put(GCacheFactory.EXPIRATION_DELTA, 600); // on garde la liste en cache 10 minutes
		
		try {
	        cache = CacheManager.getInstance().getCacheFactory().createCache(props);
	                    
	        res = (List<Entity>) cache.get("allRequestsKeys");
			
			if(res == null) {
				res = getAllRequestsKeys();
				cache.put("allRequestsKeys", res);
			}
			
	    } catch (CacheException e) {
	    	res = getAllRequestsKeys();
	    }
		
		return res;
		
	}
	
	/**
	 * Récupère la liste des requêtes ouvertes.
	 */
	public static List<Entity> getOpenAdminRequestsKeys() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Filter openarFilter = new FilterPredicate("state", FilterOperator.EQUAL, AdminRequest.State.Open.name());
		
		com.google.appengine.api.datastore.Query q = new com.google.appengine.api.datastore.Query(AdminRequest.class.getSimpleName());
		q.setKeysOnly();
		q.setFilter(openarFilter);
	
		PreparedQuery pq = datastore.prepare(q);
		
		return pq.asList(FetchOptions.Builder.withDefaults());
	}
	
	/**
	 * Récupère la liste des requêtes fermées depuis plus de 2 semaines.
	 * @param detached
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<AdminRequest> getRequestsToDelete() {
		List<AdminRequest> res;

		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();

		Calendar c = Calendar.getInstance();
		c.add(Calendar.WEEK_OF_YEAR, -2);
		
		Query query = pm.newQuery(AdminRequest.class);
		query.setFilter("(state == stateParam1 || state == stateParam2) && requestDate < twoWeeksAgo");
		query.declareParameters("String stateParam1, String stateParam2," + Date.class.getName() +  " twoWeeksAgo");
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("stateParam1", AdminRequest.State.Denied.name());
		parameters.put("stateParam2", AdminRequest.State.Processed.name());
		parameters.put("twoWeeksAgo", c.getTime());

		try {
			res = (List<AdminRequest>) query.executeWithMap(parameters);
			res = (List<AdminRequest>) pm.detachCopyAll(res);
		} finally {
			query.closeAll();
			pm.close();
		}

		return res;

	}
	
}
