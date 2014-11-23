package fr.cpcgifts.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Serialize;
import com.googlecode.objectify.annotation.Unindex;

@Cache
@Unindex
@Entity
public class CpcUser implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id Long keyId;
	
	@Index private String id;

	/** Profil d'utilisateur google. */
	private User guser;

	@Index private String cpcProfileId;

	@Index private String cpcNickname;

	private String avatarUrl = "/img/avatar.jpg";
	
	private boolean banned;
	
	/** Indique si l'utilisateur accepte l'envoi d'emails. */
	private boolean acceptEmails = false;
	
	/** Liste des concours créés. */
	private Set<Key<Giveaway>> giveawaySet;
	
	/** Liste des participations aux concours. */
	private Set<Key<Giveaway>> entrySet;
	
	/** Liste des concours gagnés */
	private Set<Key<Giveaway>> wonSet;
	
	/**	 Liste des liens vers les profils externes (steam, etc...) */
	@Serialize private HashMap<String,String> profilesMap;
	
	public CpcUser() {
		super();
		
		this.giveawaySet = new HashSet<Key<Giveaway>>();

		this.entrySet = new HashSet<Key<Giveaway>>();
		
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


	public Key<CpcUser> getKey() {
		return Key.create(CpcUser.class, keyId);
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

	public Set<Key<Giveaway>> getGiveaways() {
		if(this.giveawaySet == null)
			this.giveawaySet = (Set<Key<Giveaway>>) new HashSet<Key<Giveaway>>();
			
		return giveawaySet;
	}

	public void setGiveaways(Set<Key<Giveaway>> giveaways) {
		this.giveawaySet = giveaways;
	}

	public boolean addGiveaway(Key<Giveaway> ga) {
		getGiveaways();
		
		return this.giveawaySet.add(ga);

	}
	
	public boolean addGiveaways(Collection<Key<Giveaway>> gas) {
		getGiveaways();
		
		return this.giveawaySet.addAll(gas);

	}
	
	public boolean removeGiveaway(Key<Giveaway> ga) {
		getGiveaways();
		
		return this.giveawaySet.remove(ga);
	}

	public Set<Key<Giveaway>> getEntries() {
		if(entrySet == null)
			entrySet = new HashSet<Key<Giveaway>>();
		
		return entrySet;
	}

	public void setEntries(Set<Key<Giveaway>> entries) {
		this.entrySet = entries;
	}
	
	public boolean addEntry(Key<Giveaway> k) {
		getEntries();
		
		return this.entrySet.add(k);

	}
	
	public boolean addEntries(Collection<Key<Giveaway>> keys) {
		getEntries();
		
		return this.entrySet.addAll(keys);

	}
	
	public boolean removeEntry(Key<Giveaway> k) {
		getEntries();
		
		return this.entrySet.remove(k);
	}

	public Set<Key<Giveaway>> getWon() {
		if(wonSet == null)
			wonSet = new HashSet<Key<Giveaway>>();
			
		return wonSet;
	}

	public void setWon(Set<Key<Giveaway>> won) {
		this.wonSet = won;
	}
	
	public boolean addWon(Key<Giveaway> k) {
		getWon();
		
		return this.wonSet.add(k);

	}
	
	public boolean addWon(Collection<Key<Giveaway>> keys) {
		getWon();
		
		return this.wonSet.addAll(keys);

	}
	
	public boolean removeWon(Key<Giveaway> k) {
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
	
	public void removeProfile(String key) {
		getProfiles();
		
		this.profilesMap.remove(key);
	}	
	
	public boolean isBanned() {
		return banned;
	}


	public void setBanned(boolean banned) {
		this.banned = banned;
	}

	public boolean isAcceptEmails() {
		return acceptEmails;
	}

	public void setAcceptEmails(boolean acceptEmails) {
		this.acceptEmails = acceptEmails;
	}

	@Override
	public String toString() {
		return "CpcUser [key=" + keyId + ", id=" + id + ", guser=" + guser
				+ ", cpcProfileId=" + cpcProfileId + ", cpcNickname="
				+ cpcNickname + "]";
	}

}
