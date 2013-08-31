package fr.cpcgifts.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;

@PersistenceCapable(detachable="true")
public class CpcUser implements Serializable {
	private static final long serialVersionUID = 1L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private String id;

	@Persistent
	private User guser;

	@Persistent
	private String cpcProfileId;

	@Persistent
	private String cpcNickname;

	@Persistent
	private String avatarUrl = "/img/avatar.jpg";

	@Persistent
	private Set<Key> giveaways;

	@Persistent
	private Set<Key> entries;
	
	@Persistent
	private Set<Key> won;


	public CpcUser(User guser, String cpcProfileId) {
		super();
		this.guser = guser;

		// paramètres générés
		this.id = guser.getUserId();

		String[] splitProfileId = cpcProfileId.split("-",2);

		this.cpcNickname = splitProfileId[1];

		this.cpcProfileId = splitProfileId[0];

		this.giveaways = new HashSet<Key>();	

		this.entries = new HashSet<Key>();

	}


	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public User getGuser() {
		return guser;
	}

	public void setGuser(User guser) {
		this.id = guser.getUserId();
		this.guser = guser;
	}

	public String getCpcProfileUrl() {
		return cpcProfileId;
	}

	public void setCpcProfileUrl(String cpcProfileUrl) {
		this.cpcProfileId = cpcProfileUrl;
	}

	public String getCpcNickname() {
		return cpcNickname;
	}

	public void setCpcNickname(String cpcNickname) {
		this.cpcNickname = cpcNickname;
	}

	public String getCpcProfileId() {
		return cpcProfileId;
	}

	public void setCpcProfileId(String cpcProfileId) {
		this.cpcProfileId = cpcProfileId;
	}



	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public Set<Key> getGiveaways() {
		if(this.giveaways == null)
			this.giveaways = new HashSet<Key>();
			
		return giveaways;
	}

	public void setGiveaways(Set<Key> giveaways) {
		this.giveaways = giveaways;
	}

	public void addGiveaway(Key ga) {
		getGiveaways();
		
		this.giveaways.add(ga);

	}
	
	public void removeGiveaway(Key ga) {
		getGiveaways();
		
		this.giveaways.remove(ga);
	}

	public Set<Key> getEntries() {
		if(entries == null)
			entries = new HashSet<Key>();
		
		return entries;
	}

	public void setEntries(Set<Key> entries) {
		this.entries = entries;
	}
	
	public void addEntry(Key k) {
		getEntries();
		
		this.entries.add(k);

	}
	
	public boolean removeEntry(Key k) {
		getEntries();
		
		return this.entries.remove(k);
	}

	public Set<Key> getWon() {
		if(won == null)
			won = new HashSet<Key>();
			
		return won;
	}

	public void setWon(Set<Key> won) {
		this.won = won;
	}
	
	public void addWon(Key k) {
		getWon();
		
		this.won.add(k);

	}
	
	public void removeWon(Key k) {
		getWon();
		
		this.won.remove(k);

	}

	@Override
	public String toString() {
		return "CpcUser [key=" + key + ", id=" + id + ", guser=" + guser
				+ ", cpcProfileId=" + cpcProfileId + ", cpcNickname="
				+ cpcNickname + "]";
	}

}
