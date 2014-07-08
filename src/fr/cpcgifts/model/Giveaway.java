package fr.cpcgifts.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Unindex;

import fr.cpcgifts.persistance.CpcUserPersistance;
import fr.cpcgifts.utils.TextTools;

@Cache
@Unindex
@Entity
public class Giveaway implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final Logger log = Logger.getLogger(Giveaway.class.getSimpleName());

	@Id Long id;
	
	private Key<CpcUser> author;
	
	private String title = "";
	
	private String longDescription = "";
	
	/** Règles personnalisées */
	private String rules = "";
	
	private String imgUrl = "";
	
	private Set<Key<CpcUser>> entrantsSet;
	
	private Set<Key<Comment>> commentsSet;
	
	/* TimeZone en UTC */
	@Index private Date endDate;
	
	private Set<Key<CpcUser>> winners;
	
	@Index public int nbWinners = 0;
	
	public int nbCopies = 1;
	
	@Index private Boolean open = true;
	
	private Boolean isPrivate = false;
	
	public Giveaway() {
		super();
		
		this.entrantsSet = new HashSet<Key<CpcUser>>();
		this.commentsSet = new HashSet<Key<Comment>>();
		this.winners = new HashSet<Key<CpcUser>>();
	}
	
	public Giveaway(Key<CpcUser> author, String title, String description, String customRules, String imgUrl, Date endDate, int nbCopies) {
		this();
		
		this.author = author;		
		setTitle(title);
		setDescription(description);
		setImgUrl(imgUrl);
		setNbCopies(nbCopies);
		setRules(customRules);
		
		this.endDate = endDate;
		
	}

	public Long getId() {
		return id;
	}
	
	public Key<Giveaway> getKey() {
		return Key.create(Giveaway.class, id);
	}

	public Key<CpcUser> getAuthor() {
		return author;
	}

	public void setAuthor(Key<CpcUser> author) {
		this.author = author;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = TextTools.escapeHtml(title);
	}

	public String getDescription() {
		if(longDescription == null)
			longDescription = "";
		
		return longDescription;
	}

	public void setDescription(String description) {
		this.longDescription = TextTools.escapeHtml(description);
	}
	
	public String getRules() {
		if(rules == null)
			rules = "";
		
		return rules;
	}
	
	public void setRules(String rules) {
		this.rules = TextTools.escapeHtml(rules);
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		if(imgUrl == "")
			imgUrl = "/img/game.png";
		
		this.imgUrl = imgUrl;
	}

	public Set<Key<CpcUser>> getEntrants() {
		if(entrantsSet == null)
			entrantsSet = new HashSet<Key<CpcUser>>();
		
		return entrantsSet;
	}
	
	public boolean addEntrant(Key<CpcUser> cpcuser) {
		getEntrants();
		
		return entrantsSet.add(cpcuser);
	}
	
	public boolean removeEntrant(Key<CpcUser> cpcuser) {
		getEntrants();
		
		return entrantsSet.remove(cpcuser);
	}
	
	public void setEntrants(Set<Key<CpcUser>> entrants) {
		this.entrantsSet = entrants;
	}

	public Set<Key<Comment>> getComments() {
		if(commentsSet == null)
			commentsSet = new HashSet<Key<Comment>>();
		
		return commentsSet;
	}

	public void setComments(Set<Key<Comment>> comments) {
		this.commentsSet = comments;
	}
	
	public boolean addComment(Key<Comment> k) {
		getComments();
		
		return commentsSet.add(k);
		
	}
	
	public boolean removeComment(Key<Comment> k) {
		getComments();
		
		return commentsSet.remove(k);
		
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Set<Key<CpcUser>> getWinners() {
		if(winners == null)
			this.winners = new HashSet<Key<CpcUser>>();
		
		return winners;
	}
	
	public void setWinners(Set<Key<CpcUser>> winners) {
		this.winners = winners;
		
		this.nbWinners = winners.size();
	}

	public boolean addWinner(Key<CpcUser> winner) {
		getWinners();
		
		boolean res =winners.add(winner);
		
		nbWinners = winners.size();
		
		return res;
	}
	
	public boolean removeWinner(Key<CpcUser> winner) {
		getWinners();
		
		boolean res = winners.remove(winner);
		
		nbWinners = winners.size();
		
		return res;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}
	
	public boolean isPrivate() {
		if(this.isPrivate == null)
			this.isPrivate = false;
		
		return this.isPrivate;
	}
	
	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	public int getNbCopies() {
		return nbCopies;
	}

	public void setNbCopies(int nbCopies) {
		this.nbCopies = nbCopies;
	}

	public void drawWinner() {
		setOpen(false);
		setPrivate(false);
		
		log.info("Giveaway " + getKey().getId() + ":" + title + " ended.");
		
		if(entrantsSet.size() == 0)
			return;
		
		Random rand = new Random();
		
		int nbWinners = Math.min(nbCopies, entrantsSet.size());
		
		while(winners.size() < nbWinners) {
			int winnerIndex = rand.nextInt(entrantsSet.size());
			
			@SuppressWarnings("unchecked")
			Key<CpcUser> winner = (Key<CpcUser>) entrantsSet.toArray()[winnerIndex];
			
			boolean newInSet = winners.add(winner);
			
			if(newInSet) {
				CpcUser winnerEntity = CpcUserPersistance.getCpcUser(winner);
				winnerEntity.addWon(this.getKey());
				log.info("Winner is " + winnerEntity.getCpcNickname() + " !");
				CpcUserPersistance.updateOrCreate(winnerEntity);
			}
		}
			
	}
	
	@SuppressWarnings("unchecked")
	public void reroll(Key<CpcUser> winnerToReroll) {
		
		Random rand = new Random();
		
		if(entrantsSet.size() > winners.size()) {
			
			Key<CpcUser> newWinner = winnerToReroll;
			
			do {
				
				int winnerIndex = rand.nextInt(entrantsSet.size());
				
				newWinner = (Key<CpcUser>) entrantsSet.toArray()[winnerIndex];
				
			} while (winners.contains(newWinner));
			
			CpcUser winnerEntity = CpcUserPersistance.getCpcUser(winnerToReroll);
			CpcUser newWinnerEntity = CpcUserPersistance.getCpcUser(newWinner);
			newWinnerEntity.addWon(this.getKey());
			addWinner(newWinner);
			winnerEntity.removeWon(this.getKey());
			removeWinner(winnerToReroll);
			CpcUserPersistance.updateOrCreate(newWinnerEntity);
			CpcUserPersistance.updateOrCreate(winnerEntity);
		}
		
	}
	
	@Override
	public String toString() {
		return "Giveaway [key=" + getId() + ", author=" + getAuthor() + ", title="
				+ getTitle() + ", description=" + getDescription() + ", endDate="
				+ getEndDate() + ", winners=" + Arrays.deepToString(winners.toArray()) + ", open="
				+ isOpen() + ", private=" + isPrivate() + "]";
	}

	
	
}
