<%@page import="java.util.Comparator"%>
<%@page import="java.util.Arrays"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@page import="java.util.Collection"%>
<%@page import="org.apache.commons.collections.CollectionUtils"%>
<%@page import="java.util.Map"%>
<%@page import="fr.cpcgifts.model.Comment"%>
<%@page import="fr.cpcgifts.persistance.CommentPersistance"%>
<%@page import="fr.cpcgifts.utils.ViewTools"%>
<%@page import="fr.cpcgifts.utils.DateTools"%>
<%@page import="fr.cpcgifts.persistance.GAPersistance"%>
<%@page import="fr.cpcgifts.model.Giveaway"%>
<%@page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@page import="com.google.appengine.api.datastore.Key"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>

<%
	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();
%>


<%
	Cache cache;
	Giveaway currentGA = null;

	String gaid = request.getParameter("gaID");

	if (gaid != null) {
		Long gid = Long.parseLong(gaid);

		if (gid != null) {
			Key k = KeyFactory.createKey("Giveaway", gid);
			
			try {
			            cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());
			            
			            currentGA = (Giveaway) cache.get(k);
			            
			            if(currentGA == null) {
					currentGA = GAPersistance.getGA(k);
			            	cache.put(k, currentGA);
			            }
			} catch (CacheException e) {
				currentGA = GAPersistance.getGA(k);
			}
	
		}
	}

	if (currentGA == null) {
		response.sendRedirect("/404.html");
		return;
	}

	CpcUser gaAuthor = null;
	
	try {
        cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());

		gaAuthor = (CpcUser) cache.get(currentGA.getAuthor());
		
		if(gaAuthor == null) {
	gaAuthor = CpcUserPersistance.getCpcUserByKey(currentGA.getAuthor());
	cache.put(currentGA.getAuthor(), gaAuthor);
		}
		
	} catch (CacheException e) {
		gaAuthor = CpcUserPersistance.getCpcUserByKey(currentGA.getAuthor());
	}
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

<%@ include file="css.jspf" %>

<script src="js/vendor/modernizr-2.6.2.min.js"></script>
</head>
<body>
	<!--[if lt IE 7]>
            <p class="chromeframe">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade your browser</a> or <a href="http://www.google.com/chromeframe/?redirect=true">activate Google Chrome Frame</a> to improve your experience.</p>
        <![endif]-->

	<!-- This code is taken from http://twitter.github.com/bootstrap/examples/hero.html -->

	<%@ include file="menubar.jspf"%>
	<%@ include file="getuser.jspf"%>
	
	<%
			boolean isAuthor = cpcuser != null && cpcuser.getKey().equals(gaAuthor.getKey());
		%>

	<div class="container">


		<div class="row">
			<div class="span5">
				<img class="img-steam-game" src="<%=currentGA.getImgUrl()%>" />
				<%
					if(isAuthor) {
				%>
					<div class="row" style="margin-top: 10px;">
					<a class="btn btn-success span2" href="#modif-image"
						data-toggle="modal"> <i class="icon-pencil icon-white"></i>
						Modifier l'image
					</a>
					</div>
				<%
					}
				%>
			</div>
			<div class="span7">
				<h1><%=currentGA.getTitle()%>
				<% if(currentGA.getNbCopies() > 1) { %>
				<span class="gray">(<%= currentGA.getNbCopies() %> copies)</span>
				<% } %>
				</h1>
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
									if(currentGA.isOpen() && !isAuthor && userService.isUserLoggedIn() && !cpcuser.isBanned()) {
											if(currentGA.getEntrants().contains(cpcuser.getKey())) {
								%>
						
						<a
						href="/enterga?reqtype=exit&gaid=<%=currentGA.getKey().getId()%>"
						 style="margin-left: 250px" class="btn btn-danger">Ne plus participer</a>
						
						<%
											} else {
												%>
				<a
					href="/enterga?reqtype=enter&gaid=<%=currentGA.getKey().getId()%>"
					style="margin-left: 250px" class="btn btn-success">Participer</a>
				<%
											}
									}
				%>

			</div>
		</div>

		<hr>

		<div class="row">
			<div class="span10 offset1" id="description">
			</div>
		</div>

		<hr>
		
		
		<%
			if(isAuthor) {
						%>
				<div class="row" style="margin-top: 10px;">
				<a class="btn btn-success span2" href="#modif-desc"
					data-toggle="modal"> <i class="icon-pencil icon-white"></i>
					Modifier la description
				</a>
				</div>
				<hr />
		<%
			}
		%>

		<div class="tabbable">
			<!-- Only required for left/right tabs -->
			<ul class="nav nav-tabs">
				<li class="active"><a href="#commentaires" data-toggle="tab">Commentaires <span class="gray">(<%= currentGA.getComments().size() %>)</span></a></li>
				<li><a href="#entrants" data-toggle="tab">Participants <span class="gray">(<%= currentGA.getEntrants().size() %>)</span></a></li>
				<%
					if(isAuthor || (userService.isUserLoggedIn() && userService.isUserAdmin())) {
				%>
				<li><a href="#signature" data-toggle="tab">Signature</a></li>
				<%
					}
				%>
				<%
					if(!currentGA.isOpen() && currentGA.getWinners().size() == 1) {
				%>
				<li><a href="#winner" data-toggle="tab">Gagnant</a></li>
				<%
					} else if(!currentGA.isOpen() && currentGA.getWinners().size() > 1) {
				%>
				<li><a href="#winner" data-toggle="tab">Gagnants <span class="gray">(<%= currentGA.getWinners().size() %>)</span></a></li>
				<% 
					}
				%>
				<% 
					if(userService.isUserLoggedIn() && userService.isUserAdmin()) {
				%>
				<li><a href="#admin" data-toggle="tab">Admin</a></li>
				<%
					}
				%>
			</ul>
			<div class="tab-content">
				<div class="tab-pane active" id="commentaires">
					<%
					
					Map<Key,Comment> comments = null;
					
					try {
						cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());
						
						comments = cache.getAll(currentGA.getComments());
						
						Collection<Key> notCachedKeys = CollectionUtils.subtract(currentGA.getComments(),comments.keySet());
						
						for(Key k : notCachedKeys) {
							Comment c = CommentPersistance.getComment(k);
							comments.put(k, c);
							cache.put(k, c);
						}
						
					} catch (CacheException e) {
						
					}
					
					Comment[] commentsArray = comments.values().toArray(new Comment[0]);
					
					Arrays.sort(commentsArray, new Comparator<Comment>() {
						public int compare(Comment c1, Comment c2) {
							return c1.getCommentDate().compareTo(c2.getCommentDate());
						}
					});
					
						for(Comment c : commentsArray) {
					%>
					
					<%= ViewTools.commentView(c) %>
					
					<% if(userService.isUserLoggedIn() && (c.getAuthor().equals(cpcuser.getKey()) ||  userService.isUserAdmin())) { %>
					
						<a href="javascript:deleteComment(<%= c.getKey().getId() %>)" class="btn btn-mini"><i class="icon-trash"></i> Supprimer ce commentaire</a>
					
					<% } %>
					<hr>
					
					<%
					}
					%>

					<% if(userService.isUserLoggedIn() && cpcuser != null && !cpcuser.isBanned()) { %>

					<form action="/editga" method="post">
						<fieldset>
							<label>Laisser un commentaire :</label>
							<textarea class="span12" rows="5" id="comment" name="comment" required="required" data-provide="markdown"></textarea>
							<input type="hidden" name="req" value="comment" />
							<input type="hidden" name="gaid" value="<%= currentGA.getKey().getId() %>">
							
							<button type="submit" class="btn">Commenter</button>
						</fieldset>
					</form>
					<% } %>

				</div>
				<div class="tab-pane" id="entrants">
					
					<%
					
					Map<Key,CpcUser> entrants = null;
					
					try {
						cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());
						
						entrants = cache.getAll(currentGA.getEntrants());
						
						Collection<Key> notCachedKeys = CollectionUtils.subtract(currentGA.getEntrants(),entrants.keySet());
						
						for(Key k : notCachedKeys) {
							CpcUser u = CpcUserPersistance.getCpcUserByKey(k);
							entrants.put(k, u);
							cache.put(k, u);
						}
						
					} catch (CacheException e) {
						
					}
					
					
					for(CpcUser entrant : entrants.values()) {
						
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
				
				<% 
				if(!currentGA.isOpen() && currentGA.getWinners().size() > 0) {
						for(Key k : currentGA.getWinners()) {
				%>
						<%= ViewTools.userView(CpcUserPersistance.getCpcUserByKey(k)) %>
						<% if(userService.isUserLoggedIn() && userService.isUserAdmin() && !currentGA.isOpen() && currentGA.getEntrants().size() > currentGA.getWinners().size()) { %>
							<a	href="/admin/reroll?reqtype=reroll&gaid=<%= currentGA.getKey().getId() %>&winnerToReroll=<%= k.getId() %>"
								class="btn btn-warning"><i class="icon-repeat icon-white"></i> Relancer le tirage</a>
						<% } %>	
						<hr />
										
				<%
						}
					} 
				%>
				</div>
				<% if(userService.isUserLoggedIn() && userService.isUserAdmin()) { %>
				<div class="tab-pane" id="admin">
					<div class="row offset1">
						<a class="btn btn-success" href="#modif-title"
						data-toggle="modal"> <i class="icon-pencil icon-white"></i>
						Modifier le titre
						</a>
					</div>
					<hr />
					<div class="row offset1">
						<a	href="/admin/closega?reqtype=closeGa&gaid=<%= currentGA.getKey().getId() %>"
									class="btn btn-warning"><i class="icon-trash icon-white"></i> Fermer le concours</a>
					</div>
					<hr />
					<div class="row offset1">
						<a	href="/admin/openga?reqtype=openGa&gaid=<%= currentGA.getKey().getId() %>"
									class="btn btn-warning"><i class="icon-repeat icon-white"></i> Rouvrir le concours</a> (ne modifie pas la date de fin du concours)
					</div>
				</div>
				<% } %>
			</div>			
		</div>


		<hr>

		<%@ include file="footer.jspf" %>

	</div>
	
	<!-- Modal modification image -->
	<div id="modif-image" class="modal hide fade">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal"
				aria-hidden="true">&times;</button>
			<h3>Modifier l'image</h3>
		</div>
		<div class="modal-body">
			<form id="imgform" name="imgform" action="/editga" method="post">
				<fieldset>
					<label>Url de l'image du jeu (460x215 pixels)</label>
					<input id="imgurl" class="span4"
						name="imgurl" type="text" placeholder="url" required="required">
					<span class="help-block">Si le jeu est disponible sur steam, le plus simple est de récupérer le lien vers l'image depuis le hub de la communauté.</span>
				</fieldset>
				<input type="hidden" name="req" value="changeimg" />
				<input type="hidden" name="gaid" value="<%= currentGA.getKey().getId() %>">
			</form>
		</div>
		<div class="modal-footer">
			<a href="#" class="btn" data-dismiss="modal">Annuler</a> <a
				href="javascript:submitImgForm()" class="btn btn-primary">Modifier</a>
		</div>
	</div>

	<script type="text/javascript">
		function submitImgForm() {
			if ($("#imgurl").val() != "") {
				$("#imgform").submit();
			}
		}
	</script>
	
	<!-- Modal modification de la description -->
	<div id="modif-desc" class="modal big-modal hide fade">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal"
				aria-hidden="true">&times;</button>
			<h3>Modifier la description</h3>
		</div>
		<div class="modal-body">
			<form id="descform" name="descform" action="/editga" method="post">
				<fieldset>
					<label>Description du jeu :</label><br />
						<textarea class="span9 wmd-input" name="desc" id="desc" rows="15" data-provide="markdown" ><%= currentGA.getDescription() %></textarea>
				</fieldset>
				<input type="hidden" name="req" value="changedescription" />
				<input type="hidden" name="gaid" value="<%= currentGA.getKey().getId() %>">
			</form>
		</div>
		<div class="modal-footer">
			<a href="#" class="btn" data-dismiss="modal">Annuler</a> <a
				href="javascript:submitDescForm()" class="btn btn-primary">Modifier</a>
		</div>
	</div>
	
	<script type="text/javascript">
		function submitDescForm() {
			$("#descform").submit();
		}
	</script>
	
	<!-- Modal modification du titre -->
	<div id="modif-title" class="modal hide fade">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal"
				aria-hidden="true">&times;</button>
			<h3>Modifier le titre</h3>
		</div>
		<div class="modal-body">
			<form id="titleform" name="titleform" action="/editga" method="post">
				<fieldset>
					<label>Titre du concours</label>
					<input id="title" class="span4"
						name="title" type="text" required="required" value="<%= currentGA.getTitle() %>">
					<span class="help-block">Admin uniquement.</span>
				</fieldset>
				<input type="hidden" name="req" value="changetitle" />
				<input type="hidden" name="gaid" value="<%= currentGA.getKey().getId() %>">
			</form>
		</div>
		<div class="modal-footer">
			<a href="#" class="btn" data-dismiss="modal">Annuler</a> <a
				href="javascript:submitTitleForm()" class="btn btn-primary">Modifier</a>
		</div>
	</div>

	<script type="text/javascript">
		function submitTitleForm() {
			if ($("#title").val() != "") {
				$("#titleform").submit();
			}
		}
	</script>
	
	<!-- /container -->

	<%@ include file="jscripts.jspf" %>
	
	<script type="text/javascript">
		$(document).ready(function() {

		    $("#description").html(markdown.toHTML($("#desc").html()));
		    
            $("textarea[id^='comment-']").each(function(i) {
            	$("#" + $(this).attr("id") + "-display").html(markdown.toHTML($(this).html()));
            });
            
		});
		
		function deleteComment(commentId) {
			var r=confirm("Êtes vous sûr de vouloir supprimer ce commentaire ?\n"
					+ "Cette action est irréversible.");
			if (r==true)
			  {
				$.post( "/editga", 
						{ req: "deletecomment", gaid: "<%= currentGA.getKey().getId() %>", comment : commentId }
					);
					
				var parent = $("#comment-" + commentId).parents(".media");
				parent.slideUp();
				parent.next().hide();
			  }
		}
	</script>
	
</body>
</html>
