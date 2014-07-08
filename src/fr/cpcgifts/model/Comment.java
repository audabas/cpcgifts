package fr.cpcgifts.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Unindex;

import fr.cpcgifts.utils.TextTools;

@Cache
@Unindex
@Entity
public class Comment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id Long id;
	
	private Key<CpcUser> author;
	
	private Key<Giveaway> giveaway;
	
	private String commentText;
	
	private Date commentDate;
	
	public Comment() {
		super();
	}
	
	public Comment(Key<CpcUser> author, Key<Giveaway> giveaway, String commentText) {
		this();
		
		this.author = author;
		this.giveaway = giveaway;
		this.commentText = TextTools.escapeHtml(commentText);
		
		Calendar c = Calendar.getInstance();
		this.commentDate = c.getTime();
	}

	public Key<Comment> getKey() {
		return Key.create(Comment.class, id);
	}

	public Key<CpcUser> getAuthor() {
		return author;
	}

	public void setAuthor(Key<CpcUser> author) {
		this.author = author;
	}

	public Key<Giveaway> getGiveaway() {
		return giveaway;
	}

	public void setGiveaway(Key<Giveaway> giveaway) {
		this.giveaway = giveaway;
	}

	public String getCommentText() {
		return commentText;
	}

	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}

	public Date getCommentDate() {
		return commentDate;
	}

	public void setCommentDate(Date commentDate) {
		this.commentDate = commentDate;
	}

	@Override
	public String toString() {
		return "Comment [key=" + getKey() + ", author=" + author + ", giveaway="
				+ giveaway + ", commentDate=" + commentDate + ", commentText=" + commentText + " ]";
	}
	
	
	
}
