<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@page import="javax.jdo.JDOObjectNotFoundException"%>
<%@page import="fr.cpcgifts.utils.DateTools"%>
<%@page import="java.util.Collection"%>
<%@page import="org.apache.commons.collections.CollectionUtils"%>
<%@page import="java.util.Map"%>
<%@page import="net.sf.jsr107cache.CacheException"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.List"%>
<%@page import="fr.cpcgifts.persistance.GAPersistance"%>
<%@page import="fr.cpcgifts.utils.ViewTools"%>
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
	CpcUser profileCpcUser = null;

	String suid = request.getParameter("userID");

	if (suid != null) {
		Long uid = Long.parseLong(suid);

		if (uid != null) {
			Key k = KeyFactory.createKey("CpcUser", uid);
			
			try {
				profileCpcUser = CpcUserPersistance.getUserFromCache(k);
			} catch(JDOObjectNotFoundException e) {
				response.sendRedirect("/404.html");
				return;
			}
		}
	}

	if (profileCpcUser == null) {
		response.sendRedirect("/404.html");
		return;
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
<title>Profil de <%=profileCpcUser.getCpcNickname()%> - CPCGifts
</title>
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
		boolean isCurrentUser = userService.isUserLoggedIn() && profileCpcUser.getKey().equals(cpcuser.getKey());
	%>

	<div class="container">


		<div class="row">
			<div class="span3">
				<img alt="Avatar" src="<%=profileCpcUser.getAvatarUrl()%>" />
				<%
					if(isCurrentUser || (userService.isUserLoggedIn() && userService.isUserAdmin())) {
				%>
				<div class="row" style="margin-top: 10px;">
					<a class="btn btn-success span2" href="#modif-image"
						data-toggle="modal"> <i class="icon-pencil icon-white"></i>
						Modifier l'avatar
					</a>
				</div>
				<%
					}
				%>
			</div>
			<div class="span8">
				<div class="row">
					<h1><%=profileCpcUser.getCpcNickname()%></h1>
				</div>
				<div class="row">
					<% if(profileCpcUser.isBanned()) { %>
						<div class="alert alert-error">Utilisateur banni !</div>
					<% } else { %>
						<div class="span">
							<a class="btn"
								href="http://forum.canardpc.com/members/<%=profileCpcUser.getCpcProfileId()%>">
								<i class="icon-user"></i> Voir le profil CPC
							</a>
							<a class="btn"
								href="http://forum.canardpc.com/private.php?do=newpm&u=<%=profileCpcUser.getCpcProfileId()%>">
								<i class="icon-envelope"></i> Envoyer un message privé
							</a>
							
						</div>
						
						<div class="span">
						
							<% 
								Map<String,String> profiles = profileCpcUser.getProfiles();
							
								if(profiles.containsKey("steamgifts")) {
							%>
								<a class="btn btn-link" href="<%= profiles.get("steamgifts") %>"><img class="img-small-icon" alt="steamgifts" src="/img/sg.png"></a>
							<% 
								}
								if(profiles.containsKey("steam")) {
							%>
								<a class="btn btn-link" href="<%= profiles.get("steam") %>"><img class="img-small-icon" alt="steam" src="/img/steam.png"></a>
							<% 
								}
								if(profiles.containsKey("desura")) {
							%>
								<a class="btn btn-link" href="<%= profiles.get("desura") %>"><img class="img-small-icon" alt="desura" src="/img/desura.png"></a>
							<% 
								}
								if(profiles.containsKey("gamersgate")) {
							%>
								<a class="btn btn-link" href="<%= profiles.get("gamersgate") %>"><img class="img-small-icon" alt="gamersgate" src="/img/gg.png"></a>
							<% 
								}
							%>
							
							<%
								if(isCurrentUser || (userService.isUserLoggedIn() && userService.isUserAdmin())) {
							%>
								<a class="btn btn-success" href="#add-profile" data-toggle="modal">
								<i class="icon-plus-sign icon-white"></i> Ajouter/Modifier un profil
								</a>
							<%
								}
							%>
							
						</div>
						
					<% } %>
				</div>
				<% if(isCurrentUser || (userService.isUserLoggedIn() && userService.isUserAdmin())) { %>
					<div class="row row-top-margin">
						<form id="emailform" name="emailform" action="/edituser" method="post" class="">
						    <label class="checkbox">
						    	<input name="acceptemails" type="checkbox" <% if(profileCpcUser.isAcceptEmails()) { %> checked="checked" <% } %>> J'accepte de recevoir des emails de la part de CPC Gifts
						    </label>
						    
						    <input type="hidden" name="req" value="email" />
							<input type="hidden" name="userid" value="<%= profileCpcUser.getKey().getId() %>" />
						    
						    <button type="submit" class="btn">Valider</button>
					    </form>
					</div>
				<% } %>
			</div>
		</div>
		<hr />

		<div class="tabbable">
			<ul class="nav nav-tabs">
				<li id="created-button" class="active"><a
					onclick="javascript:changeSection('#created');" href="#created">Concours
						créés <span class="gray">(<%= profileCpcUser.getGiveaways().size() %>)</span></a></li>
				<li id="entries-button"><a
					onclick="javascript:changeSection('#entries');" href="#entries">Participations <span class="gray">(<%= profileCpcUser.getEntries().size() %>)</span></a></li>
				<li id="won-button"><a
					onclick="javascript:changeSection('#won');" href="#won">Concours gagnés <span class="gray">(<%= profileCpcUser.getWon().size() %>)</span></a></li>
				<% 
					if(userService.isUserLoggedIn() && userService.isUserAdmin()) {
				%>
					<li><a 
					onclick="javascript:changeSection('#admin');" href="#admin" data-toggle="tab">Admin</a></li>
				<%
					}
				%>	
			</ul>

			<div class="tab-content">
				<div id="created" class="tab-pane">
					<%
					Map<Key,Giveaway> gas = GAPersistance.getAllFromCache(profileCpcUser.getGiveaways());
					
					for(Giveaway ga : DateTools.sortGiveawaysByEndDate(gas)) {
					%>
		
					<%=ViewTools.gaView(ga)%>
					<hr>
					<%
						}
					%>
				</div>
		
				<div id="entries" class="tab-pane" style="display: none">
		
					<%
					
					Map<Key,Giveaway> entries = GAPersistance.getAllFromCache(profileCpcUser.getEntries());						
													
					for(Giveaway ga : DateTools.sortGiveawaysByEndDate(entries)) {
					%>
					<%=ViewTools.gaView(ga)%>
					<hr>
					<%
						}
					%>
		
				</div>
		
				<div id="won" class="tab-pane" style="display: none">
		
					<%
					Map<Key,Giveaway> won = GAPersistance.getAllFromCache(profileCpcUser.getWon());					
													
					for(Giveaway ga : DateTools.sortGiveawaysByEndDate(won)) {
					%>
					<%=ViewTools.gaView(ga)%>
					<hr>
					<%
						}
					%>
		
				</div>
				
				<% if(userService.isUserLoggedIn() && userService.isUserAdmin()) { %>
					<div class="tab-pane" id="admin">
						<div class="row offset1">
							<% if(profileCpcUser.isBanned()) { %>
								<a	href="/admin/unbanuser?reqtype=unbanuser&userid=<%= profileCpcUser.getKey().getId() %>"
										class="btn btn-warning"><i class="icon-repeat icon-white"></i> Lever le ban</a>
							<% } else { %>
								<a	href="/admin/banuser?reqtype=banuser&userid=<%= profileCpcUser.getKey().getId() %>"
										class="btn btn-danger"><i class="icon-minus-sign icon-white"></i> Bannir l'utilisateur</a>
							<% } %>
						</div>
						<hr />
					</div>
				<% } %>
				
			</div>
				
		</div>
				

		

		<%@ include file="footer.jspf"%>

	</div>
	<!-- /container -->

	<div id="modif-image" class="modal hide fade">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3>Modifier l'avatar</h3>
		</div>
		<div class="modal-body">
			<form id="imgform" name="imgform" action="/edituser" method="post">
				<fieldset>
					<label>Url de l'avatar (184x184 pixels)</label>
					<input id="imgurl"	name="imgurl" type="text" placeholder="url" required="required">
					<span class="help-block">Astuce : copiez l'url de votre
						avatar steam, elle est déjà au bon format !</span>
				</fieldset>
				<input type="hidden" name="req" value="changeimg" />
				<input type="hidden" name="userid" value="<%= profileCpcUser.getKey().getId() %>" />
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
	
	<div id="add-profile" class="modal big-modal hide fade">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3>Ajouter/Modifier un profil</h3>
		</div>
		<div class="modal-body">
			<form id="profileform" name="profileform" action="/edituser" method="post">
				<div class="row">
					<div class="control-group span2">
						<fieldset>
							<label class="radio">
							  	<input type="radio" name="service" id="steamgiftsRadio" value="steamgifts" checked>
							  	<img src="/img/sg.png" class="img-small-icon" alt="" /> Steamgifts
							</label>
							<label class="radio">
							  	<input type="radio" name="service" id="steamRadio" value="steam">
							  	<img src="/img/steam.png" class="img-small-icon" alt="" /> Steam
							</label>
							<label class="radio">
							  	<input type="radio" name="service" id="desuraRadio" value="desura">
							  	<img src="/img/desura.png" class="img-small-icon" alt="" /> Desura
							</label>
							<label class="radio">
							  	<input type="radio" name="service" id="ggRadio" value="gamersgate">
							  	<img src="/img/gg.png" class="img-small-icon" alt="" /> Gamers Gate
							</label>
						</fieldset>
					</div>
				
					<div class="span valign-profile-form">
						<div class="control-group form-horizontal">
							<fieldset>
								<label class="control-label" for="profile-link">Url du profil</label>
								<div class="controls">
									<input id="profile-link" class="span5" name="link" type="text" placeholder="url" required="required" />
								</div>
							</fieldset>
						</div>
					</div>
				</div>
				<input type="hidden" name="req" value="addprofile" />
				<input type="hidden" name="userid" value="<%= profileCpcUser.getKey().getId() %>" />
			</form>
		</div>
		<div class="modal-footer">
			<a href="#" class="btn" data-dismiss="modal">Annuler</a> <a
				href="javascript:submitProfileForm()" class="btn btn-primary">Valider</a>
		</div>
	</div>
	
	<script type="text/javascript">
		function submitProfileForm() {
			if ($("#profile-link").val() != "") {
				$("#profileform").submit();
			}
		}
	</script>

	<%@ include file="jscripts.jspf" %>
	
	<script type="text/javascript">
		var changeSection = function(sectionName) {
			if (sectionName != "") {
				$('.nav > li').removeClass("active");
				$("#created, #entries, #won, #admin").hide();

				$(sectionName + '-button').addClass("active");
				$(sectionName).show();

			} else {
				changeSection("#created");
			}
		}

		changeSection(window.location.hash);
	</script>

</body>
</html>


<%
	CpcUserPersistance.closePm();
%>