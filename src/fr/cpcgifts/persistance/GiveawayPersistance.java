package fr.cpcgifts.persistance;

import static fr.cpcgifts.persistance.OfyService.ofy;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheManager;

import fr.cpcgifts.model.CpcUser;
import fr.cpcgifts.model.Giveaway;
import fr.cpcgifts.utils.DateTools;

/**
 * Utilitaire permettant de récupérer des entités de type giveaway depuis le
 * datastore.
 * 
 * @author bastien
 */
public class GiveawayPersistance {

	public static void updateOrCreate(Giveaway g) {
		ofy().save().entity(g).now();
	}

	public static void delete(Key<Giveaway> key) {
		ofy().delete().key(key);
	}

	/**
	 * Récupère un giveaway à partir de sa clé.
	 * 
	 * @param key
	 *            La clé du giveaway demandé.
	 * @return Le giveaway correspondant.
	 * @throws JDOObjectNotFoundException
	 *             si la clé ne correspond à aucune entité dans le datastore.
	 */
	public static Giveaway getGA(Key<Giveaway> key) throws NotFoundException {
		Giveaway res;

		res = ofy().load().key(key).safe();

		return res;
	}

	/**
	 * Récupère un ensemble de giveaways.
	 * 
	 * @param keys
	 * @return
	 */
	public static List<Giveaway> getAll(Collection<Key<Giveaway>> keys,	boolean sorted) {
		List<Giveaway> res = null;
		
		res = new ArrayList<Giveaway>(ofy().load().keys(keys).values());
		
		if(sorted) {
			DateTools.sortGiveawaysByEndDate(res);
		}

		return res;
	}

	public static int getAllGACount() {
		Integer res = null;

		Cache cache;

		try {
			cache = CacheManager.getInstance().getCacheFactory()
					.createCache(Collections.emptyMap());

			res = (Integer) cache.get("allGACount");

			if (res == null) {
				res = ofy().load().type(Giveaway.class).count();
				cache.put("allGACount", res);
			}

		} catch (CacheException e) {
		}

		return res;
	}

	public static List<Giveaway> getOpenGAs() {
		List<Giveaway> res;

		res = ofy().load().type(Giveaway.class).filter("open", true)
				.order("endDate").list();

		return res;
	}

	/**
	 * Récupère les concours terminés sans participants inscrits.
	 * 
	 * @param detached
	 * @return
	 */
	public static List<Giveaway> getClosedEmptyGAs() {
		List<Giveaway> res;

		Calendar c = Calendar.getInstance();
		c.add(Calendar.WEEK_OF_YEAR, -2);

		res = ofy().load().type(Giveaway.class).filter("open", false)
				.filter("endDate <", c.getTime()).filter("nbWinners", 0).list();

		return res;
	}

	/**
	 * Récupère la liste des giveaways ouverts dont la date de fermeture est
	 * passée.
	 * 
	 * @param detached
	 * @return
	 */
	public static List<Giveaway> getOpenGAsToClose() {
		List<Giveaway> res;

		Calendar c = Calendar.getInstance();

		res = ofy().load().type(Giveaway.class).filter("open", true)
				.filter("endDate <", c.getTime()).list();

		return res;
	}

	/**
	 * Supprime un giveaway du datastore en retirant les liens vers celui-ci
	 * dans les profils de l'auteur et des participants.
	 * 
	 * @param key
	 */
	public static void deleteGa(Key<Giveaway> key) {

		Giveaway ga = getGA(key);

		CpcUser author = CpcUserPersistance.getCpcUser(ga.getAuthor());

		author.removeGiveaway(ga.getKey());
		
		CpcUserPersistance.updateOrCreate(author);

		Set<Key<CpcUser>> entrantsKeys = ga.getEntrants();

		for (Key<CpcUser> k : entrantsKeys) {
			CpcUser cpcuser = CpcUserPersistance.getCpcUser(k);
			cpcuser.removeEntry(ga.getKey());
			CpcUserPersistance.updateOrCreate(cpcuser);
		}

		Set<Key<CpcUser>> winnersKeys = ga.getWinners();

		for (Key<CpcUser> k : winnersKeys) {
			CpcUser cpcuser = CpcUserPersistance.getCpcUser(k);
			cpcuser.removeWon(ga.getKey());
			CpcUserPersistance.updateOrCreate(cpcuser);
		}

		ofy().delete().key(key);
	}

}
