<%@page import="fr.cpcgifts.persistance.AdminRequestPersistance"%>
<%@page import="fr.cpcgifts.model.AdminRequest"%>
<%@page import="fr.cpcgifts.utils.DateTools"%>
<%@page import="javax.jdo.JDOObjectNotFoundException"%>
<%@page import="fr.cpcgifts.persistance.GAPersistance"%>
<%@page import="fr.cpcgifts.model.Giveaway"%>
<%@page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@page import="com.google.appengine.api.datastore.Key"%>
<%@page import="fr.cpcgifts.persistance.CpcUserPersistance"%>
<%@page import="fr.cpcgifts.model.CpcUser"%>
<% 

String sarid = request.getParameter("arID");

Long arid = Long.parseLong(sarid);

Key k = KeyFactory.createKey(AdminRequest.class.getSimpleName(), arid);

AdminRequest ar = AdminRequestPersistance.getRequestFromCache(k);

CpcUser auth = CpcUserPersistance.getUserFromCache(ar.getAuthor());

%>

<div class='row offset1' data-type='admin-request' data-arstate='<%= ar.getState().name() %>'
  data-artype='<%= ar.getType() %>' data-arid='<%= arid %>'>
	<div class='row'>
		<h4 class='span3'>Demande #<%= arid %></h4>
		<div class='span9 btn-toolbar'>
			<% if(ar.getState() == AdminRequest.State.Open) { %>
				<button class='btn btn-success btn-small' id='proceed-" + arId + "'>Traitée</button>
				<button class='btn btn-danger btn-small' id='deny-" + arId + "'>Refusée</button>
			<% } %>
		</div>
	</div>
	
	<h5>Statut : <span class='label' id='statedisplay-<%= arid %>'></span></h5>
	
	<h5>Type de demande : <span class='label label-info' id='typedisplay-" + arId + "'></span></h5>
	
	<h6>Date : <%= ar.getRequestDate() %></h6>
	
	<h5>Envoyée par :</h5>
	<jsp:include page="/templates/userview.jsp">
		<jsp:param value="<%= auth.getKey().getId() %>" name="userID"/>
	</jsp:include>
	
	<% if(ar.getState() != AdminRequest.State.Open && ar.getConsideredBy() != null ) {	%>
		<h5>Traitée par :</h5>
		<jsp:include page="/templates/userview.jsp">
			<jsp:param value="<%= ar.getConsideredBy().getId() %>" name="userID"/>
		</jsp:include>
	<% } %>
	
	<br />
	
	<h5>Message : </h5>
	<textarea id='reqtext-<%= arid %>' class='hidden'><%= ar.getText() %></textarea>
	<p id='reqtextdisplay-<%= arid %>'></p>
	
	<% try { %>
		<h5>Pièce jointe :</h5>
		<% switch (ar.getType()) {
		case ReportUser:
			// pour obtenir une exception si l'objet n'existe pas
			CpcUserPersistance.getUserFromCache(ar.getAttachment()); %>
			<jsp:include page="/templates/userview.jsp">
				<jsp:param value="<%= ar.getAttachment().getId() %>" name="userID"/>
			</jsp:include>
			<% break;
		default:
			GAPersistance.getGAFromCache(ar.getAttachment()); %>
			<jsp:include page="/templates/gaview.jsp">
				<jsp:param value="<%= ar.getAttachment().getId() %>" name="gaID"/>
			</jsp:include>
			<%break;
		}
	} catch(JDOObjectNotFoundException e) { %> 
		<h5>Aucune pièce jointe.</h5>
	<% } %>
</div>