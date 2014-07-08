package fr.cpcgifts;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;

import fr.cpcgifts.model.AdminRequest;
import fr.cpcgifts.model.Comment;
import fr.cpcgifts.model.CpcUser;
import fr.cpcgifts.model.Giveaway;
import fr.cpcgifts.persistance.AdminRequestPersistance;
import fr.cpcgifts.persistance.CpcUserPersistance;

@SuppressWarnings("serial")
public class AdminRequestServlet extends HttpServlet {

	private static final Logger log = Logger
			.getLogger(AdminRequestServlet.class.getName());

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		CpcUser cpcuser = CpcUserPersistance.getCpcUser(user.getUserId());

		if (user != null && cpcuser != null) {

			@SuppressWarnings("unchecked")
			Map<String, String[]> params = req.getParameterMap();

			String reqType = params.get("req")[0];

			AdminRequest requestToUpdate = null;
			if (!"create".equals(reqType)) {
				String requestId = params.get("reqid")[0];
				requestToUpdate = AdminRequestPersistance.getRequest(Key
						.create(AdminRequest.class, Long.parseLong(requestId)));
			}

			if ("create".equals(reqType)) {
				String typeString = params.get("type")[0];
				Long attachmentId = Long
						.parseLong(params.get("attachmentid")[0]);
				String text = params.get("text")[0];

				AdminRequest.Type type = AdminRequest.Type.valueOf(typeString);

				Key<?> attachment = null;

				StringBuilder textStr = new StringBuilder();

				switch (type) {
				case Reroll:
					String userId = params.get("userid")[0];
					Key<CpcUser> userToRerollKey = Key.create(CpcUser.class,
							Long.parseLong(userId));
					CpcUser userToReroll = CpcUserPersistance
							.getCpcUser(userToRerollKey);
					textStr.append("Utilisateur à reroll : ["
							+ userToReroll.getCpcNickname() + "](/user?userID="
							+ userId + ")  \n");
					textStr.append("Raison du reroll :  \n");
					textStr.append(text);

					attachment = Key.create(Giveaway.class, attachmentId);

					break;
				case ReportPost:
					attachment = Key.create(Comment.class, attachmentId);

					textStr.append("Raison du signalement :  \n");
					textStr.append(text);

					break;
				case ReportUser:
					attachment = Key.create(CpcUser.class, attachmentId);

					textStr.append("Raison du signalement :  \n");
					textStr.append(text);

					break;
				case RulesModification:
					attachment = Key.create(Giveaway.class, attachmentId);

					textStr.append("Modifications à effectuer :  \n");
					textStr.append(text);

					break;
				case TitleModification:
					attachment = Key.create(Giveaway.class, attachmentId);

					textStr.append("Modifications à effectuer :  \n");
					textStr.append(text);

					break;
				default:
					break;
				}

				AdminRequest adminRequest = new AdminRequest(cpcuser.getKey(),
						type, attachment, textStr.toString());

				AdminRequestPersistance.updateOrCreate(adminRequest);

				log.info("User " + cpcuser + " submited new admin request "
						+ adminRequest);
			} else if ("process".equals(reqType)) {
				requestToUpdate.setState(AdminRequest.State.Processed);
				requestToUpdate.setConsideredBy(cpcuser.getKey());
			} else if ("deny".equals(reqType)) {
				requestToUpdate.setState(AdminRequest.State.Denied);
				requestToUpdate.setConsideredBy(cpcuser.getKey());
			}

			if (requestToUpdate != null) {
				AdminRequestPersistance.updateOrCreate(requestToUpdate);
			}
		}

	}
}
