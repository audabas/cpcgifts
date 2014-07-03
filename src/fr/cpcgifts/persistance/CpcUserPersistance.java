package fr.cpcgifts.persistance;

import static fr.cpcgifts.persistance.OfyService.ofy;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheManager;

import com.googlecode.objectify.Key;

import fr.cpcgifts.model.CpcUser;
import fr.cpcgifts.model.Giveaway;

/**
 * Utilitaire permettant de récupérer des entités de type CpcUser depuis le
 * datastore.
 * 
 * @author bastien
 */
public class CpcUserPersistance {

	public static void updateOrCreate(CpcUser u) {
		ofy().save().entity(u);
	}

	public static void delete(Key<CpcUser> key) {
		ofy().delete().key(key);
	}

	/**
	 * Récupère un utilisateur dans le datastore grâce à son id google.
	 * 
	 * @param id
	 *            L'identifiant google de l'utilisateur à récupérer.
	 * @return
	 */
	public static CpcUser getCpcUser(String id) {
		CpcUser res = null;
		
		res = ofy().load().type(CpcUser.class).filter("id", id).first().now();

		return res;
	}

	/**
	 * Récupère un utilisateur grâce à sa clé.
	 * 
	 * @param key
	 * @return
	 */
	public static CpcUser getCpcUser(Key<CpcUser> key) {
		CpcUser res = null;

		res = ofy().load().key(key).safe();

		return res;
	}

	/**
	 * Récupère un utilisateur à l'aide de son id sur le forum canard pc.
	 * 
	 * @param cpcProfileId
	 *            L'identifiant de l'utilisateur sur le forum canard pc.
	 * @param detached
	 * @return
	 */
	public static CpcUser getCpcUserByCpcProfileId(String cpcProfileId) {
		CpcUser res = null;

		res = ofy().load().type(CpcUser.class).filter("cpcProfileId", cpcProfileId).first().now();

		return res;
	}

	/**
	 * Récupère un ensemble d'utilisateurs depuis le cache. Les place dans le
	 * cache s'ils n'y sont pas déjà.
	 * 
	 * @param keys
	 * @return
	 */
	public static Map<Key<CpcUser>, CpcUser> getAll(Collection<Key<CpcUser>> keys) {
		Map<Key<CpcUser>, CpcUser> res = null;

		res = ofy().load().keys(keys);

		return res;
	}

	/**
	 * Récupère le nombre de gifts envoyés par l'utilisateur donné
	 */
	public static int getContributionValue(Key<CpcUser> user) {
		Integer res = null;

		try {
			Cache cache = CacheManager.getInstance().getCacheFactory()
					.createCache(Collections.emptyMap());

			res = (Integer) cache.get("contribution-" + user.getId());

			if (res == null) { // s'il n'est pas dans le cache, on le met en
								// cache
				res = 0;

				CpcUser u = getCpcUser(user);
				List<Giveaway> gas = GiveawayPersistance.getAll(u.getGiveaways(),
						false);

				for (Giveaway ga : gas) {
					res += ga.getWinners().size();
				}

				cache.put("contribution-" + user.getId(), res);
			}
		} catch (CacheException e) {
			res = 0;
		}

		return res;
	}

	/**
	 * Récupère tout les utilisateurs enregistrés dans le datastore.
	 * 
	 * @return
	 */
	public static List<CpcUser> getAllUsers() {
		List<CpcUser> res;

		res = ofy().load().type(CpcUser.class).order("cpcNickname").list();

		return res;
	}

	public static int getAllUserCountFromCache() {
		Integer res = null;

		Cache cache;

		try {
			cache = CacheManager.getInstance().getCacheFactory()
					.createCache(Collections.emptyMap());

			res = (Integer) cache.get("usersCount");

			if (res == null) {
				res = ofy().load().type(CpcUser.class).count();
				cache.put("usersCount", res);
			}

		} catch (CacheException e) {
		}

		return res;
	}

	/**
	 * Fusionne les giveaways de deux profils en un seul.
	 */
	public static void cpcusersFusion(Key<CpcUser> profileToKeep,
			Key<CpcUser> profileToDelete) {
		CpcUser userToKeep = getCpcUser(profileToKeep);
		CpcUser userToDelete = getCpcUser(profileToDelete);

		userToKeep.addGiveaways(userToDelete.getGiveaways());
		for (Key<Giveaway> k : userToDelete.getGiveaways()) {
			Giveaway ga = GiveawayPersistance.getGA(k);
			ga.setAuthor(profileToKeep);
			GiveawayPersistance.updateOrCreate(ga);
		}

		userToKeep.addEntries(userToDelete.getEntries());
		for (Key<Giveaway> k : userToDelete.getEntries()) {
			Giveaway ga = GiveawayPersistance.getGA(k);
			ga.removeEntrant(profileToDelete);
			ga.addEntrant(profileToKeep);
			GiveawayPersistance.updateOrCreate(ga);
		}

		userToKeep.addWon(userToDelete.getWon());
		for (Key<Giveaway> k : userToDelete.getWon()) {
			Giveaway ga = GiveawayPersistance.getGA(k);
			ga.removeWinner(profileToDelete);
			ga.addWinner(profileToKeep);
			GiveawayPersistance.updateOrCreate(ga);
		}
		
		CommentPersistance.changeCommentsAuthor(profileToDelete, profileToKeep);

		updateOrCreate(userToKeep);
		delete(profileToDelete);

	}

}
