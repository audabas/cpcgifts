package fr.cpcgifts.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

	/** Profil d'utilisateur google. */
	@Persistent(defaultFetchGroup="true")
	private User guser;

	@Persistent
	private String cpcProfileId;

	@Persistent
	private String cpcNickname;

	@Persistent
	private String avatarUrl = "/img/avatar.jpg";
	
	@Persistent
	private boolean banned;
	
	/** Liste des concours créés. */
	@Persistent(defaultFetchGroup="true")
	private Set<Key> giveawaySet;
	
	/** Liste des participations aux concours. */
	@Persistent(defaultFetchGroup="true")
	private Set<Key> entrySet;
	
	/** Liste des concours gagnés */
	@Persistent(defaultFetchGroup="true")
	private Set<Key> wonSet;
	
	/**	 Liste des liens vers les profils externes (steam, etc...) */
	@Persistent(serialized="true",defaultFetchGroup="true")
	private HashMap<String,String> profilesMap;
	
	public CpcUser() {
		super();
		
		this.giveawaySet = new HashSet<Key>();	

		this.entrySet = new HashSet<Key>();
		
		this.profilesMap = new HashMap<String, String>();
	}
	
	public CpcUser(User guser, String cpcProfileId) {
		this();
		this.guser = guser;

		// paramètres générés
		this.id = guser.getUserId();

		String[] splitProfileId = cpcProfileId.split("-",2);

		this.cpcNickname = splitProfileId[1];

		this.cpcProfileId = splitProfileId[0];

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
		if(this.giveawaySet == null)
			this.giveawaySet = new HashSet<Key>();
			
		return giveawaySet;
	}

	public void setGiveaways(Set<Key> giveaways) {
		this.giveawaySet = giveaways;
	}

	public boolean addGiveaway(Key ga) {
		getGiveaways();
		
		return this.giveawaySet.add(ga);

	}
	
	public boolean addGiveaways(Collection<Key> gas) {
		getGiveaways();
		
		return this.giveawaySet.addAll(gas);

	}
	
	public boolean removeGiveaway(Key ga) {
		getGiveaways();
		
		return this.giveawaySet.remove(ga);
	}

	public Set<Key> getEntries() {
		if(entrySet == null)
			entrySet = new HashSet<Key>();
		
		return entrySet;
	}

	public void setEntries(Set<Key> entries) {
		this.entrySet = entries;
	}
	
	public boolean addEntry(Key k) {
		getEntries();
		
		return this.entrySet.add(k);

	}
	
	public boolean addEntries(Collection<Key> keys) {
		getEntries();
		
		return this.entrySet.addAll(keys);

	}
	
	public boolean removeEntry(Key k) {
		getEntries();
		
		return this.entrySet.remove(k);
	}

	public Set<Key> getWon() {
		if(wonSet == null)
			wonSet = new HashSet<Key>();
			
		return wonSet;
	}

	public void setWon(Set<Key> won) {
		this.wonSet = won;
	}
	
	public boolean addWon(Key k) {
		getWon();
		
		return this.wonSet.add(k);

	}
	
	public boolean addWon(Collection<Key> keys) {
		getWon();
		
		return this.wonSet.addAll(keys);

	}
	
	public boolean removeWon(Key k) {
		getWon();
		
		return this.wonSet.remove(k);

	}
	
	public Map<String,String> getProfiles() {
		if(profilesMap == null)
			this.profilesMap = new HashMap<String, String>();
		
		return profilesMap;
	}
	
	public void addProfile(String key, String link) {
		getProfiles();
		
		this.profilesMap.put(key, link);
	}
	
	
	public boolean isBanned() {
		return banned;
	}


	public void setBanned(boolean banned) {
		this.banned = banned;
	}

	@Override
	public String toString() {
		return "CpcUser [key=" + key + ", id=" + id + ", guser=" + guser
				+ ", cpcProfileId=" + cpcProfileId + ", cpcNickname="
				+ cpcNickname + "]";
	}

}
