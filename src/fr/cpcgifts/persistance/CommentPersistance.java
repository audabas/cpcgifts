package fr.cpcgifts.persistance;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;

import fr.cpcgifts.model.Comment;

public class CommentPersistance {

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
