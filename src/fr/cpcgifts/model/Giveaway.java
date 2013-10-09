package fr.cpcgifts.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

import fr.cpcgifts.persistance.CpcUserPersistance;
import fr.cpcgifts.utils.TextTools;

@PersistenceCapable(detachable="true")
public class Giveaway implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final Logger log = Logger.getLogger(Giveaway.class.getSimpleName());

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	@Persistent
	private Key author;
	
	@Persistent
	private String title = "";
	
	@Persistent
	private Text longDescription = new Text("");
	
	@Persistent
	private String imgUrl = "";
	
	@Persistent
	private Set<Key> entrantsSet;
	
	private Set<Key> commentsSet;
	
	/* TimeZone en UTC */
	@Persistent
	private Date endDate;
	
	@Persistent
	private Set<Key> winners;
	
	@Persistent
	public int nbWinners = 0;
	
	@Persistent
	public int nbCopies = 1;
	
	@Persistent
	private boolean open = true;
	
	@Persistent
	private boolean rerolled = false;
	
	
	public Giveaway(Key author, String title, String description, String imgUrl, Date endDate, int nbCopies) {
		this.author = author;		
		setTitle(title);
		setDescription(description);
		setImgUrl(imgUrl);
		setNbCopies(nbCopies);
		
		this.entrantsSet = new HashSet<Key>();
		this.commentsSet = new HashSet<Key>();
		this.winners = new HashSet<Key>();
		
		this.endDate = endDate;
		
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = TextTools.escapeHtml(title);
	}

	public String getDescription() {
		if(longDescription == null)
			longDescription = new Text("");
		
		return longDescription.getValue();
	}

	public void setDescription(String description) {
		this.longDescription = new Text(TextTools.escapeHtml(description));
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		if(imgUrl == "")
			imgUrl = "img/game.png";
		
		this.imgUrl = imgUrl;
	}

	public Set<Key> getEntrants() {
		if(entrantsSet == null)
			entrantsSet = new HashSet<Key>();
		
		return entrantsSet;
	}
	
	public boolean addEntrant(Key cpcuser) {
		getEntrants();
		
		return entrantsSet.add(cpcuser);
	}
	
	public boolean removeEntrant(Key cpcuser) {
		getEntrants();
		
		return entrantsSet.remove(cpcuser);
	}
	
	public void setEntrants(Set<Key> entrants) {
		this.entrantsSet = entrants;
	}

	public Set<Key> getComments() {
		if(commentsSet == null)
			commentsSet = new HashSet<Key>();
		
		return commentsSet;
	}

	public void setComments(Set<Key> comments) {
		this.commentsSet = comments;
	}
	
	public boolean addComment(Key k) {
		getComments();
		
		return commentsSet.add(k);
		
	}
	
	public boolean removeComment(Key k) {
		getComments();
		
		return commentsSet.remove(k);
		
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Set<Key> getWinners() {
		if(winners == null)
			this.winners = new HashSet<Key>();
		
		return winners;
	}
	
	public void setWinners(Set<Key> winners) {
		this.winners = winners;
		
		this.nbWinners = winners.size();
	}

	public boolean addWinner(Key winner) {
		getWinners();
		
		boolean res =winners.add(winner);
		
		nbWinners = winners.size();
		
		return res;
	}
	
	public boolean removeWinner(Key winner) {
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

	public int getNbCopies() {
		return nbCopies;
	}

	public void setNbCopies(int nbCopies) {
		this.nbCopies = nbCopies;
	}

	public void drawWinner() {
		open = false;
		
		log.info("Giveaway " + getKey().getId() + ":" + title + " ended.");
		
		if(entrantsSet.size() == 0)
			return;
		
		Random rand = new Random();
		
		int nbWinners = Math.min(nbCopies, entrantsSet.size());
		
		while(winners.size() < nbWinners) {
			int winnerIndex = rand.nextInt(entrantsSet.size());
			
			Key winner = (Key) entrantsSet.toArray()[winnerIndex];
			
			boolean newInSet = winners.add(winner);
			
			if(newInSet) {
				CpcUser winnerEntity = CpcUserPersistance.getCpcUserUndetached(winner);
				winnerEntity.addWon(this.key);
				log.info("Winner is " + winnerEntity.getCpcNickname() + " !");
			}
		}
			
		CpcUserPersistance.closePm();
	}
	
	public void reroll(Key winnerToReroll) {
		rerolled = true;
		
		Random rand = new Random();
		
		if(entrantsSet.size() > winners.size()) {
			
			Key newWinner = winnerToReroll;
			
			do {
				
				int winnerIndex = rand.nextInt(entrantsSet.size());
				
				newWinner = (Key) entrantsSet.toArray()[winnerIndex];
				
			} while (winners.contains(newWinner));
			
			CpcUser winnerEntity = CpcUserPersistance.getCpcUserUndetached(winnerToReroll);
			CpcUser newWinnerEntity = CpcUserPersistance.getCpcUserUndetached(newWinner);
			newWinnerEntity.addWon(this.key);
			addWinner(newWinner);
			winnerEntity.removeWon(this.key);
			removeWinner(winnerToReroll);
			CpcUserPersistance.closePm();
		}
		
	}
	
	public boolean isRerolled() {
		return rerolled;
	}

	@Override
	public String toString() {
		return "Giveaway [key=" + getKey() + ", author=" + getAuthor() + ", title="
				+ getTitle() + ", description=" + getDescription() + ", endDate="
				+ getEndDate() + ", winners=" + Arrays.deepToString(winners.toArray()) + ", open=" + isOpen() + "]";
	}

	
	
}
