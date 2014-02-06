<%@page import="fr.cpcgifts.model.AdminRequest"%>
<%@page import="fr.cpcgifts.utils.TextTools"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@page import="javax.jdo.JDOObjectNotFoundException"%>
<%@page import="java.util.Comparator"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.Collection"%>
<%@page import="java.util.Map"%>
<%@page import="fr.cpcgifts.model.Comment"%>
<%@page import="fr.cpcgifts.persistance.CommentPersistance"%>
<%@page import="fr.cpcgifts.utils.DateTools"%>
<%@page import="fr.cpcgifts.persistance.GAPersistance"%>
<%@page import="fr.cpcgifts.model.Giveaway"%>
<%@page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@page import="com.google.appengine.api.datastore.Key"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>

<%	UserService userService = UserServiceFactory.getUserService(); %>
<%	User user = null; %>
<%
	user = userService.getCurrentUser();

	Giveaway currentGA = null;

	String gaid = request.getParameter("gaID");

	if (gaid != null) {
		
		Long gid = null;
		
		try {
			gid = Long.parseLong(gaid);
		} catch (NumberFormatException e) {
			response.sendRedirect("/404.html");
			return;
		}

		if (gid != null) {
			Key k = KeyFactory.createKey(Giveaway.class.getSimpleName(), gid);

			try {
				currentGA = GAPersistance.getGAFromCache(k);
			} catch (JDOObjectNotFoundException e) {
				response.sendRedirect("/404.html");
				return;
			}

		}
	}
	if(currentGA == null) {
		response.sendRedirect("/404.html");
		return;
	}

	CpcUser gaAuthor = CpcUserPersistance.getUserFromCache(currentGA.getAuthor());

	
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
	
	<%
			boolean isAuthor = cpcuser != null && cpcuser.getKey().equals(gaAuthor.getKey());
			boolean isAdmin = (userService.isUserLoggedIn() && userService.isUserAdmin());
	%>

	<div class="container">


		<div class="row">
			<div class="span5">
				<img alt="Steam game Image" class="img-steam-game" src="<%=currentGA.getImgUrl()%>" />
				<%
					if (isAuthor || isAdmin) {
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
				<%
					if (currentGA.getNbCopies() > 1) {
				%>
				<span class="gray">(<%=currentGA.getNbCopies()%> copies)</span>
				<%
					}
				%>
				</h1>
				<% if(isAuthor) { %>
				<a href="#admin-request-modal"
					class="btn btn-mini"
					onclick="editTitleRequest(<%= currentGA.getKey().getId() %>)"
					data-toggle="modal">
						<i class="icon-pencil"></i> Demander la modification du titre
					</a>
				<% } %>
				<hr>
				<div class="span2">
					<h4>Créé par :</h4>
				</div>
				<div class="media">
					<a class="pull-left"
						href="/user?userID=<%=gaAuthor.getKey().getId()%>">
						<img alt="Author avatar" class="media-object img-small-avatar"
						src="<%=gaAuthor.getAvatarUrl()%>">
					</a>
					<div class="media-body">
						<h3 class="media-heading">
							<a href="/user?userID=<%=gaAuthor.getKey().getId()%>"><%=gaAuthor.getCpcNickname()%></a>
						</h3>
					</div>
				</div>
				<hr>

				<img alt="Clock" class="img-small-icon" src="img/clock.png" />
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
					if (currentGA.isOpen() && !isAuthor && userService.isUserLoggedIn()
							&& !cpcuser.isBanned()) {
						if (currentGA.getEntrants().contains(cpcuser.getKey())) {
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
			<div class="offset1 span9 alert alert-warning alert-error-color" id="custom-rules">
			</div>
			<% if(isAuthor) { %>
			<div class="offset1 span9 well well-small">
				Si vous souhaitez rendre ces conditions moins restrictives vous pouvez demander une modification à un admin 
				via 
				<a href="#admin-request-modal"
					onclick="editRulesRequest(<%= currentGA.getKey().getId() %>)"
					data-toggle="modal">
						ce formulaire
					</a>.
			</div>
			<% } %>
		</div>

		<div class="row">
			<div class="span10 offset1" id="description">
			</div>
		</div>

		<hr>
		
		
		<%
			if (isAuthor || isAdmin) {
		%>
				<div class="row btn-toolbar" style="margin-top: 10px;">
					<a class="btn btn-success span2" href="#modif-desc"
						data-toggle="modal"> <i class="icon-pencil icon-white"></i>
						Modifier la description
					</a>
					<% if(currentGA.isOpen()) { %>
					<a	href="javascript:closeGA()"
						class="btn btn-danger"><i class="icon-trash icon-white"></i> Annuler le concours
					</a>
					<% } %>
				</div>
				<hr />
		<%
			}
		%>

		<div class="tabbable">
			<ul class="nav nav-tabs">
				<li class="active"><a href="#commentaires" data-toggle="tab">Commentaires <span class="gray">(<%=currentGA.getComments().size()%>)</span></a></li>
				<li><a href="#entrants" data-toggle="tab">Participants <span class="gray">(<%=currentGA.getEntrants().size()%>)</span></a></li>
				<%
					if (isAuthor|| isAdmin) {
				%>
				<li><a href="#signature" data-toggle="tab">Signature</a></li>
				<%
					}
				%>
				<%
					if (!currentGA.isOpen() && currentGA.getWinners().size() == 1) {
				%>
				<li><a href="#winner" data-toggle="tab">Gagnant</a></li>
				<%
					} else if (!currentGA.isOpen() && currentGA.getWinners().size() > 1) {
				%>
				<li><a href="#winner" data-toggle="tab">Gagnants <span class="gray">(<%=currentGA.getWinners().size()%>)</span></a></li>
				<%
					}
				%>
				<%
					if (isAdmin) {
				%>
				<li><a href="#admin" data-toggle="tab">Admin</a></li>
				<%
					}
				%>
			</ul>
			<div class="tab-content">
				<div class="tab-pane active" id="commentaires">
					<%
						Map<Key, Comment> comments = CommentPersistance.getAllFromCache(currentGA.getComments());

						Comment[] commentsArray = comments.values().toArray(new Comment[0]);

						Arrays.sort(commentsArray, new Comparator<Comment>() {
							public int compare(Comment c1, Comment c2) {
								return c1.getCommentDate().compareTo(c2.getCommentDate());
							}
						});

						for (Comment comment : commentsArray) {
					%>
					
					<%@ include file="/templates/commentview.jspf" %>
					
					<%
							if (userService.isUserLoggedIn() && (comment.getAuthor().equals(cpcuser.getKey()) || isAdmin)) {
					%>
						<a href="javascript:deleteComment(<%=comment.getKey().getId()%>)" class="btn btn-mini">
							<i class="icon-trash"></i> Supprimer ce commentaire
						</a>
					<%
						} if(userService.isUserLoggedIn()) {
					%>
					<a class="pull-right" title="Signaler un message hors-charte" data-toggle="modal" onclick="reportPostRequest(<%= comment.getKey().getId() %>)" href="#admin-request-modal">
						<i class="icon-warning-sign"></i>
					</a>
					<% } %>
					<hr>
					
					<%
						}
					%>

					<%
						if (userService.isUserLoggedIn() && cpcuser != null && !cpcuser.isBanned()) {
					%>

					<form id="comment-form" action="/editga" method="post">
						<fieldset>
							<label>Laisser un commentaire :</label>
							<textarea class="span12" rows="5" id="comment" name="comment" required="required" data-provide="markdown"></textarea>
							<input type="hidden" name="req" value="comment" />
							<input type="hidden" name="gaid" value="<%=currentGA.getKey().getId()%>">
							
							<button type="submit" class="btn">Commenter</button>
							<% if(currentGA.isOpen() && !currentGA.getEntrants().contains(cpcuser.getKey()) && !isAuthor) { %>
								<button type="button" onclick="enterGA(<%=currentGA.getKey().getId()%>)" class="btn">Commenter et participer</button>
							<% } %>
						</fieldset>
					</form>
					<%
						}
					%>

				</div>
				<div class="tab-pane" id="entrants">
					
					<%
							for (CpcUser entrant : CpcUserPersistance.getAllFromCache(currentGA.getEntrants()).values()) {
								CpcUser userToDisplay = entrant;
					%>
						
						<%@ include file="/templates/userview.jspf" %>
						<% if(isAdmin) { %>
							<a	href="/admin/gaWinner?reqtype=addWinner&gaid=<%=currentGA.getKey().getId()%>&userid=<%=entrant.getKey().getId()%>"
								class="btn btn-small btn-success"><i class="icon-plus icon-white"></i> Ajouter aux gagnants</a>
						<% } %>
						<hr>
					<%
						}
					%>
					
					
				</div>
				<div class="tab-pane" id="signature">
				<h4>Signature texte :</h4>
				<p class="well">[url=<%=request.getRequestURL().toString() + '?'
					+ request.getQueryString()%>]<%=currentGA.getTitle()%>[/url]</p>
				<%
					if (!currentGA.getImgUrl().equals("/img/game.png")) {
				%>
				<h4>Signature image :</h4>
				<p class="well">[url=<%=request.getRequestURL().toString() + '?'
						+ request.getQueryString()%>][IMG]<%=currentGA.getImgUrl()%>[/IMG][/url]</p>
				<%
					}
				%>
				</div>
				<div class="tab-pane" id="winner">
				
				<%
					if (!currentGA.isOpen() && currentGA.getWinners().size() > 0) {
						for (Key k : currentGA.getWinners()) {
							CpcUser winner = CpcUserPersistance.getUserFromCache(k);
							CpcUser userToDisplay = winner;
				%>
						<%@ include file="/templates/userview.jspf" %>
						<%
							if (isAdmin && !currentGA.isOpen()
										&& currentGA.getEntrants().size() > currentGA.getWinners().size()) {
						%>
							<a	href="/admin/reroll?reqtype=reroll&gaid=<%=currentGA.getKey().getId()%>&winnerToReroll=<%=k.getId()%>"
								class="btn btn-warning"><i class="icon-repeat icon-white"></i> Relancer le tirage</a>
						<%
							}
							if(isAdmin) {
						%>	
							<a	href="/admin/gaWinner?reqtype=removeWinner&gaid=<%=currentGA.getKey().getId()%>&userid=<%=k.getId()%>"
								class="btn btn-danger"><i class="icon-trash icon-white"></i> Supprimer gagnant</a>
						<%
							}
							if (isAuthor && !currentGA.isOpen() &&
									currentGA.getEntrants().size() > currentGA.getWinners().size()) {
						%>
							<a href="#admin-request-modal"
								class="btn btn-mini"
								onclick="rerollRequest(<%= currentGA.getKey().getId() %>,<%= winner.getKey().getId() %>, '<%= winner.getCpcNickname() %>')"
								data-toggle="modal">
									<i class="icon-repeat"></i> Demander un nouveau tirage
							</a>
						<%
							}
						%>
						<hr />
										
				<%
						}
					}
				%>
				</div>
				<%
					if (isAdmin) {
				%>
				<div class="tab-pane" id="admin">
					<div class="row offset1">
						<a class="btn btn-success" href="#modif-title"
						data-toggle="modal"> <i class="icon-pencil icon-white"></i>
						Modifier le titre
						</a>
					</div>
					<hr />
					
					<div class="row offset1">
						<a class="btn btn-success" href="#modif-rules"
						data-toggle="modal"> <i class="icon-pencil icon-white"></i>
						Modifier les règles
						</a>
					</div>
					<hr />
					
					<div class="row offset1">
						<a	href="/admin/closega?reqtype=closeGa&gaid=<%=currentGA.getKey().getId()%>"
									class="btn btn-warning"><i class="icon-trash icon-white"></i> Fermer le concours</a>
					</div>
					<hr />
					<div class="row offset1">
						<a	href="/admin/openga?reqtype=openGa&gaid=<%=currentGA.getKey().getId()%>"
									class="btn btn-warning"><i class="icon-repeat icon-white"></i> Rouvrir le concours</a> (ne modifie pas la date de fin du concours)
					</div>
				</div>
				<%
					}
				%>
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
				<input type="hidden" name="gaid" value="<%=currentGA.getKey().getId()%>">
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
						<textarea class="span9 wmd-input" name="desc" id="desc" rows="15" data-provide="markdown" ><%=currentGA.getDescription()%></textarea>
				</fieldset>
				<input type="hidden" name="req" value="changedescription" />
				<input type="hidden" name="gaid" value="<%=currentGA.getKey().getId()%>">
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
						name="title" type="text" required="required" value="<%=currentGA.getTitle()%>">
					<span class="help-block">Admin uniquement.</span>
				</fieldset>
				<input type="hidden" name="req" value="changetitle" />
				<input type="hidden" name="gaid" value="<%=currentGA.getKey().getId()%>">
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

	<!-- Modal modification des règles -->
	<div id="modif-rules" class="modal big-modal hide fade">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal"
				aria-hidden="true">&times;</button>
			<h3>Modifier les règles</h3>
		</div>
		<div class="modal-body">
			<form id="rulesform" name="rulesform" action="/editga" method="post">
				<fieldset>
					<label>Conditions d'inscription :</label><br />
						<textarea class="span9 wmd-input" name="rules" id="rules" rows="15" data-provide="markdown" ><%=currentGA.getRules()%></textarea>
				</fieldset>
				<input type="hidden" name="req" value="changerules" />
				<input type="hidden" name="gaid" value="<%=currentGA.getKey().getId()%>">
			</form>
		</div>
		<div class="modal-footer">
			<a href="#" class="btn" data-dismiss="modal">Annuler</a> <a
				href="javascript:submitRulesForm()" class="btn btn-primary">Modifier</a>
		</div>
	</div>
	
	<script type="text/javascript">
		function submitRulesForm() {
			$("#rulesform").submit();
		}
	</script>
	
	<!-- Modal requête admin -->
	<div id="admin-request-modal" class="modal hide fade">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3>Requête aux administrateurs</h3>
		</div>
		<div class="modal-body">
			<form id="admin-request-form" name="admin-request-form">
				<fieldset>
					<label>Objet : <span id="admin-request-type-display"></span></label>
					<input type="hidden" id="admin-request-type" name="type" />
				</fieldset>
				<fieldset id="reroll-fieldset" class="hidden">
					<label>Utilisateur concerné : <span id="user-to-reroll-name"></span></label>
					<input type="hidden" name="userid" id="user-to-reroll-id" />
					<div class="well well-small">
					Merci d'indiquer la règle que le gagnant ne respecte pas.
    				</div>
				</fieldset>
				<fieldset>
					<label>Votre message :</label>
					<textarea class="span5" name="text" id="admin-request-text"></textarea>
				</fieldset>
				<input type="hidden" name="req" id="admin-request-formreqtype" value="create" />
				<input type="hidden" name="attachmentid" id="admin-request-attachmentid" />
			</form>
		</div>
		<div class="modal-footer">
			<a href="#" class="btn" data-dismiss="modal">Annuler</a>
			<a href="javascript:submitAdminRequestForm()" class="btn btn-primary">Envoyer</a>
		</div>
	</div>
	
	<script type="text/javascript">
		function submitAdminRequestForm() {
			var params = $('#admin-request-form').serialize();
		    
		    $.post('/adminrequest',
		          params,
		          function(data){
		              alert("Votre demande a bien été enregistrée et sera bientôt traitée par les administrateurs de CPC Gifts.");
		              $("#admin-request-modal").modal("hide");
		          });
		}
	</script>

	
	<!-- /container -->

	<%@ include file="jscripts.jspf" %>
	
	<script type="text/javascript">
		$(document).ready(function() {

			$("#custom-rules").html(markdown.toHTML($("#rules").val()));
			
		    $("#description").html(markdown.toHTML($("#desc").val()));
		    
            $("textarea[id^='comment-']").each(function(i) {
            	$("#" + $(this).attr("id") + "-display").html(markdown.toHTML($(this).val()));
            });
            
            var hash = window.location.hash;
            
            if(hash.match(/comment-/g)) {
            	$(hash).css("background-color", "yellow");
            }
            
		});
		
		function deleteComment(commentId) {
			var r=confirm("Êtes-vous sûr de vouloir supprimer ce commentaire ?\n"
					+ "Cette action est irréversible.");
			if (r==true)
			  {
				$.post( "/editga", 
						{ req: "deletecomment", gaid: "<%=currentGA.getKey().getId()%>", comment : commentId }
					);
					
				var parent = $("#comment-" + commentId + "-div");
				parent.slideUp();
				parent.next().hide();
			  }
		}
		
		function enterGA(gaId) {
			$.get(
					"/enterga?reqtype=enter&gaid=" + gaId,
					function() {
						$("#comment-form").submit();
					}
			);
		}
		
		<% if(isAuthor || isAdmin) { %>
		
		function closeGA() {
			var r=confirm("Êtes-vous sûr de vouloir supprimer ce concours ?\n"
					+ "Cette action est irréversible.\n\n"
					+ "Les inscriptions seront fermées, aucun gagnant tiré au sort et le concours et ses commentaires seront supprimés au bout de quelques jours.");
			if (r==true)
			  {
				$.post( "/editga", 
						{ req: "closeGa", gaid: "<%=currentGA.getKey().getId()%>" },
						function() {
							window.location.reload(true);
						}
					);
				
			  }
		}
		
		<% } %>
		
		/* requêtes administrateur */
		<% if(isAuthor) { %>
		
		function editRulesRequest(gaid) {
			$("#reroll-fieldset").addClass("hidden");
			$("#admin-request-type").val("<%= AdminRequest.Type.RulesModification.name() %>");
			$("#admin-request-type-display").html("Modification des règles");
			$("#admin-request-attachmentid").val(gaid);
		}
		
		function editTitleRequest(gaid) {
			$("#reroll-fieldset").addClass("hidden");
			$("#admin-request-type").val("<%= AdminRequest.Type.TitleModification.name() %>");
			$("#admin-request-type-display").html("Modification du titre");
			$("#admin-request-attachmentid").val(gaid);
		}
		
		function rerollRequest(gaid,userid,username) {
			$("#reroll-fieldset").removeClass("hidden");
			$("#admin-request-type").val("<%= AdminRequest.Type.Reroll.name() %>");
			$("#admin-request-type-display").html("Demande de reroll");
			$("#user-to-reroll-id").val(userid);
			$("#user-to-reroll-name").html(username);
			$("#admin-request-attachmentid").val(gaid);
		}
		
		<% } %>
		
		function reportPostRequest(postId) {
			$("#reroll-fieldset").addClass("hidden");
			$("#admin-request-type").val("<%= AdminRequest.Type.ReportPost.name() %>");
			$("#admin-request-type-display").html("Signaler un commentaire hors charte");
			$("#admin-request-attachmentid").val(postId);
		}
	</script>
	
</body>
</html>
