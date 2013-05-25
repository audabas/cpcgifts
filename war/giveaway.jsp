<%@page import="fr.cpcgifts.model.Comment"%>
<%@page import="fr.cpcgifts.persistance.CommentParsistance"%>
<%@page import="fr.cpcgifts.utils.ViewTools"%>
<%@page import="fr.cpcgifts.utils.DateTools"%>
<%@page import="fr.cpcgifts.persistance.GAPersistance"%>
<%@page import="fr.cpcgifts.model.Giveaway"%>
<%@page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@page import="com.google.appengine.api.datastore.Key"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>

<%
	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();
%>

<%@ include file="forcelogin.jspf"%>

<%
	Giveaway currentGA = null;

	String gaid = request.getParameter("gaID");

	if (gaid != null) {
		Long gid = Long.parseLong(gaid);

		if (gid != null) {
			Key k = KeyFactory.createKey("Giveaway", gid);
			currentGA = GAPersistance.getGA(k);
		}
	}

	if (currentGA == null) {
		response.sendRedirect("/404.html");
		return;
	}

	CpcUser gaAuthor = CpcUserPersistance.getCpcUserByKey(currentGA
			.getAuthor());
%>

<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->
<html class="no-js">
<!--<![endif]-->
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<title><%=currentGA.getTitle()%> - CPCGifts</title>
<meta name="description" content="">
<meta name="viewport" content="width=device-width">

<link rel="stylesheet" href="css/bootstrap.min.css">
<style>
body {
	padding-top: 60px;
	padding-bottom: 40px;
}
</style>
<link rel="stylesheet" href="css/bootstrap-responsive.min.css">
<link rel="stylesheet" href="css/main.css">

<script src="js/vendor/modernizr-2.6.2.min.js"></script>
</head>
<body>
	<!--[if lt IE 7]>
            <p class="chromeframe">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade your browser</a> or <a href="http://www.google.com/chromeframe/?redirect=true">activate Google Chrome Frame</a> to improve your experience.</p>
        <![endif]-->

	<!-- This code is taken from http://twitter.github.com/bootstrap/examples/hero.html -->

	<%@ include file="menubar.jspf"%>
	<%@ include file="getuser.jspf"%>

	<div class="container">


		<div class="row">
			<div class="span5">
				<img class="img-steam-game" src="<%=currentGA.getImgUrl()%>" />
			</div>
			<div class="span7">
				<h1><%=currentGA.getTitle()%></h1>
				<hr>
				<div class="span2">
					<h4>Créé par :</h4>
				</div>
				<div class="media">
					<a class="pull-left"
						href="/user?userID=<%=gaAuthor.getKey().getId()%>"> <img
						class="media-object img-small-avatar"
						src="<%=gaAuthor.getAvatarUrl()%>">
					</a>
					<div class="media-body">
						<h3 class="media-heading">
							<a href="/user?userID=<%=gaAuthor.getKey().getId()%>"><%=gaAuthor.getCpcNickname()%></a>
						</h3>
					</div>
				</div>
				<hr>

				<img class="img-small-icon" src="img/clock.png" />
				<%
					if (currentGA.isOpen()) {
				%>

				Ouvert encore

				<%
					} else {
				%>

				Fermé depuis

				<%
					}
				%>
				<%=DateTools.dateDifference(currentGA.getEndDate())%>
				
				<%
				if(currentGA.isOpen() && currentGA.getAuthor().getId() != cpcuser.getKey().getId()) {
					if(currentGA.getEntrants().contains(cpcuser.getKey())) {
						
						%>
						
						<a
						href="/enterga?reqtype=exit&gaid=<%= currentGA.getKey().getId() %>"
						 style="margin-left: 250px" class="btn btn-danger">Ne plus participer</a>
						
						<%
						
					} else {
						
						%>
				<a
					href="/enterga?reqtype=enter&gaid=<%= currentGA.getKey().getId() %>"
					style="margin-left: 250px" class="btn btn-success">Participer</a>
				<%
						
					}
				}
				%>

			</div>
		</div>

		<hr>

		<div class="row">
			<div class="span10 offset1">
				<%=currentGA.getDescription()%>
			</div>
		</div>

		<hr>

		<div class="tabbable">
			<!-- Only required for left/right tabs -->
			<ul class="nav nav-tabs">
				<!-- <li class="active"><a href="#commentaires" data-toggle="tab">Commentaires</a></li> -->
				<li class="active"><a href="#entrants" data-toggle="tab">Participants</a></li>
				<% if(currentGA.getAuthor().getId() == cpcuser.getKey().getId()) { %>
				<li><a href="#signature" data-toggle="tab">Signature</a></li>
				<% } %>
				<% if(!currentGA.isOpen() && currentGA.getWinner() != null) {%>
				<li><a href="#winner" data-toggle="tab">Gagnant</a></li>
				<% } %>
			</ul>
			<div class="tab-content">
				<div class="tab-pane" id="commentaires">
					<%
					
					for(Comment c : CommentParsistance.getComments(currentGA.getComments())) {
					
					%>
					
					<%= ViewTools.commentView(c) %>
					<hr>
					
					<%
					}
					%>
					
				</div>
				<div class="tab-pane active" id="entrants">
					
					<%
					
					for(CpcUser entrant : CpcUserPersistance.getCpcUsers(currentGA.getEntrants())) {
						
						%>
						
						<%= ViewTools.userView(entrant) %>
						<hr>
						<%
						
					}
					
					%>
					
					
				</div>
				<div class="tab-pane" id="signature">
				<h4>Signature texte :</h4>
				<p class="well">[url=<%= request.getRequestURL().toString() + '?' + request.getQueryString() %>]<%= currentGA.getTitle() %>[/url]</p>
				<% if(!currentGA.getImgUrl().equals("img/game.png")) { %>
				<h4>Signature image :</h4>
				<p class="well">[url=<%= request.getRequestURL().toString() + '?' + request.getQueryString() %>][IMG]<%= currentGA.getImgUrl() %>[/IMG][/url]</p>
				<% } %>
				</div>
				<div class="tab-pane" id="winner">
				
				<% if(!currentGA.isOpen() && currentGA.getWinner() != null) { %>
				<%= ViewTools.userView(CpcUserPersistance.getCpcUserByKey(currentGA.getWinner())) %>
					<% if(userService.isUserAdmin()) { %>
						<hr>
						<% if(currentGA.isRerolled()) { %>
							<a	href="/admin/reroll?reqtype=reroll&gaid=<%= currentGA.getKey().getId() %>"
								class="btn btn-danger"><i class="icon-warning-sign icon-white"></i> <i class="icon-repeat icon-white"></i> Re-Relancer le tirage</a>
						<% } else { %>
							<a	href="/admin/reroll?reqtype=reroll&gaid=<%= currentGA.getKey().getId() %>"
								class="btn btn-warning"><i class="icon-repeat icon-white"></i> Relancer le tirage</a>
						<% } %>
					<%  } %>
				<%	} %>
				</div>
			</div>
		</div>


		<hr>

		<footer>
			<p><%@ include file="footer.jspf" %></p>
		</footer>

	</div>
	<!-- /container -->

	<script
		src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
	<script>
		window.jQuery
				|| document
						.write('<script src="js/vendor/jquery-1.9.1.min.js"><\/script>')
	</script>

	<script src="js/vendor/bootstrap.min.js"></script>

	<script src="js/main.js"></script>
</body>
</html>
