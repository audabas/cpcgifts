package fr.cpcgifts;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.cpcgifts.model.AdminRequest;
import fr.cpcgifts.persistance.AdminRequestPersistance;

@SuppressWarnings("serial")
public class CleanOldRequestsServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(CleanOldRequestsServlet.class.getName());
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		List<AdminRequest> ars = AdminRequestPersistance.getRequestsToDelete();
		
		for(AdminRequest ar : ars) {
			log.info("AdminRequest deleted : " + ar);
			
			AdminRequestPersistance.delete(ar.getKey());
		}
		
	}
	
	
}
