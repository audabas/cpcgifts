package fr.cpcgifts.persistance;

import static fr.cpcgifts.persistance.OfyService.ofy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.googlecode.objectify.Key;

import fr.cpcgifts.model.Comment;
import fr.cpcgifts.model.CpcUser;
import fr.cpcgifts.utils.DateTools;

/**
 * Utilitaire permettant de récupérer des entités de type Comment depuis le datastore.
 * @author bastien
 */
public class CommentPersistance {

	/**
	 * Récupère un commentaire à l'aide de sa clé.
	 * @param key
	 * @return
	 */
	public static Comment getComment(Key<Comment> key) {
		Comment res = null;

		res = ofy().load().key(key).safe();

		return res;
	}
	
	public static void updateOrCreate(Comment request) {
		ofy().save().entity(request).now();
	}
	
	public static void delete(Key<Comment> key) {
		ofy().delete().key(key);
	}
	
	/**
	 * Change l'auteur de tous le commentaires (utilisé lors de la fusion de profil).
	 * @param oldUser l'utilisateur qui sera supprimé
	 * @param newUser le nouvel utilisateur
	 */
	public static void changeCommentsAuthor(Key<CpcUser> oldUser, Key<CpcUser> newUser) {
		List<Comment> comments = ofy().load().type(Comment.class).filter("author", oldUser).list();
		
		for(Comment c : comments) {
			c.setAuthor(newUser);
		}
		
		ofy().save().entities(comments);
	}
	
	/**
	 * Récupère un ensemble de commentaires depuis le cache. Les place dans le cache s'ils n'y sont pas déjà.
	 * @param keys
	 * @return
	 */
	public static List<Comment> getAll(Collection<Key<Comment>> keys, boolean sorted) {
		List<Comment> res = null;
		
		res = new ArrayList<Comment>(ofy().load().keys(keys).values());

		if(sorted) {
			DateTools.sortCommentsByDate(res);
		}
		
		return res;
	}

}
