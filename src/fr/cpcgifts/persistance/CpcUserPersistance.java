package fr.cpcgifts.persistance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

import com.google.appengine.api.datastore.Key;

import fr.cpcgifts.model.CpcUser;
import fr.cpcgifts.model.Giveaway;

/**
 * Utilitaire permettant de récupérer des entités de type CpcUser depuis le datastore.
 * @author bastien
 *
 */
public class CpcUserPersistance {

	/**
	 * Récupère un utilisateur dans le datastore grâce à son id google.
	 * @param id L'identifiant google de l'utilisateur à récupérer.
	 * @return
	 */
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
	
	/**
	 * Récupère un utilisateur grâce à sa clé.
	 * @param key
	 * @return
	 */
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
	
	/**
	 * @see getCpcUserByKey
	 * @param key
	 * @return
	 * @throws JDOObjectNotFoundException
	 */
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
	
	/**
	 * Récupère un utilisateur depuis le cache s'il y est, le place dans le cache sinon.
	 * @param key La clé de l'utilisateur à récupérer.
	 * @return Le giveaway demandé.
	 * @throws JDOObjectNotFoundException si la clé ne correspond à aucune entité dans le datastore.
	 */
	public static CpcUser getUserFromCache(Key key) throws JDOObjectNotFoundException {
		CpcUser res = null;
		
		try {
			Cache cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());

			res = (CpcUser) cache.get(key);

			if (res == null) { // s'il n'est pas dans le cache, on le met en cache
				res = getCpcUserByKey(key);
				cache.put(key, res);
			}
		} catch (CacheException e) {
			res = getCpcUserByKey(key);
		}
		
		return res;
	}

	/**
	 * Récupère un utilisateur à l'aide de son id sur le forum canard pc.
	 * @param cpcProfileId L'identifiant de l'utilisateur sur le forum canard pc.
	 * @param detached
	 * @return
	 */
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
	
	/**
	 * Récupère un ensemble d'utilisateurs depuis le cache. Les place dans le cache s'ils n'y sont pas déjà.
	 * @param keys
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<Key, CpcUser> getAllFromCache(Collection<Key> keys) {
		Map<Key, CpcUser> res = null;
		
		try {
			Cache cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());

			res = cache.getAll(keys);

			Collection<Key> notCachedKeys = CollectionUtils.subtract(keys, res.keySet());

			for (Key k : notCachedKeys) {
				CpcUser u = getUserFromCache(k);
				res.put(k, u);
			}

		} catch (CacheException e) {
			List<CpcUser> cpcUsers = getCpcUsers(new ArrayList<>(keys), true);
			res = new HashedMap();
			
			for(CpcUser u : cpcUsers) {
				res.put(u.getKey(), u);
			}
		}
		
		return res;
	}
	
	/**
	 * Récupère une liste d'utilisateurs à partir de leur clés.
	 * @param keys
	 * @param detached
	 * @return
	 */
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
	
	/**
	 * Récupère le nombre de gifts envoyés par l'utilisateur donné
	 */
	public static int getContributionValue(Key user) {
		Integer res = null;
		
		try {
			Cache cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());

			res = (Integer) cache.get("contribution-" + user.getId());

			if (res == null) { // s'il n'est pas dans le cache, on le met en cache
				res = 0;
				
				CpcUser u = getUserFromCache(user);
				Map<Key, Giveaway> gas = GAPersistance.getAllFromCache(u.getGiveaways());
				
				for(Giveaway ga : gas.values()) {
					res += ga.getWinners().size();
				}
				
				cache.put("contribution-" + user.getId(), res);
			}
		} catch (CacheException e) {
			res = 0;
			
			CpcUser u = getUserFromCache(user);
			Map<Key, Giveaway> gas = GAPersistance.getAllFromCache(u.getGiveaways());
			
			for(Giveaway ga : gas.values()) {
				res += ga.getWinners().size();
			}
		}
		
		return res;
	}

	/**
	 * Récupère tout les utilisateurs enregistrés dans le datastore.
	 * @return
	 */
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

	/**
	 * Ferme le persistance manager utilisé par cette instance.
	 */
	public static void closePm() {
		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();
		pm.close();
	}

}
