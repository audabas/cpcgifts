package fr.cpcgifts;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Key;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheManager;
import fr.cpcgifts.model.Giveaway;
import fr.cpcgifts.persistance.GAPersistance;
import fr.cpcgifts.utils.SendEmailUtils;

@SuppressWarnings("serial")
public class CloseGAServlet extends HttpServlet {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(CloseGAServlet.class.getName());
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		List<Giveaway> gas = GAPersistance.getOpenGAsToClose(false);
		
		for(Giveaway ga : gas) {
			ga.drawWinner();
			
			SendEmailUtils.sendGiveawayFinishedEmail(ga);
			
			try {
	            Cache cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());
	            
	            for(Key k : ga.getWinners()) {
	            	cache.remove(k);
	            	cache.remove(k.getId() + "-won");
	            }
	            cache.remove(ga.getKey());
	            
	            cache.remove("contribution-" + ga.getAuthor().getId());
				
	        } catch (CacheException e) {
	        	//rien
	        }
			
		}
		
		GAPersistance.closePm();
		
	}
	
	
}
