package fr.cpcgifts;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.cpcgifts.model.Giveaway;
import fr.cpcgifts.persistance.GiveawayPersistance;
import fr.cpcgifts.utils.SendEmailUtils;

@SuppressWarnings("serial")
public class CloseGiveawayServlet extends HttpServlet {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(CloseGiveawayServlet.class.getName());
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		List<Giveaway> gas = GiveawayPersistance.getOpenGAsToClose();
		
		for(Giveaway ga : gas) {
			ga.drawWinner();
			
			SendEmailUtils.sendGiveawayFinishedEmail(ga);
			
			GiveawayPersistance.updateOrCreate(ga);
			
		}
		
	}
	
	
}
