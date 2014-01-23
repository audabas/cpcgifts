<%@page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@page import="com.google.appengine.api.datastore.Key"%>
<%@page import="fr.cpcgifts.persistance.CpcUserPersistance"%>
<%@page import="fr.cpcgifts.model.CpcUser"%>
<% 

String suid = request.getParameter("userID");

Long uid = Long.parseLong(suid);

Key k = KeyFactory.createKey(CpcUser.class.getSimpleName(), uid);

CpcUser u = CpcUserPersistance.getUserFromCache(k);

%>

<div class="row-fluid" data-type='user' data-id='<%= suid %>' data-nickname='<%= u.getCpcNickname() %>' >
	<div class="span1">
		<a href="/user?userID=<%= u.getKey().getId() %>">
			<img alt="Avatar" class="img-rounded img-small-avatar lazy" src="img/avatar.jpg"
			data-original="<%= u.getAvatarUrl()  %>" />
		</a>
	</div>
	<div class="span10">
		<h4><a  href="/user?userID=<%=  u.getKey().getId() %>"> <%=  u.getCpcNickname()%></a></h4>
	</div>
</div>