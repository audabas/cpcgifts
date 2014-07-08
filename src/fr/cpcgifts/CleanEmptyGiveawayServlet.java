package fr.cpcgifts;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheManager;

import com.googlecode.objectify.Key;

import fr.cpcgifts.model.CpcUser;
import fr.cpcgifts.model.Giveaway;
import fr.cpcgifts.persistance.GiveawayPersistance;

@SuppressWarnings("serial")
public class CleanEmptyGiveawayServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(CleanEmptyGiveawayServlet.class.getName());
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		Cache cache;

        try {
            cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());
            cache.clear();
        } catch (CacheException e) {
            log.warning("Cache error : " + e.getMessage());
        }
		
		
		List<Giveaway> gas = GiveawayPersistance.getClosedEmptyGAs();
		
		for(Giveaway ga : gas) {
			
			if(ga.getWinners().size() == 0) { //on vérifie que le giveaway est bien sans gagnants
				log.info(ga + " has been cleaned up.");
				
				GiveawayPersistance.deleteGa(ga.getKey());
				
			} else { // mise à jour de la variable nbWinners

				Set<Key<CpcUser>> winners = ga.getWinners();
				
				ga.setWinners(winners);
				
				GiveawayPersistance.updateOrCreate(ga);
			}
		}
		
	}
	
	
}
