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

@PersistenceCapable(detachable="true")
public class AdminRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

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
	
	@Persistent
	private Key author;
	
	@Persistent
	private Type type;
	
	@Persistent
	private State state;
	
	@Persistent
	private Key attachment;
	
	@Persistent
	private Text text;
	
	@Persistent
	private Date requestDate;
	
	/** L'admin qui à traité la requête. */
	@Persistent
	private Key consideredBy;
	
	public AdminRequest() {
		super();
	}
	
	public AdminRequest(Key author, Type type, Key attachment, String text) {
		super();
		this.author = author;
		this.type = type;
		this.attachment = attachment;
		this.text = new Text(text);
		
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

	public Key getAuthor() {
		return author;
	}

	public void setAuthor(Key author) {
		this.author = author;
	}

	public Key getAttachment() {
		return attachment;
	}

	public void setAttachment(Key attachment) {
		this.attachment = attachment;
	}

	public String getText() {
		return text.getValue();
	}

	public void setText(String text) {
		this.text = new Text(text);
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public Key getConsideredBy() {
		return consideredBy;
	}

	public void setConsideredBy(Key consideredBy) {
		this.consideredBy = consideredBy;
	}

	public Key getKey() {
		return key;
	}

	@Override
	public String toString() {
		return "Request [key=" + key + ", author=" + author + ", attachment="
				+ attachment + ", requestDate=" + requestDate + "]";
	}
	
	
	
}
