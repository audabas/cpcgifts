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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheManager;

import com.google.appengine.api.datastore.Key;

import fr.cpcgifts.model.Comment;

/**
 * Utilitaire permettant de récupérer des entités de type Comment depuis le datastore.
 * @author bastien
 *
 */
public class CommentPersistance {

	/**
	 * Récupère un commentaire à l'aide de sa clé.
	 * @param key
	 * @return
	 */
	public static Comment getComment(Key key) {
		Comment res = null;

		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();

		try {
			res = pm.getObjectById(Comment.class, key);
			res = pm.detachCopy(res);
		} finally {
			pm.close();
		}

		return res;
	}
	
	/**
	 * Récupère un commentaire depuis le cache s'il y est, le place dans le cache sinon.
	 * @param key La clé du commentaire à récupérer.
	 * @return Le commentaire demandé.
	 * @throws JDOObjectNotFoundException si la clé ne correspond à aucune entité dans le datastore.
	 */
	public static Comment getCommentFromCache(Key key) throws JDOObjectNotFoundException {
		Comment res = null;
		
		try {
			Cache cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());

			res = (Comment) cache.get(key);

			if (res == null) { // s'il n'est pas dans le cache, on le met en cache
				res = getComment(key);
				cache.put(key, res);
			}
		} catch (CacheException e) {
			res = getComment(key);
		}
		
		return res;
	}
	
	/**
	 * Récupère un ensemble de commentaires depuis le cache. Les place dans le cache s'ils n'y sont pas déjà.
	 * @param keys
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<Key, Comment> getAllFromCache(Collection<Key> keys) {
		Map<Key, Comment> res = null;
		
		try {
			Cache cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());

			res = cache.getAll(keys);

			Collection<Key> notCachedKeys = CollectionUtils.subtract(keys, res.keySet());

			for (Key k : notCachedKeys) {
				Comment c = getCommentFromCache(k);
				res.put(k, c);
			}

		} catch (CacheException e) {
			List<Comment> comments = getComments(new ArrayList<>(keys));
			res = new HashedMap();
			
			for(Comment c : comments) {
				res.put(c.getKey(), c);
			}
		}
		
		return res;
	}
	
	
	/**
	 * Récupère une liste de commentaires à partir de leur clés.
	 * @param keys
	 * @return
	 */
	public static List<Comment> getComments(List<Key> keys) {
		List<Comment> res = new ArrayList<Comment>();

		if (keys.size() == 0)
			return res;

		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();

		try {

			for (Key k : keys) {

				Comment c = pm.getObjectById(Comment.class,k);

				res.add(pm.detachCopy(c));

			}
		} finally {
			pm.close();
		}

		return res;
	}

	/**
	 * Récupère tout les commentaires présents dans le datastore.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Comment> getAllComments() {
		List<Comment> res;

		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();

		Query query = pm.newQuery(Comment.class);

		try {
			res = (List<Comment>) query.execute();
			res = (List<Comment>) pm.detachCopyAll(res);
		} finally {
			query.closeAll();
			pm.close();
		}

		return res;
	}

}
