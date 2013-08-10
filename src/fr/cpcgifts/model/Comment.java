package fr.cpcgifts.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

import fr.cpcgifts.utils.TextTools;

@PersistenceCapable(detachable="true")
public class Comment implements Serializable {
	private static final long serialVersionUID = 1L;

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	@Persistent
	private Key author;
	
	@Persistent
	private Key giveaway;
	
	@Persistent
	private Text commentText;
	
	@Persistent
	private Date commentDate;
	
	public Comment(Key author, Key giveaway, String commentText) {
		this.author = author;
		this.giveaway = giveaway;
		this.commentText = new Text(TextTools.escapeHtml(commentText));
		
		Calendar c = Calendar.getInstance();
		this.commentDate = c.getTime();
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
		return commentText.getValue();
	}

	public void setCommentText(String commentText) {
		this.commentText = new Text(commentText);
	}

	public Date getCommentDate() {
		return commentDate;
	}

	public void setCommentDate(Date commentDate) {
		this.commentDate = commentDate;
	}

	@Override
	public String toString() {
		return "Comment [key=" + key + ", author=" + author + ", giveaway="
				+ giveaway + ", commentDate=" + commentDate + "]";
	}
	
	
	
}
