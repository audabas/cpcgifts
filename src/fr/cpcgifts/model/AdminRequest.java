package fr.cpcgifts.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Unindex;

@Cache
@Unindex
@Entity
public class AdminRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id Long id;

	public enum Type {
		Reroll,
		TitleModification,
		RulesModification,
		ReportPost,
		ReportUser
	}
	
	public enum State {
		Open,
		Denied,
		Processed
	}
	
	private Key<CpcUser> author;
	
	private Type type;
	
	@Index private State state;
	
	private Key<?> attachment;
	
	private String text;
	
	@Index private Date requestDate;
	
	/** L'admin qui à traité la requête. */
	private Key<CpcUser> consideredBy;
	
	public AdminRequest() {
		super();
	}
	
	public AdminRequest(Key<CpcUser> author, Type type, Key<?> attachment, String text) {
		super();
		this.author = author;
		this.type = type;
		this.attachment = attachment;
		this.text = text;
		
		Calendar c = Calendar.getInstance();
		this.requestDate = c.getTime();
		
		this.state = State.Open;
	}



	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public Key<CpcUser> getAuthor() {
		return author;
	}

	public void setAuthor(Key<CpcUser> author) {
		this.author = author;
	}

	public Key<?> getAttachment() {
		return attachment;
	}

	public void setAttachment(Key<?> attachment) {
		this.attachment = attachment;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public Key<CpcUser> getConsideredBy() {
		return consideredBy;
	}

	public void setConsideredBy(Key<CpcUser> consideredBy) {
		this.consideredBy = consideredBy;
	}

	public Long getId() {
		return id;
	}
	
	public Key<AdminRequest> getKey() {
		return Key.create(AdminRequest.class, id);
	}

	@Override
	public String toString() {
		return "Request [key=" + getId() + ", author=" + getAuthor() + ", attachment="
				+ getAttachment() + ", requestDate=" + getRequestDate() + "]";
	}
	
	
	
}
