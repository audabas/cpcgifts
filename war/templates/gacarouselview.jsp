<%@page import="fr.cpcgifts.persistance.GAPersistance"%>
<%@page import="fr.cpcgifts.model.Giveaway"%>
<%@page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@page import="com.google.appengine.api.datastore.Key"%>
<%@page import="fr.cpcgifts.persistance.CpcUserPersistance"%>
<% 

String sgaid = request.getParameter("gaID");

String active = request.getParameter("isActive");

Long gaid = Long.parseLong(sgaid);

Key k = KeyFactory.createKey(Giveaway.class.getSimpleName(), gaid);

Giveaway ga = GAPersistance.getGAFromCache(k);

%>

<div class="item
	<%
		if ("true".equals(active)) {
	%>
			active
	<%
		}
	%>
">
	<a href="/giveaway?gaID=<%= ga.getKey().getId() %>" >
		<img alt="Steam game image" class="img-steam-game" src="<%= ga.getImgUrl()  %>" alt="" >
	</a>
	<div class="carousel-caption">
		<h4><%= ga.getTitle() %></h4>
		<p>
			<% if(ga.getDescription().length() < 150) { %>
				<%= ga.getDescription() %>
			<% } else { %>
				<%= ga.getDescription().substring(0, 150) + " ..."  %>
			<% } %>
		</p>
	</div>
</div>