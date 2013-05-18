package fr.cpcgifts.model;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

import fr.cpcgifts.utils.TextTools;

@PersistenceCapable(detachable="true")
public class Comment {
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	@Persistent
	private Key author;
	
	@Persistent
	private Key giveaway;
	
	@Persistent
	private String commentText;
	
	public Comment(Key author, Key giveaway, String commentText) {
		this.author = author;
		this.giveaway = giveaway;
		this.commentText = TextTools.escapeHtml(commentText);
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public Key getAuthor() {
		return author;
	}

	public void setAuthor(Key author) {
		this.author = author;
	}

	public Key getGiveaway() {
		return giveaway;
	}

	public void setGiveaway(Key giveaway) {
		this.giveaway = giveaway;
	}

	public String getCommentText() {
		return commentText;
	}

	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}
	
	

}
