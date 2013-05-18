package fr.cpcgifts.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
	private List<Key> giveaways;

	@Persistent
	private List<Key> entries;
	
	@Persistent
	private List<Key> won;


	public CpcUser(User guser, String cpcProfileId) {
		super();
		this.guser = guser;

		// paramètres générés
		this.id = guser.getUserId();

		String[] splitProfileId = cpcProfileId.split("-",2);

		this.cpcNickname = splitProfileId[1];

		this.cpcProfileId = splitProfileId[0];

		this.giveaways = new ArrayList<Key>();	

		this.entries = new ArrayList<Key>();

	}

	public CpcUser(Key key, String id, User guser, String cpcProfileId,
			String cpcNickname, String avatarUrl, List<Key> giveaways,
			List<Key> entries) {
		super();
		this.key = key;
		this.id = id;
		this.guser = guser;
		this.cpcProfileId = cpcProfileId;
		this.cpcNickname = cpcNickname;
		this.avatarUrl = avatarUrl;
		this.giveaways = giveaways;
		this.entries = entries;
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

	public List<Key> getGiveaways() {
		if(this.giveaways == null)
			this.giveaways = new ArrayList<Key>();
			
		return giveaways;
	}

	public void setGiveaways(List<Key> giveaways) {
		this.giveaways = giveaways;
	}

	public void addGiveaway(Key ga) {
		getGiveaways();
		
		this.giveaways.add(ga);

	}

	public List<Key> getEntries() {
		if(entries == null)
			entries = new ArrayList<Key>();
		
		return entries;
	}

	public void setEntries(List<Key> entries) {
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

	public List<Key> getWon() {
		if(won == null)
			won = new ArrayList<Key>();
			
		return won;
	}

	public void setWon(List<Key> won) {
		this.won = won;
	}
	
	public void addWon(Key k) {
		getWon();
		
		this.won.add(k);

	}

	@Override
	public String toString() {
		return "CpcUser [key=" + key + ", id=" + id + ", guser=" + guser
				+ ", cpcProfileId=" + cpcProfileId + ", cpcNickname="
				+ cpcNickname + "]";
	}

}
