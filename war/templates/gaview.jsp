<%@page import="fr.cpcgifts.utils.DateTools"%>
<%@page import="javax.jdo.JDOObjectNotFoundException"%>
<%@page import="fr.cpcgifts.persistance.GAPersistance"%>
<%@page import="fr.cpcgifts.model.Giveaway"%>
<%@page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@page import="com.google.appengine.api.datastore.Key"%>
<%@page import="fr.cpcgifts.persistance.CpcUserPersistance"%>
<%@page import="fr.cpcgifts.model.CpcUser"%>
<% 

String sgaid = request.getParameter("gaID");

Long gaid = Long.parseLong(sgaid);

Key k = KeyFactory.createKey(Giveaway.class.getSimpleName(), gaid);

Giveaway ga = GAPersistance.getGAFromCache(k);

CpcUser auth = CpcUserPersistance.getUserFromCache(ga.getAuthor());

%>

<div class="row-fluid" data-type='giveaway' data-id='<%= ga.getKey().getId() %>'
				data-title='<%= ga.getTitle() %>' data-author='<%=auth.getCpcNickname() %>'>
	<div class="span2">
		<a href="/giveaway?gaID=<%= ga.getKey().getId() %>" >
			<img alt="Game Image" class="img-rounded img-small-ga lazy" src="/img/game.png"
				data-original="<%= ga.getImgUrl() %>">
		</a>
	</div>
	<div class="span7">
		<h3 class="no-margin">
			<a href="/giveaway?gaID=<%= ga.getKey().getId() %>" >
				<%= ga.getTitle() %>
					<%
						if(ga.getNbCopies() > 1) {
					%>
							<span class="gray ">(<%= ga.getNbCopies() %> copies)</span>
					<%
						}
					%>
			</a>
		</h3>
		<div>
			<img alt="Clock" class="img-smallest-icon" src="img/clock.png" />
			<%
				if(ga.isOpen()) {
			%>
			Ouvert encore
			<% 
				} else {
			 %>
			 Fermé depuis 
			 <%
				}
			 %>
			 <%= DateTools.dateDifference(ga.getEndDate()) %>

		</div>
		<div class='top-margin10'>
			<p>
				<%
					if(ga.isOpen()) {
			 	%>	
					<% out.write(ga.getEntrants().size() + " participant");
					if(ga.getEntrants().size() > 1)
							out.write("s"); %>
				<%	
					} else if(ga.getWinners().size() == 1) {
						CpcUser winner = CpcUserPersistance.getUserFromCache(ga.getWinners().iterator().next());
				%>
					<%=	"Gagné par : " + winner.getCpcNickname() %>
				<%
					} else if(ga.getWinners().size() > 1) {
			 	%>
					<%= ga.getWinners().size() + " gagnants" %>
				<%
					}
				%>
			</p>
		</div>
	</div>
	<div class="span3">
		<div class="media">
			<a class="pull-left" href="/user?userID=<%= auth.getKey().getId() %>">
				<img alt="Avatar Image" class="media-object img-small-avatar lazy" src="img/avatar.jpg"
					data-original="<%= auth.getAvatarUrl() %>" />
			</a>
			<div class="media-body">
				<h4 class="media-heading">
					<a href="/user?userID=<%= auth.getKey().getId() %>">
						<%= auth.getCpcNickname() %>
					</a>
				</h4>
			</div>
		</div>
	</div>
</div>